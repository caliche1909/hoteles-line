/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conectar;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JPanel;
import modelo.UsuarioOperando;



public class EnsayarMetodos {
    

    public EnsayarMetodos() {
    }
    
   
}    
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////    
     /* public JButton pintarHabs(JPanel respanseg){
        JButton[] componentes =  (JButton[]) respanseg.getComponents();
        for (int i = 0; i <listaHabs.length; i++) {
            res=activarHab(listaHabs[i]);
            if (res.equals("No")) {
                for (int j = 0; j <componentes.length; j++) {
                    componentes[j].setBackground(Color.red);
                }
                
            } else {
            }
            
            
        }
    }*/
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////     
    /*  public void activarBtn(JPanel respanseg){
        JButton[] componentes;
        componentes = (JButton[]) respanseg.getComponents();
        for(int i=0; i<listaHabs.length;i++){             
            res = activarHab(listaHabs.toString());
        }    for (int j = 0; j < componentes.length; j++) {
             if (res.equals("Libre")){
            hab407.setBackground(new Color (0,153,0));
        } else if(res.equals("Ocupado")) {
            hab407.setBackground(new Color (255,53,32));
        }
        }
    }*/
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
    /*public void pint(){
        for(int i=0; i<listaHabs.length; i++){
            res = activarHab(listaHabs[i]);
            if (res=="Libre"){
                if (listaHabs[i].equals("201")){
                    hab201.setBackground(new Color (0,153,0));
                }else if (listaHabs[i].equals("202")) {
                    hab202.setBackground(new Color (0,153,0));
                }else if (listaHabs[i].equals("203")) {
                    hab203.setBackground(new Color (0,153,0));
                }else if (listaHabs[i].equals("204")) {
                    hab204.setBackground(new Color (0,153,0));
                }else if (listaHabs[i].equals("205")) {
                    hab205.setBackground(new Color (0,153,0));
                }
            } else {
                if (listaHabs[i].equals("201")){
                    hab201.setBackground(new Color (255,53,32));
                }else if (listaHabs[i].equals("202")) {
                    hab202.setBackground(new Color (255,53,32));
                }else if (listaHabs[i].equals("203")) {
                    hab203.setBackground(new Color (255,53,32));
                }else if (listaHabs[i].equals("204")) {
                    hab204.setBackground(new Color (255,53,32));
                }else if (listaHabs[i].equals("205")) {
                    hab205.setBackground(new Color (255,53,32));
                }
            }
        }
    } */       
    
        

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /*public String activarHab(String numeroHab){
        String data=null;
        Connection conex = con.conexion();
        try {
            String consulta = "SELECT Estado_Habitacion FROM doralplaza.habitaciones where Num_Habitacion = '" + numeroHab + "' ";
            ps = conex.prepareStatement(consulta);
            rs=ps.executeQuery();
           
            while(rs.next()){
                data = rs.getString("Estado_Habitacion");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }finally{
            try {
                conex.close();
            } catch (Exception e) {
                
                System.out.println("Error: " + e);
            }
        }
        
        return data;
    }*/

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*if (hab301.getBackground().equals(new Color(0, 153, 0))) {
            String[] arreglo = {"Si", "No"};
            int opcion = JOptionPane.showOptionDialog(null, "seguro desea ocupar esta habitacion? ", "Confirmar accion...", 0, JOptionPane.QUESTION_MESSAGE, null, arreglo, "Si");
            if (opcion == 0) {
                bd.ocuparHabitacion(301);
                hab301.setBackground(new Color(255, 53, 32));
                registro abrir = new registro();
                abrir.regisnom1.setText(regisnom.getText());
                abrir.lblRollregistro.setText(lblRoll.getText());
                abrir.setVisible(true);
                abrir.regishabi.setText("301");
                this.dispose();
            } else {
            }
        } else if (hab301.getBackground().equals(new Color(255, 53, 32))) {
            String[] arreglo = {"Si", "No"};
            int opcion = JOptionPane.showOptionDialog(null, "Seguro desea habilitar esta habitacion? ", "Confirmar accion...", 0, JOptionPane.QUESTION_MESSAGE, null, arreglo, "Si");
            if (opcion == 0) {
                hab301.setBackground(new Color(0, 153, 0));
                bd.liberarHabitacion(301);
            } else {
            }
        }*/
/*private void regisnumdocFocusLost(java.awt.event.FocusEvent evt) {                                      
        java.sql.Connection dom = con.conexion();
        try {
            dom = con.conexion();
            ps = dom.prepareStatement("SELECT Nombres, Apellidos, Nacionalidad, Telefono, Profesion FROM clientes WHERE Num_Documento = ?");
            ps.setString(1, regisnumdoc.getText());

            rs = ps.executeQuery();
            if (rs.next()) {
                regisnom.setText(rs.getString("Nombres"));
                regisape.setText(rs.getString("Apellidos"));
                regisnacion.setSelectedItem(rs.getString("Nacionalidad"));
                registel.setText(rs.getString("Telefono"));
                regisprofe.setText(rs.getString("Profesion"));
                con.conexion().close();
                regisproce.requestFocus();
            } else {
                registipdoc.requestFocus();
            }
        } catch (Exception e) {
        }
    }*/

/*private void printFactura() {
    // Crear el contenido de la factura como un String
    StringBuilder facturaContent = new StringBuilder();
    
    facturaContent.append("Factura\n");
    facturaContent.append("Nombre: ").append(regisnom.getText()).append("\n");
    facturaContent.append("Apellido: ").append(regisape.getText()).append("\n");
    facturaContent.append("Documento: ").append(regisnumdoc.getText()).append("\n");
    facturaContent.append("Tipo de documento: ").append(registipdoc.getSelectedItem().toString()).append("\n");
    facturaContent.append("Nacionalidad: ").append(regisnacion.getSelectedItem().toString()).append("\n");
    facturaContent.append("Telefono: ").append(registel.getText()).append("\n");
    facturaContent.append("Profesion: ").append(regisprofe.getText()).append("\n");
    facturaContent.append("Procedencia: ").append(regisproce.getText()).append("\n");
    facturaContent.append("Destino: ").append(regisdest.getText()).append("\n");
    
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    facturaContent.append("Fecha de entrada: ").append(dateFormat.format(new Date())).append("\n");
    facturaContent.append("Fecha de salida: ").append(dateFormat.format(jdchFeSa.getDate())).append("\n");
    
    facturaContent.append("Noches: ").append(regiscantno.getText()).append("\n");
    facturaContent.append("Valor habitacion: ").append(regisvalorhabi.getText().replace(".", "")).append("\n");
    facturaContent.append("Comision: ").append(regiscomi.getText().replace(".", "")).append("\n");
    facturaContent.append("Valor neto: ").append(regisvalorneto.getText().replace(".", "")).append("\n");
    
    // Imprimir la factura
    InputStream inputStream = new ByteArrayInputStream(facturaContent.toString().getBytes());
    DocFlavor docFlavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
    Doc document = new SimpleDoc(inputStream, docFlavor, null);
    PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
    
    if (printService != null) {
        DocPrintJob printJob = printService.createPrintJob();
        try {
            printJob.print(document, null);
            System.out.println("Factura impresa exitosamente!");
        } catch (PrintException e) {
            System.err.println("Error al imprimir factura: " + e.getMessage());
        }
    } else {
        System.err.println("No se encontró el servicio de impresión!");
    }
}
*/

/*package vistas;

import conectar.Canectar;
import conectar.Consultasbd;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import modelo.UsuarioOperando;

public final class Recepcion extends javax.swing.JFrame {

    Canectar con = new Canectar();
    PreparedStatement ps;
    ResultSet rs;
    Consultasbd bd = new Consultasbd();
    String res = null;
    ArrayList<JButton> habitaiones;
    Object resnombreRes;
    UsuarioOperando usus = new UsuarioOperando();

    public Recepcion(UsuarioOperando usus) {
        initComponents();
        this.usus = usus;
        switch (this.usus.getRoll_usuarios()) {
            case "ADMINISTRADOR":
                regisnom.setText(this.usus.getNombre().split(" ")[0] + " " + this.usus.getApellidos().split(" ")[0]);
                lblRoll.setText(this.usus.getRoll_usuarios() + " :");
                break;
            case "RECEPCIONISTA":
                regisnom.setText(this.usus.getNombre().split(" ")[0] + " " + this.usus.getApellidos().split(" ")[0]);
                lblRoll.setText(this.usus.getRoll_usuarios() + " :");

                break;
            case "VENDEDOR":
                regisnom.setText(this.usus.getNombre().split(" ")[0] + " " + this.usus.getApellidos().split(" ")[0]);
                lblRoll.setText(this.usus.getRoll_usuarios() + " :");

                break;
        }
        jpnVerificar.setVisible(false);
        habitaiones = new ArrayList<>();
        this.setLocationRelativeTo(null);
        jdchFechaHabs.setDate(new Date());

        habitaiones.add(hab201);
        habitaiones.add(hab202);
        habitaiones.add(hab203);
        habitaiones.add(hab204);
        habitaiones.add(hab205);
        habitaiones.add(hab301);
        habitaiones.add(hab302);
        habitaiones.add(hab303);
        habitaiones.add(hab304);
        habitaiones.add(hab305);
        habitaiones.add(hab306);
        habitaiones.add(hab307);
        habitaiones.add(hab401);
        habitaiones.add(hab402);
        habitaiones.add(hab403);
        habitaiones.add(hab404);
        habitaiones.add(hab405);
        habitaiones.add(hab406);
        habitaiones.add(hab407);
        activarHabitaciones();
    }

    public void activarHabitaciones() {
        Connection conex = con.conexion();
        try {
            String consulta = "SELECT Estado_Habitacion FROM doralplaza.habitaciones";
            ps = conex.prepareStatement(consulta);
            rs = ps.executeQuery();
            int i = 0;
            while (rs.next()) {
                String res = rs.getString("Estado_Habitacion");
                JButton hab = habitaiones.get(i);
                if (res.equals("Libre")) {
                    hab.setBackground(new Color(0, 153, 0));
                } else if (res.equals("Ocupado")) {
                    hab.setBackground(new Color(255, 53, 32));
                } else if (res.equals("Reservado")) {
                    hab.setBackground(new Color(255, 179, 39));
                }
                i++;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        } finally {
            try {
                conex.close();
            } catch (Exception e) {

                System.out.println("Error: " + e);
            }
        }
        for (JButton habitacionBtn : habitaiones) {
            habitacionBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int numeroHabitacion = Integer.parseInt(habitacionBtn.getText());
                    toggleHabitacion(numeroHabitacion, habitacionBtn);
                }
            });
            habitacionBtn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (habitacionBtn.getBackground().equals(new Color(255, 53, 32))) {
                        int numeroHabitacion = Integer.parseInt(habitacionBtn.getText());
                        String[] datosReserva = con.obtenerDatosRegistro(numeroHabitacion);
                        jblMosNom.setText(datosReserva[0]);
                        jblMosApe.setText(datosReserva[1]);
                        jblMosTel.setText(datosReserva[2]);
                        jblMosFeLle.setText(datosReserva[3]);
                        jblMosHoLle.setText(datosReserva[4]);
                        jblMosFeSa.setText(datosReserva[5]);
                        jblValNo.setText(datosReserva[6]);
                        jblValTo.setText(datosReserva[7]);
                        jpnVerificar.setVisible(true);
                    } else if (habitacionBtn.getBackground().equals(new Color(255, 179, 39))) {
                        int numeroHabitacion = Integer.parseInt(habitacionBtn.getText());
                        String[] datosReserva = con.obtenerDatosReserva(numeroHabitacion);
                        jblMosNom.setText(datosReserva[0]);
                        jblMosApe.setText(datosReserva[1]);
                        jblMosTel.setText(datosReserva[2]);
                        jblMosFeLle.setText(datosReserva[3]);
                        jblMosHoLle.setText(datosReserva[4]);
                        jblMosFeSa.setText(datosReserva[5]);
                        jblValNo.setText(datosReserva[6]);
                        jblValTo.setText(datosReserva[7]);
                        jpnVerificar.setVisible(true);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    jpnVerificar.setVisible(false);
                }
            });
        }
    }
    private void toggleHabitacion(int numeroHabitacion, JButton botonHabitacion) {
        String[] opciones = {"REGISTRAR", "RESERVAR", "CANCELAR"};
        String[] concluirReserva = {"REGISTRAR  RESERVA", "ANULAR  RESERVAR", "CANCELAR"};
        if (botonHabitacion.getBackground().equals(new Color(0, 153, 0))) {
            int seleccion = JOptionPane.showOptionDialog(
                    null,
                    "Que quieres hacer ? ",
                    "Seleccione una opción",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );
            switch (seleccion) {
                case 0: // Registrar
                    bd.ocuparHabitacion(numeroHabitacion);
                    botonHabitacion.setBackground(new Color(255, 53, 32));
                    registro abrir = new registro(usus);
                    abrir.setVisible(true);
                    abrir.regisnom1.setText(regisnom.getText());
                    abrir.lblRollregistro.setText(lblRoll.getText());
                    abrir.regishabi.setText(Integer.toString(numeroHabitacion));
                    this.dispose();
                    break;
                case 1: // Reservar
                    bd.reservarHabitacion(numeroHabitacion);
                    botonHabitacion.setBackground(new Color(255, 179, 39));
                    Reservar ab = new Reservar(usus);
                    ab.setVisible(true);
                    ab.regisom1.setText(regisnom.getText());
                    ab.lblRollRegistro.setText(lblRoll.getText());
                    ab.txtNumHab.setText(Integer.toString(numeroHabitacion));
                    this.dispose();
                    break;
                case 2: // Cancelar
                    // No se realiza ninguna acción
                    break;
                default:
                    // El usuario cerró el JOptionPane sin seleccionar ninguna opción
                    break;
            }

        } else if (botonHabitacion.getBackground().equals(new Color(255, 53, 32))) {
            String[] arreglo = {"Si", "No"};
            int opcion = JOptionPane.showOptionDialog(null, "¿Seguro desea habilitar esta habitación?", "Confirmar acción...", 0, JOptionPane.QUESTION_MESSAGE, null, arreglo, "Si");
            if (opcion == 0) {
                bd.liberarHabitacion(numeroHabitacion);
                botonHabitacion.setBackground(new Color(0, 153, 0));
            } else {
            }
        } else if (botonHabitacion.getBackground().equals(new Color(255, 179, 39))) {
            int decision = JOptionPane.showOptionDialog(
                    null,
                    "Que quiere hacer ? ",
                    "Seleccione una opción",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    concluirReserva,
                    concluirReserva[0]
            );
            switch (decision) {
                case 0: // Registrar reserva
                    bd.ocuparHabitacion(numeroHabitacion);
                    botonHabitacion.setBackground(new Color(255, 53, 32));
                    registro abrir = new registro(usus);
                    abrir.setVisible(true);
                    abrir.regisnom1.setText(regisnom.getText());
                    abrir.lblRollregistro.setText(lblRoll.getText());
                    abrir.regishabi.setText(Integer.toString(numeroHabitacion));
                    this.dispose();
                    break;
                case 1: // Anular reserva
                    String[] arreglo = {"Anular", "Cancelar"};
                    int opcion = JOptionPane.showOptionDialog(null, "¿Seguro desea ANULAR esta reseva?", "Confirmar acción...", 0, JOptionPane.QUESTION_MESSAGE, null, arreglo, "Anular");
                    if (opcion == 0) {
                        bd.liberarHabitacion(numeroHabitacion);
                        botonHabitacion.setBackground(new Color(0, 153, 0));
                    } else {
                    }
                    break;
                case 2: // Cancelar
                    // No se realiza ninguna acción
                    break;
                default:
                    // El usuario cerró el JOptionPane sin seleccionar ninguna opción
                    break;
            }
        }
    }

    private Date agregarDiasAFecha(Date fecha, int dias) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.add(Calendar.DATE, dias);
        return calendar.getTime();
    }*/


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////





/*package vistas;

import conectar.Canectar;
import conectar.Consultasbd;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import modelo.UsuarioOperando;

public final class Recepcion extends javax.swing.JFrame {

    Canectar con = new Canectar();
    PreparedStatement ps;
    ResultSet rs;
    Consultasbd bd = new Consultasbd();
    String res = null;    
    HashMap<Integer, JButton> habitaiones;
    //Object resnombreRes;
    UsuarioOperando usus = new UsuarioOperando();

    String consulta = "SELECT Num_Habitacion,Estado_Habitacion FROM doralplaza.habitaciones;";
    boolean is_queryChange = false;

    public Recepcion(UsuarioOperando usus) {
        initComponents();
        this.usus = usus;
        switch (this.usus.getRoll_usuarios()) {
            case "ADMINISTRADOR":
                regisnom.setText(this.usus.getNombre().split(" ")[0] + " " + this.usus.getApellidos().split(" ")[0]);
                lblRoll.setText(this.usus.getRoll_usuarios() + " :");
                break;
            case "RECEPCIONISTA":
                regisnom.setText(this.usus.getNombre().split(" ")[0] + " " + this.usus.getApellidos().split(" ")[0]);
                lblRoll.setText(this.usus.getRoll_usuarios() + " :");

                break;
            case "VENDEDOR":
                regisnom.setText(this.usus.getNombre().split(" ")[0] + " " + this.usus.getApellidos().split(" ")[0]);
                lblRoll.setText(this.usus.getRoll_usuarios() + " :");

                break;
        }
        jpnVerificar.setVisible(false);
        habitaiones = new HashMap<>();
        this.setLocationRelativeTo(null);
        jdchFechaHabs.setDate(new Date());
        
        System.out.println("hab201: " + hab201);
        System.out.println("hab202: " + hab202);
        System.out.println("hab203: " + hab203);
        System.out.println("hab204: " + hab204);
        System.out.println("hab205: " + hab205);
        System.out.println("hab301: " + hab301);
        System.out.println("hab302: " + hab302);
        System.out.println("hab303: " + hab303);
        System.out.println("hab304: " + hab304);
        System.out.println("hab305: " + hab305);
        System.out.println("hab306: " + hab306);
        System.out.println("hab307: " + hab307);
        System.out.println("hab401: " + hab401);
        System.out.println("hab402: " + hab402);
        System.out.println("hab403: " + hab403);
        System.out.println("hab404: " + hab404);
        System.out.println("hab405: " + hab405);
        System.out.println("hab406: " + hab406);
        System.out.println("hab407: " + hab407);
        
        habitaiones.put(201, hab201);
        habitaiones.put(202, hab202);
        habitaiones.put(203, hab203);
        habitaiones.put(204, hab204);
        habitaiones.put(205, hab205);
        habitaiones.put(301, hab301);
        habitaiones.put(302, hab302);
        habitaiones.put(303, hab303);
        habitaiones.put(304, hab304);
        habitaiones.put(305, hab305);
        habitaiones.put(306, hab306);
        habitaiones.put(307, hab307);
        habitaiones.put(401, hab401);
        habitaiones.put(402, hab402);
        habitaiones.put(403, hab403);
        habitaiones.put(404, hab404);
        habitaiones.put(405, hab405);
        habitaiones.put(406, hab406);
        habitaiones.put(407, hab407);
        activarHabitaciones(jdchFechaHabs.getDate());

    }

    public void activarHabitaciones(Date fechaSeleccionada) {
        Connection conex = con.conexion();
        try {
            System.out.println(consulta);
            ps = conex.prepareStatement(consulta);
            java.sql.Date fecha = new java.sql.Date(fechaSeleccionada.getTime());

            if (is_queryChange) {
                System.out.println("Cambió");
                ps.setDate(1, fecha);
                ps.setDate(2, fecha);
                ps.setDate(3, fecha);
                ps.setDate(4, fecha);
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                
                String res = rs.getString("Estado_Habitacion");
                int numeroHabitacion = rs.getInt("Num_Habitacion");
                JButton hab = habitaiones.get(numeroHabitacion);
                if (hab != null) {
                    if (res.equals("Libre")) {
                        hab.setBackground(new Color(0, 153, 0));
                    } else if (res.equals("Ocupado")) {
                        hab.setBackground(new Color(255, 53, 32));
                    } else if (res.equals("Reservado")) {
                        hab.setBackground(new Color(255, 179, 39));
                    }
                } else {
                    System.out.println("El botón de la habitación " + numeroHabitacion + " no está inicializado.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        } finally {
            try {
                conex.close();
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
        for (JButton habitacionBtn : habitaiones.values()) {
            habitacionBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int numeroHabitacion = Integer.parseInt(habitacionBtn.getText());
                    toggleHabitacion(numeroHabitacion, habitacionBtn);
                }
            });
            habitacionBtn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (habitacionBtn.getBackground().equals(new Color(255, 53, 32))) {
                        int numeroHabitacion = Integer.parseInt(habitacionBtn.getText());
                        String[] datosReserva = con.obtenerDatosRegistro(numeroHabitacion);
                        jblMosNom.setText(datosReserva[0]);
                        jblMosApe.setText(datosReserva[1]);
                        jblMosTel.setText(datosReserva[2]);
                        jblMosFeLle.setText(datosReserva[3]);
                        jblMosHoLle.setText(datosReserva[4]);
                        jblMosFeSa.setText(datosReserva[5]);
                        jblValNo.setText(datosReserva[6]);
                        jblValTo.setText(datosReserva[7]);
                        jpnVerificar.setVisible(true);
                    } else if (habitacionBtn.getBackground().equals(new Color(255, 179, 39))) {
                        int numeroHabitacion = Integer.parseInt(habitacionBtn.getText());
                        String[] datosReserva = con.obtenerDatosReserva(numeroHabitacion);
                        jblMosNom.setText(datosReserva[0]);
                        jblMosApe.setText(datosReserva[1]);
                        jblMosTel.setText(datosReserva[2]);
                        jblMosFeLle.setText(datosReserva[3]);
                        jblMosHoLle.setText(datosReserva[4]);
                        jblMosFeSa.setText(datosReserva[5]);
                        jblValNo.setText(datosReserva[6]);
                        jblValTo.setText(datosReserva[7]);
                        jpnVerificar.setVisible(true);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    jpnVerificar.setVisible(false);
                }
            });
        }
    }

    private void toggleHabitacion(int numeroHabitacion, JButton botonHabitacion) {
        String[] opciones = {"REGISTRAR", "RESERVAR", "CANCELAR"};
        String[] concluirReserva = {"REGISTRAR  RESERVA", "ANULAR  RESERVAR", "CANCELAR"};
        if (botonHabitacion.getBackground().equals(new Color(0, 153, 0))) {
            int seleccion = JOptionPane.showOptionDialog(
                    null,
                    "Que quieres hacer ? ",
                    "Seleccione una opción",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );
            switch (seleccion) {
                case 0: // Registrar                   
                    registro abrir = new registro(usus);                    
                    abrir.regisnom1.setText(regisnom.getText());
                    abrir.lblRollregistro.setText(lblRoll.getText());
                    abrir.regishabi.setText(Integer.toString(numeroHabitacion));
                    abrir.setVisible(true);
                    if(abrir.RegistroExitoso){
                        botonHabitacion.setBackground(new Color(255, 53, 32));
                        bd.ocuparHabitacion(numeroHabitacion);
                    }
                    this.dispose();                    
                    break;
                case 1: // Reservar
                    bd.reservarHabitacion(numeroHabitacion);
                    botonHabitacion.setBackground(new Color(255, 179, 39));
                    Reservar ab = new Reservar(usus);
                    ab.setVisible(true);
                    ab.regisom1.setText(regisnom.getText());
                    ab.lblRollRegistro.setText(lblRoll.getText());
                    ab.txtNumHab.setText(Integer.toString(numeroHabitacion));
                    this.dispose();
                    break;
                case 2: // Cancelar
                    // No se realiza ninguna acción
                    break;
                default:
                    // El usuario cerró el JOptionPane sin seleccionar ninguna opción
                    break;
            }
            

        } else if (botonHabitacion.getBackground().equals(new Color(255, 53, 32))) {
            String[] arreglo = {"Si", "No"};
            int opcion = JOptionPane.showOptionDialog(null, "¿Seguro desea habilitar esta habitación?", "Confirmar acción...", 0, JOptionPane.QUESTION_MESSAGE, null, arreglo, "Si");
            if (opcion == 0) {
                bd.liberarHabitacion(numeroHabitacion);
                botonHabitacion.setBackground(new Color(0, 153, 0));
            } else {
            }
        } else if (botonHabitacion.getBackground().equals(new Color(255, 179, 39))) {
            int decision = JOptionPane.showOptionDialog(
                    null,
                    "Que quiere hacer ? ",
                    "Seleccione una opción",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    concluirReserva,
                    concluirReserva[0]
            );
            switch (decision) {
                case 0: // Registrar reserva
                    bd.ocuparHabitacion(numeroHabitacion);
                    botonHabitacion.setBackground(new Color(255, 53, 32));
                    registro abrir = new registro(usus);
                    abrir.setVisible(true);
                    abrir.regisnom1.setText(regisnom.getText());
                    abrir.lblRollregistro.setText(lblRoll.getText());
                    abrir.regishabi.setText(Integer.toString(numeroHabitacion));
                    this.dispose();
                    break;
                case 1: // Anular reserva
                    String[] arreglo = {"Anular", "Cancelar"};
                    int opcion = JOptionPane.showOptionDialog(null, "¿Seguro desea ANULAR esta reseva?", "Confirmar acción...", 0, JOptionPane.QUESTION_MESSAGE, null, arreglo, "Anular");
                    if (opcion == 0) {
                        bd.liberarHabitacion(numeroHabitacion);
                        botonHabitacion.setBackground(new Color(0, 153, 0));
                    } else {
                    }
                    break;
                case 2: // Cancelar
                    // No se realiza ninguna acción
                    break;
                default:
                    // El usuario cerró el JOptionPane sin seleccionar ninguna opción
                    break;
            }
        }
    }

    private Date agregarDiasAFecha(Date fecha, int dias) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.add(Calendar.DATE, dias);
        return calendar.getTime();
    }*/

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/*package vistas;

import conectar.Canectar;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import vistas.Recepcion;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import modelo.UsuarioOperando;

public class Registro extends javax.swing.JDialog implements Runnable {

    Canectar con = new Canectar();
    String hora, minutos, seg;
    Thread hilo;
    PreparedStatement ps;
    ResultSet rs;
    public boolean RegistroExitoso = false;
    private UsuarioOperando usus;

    public registro(UsuarioOperando usus) {
        this.hilo = new Thread();
        initComponents();

        this.setModal(true);
        this.usus = usus;
        regisnumdoc.requestFocus();
        this.setLocationRelativeTo(null);
        regisfe.setText(Fechaparamos());
        try {
            // Parse the date from regisfe
            SimpleDateFormat formatoFechaActual = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaActual = formatoFechaActual.parse(regisfe.getText());

            // Add one day to fechaActual to get fechaSalida
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fechaActual);
            calendar.add(Calendar.DATE, 1);
            Date fechaSalida = calendar.getTime();

            // Set fechaSalida in jdchFeSa
            jdchFeSa.setDate(fechaSalida);

            // Call calcularNoches with fechaActual and fechaSalida
            calcularNoches(fechaActual, fechaSalida);

        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Error al procesar las fechas: " + e.getMessage(), "Error en la fecha", JOptionPane.ERROR_MESSAGE);
        }
        jdchFeSa.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                try {
                    SimpleDateFormat formatoFechaActual = new SimpleDateFormat("yyyy-MM-dd");
                    Date fechaActual = formatoFechaActual.parse(regisfe.getText());
                    Date fechaSalida = jdchFeSa.getDate();
                    calcularNoches(fechaActual, fechaSalida);
                } catch (ParseException e) {
                    JOptionPane.showMessageDialog(null, "Error al procesar las fechas: " + e.getMessage(), "Error en la fecha", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        hilo = new Thread(this);
        hilo.start();
        setVisible(true);
        aplicarFormatoPuntosMil(regisvalorhabi);
        aplicarFormatoPuntosMil(registotalpa);
        aplicarFormatoPuntosMil(regiscomi);
        aplicarFormatoPuntosMil(regisvalorneto);
    }

    public String Fechaparamos() {
        Date fecha = new Date();
        SimpleDateFormat FechaAct = new SimpleDateFormat("yyyy-MM-dd");
        return FechaAct.format(fecha);
    }

    public void hora() {
        Calendar calendario = new GregorianCalendar();
        Date horaactual = new Date();
        calendario.setTime(horaactual);
        hora = calendario.get(Calendar.HOUR_OF_DAY) > 9 ? "" + calendario.get(Calendar.HOUR_OF_DAY) : "" + calendario.get(Calendar.HOUR_OF_DAY);
        minutos = calendario.get(Calendar.MINUTE) > 9 ? "" + calendario.get(Calendar.MINUTE) : "0" + calendario.get(Calendar.MINUTE);
        seg = calendario.get(Calendar.SECOND) > 9 ? "" + calendario.get(Calendar.SECOND) : "0" + calendario.get(Calendar.SECOND);
    }

    @Override
    public void run() {
        Thread current = Thread.currentThread();
        while (current == hilo) {
            hora();
            regisllega.setText(hora + ":" + minutos + ":" + seg);
        }
    }

    private String formatearConPuntosDeMil(double numero) {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setGroupingSeparator('.');
        DecimalFormat formatoDelNumero = new DecimalFormat("#,##0", simbolos);
        formatoDelNumero.setGroupingSize(3);
        formatoDelNumero.setGroupingUsed(true);
        return formatoDelNumero.format(numero);
    }

    private void calcularNoches(Date fechaActual, Date fechaSalida) {
        try {
            if (!fechaSalida.before(fechaActual)) {
                // Formatea las fechas usando el formato "año mes día"
                SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy M dd");
                String fechaSalidaFormateada = formatoFecha.format(fechaSalida);
                String fechaActualFormateada = formatoFecha.format(fechaActual);
                // Convierte las fechas formateadas de nuevo a objetos Date
                Date fechaSalidaFormatoNuevo = formatoFecha.parse(fechaSalidaFormateada);
                Date fechaActualFormatoNuevo = formatoFecha.parse(fechaActualFormateada);
                // Calcula la cantidad de noches
                long diferenciaMillis = fechaSalidaFormatoNuevo.getTime() - fechaActualFormatoNuevo.getTime();
                long diferenciaDias = TimeUnit.DAYS.convert(diferenciaMillis, TimeUnit.MILLISECONDS);
                int cantidadNoches = (int) diferenciaDias;
                // Asegura que la cantidad mínima de noches sea 1
                if (cantidadNoches == 0) {
                    cantidadNoches = 1;
                }
                // Muestra la cantidad de noches en el JTextField regiscantno
                regiscantno.setText(Integer.toString(cantidadNoches));
            } else {
                JOptionPane.showMessageDialog(this, "Solo se reciben fechas futuras!", "Error en la fecha", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Error al procesar las fechas: " + e.getMessage(), "Error en la fecha", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void aplicarFormatoPuntosMil(JTextField textField) {
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent evt) {
                char c = evt.getKeyChar();
                // Solo permite números
                if (c < '0' || c > '9') {
                    evt.consume();
                    return;
                }
            }

            @Override
            public void keyReleased(KeyEvent evt) {
                formatoPuntosMil(textField);
            }
        });
    }

    private void formatoPuntosMil(JTextField textField) {
        String text = textField.getText();
        int len = text.length();
        StringBuilder newText = new StringBuilder();

        int count = 0;
        for (int i = len - 1; i >= 0; i--) {
            if (count == 3) {
                newText.insert(0, '.');
                count = 0;
            }

            char ch = text.charAt(i);
            if (ch != '.') {
                newText.insert(0, ch);
                count++;
            }
        }

        // Si el formato es diferente, actualiza el texto en textField
        if (!newText.toString().equals(text)) {
            textField.setText(newText.toString());
            textField.setCaretPosition(newText.length());
        }
    }*/