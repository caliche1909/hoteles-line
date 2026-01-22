package vistas;

import conectar.Canectar;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import modelo.Hotel;

public final class RegistroHotel extends javax.swing.JFrame {

    Canectar con = new Canectar();
    PreparedStatement ps;
    ResultSet rs;
    int IdH;

    public RegistroHotel() {
        initComponents();
        jlbDeparProvin.setVisible(false);
        jlbCiudad.setVisible(false);
        cbxCiudadHotel.setVisible(false);
        txtCiuHotel.setVisible(false);
        cbxDeparHotel.setVisible(false);
        txtDepHotel.setVisible(false);
        setIconImage(new ImageIcon(getClass().getResource("/img/LogoIcono.png")).getImage());
        rsscalelabel.RSScaleLabel.setScaleLabel(jlbLogo, "src/img/LogoIcono.png");
        this.setLocationRelativeTo(null);
        con.traerPaises(cbxPaises, null, null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        btnAgreHotel = new javax.swing.JButton();
        jlbLogo = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtSlogan = new javax.swing.JTextArea();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        txtCiuHotel = new javax.swing.JTextField();
        cbxCiudadHotel = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtNomHotel = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtDirHotel = new javax.swing.JTextField();
        jlbCiudad = new javax.swing.JLabel();
        cbxPaises = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jlbDeparProvin = new javax.swing.JLabel();
        txtTelHotel = new javax.swing.JTextField();
        txtDepHotel = new javax.swing.JTextField();
        txtRnt = new javax.swing.JTextField();
        cbxDeparHotel = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        btnVolver = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("REGISTRA TU HOTEL");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel3.setText("Registro de Hoteles.");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 140, -1));

        jSeparator1.setBackground(new java.awt.Color(255, 204, 51));
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 104, 600, 10));

        btnAgreHotel.setBackground(new java.awt.Color(255, 179, 39));
        btnAgreHotel.setText("AGREGAR REGISTRO");
        btnAgreHotel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAgreHotelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnAgreHotelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnAgreHotelMouseExited(evt);
            }
        });
        jPanel1.add(btnAgreHotel, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 290, 180, 40));
        jPanel1.add(jlbLogo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 110, 70));

        jLabel5.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel5.setText("SLOGAN:");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(392, 110, 100, 20));

        txtSlogan.setColumns(20);
        txtSlogan.setRows(5);
        jScrollPane1.setViewportView(txtSlogan);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(392, 130, 180, -1));

        jLabel12.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel12.setText("  debajo de nuestro titulo. ¡Haz el tuyo!");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 230, 190, 20));

        jLabel13.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel13.setText(" Tu slogan debe ser muy corto pero");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 210, 170, 20));

        jLabel14.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel14.setText("  preciso, como el que tenemos ");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 220, 180, 20));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(null);

        txtCiuHotel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCiuHotelKeyPressed(evt);
            }
        });
        jPanel2.add(txtCiuHotel);
        txtCiuHotel.setBounds(170, 340, 150, 25);

        cbxCiudadHotel.setBackground(new java.awt.Color(255, 219, 95));
        cbxCiudadHotel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cbxCiudadHotelKeyPressed(evt);
            }
        });
        jPanel2.add(cbxCiudadHotel);
        cbxCiudadHotel.setBounds(174, 234, 150, 25);

        jLabel6.setText("Registor Nacional De Turismo :   (RNT)");
        jPanel2.add(jLabel6);
        jLabel6.setBounds(12, 49, 220, 25);

        jLabel15.setText("Nombre del hotel:");
        jPanel2.add(jLabel15);
        jLabel15.setBounds(12, 12, 110, 25);

        txtNomHotel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtNomHotelKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNomHotelKeyTyped(evt);
            }
        });
        jPanel2.add(txtNomHotel);
        txtNomHotel.setBounds(174, 12, 150, 25);

        jLabel7.setText("Direccion :");
        jPanel2.add(jLabel7);
        jLabel7.setBounds(12, 86, 100, 25);

        txtDirHotel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtDirHotelKeyPressed(evt);
            }
        });
        jPanel2.add(txtDirHotel);
        txtDirHotel.setBounds(174, 86, 150, 25);

        jlbCiudad.setText("Ciudad :");
        jPanel2.add(jlbCiudad);
        jlbCiudad.setBounds(12, 234, 100, 25);

        cbxPaises.setBackground(new java.awt.Color(255, 219, 95));
        cbxPaises.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxPaisesItemStateChanged(evt);
            }
        });
        cbxPaises.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cbxPaisesKeyPressed(evt);
            }
        });
        jPanel2.add(cbxPaises);
        cbxPaises.setBounds(174, 160, 150, 25);

        jLabel9.setText("Telefono :");
        jPanel2.add(jLabel9);
        jLabel9.setBounds(12, 123, 100, 25);

        jLabel10.setText("Pais :");
        jPanel2.add(jLabel10);
        jLabel10.setBounds(12, 160, 100, 25);

        jlbDeparProvin.setText("Departamento / Provincia : ");
        jPanel2.add(jlbDeparProvin);
        jlbDeparProvin.setBounds(12, 197, 150, 25);

        txtTelHotel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtTelHotelKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtTelHotelKeyTyped(evt);
            }
        });
        jPanel2.add(txtTelHotel);
        txtTelHotel.setBounds(174, 123, 150, 25);

        txtDepHotel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtDepHotelKeyPressed(evt);
            }
        });
        jPanel2.add(txtDepHotel);
        txtDepHotel.setBounds(10, 340, 150, 25);

        txtRnt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRntFocusLost(evt);
            }
        });
        txtRnt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtRntKeyPressed(evt);
            }
        });
        jPanel2.add(txtRnt);
        txtRnt.setBounds(244, 49, 80, 25);

        cbxDeparHotel.setBackground(new java.awt.Color(255, 219, 95));
        cbxDeparHotel.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxDeparHotelItemStateChanged(evt);
            }
        });
        cbxDeparHotel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cbxDeparHotelKeyPressed(evt);
            }
        });
        jPanel2.add(cbxDeparHotel);
        cbxDeparHotel.setBounds(174, 197, 150, 25);

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 120, 340, 393));

        jPanel3.setBackground(new java.awt.Color(0, 102, 102));

        jLabel4.setBackground(new java.awt.Color(255, 219, 95));
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Los mejores servicios, a los mejores precios!");

        jLabel2.setFont(new java.awt.Font("Arial Black", 1, 44)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("HotelesMas");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(300, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(jLabel4))
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 630, 80));

        jPanel4.setBackground(new java.awt.Color(0, 102, 102));

        btnVolver.setBackground(new java.awt.Color(255, 179, 39));
        btnVolver.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        btnVolver.setText("VOLVER");
        btnVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVolverActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("HotelesMas.com");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 215, Short.MAX_VALUE)
                .addComponent(btnVolver)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnVolver, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 520, 630, 50));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNomHotelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNomHotelKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtRnt.requestFocus();
        }
    }//GEN-LAST:event_txtNomHotelKeyPressed

    private void txtDirHotelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDirHotelKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtTelHotel.requestFocus();
        }
    }//GEN-LAST:event_txtDirHotelKeyPressed

    private void txtTelHotelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTelHotelKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            cbxPaises.requestFocus();
        }
    }//GEN-LAST:event_txtTelHotelKeyPressed

    private void txtDepHotelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDepHotelKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (cbxCiudadHotel.isVisible()) {
                cbxCiudadHotel.requestFocus();
            } else {
                txtCiuHotel.requestFocus();
            }
        }
    }//GEN-LAST:event_txtDepHotelKeyPressed

    private void txtCiuHotelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCiuHotelKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtSlogan.requestFocus();
        }
    }//GEN-LAST:event_txtCiuHotelKeyPressed

    private void btnAgreHotelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAgreHotelMouseClicked
        if (txtNomHotel.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre del hotel!");
            txtNomHotel.requestFocus();
            return;
        }
        if (txtRnt.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese registro nacional de turismo!");
            txtRnt.requestFocus();
            return;
        }
        if (txtDirHotel.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la direccion del hotel!");
            txtDirHotel.requestFocus();
            return;
        }
        if (txtTelHotel.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese telefono de contacto!");
            txtTelHotel.requestFocus();
            return;
        }
        if (cbxPaises.getSelectedItem().toString().equals("Seleccionar")) {
            JOptionPane.showMessageDialog(this, "Seleccione un pais!");
            cbxPaises.requestFocus();
            return;
        }
        if (txtDepHotel.isVisible() && txtDepHotel.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el departamento!");
            txtDepHotel.requestFocus();
            return;
        }
        if (cbxDeparHotel.isVisible() && cbxDeparHotel.getSelectedItem().toString().equals("Seleccionar")) {
            JOptionPane.showMessageDialog(this, "Seleccione un departamento!");
            cbxDeparHotel.requestFocus();
            return;
        }
        if (txtCiuHotel.isVisible() && txtCiuHotel.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la ciudad!");
            txtCiuHotel.requestFocus();
            return;
        }
        if (cbxCiudadHotel.isVisible() && cbxCiudadHotel.getSelectedItem().toString().equals("Seleccionar")) {
            JOptionPane.showMessageDialog(this, "Seleccione una ciudad!");
            cbxCiudadHotel.requestFocus();
            return;
        }
        if (txtSlogan.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese su Slogan!");
            txtSlogan.requestFocus();
            return;
        }

        String xNomHot = txtNomHotel.getText().toUpperCase().trim();
        xNomHot = xNomHot.replaceAll("\\s+", " ");// remplasa los dobles espacios por uno solo
        String xRnt = txtRnt.getText();
        String xDirHot = txtDirHotel.getText();
        String xTelHot = txtTelHotel.getText();
        String xPaisHot = cbxPaises.getSelectedItem().toString();
        String xDepHot = "";
        if (cbxDeparHotel.isVisible()) {
            xDepHot = cbxDeparHotel.getSelectedItem().toString();
        } else {
            xDepHot = txtDepHotel.getText();
        }
        String xciuHot = "";
        if (cbxCiudadHotel.isVisible()) {
            xciuHot = cbxCiudadHotel.getSelectedItem().toString();
        } else {
            xciuHot = txtCiuHotel.getText();
        }
        String xSlogan = txtSlogan.getText();
        String xEstado = "Activo";

        try {
            Hotel xHotel = new Hotel();
            xHotel = con.buscarHotelPorRnt(xRnt);

            if (xHotel != null) {
                JOptionPane.showMessageDialog(this, "Este hotel ya se encuentra registrado");

                AgregarHabitacion ir = new AgregarHabitacion(xHotel);

                ir.setVisible(true);
                this.dispose();

            } else {

                xHotel = con.registrarHotel(xNomHot, xRnt, xDirHot, xTelHot, xciuHot, xDepHot, xPaisHot, xSlogan, xEstado);
                JOptionPane.showMessageDialog(this, "El " + xNomHot + " se registro exitosamente");
                if (xHotel != null) {
                    AgregarHabitacion ir = new AgregarHabitacion(xHotel);
                    ir.enviarTextoJlbNomHotel(xNomHot);
                    ir.enviarTextoJlbIdHotel(IdH);
                    ir.setVisible(true);
                    this.dispose();

                } else {
                    JOptionPane.showMessageDialog(this, "Hubo un error, intente mas tarde");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ocurrió un error al registrar el hotel: " + e.getMessage());
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnAgreHotelMouseClicked

    private void txtRntKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRntKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtDirHotel.requestFocus();
        }
    }//GEN-LAST:event_txtRntKeyPressed

    private void txtNomHotelKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNomHotelKeyTyped
        char c = evt.getKeyChar();
        if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && (c < ' ' || c > ' ')) {
            evt.consume();
        }
    }//GEN-LAST:event_txtNomHotelKeyTyped

    private void txtTelHotelKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTelHotelKeyTyped
        char c = evt.getKeyChar();
        if ((c < '0' || c > '9')) {
            evt.consume();
        }
    }//GEN-LAST:event_txtTelHotelKeyTyped

    private void btnVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverActionPerformed
        Iniciar ir = new Iniciar();
        ir.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnVolverActionPerformed

    private void cbxPaisesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxPaisesItemStateChanged
        String selectedItem = (String) cbxPaises.getSelectedItem();

        if (selectedItem != null) {
            String[] cadena = selectedItem.split("\\(");
            String paisSP = cadena[0].trim();
            if (!cbxPaises.getSelectedItem().toString().equals("Seleccionar")) {
                jlbDeparProvin.setVisible(true);
                jlbCiudad.setVisible(true);
                if (paisSP.equals("COLOMBIA")) {
                    con.traerDepartementos(cbxDeparHotel, null);
                    cbxDeparHotel.setVisible(true);
                    cbxCiudadHotel.setVisible(true);
                    txtDepHotel.setVisible(false);
                    txtCiuHotel.setVisible(false);
                } else {
                    cbxDeparHotel.setVisible(false);
                    cbxCiudadHotel.setVisible(false);
                    txtDepHotel.setVisible(true);
                    txtCiuHotel.setVisible(true);
                    txtDepHotel.setBounds(174, 197, 150, 25);
                    txtCiuHotel.setBounds(174, 234, 150, 25);
                }

            }

        }
    }//GEN-LAST:event_cbxPaisesItemStateChanged

    private void cbxDeparHotelItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxDeparHotelItemStateChanged
        if (cbxDeparHotel.getSelectedItem() != null && !cbxDeparHotel.getSelectedItem().toString().equals("Seleccionar")) {
            String nomDepar = cbxDeparHotel.getSelectedItem().toString();
            int idD = con.obtenerIdDepartamento(nomDepar);
            con.traerCiudadesPorDepartamento(cbxCiudadHotel, idD);
        } else if (cbxDeparHotel.getSelectedItem() != null && cbxDeparHotel.getSelectedItem().toString().equals("Seleccionar")) {
            cbxCiudadHotel.removeAllItems();
        }
    }//GEN-LAST:event_cbxDeparHotelItemStateChanged

    private void cbxPaisesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbxPaisesKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (cbxDeparHotel.isVisible()) {
                cbxDeparHotel.requestFocus();
            } else {
                txtDepHotel.requestFocus();
            }
        }
    }//GEN-LAST:event_cbxPaisesKeyPressed

    private void cbxDeparHotelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbxDeparHotelKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (cbxCiudadHotel.isVisible()) {
                cbxCiudadHotel.requestFocus();
            } else {
                txtCiuHotel.requestFocus();
            }
        }
    }//GEN-LAST:event_cbxDeparHotelKeyPressed

    private void cbxCiudadHotelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbxCiudadHotelKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtSlogan.requestFocus();
        }
    }//GEN-LAST:event_cbxCiudadHotelKeyPressed

    private void txtRntFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRntFocusLost

        String xRnt = txtRnt.getText();

        Hotel hotel = new Hotel();
        try {
            hotel = con.buscarHotelPorRnt(xRnt);

            if (hotel != null) {

                JOptionPane.showMessageDialog(this, """
                                                    En hora buena!
                                                    Ya haz registrado este hotel como """ + " " + hotel.getNombreHotel() + "\n"
                        + "ahora puedes seguir con el proceso"
                        + "");

                AgregarHabitacion ir = new AgregarHabitacion(hotel);

                ir.setVisible(true);
                this.dispose();

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ocurrió un error al registrar el hotel: " + e.getMessage());
            e.printStackTrace();
        }
    }//GEN-LAST:event_txtRntFocusLost

    private void btnAgreHotelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAgreHotelMouseEntered
        btnAgreHotel.setBackground(new Color(255, 219, 95));
    }//GEN-LAST:event_btnAgreHotelMouseEntered

    private void btnAgreHotelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAgreHotelMouseExited
        btnAgreHotel.setBackground(new Color(255, 179, 39));
    }//GEN-LAST:event_btnAgreHotelMouseExited

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgreHotel;
    private javax.swing.JButton btnVolver;
    private javax.swing.JComboBox<String> cbxCiudadHotel;
    private javax.swing.JComboBox<String> cbxDeparHotel;
    private javax.swing.JComboBox<String> cbxPaises;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel jlbCiudad;
    private javax.swing.JLabel jlbDeparProvin;
    private javax.swing.JLabel jlbLogo;
    private javax.swing.JTextField txtCiuHotel;
    private javax.swing.JTextField txtDepHotel;
    private javax.swing.JTextField txtDirHotel;
    private javax.swing.JTextField txtNomHotel;
    private javax.swing.JTextField txtRnt;
    private javax.swing.JTextArea txtSlogan;
    private javax.swing.JTextField txtTelHotel;
    // End of variables declaration//GEN-END:variables
}
