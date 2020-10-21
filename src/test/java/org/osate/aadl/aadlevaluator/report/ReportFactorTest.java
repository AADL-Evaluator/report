package org.osate.aadl.aadlevaluator.report;

import java.math.BigDecimal;
import org.junit.Test;
import org.osate.aadl.ReportFactorOld;

public class ReportFactorTest 
{
    
    @Test
    public void weightCalculated()
    {
        ReportFactor factor = new ReportFactor();
        factor.setMin( new BigDecimal( 0.1 ) );
        factor.setMax( new BigDecimal( 0.2 ) );
        factor.setUnit( "Kg" );
        factor.setLessIsBetter( true );
        
        System.out.println( "weight calculated: " + factor.getWeightCalculated() );
    }
    
    public static void main( String[] args )
    {
        ReportFactorOld old = new ReportFactorOld();
        old.setMin( 0.3 );
        old.setMax( 1.4 );
        
        ReportFactor now = new ReportFactor();
        now.setMin( new BigDecimal( 0.3 ) );
        now.setMax( new BigDecimal( 1.4 ) );
        
        System.out.println( "calculated [OLD]: " + old.getPropertyFactor()   );
        System.out.println( "calculated [NOW]: " + now.getWeightCalculated() );
    }
    
}