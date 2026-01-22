package vistas;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Iniciar extends javax.swing.JFrame {

    public Iniciar() {
        initComponents();
        setIconImage(new ImageIcon(getClass().getResource("/img/LogoIcono.png")).getImage());
        this.setLocationRelativeTo(null);
        procesoImagenFondo();
    }
    public void procesoImagenFondo() {
        try {
            // Carga la imagen
            ImageIcon icon = new ImageIcon(getClass().getResource("/img/LogoHM.png"));

            BufferedImage originalImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = originalImage.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();

            // Aplica la opacidad (por ejemplo, 0.5f para 50% de opacidad)
            BufferedImage transparentImage = setOpacity(originalImage, 1.0f);

            // Guarda la imagen transparente en un archivo temporal
            File tempFile = File.createTempFile("tempImage", ".png");
            ImageIO.write(transparentImage, "png", tempFile);

            // Aplica la escala usando la ruta del archivo temporal
            rsscalelabel.RSScaleLabel.setScaleLabel(jlbFondo, tempFile.getAbsolutePath());
        } catch (IOException e) {
            
        }
    }
    public static BufferedImage setOpacity(BufferedImage image, float opacity) {
        BufferedImage transparentImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = transparentImage.createGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        return transparentImage;
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        JPanelFondo = new javax.swing.JPanel();
        btnTurno = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        btnRegistro = new javax.swing.JButton();
        jlbFondo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("HotelesMas");
        setIconImages(null);
        setResizable(false);

        JPanelFondo.setBackground(new java.awt.Color(255, 255, 255));
        JPanelFondo.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnTurno.setBackground(new java.awt.Color(0, 51, 51));
        btnTurno.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btnTurno.setForeground(new java.awt.Color(255, 255, 255));
        btnTurno.setText("TURNO");
        btnTurno.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnTurnoMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnTurnoMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnTurnoMouseExited(evt);
            }
        });
        btnTurno.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnTurnoKeyPressed(evt);
            }
        });
        JPanelFondo.add(btnTurno, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 350, 160, 50));

        btnSalir.setBackground(new java.awt.Color(0, 51, 51));
        btnSalir.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btnSalir.setForeground(new java.awt.Color(255, 255, 255));
        btnSalir.setText("SALIR");
        btnSalir.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnSalirMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnSalirMouseExited(evt);
            }
        });
        JPanelFondo.add(btnSalir, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 350, 160, 50));

        btnRegistro.setBackground(new java.awt.Color(0, 51, 51));
        btnRegistro.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btnRegistro.setForeground(new java.awt.Color(255, 255, 255));
        btnRegistro.setText("REGISTRO");
        btnRegistro.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnRegistroMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnRegistroMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnRegistroMouseExited(evt);
            }
        });
        btnRegistro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnRegistroKeyPressed(evt);
            }
        });
        JPanelFondo.add(btnRegistro, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 350, 160, 50));
        JPanelFondo.add(jlbFondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 680, 420));

        getContentPane().add(JPanelFondo, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRegistroMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRegistroMouseClicked
        RegistroHotel ir = new RegistroHotel();
        ir.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnRegistroMouseClicked

    private void btnRegistroKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnRegistroKeyPressed
        RegistroHotel ir = new RegistroHotel();
        ir.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnRegistroKeyPressed

    private void btnTurnoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTurnoMouseClicked
        inicio ir = new inicio();
        ir.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnTurnoMouseClicked

    private void btnTurnoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnTurnoKeyPressed
        inicio ir = new inicio();
        ir.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnTurnoKeyPressed

    private void btnTurnoMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTurnoMouseEntered
        btnTurno.setBackground(new Color(0,102,102));
    }//GEN-LAST:event_btnTurnoMouseEntered

    private void btnTurnoMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTurnoMouseExited
        btnTurno.setBackground(new Color(0,51,51));
    }//GEN-LAST:event_btnTurnoMouseExited

    private void btnRegistroMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRegistroMouseEntered
        btnRegistro.setBackground(new Color(0,102,102));
    }//GEN-LAST:event_btnRegistroMouseEntered

    private void btnRegistroMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRegistroMouseExited
        btnRegistro.setBackground(new Color(0,51,51));
    }//GEN-LAST:event_btnRegistroMouseExited

    private void btnSalirMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSalirMouseEntered
        btnSalir.setBackground(new Color(0,102,102));
    }//GEN-LAST:event_btnSalirMouseEntered

    private void btnSalirMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSalirMouseExited
        btnSalir.setBackground(new Color(0,51,51));
    }//GEN-LAST:event_btnSalirMouseExited


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel JPanelFondo;
    private javax.swing.JButton btnRegistro;
    private javax.swing.JButton btnSalir;
    private javax.swing.JButton btnTurno;
    private javax.swing.JLabel jlbFondo;
    // End of variables declaration//GEN-END:variables
}
