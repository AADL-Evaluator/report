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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.jdesktop.swingx.JXHeader;
import org.osate.aadl.aadlevaluator.report.EvolutionReport;
import org.osate.aadl.aadlevaluator.report.ReportFactor;
import org.osate.aadl.aadlevaluator.report.ReportGroup;
import org.osate.aadl.aadlevaluator.report.ReportValue;

public abstract class FactorListJPanel extends javax.swing.JPanel 
{
    private EvolutionReport resume;
    private List<ReportFactor> factors;
    private FluentTable<ReportFactor> table;
    
    public FactorListJPanel()
    {
        initComponents();
        init();
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
        
        table.addColumn( new FieldTableColumn( "characteristic"    , "characteristic"    ) );
        table.addColumn( new FieldTableColumn( "subcharacteristic" , "subcharacteristic" ) );
        table.addColumn( new FieldTableColumn( "name" , "name" ) );
        
        table.addColumn( new CustomTableColumn<ReportFactor,String>( "min" , 50 ){
            @Override
            public String getValue( ReportFactor factor ) {
                return factor.getMinUnit();
            }
        });
        
        table.addColumn( new CustomTableColumn<ReportFactor,String>( "max" , 50 ){
            @Override
            public String getValue( ReportFactor factor ) {
                return factor.getMaxUnit();
            }
        });
        
        table.addColumn( new CustomTableColumn<ReportFactor,BigDecimal>( "weight calculated" , 50 ){
            @Override
            public BigDecimal getValue( ReportFactor factor ) {
                return factor.getPropertyFactor();
            }
        });
        
        table.addColumn( new FieldTableColumn( "weight defined" , "userFactor" , 50 ) );
        table.setUp();
        
        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
                table.setTabelaVaziaMensagem( "No factor was created." );
                table.setTabelaVazia();
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
        
        strategyJButton.addActionListener( new StrategyActionListener( 
            SwingUtilities.getWindowAncestor( this ) ,
            table
        ){
            @Override
            public void setAllZero() {
                for( ReportFactor factor : factors ){
                    factor.setUserFactor( new BigDecimal( 0 ) );
                }
            }
        });
        
        // ---------- //
        // ---------- // filter
        // ---------- //
        
        weightJComboBox.addItemListener( new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if( e.getStateChange() == ItemEvent.SELECTED ){
                    change();
                }
            }
        });
        
        containsJTextField.addKeyListener( new KeyAdapter() {
            @Override
            public void keyReleased( KeyEvent e ) {
                change();
            }
        });
    }
    
    private void open()
    {
        if( table.getSelectedObject() == null )
        {
            return ;
        }
        
        FactorJDialog dialog = new FactorJDialog( SwingUtilities.getWindowAncestor( this ) );
        dialog.setFactor( table.getSelectedObject().getUserFactor() );
        dialog.setVisible( true );
        dialog.dispose();
        
        for( ReportFactor report : table.getSelectedObjects() )
        {
            report.setUserFactor( dialog.getFactor() );
        }
    }
    
    public FactorListJPanel setFactors( EvolutionReport resume )
    {
        this.resume = resume;
        this.factors.clear();
        
        for( ReportGroup group : resume.getGroups().values() )
        {
            setFactors( group );
        }
        
        change();
        
        return this;
    }
            
    private FactorListJPanel setFactors( ReportGroup factor )
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
    
    private void change()
    {
        table.setData( null );
        
        final String filter = containsJTextField.getText().trim().toUpperCase();
        
        if( weightJComboBox.getSelectedIndex() == 1
            && filter.isEmpty() )
        {
            table.addData( factors );
        }
        else
        {
            for( ReportFactor factor : factors )
            {
                if( isEligible( factor , filter ) )
                {
                    table.addData( factor );
                }
            }
        }
    }
    
    private boolean isEligible( final ReportFactor factor , final String filter )
    {
        return (weightJComboBox.getSelectedIndex() == 1 || factor.getPropertyFactor().doubleValue() != 0)
            && (filter.isEmpty() || factor.getName().toUpperCase().contains( filter ) );
    }
    
    public abstract void save( final EvolutionReport resume , final Collection<ReportFactor> factors );
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar2 = new javax.swing.JToolBar();
        jLabel1 = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jLabel4 = new javax.swing.JLabel();
        containsJTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        weightJComboBox = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        strategyJButton = new javax.swing.JButton();
        saveJButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();

        setLayout(new java.awt.BorderLayout());

        jToolBar2.setFloatable(false);

        jLabel1.setText("Factors");
        jToolBar2.add(jLabel1);
        jToolBar2.add(filler2);

        jLabel4.setText("Contains:  ");
        jToolBar2.add(jLabel4);

        containsJTextField.setMaximumSize(new java.awt.Dimension(150, 25));
        containsJTextField.setMinimumSize(new java.awt.Dimension(150, 25));
        containsJTextField.setPreferredSize(new java.awt.Dimension(150, 25));
        jToolBar2.add(containsJTextField);

        jLabel2.setText("   Weight: ");
        jToolBar2.add(jLabel2);

        weightJComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No zero", "All" }));
        jToolBar2.add(weightJComboBox);

        jLabel3.setText("    ");
        jToolBar2.add(jLabel3);

        strategyJButton.setText("Strategy");
        strategyJButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        strategyJButton.setFocusable(false);
        strategyJButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        strategyJButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(strategyJButton);

        saveJButton.setText("Calculate");
        saveJButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        saveJButton.setFocusable(false);
        saveJButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveJButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveJButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(saveJButton);

        add(jToolBar2, java.awt.BorderLayout.PAGE_START);
        add(jScrollPane1, java.awt.BorderLayout.CENTER);
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

            save( resume , factors );
        }
        catch( Exception err )
        {
            JOptionPane.showMessageDialog(
                this ,
                err.getMessage()  ,
                "Error" ,
                JOptionPane.ERROR_MESSAGE
            );
        }
    }//GEN-LAST:event_saveJButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField containsJTextField;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JButton saveJButton;
    private javax.swing.JButton strategyJButton;
    private javax.swing.JComboBox<String> weightJComboBox;
    // End of variables declaration//GEN-END:variables
}
