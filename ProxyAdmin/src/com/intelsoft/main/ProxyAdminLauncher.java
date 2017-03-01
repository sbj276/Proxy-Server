/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.intelsoft.main;

import com.intelsoft.panel.AddRemoveBlockedURLs;
import com.intelsoft.panel.AddRemoveStudents;
import com.intelsoft.panel.AuditInfoPanel;
import com.intelsoft.panel.LoginPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Jai Bhavani
 */
public class ProxyAdminLauncher extends JFrame{
    private JButton home=null;
    private JButton addRemoveBlockedURL=null;
    private JButton addRemoveStudent=null;
    private JButton auditInfo=null;
    private JButton login = null;
    private JButton logout=null;
    private JPanel mainPanel=null;
    private JPanel welcomePanel=null;
    private JPanel menuPanel=new JPanel(new GridLayout(1, 5,0,0));
    private AddRemoveStudents addRemoveStudentPanel=null;
    private AddRemoveBlockedURLs addRemoveBlockedURLPanel=null;
    private AuditInfoPanel auditInfoPanel=null;
    private LoginPanel loginPanel=null;
    

    private JFrame thisFrame=null;
    private Component prevComponentInFocus=null;


    public ProxyAdminLauncher(String title) throws HeadlessException {
        this.thisFrame=this;
        initComponents();
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(600, 600));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        pack();
        setVisible(true);

    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        new ProxyAdminLauncher("Proxy Server Admin Activity");
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());

        auditInfo = new JButton("Audit Info");
        auditInfo.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.out.println("Add/Remove Blocked URL called.");
                boolean reloadData=true;
                if(auditInfoPanel==null){
                    auditInfoPanel = new AuditInfoPanel();
                    reloadData=false;
                }
                if(reloadData){
                    auditInfoPanel.refreshTableData();
                }
                mainPanel.remove(prevComponentInFocus);
                mainPanel.add(auditInfoPanel);
                prevComponentInFocus=auditInfoPanel;
                thisFrame.validate();
                thisFrame.repaint();
            }
        });
        
        addRemoveBlockedURL = new JButton("Block URLs");
        addRemoveBlockedURL.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.out.println("Add/Remove Blocked URL called.");
                boolean reloadData=true;
                if(addRemoveBlockedURLPanel==null){
                    addRemoveBlockedURLPanel = new AddRemoveBlockedURLs();
                    reloadData=false;
                }
                if(reloadData){
                    addRemoveBlockedURLPanel.refreshTableData();
                }
                mainPanel.remove(prevComponentInFocus);
                mainPanel.add(addRemoveBlockedURLPanel);
                prevComponentInFocus=addRemoveBlockedURLPanel;
                thisFrame.validate();
                thisFrame.repaint();
                addRemoveBlockedURLPanel.setDividerLocation(0.5);
            }
        });

        addRemoveStudent=new JButton("Person");
        addRemoveStudent.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.out.println("Add/Remove Student called.");
                boolean reloadData=true;
                if(addRemoveStudentPanel==null){
                    addRemoveStudentPanel = new AddRemoveStudents();
                    reloadData=false;
                }
                if(reloadData){
                    addRemoveStudentPanel.refreshTableData();
                }
                mainPanel.remove(prevComponentInFocus);
                mainPanel.add(addRemoveStudentPanel);
                prevComponentInFocus=addRemoveStudentPanel;
                thisFrame.validate();
                thisFrame.repaint();
                addRemoveStudentPanel.setDividerLocation(0.5);
            }
        });

        

        home=new JButton("Home");
        home.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.out.println("Home Called");
                mainPanel.remove(prevComponentInFocus);
                mainPanel.add(welcomePanel);
                prevComponentInFocus=welcomePanel;
                thisFrame.validate();
                thisFrame.repaint();
            }
        });

        

        login = new JButton("Login");
        login.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.out.println("Login Called");
                if(loginPanel==null){
                    loginPanel = new LoginPanel(thisFrame);
                }else{
                    loginPanel.resetFields();
                }

                mainPanel.remove(prevComponentInFocus);
                mainPanel.add(loginPanel);
                prevComponentInFocus=loginPanel;
                thisFrame.validate();
                thisFrame.repaint();
            }
        });

        logout = new JButton("Logout");
        logout.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int option=JOptionPane.showConfirmDialog(thisFrame, "Are you sure you want to log out of the system?", "Log out Confirmation", JOptionPane.YES_NO_OPTION);
                if(option == JOptionPane.YES_OPTION){
                    isLogin(false);
                }
            }
        });
        menuPanel.add(home);
        menuPanel.add(login);
        this.getContentPane().add(menuPanel,BorderLayout.NORTH);

        mainPanel=new JPanel(new BorderLayout());

        welcomePanel = new JPanel(new BorderLayout());
        JLabel label=new JLabel("This is Proxy Admin UI. Administrator can view activities performed by All the users");
        welcomePanel.add(label,BorderLayout.NORTH);

        mainPanel.add(welcomePanel,BorderLayout.NORTH);
        this.getContentPane().add(mainPanel,BorderLayout.CENTER);
        prevComponentInFocus = welcomePanel;
    }

    public void isLogin(boolean isLogin){
        menuPanel.removeAll();
        menuPanel.add(home);
        if(isLogin){
            menuPanel.add(addRemoveStudent);
            menuPanel.add(addRemoveBlockedURL);
            menuPanel.add(auditInfo);
            menuPanel.add(logout);
        }else{
            menuPanel.add(login);
        }
        mainPanel.remove(prevComponentInFocus);
        prevComponentInFocus = welcomePanel;
        mainPanel.add(prevComponentInFocus);
        thisFrame.validate();
        thisFrame.repaint();
        
    }

}
