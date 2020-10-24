package org.osate.aadl.aadlevaluator.report.strategy;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collection;
import org.osate.aadl.aadlevaluator.report.ReportFactor;

public class EqualStrategy extends Strategy
{

    public EqualStrategy() 
    {
        super( "User factor are equals" );
    }

    @Override
    public void apply( Collection<ReportFactor> factors )
    {
        BigDecimal value = new BigDecimal( 1.0 )
            .divide( new BigDecimal( factors.size() ) , MathContext.DECIMAL128 ); //1.0 / factors.size();
        
        System.out.println( "[EQUAL CALCULATE] total: " + factors.size() );
        System.out.println( "[EQUAL CALCULATE] value: " + value );
        
        for( ReportFactor factor : factors )
        {
            factor.setWeightDefined( value );
        }
    }
    
}
