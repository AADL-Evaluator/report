package org.osate.aadl.aadlevaluator.report.filler;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.osate.aadl.aadlevaluator.report.EvolutionReport;
import org.osate.aadl.aadlevaluator.report.ReportGroup;
import org.osate.aadl.evaluator.project.Component;
import org.osate.aadl.evaluator.project.Property;
import org.osate.aadl.evaluator.project.Subcomponent;
import org.osate.aadl.evaluator.reqspec.Compute;
import org.osate.aadl.evaluator.reqspec.SystemRequirement;
import org.osate.aadl.evaluator.reqspec.SystemRequirements;

public class PropertyReportFiller 
{
    public static final String VALUE_MAX = "max";
    public static final String VALUE_MIN = "min";
    public static final String ATTRIBUTE = "attribute";
    public static final String LESS_IS_BETTER = "lessIsBetter";
    public static final String GROUP_DEFAULT = "performance";
    
    private static final Logger LOG = Logger.getLogger(PropertyReportFiller.class.getName() );
    
    private PropertyReportFiller()
    {
        // do nothing
    }
    
    public static void fill( EvolutionReport report , Component component )
    {
        System.out.println( "[PROPERTY REPORT]" );
        
        for( Subcomponent sub : component.getSubcomponentsAll().values() )
        {
            Component c = sub.getComponent();
            
            if( c == null )
            {
                LOG.log( Level.WARNING , "The component {0} was not found." , sub.getComponentReferenceName() );
            }
            else
            {
                for( Property property : c.getPropertiesAll() )
                {
                    fill( report , component , property );
                }
            }
        }
    }
    
    public static void fill( EvolutionReport report , Component component , Property property )
    {
        LOG.log( 
            Level.INFO , 
            "Looking for {0} in {1}", new Object[]{
                property.getName(), 
                component.getFullName()
            }
        );
        
        if( component.getRequirements().isEmpty() )
        {
            LOG.log( Level.WARNING , "Reqspec doesn't not exist." );
            return ;
        }
        
        for( SystemRequirements reqs : component.getRequirements() )
        {
            for( SystemRequirement req : reqs.getRequirements().values() )
            {
                for( Compute compute : req.getComputeds() )
                {
                    if( compute.getValue().toUpperCase().endsWith( property.getName().toUpperCase() ) )
                    {
                        LOG.log( 
                            Level.INFO , 
                            "A requirement was found: {0}", 
                            req.getName()
                        );
                        
                        fill( report , property , req );
                        
                        return ;
                    }
                }
            }
        }
        
        LOG.log( 
            Level.INFO , 
            "The requirement for property {0} was not found. Trying to find val's...", 
            property.getName()
        );
        
        String references[] = ReferenceUtils.get( 
            component , 
            getValBaseName( property.getName() ) 
        );
        
        if( references != null 
            && (references[0] != null || references[1] != null) )
        {
            ReportGroup group = report.getGroup( GROUP_DEFAULT );
            
            group.addValue( 
                getTitle( property.getName() ) , 
                property.getName() ,
                property.getValue() ,
                true , 
                true , 
                references
            );
        }
    }
    
    public static void fill( EvolutionReport report , Property property , SystemRequirement req )
    {
        Map<String,String> constants = req.getConstantsMap();
        
        // ---- validate
        
        if( !constants.containsKey( ATTRIBUTE ) )
        {
            LOG.log( 
                Level.WARNING , 
                "The val {0} was not found, but it is mandatory." ,
                ATTRIBUTE
            );
            
            return ;
        }
        
        if( !constants.containsKey( VALUE_MIN ) && !constants.containsKey( VALUE_MAX ) )
        {
            LOG.log( 
                Level.WARNING , 
                "The val {0} and {1} were not found, but one of them should be declared." ,
                new Object[]{ VALUE_MIN , VALUE_MAX }
            );
            
            return ;
        }
        
        String category = req.getCategory() == null || req.getCategory().trim().isEmpty()
            ? GROUP_DEFAULT
            : req.getCategory().toLowerCase();
        
        LOG.log( 
            Level.INFO , 
            "Category: {0}", 
            category
        );
        
        // ---- Create/Get Group
        
        String[] parts = category.split( Pattern.quote( "." ) );
        
        LOG.log( 
            Level.INFO , 
            "Parts: {0}", 
            Arrays.toString( parts )
        );
        
        ReportGroup group = report.getGroup( parts[0] );
        
        if( parts.length > 1 )
        {
            for( int i = 1 ; i < parts.length ; i++ )
            {
                if( group.getSubgroup( parts[ i ] ) == null )
                {
                    group.addSubgroup( new ReportGroup( parts[ i ] ) );
                }
                
                group = group.getSubgroup( parts[ i ] );
            }
        }
        
        LOG.log( 
            Level.INFO , 
            "Group: {0}", 
            group.getName()
        );
        
        // ----
        
        boolean lessIsBetter = true;
        
        try
        {
            lessIsBetter = Boolean.parseBoolean( constants.get( LESS_IS_BETTER ).trim() );
        }
        catch( Exception err )
        {
            LOG.log( Level.SEVERE , "Error when try to convert variable lessIsBetter" , err );
        }
        
        final String reference[] = new String[ 2 ];
        reference[0] = constants.containsKey( VALUE_MIN ) ? constants.get( VALUE_MIN ) : null;
        reference[1] = constants.containsKey( VALUE_MAX ) ? constants.get( VALUE_MAX ) : null;
        
        // ---- addd as value of group
        switch ( property.getValueType() )
        {
            case Property.TYPE_ARRAY:
                
                int counter = 0;
                for( String value : property.getValueArray() )
                {
                    group.addValue(
                            (constants.containsKey( ATTRIBUTE ) ? constants.get( ATTRIBUTE ) : "") + " [" + counter + "]" ,
                            property.getName() + "_" + counter ,
                            value ,
                            true ,
                            lessIsBetter ,
                            reference
                    );
                    
                    counter++;
                }   break;
                
            case Property.TYPE_OBJECT:
                
                for( Map.Entry<String,Property> entry : property.getValueObject().entrySet() )
                {
                    group.addValue(
                            (constants.containsKey( ATTRIBUTE ) ? constants.get( ATTRIBUTE ) : "") + " (" + entry.getKey().toUpperCase() + ")" ,
                            property.getName() + "_" + entry.getKey() ,
                            entry.getValue().getValue() ,
                            true ,
                            lessIsBetter ,
                            reference
                    );
                }
                break;
                
            case Property.TYPE_MIN_MAX:
                
                for( Map.Entry<String,String> entry: property.getValueMinAndMax().entrySet() )
                {
                    group.addValue(
                            (constants.containsKey( ATTRIBUTE ) ? constants.get( ATTRIBUTE ) : "") + " (" + entry.getKey().toUpperCase() + ")" ,
                            property.getName() + "_" + entry.getKey() ,
                            entry.getValue() ,
                            true ,
                            lessIsBetter ,
                            reference
                    );
                }
                break;
                
            default:
                
                group.addValue(
                        constants.containsKey( ATTRIBUTE ) ? constants.get( ATTRIBUTE ) : "" ,
                        property.getName() ,
                        property.getValue() ,
                        true ,
                        lessIsBetter ,
                        reference
                );
                break;
        }
    }
    
    public static String getValBaseName( String propertyName ) 
    {
        // A_B ou T::A_B
        final List<String> parts = new LinkedList<>();
        
        String[] names = propertyName.split( Pattern.quote( "_" ) );
        
        for( String name : names )
        {
            if( name != null )
            {
                while( name.contains( "::" ) )
                {
                    name = name.replace( "::" , "_" );
                }
                
                parts.add( name );
            }
        }
        
        return ReferenceUtils.getValName( parts , "" );
    }
    
    public static String getTitle( String propertyName ) 
    {
        // A_B ou T::A_B
        StringBuilder builder = new StringBuilder();
        
        String[] names = propertyName.split( Pattern.quote( "_" ) );
        
        for( String name : names )
        {
            if( name != null )
            {
                while( name.contains( "::" ) )
                {
                    name = name.replace( "::" , " - " );
                }
                
                builder.append( 
                    ReferenceUtils.upperFirst( name )
                ).append( " " );
            }
        }
        
        return builder.toString().trim();
    }
    
}