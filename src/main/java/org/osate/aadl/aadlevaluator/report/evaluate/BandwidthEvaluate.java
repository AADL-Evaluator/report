package org.osate.aadl.aadlevaluator.report.evaluate;

import org.osate.aadl.evaluator.unit.UnitUtils;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.osate.aadl.evaluator.project.Property;
import org.osate.aadl.evaluator.project.Component;
import org.osate.aadl.evaluator.project.Connection;
import org.osate.aadl.evaluator.project.Feature;
import org.osate.aadl.evaluator.project.Subcomponent;
import org.osate.aadl.evaluator.unit.SizeUtils;
import org.osate.aadl.evaluator.unit.TimeUtils;

public class BandwidthEvaluate
{
    // largura de banda consumida = transmition_time x Data x period. 
    // (todos estes dados são propriedades)
    // Transmition_time - propriedade do barramento
    // DATA e period sao propriedades do device
    // Avaliable_bandwidth é propriedade do barramento
    
    /*
    properties:
        actual_connection_binding => (reference (can1)) applies to
			c00,c01,c02,c04,c13,c17,c16,c21,c11,c12;
    */
    
    public static final String CONNECTION_BINDING = "actual_connection_binding";
    public static final String REFERENCE = "(reference ({0})) applies to";
    public static final String CONNECTION_SEPARATOR = ",";
    
    public static final String PROPERTY_PERIOD    = "period";    // device
    public static final String PROPERTY_DATA_SIZE = "data_size"; // data
    //public static final String PROPERTY_TRANSMITION_TIME  = "compute_execution_time";   // bus
    
    public static final String PROPERTY_BUS_CONNECTION_MAX = "connection_max";
    public static final String PROPERTY_BUS_BANDWIDTH = "Bus_Properties::Bandwidth";
    public static final String PROPERTY_DEVICE_BANDWIDTH_AVALIABLE = "Bandwidth_Avaliable";
    
    public static final int DEFAULT_CONNECTION_MAX = 100;
    
    private final Component system;
    
    public BandwidthEvaluate( Component system )
    {
        this.system = system;
    }
    
    public List<BusResult> evaluate()
    {
        System.out.println( "[BANDWIDTH] Evaluating..." );
        
        // 1º) detectar os barramentos existentes
        // 2º) verificar as conexões existentes
        // 3º) verificar os devices conectados
        // 4º) calcular o consumo de cada device
        // 5º) verificar o consumo geral do barramento
        
        List<BusResult> results = new LinkedList<>();
        
        for( Subcomponent subcomponent : system.getSubcomponentsAll().values() )
        {
            if( !subcomponent.isBus() )
            {
                continue ;
            }
            
            results.add( evaluate( subcomponent ) );
        }
        
        return results;
    }
    
    private BusResult evaluate( Subcomponent busSubcomponent )
    {
        System.out.println( "[BANDWIDTH]    subcomponent: " + busSubcomponent );
        BusResult result = new BusResult( busSubcomponent );
        
        for( Property property : system.getProperties() )
        {
            // verify if property is a connection binding
            if( !CONNECTION_BINDING.equalsIgnoreCase( property.getName() ) )
            {
                continue ;
            }
            
            // "(reference ({0})) applies to"
            // "(reference ()) applies to"
            // "(reference (can1)) applies to"
            
            String ref = MessageFormat.format( REFERENCE , busSubcomponent.getName() );
            
            // verify if this connection binding referecing this bus
            if( !property.getValue().startsWith( ref ) )
            {
                continue ;
            }
            
            // get all connections
            String connectionStr = property.getValue().substring( ref.length() + 1 ).trim();
            for( String connection : connectionStr.split( CONNECTION_SEPARATOR ) )
            {
                // get the consumption of this connection
                result.getResults().add(
                    evaluate( busSubcomponent , connection.trim() )
                );
            }
        }
        
        return result;
    }
    
    private IndividualResult evaluate( Subcomponent busSubcomponent , String connectionName )
    {
        System.out.println( "[BANDWIDTH]         connection: " + connectionName );
        
        Connection connection = system.getConnection( connectionName );
        if( connection == null )
        {
            System.out.println( "[BANDWIDTH]            ERROR: The connection was not found." );
            
            return new IndividualResult( 
                connectionName , 
                connectionName , 
                "The connection was not found." 
            );
        }
        
        System.out.println( "[BANDWIDTH]            Evaluating side A:" );
        IndividualResult sideA = evaluate( busSubcomponent , connection , connection.getSubcomponentNameA() );
        
        System.out.println( "[BANDWIDTH]            Evaluating side B:" );
        IndividualResult sideB = evaluate( busSubcomponent , connection , connection.getSubcomponentNameB() );
        
        if( sideA.getMin() == null && sideA.getMax() == null 
            && sideB.getMin() == null && sideB.getMax() == null )
        {
            System.out.println( "[BANDWIDTH]            both side doesn't have all properties" );
            
            return new IndividualResult( 
                connectionName , 
                connectionName , 
                "both side doesn't have all properties" 
            );
        }
        else if( sideA.getMin() != null && sideA.getMax() != null )
        {
            System.out.println( "[BANDWIDTH]            it will used " + connection.getSubcomponentNameA() );
            return sideA;
        }
        else
        {
            System.out.println( "[BANDWIDTH]            it will used " + connection.getSubcomponentNameB() );
            return sideB;
        }
    }
    
    private IndividualResult evaluate( Subcomponent busSubcomponent , Connection connection , String side )
    {
        Subcomponent connector = system.getSubcomponent( side );
        if( connector == null )
        {
            System.out.println( "[BANDWIDTH]              ERROR: The subcomponent of the connection was not found." );
            
            return new IndividualResult( 
                connection.getName() , 
                connection.getSubcomponentNameA() , 
                "The subcomponent of the connection was not found." 
            );
        }
        
        System.out.println( "[BANDWIDTH]              subcomponent: " + connector.getName() );
        
        Component component = connector.getComponent();
        if( component == null )
        {
            System.out.println( "[BANDWIDTH]              ERROR: The component "+ connection.getComponentReferenceName() +" was not found." );
            
            return new IndividualResult( 
                connection.getName() , 
                connector.getComponent() , 
                "The component was not found." 
            );
        }
        
        Feature feature = component.getFeature( connection.getFeatureNameA() );
        if( feature == null )
        {
            System.out.println( "[BANDWIDTH]              ERROR: The feature " + connection.getFeatureNameA() + " was not found." );
            
            return new IndividualResult( 
                connection.getName() , 
                component , 
                "The feature was not found." 
            );
        }
        else if( feature.getComponent() == null )
        {
            System.out.println( "[BANDWIDTH]              ERROR: The component of the feature was not found." );
            
            return new IndividualResult( 
                connection.getName() , 
                component , 
                "The component of the feature was not found." 
            );
        }
        
        System.out.println( "[BANDWIDTH]              feature: " + feature.getName() );
        
        return find( busSubcomponent , connection , component , feature );
    }
    
    private IndividualResult find( Subcomponent busSubcomponent , Connection connection , Component component , Feature feature )
    {
        for( Connection c : component.getConnectionsAll().values() )
        {
            if( feature.getName().equalsIgnoreCase( c.getConnectionA() ) )
            {
                System.out.println( "[BANDWIDTH]                   inside of component, " + c.getSubcomponentAndFeatureB() + " output to this feature." );
                
                return evaluate( 
                    busSubcomponent , 
                    connection ,
                    c.getComponentB() , 
                    c.getFeatureB()
                );
            }
            else if( feature.getName().equalsIgnoreCase( c.getConnectionB() ) )
            {
                System.out.println( "[BANDWIDTH]                   inside of component, " + c.getSubcomponentAndFeatureA() + " output to this feature." );
                
                return evaluate( 
                    busSubcomponent , 
                    connection ,
                    c.getComponentA() , 
                    c.getFeatureA()
                );
            }
        }
        
        return evaluate( busSubcomponent , connection , component , feature );
    }
    
    private IndividualResult evaluate( Subcomponent busSubcomponent , Connection connection , Component component , Feature feature )
    {
        /*
        component:
            Period => 200ms;
            bandwith_avaliable => ( 1 , 2 , 3 );
        
        feature:
            data_size => 600 KByte;
        
        bus:
            bandwith => 140 000 Kbps;
        */
        
        List<Property> velocities = busSubcomponent.getComponent().getProperty( PROPERTY_BUS_BANDWIDTH );
        //List<Property> avaliables = component.getProperty( PROPERTY_DEVICE_BANDWIDTH_AVALIABLE );
        List<Property> periods    = component.getProperty( PROPERTY_PERIOD );
        List<Property> sizes      = feature.getComponent().getProperty( PROPERTY_DATA_SIZE );
        
        Set<String> erros = new LinkedHashSet<>();
        
        if( periods.isEmpty() )
        {
            String e = MessageFormat.format( 
                "The property {0} was not found in the device." , 
                PROPERTY_PERIOD 
            );
            
            System.out.println( "[BANDWIDTH]              ERROR: " + e );
            erros.add( e );
        }
        
        if( velocities.isEmpty() )
        {
            String e = MessageFormat.format( 
                "The property {0} was not found in the bus." , 
                PROPERTY_BUS_BANDWIDTH 
            );
            
            System.out.println( "[BANDWIDTH]              ERROR: " + e );
            erros.add( e );
        }
        
        /*
        i/f( avaliables.isEmpty() )
        {
            String e = MessageFormat.format( 
                "The property {0} was not found in the device." , 
                PROPERTY_DEVICE_BANDWIDTH_AVALIABLE 
            );
            
            System.out.println( "[BANDWIDTH]              ERROR: " + e );
            erros.add( e );
        }
        */
        
        if( sizes.isEmpty() )
        {
            String e = MessageFormat.format( 
                "The property {0} was not found in the data." , 
                PROPERTY_DATA_SIZE 
            );
            
            System.out.println( "[BANDWIDTH]              ERROR: " + e );
            erros.add( e );
        }
        
        // get the last property defined
        Property velocity = velocities.isEmpty() ? null : velocities.get( velocities.size() - 1 );
        Property period   = periods.isEmpty() ? null : periods.get( periods.size() - 1 );
        Property size     = sizes.isEmpty() ? null : sizes.get( sizes.size() - 1 );
        
        System.out.println( "[BANDWIDTH]              velocity.: " + velocity );
        System.out.println( "[BANDWIDTH]              period...: " + period   );
        System.out.println( "[BANDWIDTH]              size.....: " + size     );
        System.out.println( "[BANDWIDTH]              min......: " + min( period , size , velocity ) );
        System.out.println( "[BANDWIDTH]              max......: " + max( period , size , velocity ) );
        
        return new IndividualResult( 
            connection.getName() , 
            component , 
            min( period , size , velocity ) ,
            max( period , size , velocity ) ,
            erros
        );
    }
    
    // --------------------------------------- //
    // --------------------------------------- //
    // --------------------------------------- //
    
    public Map<String,String> min( Property period , Property size , Property bandwidth )
    {
        if( period == null || size == null )
        {
            return null;
        }
        
        return evaluate( 
            getValueStr( period    , Property.MIN ) ,
            getValueStr( size      , Property.MIN ) ,
            getValueStr( bandwidth , Property.MIN ) 
        );
    }
    
    public Map<String,String> max( Property period , Property size , Property bandwidth )
    {
        if( period == null || size == null )
        {
            return null;
        }
        
        return evaluate( 
            getValueStr( period    , Property.MAX ) ,
            getValueStr( size      , Property.MAX ) ,
            getValueStr( bandwidth , Property.MAX )
        );
    }
    
    /**
     * largura de banda consumida = transmition_time x Data x period.
     * 
     * 
     * @param period        periodo da transmissão
     * @param size          tamanho do dado
     * @param bandwidth     velocidade da banda disponivel
     * @return              largura de banda consumida (Kbps)
     */
    public Map<String,String> evaluate( String period , String size , String bandwidth )
    {
        // convert period and time to seconds
        // convert size to Kb (Kilobits)
        
        double result = SizeUtils.getValue( size , "Kb" ) 
            * (1.0 / TimeUtils.getValue( period , "s" ));
        
        double latency = UnitUtils.isEmpty( bandwidth ) || UnitUtils.getValue( bandwidth ) == 0
            ? 0
            : result / UnitUtils.getValue( bandwidth );
        
        /*
        double result = TimeUtils.getValue( period , "s" )
            * SizeUtils.getValue( velocity , "Kbps" )
            * SizeUtils.getValue( size , "Kb" );
        */
        
        Map<String,String> r = new LinkedHashMap<>();
        r.put( "period"   , TimeUtils.convert( period , "s" ) );
        r.put( "size"     , SizeUtils.convert( size , "Kb" ) );
        r.put( "latency"  , latency + " s" );
        r.put( "result"   , result + " Kbps" );
        
        //r.put( "velocity" , SizeUtils.convert( velocity , "Kbps" ) );
        
        return r;
    }
    
    public String getValueStr( Property property , String type )
    {
        return property.getValueType() == Property.TYPE_MIN_MAX
            ? property.getValueMinAndMax().get( type )
            : property.getValue();
    }
    
    // -----------------------------------------------
    // -----------------------------------------------
    // -----------------------------------------------
    
    public class BusResult
    {
        private final Subcomponent subcomponent;
        private final List<IndividualResult> results;
        private int connectionMax;
        private String bandwidth;
        
        public BusResult( Subcomponent subcomponent )
        {
            this.subcomponent = subcomponent;
            this.results = new LinkedList<>();
            
            init();
        }
        
        private void init()
        {
            if( subcomponent.getComponent() == null )
            {
                return ;
            }
            
            List<Property> maxs = subcomponent.getComponent().getProperty( PROPERTY_BUS_CONNECTION_MAX );
            List<Property> bands = subcomponent.getComponent().getProperty( PROPERTY_BUS_BANDWIDTH );
            
            connectionMax = maxs.isEmpty() 
                ? DEFAULT_CONNECTION_MAX 
                : Integer.parseInt( maxs.get( maxs.size() - 1 ).getValue() );
            
            bandwidth = bands.isEmpty() 
                ? "" 
                : bands.get( bands.size() - 1 ).getValue();
        }

        public Subcomponent getSubcomponent()
        {
            return subcomponent;
        }

        public String getBandwidth() 
        {
            return bandwidth;
        }
        
        public int getConnectionMax()
        {
            return connectionMax;
        }
        
        public Collection<IndividualResult> getResults()
        {
            return results;
        }
        
        public Collection<String> getDeviceCommonAvaliables()
        {
            Set<String> avaliables = new LinkedHashSet<>();
            
            for( IndividualResult result : getResults() )
            {
                if( avaliables.isEmpty() )
                {
                    avaliables.addAll(result.getBandwidthAvaliables() );
                }
                else
                {
                    Set<String> r = new LinkedHashSet<>();
                    
                    for( String a : avaliables )
                    {
                        if( result.getBandwidthAvaliables().contains( a ) )
                        {
                            r.add( a );
                        }
                    }
                    
                    if( r.isEmpty() )
                    {
                        return r;
                    }
                    else
                    {
                        avaliables = r;
                    }
                }
            }
            
            return avaliables;
        }
        
        public IndividualResult getTotal()
        {
            double min = 0;
            double max = 0;
            
            for( IndividualResult r : getResults() )
            {
                min += UnitUtils.getValue( r.getMin() );
                max += UnitUtils.getValue( r.getMax() );
            }
            
            // ------------------------ //
            
            return new IndividualResult( 
                "total" , 
                system , 
                min + " Kbps" , 
                max + " Kbps"
            );
        }
    }
    
    public class IndividualResult<T>
    {
        private final String connection;
        private final T device;
        private final String min;
        private final String max;
        private final Map<String,String> minParameter;
        private final Map<String,String> maxParameter;
        private final Set<String> errors;
        private final Set<String> bandwidthAvaliables;

        public IndividualResult( String connection , T device , Map<String,String> minParameter , Map<String,String> maxParameter )
        {
            this.connection = connection;
            this.device = device;
            this.min = minParameter.remove( "result" );
            this.max = maxParameter.remove( "result" );
            this.minParameter = minParameter;
            this.maxParameter = maxParameter;
            this.errors = new LinkedHashSet<>();
            this.bandwidthAvaliables = new LinkedHashSet<>();
        }
        
        public IndividualResult( String connection , T device , Map<String,String> minParameter , Map<String,String> maxParameter , Set<String> errors )
        {
            this.connection = connection;
            this.device = device;
            this.min = minParameter == null ? null : minParameter.remove( "result" );
            this.max = maxParameter == null ? null : maxParameter.remove( "result" );
            this.minParameter = minParameter;
            this.maxParameter = maxParameter;
            this.errors = errors;
            this.bandwidthAvaliables = new LinkedHashSet<>();
        }
        
        public IndividualResult( String connection , T device , String min , String max )
        {
            this.connection = connection;
            this.device = device;
            this.min = min;
            this.max = max;
            this.minParameter = null;
            this.maxParameter = null;
            this.errors = new LinkedHashSet<>();
            this.bandwidthAvaliables = new LinkedHashSet<>();
        }

        public IndividualResult( String connection , T device , String error ) 
        {
            this.connection = connection;
            this.device = device;
            this.min = null;
            this.max = null;
            this.minParameter = null;
            this.maxParameter = null;
            this.errors = new LinkedHashSet<>();
            this.bandwidthAvaliables = new LinkedHashSet<>();
            
            this.errors.add( error );
        }

        public IndividualResult( String connection , T device ) 
        {
            this.connection = connection;
            this.device = device;
            this.min = null;
            this.max = null;
            this.minParameter = null;
            this.maxParameter = null;
            this.errors = new LinkedHashSet<>();
            this.bandwidthAvaliables = new LinkedHashSet<>();
        }

        public String getConnection()
        {
            return connection;
        }
        
        public T getDevice()
        {
            return device;
        }
        
        public String getMin()
        {
            return min;
        }

        public String getMax()
        {
            return max;
        }

        public Collection<String> getErrors()
        {
            return errors;
        }

        public Map<String,String> getMaxParameter()
        {
            return maxParameter;
        }

        public Map<String,String> getMinParameter()
        {
            return minParameter;
        }

        public Set<String> getBandwidthAvaliables()
        {
            return bandwidthAvaliables;
        }
    }
    
}