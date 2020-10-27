package org.osate.aadl.aadlevaluator.report.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.osate.aadl.aadlevaluator.report.EvolutionReport;
import org.osate.aadl.aadlevaluator.report.ProjectReport;
import org.osate.aadl.aadlevaluator.report.ReportFactor;
import org.osate.aadl.aadlevaluator.report.ReportGroup;
import org.osate.aadl.aadlevaluator.report.ReportValue;
import org.osate.aadl.evaluator.unit.UnitUtils;

public class ResumeUtils 
{
    private static final Logger LOG = Logger.getLogger( ResumeUtils.class.getName() );
    
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
        LOG.log( Level.INFO , "[RESUME] Resume was started..." );
        
        for( EvolutionReport report : project.getReports().values() )
        {
            LOG.log( Level.INFO , "[RESUME] Evolution: {0} " , report.getName() );
            
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
            
            LOG.log( Level.INFO , "[RESUME] group: {0} " , entry.getKey() );
            
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
            
            String title = entry.getValue().getTitle();
            
            if( resume.containsKey( entry.getKey() ) )
            {
                LOG.log( Level.INFO , "[RESUME]    resume contains: {0} " , title );
                BigDecimal value = BigDecimal.ZERO;
                
                try
                {
                    value = BigDecimal.valueOf(
                        UnitUtils.getValue( entry.getValue().getValue().toString() )
                    );
                }
                catch( Exception err )
                {
                    //err.printStackTrace();
                }
                
                ReportFactor factor = (ReportFactor) resume.get( entry.getKey() ).getValue();
                
                factor.setReferences( entry.getValue().getReference() == null 
                    ? null 
                    : (String[]) entry.getValue().getReference()
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
                LOG.log( Level.INFO , "[RESUME]    resume not contains: {0} (and value is null)" , title );
                
                resume.put( entry.getKey() , new ReportValue(
                    entry.getKey() , 
                    new ReportFactor()
                        .setCharacteristic( g1 )
                        .setSubcharacteristic( g2 )
                        .setName( name )
                        .setTitle( title )
                        .setMax( BigDecimal.ZERO )
                        .setMin( BigDecimal.ZERO )
                        .setUnit( "" )
                        .setLessIsBetter( entry.getValue().isLessIsBetter() ) ,
                    entry.getValue().isImportant() ,
                    entry.getValue().isLessIsBetter()
                ) );
            }
            else if( entry.getValue().getValue() instanceof Double )
            {
                LOG.log( Level.INFO , "[RESUME]    resume not contains: {0} , and value is double: {1}" , 
                        new Object[]{ title , entry.getValue().getValue() } );
                
                resume.put( entry.getKey() , new ReportValue(
                    entry.getKey() , 
                    new ReportFactor()
                        .setCharacteristic( g1 )
                        .setSubcharacteristic( g2 )
                        .setName( name )
                        .setTitle( title )
                        .setMax( (Double) entry.getValue().getValue() )
                        .setMin( (Double) entry.getValue().getValue() )
                        .setUnit( "" )
                        .setLessIsBetter( entry.getValue().isLessIsBetter() ) ,
                    entry.getValue().isImportant() ,
                    entry.getValue().isLessIsBetter()
                ) );
            }
            else if( entry.getValue().getValue() instanceof Integer )
            {
                LOG.log( Level.INFO , "[RESUME]    resume not contains: {0} , and value is int: {}" , 
                        new Object[]{ title , entry.getValue().getValue() } );
                
                resume.put( entry.getKey() , new ReportValue(
                    entry.getKey() , 
                    new ReportFactor()
                        .setCharacteristic( g1 )
                        .setSubcharacteristic( g2 )
                        .setName( name )
                        .setTitle( title )
                        .setMax( (Integer) entry.getValue().getValue() )
                        .setMin( (Integer) entry.getValue().getValue() )
                        .setUnit( "" )
                        .setLessIsBetter( entry.getValue().isLessIsBetter() ) ,
                    entry.getValue().isImportant() ,
                    entry.getValue().isLessIsBetter()
                ) );
            }
            else
            {
                LOG.log( Level.INFO , "[RESUME]    resume not contains: {0} , and value is other: {1}" , 
                        new Object[]{ title , entry.getValue().getValue() } );
                
                String[] parts = UnitUtils.getValueAndUnit( entry.getValue().getValue().toString() );
                double value = 0;
                String unit  = parts[ 1 ];
                
                try
                {
                    value = Double.parseDouble( parts[ 0 ] );
                }
                catch( Exception err )
                {
                    // do nothing
                }
                
                resume.put( entry.getKey() , new ReportValue(
                    entry.getKey() , 
                    new ReportFactor()
                        .setCharacteristic( g1 )
                        .setSubcharacteristic( g2 )
                        .setName( name )
                        .setTitle( title )
                        .setMax( value )
                        .setMin( value )
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
        LOG.log( Level.INFO , "[FACTOR CALCULATE] The factor of all evolutions will be calculated..." );
        
        for( EvolutionReport report : project.getReports().values() )
        {
            LOG.log(Level.INFO , "evolution name: {0}" , report.getName() );
            BigDecimal total = BigDecimal.ZERO;
            
            for( String name : project.getResume().getGroups().keySet() )
            {
                BigDecimal value = caculate( 
                    project.getResume().getGroups().get( name ) , 
                    report.getGroups().get( name )
                );
                
                total = total.add( value );
                
                LOG.log(Level.INFO , "{0} = {1}" , new Object[]{ 
                    name , 
                    value.setScale( 20 , RoundingMode.HALF_UP ).doubleValue()
                } );
            }
            
            LOG.log(
                Level.INFO , 
                "total = {0}" , 
                total.setScale( 20 , RoundingMode.HALF_UP ).doubleValue() 
            );
            report.setFactor( total );
        }
    }
    
    private static BigDecimal caculate( ReportGroup resume , ReportGroup group )
    {
        if( group == null )
        {
            return BigDecimal.ZERO;
        }
        
        BigDecimal total = BigDecimal.ZERO;
        
        for( Map.Entry<String,ReportGroup> entry : resume.getSubgroups().entrySet() )
        {
            total = total.add( caculate( 
                entry.getValue() , 
                group.getSubgroup( entry.getKey() ) 
            ) );
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
            ) );
        }
        
        return total;
    }
    
    public static BigDecimal caculate( ReportFactor resume , ReportValue value )
    {
        if( value == null )
        {
            return BigDecimal.ZERO;
        }
        
        try
        {
            if( value.getValue() instanceof Double )
            {
                return resume.getWeightGlobal( new BigDecimal( (Double) value.getValue() ) );
            }
            else if( value.getValue() instanceof Integer )
            {
                return resume.getWeightGlobal( new BigDecimal( (Integer) value.getValue() ) );
            }
            else
            {
                return resume.getWeightGlobal( 
                    new BigDecimal( UnitUtils.getValue( value.getValue().toString() ) )
                );
            }
        }
        catch( Exception err )
        {
            return BigDecimal.ZERO;
        }
    }
    
}