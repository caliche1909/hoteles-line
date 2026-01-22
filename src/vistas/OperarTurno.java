package vistas;

import conectar.Canectar;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import modelo.Hotel;
import modelo.Turno;
import modelo.UsuarioOperando;

public final class OperarTurno extends javax.swing.JDialog {

    Canectar con = new Canectar();
    PreparedStatement ps;
    ResultSet rs;
    int IdUsuario;
    UsuarioOperando usus;
    Hotel hotel;

    public OperarTurno(java.awt.Frame parent, boolean modal, UsuarioOperando usus, Hotel hotel) {
        super(parent, modal);
        this.usus = usus;
        this.hotel = hotel;
        initComponents();

        btnOperarTurno.addMouseListener(mouseAdapter);
        btnTerminarTurno.addMouseListener(mouseAdapter);
        btnCerrarCaja.addMouseListener(mouseAdapter);
        btnNuevoGasto.addMouseListener(mouseAdapter);

        txtNomCuadre.setText(usus.getNombres().split(" ")[0] + " " + usus.getApellidos().split(" ")[0]);
        jblRolCuadre.setText(usus.getRoll_usuarios());
        jpnTiposPago.setVisible(false);
        jpnDatosTurno.setVisible(false);
        this.setLocationRelativeTo(null);
        jdchFechaCuadre.setDate(new Date());
        aplicarFormatoPuntosMil(txtTotalGastos);
        aplicarFormatoPuntosMil(txtIngresosExtra);
        btnTerminarTurno.setVisible(false);
        btnCerrarCaja.setVisible(false);
        jpnRecFinal.setVisible(false);     
        btnNuevoGasto.setVisible(false);
        
    }

    MouseAdapter mouseAdapter = new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            JButton button = (JButton) e.getSource();
            button.setBackground(new Color(255, 179, 39));
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        public void mouseExited(MouseEvent e) {
            JButton button = (JButton) e.getSource();
            button.setBackground(new Color(255, 219, 95));
        }
    };

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

    private Date agregarDiasAFecha(Date fecha, int dias) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.add(Calendar.DATE, dias);
        return calendar.getTime();
    }

    public String[] obtenerGastoRecaudos(Date fechaCuadre) {
        String[] gastoRecaudo = new String[2];
        Connection conex = con.conexion();
        PreparedStatement ps = null;
        ResultSet rs = null;
        java.sql.Date sqlDate = new java.sql.Date(fechaCuadre.getTime());
        String sqlGastRec = "SELECT Total_Gastos_Dia, Recaudo_Final_Dia FROM cuadre_diario WHERE DATE (Fecha_Cuadre) = ?";
        try {
            ps = conex.prepareStatement(sqlGastRec);
            ps.setDate(1, sqlDate);
            rs = ps.executeQuery();
            while (rs.next()) {
                String gasto = rs.getBigDecimal("Total_Gastos_Dia").toString();
                gastoRecaudo[0] = gasto.split("\\.")[0];
                String recaudo = rs.getBigDecimal("Recaudo_Final_Dia").toString();
                gastoRecaudo[1] = recaudo.split("\\.")[0];
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener gastos y recaudos diarios : " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

        }
        return gastoRecaudo;

    }

    public void contarTurnos(Date fecha, int idHotel) {
        // Limpiar el JComboBox
        cbxNumTurno.removeAllItems();

        // Agregar el mensaje de selección al inicio
        cbxNumTurno.addItem("Seleccione");
        // Convertir java.util.Date a java.sql.Date
        java.sql.Date sqlDate = new java.sql.Date(fecha.getTime());
        int totalTurnos = 0;
        String sql = "SELECT Id_Turno FROM turno WHERE DATE(Inicio) = ? AND Fk_Id_Hotel = ?";
        try ( Connection conex = con.conexion();  PreparedStatement ps = conex.prepareStatement(sql);) {

            ps.setDate(1, sqlDate);
            ps.setInt(2, idHotel);
            try ( ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    int idTurno = rs.getInt("Id_Turno");
                    // Agregar cada Id_Turno al JComboBox
                    cbxNumTurno.addItem(Integer.toString(idTurno));
                    totalTurnos++;
                }
            }

            // Ponga el total de turnos en txtCantTurnos
            txtCantTurnos.setText(String.valueOf(totalTurnos));
            if (!txtCantTurnos.getText().isEmpty() && !txtCantTurnos.getText().equals("0")) {
                cbxNumTurno.setBackground(new Color(255, 179, 39));
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pued contar los turnos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Turno obtenerTurno(int idTurno) {
        Turno turno = new Turno();
        String NomHotel = "";
        String RntHotel = "";
        String Nombres = "";
        String Apellidos = "";
        String RollUsuario = "";
        int idHoteles = 0;
        String sql = "SELECT t.Inicio, t.Fin, t.Estado_Turno, t.Criterio_Caja, h.Id_Hoteles, h.Nombre_Hotel, h.Rnt_Hotel, u.Id_Usuario, u.Nombres, u.Apellidos, tu.Roll_Usuario "
                + "FROM turno t "
                + "JOIN hoteles h ON t.Fk_Id_Hotel = h.Id_Hoteles "
                + "JOIN usuarios u ON t.Fk_Id_Usuario = u.Id_Usuario "
                + "JOIN tipo_usuario tu ON u.Id_Tipo = tu.Id_Tipo_Usuario "
                + "WHERE t.Id_Turno = ?";

        try ( Connection conex = con.conexion();  PreparedStatement sentencia = conex.prepareStatement(sql);) {
            sentencia.setInt(1, idTurno);

            try ( ResultSet rs = sentencia.executeQuery();) {
                if (rs.next()) {
                    turno.setInicio(rs.getTimestamp("Inicio").toLocalDateTime());
                    turno.setFin(rs.getTimestamp("Fin").toLocalDateTime());
                    turno.setEstadoTurno(rs.getString("Estado_Turno"));
                    turno.setCriterioCaja(rs.getString("Criterio_Caja"));
                    idHoteles = (rs.getInt("Id_Hoteles"));
                    NomHotel = (rs.getString("Nombre_Hotel"));
                    RntHotel = (rs.getString("Rnt_Hotel"));
                    IdUsuario = (rs.getInt("Id_Usuario"));
                    Nombres = (rs.getString("Nombres"));
                    Apellidos = (rs.getString("Apellidos"));
                    RollUsuario = (rs.getString("Roll_Usuario"));
                }
            }
            jlbFechaInicio.setText(turno.getInicio().format(DateTimeFormatter.ofPattern("EEE dd-MMMM-yyyy / HH:mm")));
            jlbFechaFin.setText(turno.getFin().format(DateTimeFormatter.ofPattern("EEE dd-MMMM-yyyy / HH:mm")));
            jlbEstadoTurno.setText(turno.getEstadoTurno());
            jlbNombreHotel.setText("HOTEL " + NomHotel.toUpperCase());
            jlbRntHotel.setText("RNT: " + RntHotel);
            jlbIdHoteles.setText(Integer.toString(idHoteles));
            String primerNombre = Nombres.substring(0, Nombres.indexOf(' ') != -1 ? Nombres.indexOf(' ') : Nombres.length());
            String primerApellido = Apellidos.substring(0, Apellidos.indexOf(' ') != -1 ? Apellidos.indexOf(' ') : Apellidos.length());
            jblNombres.setText(primerNombre + " " + primerApellido);
            jblRollUsuario.setText(RollUsuario);
            jblCriterioCaja.setText(turno.getCriterioCaja());

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener el turno: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return turno;
    }

    public void fillTable(int idTurno) {
        Connection conex = null;
        PreparedStatement sentencia = null;
        ResultSet rs = null;

        try {
            // Crear la conexión con la base de datos
            conex = con.conexion();

            // Crear la consulta SQL
            String sql = "SELECT "
                    + "check_in.Num_Habitacion, "
                    + "cliente.Nombres, "
                    + "cliente.Apellidos, "
                    + "cliente.Num_Documento, "
                    + "cliente.Telefono, "
                    + "check_in.Fecha_Ingreso, "
                    + "check_in.Fecha_Salida, "
                    + "contable.Tipo_Pago,"
                    + "contable.Cant_Noches, "
                    + "contable.Valor_Habitacion, "
                    + "contable.Total_Pago, "
                    + "contable.Comision, "
                    + "contable.Total_Neto "
                    + "FROM contable "
                    + "JOIN cliente ON contable.Fk_Id_Cliente = cliente.Id_Cliente "
                    + "JOIN check_in ON contable.Fk_Id_Check_In = check_in.Id_Reserva "
                    + "WHERE contable.Fk_Id_Turno = ? "
                    + "ORDER BY CAST(check_in.Num_Habitacion AS UNSIGNED)";

            sentencia = conex.prepareStatement(sql);
            sentencia.setInt(1, idTurno);

            // Ejecutar la consulta SQL
            rs = sentencia.executeQuery();

            // Limpiar la tabla
            DefaultTableModel model = (DefaultTableModel) tblCuadre.getModel();
            model.setRowCount(0);
            int totalHabs = 19;
            int cantHabsV = 0;
            int HabsQuedadas = 0;

            double totalEfectivo = 0.0;
            double totalNeki = 0.0;
            double totalBancolombia = 0.0;
            double totalDatafono = 0.0;
            double totalDavivienda = 0.0;
            double totalDaviplata = 0.0;

            // Llenar la tabla con los datos obtenidos
            while (rs.next()) {
                Object[] row = new Object[13];
                row[0] = rs.getInt("Num_Habitacion");
                row[1] = rs.getString("Nombres");
                row[2] = rs.getString("Apellidos");
                row[3] = rs.getString("Num_Documento");
                row[4] = rs.getString("Telefono");
                row[5] = rs.getString("Fecha_Ingreso");
                row[6] = rs.getString("Fecha_Salida");
                row[7] = rs.getInt("Cant_Noches");
                row[8] = rs.getString("Tipo_Pago");
                row[9] = rs.getDouble("Valor_Habitacion");
                row[10] = rs.getDouble("Total_Pago");
                row[11] = rs.getDouble("Comision");
                row[12] = rs.getDouble("Total_Neto");

                model.addRow(row);
                cantHabsV++;

                String tipoPago = rs.getString("Tipo_Pago");
                double totalPago = rs.getDouble("Total_Pago");
                switch (tipoPago) {
                    case "Efectivo":
                        totalEfectivo += totalPago;
                        break;
                    case "Neki":
                        totalNeki += totalPago;
                        break;
                    case "Bancolombia":
                        totalBancolombia += totalPago;
                        break;
                    case "Datafono Bold":
                        totalDatafono += totalPago;
                        break;
                    case "Davivienda":
                        totalDavivienda += totalPago;
                        break;
                    case "DaviPlata":
                        totalDaviplata += totalPago;
                        break;
                }
            }

            txtEfectivos.setText(String.format("%.0f", totalEfectivo));
            formatoPuntosMil(txtEfectivos);

            txtCuadreCajaEfectivo.setText(String.format("%.0f", totalEfectivo));
            formatoPuntosMil(txtCuadreCajaEfectivo);

            txtNekis.setText(String.format("%.0f", totalNeki));
            formatoPuntosMil(txtNekis);
            txtBancolombia.setText(String.format("%.0f", totalBancolombia));
            formatoPuntosMil(txtBancolombia);
            txtDatafono.setText(String.format("%.0f", totalDatafono));
            formatoPuntosMil(txtDatafono);
            txtDavivienda.setText(String.format("%.0f", totalDavivienda));
            formatoPuntosMil(txtDavivienda);
            txtDaviplata.setText(String.format("%.0f", totalDaviplata));
            formatoPuntosMil(txtDaviplata);

            HabsQuedadas = totalHabs - cantHabsV;
            if (HabsQuedadas < 0) {
                txtHabsQuedadas.setText("0");
            } else {
                txtHabsQuedadas.setText(Integer.toString(HabsQuedadas));
            }
            txtHabsVendidas.setText(Integer.toString(cantHabsV));

            tblCuadre.setModel(model);
            double sumaValorTotal = 0;
            double sumaComision = 0;
            double valorPromedio = 0;

            for (int i = 0; i < model.getRowCount(); i++) {
                sumaValorTotal += (double) model.getValueAt(i, 10);
            }
            txtTotalVentas.setText(String.format("%.0f", sumaValorTotal));
            formatoPuntosMil(txtTotalVentas);

            for (int i = 0; i < tblCuadre.getRowCount(); i++) {
                sumaComision += Double.parseDouble(tblCuadre.getValueAt(i, 11).toString());
            }
            txtTotalComision.setText(String.format("%.0f", sumaComision));
            formatoPuntosMil(txtTotalComision);

            double neto = sumaValorTotal - sumaComision;
            txtTotalNeto.setText(String.format("%.0f", neto));
            formatoPuntosMil(txtTotalNeto);

            if (sumaValorTotal > cantHabsV) {
                valorPromedio = sumaValorTotal / cantHabsV;
                txtValorPromedio.setText(String.format("%.0f", valorPromedio));
                formatoPuntosMil(txtValorPromedio);
            } else {
                txtValorPromedio.setText("50,000");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al gestionar datos de la tabla: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, "Error al cerrar recursos RS: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            if (sentencia != null) {
                try {
                    sentencia.close();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, "Error al cerrar recursos PS: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            if (conex != null) {
                try {
                    conex.close();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, "Error al cerrar recursos CONEX: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public void SumarGastosIngresos(int turno, JTextField gastos, JTextField ingresos) {
        Connection conex = con.conexion();
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql1 = "SELECT SUM(Valor_Gasto) FROM gastos WHERE Fk_Id_Turno = ?";
        String sql2 = "SELECT SUM(Valor_Ingreso) FROM ingreso_extra WHERE Fk_Id_Turno = ?";
        try {
            ps = conex.prepareStatement(sql1);
            ps.setInt(1, turno);
            rs = ps.executeQuery();
            if (rs.next()) {
                double valorGasto = rs.getDouble(1);
                gastos.setText(String.format("%.0f", valorGasto));
                formatoPuntosMil(gastos);
            }
            rs.close();
            ps.close();

            ps = conex.prepareStatement(sql2);
            ps.setInt(1, turno);
            rs = ps.executeQuery();
            if (rs.next()) {
                double valorIngresos = rs.getDouble(1);
                ingresos.setText(String.format("%.0f", valorIngresos));
                formatoPuntosMil(ingresos);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener las sumatorias: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error al cerrar la conexion: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    public void terminarTurno() {
        String[] opciones = {"SI", "NO"};
        int seleccion = JOptionPane.showOptionDialog(
                null,
                "Seguro desea terminar este turno ? ",
                "Seleccione una opción",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );
        switch (seleccion) {
            case 0://Si
                int idT = Integer.parseInt(txtIdTurno.getText());
                Canectar con = new Canectar();
                if (con.terminarTurno(idT)) {
                    JOptionPane.showMessageDialog(this, """
                                                        MUCHAS GRACIAS POR TODAS TUS LABORES, 
                                                        ESPERAMOS SEGUIR CONTANDO CONTIGO
                                                        QUE TENGAS UN EXCELENTE DIA!                                                        
                                                        """);
                    Recepciones ir = new Recepciones(usus, hotel);
                    ir.cerrarVentana();
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al terminar el turno, por favor intente de nuevo.");
                }
                break;
            case 1://No
                // No se realiza ninguna accion
                break;
            default:
                // El usuario cerró el JOptionPane sin seleccionar ninguna opción
                break;
        }
    }

    public void CerrarCaja() {
        double proPerdidaDia;
        String[] opciones = {"SI", "NO"};
        int seleccion = JOptionPane.showOptionDialog(
                null,
                "Seguro desea cerrar caja ? ",
                "Seleccione una opción",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );
        switch (seleccion) {
            case 0://Si
                if (txtTotalGastos.getText().isEmpty()) {
                    String[] opcion = {"CONFIRMAR", "CANCELAR"};
                    int selecciones = JOptionPane.showOptionDialog(
                            null,
                            "Los gastos de este turno estan en cero '0'  ",
                            "Confirmar gastos",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            null,
                            opcion,
                            opcion[0]
                    );
                    switch (selecciones) {
                        case 0://confirmar
                            txtTotalGastos.setText("0");
                            txtVentajaFinal.setText(txtTotalNeto.getText());
                            break;
                        case 1://cancelar

                            return;
                        default:
                            // El usuario cerró el JOptionPane sin seleccionar ninguna opción
                            break;
                    }
                }
                //LocalDate xInicio = jdchFechaCuadre.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                Date date = jdchFechaCuadre.getDate();
                LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                double totalDia = Double.parseDouble(txtTotalVentas.getText().replace(",", ""));
                double totalComision = Double.parseDouble(txtTotalComision.getText().replace(",", ""));
                double netoDia = Double.parseDouble(txtTotalNeto.getText().replace(",", ""));
                double gastoDia = Double.parseDouble(txtTotalGastos.getText().replace(",", ""));
                double ingresoExDia = Double.parseDouble(txtIngresosExtra.getText().replace(",", ""));
                double recaudoDia = Double.parseDouble(txtVentajaFinal.getText().replace(",", ""));
                int habsVendidas = Integer.parseInt(txtHabsVendidas.getText());
                int habsQuedadas = Integer.parseInt(txtHabsQuedadas.getText());
                double promPerdida = Double.parseDouble(txtValorPromedio.getText().replace(",", ""));
                int IdHotel = Integer.parseInt(jlbIdHoteles.getText());

                if (txtHabsQuedadas.getText().equals("0")) {
                    proPerdidaDia = 0.0;
                } else {
                    proPerdidaDia = promPerdida * habsQuedadas;
                }
                int idT = Integer.parseInt(txtIdTurno.getText());

                Canectar con = new Canectar();
                if (con.insertarDatosCaja(localDate, totalDia, totalComision, netoDia, gastoDia, ingresoExDia, recaudoDia,
                        habsVendidas, habsQuedadas, promPerdida, proPerdidaDia, idT, IdUsuario, IdHotel)) {
                    if (con.pasarCajaCerrada(idT)) {
                        JOptionPane.showMessageDialog(this, "Cierre de caja exitoso");
                        txtIngresosExtra.setFocusable(false);
                        txtTotalGastos.setFocusable(false);
                        btnCerrarCaja.setVisible(false);
                        jblCriterioCaja.setText("Cerrada");
                    }
                }
                break;
            case 1://No
                // No se realiza ninguna accion
                break;
            default:
                // El usuario cerró el JOptionPane sin seleccionar ninguna opción
                break;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCuadre = new javax.swing.JTable();
        jdchFechaCuadre = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        btnSiguiente = new javax.swing.JButton();
        btnTerminarTurno = new javax.swing.JButton();
        btnCerrarCaja = new javax.swing.JButton();
        btnAnterior = new javax.swing.JButton();
        btnHoy = new javax.swing.JButton();
        jblRolCuadre = new javax.swing.JLabel();
        txtNomCuadre = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtIdTurno = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        txtCantTurnos = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        cbxNumTurno = new javax.swing.JComboBox<>();
        jpnDatosTurno = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        btnOperarTurno = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jpnNomIdHotel = new javax.swing.JPanel();
        jlbRntHotel = new javax.swing.JLabel();
        jlbNombreHotel = new javax.swing.JLabel();
        jlbIdHoteles1 = new javax.swing.JLabel();
        jlbIdHoteles = new javax.swing.JLabel();
        jpnDatosTurnopqño = new javax.swing.JPanel();
        jblNombres = new javax.swing.JLabel();
        jblRollUsuario = new javax.swing.JLabel();
        jlbFechaFin = new javax.swing.JLabel();
        jblCriterioCaja = new javax.swing.JLabel();
        jlbFechaInicio = new javax.swing.JLabel();
        jlbEstadoTurno = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jpnValoresFinal = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        txtTotalComision = new javax.swing.JTextField();
        txtTotalNeto = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txtTotalVentas = new javax.swing.JTextField();
        jpnRecFinal = new javax.swing.JPanel();
        txtVentajaFinal = new javax.swing.JTextField();
        txtTotalGastos = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtIngresosExtra = new javax.swing.JTextField();
        jpnTiposPago = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        txtEfectivos = new javax.swing.JTextField();
        txtNekis = new javax.swing.JTextField();
        txtBancolombia = new javax.swing.JTextField();
        txtDatafono = new javax.swing.JTextField();
        txtDavivienda = new javax.swing.JTextField();
        txtDaviplata = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jpnDatosHabs = new javax.swing.JPanel();
        jlbHabsVendidas = new javax.swing.JLabel();
        txtHabsVendidas = new javax.swing.JTextField();
        jlbHabsQuedadas = new javax.swing.JLabel();
        txtHabsQuedadas = new javax.swing.JTextField();
        jlbPromedio = new javax.swing.JLabel();
        txtValorPromedio = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        btnVolver = new javax.swing.JButton();
        jpnCajaMenor = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        txtCuadreCajaEfectivo = new javax.swing.JTextField();
        txtCuadreCajaGastos = new javax.swing.JTextField();
        txtCuadreCajaSaldos = new javax.swing.JTextField();
        btnNuevoGasto = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblCuadre.setForeground(new java.awt.Color(0, 0, 0));
        tblCuadre.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Num_Hab", "Nombres", "Apellidos", "Documento", "Telefono", "Fecha_Llegada", "Fecha_Salida", "Noches", "Tipo_Pago", "Valor_Noche", "Valor_Total", "Comicion", "Valor_Neto"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblCuadre);
        if (tblCuadre.getColumnModel().getColumnCount() > 0) {
            tblCuadre.getColumnModel().getColumn(0).setMinWidth(40);
            tblCuadre.getColumnModel().getColumn(0).setMaxWidth(40);
            tblCuadre.getColumnModel().getColumn(1).setMinWidth(140);
            tblCuadre.getColumnModel().getColumn(1).setMaxWidth(140);
            tblCuadre.getColumnModel().getColumn(2).setMinWidth(140);
            tblCuadre.getColumnModel().getColumn(2).setMaxWidth(140);
            tblCuadre.getColumnModel().getColumn(3).setMinWidth(120);
            tblCuadre.getColumnModel().getColumn(3).setMaxWidth(120);
            tblCuadre.getColumnModel().getColumn(4).setMinWidth(130);
            tblCuadre.getColumnModel().getColumn(4).setMaxWidth(130);
            tblCuadre.getColumnModel().getColumn(5).setMinWidth(105);
            tblCuadre.getColumnModel().getColumn(5).setMaxWidth(105);
            tblCuadre.getColumnModel().getColumn(6).setMinWidth(90);
            tblCuadre.getColumnModel().getColumn(6).setMaxWidth(90);
            tblCuadre.getColumnModel().getColumn(7).setMinWidth(55);
            tblCuadre.getColumnModel().getColumn(7).setMaxWidth(55);
            tblCuadre.getColumnModel().getColumn(8).setMinWidth(100);
            tblCuadre.getColumnModel().getColumn(8).setMaxWidth(100);
            tblCuadre.getColumnModel().getColumn(9).setMinWidth(90);
            tblCuadre.getColumnModel().getColumn(9).setMaxWidth(90);
            tblCuadre.getColumnModel().getColumn(10).setMinWidth(85);
            tblCuadre.getColumnModel().getColumn(10).setMaxWidth(85);
            tblCuadre.getColumnModel().getColumn(11).setMinWidth(85);
            tblCuadre.getColumnModel().getColumn(11).setMaxWidth(85);
        }

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 1290, 350));

        jdchFechaCuadre.setBackground(new java.awt.Color(0, 102, 102));
        jdchFechaCuadre.setDateFormatString("EEE dd-MMM-yyyy");
        jdchFechaCuadre.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jdchFechaCuadre.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jdchFechaCuadrePropertyChange(evt);
            }
        });
        jPanel1.add(jdchFechaCuadre, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 185, 30));

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel1.setText("TURNOS: ");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 10, -1, 30));

        btnSiguiente.setBackground(new java.awt.Color(0, 102, 102));
        btnSiguiente.setForeground(new java.awt.Color(255, 255, 255));
        btnSiguiente.setText("Siguiente");
        btnSiguiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSiguienteActionPerformed(evt);
            }
        });
        jPanel1.add(btnSiguiente, new org.netbeans.lib.awtextra.AbsoluteConstraints(1210, 80, 90, -1));

        btnTerminarTurno.setBackground(new java.awt.Color(255, 219, 95));
        btnTerminarTurno.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        btnTerminarTurno.setText("TERMINAR TURNO");
        btnTerminarTurno.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnTerminarTurnoMouseClicked(evt);
            }
        });
        jPanel1.add(btnTerminarTurno, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 740, 190, 40));

        btnCerrarCaja.setBackground(new java.awt.Color(255, 219, 95));
        btnCerrarCaja.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        btnCerrarCaja.setText("CERRAR CAJA");
        btnCerrarCaja.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCerrarCajaMouseClicked(evt);
            }
        });
        jPanel1.add(btnCerrarCaja, new org.netbeans.lib.awtextra.AbsoluteConstraints(1110, 740, 190, 40));

        btnAnterior.setBackground(new java.awt.Color(0, 102, 102));
        btnAnterior.setForeground(new java.awt.Color(255, 255, 255));
        btnAnterior.setText("Anterior");
        btnAnterior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnteriorActionPerformed(evt);
            }
        });
        jPanel1.add(btnAnterior, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 90, -1));

        btnHoy.setBackground(new java.awt.Color(0, 102, 102));
        btnHoy.setForeground(new java.awt.Color(255, 255, 255));
        btnHoy.setText("HOY");
        btnHoy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHoyActionPerformed(evt);
            }
        });
        jPanel1.add(btnHoy, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 10, -1, 30));

        jblRolCuadre.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jblRolCuadre.setText("RECEPCIONISTA");
        jPanel1.add(jblRolCuadre, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 10, 110, 30));

        txtNomCuadre.setEditable(false);
        txtNomCuadre.setBackground(new java.awt.Color(0, 102, 102));
        txtNomCuadre.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtNomCuadre.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(txtNomCuadre, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 10, 160, 30));

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel2.setText("TURNO N°");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1170, 10, 70, 30));

        txtIdTurno.setEditable(false);
        txtIdTurno.setBackground(new java.awt.Color(0, 102, 102));
        txtIdTurno.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        txtIdTurno.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(txtIdTurno, new org.netbeans.lib.awtextra.AbsoluteConstraints(1240, 10, 60, 30));
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 1320, 10));

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel3.setText("FECHA:");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 10, -1, 30));

        txtCantTurnos.setEditable(false);
        txtCantTurnos.setBackground(new java.awt.Color(0, 102, 102));
        txtCantTurnos.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        txtCantTurnos.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(txtCantTurnos, new org.netbeans.lib.awtextra.AbsoluteConstraints(442, 10, 60, 30));

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel4.setText("CANT. TURNOS: ");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 10, -1, 30));

        cbxNumTurno.setBackground(new java.awt.Color(0, 102, 102));
        cbxNumTurno.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        cbxNumTurno.setForeground(new java.awt.Color(255, 255, 255));
        cbxNumTurno.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxNumTurnoItemStateChanged(evt);
            }
        });
        jPanel1.add(cbxNumTurno, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 10, 130, 30));

        jpnDatosTurno.setBackground(new java.awt.Color(0, 102, 102));
        jpnDatosTurno.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("DESCRIPCION DEL TURNO LABORAL:");
        jpnDatosTurno.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 200, -1));

        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("CRITERIO CAJA             :");
        jpnDatosTurno.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, 140, -1));

        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("CARGO                            :");
        jpnDatosTurno.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 140, -1));

        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("FECHA / HORA INICIO  : ");
        jpnDatosTurno.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 140, -1));

        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("FECHA / HORA FINAL   : ");
        jpnDatosTurno.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 140, -1));
        jpnDatosTurno.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 340, 20));

        btnOperarTurno.setBackground(new java.awt.Color(255, 219, 95));
        btnOperarTurno.setText("OPERAR TURNO");
        btnOperarTurno.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnOperarTurnoMouseClicked(evt);
            }
        });
        jpnDatosTurno.add(btnOperarTurno, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 220, 130, -1));

        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("RESPONSABLE             : ");
        jpnDatosTurno.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 140, -1));

        jpnNomIdHotel.setBackground(new java.awt.Color(0, 102, 102));
        jpnNomIdHotel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jlbRntHotel.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbRntHotel.setForeground(new java.awt.Color(255, 255, 255));
        jpnNomIdHotel.add(jlbRntHotel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 180, 15));

        jlbNombreHotel.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbNombreHotel.setForeground(new java.awt.Color(255, 255, 255));
        jpnNomIdHotel.add(jlbNombreHotel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 180, 15));

        jlbIdHoteles1.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbIdHoteles1.setForeground(new java.awt.Color(255, 255, 255));
        jlbIdHoteles1.setText("Id Hotel : ");
        jpnNomIdHotel.add(jlbIdHoteles1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, -1));

        jlbIdHoteles.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbIdHoteles.setForeground(new java.awt.Color(255, 255, 255));
        jpnNomIdHotel.add(jlbIdHoteles, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 30, 70, 15));

        jpnDatosTurno.add(jpnNomIdHotel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 340, 50));

        jpnDatosTurnopqño.setBackground(new java.awt.Color(0, 102, 102));
        jpnDatosTurnopqño.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jblNombres.setForeground(new java.awt.Color(255, 255, 255));
        jpnDatosTurnopqño.add(jblNombres, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 170, 15));

        jblRollUsuario.setForeground(new java.awt.Color(255, 255, 255));
        jpnDatosTurnopqño.add(jblRollUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 170, 15));

        jlbFechaFin.setForeground(new java.awt.Color(255, 255, 255));
        jpnDatosTurnopqño.add(jlbFechaFin, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 170, 15));

        jblCriterioCaja.setForeground(new java.awt.Color(255, 255, 255));
        jpnDatosTurnopqño.add(jblCriterioCaja, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 110, 170, 15));

        jlbFechaInicio.setForeground(new java.awt.Color(255, 255, 255));
        jpnDatosTurnopqño.add(jlbFechaInicio, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 170, 15));

        jlbEstadoTurno.setForeground(new java.awt.Color(255, 255, 255));
        jpnDatosTurnopqño.add(jlbEstadoTurno, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 90, 170, 15));

        jpnDatosTurno.add(jpnDatosTurnopqño, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 80, 190, 130));

        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("ESTADO DEL TURNO  : ");
        jpnDatosTurno.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 140, -1));

        jPanel1.add(jpnDatosTurno, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 460, 340, 260));

        jpnValoresFinal.setBackground(new java.awt.Color(255, 219, 95));

        jLabel9.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel9.setText("TOTAL COMISION  :    $");

        txtTotalComision.setEditable(false);
        txtTotalComision.setBackground(new java.awt.Color(255, 219, 95));
        txtTotalComision.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        txtTotalComision.setBorder(null);

        txtTotalNeto.setEditable(false);
        txtTotalNeto.setBackground(new java.awt.Color(255, 219, 95));
        txtTotalNeto.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        txtTotalNeto.setBorder(null);

        jLabel7.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel7.setText("RECAUDO NETO      :    $");

        jLabel16.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel16.setText("TOTAL VENTAS      :    $");

        txtTotalVentas.setEditable(false);
        txtTotalVentas.setBackground(new java.awt.Color(255, 219, 95));
        txtTotalVentas.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        txtTotalVentas.setBorder(null);

        javax.swing.GroupLayout jpnValoresFinalLayout = new javax.swing.GroupLayout(jpnValoresFinal);
        jpnValoresFinal.setLayout(jpnValoresFinalLayout);
        jpnValoresFinalLayout.setHorizontalGroup(
            jpnValoresFinalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnValoresFinalLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jpnValoresFinalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(4, 4, 4)
                .addGroup(jpnValoresFinalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtTotalNeto, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                    .addComponent(txtTotalComision)
                    .addComponent(txtTotalVentas))
                .addGap(47, 47, 47))
        );
        jpnValoresFinalLayout.setVerticalGroup(
            jpnValoresFinalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnValoresFinalLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jpnValoresFinalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jpnValoresFinalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalComision, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jpnValoresFinalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalNeto, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(50, 50, 50))
        );

        jPanel1.add(jpnValoresFinal, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 460, 230, 90));

        jpnRecFinal.setBackground(new java.awt.Color(255, 219, 95));

        txtVentajaFinal.setEditable(false);
        txtVentajaFinal.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N

        txtTotalGastos.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        txtTotalGastos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtTotalGastos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTotalGastosKeyReleased(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel14.setText("GASTOS DIARIOS    :   $");

        jLabel5.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel5.setText("INGRESOS EXTRA    :   $");

        jLabel17.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel17.setText("RECAUDO FINAL     :   $");

        txtIngresosExtra.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        txtIngresosExtra.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtIngresosExtra.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtIngresosExtraKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jpnRecFinalLayout = new javax.swing.GroupLayout(jpnRecFinal);
        jpnRecFinal.setLayout(jpnRecFinalLayout);
        jpnRecFinalLayout.setHorizontalGroup(
            jpnRecFinalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnRecFinalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpnRecFinalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jpnRecFinalLayout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(4, 4, 4)
                        .addComponent(txtTotalGastos, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpnRecFinalLayout.createSequentialGroup()
                        .addGroup(jpnRecFinalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpnRecFinalLayout.createSequentialGroup()
                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(1, 1, 1))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpnRecFinalLayout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(3, 3, 3)))
                        .addGroup(jpnRecFinalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtVentajaFinal, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                            .addComponent(txtIngresosExtra))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpnRecFinalLayout.setVerticalGroup(
            jpnRecFinalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnRecFinalLayout.createSequentialGroup()
                .addGroup(jpnRecFinalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalGastos, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(jpnRecFinalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIngresosExtra, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jpnRecFinalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtVentajaFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel1.add(jpnRecFinal, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 550, 230, 90));

        jpnTiposPago.setBackground(new java.awt.Color(0, 102, 102));

        jLabel20.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Efectivo:              $");

        jLabel21.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("Neki:                    $");

        jLabel22.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("Bancolombia:     $");

        jLabel23.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("Datafono BOLD:  $");

        txtEfectivos.setEditable(false);
        txtEfectivos.setBackground(new java.awt.Color(0, 102, 102));
        txtEfectivos.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        txtEfectivos.setForeground(new java.awt.Color(255, 255, 255));
        txtEfectivos.setBorder(null);
        txtEfectivos.setFocusable(false);

        txtNekis.setEditable(false);
        txtNekis.setBackground(new java.awt.Color(0, 102, 102));
        txtNekis.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        txtNekis.setForeground(new java.awt.Color(255, 255, 255));
        txtNekis.setBorder(null);
        txtNekis.setFocusable(false);

        txtBancolombia.setEditable(false);
        txtBancolombia.setBackground(new java.awt.Color(0, 102, 102));
        txtBancolombia.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        txtBancolombia.setForeground(new java.awt.Color(255, 255, 255));
        txtBancolombia.setBorder(null);
        txtBancolombia.setFocusable(false);

        txtDatafono.setEditable(false);
        txtDatafono.setBackground(new java.awt.Color(0, 102, 102));
        txtDatafono.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        txtDatafono.setForeground(new java.awt.Color(255, 255, 255));
        txtDatafono.setBorder(null);
        txtDatafono.setFocusable(false);

        txtDavivienda.setEditable(false);
        txtDavivienda.setBackground(new java.awt.Color(0, 102, 102));
        txtDavivienda.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        txtDavivienda.setForeground(new java.awt.Color(255, 255, 255));
        txtDavivienda.setBorder(null);
        txtDavivienda.setFocusable(false);

        txtDaviplata.setEditable(false);
        txtDaviplata.setBackground(new java.awt.Color(0, 102, 102));
        txtDaviplata.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        txtDaviplata.setForeground(new java.awt.Color(255, 255, 255));
        txtDaviplata.setBorder(null);
        txtDaviplata.setFocusable(false);

        jLabel24.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setText("Davivienda:         $");

        jLabel25.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setText("DaviPlata:            $");

        javax.swing.GroupLayout jpnTiposPagoLayout = new javax.swing.GroupLayout(jpnTiposPago);
        jpnTiposPago.setLayout(jpnTiposPagoLayout);
        jpnTiposPagoLayout.setHorizontalGroup(
            jpnTiposPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnTiposPagoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpnTiposPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnTiposPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtEfectivos, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDatafono, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDavivienda, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDaviplata, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jpnTiposPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtBancolombia, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtNekis, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpnTiposPagoLayout.setVerticalGroup(
            jpnTiposPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnTiposPagoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpnTiposPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEfectivos, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnTiposPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNekis, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnTiposPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBancolombia, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnTiposPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDatafono, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnTiposPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDavivienda, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnTiposPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDaviplata, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jpnTiposPago, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 490, 210, 230));

        jpnDatosHabs.setBackground(new java.awt.Color(255, 255, 255));
        jpnDatosHabs.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jlbHabsVendidas.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbHabsVendidas.setText("CANTIDAD DE HABITACIONES VENDIDAS:");
        jpnDatosHabs.add(jlbHabsVendidas, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 220, 20));

        txtHabsVendidas.setEditable(false);
        txtHabsVendidas.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtHabsVendidas.setText("0");
        txtHabsVendidas.setBorder(null);
        jpnDatosHabs.add(txtHabsVendidas, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 0, 30, 20));

        jlbHabsQuedadas.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbHabsQuedadas.setText("CANTIDAD DE HABITACIONES QUEDADAS:");
        jpnDatosHabs.add(jlbHabsQuedadas, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 0, 230, 20));

        txtHabsQuedadas.setEditable(false);
        txtHabsQuedadas.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtHabsQuedadas.setText("0");
        txtHabsQuedadas.setBorder(null);
        jpnDatosHabs.add(txtHabsQuedadas, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 0, 30, 20));

        jlbPromedio.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbPromedio.setText("VALOR PROMEDIO:  $");
        jpnDatosHabs.add(jlbPromedio, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 0, 120, 20));

        txtValorPromedio.setEditable(false);
        txtValorPromedio.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtValorPromedio.setText("0");
        txtValorPromedio.setBorder(null);
        jpnDatosHabs.add(txtValorPromedio, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 0, 60, 20));

        jPanel1.add(jpnDatosHabs, new org.netbeans.lib.awtextra.AbsoluteConstraints(358, 460, 710, 30));

        jPanel2.setBackground(new java.awt.Color(0, 102, 102));

        jLabel15.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("HotelesMas.com");

        btnVolver.setBackground(new java.awt.Color(255, 179, 39));
        btnVolver.setText("VOLVER ");
        btnVolver.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnVolverMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 492, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 716, Short.MAX_VALUE)
                .addComponent(btnVolver, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnVolver, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 18, Short.MAX_VALUE))
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 790, 1310, 60));

        jLabel18.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel18.setText("CUADRE DE CAJA:");

        jLabel26.setText("EFECTIVO:");

        jLabel27.setText("GASTOS :");

        jLabel28.setText("SALDO DE CAJA:");

        txtCuadreCajaEfectivo.setEditable(false);
        txtCuadreCajaEfectivo.setFocusable(false);

        txtCuadreCajaGastos.setEditable(false);
        txtCuadreCajaGastos.setFocusable(false);

        txtCuadreCajaSaldos.setEditable(false);
        txtCuadreCajaSaldos.setFocusable(false);

        javax.swing.GroupLayout jpnCajaMenorLayout = new javax.swing.GroupLayout(jpnCajaMenor);
        jpnCajaMenor.setLayout(jpnCajaMenorLayout);
        jpnCajaMenorLayout.setHorizontalGroup(
            jpnCajaMenorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnCajaMenorLayout.createSequentialGroup()
                .addGroup(jpnCajaMenorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpnCajaMenorLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpnCajaMenorLayout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addGroup(jpnCajaMenorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel26)
                            .addComponent(jLabel27)
                            .addComponent(jLabel28))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jpnCajaMenorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtCuadreCajaSaldos, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                            .addComponent(txtCuadreCajaEfectivo)
                            .addComponent(txtCuadreCajaGastos, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jpnCajaMenorLayout.setVerticalGroup(
            jpnCajaMenorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnCajaMenorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnCajaMenorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(txtCuadreCajaEfectivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnCajaMenorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(txtCuadreCajaGastos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnCajaMenorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(txtCuadreCajaSaldos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jpnCajaMenor, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 500, 280, 140));

        btnNuevoGasto.setBackground(new java.awt.Color(255, 219, 95));
        btnNuevoGasto.setFont(new java.awt.Font("sansserif", 1, 11)); // NOI18N
        btnNuevoGasto.setText("NUEVO GASTO");
        btnNuevoGasto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnNuevoGastoMouseClicked(evt);
            }
        });
        jPanel1.add(btnNuevoGasto, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 740, 190, 40));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1310, 850));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnteriorActionPerformed
        Date fechaActual = jdchFechaCuadre.getDate();
        Date fechaAnterior = agregarDiasAFecha(fechaActual, -1);
        jdchFechaCuadre.setDate(fechaAnterior);
    }//GEN-LAST:event_btnAnteriorActionPerformed

    private void btnSiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSiguienteActionPerformed
        Date fechaActual = jdchFechaCuadre.getDate();
        Date fechaSiguiente = agregarDiasAFecha(fechaActual, 1);
        jdchFechaCuadre.setDate(fechaSiguiente);
    }//GEN-LAST:event_btnSiguienteActionPerformed

    private void btnHoyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHoyActionPerformed
        jdchFechaCuadre.setDate(new Date());
    }//GEN-LAST:event_btnHoyActionPerformed

    private void jdchFechaCuadrePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jdchFechaCuadrePropertyChange
        DefaultTableModel model = (DefaultTableModel) tblCuadre.getModel();
        model.setRowCount(0);
        txtHabsVendidas.setText("");
        txtHabsQuedadas.setText("");
        txtValorPromedio.setText("");
        jpnDatosHabs.setVisible(false);
        jpnValoresFinal.setVisible(false);
        txtIdTurno.setText("");
        txtTotalGastos.setText("");
        txtTotalGastos.setFocusable(true);
        txtVentajaFinal.setText("");
        jpnRecFinal.setVisible(false);
        txtEfectivos.setText("");
        txtNekis.setText("");
        txtBancolombia.setText("");
        txtDatafono.setText("");
        jpnTiposPago.setVisible(false);
        jpnCajaMenor.setVisible(false);
        btnTerminarTurno.setVisible(false);
        btnCerrarCaja.setVisible(false);
        jpnDatosTurno.setVisible(false);
        if (btnNuevoGasto.isVisible()) {
           btnNuevoGasto.setVisible(false);
        }
        

        cbxNumTurno.setBackground(new Color(0, 102, 102));
        btnOperarTurno.setVisible(false);
        Date fechaSeleccionada = jdchFechaCuadre.getDate();
        if (fechaSeleccionada != null) {
            int idHotel = 1;
            contarTurnos(fechaSeleccionada, idHotel);
        }
    }//GEN-LAST:event_jdchFechaCuadrePropertyChange

    private void btnOperarTurnoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnOperarTurnoMouseClicked
        Object selectedItem = cbxNumTurno.getSelectedItem();
        int idTurno = 0;
        if (selectedItem != null && !selectedItem.toString().equals("Seleccione")) {
            int idT = Integer.parseInt(selectedItem.toString());
            idTurno = idT;
            fillTable(idT);
            SumarGastosIngresos(idT, txtTotalGastos, txtIngresosExtra);
            jpnDatosHabs.setVisible(true);
            jpnValoresFinal.setVisible(true);
            jpnRecFinal.setVisible(true);
            jpnTiposPago.setVisible(true);
            txtTotalGastos.setFocusable(false);
            txtIngresosExtra.setFocusable(false);
            jpnCajaMenor.setVisible(true);
            if (usus.getId_tipo() == 1) {
                btnNuevoGasto.setVisible(true);
            }
        }

        if (jlbEstadoTurno.getText().equals("Activo")) {
            btnTerminarTurno.setVisible(true);
        }

        if (jblRolCuadre.getText().equals("ADMINISTRADOR") && jblCriterioCaja.getText().equals("Abierta")) {
            btnCerrarCaja.setVisible(true);
            txtTotalGastos.setFocusable(true);
            txtIngresosExtra.setFocusable(true);
        }

        if (jblCriterioCaja.getText().equals("Cerrada") && jblRolCuadre.getText().equals("ADMINISTRADOR")) {
            String[] datos = new String[2];
            java.util.Date dateUtil = jdchFechaCuadre.getDate();
            LocalDate localDate = dateUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            java.sql.Date dateSql = java.sql.Date.valueOf(localDate);

            obtenerGastoRecaudos(dateSql);

            datos = obtenerGastoRecaudos(dateSql);
            txtTotalGastos.setText(datos[0]);
            formatoPuntosMil(txtTotalGastos);
            txtVentajaFinal.setText(datos[1]);
            formatoPuntosMil(txtVentajaFinal);
            jpnRecFinal.setVisible(true);
            txtTotalGastos.setFocusable(false);
            txtIngresosExtra.setFocusable(false);
        }

        String totalneto = txtTotalNeto.getText().replaceAll("[^\\d]", "");
        String totalIngresos = txtIngresosExtra.getText().replaceAll("[^\\d]", "");
        String gastos = txtTotalGastos.getText().replaceAll("[^\\d]", "");
        String totalEfectivos = txtEfectivos.getText().replaceAll("[^\\d]", "");
        String totalComisiones = txtTotalComision.getText().replaceAll("[^\\d]", "");

        double xNeto, xGastos, xIngresoExtra, xTotalEfectivos, xTotalComisiones, xIngresoExtraEfectivo, res, resTotalEfectivo, resSaldoCaja;

        if ("".equals(totalneto)) {
            xNeto = 0;
        } else {
            xNeto = Double.parseDouble(totalneto);
        }

        if (totalIngresos.equals("")) {
            xIngresoExtra = 0;
        } else {
            xIngresoExtra = Double.parseDouble(totalIngresos);
        }

        if ("".equals(gastos)) {
            xGastos = 0;
        } else {
            xGastos = Double.parseDouble(gastos);
        }

        if ("".equals(totalEfectivos)) {
            xTotalEfectivos = 0;
        } else {
            xTotalEfectivos = Double.parseDouble(totalEfectivos);
        }

        if ("".equals(totalComisiones)) {
            xTotalComisiones = 0;
        } else {
            xTotalComisiones = Double.parseDouble(totalComisiones);
        }

        xIngresoExtraEfectivo = con.sumaIngresosExtrasEfectivo(idTurno);

        res = xNeto + xIngresoExtra - xGastos;
        resTotalEfectivo = xTotalEfectivos + xIngresoExtraEfectivo - xTotalComisiones;
        resSaldoCaja = resTotalEfectivo - xGastos;

        txtVentajaFinal.setText(formatearConPuntosDeMil(res));
        txtVentajaFinal.setBackground(Color.YELLOW);

        txtCuadreCajaEfectivo.setText(formatearConPuntosDeMil(resTotalEfectivo));
        txtCuadreCajaGastos.setText(formatearConPuntosDeMil(xGastos));
        txtCuadreCajaSaldos.setText(formatearConPuntosDeMil(resSaldoCaja));

        System.out.println("efectivos totales: " + xTotalEfectivos);
        System.out.println("total comision: " + xTotalComisiones);
        System.out.println("ingresos extra en efectivo: " + xIngresoExtraEfectivo);

    }//GEN-LAST:event_btnOperarTurnoMouseClicked

    private void txtTotalGastosKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTotalGastosKeyReleased
        String totalneto = txtTotalNeto.getText().replaceAll("[^\\d]", "");
        String totalIngresos = txtIngresosExtra.getText().replaceAll("[^\\d]", "");
        String gastos = txtTotalGastos.getText().replaceAll("[^\\d]", "");
        double num1, num2, num3, res;
        if ("".equals(totalneto)) {
            num1 = 0;
        } else {
            num1 = Double.parseDouble(totalneto);
        }
        if (totalIngresos.equals("")) {
            num3 = 0;
        } else {
            num3 = Double.parseDouble(totalIngresos);
        }
        if ("".equals(gastos)) {
            num2 = 0;
        } else {
            num2 = Double.parseDouble(gastos);
        }
        res = num1 + num3 - num2;
        txtVentajaFinal.setText(formatearConPuntosDeMil(res));
        txtVentajaFinal.setBackground(Color.YELLOW);
    }//GEN-LAST:event_txtTotalGastosKeyReleased

    private void cbxNumTurnoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxNumTurnoItemStateChanged
        Object selectedItem = cbxNumTurno.getSelectedItem();
        if (selectedItem != null && !selectedItem.toString().equals("Seleccione")) {
            int idT = Integer.parseInt(selectedItem.toString());
            txtIdTurno.setText(Integer.toString(idT));
            obtenerTurno(idT);
            jpnDatosTurno.setVisible(true);
            jpnDatosTurnopqño.setVisible(true);
            jpnNomIdHotel.setVisible(true);
            if (!jlbNombreHotel.getText().isEmpty() && !jlbRntHotel.getText().isEmpty() && !jblNombres.getText().isEmpty()
                    && !jblRollUsuario.getText().isEmpty() && !jlbFechaInicio.getText().isEmpty() && !jlbFechaFin.getText().isEmpty()
                    && !jblCriterioCaja.getText().isEmpty() && jlbEstadoTurno.getText().equals("Activo")) {
                btnOperarTurno.setVisible(true);
            }
            if (jblRolCuadre.getText().equals("ADMINISTRADOR")) {
                btnOperarTurno.setVisible(true);
            }
        }
        jpnTiposPago.setVisible(false);
        jpnValoresFinal.setVisible(false);
        jpnRecFinal.setVisible(false);
        btnTerminarTurno.setVisible(false);
        btnCerrarCaja.setVisible(false);
        jpnDatosHabs.setVisible(false);
    }//GEN-LAST:event_cbxNumTurnoItemStateChanged

    private void btnTerminarTurnoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTerminarTurnoMouseClicked
        terminarTurno();
    }//GEN-LAST:event_btnTerminarTurnoMouseClicked

    private void btnCerrarCajaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCerrarCajaMouseClicked
        CerrarCaja();
    }//GEN-LAST:event_btnCerrarCajaMouseClicked

    private void btnVolverMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnVolverMouseClicked
        this.dispose();
    }//GEN-LAST:event_btnVolverMouseClicked

    private void txtIngresosExtraKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtIngresosExtraKeyReleased
        String totalneto = txtTotalNeto.getText().replaceAll("[^\\d]", "");
        String totalIngresos = txtIngresosExtra.getText().replaceAll("[^\\d]", "");
        String gastos = txtTotalGastos.getText().replaceAll("[^\\d]", "");
        double num1, num2, num3, res;
        if ("".equals(totalneto)) {
            num1 = 0;
        } else {
            num1 = Double.parseDouble(totalneto);
        }
        if (totalIngresos.equals("")) {
            num3 = 0;
        } else {
            num3 = Double.parseDouble(totalIngresos);
        }
        if ("".equals(gastos)) {
            num2 = 0;
        } else {
            num2 = Double.parseDouble(gastos);
        }
        res = num1 + num3 - num2;
        txtVentajaFinal.setText(formatearConPuntosDeMil(res));
        txtVentajaFinal.setBackground(Color.YELLOW);
    }//GEN-LAST:event_txtIngresosExtraKeyReleased

    private void btnNuevoGastoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNuevoGastoMouseClicked
        
        int idT = Integer.parseInt(txtIdTurno.getText());
        Frame owner = (Frame) this.getOwner();
        Gastos ir = new Gastos(owner, true, idT);
        this.setVisible(false);
        ir.setVisible(true);    
         
    }//GEN-LAST:event_btnNuevoGastoMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAnterior;
    private javax.swing.JButton btnCerrarCaja;
    private javax.swing.JButton btnHoy;
    private javax.swing.JButton btnNuevoGasto;
    private javax.swing.JButton btnOperarTurno;
    private javax.swing.JButton btnSiguiente;
    private javax.swing.JButton btnTerminarTurno;
    private javax.swing.JButton btnVolver;
    private javax.swing.JComboBox<String> cbxNumTurno;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel jblCriterioCaja;
    private javax.swing.JLabel jblNombres;
    public javax.swing.JLabel jblRolCuadre;
    private javax.swing.JLabel jblRollUsuario;
    private com.toedter.calendar.JDateChooser jdchFechaCuadre;
    private javax.swing.JLabel jlbEstadoTurno;
    private javax.swing.JLabel jlbFechaFin;
    private javax.swing.JLabel jlbFechaInicio;
    private javax.swing.JLabel jlbHabsQuedadas;
    private javax.swing.JLabel jlbHabsVendidas;
    private javax.swing.JLabel jlbIdHoteles;
    private javax.swing.JLabel jlbIdHoteles1;
    private javax.swing.JLabel jlbNombreHotel;
    private javax.swing.JLabel jlbPromedio;
    private javax.swing.JLabel jlbRntHotel;
    private javax.swing.JPanel jpnCajaMenor;
    private javax.swing.JPanel jpnDatosHabs;
    private javax.swing.JPanel jpnDatosTurno;
    private javax.swing.JPanel jpnDatosTurnopqño;
    private javax.swing.JPanel jpnNomIdHotel;
    private javax.swing.JPanel jpnRecFinal;
    private javax.swing.JPanel jpnTiposPago;
    private javax.swing.JPanel jpnValoresFinal;
    private javax.swing.JTable tblCuadre;
    private javax.swing.JTextField txtBancolombia;
    public javax.swing.JTextField txtCantTurnos;
    private javax.swing.JTextField txtCuadreCajaEfectivo;
    private javax.swing.JTextField txtCuadreCajaGastos;
    private javax.swing.JTextField txtCuadreCajaSaldos;
    private javax.swing.JTextField txtDatafono;
    private javax.swing.JTextField txtDaviplata;
    private javax.swing.JTextField txtDavivienda;
    private javax.swing.JTextField txtEfectivos;
    private javax.swing.JTextField txtHabsQuedadas;
    private javax.swing.JTextField txtHabsVendidas;
    public javax.swing.JTextField txtIdTurno;
    private javax.swing.JTextField txtIngresosExtra;
    private javax.swing.JTextField txtNekis;
    public javax.swing.JTextField txtNomCuadre;
    private javax.swing.JTextField txtTotalComision;
    private javax.swing.JTextField txtTotalGastos;
    private javax.swing.JTextField txtTotalNeto;
    private javax.swing.JTextField txtTotalVentas;
    private javax.swing.JTextField txtValorPromedio;
    private javax.swing.JTextField txtVentajaFinal;
    // End of variables declaration//GEN-END:variables
}
