package conectar;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import modelo.Check_In;
import modelo.Cliente;
import modelo.Contable;
import modelo.Habitaciones;
import modelo.Hotel;

public class Canectar {

    JOptionPane optionPane = new JOptionPane();
    private Connection con = null;
    
    private final String url = "jdbc:mysql://localhost:3306/doralplaza";
    private final String usuario = "root";
    private final String pass = "Carlos.2020#";
    private final String driver = "com.mysql.cj.jdbc.Driver";

    public Connection conexion() {

        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, usuario, pass);
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Error de conexion a la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return con;
    }

    public double sumaIngresosExtrasEfectivo(int idTurno) {
        double sumaIngresosEfectivo = 0.0;
        String sql = "SELECT SUM(Valor_Ingreso) AS SumaValorIngreso FROM ingreso_extra "
                + "WHERE Tipo_Pago = 'Efectivo' AND Fk_Id_Turno = ?";

        try ( Connection conex = this.conexion();  PreparedStatement sentencia = conex.prepareStatement(sql)) {

            sentencia.setInt(1, idTurno);

            try ( ResultSet rs = sentencia.executeQuery()) {

                if (rs.next()) {
                    sumaIngresosEfectivo = rs.getDouble("SumaValorIngreso");

                }
            }
        } catch (SQLException e) {

            JOptionPane.showMessageDialog(null, "Error al obtener la suma de ingresos extras: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return sumaIngresosEfectivo;
    }

    public boolean actualizarReporteSire(int idCliente, boolean estadoReporteSire) {
        String sql = "UPDATE cliente SET Reporte_Sire = ? WHERE Id_Cliente = ?";
        try ( Connection conexion = this.conexion();  PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setBoolean(1, estadoReporteSire); 
            ps.setInt(2, idCliente); 

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return true; 
            } else {
                return false; 
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar el estado de Reporte Sire: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false; 
        }
    }

    public Cliente registrarCliente(Cliente cliente) {
        String sqlcliente = "INSERT INTO cliente(Num_Documento,Tipo_Documento, Fecha_Nacimiento,"
                + "Nombres,Apellidos,Nacionalidad,Telefono,Profesion,Estado_Verificacion)"
                + " VALUES(?,?,?,?,?,?,?,?,?)";

        try ( Connection conexion = this.conexion();  PreparedStatement ps = conexion.prepareStatement(sqlcliente, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cliente.getNum_Documento());
            ps.setString(2, cliente.getTipo_Documento());
            if (cliente.getFecha_Nacimiento() != null) {
                ps.setDate(3, new java.sql.Date(cliente.getFecha_Nacimiento().getTime()));
            } else {
                ps.setDate(3, null);
            }
            ps.setString(4, cliente.getNombres());
            ps.setString(5, cliente.getApellidos());
            ps.setString(6, cliente.getNacionalidad());
            ps.setString(7, cliente.getTelefono());
            ps.setString(8, cliente.getProfesion());
            ps.setString(9, cliente.getEstado_Verificacion());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try ( ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cliente.setId_Cliente(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar cliente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return cliente;
    }

    public Cliente actualizarClienteDB(Cliente cliente, int idCliente) {
        String sqlUpdate = "UPDATE cliente SET Num_Documento = ?, Tipo_Documento = ?, Fecha_Nacimiento = ?, "
                + "Nombres = ?, Apellidos = ?, Nacionalidad = ?, Telefono = ?, Profesion = ?, Estado_Verificacion = ?, Edad = ?, Reporte_Sire = ? "
                + "WHERE Id_Cliente = ?";

        try ( Connection conexion = this.conexion();  PreparedStatement ps = conexion.prepareStatement(sqlUpdate)) {
            ps.setString(1, cliente.getNum_Documento());
            ps.setString(2, cliente.getTipo_Documento());
            if (cliente.getFecha_Nacimiento() != null) {
                ps.setDate(3, new java.sql.Date(cliente.getFecha_Nacimiento().getTime()));
            } else {
                ps.setNull(3, java.sql.Types.DATE);
            }
            ps.setString(4, cliente.getNombres());
            ps.setString(5, cliente.getApellidos());
            ps.setString(6, cliente.getNacionalidad());
            ps.setString(7, cliente.getTelefono());
            ps.setString(8, cliente.getProfesion());
            ps.setString(9, cliente.getEstado_Verificacion());
            ps.setInt(10, cliente.getEdad());
            ps.setBoolean(11, cliente.isReporte_Sire());
            ps.setInt(12, idCliente);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                return null;
            }

            // Consulta para obtener el cliente actualizado
            String sqlSelect = "SELECT * FROM cliente WHERE Id_Cliente = ?";
            try ( PreparedStatement psSelect = conexion.prepareStatement(sqlSelect)) {
                psSelect.setInt(1, idCliente);
                ResultSet rs = psSelect.executeQuery();
                if (rs.next()) {
                    // Construir el objeto cliente con los datos obtenidos
                    Cliente clienteActualizado = new Cliente();
                    clienteActualizado.setId_Cliente(rs.getInt("Id_Cliente"));
                    clienteActualizado.setNum_Documento(rs.getString("Num_Documento"));
                    clienteActualizado.setTipo_Documento(rs.getString("Tipo_Documento"));
                    clienteActualizado.setFecha_Nacimiento(rs.getDate("Fecha_Nacimiento"));
                    clienteActualizado.setNombres(rs.getString("Nombres"));
                    clienteActualizado.setApellidos(rs.getString("Apellidos"));
                    clienteActualizado.setNacionalidad(rs.getString("Nacionalidad"));
                    clienteActualizado.setTelefono(rs.getString("Telefono"));
                    clienteActualizado.setProfesion(rs.getString("Profesion"));
                    clienteActualizado.setEstado_Verificacion(rs.getString("Estado_Verificacion"));
                    clienteActualizado.setEdad(rs.getInt("Edad"));
                    clienteActualizado.setReporte_Sire(rs.getBoolean("Reporte_Sire"));
                    return clienteActualizado;
                }
            }
        } catch (SQLException e) {
            // Manejo de excepciones
            // Puedo considerar registrar el error
            return null;
        }
        return null; // En caso de que no se encuentre el cliente
    }

    public Cliente buscarClientePorCedula(String numDocumento) {
        Cliente cliente = null;
        String sql = "SELECT * FROM cliente WHERE Num_Documento = ?";

        try ( Connection conexion = this.conexion();  PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, numDocumento);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                cliente = new Cliente();
                cliente.setId_Cliente(rs.getInt("Id_Cliente"));
                cliente.setNum_Documento(rs.getString("Num_Documento"));
                cliente.setTipo_Documento(rs.getString("Tipo_Documento"));
                cliente.setFecha_Nacimiento(rs.getDate("Fecha_Nacimiento"));
                cliente.setNombres(rs.getString("Nombres"));
                cliente.setApellidos(rs.getString("Apellidos"));
                cliente.setNacionalidad(rs.getString("Nacionalidad"));
                cliente.setTelefono(rs.getString("Telefono"));
                cliente.setProfesion(rs.getString("Profesion"));
                cliente.setEstado_Verificacion(rs.getString("Estado_Verificacion"));
                cliente.setEdad(rs.getInt("Edad"));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar cliente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return cliente;
    }

    public boolean actualizarEstadoVerificacion(int idCliente, String nuevoEstadoVerificacion) {
        String sql = "UPDATE cliente SET Estado_Verificacion = ? WHERE Id_Cliente = ?";

        System.out.println("estamos evaluando el estado de verificacion desde el metodo de conectar");
        System.out.println("id del cliente: " + idCliente);
        System.out.println("estado de verificacion: " + nuevoEstadoVerificacion);

        try ( Connection conexion = this.conexion();  PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, nuevoEstadoVerificacion);
            ps.setInt(2, idCliente);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return true;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar el estado de verificación: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return false;
    }

    public List<Habitaciones> traerHabsHotel(int idHotel) {
        String sqlHabitaciones = "SELECT * FROM habitaciones WHERE Fk_Id_Hotel = ?";

        List<Habitaciones> habitaciones = new ArrayList<>();
        try ( Connection con = this.conexion();  PreparedStatement ps = con.prepareStatement(sqlHabitaciones);) {

            ps.setInt(1, idHotel);
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Habitaciones hab = new Habitaciones();
                    hab.setIdHabitacion(rs.getInt("Id_Habitacion"));
                    hab.setNumHabitacion(rs.getString("Num_Habitacion"));
                    hab.setTipoHabitacion(rs.getString("Tipo_Habitacion"));
                    hab.setEstadoHabitacion(rs.getString("Estado_Habitacion"));
                    hab.setFkIdHotel(rs.getInt("Fk_Id_Hotel"));

                    habitaciones.add(hab);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al traer habitaciones de la base de datos " + e.getMessage());
        }
        return habitaciones;
    }

    public void traerVendedores(JComboBox<String> caja, int idTipo) {
        caja.removeAllItems();
        caja.addItem("Seleccione Vendedor");
        String sqlV = "SELECT Usuario FROM usuarios WHERE Id_Tipo = ?";
        try ( Connection con = this.conexion();  PreparedStatement ps = con.prepareStatement(sqlV)) {
            ps.setInt(1, idTipo);
            try ( ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    caja.addItem(rs.getString("Usuario"));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al traer Vendedores " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        caja.addItem("Otro");
    }

    public void traerTiposdeDocumento(JComboBox<String> cajaDocumentos) {
        cajaDocumentos.removeAllItems();
        cajaDocumentos.addItem("Seleccionar");

        String sqlDocs = "SELECT Tipo_Documento FROM tipo_documento_tabla";
        try ( Connection con = this.conexion();  PreparedStatement ps = con.prepareStatement(sqlDocs)) {
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cajaDocumentos.addItem(rs.getString("Tipo_Documento"));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al traer tipose de documentos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        cajaDocumentos.addItem("OTRO");
    }

    public void traerDepartementos(JComboBox<String> caja1, JComboBox<String> caja2) {
        if (caja1 != null) {
            caja1.removeAllItems();
            caja1.addItem("Seleccionar");
        }
        if (caja2 != null) {
            caja2.removeAllItems();
            caja2.addItem("Seleccionar");
        }

        String sqlDep = "SELECT Nombre_Departamento FROM departamentos_colombia ORDER BY Nombre_Departamento";
        try ( Connection con = this.conexion();  PreparedStatement ps = con.prepareStatement(sqlDep)) {
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String depto = rs.getString("Nombre_Departamento");
                    if (caja1 != null) {
                        caja1.addItem(depto);
                    }
                    if (caja2 != null) {
                        caja2.addItem(depto);
                    }
                }
            }
            if (caja1 != null) {
                caja1.addItem("OTRO");
            }
            if (caja2 != null) {
                caja2.addItem("OTRO");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener Departamentos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public int obtenerIdDepartamento(String nombreDepartamento) {
        String sqlDepartamento = "SELECT Id_Departamento FROM departamentos_colombia WHERE Nombre_Departamento = ?";

        try ( Connection con = this.conexion();  PreparedStatement ps = con.prepareStatement(sqlDepartamento)) {
            ps.setString(1, nombreDepartamento);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Id_Departamento");
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener ID del Departamento: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return -1; // Retorna -1 si no se encontró el departamento
    }

    public void traerCiudadesPorDepartamento(JComboBox<String> comboBox, int idDepartamento) {
        Connection con = this.conexion();
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlCiudades = "SELECT Nombre_Ciudad FROM ciudades_colombia WHERE Fk_Id_Departamento = ? ORDER BY Nombre_Ciudad";
        try {
            ps = con.prepareStatement(sqlCiudades);
            ps.setInt(1, idDepartamento);
            rs = ps.executeQuery();
            comboBox.removeAllItems();
            comboBox.addItem("Seleccionar");
            while (rs.next()) {
                String ciudad = rs.getString("Nombre_Ciudad");
                comboBox.addItem(ciudad);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener Ciudades: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al cerrar la conexion: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void traerPaises(JComboBox<String> caja1, JComboBox<String> caja2, JComboBox<String> caja3) {
        Connection con = this.conexion();
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlPaises = "SELECT Nombre_Pais FROM pais ORDER BY Nombre_Pais";
        try {
            ps = con.prepareStatement(sqlPaises);
            rs = ps.executeQuery();
            if (caja1 != null) {
                caja1.removeAllItems();
                caja1.addItem("Seleccionar");
            }
            if (caja2 != null) {
                caja2.removeAllItems();
                caja2.addItem("Seleccionar");
            }
            if (caja3 != null) {
                caja3.removeAllItems();
                caja3.addItem("Seleccionar");
            }
            while (rs.next()) {
                String paisCP = rs.getString("Nombre_Pais");
                if (caja1 != null) {
                    caja1.addItem(paisCP.trim());
                }
                String[] cadena = paisCP.split("\\(");
                String paisSP = cadena[0].trim();
                if (caja2 != null) {
                    caja2.addItem(paisSP);
                }
                if (caja3 != null) {
                    caja3.addItem(paisSP);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener Paises: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al cerrar la conexion: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void usuariosParaCbx(JComboBox<String> cbxColaborador) {

        Connection con = this.conexion();
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT Nombres, Apellidos FROM usuarios";

        try {
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            cbxColaborador.removeAllItems();
            cbxColaborador.addItem("Seleccione");
            while (rs.next()) {
                cbxColaborador.addItem(rs.getString("Nombres").split(" ")[0] + " " + rs.getString("Apellidos").split(" ")[0]);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener colaboradores: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (ps != null) {
                    ps.close();;
                }
                if (con != null) {
                    con.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error al cerrar la conexion: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    public Check_In registrarIngreso(Check_In checkIn) {
        try {
            Connection conexion = this.conexion();
            String sqlcliente = "INSERT INTO check_in (Num_Habitacion, Fecha_Ingreso, Hora_llegada, Fecha_Salida, "
                    + "Procedencia, Destino, Fk_Id_Cliente, Fk_Id_Habitacion, Fk_Id_Turno, Fk_Id_Hotel)"
                    + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement sentencia = conexion.prepareStatement(sqlcliente, Statement.RETURN_GENERATED_KEYS);
            sentencia.setString(1, checkIn.getNumHabitacion());
            sentencia.setDate(2, new java.sql.Date(checkIn.getFechaIngreso().getTime()));
            sentencia.setString(3, checkIn.getHoraLlegada());

            // Verificar si getFechaSalida() es null
            java.sql.Date fechaSalidaSql = checkIn.getFechaSalida() != null
                    ? new java.sql.Date(checkIn.getFechaSalida().getTime()) : null;
            sentencia.setDate(4, fechaSalidaSql);

            sentencia.setString(5, checkIn.getProcedencia());
            sentencia.setString(6, checkIn.getDestino());
            sentencia.setInt(7, checkIn.getFkIdCliente());
            sentencia.setInt(8, checkIn.getFkIdHabitacion());
            sentencia.setInt(9, checkIn.getFkIdTurno());
            sentencia.setInt(10, checkIn.getFkIdHotel());

            sentencia.executeUpdate();
            ResultSet rs = sentencia.getGeneratedKeys();
            if (rs.next()) {
                checkIn.setIdCheckIn(rs.getInt(1));
            }
            conexion.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar check_in: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return checkIn;
    }

    public boolean registrarGasto(double valorGasto, String tipoGasto, String descripcion, int FkTurno) {
        Connection con = this.conexion();
        PreparedStatement ps = null;
        String sqlGasto = "INSERT INTO gastos (Valor_Gasto, Tipo_Gasto, Descripcion, Fk_Id_Turno) VALUES (?,?,?,?)";
        try {
            ps = con.prepareStatement(sqlGasto);
            ps.setDouble(1, valorGasto);
            ps.setString(2, tipoGasto);
            ps.setString(3, descripcion);
            ps.setInt(4, FkTurno);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar gasto: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al cerrar la conexion: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

        }

    }

    public boolean registrarIngresoEx(String tipoPago, double ValorIngreso, String descripcion, int Fk_Turno, int Fk_Cliente) {

        Connection con = this.conexion();
        PreparedStatement ps = null;
        String sql = "INSERT INTO ingreso_extra (Tipo_Pago, Valor_Ingreso, Descripcion, Fk_Id_Turno, Fk_Id_Cliente) VALUES (?,?,?,?,?)";
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, tipoPago);
            ps.setDouble(2, ValorIngreso);
            ps.setString(3, descripcion);
            ps.setInt(4, Fk_Turno);
            ps.setInt(5, Fk_Cliente);

            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar el ingreso: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al cerrar la conexion: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    public Contable registrarContable(Contable contable) {
        Connection con = this.conexion();
        try {
            String sqlcliente = "INSERT INTO contable(Tipo_Pago, Cant_Noches, Valor_Habitacion,"
                    + "Total_Pago, Comision, Total_Neto, Fk_Id_Cliente, Fk_Id_Check_In, Fk_Id_Turno, Fk_Id_Usuario)"
                    + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement sentencia = con.prepareStatement(sqlcliente, Statement.RETURN_GENERATED_KEYS);
            sentencia.setString(1, contable.getTipoPago());
            sentencia.setInt(2, contable.getCantNoches());
            sentencia.setBigDecimal(3, contable.getValorHabitacion());
            sentencia.setBigDecimal(4, contable.getTotalPago());
            sentencia.setBigDecimal(5, contable.getComision());
            sentencia.setBigDecimal(6, contable.getTotalNeto());
            sentencia.setInt(7, contable.getFkIdCliente());
            sentencia.setInt(8, contable.getFkIdCheckIn());
            sentencia.setInt(9, contable.getFkIdTurno());
            sentencia.setInt(10, contable.getFkIdUsuario());

            int affectedRows = sentencia.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try ( ResultSet generatedKeys = sentencia.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    contable.setIdValores(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar valores contables: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return contable;
    }

    public Hotel registrarHotel(String nomHot, String rntHot, String dirHot, String telHot, String ciudadHotel, String depHot, String paisHotel, String slogan, String estado) {
        Connection conexion = this.conexion();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Hotel xHotel = null;

        String sqlHotel = "INSERT INTO hoteles(Nombre_Hotel, Rnt_Hotel, Direccion_Hotel, Telefono_Hotel, Ciudad_Hotel, Departamento_Hotel, Pais_Hotel, Slogan_Hotel, Estado) VALUES(?,?,?,?,?,?,?,?,?)";

        try {
            ps = conexion.prepareStatement(sqlHotel, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, nomHot);
            ps.setString(2, rntHot);
            ps.setString(3, dirHot);
            ps.setString(4, telHot);
            ps.setString(5, ciudadHotel);
            ps.setString(6, depHot);
            ps.setString(7, paisHotel);
            ps.setString(8, slogan);
            ps.setString(9, estado);
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int idGenerado = rs.getInt(1);
                xHotel = new Hotel(idGenerado, nomHot, rntHot, dirHot, telHot, ciudadHotel, depHot, paisHotel, slogan, estado);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al Registrar el hotel: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conexion != null) {
                    conexion.close();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al cerrar la conexion: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return xHotel;
    }

    public Hotel buscarHotelPorRnt(String rnt) {
        Connection conexion = this.conexion();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Hotel hotelEncontrado = null;

        String sql = "SELECT * FROM hoteles WHERE Rnt_Hotel = ?";
        try {
            ps = conexion.prepareStatement(sql);
            ps.setString(1, rnt);
            rs = ps.executeQuery();

            if (rs.next()) {
                int idHoteles = rs.getInt("Id_Hoteles");
                String nombreHotel = rs.getString("Nombre_Hotel");
                String direccionHotel = rs.getString("Direccion_Hotel");
                String telefonoHotel = rs.getString("Telefono_Hotel");
                String ciudadHotel = rs.getString("Ciudad_Hotel");
                String departamentoHotel = rs.getString("Departamento_Hotel");
                String paisHotel = rs.getString("Pais_Hotel");
                String sloganHotel = rs.getString("Slogan_Hotel");
                String estadoHotel = rs.getString("Estado");

                hotelEncontrado = new Hotel(idHoteles, nombreHotel, rnt, direccionHotel, telefonoHotel, ciudadHotel, departamentoHotel, paisHotel, sloganHotel, estadoHotel);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar el hotel: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conexion != null) {
                    conexion.close();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al cerrar la conexión: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return hotelEncontrado;
    }

    public int registrarReserva(Date fechaLlegada, String horaLlegada, Date fechaSalida, int NumHabitacion, double valorNoche,
            int cantNoches, double ValorTotal, String Verificacion, String estado, int fkIdCliente, int fkIdTurno, int Fk_Id_Hotel) {

        int idGenerado = -1;

        String sqlReserva = "INSERT INTO reserva(Fecha_Llegada, Hora_Llegada, Fecha_Salida, Num_Habitacion, Valor_Noche, Cant_Noches,"
                + "Valor_Total, Verificacion, Estado_Reserva, Fk_Id_Cliente, Fk_Id_Turno, Fk_Id_Hotel)"
                + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";

        try ( Connection conexion = this.conexion();  PreparedStatement ps = conexion.prepareStatement(sqlReserva, Statement.RETURN_GENERATED_KEYS)) {

            // Convertir java.util.Date a java.sql.Date
            java.sql.Date sqlFechaLlegada = new java.sql.Date(fechaLlegada.getTime());
            java.sql.Date sqlFechaSalida = new java.sql.Date(fechaSalida.getTime());

            // Reemplazar en el PreparedStatement
            ps.setDate(1, sqlFechaLlegada);
            ps.setString(2, horaLlegada);
            ps.setDate(3, sqlFechaSalida);
            ps.setInt(4, NumHabitacion);
            ps.setDouble(5, valorNoche);
            ps.setInt(6, cantNoches);
            ps.setDouble(7, ValorTotal);
            ps.setString(8, Verificacion);
            ps.setString(9, estado);
            ps.setInt(10, fkIdCliente);
            ps.setInt(11, fkIdTurno);
            ps.setInt(12, Fk_Id_Hotel);

            ps.executeUpdate();

            // Obtener el ID generado
            try ( ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    idGenerado = rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar reserva: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Devolver el ID generado
        return idGenerado;
    }

    public int IdClienteViejo(String columna, String tabla, String columnaRef, String idSolicitado) {
        PreparedStatement ps = null;
        ResultSet rs;
        Connection con = this.conexion();
        String sql = "SELECT " + columna + " FROM " + tabla + " WHERE " + columnaRef + " = ? ";
        int id = 0;
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, idSolicitado);
            rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt(columna);

            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener Id del cliente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return id;
    }

    public int obtenerUltimoIdTurno(String columna, String tabla, String columnRef) {
        PreparedStatement ps = null;
        ResultSet rs;
        Connection con = this.conexion();
        String sql = "SELECT " + columna + " FROM " + tabla + " ORDER BY " + columnRef + " DESC LIMIT 1";
        int ultimoIdTurno = 0;

        try {
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()) {
                ultimoIdTurno = rs.getInt("Id_Turno");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener el Id: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return ultimoIdTurno;
    }

    public int registrarTurno(LocalDateTime inicio, LocalDateTime fin, String estadoTurno, String criterioCaja, int FkIdUsuario, int FkIdHotel) {
        Connection conexion = this.conexion();
        PreparedStatement ps = null;
        ResultSet rs = null;
        int idGenerado = -1;

        String sqlHotel = "INSERT INTO turno(Inicio, Fin, Estado_Turno, Criterio_caja, Fk_Id_Usuario, Fk_Id_Hotel)"
                + " VALUES(?,?,?,?,?,?)";
        try {
            ps = conexion.prepareStatement(sqlHotel, Statement.RETURN_GENERATED_KEYS);
            ps.setTimestamp(1, Timestamp.valueOf(inicio));
            ps.setTimestamp(2, Timestamp.valueOf(fin));
            ps.setString(3, estadoTurno);
            ps.setString(4, criterioCaja);
            ps.setInt(5, FkIdUsuario);
            ps.setInt(6, FkIdHotel);

            ps.executeUpdate();

            // Obtener el ID generado
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                idGenerado = rs.getInt(1);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al crear el turno: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                // Cerrar recursos utilizados
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conexion != null) {
                    conexion.close();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al cerrar la conexion: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        // Devolver el ID generado
        return idGenerado;
    }

    public boolean pasarCajaCerrada(int IdTurno) {
        Connection con = this.conexion();
        PreparedStatement ps = null;
        String sqlCerrar = "UPDATE turno SET Criterio_Caja = 'Cerrada' WHERE Id_Turno = ? ";
        try {
            ps = con.prepareStatement(sqlCerrar);
            ps.setInt(1, IdTurno);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cerrar la caja : " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean terminarTurno(int IdTurno) {
        Connection con = this.conexion();
        PreparedStatement ps = null;
        String sqlUp = "UPDATE turno SET Estado_Turno = 'Terminado' WHERE Id_Turno = ? ";
        try {
            ps = con.prepareStatement(sqlUp);
            ps.setInt(1, IdTurno);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al terminar el turno: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al cerrar la conexion: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public boolean insertarDatosCaja(LocalDate fechaCuadre, double tVentaDias, double tComisionDia, double tNetoDia, double tGastos,
            double tIngresosEx, double recFinal, int habsVendidas, int habsQuedadas, double promedioHab, double promPerdida,
            int Fk_Id_Turno, int Fk_Id_Usuario, int Fk_Id_Hotel) {
        Connection con = this.conexion();
        PreparedStatement ps = null;

        String sqlCaja = "INSERT INTO cuadre_diario(Fecha_Cuadre, Total_Venta_Dia, Total_Comision_Dia, Total_Neto_Dia, Total_Gastos_Dia, "
                + "Total_Ingresos_Extra_Dia, Recaudo_Final_Dia, Habs_Vendidas_Dia, Habs_Quedadas_Dia, Promedio_Hab_Dia, "
                + "Promedio_Perdida_Dia, Fk_Id_Turno, Fk_Id_Usuario, Fk_Id_Hotel) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            ps = con.prepareStatement(sqlCaja);
            ps.setDate(1, java.sql.Date.valueOf(fechaCuadre));
            ps.setDouble(2, tVentaDias);
            ps.setDouble(3, tComisionDia);
            ps.setDouble(4, tNetoDia);
            ps.setDouble(5, tGastos);
            ps.setDouble(6, tIngresosEx);
            ps.setDouble(7, recFinal);
            ps.setInt(8, habsVendidas);
            ps.setInt(9, habsQuedadas);
            ps.setDouble(10, promedioHab);
            ps.setDouble(11, promPerdida);
            ps.setInt(12, Fk_Id_Turno);
            ps.setInt(13, Fk_Id_Usuario);
            ps.setInt(14, Fk_Id_Hotel);

            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar el valance del dia: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
                if (ps != null) {
                    ps.close();
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al cerrar la conexion: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    public boolean registrarHabitacion(String numHab, String tipHab, String estadoHab, int idHotel) {
        Connection conexion = this.conexion();
        PreparedStatement ps = null;
        String sqlHabitaciones = "INSERT INTO habitaciones(Num_Habitacion,Tipo_Habitacion,"
                + "Estado_Habitacion,Fk_Id_Hotel)"
                + " VALUES(?,?,?,?)";
        try {
            ps = conexion.prepareStatement(sqlHabitaciones);
            ps.setString(1, numHab);
            ps.setString(2, tipHab);
            ps.setString(3, estadoHab);
            ps.setInt(4, idHotel);

            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al crear la habitacion: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } finally {
            try {
                // Cerrar recursos utilizados
                if (ps != null) {
                    ps.close();
                }
                if (conexion != null) {
                    conexion.close();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al cerrar la conexion: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    public void anularReserva(int IdReserva) {
        Connection conex = conexion();
        try {
            String consulta = "UPDATE reserva SET Estado_Reserva = 'Anulada' WHERE Id_Reserva = ?";
            PreparedStatement ps = conex.prepareStatement(consulta);
            ps.setInt(1, IdReserva);
            ps.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al anular reserva: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (conex != null) {
                    conex.close();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al cerrar la conexion: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void ReservaExitosa(int IdReserva) {
        Connection conex = conexion();
        try {
            String consulta = "UPDATE reserva SET Estado_Reserva = 'Exitosa' WHERE Id_Reserva = ?";
            PreparedStatement ps = conex.prepareStatement(consulta);
            ps.setInt(1, IdReserva);
            ps.executeUpdate();
            System.out.println("pasamos por reservas");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar reserva: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (conex != null) {
                    conex.close();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al cerrar la conexion: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public String[] obtenerDatosReserva(int numeroHabitacion, Date fechaSeleccionada, int idHotel) {
        String[] datosReserva = new String[12];
        String consulta = "SELECT c.Id_cliente, c.Nombres, c.Apellidos, c.Telefono, c.Num_Documento, r.Fecha_Llegada, r.Hora_Llegada, r.Fecha_Salida, r.Valor_Noche, "
                + "r.Valor_Total, r.Id_Reserva, r.Verificacion "
                + "FROM reserva r "
                + "INNER JOIN cliente c ON r.Fk_Id_Cliente = c.Id_Cliente "
                + "WHERE r.Num_Habitacion = ? AND r.Fecha_Llegada <= ? AND r.Fecha_Salida >= ? AND r.Fk_Id_Hotel = ? "
                + "ORDER BY r.Id_Reserva DESC LIMIT 1";

        try ( Connection conex = conexion();  PreparedStatement ps = conex.prepareStatement(consulta);) {

            ps.setInt(1, numeroHabitacion);
            java.sql.Date fechaSql = new java.sql.Date(fechaSeleccionada.getTime());
            ps.setDate(2, fechaSql);
            ps.setDate(3, fechaSql);
            ps.setInt(4, idHotel);
            try ( ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    datosReserva[0] = rs.getString("Nombres");
                    datosReserva[1] = rs.getString("Apellidos");
                    datosReserva[2] = rs.getString("Telefono");
                    datosReserva[3] = rs.getString("Num_Documento");
                    datosReserva[4] = rs.getString("Fecha_Llegada");
                    datosReserva[5] = rs.getString("Hora_Llegada");
                    datosReserva[6] = rs.getString("Fecha_Salida");
                    datosReserva[7] = rs.getString("Valor_Noche");
                    datosReserva[8] = rs.getString("Valor_Total");
                    datosReserva[9] = rs.getString("Id_Reserva");
                    datosReserva[10] = rs.getString("Verificacion");
                    datosReserva[11] = rs.getString("Id_Cliente");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener datos de reserva: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return datosReserva;
    }

    public String[] obtenerDatosRegistro(int numeroHabitacion, int idHotel) {
        String[] datosRegistro = new String[15];
        String consulta = "SELECT  c.Nombres, c.Apellidos, c.Telefono, c.Estado_Verificacion, c.Num_Documento, c.Tipo_Documento, "
                + "c.Fecha_Nacimiento, c.Nacionalidad, ci.Fecha_Ingreso, ci.Hora_Llegada, ci.Fecha_Salida, ci.Procedencia, ci.Destino,"
                + " co.Valor_Habitacion, co.Total_Pago "
                + "FROM check_in ci "
                + "INNER JOIN cliente c ON ci.Fk_Id_Cliente = c.Id_Cliente "
                + "INNER JOIN contable co ON ci.Fk_Id_Cliente = co.Fk_Id_Cliente "
                + "WHERE ci.Num_Habitacion = ? AND ci.Fk_Id_Hotel = ? "
                + "ORDER BY ci.Id_Reserva DESC, co.Id_Valores DESC "
                + "LIMIT 1";
        try ( Connection conex = conexion();  PreparedStatement ps = conex.prepareStatement(consulta)) {

            ps.setInt(1, numeroHabitacion);
            ps.setInt(2, idHotel);

            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    datosRegistro[0] = rs.getString("Nombres");
                    datosRegistro[1] = rs.getString("Apellidos");
                    datosRegistro[2] = rs.getString("Telefono");
                    datosRegistro[3] = rs.getString("Fecha_Ingreso");
                    datosRegistro[4] = rs.getString("Hora_Llegada");
                    datosRegistro[5] = rs.getString("Fecha_Salida");
                    datosRegistro[6] = rs.getString("Valor_Habitacion");
                    datosRegistro[7] = rs.getString("Total_Pago");
                    datosRegistro[8] = rs.getString("Estado_Verificacion");
                    datosRegistro[9] = rs.getString("Num_Documento");
                    datosRegistro[10] = rs.getString("Tipo_Documento");
                    datosRegistro[11] = rs.getString("Fecha_Nacimiento");
                    datosRegistro[12] = rs.getString("Nacionalidad");
                    datosRegistro[13] = rs.getString("Procedencia");
                    datosRegistro[14] = rs.getString("Destino");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener datos de registro: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return datosRegistro;
    }

    public boolean liberarHabitacion(int numeroHabitacion, int idHotel) {

        String consulta = "UPDATE habitaciones SET Estado_Habitacion = 'Libre' WHERE Num_Habitacion = ? AND Fk_Id_Hotel = ?";
        try ( Connection conexion = this.conexion();  PreparedStatement sp = conexion.prepareStatement(consulta);) {

            sp.setInt(1, numeroHabitacion);
            sp.setInt(2, idHotel);

            int resultado = sp.executeUpdate();

            return resultado > 0;
        } catch (SQLException e) {
            // Log the error message or handle it accordingly
            System.out.println("no se pudo actualizar la habitacion desde el metodo");
            return false;

        }
    }

    public Hotel existeHotel(String rnt) {

        System.out.println("entramos al metodo para buscar el hotel: " + rnt);
        PreparedStatement ps = null;
        ResultSet rs;
        Connection conn = conexion();
        String sql = "SELECT * FROM hoteles WHERE Rnt_Hotel = ?";
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, rnt);
            rs = ps.executeQuery();
            if (rs.next()) {
                Hotel hotel = new Hotel();
                hotel.setIdHoteles(rs.getInt("Id_Hoteles"));
                hotel.setNombreHotel(rs.getString("Nombre_Hotel"));
                hotel.setRntHotel(rs.getString("Rnt_Hotel"));
                hotel.setDireccionHotel(rs.getString("Direccion_Hotel"));
                hotel.setTelefonoHotel(rs.getString("Telefono_Hotel"));
                hotel.setCiudadHotel(rs.getString("Ciudad_Hotel"));
                hotel.setDepartamentoHotel(rs.getString("Departamento_Hotel"));
                hotel.setPaisHotel(rs.getString("Pais_Hotel"));
                hotel.setSloganHotel(rs.getString("Slogan_Hotel"));
                hotel.setEstadoHotel(rs.getString("Estado"));
                return hotel;
            }
            return null; // Si no se encuentra el hotel, devolver null
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar hotel: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null; // Si hay una excepción, devolver null
        }
    }

}
