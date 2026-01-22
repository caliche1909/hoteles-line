package vistas;

import conectar.Canectar;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public final class Gastos extends javax.swing.JDialog {

    int maxCaracteres = 100;
    int idTurno;

    public Gastos(java.awt.Frame parent, boolean modal, int idTurno) {
        super(parent, modal);
        this.idTurno = idTurno;
        initComponents();
        jlbMosTurno.setText("Nuevo gasto para el turno N° " + idTurno);
        jlbColaborador.setVisible(false);
        cbxColaborador.setVisible(false);
        this.setLocationRelativeTo(null);
        aplicarFormatoPuntosMil(txtValorGasto);
        fechaHoy();
    }

    public void fechaHoy() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("EEE dd-MMMM-yyyy");
        LocalDate fecha = LocalDate.now();
        String FechaF = fecha.format(formato);
        jlbFecha.setText(FechaF);
    }

    public void aplicarFormatoPuntosMil(JTextField textField) {
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent evt) {
                char c = evt.getKeyChar();
                // Solo permite números
                if (c < '0' || c > '9') {
                    evt.consume();
                    return;
                }
            }

            @Override
            public void keyReleased(KeyEvent evt) {
                formatoPuntosMil(textField);
            }
        });
    }

    private void formatoPuntosMil(JTextField textField) {
        String text = textField.getText();
        int len = text.length();
        StringBuilder newText = new StringBuilder();

        int count = 0;
        for (int i = len - 1; i >= 0; i--) {
            if (count == 3) {
                newText.insert(0, ',');
                count = 0;
            }

            char ch = text.charAt(i);
            if (ch != ',') {
                newText.insert(0, ch);
                count++;
            }
        }

        // Si el formato es diferente, actualiza el texto en textField
        if (!newText.toString().equals(text)) {
            textField.setText(newText.toString());
            textField.setCaretPosition(newText.length());
        }
    }

    public void registrarGasto() {
        if (txtValorGasto.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el valor del gasto");
            txtValorGasto.requestFocus();
            return;
        }
        if (cbxTipoGasto.getSelectedItem().equals("Seleccione")) {
            JOptionPane.showMessageDialog(this, "Seleccione el tipo de gasto");
            cbxTipoGasto.requestFocus();
            return;
        }
        if (txaDescripcion.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el concepto del gasto");
            txaDescripcion.requestFocus();
            return;
        }
        Canectar con = new Canectar();
        double xValorGasto = Double.parseDouble(txtValorGasto.getText().replace(",", ""));
        String xTipoGasto = cbxTipoGasto.getSelectedItem().toString();
        String xDescripcion = txaDescripcion.getText();

        if (con.registrarGasto(xValorGasto, xTipoGasto, xDescripcion, idTurno)) {
            Object[] opciones = {"Nuevo Gasto", "Salir"};
            
            int eleccion = JOptionPane.showOptionDialog(this,
                    "Registro de gasto exitoso. ¿Desea agregar un nuevo gasto?",
                    "Gasto Registrado",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, opciones, opciones[0]);

            if (eleccion == JOptionPane.YES_OPTION) {
                cbxTipoGasto.setSelectedIndex(0);
                txtValorGasto.setText("");
                txaDescripcion.setText("");
            } else if (eleccion == JOptionPane.NO_OPTION) {                
                this.setVisible(false);
            }
        }

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jlbNameUsu = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jlbFecha = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jlbRollUser = new javax.swing.JLabel();
        btnRegistrarGasto = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jlbColaborador = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtValorGasto = new javax.swing.JTextField();
        cbxTipoGasto = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaDescripcion = new javax.swing.JTextArea();
        jblContCar = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        cbxColaborador = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jlbMosTurno = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("INGRESO DE GASTOS");
        setResizable(false);
        setType(java.awt.Window.Type.POPUP);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(0, 102, 102));

        jlbNameUsu.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbNameUsu.setForeground(new java.awt.Color(255, 255, 255));
        jlbNameUsu.setText("Carlos Moran");

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("FECHA :");

        jlbFecha.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbFecha.setForeground(new java.awt.Color(255, 255, 255));

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText(":");

        jlbRollUser.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbRollUser.setForeground(new java.awt.Color(255, 255, 255));
        jlbRollUser.setText("ADMINISTRADOR");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlbFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jlbRollUser, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlbNameUsu, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlbFecha, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jlbNameUsu, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jlbRollUser, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        btnRegistrarGasto.setBackground(new java.awt.Color(0, 51, 51));
        btnRegistrarGasto.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        btnRegistrarGasto.setForeground(new java.awt.Color(255, 255, 255));
        btnRegistrarGasto.setText("REGISTRAR GASTO");
        btnRegistrarGasto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnRegistrarGastoMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnRegistrarGastoMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnRegistrarGastoMouseExited(evt);
            }
        });
        btnRegistrarGasto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnRegistrarGastoKeyPressed(evt);
            }
        });

        btnCancelar.setBackground(new java.awt.Color(0, 51, 51));
        btnCancelar.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        btnCancelar.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelar.setText("CANCELAR");
        btnCancelar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCancelarMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnCancelarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnCancelarMouseExited(evt);
            }
        });

        jLabel5.setText("VALOR :");

        jlbColaborador.setText("COLABORADOR :");

        jLabel7.setText("DESCRIPCION :");

        txtValorGasto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtValorGastoKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtValorGastoKeyTyped(evt);
            }
        });

        cbxTipoGasto.setBackground(new java.awt.Color(255, 179, 39));
        cbxTipoGasto.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione", "Aseo", "Servicios", "Nomina", "Mantenimiento", "Otro" }));
        cbxTipoGasto.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxTipoGastoItemStateChanged(evt);
            }
        });

        txaDescripcion.setColumns(20);
        txaDescripcion.setRows(5);
        txaDescripcion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txaDescripcionKeyTyped(evt);
            }
        });
        jScrollPane1.setViewportView(txaDescripcion);

        jLabel8.setText("TIPO DE GASTO :");

        cbxColaborador.setBackground(new java.awt.Color(255, 179, 39));
        cbxColaborador.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione", "Aseo", "Servicios", "Nomina", "Mantenimiento" }));
        cbxColaborador.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxColaboradorItemStateChanged(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(0, 102, 102));

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("HotelesMas.com");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addContainerGap())
        );

        jlbMosTurno.setFont(new java.awt.Font("sansserif", 0, 10)); // NOI18N
        jlbMosTurno.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlbMosTurno.setText("Nuevo gasto para el turno N° 1000");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8)
                            .addComponent(jlbColaborador))
                        .addGap(33, 33, 33)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbxTipoGasto, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbxColaborador, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(btnRegistrarGasto, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(110, 110, 110)
                            .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel7)
                                .addComponent(jLabel5))
                            .addGap(39, 39, 39)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(1, 1, 1)
                                    .addComponent(txtValorGasto, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jblContCar, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(82, Short.MAX_VALUE))
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jlbMosTurno, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlbMosTurno)
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbxTipoGasto, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlbColaborador, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxColaborador, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtValorGasto, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jblContCar, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRegistrarGasto, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtValorGastoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorGastoKeyTyped
        char c = evt.getKeyChar();
        if ((c < '0' || c > '9'))
            evt.consume();
    }//GEN-LAST:event_txtValorGastoKeyTyped

    private void txtValorGastoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorGastoKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txaDescripcion.requestFocus();
        }
    }//GEN-LAST:event_txtValorGastoKeyPressed

    private void txaDescripcionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txaDescripcionKeyTyped
        String text = txaDescripcion.getText();
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].length() > 40) {
                String line = lines[i];
                int lastSpaceIndex = line.lastIndexOf(' ', 40);
                if (lastSpaceIndex == -1) {
                    lastSpaceIndex = 40;
                }
                lines[i] = line.substring(0, lastSpaceIndex) + "\n" + line.substring(lastSpaceIndex).trim();
            }
        }
        txaDescripcion.setText(String.join("\n", lines));

        if (text.length() >= maxCaracteres) {
            evt.consume();
        } else {
            jblContCar.setText((maxCaracteres - text.length() - 1) + "/" + maxCaracteres);
        }
    }//GEN-LAST:event_txaDescripcionKeyTyped

    private void btnRegistrarGastoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRegistrarGastoMouseClicked
        registrarGasto();
    }//GEN-LAST:event_btnRegistrarGastoMouseClicked

    private void btnRegistrarGastoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnRegistrarGastoKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            registrarGasto();
        }
    }//GEN-LAST:event_btnRegistrarGastoKeyPressed

    private void btnCancelarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCancelarMouseClicked
        this.setVisible(false);
    }//GEN-LAST:event_btnCancelarMouseClicked

    private void cbxTipoGastoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxTipoGastoItemStateChanged
        txtValorGasto.setText("");
        if (cbxTipoGasto.getSelectedItem().toString().equals("Nomina")) {
            Canectar con = new Canectar();
            con.usuariosParaCbx(cbxColaborador);
            jlbColaborador.setVisible(true);
            cbxColaborador.setVisible(true);
            cbxColaborador.requestFocus();
        } else {
            jlbColaborador.setVisible(false);
            cbxColaborador.setVisible(false);
            txtValorGasto.requestFocus();
        }
    }//GEN-LAST:event_cbxTipoGastoItemStateChanged

    private void cbxColaboradorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxColaboradorItemStateChanged
        if (cbxColaborador.getSelectedIndex() != 0) {
            txtValorGasto.requestFocus();
        }
    }//GEN-LAST:event_cbxColaboradorItemStateChanged

    private void btnRegistrarGastoMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRegistrarGastoMouseEntered
        btnRegistrarGasto.setBackground(new Color(0, 102, 102));
    }//GEN-LAST:event_btnRegistrarGastoMouseEntered

    private void btnRegistrarGastoMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRegistrarGastoMouseExited
        btnRegistrarGasto.setBackground(new Color(0, 51, 51));
    }//GEN-LAST:event_btnRegistrarGastoMouseExited

    private void btnCancelarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCancelarMouseEntered
        btnCancelar.setBackground(new Color(0, 102, 102));
    }//GEN-LAST:event_btnCancelarMouseEntered

    private void btnCancelarMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCancelarMouseExited
        btnCancelar.setBackground(new Color(0, 51, 51));
    }//GEN-LAST:event_btnCancelarMouseExited
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnRegistrarGasto;
    private javax.swing.JComboBox<String> cbxColaborador;
    private javax.swing.JComboBox<String> cbxTipoGasto;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jblContCar;
    private javax.swing.JLabel jlbColaborador;
    private javax.swing.JLabel jlbFecha;
    private javax.swing.JLabel jlbMosTurno;
    private javax.swing.JLabel jlbNameUsu;
    private javax.swing.JLabel jlbRollUser;
    private javax.swing.JTextArea txaDescripcion;
    private javax.swing.JTextField txtValorGasto;
    // End of variables declaration//GEN-END:variables
}
