package vistas;

import conectar.Canectar;
import conectar.Consultasbd;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
//import vistas.Recepciones;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import modelo.Check_In;
import modelo.Cliente;
import modelo.Contable;
import modelo.Hotel;
//import modelo.MensajesWATest;
import modelo.MensajesWATest2;
import modelo.ReporteSire;
import modelo.UsuarioOperando;
//import org.openqa.selenium.WebDriver;

public final class Registros extends javax.swing.JDialog implements Runnable {

    Recepciones res;
    Canectar con = new Canectar();
    String hora, minutos, seg;
    Thread hilo;
    PreparedStatement ps;
    ResultSet rs;
    String MensajeWhatsApp;
    String estadoVerificacion = "No Verificado";
    int idReservaExitosa = 0;
    private UsuarioOperando usus;
    private Hotel hotel;
    java.sql.Date sqlxSalida;
    private boolean hayComision = false;
    private Timer timerVerificarWa = null;
    boolean clienteViejo = false;
    private SwingWorker<Boolean, Void> worker;
    private boolean saliendo = false;
    ImageIcon icon = new ImageIcon("src/img/pequeño.png");
    String nacionalidadSire = "";
    //private boolean botonWaClickeado = false;
    Cliente clienteGlobal = new Cliente();
    private boolean elMensajeYaFueEnviado = false;

    public Registros(Recepciones parent, boolean modal, UsuarioOperando usus, Hotel hotel) {
        super(parent, modal);

        this.res = parent;//new Recepcion(usus, hotel);
        this.hilo = new Thread();

        initComponents();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saliendo = true;
            }
        });

        cbxVendedor.setVisible(false);
        jlbVistoMalo.setVisible(false);
        jlbVistoBueno.setVisible(false);
        btnVerificarWa.setVisible(false);
        jlbMensajeError.setVisible(false);
        jlbMensajeExitoso.setVisible(false);
        jpnHuespedDest.setVisible(false);
        jpnDatosFact.setVisible(false);

        // Obtiene el ClassLoader del contexto actual
        ClassLoader classLoader = getClass().getClassLoader();
        // Carga la imagen como un recurso del ClassLoader
        ImageIcon icon1 = new ImageIcon(classLoader.getResource("img/VistoBueno.png"));
        ImageIcon icon2 = new ImageIcon(classLoader.getResource("img/vistoMalo.png"));
        ImageIcon icon3 = new ImageIcon(classLoader.getResource("img/imatranres.png"));
        // Escalar la imagen si es necesario y establecerla en el JLabel
        jlbVistoBueno.setIcon(new ImageIcon(icon1.getImage().getScaledInstance(jlbVistoBueno.getWidth(), jlbVistoBueno.getHeight(), Image.SCALE_SMOOTH)));
        jlbVistoMalo.setIcon(new ImageIcon(icon2.getImage().getScaledInstance(jlbVistoMalo.getWidth(), jlbVistoMalo.getHeight(), Image.SCALE_SMOOTH)));
        jlbFondo.setIcon(new ImageIcon(icon3.getImage().getScaledInstance(jlbFondo.getWidth(), jlbFondo.getHeight(), Image.SCALE_SMOOTH)));

        con.traerPaises(cbxNacionalidad, cbxPaisProce, cbxPaisDest);
        con.traerTiposdeDocumento(registipdoc);
        this.usus = usus;
        this.hotel = hotel;

        regisnumdoc.requestFocus();
        this.setLocationRelativeTo(null);
        regisfe.setText(Fechaparamos());

        try {
            // Parse the date from regisfe
            SimpleDateFormat formatoFechaActual = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaActual = formatoFechaActual.parse(regisfe.getText());

            // Add one day to fechaActual to get fechaSalida
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fechaActual);
            calendar.add(Calendar.DATE, 1);
            Date fechaSalida = calendar.getTime();

            // Set fechaSalida in jdchFeSa
            jdchFeSa.setDate(fechaSalida);

            // Call calcularNoches with fechaActual and fechaSalida
            calcularNoches(fechaActual, fechaSalida);

        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Error al procesar las fechas: " + e.getMessage(), "Error en la fecha", JOptionPane.ERROR_MESSAGE);
        }

        jdchFeSa.addPropertyChangeListener((java.beans.PropertyChangeEvent evt) -> {
            try {
                SimpleDateFormat formatoFechaActual = new SimpleDateFormat("yyyy-MM-dd");
                Date fechaActual = formatoFechaActual.parse(regisfe.getText());
                Date fechaSalida = jdchFeSa.getDate();
                calcularNoches(fechaActual, fechaSalida);
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(null, "Error al procesar las fechas: " + e.getMessage(), "Error en la fecha", JOptionPane.ERROR_MESSAGE);
                jdchFeSa.setDate(new Date());
            }
        });

        hilo = new Thread(this);
        hilo.start();
        setVisible(true);
        aplicarFormatoPuntosMil(regisvalorhabi);
        aplicarFormatoPuntosMil(registotalpa);
        aplicarFormatoPuntosMil(regiscomi);
        aplicarFormatoPuntosMil(regisvalorneto);
    }

    public String Fechaparamos() {
        Date fecha = new Date();
        SimpleDateFormat FechaAct = new SimpleDateFormat("yyyy-MM-dd");
        return FechaAct.format(fecha);
    }

    public void hora() {
        Calendar calendario = new GregorianCalendar();
        Date horaactual = new Date();
        calendario.setTime(horaactual);
        hora = calendario.get(Calendar.HOUR_OF_DAY) > 9 ? "" + calendario.get(Calendar.HOUR_OF_DAY) : "" + calendario.get(Calendar.HOUR_OF_DAY);
        minutos = calendario.get(Calendar.MINUTE) > 9 ? "" + calendario.get(Calendar.MINUTE) : "0" + calendario.get(Calendar.MINUTE);
        seg = calendario.get(Calendar.SECOND) > 9 ? "" + calendario.get(Calendar.SECOND) : "0" + calendario.get(Calendar.SECOND);
    }

    @Override
    public void run() {
        Thread current = Thread.currentThread();
        while (current == hilo) {
            hora();
            regisllega.setText(hora + ":" + minutos + ":" + seg);
        }
    }

    private String formatearConPuntosDeMil(double numero) {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setGroupingSeparator(',');
        DecimalFormat formatoDelNumero = new DecimalFormat("#,##0", simbolos);
        formatoDelNumero.setGroupingSize(3);
        formatoDelNumero.setGroupingUsed(true);
        return formatoDelNumero.format(numero);
    }

    private void calcularNoches(Date fechaActual, Date fechaSalida) {
        try {
            if (!fechaSalida.before(fechaActual)) {
                // Formatea las fechas usando el formato "año mes día"
                SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy M dd");
                String fechaSalidaFormateada = formatoFecha.format(fechaSalida);
                String fechaActualFormateada = formatoFecha.format(fechaActual);
                // Convierte las fechas formateadas de nuevo a objetos Date
                Date fechaSalidaFormatoNuevo = formatoFecha.parse(fechaSalidaFormateada);
                Date fechaActualFormatoNuevo = formatoFecha.parse(fechaActualFormateada);
                // Calcula la cantidad de noches
                long diferenciaMillis = fechaSalidaFormatoNuevo.getTime() - fechaActualFormatoNuevo.getTime();
                long diferenciaDias = TimeUnit.DAYS.convert(diferenciaMillis, TimeUnit.MILLISECONDS);
                int cantidadNoches = (int) diferenciaDias;
                // Asegura que la cantidad mínima de noches sea 1
                if (cantidadNoches == 0) {
                    cantidadNoches = 1;
                }
                // Muestra la cantidad de noches en el JTextField regiscantno
                regiscantno.setText(Integer.toString(cantidadNoches));
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione una fecha presente o futura!", "Error en la fecha", JOptionPane.ERROR_MESSAGE);
                jdchFeSa.setDate(fechaActual);
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Error al procesar las fechas: " + e.getMessage(), "Error en la fecha", JOptionPane.ERROR_MESSAGE);
        }
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

    public void gestionImpresora() {

        System.out.println("entramos a imprimir la impresora en el metodo gestion impresora de la linea 277 clase registro---------------------");

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String xfecha = now.format(formatoFecha);
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("hh:mm a");
        String xhora = now.format(formatoHora);

        ThermalPrinter thermalPrinter = new ThermalPrinter();
        String clientInfo = String.format("""
                                                  Nombres               : %s
                                                  Apellidos               : %s
                                                  Documento           : %s
                                                  Fecha de llegada  : %s
                                                  Hora de llegada    : %s
                                                  Fecha de salida    : %s
                                                  Num. habitacion   : %s
                                                  Cant. noches        : %s
                                                  Tipo de pago        : %s
                                                  Valor por noche    : %s 
                                                  Valor total             : %s""",
                regisnom.getText(), regisape.getText(), regisnumdoc.getText(), regisfe.getText(), xhora, sqlxSalida,
                regishabi.getText(), regiscantno.getText(), cbxTipoPago.getSelectedItem().toString(), regisvalorhabi.getText(),
                registotalpa.getText());

        String nombre = regisnom.getText().split(" ")[0];
        String apellido = regisape.getText().split(" ")[0];

        List<String[]> productList = new ArrayList<>();
        String nuevoiPdf = thermalPrinter.createPDF(clientInfo, productList, nombre + "_" + apellido);
        System.out.println("llamando a la impresion del pdf en el termalprinter en la linea 307--------------------");
        thermalPrinter.printPDF(nuevoiPdf);
        System.out.println("salimos de termal printer------------------------------------------linea 309 de registros");

        if (hayComision) {

            String xNomUsu = null;
            String nombreComi = null;
            String telComision = null;
            if (cbxVendedor.isVisible() && !cbxVendedor.getSelectedItem().toString().equals("Otro")) {
                xNomUsu = "Usuario: ";
                nombreComi = cbxVendedor.getSelectedItem().toString();
                java.sql.Connection conex = con.conexion();
                PreparedStatement ps = null;
                ResultSet rs = null;
                String sql = "SELECT Telefono FROM usuarios WHERE Usuario = ?";
                try {
                    ps = conex.prepareStatement(sql);
                    ps.setString(1, nombreComi);
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        telComision = rs.getString("Telefono");
                    } else {
                        telComision = "____________________";
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error al traer datos del comisionista " + e.getMessage());
                } finally {
                    try {
                        if (conex != null) {
                            conex.close();
                        }
                        if (ps != null) {
                            ps.close();
                        }
                        if (rs != null) {
                            rs.close();
                        }
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(this, "Error al cerrar recursos de traer datos del comisionista " + e.getMessage());
                    }
                }
            } else if (cbxVendedor.isVisible() && cbxVendedor.getSelectedItem().toString().equals("Otro")) {
                xNomUsu = "Nombre: ";
                nombreComi = "_____________________";
                telComision = "____________________";
            }
            String valorComision = regiscomi.getText();
            thermalPrinter.imprimirComision(xNomUsu, nombreComi, telComision, valorComision, xfecha, xhora);
            thermalPrinter.printCommissionPDF();
        }
    }

    public boolean verificarCampos() {

        /*datos que corresponden a la tabla clienteGlobal*/
        if (regisnumdoc.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese numero de documento!");
            regisnumdoc.requestFocus();
            return false;
        }
        if (registipdoc.getSelectedItem().toString().equals("Seleccionar")) {
            JOptionPane.showMessageDialog(this, "Seleccione el tipo de documento!");
            registipdoc.requestFocus();
            return false;
        }
        if (jdchNacimiento.isVisible() && jdchNacimiento.getDate() == null) {
            JOptionPane.showMessageDialog(this, "La fehca de nacimiento no puede quedar vacia!");
            jdchNacimiento.requestFocus();
            return false;
        }
        if (regisnom.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese nombres!");
            regisnom.requestFocus();
            return false;
        }
        if (regisape.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese apellidos!");
            regisape.requestFocus();
            return false;
        }
        if (cbxPaisProce.getSelectedItem().toString().equals("Seleccionar")) {
            JOptionPane.showMessageDialog(this, "Ingrese la nacionalidad!");
            cbxNacionalidad.requestFocus();
            return false;

        }
        if (registel.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese numero de telefono!");
            registel.requestFocus();
            return false;
        }
        if (regisprofe.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese profesion!");
            regisprofe.requestFocus();
            return false;
        }

        /*datos que corresponden a la tabla check_In*/
        if (cbxPaisProce.getSelectedItem().toString().equals("Seleccionar")) {
            JOptionPane.showMessageDialog(this, "Seleccióne un país de procedencia!");
            cbxPaisProce.requestFocus();
            return false;
        }
        if (cbxDeparProce.isVisible() && cbxDeparProce.getSelectedItem().toString().equals("Seleccionar")) {
            JOptionPane.showMessageDialog(this, "Seleccióne el departamento!");
            cbxDeparProce.requestFocus();
            return false;
        }
        if (cbxCiudadProce.isVisible() && cbxCiudadProce.getSelectedItem().toString().equals("Seleccionar")) {
            JOptionPane.showMessageDialog(this, "Seleccione una ciudad de procedencia!");
            cbxCiudadProce.requestFocus();
            return false;
        }
        if (cbxPaisDest.isVisible() && cbxPaisDest.getSelectedItem().toString().equals("Seleccionar")) {
            JOptionPane.showMessageDialog(this, "Seleccióne un país de destino!");
            cbxPaisDest.requestFocus();
            return false;
        }
        if (cbxDeparDest.isVisible() && cbxDeparDest.getSelectedItem().toString().equals("Seleccionar")) {
            JOptionPane.showMessageDialog(this, "Seleccióne el departamento!");
            cbxDeparDest.requestFocus();
            return false;
        }
        if (cbxCiudadDest.isVisible() && cbxCiudadDest.getSelectedItem().toString().equals("Seleccionar")) {
            JOptionPane.showMessageDialog(this, "Ingrese una ciudad destino!");
            cbxCiudadDest.requestFocus();
            return false;
        }

        /*datos que corresponden a la tabla contable*/
        if (cbxTipoPago.getSelectedItem().equals("Seleccionar")) {
            JOptionPane.showMessageDialog(this, "Seleccione el tipo de pago");
            cbxTipoPago.requestFocus();
            return false;
        }
        if (regiscantno.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la cantidad de noches!");
            regiscantno.requestFocus();
            return false;
        }
        if (regisvalorhabi.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el valor por noche!");
            regisvalorhabi.requestFocus();
            return false;
        }
        if (registotalpa.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Existe un error, comuniquese con el administrador !");
            return false;
        }
        if (regiscomi.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el valor de la comision!");
            regiscomi.requestFocus();
            return false;
        }
        if (regisvalorneto.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Existe un error, comuniquese con el administrador !");
            return false;
        }

        if (cbxVendedor.isVisible() && cbxVendedor.getSelectedItem().toString().equals("Seleccione Vendedor")) {
            JOptionPane.showMessageDialog(this, "Seleccione un vendedor para la comision!");
            cbxVendedor.requestFocus();
            return false;
        }

        return true;
    }

    public void mensajeWaRes(int idCliente) {
        System.out.println(">>> ENTRANDO A mensajeWaRes() con idCliente: " + idCliente);
        System.out.println("Ocultando ventana de registro y llamando a enviarMensajeWA()...");
        this.setVisible(false);
        if (res != null) {
            res.toFront();
        }

        CompletableFuture<String> resultado = enviarMensajeWA();
        resultado.thenAccept(resultadoWa -> {
            SwingUtilities.invokeLater(() -> {
                System.out.println(">>> mensajeWaRes resultado: " + resultadoWa + ", elMensajeYaFueEnviado=" + elMensajeYaFueEnviado);
                if (resultadoWa.equals("Verificado")) {
                    con.actualizarEstadoVerificacion(idCliente, "Verificado");
                } else if (clienteGlobal.getEstado_Verificacion().equals("No Verificado")
                        || clienteGlobal.getEstado_Verificacion().isEmpty()) {
                    con.actualizarEstadoVerificacion(idCliente, "No Verificado");
                }
            });
        }).exceptionally(ex -> {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "No se confirmo el ingreso con el cliente! " + ex, "Error", JOptionPane.ERROR_MESSAGE);
                if (clienteGlobal.getEstado_Verificacion().equals("")
                        || clienteGlobal.getEstado_Verificacion() == null) {
                    con.actualizarEstadoVerificacion(idCliente, "No Verificado");
                }
            });
            return null;
        });
    }

    public CompletableFuture<String> enviarMensajeWA() {

        return CompletableFuture.supplyAsync(() -> {
            String nombreCliente = "*" + regisnom.getText().split(" ")[0] + "*";
            String telefonoWA = txtIndicativo.getText() + registel.getText();
          
            construirMensajeWatsaap(nombreCliente);
           
            MensajesWATest2 enviar = new MensajesWATest2();
            try {
                System.out.println("Llamando a enviar.enviarMensaje()...");
                boolean mensajeEnviado = enviar.enviarMensaje(telefonoWA, MensajeWhatsApp);
                System.out.println("Resultado enviarMensaje: " + mensajeEnviado);
                if (mensajeEnviado) {
                    jlbMensajeExitoso.setVisible(true);
                    btnVerificarWa.setVisible(false);
                    elMensajeYaFueEnviado = true;
                    estadoVerificacion = "Verificado";

                    return "Verificado";

                } else {
                    return "No Verificado";
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Mensaje de WhatsApp ¡NO ENVIADO! ");

            }
            return estadoVerificacion;
        });
    }

    public void registrar(String estadoVerificacion) {

        // Validar que existe un turno activo antes de registrar
        int idTurno = usus.getTurnoPresente();
        if (idTurno <= 0) {
            JOptionPane.showMessageDialog(this, 
                "No hay un turno activo. Debe crear un turno antes de registrar clientes.\n" +
                "Vaya a la pantalla principal y cree un nuevo turno.", 
                "Turno Requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Cliente clienteARegistraR = new Cliente();
        Check_In chek_InARegistrar = new Check_In();
        Contable contableARegistrar = new Contable();
        String tipoMovimiento = "Entrada";
        int idHotel = this.hotel.getIdHoteles();
        /*obtener el id del usuario que esta gestionando el registro*/
        String usuario = "";
        if (cbxVendedor.isVisible() && !cbxVendedor.getSelectedItem().toString().equals("Otro")) {
            usuario = cbxVendedor.getSelectedItem().toString();
        } else {
            usuario = txtUsuario.getText();
        }
        int idUsuario = con.IdClienteViejo("Id_usuario", "usuarios", "Usuario", usuario);

        Date xSalida = jdchFeSa.getDate();
        sqlxSalida = new java.sql.Date(xSalida.getTime());

        /*---------------------------------------------recogemos datos del CLIENTE a registrar------------------------------------------*/
        clienteARegistraR.setNum_Documento(regisnumdoc.getText());
        clienteARegistraR.setTipo_Documento(registipdoc.getSelectedItem().toString());
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
        clienteARegistraR.setNombres(regisnom.getText());
        String apellidos = regisape.getText();
        clienteARegistraR.setApellidos(apellidos);
        clienteARegistraR.setNacionalidad(cbxNacionalidad.getSelectedItem().toString());
        String tel = txtIndicativo.getText() + registel.getText();
        clienteARegistraR.setTelefono(tel);
        clienteARegistraR.setProfesion(regisprofe.getText());
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

        /*---------------------------------------recogemos datos del check_In a registrar-------------------------------------------*/
        String numHabitacion = regishabi.getText();
        chek_InARegistrar.setNumHabitacion(numHabitacion);
        String fechaString = regisfe.getText();
        try {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaLlegada = formatoFecha.parse(fechaString);
            java.sql.Date sqlxllegada = new java.sql.Date(fechaLlegada.getTime());
            chek_InARegistrar.setFechaIngreso(sqlxllegada);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        chek_InARegistrar.setHoraLlegada(hora + ":" + minutos + ":" + seg);
        chek_InARegistrar.setFechaSalida(sqlxSalida);
        //String destino = " ";
        //String procedencia = " ";
        if (cbxPaisProce.getSelectedItem().toString().equals("COLOMBIA")) {
            chek_InARegistrar.setProcedencia(cbxPaisProce.getSelectedItem().toString() + "-" + cbxDeparProce.getSelectedItem().toString() + "-" + cbxCiudadProce.getSelectedItem().toString());
        } else {
            chek_InARegistrar.setProcedencia(cbxPaisProce.getSelectedItem().toString());
        }
        if (cbxPaisDest.getSelectedItem().toString().equals("COLOMBIA")) {
            chek_InARegistrar.setDestino(cbxPaisDest.getSelectedItem().toString() + "-" + cbxDeparDest.getSelectedItem().toString() + "-" + cbxCiudadDest.getSelectedItem().toString());
        } else {
            chek_InARegistrar.setDestino(cbxPaisDest.getSelectedItem().toString());
        }
        int idHabitacion = con.IdClienteViejo("Id_Habitacion", "habitaciones", "Num_Habitacion", numHabitacion);

        chek_InARegistrar.setFkIdHabitacion(idHabitacion);
        chek_InARegistrar.setFkIdTurno(idTurno);
        chek_InARegistrar.setFkIdHotel(idHotel);

        /*-------------------------------------------------------------------recogemos valores CONTABLES que se van a registrar -----------------------------------------------*/
        contableARegistrar.setTipoPago((String) cbxTipoPago.getSelectedItem());
        contableARegistrar.setCantNoches(Integer.parseInt(regiscantno.getText()));
        double valorHabi = obtenerNumeroSinSeparador(regisvalorhabi);
        double totalPa = obtenerNumeroSinSeparador(registotalpa);
        double valorComi = obtenerNumeroSinSeparador(regiscomi);
        double valorNeto = obtenerNumeroSinSeparador(regisvalorneto);

        BigDecimal valorHabitacion = BigDecimal.valueOf(valorHabi);
        BigDecimal totalPago = BigDecimal.valueOf(totalPa);
        BigDecimal valorComision = BigDecimal.valueOf(valorComi);
        BigDecimal valorTotalNeto = BigDecimal.valueOf(valorNeto);

        contableARegistrar.setValorHabitacion(valorHabitacion);
        contableARegistrar.setTotalPago(totalPago);
        contableARegistrar.setComision(valorComision);
        contableARegistrar.setTotalNeto(valorTotalNeto);

        contableARegistrar.setFkIdTurno(idTurno);
        contableARegistrar.setFkIdUsuario(idUsuario);

        /*---------------------------------------------------------------PROCEDEMOS CON EL REGISTRO EN LA BASE DE DATOS----------------------------------------------------------*/
        Cliente clienteRegistrado = new Cliente();
        Check_In check_InRegistrado = new Check_In();
        Contable contableRegistrado = new Contable();

        int idCliente = 0;

        if (clienteViejo) {
            /*vamos a registrar un cliente que ya existe en el hotel*/
            idCliente = clienteGlobal.getId_Cliente();

            clienteRegistrado = con.actualizarClienteDB(clienteARegistraR, idCliente);
            if (clienteRegistrado != null) {

                chek_InARegistrar.setFkIdCliente(idCliente);
                contableARegistrar.setFkIdCliente(idCliente);

            }

        } else {
            /*vamos a registrar un cliente nuevo*/

            clienteRegistrado = con.registrarCliente(clienteARegistraR);
            if (clienteRegistrado != null) {
                idCliente = clienteRegistrado.getId_Cliente();
                chek_InARegistrar.setFkIdCliente(idCliente);
                contableARegistrar.setFkIdCliente(idCliente);

            }

        }

        clienteGlobal = clienteRegistrado;

        check_InRegistrado = con.registrarIngreso(chek_InARegistrar);
        int idCheckIn = check_InRegistrado.getIdCheckIn();
        contableARegistrar.setFkIdCheckIn(idCheckIn);
        contableRegistrado = con.registrarContable(contableARegistrar);

        if (clienteRegistrado != null && check_InRegistrado != null && contableRegistrado != null) {

            Consultasbd bd = new Consultasbd();
            boolean habOcupada = bd.ocuparHabitacion(Integer.parseInt(numHabitacion), idHotel);
            if (habOcupada) {
                System.out.println("la habitacion ya se ocupo en la base de datos...    CLIENTE NUEVO");
            }
            if (idReservaExitosa > 0) {
                con.ReservaExitosa(idReservaExitosa);
            }

            gestionImpresora();
            System.out.println("salimos del metodo gestion impresora en registros metodo registrar y linea 705---------------------------");

            res.activarHabitaciones(res.jdchFechaHabs.getDate(), hotel.getIdHoteles());

            JOptionPane.showMessageDialog(this, "El cliente de la habitacion " + numHabitacion + " se registro exitosamente.");

        } else {
            JOptionPane.showMessageDialog(this, "Hubo un error en el registro, intente de nuevo!");
            return;

        }

        String fechaMovSire = regisfe.getText();
        LocalDate fecha = LocalDate.parse(fechaMovSire);
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("d/M/yyyy");
        String fechaMovimiento = fecha.format(formato);

        try {

            // Reportar a SIRE si el cliente tiene nacionalidad extranjera (diferente a COLOMBIA)
            // La nacionalidad determina si es extranjero, no el tipo de documento
            // Ejemplos: Venezolano con cédula colombiana → Reportar | Colombiano con cédula de extranjería → No reportar
            if (!nacionalidadSire.equals("COLOMBIA")) {

                String paisProce = cbxPaisProce.getSelectedItem().toString();
                String deparProce = "NA";
                String ciudadProce = "NA";
                if (paisProce.equals("COLOMBIA")) {
                    if (cbxDeparProce.isVisible()) {
                        deparProce = cbxDeparProce.getSelectedItem().toString();
                    }
                    if (cbxCiudadProce.isVisible()) {
                        ciudadProce = cbxCiudadProce.getSelectedItem().toString();
                    }
                }

                String paisDest = cbxPaisDest.getSelectedItem().toString();
                String deparDest = "NA";
                String ciudadDest = "NA";
                if (paisDest.equals("COLOMBIA")) {
                    if (cbxDeparDest.isVisible()) {
                        deparDest = cbxDeparDest.getSelectedItem().toString();
                    }
                    if (cbxCiudadDest.isVisible()) {
                        ciudadDest = cbxCiudadDest.getSelectedItem().toString();
                    }

                }

                // Formatear fecha de nacimiento para SIRE (formato d/M/yyyy, ej: 25/12/1980)
                SimpleDateFormat formatoSire = new SimpleDateFormat("d/M/yyyy");
                String fechaNacimientoFormateada = formatoSire.format(clienteARegistraR.getFecha_Nacimiento());
                
                manejarFlujoAsincronoParaExtranjeros(tipoMovimiento, fechaMovimiento, clienteARegistraR.getTipo_Documento(), fechaNacimientoFormateada,
                        clienteARegistraR.getNum_Documento(), primerApellido, segundoApellido, clienteARegistraR.getNombres(), nacionalidadSire, paisProce, deparProce, ciudadProce,
                        paisDest, deparDest, ciudadDest);
                
                // IMPORTANTE: Para extranjeros, el WhatsApp se gestiona dentro de 
                // manejarFlujoAsincronoParaExtranjeros() (ya sea con o sin SIRE)
                // Por eso hacemos return aquí para evitar el envío duplicado de WhatsApp
                // que está al final de este método
                return;

            }

        } catch (HeadlessException | NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Hubo un error en el registro del cliente\nintentelo de nuevo\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
            registipdoc.requestFocus();
            this.setVisible(true);
        }
        /*si el mensaje de watsaap no ha sido enviado, entonces lo envia*/
        System.out.println("========== VERIFICANDO ENVIO DE WHATSAPP ==========");
        System.out.println("elMensajeYaFueEnviado = " + elMensajeYaFueEnviado);
        System.out.println("ID Cliente: " + clienteGlobal.getId_Cliente());
        if (!elMensajeYaFueEnviado) {
            System.out.println("Llamando a mensajeWaRes()...");
            mensajeWaRes(clienteGlobal.getId_Cliente());

        } else {
            System.out.println("se va actualizar el estado de verificacion desde el metodo registra con el boton");
            con.actualizarEstadoVerificacion(clienteGlobal.getId_Cliente(), this.estadoVerificacion);
        }

        this.dispose();
    }

    private void manejarFlujoAsincronoParaExtranjeros(String tipoMov, String fechaMov, String tipoDoc, String fechaNacimiento,
            String numDoc, String primerApe, String segApe, String nombres, String nacionalidad,
            String paisProce, String deparProce, String ciudadProce, String paisDest,
            String deparDest, String ciudadDest) {

        SwingUtilities.invokeLater(() -> {
            ClassLoader classLoader = getClass().getClassLoader();
            ImageIcon iconSire = new ImageIcon(classLoader.getResource("img/pequeño.png"));

            String nombreHuesped = regisnom.getText().split(" ")[0];

            String[] opciones = {"GESTIONAR", "CANCELAR"};
            int seleccion = JOptionPane.showOptionDialog(
                    null,
                    "El huésped " + nombreHuesped + " es de nacionalidad " + nacionalidad + ".\n\n"
                    + "De acuerdo con la normativa colombiana, se debe\n"
                    + "reportar su ingreso al sistema SIRE de Migración\n"
                    + "Colombia.\n\n"
                    + "¿Desea gestionar el reporte de entrada ahora?",
                    "Reporte SIRE de Entrada",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    iconSire,
                    opciones,
                    opciones[0]
            );

            if (seleccion == 0) {
                // Realizar reporte SIRE y luego enviar mensaje de WhatsApp
                realizarReporteSIREYEnviarWhatsApp(
                        this.estadoVerificacion, tipoMov, fechaMov, tipoDoc, fechaNacimiento, numDoc, primerApe, segApe,
                        nombres, nacionalidad, paisProce, deparProce, ciudadProce, paisDest, deparDest, ciudadDest);
            } else {
                // Solo enviar mensaje de WhatsApp
                /*si el mensaje de watsaap no ha sido enviado, entonces lo envia*/
                if (!elMensajeYaFueEnviado) {
                    mensajeWaRes(clienteGlobal.getId_Cliente());

                } else {
                    System.out.println("se va actualizar el estado de verificacion desde el metodo registra con el boton");
                    con.actualizarEstadoVerificacion(clienteGlobal.getId_Cliente(), this.estadoVerificacion);
                }
            }

            this.dispose(); // Cerrar la ventana después de la selección del usuario
        });
    }

    private void realizarReporteSIREYEnviarWhatsApp(String estadoVerificacion, String tipoMov, String fechaMov, String tipoDoc, String fechaNacimiento,
            String numDoc, String primerApe, String segApe, String nombres, String nacionalidad,
            String paisProce, String deparProce, String ciudadProce, String paisDest,
            String deparDest, String ciudadDest) {

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    System.out.println("vamos a hacer el reporte a sire");
                    ReporteSire reporte = new ReporteSire(tipoMov, fechaMov, tipoDoc, fechaNacimiento, numDoc,
                            primerApe, segApe, nombres, nacionalidad, paisProce, deparProce, ciudadProce, paisDest, deparDest, ciudadDest);
                    reporte.abrirPagina();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();

                    Thread.sleep(2000);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();

                }

                /*si el mensaje de watsaap no ha sido enviado, entonces lo envia*/
                if (!elMensajeYaFueEnviado) {
                    mensajeWaRes(clienteGlobal.getId_Cliente());

                } else {
                    System.out.println("se va actualizar el estado de verificacion desde el metodo registra con el boton");
                    con.actualizarEstadoVerificacion(clienteGlobal.getId_Cliente(), estadoVerificacion);
                }
            }
        };
        worker.execute();
        this.dispose();
    }

    public void agitarBoton() {
        timerVerificarWa = new Timer(10, new ActionListener() {
            int direction = 1;
            int initialPos = btnVerificarWa.getLocation().x;

            @Override
            public void actionPerformed(ActionEvent e) {
                int newPos = btnVerificarWa.getLocation().x + (1 * direction);
                if (newPos > initialPos + 1 || newPos < initialPos - 1) {
                    direction *= -1;
                }
                btnVerificarWa.setLocation(newPos, btnVerificarWa.getLocation().y);
            }
        });
        timerVerificarWa.start();
    }

    public void construirMensajeWatsaap(String nombreCliente) {
        if (clienteViejo) {
            MensajeWhatsApp = "¡Hola, " + nombreCliente.toUpperCase() + "🫱🏻‍🫲🏼!\n\n"
                    + "Qué bueno tenerte de vuelta en el *" + hotel.getNombreHotel().toUpperCase() + "*.\n\n"
                    + "Estamos agradecidos por tu continua preferencia y lealtad.\n\n"
                    + "*Por favor, recuerda:*\n"
                    + "1️⃣ Realizar el *Check Out* antes de la *1:00PM*⌚ en tu fecha de salida. Exceder este límite de tiempo puede ocasionar cobros adicionales al valor de esta factura.\n\n"
                    + "2️⃣ Nuestro hotel cuenta con servicio de lavado de ropa, solicítalo con anticipación.\n\n"
                    + "3️⃣ Nuestro hotel cuenta con servicio de transporte al aeropuerto. Contáctanos al WhatsApp +573232951780 y agenda tu viaje.\n\n"
                    + "4️⃣ Puedes comunicarte con *RECEPCIÓN* marcando cero *(0)* desde el citófono de tu habitación.\n\n"
                    + "5️⃣ Recuerda que cuentas con servicio de transporte gratuito desde la terminal de transportes de Pasto hasta nuestras"
                    + " instalaciones, en horario de 3:00PM hasta las 3:00AM. Solicitud anticipada.\n\n"
                    + "6️⃣ Para hacer una reserva, o si tienes alguna duda, queja, sugerencia o reclamo, por favor comunícate con nosotros:\n"
                    + "     *RECEPCIÓN:*\n"
                    + "          📞6027418969\n"
                    + "          📱+573145519811\n"
                    + "     *ADMINISTRACIÓN:*\n"
                    + "          📱+573232951780\n\n"
                    + "Deseamos que tu estancia en el *" + hotel.getNombreHotel() + "* sea nuevamente maravillosa. *MUCHAS GRACIAS* por preferirnos.\n\n"
                    + "*Atentamente:*\n"
                    + "" + hotel.getNombreHotel() + "\n"
                    + "" + hotel.getDireccionHotel() + "\n"
                    + "" + hotel.getCiudadHotel() + " - " + hotel.getDepartamentoHotel() + " - " + hotel.getPaisHotel() + "";

        } else {
            MensajeWhatsApp = "Cordial saludo " + nombreCliente.toUpperCase() + "🫱🏻‍🫲🏼\n\n"
                    + "Es un placer darle la bienvenida al *" + hotel.getNombreHotel().toUpperCase() + "*, el hecho de que "
                    + "nos prefiera es el mayor de nuestros elogios.\n\n"
                    + "*Recuerde:*\n"
                    + "1️⃣ Realizar el *Check Out* antes de la *1:00PM*⌚ en su fecha de salida. Exceder este límite de tiempo puede ocasionar cobros adicionales al valor de esta factura.\n\n"
                    + "2️⃣ Nuestro hotel cuenta con servicio de lavado de ropa, solicítelo con anticipación.\n\n"
                    + "3️⃣ Nuestro hotel cuenta con servicio de transporte al aeropuerto. Contáctenos al WhatsApp +573232951780 y agende su viaje.\n\n"
                    + "4️⃣ Para comunicarse con *RECEPCIÓN*, marque cero *(0)* desde el citófono de su habitación.\n\n"
                    + "5️⃣ Recuerde que cuenta con servicio de transporte gratuito desde la terminal de transportes de Pasto hasta nuestras "
                    + "instalaciones, en horario de 3:00PM hasta las 3:00AM. Solicitud anticipada.\n\n"
                    + "6️⃣ Si desea hacer una reserva, o tiene alguna duda, queja, sugerencia o reclamo, comuníquese con nosotros:\n"
                    + "     *RECEPCIÓN:*\n"
                    + "          📞6027418969\n"
                    + "          📱+573145519811\n"
                    + "     *ADMINISTRACIÓN:*\n"
                    + "          📱+573232951780\n\n"
                    + "Esperamos que su experiencia en el *" + hotel.getNombreHotel() + "* sea totalmente agradable y de nuevo, *MUCHAS GRACIAS* por estar aquí.\n\n"
                    + "*Atentamente:*\n"
                    + "" + hotel.getNombreHotel() + "\n"
                    + "" + hotel.getDireccionHotel() + "\n"
                    + "" + hotel.getCiudadHotel() + " - " + hotel.getDepartamentoHotel() + " - " + hotel.getPaisHotel() + "";

        }
    }

    public void visualizarJpnDest(JComboBox<String> cbxpais, JComboBox<String> cbxDepar, JComboBox<String> cbxCiu, JPanel panel) {
        Object pais = cbxpais.getSelectedItem();
        Object departamento = cbxDepar.getSelectedItem();
        Object ciudad = cbxCiu.getSelectedItem();

        if (pais == null || pais.equals("Seleccionar")) {
            panel.setVisible(false);
        } else if (pais.equals("COLOMBIA")) {
            if (departamento != null && !departamento.equals("Seleccionar")
                    && ciudad != null && !ciudad.equals("Seleccionar")) {
                panel.setVisible(true);
            } else {
                panel.setVisible(false);
            }
        } else {
            panel.setVisible(true);
        }
    }

    public void procesoDatosCliViejo() {
        if (saliendo) {
            return;
        }
        if (regisnumdoc.getText().length() <= 5) {
            JOptionPane.showMessageDialog(this, "Ingrese un numero de documento valido");
            regisnumdoc.requestFocus();
            return;
        }

        try {
            clienteGlobal = con.buscarClientePorCedula(regisnumdoc.getText());

            if (clienteGlobal != null) {
//                clienteGlobal.setId_Cliente(rs.getInt("Id_Cliente"));
//                clienteGlobal.setNum_Documento(rs.getString("Num_Documento"));
//                clienteGlobal.setTipo_Documento(rs.getString("Tipo_Documento"));
//                clienteGlobal.setFecha_Nacimiento(rs.getDate("Fecha_Nacimiento"));
//                clienteGlobal.setNombres(rs.getString("Nombres"));
//                clienteGlobal.setApellidos(rs.getString("Apellidos"));
//                clienteGlobal.setNacionalidad(rs.getString("Nacionalidad"));
//                clienteGlobal.setTelefono(rs.getString("Telefono"));
//                clienteGlobal.setProfesion(rs.getString("Profesion"));
//                clienteGlobal.setEstado_Verificacion(rs.getString("Estado_Verificacion"));
//                clienteGlobal.setEdad(rs.getInt("Edad"));
//                clienteGlobal.setReporte_Sire(rs.getBoolean("Reporte_Sire"));

                // Establecer la interfaz con los datos del cliente
                jdchNacimiento.setDate(clienteGlobal.getFecha_Nacimiento());
                registipdoc.setSelectedItem(clienteGlobal.getTipo_Documento());
                regisnom.setText(clienteGlobal.getNombres());
                regisape.setText(clienteGlobal.getApellidos());
                cbxNacionalidad.setSelectedItem(clienteGlobal.getNacionalidad());
                String telefono = clienteGlobal.getTelefono();
                String prefijoPais = txtIndicativo.getText();
                String telefonoSinPre = telefono.replaceFirst(Pattern.quote(prefijoPais), "");
                registel.setText(telefonoSinPre);
                regisprofe.setText(clienteGlobal.getProfesion());

                clienteViejo = true;
                btnVerificarWa.setVisible(true);
                cbxPaisProce.requestFocus();
            } else {
                clienteGlobal = null;
                clienteViejo = false;
                // Limpiar la interfaz
                regisnom.setText("");
                regisape.setText("");
                cbxNacionalidad.setSelectedItem("Seleccione");
                registel.setText("");
                regisprofe.setText("");
                JOptionPane.showMessageDialog(null, "TENEMOS UN CLIENTE NUEVO..."
                        + " SE MUY AMABLE");
                registipdoc.requestFocus();
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, "Error al traer los datos del cliente :" + e.getMessage());
        }
    }

    public void cbxdeparProceCambio() {
        if (cbxDeparProce.getSelectedItem() != null && !cbxDeparProce.getSelectedItem().equals("Seleccionar")) {
            String departamento = cbxDeparProce.getSelectedItem().toString();
            int idC = con.obtenerIdDepartamento(departamento);
            con.traerCiudadesPorDepartamento(cbxCiudadProce, idC);
            jlbCiudadProce.setVisible(true);
            cbxCiudadProce.setVisible(true);

        } else {
            cbxCiudadProce.removeAllItems();
            jlbCiudadProce.setVisible(false);
            cbxCiudadProce.setVisible(false);
        }
    }

    public void procesoRegistro() {
        System.out.println("[REGISTRO] ========== procesoRegistro() INICIADO ==========");
        // PRIMERO: Verificar si hay una ventana de reporte SIRE abierta
        // Esto se hace ANTES de cualquier otra validación o proceso
        System.out.println("[REGISTRO] Verificando si hay ventana SIRE abierta...");
        if (ReporteSire.hayVentanaAbierta()) {
            System.out.println("[REGISTRO] ⚠ Intentó registrar con ventana SIRE abierta");
            JOptionPane.showMessageDialog(this,
                "⚠ REGISTRO SIRE PENDIENTE\n\n" +
                "No se puede registrar un nuevo huésped hasta que\n" +
                "complete el reporte SIRE anterior.\n\n" +
                "Por favor:\n" +
                "1. Complete el formulario SIRE que está abierto\n" +
                "2. Haga clic en 'AGREGAR REGISTRO'\n" +
                "3. Cierre la ventana del navegador\n" +
                "4. Intente nuevamente el registro",
                "Registro SIRE Pendiente",
                JOptionPane.WARNING_MESSAGE);
            
            // Maximizar y traer al frente la ventana SIRE pendiente
            ReporteSire.maximizarVentanaPendiente();
            System.out.println("[REGISTRO] ✓ Proceso DETENIDO por ventana SIRE abierta");
            return; // DETENER COMPLETAMENTE el proceso
        }
        
        System.out.println("[REGISTRO] ✓ No hay ventana SIRE abierta, continuando...");
        // SEGUNDO: Verificar campos y proceder con el registro
        if (verificarCampos()) {
            registrar(estadoVerificacion);
        }
    }

    //@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        JPanel1 = new javax.swing.JPanel();
        regisnom = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        regisnumdoc = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        registipdoc = new javax.swing.JComboBox<>();
        regisape = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        registel = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        regisprofe = new javax.swing.JTextField();
        cbxNacionalidad = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        regisfe = new javax.swing.JTextField();
        jdchFeSa = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        regishabi = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        regisllega = new javax.swing.JTextField();
        lblRollregistro = new javax.swing.JLabel();
        nombreUsuario = new javax.swing.JTextField();
        txtUsuario = new javax.swing.JTextField();
        lblRollregistro1 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jlbVistoMalo = new javax.swing.JLabel();
        jlbVistoBueno = new javax.swing.JLabel();
        btnVerificarWa = new javax.swing.JButton();
        txtIndicativo = new javax.swing.JTextField();
        jlbMensajeError = new javax.swing.JLabel();
        jlbMensajeExitoso = new javax.swing.JLabel();
        jlbDeparProce = new javax.swing.JLabel();
        cbxDeparProce = new javax.swing.JComboBox<>();
        jlbCiudadProce = new javax.swing.JLabel();
        cbxCiudadProce = new javax.swing.JComboBox<>();
        jSeparator2 = new javax.swing.JSeparator();
        cbxPaisProce = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jpnHuespedDest = new javax.swing.JPanel();
        cbxPaisDest = new javax.swing.JComboBox<>();
        jlbDeparDest = new javax.swing.JLabel();
        cbxDeparDest = new javax.swing.JComboBox<>();
        jlbCiudadDest = new javax.swing.JLabel();
        cbxCiudadDest = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jpnDatosFact = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        regiscomi = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        regisvalorhabi = new javax.swing.JTextField();
        cbxTipoPago = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        registotalpa = new javax.swing.JTextField();
        regiscantno = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        regisvalorneto = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        regisgenre = new javax.swing.JButton();
        regisvolmen = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        cbxVendedor = new javax.swing.JComboBox<>();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jPanel6 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        jlbFondo = new javax.swing.JLabel();
        jlbNacimiento = new javax.swing.JLabel();
        jdchNacimiento = new com.toedter.calendar.JDateChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jScrollPane1.setBackground(new java.awt.Color(0, 102, 102));
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel1.setBackground(new java.awt.Color(255, 255, 255));

        regisnom.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                regisnomKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                regisnom(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel3.setText("NUM.  DOCUMENTO:");

        regisnumdoc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                regisnumdocFocusLost(evt);
            }
        });
        regisnumdoc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                regisnumdocKeyPressed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel4.setText("NOMBRES:");

        jLabel5.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel5.setText("APELLIDOS:");

        registipdoc.setBackground(new java.awt.Color(255, 219, 95));
        registipdoc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                registipdocKeyPressed(evt);
            }
        });

        regisape.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                regisapeKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                regisape(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel6.setText("NACIONALIDAD:");

        jLabel7.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel7.setText("TELEFONO:");

        registel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                registelKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                registelKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                registel(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel10.setText("PROFESION:");

        regisprofe.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                regisprofeKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                regisprofeKeyTyped(evt);
            }
        });

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

        jPanel1.setBackground(new java.awt.Color(0, 102, 102));

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("FECHA LLEGADA :");

        regisfe.setEditable(false);
        regisfe.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N

        jdchFeSa.setBackground(new java.awt.Color(255, 219, 95));
        jdchFeSa.setDateFormatString("EEE dd-MMM-yyyy");

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("FECHA DE SALIDA:");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("NUM. HABITACION:");

        regishabi.setEditable(false);
        regishabi.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N

        jLabel11.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("HORA DE LLEGADA:");

        regisllega.setEditable(false);
        regisllega.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        regisllega.setText("00:00:00");

        lblRollregistro.setBackground(new java.awt.Color(255, 255, 255));
        lblRollregistro.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        lblRollregistro.setForeground(new java.awt.Color(255, 255, 255));
        lblRollregistro.setText("CARGO");

        nombreUsuario.setEditable(false);
        nombreUsuario.setBackground(new java.awt.Color(0, 102, 102));
        nombreUsuario.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        nombreUsuario.setForeground(new java.awt.Color(255, 255, 255));

        txtUsuario.setEditable(false);
        txtUsuario.setBackground(new java.awt.Color(0, 102, 102));
        txtUsuario.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        txtUsuario.setForeground(new java.awt.Color(255, 255, 255));

        lblRollregistro1.setBackground(new java.awt.Color(255, 255, 255));
        lblRollregistro1.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        lblRollregistro1.setForeground(new java.awt.Color(255, 255, 255));
        lblRollregistro1.setText("USUARIO");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(regisfe, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jdchFeSa, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(90, 90, 90)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(regishabi, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(regisllega, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(108, 108, 108)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblRollregistro1, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                    .addComponent(lblRollregistro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nombreUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regisfe, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regishabi, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRollregistro, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nombreUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jdchFeSa, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(regisllega, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblRollregistro1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jLabel19.setBackground(new java.awt.Color(0, 0, 0));
        jLabel19.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel19.setText("TIPO DE DOCUMENTO:");

        jLabel20.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel20.setText("PROCEDENCIA:");

        jlbVistoMalo.setForeground(new java.awt.Color(0, 102, 102));
        jlbVistoMalo.setText("M");
        jlbVistoMalo.setFocusable(false);

        jlbVistoBueno.setForeground(new java.awt.Color(0, 102, 102));
        jlbVistoBueno.setText("B");
        jlbVistoBueno.setFocusable(false);

        btnVerificarWa.setBackground(new java.awt.Color(0, 51, 51));
        btnVerificarWa.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        btnVerificarWa.setForeground(new java.awt.Color(255, 255, 255));
        btnVerificarWa.setText("ENVIAR WA");
        btnVerificarWa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerificarWaActionPerformed(evt);
            }
        });

        txtIndicativo.setEditable(false);
        txtIndicativo.setFocusable(false);

        jlbMensajeError.setBackground(new java.awt.Color(0, 0, 0));
        jlbMensajeError.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
        jlbMensajeError.setForeground(new java.awt.Color(255, 51, 51));
        jlbMensajeError.setText("WhatsApp Error");
        jlbMensajeError.setFocusable(false);

        jlbMensajeExitoso.setBackground(new java.awt.Color(0, 0, 0));
        jlbMensajeExitoso.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
        jlbMensajeExitoso.setForeground(new java.awt.Color(51, 204, 0));
        jlbMensajeExitoso.setText("WhatsApp Exitóso");
        jlbMensajeExitoso.setFocusable(false);

        jlbDeparProce.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbDeparProce.setText("DEPARTAMENTO:");

        cbxDeparProce.setBackground(new java.awt.Color(255, 219, 95));
        cbxDeparProce.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxDeparProceItemStateChanged(evt);
            }
        });
        cbxDeparProce.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cbxDeparProceKeyPressed(evt);
            }
        });

        jlbCiudadProce.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbCiudadProce.setText("CIUDAD:");

        cbxCiudadProce.setBackground(new java.awt.Color(255, 219, 95));
        cbxCiudadProce.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxCiudadProceItemStateChanged(evt);
            }
        });
        cbxCiudadProce.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cbxCiudadProceKeyPressed(evt);
            }
        });

        jSeparator2.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator2.setForeground(new java.awt.Color(255, 179, 39));

        cbxPaisProce.setBackground(new java.awt.Color(255, 219, 95));
        cbxPaisProce.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxPaisProceItemStateChanged(evt);
            }
        });
        cbxPaisProce.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cbxPaisProceKeyPressed(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(0, 51, 51));

        jLabel2.setFont(new java.awt.Font("Dialog", 3, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 179, 39));
        jLabel2.setText("DESTINO DEL HUESPED:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel4.setBackground(new java.awt.Color(0, 51, 51));

        jLabel27.setFont(new java.awt.Font("Dialog", 3, 14)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(255, 179, 39));
        jLabel27.setText("DATOS DEL CLIENTE:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel27)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel5.setBackground(new java.awt.Color(0, 51, 51));

        jLabel28.setFont(new java.awt.Font("Dialog", 3, 14)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(255, 179, 39));
        jLabel28.setText("DATOS DE FACTURACION :");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel28)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jpnHuespedDest.setBackground(new java.awt.Color(255, 255, 255));

        cbxPaisDest.setBackground(new java.awt.Color(255, 219, 95));
        cbxPaisDest.setToolTipText("");
        cbxPaisDest.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxPaisDestItemStateChanged(evt);
            }
        });
        cbxPaisDest.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cbxPaisDestKeyPressed(evt);
            }
        });

        jlbDeparDest.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbDeparDest.setText("DEPARTAMENTO :");

        cbxDeparDest.setBackground(new java.awt.Color(255, 219, 95));
        cbxDeparDest.setToolTipText("");
        cbxDeparDest.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxDeparDestItemStateChanged(evt);
            }
        });
        cbxDeparDest.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cbxDeparDestKeyPressed(evt);
            }
        });

        jlbCiudadDest.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbCiudadDest.setText("CIUDAD DESTINO:");

        cbxCiudadDest.setBackground(new java.awt.Color(255, 219, 95));
        cbxCiudadDest.setToolTipText("");
        cbxCiudadDest.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxCiudadDestItemStateChanged(evt);
            }
        });
        cbxCiudadDest.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cbxCiudadDestKeyPressed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel9.setText("PAIS DE DESTINO:");

        javax.swing.GroupLayout jpnHuespedDestLayout = new javax.swing.GroupLayout(jpnHuespedDest);
        jpnHuespedDest.setLayout(jpnHuespedDestLayout);
        jpnHuespedDestLayout.setHorizontalGroup(
            jpnHuespedDestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnHuespedDestLayout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jpnHuespedDestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                    .addComponent(jlbCiudadDest, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnHuespedDestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbxPaisDest, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxCiudadDest, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(50, 50, 50)
                .addComponent(jlbDeparDest, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxDeparDest, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jpnHuespedDestLayout.setVerticalGroup(
            jpnHuespedDestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnHuespedDestLayout.createSequentialGroup()
                .addGroup(jpnHuespedDestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jpnHuespedDestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbxPaisDest, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jlbDeparDest, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbxDeparDest, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12)
                .addGroup(jpnHuespedDestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlbCiudadDest, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxCiudadDest, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jpnDatosFact.setBackground(new java.awt.Color(255, 255, 255));

        jLabel8.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel8.setText("TIPO DE PAGO:");

        jLabel12.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel12.setText("CANT. NOCHES:");

        jLabel13.setBackground(new java.awt.Color(0, 0, 0));
        jLabel13.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel13.setText("COMISION:");

        regiscomi.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        regiscomi.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                regiscomiFocusLost(evt);
            }
        });
        regiscomi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                regiscomiMouseClicked(evt);
            }
        });
        regiscomi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                regiscomiKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                regiscomiKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                regiscomiKeyTyped(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel14.setText("VALOR HABITACION:");

        regisvalorhabi.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        regisvalorhabi.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                regisvalorhabiFocusLost(evt);
            }
        });
        regisvalorhabi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                regisvalorhabiMouseClicked(evt);
            }
        });
        regisvalorhabi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                regisvalorhabiKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                regisvalorhabiKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                regisvalorhabiKeyTyped(evt);
            }
        });

        cbxTipoPago.setBackground(new java.awt.Color(255, 219, 95));
        cbxTipoPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar", "Efectivo", "Neki", "Bancolombia", "Datafono Bold", "Davivienda", "DaviPlata", " " }));
        cbxTipoPago.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cbxTipoPagoKeyPressed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel15.setText("TOTAL A PAGAR:");

        registotalpa.setEditable(false);
        registotalpa.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N

        regiscantno.setEditable(false);
        regiscantno.setBackground(new java.awt.Color(255, 255, 51,80));
        regiscantno.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        regiscantno.addVetoableChangeListener(new java.beans.VetoableChangeListener() {
            public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                regiscantnoVetoableChange(evt);
            }
        });

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel18.setText("VALOR NETO:");

        regisvalorneto.setEditable(false);
        regisvalorneto.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        regisvalorneto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                regisvalornetoKeyTyped(evt);
            }
        });

        regisgenre.setBackground(new java.awt.Color(0, 51, 51));
        regisgenre.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        regisgenre.setForeground(new java.awt.Color(255, 255, 255));
        regisgenre.setText("GENERAR REGISTRO");
        regisgenre.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                regisgenreFocusGained(evt);
            }
        });
        regisgenre.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                regisgenreMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                regisgenreMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                regisgenreMouseExited(evt);
            }
        });
        regisgenre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                regisgenreKeyPressed(evt);
            }
        });

        regisvolmen.setBackground(new java.awt.Color(0, 51, 51));
        regisvolmen.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        regisvolmen.setForeground(new java.awt.Color(255, 255, 255));
        regisvolmen.setText("VOLVER AL MENU");
        regisvolmen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                regisvolmenMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                regisvolmenMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                regisvolmenMouseExited(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 179, 39));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cbxVendedor.setBackground(new java.awt.Color(255, 219, 95));
        cbxVendedor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cbxVendedorKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jpnDatosFactLayout = new javax.swing.GroupLayout(jpnDatosFact);
        jpnDatosFact.setLayout(jpnDatosFactLayout);
        jpnDatosFactLayout.setHorizontalGroup(
            jpnDatosFactLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnDatosFactLayout.createSequentialGroup()
                .addGroup(jpnDatosFactLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 1367, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jpnDatosFactLayout.createSequentialGroup()
                        .addGroup(jpnDatosFactLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpnDatosFactLayout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addGroup(jpnDatosFactLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jpnDatosFactLayout.createSequentialGroup()
                                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(regiscantno, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jpnDatosFactLayout.createSequentialGroup()
                                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(regisvalorhabi, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jpnDatosFactLayout.createSequentialGroup()
                                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(registotalpa, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(50, 50, 50)
                                .addGroup(jpnDatosFactLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jpnDatosFactLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(regisvalorneto, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                                    .addComponent(cbxTipoPago, 0, 200, Short.MAX_VALUE)
                                    .addComponent(regiscomi, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbxVendedor, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jpnDatosFactLayout.createSequentialGroup()
                                .addGap(190, 190, 190)
                                .addComponent(regisgenre, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(198, 198, 198)
                                .addComponent(regisvolmen, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(412, 412, 412)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpnDatosFactLayout.setVerticalGroup(
            jpnDatosFactLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnDatosFactLayout.createSequentialGroup()
                .addGroup(jpnDatosFactLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regisvalorhabi, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxTipoPago, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jpnDatosFactLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regiscantno, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regiscomi, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxVendedor, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnDatosFactLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(registotalpa, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regisvalorneto, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(48, 48, 48)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jpnDatosFactLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(regisgenre, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regisvolmen, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel6.setBackground(new java.awt.Color(0, 51, 51));

        jLabel21.setFont(new java.awt.Font("Dialog", 3, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 179, 39));
        jLabel21.setText("PROCEDENCIA DEL HUESPED:");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel21)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jlbNacimiento.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbNacimiento.setText("FECHA NACIMIENTO :");

        jdchNacimiento.setBackground(new java.awt.Color(255, 219, 95));
        jdchNacimiento.setDateFormatString("d/MMMM/yyyy");

        javax.swing.GroupLayout JPanel1Layout = new javax.swing.GroupLayout(JPanel1);
        JPanel1.setLayout(JPanel1Layout);
        JPanel1Layout.setHorizontalGroup(
            JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator5)
            .addGroup(JPanel1Layout.createSequentialGroup()
                .addGroup(JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jpnHuespedDest, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jpnDatosFact, javax.swing.GroupLayout.PREFERRED_SIZE, 967, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(JPanel1Layout.createSequentialGroup()
                .addGroup(JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(JPanel1Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addGroup(JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(JPanel1Layout.createSequentialGroup()
                                .addComponent(jlbCiudadProce, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cbxCiudadProce, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(JPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbxPaisProce, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(50, 50, 50)
                        .addComponent(jlbDeparProce, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbxDeparProce, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(JPanel1Layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(JPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(registipdoc, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(regisnom, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cbxNacionalidad, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(JPanel1Layout.createSequentialGroup()
                                .addGroup(JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(regisprofe, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(50, 50, 50)
                        .addGroup(JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(JPanel1Layout.createSequentialGroup()
                                .addGroup(JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(JPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jlbVistoMalo, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jlbVistoBueno, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(1, 1, 1))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JPanel1Layout.createSequentialGroup()
                                        .addGroup(JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING))
                                        .addGap(9, 9, 9)))
                                .addGroup(JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jlbMensajeError)
                                    .addGroup(JPanel1Layout.createSequentialGroup()
                                        .addComponent(txtIndicativo, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(registel, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnVerificarWa))
                                    .addComponent(regisape, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(regisnumdoc, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jlbMensajeExitoso)))
                            .addGroup(JPanel1Layout.createSequentialGroup()
                                .addComponent(jlbNacimiento)
                                .addGap(4, 4, 4)
                                .addComponent(jdchNacimiento, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(153, Short.MAX_VALUE))
            .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JPanel1Layout.createSequentialGroup()
                    .addGap(0, 978, Short.MAX_VALUE)
                    .addComponent(jlbFondo, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 1002, Short.MAX_VALUE))
        );
        JPanel1Layout.setVerticalGroup(
            JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JPanel1Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(JPanel1Layout.createSequentialGroup()
                        .addGap(110, 110, 110)
                        .addGroup(JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jlbMensajeExitoso, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlbMensajeError, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(JPanel1Layout.createSequentialGroup()
                        .addGap(80, 80, 80)
                        .addGroup(JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbxNacionalidad, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(registipdoc, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(JPanel1Layout.createSequentialGroup()
                        .addGroup(JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(JPanel1Layout.createSequentialGroup()
                                .addGap(40, 40, 40)
                                .addGroup(JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(regisape, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(regisnumdoc, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(JPanel1Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(regisnom, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(JPanel1Layout.createSequentialGroup()
                        .addGap(79, 79, 79)
                        .addGroup(JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtIndicativo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(registel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnVerificarWa, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jlbVistoBueno, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jlbVistoMalo, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGroup(JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(regisprofe, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jlbNacimiento, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jdchNacimiento, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxPaisProce, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlbDeparProce, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxDeparProce, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlbCiudadProce, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxCiudadProce, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jpnHuespedDest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jpnDatosFact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(124, Short.MAX_VALUE))
            .addGroup(JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JPanel1Layout.createSequentialGroup()
                    .addContainerGap(91, Short.MAX_VALUE)
                    .addComponent(jlbFondo, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(841, 841, 841)))
            .addGroup(JPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(JPanel1Layout.createSequentialGroup()
                    .addGap(80, 80, 80)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(884, Short.MAX_VALUE)))
        );

        jScrollPane1.setViewportView(JPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 988, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 858, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void regisnomKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_regisnomKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            regisape.requestFocus();
        }
    }//GEN-LAST:event_regisnomKeyPressed

    private void regisnom(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_regisnom
        char c = evt.getKeyChar();
        if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && (c < ' ' || c > ' ')) {
            evt.consume();
        }
        String str = regisnom.getText();

        StringBuffer strbf = new StringBuffer();
        Matcher match = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(str);
        while (match.find()) {
            match.appendReplacement(strbf, match.group(1).toUpperCase() + match.group(2).toLowerCase());
        }
        regisnom.setText(match.appendTail(strbf).toString());
    }//GEN-LAST:event_regisnom

    private void regisnumdocFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_regisnumdocFocusLost
        procesoDatosCliViejo();
    }//GEN-LAST:event_regisnumdocFocusLost

    private void regisnumdocKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_regisnumdocKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            registipdoc.requestFocus();
        }
    }//GEN-LAST:event_regisnumdocKeyPressed

    private void registipdocKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_registipdocKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            regisnom.requestFocus();
        }
    }//GEN-LAST:event_registipdocKeyPressed

    private void regisapeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_regisapeKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            cbxNacionalidad.requestFocus();
        }
    }//GEN-LAST:event_regisapeKeyPressed

    private void regisape(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_regisape
        char c = evt.getKeyChar();
        if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && (c < ' ' || c > ' ')) {
            evt.consume();
        }
        String str = regisape.getText();

        StringBuffer strbf = new StringBuffer();
        Matcher match = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(str);
        while (match.find()) {
            match.appendReplacement(strbf, match.group(1).toUpperCase() + match.group(2).toLowerCase());
        }
        regisape.setText(match.appendTail(strbf).toString());
    }//GEN-LAST:event_regisape

    private void registelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_registelKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            regisprofe.requestFocus();
        }
    }//GEN-LAST:event_registelKeyPressed

    private void registelKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_registelKeyReleased
        jlbVistoBueno.setVisible(false);
        jlbVistoMalo.setVisible(false);
        String telefono = registel.getText();
        int tel = telefono.length();
        if (cbxNacionalidad.getSelectedItem().toString().equals("COLOMBIA (+57)")) {
            if (tel < 10) {
                jlbVistoMalo.setVisible(true);
            } else if (tel == 10) {
                jlbVistoBueno.setVisible(true);
                btnVerificarWa.setVisible(true);
            } else if (tel > 10) {
                jlbVistoMalo.setVisible(true);
            }
        } else if (cbxNacionalidad.getSelectedItem().toString().equals("ECUADOR (+593)")) {
            if (tel < 9) {
                jlbVistoMalo.setVisible(true);
            } else if (tel == 9) {
                jlbVistoBueno.setVisible(true);
                btnVerificarWa.setVisible(true);
            } else if (tel > 9) {
                jlbVistoMalo.setVisible(true);
            }
        }
    }//GEN-LAST:event_registelKeyReleased

    private void registel(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_registel
        char c = evt.getKeyChar();
        if ((c < '0' || c > '9')) {
            evt.consume();
        }
    }//GEN-LAST:event_registel

    private void regisprofeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_regisprofeKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            cbxPaisProce.requestFocus();
        }
    }//GEN-LAST:event_regisprofeKeyPressed

    private void regisprofeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_regisprofeKeyTyped
        char c = evt.getKeyChar();
        if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && (c < ' ' || c > ' ')) {
            evt.consume();
        }
        String str = regisprofe.getText();

        StringBuffer strbf = new StringBuffer();
        Matcher match = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(str);
        while (match.find()) {
            match.appendReplacement(strbf, match.group(1).toUpperCase() + match.group(2).toLowerCase());
        }
        regisprofe.setText(match.appendTail(strbf).toString());
    }//GEN-LAST:event_regisprofeKeyTyped

    private void cbxNacionalidadItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxNacionalidadItemStateChanged
        String selectedItem = (String) cbxNacionalidad.getSelectedItem();

        if (selectedItem != null) {
            Matcher matcher = Pattern.compile("\\+?(\\d+)").matcher(selectedItem);
            if (matcher.find()) {
                String indicativo = "+" + matcher.group(1);
                txtIndicativo.setText(indicativo);
            } else {
                txtIndicativo.setText("");
            }
            String[] cadena = selectedItem.split("\\(");
            String paisSP = cadena[0].trim();
            cbxPaisProce.setSelectedItem(paisSP);
            cbxPaisDest.setSelectedItem(paisSP);
            nacionalidadSire = paisSP;
        }
    }//GEN-LAST:event_cbxNacionalidadItemStateChanged

    private void cbxNacionalidadKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbxNacionalidadKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            registel.requestFocus();
        }
    }//GEN-LAST:event_cbxNacionalidadKeyPressed

    private void btnVerificarWaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerificarWaActionPerformed
        //botonWaClickeado = true;
        if (worker != null && !worker.isDone()) {
            JOptionPane.showMessageDialog(this, "Por favor, espera a que el proceso anterior termine.");
            return;
        }
        if (timerVerificarWa != null) {
            timerVerificarWa.stop();
            timerVerificarWa = null;
        }
        jlbMensajeError.setVisible(false);
        jlbMensajeExitoso.setVisible(false);
        if (regisnom.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El campo 'Nombre' debe estar lleno");
            regisnom.requestFocus();
            return;
        }
        if (regisape.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El campo 'Apellido' debe estar lleno");
            regisape.requestFocus();
            return;
        }
        if (registel.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El campo 'Telefono' debe estar lleno");
            registel.requestFocus();
            return;
        }

        btnVerificarWa.setText("Enviando...");
        registel.setFocusable(false);
        regisprofe.requestFocus();

        String nombreCliente = "*" + regisnom.getText().split(" ")[0] + "*";
        String telefonoWA = txtIndicativo.getText() + registel.getText();

        construirMensajeWatsaap(nombreCliente);

        MensajesWATest2 enviar = new MensajesWATest2();

        worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    return enviar.enviarMensaje(telefonoWA, MensajeWhatsApp);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error al codificar el mensaje: " + ex.getMessage());
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    boolean mensajeEnviado = get();
                    if (mensajeEnviado) {
                        jlbMensajeExitoso.setVisible(true);
                        btnVerificarWa.setVisible(false);
                        registel.setFocusable(false);
                        estadoVerificacion = "Verificado";
                        elMensajeYaFueEnviado = true;
                    } else {
                        registel.setFocusable(true);
                        jlbMensajeError.setVisible(true);
                        agitarBoton();
                        btnVerificarWa.setText("ENVIAR WA");
                    }
                } catch (ExecutionException | InterruptedException e) {
                    registel.setFocusable(true);
                    jlbMensajeError.setVisible(true);
                    agitarBoton();
                    btnVerificarWa.setText("ENVIAR WA");
                }
            }
        };

        worker.execute();
    }//GEN-LAST:event_btnVerificarWaActionPerformed

    private void cbxDeparProceItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxDeparProceItemStateChanged
        cbxdeparProceCambio();
    }//GEN-LAST:event_cbxDeparProceItemStateChanged

    private void cbxDeparProceKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbxDeparProceKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            cbxCiudadProce.requestFocus();
        }
    }//GEN-LAST:event_cbxDeparProceKeyPressed

    private void cbxCiudadProceItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxCiudadProceItemStateChanged
        visualizarJpnDest(cbxPaisProce, cbxDeparProce, cbxCiudadProce, jpnHuespedDest);
    }//GEN-LAST:event_cbxCiudadProceItemStateChanged

    private void cbxCiudadProceKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbxCiudadProceKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!cbxCiudadProce.getSelectedItem().equals("Seleccionar")) {
                cbxPaisDest.requestFocus();
            }
        }
    }//GEN-LAST:event_cbxCiudadProceKeyPressed

    private void cbxPaisProceItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxPaisProceItemStateChanged
        if (cbxPaisProce.getSelectedItem().equals("COLOMBIA")) {
            jlbDeparProce.setVisible(true);
            jlbCiudadProce.setVisible(true);
            cbxDeparProce.setVisible(true);
            cbxCiudadProce.setVisible(true);
            con.traerDepartementos(cbxDeparProce, cbxDeparDest);
        } else {
            jlbDeparProce.setVisible(false);
            jlbCiudadProce.setVisible(false);
            cbxDeparProce.setVisible(false);
            cbxCiudadProce.setVisible(false);
        }
        visualizarJpnDest(cbxPaisProce, cbxDeparProce, cbxCiudadProce, jpnHuespedDest);
    }//GEN-LAST:event_cbxPaisProceItemStateChanged

    private void cbxPaisProceKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbxPaisProceKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!cbxPaisProce.getSelectedItem().equals("Seleccionar")) {
                if (cbxPaisProce.getSelectedItem().equals("COLOMBIA")) {
                    cbxDeparProce.requestFocus();
                } else {
                    cbxPaisDest.requestFocus();
                }
            }
        }
    }//GEN-LAST:event_cbxPaisProceKeyPressed

    private void cbxPaisDestItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxPaisDestItemStateChanged
        if (cbxPaisDest.getSelectedItem().equals("COLOMBIA")) {
            jlbDeparDest.setVisible(true);
            cbxDeparDest.setVisible(true);
            con.traerDepartementos(null, cbxDeparDest);

        } else {
            jlbDeparDest.setVisible(false);
            jlbCiudadDest.setVisible(false);
            cbxDeparDest.setVisible(false);
            cbxCiudadDest.setVisible(false);
        }
        visualizarJpnDest(cbxPaisDest, cbxDeparDest, cbxCiudadDest, jpnDatosFact);
    }//GEN-LAST:event_cbxPaisDestItemStateChanged

    private void cbxPaisDestKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbxPaisDestKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!cbxPaisDest.getSelectedItem().equals("Seleccionar")) {
                if (cbxPaisDest.getSelectedItem().equals("COLOMBIA")) {
                    cbxDeparDest.requestFocus();
                } else {
                    regisvalorhabi.requestFocus();
                }
            }
        }
    }//GEN-LAST:event_cbxPaisDestKeyPressed

    private void cbxDeparDestItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxDeparDestItemStateChanged
        if (cbxDeparDest.getSelectedItem() != null && !cbxDeparDest.getSelectedItem().equals("Seleccionar")) {
            String departamento = cbxDeparDest.getSelectedItem().toString();
            int idC = con.obtenerIdDepartamento(departamento);
            con.traerCiudadesPorDepartamento(cbxCiudadDest, idC);
            jlbCiudadDest.setVisible(true);
            cbxCiudadDest.setVisible(true);
        } else {
            cbxCiudadDest.removeAllItems();
            jlbCiudadDest.setVisible(false);
            cbxCiudadDest.setVisible(false);
        }
    }//GEN-LAST:event_cbxDeparDestItemStateChanged

    private void cbxDeparDestKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbxDeparDestKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!cbxDeparDest.getSelectedItem().equals("Seleccionar")) {
                cbxCiudadDest.requestFocus();
            }
        }
    }//GEN-LAST:event_cbxDeparDestKeyPressed

    private void cbxCiudadDestItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxCiudadDestItemStateChanged
        visualizarJpnDest(cbxPaisDest, cbxDeparDest, cbxCiudadDest, jpnDatosFact);
    }//GEN-LAST:event_cbxCiudadDestItemStateChanged

    private void cbxCiudadDestKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbxCiudadDestKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!cbxCiudadDest.getSelectedItem().equals("Seleccionar")) {
                regisvalorhabi.requestFocus();
            }
        }
    }//GEN-LAST:event_cbxCiudadDestKeyPressed

    private void regiscomiFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_regiscomiFocusLost
        String totalpa = "";
        String comision = "";
        double num1, num2, res;

        if (!registotalpa.getText().isEmpty()) {
            totalpa = registotalpa.getText().replaceAll("[^\\d]", "");
            if (!regiscomi.getText().isEmpty()) {
                comision = regiscomi.getText().replaceAll("[^\\d]", "");
                num1 = Double.parseDouble(totalpa);
                num2 = Double.parseDouble(comision);
                res = num1 - num2;
                regisvalorneto.setText(formatearConPuntosDeMil(res));
                regisvalorneto.setBackground(Color.YELLOW);
                if (num2 >= 1000) {
                    hayComision = true;
                } else {
                    hayComision = false;
                }
            } else {
                regisvalorneto.setText(registotalpa.getText());
                regisvalorneto.setBackground(Color.YELLOW);
                regiscomi.setText("0");
            }
        }
    }//GEN-LAST:event_regiscomiFocusLost

    private void regiscomiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_regiscomiMouseClicked
        regiscomi.selectAll();
    }//GEN-LAST:event_regiscomiMouseClicked

    private void regiscomiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_regiscomiKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (cbxVendedor.isVisible()) {
                cbxVendedor.requestFocus();
            } else {
                regisgenre.requestFocus();
            }
        }
    }//GEN-LAST:event_regiscomiKeyPressed

    private void regiscomiKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_regiscomiKeyReleased
        String totalpa = "";
        String comision = "";
        double num1, num2, res;

        if (!registotalpa.getText().isEmpty()) {
            totalpa = registotalpa.getText().replaceAll("[^\\d]", "");
            if (!regiscomi.getText().isEmpty()) {
                comision = regiscomi.getText().replaceAll("[^\\d]", "");
                num1 = Double.parseDouble(totalpa);
                num2 = Double.parseDouble(comision);
                res = num1 - num2;
                regisvalorneto.setText(formatearConPuntosDeMil(res));
                regisvalorneto.setBackground(Color.YELLOW);

                double comi = Double.parseDouble(comision);
                if (comi >= 1000) {
                    int idTipo = 3;//usuario tipo vendedor
                    cbxVendedor.setVisible(true);
                    con.traerVendedores(cbxVendedor, idTipo);
                } else {
                    cbxVendedor.setVisible(false);
                }
            } else {
                regisvalorneto.setText(registotalpa.getText());
                regisvalorneto.setBackground(Color.YELLOW);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Ingrese el valor de la habitacion");
            regisvalorhabi.requestFocus();
        }

    }//GEN-LAST:event_regiscomiKeyReleased

    private void regiscomiKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_regiscomiKeyTyped
        char c = evt.getKeyChar();
        if ((c < '0' || c > '9')) {
            evt.consume();
        }
    }//GEN-LAST:event_regiscomiKeyTyped

    private void regisvalorhabiFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_regisvalorhabiFocusLost
        if (regisvalorhabi.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el valor de la habitacion");
            regisvalorhabi.requestFocus();
            return;
        }
        String valorPorNocheStr = regisvalorhabi.getText().replaceAll("[^\\d]", "");
        double num2, res;
        int num1;
        if (!regiscantno.getText().isEmpty() && !regisvalorhabi.getText().isEmpty()) {
            num1 = Integer.parseInt(regiscantno.getText());
            num2 = Double.parseDouble(valorPorNocheStr);
            res = num1 * num2;
            registotalpa.setText(formatearConPuntosDeMil(res));
        }
    }//GEN-LAST:event_regisvalorhabiFocusLost

    private void regisvalorhabiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_regisvalorhabiMouseClicked
        regisvalorhabi.selectAll();
    }//GEN-LAST:event_regisvalorhabiMouseClicked

    private void regisvalorhabiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_regisvalorhabiKeyPressed
        if (regisvalorhabi.getText().isEmpty() && evt.getKeyCode() == KeyEvent.VK_ENTER) {
            JOptionPane.showMessageDialog(null, "Este campo es obligatorio");
            regisvalorhabi.requestFocus();
        }
        if (evt.getKeyCode() == KeyEvent.VK_ENTER && !regisvalorhabi.getText().isEmpty()) {
            cbxTipoPago.requestFocus();
        }
    }//GEN-LAST:event_regisvalorhabiKeyPressed

    private void regisvalorhabiKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_regisvalorhabiKeyReleased
        String valorPorNocheStr = regisvalorhabi.getText().replaceAll("[^\\d]", "");
        double num2, res;
        int num1;
        if (!regiscantno.getText().isEmpty() && !regisvalorhabi.getText().isEmpty()) {
            num1 = Integer.parseInt(regiscantno.getText());
            num2 = Double.parseDouble(valorPorNocheStr);
            res = num1 * num2;
            registotalpa.setText(formatearConPuntosDeMil(res));
        }
    }//GEN-LAST:event_regisvalorhabiKeyReleased

    private void regisvalorhabiKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_regisvalorhabiKeyTyped
        char c = evt.getKeyChar();
        if ((c < '0' || c > '9')) {
            evt.consume();
        }
    }//GEN-LAST:event_regisvalorhabiKeyTyped

    private void cbxTipoPagoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbxTipoPagoKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!cbxTipoPago.getSelectedItem().equals("Seleccionar")) {
                regiscomi.requestFocus();
                if (!regiscomi.getText().isEmpty()) {
                    regiscomi.selectAll();
                }
            }
        }
    }//GEN-LAST:event_cbxTipoPagoKeyPressed

    private void regiscantnoVetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_regiscantnoVetoableChange
        String valorPorNocheStr = regisvalorhabi.getText().replaceAll("[^\\d]", "");
        double num2, res;
        int num1;
        if (!regiscantno.getText().isEmpty() && !regisvalorhabi.getText().isEmpty()) {
            num1 = Integer.parseInt(regiscantno.getText());
            num2 = Double.parseDouble(valorPorNocheStr);
            res = num1 * num2;
            registotalpa.setText(formatearConPuntosDeMil(res));
        }
    }//GEN-LAST:event_regiscantnoVetoableChange

    private void regisvalornetoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_regisvalornetoKeyTyped
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            regisgenre.requestFocus();
        }
    }//GEN-LAST:event_regisvalornetoKeyTyped

    private void regisgenreFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_regisgenreFocusGained
        double num2, res;
        int num1;

        if (!regiscantno.getText().isEmpty() && !regisvalorhabi.getText().isEmpty()) {
            String valorPorNocheStr = regisvalorhabi.getText().replaceAll("[^\\d]", "");
            num1 = Integer.parseInt(regiscantno.getText());
            num2 = Double.parseDouble(valorPorNocheStr);
            res = num1 * num2;
            registotalpa.setText(formatearConPuntosDeMil(res));
        }

        double num11, num12, resu;

        if (!registotalpa.getText().isEmpty() && !regiscomi.getText().isEmpty()) {
            String totalpa = registotalpa.getText().replaceAll("[^\\d]", "");
            String comision = regiscomi.getText().replaceAll("[^\\d]", "");
            num11 = Double.parseDouble(totalpa);
            num12 = Double.parseDouble(comision);
            resu = num11 - num12;
            regisvalorneto.setText(formatearConPuntosDeMil(resu));
            regisvalorneto.setBackground(Color.YELLOW);
        } else if (regiscomi.getText().equals("0") || regiscomi.getText().isEmpty()) {
            regisvalorneto.setText(registotalpa.getText());
            regiscomi.setText("0");
        }
        if (regiscomi.getText().equals("0") && cbxVendedor.isVisible()) {
            cbxVendedor.setVisible(false);
        }
    }//GEN-LAST:event_regisgenreFocusGained

    private void regisgenreMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_regisgenreMouseClicked
        procesoRegistro();
    }//GEN-LAST:event_regisgenreMouseClicked

    private void regisgenreMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_regisgenreMouseEntered
        regisgenre.setBackground(new Color(0, 102, 102));
    }//GEN-LAST:event_regisgenreMouseEntered

    private void regisgenreMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_regisgenreMouseExited
        regisgenre.setBackground(new Color(0, 51, 51));
    }//GEN-LAST:event_regisgenreMouseExited

    private void regisgenreKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_regisgenreKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            procesoRegistro();
        }
    }//GEN-LAST:event_regisgenreKeyPressed

    private void regisvolmenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_regisvolmenMouseClicked
        saliendo = true;
        this.dispose();
    }//GEN-LAST:event_regisvolmenMouseClicked

    private void regisvolmenMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_regisvolmenMouseEntered
        regisvolmen.setBackground(new Color(0, 102, 102));
        saliendo = true;
    }//GEN-LAST:event_regisvolmenMouseEntered

    private void regisvolmenMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_regisvolmenMouseExited
        regisvolmen.setBackground(new Color(0, 51, 51));
        saliendo = false;
    }//GEN-LAST:event_regisvolmenMouseExited

    private void cbxVendedorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbxVendedorKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!cbxVendedor.getSelectedItem().toString().equals("Otro") && !cbxVendedor.getSelectedItem().toString().equals("Seleccione Vendedor")) {
                regisgenre.requestFocus();
            } else if (cbxVendedor.getSelectedItem().toString().equals("Seleccione Vendedor")) {
                cbxVendedor.requestFocus();
            }
        }
    }//GEN-LAST:event_cbxVendedorKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel JPanel1;
    private javax.swing.JButton btnVerificarWa;
    public javax.swing.JComboBox<String> cbxCiudadDest;
    public javax.swing.JComboBox<String> cbxCiudadProce;
    public javax.swing.JComboBox<String> cbxDeparDest;
    public javax.swing.JComboBox<String> cbxDeparProce;
    private javax.swing.JComboBox<String> cbxNacionalidad;
    public javax.swing.JComboBox<String> cbxPaisDest;
    private javax.swing.JComboBox<String> cbxPaisProce;
    public javax.swing.JComboBox<String> cbxTipoPago;
    private javax.swing.JComboBox<String> cbxVendedor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
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
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    public com.toedter.calendar.JDateChooser jdchFeSa;
    public com.toedter.calendar.JDateChooser jdchNacimiento;
    private javax.swing.JLabel jlbCiudadDest;
    private javax.swing.JLabel jlbCiudadProce;
    private javax.swing.JLabel jlbDeparDest;
    private javax.swing.JLabel jlbDeparProce;
    private javax.swing.JLabel jlbFondo;
    private javax.swing.JLabel jlbMensajeError;
    private javax.swing.JLabel jlbMensajeExitoso;
    private javax.swing.JLabel jlbNacimiento;
    private javax.swing.JLabel jlbVistoBueno;
    private javax.swing.JLabel jlbVistoMalo;
    private javax.swing.JPanel jpnDatosFact;
    private javax.swing.JPanel jpnHuespedDest;
    public javax.swing.JLabel lblRollregistro;
    public javax.swing.JLabel lblRollregistro1;
    public javax.swing.JTextField nombreUsuario;
    private javax.swing.JTextField regisape;
    public javax.swing.JTextField regiscantno;
    public javax.swing.JTextField regiscomi;
    public javax.swing.JTextField regisfe;
    private javax.swing.JButton regisgenre;
    public javax.swing.JTextField regishabi;
    private javax.swing.JTextField regisllega;
    private javax.swing.JTextField regisnom;
    public javax.swing.JTextField regisnumdoc;
    private javax.swing.JTextField regisprofe;
    private javax.swing.JTextField registel;
    public javax.swing.JComboBox<String> registipdoc;
    private javax.swing.JTextField registotalpa;
    public javax.swing.JTextField regisvalorhabi;
    private javax.swing.JTextField regisvalorneto;
    private javax.swing.JButton regisvolmen;
    private javax.swing.JTextField txtIndicativo;
    public javax.swing.JTextField txtUsuario;
    // End of variables declaration//GEN-END:variables

}
