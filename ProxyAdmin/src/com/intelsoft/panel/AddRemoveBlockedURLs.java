/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.intelsoft.panel;

import com.intelsoft.helper.DBHelper;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Jai Bhavani
 */
public class AddRemoveBlockedURLs  extends JSplitPane {
Component thisComponent = null;
    JPanel topPanel = null;
    JPanel bottomPanel = null;
    JTextField searchBox = null;
    JButton searchButton = null;
    JButton add = null;
    JButton remove = null;
    JButton edit = null;
    JButton save = null;
    JButton cancel = null;
    String[] columns = new String[]{"Sr.No.", "Person Id", "Name", "Blocked URL"};
    AbstractTableModel model = null;
    JTable table = null;
    List<Object[]> data = null;
    JLabel bottomTitle = new JLabel("");
    JLabel personIdLabel = null;
    JComboBox personIdComboBox = null;
    JLabel nameLabel = null;
    JTextField nameTxtField = null;
    JLabel blockedURLLabel = null;
    JTextField blockedURLTxtField = null;
    boolean isUpdateFlow = false;
    Map<String,String> personIdNameMap=new LinkedHashMap<String, String>();
    Object[] rowData = null;

    TableRowSorter<AbstractTableModel> sorter = null;

    public AddRemoveBlockedURLs() {
        initiComponents();
        setOrientation(JSplitPane.VERTICAL_SPLIT);
        setLeftComponent(topPanel);
        setRightComponent(bottomPanel);
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
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                enableDisableTopButtons();
            }
        });
        sorter = new TableRowSorter<AbstractTableModel>(model);
        table.setRowSorter(sorter);
        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JPanel topSouthPanel = new JPanel(new FlowLayout());
        add = new JButton("Add");
        add.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                isUpdateFlow=false;
                loadPersonIds();
                personIdComboBox.setModel(new DefaultComboBoxModel(personIdNameMap.keySet().toArray()));
                personIdComboBox.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        nameTxtField.setText(personIdNameMap.get(personIdComboBox.getSelectedItem()));
                    }
                });
                personIdComboBox.revalidate();
                personIdComboBox.setSelectedIndex(0);
                resetFields();
                disableTopButtonsAndEnableBottomButtons(true);
                bottomPanel.setEnabled(true);
            }
        });

        edit = new JButton("Edit");
        edit.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                resetFields();
                disableTopButtonsAndEnableBottomButtons(false);
                bottomPanel.setEnabled(true);

                if (table.getSelectedRowCount() == 1) {
                    isUpdateFlow = true;
                    personIdComboBox.setModel(new DefaultComboBoxModel(personIdNameMap.keySet().toArray()));
                    personIdComboBox.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            nameTxtField.setText(personIdNameMap.get(personIdComboBox.getSelectedItem()));
                        }
                    });
                    personIdComboBox.revalidate();
                    rowData = data.get(table.convertRowIndexToModel(table.getSelectedRow()));
                    personIdComboBox.setSelectedItem("" + rowData[0]);
                    nameTxtField.setText("" + rowData[1]);
                    blockedURLTxtField.setText("" + rowData[2]);
                }
            }
        });

        remove = new JButton("Remove");
        remove.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int[] selectedRowCount = table.getSelectedRows();
                if (selectedRowCount.length > 0) {
                    List<Integer> integerList = new ArrayList<Integer>(selectedRowCount.length);
                    for (int row : selectedRowCount) {
                        integerList.add(table.convertRowIndexToModel(row));
                    }

                    Collections.sort(integerList);

                    Connection con = null;
                    PreparedStatement st = null;
                    ResultSet rs = null;
                    try {
                        con = DBHelper.getInstance().getConnection();
                        String sql = "Delete BlockedURLSForUsers where PersonId in (TEST)";
                        StringBuilder builder = new StringBuilder("'" + data.get(integerList.get(0))[0] + "'");

                        for (int i = 1; i < integerList.size(); i++) {
                            builder.append(",'").append(data.get(integerList.get(i))[0] + "'");
                        }

                        sql = sql.replaceAll("TEST", builder.toString());

                        st = con.prepareStatement(sql);
                        st.executeUpdate();
                        for (int count = integerList.size() - 1; count >= 0; count--) {
                            int index = integerList.get(count);
                            data.remove(index);
                        }

                        ((AbstractTableModel) table.getModel()).fireTableDataChanged();
                        table.invalidate();
                        table.repaint();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        DBHelper.closeResultSet(rs);
                        DBHelper.closeStatement(st);
                        DBHelper.closeConnection(con);
                    }
                }
            }
        });

        topSouthPanel.add(add);
        topSouthPanel.add(edit);
        topSouthPanel.add(remove);

        topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.add(topNorthPanel, BorderLayout.NORTH);
        topPanel.add(scrollPane, BorderLayout.CENTER);
        topPanel.add(topSouthPanel, BorderLayout.SOUTH);


        JPanel bottomNorthPanel = new JPanel(new FlowLayout());
        bottomNorthPanel.add(bottomTitle);

//        JPanel bottomCenterPanel = new JPanel(new GridLayout(6, 2, 20, 10));
        JPanel bottomCenterPanel = new JPanel(new GridLayout(6, 1));
        personIdLabel = new JLabel("Person Id     ", SwingConstants.RIGHT);

        loadPersonIds();
        personIdComboBox = new JComboBox();
        personIdComboBox.setModel(new DefaultComboBoxModel(personIdNameMap.keySet().toArray()));
        personIdComboBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                nameTxtField.setText(personIdNameMap.get(personIdComboBox.getSelectedItem()));
            }
        });

        personIdComboBox.setMinimumSize(new Dimension(330, 20));
        personIdComboBox.setPreferredSize(new Dimension(330, 20));
        
        nameLabel = new JLabel("Name", SwingConstants.RIGHT);
        nameTxtField = new JTextField();
        nameTxtField.setColumns(30);
        nameTxtField.setHorizontalAlignment(JTextField.LEFT);
        nameTxtField.setEditable(false);
        nameTxtField.setEnabled(false);
        blockedURLLabel = new JLabel("Blocked URL", SwingConstants.RIGHT);
        blockedURLTxtField = new JTextField();
        blockedURLTxtField.setColumns(30);
        blockedURLTxtField.setHorizontalAlignment(JTextField.LEFT);
        
        JPanel p1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        p1.add(personIdLabel);
        p1.add(personIdComboBox);
        bottomCenterPanel.add(p1);

        
        JPanel p3 = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        p3.add(nameLabel);
        p3.add(nameTxtField);
        bottomCenterPanel.add(p3);

        JPanel p4 = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        p4.add(blockedURLLabel);
        p4.add(blockedURLTxtField);
        bottomCenterPanel.add(p4);

        JScrollPane sPane = new JScrollPane(bottomCenterPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        save = new JButton("Save");
        save.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                String personId = (String)personIdComboBox.getSelectedItem();
                String name = nameTxtField.getText();
                String blockedUrl = blockedURLTxtField.getText();
                

                if (personId == null || "".equals(personId)) {
                    JOptionPane.showMessageDialog(thisComponent, "Please select PersonId", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (name == null || "".equals(name)) {
                    JOptionPane.showMessageDialog(thisComponent, "Please specify valid Name", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (blockedUrl == null || "".equals(blockedUrl)) {
                    JOptionPane.showMessageDialog(thisComponent, "Please specify blocked URL", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String sql = null;
                
                if (!isUpdateFlow) {
                    sql = "insert into BlockedURLSForUsers(BlockedURL,PersonId) values (?,?)";
                } else {
                    sql = "update BlockedURLSForUsers set BlockedURL= ? where BlockedURL= ? and PersonId=?";
                }

                Connection con = null;
                PreparedStatement st = null;
                try {
                    con = DBHelper.getInstance().getConnection();
                    st = con.prepareStatement(sql);
                    st.setString(1, blockedUrl);
                    int index=2;
                    if(isUpdateFlow){
                        st.setString(2, (String)rowData[2]);
                        index++;
                    }
                    st.setString(index, personId);

                    st.executeUpdate();

                    resetFields();
                    enableDisableTopButtons();
                    disableBottomPanel();
                    if (isUpdateFlow) {
                        Object[] modifiedData = data.get(table.convertRowIndexToModel(table.getSelectedRow()));
                        modifiedData[2] = blockedUrl;

                    } else {
                        Object[] newData = new Object[]{
                            personId, name, blockedUrl
                        };
                        data.add(newData);
                    }
                    ((AbstractTableModel) table.getModel()).fireTableDataChanged();
                    table.invalidate();
                    table.repaint();
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    DBHelper.closeStatement(st);
                    DBHelper.closeConnection(con);
                }
            }
        });

        cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                isUpdateFlow=false;
                resetFields();
                enableDisableTopButtons();
                disableBottomPanel();
            }
        });

        JPanel bottomSouthPanel = new JPanel(new FlowLayout());
        bottomSouthPanel.add(save);
        bottomSouthPanel.add(cancel);

        bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.add(bottomNorthPanel, BorderLayout.NORTH);
        bottomPanel.add(sPane, BorderLayout.CENTER);
        bottomPanel.add(bottomSouthPanel, BorderLayout.SOUTH);
        edit.setEnabled(false);
        remove.setEnabled(false);
        disableBottomPanel();
    }

    public void loadPersonIds(){
        personIdNameMap.clear();
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs=null;
        try {
            con = DBHelper.getInstance().getConnection();
            st=con.prepareStatement("select PersonId,Name from Person where TOPId <> 1 order by PersonId");
            rs=st.executeQuery();
            while(rs.next()){
                personIdNameMap.put(rs.getString("PersonId"), rs.getString("Name"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            DBHelper.closeResultSet(rs);
            DBHelper.closeStatement(st);
            DBHelper.closeConnection(con);
        }
    }


    private void disableBottomPanel() {
        resetFields();
        personIdComboBox.setEnabled(false);
        nameTxtField.setEnabled(false);
        blockedURLTxtField.setEnabled(false);
        save.setEnabled(false);
        cancel.setEnabled(false);
    }

    private void enableDisableTopButtons() {
        if (table.getSelectedRowCount() > 0) {
            remove.setEnabled(true);
            if (table.getSelectedRowCount() == 1) {
                edit.setEnabled(true);
            } else {
                edit.setEnabled(false);
            }
        } else {
            edit.setEnabled(false);
            remove.setEnabled(false);
        }
        add.setEnabled(true);
        disableBottomPanel();
    }

    private void resetFields() {
        nameTxtField.setText("");
        blockedURLTxtField.setText("");
        
    }

    private void disableTopButtonsAndEnableBottomButtons(boolean isAdd) {
        add.setEnabled(false);
        edit.setEnabled(false);
        remove.setEnabled(false);
        personIdComboBox.setEnabled(isAdd);
        nameTxtField.setEnabled(false);
        blockedURLTxtField.setEnabled(true);
        save.setEnabled(true);
        cancel.setEnabled(true);
    }

    private void initializeAndLoadTableData() {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        try {

            con = DBHelper.getInstance().getConnection();

            st = con.createStatement();

            String sql = "Select * from Person p,BlockedURLSForUsers bu where p.PersonId=bu.PersonId order by p.Name";

            rs = st.executeQuery(sql);
            data = new ArrayList<Object[]>();
            while (rs.next()) {
                Object[] obj = new Object[]{
                    rs.getString("PersonId"),
                    rs.getString("Name"),
                    rs.getString("BlockedURL")
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
