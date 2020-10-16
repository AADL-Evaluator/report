package org.osate.aadl.aadlevaluator.report.gui;

import fluent.gui.impl.swing.document.MoneyPlainDocument;
import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import org.jdesktop.swingx.JXHeader;

public class FactorJDialog extends javax.swing.JDialog 
{
    private boolean saved;
    
    public FactorJDialog( java.awt.Window parent )
    {
        super( parent );
        
        initComponents();
        init();
        
        setTitle( "User Factor" );
        setModal( true );
        setLocationRelativeTo( parent );
    }
    
    private void init()
    {
        add( new JXHeader( 
            "User Factor" , 
            "Please, you should set a value between 0 (no importance) and 1 (very importance)." 
        ) , BorderLayout.NORTH );
        
        factorJTextField.requestFocus();
        factorJTextField.setDocument( new MoneyPlainDocument() );
        factorJTextField.addKeyListener( new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if( e.getKeyCode() == KeyEvent.VK_ENTER ){
                    saveJButton.doClick();
                }
                else if( e.getKeyCode() == KeyEvent.VK_ESCAPE ){
                    cancelJButton.doClick();
                }
            }
        });
    }

    public void setFactor( BigDecimal factor )
    {
        this.factorJTextField.setText( factor + "" );
    }
    
    public BigDecimal getFactor()
    {
        return new BigDecimal( 
            factorJTextField.getText()
                .replaceAll( Pattern.quote( "." ) , "" ) 
                .replace( ',' , '.' ) 
        );
    }
    
    public boolean isSaved() 
    {
        return saved;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        saveJButton = new javax.swing.JButton();
        cancelJButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        factorJTextField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jToolBar1.setFloatable(false);
        jToolBar1.add(filler1);

        saveJButton.setText("Save");
        saveJButton.setFocusable(false);
        saveJButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveJButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveJButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(saveJButton);

        cancelJButton.setText("Cancel");
        cancelJButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cancelJButton.setFocusable(false);
        cancelJButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cancelJButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cancelJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelJButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(cancelJButton);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.PAGE_END);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(factorJTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(factorJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(80, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveJButtonActionPerformed
        try
        {
            BigDecimal factor = getFactor();
            
            if( factor.doubleValue() > 1 )
            {
                throw new Exception( "The factor should be between 0 and 1." );
            }
            
            saved = true;
            setVisible( false );
        }
        catch( Exception err )
        {
            JOptionPane.showMessageDialog( 
                rootPane , 
                err.getMessage()  , 
                "Error" , 
                JOptionPane.ERROR_MESSAGE 
            );
        }
    }//GEN-LAST:event_saveJButtonActionPerformed

    private void cancelJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelJButtonActionPerformed
        saved = false;
        setVisible( false );
    }//GEN-LAST:event_cancelJButtonActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelJButton;
    private javax.swing.JTextField factorJTextField;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton saveJButton;
    // End of variables declaration//GEN-END:variables
}
