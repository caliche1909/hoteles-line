package modelo;

import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import org.mindrot.jbcrypt.BCrypt;

public class SqlUsuarios extends conectar.Canectar {

    public boolean registrar(UsuarioOperando usr) {
        PreparedStatement ps = null;
        Connection conn = conexion();
        String sql = "INSERT INTO usuarios (Cedula, Nombres, Apellidos, Telefono, Correo, Usuario, Contrasenia, Id_Tipo, Id_Hotel, Ultima_Secion) "
                + "VALUES(?,?,?,?,?,?,?,?,?,?)"; // <-- ahora son 10 signos de pregunta
        try {
            ps = conn.prepareStatement(sql);

            ps.setString(1, usr.getCedula());
            ps.setString(2, usr.getNombres());
            ps.setString(3, usr.getApellidos());
            ps.setString(4, usr.getTelefono());
            ps.setString(5, usr.getCorreo());
            ps.setString(6, usr.getUsuario());
            ps.setString(7, usr.getContrasenia());
            ps.setInt(8, usr.getId_tipo());
            ps.setInt(9, usr.getIdHotel());

            // 🚀 Aquí agregas el valor de Ultima_Secion
            if (usr.getUltima_secion() != null) {
                ps.setTimestamp(10, Timestamp.valueOf(usr.getUltima_secion()));
            } else {
                ps.setTimestamp(10, null); // o puedes poner NOW() en SQL si prefieres
            }

            ps.execute();
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public boolean registrarModificacion(UsuarioOperando usr) {
        PreparedStatement ps = null;
        Connection conn = conexion();
        String sql = "UPDATE usuarios SET Nombres=?, Apellidos=?, Cedula=?, Correo=?, Usuario=?, Contrasenia=?, Id_Tipo=?, Telefono=?, Id_Hotel=? WHERE Id_Usuario=?";
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, usr.getNombres());
            ps.setString(2, usr.getApellidos());
            ps.setString(3, usr.getCedula());
            ps.setString(4, usr.getCorreo());
            ps.setString(5, usr.getUsuario());
            ps.setString(6, usr.getContrasenia());
            ps.setInt(7, usr.getId_tipo());
            ps.setString(8, usr.getTelefono());
            ps.setInt(9, usr.getIdHotel());
            ps.setInt(10, usr.getIdUsuario());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }

    public UsuarioOperando login(UsuarioOperando usr) {
        Connection conn = null;
        try {
            conn = conexion();
            String sql = "SELECT u.*, t.Roll_Usuario "
                    + "FROM usuarios AS u "
                    + "INNER JOIN tipo_usuario AS t ON u.Id_Tipo = t.Id_Tipo_Usuario "
                    + "WHERE binary u.Usuario = ?";
            try ( PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, usr.getUsuario());
                try ( ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && BCrypt.checkpw(usr.getContrasenia(), rs.getString("Contrasenia"))) {
                        UsuarioOperando usuarioLogueado = new UsuarioOperando();
                        usuarioLogueado.setIdUsuario(rs.getInt("Id_Usuario"));
                        usuarioLogueado.setUltima_secion(rs.getTimestamp("Ultima_Secion").toLocalDateTime());
                        usuarioLogueado.setCedula(rs.getString("Cedula"));
                        usuarioLogueado.setNombres(rs.getString("Nombres"));
                        usuarioLogueado.setApellidos(rs.getString("Apellidos"));
                        usuarioLogueado.setTelefono(rs.getString("Telefono"));
                        usuarioLogueado.setCorreo(rs.getString("Correo"));
                        usuarioLogueado.setUsuario(rs.getString("Usuario"));
                        usuarioLogueado.setContrasenia(rs.getString("Contrasenia"));
                        usuarioLogueado.setId_tipo(rs.getInt("Id_Tipo"));
                        usuarioLogueado.setRoll_usuarios(rs.getString("Roll_Usuario"));
                        usuarioLogueado.setIdHotel(rs.getInt("Id_Hotel"));

                        // Actualizar la última sesión del usuario
                        String sqlUpdate = "UPDATE usuarios SET Ultima_Secion = NOW() WHERE Id_Usuario = ?";
                        try ( PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate)) {
                            psUpdate.setInt(1, usuarioLogueado.getIdUsuario());
                            psUpdate.executeUpdate();
                        }

                        return usuarioLogueado;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println(e);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
        return null; // Retorna null si no se encuentra el usuario o si la contraseña no coincide
    }

    public UsuarioOperando existeUsuario(String usuario) {
        Connection conn = conexion();
        String sql = "SELECT * FROM usuarios WHERE Usuario = ?";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UsuarioOperando usuarioEncontrado = new UsuarioOperando();
                    usuarioEncontrado.setIdUsuario(rs.getInt("Id_Usuario"));
                    usuarioEncontrado.setUltima_secion(rs.getTimestamp("Ultima_Secion").toLocalDateTime());
                    usuarioEncontrado.setCedula(rs.getString("Cedula"));
                    usuarioEncontrado.setNombres(rs.getString("Nombres"));
                    usuarioEncontrado.setApellidos(rs.getString("Apellidos"));
                    usuarioEncontrado.setTelefono(rs.getString("Telefono"));
                    usuarioEncontrado.setCorreo(rs.getString("Correo"));
                    usuarioEncontrado.setUsuario(rs.getString("Usuario"));
                    usuarioEncontrado.setContrasenia(rs.getString("Contrasenia"));
                    usuarioEncontrado.setId_tipo(rs.getInt("Id_Tipo"));
                    usuarioEncontrado.setIdHotel(rs.getInt("Id_Hotel"));
                    // Aquí puedes agregar el resto de los setters para los campos restantes

                    return usuarioEncontrado;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return null; // Retorna null si no se encontró el usuario o si ocurrió un error
    }

    public Turno obtenerUltimoTurno(int FkIdHotel) {
        Connection conexion = this.conexion();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Turno ultimoTurno = null;

        String sql = "SELECT turno.*, usuarios.* "
                + "FROM turno "
                + "INNER JOIN usuarios ON turno.Fk_Id_Usuario = usuarios.Id_Usuario "
                + "WHERE turno.Fk_Id_Hotel = ? "
                + "ORDER BY turno.Id_Turno DESC LIMIT 1";
        try {
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, FkIdHotel);

            rs = ps.executeQuery();

            // Si se encontró un turno, crea un nuevo objeto Turno y Usuario con los datos recuperados
            if (rs.next()) {
                ultimoTurno = new Turno();
                ultimoTurno.setIdTurno(rs.getInt("Id_Turno"));
                ultimoTurno.setInicio(rs.getTimestamp("Inicio").toLocalDateTime());
                ultimoTurno.setFin(rs.getTimestamp("Fin").toLocalDateTime());
                ultimoTurno.setEstadoTurno(rs.getString("Estado_Turno"));
                ultimoTurno.setCriterioCaja(rs.getString("Criterio_Caja"));
                ultimoTurno.setFkIdUsuario(rs.getInt("Fk_Id_Usuario"));
                ultimoTurno.setFkIdHotel(rs.getInt("Fk_Id_Hotel"));

                UsuarioOperando usuario = new UsuarioOperando();
                usuario.setIdUsuario(rs.getInt("Id_Usuario"));
                usuario.setUltima_secion(rs.getTimestamp("Ultima_Secion").toLocalDateTime());
                usuario.setCedula(rs.getString("Cedula"));
                usuario.setNombres(rs.getString("Nombres"));
                usuario.setApellidos(rs.getString("Apellidos"));
                usuario.setTelefono(rs.getString("Telefono"));
                usuario.setCorreo(rs.getString("Correo"));
                usuario.setUsuario(rs.getString("Usuario"));
                usuario.setContrasenia(rs.getString("Contrasenia"));
                usuario.setId_tipo(rs.getInt("Id_Tipo"));
                usuario.setIdHotel(rs.getInt("Id_Hotel"));

                ultimoTurno.setUsuario(usuario);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener el último turno: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(null, "Error al cerrar la conexión: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        return ultimoTurno;
    }

    public boolean esEmail(String correo) {
        Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher mather = pattern.matcher(correo);
        return mather.find();
    }

}
