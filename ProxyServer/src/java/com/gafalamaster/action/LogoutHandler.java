/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.gafalamaster.action;

import com.gafalamaster.helper.ConstantClass;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Jai Bhavani
 */
public class LogoutHandler extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        Map content=new HashMap();
        HttpSession session=request.getSession(false);
        if(session==null){
            System.out.println("Invalid request.....................");
            content.put(ConstantClass.IS_LOGOUT_SUCCESS, false);
        }else{
            System.out.println("SessionId:"+session.getId());
            session.invalidate();
            content.put(ConstantClass.IS_LOGOUT_SUCCESS, true);
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
