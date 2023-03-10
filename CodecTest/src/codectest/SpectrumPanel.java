package codectest;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public class SpectrumPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private float[] fftdata;
    private float max_mag;
    private final int bufferSize;

    public SpectrumPanel(int size) {
        super();
        this.bufferSize = size;
        this.setBackground(Color.black);
    }

    public void displayData(float[] data, float max) {
        this.fftdata = data;
        this.max_mag = max;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        float dx = (float) getSize().width / this.bufferSize;
        float dy = (float) getSize().height / max_mag;

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(102, 204, 0));

        int prev_x = 0;
        int prev_y;

        try {
            prev_y = (int) (fftdata[0] * dy);       // start y here
        } catch (NullPointerException e) {
            g2.dispose();
            return;     // nothing to plot
        }

        for (int i = 0; i < this.bufferSize; i++) {
            int px = (int) (i * dx);
            int py = getSize().height - (int) (fftdata[i] * dy);
            g2.drawLine(prev_x, prev_y, px, py);    // connect the dots
            prev_x = px;
            prev_y = py;
        }

        g2.dispose();
    }
}
