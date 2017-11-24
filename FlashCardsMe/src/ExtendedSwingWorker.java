
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.showOptionDialog;
import javax.swing.SwingWorker;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Administrator
 */
public abstract class ExtendedSwingWorker extends SwingWorker<Void, Void> implements Model.Model.IProgress {

    @Override
    public void setProgress2(int i) {
        setProgress(i);
    }

    @Override
    public boolean isCancelled2() {
        return isCancelled();
    }

    @Override
    public int showOptionDialog2(String message, String title, String[] buttons, String selected) {
        return showOptionDialog(null, message, title, YES_NO_OPTION, QUESTION_MESSAGE, null, buttons, selected);
    }
    
}
