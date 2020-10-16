package org.osate.aadl.aadlevaluator.report.generator;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;
import org.osate.aadl.aadlevaluator.report.EvolutionReport;
import org.osate.aadl.aadlevaluator.report.ProjectReport;
import org.osate.aadl.aadlevaluator.report.ReportFactor;
import org.osate.aadl.aadlevaluator.report.ReportGroup;
import org.osate.aadl.aadlevaluator.report.ReportValue;

public class ReportYAML 
{
    public static final String FIRST_LINE = "---";
    public static final String SEPARATOR = ":";
    public static final String SPACE = " ";
    
    private ReportYAML()
    {
        // faz nada
    }
    
    public static File create( File dir , ProjectReport projectReport ) throws Exception
    {
        File yamlDir = new File( dir , "yaml" );
        yamlDir.mkdirs();
        
        append( yamlDir , projectReport.getResume() );
        
        for( EvolutionReport report : projectReport.getReports().values() )
        {
            append( yamlDir , report );
        }
        
        return yamlDir;
    }
    
    private static void append( File jsonDir , EvolutionReport report ) throws Exception
    {
        File f = new File( jsonDir , report.getName() + ".yaml" );
            
        try ( FileWriter writer = new FileWriter( f ) )
        {
            writer.append( FIRST_LINE );
            writer.write( System.lineSeparator() );
            writer.write( System.lineSeparator() );

            append( writer , report );
        }
    }
    
    private static void append( FileWriter writer , EvolutionReport report ) throws Exception
    {
        append( writer , "" , "name" , report.getName() );
        writer.append( System.lineSeparator() );
        
        append( writer , "" , "factor" , report.getFactor() );
        
        for( ReportGroup group : report.getGroups().values() )
        {
            writer.append( System.lineSeparator() );
            writer.append( System.lineSeparator() );
            append( writer , "" , group );
        }
    }
    
    private static void append( FileWriter writer , String space , ReportGroup report ) throws Exception
    {
        append( writer , space , report.getName() );
        writer.append( System.lineSeparator() );
        
        boolean first = true;
        
        for( ReportGroup group : report.getSubgroups().values() )
        {
            if( !first )
            {
                writer.append( System.lineSeparator() );
            }
            
            first = false;
            append( writer , space + SPACE + SPACE , group );
        }
        
        for( Map.Entry<String,ReportValue> entry : report.getValues().entrySet())
        {
            if( !first )
            {
                writer.append( System.lineSeparator() );
            }
            
            first = false;
            append( writer , space + SPACE + SPACE , entry.getKey() , entry.getValue().getValue() );
        }
        
        writer.write( System.lineSeparator() );
    }
    
    private static void append( FileWriter writer , String space , String name ) throws Exception
    {
        writer.append( space )
            .append( name )
            .append( SEPARATOR )
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
            writer.append( value.toString() );
        }
    }
    
    private static void append( FileWriter writer , String space , ReportFactor factor ) throws Exception
    {
        writer.append( System.lineSeparator() );
        
        append( writer , space , "name" , factor.getName() );
        writer.append( System.lineSeparator() );
        
        append( writer , space , "min"  , factor.getMin()  );
        writer.append( System.lineSeparator() );
        
        append( writer , space , "max"  , factor.getMax()  );
        writer.append( System.lineSeparator() );
        
        append( writer , space , "unit" , factor.getUnit() );
        writer.append( System.lineSeparator() );
        
        append( writer , space , "property_factor" , factor.getPropertyFactor() );
        writer.append( System.lineSeparator() );
        
        append( writer , space , "user_factor"     , factor.getUserFactor()     );
        writer.write( System.lineSeparator() );
    }
    
}