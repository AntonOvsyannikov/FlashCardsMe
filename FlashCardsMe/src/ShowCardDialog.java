
import Model.Model;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import javax.sound.sampled.*;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.FormSubmitEvent;
import javax.swing.text.html.HTMLEditorKit;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Administrator
 */
public class ShowCardDialog extends javax.swing.JDialog {
    
//    enum Result { EXIT, NEXT, LEFT, RIGHT };

    /**
     * Creates new form FCShowCardDialog
     */

    boolean isPreviewMode;
    AudioCapture audioCapture = null;

// preview mode
    Model.Deck.Template template;
    List<Model.Deck.Record> records;
    int currentRecord;
    
// learn mode   
    Model.Deck.LearningSession ls;
    Model.Deck.LearningSession.Stack stack, stackLeft, stackRight, stackFav;
    boolean isFav;
    List<Model.Deck.Card> cards;
    int nCurrentCard;
    String currentData;
    
// common 
//    Model.Deck deck;
    Model.Deck.Card currentCard;
    boolean currentFace;
    List<String> sounds;
    
//    Result result;
    
    void afterInitComponents() {
        
        Misc.centerWindow(this);        

        getRootPane().setDefaultButton(jButtonNext); 
        Misc.assignESCforClose(this);
        getRootPane().registerKeyboardAction(new ActionListener(){public void actionPerformed(ActionEvent ae){jButtonPrev.doClick();}},KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE,0),JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
    // Creates dialog to show cards and perform learning session
    public ShowCardDialog(java.awt.Dialog parent, Model.Deck.LearningSession.Stack stack, Model.Deck.Card card) {
        super(parent, true);
        if( stack==null) throw new IllegalArgumentException();

        
        initComponents();
        afterInitComponents();
    
        
        HTMLEditorKit kit = (HTMLEditorKit)jEditorPane.getEditorKit();
        kit.setAutoFormSubmission(false);
        jEditorPane.addHyperlinkListener(new HyperlinkListener() {                           
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e instanceof FormSubmitEvent) {
//                    JOptionPane.showMessageDialog(FCShowCardDialog.this, ((FormSubmitEvent)e).getData());
                    currentData = ((FormSubmitEvent)e).getData();
                    try {
                        currentData = URLDecoder.decode(currentData, "utf-8");
                    } catch (UnsupportedEncodingException ex) {
                        Logger.getLogger(ShowCardDialog.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    jButtonNextActionPerformed(null);    
                }
            }
        });

        if (!initData(stack,card)) throw new IllegalArgumentException();
        
        rebuildInterface();
        
//        jButtonLeft.setBackground(Color.RED);
//        jButtonLeft.setOpaque(true);

/*        FCModel.FCDeck.FCLearningSession ls = (FCModel.FCDeck.FCLearningSession) jComboBoxLS.getSelectedItem();
        boolean showLeft = (nStackLeft>=0);
        boolean showRight = (nStackRight>=0);
        
        List<FCModel.FCDeck.FCCard> cards = ls.listCards(nStack);
        for (FCModel.FCDeck.FCCard card : cards) {
            FCShowCardDialog.Result result;
            result = new FCShowCardDialog(this, card.record, card.template, true, showLeft, showRight).showDialog();
            if ( result == FCShowCardDialog.Result.EXIT) break;
            if ( result == FCShowCardDialog.Result.LEFT ) { ls.moveCard(nStack, nStackLeft, card); rebuildInterface(); }
            if ( result == FCShowCardDialog.Result.RIGHT ) { ls.moveCard(nStack, nStackRight, card); rebuildInterface(); }
        }    */
        
    
    }
    
    boolean initData(Model.Deck.LearningSession.Stack stack, Model.Deck.Card card) {
        this.isPreviewMode = false;

        this.ls = stack.getLearningSession();
        this.stack = stack;
        this.stackLeft = stack.getLinked(true);
        this.stackRight = stack.getLinked(false);
        this.stackFav = ls.getStack("Favorities"); // TODO super ugly!!! 
        this.isFav = stack.isFavorities();
        
        this.currentData = null;

        cards = stack.listVisibleCards();
        if (cards.isEmpty()) return false; 
        if (card!= null && stack.contains(card))
            nCurrentCard = cards.indexOf(card);
        else
            nCurrentCard = 0;
        
        currentCard = cards.get(nCurrentCard);
        currentFace = true;
        
        return true;
    }
    
        // Creates dialog to preview template
    public ShowCardDialog(java.awt.Dialog parent, Model.Deck.Template template, boolean isFront) {
        super(parent, true);
        this.isPreviewMode = true;

        records = template.getDeck().listRecords();
        if (records.size() == 0) throw new IllegalArgumentException();
        currentRecord = 0;
        
        this.template = template;
        currentCard = template.getDeck().new Card(records.get(currentRecord), template);
        currentFace = isFront;
        
        initComponents();
        afterInitComponents();
        
        jButtonLeft.setEnabled(false);
        jButtonRight.setEnabled(false);
        jButtonShuffle.setEnabled(false);
        jButtonFav.setEnabled(false);
        jButtonCards.setEnabled(false);
        
        rebuildInterface();
            
    }
    
    boolean compareTyped(String fieldName, String fieldValue){
        String s="";
        
        if (currentData == null)
            s="";
        else {
            Pattern p = Pattern.compile(fieldName+"=(.*)$|&");
            Matcher m=p.matcher(currentData);
            if (!m.matches()) 
                s="";
            else
                s=m.group(1);
        }
       
        return s.equals(fieldValue);
        
    }
    String compareAndFormatTyped(String fieldName, String fieldValue){
//        if (currentData)
//        return "[compare typed " + fieldName + "]";
        String s="";
        
        if (currentData == null)
            s="";
        else {
            Pattern p = Pattern.compile(fieldName+"=(.*)$|&");
            Matcher m=p.matcher(currentData);
            if (!m.matches()) 
                s="";
            else
                s=m.group(1);
        }
       
        if (s.equals(fieldValue)){
            return "<p style=\"background-color: Lime\">"+fieldValue+"</p>";
        } else {
            return "<p style=\"background-color: Red\">"+s+"</p>" + "<p style=\"background-color: Lime\">"+fieldValue+"</p>";
            
        }
       
        //return "[typed:"+s+"][value:"+fieldValue+"]";
        
    }
    
    
    void rebuildInterface(){
        jEditorPane.removeAll();
        sounds = new ArrayList<>();

        String html2 = "<html><body><center><form action=\"#\">";
        
        Model.Deck deck=currentCard.getDeck();
        List<Model.Deck.Field> fields = deck.listFields();
        Model.Deck.Template template = currentCard.template;
        Model.Deck.Record record = currentCard.record;
        
        class Booleaner {
            boolean b = false;
            void setTrue() { b = true; }
        };
        Booleaner b = new Booleaner();
        
        html2+=fields.stream()
            .sorted((f1,f2)->(Integer.compare(template.getTemplateField(currentFace, f1.getName()).order, template.getTemplateField(currentFace, f2.getName()).order)))
            .map(f -> { 
                String html = "";
                String fieldName = f.getName();
                Model.CardTemplateField ctf = template.getTemplateField(currentFace, fieldName);
                
                if (!ctf.action.equals("Hide") && record.getFieldContent(f)!=null) {
                    html += "<div style=\"font-size: "+ctf.fontSize+"; color: "+ctf.color+"\">";

                    switch (ctf.action) {
                        case "Text": 
                            html += record.getFieldContent(f);
                            break;
                        case "Image":
                            html += "<img src=\"file:/"+deck.getDeckMediaDirectory()+ record.getFieldContent(f) + "\">";
                            break;
                        case "Sound":
                            String[] sparts = record.getFieldContent(f).split(",");
                            for (int i=0; i<sparts.length; i++) sounds.add(deck.getDeckMediaDirectory() + sparts[i]);
                            break;
                        case "Type":
                            html += ctf.prompt+"<input name=\"" + fieldName + "\" type=\"text\" />";
                            break;
                        case "Compare":
                            if (compareTyped(fieldName,record.getFieldContent(f)))
                                b.setTrue();
                            html += compareAndFormatTyped(fieldName,record.getFieldContent(f));
                            break;
                    }
                    html += "</div>";
                }
                return html; 
            })
            .collect(joining());        
        html2+="</form></body></html>";
        jEditorPane.setText(html2);
        if (!currentFace) currentData = null;
        
        if (currentFace) {
            jEditorPane.requestFocusInWindow();
        } else
            jButtonNext.requestFocus();
        
        jButtonPlay.setEnabled(sounds.size()!=0);
        jButtonPlayActionPerformed(null);
//        if(jButtonPlay.isEnabled()) jButtonPlay.doClick();
//        dispatchEvent(new ActionEvent(jButtonPlay,0,""));

        if (!isPreviewMode) {

            if (stackLeft != null) jButtonLeft.setText(stackLeft.getName() + " \u2190"); else { jButtonLeft.setText(" ");jButtonLeft.setEnabled(false); }
            if (stackRight != null) jButtonRight.setText("\u2192 " + stackRight.getName()); else { jButtonRight.setText(" "); jButtonRight.setEnabled(false); }
            
            if (!isFav) {

                if (currentFace) ls.statCardShown(currentCard);
                else ls.statCardAnswered(currentCard, b.b);

                int sc = ls.statGetShowsCount(currentCard, true);
                int scs = ls.statGetShowsCount(currentCard, false);
                int scnt = ls.statGetSuccessCounter(currentCard);
                
                setTitle(
                    "\"" + stack.getName() + "\" stack." +
                    " Card #" + Integer.toString(stack.getOrder(currentCard)+1) + " of " + Integer.toString(stack.getCardsNumber()) +
                    " [" + Integer.toString(sc) + " shows total, " + Integer.toString(scs) + " in this stack]" +
                    " [" + Integer.toString(scnt) + " success counter]"
                );

                if (scnt <= -20) 
                    jButtonLeft.setBackground(Color.red); 
                else if (scnt <= -10) 
                    jButtonLeft.setBackground(Color.yellow); 
                else jButtonLeft.setBackground(null);

                if (scnt >= 6) 
                    jButtonRight.setBackground(Color.green); 
                else if (scnt >= 3) 
                    jButtonRight.setBackground(Color.yellow); 
                else jButtonRight.setBackground(null);
            } else {
                setTitle(
                    "\"" + stack.getName() + "\" stack." +
                    " Card #" + Integer.toString(stack.getOrder(currentCard)+1) + " of " + Integer.toString(stack.getCardsNumber())
                );
            }
            repaintFavButton();
        }
    }
    
    void repaintFavButton(){
        jButtonFav.setBackground(stackFav.contains(currentCard)?Color.yellow:null); 
    }
    
    
      
 /*
    void addBut(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        jPanel.add(button);
    }
*/    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane = new javax.swing.JEditorPane();
        jPanel4 = new javax.swing.JPanel();
        jButtonRight = new javax.swing.JButton();
        jButtonPlay = new javax.swing.JButton();
        jButtonLeft = new javax.swing.JButton();
        jButtonPrev = new javax.swing.JButton();
        jButtonNext = new javax.swing.JButton();
        jButtonExit = new javax.swing.JButton();
        jButtonRec = new javax.swing.JButton();
        jButtonFav = new javax.swing.JButton();
        jButtonShuffle = new javax.swing.JButton();
        jButtonCards = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jEditorPane.setEditable(false);
        jEditorPane.setContentType("text/html"); // NOI18N
        jEditorPane.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jEditorPaneFocusGained(evt);
            }
        });
        jScrollPane1.setViewportView(jEditorPane);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButtonRight.setMnemonic('3');
        jButtonRight.setText("->");
        jButtonRight.setToolTipText("Alt-3");
        jButtonRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRightActionPerformed(evt);
            }
        });

        jButtonPlay.setMnemonic('p');
        jButtonPlay.setText("Play");
        jButtonPlay.setToolTipText("Alt-P");
        jButtonPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPlayActionPerformed(evt);
            }
        });

        jButtonLeft.setMnemonic('1');
        jButtonLeft.setText("<-");
        jButtonLeft.setToolTipText("Alt-1");
        jButtonLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLeftActionPerformed(evt);
            }
        });

        jButtonPrev.setMnemonic(',');
        jButtonPrev.setText("< Prev");
        jButtonPrev.setToolTipText("Alt-<");
        jButtonPrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrevActionPerformed(evt);
            }
        });

        jButtonNext.setMnemonic('.');
        jButtonNext.setText("Next >");
        jButtonNext.setToolTipText("Alt->");
        jButtonNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextActionPerformed(evt);
            }
        });

        jButtonExit.setMnemonic('l');
        jButtonExit.setText("Exit");
        jButtonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExitActionPerformed(evt);
            }
        });

        jButtonRec.setMnemonic('r');
        jButtonRec.setText("Rec");
        jButtonRec.setToolTipText("Alt-R");
        jButtonRec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRecActionPerformed(evt);
            }
        });

        jButtonFav.setMnemonic('8');
        jButtonFav.setText("*");
        jButtonFav.setToolTipText("Alt-*");
        jButtonFav.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFavActionPerformed(evt);
            }
        });

        jButtonShuffle.setMnemonic('s');
        jButtonShuffle.setText("Shuffle");
        jButtonShuffle.setToolTipText("Alt-S");
        jButtonShuffle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonShuffleActionPerformed(evt);
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
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jButtonPrev, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonLeft, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonCards, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonRight, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonNext, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jButtonPlay, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonRec, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonShuffle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonFav, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonPrev)
                    .addComponent(jButtonLeft)
                    .addComponent(jButtonRight)
                    .addComponent(jButtonNext)
                    .addComponent(jButtonCards))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonPlay)
                    .addComponent(jButtonShuffle)
                    .addComponent(jButtonRec)
                    .addComponent(jButtonExit)
                    .addComponent(jButtonFav))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    
    private void jEditorPaneFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jEditorPaneFocusGained
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.focusNextComponent();
    }//GEN-LAST:event_jEditorPaneFocusGained

    void nextCard() {
        if (isPreviewMode) throw new IllegalArgumentException();
        if (nCurrentCard >= cards.size()) nCurrentCard = 0;
        currentCard = cards.get(nCurrentCard);
        currentFace = true;
        rebuildInterface();
    }
    
    private void jButtonPrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrevActionPerformed
        if (isPreviewMode) {
            currentRecord--;
            if (currentRecord<0) currentRecord = records.size()-1;
            currentCard = template.getDeck().new Card(records.get(currentRecord), currentCard.template);
        } else {
            if (!currentFace) currentFace = true; else {
                nCurrentCard--;
                if (nCurrentCard < 0) nCurrentCard =  cards.size()-1;
                currentCard = cards.get(nCurrentCard);
                currentFace = true;
            }
        }
        rebuildInterface();
    }//GEN-LAST:event_jButtonPrevActionPerformed

    void moveCard(Model.Deck.LearningSession.Stack stackTo) {
        if (isPreviewMode) throw new IllegalArgumentException();
        if (isFav || stackTo.isFavorities()) throw new IllegalArgumentException();

        stack.removeCard(currentCard);
        cards.remove(nCurrentCard);
        stackTo.addCard(currentCard, true);
        
        ls.statCardMoved(currentCard, stack, stackTo);
        
        if (cards.size() == 0) Misc.closeWindow(this); else nextCard();        
    }
    
    private void jButtonLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLeftActionPerformed
        moveCard(stackLeft);
    }//GEN-LAST:event_jButtonLeftActionPerformed

    private void jButtonRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRightActionPerformed
        moveCard(stackRight);
    }//GEN-LAST:event_jButtonRightActionPerformed

    private void jButtonNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextActionPerformed
        if (isPreviewMode) {
            currentRecord++;
            if (currentRecord>=records.size()) currentRecord = 0;
            currentCard = template.getDeck().new Card(records.get(currentRecord), currentCard.template);
        } else {
            if (currentFace) currentFace = false; else {
                nCurrentCard++;
                if (nCurrentCard >= cards.size()) nCurrentCard = 0;
                currentCard = cards.get(nCurrentCard);
                currentFace = true;
            }
        }
        rebuildInterface();
    }//GEN-LAST:event_jButtonNextActionPerformed

    private void jButtonFavActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFavActionPerformed
        if (stackFav.contains(currentCard)) {
            stackFav.removeCard(currentCard);
            if (isFav) {
                cards.remove(nCurrentCard);
                if (cards.size() == 0) Misc.closeWindow(this); else nextCard();
            }
        } else stackFav.addCard(currentCard, true);
        repaintFavButton();
    }//GEN-LAST:event_jButtonFavActionPerformed

    private void jButtonPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPlayActionPerformed

        SwingWorker<Void,Void> task = new SwingWorker<Void,Void>(){
            protected Void doInBackground() {
                for (String sound : sounds){
                    try {
                        File file = new File(sound);
                        if (file.canRead()) {
                            AudioInputStream inputStream = AudioSystem.getAudioInputStream(file);
                            Clip clip = AudioSystem.getClip();
                            clip.open(inputStream);
                            clip.start();

                            while (clip.getMicrosecondLength() != clip.getMicrosecondPosition()) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException ex) {
                                }
                            }
                        }
                    } catch (LineUnavailableException ex) {
                        Logger.getLogger(ShowCardDialog.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(ShowCardDialog.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (UnsupportedAudioFileException ex) {
                        Logger.getLogger(ShowCardDialog.class.getName()).log(Level.SEVERE, null, ex);
                    } 
                }
                return null;
            }
        };
        task.execute();
        
    }//GEN-LAST:event_jButtonPlayActionPerformed

    private void jButtonShuffleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonShuffleActionPerformed
        stack.shuffle();
        cards = stack.listVisibleCards();
        currentCard = cards.get(nCurrentCard);
        currentFace = true;
        rebuildInterface();
    }//GEN-LAST:event_jButtonShuffleActionPerformed

    private void jButtonRecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRecActionPerformed
        if (audioCapture == null) {
            audioCapture = new AudioCapture();
            audioCapture.rec();
            jButtonRec.setText("Stop");
        } else {
            audioCapture.play();
            jButtonRec.setText("Rec");
            audioCapture = null;
        }
    }//GEN-LAST:event_jButtonRecActionPerformed

    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        Misc.closeWindow(this);
    }//GEN-LAST:event_jButtonExitActionPerformed

    private void jButtonCardsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCardsActionPerformed
        Model.Deck.LearningSession.Stack _stack = null;
        Model.Deck.Card _card = null        ;
        
        CardsDialog.Result result = new CardsDialog(this, ls, stack, currentCard).showDialog();
        if (result == null) {
            _stack = stack;
            _card = currentCard;
        } else {
            _stack = result.resultStack;
            _card = result.resultCard;
        }
        if (initData(_stack, _card))
            rebuildInterface();
        else
            Misc.closeWindow(this);
    }//GEN-LAST:event_jButtonCardsActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (!isPreviewMode) ls.getDeck().dbCommit();
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCards;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonFav;
    private javax.swing.JButton jButtonLeft;
    private javax.swing.JButton jButtonNext;
    private javax.swing.JButton jButtonPlay;
    private javax.swing.JButton jButtonPrev;
    private javax.swing.JButton jButtonRec;
    private javax.swing.JButton jButtonRight;
    private javax.swing.JButton jButtonShuffle;
    private javax.swing.JEditorPane jEditorPane;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
