/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.gafalamaster.action;

import com.gafalamaster.helper.ConstantClass;
import com.gafalamaster.helper.DBHelper;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Jai Bhavani
 */
public class AuthenticationHandler extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String userName=request.getParameter("uName");
        String password=request.getParameter("pw");
        Map content=new HashMap();
        System.out.println("SessionId:"+request.getSession().getId());
        if(userName == null || "".equals(userName) || password == null || "".equals(password)){
            content.put(ConstantClass.IS_AUTHENTICATED, false);
            forward(request, response, content);
            return;
        }

        String obj[]=new String[2];

        boolean isAuthenticated=DBHelper.getInstance().authenticateUser(userName, password, obj);
        content.put(ConstantClass.IS_AUTHENTICATED, isAuthenticated);
        if(isAuthenticated){
            request.getSession().setAttribute(ConstantClass.AUTHORIZATION, true);
            request.getSession().setAttribute(ConstantClass.PERSON_ID, obj[0]);
            request.getSession().setAttribute(ConstantClass.TOP_NAME, obj[1]);
            System.out.println("Created Session Id:"+request.getSession().getId());
        }
        forward(request, response, content);
    } 

    private void forward(HttpServletRequest request, HttpServletResponse response, Object obj) throws ServletException, IOException {
        ObjectOutputStream oStream = new ObjectOutputStream(response.getOutputStream());
        oStream.writeObject(obj);
        oStream.flush();
        oStream.close();
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

