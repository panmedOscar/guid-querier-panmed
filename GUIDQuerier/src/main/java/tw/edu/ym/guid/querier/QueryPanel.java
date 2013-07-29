package tw.edu.ym.guid.querier;

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
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wmw.aop.terminator.CountdownTerminatorModule;
import wmw.aop.terminator.ResetTerminator;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import exceldb.model.Pii;

import static tw.edu.ym.guid.querier.ExcelManager.newExcelManager;
import static tw.edu.ym.guid.querier.api.Authentications.RoleType.ADMIN;
import static wmw.util.BeanConverter.toObjectArray;

/**
 * 
 * QueryPanel is the GUI of ExcelManager.
 * 
 * @author Wei-Ming Wu
 * 
 */
public final class QueryPanel {

  private static final Logger log = LoggerFactory.getLogger(QueryPanel.class);

  public static final String PROPS_PATH = "excel_manager.properties";
  private JFrame frame;
  private JTextField textField;
  private JScrollPane scrollPane;
  private JTable table;
  private JMenuBar menuBar;
  private final ExcelManager manager;
  private DefaultTableModel dataModel;
  // private JPanel statusPanel;
  private JMenu totalRecords;

  public QueryPanel() throws SQLException, ClassNotFoundException,
      FileNotFoundException, IOException {
    manager = newExcelManager(PROPS_PATH);
    setStyle();
    authenticate();
    initialize();
    autoBackup();
  }

  private void setStyle() {
    try {
      for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (Exception e) {
      try {
        UIManager.setLookAndFeel(UIManager
            .getCrossPlatformLookAndFeelClassName());
      } catch (Exception ex) {
        log.error(ex.getMessage());
      }
    }
  }

  private void authenticate() {
    String password1 = null;
    String password2 = null;
    int retry = 0;
    do {
      if (retry >= 3)
        System.exit(0);
      password1 = getPassword("Enter 1st Password:", true).getValue();
      password2 = getPassword("Enter 2nd Password:", true).getValue();
      retry++;
    } while (!manager.authenticate(ADMIN, password1)
        || !manager.authenticate(ADMIN, password2));
  }

  private void setTotalRecords() {
    totalRecords.setText(" Total Records: " + manager.total());
  }

  private void autoBackup() {
    Timer timer = new Timer(600000, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        manager.backup();
      }
    });
    timer.start();
  }

  private Entry<Integer, String> getPassword(String msg) {
    return getPassword(msg, false);
  }

  @ResetTerminator
  private Entry<Integer, String> getPassword(String msg, boolean enableExit) {
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
    dataModel.setColumnIdentifiers(manager.getHeader());
    for (Object[] record : manager.selectAll(500))
      dataModel.addRow(record);
    setTotalRecords();
    return dataModel;
  }

  @ResetTerminator
  private void querying() {
    String query = textField.getText().trim();
    if (query.isEmpty()) {
      dataModel = initDataModel();
      table.setModel(dataModel);
    } else {
      String[] keywords = query.trim().split("\\s+");
      List<Pii> piis = manager.query(keywords);
      dataModel = new DefaultTableModel();
      dataModel.setColumnIdentifiers(manager.getHeader());
      for (Pii pii : piis)
        dataModel.addRow(toObjectArray(pii));
      table.setModel(dataModel);
    }
  }

  @ResetTerminator
  private void importExcels() {
    JFileChooser chooser = new JFileChooser();
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    chooser.showOpenDialog(frame);
    if (chooser.getSelectedFile() != null) {
      manager.importExcelsInFolder(chooser.getSelectedFile().getAbsolutePath());
      table.setModel(initDataModel());
    }
  }

  private void setPassword() {
    int option = -1;

    String oldPassword = null;
    do {
      Entry<Integer, String> pwd = getPassword("Enter old password:");
      option = pwd.getKey();
      oldPassword = pwd.getValue();
    } while (!manager.authenticate(ADMIN, oldPassword)
        && !manager.authenticate(ADMIN, oldPassword) && option == 0);

    if (option != 0)
      return;

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
    } while (!verifyPassword.equals(newPassword) && option == 0);

    if (option != 0)
      return;

    manager.setAdminPassword(ADMIN, oldPassword, newPassword);
  }

  @ResetTerminator
  private void backup() {
    JFileChooser chooser = new JFileChooser();
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    chooser.showOpenDialog(frame);
    if (chooser.getSelectedFile() != null)
      manager.setBackup(chooser.getSelectedFile().getAbsolutePath());
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

    JMenu backup = new JMenu("Backup");
    JMenuItem backupPath = new JMenuItem("Select a backup folder...");
    backupPath.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        backup();
      }
    });
    backup.add(backupPath);
    menuBar.add(backup);

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
          Injector injector =
              Guice
                  .createInjector(new CountdownTerminatorModule(300000000000L));
          QueryPanel window = injector.getInstance(QueryPanel.class);
          // QueryPanel window = new QueryPanel();
          window.frame.setVisible(true);
        } catch (Exception e) {
          log.error(e.getMessage());
        }
      }
    });
  }

}
