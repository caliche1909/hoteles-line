package vistas;

import conectar.Canectar;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import modelo.SqlUsuarios;
import modelo.UsuarioOperando;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import modelo.Hotel;
import org.mindrot.jbcrypt.BCrypt;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;


public class FrmRegistrarUsuarios extends javax.swing.JDialog {

    SqlUsuarios sqlusu = new SqlUsuarios();
    UsuarioOperando mod = new UsuarioOperando();
    boolean vengoDeRegistro = false;
    Hotel hotel;

    public FrmRegistrarUsuarios(java.awt.Frame parent, boolean modal, Hotel hotel, boolean vengoDeRegistro) {
        super(parent, modal);
        this.hotel = hotel;
        this.vengoDeRegistro = vengoDeRegistro;
        initComponents();

        jlbHotel.setText(hotel.getNombreHotel().toUpperCase());
        jlbSlogan.setText(hotel.getSloganHotel());
        System.out.println("vengo de registro esta en: " + vengoDeRegistro);

        if (vengoDeRegistro) {
            regisumod.setVisible(false);
            regisueli.setVisible(false);
            regisuregis.setBounds(210, 400, 150, 40);
        }

        btnModUsu.setVisible(false);
        txtIdUsu.setVisible(false);
        txtIdHotel.setText(hotel.getRntHotel());

        this.setLocationRelativeTo(null);
        try {
            // Obtiene el ClassLoader del contexto actual
            ClassLoader classLoader = getClass().getClassLoader();

            // Carga la imagen como un recurso del ClassLoader
            ImageIcon icon = new ImageIcon(classLoader.getResource("img/RecepcionDoral.png"));

            // Crea una imagen BufferedImage del ImageIcon
            BufferedImage originalImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = originalImage.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();

            // Aplicar la opacidad 
            BufferedImage transparentImage = setOpacity(originalImage, 0.5f);

            // Escala la imagen al tamaño del JLabel
            Image scaledImage = transparentImage.getScaledInstance(jlbImagen.getWidth(), jlbImagen.getHeight(), Image.SCALE_SMOOTH);

            // Crear un ImageIcon con la imagen transparente
            ImageIcon scaledIcon = new ImageIcon(scaledImage);

            // Establece este ImageIcon en el label
            jlbImagen.setIcon(scaledIcon);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int tipoUsuario() {
        int num = 0;
        if (cbxtipoUsuario.getSelectedItem().equals("Administrador")) {
            num = 1;
        } else if (cbxtipoUsuario.getSelectedItem().equals("Recepcionista")) {
            num = 2;
        } else if (cbxtipoUsuario.getSelectedItem().equals("Vendedor")) {
            num = 3;
        }
        return num;
    }

    public static BufferedImage setOpacity(BufferedImage image, float opacity) {
        BufferedImage transparentImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = transparentImage.createGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        return transparentImage;
    }

    public void registroUsuario() {
        Hotel hotel = sqlusu.existeHotel(txtIdHotel.getText());
        if (hotel != null) {
            int idHotel = hotel.getIdHoteles();
            String contrasenia = new String(regisucon.getPassword());
            String confirContrasenia = new String(regisuconfircon.getPassword());
            if (regisunom.getText().equals("") || regisuape.getText().equals("")
                    || regisudoc.getText().isEmpty() || regisuco.getText().equals("") || regisusu.getText().equals("")
                    || contrasenia.equals("") || confirContrasenia.equals("")) {
                JOptionPane.showMessageDialog(null, "Para registrar un nuevo usuario, es necesario "
                        + "llenar todos los campos");
                regisunom.requestFocus();
            } else {

                if (contrasenia.equals(confirContrasenia)) {
                    if (sqlusu.existeUsuario(regisusu.getText()) == null) {
                        if (sqlusu.esEmail(regisuco.getText())) {

                            String nuevopass = BCrypt.hashpw(contrasenia, BCrypt.gensalt());
                            mod.setNombres(regisunom.getText());
                            mod.setApellidos(regisuape.getText());
                            mod.setCedula(regisudoc.getText());
                            mod.setCorreo(regisuco.getText());
                            mod.setUsuario(regisusu.getText());
                            mod.setContrasenia(nuevopass);
                            mod.setId_tipo(tipoUsuario());
                            mod.setTelefono(regisutel.getText());
                            mod.setIdHotel(idHotel);
                            mod.setUltima_secion(LocalDateTime.now());
                            
                            if (sqlusu.registrar(mod)) {
                                JOptionPane.showMessageDialog(null, "Usuario registrado exitosamente!");
                                if (vengoDeRegistro) {
                                    inicio ir = new inicio();
                                    ir.setVisible(true);
                                    this.dispose();
                                }
                                this.dispose();
                            } else {
                                JOptionPane.showMessageDialog(null, "ERROR! No se pudo registrar el nuevo usuario");
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "El correo electronico no es valido");
                            regisuco.requestFocus();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "El usuario ya existe, por favor elija otro nombre de usuario");
                        regisusu.requestFocus();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Las contraseñas no coinciden...");
                    regisucon.requestFocus();
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "El codigo del hotel no existe!");
            txtIdHotel.setText("");
            txtIdHotel.requestFocus();
        }

    }

    //@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new JPanel();
        jLabel1 = new JLabel();
        regisunom = new JTextField();
        regisuape = new JTextField();
        regisuco = new JTextField();
        regisusu = new JTextField();
        regisucon = new JPasswordField();
        regisuconfircon = new JPasswordField();
        cbxtipoUsuario = new JComboBox<>();
        regisudoc = new JTextField();
        regisutel = new JTextField();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        jLabel4 = new JLabel();
        jLabel5 = new JLabel();
        jLabel6 = new JLabel();
        jLabel7 = new JLabel();
        regisuregis = new JButton();
        txtIdHotel = new JTextField();
        regisumod = new JButton();
        regisueli = new JButton();
        btnVolverInicio = new JButton();
        jLabel8 = new JLabel();
        jLabel9 = new JLabel();
        btnModUsu = new JButton();
        txtIdUsu = new JTextField();
        jLabel10 = new JLabel();
        jPanel2 = new JPanel();
        jlbHotel = new JLabel();
        jlbSlogan = new JLabel();
        jPanel3 = new JPanel();
        jLabel11 = new JLabel();
        jlbImagen = new JLabel();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("REGISTRO DE USUARIOS");
        setBackground(new Color(255, 219, 95));
        setMinimumSize(new Dimension(835, 620));
        setPreferredSize(new Dimension(835, 620));
        setResizable(false);

        jPanel1.setBackground(new Color(255, 255, 255));
        jPanel1.setMinimumSize(new Dimension(850, 628));
        jPanel1.setPreferredSize(new Dimension(850, 628));
        jPanel1.setLayout(new AbsoluteLayout());

        jLabel1.setBackground(new Color(255, 255, 255));
        jLabel1.setFont(new Font("Dialog", 1, 11)); // NOI18N
        jLabel1.setText("ID HOTEL:");
        jPanel1.add(jLabel1, new AbsoluteConstraints(50, 342, 70, 25));

        regisunom.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                regisunomKeyPressed(evt);
            }
            public void keyTyped(KeyEvent evt) {
                regisunomKeyTyped(evt);
            }
        });
        jPanel1.add(regisunom, new AbsoluteConstraints(214, 99, 225, 25));

        regisuape.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                regisuapeKeyPressed(evt);
            }
            public void keyTyped(KeyEvent evt) {
                regisuapeKeyTyped(evt);
            }
        });
        jPanel1.add(regisuape, new AbsoluteConstraints(214, 139, 225, 25));

        regisuco.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                regisucoKeyPressed(evt);
            }
        });
        jPanel1.add(regisuco, new AbsoluteConstraints(214, 176, 225, 25));

        regisusu.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                regisusuKeyPressed(evt);
            }
        });
        jPanel1.add(regisusu, new AbsoluteConstraints(214, 220, 225, 25));

        regisucon.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                regisuconKeyPressed(evt);
            }
        });
        jPanel1.add(regisucon, new AbsoluteConstraints(214, 261, 225, 25));

        regisuconfircon.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                regisuconfirconKeyPressed(evt);
            }
        });
        jPanel1.add(regisuconfircon, new AbsoluteConstraints(219, 302, 220, 25));

        cbxtipoUsuario.setBackground(new Color(255, 219, 95));
        cbxtipoUsuario.setModel(new DefaultComboBoxModel<>(new String[] { "Seleccionar", "Administrador", "Recepcionista", "Vendedor" }));
        jPanel1.add(cbxtipoUsuario, new AbsoluteConstraints(222, 386, 217, -1));

        regisudoc.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                regisudocKeyPressed(evt);
            }
        });
        jPanel1.add(regisudoc, new AbsoluteConstraints(570, 100, 225, 25));

        regisutel.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                regisutelKeyPressed(evt);
            }
            public void keyTyped(KeyEvent evt) {
                regisutelKeyTyped(evt);
            }
        });
        jPanel1.add(regisutel, new AbsoluteConstraints(570, 140, 230, 25));

        jLabel2.setBackground(new Color(255, 255, 255));
        jLabel2.setFont(new Font("Dialog", 1, 11)); // NOI18N
        jLabel2.setText("APELLIDOS:");
        jPanel1.add(jLabel2, new AbsoluteConstraints(50, 139, 160, 25));

        jLabel3.setBackground(new Color(255, 255, 255));
        jLabel3.setFont(new Font("Dialog", 1, 11)); // NOI18N
        jLabel3.setText("CORREO:");
        jPanel1.add(jLabel3, new AbsoluteConstraints(50, 180, 160, 25));

        jLabel4.setBackground(new Color(255, 255, 255));
        jLabel4.setFont(new Font("Dialog", 1, 11)); // NOI18N
        jLabel4.setText("USUARIO:");
        jPanel1.add(jLabel4, new AbsoluteConstraints(50, 220, 160, 25));

        jLabel5.setBackground(new Color(255, 255, 255));
        jLabel5.setFont(new Font("Dialog", 1, 11)); // NOI18N
        jLabel5.setText("CONTRASEÑA:");
        jPanel1.add(jLabel5, new AbsoluteConstraints(50, 260, 160, 25));

        jLabel6.setBackground(new Color(255, 255, 255));
        jLabel6.setFont(new Font("Dialog", 1, 11)); // NOI18N
        jLabel6.setText("CONFIRMAR CONTRASEÑA:");
        jPanel1.add(jLabel6, new AbsoluteConstraints(50, 301, 150, 25));

        jLabel7.setBackground(new Color(255, 255, 255));
        jLabel7.setFont(new Font("Dialog", 1, 11)); // NOI18N
        jLabel7.setText("CARGO:");
        jPanel1.add(jLabel7, new AbsoluteConstraints(50, 383, 130, 25));

        regisuregis.setBackground(new Color(0, 51, 51));
        regisuregis.setForeground(new Color(255, 255, 255));
        regisuregis.setText("REGISTRAR");
        regisuregis.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                regisuregisMouseEntered(evt);
            }
            public void mouseExited(MouseEvent evt) {
                regisuregisMouseExited(evt);
            }
        });
        regisuregis.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                regisuregisActionPerformed(evt);
            }
        });
        regisuregis.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                regisuregisKeyPressed(evt);
            }
        });
        jPanel1.add(regisuregis, new AbsoluteConstraints(70, 460, 160, 40));

        txtIdHotel.setEditable(false);
        txtIdHotel.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                txtIdHotelKeyPressed(evt);
            }
        });
        jPanel1.add(txtIdHotel, new AbsoluteConstraints(219, 343, 220, 25));

        regisumod.setBackground(new Color(0, 51, 51));
        regisumod.setForeground(new Color(255, 255, 255));
        regisumod.setText("MODIFICAR");
        regisumod.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                regisumodMouseClicked(evt);
            }
            public void mouseEntered(MouseEvent evt) {
                regisumodMouseEntered(evt);
            }
            public void mouseExited(MouseEvent evt) {
                regisumodMouseExited(evt);
            }
        });
        jPanel1.add(regisumod, new AbsoluteConstraints(250, 460, 160, 40));

        regisueli.setBackground(new Color(0, 51, 51));
        regisueli.setForeground(new Color(255, 255, 255));
        regisueli.setText("ELIMINAR");
        regisueli.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                regisueliMouseEntered(evt);
            }
            public void mouseExited(MouseEvent evt) {
                regisueliMouseExited(evt);
            }
        });
        jPanel1.add(regisueli, new AbsoluteConstraints(610, 460, 160, 40));

        btnVolverInicio.setBackground(new Color(255, 179, 39));
        btnVolverInicio.setFont(new Font("Dialog", 1, 12)); // NOI18N
        btnVolverInicio.setText("SALIR");
        btnVolverInicio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnVolverInicioActionPerformed(evt);
            }
        });
        jPanel1.add(btnVolverInicio, new AbsoluteConstraints(660, 530, 130, 30));

        jLabel8.setBackground(new Color(255, 255, 255));
        jLabel8.setFont(new Font("Dialog", 1, 11)); // NOI18N
        jLabel8.setText("NOMBRES: ");
        jPanel1.add(jLabel8, new AbsoluteConstraints(50, 98, 160, 25));

        jLabel9.setBackground(new Color(255, 255, 255));
        jLabel9.setFont(new Font("Dialog", 1, 11)); // NOI18N
        jLabel9.setText("CEDULA: ");
        jPanel1.add(jLabel9, new AbsoluteConstraints(498, 98, 70, 25));

        btnModUsu.setBackground(new Color(0, 51, 51));
        btnModUsu.setForeground(new Color(255, 255, 255));
        btnModUsu.setText("MODIFICAR USUARIO ");
        btnModUsu.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                btnModUsuMouseClicked(evt);
            }
            public void mouseEntered(MouseEvent evt) {
                btnModUsuMouseEntered(evt);
            }
            public void mouseExited(MouseEvent evt) {
                btnModUsuMouseExited(evt);
            }
        });
        jPanel1.add(btnModUsu, new AbsoluteConstraints(430, 460, 160, 40));

        txtIdUsu.setEditable(false);
        txtIdUsu.setFocusable(false);
        jPanel1.add(txtIdUsu, new AbsoluteConstraints(20, 540, 47, 30));

        jLabel10.setBackground(new Color(255, 255, 255));
        jLabel10.setFont(new Font("Dialog", 1, 11)); // NOI18N
        jLabel10.setText("TELEFONO:");
        jPanel1.add(jLabel10, new AbsoluteConstraints(498, 139, 70, 25));

        jPanel2.setBackground(new Color(0, 102, 102));
        jPanel2.setLayout(new AbsoluteLayout());

        jlbHotel.setFont(new Font("Dialog", 1, 24)); // NOI18N
        jlbHotel.setForeground(new Color(255, 255, 255));
        jlbHotel.setText("HOTEL DORAL ");
        jPanel2.add(jlbHotel, new AbsoluteConstraints(12, 12, 520, 36));

        jlbSlogan.setForeground(new Color(255, 255, 255));
        jlbSlogan.setText(" Un cinco estrellas, a precio de una!");
        jPanel2.add(jlbSlogan, new AbsoluteConstraints(10, 40, 450, -1));

        jPanel1.add(jPanel2, new AbsoluteConstraints(0, 0, 950, 80));

        jPanel3.setBackground(new Color(0, 102, 102));

        jLabel11.setFont(new Font("Dialog", 1, 18)); // NOI18N
        jLabel11.setForeground(new Color(255, 255, 255));
        jLabel11.setText("HotelesMas.com");

        GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11, GroupLayout.PREFERRED_SIZE, 249, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1.add(jPanel3, new AbsoluteConstraints(0, 576, 950, -1));
        jPanel1.add(jlbImagen, new AbsoluteConstraints(500, 180, 300, 230));

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, 850, GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnVolverInicioActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnVolverInicioActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnVolverInicioActionPerformed

    private void regisunomKeyPressed(KeyEvent evt) {//GEN-FIRST:event_regisunomKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            regisuape.requestFocus();
        }
    }//GEN-LAST:event_regisunomKeyPressed

    private void regisuapeKeyPressed(KeyEvent evt) {//GEN-FIRST:event_regisuapeKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            regisudoc.requestFocus();
        }
    }//GEN-LAST:event_regisuapeKeyPressed

    private void regisucoKeyPressed(KeyEvent evt) {//GEN-FIRST:event_regisucoKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            regisusu.requestFocus();
        }
    }//GEN-LAST:event_regisucoKeyPressed

    private void regisusuKeyPressed(KeyEvent evt) {//GEN-FIRST:event_regisusuKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            regisucon.requestFocus();
        }
    }//GEN-LAST:event_regisusuKeyPressed

    private void regisuconKeyPressed(KeyEvent evt) {//GEN-FIRST:event_regisuconKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            regisuconfircon.requestFocus();
        }
    }//GEN-LAST:event_regisuconKeyPressed

    private void regisuregisActionPerformed(ActionEvent evt) {//GEN-FIRST:event_regisuregisActionPerformed
        registroUsuario();
    }//GEN-LAST:event_regisuregisActionPerformed

    private void regisuconfirconKeyPressed(KeyEvent evt) {//GEN-FIRST:event_regisuconfirconKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtIdHotel.requestFocus();
        }
    }//GEN-LAST:event_regisuconfirconKeyPressed

    private void regisunomKeyTyped(KeyEvent evt) {//GEN-FIRST:event_regisunomKeyTyped
        char c = evt.getKeyChar();
        if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && (c < ' ' || c > ' ')) {
            evt.consume();
        }
        String str = regisunom.getText();

        StringBuffer strbf = new StringBuffer();
        Matcher match = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(str);
        while (match.find()) {
            match.appendReplacement(strbf, match.group(1).toUpperCase() + match.group(2).toLowerCase());
        }
        regisunom.setText(match.appendTail(strbf).toString());
    }//GEN-LAST:event_regisunomKeyTyped

    private void regisuapeKeyTyped(KeyEvent evt) {//GEN-FIRST:event_regisuapeKeyTyped
        char c = evt.getKeyChar();
        if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && (c < ' ' || c > ' ')) {
            evt.consume();
        }
        String str = regisuape.getText();
        StringBuffer strbf = new StringBuffer();
        Matcher match = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(str);
        while (match.find()) {
            match.appendReplacement(strbf, match.group(1).toUpperCase() + match.group(2).toLowerCase());
        }
        regisuape.setText(match.appendTail(strbf).toString());
    }//GEN-LAST:event_regisuapeKeyTyped

    private void regisudocKeyPressed(KeyEvent evt) {//GEN-FIRST:event_regisudocKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            regisutel.requestFocus();
        }
    }//GEN-LAST:event_regisudocKeyPressed

    private void regisutelKeyPressed(KeyEvent evt) {//GEN-FIRST:event_regisutelKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            regisuco.requestFocus();
        }
    }//GEN-LAST:event_regisutelKeyPressed

    private void regisutelKeyTyped(KeyEvent evt) {//GEN-FIRST:event_regisutelKeyTyped
        char c = evt.getKeyChar();
        if ((c < '0' || c > '9'))
            evt.consume();
    }//GEN-LAST:event_regisutelKeyTyped

    private void regisuregisKeyPressed(KeyEvent evt) {//GEN-FIRST:event_regisuregisKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            registroUsuario();
        }
    }//GEN-LAST:event_regisuregisKeyPressed

    private void regisumodMouseClicked(MouseEvent evt) {//GEN-FIRST:event_regisumodMouseClicked
        String cedula = JOptionPane.showInputDialog("Ingrese el numero de documento\ndel usuario a modificar");
        Canectar con = new Canectar();
        java.sql.Connection conexion = con.conexion();
        PreparedStatement sp = null;
        ResultSet rs = null;
        try {
            String consulta = "SELECT u.Id_Usuario, u.Cedula, u.Nombres, u.Apellidos, u.Telefono, u.Correo, u.Usuario, u.Contrasenia, u.Id_Hotel, h.Rnt_Hotel "
                    + "FROM usuarios u "
                    + "JOIN hoteles h ON u.Id_Hotel = h.Id_Hoteles "
                    + "WHERE u.Cedula = ?";

            sp = conexion.prepareStatement(consulta);
            sp.setString(1, cedula);
            rs = sp.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    txtIdUsu.setText(Integer.toString(rs.getInt("Id_Usuario")));
                    regisudoc.setText(rs.getString("Cedula"));
                    regisunom.setText(rs.getString("Nombres"));
                    regisuape.setText(rs.getString("Apellidos"));
                    regisutel.setText(rs.getString("Telefono"));
                    regisuco.setText(rs.getString("Correo"));
                    regisusu.setText(rs.getString("Usuario"));
                    txtIdHotel.setText(rs.getString("Rnt_Hotel"));

                }
                txtIdUsu.setVisible(true);
                regisumod.setVisible(false);
                btnModUsu.setVisible(true);
                regisuregis.setVisible(false);
                regisueli.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "El usuario solicitado no existe en la base de datos");
            }
            // Procesar los resultados de la consulta
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Cerrar ResultSet
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            // Cerrar PreparedStatement
            if (sp != null) {
                try {
                    sp.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            // Cerrar conexión
            if (conexion != null) {
                try {
                    conexion.close();
                } catch (SQLException ex) {
                    Logger.getLogger(FrmRegistrarUsuarios.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_regisumodMouseClicked

    private void btnModUsuMouseClicked(MouseEvent evt) {//GEN-FIRST:event_btnModUsuMouseClicked
        Hotel hotel = sqlusu.existeHotel(txtIdHotel.getText());

        if (hotel != null) {
            JOptionPane.showMessageDialog(null, "El " + hotel.getNombreHotel() + " se verifico exitosamente");
            String contrasenia = new String(regisucon.getPassword());
            String confirContrasenia = new String(regisuconfircon.getPassword());
            if (regisunom.getText().equals("") || regisuape.getText().equals("")
                    || regisudoc.getText().isEmpty() || regisuco.getText().equals("") || regisusu.getText().equals("")
                    || contrasenia.equals("") || confirContrasenia.equals("")) {
                JOptionPane.showMessageDialog(null, "Para modificar este usuario, es necesario "
                        + "que todos los campos esten correctamente diligenciados");
                regisunom.requestFocus();
            } else {
                if (hotel == null) {
                    JOptionPane.showMessageDialog(this, "El 'IDENTIFICADOR' ingresado NO EXISTE!");
                    txtIdHotel.setText("");
                    txtIdHotel.requestFocus();
                    return;
                }
                if (contrasenia.equals(confirContrasenia)) {
                    if (sqlusu.esEmail(regisuco.getText())) {
                        int IdHotel = hotel.getIdHoteles();
                        String nuevopass = BCrypt.hashpw(contrasenia, BCrypt.gensalt());
                        mod.setNombres(regisunom.getText());
                        mod.setApellidos(regisuape.getText());
                        mod.setCedula(regisudoc.getText());
                        mod.setCorreo(regisuco.getText());
                        mod.setUsuario(regisusu.getText());
                        mod.setContrasenia(nuevopass);
                        mod.setId_tipo(tipoUsuario());
                        mod.setTelefono(regisutel.getText());
                        mod.setIdUsuario(Integer.parseInt(txtIdUsu.getText()));
                        mod.setIdHotel(IdHotel);
                        if (sqlusu.registrarModificacion(mod)) {
                            JOptionPane.showMessageDialog(null, "Usuario modificado exitosamente!");
                            regisumod.setVisible(true);
                            btnModUsu.setVisible(false);
                            regisuregis.setVisible(true);
                            regisueli.setVisible(true);

                            regisunom.setText("");
                            regisuape.setText("");
                            regisudoc.setText("");
                            regisutel.setText("");
                            regisuco.setText("");
                            regisucon.setText("");
                            regisuconfircon.setText("");
                            regisusu.setText("");
                            txtIdHotel.setText("");

                        } else {
                            JOptionPane.showMessageDialog(null, "ERROR! El usuario no se pudo modificar");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "El correo electronico no es valido");
                        regisuco.requestFocus();
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "Las contraseñas no coinciden...");
                    regisucon.requestFocus();
                }
            }

        } else {
        }


    }//GEN-LAST:event_btnModUsuMouseClicked

    private void txtIdHotelKeyPressed(KeyEvent evt) {//GEN-FIRST:event_txtIdHotelKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            cbxtipoUsuario.requestFocus();
        }
    }//GEN-LAST:event_txtIdHotelKeyPressed

    private void regisuregisMouseEntered(MouseEvent evt) {//GEN-FIRST:event_regisuregisMouseEntered
        regisuregis.setBackground(new Color(0, 102, 102));
    }//GEN-LAST:event_regisuregisMouseEntered

    private void regisuregisMouseExited(MouseEvent evt) {//GEN-FIRST:event_regisuregisMouseExited
        regisuregis.setBackground(new Color(0, 51, 51));
    }//GEN-LAST:event_regisuregisMouseExited

    private void btnModUsuMouseEntered(MouseEvent evt) {//GEN-FIRST:event_btnModUsuMouseEntered
        btnModUsu.setBackground(new Color(0, 102, 102));
    }//GEN-LAST:event_btnModUsuMouseEntered

    private void btnModUsuMouseExited(MouseEvent evt) {//GEN-FIRST:event_btnModUsuMouseExited
        btnModUsu.setBackground(new Color(0, 51, 51));
    }//GEN-LAST:event_btnModUsuMouseExited

    private void regisumodMouseEntered(MouseEvent evt) {//GEN-FIRST:event_regisumodMouseEntered
        regisumod.setBackground(new Color(0, 102, 102));
    }//GEN-LAST:event_regisumodMouseEntered

    private void regisumodMouseExited(MouseEvent evt) {//GEN-FIRST:event_regisumodMouseExited
        regisumod.setBackground(new Color(0, 51, 51));
    }//GEN-LAST:event_regisumodMouseExited

    private void regisueliMouseEntered(MouseEvent evt) {//GEN-FIRST:event_regisueliMouseEntered
        regisueli.setBackground(new Color(0, 102, 102));
    }//GEN-LAST:event_regisueliMouseEntered

    private void regisueliMouseExited(MouseEvent evt) {//GEN-FIRST:event_regisueliMouseExited
        regisueli.setBackground(new Color(0, 51, 51));
    }//GEN-LAST:event_regisueliMouseExited


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton btnModUsu;
    private JButton btnVolverInicio;
    private JComboBox<String> cbxtipoUsuario;
    private JLabel jLabel1;
    private JLabel jLabel10;
    private JLabel jLabel11;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JLabel jLabel8;
    private JLabel jLabel9;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JLabel jlbHotel;
    private JLabel jlbImagen;
    private JLabel jlbSlogan;
    private JTextField regisuape;
    private JTextField regisuco;
    private JPasswordField regisucon;
    private JPasswordField regisuconfircon;
    private JTextField regisudoc;
    private JButton regisueli;
    private JButton regisumod;
    private JTextField regisunom;
    private JButton regisuregis;
    private JTextField regisusu;
    private JTextField regisutel;
    private JTextField txtIdHotel;
    private JTextField txtIdUsu;
    // End of variables declaration//GEN-END:variables
}
