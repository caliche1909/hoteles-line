package modelo;


import java.util.Date;



public class Check_In {

    private int idCheckIn;
    private String numHabitacion;
    private Date fechaIngreso;
    private String horaLlegada;
    private Date fechaSalida;
    private String procedencia;
    private String destino;
    private int fkIdCliente;
    private int fkIdHabitacion;
    private int fkIdTurno;
    private int fkIdHotel;

    public Check_In() {
    }

    public Check_In(int idCheckIn, String numHabitacion, Date fechaIngreso, String horaLlegada, Date fechaSalida, String procedencia, String destino, int fkIdCliente, int fkIdHabitacion, int fkIdTurno, int fkIdHotel) {
        this.idCheckIn = idCheckIn;
        this.numHabitacion = numHabitacion;
        this.fechaIngreso = fechaIngreso;
        this.horaLlegada = horaLlegada;
        this.fechaSalida = fechaSalida;
        this.procedencia = procedencia;
        this.destino = destino;
        this.fkIdCliente = fkIdCliente;
        this.fkIdHabitacion = fkIdHabitacion;
        this.fkIdTurno = fkIdTurno;
        this.fkIdHotel = fkIdHotel;
    }

    public int getIdCheckIn() {
        return idCheckIn;
    }

    public void setIdCheckIn(int idCheckIn) {
        this.idCheckIn = idCheckIn;
    }

    public String getNumHabitacion() {
        return numHabitacion;
    }

    public void setNumHabitacion(String numHabitacion) {
        this.numHabitacion = numHabitacion;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getHoraLlegada() {
        return horaLlegada;
    }

    public void setHoraLlegada(String horaLlegada) {
        this.horaLlegada = horaLlegada;
    }

    public Date getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(Date fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public String getProcedencia() {
        return procedencia;
    }

    public void setProcedencia(String procedencia) {
        this.procedencia = procedencia;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public int getFkIdCliente() {
        return fkIdCliente;
    }

    public void setFkIdCliente(int fkIdCliente) {
        this.fkIdCliente = fkIdCliente;
    }

    public int getFkIdHabitacion() {
        return fkIdHabitacion;
    }

    public void setFkIdHabitacion(int fkIdHabitacion) {
        this.fkIdHabitacion = fkIdHabitacion;
    }

    public int getFkIdTurno() {
        return fkIdTurno;
    }

    public void setFkIdTurno(int fkIdTurno) {
        this.fkIdTurno = fkIdTurno;
    }

    public int getFkIdHotel() {
        return fkIdHotel;
    }

    public void setFkIdHotel(int fkIdHotel) {
        this.fkIdHotel = fkIdHotel;
    }

    @Override
    public String toString() {
        return String.format(
                """
        Check_In: -------------------------------------------------------
        idReserva: %d
        numHabitacion: %s
        fechaIngreso: %s
        horaLlegada: %s
        fechaSalida: %s
        procedencia: %s
        destino: %s
        fkIdCliente: %d
        fkIdHabitacion: %d
        fkIdTurno: %d
        fkIdHotel: %d
        \n""",
                idCheckIn, numHabitacion, fechaIngreso, horaLlegada, fechaSalida,
                procedencia, destino, fkIdCliente, fkIdHabitacion, fkIdTurno, fkIdHotel
        );
    }

    

}
