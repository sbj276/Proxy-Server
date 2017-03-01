package com.gafalamaster.helper;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import com.intelsoft.provider.DaoFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Jai Bhavani
 */
public class DBHelper {
    private DaoFactory factory;
    public static DBHelper instance;

    public static DBHelper getInstance(){
        if(instance == null){
            synchronized(DBHelper.class){
                if(instance == null){
                    instance = new DBHelper();
                }
            }
        }
        return instance ;
    }


    private DBHelper() {
        factory = new DaoFactory();
    }

    public boolean authenticateUser(String username, String password,String[] obj){
        PreparedStatement ps=null;
        ResultSet rs=null;
        boolean isAuthenticated=false;
        Connection conn=null;
        try{
            conn=factory.getconnection();
            ps=conn.prepareStatement("select * from Login where UserName = ?");
            ps.setString(1, username);
            rs= ps.executeQuery();
//            rs=conn.createStatement().executeQuery("select UserID from User where Password =\""+password+"\"");
            if(rs.next()){
                if(password.equals(rs.getString("Password"))){
                    obj[0]=rs.getString("PersonId");
                    closeResultSet(rs);
                    closeStatement(ps);

                    ps=conn.prepareStatement("select TOPName from PersonMaster where TOPId = (SELECT TOPId from Person where PersonId = ?)");
                    ps.setString(1, obj[0]);
                    rs= ps.executeQuery();
                    if(rs.next()){
                        obj[1]=rs.getString("TOPName");
                        isAuthenticated=true;
                    }
                }
            }
        }catch(Exception ex){
            System.out.println("Unable to Authenticate the user.");
            ex.printStackTrace();
        }finally{
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(conn);
        }
        return isAuthenticated;
    }

   public boolean isPasswordMatchForUser(String oldPassword, String userId){
        boolean isPasswordMatch=false;

        PreparedStatement ps=null;
        ResultSet rs=null;
        Connection conn=factory.getconnection();
        try{
            ps=conn.prepareStatement("SELECT Password FROM Login WHERE PersonId = ?");
            ps.setString(1, userId);
            rs= ps.executeQuery();
            if(rs.next()){
                String existingPassword=rs.getString("Password");
                if(oldPassword.equals(existingPassword)){
                 isPasswordMatch=true;
                }
            }
        }catch(SQLException ex){
            System.out.println("Failed to retrieve balance information for user account");
            ex.printStackTrace();
        }finally{
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(conn);
        }

        return isPasswordMatch;
    }

    public boolean updatePasswordForUser(String newPass,String userId){
        boolean isSuccess=false;

        PreparedStatement ps=null;
        ResultSet rs=null;
        Connection conn=factory.getconnection();
        try{
            ps=conn.prepareStatement("Update Login set Password = ? WHERE PersonId = ?");
            ps.setString(1, newPass);
            ps.setString(2, userId);
            if(ps.executeUpdate() == 1){
                isSuccess=true;
            }
        }catch(SQLException ex){
            System.out.println("Failed to update password for user");
            ex.printStackTrace();
        }finally{
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(conn);
        }

        return isSuccess;
    }

   
    public static void closeResultSet(ResultSet rs){
        try{
            if(null != rs){
                rs.close();
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

   public static void closeStatement(Statement stmt){
        try{
            if(null != stmt){
                stmt.close();
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static void closeConnection(Connection conn) {
        try{
            if(null != conn){
                conn.close();
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public boolean checkIfBlockedURL(String requestedURL, String personId) {
        PreparedStatement ps=null;
        ResultSet rs=null;
        Connection conn=null;
        try{
            conn=factory.getconnection();
            ps=conn.prepareStatement("Select BlockedURL from BlockedURLSForUsers where PersonId =?");
            ps.setString(1, personId);
            rs=ps.executeQuery();
            while(rs.next()){
                String blockedURL=rs.getString("BlockedURL");
                if(requestedURL.toLowerCase().contains(blockedURL.toLowerCase())){
                    return true;
                }
            }
        }catch(SQLException ex){
            System.out.println("Failed to check blocked URL");
            ex.printStackTrace();
        }finally{
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(conn);
        }
        return false;
    }

    public void logRequest(HttpServletRequest request, String personId,boolean isBlocked){
        PreparedStatement ps=null;
        ResultSet rs=null;
        Connection conn=null;
        try{
            String url=request.getParameter(ConstantClass.REQUESTED_URL);
            if(url==null){
                url="";
            }
            conn=factory.getconnection();
            ps=conn.prepareStatement("insert into UserActivityLog (PersonId,SessionId,RemoteAddress,RemotePort,HttpMethod,RemoteHost,Scheme,RequestURL,QueryString,IsAllowed,TimeOfRequest,ActualURL) values(?,?,?,?,?,?,?,?,?,?,?,?)");
            ps.setString(1, personId == null ? "" : personId);
            ps.setString(2, request.getSession(false) == null? "" : request.getSession(false).getId());
            ps.setString(3, request.getRemoteAddr());
            ps.setString(4, request.getRemotePort()+"");
            ps.setString(5, request.getMethod());
            ps.setString(6, request.getRemoteHost());
            ps.setString(7, request.getScheme());
            ps.setString(8, request.getRequestURL().toString());
            ps.setString(9, request.getQueryString());
            ps.setString(10, isBlocked+"");
            ps.setTimestamp(11, new Timestamp(new Date().getTime()));
            ps.setString(12, url);
            
            ps.executeUpdate();
            
        }catch(SQLException ex){
            System.out.println("Failed to add log in UserActivityLog");
            ex.printStackTrace();
        }finally{
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(conn);
        }
    }
}
