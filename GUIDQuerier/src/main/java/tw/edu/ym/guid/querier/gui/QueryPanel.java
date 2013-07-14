package tw.edu.ym.guid.querier.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.Box;
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
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.edu.ym.guid.querier.ExcelManager;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import exceldb.model.Pii;

import static tw.edu.ym.guid.querier.ExcelManager.newExcelManager;
import static wmw.util.bean.BeanConverter.toObjectArray;

public final class QueryPanel {

  private static final Logger log = LoggerFactory.getLogger(QueryPanel.class);

  public static final String PROPS_PATH = "excel_manager.properties";
  private static final long SHUTDOWN_TIME = 300000000000L; // 5 minutes
  private long idleTime = System.nanoTime();
  private JFrame frame;
  private JTextField textField;
  private JScrollPane scrollPane;
  private JTable table;
  private JMenuBar menuBar;
  private final ExcelManager em;
  private DefaultTableModel dataModel;
  // private JPanel statusPanel;
  private JMenu totalRecords;

  public QueryPanel() throws SQLException, ClassNotFoundException,
      FileNotFoundException, IOException {
    autoShutdown();
    em = newExcelManager(PROPS_PATH);
    String password1 = null;
    String password2 = null;
    int retry = 0;
    do {
      if (retry >= 3)
        System.exit(0);
      password1 = getPassword("Enter 1st Password:", true).getValue();
      retry++;
    } while (!(em.authenticate("admin1", password1))
        && !(em.authenticate("admin2", password1)));

    retry = 0;
    do {
      if (retry >= 3)
        System.exit(0);
      password2 = getPassword("Enter 2nd Password:", true).getValue();
      retry++;
    } while (em.authenticate("admin1", password1) ? !(em.authenticate("admin2",
        password2)) : !(em.authenticate("admin1", password2)));
    initialize();
  }

  private void setTotalRecords() {
    totalRecords.setText(" Total Records: " + em.total());
  }

  private void autoShutdown() {
    Timer timer = new Timer(2000, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (System.nanoTime() - idleTime > SHUTDOWN_TIME)
          System.exit(0);
      }
    });
    timer.start();
  }

  private void resetIdleTime() {
    idleTime = System.nanoTime();
  }

  private Entry<Integer, String> getPassword(String msg) {
    return getPassword(msg, false);
  }

  private Entry<Integer, String> getPassword(String msg, boolean enableExit) {
    resetIdleTime();

    JPanel panel = new JPanel();
    JLabel label = new JLabel(msg);
    JPasswordField pass = new JPasswordField(16);
    panel.add(label);
    panel.add(pass);
    String[] options = new String[] { "OK", "Cancel" };
    int option =
        JOptionPane.showOptionDialog(null, panel, "Authentication",
            JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options,
            options[0]);
    if (enableExit && (option == -1 || option == 1))
      System.exit(0);
    return new SimpleEntry<Integer, String>(option, new String(
        pass.getPassword()).trim());
  }

  private DefaultTableModel initDataModel() {
    dataModel = new DefaultTableModel();
    dataModel.setColumnIdentifiers(em.getHeader());
    for (Object[] record : em.selectAll(500))
      dataModel.addRow(record);
    setTotalRecords();
    return dataModel;
  }

  private void querying() {
    resetIdleTime();

    String query = textField.getText().trim();
    if (query.isEmpty()) {
      dataModel = initDataModel();
      table.setModel(dataModel);
    } else {
      String[] keywords = query.trim().split("\\s+");
      List<Pii> piis = em.query(keywords);
      dataModel = new DefaultTableModel();
      dataModel.setColumnIdentifiers(em.getHeader());
      for (Pii pii : piis)
        dataModel.addRow(toObjectArray(pii));
      table.setModel(dataModel);
    }
  }

  private void importExcels() {
    resetIdleTime();

    JFileChooser chooser = new JFileChooser();
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    chooser.showOpenDialog(frame);
    if (chooser.getSelectedFile() != null) {
      em.importExcelsInFolder(chooser.getSelectedFile().getAbsolutePath());
      table.setModel(initDataModel());
    }
  }

  private void setPassword() {
    int option = -1;

    String role = null;
    String oldPassword = null;
    do {
      Entry<Integer, String> pwd = getPassword("Enter old password:");
      option = pwd.getKey();
      oldPassword = pwd.getValue();
    } while ((!em.authenticate("admin1", oldPassword) && !em.authenticate(
        "admin2", oldPassword)) && option == 0);

    if (option != 0)
      return;

    if (em.authenticate("admin1", oldPassword))
      role = "admin1";
    else
      role = "admin2";

    String newPassword = null;
    do {
      Entry<Integer, String> pwd =
          getPassword("Enter new password (least 4 charaters):");
      option = pwd.getKey();
      newPassword = pwd.getValue();
    } while (newPassword.length() < 4 && option == 0);

    if (option != 0)
      return;

    String verifyPassword = null;
    do {
      Entry<Integer, String> pwd = getPassword("Enter new password again:");
      option = pwd.getKey();
      verifyPassword = pwd.getValue();
    } while (!(verifyPassword.equals(newPassword)) && option == 0);

    if (option != 0)
      return;

    em.setAdminPassword(role, newPassword);
  }

  private void initialize() {
    frame = new JFrame();
    frame.setTitle("GUID Querier");
    frame.setBounds(100, 100, 640, 480);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(
        new FormLayout(new ColumnSpec[] { ColumnSpec.decode("640px:grow"),
            ColumnSpec.decode("1px"), }, new RowSpec[] {
            RowSpec.decode("30px"),
            RowSpec.decode("fill:max(240dlu;default):grow"), }));

    menuBar = new JMenuBar();
    JMenu menu = new JMenu("Import Excels");
    JMenuItem item = new JMenuItem("Select a folder...");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        importExcels();
      }
    });
    menu.add(item);
    menuBar.add(menu);

    JMenu auth = new JMenu("Authentication");
    JMenuItem password = new JMenuItem("Change password");
    password.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        setPassword();
      }
    });
    auth.add(password);
    menuBar.add(auth);

    menuBar.add(Box.createHorizontalGlue());
    totalRecords = new JMenu("Total Records: 0");
    menuBar.add(totalRecords);

    /*
     * statusPanel = new JPanel(); frame.getContentPane().add(statusPanel,
     * "1, 3, fill, bottom"); statusPanel.setPreferredSize(new
     * Dimension(frame.getWidth(), 16)); statusPanel.setLayout(new
     * BoxLayout(statusPanel, BoxLayout.X_AXIS)); statusLabel = new
     * JLabel(" Total Records: 0");
     * statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
     * statusPanel.add(statusLabel);
     */

    textField = new JTextField();
    textField.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void changedUpdate(DocumentEvent arg0) {
        querying();
      }

      @Override
      public void insertUpdate(DocumentEvent arg0) {
        querying();
      }

      @Override
      public void removeUpdate(DocumentEvent arg0) {
        querying();
      }
    });

    frame.getContentPane().add(textField, "1, 1, fill, top");

    scrollPane = new JScrollPane();
    frame.getContentPane().add(scrollPane, "1, 2, fill, fill");

    table = new JTable();
    table.setBackground(new Color(230, 230, 250));

    scrollPane.setViewportView(table);
    table.setModel(initDataModel());

    frame.setJMenuBar(menuBar);
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          QueryPanel window = new QueryPanel();
          window.frame.setVisible(true);
        } catch (Exception e) {
          log.error(e.getMessage());
        }
      }
    });
  }

}
