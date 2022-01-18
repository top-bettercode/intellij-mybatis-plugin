package com.wuzhizhan.mybatis.ui;

import com.intellij.database.model.DasColumn;
import com.intellij.database.model.DasNamespace;
import com.intellij.database.model.DasObject;
import com.intellij.database.model.ObjectKind;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.util.DasUtil;
import com.intellij.database.util.DbUtil;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.ui.SimpleListCellRenderer;
import com.intellij.util.containers.JBIterable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.AbstractTableModel;

public class PropertyGenerateUi extends JDialog {

  private JPanel contentPane;
  private JButton buttonOK;
  private JButton buttonCancel;
  private JComboBox dbComboBox;
  private JComboBox tableComboBox;
  private JLabel dbName;
  private JLabel tableName;
  private JScrollPane fieldScorePane;
  private Project project;

  public PropertyGenerateUi(Project project) {

    this.project = project;

    setContentPane(contentPane);
    setModal(true);
    getRootPane().setDefaultButton(buttonOK);

    refreshDbNameList();
    refreshTableNameList();
    refreshTableField();

    buttonOK.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onOK();
      }
    });

    buttonCancel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onCancel();
      }
    });

    // call onCancel() when cross is clicked
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        onCancel();
      }
    });

    // call onCancel() on ESCAPE
    contentPane.registerKeyboardAction(new ActionListener() {
                                         @Override
                                         public void actionPerformed(ActionEvent e) {
                                           onCancel();
                                         }
                                       }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
        JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
  }


  //字段名刷新
  private void refreshTableField() {
    DasObject dbTable = (DasObject) tableComboBox.getSelectedItem();
    if (dbTable == null) {
      return;
    }
    JBIterable<? extends DasColumn> fields = DasUtil.getColumns(dbTable);

    AbstractTableModel model = new AbstractTableModel() {

      @Override
      public int getRowCount() {
        return fields.size();
      }

      @Override
      public int getColumnCount() {
        return 2;
      }

      @Override
      public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {

          return fields.get(rowIndex).getName();
        } else {
          return fields.get(rowIndex).getDataType();
        }
      }

      @Override
      public String getColumnName(int column) {
        switch (column) {
          case 0:
            return "字段";
          case 1:
            return "类型";
          default:
            return "未知";
        }
      }
    };
    JTable table = new JTable();
    table.setModel(model);
    fieldScorePane.setViewportView(table);

  }

  //数据库刷新
  private void refreshDbNameList() {

    JBIterable<DbDataSource> dataSources = DbUtil.getDataSources(project);
    DbDataSource[] dbDataSources = dataSources.toList().toArray(new DbDataSource[0]);
    dbComboBox.setModel(new DefaultComboBoxModel(dbDataSources));

    dbComboBox.setRenderer(new SimpleListCellRenderer() {
      @Override
      public void customize(JList list, Object value, int index, boolean selected,
          boolean hasFocus) {
        setText(((DbDataSource) value).getName());
      }
    });

    dbComboBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (ItemEvent.SELECTED == e.getStateChange()) {
          refreshTableNameList();
        }
      }
    });

  }

  //表刷新
  private void refreshTableNameList() {
    DbDataSource selectedDb = (DbDataSource) dbComboBox.getSelectedItem();
    JBIterable<? extends DasNamespace> schemas = DasUtil.getSchemas(selectedDb);
    if (schemas.isEmpty()) {
      return;
    }
    JBIterable<? extends DasObject> dbTables = schemas.get(0).getDasChildren(ObjectKind.TABLE);

    tableComboBox.setModel(new DefaultComboBoxModel(dbTables.toList().toArray()));
    tableComboBox.setRenderer(new ListCellRendererWrapper() {
      @Override
      public void customize(JList list, Object value, int index, boolean selected,
          boolean hasFocus) {
        DasObject dasObject = (DasObject) value;
          if (dasObject != null) {
              setText(dasObject.getName());
          }
      }
    });
    tableComboBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (ItemEvent.SELECTED == e.getStateChange()) {
          refreshTableField();
        }
      }
    });
    refreshTableField();
  }


  private void onOK() {
    // add your code here
    dispose();
  }

  private void onCancel() {
    // add your code here if necessary
    dispose();
  }

}
