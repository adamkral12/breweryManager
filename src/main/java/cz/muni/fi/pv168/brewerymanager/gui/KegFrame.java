package cz.muni.fi.pv168.brewerymanager.gui;

import cz.muni.fi.pv168.brewerymanager.backend.Keg;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;

/**
 *
 * @author adam
 */
public class KegFrame extends javax.swing.JFrame {

    private static final ResourceBundle texts = ResourceBundle.getBundle("gui/texts");
    private final KegTableModel kegModel;

    public KegFrame(KegTableModel kegModel) {
        initComponents();
        this.kegModel = kegModel;
    }

    private void actionCancel(){
        this.dispose();
    }
        private void actionAddKeg(){
        String brand = jTextFieldKegBrand.getText();
        String capacity = jTextFieldKegCapacity.getText();
        String price = jTextFieldKegPrice.getText();
        String deposit = jTextFieldKegDeposit.getText();
        
        if (brand.isEmpty()) {
            JOptionPane.showMessageDialog(this, texts.getString("YOU HAVE TO ENTER THE BRAND"), texts.getString("BRAND REQUIRED"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Integer.parseInt(deposit);
        } catch (NumberFormatException e){
                       JOptionPane.showMessageDialog(this, texts.getString("WRONG FORMAT OF DEPOSIT"), texts.getString("DEPOSIT REQUIRED"), JOptionPane.WARNING_MESSAGE);
            return; 
        }
        try {
            Integer.parseInt(price);
        } catch (NumberFormatException e){
                       JOptionPane.showMessageDialog(this, texts.getString("WRONG FORMAT OF PRICE"), texts.getString("DEPOSIT REQUIRED"), JOptionPane.WARNING_MESSAGE);
            return; 
        }
        
        try {
            Integer.parseInt(capacity);
        } catch (NumberFormatException e){
                       JOptionPane.showMessageDialog(this, texts.getString("WRONG FORMAT OF CAPACITY"), texts.getString("DEPOSIT REQUIRED"), JOptionPane.WARNING_MESSAGE);
            return; 
        }
        
        
        Keg keg = new Keg();
        keg.setBrand(brand);
        keg.setDeposit(Integer.parseInt(deposit));
        keg.setCapacity(Integer.parseInt(capacity));
        keg.setPrice(Integer.parseInt(price));
        
        kegModel.addRow(keg, kegModel);
        this.dispose();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelKegs = new javax.swing.JPanel();
        jTextFieldKegDeposit = new javax.swing.JTextField();
        jTextFieldKegPrice = new javax.swing.JTextField();
        jTextFieldKegCapacity = new javax.swing.JTextField();
        jTextFieldKegBrand = new javax.swing.JTextField();
        jLabelBrand = new javax.swing.JLabel();
        jLabelCapacity = new javax.swing.JLabel();
        jLabelPrice = new javax.swing.JLabel();
        jLabelDeposit = new javax.swing.JLabel();
        jButtonAddKeg = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();

        jTextFieldKegDeposit.setToolTipText(texts.getString("KEG DEPOSIT"));

        jTextFieldKegPrice.setToolTipText(texts.getString("PRICE OF KEG"));

        jTextFieldKegCapacity.setToolTipText(texts.getString("CAPACITY OF KEG"));

        jTextFieldKegBrand.setToolTipText(texts.getString("BRAND OF KEG"));
        jTextFieldKegBrand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldKegBrandActionPerformed(evt);
            }
        });

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("gui/texts_cs_CZ"); // NOI18N
        jLabelBrand.setText(bundle.getString("NAME:")); // NOI18N

        jLabelCapacity.setText(bundle.getString("CAPACITY:")); // NOI18N

        jLabelPrice.setText(bundle.getString("PRICE:")); // NOI18N

        jLabelDeposit.setText(bundle.getString("DEPOSIT:")); // NOI18N

        jButtonAddKeg.setText(bundle.getString("ADD")); // NOI18N
        jButtonAddKeg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddKegActionPerformed(evt);
            }
        });

        jButtonCancel.setText(bundle.getString("CANCEL")); // NOI18N
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelKegsLayout = new javax.swing.GroupLayout(jPanelKegs);
        jPanelKegs.setLayout(jPanelKegsLayout);
        jPanelKegsLayout.setHorizontalGroup(
            jPanelKegsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelKegsLayout.createSequentialGroup()
                .addGroup(jPanelKegsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonAddKeg, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelCapacity)
                    .addComponent(jLabelPrice)
                    .addComponent(jLabelDeposit)
                    .addComponent(jLabelBrand))
                .addGap(18, 18, 18)
                .addGroup(jPanelKegsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelKegsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jTextFieldKegBrand, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                        .addComponent(jTextFieldKegPrice)
                        .addComponent(jTextFieldKegDeposit)
                        .addComponent(jTextFieldKegCapacity))
                    .addComponent(jButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 8, Short.MAX_VALUE))
        );
        jPanelKegsLayout.setVerticalGroup(
            jPanelKegsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelKegsLayout.createSequentialGroup()
                .addGroup(jPanelKegsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelBrand)
                    .addComponent(jTextFieldKegBrand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanelKegsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelCapacity)
                    .addComponent(jTextFieldKegCapacity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelKegsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelPrice)
                    .addComponent(jTextFieldKegPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelKegsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldKegDeposit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelDeposit))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addGroup(jPanelKegsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCancel)
                    .addComponent(jButtonAddKeg))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelKegs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelKegs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonAddKegActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddKegActionPerformed
        actionAddKeg();
    }//GEN-LAST:event_jButtonAddKegActionPerformed

    private void jTextFieldKegBrandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldKegBrandActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldKegBrandActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        actionCancel();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    /*
     public static void main(String args[]) {

     try {
     for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
     if ("Nimbus".equals(info.getName())) {
     javax.swing.UIManager.setLookAndFeel(info.getClassName());
     break;
     }
     }
     } catch (ClassNotFoundException ex) {
     java.util.logging.Logger.getLogger(KegFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
     } catch (InstantiationException ex) {
     java.util.logging.Logger.getLogger(KegFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
     } catch (IllegalAccessException ex) {
     java.util.logging.Logger.getLogger(KegFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
     } catch (javax.swing.UnsupportedLookAndFeelException ex) {
     java.util.logging.Logger.getLogger(KegFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
     }
     //</editor-fold>


     java.awt.EventQueue.invokeLater(new Runnable() {
     public void run() {
     new KegFrame().setVisible(true);
     }
     });
     }
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddKeg;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JLabel jLabelBrand;
    private javax.swing.JLabel jLabelCapacity;
    private javax.swing.JLabel jLabelDeposit;
    private javax.swing.JLabel jLabelPrice;
    private javax.swing.JPanel jPanelKegs;
    private javax.swing.JTextField jTextFieldKegBrand;
    private javax.swing.JTextField jTextFieldKegCapacity;
    private javax.swing.JTextField jTextFieldKegDeposit;
    private javax.swing.JTextField jTextFieldKegPrice;
    // End of variables declaration//GEN-END:variables
}