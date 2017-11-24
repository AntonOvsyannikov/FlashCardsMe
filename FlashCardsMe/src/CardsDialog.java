
import Model.Model;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.of;
import static java.util.stream.Stream.concat;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.NO_OPTION;
import static javax.swing.JOptionPane.OK_OPTION;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showInputDialog;
import static javax.swing.JOptionPane.showMessageDialog;
import static javax.swing.JOptionPane.showOptionDialog;
import javax.swing.KeyStroke;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Administrator
 */
public class CardsDialog extends javax.swing.JDialog {
    

    /**
     * Creates new form ShowStackDialog
     */
    Model.Deck.LearningSession ls;
    boolean updating = false;
    List<Model.Deck.Card> cards = null;
    
    class Result {
        public Model.Deck.Card resultCard;
        public Model.Deck.LearningSession.Stack resultStack;
    };
    
    Result result = null;
    Result showDialog() { setVisible(true); return result; }
    
    public CardsDialog(JDialog parent, Model.Deck.LearningSession ls, Model.Deck.LearningSession.Stack stack, Model.Deck.Card card) {
        super(parent, true);
        this.ls = ls;
        
        initComponents();
        jTableCards.setDefaultEditor(Object.class, null);


        
        
        Misc.centerWindow(this);        

        Misc.assignESCforClose(this);

        class DefCellRenderer extends DefaultListCellRenderer {
            Font fontBold = null;
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Model.Deck.LearningSession.Stack stack = (Model.Deck.LearningSession.Stack) value;
                String s = "";
                
                s+=stack.getName();
                s+=" ["+Integer.toString(stack.getVisibleCardsNumber())+" cards]";
                setText(s);
                
                if (fontBold == null) fontBold = new Font(getFont().getFontName(), Font.BOLD, getFont().getSize());
//                if (stack.isCustom()) setFont(new Font(getFont().getFontName(), Font.ITALIC, getFont().getSize()));                
                if (!stack.isCustom()) setFont(fontBold);                
                
                return this;
            }
        };
        jComboBoxStacks.setRenderer(new DefCellRenderer());

        rebuildStackList();
        if (stack != null) jComboBoxStacks.setSelectedItem(stack);
        rebuildCardsTable(card);
    }
    
    void rebuildStackList() {
        updating = true;
        jComboBoxStacks.removeAllItems();
        ls.listStacks().forEach(s->jComboBoxStacks.addItem(s));
        updating = false;
    
    }
    
    void rebuildCardsTable(Model.Deck.Card selectedCard) {
        Model.Deck.LearningSession.Stack stack = (Model.Deck.LearningSession.Stack) jComboBoxStacks.getSelectedItem();
        
        DefaultTableModel t = (DefaultTableModel)jTableCards.getModel();
        t.setRowCount(0);
        t.setColumnCount(0);
        
        t.addColumn("Template");

        List<Model.Deck.Field> fields = stack.getLearningSession().getDeck().listVisibleFields();
        fields.stream().forEachOrdered(f -> t.addColumn(f.getName()));
/*        stack.listVisibleCards().forEach( c -> t.addRow(new Vector<>(
                fields.stream()
                    .map(f->c.record.getFieldContent(f))
                    .collect(toList())
        )));*/
        cards = stack.listVisibleCards();
        
        int selectedCardIndex = -1;
        for (int i = 0; i < cards.size(); i++) {
            Model.Deck.Card card = cards.get(i);
            if (card.equals(selectedCard)) selectedCardIndex = i;
            t.addRow(new Vector<>(
                concat(
                    of(card.template.getName()), 
                    fields.stream().map(f->card.record.getFieldContent(f))
                ).collect(toList())
            ));
        }
        if (selectedCardIndex != -1)
            jTableCards.setRowSelectionInterval(selectedCardIndex, selectedCardIndex);
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableCards = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jComboBoxStacks = new javax.swing.JComboBox();
        jButtonAddStack = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButtonDeleteStack = new javax.swing.JButton();
        jButtonStackStat = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jButtonMoveTo = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jButtonShowCard = new javax.swing.JButton();
        jButtonCardStat = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jButtonExit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cards");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTableCards.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTableCards);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 806, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jComboBoxStacks.setModel(new javax.swing.DefaultComboBoxModel<>( ));
        jComboBoxStacks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxStacksActionPerformed(evt);
            }
        });

        jButtonAddStack.setText("+");
        jButtonAddStack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddStackActionPerformed(evt);
            }
        });

        jLabel1.setText("Stack:");

        jButtonDeleteStack.setText("-");
        jButtonDeleteStack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteStackActionPerformed(evt);
            }
        });

        jButtonStackStat.setText("Stat...");
        jButtonStackStat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStackStatActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBoxStacks, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonAddStack)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonDeleteStack)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonStackStat)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxStacks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jButtonAddStack)
                    .addComponent(jButtonDeleteStack)
                    .addComponent(jButtonStackStat))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButtonMoveTo.setText("Move to...");
        jButtonMoveTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMoveToActionPerformed(evt);
            }
        });

        jLabel2.setText("Card(s):");

        jButtonShowCard.setText("Show");
        jButtonShowCard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonShowCardActionPerformed(evt);
            }
        });

        jButtonCardStat.setText("Stat...");
        jButtonCardStat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCardStatActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonMoveTo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonShowCard)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonCardStat)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonMoveTo)
                    .addComponent(jLabel2)
                    .addComponent(jButtonShowCard)
                    .addComponent(jButtonCardStat))
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButtonExit.setText("Exit");
        jButtonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonExit)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonExit)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        Misc.closeWindow(this);
    }//GEN-LAST:event_jButtonExitActionPerformed

    private void jButtonAddStackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddStackActionPerformed
        String s = showInputDialog("Please input New Stack name");
        if ((s != null) && (s.length()>0)) {
            try {
                jComboBoxStacks.addItem(ls.createStack(s, true));
                jComboBoxStacks.setSelectedIndex(jComboBoxStacks.getItemCount()-1);
            } catch (Model.Exception ex) {
                showMessageDialog(this, ex.getMessage(), "Error", WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButtonAddStackActionPerformed

    private void jButtonDeleteStackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteStackActionPerformed
        Model.Deck.LearningSession.Stack stack = (Model.Deck.LearningSession.Stack) jComboBoxStacks.getSelectedItem();

        if (stack!=null) {
            if (!stack.isCustom()){
                showMessageDialog(this, "Can not delete built-it stack", "Error", WARNING_MESSAGE);
            } else if (stack.getCardsNumber()>0){
                showMessageDialog(this, "Stack is not empty", "Error", WARNING_MESSAGE);
            } else {
                jComboBoxStacks.removeItem(stack);
                ls.deleteStack(stack);
            }
        }
    }//GEN-LAST:event_jButtonDeleteStackActionPerformed

    private void jComboBoxStacksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxStacksActionPerformed
        if (!updating) rebuildCardsTable(null);
    }//GEN-LAST:event_jComboBoxStacksActionPerformed

    private void jButtonMoveToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMoveToActionPerformed
        int[] rows = jTableCards.getSelectedRows();
        if (rows.length==0) {
            showMessageDialog(this, "Select cards first");
            return;
        }
        
        Model.Deck.LearningSession.Stack stacks[] = ls.listStacks().stream().toArray(Model.Deck.LearningSession.Stack[]::new);
        Model.Deck.LearningSession.Stack stackFrom = (Model.Deck.LearningSession.Stack) jComboBoxStacks.getSelectedItem();

        Model.Deck.LearningSession.Stack stackTo = null;
        int removeFromFav = NO_OPTION;

        
        if (!stackFrom.isFavorities())
            stackTo = (Model.Deck.LearningSession.Stack) showInputDialog(this, "Choose Stack to move card(s) to", "Choose Stack", QUESTION_MESSAGE, null, stacks, null);
        else
            removeFromFav = showConfirmDialog (this, "Remove from Favorities?", "Confirm", YES_NO_OPTION);


        if ((stackTo != null && !stackTo.equals(stackFrom)) || removeFromFav != NO_OPTION) {
            for (int i : rows) {
                Model.Deck.Card card = cards.get(i);
                if (stackFrom.isFavorities()){
                    stackFrom.removeCard(card);
                } else {
                    if (!stackTo.isFavorities()) {
                        stackFrom.removeCard(card);
                        ls.statCardMoved(card, stackFrom, stackTo);
                    } 
                    if (!stackTo.contains(card))  // prevent adding multiply cards in favorities
                        stackTo.addCard(card,true);
                }
            }
            if (stackTo.isFavorities()) showMessageDialog(this, "Added to favorities");
        }

        rebuildCardsTable(null);
        
    }//GEN-LAST:event_jButtonMoveToActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        ls.getDeck().dbCommit();
    }//GEN-LAST:event_formWindowClosing

    private void jButtonShowCardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonShowCardActionPerformed
        result = new Result();
        result.resultStack = (Model.Deck.LearningSession.Stack) jComboBoxStacks.getSelectedItem();
        int row = jTableCards.getSelectedRow();
        if (row!=-1) result.resultCard = cards.get(row);
        else{
            showMessageDialog(this, "Select card first");
            return;
        }
        Misc.closeWindow(this);
    }//GEN-LAST:event_jButtonShowCardActionPerformed

    private void jButtonStackStatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStackStatActionPerformed
        new StatStackDialog(this, (Model.Deck.LearningSession.Stack) jComboBoxStacks.getSelectedItem()).setVisible(true);
    }//GEN-LAST:event_jButtonStackStatActionPerformed

    private void jButtonCardStatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCardStatActionPerformed
        int row = jTableCards.getSelectedRow();
        if (row!=-1) {
            Model.Deck.Card card = cards.get(row);
            new StatCardDialog(this, ls, card).setVisible(true);
        } else {
            showMessageDialog(this, "Select card first");
            return;
        }
    }//GEN-LAST:event_jButtonCardStatActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddStack;
    private javax.swing.JButton jButtonCardStat;
    private javax.swing.JButton jButtonDeleteStack;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonMoveTo;
    private javax.swing.JButton jButtonShowCard;
    private javax.swing.JButton jButtonStackStat;
    private javax.swing.JComboBox jComboBoxStacks;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableCards;
    // End of variables declaration//GEN-END:variables
}
