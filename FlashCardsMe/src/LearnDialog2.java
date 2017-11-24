
import Model.Model;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
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
public class LearnDialog2 extends javax.swing.JDialog {

    Model.Deck deck;
    
    String[] stackNames = {"Deck", "Learning", "Hard", "Hardest", "Easy", "Learned", "Favorities"};
    Model.Deck.LearningSession.Stack[] stacks = new Model.Deck.LearningSession.Stack[stackNames.length];
    
    JLabel[] labels = new JLabel[7];
    JButton[] buttons = new JButton[7];

    /**
     * Creates new form LearnDialog2
     */
    public LearnDialog2(java.awt.Frame parent, Model.Deck deck) {
        super(parent, true);
        this.deck=deck;
        
        setTitle("Learn deck \""+deck.getName()+"\"");
        initComponents();

        /*
        try {
            for (int i=0; i<=5; i++) {
                    labels[i] = (JLabel) LearnDialog2.class.getField("jLabelS"+Integer.toString(i)).get(this);
                    buttons[i] = (JButton) LearnDialog2.class.getField("jButtonS"+Integer.toString(i)).get(this);
            }
        } catch (Exception ex) {
            Logger.getLogger(LearnDialog2.class.getName()).log(Level.SEVERE, null, ex);
        } 
        */
        
        labels[0] = jLabelS0;
        labels[1] = jLabelS1;
        labels[2] = jLabelS2;
        labels[3] = jLabelS3;
        labels[4] = jLabelS4;
        labels[5] = jLabelS5;
        labels[6] = jLabelFav;
        
        buttons[0] = jButtonS0;
        buttons[1] = jButtonS1;
        buttons[2] = jButtonS2;
        buttons[3] = jButtonS3;
        buttons[4] = jButtonS4;
        buttons[5] = jButtonS5;
        buttons[6] = jButtonFav;

        
        Misc.centerWindow(this);        

        Misc.assignESCforClose(this);

        if (deck.listLearningSessions().isEmpty())
            createLearningSession(getDefaultLSName());
        rebuildLSList();

        rebuildInterface();
    }
    final void rebuildLSList(){
        List<Model.Deck.LearningSession> lss = deck.listLearningSessions();
        DefaultComboBoxModel<Model.Deck.LearningSession> m = (DefaultComboBoxModel) jComboBoxLS.getModel();
        m.removeAllElements();
        lss.forEach(ls->m.addElement(ls));
    }

    final void rebuildInterface(){
        Model.Deck.LearningSession ls = (Model.Deck.LearningSession) jComboBoxLS.getSelectedItem();

        if (ls != null) {
            for (int i = 0; i < stackNames.length; i++) {
                Model.Deck.LearningSession.Stack stack=ls.getStack(stackNames[i]);
                labels[i].setText(Integer.toString(stack.getVisibleCardsNumber())+" cards");
                buttons[i].setEnabled(stack.getVisibleCardsNumber()>0);
            }

/*            
            jLabelFav.setText(Integer.toString(ls.countFavs())+" cards");
            jButtonFav.setEnabled(ls.countFavs()>0);
            jButtonFavClear.setEnabled(ls.countFavs()>0);
*/            
            jButtonFilter.setEnabled(true);

        } else {
            for (int i=0;i<=5;i++) {
                labels[i].setText("");
                buttons[i].setEnabled(false);
            }

            
            jLabelFav.setText("");
            jButtonFav.setEnabled(false);
            jButtonFavClear.setEnabled(false);
            
            jButtonFilter.setEnabled(false);
        }
        
        jButton01.setEnabled(jButtonS0.isEnabled());
        jButton10.setEnabled(jButtonS1.isEnabled());

        jButton12.setEnabled(jButtonS1.isEnabled());
        jButton21.setEnabled(jButtonS2.isEnabled());
        
        jButton23.setEnabled(jButtonS2.isEnabled());
        jButton32.setEnabled(jButtonS3.isEnabled());

        jButton14.setEnabled(jButtonS1.isEnabled());
        jButton41.setEnabled(jButtonS4.isEnabled());
        
        jButton45.setEnabled(jButtonS4.isEnabled());
        jButton54.setEnabled(jButtonS5.isEnabled());
        
        jButtonFavClear.setEnabled(jButtonFav.isEnabled());
    }

    void goLearn(Model.Deck.LearningSession.Stack stack, Model.Deck.Card card) {
        new ShowCardDialog(this, stack, card).setVisible(true);
        rebuildInterface();
    }
    
    void showCards(int nStack) {
        Model.Deck.LearningSession ls = (Model.Deck.LearningSession) jComboBoxLS.getSelectedItem();
        goLearn(ls.getStack(stackNames[nStack]), null);
    }
    
    void moveCards(int nStackFrom, int nStackTo){
        Model.Deck.LearningSession ls = (Model.Deck.LearningSession)jComboBoxLS.getSelectedItem();
        if (ls == null) return;
        MoveCardsDialog2.Result result = new MoveCardsDialog2(this, ls.getDeck()).showDialog();
        if (result.isOk) {
            if (ls.moveCards(ls.getStack(stackNames[nStackFrom]), ls.getStack(stackNames[nStackTo]), result.cardsNumber, result.order) > 0 ) {
                rebuildInterface();
            } else {
                JOptionPane.showMessageDialog(this, "No cards are moved.");
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel10 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jComboBoxLS = new javax.swing.JComboBox();
        jButtonLSRename = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButtonLSDelete = new javax.swing.JButton();
        jButtonLSAdd = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabelS0 = new javax.swing.JLabel();
        jButton54 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton01 = new javax.swing.JButton();
        jButton45 = new javax.swing.JButton();
        jButtonS3 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jLabelS5 = new javax.swing.JLabel();
        jLabelS1 = new javax.swing.JLabel();
        jButton32 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        jButtonS5 = new javax.swing.JButton();
        jButtonS0 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        jButton41 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButtonS1 = new javax.swing.JButton();
        jLabelS4 = new javax.swing.JLabel();
        jLabelS3 = new javax.swing.JLabel();
        jLabelS2 = new javax.swing.JLabel();
        jButtonS4 = new javax.swing.JButton();
        jButtonS2 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jButtonFav = new javax.swing.JButton();
        jLabelFav = new javax.swing.JLabel();
        jButtonFavClear = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jButtonExit = new javax.swing.JButton();
        jButtonFilter = new javax.swing.JButton();
        jButtonCards = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Learning path");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jComboBoxLS.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxLS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxLSActionPerformed(evt);
            }
        });

        jButtonLSRename.setText("Rename...");
        jButtonLSRename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLSRenameActionPerformed(evt);
            }
        });

        jLabel1.setText("Learning sessions: ");

        jButtonLSDelete.setText("-");
        jButtonLSDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLSDeleteActionPerformed(evt);
            }
        });

        jButtonLSAdd.setText("+");
        jButtonLSAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLSAddActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxLS, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonLSAdd)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonLSDelete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonLSRename)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxLS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jButtonLSAdd)
                    .addComponent(jButtonLSDelete)
                    .addComponent(jButtonLSRename))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabelS0.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelS0.setText("jLabel2");

        jButton54.setText("\u2193");
        jButton54.setMaximumSize(new java.awt.Dimension(40, 35));
        jButton54.setMinimumSize(new java.awt.Dimension(40, 35));
        jButton54.setPreferredSize(new java.awt.Dimension(40, 35));
        jButton54.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton54ActionPerformed(evt);
            }
        });

        jButton12.setText("\u2190");
        jButton12.setMaximumSize(new java.awt.Dimension(40, 35));
        jButton12.setMinimumSize(new java.awt.Dimension(40, 35));
        jButton12.setPreferredSize(new java.awt.Dimension(40, 35));
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton01.setText("\u2193");
        jButton01.setMaximumSize(new java.awt.Dimension(40, 35));
        jButton01.setMinimumSize(new java.awt.Dimension(40, 35));
        jButton01.setPreferredSize(new java.awt.Dimension(40, 35));
        jButton01.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton01ActionPerformed(evt);
            }
        });

        jButton45.setText("\u2191");
        jButton45.setMaximumSize(new java.awt.Dimension(40, 35));
        jButton45.setMinimumSize(new java.awt.Dimension(40, 35));
        jButton45.setPreferredSize(new java.awt.Dimension(40, 35));
        jButton45.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton45ActionPerformed(evt);
            }
        });

        jButtonS3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButtonS3.setText("Hardest");
        jButtonS3.setMaximumSize(new java.awt.Dimension(120, 100));
        jButtonS3.setMinimumSize(new java.awt.Dimension(120, 100));
        jButtonS3.setPreferredSize(new java.awt.Dimension(120, 100));
        jButtonS3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonS3ActionPerformed(evt);
            }
        });

        jButton10.setText("\u2191");
        jButton10.setMaximumSize(new java.awt.Dimension(40, 35));
        jButton10.setMinimumSize(new java.awt.Dimension(40, 35));
        jButton10.setPreferredSize(new java.awt.Dimension(40, 35));
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jLabelS5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelS5.setText("jLabel2");

        jLabelS1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelS1.setText("jLabel2");

        jButton32.setText("\u2193");
        jButton32.setMaximumSize(new java.awt.Dimension(40, 35));
        jButton32.setMinimumSize(new java.awt.Dimension(40, 35));
        jButton32.setPreferredSize(new java.awt.Dimension(40, 35));
        jButton32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton32ActionPerformed(evt);
            }
        });

        jButton21.setText("\u2192");
        jButton21.setMaximumSize(new java.awt.Dimension(40, 35));
        jButton21.setMinimumSize(new java.awt.Dimension(40, 35));
        jButton21.setPreferredSize(new java.awt.Dimension(40, 35));
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        jButtonS5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButtonS5.setText("Learned");
        jButtonS5.setMaximumSize(new java.awt.Dimension(120, 100));
        jButtonS5.setMinimumSize(new java.awt.Dimension(120, 100));
        jButtonS5.setPreferredSize(new java.awt.Dimension(120, 100));
        jButtonS5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonS5ActionPerformed(evt);
            }
        });

        jButtonS0.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jButtonS0.setText("Deck");
        jButtonS0.setMaximumSize(new java.awt.Dimension(120, 100));
        jButtonS0.setMinimumSize(new java.awt.Dimension(120, 100));
        jButtonS0.setPreferredSize(new java.awt.Dimension(120, 100));
        jButtonS0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonS0ActionPerformed(evt);
            }
        });

        jButton23.setText("\u2191");
        jButton23.setMaximumSize(new java.awt.Dimension(40, 35));
        jButton23.setMinimumSize(new java.awt.Dimension(40, 35));
        jButton23.setPreferredSize(new java.awt.Dimension(40, 35));
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });

        jButton41.setText("\u2190");
        jButton41.setMaximumSize(new java.awt.Dimension(40, 35));
        jButton41.setMinimumSize(new java.awt.Dimension(40, 35));
        jButton41.setPreferredSize(new java.awt.Dimension(40, 35));
        jButton41.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton41ActionPerformed(evt);
            }
        });

        jButton14.setText("\u2192");
        jButton14.setMaximumSize(new java.awt.Dimension(40, 35));
        jButton14.setMinimumSize(new java.awt.Dimension(40, 35));
        jButton14.setPreferredSize(new java.awt.Dimension(40, 35));
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButtonS1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButtonS1.setText("Learning");
        jButtonS1.setMaximumSize(new java.awt.Dimension(120, 100));
        jButtonS1.setMinimumSize(new java.awt.Dimension(120, 100));
        jButtonS1.setPreferredSize(new java.awt.Dimension(120, 100));
        jButtonS1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonS1ActionPerformed(evt);
            }
        });

        jLabelS4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelS4.setText("jLabel2");

        jLabelS3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelS3.setText("jLabel2");

        jLabelS2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelS2.setText("jLabel2");

        jButtonS4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButtonS4.setText("Easy");
        jButtonS4.setMaximumSize(new java.awt.Dimension(120, 100));
        jButtonS4.setMinimumSize(new java.awt.Dimension(120, 100));
        jButtonS4.setPreferredSize(new java.awt.Dimension(120, 100));
        jButtonS4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonS4ActionPerformed(evt);
            }
        });

        jButtonS2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButtonS2.setText("Hard");
        jButtonS2.setMaximumSize(new java.awt.Dimension(120, 100));
        jButtonS2.setMinimumSize(new java.awt.Dimension(120, 100));
        jButtonS2.setPreferredSize(new java.awt.Dimension(120, 100));
        jButtonS2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonS2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButtonS2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonS3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelS3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelS2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabelS1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonS1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabelS0, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton01, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jButtonS0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonS4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButtonS5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelS5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelS4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelS3)
                    .addComponent(jLabelS0)
                    .addComponent(jLabelS5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonS0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonS3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonS5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton01, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jButtonS1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jButton21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jButton41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jButtonS2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonS4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelS4)
                    .addComponent(jLabelS1)
                    .addComponent(jLabelS2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButtonFav.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButtonFav.setText("Favorities");
        jButtonFav.setMaximumSize(new java.awt.Dimension(120, 100));
        jButtonFav.setMinimumSize(new java.awt.Dimension(120, 100));
        jButtonFav.setPreferredSize(new java.awt.Dimension(120, 100));
        jButtonFav.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFavActionPerformed(evt);
            }
        });

        jLabelFav.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelFav.setText("jLabel2");

        jButtonFavClear.setText("Clear");
        jButtonFavClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFavClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jButtonFav, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonFavClear, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelFav, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelFav)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonFav, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonFavClear)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButtonExit.setText("Exit");
        jButtonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExitActionPerformed(evt);
            }
        });

        jButtonFilter.setText("Filter...");
        jButtonFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFilterActionPerformed(evt);
            }
        });

        jButtonCards.setText("Cards...");
        jButtonCards.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCardsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonExit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonCards, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonCards)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonFilter)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonExit)
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Learning path (stacks)");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        Misc.closeWindow(this);
    }//GEN-LAST:event_jButtonExitActionPerformed

    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
        moveCards(2,3);
    }//GEN-LAST:event_jButton23ActionPerformed

    private void jButtonS0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonS0ActionPerformed
        showCards(0);
    }//GEN-LAST:event_jButtonS0ActionPerformed

    private void jButtonS1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonS1ActionPerformed
        showCards(1);
    }//GEN-LAST:event_jButtonS1ActionPerformed

    private void jButtonS4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonS4ActionPerformed
        showCards(4);
    }//GEN-LAST:event_jButtonS4ActionPerformed

    private void jButtonS5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonS5ActionPerformed
        showCards(5);
    }//GEN-LAST:event_jButtonS5ActionPerformed

    private void jButtonS2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonS2ActionPerformed
        showCards(2);
    }//GEN-LAST:event_jButtonS2ActionPerformed

    private void jButtonS3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonS3ActionPerformed
        showCards(3);
    }//GEN-LAST:event_jButtonS3ActionPerformed

    private void jButton01ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton01ActionPerformed
        moveCards(0,1);
    }//GEN-LAST:event_jButton01ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        moveCards(1,0);
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        moveCards(1,4);
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton41ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton41ActionPerformed
        moveCards(4,1);
    }//GEN-LAST:event_jButton41ActionPerformed

    private void jButton45ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton45ActionPerformed
        moveCards(4,5);
    }//GEN-LAST:event_jButton45ActionPerformed

    private void jButton54ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton54ActionPerformed
        moveCards(5,4);
    }//GEN-LAST:event_jButton54ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        moveCards(1,2);
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        moveCards(2,1);
    }//GEN-LAST:event_jButton21ActionPerformed

    private void jButton32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton32ActionPerformed
        moveCards(3,2);
    }//GEN-LAST:event_jButton32ActionPerformed

    final String getDefaultLSName(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
    
    Model.Deck.LearningSession createLearningSession(String name) {
        Model.Deck.LearningSession ls = deck.createLearningSession(name);
        Model.Deck.LearningSession.Stack[] stacks = new Model.Deck.LearningSession.Stack[stackNames.length];
        try {
            for (int i=0; i < stackNames.length; i++) {
                 stacks[i] = ls.createStack(stackNames[i], false);
                if (i==6) stacks[i].setIsFavorities(true);
                
            }
            
            // ugly but works
            stacks[0].linkStack(stacks[1], false);

            stacks[1].linkStack(stacks[2], true);
            stacks[1].linkStack(stacks[4], false);
            
            stacks[2].linkStack(stacks[3], true);
            stacks[2].linkStack(stacks[1], false);
            
            stacks[3].linkStack(stacks[2], false);
            
            stacks[4].linkStack(stacks[1], true);
            stacks[4].linkStack(stacks[5], false);

            stacks[5].linkStack(stacks[4], true);
            
            ls.updateCards(stacks[0]);
            
        } catch (Model.Exception ex) {
            Logger.getLogger(LearnDialog2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ls;
    }
    
    private void jButtonLSAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLSAddActionPerformed
        String s = JOptionPane.showInputDialog("Please input Learning Session name", getDefaultLSName());
        if ((s != null) && (s.length()>0)) {
            Model.Deck.LearningSession ls = createLearningSession(s);
            rebuildLSList();
            jComboBoxLS.setSelectedItem(ls);
        }
    }//GEN-LAST:event_jButtonLSAddActionPerformed

    private void jButtonLSDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLSDeleteActionPerformed
        Model.Deck.LearningSession ls = (Model.Deck.LearningSession) jComboBoxLS.getSelectedItem();
        
        if (ls!=null) {
            if (showConfirmDialog(this, "Are you sure to delete leaning session?", "Delete leaning session", YES_NO_OPTION) == YES_OPTION) {
                deck.deleteLearningSession(ls);
                deck.dbCommit();
                rebuildLSList();
            }
        }
    }//GEN-LAST:event_jButtonLSDeleteActionPerformed

    private void jButtonLSRenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLSRenameActionPerformed
        Model.Deck.LearningSession ls = (Model.Deck.LearningSession) jComboBoxLS.getSelectedItem();
        if (ls!=null) {
            String s = JOptionPane.showInputDialog("Please input new Learning Session name", ls.toString());
            if ((s != null) && (s.length()>0)) {
                ls.setName(s);
                rebuildLSList();
                jComboBoxLS.setSelectedItem(ls);
            }            
        }
    }//GEN-LAST:event_jButtonLSRenameActionPerformed

    private void jComboBoxLSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxLSActionPerformed
        Model.Deck.LearningSession ls = (Model.Deck.LearningSession) jComboBoxLS.getSelectedItem();
        if (ls != null) ls.updateCards(ls.getStack("Deck"));
        rebuildInterface();
    }//GEN-LAST:event_jComboBoxLSActionPerformed

    private void jButtonFavActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFavActionPerformed
        showCards(6);
    }//GEN-LAST:event_jButtonFavActionPerformed

    private void jButtonFavClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFavClearActionPerformed
        Model.Deck.LearningSession ls = (Model.Deck.LearningSession) jComboBoxLS.getSelectedItem();
        ls.getStack(stackNames[6]).clear();
//        ls.clearFavs();
        rebuildInterface();
    }//GEN-LAST:event_jButtonFavClearActionPerformed

    private void jButtonFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFilterActionPerformed
        Model.Deck.LearningSession ls = (Model.Deck.LearningSession) jComboBoxLS.getSelectedItem();
        if (ls!=null) {
            new FilterDialog(this, ls).setVisible(true);
            rebuildInterface();
        }
    }//GEN-LAST:event_jButtonFilterActionPerformed

    private void jButtonCardsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCardsActionPerformed
        Model.Deck.LearningSession ls = (Model.Deck.LearningSession) jComboBoxLS.getSelectedItem();
        if (ls!=null) {
//            new CustomStacksDialog(this, ls).setVisible(true);
            CardsDialog.Result result = new CardsDialog(this, ls, null, null).showDialog();
            rebuildInterface();
            if (result!=null) goLearn(result.resultStack, result.resultCard);
        }
    }//GEN-LAST:event_jButtonCardsActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        deck.dbCommit();
    }//GEN-LAST:event_formWindowClosing


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton01;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton32;
    private javax.swing.JButton jButton41;
    private javax.swing.JButton jButton45;
    private javax.swing.JButton jButton54;
    private javax.swing.JButton jButtonCards;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonFav;
    private javax.swing.JButton jButtonFavClear;
    private javax.swing.JButton jButtonFilter;
    private javax.swing.JButton jButtonLSAdd;
    private javax.swing.JButton jButtonLSDelete;
    private javax.swing.JButton jButtonLSRename;
    private javax.swing.JButton jButtonS0;
    private javax.swing.JButton jButtonS1;
    private javax.swing.JButton jButtonS2;
    private javax.swing.JButton jButtonS3;
    private javax.swing.JButton jButtonS4;
    private javax.swing.JButton jButtonS5;
    private javax.swing.JComboBox jComboBoxLS;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabelFav;
    private javax.swing.JLabel jLabelS0;
    private javax.swing.JLabel jLabelS1;
    private javax.swing.JLabel jLabelS2;
    private javax.swing.JLabel jLabelS3;
    private javax.swing.JLabel jLabelS4;
    private javax.swing.JLabel jLabelS5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    // End of variables declaration//GEN-END:variables
}
