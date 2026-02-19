package vistas;

import com.toedter.calendar.JTextFieldDateEditor;
import conectar.Canectar;
import conectar.Consultasbd;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import modelo.DatosSire;
import modelo.Habitaciones;
import modelo.Hotel;
import modelo.ReporteSire;
import modelo.UsuarioOperando;

public final class Recepciones extends javax.swing.JFrame {

    public static final Color COLOR_ROJO = new Color(255, 53, 32);
    public static final Color COLOR_NARANJA = new Color(255, 179, 39);
    public static final Color COLOR_VERDE = new Color(0, 153, 0);
    
    String hora, minutos, seg;
    private final Recepciones.RelojSwingWorker worker;
    int idHotel;
    int filas;
    int columnas;
    int largoBoton;
    int anchoBoton;
    String consulta = "SELECT Num_Habitacion,Estado_Habitacion FROM habitaciones WHERE Fk_Id_Hotel = ?";
    boolean is_queryChange = false;
    public JButton[][] botonesHabs = new JButton[filas][columnas];
    Canectar con = new Canectar();
    DatosSire datosReporte = new DatosSire();

    ClassLoader classLoader = getClass().getClassLoader();
    ImageIcon icon = new ImageIcon(classLoader.getResource("img/pequeño.png"));

    Consultasbd bd = new Consultasbd();
    public boolean turnoCreado = false;
    UsuarioOperando usus = new UsuarioOperando();
    Hotel hotel;
    List<Habitaciones> habitaciones;

    public Recepciones(UsuarioOperando usus, Hotel hotel) {
        this.usus = usus;
        this.hotel = hotel;
        this.idHotel = hotel.getIdHoteles();
        

        System.out.println("usuario que opera en recepcion: " + usus);
        System.out.println("hotel que abre operaciones: " + hotel);

        System.out.println("tipo de usuario opreando el turno: " + usus.getId_tipo());
        System.out.println("roll de usuario operando el turno: " + usus.getRoll_usuarios());

        initComponents();

        Point coordenadasJpnVerificar = jpnVerificar.getLocation();

        if (usus.getId_tipo() != 1) {
            btnRegistrarUsuario.setVisible(false);
            btnVentasComisiones.setVisible(false);
            btnInformeContable.setVisible(false);
            jpnVerificar.setLocation(coordenadasJpnVerificar);

        }

        // Suponiendo que tu JDateChooser se llama jdchFechaHabs
        JTextFieldDateEditor editor = (JTextFieldDateEditor) jdchFechaHabs.getDateEditor();
        editor.setEditable(false);

        jlbNombreHotel.setText(hotel.getNombreHotel());
        jlbSogan.setText(hotel.getSloganHotel());
        habitaciones = con.traerHabsHotel(idHotel);
        jdchFechaHabs.setDate(new Date());
        generarBotonesHabitaciones(anchoBoton, largoBoton);

        jpnHabitaciones.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                generarBotonesHabitaciones(anchoBoton, largoBoton);

            }
        });
        btnIngresoExtra.addMouseListener(mouseAdapter);
        btnGastos.addMouseListener(mouseAdapter);
        btnInformeContable.addMouseListener(mouseAdapter);
        btnOperarTurno.addMouseListener(mouseAdapter);
        btnRegistrarUsuario.addMouseListener(mouseAdapter);
        btnSalir.addMouseListener(mouseAdapter);
        btnVentasComisiones.addMouseListener(mouseAdapter);
        labelsJpnVerificarInvisibles();
        txtIdTurno.setText(String.valueOf(usus.getTurnoPresente()));
        setIconImage(new ImageIcon(getClass().getResource("/img/LogoIcono.png")).getImage());

        if (turnoCreado || usus.getTurnoNCreado()) {
            try {
                PreparedStatement ps = null;
                // Obtener la fecha actual
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String fechaActual = dateFormat.format(new Date());

                // Consultar la tabla reservas para obtener las reservas que comienzan en la fecha actual
                String query = "SELECT Num_Habitacion FROM reserva WHERE Fecha_Llegada = ? AND Estado_reserva = 'Vigente' AND Fk_Id_Hotel = ?";
                ps = con.conexion().prepareStatement(query);
                ps.setString(1, fechaActual);
                ps.setInt(2, idHotel);

                ResultSet rs = ps.executeQuery();

                // Para cada reserva encontrada, actualizar el estado de la habitación correspondiente
                while (rs.next()) {
                    int numHabitacion = rs.getInt("Num_Habitacion");

                    // Actualizar el estado de la habitación
                    String updateQuery = "UPDATE habitaciones SET Estado_Habitacion = 'Reservado' WHERE Num_Habitacion = ? AND Fk_Id_Hotel = ?";
                    PreparedStatement psUpdate = con.conexion().prepareStatement(updateQuery);
                    psUpdate.setString(1, String.valueOf(numHabitacion)); // Aquí convertimos el entero a cadena
                    psUpdate.setInt(2, idHotel);
                    psUpdate.executeUpdate();
                }
                activarHabitaciones(jdchFechaHabs.getDate(), idHotel);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "No se puede actualizar las habitaciones reservadas " + e.getMessage());
            }
        }
        System.out.println("vamos a entrar al switch de recepcion");
        switch (this.usus.getRoll_usuarios().trim()) {

            case "ADMINISTRADOR":
                txtNombreUsuario.setText(this.usus.getNombres().split(" ")[0] + " " + this.usus.getApellidos().split(" ")[0]);
                lblRoll.setText(this.usus.getRoll_usuarios() + " :");
                jlbUsuario.setText(this.usus.getUsuario());
                btnRegistrarUsuario.setVisible(true);
                btnVentasComisiones.setVisible(true);
                btnInformeContable.setVisible(true);
                break;
            case "RECEPCIONISTA":
                txtNombreUsuario.setText(this.usus.getNombres().split(" ")[0] + " " + this.usus.getApellidos().split(" ")[0]);
                lblRoll.setText(this.usus.getRoll_usuarios() + " :");
                jlbUsuario.setText(this.usus.getUsuario());
                break;
            case "VENDEDOR":
                txtNombreUsuario.setText(this.usus.getNombres().split(" ")[0] + " " + this.usus.getApellidos().split(" ")[0]);
                lblRoll.setText(this.usus.getRoll_usuarios() + " :");
                jlbUsuario.setText(this.usus.getUsuario());
                break;
        }
        this.setLocationRelativeTo(null);
        worker = new Recepciones.RelojSwingWorker();
        worker.execute();
    }

    

    

    public void cerrarVentana() {
        inicio ir = new inicio();
        ir.setVisible(true);
        this.dispose();
    }

    private void generarBotonesHabitaciones(int anchoBoton, int largoBoton) {
        int totalHabitaciones = habitaciones.size();
        int columnasMax = 0; // Número máximo de columnas
        if (totalHabitaciones > 0 && totalHabitaciones <= 20) {
            columnasMax = 5;
        } else if (totalHabitaciones > 20 && totalHabitaciones <= 24) {
            columnasMax = 6;
        } else if (totalHabitaciones > 24 && totalHabitaciones <= 28) {
            columnasMax = 7;
        } else if (totalHabitaciones > 28 && totalHabitaciones <= 40) {
            columnasMax = 8;
        } else if (totalHabitaciones > 40 && totalHabitaciones <= 54) {
            columnasMax = 9;
        } else if (totalHabitaciones > 54 && totalHabitaciones <= 80) {
            columnasMax = 10;
        } else if (totalHabitaciones > 80 && totalHabitaciones <= 110) {
            columnasMax = 11;
        } else if (totalHabitaciones < 0 || totalHabitaciones > 110) {
            JOptionPane.showMessageDialog(this, "La cantidad de habitaciones, excede el limite permitido!");
            return;
        }
        this.filas = (int) Math.ceil((double) totalHabitaciones / columnasMax); // Calculamos las filas necesarias
        this.columnas = Math.min(totalHabitaciones, columnasMax); // Número de columnas

        botonesHabs = new JButton[this.filas][this.columnas];

        // Calculamos el tamaño de los botones basándose en el tamaño del panel y el número de botones
        anchoBoton = jpnHabitaciones.getWidth() / this.columnas;
        largoBoton = jpnHabitaciones.getHeight() / this.filas;
        jpnHabitaciones.removeAll();
        for (int i = 0; i < this.filas; i++) {
            for (int j = 0; j < this.columnas; j++) {
                int numHabitacion = i * this.columnas + j;

                if (numHabitacion < habitaciones.size()) { // Si todavía hay habitaciones por crear
                    Habitaciones hab = habitaciones.get(numHabitacion);
                    botonesHabs[i][j] = diseñarBoton(hab, anchoBoton, largoBoton);
                    botonesHabs[i][j].setBounds(j * anchoBoton, i * largoBoton, anchoBoton, largoBoton);
                    agregarFuncionalidadBoton(botonesHabs[i][j]);
                    agregarEventoClick(botonesHabs[i][j]);
                    jpnHabitaciones.add(botonesHabs[i][j]);
                }
            }
        }
        jpnHabitaciones.revalidate();
        jpnHabitaciones.repaint();
        activarHabitaciones(jdchFechaHabs.getDate(), idHotel);
    }

    private JButton getBotonHabitacion(String numeroHabitacion) {
        int numeroHabitacionInt = Integer.parseInt(numeroHabitacion);
        for (int i = 0; i < this.filas; i++) {
            for (int j = 0; j < this.columnas; j++) {
                JButton boton = botonesHabs[i][j];
                if (getNumeroHabitacion(boton) == numeroHabitacionInt) {
                    return boton;
                }
            }
        }
        return null;
    }

    private int getNumeroHabitacion(JButton boton) {
        try {
            return Integer.parseInt(boton.getText());
        } catch (NumberFormatException e) {
            System.out.println("Error al obtener el número de habitación: " + e);
            return -1; // Devuelve un valor que indique que algo salió mal
        }
    }

    public void activarHabitaciones(Date fechaSeleccionada, int idHotel) {
        java.sql.Connection conex = con.conexion();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conex.prepareStatement(consulta);
            java.sql.Date fecha = new java.sql.Date(fechaSeleccionada.getTime());
            if (is_queryChange) {
                ps.setDate(1, fecha);
                ps.setDate(2, fecha);
                ps.setDate(3, fecha);
                ps.setDate(4, fecha);
                ps.setInt(5, idHotel);
            } else {
                ps.setInt(1, idHotel);
            }
            rs = ps.executeQuery();

            while (rs.next()) {
                String res = rs.getString("Estado_Habitacion");
                String numeroHabitacion = rs.getString("Num_Habitacion");
                JButton hab = getBotonHabitacion(numeroHabitacion);

                if (hab != null) {
                    // Set title for the border based on the status
                    CompoundBorder compoundBorder = (CompoundBorder) hab.getBorder();
                    TitledBorder topBorder = (TitledBorder) compoundBorder.getOutsideBorder();
                    TitledBorder bottomBorder = (TitledBorder) compoundBorder.getInsideBorder();

                    topBorder.setTitle(res);
                    topBorder.setTitleColor(new Color(0, 51, 51));
                    bottomBorder.setTitleColor(new Color(0, 51, 51));
                    hab.repaint();

                    // Set the background color based on the status
                    if (res.equals("Libre")) {
                        hab.setBackground(COLOR_VERDE);
                    } else if (res.equals("Ocupado")) {
                        hab.setBackground(COLOR_ROJO);
                    } else if (res.equals("Reservado")) {
                        hab.setBackground(COLOR_NARANJA);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e);
        } finally {
            try {
                conex.close();
            } catch (SQLException e) {
                System.out.println("Error: " + e);
            }
        }
    }

    private JButton diseñarBoton(Habitaciones hab, int anchoBoton, int largoBoton) {
        JButton button = new JButton(hab.getNumHabitacion());
        // Calcular el tamaño de la fuente en función del tamaño del botón
        int fontSizeText = Math.min(anchoBoton, largoBoton) / 9;// hacer este valor mas pequeño para obtener una fuente mas grande
        int fontSizeBorder = Math.min(anchoBoton, largoBoton) / 10;
        int fontSizeBorderTop = Math.min(anchoBoton, largoBoton) / 15;

        // Configurar la fuente del botón
        Font buttonFont = new Font("Dialog", Font.BOLD, fontSizeText);
        button.setFont(buttonFont);

        // Agregar color verde a los botones
        Border emptyBorder = BorderFactory.createEmptyBorder();
        // Crear borde con título arriba
        TitledBorder borderArriba = BorderFactory.createTitledBorder(
                emptyBorder,
                hab.getEstadoHabitacion(), // Título
                TitledBorder.LEFT, // Justificación
                TitledBorder.ABOVE_TOP, // Posición
                new Font("Dialog", Font.BOLD, fontSizeBorderTop)); // Fuente

        // Crear borde con título abajo
        TitledBorder borderAbajo = BorderFactory.createTitledBorder(
                emptyBorder,
                hab.getTipoHabitacion().toUpperCase(), // Título
                TitledBorder.CENTER, // Justificación
                TitledBorder.ABOVE_BOTTOM, // Posición
                new Font("Dialog", Font.BOLD, fontSizeBorder)); // Fuente

        // Combinar los dos bordes en un borde compuesto
        CompoundBorder border = new CompoundBorder(borderArriba, borderAbajo);
        button.setBorder(border);
        return button;
    }

    private void agregarEventoClick(JButton button) {
        button.addActionListener((ActionEvent e) -> {
            int numHabitacion = getNumeroHabitacion(button);
            toggleHabitacion(numHabitacion, button);
        });
    }

    private void agregarFuncionalidadBoton(JButton button) {
        button.addMouseListener(new MouseAdapter() {
            Color oldForeground = button.getForeground();
            Border oldBorder = button.getBorder();
            Color oldTitleColor;

            @Override
            public void mouseEntered(MouseEvent e) {
                Date fechaSeleccionada = jdchFechaHabs.getDate();
                java.sql.Date fechaSql = new java.sql.Date(fechaSeleccionada.getTime());
                if (button.getBackground().equals(COLOR_ROJO)) { //si el color del boton es rojo
                    SimpleDateFormat formatoOrigen = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat formatoDestino = new SimpleDateFormat("EEE dd-MMM-yyyy");
                    int numeroHabitacion = Integer.parseInt(button.getText());
                    String[] datosRegistro = con.obtenerDatosRegistro(numeroHabitacion, idHotel);

                    datosReporte.setNombres(datosRegistro[0] != null ? datosRegistro[0] : "");
                    String apellidos = datosRegistro[1] != null ? datosRegistro[1] : "";
                    String[] partesApellidos = apellidos.split(" ");
                    
                    // Inicializar ambos apellidos como vacíos por defecto
                    datosReporte.setPrimerApeliido("");
                    datosReporte.setSegundoApellido("");
                    
                    if (!apellidos.equals("")) {
                        if (partesApellidos.length >= 1) {
                            datosReporte.setPrimerApeliido(partesApellidos[0]);
                        }
                        if (partesApellidos.length >= 2) {
                            datosReporte.setSegundoApellido(partesApellidos[1]);
                        }
                    }
                    datosReporte.setNumDocumento(datosRegistro[9] != null ? datosRegistro[9] : "");
                    datosReporte.setTipoDocumento(datosRegistro[10] != null ? datosRegistro[10] : "");
                    
                    // Convertir fecha de nacimiento de formato MySQL (yyyy-MM-dd) a formato SIRE (d/M/yyyy)
                    String fechaNacimientoOriginal = datosRegistro[11] != null ? datosRegistro[11] : "";
                    String fechaNacimientoFormateada = "";
                    if (!fechaNacimientoOriginal.isEmpty()) {
                        try {
                            SimpleDateFormat formatoMySQL = new SimpleDateFormat("yyyy-MM-dd");
                            SimpleDateFormat formatoSire = new SimpleDateFormat("d/M/yyyy");
                            Date fecha = formatoMySQL.parse(fechaNacimientoOriginal);
                            fechaNacimientoFormateada = formatoSire.format(fecha);
                        } catch (ParseException pex) {
                            System.err.println("Error al convertir fecha de nacimiento: " + pex.getMessage());
                            fechaNacimientoFormateada = fechaNacimientoOriginal; // Usar original si falla la conversión
                        }
                    }
                    datosReporte.setFechaNacimiento(fechaNacimientoFormateada);
                    
                    datosReporte.setNacionalidad(datosRegistro[12] != null ? datosRegistro[12] : "");
                    String procedencia = datosRegistro[13] != null ? datosRegistro[13] : "";
                    String[] partesProce = procedencia.split("-");
                    if (!"".equals(procedencia)) {
                        if (partesProce.length == 1) {
                            datosReporte.setPaisProce(partesProce[0]);
                        } else if (partesProce.length == 3) {
                            datosReporte.setPaisProce(partesProce[0]);
                            datosReporte.setDeparProce(partesProce[1]);
                            datosReporte.setCiudadProce(partesProce[2]);
                        }

                    }
                    String destino = datosRegistro[14] != null ? datosRegistro[14] : "";
                    String[] partesDest = destino.split("-");
                    if (!"".equals(destino)) {
                        if (partesDest.length == 1) {
                            datosReporte.setPaisDest(partesDest[0]);
                        } else if (partesDest.length == 3) {
                            datosReporte.setPaisDest(partesDest[0]);
                            datosReporte.setDeparDet(partesDest[1]);
                            datosReporte.setCiudadDest(partesDest[2]);
                        }
                    }

                    jblMosNom.setText(datosRegistro[0] != null ? datosRegistro[0] : "");
                    jblMosApe.setText(datosRegistro[1] != null ? datosRegistro[1] : "");
                    jblMosTel.setText(datosRegistro[2] != null ? datosRegistro[2] : "");
                    try {

                        if (datosRegistro[3] != null && datosRegistro[5] != null) {
                            Date fechaLlegada = formatoOrigen.parse(datosRegistro[3]);
                            Date fechasalida = formatoOrigen.parse(datosRegistro[5]);
                            String fechaLlegadaFormateada = formatoDestino.format(fechaLlegada);
                            String fechaSalidaForm = formatoDestino.format(fechasalida);
                            jblMosFeLle.setText(fechaLlegadaFormateada);
                            jblMosFeSa.setText(fechaSalidaForm);
                        } else {
                            jblMosFeLle.setText("");
                            jblMosFeSa.setText("");
                        }

                        Date fechaSalidaDate = null;
                        String fechaSalidaString = jblMosFeSa.getText(); // la fecha en String
                        SimpleDateFormat formatoOrigen1 = new SimpleDateFormat("EEE dd-MMM-yyyy", new Locale("es", "ES"));

                        try {
                            fechaSalidaDate = formatoOrigen1.parse(fechaSalidaString);
                        } catch (ParseException ex) {
                            formatoOrigen1 = new SimpleDateFormat("EEE dd-MMM.-yyyy", new Locale("es", "ES"));
                            try {
                                fechaSalidaDate = formatoOrigen1.parse(fechaSalidaString);
                            } catch (ParseException ex2) {
                                jlbVerificacion.setText("Error en el panel de Verificaciones");
                            }
                        }

                        SimpleDateFormat formatoDestino1 = new SimpleDateFormat("yyyy-MM-dd"); // formateador de fecha en el formato destino
                        Date fechaSeleccionada1 = jdchFechaHabs.getDate();
                        String fechaFormateada = fechaSeleccionada1 != null ? formatoDestino1.format(fechaSeleccionada1) : "";

                        // Verificación de null para fechaSalidaDate
                        String fechaSalidaFormateada = fechaSalidaDate != null ? formatoDestino1.format(fechaSalidaDate) : "";

                        String horaActual = txtHoraRec.getText().trim();
                        if (horaActual != null && !horaActual.isEmpty()) {
                            horaActual = horaActual.split(":")[0];
                            int horaActualT = Integer.parseInt(horaActual);
                            if (fechaSalidaFormateada.equals(fechaFormateada) && horaActualT > 13) {
                                CompoundBorder compoundBorder = (CompoundBorder) button.getBorder();
                                TitledBorder topBorder = (TitledBorder) compoundBorder.getOutsideBorder();
                                topBorder.setTitle("Hora de Salida !");
                                topBorder.setTitleColor(Color.WHITE);
                                button.repaint();
                            }
                        }
                    } catch (ParseException ex) {
                        jlbVerificacion.setText("Error en el panel de Verificaciones");
                    }

                    jblMosHoLle.setText(datosRegistro[4] != null ? datosRegistro[4] : "");
                    if (datosRegistro[6] != null && !datosRegistro[6].isEmpty()) {
                        double num = Double.parseDouble(datosRegistro[6]);
                        int intNum = (int) num;
                        // Formatear el entero para que use puntos como separador de miles
                        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
                        String formattedNum = formatter.format(intNum);
                        // Asignar el valor formateado al JLabel
                        jblValNo.setText(formattedNum);
                    } else {
                        jblValNo.setText("");
                    }
                    if (datosRegistro[7] != null && !datosRegistro[7].isEmpty()) {
                        double num = Double.parseDouble(datosRegistro[7]);
                        int intNum = (int) num;
                        NumberFormat formato = NumberFormat.getNumberInstance(Locale.US);
                        String numeroConPuntos = formato.format(intNum);
                        jblValTo.setText(numeroConPuntos);
                    } else {
                        jblValTo.setText("");
                    }

                    jlbVerificacion.setText(datosRegistro[8] != null ? datosRegistro[8] : "");
                    jlbDocumento.setText(datosRegistro[9] != null ? datosRegistro[9] : "");
                    labelsjpnVerificarVisibles();

                } else if (button.getBackground().equals(COLOR_NARANJA)) { //si el color del boton es naranja
                    SimpleDateFormat formatoOrigen = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat formatoDestino = new SimpleDateFormat("EEE dd-MMM-yyyy");
                    int numeroHabitacion = Integer.parseInt(button.getText());
                    String[] datosReserva = con.obtenerDatosReserva(numeroHabitacion, (java.sql.Date) fechaSql, idHotel);
                    jblMosNom.setText(datosReserva[0] != null ? datosReserva[0] : "");
                    jblMosApe.setText(datosReserva[1] != null ? datosReserva[1] : "");
                    jblMosTel.setText(datosReserva[2] != null ? datosReserva[2] : "");
                    jlbDocumento.setText(datosReserva[3] != null ? datosReserva[3] : "");
                    try {
                        if (datosReserva[4] != null && datosReserva[6] != null) {
                            Date fechaLlegada = formatoOrigen.parse(datosReserva[4]);
                            Date fechasalida = formatoOrigen.parse(datosReserva[6]);
                            String fechaLlegadaFormateada = formatoDestino.format(fechaLlegada);
                            String fechaSalidaForm = formatoDestino.format(fechasalida);
                            jblMosFeLle.setText(fechaLlegadaFormateada);
                            jblMosFeSa.setText(fechaSalidaForm);
                        } else {
                            jblMosFeLle.setText("");
                            jblMosFeSa.setText("");
                        }

                    } catch (ParseException ex) {
                        JOptionPane.showMessageDialog(null, """
                                                            Error en el panel de Verificaciones con la fecha de llagada y salida
                                                            Error! """ + ex.getMessage());
                    }
                    jblMosHoLle.setText(datosReserva[5] != null ? datosReserva[5] : "");
                    jblValNo.setText(datosReserva[7] != null ? datosReserva[7] : "");
                    jblValTo.setText(datosReserva[8] != null ? datosReserva[8] : "");
                    jblIdRes.setText(datosReserva[9] != null ? datosReserva[9] : "");
                    jlbVerificacion.setText(datosReserva[10] != null ? datosReserva[10] : "");
                    labelsjpnVerificarVisibles();
                }
                oldForeground = button.getForeground();
                oldBorder = button.getBorder();

                CompoundBorder compoundBorder = (CompoundBorder) button.getBorder();
                TitledBorder topBorder = (TitledBorder) compoundBorder.getOutsideBorder();
                TitledBorder bottomBorder = (TitledBorder) compoundBorder.getInsideBorder();

                oldTitleColor = topBorder.getTitleColor(); // Suponiendo que ambos títulos tienen el mismo color.

                topBorder.setTitleColor(Color.WHITE);
                bottomBorder.setTitleColor(Color.WHITE);

                button.setForeground(Color.WHITE);
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                jblIdRes.setText("");
                jblMosNom.setText("");
                jblMosApe.setText("");
                jblMosFeLle.setText("");
                jblMosFeSa.setText("");
                jblMosHoLle.setText("");
                jblMosTel.setText("");
                jlbDocumento.setText("");
                jlbVerificacion.setText("");
                jblValNo.setText("");
                jblValTo.setText("");
                button.setForeground(oldForeground);

                CompoundBorder compoundBorder = (CompoundBorder) button.getBorder();
                TitledBorder topBorder = (TitledBorder) compoundBorder.getOutsideBorder();
                TitledBorder bottomBorder = (TitledBorder) compoundBorder.getInsideBorder();

                topBorder.setTitleColor(oldTitleColor);
                bottomBorder.setTitleColor(oldTitleColor);

                button.setBorder(oldBorder);
                button.repaint();
                labelsJpnVerificarInvisibles();
            }
        });
    }

    private void labelsjpnVerificarVisibles() {
        for (Component comp : jpnVerificar.getComponents()) {
            if (comp instanceof JLabel) {
                comp.setVisible(true);
            }

        }
    }

    private void labelsJpnVerificarInvisibles() {
        for (Component comp : jpnVerificar.getComponents()) {
            if (comp instanceof JLabel) {
                comp.setVisible(false);
            }
        }
    }

    private Date agregarDiasAFecha(Date fecha, int dias) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.add(Calendar.DATE, dias);
        return calendar.getTime();
    }

    public void hora() {
        Calendar calendario = new GregorianCalendar();
        Date horaactual = new Date();
        calendario.setTime(horaactual);
        hora = calendario.get(Calendar.HOUR_OF_DAY) > 9 ? "" + calendario.get(Calendar.HOUR_OF_DAY) : "" + calendario.get(Calendar.HOUR_OF_DAY);
        minutos = calendario.get(Calendar.MINUTE) > 9 ? "" + calendario.get(Calendar.MINUTE) : "0" + calendario.get(Calendar.MINUTE);
        seg = calendario.get(Calendar.SECOND) > 9 ? "" + calendario.get(Calendar.SECOND) : "0" + calendario.get(Calendar.SECOND);
    }

    public class RelojSwingWorker extends SwingWorker<Void, String> {

        private String lastTime = "";

        @Override
        protected Void doInBackground() throws Exception {
            while (!isCancelled()) {
                hora();
                String newTime = hora + ":" + minutos + ":" + seg;
                if (!newTime.equals(lastTime)) {
                    lastTime = newTime;
                    publish(newTime);
                }
                Thread.sleep(1000);
            }
            return null;
        }

        @Override
        protected void process(List<String> chunks) {
            String lastChunk = chunks.get(chunks.size() - 1);
            txtHoraRec.setText(" " + lastChunk);
        }
    }

    MouseAdapter mouseAdapter = new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            JButton button = (JButton) e.getSource();
            button.setBackground(new Color(255, 219, 95));
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        public void mouseExited(MouseEvent e) {
            JButton button = (JButton) e.getSource();
            button.setBackground(COLOR_NARANJA);

        }
    };

    /**
     * Gestiona el reporte SIRE de salida para huéspedes extranjeros.
     * Este método es SÍNCRONO cuando se usa para liberación de habitación:
     * espera a que el formulario SIRE se complete antes de retornar.
     * 
     * @param bloquearHastaCompletar si es true, espera a que SIRE termine antes de retornar
     * @return true si el formulario SIRE se completó exitosamente, false si falló o el usuario canceló
     */
    public boolean registroSireSalida(boolean bloquearHastaCompletar) {
        String tipoMov = "Salida";
       
        String nacionalidadDisplay = datosReporte.getNacionalidad() != null 
            ? datosReporte.getNacionalidad().replaceAll("\\s*\\(.*\\)$", "").trim() 
            : "extranjera";

        String[] opciones = {"GESTIONAR", "CANCELAR"};
        int seleccion = JOptionPane.showOptionDialog(
                null,
                "La habitación que desea liberar tiene un huésped\n"
                + "de nacionalidad " + nacionalidadDisplay + " que fue reportado\n"
                + "al sistema SIRE de Migración Colombia.\n\n"
                + "Para cumplir con la normativa, es necesario registrar\n"
                + "la salida del huésped en el SIRE antes de liberar\n"
                + "la habitación.\n\n"
                + "¿Desea gestionar el reporte de salida ahora?",
                "Reporte SIRE de Salida",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                icon,
                opciones,
                opciones[0]
        );
        
        if (seleccion != 0) {
            // Usuario canceló o cerró el diálogo
            System.out.println("[SIRE-SALIDA] Usuario canceló el reporte SIRE de salida");
            return !bloquearHastaCompletar; // Si estamos bloqueando, retornar false (no puede liberar sin SIRE)
        }
        
        // Preparar datos del reporte
        LocalDate fecha = LocalDate.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("d/M/yyyy");
        String fechaMov = fecha.format(formato);
        String tipoDoc = datosReporte.getTipoDocumento();
        String fechaNacimiento = datosReporte.getFechaNacimiento();
        String numDoc = datosReporte.getNumDocumento();
        String primerApe = datosReporte.getPrimerApeliido();
        String segApe = datosReporte.getSegundoApellido();
        String nombres = datosReporte.getNombres();
        String nacionalidad = datosReporte.getNacionalidad().replaceAll("\\s*\\(.*\\)$", "").trim();
        String paisProce = datosReporte.getPaisProce();
        String deparProce = datosReporte.getDeparProce();
        String ciudadProce = datosReporte.getCiudadProce();
        String paisDest = datosReporte.getPaisDest();
        String deparDest = datosReporte.getDeparDet();
        String ciudadDest = datosReporte.getCiudadDest();

        if (bloquearHastaCompletar) {
            // MODO BLOQUEANTE: Ejecutar SIRE y esperar resultado antes de liberar habitación
            System.out.println("[SIRE-SALIDA] Modo bloqueante: ejecutando reporte SIRE de salida...");
            
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() {
                    try {
                        ReporteSire reporte = new ReporteSire(tipoMov, fechaMov, tipoDoc, fechaNacimiento, numDoc, primerApe, segApe,
                                nombres, nacionalidad, paisProce, deparProce, ciudadProce, paisDest, deparDest, ciudadDest);
                        return reporte.abrirPagina();
                    } catch (Exception e) {
                        System.err.println("[SIRE-SALIDA] ❌ Error en reporte SIRE: " + e.getMessage());
                        e.printStackTrace();
                        return false;
                    }
                }

                @Override
                protected void done() {
                    try {
                        boolean resultado = get();
                        if (!resultado) {
                            manejarErrorSireSalida(tipoMov, fechaMov, tipoDoc, fechaNacimiento, numDoc, 
                                primerApe, segApe, nombres, nacionalidad, paisProce, deparProce, 
                                ciudadProce, paisDest, deparDest, ciudadDest);
                        }
                    } catch (InterruptedException | ExecutionException ex) {
                        manejarErrorSireSalida(tipoMov, fechaMov, tipoDoc, fechaNacimiento, numDoc, 
                            primerApe, segApe, nombres, nacionalidad, paisProce, deparProce, 
                            ciudadProce, paisDest, deparDest, ciudadDest);
                    }
                }
            };
            
            worker.execute();
            
            // Esperar a que el SwingWorker termine y obtener el resultado
            try {
                boolean resultado = worker.get(); // BLOQUEA hasta que SIRE termine
                System.out.println("[SIRE-SALIDA] Resultado del reporte: " + resultado);
                return resultado;
            } catch (InterruptedException | ExecutionException ex) {
                System.err.println("[SIRE-SALIDA] ❌ Error esperando resultado SIRE: " + ex.getMessage());
                return false;
            }
            
        } else {
            // MODO NO BLOQUEANTE: Ejecutar SIRE en segundo plano (comportamiento original)
            System.out.println("[SIRE-SALIDA] Modo no bloqueante: ejecutando reporte SIRE en segundo plano...");
            
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    ReporteSire reporte = new ReporteSire(tipoMov, fechaMov, tipoDoc, fechaNacimiento, numDoc, primerApe, segApe,
                            nombres, nacionalidad, paisProce, deparProce, ciudadProce, paisDest, deparDest, ciudadDest);
                    reporte.abrirPagina();
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                    } catch (InterruptedException | ExecutionException ex) {
                        manejarErrorSireSalida(tipoMov, fechaMov, tipoDoc, fechaNacimiento, numDoc, 
                            primerApe, segApe, nombres, nacionalidad, paisProce, deparProce, 
                            ciudadProce, paisDest, deparDest, ciudadDest);
                    }
                }
            };
            worker.execute();
            return true;
        }
    }
    
    /**
     * Método de compatibilidad: ejecuta el reporte SIRE en modo NO bloqueante (comportamiento original)
     */
    public void registroSireSalida() {
        registroSireSalida(false);
    }
    
    /**
     * Maneja errores en el reporte SIRE de salida: muestra mensaje e imprime datos para reporte manual
     */
    private void manejarErrorSireSalida(String tipoMov, String fechaMov, String tipoDoc, String fechaNacimiento,
            String numDoc, String primerApe, String segApe, String nombres, String nacionalidad,
            String paisProce, String deparProce, String ciudadProce, String paisDest, String deparDest, String ciudadDest) {
        JOptionPane.showMessageDialog(null, """
                                            Error al gestionar el reporte de salida en SIRE,
                                            por favor hagalo manualmente""");
        ThermalPrinter thermalPrinter = new ThermalPrinter();
        String clientInfo = String.format("""
                                          Tipo Movimiento   : %s  
                                          Fecha Movimiento  : %s
                                          Tipo Documento    : %s
                                          Fecha Nacimiento  : %s
                                          Numero Documento  : %s 
                                          Primer apellido   : %s
                                          Segundo Apellido  : %s
                                          Nombres           : %s
                                          Nacionalidad      : %s
                                          Pais Procedencia  : %s
                                          Depto procedencia : %s
                                          Ciudad Procedencia: %s
                                          Pais destino      : %s
                                          Depto destino     : %s
                                          Ciudad Destino    : %s
                                          """,
                tipoMov, fechaMov, tipoDoc, fechaNacimiento, numDoc, primerApe, segApe, nombres, nacionalidad,
                paisProce, deparProce, ciudadProce, paisDest, deparDest, ciudadDest);
        List<String[]> productList = new ArrayList<>();
        String nuevoPDF = thermalPrinter.createPDF(clientInfo, productList, "SIRE_" + nombres + "_" + primerApe);
        thermalPrinter.printPDF(nuevoPDF);
    }

    /**
     * Libera la habitación en la BD y actualiza visualmente el botón a estado "Libre" (verde)
     */
    private void liberarHabitacionVisualmente(int numeroHabitacion, JButton botonHabitacion) {
        if (con.liberarHabitacion(numeroHabitacion, idHotel)) {
            System.out.println("[LIBERAR-HAB] ✓ Habitación " + numeroHabitacion + " liberada exitosamente en BD");
            botonHabitacion.setBackground(new Color(0, 153, 0));
            CompoundBorder compoundBorder = (CompoundBorder) botonHabitacion.getBorder();
            TitledBorder topBorder = (TitledBorder) compoundBorder.getOutsideBorder();
            TitledBorder bottomBorder = (TitledBorder) compoundBorder.getInsideBorder();
            topBorder.setTitle("Libre");
            topBorder.setTitleColor(new Color(0, 51, 51));
            bottomBorder.setTitleColor(new Color(0, 51, 51));
            botonHabitacion.repaint();
        } else {
            System.out.println("[LIBERAR-HAB] ❌ No se pudo liberar la habitación " + numeroHabitacion + " en BD");
            JOptionPane.showMessageDialog(null, "No se pudo liberar la habitación. Intente nuevamente.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void toggleHabitacion(int numeroHabitacion, JButton botonHabitacion) {
        // ===== VERIFICACIÓN CRÍTICA: Revisar si hay ventana SIRE abierta =====
        // Esto DEBE hacerse ANTES de mostrar cualquier opción al usuario
        System.out.println("[RECEPCION] toggleHabitacion() - Verificando estado SIRE...");
        if (ReporteSire.hayVentanaAbierta()) {
            System.out.println("[RECEPCION] ⚠ Ventana SIRE abierta detectada - Bloqueando acceso");
            JOptionPane.showMessageDialog(this,
                "⚠ REGISTRO SIRE PENDIENTE\n\n" +
                "Hay un reporte SIRE en proceso que debe completarse primero.\n\n" +
                "Por favor:\n" +
                "1. Complete el formulario SIRE abierto\n" +
                "2. Haga clic en 'AGREGAR REGISTRO'\n" +
                "3. Cierre la ventana del navegador\n" +
                "4. Intente nuevamente",
                "Operación Bloqueada",
                JOptionPane.WARNING_MESSAGE);
            
            // Maximizar y traer al frente la ventana SIRE pendiente
            ReporteSire.maximizarVentanaPendiente();
            return; // DETENER - No mostrar el panel de opciones
        }
        System.out.println("[RECEPCION] ✓ No hay ventana SIRE abierta, mostrando opciones...");
        
        String[] opciones = {"REGISTRAR", "RESERVAR", "CANCELAR"};
        String[] concluirReserva = {"REGISTRAR  RESERVA", "ANULAR  RESERVAR", "CANCELAR"};
        if (botonHabitacion.getBackground().equals(new Color(0, 153, 0))) {//color verde
            int seleccion = JOptionPane.showOptionDialog(
                    null,
                    "Que quieres hacer ? ",
                    "Seleccione una opción",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    icon,
                    opciones,
                    opciones[0]
            );
            switch (seleccion) {
                case 0 -> {// Registrar
                    Date fechaSeleccionada = jdchFechaHabs.getDate();
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);

                    if (fechaSeleccionada.before(calendar.getTime())) {
                        // La fecha seleccionada es anterior a la actual, muestra un mensaje de error
                        JOptionPane.showMessageDialog(null, "Por favor seleccione una fecha presente o futura.", "Error en la fecha", JOptionPane.ERROR_MESSAGE);
                        // Restablece la fecha en el JDateChooser a la fecha actual
                        jdchFechaHabs.setDate(new Date());
                        return;
                    } else {
                        // La fecha seleccionada es válida, se abre el diálogo de reserva
                        Registros abrir = new Registros(this, false, usus, hotel);//aqui cambie registros

                        abrir.regishabi.setText(Integer.toString(numeroHabitacion));
                        abrir.nombreUsuario.setText(txtNombreUsuario.getText());
                        abrir.lblRollregistro.setText(lblRoll.getText());
                        abrir.txtUsuario.setText(jlbUsuario.getText());
                        abrir.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowOpened(WindowEvent e) {
                                botonHabitacion.setEnabled(false);
                            }

                            @Override
                            public void windowClosed(WindowEvent e) {
                                botonHabitacion.setEnabled(true);
                            }
                        });
                        abrir.setVisible(true);
                    }
                }
                case 1 -> {// Reservar
                    Date fechaSeleccionada = jdchFechaHabs.getDate();
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);

                    if (fechaSeleccionada.before(calendar.getTime())) {
                        // La fecha seleccionada es anterior a la actual, muestra un mensaje de error
                        JOptionPane.showMessageDialog(null, "Por favor seleccione una fecha presente o futura.", "Error en la fecha", JOptionPane.ERROR_MESSAGE);
                        // Restablece la fecha en el JDateChooser a la fecha actual
                        jdchFechaHabs.setDate(new Date());
                        return;
                    } else {
                        // La fecha seleccionada es válida, se abre el diálogo de reserva
                        Reserva ab = new Reserva(this, false, usus, hotel);

                        Reserva.jdchLlegada.setDate(jdchFechaHabs.getDate());
                        Reserva.jdchLlegada.setEnabled(false);
                        ab.txtNumHab.setText(Integer.toString(numeroHabitacion));
                        ab.txtNombre.setText(txtNombreUsuario.getText());
                        ab.lblRollRegistro.setText(lblRoll.getText());
                        ab.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowOpened(WindowEvent e) {
                                botonHabitacion.setEnabled(false);
                            }

                            @Override
                            public void windowClosed(WindowEvent e) {
                                botonHabitacion.setEnabled(true);
                            }
                        });
                        ab.setVisible(true);
                    }
                }
                case 2 -> {
                }
                default -> {
                }
            }
            // Cancelar
            // No se realiza ninguna acción

        } else if (botonHabitacion.getBackground().equals(COLOR_ROJO)) {

            Date fechaSeleccionada = jdchFechaHabs.getDate();

            // Inicializar fechaActual a las 00:00:00 del día actual
            Calendar calActual = Calendar.getInstance();
            calActual.set(Calendar.HOUR_OF_DAY, 0);
            calActual.set(Calendar.MINUTE, 0);
            calActual.set(Calendar.SECOND, 0);
            calActual.set(Calendar.MILLISECOND, 0);
            Date fechaActual = calActual.getTime();

            JButton liberarButton = new JButton("Liberar");
            liberarButton.setBackground(new Color(0, 51, 51));
            liberarButton.setForeground(Color.WHITE);

            liberarButton.addActionListener(e -> {

                if (fechaSeleccionada.compareTo(fechaActual) < 0) {
                    JOptionPane.showMessageDialog(null, "Operación incorrecta. La fecha seleccionada es menor a la actual", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Verificar si hay una ventana de reporte SIRE abierta de un proceso anterior
                if (ReporteSire.hayVentanaAbierta()) {
                    System.out.println("[LIBERAR-HAB] ⚠ Intentó liberar con ventana SIRE abierta");
                    JOptionPane.showMessageDialog(null,
                        "⚠ REGISTRO SIRE PENDIENTE\n\n" +
                        "No se puede liberar esta habitación hasta que\n" +
                        "complete el reporte SIRE anterior.\n\n" +
                        "Por favor:\n" +
                        "1. Complete el formulario SIRE que está abierto\n" +
                        "2. Haga clic en 'AGREGAR REGISTRO'\n" +
                        "3. Cierre la ventana del navegador\n" +
                        "4. Intente nuevamente liberar la habitación",
                        "Registro SIRE Pendiente",
                        JOptionPane.WARNING_MESSAGE);
                    ReporteSire.maximizarVentanaPendiente();
                    return;
                }

                // Verificar si el huésped es extranjero (requiere reporte SIRE de salida)
                String nacionalidadHuesped = datosReporte.getNacionalidad() != null 
                    ? datosReporte.getNacionalidad().replaceAll("\\s*\\(.*\\)$", "").trim() 
                    : "COLOMBIA";
                boolean esExtranjero = !nacionalidadHuesped.equals("COLOMBIA");
                
                System.out.println("[LIBERAR-HAB] Nacionalidad: " + nacionalidadHuesped + " | Extranjero: " + esExtranjero);
                
                if (esExtranjero) {
                    // ===== FLUJO PARA EXTRANJEROS: SIRE PRIMERO, LIBERAR DESPUÉS =====
                    System.out.println("[LIBERAR-HAB] Huésped extranjero - Iniciando reporte SIRE de salida ANTES de liberar");
                    
                    // Cerrar el diálogo de opciones
                    Window win = SwingUtilities.getWindowAncestor(liberarButton);
                    if (win != null) {
                        win.dispose();
                    }
                    
                    // Ejecutar SIRE en un SwingWorker para no bloquear la UI
                    SwingWorker<Boolean, Void> sireWorker = new SwingWorker<Boolean, Void>() {
                        @Override
                        protected Boolean doInBackground() {
                            // registroSireSalida(true) es BLOQUEANTE: espera resultado de SIRE
                            return registroSireSalida(true);
                        }
                        
                        @Override
                        protected void done() {
                            try {
                                boolean sireExitoso = get();
                                System.out.println("[LIBERAR-HAB] Resultado SIRE de salida: " + sireExitoso);
                                
                                if (sireExitoso) {
                                    // SIRE completado exitosamente → Ahora sí liberar la habitación
                                    System.out.println("[LIBERAR-HAB] ✓ SIRE completado - Procediendo a liberar habitación " + numeroHabitacion);
                                    liberarHabitacionVisualmente(numeroHabitacion, botonHabitacion);
                                } else {
                                    // SIRE falló o fue cancelado → NO liberar
                                    System.out.println("[LIBERAR-HAB] ⚠ SIRE no completado - Habitación NO liberada");
                                    JOptionPane.showMessageDialog(null,
                                        "⚠ La habitación NO fue liberada.\n\n" +
                                        "El reporte SIRE de salida no se completó.\n" +
                                        "La habitación permanecerá ocupada hasta que\n" +
                                        "se complete el reporte SIRE.",
                                        "Liberación Cancelada",
                                        JOptionPane.WARNING_MESSAGE);
                                }
                            } catch (InterruptedException | ExecutionException ex) {
                                System.err.println("[LIBERAR-HAB] ❌ Error en proceso SIRE: " + ex.getMessage());
                                JOptionPane.showMessageDialog(null,
                                    "Error durante el reporte SIRE.\nLa habitación NO fue liberada.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    };
                    sireWorker.execute();
                    
                } else {
                    // ===== FLUJO PARA NACIONALES: Liberar directamente =====
                    System.out.println("[LIBERAR-HAB] Huésped nacional - Liberando directamente");
                    
                    Window win = SwingUtilities.getWindowAncestor(liberarButton);
                    if (win != null) {
                        win.dispose();
                    }
                    
                    liberarHabitacionVisualmente(numeroHabitacion, botonHabitacion);
                }
            });

            JButton reasignarButton = new JButton("Reasignar");
            reasignarButton.setBackground(new Color(0, 51, 51));
            reasignarButton.setForeground(Color.WHITE);
            reasignarButton.addActionListener(e -> {
                if (fechaSeleccionada.compareTo(fechaActual) < 0) {
                    JOptionPane.showMessageDialog(null, "Operación no Autorizada!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    // Verificar si hay ventana SIRE abierta antes de reasignar
                    System.out.println("[RECEPCION-REASIGNAR] Verificando estado SIRE...");
                    if (ReporteSire.hayVentanaAbierta()) {
                        System.out.println("[RECEPCION-REASIGNAR] ⚠ Ventana SIRE abierta - Bloqueando reasignación");
                        JOptionPane.showMessageDialog(null,
                            "⚠ REGISTRO SIRE PENDIENTE\n\n" +
                            "Hay un reporte SIRE en proceso que debe completarse primero.\n\n" +
                            "Por favor:\n" +
                            "1. Complete el formulario SIRE abierto\n" +
                            "2. Haga clic en 'AGREGAR REGISTRO'\n" +
                            "3. Cierre la ventana del navegador\n" +
                            "4. Intente nuevamente",
                            "Operación Bloqueada",
                            JOptionPane.WARNING_MESSAGE);
                        ReporteSire.maximizarVentanaPendiente();
                        return;
                    }
                    System.out.println("[RECEPCION-REASIGNAR] ✓ Procediendo con reasignación...");
                    
                    Window win = SwingUtilities.getWindowAncestor(liberarButton);
                    if (win != null) {
                        win.dispose();
                    }
                    Registros abrir = new Registros(this, false, usus, hotel);
                    abrir.regishabi.setText(Integer.toString(numeroHabitacion));
                    abrir.nombreUsuario.setText(txtNombreUsuario.getText());
                    abrir.lblRollregistro.setText(lblRoll.getText());
                    abrir.regisnumdoc.setText(jlbDocumento.getText());
                    abrir.procesoDatosCliViejo();
                    abrir.cbxDeparProce.setSelectedItem(datosReporte.getDeparProce());
                    abrir.cbxCiudadProce.setSelectedItem(datosReporte.getCiudadProce());
                    abrir.cbxDeparDest.setSelectedItem(datosReporte.getDeparDet());
                    abrir.cbxCiudadDest.setSelectedItem(datosReporte.getCiudadDest());
                    abrir.regisvalorhabi.setText(jblValNo.getText());
                    abrir.txtUsuario.setText(usus.getUsuario());
                    String tipoDoc = abrir.registipdoc.getSelectedItem().toString();
                    if (!tipoDoc.equals("CÉDULA DE CIUDADANÍA")) {
                        String fecha = datosReporte.getFechaNacimiento();
                        // Verificar que la cadena de fecha no esté vacía y tenga el formato esperado
                        if (fecha != null && !fecha.isEmpty()) {
                            SimpleDateFormat formato = new SimpleDateFormat("d/M/yyyy");
                            try {
                                Date fechaNacimiento = formato.parse(fecha);
                                abrir.jdchNacimiento.setDate(fechaNacimiento);
                            } catch (ParseException en) {
                                JOptionPane.showMessageDialog(null, "Formato de fecha inválido.", "Error de Fecha", JOptionPane.ERROR_MESSAGE);
                                en.printStackTrace();
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "No hay fecha de nacimiento disponible.", "Error de Fecha", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    abrir.regiscomi.setFocusable(false);
                    abrir.regisvalorhabi.requestFocus();
                    abrir.setVisible(true);
                }
            });

            JButton cancelButton = new JButton("Cancelar");
            cancelButton.setBackground(new Color(0, 51, 51));
            cancelButton.setForeground(Color.white);
            cancelButton.addActionListener(e -> {
                Window win = SwingUtilities.getWindowAncestor(cancelButton);
                if (win != null) {
                    win.dispose();
                }
            });

            Object[] options = {liberarButton, reasignarButton, cancelButton};

            JOptionPane.showOptionDialog(
                    null,
                    "¿Que desea hacer?",
                    "Confirmar acción...",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    icon,
                    options,
                    options[0]);

        } else if (botonHabitacion.getBackground().equals(COLOR_NARANJA)) {
            int decision = JOptionPane.showOptionDialog(
                    null,
                    "Que quiere hacer ? ",
                    "Seleccione una opción",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    icon,
                    concluirReserva,
                    concluirReserva[0]
            );
            switch (decision) {
                case 0: // Registrar reserva
                    // Verificar si hay ventana SIRE abierta antes de registrar la reserva
                    System.out.println("[RECEPCION-RESERVA] Verificando estado SIRE...");
                    if (ReporteSire.hayVentanaAbierta()) {
                        System.out.println("[RECEPCION-RESERVA] ⚠ Ventana SIRE abierta - Bloqueando registro de reserva");
                        JOptionPane.showMessageDialog(null,
                            "⚠ REGISTRO SIRE PENDIENTE\n\n" +
                            "Hay un reporte SIRE en proceso que debe completarse primero.\n\n" +
                            "Por favor:\n" +
                            "1. Complete el formulario SIRE abierto\n" +
                            "2. Haga clic en 'AGREGAR REGISTRO'\n" +
                            "3. Cierre la ventana del navegador\n" +
                            "4. Intente nuevamente",
                            "Operación Bloqueada",
                            JOptionPane.WARNING_MESSAGE);
                        ReporteSire.maximizarVentanaPendiente();
                        return;
                    }
                    System.out.println("[RECEPCION-RESERVA] ✓ Procediendo con registro de reserva...");
                    
                    Registros abrir = new Registros(this, false, usus, hotel);
                    int idRes1 = 0;
                    if (!jblIdRes.getText().isEmpty()) {
                        idRes1 = Integer.parseInt(jblIdRes.getText());
                    }
                    String fechaInicio = jblMosFeLle.getText();
                    String fechaFinal = jblMosFeSa.getText();

                    Locale locale = new Locale("es", "ES");

                    SimpleDateFormat formatoOrigen = new SimpleDateFormat("EEE dd-MMM-yyyy", locale);
                    SimpleDateFormat formatoDestino = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat formatoDestinoJDateChooser = new SimpleDateFormat("EEE dd-MMM-yyyy");
                    try {
                        Date fecha = formatoOrigen.parse(fechaInicio);
                        Date fechasa = formatoOrigen.parse(fechaFinal);
                        String fechaIniEmv = formatoDestino.format(fecha);
                        String fechaFinalJDateChooser = formatoDestinoJDateChooser.format(fechasa);
                        abrir.regisfe.setText(fechaIniEmv);
                        abrir.jdchFeSa.setDateFormatString("EEE dd-MMM-yyyy");
                        abrir.jdchFeSa.setDate(formatoDestinoJDateChooser.parse(fechaFinalJDateChooser));
                        abrir.jdchFeSa.setEnabled(false);
                    } catch (ParseException ex) {
                        Logger.getLogger(Recepciones.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    abrir.idReservaExitosa = idRes1;
                    abrir.nombreUsuario.setText(txtNombreUsuario.getText());
                    abrir.lblRollregistro.setText(lblRoll.getText());
                    abrir.regishabi.setText(Integer.toString(numeroHabitacion));
                    abrir.txtUsuario.setText(usus.getUsuario());
                    abrir.regisnumdoc.setText(jlbDocumento.getText());
                    abrir.procesoDatosCliViejo();

                    abrir.setVisible(true);

                    break;

                case 1: // Anular reserva
                    String[] arreglo = {"Anular", "Cancelar"};
                    int opcion = JOptionPane.showOptionDialog(null, "¿Seguro desea ANULAR esta reseva?", "Confirmar acción...", 0, JOptionPane.QUESTION_MESSAGE, null, arreglo, "Anular");
                    if (opcion == 0) {
                        int idRes = 0;
                        if (jblIdRes.getText().isEmpty()) {
                            idRes = Integer.parseInt(jblIdRes.getText());
                        }
                        con.anularReserva(idRes);
                        con.liberarHabitacion(numeroHabitacion, hotel.getIdHoteles());
                        botonHabitacion.setBackground(new Color(0, 153, 0));
                    } else {
                    }
                    break;
                case 2: // Cancelar
                    // No se realiza ninguna acción
                    break;
                default:
                    // El usuario cerró el JOptionPane sin seleccionar ninguna opción
                    break;
            }
        }
    }

    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jpnHabitaciones = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        btnIngresoExtra = new javax.swing.JButton();
        btnGastos = new javax.swing.JButton();
        btnOperarTurno = new javax.swing.JButton();
        btnInformeContable = new javax.swing.JButton();
        btnVentasComisiones = new javax.swing.JButton();
        btnRegistrarUsuario = new javax.swing.JButton();
        jpnVerificar = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jblMosNom = new javax.swing.JLabel();
        jblMosApe = new javax.swing.JLabel();
        jblMosTel = new javax.swing.JLabel();
        jblMosFeLle = new javax.swing.JLabel();
        jblMosHoLle = new javax.swing.JLabel();
        jblMosFeSa = new javax.swing.JLabel();
        jblValNo = new javax.swing.JLabel();
        jblValTo = new javax.swing.JLabel();
        jblIdRes = new javax.swing.JLabel();
        jlbVerificacion = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jlbDocumento = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jdchFechaHabs = new com.toedter.calendar.JDateChooser();
        btnHoy = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        lblRoll = new javax.swing.JLabel();
        txtNombreUsuario = new javax.swing.JTextField();
        txtIdTurno = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtHoraRec = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        btnSalir = new javax.swing.JButton();
        jLabel26 = new javax.swing.JLabel();
        btnSiguiente = new javax.swing.JButton();
        btnAtraz = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jlbUsuario = new javax.swing.JLabel();
        jlbNombreHotel = new javax.swing.JLabel();
        jlbSogan = new javax.swing.JLabel();
        pnlVerde1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        pnlVerde = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        pnlRojo = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jpnHabitacionesLayout = new javax.swing.GroupLayout(jpnHabitaciones);
        jpnHabitaciones.setLayout(jpnHabitacionesLayout);
        jpnHabitacionesLayout.setHorizontalGroup(
            jpnHabitacionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 966, Short.MAX_VALUE)
        );
        jpnHabitacionesLayout.setVerticalGroup(
            jpnHabitacionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 602, Short.MAX_VALUE)
        );

        jPanel2.setBackground(new java.awt.Color(0, 102, 102));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnIngresoExtra.setBackground(new java.awt.Color(255, 179, 39));
        btnIngresoExtra.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        btnIngresoExtra.setForeground(new java.awt.Color(0, 51, 51));
        btnIngresoExtra.setText("INGRESO EXTRA");
        btnIngresoExtra.setBorder(null);
        btnIngresoExtra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIngresoExtraActionPerformed(evt);
            }
        });
        jPanel2.add(btnIngresoExtra, new org.netbeans.lib.awtextra.AbsoluteConstraints(9, 5, 275, 40));

        btnGastos.setBackground(new java.awt.Color(255, 179, 39));
        btnGastos.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        btnGastos.setForeground(new java.awt.Color(0, 51, 51));
        btnGastos.setText("GASTOS");
        btnGastos.setBorder(null);
        btnGastos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnGastosMouseClicked(evt);
            }
        });
        jPanel2.add(btnGastos, new org.netbeans.lib.awtextra.AbsoluteConstraints(9, 57, 275, 40));

        btnOperarTurno.setBackground(new java.awt.Color(255, 179, 39));
        btnOperarTurno.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        btnOperarTurno.setForeground(new java.awt.Color(0, 51, 51));
        btnOperarTurno.setText("OPERAR TURNO");
        btnOperarTurno.setBorder(null);
        btnOperarTurno.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnOperarTurnoMouseClicked(evt);
            }
        });
        jPanel2.add(btnOperarTurno, new org.netbeans.lib.awtextra.AbsoluteConstraints(9, 109, 275, 40));

        btnInformeContable.setBackground(new java.awt.Color(255, 179, 39));
        btnInformeContable.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        btnInformeContable.setForeground(new java.awt.Color(0, 51, 51));
        btnInformeContable.setText("INFORME CONTABLE");
        btnInformeContable.setBorder(null);
        btnInformeContable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnInformeContableMouseClicked(evt);
            }
        });
        jPanel2.add(btnInformeContable, new org.netbeans.lib.awtextra.AbsoluteConstraints(9, 161, 275, 40));

        btnVentasComisiones.setBackground(new java.awt.Color(255, 179, 39));
        btnVentasComisiones.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        btnVentasComisiones.setForeground(new java.awt.Color(0, 51, 51));
        btnVentasComisiones.setText("INFORME DE VENTAS & COMISIONES");
        btnVentasComisiones.setBorder(null);
        btnVentasComisiones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVentasComisionesActionPerformed(evt);
            }
        });
        jPanel2.add(btnVentasComisiones, new org.netbeans.lib.awtextra.AbsoluteConstraints(9, 213, 275, 40));

        btnRegistrarUsuario.setBackground(new java.awt.Color(255, 179, 39));
        btnRegistrarUsuario.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        btnRegistrarUsuario.setForeground(new java.awt.Color(0, 51, 51));
        btnRegistrarUsuario.setText("REGISTRAR USUARIO");
        btnRegistrarUsuario.setBorder(null);
        btnRegistrarUsuario.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnRegistrarUsuarioMouseClicked(evt);
            }
        });
        btnRegistrarUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarUsuarioActionPerformed(evt);
            }
        });
        jPanel2.add(btnRegistrarUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(9, 265, 275, 40));

        jpnVerificar.setBackground(new java.awt.Color(0, 51, 51));
        jpnVerificar.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 179, 39), 1, true));

        jLabel3.setBackground(new java.awt.Color(255, 179, 39));
        jLabel3.setForeground(new java.awt.Color(255, 179, 39));
        jLabel3.setText("Nombres      ");
        jLabel3.setFocusable(false);

        jLabel4.setForeground(new java.awt.Color(255, 179, 39));
        jLabel4.setText("Apellidos      ");
        jLabel4.setFocusable(false);

        jLabel5.setForeground(new java.awt.Color(255, 179, 39));
        jLabel5.setText("Telefono   ");
        jLabel5.setFocusable(false);

        jLabel6.setForeground(new java.awt.Color(255, 179, 39));
        jLabel6.setText("Fecha LLegada  ");
        jLabel6.setFocusable(false);

        jLabel7.setForeground(new java.awt.Color(255, 179, 39));
        jLabel7.setText("Hora Llegada    ");
        jLabel7.setFocusable(false);

        jLabel8.setForeground(new java.awt.Color(255, 179, 39));
        jLabel8.setText("Fecha Salida      ");
        jLabel8.setFocusable(false);

        jLabel9.setForeground(new java.awt.Color(255, 179, 39));
        jLabel9.setText("Valor Por  Noche ");
        jLabel9.setFocusable(false);

        jLabel10.setForeground(new java.awt.Color(255, 179, 39));
        jLabel10.setText("Valor Total            ");
        jLabel10.setFocusable(false);

        jblMosNom.setBackground(new java.awt.Color(255, 255, 255));
        jblMosNom.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jblMosNom.setForeground(new java.awt.Color(255, 179, 39));
        jblMosNom.setText("Carlos Andres");
        jblMosNom.setFocusable(false);

        jblMosApe.setBackground(new java.awt.Color(255, 255, 255));
        jblMosApe.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jblMosApe.setForeground(new java.awt.Color(255, 179, 39));
        jblMosApe.setText("Moran Caicedo");
        jblMosApe.setFocusable(false);

        jblMosTel.setBackground(new java.awt.Color(255, 255, 255));
        jblMosTel.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jblMosTel.setForeground(new java.awt.Color(255, 179, 39));
        jblMosTel.setText("3232951780");
        jblMosTel.setFocusable(false);

        jblMosFeLle.setBackground(new java.awt.Color(255, 255, 255));
        jblMosFeLle.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jblMosFeLle.setForeground(new java.awt.Color(255, 179, 39));
        jblMosFeLle.setText("11 01 89");
        jblMosFeLle.setFocusable(false);

        jblMosHoLle.setBackground(new java.awt.Color(255, 255, 255));
        jblMosHoLle.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jblMosHoLle.setForeground(new java.awt.Color(255, 179, 39));
        jblMosHoLle.setText("10:55 ");
        jblMosHoLle.setFocusable(false);

        jblMosFeSa.setBackground(new java.awt.Color(255, 255, 255));
        jblMosFeSa.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jblMosFeSa.setForeground(new java.awt.Color(255, 179, 39));
        jblMosFeSa.setText("12 01 89");
        jblMosFeSa.setFocusable(false);

        jblValNo.setBackground(new java.awt.Color(255, 255, 255));
        jblValNo.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jblValNo.setForeground(new java.awt.Color(255, 179, 39));
        jblValNo.setText("50,000");
        jblValNo.setFocusable(false);

        jblValTo.setBackground(new java.awt.Color(255, 255, 255));
        jblValTo.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jblValTo.setForeground(new java.awt.Color(255, 179, 39));
        jblValTo.setText("150.000");
        jblValTo.setFocusable(false);

        jblIdRes.setBackground(new java.awt.Color(255, 255, 255));
        jblIdRes.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jblIdRes.setForeground(new java.awt.Color(255, 179, 39));
        jblIdRes.setFocusable(false);

        jlbVerificacion.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jlbVerificacion.setForeground(new java.awt.Color(255, 255, 255));
        jlbVerificacion.setText("No Verificado");
        jlbVerificacion.setFocusable(false);

        jLabel16.setForeground(new java.awt.Color(255, 179, 39));
        jLabel16.setText("Documento     ");

        jlbDocumento.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbDocumento.setForeground(new java.awt.Color(255, 179, 39));
        jlbDocumento.setText("1085275894");

        jLabel14.setBackground(new java.awt.Color(255, 179, 39));
        jLabel14.setForeground(new java.awt.Color(255, 179, 39));
        jLabel14.setText(":");
        jLabel14.setFocusable(false);

        jLabel15.setBackground(new java.awt.Color(255, 179, 39));
        jLabel15.setForeground(new java.awt.Color(255, 179, 39));
        jLabel15.setText(":");
        jLabel15.setFocusable(false);

        jLabel18.setBackground(new java.awt.Color(255, 179, 39));
        jLabel18.setForeground(new java.awt.Color(255, 179, 39));
        jLabel18.setText(":");
        jLabel18.setFocusable(false);

        jLabel19.setBackground(new java.awt.Color(255, 179, 39));
        jLabel19.setForeground(new java.awt.Color(255, 179, 39));
        jLabel19.setText(":");
        jLabel19.setFocusable(false);

        jLabel20.setBackground(new java.awt.Color(255, 179, 39));
        jLabel20.setForeground(new java.awt.Color(255, 179, 39));
        jLabel20.setText(":");
        jLabel20.setFocusable(false);

        jLabel21.setBackground(new java.awt.Color(255, 179, 39));
        jLabel21.setForeground(new java.awt.Color(255, 179, 39));
        jLabel21.setText(":");
        jLabel21.setFocusable(false);

        jLabel22.setBackground(new java.awt.Color(255, 179, 39));
        jLabel22.setForeground(new java.awt.Color(255, 179, 39));
        jLabel22.setText(":");
        jLabel22.setFocusable(false);

        jLabel23.setBackground(new java.awt.Color(255, 179, 39));
        jLabel23.setForeground(new java.awt.Color(255, 179, 39));
        jLabel23.setText(":");
        jLabel23.setFocusable(false);

        jLabel24.setBackground(new java.awt.Color(255, 179, 39));
        jLabel24.setForeground(new java.awt.Color(255, 179, 39));
        jLabel24.setText(":");
        jLabel24.setFocusable(false);

        javax.swing.GroupLayout jpnVerificarLayout = new javax.swing.GroupLayout(jpnVerificar);
        jpnVerificar.setLayout(jpnVerificarLayout);
        jpnVerificarLayout.setHorizontalGroup(
            jpnVerificarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnVerificarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpnVerificarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpnVerificarLayout.createSequentialGroup()
                        .addGroup(jpnVerificarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jpnVerificarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpnVerificarLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jblMosTel, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpnVerificarLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jblMosApe, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(7, 7, 7))
                    .addGroup(jpnVerificarLayout.createSequentialGroup()
                        .addGroup(jpnVerificarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpnVerificarLayout.createSequentialGroup()
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jlbDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jpnVerificarLayout.createSequentialGroup()
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jblMosFeSa, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jpnVerificarLayout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jblMosHoLle, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jblIdRes, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jpnVerificarLayout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jblMosFeLle, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jpnVerificarLayout.createSequentialGroup()
                                .addGroup(jpnVerificarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jpnVerificarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jpnVerificarLayout.createSequentialGroup()
                                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jblValTo, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jpnVerificarLayout.createSequentialGroup()
                                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jblValNo, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jpnVerificarLayout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jblMosNom, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jlbVerificacion, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jpnVerificarLayout.setVerticalGroup(
            jpnVerificarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpnVerificarLayout.createSequentialGroup()
                .addComponent(jlbVerificacion, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addGroup(jpnVerificarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jblMosNom)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jpnVerificarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jblMosApe)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnVerificarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlbDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jpnVerificarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jblMosTel)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnVerificarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jblMosFeLle)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnVerificarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jblMosHoLle)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnVerificarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jblMosFeSa)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnVerificarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jblValNo)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnVerificarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jblValTo)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jblIdRes, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel2.add(jpnVerificar, new org.netbeans.lib.awtextra.AbsoluteConstraints(9, 317, 275, 260));

        jPanel3.setBackground(new java.awt.Color(0, 102, 102));

        jLabel13.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("FECHA:");

        jdchFechaHabs.setBackground(new java.awt.Color(255, 179, 39));
        jdchFechaHabs.setDateFormatString("EEE  dd-MMMM-yyyy");
        jdchFechaHabs.setFocusable(false);
        jdchFechaHabs.setOpaque(false);
        jdchFechaHabs.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jdchFechaHabsPropertyChange(evt);
            }
        });

        btnHoy.setBackground(new java.awt.Color(255, 179, 39));
        btnHoy.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        btnHoy.setForeground(new java.awt.Color(0, 51, 51));
        btnHoy.setText("HOY");
        btnHoy.setBorder(null);
        btnHoy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHoyActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("HORA:");

        lblRoll.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblRoll.setForeground(new java.awt.Color(255, 255, 255));

        txtNombreUsuario.setEditable(false);
        txtNombreUsuario.setFocusable(false);

        txtIdTurno.setEditable(false);
        txtIdTurno.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        txtIdTurno.setFocusable(false);

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("TURNO:");

        txtHoraRec.setEditable(false);
        txtHoraRec.setBorder(null);
        txtHoraRec.setFocusable(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jdchFechaHabs, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnHoy, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtHoraRec, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblRoll, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNombreUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtIdTurno, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jdchFechaHabs, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnHoy, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtHoraRec, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblRoll, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtNombreUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtIdTurno, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );

        jPanel4.setBackground(new java.awt.Color(0, 102, 102));

        btnSalir.setBackground(new java.awt.Color(255, 179, 39));
        btnSalir.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        btnSalir.setText("SALIR");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(255, 255, 255));
        jLabel26.setText("HotelesMas.com");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnSalir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        btnSiguiente.setBackground(new java.awt.Color(0, 51, 51));
        btnSiguiente.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        btnSiguiente.setForeground(new java.awt.Color(255, 255, 255));
        btnSiguiente.setText("SIGUIENTE");
        btnSiguiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSiguienteActionPerformed(evt);
            }
        });

        btnAtraz.setBackground(new java.awt.Color(0, 51, 51));
        btnAtraz.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        btnAtraz.setForeground(new java.awt.Color(255, 255, 255));
        btnAtraz.setText("ATRAS");
        btnAtraz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtrazActionPerformed(evt);
            }
        });

        jPanel5.setBackground(new java.awt.Color(0, 102, 102));

        jLabel17.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel17.setText("USUARIO:");

        jlbUsuario.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jlbUsuario.setForeground(new java.awt.Color(255, 255, 255));
        jlbUsuario.setText("carlos1909");

        jlbNombreHotel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jlbNombreHotel.setForeground(new java.awt.Color(255, 255, 255));
        jlbNombreHotel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlbNombreHotel.setText("Doral Plaza");

        jlbSogan.setForeground(new java.awt.Color(255, 255, 255));
        jlbSogan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlbSogan.setText("Un cinco estrellas, a precio de una!");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlbNombreHotel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jlbUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jlbSogan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jlbNombreHotel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jlbSogan)
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlbUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pnlVerde1.setBackground(new java.awt.Color(0, 153, 0));

        javax.swing.GroupLayout pnlVerde1Layout = new javax.swing.GroupLayout(pnlVerde1);
        pnlVerde1.setLayout(pnlVerde1Layout);
        pnlVerde1Layout.setHorizontalGroup(
            pnlVerde1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 14, Short.MAX_VALUE)
        );
        pnlVerde1Layout.setVerticalGroup(
            pnlVerde1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 51, 51));
        jLabel2.setText("Habitaciones Libres");

        pnlVerde.setBackground(new java.awt.Color(255, 179, 39));

        javax.swing.GroupLayout pnlVerdeLayout = new javax.swing.GroupLayout(pnlVerde);
        pnlVerde.setLayout(pnlVerdeLayout);
        pnlVerdeLayout.setHorizontalGroup(
            pnlVerdeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 14, Short.MAX_VALUE)
        );
        pnlVerdeLayout.setVerticalGroup(
            pnlVerdeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );

        jLabel12.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 51, 51));
        jLabel12.setText("Habitaciones Reservadas");

        pnlRojo.setBackground(new java.awt.Color(255, 53, 32));

        javax.swing.GroupLayout pnlRojoLayout = new javax.swing.GroupLayout(pnlRojo);
        pnlRojo.setLayout(pnlRojoLayout);
        pnlRojoLayout.setHorizontalGroup(
            pnlRojoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 14, Short.MAX_VALUE)
        );
        pnlRojoLayout.setVerticalGroup(
            pnlRojoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );

        jLabel25.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(0, 51, 51));
        jLabel25.setText("Habitaciones Ocupadas");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(pnlVerde1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(pnlVerde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(pnlRojo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jpnHabitaciones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnAtraz, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnSiguiente, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(20, 20, 20))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnSiguiente, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAtraz, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jpnHabitaciones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlVerde1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlVerde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlRojo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

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

    private void btnHoyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHoyActionPerformed
        jdchFechaHabs.setDate(new Date());
    }//GEN-LAST:event_btnHoyActionPerformed

    private void btnSiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSiguienteActionPerformed
        Date fechaActual = jdchFechaHabs.getDate();
        Date fechaSiguiente = agregarDiasAFecha(fechaActual, 1);
        jdchFechaHabs.setDate(fechaSiguiente);
    }//GEN-LAST:event_btnSiguienteActionPerformed

    private void btnAtrazActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtrazActionPerformed
        Date fechaActual = jdchFechaHabs.getDate();
        Date fechaAnterior = agregarDiasAFecha(fechaActual, -1);
        jdchFechaHabs.setDate(fechaAnterior);
    }//GEN-LAST:event_btnAtrazActionPerformed

    private void jdchFechaHabsPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jdchFechaHabsPropertyChange
        if ("date".equals(evt.getPropertyName()) && botonesHabs != null) {
            Date fecha = jdchFechaHabs.getDate();
            Date fechaactual = new Date();

            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");

            if (formato.format(fecha).equals(formato.format(fechaactual))) {
                consulta = "SELECT Num_Habitacion,Estado_Habitacion FROM habitaciones WHERE Fk_Id_Hotel = ?";
                is_queryChange = false;
            } else {
                consulta = "SELECT Num_Habitacion,"
                        + " CASE"
                        + "     WHEN Id_Habitacion IN (SELECT Fk_Id_Habitacion FROM check_in WHERE Fecha_Ingreso<=? and Fecha_Salida>=? ) THEN 'Ocupado'"
                        + "     WHEN Num_Habitacion IN (SELECT Num_Habitacion FROM reserva WHERE Fecha_Llegada<=? and Fecha_Salida>=? AND Estado_Reserva = 'Vigente') THEN 'Reservado'"
                        + "     ELSE 'Libre'"
                        + " END Estado_Habitacion"
                        + " FROM habitaciones WHERE Fk_Id_Hotel = ?";
                is_queryChange = true;
            }
            activarHabitaciones(jdchFechaHabs.getDate(), idHotel);
        }
    }//GEN-LAST:event_jdchFechaHabsPropertyChange

    private void btnVentasComisionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVentasComisionesActionPerformed
        VentasComisiones ir = new VentasComisiones(this, true, usus, hotel);
        ir.setVisible(true);
    }//GEN-LAST:event_btnVentasComisionesActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        inicio ir = new inicio();
        ir.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnSalirActionPerformed

    private void btnGastosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGastosMouseClicked
        int idTurno = Integer.parseInt(txtIdTurno.getText());
        Gastos ir = new Gastos(new JFrame(), true, idTurno);
        ir.setVisible(true);
    }//GEN-LAST:event_btnGastosMouseClicked

    private void btnOperarTurnoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnOperarTurnoMouseClicked
        OperarTurno ir = new OperarTurno(this, true, usus, hotel);
        ir.setVisible(true);

    }//GEN-LAST:event_btnOperarTurnoMouseClicked

    private void btnIngresoExtraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIngresoExtraActionPerformed
        IngresoExtra abrir = new IngresoExtra(this, true, usus, hotel);
        abrir.setVisible(true);
    }//GEN-LAST:event_btnIngresoExtraActionPerformed

    private void btnInformeContableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnInformeContableMouseClicked
        InformeContable ir = new InformeContable(this, true, usus, hotel);
        ir.setVisible(true);
    }//GEN-LAST:event_btnInformeContableMouseClicked

    private void btnRegistrarUsuarioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRegistrarUsuarioMouseClicked
        FrmRegistrarUsuarios ir = new FrmRegistrarUsuarios(this, true, hotel, false);
        ir.setVisible(true);
    }//GEN-LAST:event_btnRegistrarUsuarioMouseClicked

    private void btnRegistrarUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarUsuarioActionPerformed
        FrmRegistrarUsuarios ir = new FrmRegistrarUsuarios(this, true, hotel, false);
        ir.setVisible(true);
    }//GEN-LAST:event_btnRegistrarUsuarioActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAtraz;
    private javax.swing.JButton btnGastos;
    private javax.swing.JButton btnHoy;
    private javax.swing.JButton btnInformeContable;
    private javax.swing.JButton btnIngresoExtra;
    private javax.swing.JButton btnOperarTurno;
    private javax.swing.JButton btnRegistrarUsuario;
    private javax.swing.JButton btnSalir;
    private javax.swing.JButton btnSiguiente;
    private javax.swing.JButton btnVentasComisiones;
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
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
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
    private javax.swing.JLabel jblIdRes;
    private javax.swing.JLabel jblMosApe;
    private javax.swing.JLabel jblMosFeLle;
    private javax.swing.JLabel jblMosFeSa;
    private javax.swing.JLabel jblMosHoLle;
    private javax.swing.JLabel jblMosNom;
    private javax.swing.JLabel jblMosTel;
    private javax.swing.JLabel jblValNo;
    private javax.swing.JLabel jblValTo;
    public com.toedter.calendar.JDateChooser jdchFechaHabs;
    private javax.swing.JLabel jlbDocumento;
    private javax.swing.JLabel jlbNombreHotel;
    private javax.swing.JLabel jlbSogan;
    public javax.swing.JLabel jlbUsuario;
    private javax.swing.JLabel jlbVerificacion;
    private javax.swing.JPanel jpnHabitaciones;
    private javax.swing.JPanel jpnVerificar;
    public javax.swing.JLabel lblRoll;
    private javax.swing.JPanel pnlRojo;
    private javax.swing.JPanel pnlVerde;
    private javax.swing.JPanel pnlVerde1;
    private javax.swing.JTextField txtHoraRec;
    public javax.swing.JTextField txtIdTurno;
    private javax.swing.JTextField txtNombreUsuario;
    // End of variables declaration//GEN-END:variables
}
