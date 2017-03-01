/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.intelsoft.helper;

import com.intelsoft.provider.DaoFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author Jai Bhavani
 */
public class DBHelper {
    private DaoFactory factory;
    public static DBHelper instance;
    private long currentSequenceNumber=0l;
    private int fetchCount=10;
    private int remainingCount=0;
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

    public Connection getConnection(){
        return factory.getconnection();
    }


    private DBHelper() {
        factory = new DaoFactory();
    }

    public String nextVal(){
        return nextVal("User");
    }

    public String nextVal(String preText){

        if(remainingCount==0){
            fetchNextCountFromSequence();
        }
        remainingCount--;
        long val=currentSequenceNumber + (fetchCount-remainingCount);
        return preText+val;
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

    private void fetchNextCountFromSequence() {
        Connection con =null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            con=getConnection();
            st=con.prepareStatement("Select * from SequenceTable where SequenceName='LM'");
            rs=st.executeQuery();
            if(rs.next()){
                currentSequenceNumber = rs.getLong("CurrentSequenceNumber");
                rs.close();
                rs=null;
                st.close();
                st=null;
            }

            st=con.prepareStatement("update SequenceTable set CurrentSequenceNumber=? where SequenceName=?");
            st.setLong(1, currentSequenceNumber+10);
            st.setString(2, "LM");
            st.executeUpdate();
            remainingCount=fetchCount;
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            closeResultSet(rs);
            closeStatement(st);
            closeConnection(con);
        }
    }

    /*
        Connection conn=null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        try {

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to perform login operation");
        }finally{
            DBHelper.closeResultSet(rs);
            DBHelper.closeStatement(ps);
            DBHelper.closeConnection(conn);
        }
        return false;
        */
    public boolean login(String userName,String password){
        boolean isSuccess=false;
        Connection conn=null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        try {
            conn = getConnection();
            ps=conn.prepareStatement("Select l.Password,pm.TOPName from Login l,Person p ,PersonMaster pm where l.PersonId=p.PersonId and p.TOPId = pm.TOPId");
            rs=ps.executeQuery();
            if(rs.next()){
                if(password.equals(rs.getString("Password")) && "Admin".equals(rs.getString("TOPName"))){
                    isSuccess = true;
                }else{
                    System.out.println("Either Password is mismatching or Someoone other than Admin is trying to login to the system");
                }
            }else{
                System.out.println("invalid Username/Password");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to perform login operation");
        }finally{
            DBHelper.closeResultSet(rs);
            DBHelper.closeStatement(ps);
            DBHelper.closeConnection(conn);
        }
        return isSuccess;
    }
}

