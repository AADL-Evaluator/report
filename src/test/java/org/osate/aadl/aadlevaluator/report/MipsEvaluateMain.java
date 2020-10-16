package org.osate.aadl.aadlevaluator.report;

import java.util.List;
import org.osate.aadl.aadlevaluator.report.evaluate.MipsEvaluate;
import org.osate.aadl.evaluator.project.ComponentPackage;
import org.osate.aadl.evaluator.project.Component;
import org.osate.aadl.evaluator.project.Project;
import org.osate.aadl.evaluator.file.ProjectFile;

public class MipsEvaluateMain 
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
                
                List<MipsEvaluate.MipsResult> results = new MipsEvaluate( component )
                    .evaluate();
                
                for( MipsEvaluate.MipsResult result : results )
                {
                    evaluate( "" , result );
                }
                
                System.out.println( "" );
            }
        }
    }
    
    private static void evaluate( String space , MipsEvaluate.MipsResult result ) throws Exception
    {
        System.out.print( 
            print( space + "ELEMENT: " + result.getElement() , 100 ) 
        );
        
        if( result.isError() )
        {
            System.out.print( "ERROR: " + result.getError() );
        }
        else
        {
            System.out.print( 
                print( "VALUE (MIN): " + result.getValueMinStr() , 40 )
            );
            
            System.out.print( 
                print( "VALUE (MAX): " + result.getValueMaxStr() , 40 )
            );
            
            if( result.getCapacity() > 0 )
            {
                System.out.print( "CAPACITY: " + result.getCapacityStr() );
            }
        }
        
        System.out.println( "" );
        
        for( MipsEvaluate.MipsResult r : result.getResults() )
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