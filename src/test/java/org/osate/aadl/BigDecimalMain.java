package org.osate.aadl;

import org.junit.Ignore;
import org.osate.aadl.evaluator.unit.UnitUtils;
import org.osate.aadl.evaluator.unit.WeightUtils;

@Ignore
public class BigDecimalMain 
{
    
    public static void main( String[] args )
    {
        System.out.println( "---- UnitUtils.convert" );
        System.out.println( "result: " + UnitUtils.convert( 67 , 1 , 2, 1000 ) );
        
        System.out.println( "" );
        System.out.println( "" );
        System.out.println( "---- WeightUtils.convert" );
        System.out.println( WeightUtils.convert( "67 g" , "kg" ) );
        
        System.out.println( "" );
        System.out.println( "" );
        System.out.println( "---- WeightUtils.getValue" );
        System.out.println( WeightUtils.getValue( "67 g" , "kg" ) );
        
        System.out.println( "" );
        System.out.println( "" );
        System.out.println( "---- WeightUtils.sum" );
        System.out.println( WeightUtils.sum( "67 g" , "0 kg" , "kg" ) );
        
        System.out.println( "" );
        System.out.println( "" );
        System.out.println( "---- UnitUtils.convert" );
        System.out.println( "result: " + UnitUtils.convert( 67 , 1 , 2, 1000 ) );
    }
    
}