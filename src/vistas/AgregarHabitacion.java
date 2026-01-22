package vistas;

import conectar.Canectar;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import modelo.Hotel;

public final class AgregarHabitacion extends javax.swing.JFrame {

    int xTotalHab = 0;
    int xTotalHabCont = 0;
    String xTotalHabis = "";
    String numHabitacion;
    String tipHabitacion;
    String estado = "Libre";
    Canectar con = new Canectar();

    PreparedStatement ps;
    ResultSet rs;
    int IdHo;
    Hotel xhotel;

    public AgregarHabitacion(Hotel hotel) {
        if (hotel != null) {
            this.xhotel = hotel;

            initComponents();
            jpnInf.setVisible(false);
            this.setLocationRelativeTo(null);
            setIconImage(new ImageIcon(getClass().getResource("/img/LogoIcono.png")).getImage());

            enviarTextoJlbIdHotel(hotel.getIdHoteles());
            enviarTextoJlbNomHotel(hotel.getNombreHotel());

        } else {
            JOptionPane.showMessageDialog(this, "Hotel no disponible");
            this.dispose();
        }
    }

    public void enviarTextoJlbNomHotel(String text) {
        jlbNomHotel.setText(text);
    }

    public void enviarTextoJlbIdHotel(int numId) {
        jlbIdHotel.setText(Integer.toString(numId));
    }

    public void inicioHabis() {

        String numHabitacion = "";
        String tipoHabitacion = "";
        java.sql.Connection dom = null;
        try {

            if (txtCantHab.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese la cantidad de habitaciones que existen en su hotel");
                txtCantHab.requestFocus();
                return;

            } else {
                int idHotel = xhotel.getIdHoteles();
                xTotalHabis = txtCantHab.getText();
                dom = con.conexion();
                String sql1 = "SELECT COUNT(*) FROM habitaciones WHERE Fk_Id_Hotel = ?";
                String sql2 = "SELECT Num_Habitacion, Tipo_Habitacion FROM habitaciones WHERE Fk_Id_Hotel = ? ORDER BY Id_Habitacion DESC LIMIT 1";
                // Obtener la cuenta de registros
                ps = dom.prepareStatement(sql1);
                ps.setInt(1, idHotel);
                rs = ps.executeQuery();
                if (rs.next()) {
                    xTotalHabCont = rs.getInt(1);
                }
                int Total = Integer.parseInt(xTotalHabis);
                if (Total == xTotalHabCont) {
                    JOptionPane.showMessageDialog(this, "Ya se han creado todas las habitaciones de este hotel");
                    FrmRegistrarUsuarios frmRegistrarUsuarios = new FrmRegistrarUsuarios(null, true, xhotel, true);

                    frmRegistrarUsuarios.setVisible(true);

                    this.dispose();
                }
                xTotalHab = Integer.parseInt(txtCantHab.getText());
                xTotalHab = xTotalHab - xTotalHabCont;
                // Obtener Num_Habitacion y Tipo_Habitacion del último registro
                ps = dom.prepareStatement(sql2);
                ps.setInt(1, idHotel);
                rs = ps.executeQuery();
                if (rs.next()) {
                    numHabitacion = rs.getString("Num_Habitacion");
                    tipoHabitacion = rs.getString("Tipo_Habitacion");
                    jlbUltHabAgregada.setText("Ultima habitacion agregada: " + numHabitacion + "  Tipo: " + tipoHabitacion);
                } else {
                    jlbUltHabAgregada.setText("No se han agregado habitaciones");
                }
                jpnInf.setVisible(true);
                jpnSup.setVisible(false);
            }

            if (xTotalHab == 1) {
                jlbFaltAgre.setText("Falta agregar " + xTotalHab + " habitacion");
            } else if (xTotalHab > 1 && xTotalHabCont <= xTotalHab) {
                jlbFaltAgre.setText("Faltan agregar " + xTotalHab + " habitaciones");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al obtener la información: " + e.getMessage());
        } finally {
            try {
                if (dom != null) {
                    dom.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error al cerrar la conexion: " + e.getMessage());
            }
        }

        System.out.println("Faltan agregar " + xTotalHab);
        System.out.println("Último registro - Num_Habitacion: " + numHabitacion + ", Tipo_Habitacion: " + tipoHabitacion);

    }

    public void agregarHabitacion() {
        if (jbxTipHab.getSelectedItem().toString().equals("Seleccione")) {
            JOptionPane.showMessageDialog(this, "Seleccione el tipo de habitacion");
            jbxTipHab.requestFocus();
            return;
        }
        if (txtNumHab.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite el numero de habitacion");
            txtNumHab.requestFocus();
            return;
        }
        IdHo = xhotel.getIdHoteles();
        numHabitacion = txtNumHab.getText();
        tipHabitacion = jbxTipHab.getSelectedItem().toString();
        String tipoHabitacion = jbxTipHab.getSelectedItem().toString();

        java.sql.Connection dom = null;
        try {
            dom = con.conexion();
            ps = dom.prepareStatement("SELECT Num_Habitacion, Fk_Id_Hotel FROM habitaciones WHERE Num_Habitacion = ? And Fk_Id_Hotel = ?");
            ps.setString(1, numHabitacion);
            ps.setInt(2, IdHo);

            rs = ps.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "La Habitacion " + numHabitacion + " ya esta creada!");
                txtNumHab.setText("");
                jbxTipHab.setSelectedIndex(0);
            } else {
                if (con.registrarHabitacion(numHabitacion, tipHabitacion, estado, IdHo)) {
                    JOptionPane.showMessageDialog(this, "La habitacion " + numHabitacion + " se creo exitosamente");
                    txtNumHab.setText("");
                    jbxTipHab.setSelectedIndex(0);
                    jlbUltHabAgregada.setText("Ultima habitacion agregada: " + numHabitacion + "  Tipo: " + tipoHabitacion);
                    xTotalHab--;
                    if (xTotalHab == 1) {
                        jlbFaltAgre.setText("Falta agregar " + xTotalHab + " habitacion");
                    } else if (xTotalHab > 1 && xTotalHabCont <= xTotalHab) {
                        jlbFaltAgre.setText("Faltan agregar " + xTotalHab + " habitaciones");
                    } else if (xTotalHab < 1) {
                        JOptionPane.showMessageDialog(null, "Felicitaciones! Ya haz teminado de agragar las " + xTotalHabis + " habitaciones");
                        FrmRegistrarUsuarios frmRegistrarUsuarios = new FrmRegistrarUsuarios(null, true, xhotel, true);

                        frmRegistrarUsuarios.setVisible(true);
                        this.dispose();
                    }
                }
            }
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Ocurrió un error al buscar el cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (dom != null) {
                    dom.close();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al cerrar la conexión a la base de datos: " + e.getMessage());
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator1 = new javax.swing.JSeparator();
        panelFondo = new javax.swing.JPanel();
        jlbNomHotel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnCancelar = new javax.swing.JButton();
        jpnSup = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtCantHab = new javax.swing.JTextField();
        btnAgreCantHab = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        jpnInf = new javax.swing.JPanel();
        jlbUltHabAgregada = new javax.swing.JLabel();
        jlbFaltAgre = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtNumHab = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jbxTipHab = new javax.swing.JComboBox<>();
        jSeparator2 = new javax.swing.JSeparator();
        jlbIdHotel = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnAgreHab = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panelFondo.setBackground(new java.awt.Color(255, 255, 255));

        jlbNomHotel.setFont(new java.awt.Font("Dialog", 1, 48)); // NOI18N
        jlbNomHotel.setText("Hotel Doral Plaza");

        jLabel2.setBackground(new java.awt.Color(0, 0, 0));
        jLabel2.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 102, 102));
        jLabel2.setText("HotelesMas.com");

        btnCancelar.setBackground(new java.awt.Color(255, 179, 39));
        btnCancelar.setText("CANCELAR");

        jpnSup.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setText("Cuantas Habitaciones Tiene Tu Hotel ?");

        txtCantHab.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCantHabKeyPressed(evt);
            }
        });

        btnAgreCantHab.setBackground(new java.awt.Color(255, 219, 95));
        btnAgreCantHab.setText("AGREGAR");
        btnAgreCantHab.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAgreCantHabMouseClicked(evt);
            }
        });
        btnAgreCantHab.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnAgreCantHabKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jpnSupLayout = new javax.swing.GroupLayout(jpnSup);
        jpnSup.setLayout(jpnSupLayout);
        jpnSupLayout.setHorizontalGroup(
            jpnSupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnSupLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCantHab, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 82, Short.MAX_VALUE)
                .addComponent(btnAgreCantHab, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jpnSupLayout.setVerticalGroup(
            jpnSupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnSupLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpnSupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCantHab, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAgreCantHab, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpnInf.setBackground(new java.awt.Color(255, 255, 255));

        jlbUltHabAgregada.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jlbUltHabAgregada.setForeground(new java.awt.Color(0, 51, 51));

        jlbFaltAgre.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbFaltAgre.setForeground(new java.awt.Color(0, 51, 51));
        jlbFaltAgre.setText("Faltan Agregar ");

        jLabel9.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel9.setText("Numero de habitacion:");

        txtNumHab.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtNumHabKeyPressed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel10.setText("Tipo de habitacion:");

        jbxTipHab.setBackground(new java.awt.Color(255, 219, 95));
        jbxTipHab.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar", "Individual", "Matrimonial", "Dos camas", "Tres camas", " " }));
        jbxTipHab.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbxTipHabKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jpnInfLayout = new javax.swing.GroupLayout(jpnInf);
        jpnInf.setLayout(jpnInfLayout);
        jpnInfLayout.setHorizontalGroup(
            jpnInfLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator2)
            .addGroup(jpnInfLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpnInfLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlbFaltAgre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlbUltHabAgregada, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jpnInfLayout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNumHab, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbxTipHab, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jpnInfLayout.setVerticalGroup(
            jpnInfLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnInfLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jlbFaltAgre)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jlbUltHabAgregada, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addGroup(jpnInfLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNumHab, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbxTipHab, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(54, 54, 54))
        );

        jlbIdHotel.setForeground(new java.awt.Color(0, 51, 51));

        jLabel7.setForeground(new java.awt.Color(0, 51, 51));
        jLabel7.setText("ID Hotel :");

        jPanel1.setBackground(new java.awt.Color(0, 102, 102));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 14, Short.MAX_VALUE)
        );

        btnAgreHab.setBackground(new java.awt.Color(0, 51, 51));
        btnAgreHab.setForeground(new java.awt.Color(255, 255, 255));
        btnAgreHab.setText("AGREGAR HABITACION");
        btnAgreHab.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAgreHabMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnAgreHabMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnAgreHabMouseExited(evt);
            }
        });
        btnAgreHab.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnAgreHabKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout panelFondoLayout = new javax.swing.GroupLayout(panelFondo);
        panelFondo.setLayout(panelFondoLayout);
        panelFondoLayout.setHorizontalGroup(
            panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFondoLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlbNomHotel, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelFondoLayout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlbIdHotel, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCancelar)
                        .addContainerGap())))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFondoLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAgreHab)
                .addGap(171, 171, 171))
            .addGroup(panelFondoLayout.createSequentialGroup()
                .addComponent(jpnSup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 19, Short.MAX_VALUE))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator3)
            .addComponent(jpnInf, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelFondoLayout.setVerticalGroup(
            panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFondoLayout.createSequentialGroup()
                .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFondoLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelFondoLayout.createSequentialGroup()
                                .addGap(80, 80, 80)
                                .addComponent(jpnSup, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelFondoLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jlbNomHotel, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelFondoLayout.createSequentialGroup()
                        .addGap(80, 80, 80)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addComponent(jpnInf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAgreHab)
                .addGap(29, 29, 29)
                .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlbIdHotel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelFondo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelFondo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtCantHabKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantHabKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnAgreCantHab.requestFocus();
        }
    }//GEN-LAST:event_txtCantHabKeyPressed

    private void btnAgreCantHabMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAgreCantHabMouseClicked
        inicioHabis();
    }//GEN-LAST:event_btnAgreCantHabMouseClicked

    private void btnAgreHabMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAgreHabMouseClicked
        agregarHabitacion();
    }//GEN-LAST:event_btnAgreHabMouseClicked

    private void txtNumHabKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNumHabKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jbxTipHab.requestFocus();
        }
    }//GEN-LAST:event_txtNumHabKeyPressed

    private void btnAgreCantHabKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnAgreCantHabKeyPressed
        inicioHabis();
    }//GEN-LAST:event_btnAgreCantHabKeyPressed

    private void jbxTipHabKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbxTipHabKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnAgreHab.requestFocus();
        }
    }//GEN-LAST:event_jbxTipHabKeyPressed

    private void btnAgreHabKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnAgreHabKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            agregarHabitacion();
        }
    }//GEN-LAST:event_btnAgreHabKeyPressed

    private void btnAgreHabMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAgreHabMouseEntered
        btnAgreHab.setBackground(new Color(0, 102, 102));
    }//GEN-LAST:event_btnAgreHabMouseEntered

    private void btnAgreHabMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAgreHabMouseExited
        btnAgreHab.setBackground(new Color(0, 51, 51));
    }//GEN-LAST:event_btnAgreHabMouseExited


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgreCantHab;
    private javax.swing.JButton btnAgreHab;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JComboBox<String> jbxTipHab;
    private javax.swing.JLabel jlbFaltAgre;
    private javax.swing.JLabel jlbIdHotel;
    private javax.swing.JLabel jlbNomHotel;
    private javax.swing.JLabel jlbUltHabAgregada;
    private javax.swing.JPanel jpnInf;
    private javax.swing.JPanel jpnSup;
    private javax.swing.JPanel panelFondo;
    private javax.swing.JTextField txtCantHab;
    private javax.swing.JTextField txtNumHab;
    // End of variables declaration//GEN-END:variables
}
