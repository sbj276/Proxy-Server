/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.intelsoft.panel;

import com.intelsoft.helper.DBHelper;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Jai Bhavani
 */
public class AuditInfoPanel  extends JPanel{
    Component thisComponent = null;
    JPanel topPanel = null;

    JTextField searchBox = null;
    JButton searchButton = null;
    JButton refresh = null;
    JButton cancel = null;
    String[] columns = new String[]{"Sr.No.", "Person Id","Name","Session Id","Remote Address","Remote Port","Http Method","Remote Host","Scheme","Request URL","Query String","Is Allowed","TimeOfRequest","Actual URL"};
    AbstractTableModel model = null;
    JTable table = null;
    List<Object[]> data = null;
    TableRowSorter<AbstractTableModel> sorter = null;

    public AuditInfoPanel() {
        setLayout(new BorderLayout(10,10));
        initiComponents();
        thisComponent = this;
    }

    private void initiComponents() {
        JPanel topNorthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label1 = new JLabel("Search Text:");
        searchBox = new JTextField();
        searchBox.setColumns(30);
        searchButton = new JButton("Search");
        topNorthPanel.add(label1);
        topNorthPanel.add(searchBox);
        topNorthPanel.add(searchButton);

        searchButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (searchBox.getText() != null) {
                    RowFilter<TableModel, Integer> rf = null;
                    try {
                        rf = RowFilter.regexFilter(searchBox.getText());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    sorter.setRowFilter(rf);
                }
            }
        });

        initializeAndLoadTableData();
        model = new AbstractTableModel() {

            public String getColumnName(int column) {
                return columns[column].toString();
            }

            public int getRowCount() {
                return data.size();
            }

            public int getColumnCount() {
                return columns.length;
            }

            public Object getValueAt(int row, int col) {
                if (col == 0) {
                    return row + 1;
                } else {
                    return data.get(row)[col - 1];
                }
            }

            public boolean isCellEditable(int row, int column) {
                return true;
            }

            public void setValueAt(Object value, int row, int col) {
                if (col != 0) {
                    data.get(row)[col - 1] = value;
                    fireTableCellUpdated(row, col);
                }
            }

            public void setData(List<Object[]> dataVal) {
                data = dataVal;
            }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setShowGrid(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        TableColumn column = null;
        for (int i = 0; i < columns.length; i++) {
            column = table.getColumnModel().getColumn(i);
            if (i == 1 || i == 2 || i == 3 || i == 7 || i == 10 || i == 11 || i == 12 || i == 13) {
                column.setMinWidth(150); //third column is bigger
                column.setPreferredWidth(150); //third column is bigger
            } else {
                column.setMinWidth(80);
                column.setPreferredWidth(80);
            }
        }

        sorter = new TableRowSorter<AbstractTableModel>(model);
        table.setRowSorter(sorter);
        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JPanel topSouthPanel = new JPanel(new FlowLayout());
        refresh = new JButton("Refresh");
        refresh.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                refreshTableData();
            }
        });
        topSouthPanel.add(refresh);

        this.add(topNorthPanel, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(topSouthPanel, BorderLayout.SOUTH);


    }



    private void initializeAndLoadTableData() {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        try {

            con = DBHelper.getInstance().getConnection();

            st = con.createStatement();

                String sql = "Select * from UserActivityLog ual Left Outer join Person p on (ual.PersonId = p.personId) order by ual.TimeOfRequest desc";

            rs = st.executeQuery(sql);
            data = new ArrayList<Object[]>();
            while (rs.next()) {

                Object[] obj = new Object[]{
                    rs.getString("PersonId") == null ? "" :rs.getString("PersonId"),
                    rs.getString("Name") == null ? "" :rs.getString("Name"),
                    rs.getString("SessionId") == null ? "" :rs.getString("SessionId"),
                    rs.getString("RemoteAddress") == null ? "" :rs.getString("RemoteAddress"),
                    rs.getString("RemotePort") == null ? "" :rs.getString("RemotePort"),
                    rs.getString("HttpMethod") == null ? "" :rs.getString("HttpMethod"),
                    rs.getString("RemoteHost") == null ? "" :rs.getString("RemoteHost"),
                    rs.getString("Scheme") == null ? "" :rs.getString("Scheme"),
                    rs.getString("RequestURL") == null ? "" :rs.getString("RequestURL"),
                    rs.getString("QueryString") == null ? "" :rs.getString("QueryString"),
                    rs.getString("IsAllowed") == null ? "" :rs.getString("IsAllowed"),
                    new Date(rs.getTimestamp("TimeOfRequest").getTime()),
                    rs.getString("ActualURL")
                    };
                data.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBHelper.closeResultSet(rs);
            DBHelper.closeStatement(st);
            DBHelper.closeConnection(con);
        }
    }

    public void refreshTableData() {
        initializeAndLoadTableData();
        ((AbstractTableModel) table.getModel()).fireTableDataChanged();
        table.invalidate();
        table.repaint();
    }
}
