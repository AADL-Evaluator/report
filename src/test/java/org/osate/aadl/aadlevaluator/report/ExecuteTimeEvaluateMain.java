package org.osate.aadl.aadlevaluator.report;

import java.text.MessageFormat;
import java.util.List;
import org.osate.aadl.aadlevaluator.report.evaluate.ExecuteTimeEvaluate;
import org.osate.aadl.evaluator.project.ComponentPackage;
import org.osate.aadl.evaluator.project.Component;
import org.osate.aadl.evaluator.project.Project;
import org.osate.aadl.evaluator.file.ProjectFile;

public class ExecuteTimeEvaluateMain 
{

    public static void main( String args[] ) throws Exception
    {
        String directory = "/home/avld/projects/Netbeans Projects/AadlEvaluator/examples/example 01/";
        
        Project project = ProjectFile.open( directory );
        
        for( ComponentPackage file : project.getPackages().values() )
        {
            if( "integration.aadl".equalsIgnoreCase( file.getFile().getName() ) )
            {
                evaluate( file );
            }
        }
    }
    
    private static void evaluate( ComponentPackage file ) throws Exception
    {
        for( Component component : file.getComponents().values() )
        {
            if( component.isImplementation() 
                && Component.TYPE_SYSTEM.equalsIgnoreCase( component.getType() ) )
            {
                System.out.println( "Name: " + component.getName() );
                
                List<ExecuteTimeEvaluate.TimeResult> results = new ExecuteTimeEvaluate( component )
                    .evaluate();
                
                for( ExecuteTimeEvaluate.TimeResult result : results )
                {
                    evaluate( "" , result );
                }
                
                System.out.println( "" );
            }
        }
    }
    
    private static void evaluate( String space , ExecuteTimeEvaluate.TimeResult result ) throws Exception
    {
        System.out.print( 
            print( space + "ELEMENT: " + result.getElement() , 50 ) 
        );
        
        if( result.isError() )
        {
            System.out.print( "ERROR: " + result.getError() );
        }
        else if( result.getTimeMax() != null )
        {
            System.out.print( 
                print( 
                    MessageFormat.format( "MIN: {0} ({1}%)" , 
                    result.getTimeMin() , 
                    result.getUsageMin() 
                ) , 25 )
            );
            
            System.out.print( 
                print( 
                    MessageFormat.format( "MAX: {0} ({1}%)" , 
                    result.getTimeMax() , 
                    result.getUsageMax() 
                ) , 25 )
            );
            
            System.out.print( 
                print( 
                    MessageFormat.format( "PERIOD: {0}" , 
                    result.getPeriod()
                ) , 10 )
            );
        }
        
        System.out.println( "" );
        
        for( ExecuteTimeEvaluate.TimeResult r : result.getResults() )
        {
            evaluate( space + "  " , r );
        }
        
        if( !result.getResults().isEmpty() )
        {
            System.out.println( "" );
        }
    }
    
    private static String print( final String text , int size )
    {
        String t = text;
        
        for( int i = t.length() ; i < size ; i++ )
        {
            t += " ";
        }
        
        return t;
    }
    
}