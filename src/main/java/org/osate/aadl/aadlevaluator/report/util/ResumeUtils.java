package org.osate.aadl.aadlevaluator.report.util;

import java.math.BigDecimal;
import java.util.Map;
import org.osate.aadl.aadlevaluator.report.EvolutionReport;
import org.osate.aadl.aadlevaluator.report.ProjectReport;
import org.osate.aadl.aadlevaluator.report.ReportFactor;
import org.osate.aadl.aadlevaluator.report.ReportGroup;
import org.osate.aadl.aadlevaluator.report.ReportValue;
import org.osate.aadl.evaluator.unit.UnitUtils;

public class ResumeUtils 
{
    
    private ResumeUtils()
    {
        // do nothing
    }
    
    public static EvolutionReport getResume( ProjectReport project )
    {
        return getResume( project , new EvolutionReport( "resume" ) );
    }
    
    public static EvolutionReport getResume( ProjectReport project , EvolutionReport resume )
    {
        for( EvolutionReport report : project.getReports().values() )
        {
            for( String name : report.getGroups().keySet() )
            {
                ReportGroup g1 = resume.getGroup( name );
                ReportGroup g2 = report.getGroup( name );
                
                groups( name , "" , "" , g1.getSubgroups() , g2.getSubgroups() );
                values( name , "" , "" , g1.getValues()    , g2.getValues()    );
            }
        }
        
        caculate( project );
        
        return resume;
    }
    
    private static void groups( String g1 , String g2 , String parent , Map<String,ReportGroup> resume , Map<String,ReportGroup> older )
    {
        for( Map.Entry<String,ReportGroup> entry : older.entrySet() )
        {
            ReportGroup group = resume.containsKey( entry.getKey() )
                ? resume.get( entry.getKey() )
                : new ReportGroup( entry.getKey() );
            
            if( g2 == null || g2.trim().isEmpty() )
            {
                groups( g1 , entry.getKey() , "" , group.getSubgroups() , entry.getValue().getSubgroups() );
                values( g1 , entry.getKey() , "" , group.getValues()    , entry.getValue().getValues()    );
            }
            else
            {
                String p = parent == null || parent.trim().isEmpty()
                    ? entry.getKey() 
                    : parent + "." + entry.getKey();
            
                groups( g1 , g2 , p , group.getSubgroups() , entry.getValue().getSubgroups() );
                values( g1 , g2 , p , group.getValues()    , entry.getValue().getValues()    );
            }
            
            resume.put( entry.getKey() , group );
        }
    }
    
    private static void values( String g1 , String g2 , String parent , Map<String,ReportValue> resume , Map<String,ReportValue> older )
    {
        for( Map.Entry<String,ReportValue> entry : older.entrySet() )
        {
            String name = parent == null || parent.trim().isEmpty()
                ? entry.getKey() 
                : parent + "." + entry.getKey();
            
            // System.out.println( "factor name: " + name );
            
            if( resume.containsKey( entry.getKey() ) )
            {
                BigDecimal value = new BigDecimal( 0 );
                
                try
                {
                    value = new BigDecimal(
                        UnitUtils.getValue( entry.getValue().getValue().toString() )
                    );
                }
                catch( Exception err )
                {
                    //err.printStackTrace();
                }
                
                ReportFactor factor = (ReportFactor) resume.get( entry.getKey() ).getValue();
                
                factor.setReference( entry.getValue().getReference() == null 
                    ? "" 
                    :  entry.getValue().getReference().toString()
                );
                
                if( factor.getMin().compareTo( value ) == 1 )
                {
                    factor.setMin( value );
                }
                
                if( factor.getMax().compareTo( value ) == -1 )
                {
                    factor.setMax( value );
                }
            }
            else if( entry.getValue().getValue() == null )
            {
                resume.put( entry.getKey() , new ReportValue(
                    entry.getKey() , 
                    new ReportFactor()
                        .setCharacteristic( g1 )
                        .setSubcharacteristic( g2 )
                        .setName( name )
                        .setMax( new BigDecimal( 0 ) )
                        .setMin( new BigDecimal( 0 ) )
                        .setUnit( "" )
                        .setLessIsBetter( entry.getValue().isLessIsBetter() ) ,
                    entry.getValue().isImportant() ,
                    entry.getValue().isLessIsBetter()
                ) );
            }
            else if( entry.getValue().getValue() instanceof Double )
            {
                resume.put( entry.getKey() , new ReportValue(
                    entry.getKey() , 
                    new ReportFactor()
                        .setCharacteristic( g1 )
                        .setSubcharacteristic( g2 )
                        .setName( name )
                        .setMax( new BigDecimal( (Double) entry.getValue().getValue() ) )
                        .setMin( new BigDecimal( (Double) entry.getValue().getValue() ) )
                        .setUnit( "" )
                        .setLessIsBetter( entry.getValue().isLessIsBetter() ) ,
                    entry.getValue().isImportant() ,
                    entry.getValue().isLessIsBetter()
                ) );
            }
            else if( entry.getValue().getValue() instanceof Integer )
            {
                resume.put( entry.getKey() , new ReportValue(
                    entry.getKey() , 
                    new ReportFactor()
                        .setCharacteristic( g1 )
                        .setSubcharacteristic( g2 )
                        .setName( name )
                        .setMax( new BigDecimal( (Integer) entry.getValue().getValue() ) )
                        .setMin( new BigDecimal( (Integer) entry.getValue().getValue() ) )
                        .setUnit( "" )
                        .setLessIsBetter( entry.getValue().isLessIsBetter() ) ,
                    entry.getValue().isImportant() ,
                    entry.getValue().isLessIsBetter()
                ) );
            }
            else
            {
                String[] parts = UnitUtils.getValueAndUnit( entry.getValue().getValue().toString() );
                double value = 0;
                String unit  = parts[ 1 ];
                
                try
                {
                    value = Double.parseDouble( parts[ 0 ] );
                }
                catch( Exception err )
                {
                    //err.printStackTrace();
                }
                
                resume.put( entry.getKey() , new ReportValue(
                    entry.getKey() , 
                    new ReportFactor()
                        .setCharacteristic( g1 )
                        .setSubcharacteristic( g2 )
                        .setName( name )
                        .setMax( new BigDecimal( value ) )
                        .setMin( new BigDecimal( value ) )
                        .setUnit( unit )
                        .setLessIsBetter( entry.getValue().isLessIsBetter() ) ,
                    entry.getValue().isImportant() ,
                    entry.getValue().isLessIsBetter()
                ) );
            }
        }
    }
    
    // ----------------------------
    // ---------------------------- CALCULATE
    // ----------------------------
    
    public static void caculate( ProjectReport project )
    {
        for( EvolutionReport report : project.getReports().values() )
        {
            BigDecimal total = new BigDecimal( 0 );
            
            for( String name : project.getResume().getGroups().keySet() )
            {
                total = total.add( caculate( 
                    project.getResume().getGroups().get( name ) , 
                    report.getGroups().get( name )
                ));
            }
            
            report.setFactor( total );
        }
    }
    
    private static BigDecimal caculate( ReportGroup resume , ReportGroup group )
    {
        if( group == null )
        {
            return new BigDecimal( 0 );
        }
        
        BigDecimal total = new BigDecimal( 0 );
        
        for( Map.Entry<String,ReportGroup> entry : resume.getSubgroups().entrySet() )
        {
            total = total.add( caculate( 
                entry.getValue() , 
                group.getSubgroup( entry.getKey() ) 
            ));
        }
        
        for( Map.Entry<String,ReportValue> entry : resume.getValues().entrySet() )
        {
            if( !entry.getValue().isImportant() )
            {
                continue ;
            }
            
            total = total.add( caculate( 
                (ReportFactor) entry.getValue().getValue() , 
                group.getValue( entry.getKey() ) 
            ));
        }
        
        return total;
    }
    
    public static BigDecimal caculate( ReportFactor resume , ReportValue value )
    {
        if( value == null )
        {
            return new BigDecimal( 0 );
        }
        
        try
        {
            if( value.getValue() instanceof Double )
            {
                return resume.getGlobalFactor( new BigDecimal( (Double) value.getValue() ) );
            }
            else if( value.getValue() instanceof Integer )
            {
                return resume.getGlobalFactor( new BigDecimal( (Integer) value.getValue() ) );
            }
            else
            {
                return resume.getGlobalFactor( 
                    new BigDecimal( UnitUtils.getValue( value.getValue().toString() ) )
                );
            }
        }
        catch( Exception err )
        {
            return new BigDecimal( 0 );
        }
    }
    
}