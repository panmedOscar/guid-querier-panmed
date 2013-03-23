package tw.edu.ym.guid.querier.gui;

import java.awt.EventQueue;
import java.util.List;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import tw.edu.ym.guid.querier.ExcelManager;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class QueryPanel {
  private JFrame frame;
  private JTextField textField;
  private JScrollPane scrollPane;
  private JTable table;
  private final ExcelManager em;
  private final Object[] tableHeader;
  private DefaultTableModel dataModel;

  public QueryPanel() throws SQLException, ClassNotFoundException {
    em = new ExcelManager();
    dataModel = new DefaultTableModel();
    tableHeader = em.getEmbeddedStorage().getColumns("pii").toArray();
    dataModel.setColumnIdentifiers(tableHeader);
    initialize();
  }

  private void querying() {
    String query = textField.getText();
    if (!(query.trim().isEmpty())) {
      List<String[]> result = em.query2ListOfStrAry(query.trim().split("\\s+"));
      dataModel = new DefaultTableModel();
      dataModel.setColumnIdentifiers(tableHeader);
      for (String[] record : result)
        dataModel.addRow(record);
      table.setModel(dataModel);
    }
  }

  private void initialize() throws SQLException, ClassNotFoundException {
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
    scrollPane.setViewportView(table);
    table.setModel(dataModel);
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          QueryPanel window = new QueryPanel();
          window.frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

}
