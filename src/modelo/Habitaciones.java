package modelo;


public class Habitaciones {
    private int idHabitacion;
    private String numHabitacion;
    private String tipoHabitacion;
    private String estadoHabitacion;
    private int fkIdHotel;

    public int getIdHabitacion() {
        return idHabitacion;
    }

    public void setIdHabitacion(int idHabitacion) {
        this.idHabitacion = idHabitacion;
    }

    public String getNumHabitacion() {
        return numHabitacion;
    }

    public void setNumHabitacion(String numHabitacion) {
        this.numHabitacion = numHabitacion;
    }

    public String getTipoHabitacion() {
        return tipoHabitacion;
    }

    public void setTipoHabitacion(String tipoHabitacion) {
        this.tipoHabitacion = tipoHabitacion;
    }

    public String getEstadoHabitacion() {
        return estadoHabitacion;
    }

    public void setEstadoHabitacion(String estadoHabitacion) {
        this.estadoHabitacion = estadoHabitacion;
    }

    public int getFkIdHotel() {
        return fkIdHotel;
    }

    public void setFkIdHotel(int fkIdHotel) {
        this.fkIdHotel = fkIdHotel;
    }     
}
