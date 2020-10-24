package org.osate.aadl.aadlevaluator.report.strategy;

import java.math.BigDecimal;
import java.math.MathContext;
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
        BigDecimal total = BigDecimal.ZERO;
        
        for( ReportFactor factor : factors )
        {
            total = total.add( factor.getWeightCalculated() );
        }
        
        if( total.doubleValue() == 0 )
        {
            return ;
        }
        
        System.out.println( "[MORE IS LESS] total: " + total );
        
        for( ReportFactor factor : factors )
        {
            // 1.0 - (factor.getPropertyFactor() / total)
            factor.setWeightDefined(
                new BigDecimal( 1.0 ).subtract(
                    factor.getWeightCalculated().divide( total , MathContext.DECIMAL128 )
                )
            );
            
            System.out.println( "[MORE IS LESS] title: " 
                + factor.getTitle()
                + " | user factor: " 
                + factor.getWeightDefined()
            );
        }
    }
    
}
