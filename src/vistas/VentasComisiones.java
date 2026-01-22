package vistas;

import conectar.Canectar;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.Hotel;
import modelo.UsuarioOperando;

public final class VentasComisiones extends javax.swing.JDialog {

    Canectar con = new Canectar();
    Calendar current;
    private final UsuarioOperando usus;
    private final Hotel hotel;

    public VentasComisiones(java.awt.Frame parent, boolean modal, UsuarioOperando usus, Hotel hotel) {
        super(parent, modal);
        this.usus = usus;
        this.hotel = hotel;
        initComponents();
        jpnFechasBoton.setVisible(false);
        jpnPremiados.setVisible(false);
        jpnPremiadosV.setVisible(false);
        this.setLocationRelativeTo(null);
        traerHotelesCbx();
        btnGenerar.addMouseListener(mouseAdapter);
        btnSalir.addMouseListener(mouseAdapter);

    }

    public void traerHotelesCbx() {
        java.sql.Connection conex = con.conexion();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT Nombre_Hotel FROM hoteles ORDER BY Nombre_Hotel";
            ps = conex.prepareStatement(sql);
            rs = ps.executeQuery();

            cbxSelecHotel.removeAllItems();
            cbxSelecHotel.addItem("Seleccionar");
            cbxSelecHotel.addItem("TODOS");
            while (rs.next()) {
                String hotel = rs.getString("Nombre_Hotel");
                cbxSelecHotel.addItem(hotel);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener datos de hotel  : " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(null, "Error al serrar recursos de traerHotelescbx  : " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void actualizarDateChooser(int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 1);  // Primer día del mes
        jdchDesde.setDate(cal.getTime());

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));  // Último día del mes
        jdchHasta.setDate(cal.getTime());
    }

    public void llenarTablaDatosGL(Date fechaInicio, Date fechaFin) {
        java.sql.Connection conex = con.conexion();
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "";

        if (cbxSelecHotel.getSelectedItem().toString().equals("TODOS")) {
            // Crear la consulta SQL para TODOS los hoteles
            query = "SELECT c.Valor_Habitacion, c.Comision, ck.Fecha_Ingreso, ck.Num_Habitacion, u.Usuario "
                    + "FROM contable c "
                    + "INNER JOIN check_in ck ON c.Fk_Id_Check_In = ck.Id_Reserva "
                    + "INNER JOIN usuarios u ON c.Fk_Id_Usuario = u.Id_Usuario "
                    + "WHERE ck.Fecha_Ingreso BETWEEN ? AND ? "
                    + "ORDER BY c.Id_Valores ASC";
        } else {
            // Crear la consulta SQL para un hotel específico
            query = "SELECT c.Valor_Habitacion, c.Comision, ck.Fecha_Ingreso, ck.Num_Habitacion, u.Usuario "
                    + "FROM contable c "
                    + "INNER JOIN check_in ck ON c.Fk_Id_Check_In = ck.Id_Reserva "
                    + "INNER JOIN usuarios u ON c.Fk_Id_Usuario = u.Id_Usuario "
                    + "INNER JOIN hoteles h ON ck.Fk_Id_Hotel = h.Id_Hoteles "
                    + "WHERE ck.Fecha_Ingreso BETWEEN ? AND ? AND h.Nombre_Hotel = ? "
                    + "ORDER BY c.Id_Valores ASC";
        }

        try {
            ps = conex.prepareStatement(query);

            // Convertir las fechas a SQL Date
            java.sql.Date sqlFechaInicio = new java.sql.Date(fechaInicio.getTime());
            java.sql.Date sqlFechaFin = new java.sql.Date(fechaFin.getTime());

            // Asignar los parámetros de la consulta
            if (cbxSelecHotel.getSelectedItem().toString().equals("TODOS")) {
                // Para TODOS los hoteles
                ps.setDate(1, sqlFechaInicio);
                ps.setDate(2, sqlFechaFin);
            } else {
                // Para un hotel específico
                ps.setDate(1, sqlFechaInicio);
                ps.setDate(2, sqlFechaFin);
                ps.setString(3, cbxSelecHotel.getSelectedItem().toString());
            }

            rs = ps.executeQuery();
            DefaultTableModel model = (DefaultTableModel) tblDatosGL.getModel();  // Inicializar model
            model.setRowCount(0);
            int cantDatos = 0;

            // Llenar el modelo con los datos obtenidos
            while (rs.next()) {
                Object[] row = new Object[5];
                row[0] = rs.getDate("Fecha_Ingreso");
                row[1] = rs.getString("Usuario");
                row[2] = rs.getString("Num_Habitacion");
                row[3] = rs.getDouble("Valor_Habitacion");
                row[4] = rs.getDouble("Comision");
                model.addRow(row);
                cantDatos++;
            }
            if (cantDatos == 0) {
                JOptionPane.showMessageDialog(this, "No existen datos!");
                DefaultTableModel modelEs = (DefaultTableModel) tblDatosES.getModel();
                modelEs.setRowCount(0);
                jlbNombreRes.setText(""); jlbApeRes.setText(""); jlbDocRes.setText(""); jlbTelRes.setText(""); jlbHotelRes.setText("");
                jlbNomVen.setText(""); jlbApeVen.setText(""); jlbDocVen.setText(""); jlbTelVen.setText(""); jlbHotelVen.setText("");
                jlbNoRes.setText("NO EXISTEN DATOS!");
                jlbNoVen.setText("NO EXISTEN DATOS!"); 
            } else {
                jlbNoRes.setText("DATO ENCONTRADO!");
                jlbNoVen.setText("DATO ENCONTRADO!");
                if (cantDatos == 1) {
                    JOptionPane.showMessageDialog(this, "Se encontro " + cantDatos + " linea de datos.");

                } else {
                    JOptionPane.showMessageDialog(this, "Se encontraron " + cantDatos + " lineas de datos en total.");

                }
                llenarTablaDatosES(fechaInicio, fechaFin);
                String hotel = cbxSelecHotel.getSelectedItem().toString();
                obtenerRecepcionistaMasVentas(hotel, fechaInicio, fechaFin);
                obtenerVendedorMasVentas(hotel, fechaInicio, fechaFin);
            }
            jpnPremiados.setVisible(true);
            jpnPremiadosV.setVisible(true);

            // Asignar el modelo a la tabla
            tblDatosGL.setModel(model);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al llenar la tabla 'tblDatosGL' ");
            System.out.println("error! " + e.getMessage());
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
                JOptionPane.showMessageDialog(this, "Error al cerrar recursos de llenar la tabla 'tblDatosGL' ");
            }
        }
    }

    public void llenarTablaDatosES(Date fechaInicio, Date fechaFin) {
        java.sql.Connection conex = con.conexion();
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "";

        if (cbxSelecHotel.getSelectedItem().toString().equals("TODOS")) {
            // Crear la consulta SQL para TODOS los hoteles
            query = "SELECT u.Usuario, "
                    + "SUM(c.Valor_Habitacion) AS Total_Ventas, "
                    + "SUM(c.Comision) AS Total_Comisiones, "
                    + "COUNT(c.Fk_Id_Check_In) AS Total_Habitaciones_Vendidas "
                    + "FROM contable c "
                    + "INNER JOIN check_in ck ON c.Fk_Id_Check_In = ck.Id_Reserva "
                    + "INNER JOIN usuarios u ON c.Fk_Id_Usuario = u.Id_Usuario "
                    + "WHERE ck.Fecha_Ingreso BETWEEN ? AND ? "
                    + "GROUP BY u.Usuario "
                    + "ORDER BY Total_Ventas DESC";
        } else {
            // Crear la consulta SQL para un hotel específico
            query = "SELECT u.Usuario, "
                    + "SUM(c.Valor_Habitacion) AS Total_Ventas, "
                    + "SUM(c.Comision) AS Total_Comisiones, "
                    + "COUNT(c.Fk_Id_Check_In) AS Total_Habitaciones_Vendidas "
                    + "FROM contable c "
                    + "INNER JOIN check_in ck ON c.Fk_Id_Check_In = ck.Id_Reserva "
                    + "INNER JOIN usuarios u ON c.Fk_Id_Usuario = u.Id_Usuario "
                    + "INNER JOIN hoteles h ON ck.Fk_Id_Hotel = h.Id_Hoteles "
                    + "WHERE ck.Fecha_Ingreso BETWEEN ? AND ? AND h.Nombre_Hotel = ? "
                    + "GROUP BY u.Usuario "
                    + "ORDER BY Total_Ventas DESC";
        }

        try {
            ps = conex.prepareStatement(query);

            // Convertir las fechas a SQL Date
            java.sql.Date sqlFechaInicio = new java.sql.Date(fechaInicio.getTime());
            java.sql.Date sqlFechaFin = new java.sql.Date(fechaFin.getTime());

            // Asignar los parámetros de la consulta
            if (cbxSelecHotel.getSelectedItem().toString().equals("TODOS")) {
                // Para TODOS los hoteles
                ps.setDate(1, sqlFechaInicio);
                ps.setDate(2, sqlFechaFin);
            } else {
                // Para un hotel específico
                ps.setDate(1, sqlFechaInicio);
                ps.setDate(2, sqlFechaFin);
                ps.setString(3, cbxSelecHotel.getSelectedItem().toString());
            }

            rs = ps.executeQuery();
            DefaultTableModel model = (DefaultTableModel) tblDatosES.getModel();  // Inicializar model
            model.setRowCount(0);

            // Llenar el modelo con los datos obtenidos
            while (rs.next()) {
                Object[] row = new Object[4];
                row[0] = rs.getString("Usuario");
                row[1] = rs.getDouble("Total_Ventas");
                row[2] = rs.getDouble("Total_Comisiones");
                row[3] = rs.getInt("Total_Habitaciones_Vendidas");
                model.addRow(row);
            }

            // Asignar el modelo a la tabla
            tblDatosES.setModel(model);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al llenar la tabla 'tblDatosES' ");
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
                JOptionPane.showMessageDialog(this, "Error al cerrar recursos de llenar la tabla 'tblDatosES' ");
            }
        }
    }

    public void obtenerRecepcionistaMasVentas(String hotel, Date fechaInicio, Date fechaFin) {
        java.sql.Connection conex = con.conexion();
        String query = "";
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            if (hotel.equals("TODOS")) {
                query = "SELECT u.Id_Usuario, u.Nombres, u.Apellidos, u.Cedula, u.Telefono, h.Nombre_Hotel, "
                        + "SUM(c.Valor_Habitacion) AS Total_Ventas "
                        + "FROM contable c "
                        + "INNER JOIN check_in ck ON c.Fk_Id_Check_In = ck.Id_Reserva "
                        + "INNER JOIN usuarios u ON c.Fk_Id_Usuario = u.Id_Usuario AND u.Id_Tipo = 2 "
                        + "INNER JOIN hoteles h ON ck.Fk_Id_Hotel = h.Id_Hoteles "
                        + "WHERE ck.Fecha_Ingreso BETWEEN ? AND ? "
                        + "GROUP BY u.Id_Usuario "
                        + "ORDER BY Total_Ventas DESC "
                        + "LIMIT 1";

            } else {
                query = "SELECT u.Id_Usuario, u.Nombres, u.Apellidos, u.Cedula, u.Telefono, "
                        + "SUM(c.Valor_Habitacion) AS Total_Ventas "
                        + "FROM contable c "
                        + "INNER JOIN check_in ck ON c.Fk_Id_Check_In = ck.Id_Reserva "
                        + "INNER JOIN usuarios u ON c.Fk_Id_Usuario = u.Id_Usuario AND u.Id_Tipo = 2 "
                        + "INNER JOIN hoteles h ON ck.Fk_Id_Hotel = h.Id_Hoteles "
                        + "WHERE h.Nombre_Hotel = ? AND ck.Fecha_Ingreso BETWEEN ? AND ? "
                        + "GROUP BY u.Id_Usuario "
                        + "ORDER BY Total_Ventas DESC "
                        + "LIMIT 1";
            }
            ps = conex.prepareStatement(query);

            if (hotel.equals("TODOS")) {
                ps.setString(1, sdf.format(fechaInicio));
                ps.setString(2, sdf.format(fechaFin));
            } else {
                ps.setString(1, hotel);
                ps.setString(2, sdf.format(fechaInicio));
                ps.setString(3, sdf.format(fechaFin));
            }

            rs = ps.executeQuery();

            if (rs.next()) {
                jlbNombreRes.setText(rs.getString("Nombres"));
                jlbApeRes.setText(rs.getString("Apellidos"));
                jlbDocRes.setText(rs.getString("Cedula"));
                jlbTelRes.setText(rs.getString("Telefono"));
                jlbHotelRes.setText(hotel);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al traer a la recepcionista con mas ventas" + ex);
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
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
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al cerrar recursos de  traer a la recepcionista con mas ventas" + ex);
            }
        }
    }

    public void obtenerVendedorMasVentas(String hotel, Date fechaInicio, Date fechaFin) {
        java.sql.Connection conex = con.conexion();
        String query = "";
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            if (hotel.equals("TODOS")) {
                query = "SELECT u.Id_Usuario, u.Nombres, u.Apellidos, u.Cedula, u.Telefono, h.Nombre_Hotel, "
                        + "SUM(c.Valor_Habitacion) AS Total_Ventas "
                        + "FROM contable c "
                        + "INNER JOIN check_in ck ON c.Fk_Id_Check_In = ck.Id_Reserva "
                        + "INNER JOIN usuarios u ON c.Fk_Id_Usuario = u.Id_Usuario AND u.Id_Tipo = 3 "
                        + "INNER JOIN hoteles h ON ck.Fk_Id_Hotel = h.Id_Hoteles "
                        + "WHERE ck.Fecha_Ingreso BETWEEN ? AND ? "
                        + "GROUP BY u.Id_Usuario "
                        + "ORDER BY Total_Ventas DESC "
                        + "LIMIT 1";

            } else {
                query = "SELECT u.Id_Usuario, u.Nombres, u.Apellidos, u.Cedula, u.Telefono, "
                        + "SUM(c.Valor_Habitacion) AS Total_Ventas "
                        + "FROM contable c "
                        + "INNER JOIN check_in ck ON c.Fk_Id_Check_In = ck.Id_Reserva "
                        + "INNER JOIN usuarios u ON c.Fk_Id_Usuario = u.Id_Usuario AND u.Id_Tipo = 3 "
                        + "INNER JOIN hoteles h ON ck.Fk_Id_Hotel = h.Id_Hoteles "
                        + "WHERE h.Nombre_Hotel = ? AND ck.Fecha_Ingreso BETWEEN ? AND ? "
                        + "GROUP BY u.Id_Usuario "
                        + "ORDER BY Total_Ventas DESC "
                        + "LIMIT 1";
            }
            ps = conex.prepareStatement(query);

            if (hotel.equals("TODOS")) {
                ps.setString(1, sdf.format(fechaInicio));
                ps.setString(2, sdf.format(fechaFin));
            } else {
                ps.setString(1, hotel);
                ps.setString(2, sdf.format(fechaInicio));
                ps.setString(3, sdf.format(fechaFin));
            }

            rs = ps.executeQuery();

            if (rs.next()) {
                jlbNomVen.setText(rs.getString("Nombres"));
                jlbApeVen.setText(rs.getString("Apellidos"));
                jlbDocVen.setText(rs.getString("Cedula"));
                jlbTelVen.setText(rs.getString("Telefono"));
                jlbHotelVen.setText(hotel);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al traer al vendedor con mas ventas" + ex);
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
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
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al cerrar recursos de traer al vendedor con mas ventas" + ex);
            }
        }
    }

    public void vaciarTablas() {
        DefaultTableModel modelGL = (DefaultTableModel) tblDatosGL.getModel();
        DefaultTableModel modelES = (DefaultTableModel) tblDatosES.getModel();
        modelGL.setRowCount(0);
        modelES.setRowCount(0);
        jpnPremiados.setVisible(false);
        jpnPremiadosV.setVisible(false);
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
            button.setBackground(new Color(255, 179, 39));

        }
    };

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cbxSelecHotel = new javax.swing.JComboBox<>();
        jlbRollUsuario = new javax.swing.JLabel();
        jlbNombreUsuario = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btnSalir = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDatosGL = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDatosES = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jpnFechasBoton = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        cbxMeses = new javax.swing.JComboBox<>();
        btnGenerar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jdchDesde = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        jdchHasta = new com.toedter.calendar.JDateChooser();
        jpnPremiados = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jlbNombreRes = new javax.swing.JLabel();
        jlbApeRes = new javax.swing.JLabel();
        jlbDocRes = new javax.swing.JLabel();
        jlbTelRes = new javax.swing.JLabel();
        jlbHotelRes = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jlbNoRes = new javax.swing.JLabel();
        jpnPremiadosV = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jlbNomVen = new javax.swing.JLabel();
        jlbApeVen = new javax.swing.JLabel();
        jlbDocVen = new javax.swing.JLabel();
        jlbTelVen = new javax.swing.JLabel();
        jlbHotelVen = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jlbNoVen = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setForeground(new java.awt.Color(0, 0, 0));

        jPanel2.setBackground(new java.awt.Color(0, 102, 102));

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("SELECCIONE UN HOTEL :");

        cbxSelecHotel.setBackground(new java.awt.Color(255, 219, 95));
        cbxSelecHotel.setForeground(new java.awt.Color(0, 0, 0));
        cbxSelecHotel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 219, 95)));
        cbxSelecHotel.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxSelecHotelItemStateChanged(evt);
            }
        });

        jlbRollUsuario.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbRollUsuario.setForeground(new java.awt.Color(255, 255, 255));
        jlbRollUsuario.setText("ADMINISTRADOR");

        jlbNombreUsuario.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbNombreUsuario.setForeground(new java.awt.Color(255, 255, 255));
        jlbNombreUsuario.setText("Carlos Moran");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxSelecHotel, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jlbRollUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlbNombreUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxSelecHotel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlbRollUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlbNombreUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(0, 102, 102));

        btnSalir.setBackground(new java.awt.Color(255, 179, 39));
        btnSalir.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        btnSalir.setForeground(new java.awt.Color(0, 0, 0));
        btnSalir.setText("Salir");
        btnSalir.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSalirMouseClicked(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("HotelesMas.com");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 422, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnSalir, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5))
        );

        jPanel4.setBackground(new java.awt.Color(0, 51, 51));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(0, 0, 0), new java.awt.Color(255, 179, 39)));

        tblDatosGL.setForeground(new java.awt.Color(0, 0, 0));
        tblDatosGL.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "FECHA", "USUARIO", "NUM. HABITACION", "VALOR HABITACION", "VALOR COMISION"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblDatosGL);
        if (tblDatosGL.getColumnModel().getColumnCount() > 0) {
            tblDatosGL.getColumnModel().getColumn(0).setResizable(false);
            tblDatosGL.getColumnModel().getColumn(1).setResizable(false);
            tblDatosGL.getColumnModel().getColumn(2).setMinWidth(90);
            tblDatosGL.getColumnModel().getColumn(2).setMaxWidth(90);
            tblDatosGL.getColumnModel().getColumn(3).setResizable(false);
            tblDatosGL.getColumnModel().getColumn(4).setResizable(false);
        }

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("DATOS GLOBALES");
        jLabel4.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 652, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(0, 51, 51));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(0, 0, 0), new java.awt.Color(255, 179, 39)));

        tblDatosES.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "USUARIO", "TOTAL VENTAS", "TOTAL COMISIONES", "TOTAL HABS. VENDIDAS"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblDatosES);

        jLabel5.setBackground(new java.awt.Color(0, 51, 51));
        jLabel5.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("DATOS ESPECIFICOS");
        jLabel5.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 652, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jpnFechasBoton.setBackground(new java.awt.Color(0, 102, 102));

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("MES :");

        cbxMeses.setBackground(new java.awt.Color(255, 219, 95));
        cbxMeses.setForeground(new java.awt.Color(0, 0, 0));
        cbxMeses.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE" }));
        cbxMeses.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 219, 95)));
        cbxMeses.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxMesesItemStateChanged(evt);
            }
        });

        btnGenerar.setBackground(new java.awt.Color(255, 179, 39));
        btnGenerar.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        btnGenerar.setForeground(new java.awt.Color(0, 0, 0));
        btnGenerar.setText("GENERAR");
        btnGenerar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnGenerarMouseClicked(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("DESDE :");

        jdchDesde.setBackground(new java.awt.Color(255, 219, 95));
        jdchDesde.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 219, 95), 1, true));
        jdchDesde.setForeground(new java.awt.Color(0, 0, 0));

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("HASTA :");

        jdchHasta.setBackground(new java.awt.Color(255, 219, 95));
        jdchHasta.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 219, 95)));
        jdchHasta.setForeground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout jpnFechasBotonLayout = new javax.swing.GroupLayout(jpnFechasBoton);
        jpnFechasBoton.setLayout(jpnFechasBotonLayout);
        jpnFechasBotonLayout.setHorizontalGroup(
            jpnFechasBotonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnFechasBotonLayout.createSequentialGroup()
                .addGroup(jpnFechasBotonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpnFechasBotonLayout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addGroup(jpnFechasBotonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpnFechasBotonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpnFechasBotonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jdchDesde, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cbxMeses, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jdchHasta, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(50, 50, 50))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpnFechasBotonLayout.createSequentialGroup()
                        .addGap(86, 86, 86)
                        .addComponent(btnGenerar, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(86, 86, 86)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpnFechasBotonLayout.setVerticalGroup(
            jpnFechasBotonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnFechasBotonLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jpnFechasBotonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxMeses, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jpnFechasBotonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jdchDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnFechasBotonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jdchHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addComponent(btnGenerar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jpnPremiados.setBackground(new java.awt.Color(255, 219, 95));

        jLabel7.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 51, 51));
        jLabel7.setText("RECEPCIONISTA DEL MES");

        jLabel8.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 51, 51));
        jLabel8.setText("Nombres:");

        jLabel9.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 51, 51));
        jLabel9.setText("Apellidos:");

        jLabel10.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 51, 51));
        jLabel10.setText("Telefono:");

        jLabel11.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 51, 51));
        jLabel11.setText("Documento:");

        jLabel12.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 51, 51));
        jLabel12.setText("Hotel:");

        jlbNombreRes.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbNombreRes.setForeground(new java.awt.Color(0, 51, 51));

        jlbApeRes.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbApeRes.setForeground(new java.awt.Color(0, 51, 51));

        jlbDocRes.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbDocRes.setForeground(new java.awt.Color(0, 51, 51));

        jlbTelRes.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbTelRes.setForeground(new java.awt.Color(0, 51, 51));

        jlbHotelRes.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbHotelRes.setForeground(new java.awt.Color(0, 51, 51));

        jlbNoRes.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jlbNoRes.setForeground(new java.awt.Color(0, 51, 51));
        jlbNoRes.setText("NO EXISTEN DATOS!");

        javax.swing.GroupLayout jpnPremiadosLayout = new javax.swing.GroupLayout(jpnPremiados);
        jpnPremiados.setLayout(jpnPremiadosLayout);
        jpnPremiadosLayout.setHorizontalGroup(
            jpnPremiadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator2)
            .addGroup(jpnPremiadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpnPremiadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addGroup(jpnPremiadosLayout.createSequentialGroup()
                        .addGroup(jpnPremiadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpnPremiadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlbNombreRes, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlbApeRes, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlbHotelRes, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlbTelRes, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlbDocRes, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jpnPremiadosLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jlbNoRes)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpnPremiadosLayout.setVerticalGroup(
            jpnPremiadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnPremiadosLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addGroup(jpnPremiadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jlbNombreRes, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnPremiadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jlbApeRes, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnPremiadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jlbDocRes, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnPremiadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jlbTelRes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnPremiadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jlbHotelRes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlbNoRes, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE))
        );

        jpnPremiadosV.setBackground(new java.awt.Color(255, 219, 95));

        jLabel18.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(0, 51, 51));
        jLabel18.setText("VENDEDOR DEL MES");

        jLabel19.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(0, 51, 51));
        jLabel19.setText("Nombres:");

        jLabel20.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(0, 51, 51));
        jLabel20.setText("Apellidos:");

        jLabel21.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(0, 51, 51));
        jLabel21.setText("Telefono:");

        jLabel22.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(0, 51, 51));
        jLabel22.setText("Documento:");

        jLabel23.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(0, 51, 51));
        jLabel23.setText("Hotel:");

        jlbNomVen.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbNomVen.setForeground(new java.awt.Color(0, 51, 51));

        jlbApeVen.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbApeVen.setForeground(new java.awt.Color(0, 51, 51));

        jlbDocVen.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbDocVen.setForeground(new java.awt.Color(0, 51, 51));

        jlbTelVen.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbTelVen.setForeground(new java.awt.Color(0, 51, 51));

        jlbHotelVen.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbHotelVen.setForeground(new java.awt.Color(0, 51, 51));

        jlbNoVen.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jlbNoVen.setForeground(new java.awt.Color(0, 51, 51));
        jlbNoVen.setText("NO EXISTEN DATOS!");

        javax.swing.GroupLayout jpnPremiadosVLayout = new javax.swing.GroupLayout(jpnPremiadosV);
        jpnPremiadosV.setLayout(jpnPremiadosVLayout);
        jpnPremiadosVLayout.setHorizontalGroup(
            jpnPremiadosVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(jpnPremiadosVLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpnPremiadosVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18)
                    .addGroup(jpnPremiadosVLayout.createSequentialGroup()
                        .addGroup(jpnPremiadosVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpnPremiadosVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlbNomVen, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlbApeVen, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlbHotelVen, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlbTelVen, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlbDocVen, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jpnPremiadosVLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jlbNoVen)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpnPremiadosVLayout.setVerticalGroup(
            jpnPremiadosVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnPremiadosVLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addGroup(jpnPremiadosVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(jlbNomVen, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnPremiadosVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20)
                    .addComponent(jlbApeVen, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnPremiadosVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jlbDocVen, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnPremiadosVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jlbTelVen, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnPremiadosVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jlbHotelVen, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlbNoVen, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jpnFechasBoton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jpnPremiados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jpnPremiadosV, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jpnFechasBoton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jpnPremiados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jpnPremiadosV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void cbxSelecHotelItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxSelecHotelItemStateChanged
        Object ItemSelected = cbxSelecHotel.getSelectedItem();
        if (ItemSelected != null) {
            vaciarTablas();
            String seleccion = cbxSelecHotel.getSelectedItem().toString();
            if (!seleccion.equals("Seleccionar")) {
                jpnFechasBoton.setVisible(true);
                current = Calendar.getInstance();
                cbxMeses.setSelectedIndex(current.get(Calendar.MONTH));
            } else {
                jpnFechasBoton.setVisible(false);
            }
        }
    }//GEN-LAST:event_cbxSelecHotelItemStateChanged

    private void cbxMesesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxMesesItemStateChanged
        Object itemSelected = cbxMeses.getSelectedItem();
        if (itemSelected != null) {
            actualizarDateChooser(current.get(Calendar.MONTH));
            actualizarDateChooser(cbxMeses.getSelectedIndex());
            vaciarTablas();
        }
    }//GEN-LAST:event_cbxMesesItemStateChanged

    private void btnGenerarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGenerarMouseClicked
        Date fechaInicio = jdchDesde.getDate();
        Date fechaFin = jdchHasta.getDate();
        llenarTablaDatosGL(fechaInicio, fechaFin);
    }//GEN-LAST:event_btnGenerarMouseClicked

    private void btnSalirMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSalirMouseClicked
        this.dispose();
    }//GEN-LAST:event_btnSalirMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGenerar;
    private javax.swing.JButton btnSalir;
    private javax.swing.JComboBox<String> cbxMeses;
    private javax.swing.JComboBox<String> cbxSelecHotel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private com.toedter.calendar.JDateChooser jdchDesde;
    private com.toedter.calendar.JDateChooser jdchHasta;
    private javax.swing.JLabel jlbApeRes;
    private javax.swing.JLabel jlbApeVen;
    private javax.swing.JLabel jlbDocRes;
    private javax.swing.JLabel jlbDocVen;
    private javax.swing.JLabel jlbHotelRes;
    private javax.swing.JLabel jlbHotelVen;
    private javax.swing.JLabel jlbNoRes;
    private javax.swing.JLabel jlbNoVen;
    private javax.swing.JLabel jlbNomVen;
    private javax.swing.JLabel jlbNombreRes;
    private javax.swing.JLabel jlbNombreUsuario;
    private javax.swing.JLabel jlbRollUsuario;
    private javax.swing.JLabel jlbTelRes;
    private javax.swing.JLabel jlbTelVen;
    private javax.swing.JPanel jpnFechasBoton;
    private javax.swing.JPanel jpnPremiados;
    private javax.swing.JPanel jpnPremiadosV;
    private javax.swing.JTable tblDatosES;
    private javax.swing.JTable tblDatosGL;
    // End of variables declaration//GEN-END:variables
}
