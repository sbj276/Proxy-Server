/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.intelsoft.provider;

/**
 *
 * @author Jai Bhavani
 */
import java.sql.*;


public class DaoFactory {

    public Connection getconnection()
    {
        try{
//            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            Class.forName( "com.microsoft.sqlserver.jdbc.SQLServerDriver" );
            System.out.println("Driver Registered");
            Connection con;
//            con = DriverManager.getConnection("jdbc:odbc:OnlineVoting");
            con = DriverManager.getConnection("jdbc:sqlserver://localhost:12500;DatabaseName=ProxyServer;","sa", "adminadmin");

            System.out.println("Connection Established");
            return con;
        }
        catch (ClassNotFoundException ex)
        {
            ex.printStackTrace();
            return null;
        }
        catch (SQLException ex)
            {
                ex.printStackTrace();
                return null;
            }
    }

}
