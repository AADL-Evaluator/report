package org.osate.aadl.aadlevaluator.report.gui;

import fluent.gui.impl.swing.FluentTable;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.osate.aadl.aadlevaluator.report.ReportFactor;
import org.osate.aadl.aadlevaluator.report.strategy.StrategyManager;

public abstract class StrategyActionListener implements ActionListener
{
    private final Window parent;
    private final FluentTable<ReportFactor> table;
    
    public StrategyActionListener( final Window parent , final FluentTable<ReportFactor> table )
    {
        this.parent = parent;
        this.table  = table;
    }
    
    public abstract void setAllZero();

    @Override
    public void actionPerformed( ActionEvent e )
    {
        StrategyListJDialog d = new StrategyListJDialog( parent );
        d.setVisible( true );
        d.dispose();
        
        if( d.isApplied() )
        {
            setAllZero();
            
            StrategyManager.getInstance().apply(
                d.getStrategyName() , 
                table.getTabelModel().getData()
            );
            
            table.getTabelModel().fireTableDataChanged();
        }
    }
    
}