package org.osate.aadl.aadlevaluator.report.evaluate;

import java.math.BigDecimal;
import org.osate.aadl.evaluator.unit.UnitUtils;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.osate.aadl.evaluator.project.Component;
import org.osate.aadl.evaluator.project.Property;
import org.osate.aadl.evaluator.project.Subcomponent;
import org.osate.aadl.evaluator.unit.TimeUtils;

public class MipsEvaluate
{
    // (todos estes dados são propriedades)
    // sei::mipsbudget      usado da CPU em MIPS        THREAD
    // SEI::MIPSCapacity    capacidade total em MIPS    CPU
    
    /*
    properties:
        Processor_Properties::Processor_Frequency => 168 Mhz;
    
        actual_processor_binding => (reference (cpu)) applies to
        image_acquisition, obstacle_detection, speed_voter, speed_ctrl, panel_controller, entertainment;
    */
    
    public static final String CONNECTION_BINDING = "actual_processor_binding";
    public static final String REFERENCE = "(reference ({0})) applies to";
    public static final String CONNECTION_SEPARATOR = ",";
    
    public static final String PROPERTY_MIPS_USAGE    = "SEI::MIPSBudget";
    public static final String PROPERTY_MIPS_CAPACITY = "SEI::MIPSCapacity";
    public static final String PROPERTY_FREQUENCY     = "Processor_Properties::Processor_Frequency";
    
    private final Component system;

    public MipsEvaluate( Component system )
    {
        this.system = system;
    }
    
    public List<MipsResult> evaluate()
    {
        System.out.println( "[MIPS] Evaluating..." );
        
        // 1º) detectar os processadores existentes
        // 2º) verificar as conexões existentes
        // 3º) verificar os processos conectados
        // 4º) verificar as threads conectados
        // 5º) recuperar e somar a propriedades MIPS
        
        List<MipsResult> results = new LinkedList<>();
        
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
    
    private MipsResult evaluate( Subcomponent subcomponent )
    {
        System.out.println( "[MIPS] subcomponent..: " + subcomponent.getName() );
        
        String capacity  = getValue( subcomponent , PROPERTY_MIPS_CAPACITY );
        String frequency = getValue( subcomponent , PROPERTY_FREQUENCY     );
        
        if( UnitUtils.isEmpty( capacity ) )
        {
            capacity = "100 MIPS";
        }
        
        System.out.println( "[MIPS]    capacity...: " + capacity  );
        System.out.println( "[MIPS]    frequency..: " + frequency );
        
        MipsResult result = new MipsResult( 
            subcomponent ,
            0 ,
            UnitUtils.getValue( capacity )
        );
        
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
                    evaluate( 
                        UnitUtils.getValue( capacity ) , 
                        process.trim() 
                    )
                );
            }
        }
        
        System.out.println( "[MIPS]    result min.: " + result.getValueMinStr() );
        System.out.println( "[MIPS]    result max.: " + result.getValueMaxStr() );
        
        return result;
    }
    
    private MipsResult evaluate( double ips , String processName )
    {
        System.out.println( "[MIPS]        process name.: " + processName );
        
        Subcomponent subcomponent = system.getSubcomponent( processName );
        if( subcomponent == null )
        {
           System.out.println( "[MIPS]           ERROR.: The subcomponent " + processName + " was not found." );
            
           return new MipsResult( 
                processName , 
                "The subcomponent " + processName + " was not found." 
           );
        }
        
        Component process = subcomponent.getComponent();
        if( process == null )
        {
            System.out.println( "[MIPS]           ERROR.: The process " + subcomponent.getComponentReferenceName() + " was not found." );
            
           return new MipsResult(                    
                subcomponent.getComponentReferenceName() , 
                "The process " + subcomponent.getComponentReferenceName() + " was not found." 
           );
        }
        
        MipsResult processResult = new MipsResult( process );
        
        for( Subcomponent sub : process.getSubcomponentsAll().values() )
        {
            if( !sub.isThread() )
            {
                continue ;
            }
            
            MipsResult r = evaluateThread( ips , sub );
            System.out.println( "[MIPS]              value min.: " + r.getValueMinStr() );
            System.out.println( "[MIPS]              value max.: " + r.getValueMaxStr() );
            
            processResult.getResults().add( r );
        }
        
        return processResult;
    }
    
    private MipsResult evaluateThread( double ips , Subcomponent subThread )
    {
        System.out.println( "[MIPS]           thread.: " + subThread.getName() );
        
        Component thread = subThread.getComponent();
        if( thread == null )
        {
            System.out.println( "[MIPS]              ERROR.: The thread " + subThread.getComponentReferenceName() + " was not found." );
            
            return new MipsResult( 
                subThread.getName() , 
                "The thread " + subThread.getComponentReferenceName() + " was not found."
            );
        }
        
        List<Property> mips = thread.getProperty( PROPERTY_MIPS_USAGE );
        List<Property> execute = thread.getProperty( ExecuteTimeEvaluate.PROPERTY_EXECUTION_TIME );
        
        if( mips.isEmpty() && execute.isEmpty() )
        {
            System.out.println( "[MIPS]              ERROR.: The properties " 
                + PROPERTY_MIPS_USAGE + " and " 
                + ExecuteTimeEvaluate.PROPERTY_EXECUTION_TIME 
                + " were not found." 
            );
            
            return new MipsResult( 
                thread , 
                "The properties " + PROPERTY_MIPS_USAGE + " and " 
                + ExecuteTimeEvaluate.PROPERTY_EXECUTION_TIME 
                + " were not found." 
            );
        }
        else if( mips.isEmpty() )
        {
            // get the last proprety defined
            Map<String,String> value = execute.get( execute.size() - 1 ).getValueMinAndMax();
            System.out.println( "[MIPS]              execute time.: " + value );
            System.out.println( "[MIPS]              execute time.: min=" 
                + TimeUtils.convert( value.get( Property.MIN ) , "s" ) + ", max="
                + TimeUtils.convert( value.get( Property.MAX ) , "s" )
            );
            
            return new MipsResult( 
                thread ,
                TimeUtils.getValue( value.get( Property.MIN ) , "s" ) * ips ,
                TimeUtils.getValue( value.get( Property.MAX ) , "s" ) * ips ,
                0
            );
        }
        else
        {
            // get the last proprety defined
            String value = mips.get( mips.size() - 1 ).getValue();
            System.out.println( "[MIPS]              value.: " + value );

            return new MipsResult( 
                thread ,
                UnitUtils.getValue( value )
            );
        }
    }
    
    private String getValue( Subcomponent subcomponent , String property )
    {
        Component cpu = subcomponent.getComponent();
        if( cpu == null )
        {
            System.out.println( "[MIPS]    ERROR.: o component " + subcomponent.getComponentReferenceName() + " was not found." );
            return "";
        }
        
        List<Property> capacities = cpu.getProperty( property );
        if( capacities.isEmpty() )
        {
            System.out.println( "[MIPS]    ERROR.: the property " + property  + " was not found." );
            return "";
        }
        
        // get last property defined
        return capacities.get( capacities.size() - 1 ).getValue();
    }
    
    private double getValueDouble( Subcomponent subcomponent , String property )
    {
        return UnitUtils.getValue(
            getValue( subcomponent , property )
        );
    }
    
    // -----------------------------------------------
    // -----------------------------------------------
    // -----------------------------------------------
    
    public class MipsResult
    {
        private final Object element;
        private final List<MipsResult> results;
        private final BigDecimal valueMin;
        private final BigDecimal valueMax;
        private final double capacity;
        private final String error;

        public MipsResult( Object element )
        {
            this.element  = element;
            this.results  = new LinkedList<>();
            this.valueMin = BigDecimal.ZERO;
            this.valueMax = BigDecimal.ZERO;
            this.capacity = 0;
            this.error    = null;
        }

        public MipsResult( Object element , double value )
        {
            this.element  = element;
            this.results  = new LinkedList<>();
            this.valueMin = BigDecimal.valueOf( value );
            this.valueMax = BigDecimal.valueOf( value );
            this.capacity = 0;
            this.error    = null;
        }

        public MipsResult( Object element , double value , double capacity )
        {
            this.element = element;
            this.results = new LinkedList<>();
            this.valueMin = BigDecimal.valueOf( value );
            this.valueMax = BigDecimal.valueOf( value );
            this.capacity = capacity;
            this.error   = null;
        }

        public MipsResult( Object element , double min , double max , double capacity )
        {
            this.element = element;
            this.results = new LinkedList<>();
            this.valueMin = BigDecimal.valueOf( min );
            this.valueMax = BigDecimal.valueOf( max );
            this.capacity = capacity;
            this.error   = null;
        }

        public MipsResult( Object element , String error )
        {
            this.element = element;
            this.results = new LinkedList<>();
            this.valueMin = BigDecimal.ZERO;
            this.valueMax = BigDecimal.ZERO;
            this.capacity = 0;
            this.error = error;
        }
        
        public Object getElement()
        {
            return element;
        }
        
        public List<MipsResult> getResults()
        {
            return results;
        }

        public BigDecimal getValueMin()
        {
            return results.isEmpty() 
                ? valueMin
                : getTotalMin();
        }

        public BigDecimal getValueMax()
        {
            return results.isEmpty() 
                ? valueMax
                : getTotalMax();
        }

        public String getValueMinStr()
        {
            return results.isEmpty() 
                ? valueMin + " MIPS"
                : getTotalMin() + " MIPS";
        }

        public String getValueMaxStr()
        {
            return results.isEmpty() 
                ? valueMax + " MIPS"
                : getTotalMax() + " MIPS";
        }

        public double getCapacity()
        {
            return capacity;
        }

        public String getCapacityStr()
        {
            return capacity + " MIPS";
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
        
        private BigDecimal getTotalMin()
        {
            BigDecimal total = BigDecimal.ZERO;
            
            for( MipsResult r : getResults() )
            {
                total = total.add( r.getValueMin() );
            }
            
            return total;
        }
        
        private BigDecimal getTotalMax()
        {
            BigDecimal total = BigDecimal.ZERO;
            
            for( MipsResult r : getResults() )
            {
                total = total.add( r.getValueMax() );
            }
            
            return total;
        }
    }
}