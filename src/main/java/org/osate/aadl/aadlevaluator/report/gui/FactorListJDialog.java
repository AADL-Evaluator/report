package org.osate.aadl.aadlevaluator.report.gui;

import fluent.gui.impl.swing.FluentTable;
import fluent.gui.table.CustomTableColumn;
import fluent.gui.table.FieldTableColumn;
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.jdesktop.swingx.JXHeader;
import org.osate.aadl.aadlevaluator.report.EvolutionReport;
import org.osate.aadl.aadlevaluator.report.ReportFactor;
import org.osate.aadl.aadlevaluator.report.ReportGroup;
import org.osate.aadl.aadlevaluator.report.ReportValue;

public class FactorListJDialog extends javax.swing.JDialog
{
    private boolean saved;
    private List<ReportFactor> factors;
    private FluentTable<ReportFactor> table;
    
    // Software Quality
    // Ca
    
    public FactorListJDialog( java.awt.Window parent )
    {
        super( parent );
        
        initComponents();
        init();
        
        setTitle( "Factor" );
        setSize( 800 , 600 );
        setModal( true );
        setLocationRelativeTo( parent );
    }
    
    private void init()
    {
        add( new JXHeader( 
            "Factor" , 
            "Factor" 
        ) , BorderLayout.NORTH );
        
        jScrollPane1.setViewportView( 
            table = new FluentTable<>( "factors" ) 
        );
        
        factors = new LinkedList<>();
        
        table.addColumn( new FieldTableColumn( 
            "select" , 
            "selected", 
            Boolean.class , 
            50 , 
            true
        ) );
        
        table.addColumn( new FieldTableColumn( "name" , "name" ) );
        table.addColumn( new FieldTableColumn( "min"  , "min" ) );
        table.addColumn( new FieldTableColumn( "max"  , "max" ) );
        
        table.addColumn( new CustomTableColumn<ReportFactor,BigDecimal>( "property factor" ){
            @Override
            public BigDecimal getValue( ReportFactor factor ) {
                return factor.getPropertyFactor();
            }
        });
        
        table.addColumn( new FieldTableColumn( "user factor" , "userFactor" ) );
        table.setUp();
        
        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
                table.setTabelaVaziaMensagem( "No factor was created." );
                table.setTabelaVazia();
            }
        });
        
        typeJComboBox.addItemListener( new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if( e.getStateChange() == ItemEvent.SELECTED ){
                    change();
                }
            }
        });
        
        table.addMouseListener( new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if( e.getClickCount() >= 2 ){
                    open();
                }
            }
        });
        
        table.addKeyListener( new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if( e.getKeyCode() == KeyEvent.VK_ENTER ){
                    open();
                }
            }
        });
        
        strategyJButton.addActionListener( 
            new StrategyActionListener( this , table ){
                @Override
                public void setAllZero() {
                    for( ReportFactor factor : factors ){
                        factor.setUserFactor( new BigDecimal( 0 ) );
                    }
                }
            }
        );
    }
    
    private void open()
    {
        if( table.getSelectedObject() == null )
        {
            return ;
        }
        
        FactorJDialog dialog = new FactorJDialog( this );
        dialog.setFactor( table.getSelectedObject().getUserFactor() );
        dialog.setVisible( true );
        dialog.dispose();
        
        for( ReportFactor report : table.getSelectedObjects() )
        {
            report.setUserFactor( dialog.getFactor() );
        }
    }
    
    public FactorListJDialog setFactors( EvolutionReport resume )
    {
        this.factors.clear();
        
        for( ReportGroup group : resume.getGroups().values() )
        {
            setFactors( group );
        }
        
        change();
        
        return this;
    }
            
    private FactorListJDialog setFactors( ReportGroup factor )
    {
        for( ReportGroup sub : factor.getSubgroups().values() )
        {
            setFactors( sub );
        }
        
        for( ReportValue value : factor.getValues().values() )
        {
            if( value.getValue() instanceof ReportFactor 
                && value.isImportant() )
            {
                factors.add( (ReportFactor) value.getValue() );
            }
        }
        
        return this;
    }

    public FluentTable<ReportFactor> getTable() 
    {
        return table;
    }

    public boolean isSaved() 
    {
        return saved;
    }
    
    private void change()
    {
        table.setData( null );
        
        if( typeJComboBox.getSelectedIndex() == 1 )
        {
            table.addData( factors );
        }
        else
        {
            for( ReportFactor factor : factors )
            {
                if( factor.getPropertyFactor().doubleValue() != 0 )
                {
                    table.addData( factor );
                }
            }
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        saveJButton = new javax.swing.JButton();
        cancelJButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        jLabel1 = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jLabel2 = new javax.swing.JLabel();
        typeJComboBox = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        strategyJButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jToolBar1.setFloatable(false);
        jToolBar1.setToolTipText("");
        jToolBar1.add(filler1);

        saveJButton.setText("Calculate");
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

        jPanel1.setLayout(new java.awt.BorderLayout());

        jToolBar2.setFloatable(false);

        jLabel1.setText("Factors");
        jToolBar2.add(jLabel1);
        jToolBar2.add(filler2);

        jLabel2.setText("show: ");
        jToolBar2.add(jLabel2);

        typeJComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No zero", "All" }));
        jToolBar2.add(typeJComboBox);

        jLabel3.setText("    ");
        jToolBar2.add(jLabel3);

        strategyJButton.setText("Strategy");
        strategyJButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        strategyJButton.setFocusable(false);
        strategyJButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        strategyJButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(strategyJButton);

        jPanel1.add(jToolBar2, java.awt.BorderLayout.PAGE_START);
        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveJButtonActionPerformed
        try
        {
            BigDecimal big = new BigDecimal( 0 );
            
            for( ReportFactor factor : factors )
            {
                big = big.add( factor.getUserFactor() );
            }
            
            if( big.intValue() > 1 )
            {
                throw new Exception( 
                    "The sum of all factor is over 1. Please, edit one or more factor to correct that." 
                );
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
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JButton saveJButton;
    private javax.swing.JButton strategyJButton;
    private javax.swing.JComboBox<String> typeJComboBox;
    // End of variables declaration//GEN-END:variables
}
