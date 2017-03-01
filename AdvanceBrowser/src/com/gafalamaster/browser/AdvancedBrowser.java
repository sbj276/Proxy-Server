/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gafalamaster.browser;

/**
 *
 * @author Jai Bhavani
 */
import com.gafalamaster.helper.ConstantClass;
import com.gafalamaster.login.PasswordDialog;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.CloseWindowListener;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AdvancedBrowser {

    private static final String AT_REST = "Ready";
    private Map<String,String> cookieMap = new HashMap<String, String>();
    StringBuilder builder=new StringBuilder();
    private String webUrl="http://localhost:8084";

    public AdvancedBrowser(String location) {
        Display display = new Display();
        final Shell shell = new Shell(display);
        shell.setText("Advanced Browser");

        shell.setLayout(new FormLayout());

        Composite controls = new Composite(shell, SWT.NONE);
        FormData data = new FormData();
        data.top = new FormAttachment(0, 0);
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(100, 0);
        controls.setLayoutData(data);

        Label status = new Label(shell, SWT.NONE);
        data = new FormData();
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(100, 0);
        data.bottom = new FormAttachment(100, 0);
        status.setLayoutData(data);

        final Browser browser = new Browser(shell, SWT.BORDER);
        data = new FormData();
        data.top = new FormAttachment(controls);
        data.bottom = new FormAttachment(status);
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(100, 0);
        browser.setLayoutData(data);

        controls.setLayout(new GridLayout(8, false));

        Button button = new Button(controls, SWT.PUSH);
        button.setText("Back");
        button.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
                browser.back();
            }
        });

        button = new Button(controls, SWT.PUSH);
        button.setText("Forward");
        button.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
                browser.forward();
            }
        });

        button = new Button(controls, SWT.PUSH);
        button.setText("Refresh");
        button.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
                browser.refresh();
            }
        });

        button = new Button(controls, SWT.PUSH);
        button.setText("Stop");
        button.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
                browser.stop();
            }
        });

        final Text url = new Text(controls, SWT.BORDER);
        url.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        url.setFocus();

        button = new Button(controls, SWT.PUSH);
        button.setText("Go");
        button.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
                browser.setUrl(url.getText());
            }
        });

        Label throbber = new Label(controls, SWT.NONE);
        throbber.setText(AT_REST);

        button = new Button(controls, SWT.PUSH);
        button.setText("Login  ");
        button.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
                Button button = (Button) event.getSource();
                if ("Login  ".equals(button.getText())) {
                    PasswordDialog dialog = new PasswordDialog(shell);
                    if (dialog.open() == Window.OK) {
                        String user = dialog.getUser();
                        String pw = dialog.getPassword();
                        System.out.println(user);
                        System.out.println(pw);
                        if (user == null || "".equals(user) || pw == null || "".equals(pw)) {
                            MessageDialog.openError(shell, "Invalid Username/Password", "Please Specify Username and Password");
                        } else {
                            boolean isSuccess = login(user, pw);
                            if (!isSuccess) {
                                MessageDialog.openError(shell, "Invalid Username/Password", "Please specify correct Username and Password");
                            } else {
                                MessageDialog.openInformation(shell, "Login Success", "You are logged in to system successfully");
                                button.setText("Logout");
                                button.redraw();
                            }
                        }
                    }
                } else {
                    boolean okClicked = MessageDialog.openConfirm(shell, "Logout Confirmation", "Do you really want to log out of system?");
                    if (okClicked) {
                        System.out.println("Ok clicked");
                        boolean isSuccess = logout();
                        if (!isSuccess) {
                            MessageDialog.openError(shell, "Logout Failure", "Failed to log out of the application");
                        } else {
                            MessageDialog.openInformation(shell, "Logout Success", "You are logged out of the system successfully");
                            builder = new StringBuilder();
                            button.setText("Login  ");
                            System.out.println("Logout called successfully");
                            button.redraw();
                        }
                    }


                }
            }
        });

        shell.setDefaultButton(button);

        browser.addCloseWindowListener(new AdvancedCloseWindowListener());
        browser.addLocationListener(new AdvancedLocationListener(url));
        browser.addProgressListener(new AdvancedProgressListener(throbber));
        browser.addStatusTextListener(new AdvancedStatusTextListener(status));

        // Go to the initial URL
        if (location != null) {
            browser.setUrl(location);
        }


        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }

    private boolean login(String user, String pw) {
        try {
            URL imageMapprURL = new URL(webUrl + "/AuthenticationHandler");
            HttpURLConnection con = (HttpURLConnection) imageMapprURL.openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
            out.write("uName=" + user+"&pw="+pw);
            out.flush();
//            ObjectOutputStream out = new ObjectOutputStream(con.getOutputStream());
//            out.writeObject(longImageId);
            out.close();
            int responseCode = con.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                String headerName=null;
                for (int i=1; (headerName = con.getHeaderFieldKey(i))!=null; i++) {
                    if (headerName.equals("Set-Cookie")) {
                        String cookie = con.getHeaderField(i);
                        System.out.println("Cookie:===="+cookie);
                        cookie = cookie.substring(0, cookie.indexOf(";"));
                        String cookieName = cookie.substring(0, cookie.indexOf("="));
                        String cookieValue = cookie.substring(cookie.indexOf("=") + 1, cookie.length());
                        System.out.println("CookieName:"+cookieName+", CookieValue"+cookieValue);
                        cookieMap.put(cookieName,cookieValue);
                        builder.append(cookieName).append("=").append(cookieValue).append(";");
                    }
                }

                if(builder.length()>0){
                    builder.deleteCharAt(builder.length()-1);
                }
                System.out.println("Builder:"+builder.toString());
                ObjectInputStream in = new ObjectInputStream(con.getInputStream());
                Map response = (Map) in.readObject();
                in.close();

                return (Boolean)response.get(ConstantClass.IS_AUTHENTICATED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean logout() {
        try {
            URL imageMapprURL = new URL(webUrl + "/LogoutHandler");
            HttpURLConnection con = (HttpURLConnection) imageMapprURL.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Cookie", builder.toString());
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
            out.write("test=tester");
            out.flush();
//            ObjectOutputStream out = new ObjectOutputStream(con.getOutputStream());
//            out.writeObject(longImageId);
            out.close();
            int responseCode = con.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                
                ObjectInputStream in = new ObjectInputStream(con.getInputStream());
                Map response = (Map) in.readObject();
                in.close();

                return (Boolean)response.get(ConstantClass.IS_LOGOUT_SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    class AdvancedCloseWindowListener implements CloseWindowListener {

        public void close(WindowEvent event) {
            ((Browser) event.widget).getShell().close();
        }
    }

    class AdvancedLocationListener implements LocationListener {

        private Text location;

        public AdvancedLocationListener(Text text) {
            location = text;
        }

        public void changing(LocationEvent event) {
            //System.out.println("Changing Started...............");
//    	event.doit = false;
            event.doit = verifyWhetherURLIsAllowed(event.location,event.display.getActiveShell());
            if(event.doit){
                location.setText(event.location);
            }else{
                MessageDialog.openError(event.display.getActiveShell(), "Blocked URL error", "This URL is blocked for you!!!");
                location.setText("");
            }

            System.out.println(event.location);
            //System.out.println("Changing Completed...............");
        }

        public void changed(LocationEvent event) {
            System.out.println("Changed Started...............");
//    	event.doit = false;
            location.setText(event.location);
            System.out.println(event.location);
            System.out.println("Changed Completed...............");
        }
    }

    private boolean verifyWhetherURLIsAllowed(String location,Shell shell) {
        try {
            URL imageMapprURL = new URL(webUrl + "/URLFilter");
            HttpURLConnection con = (HttpURLConnection) imageMapprURL.openConnection();
            con.setRequestMethod("POST");
            if(builder.length()>0){
                con.setRequestProperty("Cookie", builder.toString());
            }
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
            out.write(ConstantClass.REQUESTED_URL+"=" + location);
            out.flush();
//            ObjectOutputStream out = new ObjectOutputStream(con.getOutputStream());
//            out.writeObject(longImageId);
            out.close();
            int responseCode = con.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                ObjectInputStream in = new ObjectInputStream(con.getInputStream());
                Map response = (Map) in.readObject();
                in.close();

                boolean isLoggedIn = (Boolean)response.get(ConstantClass.IS_LoggedIn);
                if(!isLoggedIn){
                    MessageDialog.openError(shell, "Not Logged In error", "Please Login First and then try again");
                    return false;
                }

                return (Boolean)response.get(ConstantClass.AUTHORIZED_URL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    class AdvancedProgressListener implements ProgressListener {

        private Label progress;

        public AdvancedProgressListener(Label label) {
            progress = label;
        }

        public void changed(ProgressEvent event) {
            if (event.total != 0) {
                int percent = (int) (event.current / event.total);
                progress.setText(percent + "%");
            } else {
                progress.setText("?");
            }
        }

        public void completed(ProgressEvent event) {
            progress.setText(AT_REST);
        }
    }

    class AdvancedStatusTextListener implements StatusTextListener {

        private Label status;

        public AdvancedStatusTextListener(Label label) {
            status = label;
        }

        public void changed(StatusTextEvent event) {
            status.setText(event.text);
        }
    }

    public static void main(String[] args) {
        new AdvancedBrowser(null);
    }
}
