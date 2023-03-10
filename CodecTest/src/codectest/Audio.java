package codectest;

import codec2.Codec2;
import codec2.FFT;
import complex.Complex;
import complex.ComplexMath;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

/*
 * Signed 16-bit PCM Audio processing
 */
public final class Audio extends Thread {

    public static final int FFTSIZE = 1024;
    //
    private static final float FS = 8000.0f;
    private static final float BETA = 0.9f;
    //
    private final Thread codecThread;
    private final Thread captureThread;
    private final Thread playbackThread;
    //
    private FFT fft;
    private final Display scope;
    //
    private static boolean capture;
    //
    private PipedOutputStream outputStream;
    private PipedInputStream inputStream;
    //
    private PipedOutputStream playbackOutputStream;
    private PipedInputStream playbackInputStream;
    //
    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    private SourceDataLine sourceDataLine;
    //
    private final Codec2 codec;
    private final short[] speech_frame;
    private final byte[] speech_bits;
    private final int spf;
    private int bufferSize;
    //
    private Complex[] voice;
    private float[] avmag;
    private float[] eq;
    //
    private boolean rawAudio;

    public Audio() {
        fft = new FFT(FFTSIZE);
        Audio.capture = false;
        rawAudio = false;

        voice = new Complex[FFTSIZE];
        avmag = new float[FFTSIZE / 2];

        codec = new Codec2();

        codec.codec2_setEQBoolean(true);            // equalizer default enable
        eq = new float[codec.codec2_getAMPK()];    // copy of equalizer values

        spf = codec.codec2_getSamplesPerFrame();    // 320
        bufferSize = spf * 65536;
        speech_frame = new short[spf];
        speech_bits = new byte[codec.codec2_getBytesPerFrame()];

        try {
            audioFormat = setAudioFormat();

            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);

            DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
            sourceDataLine.open(audioFormat);

            inputStream = new PipedInputStream(spf * 2 * 16768);
            outputStream = new PipedOutputStream();
            outputStream.connect(inputStream);

            playbackInputStream = new PipedInputStream(spf * 2 * 16768);
            playbackOutputStream = new PipedOutputStream();
            playbackOutputStream.connect(playbackInputStream);
        } catch (LineUnavailableException | IOException p) {
            System.out.println("Fatal Error - Can't initialize software " + p.toString());
            System.exit(-1);
        }

        for (int i = 0; i < FFTSIZE; i++) {
            voice[i] = new Complex();
        }

        scope = new Display(this, spf);
        scope.setVisible(true);

        codecThread = new Thread(this);
        codecThread.setName("Codec Thread");

        captureThread = new AudioCapture();
        captureThread.setName("Audio Capture");

        playbackThread = new AudioPlayback();
        playbackThread.setName("Audio Playback");
    }

    public int getfftsize() {
        return FFTSIZE;
    }

    public void setRawAudio(boolean val) {
        rawAudio = val;
    }

    public boolean getRawAudio() {
        return rawAudio;
    }

    public void startCapture() {
        targetDataLine.start();
        sourceDataLine.start();

        targetDataLine.flush();
        sourceDataLine.flush();

        Audio.capture = true;

        codecThread.start();
        captureThread.start();
        playbackThread.start();
    }

    public boolean capturing() {
        return Audio.capture;
    }

    public void stopCapture() {
        Audio.capture = false;

        try {
            targetDataLine.drain();
            sourceDataLine.drain();

            targetDataLine.close();
            sourceDataLine.close();

            captureThread.join();
            playbackThread.join();
            codecThread.join();
        } catch (InterruptedException e) {
        }

        System.exit(0);
    }

    private AudioFormat setAudioFormat() {
        return new AudioFormat(
                Audio.FS, // sample rate
                16, // 16 bits in each sample
                1, // 1 channel = mono, 2 channel = stereo
                true, // signed = true
                false // big-endian = false = little-endian
        );
    }

    @Override
    public void run() {
        float max;
        int i, lsb, msb;

        while (Audio.capture == true) {
            try {
                /*
                 * Get audio from capture Pipe
                 */

                if (inputStream.available() >= (spf * 2)) { // samples per frame times 2 bytes (short)

                    /*
                     * Convert from little-endian stream bytes to signed short
                     */
                    for (i = 0; i < spf; i++) {
                        lsb = inputStream.read() & 0xFF;
                        msb = inputStream.read() & 0xFF;
                        speech_frame[i] = (short) (msb << 8 | lsb);
                    }

                    if (scope.getChanged() == true) {
                        scope.setChanged(false);
                    }

                    /*
                     * Send the audio samples through the codec
                     */
                    if (rawAudio == false) {
                        codec.codec2_encode(speech_bits, speech_frame);
                        eq = codec.codec2_getEQValues();                // TODO maybe graph this?
                        codec.codec2_decode(speech_frame, speech_bits);
                    }

                    /*
                     * Convert from signed short back to little-endian stream bytes
                     */
                    for (i = 0; i < spf; i++) {
                        lsb = speech_frame[i] & 0xFF;
                        msb = (speech_frame[i] >>> 8) & 0xFF;
                        playbackOutputStream.write(lsb);
                        playbackOutputStream.write(msb);
                    }

                    /*
                     * The following is for the display
                     */
                    for (i = 0; i < spf; i++) {
                        /*
                         * Create a Complex number for FFT display
                         */
                        voice[i] = new Complex((float) speech_frame[i] / 16384.0f, 0.0f);
                    }

                    /*
                     * Zero out unused FFT Complex number bins
                     */
                    for (i = spf; i < FFTSIZE; i++) {
                        voice[i] = new Complex();
                    }

                    /*
                     * Convert complex audio to frequency domain
                     */
                    fft.transform(voice);

                    max = 8.0f;  // 8 seems to work

                    /*
                     * Average the magnitude data using a simple IIR low pass filter
                     */
                    for (i = 0; i < spf; i++) {
                        avmag[i] = BETA * avmag[i] + (1.0f - BETA) * ComplexMath.cabs(voice[i]);

                        if (avmag[i] > max) {
                            max = avmag[i];
                        }
                    }

                    scope.showData(avmag, max);
                } else {
                    sleep(0,1);
                }
            } catch (IOException | InterruptedException e) {
                System.out.println("Fatal: Codec Thread " + e.toString());
                stopCapture();
            }
        }
    }

    private final class AudioCapture extends Thread {

        private final byte[] captureBuffer = new byte[bufferSize];
        private int count;

        @Override
        public void run() {
            try {
                while (Audio.capture == true) {
                    count = targetDataLine.available();
                    count = (count < bufferSize) ? count - (count % 2) : bufferSize;    // Even byte boundary

                    if (count > 1) {
                        targetDataLine.read(captureBuffer, 0, count);
                        outputStream.write(captureBuffer, 0, count);
                    } else {
                        sleep(0,1);
                    }
                }

                outputStream.close();
                targetDataLine.close();
            } catch (ArrayIndexOutOfBoundsException | IOException | InterruptedException e) {
                System.out.println("Fatal Error: Audio Capture Thread: " + e.toString());
                stopCapture();
            }
        }
    }

    private final class AudioPlayback extends Thread {

        private final byte[] playbackBuffer = new byte[bufferSize];
        private int count;

        @Override
        public void run() {
            try {
                while (Audio.capture == true) {
                    count = playbackInputStream.available();
                    count = (count < bufferSize) ? count - (count % 2) : bufferSize;    // Even byte boundary

                    if (count > 1) {
                        for (int i = 0; i < count; i += 2) {
                            playbackBuffer[i] = (byte) playbackInputStream.read();
                            playbackBuffer[i + 1] = (byte) playbackInputStream.read();
                        }

                        try {
                            sourceDataLine.write(playbackBuffer, 0, count);
                        } catch (Exception s) {
                            System.out.println("Playback sourcedataline exception " + s.toString());
                        }
                    } else {
                        sleep(0,1);
                    }
                }

                playbackInputStream.close();
                sourceDataLine.close();
            } catch (ArrayIndexOutOfBoundsException | IOException | InterruptedException e) {
                System.out.println("Fatal Error: Audio Playback Thread: " + e.toString());
                stopCapture();
            }
        }
    }
}
