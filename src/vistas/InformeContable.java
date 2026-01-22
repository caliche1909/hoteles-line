package vistas;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import conectar.Canectar;
import java.awt.Color;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.Hotel;
import modelo.UsuarioOperando;

public final class InformeContable extends javax.swing.JDialog {

    Canectar con = new Canectar();
    UsuarioOperando usus;
    Hotel hotel;

    public InformeContable(java.awt.Frame parent, boolean modal, UsuarioOperando usus, Hotel hotel) {
        super(parent, modal);
        this.usus = usus;
        this.hotel = hotel;
        initComponents();
        jpnHotelSlogan.setVisible(false);
        jpnDatosHotel.setVisible(false);
        jpnPromedios.setVisible(false);
        jpnCalculos.setVisible(false);
        jlbDesde.setVisible(false);
        jdchDesde.setVisible(false);
        jlbHasta.setVisible(false);
        jdchHasta.setVisible(false);
        btnGenerar.setVisible(false);
        traerHotelesCbx();
        this.setLocationRelativeTo(null);
        jdchHasta.setDate(new Date());
        jdchDesde.setDate(agregarDiasAFecha(new Date(), -15));
    }

    private void formatoPuntosMil(JLabel label) {
        String text = label.getText();
        int len = text.length();
        StringBuilder newText = new StringBuilder();

        int count = 0;
        for (int i = len - 1; i >= 0; i--) {
            if (count == 3) {
                newText.insert(0, ',');
                count = 0;
            }

            char ch = text.charAt(i);
            if (ch != ',') {
                newText.insert(0, ch);
                count++;
            }
        }

        // Si el formato es diferente, actualiza el texto en label
        if (!newText.toString().equals(text)) {
            label.setText(newText.toString());
        }
    }

    private Date agregarDiasAFecha(Date fecha, int dias) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.add(Calendar.DATE, dias);
        return calendar.getTime();
    }

    public void traerHotelesCbx() {
        java.sql.Connection conex = con.conexion();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT Nombre_Hotel FROM hoteles ORDER BY Nombre_Hotel";
            ps = conex.prepareStatement(sql);
            rs = ps.executeQuery();

            cbxSelecHotel.removeAllItems();
            cbxSelecHotel.addItem("Seleccione");
            while (rs.next()) {
                String hotel = rs.getString("Nombre_Hotel");
                cbxSelecHotel.addItem(hotel);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener datos de hotel  : " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (conex != null) {
                    conex.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al serrar recursos de traerHotelescbx  : " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void llenarTabla() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechaDesde = sdf.format(jdchDesde.getDate());
        String fechaHasta = sdf.format(jdchHasta.getDate());
        int idHotel = Integer.parseInt(jlbIdHotel.getText());
        DefaultTableModel model = (DefaultTableModel) tblInforme.getModel();
        model.setRowCount(0);
        int cantDatos = 0;

        String sql = "SELECT * FROM cuadre_diario WHERE Fecha_Cuadre >= ? AND Fecha_Cuadre <= ? AND Fk_Id_Hotel = ?";

        try (java.sql.Connection conex = con.conexion();
                PreparedStatement ps = conex.prepareStatement(sql);) {
            ps.setString(1, fechaDesde);
            ps.setString(2, fechaHasta);
            ps.setInt(3, idHotel);
            try (ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    Object[] row = new Object[11];
                    row[0] = rs.getDate("Fecha_Cuadre");
                    row[1] = rs.getDouble("Total_Venta_Dia");
                    row[2] = rs.getDouble("Total_Comision_Dia");
                    row[3] = rs.getDouble("Total_Neto_Dia");
                    row[4] = rs.getDouble("Total_Gastos_Dia");
                    row[5] = rs.getDouble("Total_Ingresos_Extra_Dia");
                    row[6] = rs.getDouble("Recaudo_Final_Dia");
                    row[7] = rs.getInt("Habs_Vendidas_Dia");
                    row[8] = rs.getInt("Habs_Quedadas_Dia");
                    row[9] = rs.getDouble("Promedio_Hab_Dia");
                    row[10] = rs.getDouble("Promedio_perdida_Dia");

                    model.addRow(row);
                    cantDatos++;
                }

            }

            if (cantDatos == 0) {
                JOptionPane.showMessageDialog(this, "No existen datos");

            } else {
                if (cantDatos == 1) {
                    JOptionPane.showMessageDialog(this, "Se encontro " + cantDatos + "linea de datos.");
                } else {
                    JOptionPane.showMessageDialog(this, "Se encontraron " + cantDatos + " lineas de datos en total.");
                }

                double sumTotalVenta = 0.0;
                double sumTotalComision = 0.0;
                double sumTotalNeto = 0.0;
                double sumTotalGastos = 0.0;
                double sumTotalIngresosEx = 0.0;
                double sumRecaudoFinal = 0.0;
                double sumPromedioPerdida = 0.0;
                int habsV = 0;
                int habsQ = 0;

                for (int i = 0; i < model.getRowCount(); i++) {
                    sumTotalVenta += (double) model.getValueAt(i, 1);
                }
                jlbTotalVenta.setText(String.format("%.0f", sumTotalVenta));
                formatoPuntosMil(jlbTotalVenta);

                for (int i = 0; i < model.getRowCount(); i++) {
                    sumTotalComision += (double) model.getValueAt(i, 2);
                }
                jlbTotalComi.setText(String.format("%.0f", sumTotalComision));
                formatoPuntosMil(jlbTotalComi);

                for (int i = 0; i < model.getRowCount(); i++) {
                    sumTotalNeto += (double) model.getValueAt(i, 3);
                }
                jlbTotalNeto.setText(String.format("%.0f", sumTotalNeto));
                formatoPuntosMil(jlbTotalNeto);

                for (int i = 0; i < model.getRowCount(); i++) {
                    sumTotalGastos += (double) model.getValueAt(i, 4);
                }
                jlbTotalGastos.setText(String.format("%.0f", sumTotalGastos));
                formatoPuntosMil(jlbTotalGastos);

                for (int i = 0; i < model.getRowCount(); i++) {
                    sumTotalIngresosEx += (double) model.getValueAt(i, 5);
                }
                jlbIngresosExtra.setText(String.format("%.0f", sumTotalIngresosEx));
                formatoPuntosMil(jlbIngresosExtra);

                for (int i = 0; i < model.getRowCount(); i++) {
                    sumRecaudoFinal += (double) model.getValueAt(i, 6);
                }
                jlbRecaudoFinal.setText(String.format("%.0f", sumRecaudoFinal));
                formatoPuntosMil(jlbRecaudoFinal);

                for (int i = 0; i < model.getRowCount(); i++) {
                    sumPromedioPerdida += (double) model.getValueAt(i, 10);
                }
                jlbPromPerdida.setText(String.format("%.0f", sumPromedioPerdida));
                formatoPuntosMil(jlbPromPerdida);

                for (int i = 0; i < model.getRowCount(); i++) {
                    habsV += (int) model.getValueAt(i, 7);
                }
                jlbHabsVendidas.setText(String.format("%d", habsV));

                for (int i = 0; i < model.getRowCount(); i++) {
                    habsQ += (int) model.getValueAt(i, 8);
                }
                jlbHabsQuedadas.setText(String.format("%d", habsQ));
                jpnCalculos.setVisible(true);
                jpnPromedios.setVisible(true);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cerrar recursos CONEX: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblInforme = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jdchDesde = new com.toedter.calendar.JDateChooser();
        jlbDesde = new javax.swing.JLabel();
        jlbHasta = new javax.swing.JLabel();
        jdchHasta = new com.toedter.calendar.JDateChooser();
        btnGenerar = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        cbxSelecHotel = new javax.swing.JComboBox<>();
        jpnCalculos = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jlbTotalVenta = new javax.swing.JLabel();
        jlbTotalComi = new javax.swing.JLabel();
        jlbTotalNeto = new javax.swing.JLabel();
        jlbTotalGastos = new javax.swing.JLabel();
        jlbIngresosExtra = new javax.swing.JLabel();
        jlbRecaudoFinal = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jpnPromedios = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jlbHabsVendidas = new javax.swing.JLabel();
        jlbHabsQuedadas = new javax.swing.JLabel();
        jlbPromPerdida = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jpnHotelSlogan = new javax.swing.JPanel();
        jlbNomHotel = new javax.swing.JLabel();
        jlbSlogan = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jpnDatosHotel = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jlbDireccion = new javax.swing.JLabel();
        jlbCiudad = new javax.swing.JLabel();
        jlbDepartamento = new javax.swing.JLabel();
        jlbPais = new javax.swing.JLabel();
        jlbIdHotel = new javax.swing.JLabel();
        jlbTelefono = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jlbRntHotel = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        btnVolver = new javax.swing.JButton();
        jlbFondo = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("SOLICITUD DE INFORMES");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblInforme.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "FECHA", "TOTAL VENTA DIA", "TOTAL COMISION DIA", "TOTAL NETO DIA", "GASTOS", "INGRESOS EXTRA", "RECAUDO FINAL", "HABS VENDIDAS", "HABS QUEDADAS", "PROMEDIO HAB", "PROMEDIO PERDIDA"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblInforme);
        if (tblInforme.getColumnModel().getColumnCount() > 0) {
            tblInforme.getColumnModel().getColumn(0).setMinWidth(80);
            tblInforme.getColumnModel().getColumn(0).setMaxWidth(80);
            tblInforme.getColumnModel().getColumn(1).setMinWidth(110);
            tblInforme.getColumnModel().getColumn(1).setMaxWidth(110);
            tblInforme.getColumnModel().getColumn(2).setResizable(false);
            tblInforme.getColumnModel().getColumn(3).setMinWidth(110);
            tblInforme.getColumnModel().getColumn(3).setMaxWidth(110);
            tblInforme.getColumnModel().getColumn(4).setMinWidth(85);
            tblInforme.getColumnModel().getColumn(4).setMaxWidth(85);
            tblInforme.getColumnModel().getColumn(5).setMinWidth(120);
            tblInforme.getColumnModel().getColumn(5).setMaxWidth(120);
            tblInforme.getColumnModel().getColumn(6).setMinWidth(110);
            tblInforme.getColumnModel().getColumn(6).setMaxWidth(110);
            tblInforme.getColumnModel().getColumn(7).setMinWidth(110);
            tblInforme.getColumnModel().getColumn(7).setMaxWidth(110);
            tblInforme.getColumnModel().getColumn(8).setMinWidth(120);
            tblInforme.getColumnModel().getColumn(8).setMaxWidth(120);
            tblInforme.getColumnModel().getColumn(9).setMinWidth(110);
            tblInforme.getColumnModel().getColumn(9).setMaxWidth(110);
            tblInforme.getColumnModel().getColumn(10).setResizable(false);
        }

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 1250, 440));

        jPanel2.setBackground(new java.awt.Color(0, 102, 102));

        jdchDesde.setBackground(new java.awt.Color(255, 219, 95));
        jdchDesde.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 219, 95)));
        jdchDesde.setDateFormatString("EEE d MMM y");

        jlbDesde.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbDesde.setForeground(new java.awt.Color(255, 255, 255));
        jlbDesde.setText("DESDE :");

        jlbHasta.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbHasta.setForeground(new java.awt.Color(255, 255, 255));
        jlbHasta.setText("HASTA :");

        jdchHasta.setBackground(new java.awt.Color(255, 219, 95));
        jdchHasta.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 219, 95)));

        btnGenerar.setBackground(new java.awt.Color(255, 179, 39));
        btnGenerar.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        btnGenerar.setForeground(new java.awt.Color(0, 0, 0));
        btnGenerar.setText("GENERAR");
        btnGenerar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnGenerarMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnGenerarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnGenerarMouseExited(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("SELECCIONE UN HOTEL : ");

        cbxSelecHotel.setBackground(new java.awt.Color(255, 219, 95));
        cbxSelecHotel.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        cbxSelecHotel.setForeground(new java.awt.Color(0, 0, 0));
        cbxSelecHotel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cbxSelecHotel.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxSelecHotelItemStateChanged(evt);
            }
        });
        cbxSelecHotel.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cbxSelecHotelFocusGained(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxSelecHotel, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(208, 208, 208)
                .addComponent(jlbDesde)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jdchDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47)
                .addComponent(jlbHasta)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jdchHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addComponent(btnGenerar, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnGenerar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jdchHasta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlbHasta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jdchDesde, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jlbDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbxSelecHotel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1300, 70));

        jpnCalculos.setBackground(new java.awt.Color(0, 102, 102));

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Total venta        : ");

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Total comision : ");

        jLabel5.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Total neto          :");

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Total gastos     :");

        jLabel7.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Ingresos extra :");

        jLabel8.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Recaudo final   :");

        jlbTotalVenta.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jlbTotalVenta.setForeground(new java.awt.Color(255, 255, 255));

        jlbTotalComi.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jlbTotalComi.setForeground(new java.awt.Color(255, 255, 255));

        jlbTotalNeto.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jlbTotalNeto.setForeground(new java.awt.Color(255, 255, 255));

        jlbTotalGastos.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jlbTotalGastos.setForeground(new java.awt.Color(255, 255, 255));

        jlbIngresosExtra.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jlbIngresosExtra.setForeground(new java.awt.Color(255, 255, 255));

        jlbRecaudoFinal.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jlbRecaudoFinal.setForeground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 219, 95));
        jLabel1.setText("ESTADISTICAS CONTABLES");

        javax.swing.GroupLayout jpnCalculosLayout = new javax.swing.GroupLayout(jpnCalculos);
        jpnCalculos.setLayout(jpnCalculosLayout);
        jpnCalculosLayout.setHorizontalGroup(
            jpnCalculosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnCalculosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpnCalculosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpnCalculosLayout.createSequentialGroup()
                        .addGroup(jpnCalculosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jpnCalculosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpnCalculosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jlbIngresosExtra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jlbTotalGastos, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jlbTotalComi, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jlbTotalVenta, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jlbRecaudoFinal, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE))
                            .addComponent(jlbTotalNeto, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpnCalculosLayout.createSequentialGroup()
                .addGap(0, 2, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jpnCalculosLayout.setVerticalGroup(
            jpnCalculosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnCalculosLayout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnCalculosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlbTotalVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnCalculosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlbTotalComi, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnCalculosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlbTotalNeto, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnCalculosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlbTotalGastos, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnCalculosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlbIngresosExtra, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnCalculosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlbRecaudoFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jPanel1.add(jpnCalculos, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 530, 330, 250));

        jpnPromedios.setBackground(new java.awt.Color(0, 102, 102));

        jLabel15.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Total habitaciones vendidas   : ");

        jLabel16.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Total habitaciones quedadas  : ");

        jLabel17.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Promedio perdida                     : ");

        jlbHabsVendidas.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jlbHabsVendidas.setForeground(new java.awt.Color(255, 255, 255));

        jlbHabsQuedadas.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jlbHabsQuedadas.setForeground(new java.awt.Color(255, 255, 255));

        jlbPromPerdida.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jlbPromPerdida.setForeground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 219, 95));
        jLabel2.setText("OCUPACION");

        javax.swing.GroupLayout jpnPromediosLayout = new javax.swing.GroupLayout(jpnPromedios);
        jpnPromedios.setLayout(jpnPromediosLayout);
        jpnPromediosLayout.setHorizontalGroup(
            jpnPromediosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator2)
            .addGroup(jpnPromediosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpnPromediosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpnPromediosLayout.createSequentialGroup()
                        .addGroup(jpnPromediosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpnPromediosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlbHabsVendidas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jlbHabsQuedadas, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                            .addComponent(jlbPromPerdida, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)))
                    .addGroup(jpnPromediosLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jpnPromediosLayout.setVerticalGroup(
            jpnPromediosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnPromediosLayout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jpnPromediosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jpnPromediosLayout.createSequentialGroup()
                        .addGroup(jpnPromediosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jpnPromediosLayout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpnPromediosLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                                .addComponent(jlbHabsVendidas, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jlbHabsQuedadas, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpnPromediosLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnPromediosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlbPromPerdida, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jPanel1.add(jpnPromedios, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 530, 310, 150));

        jpnHotelSlogan.setBackground(new java.awt.Color(0, 102, 102));
        jpnHotelSlogan.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jlbNomHotel.setFont(new java.awt.Font("Dialog", 1, 26)); // NOI18N
        jlbNomHotel.setForeground(new java.awt.Color(255, 219, 95));
        jlbNomHotel.setText("HOTELMAS.COM");
        jpnHotelSlogan.add(jlbNomHotel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 10, 370, 43));

        jlbSlogan.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jlbSlogan.setForeground(new java.awt.Color(255, 255, 255));
        jlbSlogan.setText("Slogan");
        jpnHotelSlogan.add(jlbSlogan, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 40, 310, -1));
        jpnHotelSlogan.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 380, 10));

        jPanel1.add(jpnHotelSlogan, new org.netbeans.lib.awtextra.AbsoluteConstraints(888, 530, 380, 80));

        jpnDatosHotel.setBackground(new java.awt.Color(0, 102, 102));

        jLabel24.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 219, 95));
        jLabel24.setText("DIRECCION            :");

        jLabel25.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 219, 95));
        jLabel25.setText("TELEFONO             :");

        jLabel26.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(255, 219, 95));
        jLabel26.setText("CIUDAD                  :");

        jLabel27.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(255, 219, 95));
        jLabel27.setText("DEPARTAMENTO   :");

        jLabel29.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(255, 219, 95));
        jLabel29.setText("ID HOTEL                :");

        jlbDireccion.setForeground(new java.awt.Color(255, 219, 95));

        jlbCiudad.setForeground(new java.awt.Color(255, 219, 95));

        jlbDepartamento.setForeground(new java.awt.Color(255, 219, 95));

        jlbPais.setForeground(new java.awt.Color(255, 219, 95));

        jlbIdHotel.setForeground(new java.awt.Color(255, 219, 95));

        jlbTelefono.setForeground(new java.awt.Color(255, 219, 95));

        jLabel30.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(255, 219, 95));
        jLabel30.setText("RNT HOTEL            :");

        jlbRntHotel.setForeground(new java.awt.Color(255, 219, 95));

        jLabel28.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(255, 219, 95));
        jLabel28.setText("PAIS                       :");

        javax.swing.GroupLayout jpnDatosHotelLayout = new javax.swing.GroupLayout(jpnDatosHotel);
        jpnDatosHotel.setLayout(jpnDatosHotelLayout);
        jpnDatosHotelLayout.setHorizontalGroup(
            jpnDatosHotelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnDatosHotelLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jpnDatosHotelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jpnDatosHotelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jpnDatosHotelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(jLabel26)
                        .addComponent(jLabel28, javax.swing.GroupLayout.Alignment.TRAILING)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jpnDatosHotelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpnDatosHotelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jlbIdHotel, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jlbCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jlbDepartamento, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jlbPais, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jpnDatosHotelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jlbDireccion, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jlbTelefono, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)))
                    .addComponent(jlbRntHotel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17))
        );
        jpnDatosHotelLayout.setVerticalGroup(
            jpnDatosHotelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnDatosHotelLayout.createSequentialGroup()
                .addGroup(jpnDatosHotelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jlbDireccion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(6, 6, 6)
                .addGroup(jpnDatosHotelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel25)
                    .addComponent(jlbTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jpnDatosHotelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlbCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnDatosHotelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jpnDatosHotelLayout.createSequentialGroup()
                        .addGroup(jpnDatosHotelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel27)
                            .addComponent(jlbDepartamento, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addComponent(jlbPais, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel26))
                .addGap(6, 6, 6)
                .addGroup(jpnDatosHotelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel29)
                    .addComponent(jlbIdHotel, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jpnDatosHotelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlbRntHotel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jPanel1.add(jpnDatosHotel, new org.netbeans.lib.awtextra.AbsoluteConstraints(888, 610, 380, 150));

        btnVolver.setBackground(new java.awt.Color(255, 179, 39));
        btnVolver.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        btnVolver.setForeground(new java.awt.Color(0, 0, 0));
        btnVolver.setText("VOLVER");
        btnVolver.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnVolverMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnVolverMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnVolverMouseExited(evt);
            }
        });
        jPanel1.add(btnVolver, new org.netbeans.lib.awtextra.AbsoluteConstraints(1170, 770, 102, 30));

        jlbFondo.setForeground(new java.awt.Color(0, 0, 0));
        jPanel1.add(jlbFondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1300, 800));

        jPanel3.setBackground(new java.awt.Color(0, 102, 102));

        jLabel9.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("HotelesMas.com");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(929, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 830, 1300, 50));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGenerarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGenerarMouseClicked
        if (cbxSelecHotel.getSelectedItem().toString().equals("Seleccione")) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un hotel");
            cbxSelecHotel.requestFocus();
            return;
        }
        llenarTabla();
    }//GEN-LAST:event_btnGenerarMouseClicked

    private void cbxSelecHotelItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxSelecHotelItemStateChanged
        if (cbxSelecHotel != null && !cbxSelecHotel.getSelectedItem().toString().equals("Seleccione")) {
            String hotel = cbxSelecHotel.getSelectedItem().toString();
            java.sql.Connection conex = con.conexion();
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                String sqldatosH = "SELECT Id_Hoteles, Rnt_Hotel, Direccion_Hotel, Telefono_Hotel, Ciudad_Hotel, Departamento_Hotel,"
                        + " Pais_Hotel, Slogan_Hotel FROM hoteles WHERE Nombre_Hotel = ?";
                ps = conex.prepareStatement(sqldatosH);
                ps.setString(1, hotel);
                rs = ps.executeQuery();
                while (rs.next()) {
                    jlbIdHotel.setText(Integer.toString(rs.getInt("Id_Hoteles")));
                    jlbRntHotel.setText(rs.getString("Rnt_Hotel"));
                    jlbDireccion.setText(rs.getString("Direccion_Hotel"));
                    jlbTelefono.setText(rs.getString("Telefono_Hotel"));
                    jlbCiudad.setText(rs.getString("Ciudad_Hotel"));
                    jlbDepartamento.setText(rs.getString("Departamento_Hotel"));
                    jlbPais.setText(rs.getString("Pais_Hotel"));
                    jlbSlogan.setText(rs.getString("Slogan_Hotel"));
                }
                jlbNomHotel.setText(hotel);
                jpnHotelSlogan.setVisible(true);
                jpnDatosHotel.setVisible(true);
                jlbDesde.setVisible(true);
                jdchDesde.setVisible(true);
                jlbHasta.setVisible(true);
                jdchHasta.setVisible(true);
                btnGenerar.setVisible(true);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al obtener datos de hotel  : " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

        }
    }//GEN-LAST:event_cbxSelecHotelItemStateChanged

    private void cbxSelecHotelFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbxSelecHotelFocusGained
        jpnCalculos.setVisible(false);
        jpnPromedios.setVisible(false);
        jpnDatosHotel.setVisible(false);
        jpnHotelSlogan.setVisible(false);
    }//GEN-LAST:event_cbxSelecHotelFocusGained

    private void btnGenerarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGenerarMouseEntered
        btnGenerar.setBackground(new Color(255, 219, 95));
    }//GEN-LAST:event_btnGenerarMouseEntered

    private void btnGenerarMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGenerarMouseExited
        btnGenerar.setBackground(new Color(255, 179, 39));
    }//GEN-LAST:event_btnGenerarMouseExited

    private void btnVolverMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnVolverMouseEntered
        btnVolver.setBackground(new Color(255, 219, 95));
    }//GEN-LAST:event_btnVolverMouseEntered

    private void btnVolverMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnVolverMouseExited
        btnVolver.setBackground(new Color(255, 179, 39));
    }//GEN-LAST:event_btnVolverMouseExited

    private void btnVolverMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnVolverMouseClicked
        this.dispose();
    }//GEN-LAST:event_btnVolverMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGenerar;
    private javax.swing.JButton btnVolver;
    private javax.swing.JComboBox<String> cbxSelecHotel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private com.toedter.calendar.JDateChooser jdchDesde;
    private com.toedter.calendar.JDateChooser jdchHasta;
    private javax.swing.JLabel jlbCiudad;
    private javax.swing.JLabel jlbDepartamento;
    private javax.swing.JLabel jlbDesde;
    private javax.swing.JLabel jlbDireccion;
    private javax.swing.JLabel jlbFondo;
    private javax.swing.JLabel jlbHabsQuedadas;
    private javax.swing.JLabel jlbHabsVendidas;
    private javax.swing.JLabel jlbHasta;
    private javax.swing.JLabel jlbIdHotel;
    private javax.swing.JLabel jlbIngresosExtra;
    private javax.swing.JLabel jlbNomHotel;
    private javax.swing.JLabel jlbPais;
    private javax.swing.JLabel jlbPromPerdida;
    private javax.swing.JLabel jlbRecaudoFinal;
    private javax.swing.JLabel jlbRntHotel;
    private javax.swing.JLabel jlbSlogan;
    private javax.swing.JLabel jlbTelefono;
    private javax.swing.JLabel jlbTotalComi;
    private javax.swing.JLabel jlbTotalGastos;
    private javax.swing.JLabel jlbTotalNeto;
    private javax.swing.JLabel jlbTotalVenta;
    private javax.swing.JPanel jpnCalculos;
    private javax.swing.JPanel jpnDatosHotel;
    private javax.swing.JPanel jpnHotelSlogan;
    private javax.swing.JPanel jpnPromedios;
    private javax.swing.JTable tblInforme;
    // End of variables declaration//GEN-END:variables
}
