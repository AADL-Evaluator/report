package org.osate.aadl.aadlevaluator.report.generator;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import org.osate.aadl.aadlevaluator.report.ProjectReport;

/**
 * 
 * Read:
 * https://css-tricks.com/the-many-ways-of-getting-data-into-charts/
 * https://datatables.net/
 * 
 * @author avld
 */
public class ReportTableHtml 
{
    private static final String HTML = "<!DOCTYPE html>\n"
        + "<html>\n"
        + "<header>\n"
        + "<meta charset='UTF-8'>\n"
        + "<title>Report</title>\n"
        + "<link rel=\"stylesheet\" type=\"text/css\" href=\"https://cdn.datatables.net/1.10.21/css/jquery.dataTables.min.css\">\n"
        + "<script src='https://code.jquery.com/jquery-3.5.1.js'></script>\n"
        + "<script src='https://cdn.datatables.net/1.10.21/js/jquery.dataTables.min.js'></script>\n"
        + "</header>\n"
        + "<body>\n"
        + "<table id='example' class='display' width='100%'></table>\n"
        + "<script>\n"
        + "$(document).ready(function() {\n" 
        + "    $('#example').DataTable( {"
        + "   data : [{DATA}] , "
        + "   columns : [{COLUMN}] "
        + " } );\n" 
        + "} );"
        + "</script>\n"
        + "</body>\n"
        + "</html>";
    
    private static final String COLUMN = "{ title : '{TITLE}' }";
    
    public static final String ARRAY_START = "[";
    public static final String ARRAY_END = "]";
    public static final String ITEM_SEPARATOR = ",";
    
    private ReportTableHtml()
    {
        // faz nada
    }
    
    public static File create( File dir , ProjectReport projectReport ) throws Exception
    {
        File file = new File( dir , "table_report.html" );
        
        try ( FileWriter writer = new FileWriter( file ) )
        {
            writer.write( getContent( dir ) );
        }
        
        return file;
    }
    
    private static String getContent( File dir ) throws Exception
    {
       List<String> lines = Files.readAllLines( 
            new File( dir , "report.csv" ).toPath() 
       );
       
       String columns = createColumns( lines.remove( 0 ) );
       String data = createData( lines );
       
       return HTML
            .replace( "{DATA}"   , data    )
            .replace( "{COLUMN}" , columns );
    }
    
    private static String createColumns( String line ) throws Exception
    {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        
        for( String name : line.split( ReportCSV.SEPARATOR ) )
        {
            builder.append( first ? "\n" : ",\n" );
            first = false;
            builder.append( COLUMN.replace( "{TITLE}" , name ) );
        }
        
        return builder.toString();
    }
    
    private static String createData( Collection<String> lines ) throws Exception
    {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        
        for( String line : lines )
        {
            builder.append( first ? "\n" : ",\n" );
            first = false;
            
            builder.append( ARRAY_START );
            builder.append( "\"" );
            builder.append( 
                line.replaceAll( 
                    Pattern.quote( ReportCSV.SEPARATOR ) , 
                    "\"" + ITEM_SEPARATOR + "\"" 
                ) 
            );
            builder.append( "\"" );
            builder.append( ARRAY_END );
        }
        
        return builder.toString();
    }
    
}