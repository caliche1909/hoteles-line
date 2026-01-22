
package proyectodoral02;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import vistas.Iniciar;


public class ProyectoDoral02 {

    
    public static void main(String[] args) {
        try {
            
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            JOptionPane.showMessageDialog(null, "Ocurrió un error al inicicar el sistema: " + e.getMessage());
        }
        
        Iniciar iniciar = new Iniciar();
        iniciar.setVisible(true);
    }
    
}
