
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import static java.awt.event.WindowEvent.WINDOW_CLOSING;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import java.util.zip.ZipOutputStream;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Administrator
 */
public class Misc {
    static void closeWindow(Window window) {
        window.dispatchEvent(new WindowEvent(window, WINDOW_CLOSING));
    }
    static void assignESCforClose(JDialog window) {
        window.getRootPane().registerKeyboardAction(new ActionListener(){public void actionPerformed(ActionEvent ae){Misc.closeWindow(window);}},KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
    static void assignESCforClose(JFrame window) {
        window.getRootPane().registerKeyboardAction(new ActionListener(){public void actionPerformed(ActionEvent ae){Misc.closeWindow(window);}},KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    static void centerWindow(Window window) {
        
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        window.setLocation(dim.width/2-window.getSize().width/2, dim.height/2-window.getSize().height/2);        
    }
    
}
