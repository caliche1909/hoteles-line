package modelo;

import java.time.LocalDateTime;

public class UsuarioOperando {

    private int turnoPresente;
    private boolean turnoNCreado;
    private int idUsuario;
    private LocalDateTime ultima_secion;
    private String cedula;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String correo;
    private String usuario;
    private String contrasenia;
    private int id_tipo;
    private String roll_usuarios;
    private int idHotel;

    public UsuarioOperando() {
    }

    public UsuarioOperando(int turnoPresente, boolean turnoNCreado, int idUsuario, LocalDateTime ultima_secion, String cedula, String nombres, String apellidos, String telefono, String correo, String usuario, String contrasenia, int id_tipo, String roll_usuarios, int idHotel) {
        this.turnoPresente = turnoPresente;
        this.turnoNCreado = turnoNCreado;
        this.idUsuario = idUsuario;
        this.ultima_secion = ultima_secion;
        this.cedula = cedula;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.correo = correo;
        this.usuario = usuario;
        this.contrasenia = contrasenia;
        this.id_tipo = id_tipo;
        this.roll_usuarios = roll_usuarios;
        this.idHotel = idHotel;
    }    

    public boolean getTurnoNCreado() {
        return turnoNCreado;
    }

    public void setTurnoNCreado(boolean turnoNCreado) {
        this.turnoNCreado = turnoNCreado;
    }

    public int getTurnoPresente() {
        return turnoPresente;
    }

    public void setTurnoPresente(int turnoPresente) {
        this.turnoPresente = turnoPresente;
    }

    public int getIdHotel() {
        return idHotel;
    }

    public void setIdHotel(int idHotel) {
        this.idHotel = idHotel;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDateTime getUltima_secion() {
        return ultima_secion;
    }

    public void setUltima_secion(LocalDateTime ultima_secion) {
        this.ultima_secion = ultima_secion;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public int getId_tipo() {
        return id_tipo;
    }

    public void setId_tipo(int id_tipo) {
        this.id_tipo = id_tipo;
    }

    public String getRoll_usuarios() {
        return roll_usuarios;
    }

    public void setRoll_usuarios(String roll_usuarios) {
        this.roll_usuarios = roll_usuarios;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return String.format(
                """
            UsuarioOperando: -------------------------------------------------------
            Turno Presente: %s
            Turno No Creado: %s
            ID Usuario: %d
            Última Sesión: %s
            Cédula: %s
            Nombres: %s
            Apellidos: %s
            Teléfono: %s
            Correo: %s
            Usuario: %s
            Contraseña: %s
            ID Tipo: %d
            Rol Usuarios: %s
            ID Hotel: %d
            \n""",
                turnoPresente, turnoNCreado, idUsuario, ultima_secion, cedula,
                nombres, apellidos, telefono, correo, usuario,
                contrasenia, id_tipo, roll_usuarios, idHotel
        );
    }
}
