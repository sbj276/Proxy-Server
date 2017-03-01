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
import java.util.List;
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
public class AddRemoveStudents extends JSplitPane {

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
    String[] columns = new String[]{"Sr.No.", "Person Id", "Person Type", "Name", "Address", "Blood Group", "Age", "Contact No.","Email Id","Gender"};
    AbstractTableModel model = null;
    JTable table = null;
    List<Object[]> data = null;
    JLabel bottomTitle = new JLabel("");
    JLabel personIdLabel = null;
    JTextField personIdTxtField = null;
    JLabel typeOfPersonLabel = null;
    JComboBox typeOfPersonTxtField = null;
    JLabel nameLabel = null;
    JTextField nameTxtField = null;
    JLabel addressLabel = null;
    JTextField addressTxtField = null;
    JLabel bloodGroupLabel = null;
    JTextField bloodGroupTxtField = null;
    JLabel ageLabel = null;
    JTextField ageTxtField = null;
    JLabel mobileNoLabel = null;
    JTextField mobileNoTxtField = null;
    JLabel emailIdLabel = null;
    JTextField emailIdTxtField = null;
    JLabel sexLabel = null;
    JComboBox sexComboBox = null;
    
    
    TableRowSorter<AbstractTableModel> sorter = null;

    public AddRemoveStudents() {
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
                resetFields();
                disableTopButtonsAndEnableBottomButtons();
                bottomPanel.setEnabled(true);
            }
        });

        edit = new JButton("Edit");
        edit.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                resetFields();
                disableTopButtonsAndEnableBottomButtons();
                bottomPanel.setEnabled(true);

                if (table.getSelectedRowCount() == 1) {
                    Object[] rowData = data.get(table.convertRowIndexToModel(table.getSelectedRow()));
                    personIdTxtField.setText("" + rowData[0]);
                    typeOfPersonTxtField.setSelectedItem("" + rowData[1]);
                    nameTxtField.setText("" + rowData[2]);
                    addressTxtField.setText("" + rowData[3]);
                    bloodGroupTxtField.setText("" + rowData[4]);
                    ageTxtField.setText("" + rowData[5]);
                    mobileNoTxtField.setText("" + rowData[6]);
                    emailIdTxtField.setText("" + rowData[7]);
                    sexComboBox.setSelectedItem("" + rowData[8]);
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
                        String sql = "update Person set IsRemoved='true' where PersonId in (TEST)";
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
        personIdTxtField = new JTextField();
        personIdTxtField.setColumns(30);
        personIdTxtField.setHorizontalAlignment(JTextField.LEFT);
        personIdTxtField.setEditable(false);
        personIdTxtField.setEnabled(false);
        typeOfPersonLabel = new JLabel("Person Type", SwingConstants.RIGHT);
        typeOfPersonTxtField = new JComboBox(new Object[]{"Admin","User"});
        typeOfPersonTxtField.setMinimumSize(new Dimension(330, 20));
        typeOfPersonTxtField.setPreferredSize(new Dimension(330, 20));
        nameLabel = new JLabel("Name", SwingConstants.RIGHT);
        nameTxtField = new JTextField();
        nameTxtField.setColumns(30);
        nameTxtField.setHorizontalAlignment(JTextField.LEFT);
        addressLabel = new JLabel("Address", SwingConstants.RIGHT);
        addressTxtField = new JTextField();
        addressTxtField.setColumns(30);
        addressTxtField.setHorizontalAlignment(JTextField.LEFT);
        bloodGroupLabel = new JLabel("Blood Group", SwingConstants.RIGHT);
        bloodGroupTxtField = new JTextField();
        bloodGroupTxtField.setColumns(30);
        bloodGroupTxtField.setHorizontalAlignment(JTextField.LEFT);
        ageLabel = new JLabel("Age                 ", SwingConstants.RIGHT);
        ageTxtField = new JTextField();
        ageTxtField.setColumns(30);
        ageTxtField.setHorizontalAlignment(JTextField.LEFT);
        mobileNoLabel = new JLabel("Mobile No.    ", SwingConstants.RIGHT);
        mobileNoTxtField = new JTextField();
        mobileNoTxtField.setColumns(30);
        mobileNoTxtField.setHorizontalAlignment(JTextField.LEFT);
        emailIdLabel = new JLabel("Email Id    ", SwingConstants.RIGHT);
        emailIdTxtField = new JTextField();
        emailIdTxtField.setColumns(30);
        emailIdTxtField.setHorizontalAlignment(JTextField.LEFT);
        sexLabel = new JLabel("Sex           ", SwingConstants.RIGHT);
        sexComboBox = new JComboBox(new Object[]{"Male","Female"});
        sexComboBox.setMinimumSize(new Dimension(330, 20));
        sexComboBox.setPreferredSize(new Dimension(330, 20));

        JPanel p1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        p1.add(personIdLabel);
        p1.add(personIdTxtField);
        bottomCenterPanel.add(p1);

        JPanel p2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        p2.add(typeOfPersonLabel);
        p2.add(typeOfPersonTxtField);
        bottomCenterPanel.add(p2);

        JPanel p3 = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        p3.add(nameLabel);
        p3.add(nameTxtField);
        bottomCenterPanel.add(p3);

        JPanel p4 = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        p4.add(addressLabel);
        p4.add(addressTxtField);
        bottomCenterPanel.add(p4);

        JPanel p5 = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        p5.add(bloodGroupLabel);
        p5.add(bloodGroupTxtField);
        bottomCenterPanel.add(p5);

        JPanel p6 = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        p6.add(ageLabel);
        p6.add(ageTxtField);
        bottomCenterPanel.add(p6);

        JPanel p9 = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        p9.add(mobileNoLabel);
        p9.add(mobileNoTxtField);
        bottomCenterPanel.add(p9);

        JPanel p10 = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        p10.add(emailIdLabel);
        p10.add(emailIdTxtField);
        bottomCenterPanel.add(p10);

        JPanel p8 = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        p8.add(sexLabel);
        p8.add(sexComboBox);
        bottomCenterPanel.add(p8);
        
        JScrollPane sPane = new JScrollPane(bottomCenterPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        save = new JButton("Save");
        save.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                String personId = personIdTxtField.getText();
                String typeOfPerson = (String)typeOfPersonTxtField.getSelectedItem();
                String name = nameTxtField.getText();
                String address = addressTxtField.getText();
                String bloodGroup=bloodGroupTxtField.getText();
                String ageTxt=ageTxtField.getText();
                String mobNo = mobileNoTxtField.getText();
                String emailId=emailIdTxtField.getText();
                String sex= (String)sexComboBox.getSelectedItem();
                
                if (typeOfPerson == null || "".equals(typeOfPerson)) {
                    JOptionPane.showMessageDialog(thisComponent, "Please select correct type of person", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                 if (name == null || "".equals(name)) {
                    JOptionPane.showMessageDialog(thisComponent, "Please specify valid Name", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (address == null || "".equals(address)) {
                    JOptionPane.showMessageDialog(thisComponent, "Please specify address", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (bloodGroup == null || "".equals(bloodGroup)) {
                    JOptionPane.showMessageDialog(thisComponent, "Please specify correct blood group", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (mobNo == null || "".equals(mobNo)) {
                    JOptionPane.showMessageDialog(thisComponent, "Please specify valid Mobile Number", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (emailId == null || "".equals(emailId)) {
                    JOptionPane.showMessageDialog(thisComponent, "Please specify valid EmailId", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (sex == null || "".equals(sex)) {
                    JOptionPane.showMessageDialog(thisComponent, "Please specify valid gender", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int topId=0;

                if("Admin".equals(typeOfPerson)){
                    topId = 1;
                }else if("User".equals(typeOfPerson)){
                    topId = 2;
                }else{
                    JOptionPane.showMessageDialog(thisComponent, "Please select correct type of person", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int age = 0;
                try {
                    age = Integer.parseInt(ageTxt);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(thisComponent, "Please specify age as intger number", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String sql = null;
                boolean isUpdateFlow = true;
                if (personId == null || "".equals(personId)) {
                    isUpdateFlow = false;
                    personId = DBHelper.getInstance().nextVal("User");
                    sql = "insert into Person(TOPId,Name,Address, BGroup,DOB,ContactNo,EmailId,Gender,IsRemoved,PersonId) values (?,?,?,?,?,?,?,?,'false',?)";
                } else {
                    sql = "update Person set TOPId= ? , Name=?,Address=?,BGroup=?,DOB=?,ContactNo=?,EmailId=?,Gender=? where PersonId=?";
                }

                Connection con = null;
                PreparedStatement st = null;
                try {
                    con = DBHelper.getInstance().getConnection();
                    st = con.prepareStatement(sql);
                    st.setInt(1, topId);
                    st.setString(2, name);
                    st.setString(3, address);
                    st.setString(4, bloodGroup);
                    st.setInt(5, age);
                    st.setString(6, mobNo);
                    st.setString(7, emailId);
                    st.setString(8, sex);
                    st.setString(9, personId);

                    st.executeUpdate();

                    if(!isUpdateFlow){
                        DBHelper.getInstance().closeStatement(st);
                        st=con.prepareStatement("insert into Login (UserName,Password,PersonId) values (?,?,?)");
                        st.setString(1, personId);
                        st.setString(2, personId);
                        st.setString(3, personId);
                        st.executeUpdate();
                    }
                    resetFields();
                    enableDisableTopButtons();
                    disableBottomPanel();
                    if (isUpdateFlow) {
                        Object[] modifiedData = data.get(table.convertRowIndexToModel(table.getSelectedRow()));
                        modifiedData[1] = typeOfPerson;
                        modifiedData[2] = name;
                        modifiedData[3] = address;
                        modifiedData[4] = bloodGroup;
                        modifiedData[5] = age;
                        modifiedData[6] = mobNo;
                        modifiedData[7] = emailId;
                        modifiedData[8] = sex;

                    } else {
                        Object[] newData = new Object[]{
                            personId, typeOfPerson, name, address,bloodGroup,age,mobNo,emailId,sex
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

    private void disableBottomPanel() {
        resetFields();
        personIdTxtField.setEnabled(false);
        typeOfPersonTxtField.setEnabled(false);
        nameTxtField.setEnabled(false);
        addressTxtField.setEnabled(false);
        bloodGroupTxtField.setEnabled(false);
        sexComboBox.setEnabled(false);
        mobileNoTxtField.setEnabled(false);
        emailIdTxtField.setEnabled(false);
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
        personIdTxtField.setText("");
        nameTxtField.setText("");
        addressTxtField.setText("");
        bloodGroupTxtField.setText("");
        mobileNoTxtField.setText("");
        emailIdTxtField.setText("");
    }

    private void disableTopButtonsAndEnableBottomButtons() {
        add.setEnabled(false);
        edit.setEnabled(false);
        remove.setEnabled(false);
        personIdTxtField.setEnabled(false);
        typeOfPersonTxtField.setEnabled(true);
        nameTxtField.setEnabled(true);
        addressTxtField.setEnabled(true);
        bloodGroupTxtField.setEnabled(true);
        sexComboBox.setEnabled(true);
        mobileNoTxtField.setEnabled(true);
        emailIdTxtField.setEnabled(true);
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

            String sql = "Select * from Person p,PersonMaster pm where p.TOPId=pm.TopId and IsRemoved='false' order by Name,DOB,Address";

            rs = st.executeQuery(sql);
            data = new ArrayList<Object[]>();
            while (rs.next()) {
                Object[] obj = new Object[]{
                    rs.getString("PersonId"),
                    rs.getString("TOPName"),
                    rs.getString("Name"),
                    rs.getString("Address"),
                    rs.getString("BGroup"),
                    rs.getInt("DOB"),
                    rs.getString("ContactNo"),
                    rs.getString("EmailId"),
                    rs.getString("Gender")
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
