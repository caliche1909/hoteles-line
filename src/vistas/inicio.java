package vistas;

import conectar.Canectar;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import modelo.Hotel;
import modelo.SqlUsuarios;
import modelo.Turno;
import modelo.UsuarioOperando;

public final class inicio extends javax.swing.JFrame {

    Canectar con = new Canectar();
    SqlUsuarios sql = new SqlUsuarios();
    UsuarioOperando usus = new UsuarioOperando();
    boolean validarTxt = true;

    public inicio() {

        initComponents();

        btnIniciarTurno.addMouseListener(mouseAdapter);
        iniingre.addMouseListener(mouseAdapter);
        btnCrearTurno.addMouseListener(mouseAdapter);

        jpnTurno.setVisible(false);
        btnAtraz.setVisible(false);
        btnIniciarTurno.setVisible(false);
        txtIdHotel.requestFocus();
        jdchInicio.setDate(new Date());
        this.setLocationRelativeTo(null);

        // Obtiene el ClassLoader del contexto actual
        ClassLoader classLoader = getClass().getClassLoader();
        // Carga la imagen como un recurso del ClassLoader
        ImageIcon icon = new ImageIcon(classLoader.getResource("img/Imagen2.png"));
        ImageIcon icon2 = new ImageIcon(classLoader.getResource("img/LogoHM.png"));
        // Escalar la imagen si es necesario y establecerla en el JLabel
        jlbFondoInicio1.setIcon(new ImageIcon(icon.getImage().getScaledInstance(jlbFondoInicio1.getWidth(), jlbFondoInicio1.getHeight(), Image.SCALE_SMOOTH)));
        jlbFondoP.setIcon(new ImageIcon(icon2.getImage().getScaledInstance(jlbFondoP.getWidth(), jlbFondoP.getHeight(), Image.SCALE_SMOOTH)));

    }

    public boolean validarHora(String hora) {
        String regex = "^([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$";
        return hora.matches(regex);
    }

    public void crearTurno() {
        LocalDateTime now = LocalDateTime.now();
        String pass = new String(inipas.getPassword());
        UsuarioOperando usuario = sql.existeUsuario(iniusu.getText());
        int FkIdUsuario = 0;
        if (usuario != null) {
            FkIdUsuario = usuario.getIdUsuario();
        }
         

        usus.setUsuario(iniusu.getText());
        usus.setContrasenia(pass);
        usus.setUltima_secion(now);
        
        UsuarioOperando usuarioOperando = sql.login(usus);
        boolean registroExitoso = false;
        if (usuarioOperando != null) {
            registroExitoso = true;
            usus = usuarioOperando;
            
        }

        if ( registroExitoso) {
            LocalDateTime xInicio = LocalDateTime.ofInstant(jdchInicio.getDate().toInstant(), ZoneId.systemDefault());
            LocalDateTime xFin = LocalDateTime.ofInstant(jdchFinal.getDate().toInstant(), ZoneId.systemDefault());

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            String fechaInicioStr = dateFormatter.format(xInicio);
            String fechaFinStr = dateFormatter.format(xFin);

            String horaInicioStr = txtHoraInicio.getText();
            String horaFinStr = txtHoraFin.getText();

            try {
                Hotel hotel = sql.existeHotel(txtIdHotel.getText());
                if (hotel != null) {
                    int FkIdHotel = hotel.getIdHoteles();
                    LocalDateTime inicio = LocalDateTime.parse(fechaInicioStr + " " + horaInicioStr, dateTimeFormatter);
                    LocalDateTime fin = LocalDateTime.parse(fechaFinStr + " " + horaFinStr, dateTimeFormatter);
                    String estadoTurno = "Activo";
                    String criterioCaja = "Abierta";
                    Turno nuevoTurno = new Turno();
                    nuevoTurno.setInicio(inicio);
                    nuevoTurno.setFin(fin);
                    nuevoTurno.setEstadoTurno(estadoTurno);
                    nuevoTurno.setCriterioCaja(criterioCaja);
                    nuevoTurno.setFkIdUsuario(FkIdUsuario);
                    nuevoTurno.setFkIdHotel(FkIdHotel);
                    try {
                        int idGenerado = con.registrarTurno(
                                nuevoTurno.getInicio(),
                                nuevoTurno.getFin(),
                                nuevoTurno.getEstadoTurno(),
                                nuevoTurno.getCriterioCaja(),
                                nuevoTurno.getFkIdUsuario(),
                                nuevoTurno.getFkIdHotel()
                        );

                        JOptionPane.showMessageDialog(this, """
                                                        Su nuevo turno se a creado exitosamente
                                                        BIENVENIDO
                                                        Seguro será un excelente día.
                                                        """, "HotelesMas", JOptionPane.INFORMATION_MESSAGE);
                        usus.setTurnoPresente(idGenerado);
                        usus.setTurnoNCreado(true);
                        Recepciones ir = new Recepciones(usus, hotel);
                        ir.turnoCreado = true;
                        ir.setVisible(true);
                        this.dispose();
                    } catch (HeadlessException e) {
                        JOptionPane.showMessageDialog(this, "Error al iniciar el turno", "HotelesMas", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Este hotel no existe en nuestra base de datos", "HotelesMas", JOptionPane.INFORMATION_MESSAGE);
                }

            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, """
                                                        Error en el formato de la hora y/o fecha.
                                                        Por favor, verifica que el formato sea correcto.""", "HotelesMas", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        } else {
            JOptionPane.showMessageDialog(null, "Los datos insertados para iniciar sesion son incorrectos", "HotelesMas", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void sumarDia() {
        Date fechaIniTurno = jdchInicio.getDate();
        if (fechaIniTurno != null) {
            Calendar calendar = Calendar.getInstance();
            // Remueve la hora, minuto y segundo del calendario actual
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            // Comprueba si la fecha seleccionada es anterior a la fecha actual
            if (fechaIniTurno.before(calendar.getTime())) {
                JOptionPane.showMessageDialog(null, "Por favor seleccione la fecha presente.", "Error en la fecha", JOptionPane.ERROR_MESSAGE);
                jdchInicio.setDate(new Date());

            } else {
                calendar.setTime(fechaIniTurno);
                calendar.add(Calendar.DATE, 1);
                Date fechaSalida = calendar.getTime();
                jdchFinal.setDate(fechaSalida);
            }
        }
    }

    MouseAdapter mouseAdapter = new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            JButton button = (JButton) e.getSource();
            button.setBackground(new Color(0, 102, 102));
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        public void mouseExited(MouseEvent e) {
            JButton button = (JButton) e.getSource();
            button.setBackground(new Color(0, 51, 51));
        }
    };

    public String horaHoy() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        return formattedDateTime;
    }

    private boolean verificarCampos() {
        if (iniusu.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el usuario", "HotelesMas", JOptionPane.INFORMATION_MESSAGE);
            iniusu.requestFocus();
            return false;
        }
        String pass = new String(inipas.getPassword());
        if (pass.equals("")) {
            JOptionPane.showMessageDialog(this, "Ingrese su clave", "HotelesMas", JOptionPane.INFORMATION_MESSAGE);
            inipas.requestFocus();
            return false;
        }
        if (txtIdHotel.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Ingrese el codigo del hotel", "HotelesMas", JOptionPane.INFORMATION_MESSAGE);
            txtIdHotel.requestFocus();
            return false;
        }

        return true;
    }

    public void loginx() {

        if (!verificarCampos()) {
            return;
        }
        Hotel hotel = con.existeHotel(txtIdHotel.getText());

        if (hotel == null) {
            JOptionPane.showMessageDialog(null, "El Codigo del hotel no existe!", "HotelesMas", JOptionPane.INFORMATION_MESSAGE);
            txtIdHotel.setText("");
            iniusu.setText("");
            inipas.setText("");
            txtIdHotel.requestFocus();
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        // ultimo turno me devuelva los datos del ultimo turno y el usuario encargado
        Turno ultimoTurno = sql.obtenerUltimoTurno(hotel.getIdHoteles());

        usus.setUsuario(iniusu.getText());
        usus.setContrasenia(new String(inipas.getPassword()));
        usus.setUltima_secion(now);
        usus.setTurnoPresente(ultimoTurno.getIdTurno());

        UsuarioOperando usuarioLogin = sql.login(usus);
        boolean registroExitoso = false;

        if (usuarioLogin != null) {

            registroExitoso = true;
            usus = usuarioLogin;

        } else {
            JOptionPane.showMessageDialog(this, "El usuario ingresado No Existe.");
            return;
        }

        if (registroExitoso && usuarioLogin.getId_tipo() == 1) {
            if (ultimoTurno != null) {
                usus.setTurnoPresente(ultimoTurno.getIdTurno());
            }

            JOptionPane.showMessageDialog(this, "BIENVENIDO " + usus.getNombres() + "\nSeguro tendras un excelente dia.", "HotelesMas", JOptionPane.INFORMATION_MESSAGE);
            Recepciones ir = new Recepciones(usus, hotel);
            ir.setVisible(true);
            this.dispose();
            return;
        }

        if (ultimoTurno != null) {

            if (ultimoTurno.getEstadoTurno().equals("Activo") && ultimoTurno.getUsuario().getUsuario().equals(iniusu.getText())) {

                if (registroExitoso) {
                    usus.setTurnoPresente(ultimoTurno.getIdTurno());
                    usus.setTurnoNCreado(false);
                    JOptionPane.showMessageDialog(this, "BIENVENIDO\nSeguro tendras un excelente dia.", "HotelesMas", JOptionPane.INFORMATION_MESSAGE);
                    Recepciones ir = new Recepciones(usus, hotel);
                    ir.setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Los datos insertados para iniciar sesion son incorrectos", "HotelesMas", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Debe crear un turno para\ningresar al sistema.", "HotelesMas", JOptionPane.INFORMATION_MESSAGE);
                btnCrearTurnoMouseClicked(null);

            }
        } else {
            JOptionPane.showMessageDialog(this, "No existen turnos en la base de datos, por favor debe crear uno.", "HotelesMas", JOptionPane.INFORMATION_MESSAGE);
            txtIdHotel.setText("");
            iniusu.setText("");
            inipas.setText("");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        iniusu = new javax.swing.JTextField();
        inipas = new javax.swing.JPasswordField();
        moscon = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        btnIniciarTurno = new javax.swing.JButton();
        btnCrearTurno = new javax.swing.JButton();
        iniingre = new javax.swing.JButton();
        btnAtraz = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtIdHotel = new javax.swing.JTextField();
        jpnTurno = new javax.swing.JPanel();
        jlbFondoInicio = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jdchInicio = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        jdchFinal = new com.toedter.calendar.JDateChooser();
        jLabel11 = new javax.swing.JLabel();
        txtHoraInicio = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtHoraFin = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jlbFondoP = new javax.swing.JLabel();
        jlbFondoInicio1 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("HOTEL DORAL PLAZA");
        setBounds(new java.awt.Rectangle(100, 100, 0, 0));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(0, 102, 102));

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setBackground(new java.awt.Color(0, 0, 0));
        jLabel3.setForeground(new java.awt.Color(255, 219, 95));
        jLabel3.setText("CLAVE:");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, 60, 25));

        iniusu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                iniusuKeyPressed(evt);
            }
        });
        jPanel2.add(iniusu, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 170, 180, 25));

        inipas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                inipasKeyPressed(evt);
            }
        });
        jPanel2.add(inipas, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 210, 180, 25));

        moscon.setBackground(new java.awt.Color(0, 102, 102));
        moscon.setForeground(new java.awt.Color(255, 255, 255));
        moscon.setText("Mostrar contraseña");
        moscon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mosconActionPerformed(evt);
            }
        });
        jPanel2.add(moscon, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 240, 150, -1));

        jLabel5.setForeground(new java.awt.Color(255, 219, 95));
        jLabel5.setText("CODIGO HOTEL:");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 110, 25));

        btnIniciarTurno.setBackground(new java.awt.Color(0, 51, 51));
        btnIniciarTurno.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        btnIniciarTurno.setForeground(new java.awt.Color(255, 255, 255));
        btnIniciarTurno.setText("INICIAR NUEVO TURNO");
        btnIniciarTurno.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnIniciarTurnoMouseClicked(evt);
            }
        });
        jPanel2.add(btnIniciarTurno, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 390, 160, 40));

        btnCrearTurno.setBackground(new java.awt.Color(0, 51, 51));
        btnCrearTurno.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        btnCrearTurno.setForeground(new java.awt.Color(255, 255, 255));
        btnCrearTurno.setText("CREAR TURNO");
        btnCrearTurno.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCrearTurnoMouseClicked(evt);
            }
        });
        jPanel2.add(btnCrearTurno, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 450, 120, 40));

        iniingre.setBackground(new java.awt.Color(0, 51, 51));
        iniingre.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        iniingre.setForeground(new java.awt.Color(255, 255, 255));
        iniingre.setText("INGRESAR");
        iniingre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iniingreActionPerformed(evt);
            }
        });
        iniingre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                iniingreKeyPressed(evt);
            }
        });
        jPanel2.add(iniingre, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 450, 120, 40));

        btnAtraz.setBackground(new java.awt.Color(255, 179, 39));
        btnAtraz.setForeground(new java.awt.Color(0, 0, 0));
        btnAtraz.setText("Atras");
        btnAtraz.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAtrazMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnAtrazMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnAtrazMouseExited(evt);
            }
        });
        jPanel2.add(btnAtraz, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 510, -1, -1));

        jLabel7.setForeground(new java.awt.Color(255, 219, 95));
        jLabel7.setText("USUARIO:");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, 70, 25));

        txtIdHotel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtIdHotelKeyPressed(evt);
            }
        });
        jPanel2.add(txtIdHotel, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 130, 120, 25));

        jpnTurno.setBackground(new java.awt.Color(0, 0, 0,80));
        jpnTurno.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jlbFondoInicio.setBackground(new java.awt.Color(0, 0, 0));
        jpnTurno.add(jlbFondoInicio, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 40, 410, 440));

        jLabel10.setForeground(new java.awt.Color(255, 219, 95));
        jLabel10.setText("FECHA DE INICIO ");
        jpnTurno.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 110, 25));

        jdchInicio.setBackground(new java.awt.Color(255, 219, 95));
        jdchInicio.setDateFormatString("EEE dd-MMM-yyyy");
        jpnTurno.add(jdchInicio, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, 160, 25));

        jLabel2.setForeground(new java.awt.Color(255, 219, 95));
        jLabel2.setText("FECHA  FINAL     ");
        jpnTurno.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 110, 25));

        jdchFinal.setBackground(new java.awt.Color(255, 219, 95));
        jdchFinal.setDateFormatString("EEE dd-MMM-yyyy");
        jpnTurno.add(jdchFinal, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 50, 160, 25));

        jLabel11.setForeground(new java.awt.Color(255, 219, 95));
        jLabel11.setText("HORA");
        jpnTurno.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 20, 40, 25));

        txtHoraInicio.setEditable(false);
        jpnTurno.add(txtHoraInicio, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 20, 65, 25));

        jLabel8.setForeground(new java.awt.Color(255, 219, 95));
        jLabel8.setText("HORA");
        jpnTurno.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 50, 40, 25));

        txtHoraFin.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtHoraFinFocusLost(evt);
            }
        });
        jpnTurno.add(txtHoraFin, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 50, 65, 25));

        jSeparator1.setBackground(new java.awt.Color(255, 219, 95));
        jSeparator1.setForeground(new java.awt.Color(255, 219, 95));
        jpnTurno.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 90, 430, 10));

        jPanel2.add(jpnTurno, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 430, 95));
        jPanel2.add(jlbFondoP, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 430, 550));

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Doral Plaza");

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("¡Bienvenido!");
        jLabel4.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("www.HotelesMas.com");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jlbFondoInicio1, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(81, 81, 81))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(86, 86, 86)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(65, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel1)
                .addGap(0, 0, 0)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlbFondoInicio1, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 940, 550));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void iniingreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iniingreActionPerformed
        loginx();
    }//GEN-LAST:event_iniingreActionPerformed

    private void mosconActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mosconActionPerformed
        if (moscon.isSelected()) {
            inipas.setEchoChar((char) 0);
        } else {
            inipas.setEchoChar('*');
        }
    }//GEN-LAST:event_mosconActionPerformed

    private void iniusuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_iniusuKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            inipas.requestFocus();
        }
    }//GEN-LAST:event_iniusuKeyPressed

    private void inipasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inipasKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            iniingre.requestFocus();
        }
    }//GEN-LAST:event_inipasKeyPressed

    private void iniingreKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_iniingreKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            loginx();
        }
    }//GEN-LAST:event_iniingreKeyPressed

    private void txtIdHotelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtIdHotelKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            iniusu.requestFocus();
        }
    }//GEN-LAST:event_txtIdHotelKeyPressed

    private void btnCrearTurnoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCrearTurnoMouseClicked
        jpnTurno.setVisible(true);
        btnIniciarTurno.setVisible(true);
        btnCrearTurno.setVisible(false);
        iniingre.setVisible(false);
        txtHoraInicio.setText(horaHoy());
        btnAtraz.setVisible(true);
        sumarDia();
        txtHoraFin.requestFocus();
    }//GEN-LAST:event_btnCrearTurnoMouseClicked

    private void btnAtrazMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAtrazMouseClicked
        jpnTurno.setVisible(false);
        btnIniciarTurno.setVisible(false);
        btnCrearTurno.setVisible(true);
        iniingre.setVisible(true);
        btnAtraz.setVisible(false);
    }//GEN-LAST:event_btnAtrazMouseClicked

    private void btnIniciarTurnoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnIniciarTurnoMouseClicked
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime inicioDate = LocalDateTime.ofInstant(jdchInicio.getDate().toInstant(), ZoneId.systemDefault());        
        Hotel hotel = sql.existeHotel(txtIdHotel.getText());
        boolean sameDay = now.getYear() == inicioDate.getYear()
                && now.getDayOfYear() == inicioDate.getDayOfYear();

        if (!sameDay) {
            JOptionPane.showMessageDialog(this, """
                                        Por favor seleccione como fecha de inicio
                                        la que corresponda al dia de hoy""", "HotelesMas", JOptionPane.INFORMATION_MESSAGE);
            jdchInicio.setDate(Date.from(Instant.now()));
            return;
        }

        if (txtHoraInicio.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la hora de inicio de su turno", "HotelesMas", JOptionPane.INFORMATION_MESSAGE);
            txtHoraInicio.requestFocus();
            return;
        }

        if (txtHoraFin.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la hora de finalizacion de su turno", "HotelesMas", JOptionPane.INFORMATION_MESSAGE);
            txtHoraFin.requestFocus();
            return;
        }

        if (txtIdHotel.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el codigo del hotel", "HotelesMas", JOptionPane.INFORMATION_MESSAGE);
            txtIdHotel.requestFocus();
            return;
        }

        if (iniusu.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese Usuario", "HotelesMas", JOptionPane.INFORMATION_MESSAGE);
            iniusu.requestFocus();
            return;
        }

        if (inipas.getPassword().equals("")) {
            JOptionPane.showMessageDialog(this, "Digite su clave de ingreso", "HotelesMas", JOptionPane.INFORMATION_MESSAGE);
            inipas.requestFocus();
            return;
        }
        if (hotel == null) {
            JOptionPane.showMessageDialog(null, "El Codigo del hotel no existe!", "HotelesMas", JOptionPane.INFORMATION_MESSAGE);
            txtIdHotel.setText("");
            iniusu.setText("");
            inipas.setText("");
            txtIdHotel.requestFocus();
            return;
        }
        int FkIdHotel = hotel.getIdHoteles();
        Turno ultimoTurno = sql.obtenerUltimoTurno(FkIdHotel);

        if (ultimoTurno == null) {
            crearTurno();
        } else {
            if (ultimoTurno.getEstadoTurno().equals("Terminado")) {
                crearTurno();
            } else {
                JOptionPane.showMessageDialog(this, """
                                                No se puede iniciar un nuevo turno,
                                                si el turno anterior aun sigue activo.
                                                Por favor finalice el ultimo turno""", "HotelesMas", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnIniciarTurnoMouseClicked

    private void txtHoraFinFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtHoraFinFocusLost
        if (validarTxt) {
            String horaFinal = txtHoraFin.getText();
            if (validarHora(horaFinal)) {
                txtIdHotel.requestFocus();
            } else {
                JOptionPane.showMessageDialog(this, """
                                                Ingrese la hora de salida en
                                                formato "HH:mm:ss".
                                                """, "HotelesMas", JOptionPane.INFORMATION_MESSAGE);
                txtHoraFin.setText("");
                txtHoraFin.requestFocus();
            }
        }
    }//GEN-LAST:event_txtHoraFinFocusLost

    private void btnAtrazMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAtrazMouseEntered
        btnAtraz.setBackground(new Color(255, 219, 95));
        validarTxt = false;
    }//GEN-LAST:event_btnAtrazMouseEntered

    private void btnAtrazMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAtrazMouseExited
        btnAtraz.setBackground(new Color(255, 179, 39));
        validarTxt = true;
    }//GEN-LAST:event_btnAtrazMouseExited


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAtraz;
    private javax.swing.JButton btnCrearTurno;
    private javax.swing.JButton btnIniciarTurno;
    private javax.swing.JButton iniingre;
    private javax.swing.JPasswordField inipas;
    private javax.swing.JTextField iniusu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private com.toedter.calendar.JDateChooser jdchFinal;
    private com.toedter.calendar.JDateChooser jdchInicio;
    private javax.swing.JLabel jlbFondoInicio;
    private javax.swing.JLabel jlbFondoInicio1;
    private javax.swing.JLabel jlbFondoP;
    private javax.swing.JPanel jpnTurno;
    private javax.swing.JCheckBox moscon;
    private javax.swing.JTextField txtHoraFin;
    private javax.swing.JTextField txtHoraInicio;
    private javax.swing.JTextField txtIdHotel;
    // End of variables declaration//GEN-END:variables
}
