package org.osate.aadl.aadlevaluator.report.evaluate;

import org.osate.aadl.evaluator.unit.UnitUtils;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.osate.aadl.evaluator.project.Component;
import org.osate.aadl.evaluator.project.Property;
import org.osate.aadl.evaluator.project.Subcomponent;
import org.osate.aadl.evaluator.unit.TimeUtils;

public class ExecuteTimeEvaluate 
{
    // (todos estes dados são propriedades)
    // Period                       periodicidade da thread
    // compute_execution_time       tempo de execução da thread
    //
    // razão: compute_execution_time / period
    
    /*
    properties:
        actual_processor_binding => (reference (cpu)) applies to
            image_acquisition, obstacle_detection, speed_voter, speed_ctrl, panel_controller, entertainment;
    */
    
    public static final String CONNECTION_BINDING = "actual_processor_binding";
    public static final String REFERENCE = "(reference ({0})) applies to";
    public static final String CONNECTION_SEPARATOR = ",";
    
    public static final String PROPERTY_PERIOD = "Period";
    public static final String PROPERTY_EXECUTION_TIME = "compute_execution_time";
    
    private final Component system;

    public ExecuteTimeEvaluate( Component system )
    {
        this.system = system;
    }
    
    public List<TimeResult> evaluate()
    {
        System.out.println( "[EXECUTE TIME] Evaluting..." );
        
        // 1º) detectar os processadores existentes
        // 2º) verificar as conexões existentes
        // 3º) verificar os processos conectados
        // 4º) verificar as threads conectados
        // 5º) recuperar e somar a propriedades MIPS
        
        List<TimeResult> results = new LinkedList<>();
        
        for( Subcomponent subcomponent : system.getSubcomponentsAll().values() )
        {
            if( !subcomponent.isProcessor() )
            {
                continue ;
            }
            
            results.add( evaluate( subcomponent ) );
        }
        
        return results;
    }
    
    private TimeResult evaluate( Subcomponent subcomponent )
    {
        System.out.println( "[EXECUTE TIME]   subcomponent: " + subcomponent.getName() );
        
        TimeResult result = new TimeResult( subcomponent );
        
        for( Property property : system.getProperties() )
        {
            // verify if property is a connection binding
            if( !CONNECTION_BINDING.equalsIgnoreCase( property.getName() ) )
            {
                continue ;
            }
            
            String ref = MessageFormat.format( REFERENCE , subcomponent.getName() );

            // verify if this connection binding referecing this bus
            if( !property.getValue().startsWith( ref ) )
            {
                continue ;
            }
            
            // get all connections
            String connectionStr = property.getValue().substring( ref.length() + 1 ).trim();
            for( String process : connectionStr.split( CONNECTION_SEPARATOR ) )
            {
                // get the consumption of this connection
                result.getResults().add(
                    evaluate( process.trim() )
                );
            }
        }
        
        return result;
    }
    
    private TimeResult evaluate( String processName )
    {
        System.out.println( "[EXECUTE TIME]      process: " + processName );
        
        Subcomponent subcomponent = system.getSubcomponent( processName );
        if( subcomponent == null )
        {
            System.out.println( "[EXECUTE TIME]         ERROR: The subcomponent " + processName + " was not found." );
            
           return new TimeResult( 
                processName , 
                "The subcomponent " + processName + " was not found." 
           );
        }
        
        Component process = subcomponent.getComponent();
        if( process == null )
        {
            System.out.println( "[EXECUTE TIME]         ERROR: The process " + subcomponent.getComponentReferenceName() + " was not found." );
            
           return new TimeResult( 
                subcomponent.getComponentReferenceName() , 
                "The process " + subcomponent.getComponentReferenceName() + " was not found." 
           );
        }
        
        TimeResult processResult = new TimeResult( process );
        
        for( Subcomponent sub : process.getSubcomponentsAll().values() )
        {
            if( !sub.isThread() )
            {
                continue ;
            }
            
            processResult.getResults().add( evaluateThread( sub ) );
        }
        
        return processResult;
    }
    
    private TimeResult evaluateThread( Subcomponent subThread  )
    {
        System.out.println( "[EXECUTE TIME]         Thread: " + subThread );
        
        Component thread = subThread.getComponent();
        if( thread == null )
        {
            System.out.println( "[EXECUTE TIME]           ERROR: The thread " + subThread.getComponentReferenceName() + " was not found." );
            
            return new TimeResult( 
                subThread.getName() , 
                "The thread " + subThread.getComponentReferenceName() + " was not found."
            );
        }
        
        List<Property> periods = thread.getProperty( PROPERTY_PERIOD );
        List<Property> times   = thread.getProperty( PROPERTY_EXECUTION_TIME );
        
        if( periods.isEmpty() )
        {
            System.out.println( "[EXECUTE TIME]           ERROR: The property " + PROPERTY_PERIOD + " was not found." );
            
            return new TimeResult( 
                thread , 
                "The property " + PROPERTY_PERIOD + " was not found." 
            );
        }
        
        if( times.isEmpty() )
        {
            System.out.println( "[EXECUTE TIME]           ERROR: The property " + PROPERTY_EXECUTION_TIME + " was not found." );
            
            return new TimeResult( 
                thread , 
                "The property " + PROPERTY_EXECUTION_TIME + " was not found." 
            );
        }
        
        // get the last proprety defined
        String value = periods.get( periods.size() - 1 ).getValue();
        Map<String,String> time = times.get( periods.size() - 1 ).getValueMinAndMax();
        
        System.out.println( "[EXECUTE TIME]           value: " 
            + value 
            + "     " + time.toString()
        );
        
        return new TimeResult( 
            thread ,
            value ,
            time.get( Property.MIN ) ,
            time.get( Property.MAX )
        );
    }
    
    // -----------------------------------------------
    // -----------------------------------------------
    // -----------------------------------------------
    
    public class TimeResult
    {
        private final Object element;
        private final List<TimeResult> results;
        private final String period;
        private final String timeMin;
        private final String timeMax;
        private final String error;

        public TimeResult( Object element )
        {
            this.element = element;
            this.results = new LinkedList<>();
            this.period  = null;
            this.timeMin = null;
            this.timeMax = null;
            this.error   = null;
        }

        public TimeResult( Object element , String period , String min , String max )
        {
            this.element = element;
            this.results = new LinkedList<>();
            this.period  = period;
            this.timeMin = min;
            this.timeMax = max;
            this.error   = null;
        }

        public TimeResult( Object element , String error )
        {
            this.element = element;
            this.results = new LinkedList<>();
            this.period  = null;
            this.timeMin = null;
            this.timeMax = null;
            this.error = error;
        }
        
        public Object getElement()
        {
            return element;
        }
        
        public List<TimeResult> getResults()
        {
            return results;
        }

        public String getPeriod()
        {
            return period;
        }

        public String getTimeMax()
        {
            return timeMax;
        }

        public String getTimeMin()
        {
            return timeMin;
        }
        
        public double getUsageMax()
        {
            String parts[] = UnitUtils.getValueAndUnit( period );
            
            return TimeUtils.getValue( timeMax , parts[1] ) 
                / Double.parseDouble( parts[0] ) 
                * 100;
        }

        public double getUsageMin()
        {
            String parts[] = UnitUtils.getValueAndUnit( period );
            
            return TimeUtils.getValue( timeMin , parts[1] ) 
                / Double.parseDouble( parts[0] ) 
                * 100;
        }
        
        public String getError()
        {
            return error;
        }

        public boolean isError()
        {
            return error != null 
                && !error.trim().isEmpty();
        }
        
    }
}