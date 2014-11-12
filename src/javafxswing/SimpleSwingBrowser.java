/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javafxswing;

/**
 *
 * @author Akkarapon
 */
import java.awt.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import javafx.scene.control.TextField;
import javafx.geometry.Insets;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.*;
  
public class SimpleSwingBrowser extends JFrame {
 
    private final JFXPanel jfxPanel = new JFXPanel();
    private WebEngine engine;
    private WebEngine rEngine;
 
    private final JPanel panel = new JPanel(new BorderLayout());
    private final Button btnGet = new Button();
    private final TextField lTxtURL = new TextField();
    private final TextField rTxtURL = new TextField();
    private final TextArea textArea = new TextArea();
    private final TextArea rTextArea = new TextArea();
    private final JProgressBar progressBar = new JProgressBar();
 
    public SimpleSwingBrowser() {
        super();
        initComponents();
    }

    private void initComponents() {
        createScene();
 
        ActionListener al = (ActionEvent e) -> {
            new Thread(() -> {
                textArea.append("THREAD 1 : START\n");
                Platform.runLater(() -> {
                    textArea.append("THREAD 1 : GET -> "+lTxtURL.getText()+"\n");
                    engine.load(lTxtURL.getText());
                    getHttpResponse(lTxtURL.getText(), "THREAD 1");
                });
            }).start();
            new Thread(() -> {
                textArea.append("THREAD 2 : START\n");
                Platform.runLater(() -> {
                    textArea.append("THREAD 2 : GET -> "+rTxtURL.getText()+"\n");
                    rEngine.load(rTxtURL.getText());
                    getHttpResponse(rTxtURL.getText(), "THREAD 2");
                });
            }).start();
            
        };
 
        btnGet.addActionListener(al);
        
        progressBar.setPreferredSize(new Dimension(150, 18));
        progressBar.setStringPainted(true);
        
        btnGet.setPreferredSize(new Dimension(150, 45));
        btnGet.setLabel("Get Pages");
        btnGet.setBackground(Color.LIGHT_GRAY);
        
        textArea.setPreferredSize(new Dimension(620, 250));
        rTextArea.setPreferredSize(new Dimension(620, 250));
  
        JPanel topBar = new JPanel(new BorderLayout(5, 0));
        topBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        topBar.add(btnGet, BorderLayout.LINE_END);
 
        JPanel statusBar = new JPanel(new BorderLayout(5, 0));
        statusBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        statusBar.add(textArea, BorderLayout.LINE_START);
        statusBar.add(rTextArea, BorderLayout.LINE_END);
         
        panel.add(topBar, BorderLayout.NORTH);
        panel.add(jfxPanel, BorderLayout.CENTER);
        panel.add(statusBar, BorderLayout.SOUTH);
        
        getContentPane().add(panel);
        
        setPreferredSize(new Dimension(1280, 720));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();

    }
 
    private void createScene() {
 
        Platform.runLater(() -> {
            WebView rView = new WebView(); 
            rEngine = rView.getEngine();
            
            rEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
                @Override
                    public void changed(ObservableValue ov, State oldState, State newState) {
                        if (newState == State.SUCCEEDED) {
                            textArea.append("THREAD 2 : TEMINATE\n");
                    }
                }
            });
            
            WebView view = new WebView();
            engine = view.getEngine();
            
            engine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
                @Override
                    public void changed(ObservableValue ov, State oldState, State newState) {
                        if (newState == State.SUCCEEDED) {
                            textArea.append("THREAD 1 : TEMINATE\n");
                    }
                }
            });
            
            SimpleSwingBrowser.this.setTitle("Akkarapon Jairangsee s5050537@kmitl.ac.th - Java Web Browser (Network Programming Class Assignment)");
            
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(0, 10, 5, 10));
            grid.add(view, 0, 0);
            grid.add(rView, 1, 0);
            grid.add(rTxtURL, 1 ,1);
            grid.add(lTxtURL, 0 , 1);
            Scene scene = new Scene(grid, 700, 500);
            jfxPanel.setScene(scene);
     
        });
    }
 
    public void loadURL(final String url) {
        Platform.runLater(() -> {
            String tmp = toURL(url);
            
            if (tmp == null) {
                tmp = toURL("http://" + url);
            }
        });
    }
    
    public void getHttpResponse(String url, String caller) {
         try {
 
	URL obj = new URL(url);
	URLConnection conn = obj.openConnection();
	Map<String, java.util.List<String>> map = conn.getHeaderFields();
 
	//System.out.println("Printing Response Header...\n");
 
	for (Map.Entry<String, java.util.List<String>> entry : map.entrySet()) {
		rTextArea.append(caller+" "+entry.getKey() + " : " + entry.getValue()+"\n");
	}
 
    } catch (Exception e) {
	e.printStackTrace();
    }
    }

    private static String toURL(String str) {
        try {
            return new URL(str).toExternalForm();
        } catch (MalformedURLException exception) {
                return null;
        }
    }

   

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimpleSwingBrowser browser = new SimpleSwingBrowser();
            browser.setVisible(true);
        });
    }
}

