package org.osate.aadl.aadlevaluator.report.generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import org.osate.aadl.evaluator.unit.UnitUtils;

public class GeneratorUtils 
{
    
    private GeneratorUtils()
    {
        // do nothing
    }
    
    public static String hash( String... values )
    {
        return Arrays.toString( values );
    }
    
    public static File createFile( String content , String ext ) throws Exception
    {
        File file = File.createTempFile( "report-" , ext );
                
        try ( FileWriter writer = new FileWriter( file ) )
        {
            writer.write( content );
        }
        
        return file;
    }
    
    public static String getContent( String name )
    {
        StringBuilder builder = new StringBuilder();
        
        try ( BufferedReader reader = new BufferedReader(
            new InputStreamReader( 
                ClassLoader.getSystemResourceAsStream( name )
            )
        ))
        {
            String line;
            while( (line = reader.readLine()) != null )
            {
                builder.append( line )
                    .append( "\n" );
            }
        }
        catch( Exception err )
        {
            System.out.println( "[ERROR] problem to open file: " + name );
            err.printStackTrace();
        }

        return builder.toString();
    }
    
    public static String toValueArray( Collection values )
    {
        return toArray( values.toArray() , false );
    }
    
    public static String toStringArray( Collection values )
    {
        return toArray( values.toArray() , true );
    }
    
    public static String toArray( Object[] values , boolean isString )
    {
        boolean first = true;
        
        StringBuilder builder = new StringBuilder();
        builder.append( "[" );
        
        for( Object value : values )
        {
            if( !first )
            {
                builder.append( "," );
            }
            
            first = false;
            
            if( isString )
            {
                builder.append( "\"" )
                    .append( value )
                    .append("\"" );
            }
            else
            {
                builder.append( 
                    UnitUtils.getValue( value.toString() ) 
                );
            }
        }
        
        builder.append( "]" );
        
        return builder.toString();
    }
    
}
