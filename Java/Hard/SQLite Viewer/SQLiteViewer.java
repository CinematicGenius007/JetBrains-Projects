package viewer;


import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteViewer extends JFrame {
    public static Connection c = null;
    static String file_name = "";

    public SQLiteViewer() {
        super("SQLite Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 700);

        initComponents();

        setLayout(null);
        setResizable(false);
        setVisible(true);
        setLocationRelativeTo(null);

    }

    private List<String> buildConnection2(String url) {
        List<String> list = new ArrayList<>();

        try {
            c = DriverManager.getConnection(url);

//            System.out.println("Connection to SQLite has been established.");

            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery("SELECT name FROM sqlite_master WHERE type ='table' AND name NOT LIKE 'sqlite_%';");

//            ResultSet rs = st.executeQuery("SELECT ID, NAME FROM data;");

            while (rs.next()) {
//                System.out.println(rs.getString("name"));
                list.add(rs.getString("name"));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return list;
    }

    private void initComponents() {
        Font font1 = new Font("Courier", Font.BOLD,12);

        JTextField fileNameTextField = new JTextField();
        fileNameTextField.setName("FileNameTextField");
        fileNameTextField.setFont(font1);
        fileNameTextField.setBounds(20, 15, 550, 40);
        add(fileNameTextField);

        JButton openFileButton = new JButton("Open");
        openFileButton.setName("OpenFileButton");
        openFileButton.setFont(new Font("Sans", Font.BOLD, 16));
        openFileButton.setBackground(Color.WHITE);
        openFileButton.setBounds(580, 15, 100, 38);
        add(openFileButton);

        JComboBox<String> tablesComboBox = new JComboBox<>(new String[] {"contacts"});

        tablesComboBox.setName("TablesComboBox");
        tablesComboBox.setBounds(20, 70, 660, 36);
        add(tablesComboBox);

        JTextArea queryTextArea = new JTextArea();
        queryTextArea.setEnabled(false);
        queryTextArea.setName("QueryTextArea");
        queryTextArea.setBounds(20, 120, 530, 140);
        add(queryTextArea);

        JButton executeQueryButton = new JButton("Execute");
        executeQueryButton.setEnabled(false);
        executeQueryButton.setName("ExecuteQueryButton");
        executeQueryButton.setFont(new Font("Sans", Font.BOLD, 16));
        executeQueryButton.setBackground(Color.WHITE);
        executeQueryButton.setBounds(560, 120, 120, 38);
        add(executeQueryButton);

        DefaultTableModel tableModel = new DefaultTableModel();
        JTable table = new JTable(tableModel);
        table.setName("Table");

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(20, 280, 660, 350);
        add(table);

        executeQueryButton.addActionListener(actionEvent -> {
            DefaultTableModel dm = (DefaultTableModel) table.getModel();
            dm.getDataVector().removeAllElements();
            dm.fireTableDataChanged();
            try {
                if (c == null || c.isClosed()) {
                    c = DriverManager.getConnection("jdbc:sqlite:" + file_name);
                }
                Statement ps2 = c.createStatement();
                ResultSet rs2 = ps2.executeQuery(queryTextArea.getText());
                ResultSetMetaData rsmd = rs2.getMetaData();
                int noOfCol = rsmd.getColumnCount();
                for (int i = 1; i <= noOfCol; i++) {
                    tableModel.addColumn(rsmd.getColumnName(i));
                }
                while (rs2.next()) {
                    Object[] entry = new Object[noOfCol];
                    for (int i = 1; i <= noOfCol; i++) {
                        entry[i-1] = rs2.getString(i);
                    }
                    tableModel.addRow(entry);
                }
                //executeQueryButton.setEnabled(false);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(new Frame(), "ERROR MESSAGE");
            }
        });

        openFileButton.addActionListener(actionEvent -> {
            String fileName = fileNameTextField.getText();
            file_name = fileName;
            fileNameTextField.setText("");
            //System.out.println(fileName);
            try {
                c = DriverManager.getConnection("jdbc:sqlite:" + fileName);
                queryTextArea.setEnabled(true);
                executeQueryButton.setEnabled(true);
                Statement ps1 = c.createStatement();
                ResultSet rs1 = ps1.executeQuery("SELECT name FROM sqlite_master WHERE type ='table' AND name NOT LIKE 'sqlite_%';");
                if (!rs1.next()) {
                    throw new Exception();
                }
                tablesComboBox.removeAllItems();
                tablesComboBox.addItem(rs1.getString(1));
                while (rs1.next()) {
                    tablesComboBox.addItem(rs1.getString(1));
                }
                queryTextArea.setText("SELECT * FROM " + tablesComboBox.getItemAt(0) + ";");

                //System.out.println("Connection to SQLite has been established.");

            } catch (Exception e) {
                file_name = "Nanotube";
                executeQueryButton.setEnabled(false);
                queryTextArea.setEnabled(false);
                JOptionPane.showMessageDialog(new Frame(), "Wrong file name!");
            } finally {
                try {
                    if (c != null) {
                        c.close();
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(new Frame(), "Wrong file name!");
                }
            }
        });

        executeQueryButton.addActionListener(actionEvent -> {
            String name = (String) tablesComboBox.getSelectedItem();
            queryTextArea.setText("SELECT * FROM " + name + ";");
        });

        tablesComboBox.addActionListener(actionEvent -> {
            //System.out.println("it's called");
            String name = (String) tablesComboBox.getSelectedItem();
            queryTextArea.setText("Select * from " + name + ";");
        });
    }

}
