package org.osate.aadl.aadlevaluator.report.generator;

import java.io.File;
import java.io.FileWriter;
import org.osate.aadl.aadlevaluator.report.EvolutionReport;
import org.osate.aadl.aadlevaluator.report.ProjectReport;
import org.osate.aadl.aadlevaluator.report.ReportGroup;

public class ReportCSV 
{
    public static final String SEPARATOR = ";";
    
    private ReportCSV()
    {
        // faz nada
    }
    
    public static File create( File dir , ProjectReport projectReport ) throws Exception
    {
        File file = new File( dir , "report.csv" );
        EvolutionReport resume = projectReport.getResume();
        
        try ( FileWriter writer = new FileWriter( file ) )
        {
            append( writer , resume , resume );
            
            for( EvolutionReport report : projectReport.getReports().values() )
            {
                writer.append( System.lineSeparator() );
                append( writer , resume , report );
            }
        }
        
        return file;
    }
    
    private static void append( FileWriter writer , EvolutionReport resume , EvolutionReport report ) throws Exception
    {
        writer.append( report.getName() );
        writer.append( SEPARATOR );
        writer.append( "resume".equalsIgnoreCase( report.getName() )
            ? "factor"
            : report.getFactor() + "" );
        
        for( String name : resume.getGroups().keySet() )
        {
            append( 
                writer , 
                resume.getGroups().get( name ) , 
                report.getGroups().get( name ) 
            );
        }
    }
    
    private static void append( FileWriter writer , ReportGroup resume , ReportGroup report ) throws Exception
    {
        for( String key : resume.getSubgroups().keySet() )
        {
            if( report == null 
                || !report.getSubgroups().containsKey( key ) )
            {
                append( writer , resume.getSubgroup( key ) , null );
            }
            else
            {
                append( writer , resume.getSubgroup( key ) , report.getSubgroup( key ) );
            }
        }
        
        for( String key : resume.getValues().keySet() )
        {
            writer.append( SEPARATOR );
            
            if( report != null 
                && report.getValues().containsKey( key ) 
                && report.getValue( key ).getValue() != null )
            {
                writer.append( report.getValue( key ).getValue().toString() );
            }
        }
    }
    
}