package org.osate.aadl.aadlevaluator.report.generator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.osate.aadl.aadlevaluator.report.EvolutionReport;
import org.osate.aadl.aadlevaluator.report.ProjectReport;
import org.osate.aadl.aadlevaluator.report.ReportFactor;
import org.osate.aadl.aadlevaluator.report.ReportGroup;
import org.osate.aadl.aadlevaluator.report.ReportValue;
import org.osate.aadl.evaluator.unit.UnitUtils;

/**
 * A estrutura interna do HTML estará assim:
 * 
 * var report = [{
 *    name: Evolution 01
 *    group: {
 *        'performance' : {
 *            'Weight Total' : 10 ,
 *        }
 *    }
 * }];
 * 
 * var resume = {
 *    'performance' : {
 *       'Weight Total' : {
 *            max : 10 ,
 *            min : 1  ,
 *            unit : 'kg'
 *       },
 *    }
 * };
 * 
 * 
 * Read:
 * https://www.chartjs.org/samples/latest/charts/combo-bar-line.html
 * https://www.chartjs.org/samples/latest/charts/scatter/basic.html
 * 
 * @author avld
 */
public class ReportChartHtml 
{
    private static final String RESUME = " '{NAME}' : { "
        + "valueMin : {VALUE_MIN} , "
        + "valueMax : {VALUE_MAX} , "
        + "limitMin : {LIMIT_MIN} , "
        + "limitMax : {LIMIT_MAX} , "
        + "unit : '{UNIT}', "
        + "lessIsBetter : {LESS_IS_BETTER}, "
        + "}";
    
    private static final String REPORT = "{ \n"
        + "name : '{NAME}' , \n"
        + "ranking : {RANKING} , \n"
        + "{FACTORS}"
        + "characteristics : { \n"
        + "{CHARACTERISTICS}\n"
        + "}"
    + "}";
    
    private static final String CHARACTERISTIC = "'{NAME}' : {VALUE}";
    
    private static final String DATA_FILE = "data.js";
    
    private static final String[] AUX_FILES = new String[]{
        "chart_bar.js" ,
        "chart_radar.js" ,
        "chart_scatter.js" ,
        "filter_dialog.js" ,
        "menu.js" ,
        "chart_list.js" ,
        "index.html",
        "filter_list.js" , 
        "nav.js" ,
        "table_list.js"
    };
    
    // ----- //
    
    public static final String ARRAY_START = "[";
    public static final String ARRAY_END = "]";
    
    public static final String OBJECT_START = "{";
    public static final String OBJECT_END = "}";
    
    public static final String ARRAY_SEPARATOR = ",";
    
    private ReportChartHtml()
    {
        // faz nada
    }
    
    public static File create( File dir , ProjectReport projectReport ) throws Exception
    {
        File chartDir = new File( dir , "chart_report" );
        chartDir.mkdirs();
        
        // copy auxiliar files
        for( String filename : AUX_FILES )
        {
            File file = filename.equalsIgnoreCase( "index.html" )
                ? new File( dir      , filename )
                : new File( chartDir , filename );
        
            try ( FileWriter writer = new FileWriter( file ) )
            {
                writer.write( getContent( filename ) );
            }
        }
        
        // create and copy data file
        File file = new File( chartDir , DATA_FILE );
        
        try ( FileWriter writer = new FileWriter( file ) )
        {
            writer.write( getContent( projectReport ) );
        }
        
        return chartDir;
    }
    
    private static String getContent( String filename ) throws Exception
    {
        InputStream input = ReportChartHtml.class.getResourceAsStream( filename );
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        
        byte[] buffer = new byte[ 1024 ];
        int length;
        
        while( (length = input.read( buffer ) ) != -1 )
        {
            output.write( buffer , 0 , length );
        }
        
        return output.toString();
    }
    
    private static String getContent( ProjectReport projectReport ) throws Exception
    {
        return getContent( DATA_FILE )
            .replace( "{RESUME}"  , getResume( projectReport ) )
            .replace( "{REPORTS}" , getReports( projectReport ) );
    }
    
    private static String getResume( ProjectReport project ) throws Exception
    {
        EvolutionReport resume = project.getResume();
        
        StringBuilder builder = new StringBuilder();
        builder.append( "{" )
            .append( System.lineSeparator() );
        
        builder.append( RESUME
            .replace( "{NAME}"       , "ranking" ) 
            .replace( "{LIMIT_MIN}"  , "1"    ) 
            .replace( "{LIMIT_MAX}"  , project.getReports().size() + "" ) 
            .replace( "{VALUE_MIN}"  , "1"    ) 
            .replace( "{VALUE_MAX}"  , project.getReports().size() + "" ) 
            .replace( "{UNIT}"       , "" ) 
            .replace( "{LESS_IS_BETTER}" , "true" ) 
        ).append( ARRAY_SEPARATOR )
        .append( System.lineSeparator() );
        
        builder.append( RESUME
            .replace( "{NAME}"       , "factor" ) 
            .replace( "{LIMIT_MIN}"  , "0" ) 
            .replace( "{LIMIT_MAX}"  , "1" ) 
            .replace( "{VALUE_MIN}"  , "0" ) 
            .replace( "{VALUE_MAX}"  , "1" ) 
            .replace( "{UNIT}"       , ""  ) 
            .replace( "{LESS_IS_BETTER}" , "false" ) 
        ).append( ARRAY_SEPARATOR )
        .append( System.lineSeparator() );
        
        for( ReportGroup group : resume.getGroups().values() )
        {
            //TODO: recuperar os valores buscando
            builder.append( RESUME
                .replace( "{NAME}"       , group.getName() + " factor" ) 
                .replace( "{LIMIT_MIN}"  , "0" ) 
                .replace( "{LIMIT_MAX}"  , "1" ) 
                .replace( "{VALUE_MIN}"  , "0" ) 
                .replace( "{VALUE_MAX}"  , "1" ) 
                .replace( "{UNIT}"       , "" ) 
                .replace( "{LESS_IS_BETTER}" , "false" )
            ).append( ARRAY_SEPARATOR );
            
            builder.append( group.getName() );
            builder.append( ": {" );
            
            getResume( builder , group );
            
            builder.append( "} , \n" );
        }
        
        builder.append( "}" );
        
        return builder.toString();
    }
    
    private static void getResume( StringBuilder builder , ReportGroup group ) throws Exception
    {
        for( ReportGroup subgroup : group.getSubgroups().values() )
        {
            getResume( builder , subgroup );
        }
        
        for( ReportValue value : group.getValues().values() )
        {
            if( !value.isImportant() 
                || !(value.getValue() instanceof ReportFactor) )
            {
                continue ;
            }
            
            ReportFactor factor = (ReportFactor) value.getValue();
            
            String min = factor.getReferences() == null || factor.getReferences()[0] == null 
                ? factor.getMin().toString()
                : UnitUtils.getValueAndUnit( (String) factor.getReferences()[0] )[0];
            
            String max = factor.getReferences() == null || factor.getReferences()[1] == null
                ? factor.getMax().toString()
                : UnitUtils.getValueAndUnit( (String) factor.getReferences()[1] )[0];
            
            builder.append( RESUME
                .replace( "{NAME}" , factor.getTitle() ) 
                .replace( "{LIMIT_MIN}"  , min ) 
                .replace( "{LIMIT_MAX}"  , max ) 
                .replace( "{VALUE_MIN}"  , factor.getMin().toString() ) 
                .replace( "{VALUE_MAX}"  , factor.getMax().toString() ) 
                .replace( "{UNIT}"       , factor.getUnit() ) 
                .replace( "{LESS_IS_BETTER}" , factor.isLessIsBetter() + "" )
            )
                .append( ARRAY_SEPARATOR );
        }
    }
    
    private static String getReports( ProjectReport projectReport ) throws Exception
    {
        List<String> lines = new LinkedList<>();
       
        for( EvolutionReport report : projectReport.getReports().values() )
        {
           lines.add( getReport( report ) );
        }
        
        return toString( lines , ARRAY_SEPARATOR );
    }
    
    private static String getReport( EvolutionReport report ) throws Exception
    {
        StringBuilder builder = new StringBuilder();
       
        for( ReportGroup group : report.getGroups().values() )
        {
            builder.append( group.getName() )
                .append( ": {" );
            
            getReport( builder , group );
            
            builder.append( "} ," );
        }
        
        StringBuilder factors = new StringBuilder();
        
        for( Map.Entry<String,BigDecimal> entry : report.getFactor().entrySet() )
        {
            String value = entry.getValue().setScale( 5 , RoundingMode.HALF_UP ) + "";
            
            if( entry.getKey().equalsIgnoreCase( "total" ) )
            {
                factors.append( CHARACTERISTIC
                    .replace( "{NAME}"  , "factor" )
                    .replace( "{VALUE}" ,  value ) )
                .append( ARRAY_SEPARATOR )
                .append( System.lineSeparator() );
            }
            else
            {
                factors.append( CHARACTERISTIC
                    .replace( "{NAME}"  , entry.getKey() + " factor" )
                    .replace( "{VALUE}" ,  value ) )
                .append( ARRAY_SEPARATOR )
                .append( System.lineSeparator() );
            }
        }
        
        // ----- //
        
        return REPORT
            .replace( "{NAME}"    , report.getName() )
            .replace( "{RANKING}" , report.getRanking() + "" )
            .replace( "{FACTORS}" , factors.toString() )
            .replace( "{CHARACTERISTICS}" , builder.toString() );
    }
    
    private static void getReport( StringBuilder builder , ReportGroup group ) throws Exception
    {
        for( ReportGroup subgroup : group.getSubgroups().values() )
        {
            getReport( builder , subgroup );
        }
        
        for( ReportValue value : group.getValues().values() )
        {
            if( !value.isImportant() )
            {
                continue ;
            }
            
            builder.append( CHARACTERISTIC
                .replace( "{NAME}"  , value.getTitle() )
                .replace( "{VALUE}" , value.getValueNumber() + "" )
            ).append( ARRAY_SEPARATOR );
        }
    }
    
    private static String toString( List<String> lines , String extra ) throws Exception
    {
        StringBuilder builder = new StringBuilder();
        
        for( String line : lines )
        {
            builder.append( line )
                .append( extra )
                .append( System.lineSeparator() );
        }
        
        return builder.toString();
    }
    
}