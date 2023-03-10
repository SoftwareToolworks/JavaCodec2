package codectest;

/*
 * Program to send audio through Vocoder
 */
public class Main {

    private static Audio audio;

    public static void main(String[] args) {
        audio = new Audio();
        audio.startCapture();
    }
}
