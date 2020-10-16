package org.osate.aadl.aadlevaluator.report.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import org.osate.aadl.aadlevaluator.report.ReportFactor;

public class MoreIsLessStrategy extends Strategy
{

    public MoreIsLessStrategy()
    {
        super( "More Property Factor is less User Factor" );
    }
    
    @Override
    public void apply( Collection<ReportFactor> factors )
    {
        BigDecimal total = new BigDecimal( 0 );
        
        for( ReportFactor factor : factors )
        {
            total = total.add( factor.getPropertyFactor() );
        }
        
        if( total.doubleValue() == 0 )
        {
            return ;
        }
        
        System.out.println( "[MORE IS LESS] total: " + total );
        
        for( ReportFactor factor : factors )
        {
            // 1.0 - (factor.getPropertyFactor() / total)
            factor.setUserFactor(
                new BigDecimal( 1.0 ).subtract(
                    factor.getPropertyFactor().divide( total , RoundingMode.HALF_UP )
                )
            );
            
            System.out.println( "[MORE IS LESS] name.: " 
                + factor.getName() 
                + " | user factor: " 
                + factor.getUserFactor()
            );
        }
    }
    
}
