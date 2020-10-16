package org.osate.aadl.aadlevaluator.report;

import org.junit.Assert;
import org.junit.Test;
import org.osate.aadl.aadlevaluator.report.evaluate.WeightEvaluate;
import org.osate.aadl.evaluator.project.Component;
import org.osate.aadl.evaluator.project.ComponentPackage;
import org.osate.aadl.evaluator.project.Property;
import org.osate.aadl.evaluator.project.Subcomponent;
import org.osate.aadl.evaluator.unit.WeightUtils;

public class WeightResultTest 
{
    private final ComponentPackage PACK = new ComponentPackage( "teste" );
    
    @Test
    public void example_01()
    {
        WeightEvaluate evaluate = new WeightEvaluate(
            create( "C1" , "1.0g" )
        );
        
        Assert.assertEquals( "0.001 Kg" , evaluate.evaluate().getTotal() );
    }
    
    @Test
    public void example_02()
    {
        create( "C4" , "3 Kg" );
        
        Component c = create( "C3" , "2 Kg" );
        c.add( new Subcomponent( "C4" , "process C4" ) );
        
        WeightEvaluate evaluate = new WeightEvaluate( c );
        
        Assert.assertEquals( "5.0 Kg" , evaluate.evaluate().getTotal() );
    }
    
    private Component create( String name , String price )
    {
        Component c = new Component();
        c.setParent( PACK );
        c.setName( name );
        c.getProperties().add( 
            new Property( WeightEvaluate.PROPERTY_WEIGHT[0] , price ) 
        );
        
        PACK.add( c );
        
        return c;
    }
    
    public static void main( String arg[] ) throws Exception
    {
        System.out.println(
            WeightUtils.sum( "1.0 g" , "0" , "Kg" )
        );
    }
    
}
