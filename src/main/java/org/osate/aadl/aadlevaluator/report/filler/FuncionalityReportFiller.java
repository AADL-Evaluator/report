package org.osate.aadl.aadlevaluator.report.filler;

import java.util.HashMap;
import java.util.Map;
import org.osate.aadl.aadlevaluator.report.EvolutionReport;
import org.osate.aadl.aadlevaluator.report.ReportGroup;
import org.osate.aadl.evaluator.project.Component;
import org.osate.aadl.evaluator.project.Subcomponent;

public class FuncionalityReportFiller 
{
    public static final String SOFTWARE_INSIDE = "Integrated Software";
    public static final String SOFTWARE_WRAPPER = "Software Wrapper";
    public static final String HARDWARE_WRAPPER = "Hardware Conversor";
    
    private FuncionalityReportFiller()
    {
        // do nothing
    }
    
    public static void fill( EvolutionReport report , Component component )
    {
        Map<String,Integer> counter = new HashMap<>();
        counter.put( SOFTWARE_INSIDE  , 0 );
        counter.put( SOFTWARE_WRAPPER , 0 );
        counter.put( HARDWARE_WRAPPER , 0 );
        
        // --- //
        
        for( Subcomponent sub : component.getSubcomponentsAll().values() )
        {
            if( sub.isDevice() && hasProcessorsAndThreadsInside( sub.getComponent() ) )
            {
                add( counter , SOFTWARE_INSIDE );
            }
            else if( isWrapper( sub ) )
            {
                add( counter , isSoftwareWrapper( sub.getComponent() ) 
                    ? SOFTWARE_WRAPPER 
                    : HARDWARE_WRAPPER
                );
            }
        }
        
        // --- //
        
        ReportGroup group = report.getGroup( "functionality" );
        
        group.addValue( SOFTWARE_INSIDE  , counter.get( SOFTWARE_INSIDE  ) , true , false );
        group.addValue( SOFTWARE_WRAPPER , counter.get( SOFTWARE_WRAPPER ) , true , true );
        group.addValue( HARDWARE_WRAPPER , counter.get( HARDWARE_WRAPPER ) , true , true );
    }
    
    private static Map<String,Integer> add( Map<String,Integer> counter , String key )
    {
        counter.put( key , counter.get( key ) + 1 );
        return counter;
    }
    
    private static boolean isWrapper( Subcomponent sub )
    {
        return sub.getName().toUpperCase().startsWith( "WRAPPER_" );
    }
    
    private static boolean hasProcessorsAndThreadsInside( Component component )
    {
        if( component == null )
        {
            return false;
        }
        
        for( Subcomponent sub : component.getSubcomponentsAll().values() )
        {
            if( sub.isProcessor() || sub.isThread() )
            {
                return true;
            }
        }
        
        return false;
    }
    
    private static boolean isSoftwareWrapper( Component component )
    {
        return component != null 
            && Component.TYPE_PROCESS.equalsIgnoreCase( component.getType() );
    }
    
}