package org.osate.aadl.aadlevaluator.report.generator;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;
import org.osate.aadl.aadlevaluator.report.EvolutionReport;
import org.osate.aadl.aadlevaluator.report.ProjectReport;
import org.osate.aadl.aadlevaluator.report.ReportFactor;
import org.osate.aadl.aadlevaluator.report.ReportGroup;
import org.osate.aadl.aadlevaluator.report.ReportValue;

public class ReportJSON 
{
    public static final String FIELD_VALUE_SEPARATOR = ":";
    public static final String SEPARATOR = ",";
    public static final String STRING = "\"";
    public static final String SPACE = " ";
    public static final String OBJECT_START = "{";
    public static final String OBJECT_END = "}";
    
    private ReportJSON()
    {
        // faz nada
    }
    
    public static File create( File dir , ProjectReport projectReport ) throws Exception
    {
        File jsonDir = new File( dir , "json" );
        jsonDir.mkdirs();
        
        append( jsonDir , projectReport.getResume() );
        
        for( EvolutionReport report : projectReport.getReports().values() )
        {
            append( jsonDir , report );
        }
        
        return jsonDir;
    }
    
    private static void append( File jsonDir , EvolutionReport report ) throws Exception
    {
        File f = new File( jsonDir , report.getName() + ".json" );
            
        try ( FileWriter writer = new FileWriter( f ) )
        {
            append( writer , report );
        }
    }
    
    private static void append( FileWriter writer , EvolutionReport report ) throws Exception
    {
        writer.append( OBJECT_START );
        writer.append( System.lineSeparator() );
        
        append( writer , SPACE , "name" , report.getName() );
        writer.append( SEPARATOR );
        writer.append( System.lineSeparator() );
        
        append( writer , SPACE , "factor" , report.getFactor() );
        writer.append( SEPARATOR );
        
        for( ReportGroup group : report.getGroups().values() )
        {
            writer.append( System.lineSeparator() );
            writer.append( System.lineSeparator() );
            append( writer , SPACE , group );
            writer.append( SEPARATOR );
        }
        
        writer.append( System.lineSeparator() );
        writer.append( System.lineSeparator() );
        writer.append( OBJECT_END );
    }
    
    private static void append( FileWriter writer , String space , ReportGroup report ) throws Exception
    {
        append( writer , space , report.getName() );
        
        writer.append( OBJECT_START );
        writer.append( System.lineSeparator() );
        
        boolean first = true;
        
        for( ReportGroup group : report.getSubgroups().values() )
        {
            if( !first )
            {
                writer.append( SEPARATOR );
                writer.append( System.lineSeparator() );
            }
            
            first = false;
            append( writer , space + SPACE + SPACE , group );
        }
        
        for( Map.Entry<String,ReportValue> entry : report.getValues().entrySet())
        {
            if( !first )
            {
                writer.append( SEPARATOR );
                writer.append( System.lineSeparator() );
            }
            
            first = false;
            append( writer , space + SPACE + SPACE , entry.getKey() , entry.getValue().getValue() );
        }
        
        writer.write( System.lineSeparator() );
        writer.write( space );
        writer.append( OBJECT_END );
    }
    
    private static void append( FileWriter writer , String space , String name ) throws Exception
    {
        writer.append( space )
            .append( STRING )
            .append( name )
            .append( STRING )
            .append( SPACE )
            .append( FIELD_VALUE_SEPARATOR )
            .append( SPACE );
    }
    
    private static void append( FileWriter writer , String space , String name , Object value ) throws Exception
    {
        append( writer , space , name );
        
        if( value instanceof Integer
            || value instanceof Double )
        {
            writer.append( value.toString() );
        }
        else if( value == null )
        {
            writer.append( "null" );
        }
        else if( value instanceof ReportFactor )
        {
            append( writer , space + SPACE + SPACE , (ReportFactor) value );
        }
        else
        {
            writer.append( STRING )
                .append( value.toString() )
                .append( STRING );
        }
    }
    
    private static void append( FileWriter writer , String space , ReportFactor factor ) throws Exception
    {
        writer.append( OBJECT_START );
        writer.append( System.lineSeparator() );
        
        append( writer , space , "name" , factor.getName() );
        writer.append( SEPARATOR );
        writer.append( System.lineSeparator() );
        
        append( writer , space , "min"  , factor.getMin()  );
        writer.append( SEPARATOR );
        writer.append( System.lineSeparator() );
        
        append( writer , space , "max"  , factor.getMax()  );
        writer.append( SEPARATOR );
        writer.append( System.lineSeparator() );
        
        append( writer , space , "unit" , factor.getUnit() );
        writer.append( SEPARATOR );
        writer.append( System.lineSeparator() );
        
        append( writer , space , "property_factor" , factor.getWeightCalculated() );
        writer.append( SEPARATOR );
        writer.append( System.lineSeparator() );
        
        append( writer , space , "user_factor"     , factor.getWeightDefined()     );
        writer.write( System.lineSeparator() );
        
        writer.write( space );
        writer.append( OBJECT_END );
    }
    
}