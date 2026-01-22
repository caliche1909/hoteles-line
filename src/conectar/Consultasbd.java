package conectar;

import java.sql.Connection;
import javax.swing.JOptionPane;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Consultasbd {

    Canectar con = new Canectar();
    Connection conexion;
    PreparedStatement sp;
    ResultSet resultado;

    public boolean ocuparHabitacion(int numeroHabitacion, int idHotel) {
        String consulta = "UPDATE habitaciones SET Estado_Habitacion = 'Ocupado' WHERE Num_Habitacion = ? AND Fk_Id_Hotel = ?";

        try (Connection conexion = con.conexion();
                PreparedStatement sp = conexion.prepareStatement(consulta)) {

            sp.setInt(1, numeroHabitacion);
            sp.setInt(2, idHotel);

            int mensaje = sp.executeUpdate();
            if (mensaje > 0) {
                JOptionPane.showMessageDialog(null, "La habitacion "+numeroHabitacion+" cambiara a modo ocupado... ", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            }
            return mensaje > 0;

        } catch (SQLException e) {
            System.out.println("Error: " + e);
            return false;
        }
    }

    public void reservarHabitacion(int numeroHabitacion, int idHotel) {
        String consulta = "UPDATE habitaciones SET Estado_Habitacion = 'Reservado' WHERE Num_Habitacion = ? AND Fk_Id_Hotel = ?";

        try (Connection conexion = con.conexion();
                PreparedStatement sp = conexion.prepareStatement(consulta)) {

            sp.setInt(1, numeroHabitacion);
            sp.setInt(2, idHotel);
            int mensaje = sp.executeUpdate();

            if (mensaje > 0) {
                JOptionPane.showMessageDialog(null, "Se ha reservado la habitacion " + numeroHabitacion, "Informacion", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo Reservar la habitación", "ERROR! ", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Falló la reserva\n " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
