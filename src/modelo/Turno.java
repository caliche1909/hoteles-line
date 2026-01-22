package modelo;

import java.time.LocalDateTime;

public class Turno {

    private int idTurno;
    private LocalDateTime inicio;
    private LocalDateTime fin;
    private String estadoTurno;
    private String criterioCaja;
    private int fkIdUsuario;
    private int fkIdHotel;
    private UsuarioOperando usuario;

    public Turno() {
    }

    public Turno(int idTurno, LocalDateTime inicio, LocalDateTime fin, String estadoTurno, String criterioCaja, int fkIdUsuario, int fkIdHotel, UsuarioOperando usuario) {
        this.idTurno = idTurno;
        this.inicio = inicio;
        this.fin = fin;
        this.estadoTurno = estadoTurno;
        this.criterioCaja = criterioCaja;
        this.fkIdUsuario = fkIdUsuario;
        this.fkIdHotel = fkIdHotel;
        this.usuario = usuario;
    }

    public int getIdTurno() {
        return idTurno;
    }

    public void setIdTurno(int idTurno) {
        this.idTurno = idTurno;
    }
    
    public LocalDateTime getInicio() {
        return inicio;
    }

    public void setInicio(LocalDateTime inicio) {
        this.inicio = inicio;
    }

    public LocalDateTime getFin() {
        return fin;
    }

    public void setFin(LocalDateTime fin) {
        this.fin = fin;
    }

    public String getEstadoTurno() {
        return estadoTurno;
    }

    public void setEstadoTurno(String estadoTurno) {
        this.estadoTurno = estadoTurno;
    }

    public String getCriterioCaja() {
        return criterioCaja;
    }

    public void setCriterioCaja(String criterioCaja) {
        this.criterioCaja = criterioCaja;
    }
    
    

    public int getFkIdUsuario() {
        return fkIdUsuario;
    }

    public void setFkIdUsuario(int fkIdUsuario) {
        this.fkIdUsuario = fkIdUsuario;
    }

    public int getFkIdHotel() {
        return fkIdHotel;
    }

    public void setFkIdHotel(int fkIdHotel) {
        this.fkIdHotel = fkIdHotel;
    }
    public UsuarioOperando getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioOperando usuario) {
        this.usuario = usuario;
    }
    

    

    
    @Override
    public String toString() {
        return "Turno{"
                + "fechaInicio='" + inicio + '\''
                + ", fechaFin='" + fin + '\''
                + ", estadoTurno='" + estadoTurno + '\''
                + "'FkIdUsuario='" + fkIdUsuario + '\''
                + "'FkIdHotel='" + fkIdHotel + '\''
                + '}';
    }
}
