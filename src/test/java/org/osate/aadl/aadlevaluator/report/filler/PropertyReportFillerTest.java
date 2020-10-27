package org.osate.aadl.aadlevaluator.report.filler;

import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.osate.aadl.aadlevaluator.report.EvolutionReport;
import org.osate.aadl.aadlevaluator.report.ReportGroup;
import org.osate.aadl.aadlevaluator.report.ReportValue;
import org.osate.aadl.evaluator.project.Property;
import org.osate.aadl.evaluator.reqspec.Constant;
import org.osate.aadl.evaluator.reqspec.SystemRequirement;

public class PropertyReportFillerTest 
{
    
    @Test
    public void valName()
    {
        String name = "Device_property::Device_Last_Update";
        String expected = "DeviceProperty_DeviceLastUpdate";
        String result = PropertyReportFiller.getValBaseName( name );
        
        System.out.println( "--- val name" );
        System.out.println( "expected: " + expected );
        System.out.println( "result..: " + result   );
        
        Assert.assertEquals( 
            expected.toUpperCase() , 
            result.toUpperCase()
        );
    }
    
    @Test
    public void title()
    {
        String name = "Device_property::Device_Last_Update";
        String expected = "Device Property - Device Last Update";
        String result = PropertyReportFiller.getTitle( name );
        
        System.out.println( "--- title" );
        System.out.println( "expected: " + expected );
        System.out.println( "result..: " + result   );
        
        Assert.assertEquals( 
            expected.toUpperCase() , 
            result.toUpperCase()
        );
    }
    
    @Test
    public void fill_normal()
    {
        EvolutionReport report = new EvolutionReport( "test" );
        
        Property property = new Property( "Normal" , "10 bits" );
        
        SystemRequirement req = new SystemRequirement();
        req.getConstants().add( new Constant( PropertyReportFiller.VALUE_MIN , "5 bits" ) );
        req.getConstants().add( new Constant( PropertyReportFiller.VALUE_MAX , "20 bits" ) );
        req.getConstants().add( new Constant( PropertyReportFiller.ATTRIBUTE , "Normal" ) );
        req.getConstants().add( new Constant( PropertyReportFiller.LESS_IS_BETTER , "true" ) );
        req.setCategory( PropertyReportFiller.GROUP_DEFAULT );
        
        PropertyReportFiller.fill( report , property , req );
        
        String expected_name = property.getName();
        Map<String,String> constants = req.getConstantsMap();
        
        // ------- results
        
        ReportGroup group = report.getGroup( PropertyReportFiller.GROUP_DEFAULT );
        Assert.assertTrue( "The group is not exist!" , group != null );
        
        ReportValue value = group.getValue( expected_name );
        Assert.assertTrue( "The value is not exist!" , value != null );
        Assert.assertEquals( expected_name       , value.getTitle() );
        Assert.assertEquals( property.getValue() , value.getValue() );
        Assert.assertEquals( constants.get( PropertyReportFiller.VALUE_MIN ) , value.getReferenceMin() );
        Assert.assertEquals( constants.get( PropertyReportFiller.VALUE_MAX ) , value.getReferenceMax() );
    }
    
    @Test
    public void fill_array()
    {
        EvolutionReport report = new EvolutionReport( "test" );
        
        Property property = new Property( "Normal" , "(10 bits)" );
        
        SystemRequirement req = new SystemRequirement();
        req.getConstants().add( new Constant( PropertyReportFiller.VALUE_MIN , "5 bits" ) );
        req.getConstants().add( new Constant( PropertyReportFiller.VALUE_MAX , "20 bits" ) );
        req.getConstants().add( new Constant( PropertyReportFiller.ATTRIBUTE , "Normal" ) );
        req.getConstants().add( new Constant( PropertyReportFiller.LESS_IS_BETTER , "true" ) );
        req.setCategory( PropertyReportFiller.GROUP_DEFAULT );
        
        PropertyReportFiller.fill( report , property , req );
        
        String expected_name = property.getName() + "_0";
        Map<String,String> constants = req.getConstantsMap();
        
        // ------- results
        
        ReportGroup group = report.getGroup( PropertyReportFiller.GROUP_DEFAULT );
        Assert.assertTrue( "The group is not exist!" , group != null );
        
        ReportValue value = group.getValue( expected_name );
        Assert.assertTrue( "The value is not exist!" , value != null );
        Assert.assertEquals( property.getName() + " [0]"       , value.getTitle() );
        Assert.assertEquals( property.getValueArray().get( 0 ) , value.getValue() );
        Assert.assertEquals( constants.get( PropertyReportFiller.VALUE_MIN ) , value.getReferenceMin() );
        Assert.assertEquals( constants.get( PropertyReportFiller.VALUE_MAX ) , value.getReferenceMax() );
    }
    
    @Test
    public void fill_object()
    {
        EvolutionReport report = new EvolutionReport( "test" );
        
        Property property = new Property( "Normal" , "[ B => 10 bits; ]" );
        
        SystemRequirement req = new SystemRequirement();
        req.getConstants().add( new Constant( PropertyReportFiller.VALUE_MIN , "5 bits" ) );
        req.getConstants().add( new Constant( PropertyReportFiller.VALUE_MAX , "20 bits" ) );
        req.getConstants().add( new Constant( PropertyReportFiller.ATTRIBUTE , "Normal" ) );
        req.getConstants().add( new Constant( PropertyReportFiller.LESS_IS_BETTER , "true" ) );
        req.setCategory( PropertyReportFiller.GROUP_DEFAULT );
        
        PropertyReportFiller.fill( report , property , req );
        
        String expected_name = property.getName() + "_B";
        Map<String,String> constants = req.getConstantsMap();
        
        // ------- results
        
        ReportGroup group = report.getGroup( PropertyReportFiller.GROUP_DEFAULT );
        Assert.assertTrue( "The group is not exist!" , group != null );
        
        ReportValue value = group.getValue( expected_name );
        Assert.assertTrue( "The value is not exist!" , value != null );
        Assert.assertEquals( property.getName() + " (B)"       , value.getTitle() );
        Assert.assertEquals( property.getValueObject().get( "B" ).getValue() , value.getValue() );
        Assert.assertEquals( constants.get( PropertyReportFiller.VALUE_MIN ) , value.getReferenceMin() );
        Assert.assertEquals( constants.get( PropertyReportFiller.VALUE_MAX ) , value.getReferenceMax() );
    }
    
    @Test
    public void fill_min_max()
    {
        EvolutionReport report = new EvolutionReport( "test" );
        
        Property property = new Property( "Normal" , "10 bits .. 15 bits" );
        
        SystemRequirement req = new SystemRequirement();
        req.getConstants().add( new Constant( PropertyReportFiller.VALUE_MIN , "5 bits" ) );
        req.getConstants().add( new Constant( PropertyReportFiller.VALUE_MAX , "20 bits" ) );
        req.getConstants().add( new Constant( PropertyReportFiller.ATTRIBUTE , "Normal" ) );
        req.getConstants().add( new Constant( PropertyReportFiller.LESS_IS_BETTER , "true" ) );
        req.setCategory( PropertyReportFiller.GROUP_DEFAULT );
        
        PropertyReportFiller.fill( report , property , req );
        
        String expected_name = property.getName() + "_min";
        Map<String,String> constants = req.getConstantsMap();
        
        // ------- results
        
        ReportGroup group = report.getGroup( PropertyReportFiller.GROUP_DEFAULT );
        Assert.assertTrue( "The group is not exist!" , group != null );
        
        ReportValue value = group.getValue( expected_name );
        Assert.assertTrue( "The value is not exist!" , value != null );
        Assert.assertEquals( property.getName() + " (MIN)"     , value.getTitle() );
        Assert.assertEquals( property.getValueMinAndMax().get( Property.MIN ) , value.getValue() );
        Assert.assertEquals( constants.get( PropertyReportFiller.VALUE_MIN )  , value.getReferenceMin() );
        Assert.assertEquals( constants.get( PropertyReportFiller.VALUE_MAX )  , value.getReferenceMax() );
    }
    
}