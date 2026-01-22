package modelo;

import java.util.Date;

public class Cliente {

    private int Id_Cliente;
    private String Num_Documento;
    private String Tipo_Documento;
    private Date Fecha_Nacimiento;
    private String Nombres;
    private String Apellidos;
    private String Nacionalidad;
    private String Telefono;
    private String Profesion;
    private String Estado_Verificacion;
    private int Edad;
    private boolean Reporte_Sire = false;

    public Cliente() {
    }

    public Cliente(int Id_Cliente, String Num_Documento, String Tipo_Documento, Date Fecha_Nacimiento,
            String Nombres, String Apellidos, String Nacionalidad, String Telefono,
            String Profesion, String Estado_Verificacion, int Edad, boolean ReporteSire) {
        this.Id_Cliente = Id_Cliente;
        this.Num_Documento = Num_Documento;
        this.Tipo_Documento = Tipo_Documento;
        this.Fecha_Nacimiento = Fecha_Nacimiento;
        this.Nombres = Nombres;
        this.Apellidos = Apellidos;
        this.Nacionalidad = Nacionalidad;
        this.Telefono = Telefono;
        this.Profesion = Profesion;
        this.Estado_Verificacion = Estado_Verificacion;
        this.Edad = Edad;
        this.Reporte_Sire = ReporteSire;
    }

    public boolean isReporte_Sire() {
        return Reporte_Sire;
    }

    public void setReporte_Sire(boolean Reporte_Sire) {
        this.Reporte_Sire = Reporte_Sire;
    }       

    public int getId_Cliente() {
        return Id_Cliente;
    }

    public String getNum_Documento() {
        return Num_Documento;
    }

    public String getTipo_Documento() {
        return Tipo_Documento;
    }

    public Date getFecha_Nacimiento() {
        return Fecha_Nacimiento;
    }

    public String getNombres() {
        return Nombres;
    }

    public String getApellidos() {
        return Apellidos;
    }

    public String getNacionalidad() {
        return Nacionalidad;
    }

    public String getTelefono() {
        return Telefono;
    }

    public String getProfesion() {
        return Profesion;
    }

    public String getEstado_Verificacion() {
        return Estado_Verificacion;
    }

    public int getEdad() {
        return Edad;
    }

    public void setId_Cliente(int Id_Cliente) {
        this.Id_Cliente = Id_Cliente;
    }

    public void setNum_Documento(String Num_Documento) {
        this.Num_Documento = Num_Documento;
    }

    public void setTipo_Documento(String Tipo_Documento) {
        this.Tipo_Documento = Tipo_Documento;
    }

    public void setFecha_Nacimiento(Date Fecha_Nacimiento) {
        this.Fecha_Nacimiento = Fecha_Nacimiento;
    }

    public void setNombres(String Nombres) {
        this.Nombres = Nombres;
    }

    public void setApellidos(String Apellidos) {
        this.Apellidos = Apellidos;
    }

    public void setNacionalidad(String Nacionalidad) {
        this.Nacionalidad = Nacionalidad;
    }

    public void setTelefono(String Telefono) {
        this.Telefono = Telefono;
    }

    public void setProfesion(String Profesion) {
        this.Profesion = Profesion;
    }

    public void setEstado_Verificacion(String Estado_Verificacion) {
        this.Estado_Verificacion = Estado_Verificacion;
    }

    public void setEdad(int Edad) {
        this.Edad = Edad;
    }

    @Override
    public String toString() {
        return """
               Cliente{
                 Id_Cliente= """ + Id_Cliente + "\n"
                + ", Num_Documento=' " + Num_Documento + "\n"
                + ", Tipo_Documento=' " + Tipo_Documento + "\n"
                + ", Fecha_Nacimiento= " + Fecha_Nacimiento + "\n"
                + ", Nombres=' " + Nombres + "\n"
                + ", Apellidos=' " + Apellidos + "\n"
                + ", Nacionalidad=' " + Nacionalidad + "\n"
                + ", Telefono=' " + Telefono + "\n"
                + ", Profesion=' " + Profesion + "\n"
                + ", Estado_Verificacion=' " + Estado_Verificacion + "\n"
                + ", Edad= " + Edad+ "\n"
                + ", Reporte_Sire=' "+ Reporte_Sire+ "\n"
                + '}';
    }
}
