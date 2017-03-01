/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.intelsoft.panel;

import com.intelsoft.helper.DBHelper;
import com.intelsoft.main.ProxyAdminLauncher;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author Jai Bhavani
 */
public class LoginPanel extends JPanel{

    private JFrame parentFrame=null;
    JTextField userNameTxtField=new JTextField();
    JPasswordField passwordField=new JPasswordField();
    public LoginPanel(JFrame thisFrame) {
        this.parentFrame=thisFrame;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        initiComponents();
    }

    private void initiComponents() {
        JLabel userNameLabel=new JLabel("User Name",JLabel.CENTER);
        JLabel passwordLabel=new JLabel("Password ",JLabel.CENTER);


        JButton login =new JButton("Login");
        login.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String uName=userNameTxtField.getText();
                String password=passwordField.getText();

                if(uName == null || password == null || "".equals(uName) || "".equals(password)){
                    JOptionPane.showMessageDialog(parentFrame, "Please specify correct User Name and Password", "Invalid UserName/Password", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean isSuccess=DBHelper.getInstance().login(uName, password);
                if(!isSuccess){
                    JOptionPane.showMessageDialog(parentFrame, "Please specify correct User Name and Password", "Login Failure", JOptionPane.ERROR_MESSAGE);
                    return;
                }else{
                    ((ProxyAdminLauncher)parentFrame).isLogin(true);
                }

                
            }
        });

        JPanel innerPanel=new JPanel(new GridLayout(3, 2, 10, 10));
        innerPanel.setMaximumSize(new Dimension(600, 150));
        innerPanel.add(userNameLabel);
        innerPanel.add(userNameTxtField);
        innerPanel.add(passwordLabel);
        innerPanel.add(passwordField);
        innerPanel.add(login);

        this.add(Box.createVerticalGlue());
        this.add(innerPanel);
        this.add(Box.createVerticalGlue());
    }

    public void resetFields(){
        userNameTxtField.setText("");
        passwordField.setText("");
    }

}
