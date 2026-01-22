package modelo;

public class DatosSire {

    String tipoMovimiento;
    String tipoDocumento;
    String fechaNacimiento;
    String numDocumento;
    String primerApeliido;
    String segundoApellido;
    String nombres;
    String nacionalidad;
    String paisProce;
    String deparProce;
    String ciudadProce;
    String paisDest;
    String deparDet;
    String ciudadDest;

    public DatosSire() {
    }

    public DatosSire(String tipoMovimiento, String tipoDocumento, String fechaNacimiento, String numDocumento, String primerApeliido, String segundoApellido, String nombres, String Nacionalidad, String paisProce, String deparProce, String ciudadProce, String paisDest, String deparDet, String ciudadDest) {
        this.tipoMovimiento = tipoMovimiento;
        this.tipoDocumento = tipoDocumento;
        this.fechaNacimiento = fechaNacimiento;
        this.numDocumento = numDocumento;
        this.primerApeliido = primerApeliido;
        this.segundoApellido = segundoApellido;
        this.nombres = nombres;
        this.nacionalidad = Nacionalidad;
        this.paisProce = paisProce;
        this.deparProce = deparProce;
        this.ciudadProce = ciudadProce;
        this.paisDest = paisDest;
        this.deparDet = deparDet;
        this.ciudadDest = ciudadDest;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getNumDocumento() {
        return numDocumento;
    }

    public void setNumDocumento(String numDocumento) {
        this.numDocumento = numDocumento;
    }

    public String getPrimerApeliido() {
        return primerApeliido;
    }

    public void setPrimerApeliido(String primerApeliido) {
        this.primerApeliido = primerApeliido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String Nacionalidad) {
        this.nacionalidad = Nacionalidad;
    }

    public String getPaisProce() {
        return paisProce;
    }

    public void setPaisProce(String paisProce) {
        this.paisProce = paisProce;
    }

    public String getDeparProce() {
        return deparProce;
    }

    public void setDeparProce(String deparProce) {
        this.deparProce = deparProce;
    }

    public String getCiudadProce() {
        return ciudadProce;
    }

    public void setCiudadProce(String ciudadProce) {
        this.ciudadProce = ciudadProce;
    }

    public String getPaisDest() {
        return paisDest;
    }

    public void setPaisDest(String paisDest) {
        this.paisDest = paisDest;
    }

    public String getDeparDet() {
        return deparDet;
    }

    public void setDeparDet(String deparDet) {
        this.deparDet = deparDet;
    }

    public String getCiudadDest() {
        return ciudadDest;
    }

    public void setCiudadDest(String ciudadDest) {
        this.ciudadDest = ciudadDest;
    }

    @Override
    public String toString() {
        return "DatosSire\n\n"
                + " tipoMovimiento=" + tipoMovimiento + "\n"
                + " tipoDocumento=" + tipoDocumento + "\n"
                + " fechaNacimiento=" + fechaNacimiento + "\n"
                + " numDocumento=" + numDocumento + "\n"
                + " primerApeliido=" + primerApeliido + "\n"
                + " segundoApellido=" + segundoApellido + "\n"
                + " nombres=" + nombres + "\n"
                + " nacionalidad=" + nacionalidad + "\n"
                + " paisProce=" + paisProce + "\n"
                + " deparProce=" + deparProce + "\n"
                + " ciudadProce=" + ciudadProce + "\n"
                + " paisDest=" + paisDest + "\n"
                + " deparDet=" + deparDet + "\n"
                + " ciudadDest=" + ciudadDest ;
    }

}
