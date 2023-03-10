package codectest;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.UnsupportedLookAndFeelException;

public final class Display extends JFrame {

    private static final long serialVersionUID = 1L;

    private final SpectrumPanel spectrumPanel;
    private final Audio audio;
    private boolean hasChanged;
    private boolean digitalEnabled;
    private int bufferSize;

    public Display(Audio a, int size) {
        audio = a;
        bufferSize = size;
        hasChanged = false;
        digitalEnabled = true;

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException ex) {
        }

        initComponents();

        spectrumPanel = new SpectrumPanel(bufferSize);
        scopePanel.setLayout(new BorderLayout());
        scopePanel.add(spectrumPanel, BorderLayout.CENTER);
    }

    public boolean getChanged() {
        return hasChanged;
    }

    public void setChanged(boolean val) {
        hasChanged = val;
    }

    public void showData(float[] in_data, float max) {
        spectrumPanel.displayData(in_data, max);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        modeSwitch = new javax.swing.JButton();
        scopePanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CodecTest");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridLayout(1, 0));

        modeSwitch.setText("DIGITAL");
        modeSwitch.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        modeSwitch.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                modeSwitchMousePressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(modeSwitch, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(330, 330, 330)
                .addComponent(modeSwitch)
                .addGap(0, 42, Short.MAX_VALUE))
        );

        scopePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("RX Spectrum"));
        scopePanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(scopePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(scopePanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        audio.stopCapture();
    }//GEN-LAST:event_formWindowClosed

    private void modeSwitchMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_modeSwitchMousePressed
        if (digitalEnabled == true) {
            digitalEnabled = false;
            modeSwitch.setText("ANALOG");
            audio.setRawAudio(true);
        } else {
            digitalEnabled = true;
            modeSwitch.setText("DIGITAL");
            audio.setRawAudio(false);
        }
    }//GEN-LAST:event_modeSwitchMousePressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JButton modeSwitch;
    private javax.swing.JPanel scopePanel;
    // End of variables declaration//GEN-END:variables
}
