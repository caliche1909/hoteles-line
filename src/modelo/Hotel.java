package modelo;


public class Hotel {
    int idHoteles;
    String nombreHotel;
    String rntHotel;
    String direccionHotel;
    String telefonoHotel;
    String ciudadHotel;
    String departamentoHotel;
    String paisHotel;
    String sloganHotel;
    String estadoHotel;

    public Hotel() {
    }

    public Hotel(int idHoteles, String nombreHotel, String rntHotel, String direccionHotel, String telefonoHotel, String ciudadHotel, 
            String departamentoHotel, String paisHotel, String sloganHotel, String estadoHotel) {
        this.idHoteles = idHoteles;
        this.nombreHotel = nombreHotel;
        this.rntHotel = rntHotel;
        this.direccionHotel = direccionHotel;
        this.telefonoHotel = telefonoHotel;
        this.ciudadHotel = ciudadHotel;
        this.departamentoHotel = departamentoHotel;
        this.paisHotel = paisHotel;
        this.sloganHotel = sloganHotel;
        this.estadoHotel = estadoHotel;
    }

    public String getEstadoHotel() {
        return estadoHotel;
    }

    public void setEstadoHotel(String estadoHotel) {
        this.estadoHotel = estadoHotel;
    }
    

    public int getIdHoteles() {
        return idHoteles;
    }

    public void setIdHoteles(int idHoteles) {
        this.idHoteles = idHoteles;
    }

    public String getNombreHotel() {
        return nombreHotel;
    }

    public void setNombreHotel(String nombreHotel) {
        this.nombreHotel = nombreHotel;
    }

    public String getRntHotel() {
        return rntHotel;
    }

    public void setRntHotel(String rntHotel) {
        this.rntHotel = rntHotel;
    }

    public String getDireccionHotel() {
        return direccionHotel;
    }

    public void setDireccionHotel(String direccionHotel) {
        this.direccionHotel = direccionHotel;
    }

    public String getTelefonoHotel() {
        return telefonoHotel;
    }

    public void setTelefonoHotel(String telefonoHotel) {
        this.telefonoHotel = telefonoHotel;
    }

    public String getCiudadHotel() {
        return ciudadHotel;
    }

    public void setCiudadHotel(String ciudadHotel) {
        this.ciudadHotel = ciudadHotel;
    }

    public String getDepartamentoHotel() {
        return departamentoHotel;
    }

    public void setDepartamentoHotel(String departamentoHotel) {
        this.departamentoHotel = departamentoHotel;
    }

    public String getPaisHotel() {
        return paisHotel;
    }

    public void setPaisHotel(String paisHotel) {
        this.paisHotel = paisHotel;
    }

    public String getSloganHotel() {
        return sloganHotel;
    }

    public void setSloganHotel(String sloganHotel) {
        this.sloganHotel = sloganHotel;
    }   

    @Override
    public String toString() {
        return "Hotel {\n"
                + "idHoteles=" + idHoteles + "\n"
                + "nombreHotel=" + nombreHotel + "\n"
                + "rntHotel=" + rntHotel + "\n"
                + "direccionHotel=" + direccionHotel + "\n"
                + "telefonoHotel=" + telefonoHotel + "\n"
                + "ciudadHotel=" + ciudadHotel + "\n"
                + "departamentoHotel=" + departamentoHotel + "\n"
                + "paisHotel=" + paisHotel + "\n"
                + "sloganHotel=" + sloganHotel + "\n"
                + "estadoHotel=" + estadoHotel + '}';
    }
    
    
}
