package org.osate.aadl.aadlevaluator.report.gui;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.DefaultListModel;
import org.jdesktop.swingx.JXHeader;
import org.osate.aadl.aadlevaluator.report.strategy.StrategyManager;

public class StrategyListJDialog extends javax.swing.JDialog 
{
    private boolean applied;
    
    public StrategyListJDialog( java.awt.Window parent )
    {
        super( parent );
        
        initComponents();
        init();
        
        setTitle( "Strategies" );
        setModal( true );
        setSize( 400 , 300 );
        setLocationRelativeTo( parent );
    }

    private void init()
    {
        add( new JXHeader( 
                "Strategies" , 
                "Please, select one of them strategy to apply the weight." 
            ) 
            , BorderLayout.NORTH 
        );
        
        
        DefaultListModel<String> listModel = new DefaultListModel<>();
        strategyJList.setModel( listModel );
        
        for( String name : StrategyManager.getInstance().getCalculateNames() )
        {
            listModel.addElement( name );
        }
        
        strategyJList.requestFocus();
        strategyJList.setSelectedIndex( 0 );
        
        strategyJList.addMouseListener( new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if( e.getClickCount() >= 2 ){
                    applyJButton.doClick();
                }
            }
        });
        
        strategyJList.addKeyListener( new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if( e.getKeyCode() == KeyEvent.VK_ENTER ){
                    applyJButton.doClick();
                }
                else if( e.getKeyCode() == KeyEvent.VK_ESCAPE ){
                    cancelJButton.doClick();
                }
            }
        });
    }

    public boolean isApplied()
    {
        return applied;
    }
    
    public String getStrategyName()
    {
        if( !applied 
            && strategyJList.getSelectedIndex() == -1 )
        {
            return null;
        }
        
        return strategyJList.getSelectedValue();
    }
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        strategyJList = new javax.swing.JList<>();
        jToolBar1 = new javax.swing.JToolBar();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        applyJButton = new javax.swing.JButton();
        cancelJButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        strategyJList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(strategyJList);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jToolBar1.setFloatable(false);
        jToolBar1.add(filler1);

        applyJButton.setText("Apply");
        applyJButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        applyJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyJButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(applyJButton);

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

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void applyJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyJButtonActionPerformed
        applied = true;
        setVisible( false );
    }//GEN-LAST:event_applyJButtonActionPerformed

    private void cancelJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelJButtonActionPerformed
        applied = false;
        setVisible( false );
    }//GEN-LAST:event_cancelJButtonActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applyJButton;
    private javax.swing.JButton cancelJButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JList<String> strategyJList;
    // End of variables declaration//GEN-END:variables
}
