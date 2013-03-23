package tw.edu.ym.guid.querier.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.edu.ym.guid.querier.ExcelManager;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class QueryPanel {
  static final Logger logger = LoggerFactory.getLogger(QueryPanel.class);

  private JFrame frame;
  private JTextField textField;
  private JScrollPane scrollPane;
  private JTable table;
  private JMenuBar menuBar;
  private final ExcelManager em;
  private DefaultTableModel dataModel;

  public QueryPanel() throws SQLException, ClassNotFoundException {
    em = new ExcelManager();
    String password = null;
    int retry = 0;
    do {
      if (retry >= 3)
        System.exit(0);
      password = getPassword("Enter Password:");
      if (password == null)
        password = "";
      retry++;
    } while (!(em.authenticate("admin", password)));
    initialize();
  }

  private String getPassword(String msg) {
    JPanel panel = new JPanel();
    JLabel label = new JLabel(msg);
    JPasswordField pass = new JPasswordField(32);
    panel.add(label);
    panel.add(pass);
    String[] options = new String[] { "OK", "Cancel" };
    int option =
        JOptionPane.showOptionDialog(null, panel, "Authentication",
            JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options,
            options[1]);
    if (option == 0)
      return new String(pass.getPassword());
    else
      return null;
  }

  private DefaultTableModel initDataModel() {
    dataModel = new DefaultTableModel();
    dataModel.setColumnIdentifiers(em.getHeader());
    for (Object[] record : em.selectAll(100))
      dataModel.addRow(record);
    return dataModel;
  }

  private void querying() {
    String query = textField.getText();
    if (!(query.trim().isEmpty())) {
      List<String[]> result = em.query2ListOfStrAry(query.trim().split("\\s+"));
      dataModel = new DefaultTableModel();
      dataModel.setColumnIdentifiers(em.getHeader());
      for (String[] record : result)
        dataModel.addRow(record);
      table.setModel(dataModel);
    }
  }

  private void initialize() {
    frame = new JFrame();
    frame.setBounds(100, 100, 640, 480);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(
        new FormLayout(new ColumnSpec[] { ColumnSpec.decode("640px:grow"),
            ColumnSpec.decode("1px"), }, new RowSpec[] {
            RowSpec.decode("30px"),
            RowSpec.decode("fill:max(264dlu;default):grow"), }));

    textField = new JTextField();
    textField.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        querying();
      }
    });
    frame.getContentPane().add(textField, "1, 1, center, top");
    textField.setColumns(1024);

    scrollPane = new JScrollPane();
    frame.getContentPane().add(scrollPane, "1, 2, fill, fill");

    table = new JTable();
    table.setBackground(new Color(230, 230, 250));

    scrollPane.setViewportView(table);
    table.setModel(initDataModel());

    menuBar = new JMenuBar();
    JMenu menu = new JMenu("Import Excels");
    JMenuItem item = new JMenuItem("Select a folder...");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.showOpenDialog(frame);
        if (chooser.getSelectedFile() != null) {
          em.importExcelsInFolder(chooser.getSelectedFile().getAbsolutePath());
          table.setModel(initDataModel());
        }
      }
    });
    menu.add(item);
    menuBar.add(menu);

    JMenu auth = new JMenu("Authentication");
    JMenuItem password = new JMenuItem("Set password");
    password.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        int retry = 0;
        String oldPassword = null;
        do {
          oldPassword = getPassword("Enter old password:");
          if (oldPassword == null)
            oldPassword = "";
          retry++;
        } while (!(em.authenticate("admin", oldPassword)) && retry < 3);

        String newPassword = null;
        do {
          newPassword = getPassword("Enter new password (least 4 charaters):");
          if (newPassword == null)
            newPassword = "";
        } while (newPassword.length() < 4);

        String verifyPassword = null;
        do {
          verifyPassword = getPassword("Enter new password again:");
          if (verifyPassword == null)
            verifyPassword = "";
        } while (verifyPassword.equals(newPassword));

        em.setAdminPassword(newPassword);
      }
    });
    auth.add(password);
    menuBar.add(auth);

    frame.setJMenuBar(menuBar);
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          QueryPanel window = new QueryPanel();
          window.frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
          logger.error(e.getMessage());
        }
      }
    });
  }

}
