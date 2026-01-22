package modelo;

import java.math.BigDecimal;

public class Contable {

    private int idValores;
    private String tipoPago;
    private int cantNoches;
    private BigDecimal valorHabitacion;
    private BigDecimal totalPago;
    private BigDecimal comision;
    private BigDecimal totalNeto;
    private int fkIdCliente;
    private int fkIdCheckIn;
    private int fkIdTurno;
    private int fkIdUsuario;

    public Contable() {
    }

    public Contable(int idValores, String tipoPago, int cantNoches, BigDecimal valorHabitacion, BigDecimal totalPago, BigDecimal comision, BigDecimal totalNeto, int fkIdCliente, int fkIdCheckIn, int fkIdTurno, int fkIdUsuario) {
        this.idValores = idValores;
        this.tipoPago = tipoPago;
        this.cantNoches = cantNoches;
        this.valorHabitacion = valorHabitacion;
        this.totalPago = totalPago;
        this.comision = comision;
        this.totalNeto = totalNeto;
        this.fkIdCliente = fkIdCliente;
        this.fkIdCheckIn = fkIdCheckIn;
        this.fkIdTurno = fkIdTurno;
        this.fkIdUsuario = fkIdUsuario;
    }

    public int getIdValores() {
        return idValores;
    }

    public void setIdValores(int idValores) {
        this.idValores = idValores;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public int getCantNoches() {
        return cantNoches;
    }

    public void setCantNoches(int cantNoches) {
        this.cantNoches = cantNoches;
    }

    public BigDecimal getValorHabitacion() {
        return valorHabitacion;
    }

    public void setValorHabitacion(BigDecimal valorHabitacion) {
        this.valorHabitacion = valorHabitacion;
    }

    public BigDecimal getTotalPago() {
        return totalPago;
    }

    public void setTotalPago(BigDecimal totalPago) {
        this.totalPago = totalPago;
    }

    public BigDecimal getComision() {
        return comision;
    }

    public void setComision(BigDecimal comision) {
        this.comision = comision;
    }

    public BigDecimal getTotalNeto() {
        return totalNeto;
    }

    public void setTotalNeto(BigDecimal totalNeto) {
        this.totalNeto = totalNeto;
    }

    public int getFkIdCliente() {
        return fkIdCliente;
    }

    public void setFkIdCliente(int fkIdCliente) {
        this.fkIdCliente = fkIdCliente;
    }

    public int getFkIdCheckIn() {
        return fkIdCheckIn;
    }

    public void setFkIdCheckIn(int fkIdCheckIn) {
        this.fkIdCheckIn = fkIdCheckIn;
    }

    public int getFkIdTurno() {
        return fkIdTurno;
    }

    public void setFkIdTurno(int fkIdTurno) {
        this.fkIdTurno = fkIdTurno;
    }

    public int getFkIdUsuario() {
        return fkIdUsuario;
    }

    public void setFkIdUsuario(int fkIdUsuario) {
        this.fkIdUsuario = fkIdUsuario;
    }

    @Override
    public String toString() {
        return String.format("""
        Contable {
        idValores = %d,
        tipoPago = '%s',
        cantNoches = %d,
        valorHabitacion = %.2f,
        totalPago = %.2f,
        comision = %.2f,
        totalNeto = %.2f,
        fkIdCliente = %d,
        fkIdCheckIn = %d,
        fkIdTurno = %d,
        fkIdUsuario = %d
        }
        \n""",
                idValores, tipoPago, cantNoches, valorHabitacion, totalPago, comision, totalNeto, fkIdCliente, fkIdCheckIn, fkIdTurno, fkIdUsuario);
    }

}
