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
import javax.servlet.http.HttpSession;

/**
 *
 * @author Jai Bhavani
 */
public class URLFilter extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession session=request.getSession(false);
        Map content=new HashMap();
        if(session == null || session.getAttribute(ConstantClass.AUTHORIZATION) == null){
            DBHelper.getInstance().logRequest(request, null, true);
            content.put(ConstantClass.IS_LoggedIn,false);
            forward(request, response, content);
            return;
        }else{
            content.put(ConstantClass.IS_LoggedIn,true);
        }
        String url=request.getParameter(ConstantClass.REQUESTED_URL);
        content.put(ConstantClass.AUTHORIZED_URL, false);
        if(url==null){
            DBHelper.getInstance().logRequest(request, null, true);
            forward(request, response, content);
            return;
        }

        String requestedURL=url;
        System.out.println("Original URL:"+requestedURL);
        int indexOf=url.indexOf("url?q=");
        if(indexOf >= 0){
            requestedURL = requestedURL.substring(indexOf+6);
            requestedURL = requestedURL.substring(0, requestedURL.indexOf(";") <0 ? requestedURL.length()-1 : requestedURL.indexOf(";"));
            System.out.println("New Requested URL:"+requestedURL);
        }

        boolean isBlockedURL=DBHelper.getInstance().checkIfBlockedURL(requestedURL,(String)session.getAttribute(ConstantClass.PERSON_ID));
        DBHelper.getInstance().logRequest(request, (String)session.getAttribute(ConstantClass.PERSON_ID), isBlockedURL);

        content.put(ConstantClass.AUTHORIZED_URL, !isBlockedURL);
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
