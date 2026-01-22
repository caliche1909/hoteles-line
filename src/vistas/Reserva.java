package vistas;

import conectar.Canectar;
import conectar.Consultasbd;
import java.awt.Color;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import modelo.UsuarioOperando;
import java.util.regex.Matcher;
import javax.swing.SwingUtilities;
import modelo.Cliente;
import modelo.Hotel;
import modelo.MensajesWATest;
import static vistas.Reserva.jdchLlegada;

public final class Reserva extends javax.swing.JDialog {

    Recepciones ir;
    Consultasbd bd = new Consultasbd();
    Canectar con = new Canectar();
    PreparedStatement ps;
    ResultSet rs;
    String res = null;
    Object resnombreRes;
    UsuarioOperando mod = new UsuarioOperando();
    Hotel hotel = new Hotel();
    String estadoVerificacion = "No Verificado";

    public Reserva(Recepciones parent, boolean modal, UsuarioOperando usus, Hotel hotel) {
        super(parent, modal);
        this.ir = parent;
        mod = usus;
        this.hotel = hotel;
        initComponents();

        jlbVistoMalo.setVisible(false);
        jlbVistoBueno.setVisible(false);
        btnVerificarWa.setVisible(false);
        jlbMensajeError.setVisible(false);
        jlbMensajeExitoso.setVisible(false);

        rsscalelabel.RSScaleLabel.setScaleLabel(jlbVistoBueno, "src/img/VistoBueno.png");
        rsscalelabel.RSScaleLabel.setScaleLabel(jlbVistoMalo, "src/img/vistoMalo.png");
        con.traerPaises(jbxNacionRe, null, null);
        con.traerTiposdeDocumento(jbxTipDoc);
        if (usus.getNombres() != null && usus.getApellidos() != null && usus.getRoll_usuarios() != null) {
            lblRollRegistro.setText(usus.getRoll_usuarios());
            txtNombre.setText(usus.getNombres().split(" ")[0] + " " + usus.getApellidos().split(" ")[0]);
        }
        this.setLocationRelativeTo(null);
        sumarDia();
        aplicarFormatoPuntosMil(txtValPorNoRe);
    }

    public void sumarDia() {
        jdchLlegada.getDateEditor().addPropertyChangeListener(
                (PropertyChangeEvent e) -> {
                    if ("date".equals(e.getPropertyName())) {
                        Date fechaLlegada = jdchLlegada.getDate();
                        if (fechaLlegada != null) {
                            Calendar calendar = Calendar.getInstance();
                            // Remueve la hora, minuto y segundo del calendario actual
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);

                            // Comprueba si la fecha seleccionada es anterior a la fecha actual
                            if (fechaLlegada.before(calendar.getTime())) {
                                System.out.println("metodo sumar dia de la clase reserva");
                                JOptionPane.showMessageDialog(null, "Por favor seleccione una fecha presente o futura.", "Error en la fecha", JOptionPane.ERROR_MESSAGE);

                                // Restablece la fecha al valor anterior o a la fecha actual
                                jdchLlegada.setDate((Date) e.getOldValue());
                            } else {
                                calendar.setTime(fechaLlegada);
                                calendar.add(Calendar.DATE, 1);
                                Date fechaSalida = calendar.getTime();
                                jdchSalida.setDate(fechaSalida);
                                int noches = calcularNoches(fechaLlegada, fechaSalida);
                                txtCantNoches.setText(Integer.toString(noches));
                                txtHora.requestFocus();
                            }
                        }
                    }
                }
        );
        jdchSalida.getDateEditor().addPropertyChangeListener(
                (PropertyChangeEvent e) -> {
                    if ("date".equals(e.getPropertyName())) {
                        Date fechaSalida = jdchSalida.getDate();
                        if (fechaSalida != null) {
                            Date fechaLlegada = jdchLlegada.getDate();

                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);

                            if (fechaSalida.before(calendar.getTime()) || fechaSalida.equals(calendar.getTime()) || fechaSalida.before(fechaLlegada)) {
                                JOptionPane.showMessageDialog(null, "Por favor seleccione una fecha futura.", "Error en la fecha de salida", JOptionPane.ERROR_MESSAGE);
                                jdchSalida.setDate((Date) e.getOldValue());
                            } else {
                                int noches = calcularNoches(fechaLlegada, fechaSalida);
                                txtCantNoches.setText(Integer.toString(noches));
                                txtHora.requestFocus();
                            }
                        }
                    }
                }
        );
    }

    public int calcularNoches(Date fechaLlegada, Date fechaSalida) {
        long diffInMillies = fechaSalida.getTime() - fechaLlegada.getTime();
        long noches = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        return (int) noches;
    }

    private String formatearConPuntosDeMil(double numero) {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setGroupingSeparator(',');
        DecimalFormat formatoDelNumero = new DecimalFormat("#,##0", simbolos);
        formatoDelNumero.setGroupingSize(3);
        formatoDelNumero.setGroupingUsed(true);
        return formatoDelNumero.format(numero);
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

    private double obtenerNumeroSinSeparador(JTextField textField) {
        String text = textField.getText();
        String textSinSeparador = text.replace(",", ""); // Reemplaza las comas por vacío
        return Double.parseDouble(textSinSeparador);
    }

    public CompletableFuture<String> enviarMensajeWA() {
        return CompletableFuture.supplyAsync(() -> {
            String nombreCliente = "*" + txtNomRe.getText().split(" ")[0] + "*";
            String telefonoWA = txtIndicativo.getText() + txtTelRe.getText();
            String horaLl = txtHora.getText() + ":" + txtMinutos.getText() + " " + jbxAmPm.getSelectedItem().toString();
            String cantNoches = "Noches";
            Date fechaLlegada = jdchLlegada.getDate();
            Date fechaSalida = jdchSalida.getDate();
            SimpleDateFormat formato = new SimpleDateFormat("EEEE dd MMMM yyyy", new Locale("es", "ES"));
            String llegada = formato.format(fechaSalida);
            String salida = formato.format(fechaLlegada);

            int cantidadN = Integer.parseInt(txtCantNoches.getText());
            if (cantidadN == 1) {
                cantNoches = "Noche";
            }

            String MensajeWhatsApp = "Cordial saludo " + nombreCliente.toUpperCase() + "🫱🏻‍🫲🏼\n"
                    + "Hemos confirmado su reserva en el *" + hotel.getNombreHotel().toUpperCase() + "*.\n\n"
                    + "*Datos da la reserva:*\n\n"
                    + "*-FECHA DE CHECK IN*\n "
                    + llegada + "\n\n"
                    + "*-Hora Llegada :*  " + horaLl + "\n\n"
                    + "*-FECHA DE CHECK OUT*\n"
                    + salida + "\n\n"
                    + txtCantNoches.getText() + " " + cantNoches + "\n"
                    + "Valor Noche: *" + txtValPorNoRe.getText() + "*\n"
                    + "Valor Total: *" + txtTotalRe.getText() + "*\n\n"
                    + "Recuerde que le hemos asignado la habitacion *" + txtNumHab.getText() + "* cualquier inquietud puede coumicarse con nosotros a los siguiente numeros:\n"
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
                    txtTelRe.setFocusable(false);
                    return "Verificado";

                } else {
                    txtTelRe.setFocusable(true);
                    jlbMensajeError.setVisible(true);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error al codificar el mensaje: " + ex.getMessage());
                txtTelRe.setFocusable(true);
                jlbMensajeError.setVisible(true);
            }

            return estadoVerificacion;
        });
    }

    public void reservar(String estadoVerificacion) {

        try {

            String estadoReserva = "Vigente";
            Date xllegada = jdchLlegada.getDate();
            java.sql.Date sqlxllegada = new java.sql.Date(xllegada.getTime());
            String xhora = txtHora.getText() + ":" + txtMinutos.getText() + jbxAmPm.getSelectedItem().toString();
            Date xsalida = jdchSalida.getDate();
            java.sql.Date sqlxsalida = new java.sql.Date(xsalida.getTime());
            int xNumHab = Integer.parseInt(txtNumHab.getText());

            double xvalornoche = obtenerNumeroSinSeparador(txtValPorNoRe);
            int xcantnoches = Integer.parseInt(txtCantNoches.getText());
            double xtotal = obtenerNumeroSinSeparador(txtTotalRe);

            int cli = 0;
            cli = con.IdClienteViejo("Id_Cliente", "cliente", "Num_Documento", txtNumDocRe.getText());
            int IdT = mod.getTurnoPresente();
            int IdC = 0;
            int IdR = 0;
            int idHotel = hotel.getIdHoteles();
            String tel = txtIndicativo.getText() + txtTelRe.getText();

            Cliente clienteReservando = new Cliente();
            clienteReservando.setNum_Documento(txtNumDocRe.getText());
            clienteReservando.setTipo_Documento((String) jbxTipDoc.getSelectedItem());
            clienteReservando.setNombres(txtNomRe.getText());
            clienteReservando.setApellidos(txtApeRe.getText());
            clienteReservando.setTelefono(tel);
            clienteReservando.setNacionalidad(jbxNacionRe.getSelectedItem().toString());
            clienteReservando.setProfesion(txtProfesion.getText());
            clienteReservando.setEstado_Verificacion(estadoVerificacion);

            if (cli == 0) {
                Cliente clienteQueReservo = con.registrarCliente(clienteReservando);

                if (clienteQueReservo != null) {
                    IdC = clienteQueReservo.getId_Cliente();
                }

                //enviarMensajeWA();
                IdR = con.registrarReserva(sqlxllegada, xhora, sqlxsalida, xNumHab, xvalornoche, xcantnoches, xtotal,
                        estadoVerificacion, estadoReserva, IdC, IdT, idHotel);

            } else {
                //enviarMensajeWA();
                IdR = con.registrarReserva(sqlxllegada, xhora, sqlxsalida, xNumHab, xvalornoche, xcantnoches, xtotal,
                        estadoVerificacion, estadoReserva, cli, IdT, idHotel);

            }

            // verifica si la fecha de llegada es hoy y actualiza la tabla de habitaciones 
            Date fechaHoy = new Date();
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
            if (formato.format(fechaHoy).equals(formato.format(xllegada))) {
                Consultasbd bd = new Consultasbd();
                bd.reservarHabitacion(xNumHab, idHotel);
            }
            JOptionPane.showMessageDialog(null, "Reserva Exitosa\n\n"
                    + "Nombre:  " + clienteReservando.getNombres().split(" ")[0] + " " + clienteReservando.getApellidos().split(" ")[0] + "\n"
                    + "Num. Habitacion:  " + xNumHab + "\n"
                    + "Fecha de Llegada:  " + sqlxllegada.toString(), "Informacion", JOptionPane.INFORMATION_MESSAGE);
            ir.activarHabitaciones(ir.jdchFechaHabs.getDate(), idHotel);
            this.dispose();
        } catch (HeadlessException | NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Intente de nuevo " + e, "Error", JOptionPane.ERROR_MESSAGE);
            this.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Ha ocurrido un error desconocido: " + ex, "Error", JOptionPane.ERROR_MESSAGE);
            this.setVisible(true);
        }
    }

    public void mensajeWARes() {

        if (jdchLlegada.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una fecha de Llegada!");
            jdchLlegada.requestFocus();
            return;
        }
        if (jdchSalida.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una fecha de Salida!");
            jdchSalida.requestFocus();
            return;
        }
        if (txtHora.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite una hora de Llegada!");
            txtHora.requestFocus();
            return;
        }
        if (txtMinutos.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite una hora de Llegada!");
            txtMinutos.requestFocus();
            return;
        }
        if (jbxTipDoc.getSelectedItem().toString().equals("Seleccione")) {
            JOptionPane.showMessageDialog(this, "Seleccione tipo de documento!");
            jbxTipDoc.requestFocus();
            return;
        }
        if (txtNumDocRe.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite el numero de documento!");
            txtNumDocRe.requestFocus();
            return;
        }
        if (txtNomRe.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite el nombre!");
            txtNomRe.requestFocus();
            return;
        }
        if (txtApeRe.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite Apellidos!");
            txtApeRe.requestFocus();
            return;
        }
        if (jbxNacionRe.getSelectedItem().toString().equals("Seleccione")) {
            JOptionPane.showMessageDialog(this, "Seleccione una nacionalidad!");
            jbxNacionRe.requestFocus();
            return;
        }
        if (txtTelRe.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite el numero de telefono!");
            txtTelRe.requestFocus();
            return;
        }
        if (txtProfesion.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite Profesion!");
            txtProfesion.requestFocus();
            return;
        }
        if (txtValPorNoRe.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite el valor por noche!");
            txtValPorNoRe.requestFocus();
            return;
        }
        this.setVisible(false);
        ir.toFront();

        CompletableFuture<String> resultado = enviarMensajeWA();

        resultado.thenAccept(res -> {
            SwingUtilities.invokeLater(() -> {
                // llamar al método reservar con el resultado
                reservar(res);

            });
        }).exceptionally(ex -> {
            SwingUtilities.invokeLater(() -> {
                // Aquí puedes manejar la excepción.
                JOptionPane.showMessageDialog(this, "No se confirmo la reserva con el cliente! " + ex, "Error", JOptionPane.ERROR_MESSAGE);
                // Aquí puedes llamar al método reservar con el valor por defecto
                reservar("No Verificado");

            });
            return null;
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpnFondoReservar = new javax.swing.JPanel();
        jdchSalida = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jdchLlegada = new com.toedter.calendar.JDateChooser();
        txtMinutos = new javax.swing.JTextField();
        jblSepHorMin = new javax.swing.JLabel();
        txtHora = new javax.swing.JTextField();
        btnCanRe = new javax.swing.JButton();
        btnGenReRe = new javax.swing.JButton();
        jpnDatosRe = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtNumDocRe = new javax.swing.JTextField();
        txtNomRe = new javax.swing.JTextField();
        txtTelRe = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtApeRe = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtValPorNoRe = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jbxTipDoc = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        jbxNacionRe = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        txtProfesion = new javax.swing.JTextField();
        txtUltValPa = new javax.swing.JTextField();
        txtTotalRe = new javax.swing.JTextField();
        txtIndicativo = new javax.swing.JTextField();
        jlbVistoBueno = new javax.swing.JLabel();
        btnVerificarWa = new javax.swing.JButton();
        jlbMensajeError = new javax.swing.JLabel();
        jlbMensajeExitoso = new javax.swing.JLabel();
        jlbVistoMalo = new javax.swing.JLabel();
        txtCantNoches = new javax.swing.JTextField();
        jbxAmPm = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        txtNumHab = new javax.swing.JTextField();
        lblRollRegistro = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        jblFondoReservar = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jpnFondoReservar.setBackground(new java.awt.Color(255, 255, 255));
        jpnFondoReservar.setPreferredSize(new java.awt.Dimension(950, 550));

        jdchSalida.setBackground(new java.awt.Color(0, 102, 102));
        jdchSalida.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 102, 102), 1, true));
        jdchSalida.setDateFormatString("EEE  dd-MMMM-yyyy");

        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("Salida : ");

        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Cantidad de Noches:");

        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Llegada : ");

        jdchLlegada.setBackground(new java.awt.Color(0, 102, 102));
        jdchLlegada.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 102, 102), 1, true));
        jdchLlegada.setDateFormatString("EEE dd-MMMM-yyyy");

        txtMinutos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtMinutosKeyPressed(evt);
            }
        });

        jblSepHorMin.setForeground(new java.awt.Color(0, 0, 0));
        jblSepHorMin.setText(":");

        txtHora.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtHoraKeyPressed(evt);
            }
        });

        btnCanRe.setBackground(new java.awt.Color(0, 51, 51));
        btnCanRe.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        btnCanRe.setForeground(new java.awt.Color(255, 255, 255));
        btnCanRe.setText("CANCELAR");
        btnCanRe.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnCanReMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnCanReMouseExited(evt);
            }
        });
        btnCanRe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCanReActionPerformed(evt);
            }
        });

        btnGenReRe.setBackground(new java.awt.Color(0, 51, 51));
        btnGenReRe.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        btnGenReRe.setForeground(new java.awt.Color(255, 255, 255));
        btnGenReRe.setText("GENERAR RESERVA ");
        btnGenReRe.setPreferredSize(new java.awt.Dimension(950, 550));
        btnGenReRe.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                btnGenReReFocusGained(evt);
            }
        });
        btnGenReRe.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnGenReReMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnGenReReMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnGenReReMouseExited(evt);
            }
        });
        btnGenReRe.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnGenReReKeyPressed(evt);
            }
        });

        jpnDatosRe.setBackground(new java.awt.Color(0, 102, 102));
        jpnDatosRe.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Num. Documento :");
        jpnDatosRe.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(314, 15, -1, 25));

        jLabel8.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Tipo. Documento:");
        jpnDatosRe.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 15, -1, 25));

        jLabel7.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Telefono :");
        jpnDatosRe.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(312, 105, 83, 25));

        txtNumDocRe.setForeground(new java.awt.Color(0, 0, 0));
        txtNumDocRe.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNumDocReFocusLost(evt);
            }
        });
        txtNumDocRe.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtNumDocReKeyPressed(evt);
            }
        });
        jpnDatosRe.add(txtNumDocRe, new org.netbeans.lib.awtextra.AbsoluteConstraints(426, 13, 180, 30));

        txtNomRe.setForeground(new java.awt.Color(0, 0, 0));
        txtNomRe.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtNomReKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNomReKeyTyped(evt);
            }
        });
        jpnDatosRe.add(txtNomRe, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 55, 180, 30));

        txtTelRe.setForeground(new java.awt.Color(0, 0, 0));
        txtTelRe.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtTelReKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTelReKeyReleased(evt);
            }
        });
        jpnDatosRe.add(txtTelRe, new org.netbeans.lib.awtextra.AbsoluteConstraints(486, 103, 120, 30));

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Apellidos :");
        jpnDatosRe.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(314, 57, 64, 25));

        txtApeRe.setForeground(new java.awt.Color(0, 0, 0));
        txtApeRe.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtApeReKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtApeReKeyTyped(evt);
            }
        });
        jpnDatosRe.add(txtApeRe, new org.netbeans.lib.awtextra.AbsoluteConstraints(426, 55, 180, 30));

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Ult. Valor Pagado :");
        jpnDatosRe.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(312, 201, -1, 25));

        jLabel12.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("TOTAL : ");
        jpnDatosRe.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(426, 237, 50, 30));

        jLabel10.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Valor Por Noche:");
        jpnDatosRe.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 201, 96, 25));

        txtValPorNoRe.setForeground(new java.awt.Color(0, 0, 0));
        txtValPorNoRe.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtValPorNoReKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtValPorNoReKeyReleased(evt);
            }
        });
        jpnDatosRe.add(txtValPorNoRe, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 199, 180, 30));

        jLabel13.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Nombres : ");
        jpnDatosRe.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 57, 96, 25));

        jbxTipDoc.setBackground(new java.awt.Color(255, 219, 95));
        jbxTipDoc.setForeground(new java.awt.Color(0, 0, 0));
        jbxTipDoc.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jbxTipDocMouseClicked(evt);
            }
        });
        jbxTipDoc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbxTipDocKeyPressed(evt);
            }
        });
        jpnDatosRe.add(jbxTipDoc, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 12, 180, 30));

        jLabel14.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Nacionalidad:");
        jpnDatosRe.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 106, 96, 25));

        jbxNacionRe.setBackground(new java.awt.Color(255, 219, 95));
        jbxNacionRe.setForeground(new java.awt.Color(0, 0, 0));
        jbxNacionRe.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jbxNacionReItemStateChanged(evt);
            }
        });
        jbxNacionRe.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jbxNacionReMouseClicked(evt);
            }
        });
        jbxNacionRe.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbxNacionReKeyPressed(evt);
            }
        });
        jpnDatosRe.add(jbxNacionRe, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 103, 180, 30));

        jLabel15.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Profesion: ");
        jpnDatosRe.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 153, 96, 25));

        txtProfesion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtProfesionKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtProfesionKeyTyped(evt);
            }
        });
        jpnDatosRe.add(txtProfesion, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 151, 180, 30));

        txtUltValPa.setEditable(false);
        txtUltValPa.setFocusable(false);
        jpnDatosRe.add(txtUltValPa, new org.netbeans.lib.awtextra.AbsoluteConstraints(422, 199, 180, 30));

        txtTotalRe.setEditable(false);
        txtTotalRe.setFocusable(false);
        jpnDatosRe.add(txtTotalRe, new org.netbeans.lib.awtextra.AbsoluteConstraints(506, 238, 100, 30));

        txtIndicativo.setForeground(new java.awt.Color(0, 0, 0));
        txtIndicativo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtIndicativoKeyPressed(evt);
            }
        });
        jpnDatosRe.add(txtIndicativo, new org.netbeans.lib.awtextra.AbsoluteConstraints(426, 103, 57, 30));

        jlbVistoBueno.setForeground(new java.awt.Color(0, 102, 102));
        jlbVistoBueno.setFocusable(false);
        jpnDatosRe.add(jlbVistoBueno, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 117, 15, 15));

        btnVerificarWa.setBackground(new java.awt.Color(255, 179, 39));
        btnVerificarWa.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        btnVerificarWa.setForeground(new java.awt.Color(0, 0, 0));
        btnVerificarWa.setText("VERIFICAR WA");
        jpnDatosRe.add(btnVerificarWa, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 150, -1, -1));

        jlbMensajeError.setBackground(new java.awt.Color(0, 0, 0));
        jlbMensajeError.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
        jlbMensajeError.setForeground(new java.awt.Color(255, 51, 51));
        jlbMensajeError.setText("WhatsApp Error");
        jlbMensajeError.setFocusable(false);
        jpnDatosRe.add(jlbMensajeError, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 135, 120, -1));

        jlbMensajeExitoso.setBackground(new java.awt.Color(0, 0, 0));
        jlbMensajeExitoso.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
        jlbMensajeExitoso.setForeground(new java.awt.Color(51, 204, 0));
        jlbMensajeExitoso.setText("WhatsApp Exitóso");
        jlbMensajeExitoso.setFocusable(false);
        jpnDatosRe.add(jlbMensajeExitoso, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 135, 120, 15));

        jlbVistoMalo.setForeground(new java.awt.Color(0, 102, 102));
        jlbVistoMalo.setFocusable(false);
        jpnDatosRe.add(jlbVistoMalo, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 117, 15, 15));

        txtCantNoches.setEditable(false);
        txtCantNoches.setBackground(new java.awt.Color(255, 255, 51,80));
        txtCantNoches.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtCantNoches.setForeground(new java.awt.Color(0, 0, 0));

        jbxAmPm.setBackground(new java.awt.Color(255, 179, 39));
        jbxAmPm.setForeground(new java.awt.Color(0, 0, 0));
        jbxAmPm.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AM", "PM" }));
        jbxAmPm.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbxAmPmKeyPressed(evt);
            }
        });

        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("Hora De Llegada : ");

        jPanel1.setBackground(new java.awt.Color(0, 102, 102));

        jLabel9.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Num. Habitacion : ");

        txtNumHab.setEditable(false);
        txtNumHab.setBackground(new java.awt.Color(255, 255, 255));
        txtNumHab.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtNumHab.setForeground(new java.awt.Color(0, 0, 0));

        lblRollRegistro.setBackground(new java.awt.Color(0, 102, 102));
        lblRollRegistro.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblRollRegistro.setForeground(new java.awt.Color(255, 255, 255));

        txtNombre.setEditable(false);
        txtNombre.setBackground(new java.awt.Color(0, 102, 102));
        txtNombre.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtNombre.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNumHab, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblRollRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRollRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtNumHab, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jblFondoReservar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/imatranres.png"))); // NOI18N

        jPanel2.setBackground(new java.awt.Color(0, 102, 102));

        jLabel16.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("HotelesMas.com");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jpnFondoReservarLayout = new javax.swing.GroupLayout(jpnFondoReservar);
        jpnFondoReservar.setLayout(jpnFondoReservarLayout);
        jpnFondoReservarLayout.setHorizontalGroup(
            jpnFondoReservarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnFondoReservarLayout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addGroup(jpnFondoReservarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpnFondoReservarLayout.createSequentialGroup()
                        .addGroup(jpnFondoReservarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpnFondoReservarLayout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(jdchLlegada, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jpnFondoReservarLayout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(jdchSalida, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jpnFondoReservarLayout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addGap(31, 31, 31)
                                .addComponent(txtHora, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(3, 3, 3)
                                .addGroup(jpnFondoReservarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jpnFondoReservarLayout.createSequentialGroup()
                                        .addGap(7, 7, 7)
                                        .addComponent(txtMinutos, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jblSepHorMin, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(10, 10, 10)
                                .addComponent(jbxAmPm, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jpnFondoReservarLayout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(txtCantNoches, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                        .addComponent(jpnDatosRe, javax.swing.GroupLayout.PREFERRED_SIZE, 640, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(84, 84, 84))
                    .addGroup(jpnFondoReservarLayout.createSequentialGroup()
                        .addGap(210, 210, 210)
                        .addComponent(btnGenReRe, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(250, 250, 250)
                        .addComponent(btnCanRe, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jpnFondoReservarLayout.createSequentialGroup()
                .addComponent(jblFondoReservar, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jpnFondoReservarLayout.setVerticalGroup(
            jpnFondoReservarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnFondoReservarLayout.createSequentialGroup()
                .addGroup(jpnFondoReservarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpnFondoReservarLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41)
                        .addGroup(jpnFondoReservarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpnFondoReservarLayout.createSequentialGroup()
                                .addGroup(jpnFondoReservarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jdchLlegada, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(10, 10, 10)
                                .addGroup(jpnFondoReservarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jdchSalida, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(10, 10, 10)
                                .addGroup(jpnFondoReservarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtHora, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtMinutos, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jblSepHorMin, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbxAmPm, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(15, 15, 15)
                                .addGroup(jpnFondoReservarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtCantNoches, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jpnDatosRe, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jblFondoReservar, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 153, Short.MAX_VALUE)
                .addGroup(jpnFondoReservarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnGenReRe, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCanRe, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1132, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jpnFondoReservar, javax.swing.GroupLayout.DEFAULT_SIZE, 1132, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 628, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jpnFondoReservar, javax.swing.GroupLayout.DEFAULT_SIZE, 628, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtMinutosKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMinutosKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jbxAmPm.requestFocus();
        }
    }//GEN-LAST:event_txtMinutosKeyPressed

    private void txtHoraKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHoraKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtMinutos.requestFocus();
        }
    }//GEN-LAST:event_txtHoraKeyPressed

    private void btnCanReMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCanReMouseEntered
        btnCanRe.setBackground(new Color(0, 102, 102));
    }//GEN-LAST:event_btnCanReMouseEntered

    private void btnCanReMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCanReMouseExited
        btnCanRe.setBackground(new Color(0, 51, 51));
    }//GEN-LAST:event_btnCanReMouseExited

    private void btnCanReActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCanReActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCanReActionPerformed

    private void btnGenReReFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_btnGenReReFocusGained
        String valorPorNocheStr = txtValPorNoRe.getText().replaceAll("[^\\d]", "");
        double num2, res;
        int num1;

        if (!txtCantNoches.getText().isEmpty() && !txtValPorNoRe.getText().isEmpty()) {
            num1 = Integer.parseInt(txtCantNoches.getText());
            num2 = Double.parseDouble(valorPorNocheStr);
            res = num1 * num2;
            txtTotalRe.setText(formatearConPuntosDeMil(res));
        }
    }//GEN-LAST:event_btnGenReReFocusGained

    private void btnGenReReMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGenReReMouseClicked
        mensajeWARes();
    }//GEN-LAST:event_btnGenReReMouseClicked

    private void btnGenReReMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGenReReMouseEntered
        btnGenReRe.setBackground(new Color(0, 102, 102));
    }//GEN-LAST:event_btnGenReReMouseEntered

    private void btnGenReReMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGenReReMouseExited
        btnGenReRe.setBackground(new Color(0, 51, 51));
    }//GEN-LAST:event_btnGenReReMouseExited

    private void btnGenReReKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnGenReReKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            mensajeWARes();
        }
    }//GEN-LAST:event_btnGenReReKeyPressed

    private void txtNumDocReFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNumDocReFocusLost
        if (txtNumDocRe.getText().equals("") || txtNumDocRe.getText().length() <= 6) {
            JOptionPane.showMessageDialog(this, "Ingrese un numero de documento valido");
            txtNumDocRe.requestFocus();
            return;
        }
        java.sql.Connection dom = null;
        try {
            dom = con.conexion();
            ps = dom.prepareStatement("SELECT Tipo_Documento, Nombres, Apellidos, Nacionalidad, Telefono, Profesion, Estado_Verificacion FROM cliente WHERE Num_Documento = ?");
            ps.setString(1, txtNumDocRe.getText());

            rs = ps.executeQuery();

            if (rs.next()) {
                jbxTipDoc.setSelectedItem(rs.getString("Tipo_Documento"));
                txtNomRe.setText(rs.getString("Nombres"));
                txtApeRe.setText(rs.getString("Apellidos"));
                jbxNacionRe.setSelectedItem(rs.getString("Nacionalidad"));
                String telefono = rs.getString("Telefono");
                String prefijoPais = txtIndicativo.getText();
                String telefonoSinPre = telefono.replaceFirst(Pattern.quote(prefijoPais), "");
                txtTelRe.setText(telefonoSinPre);
                txtProfesion.setText(rs.getString("Profesion"));
                estadoVerificacion = rs.getString("Estado_Verificacion");
                txtValPorNoRe.requestFocus();
            } else {
                jbxTipDoc.setSelectedItem("Seleccione");
                txtNomRe.setText("");
                txtApeRe.setText("");
                jbxNacionRe.setSelectedItem("Seleccione");
                txtTelRe.setText("");
                txtProfesion.setText("");
                JOptionPane.showMessageDialog(null, "TENEMOS UN CLIENTE NUEVO..."
                        + " SE MUY AMABLE");
                jbxTipDoc.requestFocus();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ocurrió un error al buscar el cliente: " + e.getMessage());
        } finally {
            try {
                if (dom != null) {
                    dom.close();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error al cerrar la conexión a la base de datos: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_txtNumDocReFocusLost

    private void txtNumDocReKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNumDocReKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jbxTipDoc.requestFocus();
        }
    }//GEN-LAST:event_txtNumDocReKeyPressed

    private void txtNomReKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNomReKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtApeRe.requestFocus();
        }
    }//GEN-LAST:event_txtNomReKeyPressed

    private void txtNomReKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNomReKeyTyped
        char c = evt.getKeyChar();
        if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && (c < ' ' || c > ' ')) {
            evt.consume();
        }
        String str = txtNomRe.getText();
        StringBuffer strbf = new StringBuffer();
        Matcher match = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(str);
        while (match.find()) {
            match.appendReplacement(strbf, match.group(1).toUpperCase() + match.group(2).toLowerCase());
        }
        txtNomRe.setText(match.appendTail(strbf).toString());
    }//GEN-LAST:event_txtNomReKeyTyped

    private void txtTelReKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTelReKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtProfesion.requestFocus();
        }
    }//GEN-LAST:event_txtTelReKeyPressed

    private void txtTelReKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTelReKeyReleased
        jlbVistoBueno.setVisible(false);
        jlbVistoMalo.setVisible(false);
        String telefono = txtTelRe.getText();
        int tel = telefono.length();
        if (jbxNacionRe.getSelectedItem().toString().equals("Colombia (+57)")) {
            if (tel < 10) {
                jlbVistoMalo.setVisible(true);
            } else if (tel == 10) {
                jlbVistoBueno.setVisible(true);
                btnVerificarWa.setVisible(true);
            } else if (tel > 10) {
                jlbVistoMalo.setVisible(true);
            }
        } else if (jbxNacionRe.getSelectedItem().toString().equals("Ecuador (+593)")) {
            if (tel < 9) {
                jlbVistoMalo.setVisible(true);
            } else if (tel == 9) {
                jlbVistoBueno.setVisible(true);
                btnVerificarWa.setVisible(true);
            } else if (tel > 9) {
                jlbVistoMalo.setVisible(true);
            }
        }
    }//GEN-LAST:event_txtTelReKeyReleased

    private void txtApeReKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtApeReKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jbxNacionRe.requestFocus();
        }
    }//GEN-LAST:event_txtApeReKeyPressed

    private void txtApeReKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtApeReKeyTyped
        char c = evt.getKeyChar();
        if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && (c < ' ' || c > ' ')) {
            evt.consume();
        }
        String str = txtApeRe.getText();
        StringBuffer strbf = new StringBuffer();
        Matcher match = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(str);
        while (match.find()) {
            match.appendReplacement(strbf, match.group(1).toUpperCase() + match.group(2).toLowerCase());
        }
        txtApeRe.setText(match.appendTail(strbf).toString());
    }//GEN-LAST:event_txtApeReKeyTyped

    private void txtValPorNoReKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValPorNoReKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnGenReRe.requestFocus();
        }
    }//GEN-LAST:event_txtValPorNoReKeyPressed

    private void txtValPorNoReKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValPorNoReKeyReleased
        String valorPorNocheStr = txtValPorNoRe.getText().replaceAll("[^\\d]", "");
        if (!valorPorNocheStr.isEmpty()) {
            double valorPorNoche = Double.parseDouble(valorPorNocheStr);
            String cantidadNochesStr = txtCantNoches.getText();
            if (!cantidadNochesStr.isEmpty()) {
                int cantidadNoches = Integer.parseInt(cantidadNochesStr);
                double total = valorPorNoche * cantidadNoches;
                txtTotalRe.setText(formatearConPuntosDeMil(total));
            }
        } else {
            txtTotalRe.setText("");
        }
    }//GEN-LAST:event_txtValPorNoReKeyReleased

    private void jbxTipDocMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbxTipDocMouseClicked
        txtNomRe.requestFocus();
    }//GEN-LAST:event_jbxTipDocMouseClicked

    private void jbxTipDocKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbxTipDocKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtNomRe.requestFocus();
        }
    }//GEN-LAST:event_jbxTipDocKeyPressed

    private void jbxNacionReItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jbxNacionReItemStateChanged
        String selectedItem = (String) jbxNacionRe.getSelectedItem();
        if (selectedItem != null) {
            Matcher matcher = Pattern.compile("\\+?(\\d+)").matcher(selectedItem);
            if (matcher.find()) {
                String indicativo = "+" + matcher.group(1);
                txtIndicativo.setText(indicativo);
            } else {
                txtIndicativo.setText("");
            }
        }
    }//GEN-LAST:event_jbxNacionReItemStateChanged

    private void jbxNacionReMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbxNacionReMouseClicked
        txtTelRe.requestFocus();
    }//GEN-LAST:event_jbxNacionReMouseClicked

    private void jbxNacionReKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbxNacionReKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtTelRe.requestFocus();
        }
    }//GEN-LAST:event_jbxNacionReKeyPressed

    private void txtProfesionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtProfesionKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtValPorNoRe.requestFocus();
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

    private void txtIndicativoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtIndicativoKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIndicativoKeyPressed

    private void jbxAmPmKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbxAmPmKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtNumDocRe.requestFocus();
        }
    }//GEN-LAST:event_jbxAmPmKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCanRe;
    private javax.swing.JButton btnGenReRe;
    private javax.swing.JButton btnVerificarWa;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
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
    private javax.swing.JLabel jblFondoReservar;
    private javax.swing.JLabel jblSepHorMin;
    private javax.swing.JComboBox<String> jbxAmPm;
    private javax.swing.JComboBox<String> jbxNacionRe;
    private javax.swing.JComboBox<String> jbxTipDoc;
    public static com.toedter.calendar.JDateChooser jdchLlegada;
    private com.toedter.calendar.JDateChooser jdchSalida;
    private javax.swing.JLabel jlbMensajeError;
    private javax.swing.JLabel jlbMensajeExitoso;
    private javax.swing.JLabel jlbVistoBueno;
    private javax.swing.JLabel jlbVistoMalo;
    private javax.swing.JPanel jpnDatosRe;
    private javax.swing.JPanel jpnFondoReservar;
    public javax.swing.JLabel lblRollRegistro;
    private javax.swing.JTextField txtApeRe;
    public javax.swing.JTextField txtCantNoches;
    private javax.swing.JTextField txtHora;
    private javax.swing.JTextField txtIndicativo;
    private javax.swing.JTextField txtMinutos;
    private javax.swing.JTextField txtNomRe;
    public javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtNumDocRe;
    public javax.swing.JTextField txtNumHab;
    private javax.swing.JTextField txtProfesion;
    private javax.swing.JTextField txtTelRe;
    private javax.swing.JTextField txtTotalRe;
    private javax.swing.JTextField txtUltValPa;
    private javax.swing.JTextField txtValPorNoRe;
    // End of variables declaration//GEN-END:variables
}
