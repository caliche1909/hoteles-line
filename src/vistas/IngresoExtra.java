package vistas;

import conectar.Canectar;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.SwingUtilities;
import modelo.Cliente;
import modelo.Hotel;
import modelo.MensajesWATest;
import modelo.UsuarioOperando;

public final class IngresoExtra extends javax.swing.JDialog {

    int maxCaracteres = 100;
    Canectar con = new Canectar();
    String estadoVerificacion = "No Verificado";
    private UsuarioOperando usus;
    private Hotel hotel;
    Cliente clienteGlobal = new Cliente();

    public IngresoExtra(java.awt.Frame parent, boolean modal, UsuarioOperando usus, Hotel hotel) {
        super(parent, modal);
        this.usus = usus;
        this.hotel = hotel;
        initComponents();

        jlbVistoMalo.setVisible(false);
        jlbVistoBueno.setVisible(false);
        btnVerificarWa.setVisible(false);
        jlbMensajeError.setVisible(false);
        jlbMensajeExitoso.setVisible(false);

        rsscalelabel.RSScaleLabel.setScaleLabel(jlbVistoBueno, "src/img/VistoBueno.png");
        rsscalelabel.RSScaleLabel.setScaleLabel(jlbVistoMalo, "src/img/vistoMalo.png");

        txtNumDoc.requestFocus();
        this.setLocationRelativeTo(null);
        aplicarFormatoPuntosMil(txtValorExtra);
        fechaHoy();
        con.traerPaises(cbxNacionalidad, null, null);
        con.traerTiposdeDocumento(cbxTipoDoc);
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

    private boolean verificarCampos() {

        if (cbxTipoDoc.getSelectedItem().equals("Seleccione")) {
            JOptionPane.showMessageDialog(this, "Seleccione un tipo de documento ");
            cbxTipoDoc.requestFocus();
            return false;
        }
        if (txtNumDoc.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el numero de documento ");
            txtNumDoc.requestFocus();
            return false;
        }
        if (txtNombres.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre ");
            txtNombres.requestFocus();
            return false;
        }
        if (txtApellidos.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el Apellido ");
            txtApellidos.requestFocus();
            return false;
        }
        if (cbxNacionalidad.getSelectedItem().equals("Seleccione")) {
            JOptionPane.showMessageDialog(this, "Seleccione una nacionalidad  ");
            cbxNacionalidad.requestFocus();
            return false;
        }
        if (txtTelefono.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el numero de telefono ");
            txtTelefono.requestFocus();
            return false;
        }
        if (txtProfesion.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese una profesion ");
            txtProfesion.requestFocus();
            return false;
        }
        if (txtValorExtra.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el valor ");
            txtValorExtra.requestFocus();
            return false;
        }
        if (cbxTipoPago.getSelectedItem().toString().equals("Seleccionar")) {
            JOptionPane.showMessageDialog(this, "Seleccióne un tipo de pago ");
            cbxTipoPago.requestFocus();
            return false;
        }
        if (txaDescripcion.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la descripcion del ingreso ");
            txaDescripcion.requestFocus();
            return false;
        }
        if (txaDescripcion.getText().length() <= 30) {
            JOptionPane.showMessageDialog(this, "El concepto de la descripcion no es claro ");
            txaDescripcion.setText("");
            txaDescripcion.requestFocus();
            return false;
        }

        if (jdchNacimiento.isVisible() && jdchNacimiento.getDate() == null) {
            JOptionPane.showMessageDialog(this, "La fehca de nacimiento no puede quedar vacia!");
            jdchNacimiento.requestFocus();
            return false;
        }
        return true;
    }

    public void mensajeWARes(Cliente cliente) {

        CompletableFuture<String> resultado = enviarMensajeWA(cliente);
        resultado.thenAccept(res -> {
            SwingUtilities.invokeLater(() -> {

                int idCliente = cliente.getId_Cliente();
                con.actualizarEstadoVerificacion(idCliente, res);

            });
        }).exceptionally(ex -> {
            SwingUtilities.invokeLater(() -> {

            });
            return null;
        });

    }

    public CompletableFuture<String> enviarMensajeWA(Cliente cliente) {
        return CompletableFuture.supplyAsync(() -> {
            String nombreCliente = "*" + cliente.getNombres().split(" ")[0] + "*";
            String telefonoWA = txtIndicativo.getText() + txtTelefono.getText();
            String fechaIngreso = jlbFecha.getText();

            String MensajeWhatsApp = "*" + hotel.getNombreHotel().toUpperCase() + "*.\n"
                    + "*FECHA: *\n " + fechaIngreso + "\n\n"
                    + "Cordial saludo " + nombreCliente.toUpperCase() + "🫱🏻‍🫲🏼\n"
                    + "Gracias por tu pago\n"
                    + "*Valor Pagado* $ " + txtValorExtra.getText() + "\n\n"
                    + "Cualquier inquietud puede coumicarse con nosotros a los siguiente numeros:\n"
                    + "*RECEPCION:*\n"
                    + "     📞6027418969\n"
                    + "     📱+573145519811\n"
                    + "*ADMINISTRACION:*\n"
                    + "     📱+573232951780\n\n"
                    + "*Atentamente:*\n"
                    + "" + hotel.getNombreHotel() + "\n"
                    + "" + hotel.getDireccionHotel() + "\n"
                    + "" + hotel.getCiudadHotel() + " - " + hotel.getDepartamentoHotel() + " - " + hotel.getPaisHotel() + "";

            MensajesWATest enviar = new MensajesWATest();

            try {
                boolean mensajeEnviado = enviar.sendMessages(telefonoWA, MensajeWhatsApp);
                if (mensajeEnviado) {
                    jlbMensajeExitoso.setVisible(true);
                    btnVerificarWa.setVisible(false);
                    txtTelefono.setFocusable(false);
                    return "Verificado";

                } else {
                    txtTelefono.setFocusable(true);
                    jlbMensajeError.setVisible(true);
                    return "No Verificado";
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error al codificar el mensaje: " + ex.getMessage());
                txtTelefono.setFocusable(true);
                jlbMensajeError.setVisible(true);
            }

            return estadoVerificacion;
        });
    }

    public void guardarIngreso() {
        if (!verificarCampos()) {
            return;
        }

        Cliente clienteARegistraR = new Cliente();

        clienteARegistraR.setNum_Documento(txtNumDoc.getText());
        clienteARegistraR.setTipo_Documento(cbxTipoDoc.getSelectedItem().toString());
        if (jdchNacimiento.isVisible()) {
            Date fechaNacimiento = jdchNacimiento.getDate();
            if (fechaNacimiento != null) {
                clienteARegistraR.setFecha_Nacimiento(fechaNacimiento);

                // Convertir Date a LocalDate
                LocalDate fechaNacimientoLocal = fechaNacimiento.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate fechaActual = LocalDate.now();

                // Calcular la edad
                int edad = Period.between(fechaNacimientoLocal, fechaActual).getYears();
                clienteARegistraR.setEdad(edad);
                System.out.println("edad del cliente: " + edad);
            }
        }
        clienteARegistraR.setNombres(txtNombres.getText());
        String apellidos = txtApellidos.getText();
        clienteARegistraR.setApellidos(apellidos);
        clienteARegistraR.setNacionalidad(cbxNacionalidad.getSelectedItem().toString());
        String tel = txtIndicativo.getText() + txtTelefono.getText();
        clienteARegistraR.setTelefono(tel);
        clienteARegistraR.setProfesion(txtProfesion.getText());
        clienteARegistraR.setEstado_Verificacion(estadoVerificacion);

        String primerApellido = "";
        String segundoApellido = "";
        String[] cantApes = apellidos.split(" ");
        if (cantApes.length >= 2) {
            primerApellido = cantApes[0];
            segundoApellido = cantApes[1];
        } else if (cantApes.length == 1) {
            primerApellido = cantApes[0];
            segundoApellido = " ";
        }

        Cliente clienteEnRegistro = new Cliente();

        if (clienteGlobal != null) {
            //vamos a actualizar un cliente viejo
            clienteEnRegistro = con.actualizarClienteDB(clienteARegistraR, clienteGlobal.getId_Cliente());

        } else {

            clienteEnRegistro = con.registrarCliente(clienteARegistraR);

        }

        if (clienteEnRegistro != null) {

            int idTurno = con.obtenerUltimoIdTurno("Id_Turno", "turno", "Id_Turno");
            String tipoPago = cbxTipoPago.getSelectedItem().toString();
            double ingresoExtra = Double.parseDouble(txtValorExtra.getText().replace(",", ""));
            String descripcion = txaDescripcion.getText();

            boolean registroIngresoExtra = con.registrarIngresoEx(tipoPago, ingresoExtra, descripcion, idTurno, clienteEnRegistro.getId_Cliente());
            if (registroIngresoExtra) {

                mensajeWARes(clienteEnRegistro);
                JOptionPane.showMessageDialog(null, "Registro exitoso!");
                this.dispose();

            }

        } else {
            JOptionPane.showMessageDialog(this, "Hubo un problema con el registro. ");
            return;
        }

    }

    public void procesoDatosClienteViejo() {
        if (txtNumDoc.getText().equals("") || txtNumDoc.getText().length() <= 6) {
            JOptionPane.showMessageDialog(this, "Ingrese un numero de documento valido");
            txtNumDoc.requestFocus();
            return;
        }

        clienteGlobal = con.buscarClientePorCedula(txtNumDoc.getText());

        if (clienteGlobal != null) {
            cbxTipoDoc.setSelectedItem(clienteGlobal.getTipo_Documento());
            txtNombres.setText(clienteGlobal.getNombres());
            txtApellidos.setText(clienteGlobal.getApellidos());
            cbxNacionalidad.setSelectedItem(clienteGlobal.getNacionalidad());
            String telefono = clienteGlobal.getTelefono();
            String prefijoPais = txtIndicativo.getText();
            String telefonoSinPre = telefono.replaceFirst(Pattern.quote(prefijoPais), "");
            txtTelefono.setText(telefonoSinPre);
            txtProfesion.setText(clienteGlobal.getProfesion());
            jdchNacimiento.setDate(clienteGlobal.getFecha_Nacimiento());
            estadoVerificacion = clienteGlobal.getEstado_Verificacion();
            txtValorExtra.requestFocus();
            btnVerificarWa.setVisible(true);
        } else {
            clienteGlobal = null;
            btnVerificarWa.setVisible(false);
            txtNombres.setText("");
            txtApellidos.setText("");
            cbxNacionalidad.setSelectedIndex(0);
            cbxTipoDoc.setSelectedIndex(0);
            txtTelefono.setText("");
            txtProfesion.setText("");
            jdchNacimiento.setDate(null);
            JOptionPane.showMessageDialog(null, "TENEMOS UN CLIENTE NUEVO..."
                    + " DEBES SER MUY AMABLE");
            cbxTipoDoc.requestFocus();
        }
    }

    public void gestionImpresora() {
        LocalTime hora = LocalTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("hh:mm a");
        String horaF = hora.format(formato);
        String tipoPago = cbxTipoPago.getSelectedItem().toString();
        String concepto = "Varios";

        ThermalPrinter thermalPrinter = new ThermalPrinter();
        String clientInfo = String.format("""
                                                  FECHA             : %s
                                                  HORA              : %s
                                                  Nombres           : %s
                                                  Apellidos         : %s
                                                  Documento         : %s
                                                  Tipo de pago      : %s
                                                  Valor pagado      : %s                                              
                                                  Concepto del pago : %s""",
                jlbFecha.getText(), horaF, txtNombres.getText(), txtApellidos.getText(), txtNumDoc.getText(),
                tipoPago, txtValorExtra.getText(), concepto);
        String nombre = txtNombres.getText().split(" ")[0];
        String apellido = txtApellidos.getText().split(" ")[0];

        List<String[]> productList = new ArrayList<>();
        String nuevoPDF = thermalPrinter.createPDF(clientInfo, productList, nombre + "_" + apellido);
        thermalPrinter.printPDF(nuevoPDF);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jProgressBar1 = new javax.swing.JProgressBar();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jlbFecha = new javax.swing.JTextField();
        jblContadorCaracteres = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtValorExtra = new javax.swing.JTextField();
        btnGuardarIngreso = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        txtNombres = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtNumDoc = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        cbxTipoDoc = new javax.swing.JComboBox<>();
        txtApellidos = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JTextField();
        txtIndicativo = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtProfesion = new javax.swing.JTextField();
        cbxNacionalidad = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaDescripcion = new javax.swing.JTextArea();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel3 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        btnVerificarWa = new javax.swing.JButton();
        jlbMensajeError = new javax.swing.JLabel();
        jlbMensajeExitoso = new javax.swing.JLabel();
        jlbVistoBueno = new javax.swing.JLabel();
        jlbVistoMalo = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        cbxTipoPago = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jdchNacimiento = new com.toedter.calendar.JDateChooser();
        jLabel17 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("INGRESO EXTRA");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(0, 102, 102));

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("FECHA :");

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("ADMINISTRADOR");

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 219, 95));
        jLabel4.setText(":");

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Carlos Moran");

        jlbFecha.setEditable(false);
        jlbFecha.setBackground(new java.awt.Color(0, 102, 102));
        jlbFecha.setForeground(new java.awt.Color(255, 255, 255));
        jlbFecha.setBorder(null);
        jlbFecha.setFocusable(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlbFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlbFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 880, -1));
        jPanel1.add(jblContadorCaracteres, new org.netbeans.lib.awtextra.AbsoluteConstraints(367, 521, 62, 12));

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("VALOR :");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(249, 331, 72, 30));

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("DESCRIPCION :");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(249, 415, -1, 30));

        txtValorExtra.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtValorExtraKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtValorExtraKeyTyped(evt);
            }
        });
        jPanel1.add(txtValorExtra, new org.netbeans.lib.awtextra.AbsoluteConstraints(367, 331, 185, 30));

        btnGuardarIngreso.setBackground(new java.awt.Color(0, 51, 51));
        btnGuardarIngreso.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        btnGuardarIngreso.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardarIngreso.setText("GUARDAR INGRESO");
        btnGuardarIngreso.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnGuardarIngresoMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnGuardarIngresoMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnGuardarIngresoMouseExited(evt);
            }
        });
        btnGuardarIngreso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarIngresoActionPerformed(evt);
            }
        });
        btnGuardarIngreso.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnGuardarIngresoKeyPressed(evt);
            }
        });
        jPanel1.add(btnGuardarIngreso, new org.netbeans.lib.awtextra.AbsoluteConstraints(189, 571, 150, 40));

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
        jPanel1.add(btnCancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(569, 571, 150, 40));

        txtNombres.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtNombresKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNombres(evt);
            }
        });
        jPanel1.add(txtNombres, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 164, 200, 30));

        jLabel8.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(51, 51, 51));
        jLabel8.setText("NUM.  DOCUMENTO:");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 124, 120, 30));

        txtNumDoc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNumDocFocusLost(evt);
            }
        });
        txtNumDoc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtNumDocKeyPressed(evt);
            }
        });
        jPanel1.add(txtNumDoc, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 124, 200, 30));

        jLabel9.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(51, 51, 51));
        jLabel9.setText("NOMBRES:");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 164, 130, 30));

        jLabel10.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(51, 51, 51));
        jLabel10.setText("APELLIDOS:");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 164, 110, 30));

        cbxTipoDoc.setBackground(new java.awt.Color(255, 219, 95));
        cbxTipoDoc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cbxTipoDocKeyPressed(evt);
            }
        });
        jPanel1.add(cbxTipoDoc, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 124, 200, 30));

        txtApellidos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtApellidosKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtApellidos(evt);
            }
        });
        jPanel1.add(txtApellidos, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 161, 200, 30));

        jLabel11.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(51, 51, 51));
        jLabel11.setText("NACIONALIDAD:");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 200, 120, 30));

        jLabel12.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(51, 51, 51));
        jLabel12.setText("TELEFONO:");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 200, 120, 30));

        txtTelefono.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtTelefonoKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtTelefono(evt);
            }
        });
        jPanel1.add(txtTelefono, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 200, 140, 30));
        jPanel1.add(txtIndicativo, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 200, 60, 30));

        jLabel13.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(51, 51, 51));
        jLabel13.setText("PROFESION:");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 244, 120, 30));

        txtProfesion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtProfesionKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtProfesionKeyTyped(evt);
            }
        });
        jPanel1.add(txtProfesion, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 242, 200, 30));

        cbxNacionalidad.setBackground(new java.awt.Color(255, 219, 95));
        cbxNacionalidad.setToolTipText("");
        cbxNacionalidad.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxNacionalidadItemStateChanged(evt);
            }
        });
        cbxNacionalidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cbxNacionalidadKeyPressed(evt);
            }
        });
        jPanel1.add(cbxNacionalidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 200, 200, 30));

        jLabel19.setBackground(new java.awt.Color(105, 105, 105));
        jLabel19.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(51, 51, 51));
        jLabel19.setText("TIPO DE DOCUMENTO:");
        jPanel1.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 124, 130, 30));

        jLabel14.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel14.setText("INGRESO EXTRA:");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 292, 210, -1));

        txaDescripcion.setColumns(20);
        txaDescripcion.setRows(5);
        txaDescripcion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txaDescripcionKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txaDescripcionKeyTyped(evt);
            }
        });
        jScrollPane1.setViewportView(txaDescripcion);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(367, 415, 350, 100));
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 315, 804, 10));
        jPanel1.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 119, 861, 10));

        jPanel3.setBackground(new java.awt.Color(255, 219, 95));

        jLabel15.setBackground(new java.awt.Color(0, 51, 51));
        jLabel15.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 51, 51));
        jLabel15.setText("DATOS DEL CLIENTE:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 1, Short.MAX_VALUE)
                .addComponent(jLabel15))
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 54, 880, -1));

        btnVerificarWa.setBackground(new java.awt.Color(0, 51, 51));
        btnVerificarWa.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        btnVerificarWa.setForeground(new java.awt.Color(255, 255, 255));
        btnVerificarWa.setText("VERIFICAR WA");
        btnVerificarWa.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnVerificarWaMouseClicked(evt);
            }
        });
        jPanel1.add(btnVerificarWa, new org.netbeans.lib.awtextra.AbsoluteConstraints(756, 202, -1, -1));

        jlbMensajeError.setBackground(new java.awt.Color(0, 0, 0));
        jlbMensajeError.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
        jlbMensajeError.setForeground(new java.awt.Color(255, 51, 51));
        jlbMensajeError.setText("WhatsApp Error");
        jlbMensajeError.setFocusable(false);
        jPanel1.add(jlbMensajeError, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 230, 120, -1));

        jlbMensajeExitoso.setBackground(new java.awt.Color(0, 0, 0));
        jlbMensajeExitoso.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
        jlbMensajeExitoso.setForeground(new java.awt.Color(51, 204, 0));
        jlbMensajeExitoso.setText("WhatsApp Exitóso");
        jlbMensajeExitoso.setFocusable(false);
        jPanel1.add(jlbMensajeExitoso, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 230, 120, 15));

        jlbVistoBueno.setForeground(new java.awt.Color(0, 102, 102));
        jlbVistoBueno.setFocusable(false);
        jPanel1.add(jlbVistoBueno, new org.netbeans.lib.awtextra.AbsoluteConstraints(535, 215, 15, 15));

        jlbVistoMalo.setForeground(new java.awt.Color(0, 102, 102));
        jlbVistoMalo.setFocusable(false);
        jPanel1.add(jlbVistoMalo, new org.netbeans.lib.awtextra.AbsoluteConstraints(535, 215, 15, 15));

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("TIPO PAGO:");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(249, 373, 90, 30));

        cbxTipoPago.setBackground(new java.awt.Color(255, 219, 95));
        cbxTipoPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar", "Efectivo", "Neki", "Bancolombia", "Datafono Bold", "Davivienda", "DaviPlata", " " }));
        jPanel1.add(cbxTipoPago, new org.netbeans.lib.awtextra.AbsoluteConstraints(367, 373, 185, 30));

        jPanel4.setBackground(new java.awt.Color(0, 102, 102));

        jLabel16.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("HotelesMas.com");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 617, 880, -1));

        jdchNacimiento.setBackground(new java.awt.Color(255, 219, 95));
        jdchNacimiento.setDateFormatString("d/MMMM/yyyy");
        jPanel1.add(jdchNacimiento, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 246, 198, -1));

        jLabel17.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(51, 51, 51));
        jLabel17.setText("FECHA NACIMIENTO:");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 242, 120, 30));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtValorExtraKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorExtraKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txaDescripcion.requestFocus();
        }
    }//GEN-LAST:event_txtValorExtraKeyPressed

    private void txtValorExtraKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorExtraKeyTyped
        char c = evt.getKeyChar();
        if ((c < '0' || c > '9'))
            evt.consume();
    }//GEN-LAST:event_txtValorExtraKeyTyped

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
            jblContadorCaracteres.setText((maxCaracteres - text.length() - 1) + "/" + maxCaracteres);
        }
    }//GEN-LAST:event_txaDescripcionKeyTyped

    private void btnGuardarIngresoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGuardarIngresoMouseClicked
        guardarIngreso();
    }//GEN-LAST:event_btnGuardarIngresoMouseClicked

    private void btnGuardarIngresoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnGuardarIngresoKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            guardarIngreso();
        }
    }//GEN-LAST:event_btnGuardarIngresoKeyPressed

    private void txtNombresKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombresKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtApellidos.requestFocus();
        }
    }//GEN-LAST:event_txtNombresKeyPressed

    private void txtNombres(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombres
        char c = evt.getKeyChar();
        if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && (c < ' ' || c > ' ')) {
            evt.consume();
        }
        String str = txtNombres.getText();

        StringBuffer strbf = new StringBuffer();
        Matcher match = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(str);
        while (match.find()) {
            match.appendReplacement(strbf, match.group(1).toUpperCase() + match.group(2).toLowerCase());
        }
        txtNombres.setText(match.appendTail(strbf).toString());
    }//GEN-LAST:event_txtNombres

    private void cbxTipoDocKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbxTipoDocKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtNombres.requestFocus();
        }
    }//GEN-LAST:event_cbxTipoDocKeyPressed

    private void txtApellidosKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtApellidosKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            cbxNacionalidad.requestFocus();
        }
    }//GEN-LAST:event_txtApellidosKeyPressed

    private void txtApellidos(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtApellidos
        char c = evt.getKeyChar();
        if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && (c < ' ' || c > ' ')) {
            evt.consume();
        }
        String str = txtApellidos.getText();

        StringBuffer strbf = new StringBuffer();
        Matcher match = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(str);
        while (match.find()) {
            match.appendReplacement(strbf, match.group(1).toUpperCase() + match.group(2).toLowerCase());
        }
        txtApellidos.setText(match.appendTail(strbf).toString());
    }//GEN-LAST:event_txtApellidos

    private void txtTelefonoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTelefonoKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtProfesion.requestFocus();
        }
    }//GEN-LAST:event_txtTelefonoKeyPressed

    private void txtTelefono(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTelefono
        char c = evt.getKeyChar();
        if ((c < '0' || c > '9')) {
            evt.consume();
        }

        if (txtTelefono.getText().length() > 8) {
            btnVerificarWa.setVisible(true);

        } else {
            btnVerificarWa.setVisible(false);
        }
    }//GEN-LAST:event_txtTelefono

    private void txtProfesionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtProfesionKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtValorExtra.requestFocus();
        }
    }//GEN-LAST:event_txtProfesionKeyPressed

    private void txtProfesionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtProfesionKeyTyped
        char c = evt.getKeyChar();
        if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && (c < ' ' || c > ' ')) {
            evt.consume();
        }
        String str = txtProfesion.getText();

        StringBuffer strbf = new StringBuffer();
        Matcher match = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(str);
        while (match.find()) {
            match.appendReplacement(strbf, match.group(1).toUpperCase() + match.group(2).toLowerCase());
        }
        txtProfesion.setText(match.appendTail(strbf).toString());
    }//GEN-LAST:event_txtProfesionKeyTyped

    private void cbxNacionalidadItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxNacionalidadItemStateChanged
        String selectedItem = (String) cbxNacionalidad.getSelectedItem();
        Matcher matcher = Pattern.compile("\\+?(\\d+)").matcher(selectedItem);
        if (matcher.find()) {
            String indicativo = "+" + matcher.group(1);
            txtIndicativo.setText(indicativo);
        } else {
            txtIndicativo.setText("");
        }
    }//GEN-LAST:event_cbxNacionalidadItemStateChanged

    private void cbxNacionalidadKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbxNacionalidadKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtTelefono.requestFocus();
        }
    }//GEN-LAST:event_cbxNacionalidadKeyPressed

    private void txaDescripcionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txaDescripcionKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnGuardarIngreso.requestFocus();
        }
    }//GEN-LAST:event_txaDescripcionKeyPressed

    private void btnGuardarIngresoMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGuardarIngresoMouseEntered
        btnGuardarIngreso.setBackground(new Color(0, 102, 102));

    }//GEN-LAST:event_btnGuardarIngresoMouseEntered

    private void btnGuardarIngresoMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGuardarIngresoMouseExited
        btnGuardarIngreso.setBackground(new Color(0, 51, 51));

    }//GEN-LAST:event_btnGuardarIngresoMouseExited

    private void btnCancelarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCancelarMouseEntered
        btnCancelar.setBackground(new Color(0, 102, 102));

    }//GEN-LAST:event_btnCancelarMouseEntered

    private void btnCancelarMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCancelarMouseExited
        btnCancelar.setBackground(new Color(0, 51, 51));

    }//GEN-LAST:event_btnCancelarMouseExited

    private void btnCancelarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCancelarMouseClicked
        this.setVisible(false);
    }//GEN-LAST:event_btnCancelarMouseClicked

    private void txtNumDocFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNumDocFocusLost
        procesoDatosClienteViejo();
    }//GEN-LAST:event_txtNumDocFocusLost

    private void btnGuardarIngresoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarIngresoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnGuardarIngresoActionPerformed

    private void txtNumDocKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNumDocKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            cbxTipoDoc.requestFocus();
        }
    }//GEN-LAST:event_txtNumDocKeyPressed

    private void btnVerificarWaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnVerificarWaMouseClicked
        if (clienteGlobal == null) {           
            btnVerificarWa.setVisible(false);            
        }else{
            mensajeWARes(clienteGlobal);
            
        }
    }//GEN-LAST:event_btnVerificarWaMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnGuardarIngreso;
    private javax.swing.JButton btnVerificarWa;
    private javax.swing.JComboBox<String> cbxNacionalidad;
    private javax.swing.JComboBox<String> cbxTipoDoc;
    private javax.swing.JComboBox<String> cbxTipoPago;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel jblContadorCaracteres;
    public com.toedter.calendar.JDateChooser jdchNacimiento;
    private javax.swing.JTextField jlbFecha;
    private javax.swing.JLabel jlbMensajeError;
    private javax.swing.JLabel jlbMensajeExitoso;
    private javax.swing.JLabel jlbVistoBueno;
    private javax.swing.JLabel jlbVistoMalo;
    private javax.swing.JTextArea txaDescripcion;
    private javax.swing.JTextField txtApellidos;
    private javax.swing.JTextField txtIndicativo;
    private javax.swing.JTextField txtNombres;
    private javax.swing.JTextField txtNumDoc;
    private javax.swing.JTextField txtProfesion;
    private javax.swing.JTextField txtTelefono;
    private javax.swing.JTextField txtValorExtra;
    // End of variables declaration//GEN-END:variables
}
