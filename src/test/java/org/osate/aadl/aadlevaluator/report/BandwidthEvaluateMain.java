package org.osate.aadl.aadlevaluator.report;

import java.text.MessageFormat;
import java.util.List;
import org.osate.aadl.aadlevaluator.report.evaluate.BandwidthEvaluate;
import org.osate.aadl.evaluator.project.ComponentPackage;
import org.osate.aadl.evaluator.project.Component;
import org.osate.aadl.evaluator.project.Project;
import org.osate.aadl.evaluator.file.ProjectFile;

public class BandwidthEvaluateMain 
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
                
                List<BandwidthEvaluate.BusResult> results = new BandwidthEvaluate( component )
                    .evaluate();
                
                for( BandwidthEvaluate.BusResult result : results )
                {
                    System.out.println( "  bus: " + result.getSubcomponent().getName() );
                    
                    for( BandwidthEvaluate.IndividualResult individual : result.getResults() )
                    {
                        System.out.println(
                            MessageFormat.format( 
                                "    con: {0}     min: {1}    max: {2}    error: {3}" , 
                                individual.getConnection() , 
                                individual.getMin() , 
                                individual.getMax() ,
                                individual.getErrors()
                            )
                        );
                    }
                }
            }
        }
    }
    
}