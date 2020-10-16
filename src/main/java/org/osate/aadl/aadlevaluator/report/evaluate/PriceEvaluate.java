package org.osate.aadl.aadlevaluator.report.evaluate;

import java.util.LinkedList;
import java.util.List;
import org.osate.aadl.evaluator.project.Component;
import org.osate.aadl.evaluator.project.Property;
import org.osate.aadl.evaluator.project.Subcomponent;
import org.osate.aadl.evaluator.unit.PriceUtils;

public class PriceEvaluate
{
    public static final String[] PROPERTY_PRICE = new String[]{
        "SEI::Price" , 
        "Device_property::Price" ,
        "Physical_Properties::Price"
    };
    
    private final Component system;
    
    public PriceEvaluate( Component system )
    {
        this.system = system;
    }
    
    public PriceResult<Component> evaluate()
    {
        // pecorrer por todos os subcomponentes procurando
        // pela propriedade sobre o peso e, em seguida, somar
        // todos os valores
        
        System.out.println( "[PRICE] evaluating..." );
        
        return complete( 
            new PriceResult( system ) , 
            system 
        );
    }
    
    private PriceResult complete( PriceResult result , Component component )
    {
        if( component == null )
        {
            System.out.println( "[PRICE] component is null." );
            return result;
        }
        
        System.out.println( "[PRICE] component " 
            + component.getFullName() 
            + " (value): " 
            + getValue( component ) 
        );
        
        result.setValue( getValue( component ) );
        
        for( Subcomponent sub : component.getSubcomponentsAll().values() )
        {
            result.getSubcomponents().add( 
                complete( new PriceResult<>( sub ) , sub.getComponent() ) 
            );
        }
        
        System.out.println( "[PRICE] component " 
            + component.getFullName() 
            + " (total): " 
            + result.getTotal()
        );
        
        return result;
    }
    
    private String getValue( Component component )
    {
        List<Property> properties = component.getProperty( PROPERTY_PRICE );
        
        return properties.isEmpty() 
            ? null
            : properties.get( properties.size() - 1 ).getValue();
    }
    
    
    
    
    public class PriceResult<T>
    {
        private final List<PriceResult<Subcomponent>> subcomponents;
        private final T component;
        private String value;

        public PriceResult( T component )
        {
            this.component = component;
            this.subcomponents = new LinkedList<>();
        }

        public List<PriceResult<Subcomponent>> getSubcomponents()
        {
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
            String total = PriceUtils.sum( "0 Dollar" , getValue() , "Dollar" );
            
            for( PriceResult<Subcomponent> c : subcomponents )
            {
                total = PriceUtils.sum( 
                    total , 
                    c.getTotal() , 
                    "Dollar" 
                );
            }
            
            return total;
        }
    }
}