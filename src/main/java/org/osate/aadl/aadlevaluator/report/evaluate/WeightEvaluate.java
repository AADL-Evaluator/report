package org.osate.aadl.aadlevaluator.report.evaluate;

import java.util.LinkedList;
import java.util.List;
import org.osate.aadl.evaluator.project.Component;
import org.osate.aadl.evaluator.project.Property;
import org.osate.aadl.evaluator.project.Subcomponent;
import org.osate.aadl.evaluator.unit.WeightUtils;

public class WeightEvaluate
{
    public static final String[] PROPERTY_WEIGHT = { 
        "SEI::Weight" , 
        "Device_property::Mass" ,
        "Physical_Properties::Mass"
    };
    
    private final Component system;
    
    public WeightEvaluate( Component system )
    {
        this.system = system;
    }
    
    public WeightResult<Component> evaluate()
    {
        System.out.println( "[WEIGHT] evaluating..." );
        
        // pecorrer por todos os subcomponentes procurando
        // pela propriedade sobre o peso e, em seguida, somar
        // todos os valores
        
        return complete( 
            new WeightResult( system ) , 
            system 
        );
    }
    
    private WeightResult complete( WeightResult result , Component component )
    {
        if( component == null )
        {
            System.out.println( "[WEIGHT] component is null." );
            return result;
        }
        
        System.out.println( 
            "[WEIGHT] component  " 
            + component.getFullName() 
            + " (value): " + getValue( component ) 
        );
        
        result.setValue( getValue( component ) );
        
        for( Subcomponent sub : component.getSubcomponentsAll().values() )
        {
            result.getSubcomponents().add( 
                complete( new WeightResult<>( sub ) , sub.getComponent() ) 
            );
        }
        
        System.out.println( 
            "[WEIGHT] component  " 
            + component.getFullName() 
            + " (total): " + result.getTotal()
        );
        
        return result;
    }
    
    private String getValue( Component component )
    {
        List<Property> properties = component.getProperty( PROPERTY_WEIGHT );
        
        return properties.isEmpty() 
            ? null
            : properties.get( properties.size() - 1 ).getValue();
    }
    
    
    
    
    public class WeightResult<T> {
        private final List<WeightResult<Subcomponent>> subcomponents;
        private final T component;
        private String value;

        public WeightResult( T component ) {
            this.component = component;
            this.subcomponents = new LinkedList<>();
        }

        public List<WeightResult<Subcomponent>> getSubcomponents() {
            return subcomponents;
        }

        public T getComponent()
        {
            return component;
        }

        public void setValue( String value )
        {
            this.value = value;
        }
        
        public String getValue()
        {
            return value;
        }

        public String getTotal() 
        {
            String total = WeightUtils.sum( "0 Kg" , getValue() , "Kg" );
            
            for( WeightResult<Subcomponent> c : subcomponents )
            {
                total = WeightUtils.sum( total , c.getTotal() , "Kg" );
            }
            
            return total;
        }
    }
}