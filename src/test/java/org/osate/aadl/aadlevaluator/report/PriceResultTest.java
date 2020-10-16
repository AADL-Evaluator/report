package org.osate.aadl.aadlevaluator.report;

import org.junit.Assert;
import org.junit.Test;
import org.osate.aadl.aadlevaluator.report.evaluate.PriceEvaluate;
import org.osate.aadl.evaluator.project.Component;
import org.osate.aadl.evaluator.project.ComponentPackage;
import org.osate.aadl.evaluator.project.Property;
import org.osate.aadl.evaluator.project.Subcomponent;
import org.osate.aadl.evaluator.unit.PriceUtils;

public class PriceResultTest 
{
    private final ComponentPackage PACK = new ComponentPackage( "teste" );
    
    @Test
    public void example_01()
    {
        PriceEvaluate evaluate = new PriceEvaluate(
            create( "C1" , "1" )
        );
        
        Assert.assertEquals( "1.0 Dollar" , evaluate.evaluate().getTotal() );
    }
    
    @Test
    public void example_02()
    {
        create( "C4" , "3" );
        
        Component c = create( "C3" , "2" );
        c.add( new Subcomponent( "C4" , "process C4" ) );
        
        PriceEvaluate evaluate = new PriceEvaluate( c );
        
        Assert.assertEquals( "5.0 Dollar" , evaluate.evaluate().getTotal() );
    }
    
    private Component create( String name , String price )
    {
        Component c = new Component();
        c.setParent( PACK );
        c.setName( name );
        c.getProperties().add( 
            new Property( PriceEvaluate.PROPERTY_PRICE[0] , price ) 
        );
        
        PACK.add( c );
        
        return c;
    }
    
    public static void main( String arg[] ) throws Exception
    {
        System.out.println(
            PriceUtils.sum( "0.0 Dollar" , "1" , "Dollar" )
        );
    }
    
}
