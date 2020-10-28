package org.osate.aadl.aadlevaluator.report.filler;

import java.util.List;
import java.util.Map;
import org.osate.aadl.aadlevaluator.report.EvolutionReport;
import org.osate.aadl.aadlevaluator.report.ReportGroup;
import org.osate.aadl.aadlevaluator.report.ReportValue;
import org.osate.aadl.evaluator.project.Component;
import org.osate.aadl.evaluator.project.Connection;
import org.osate.aadl.evaluator.project.Subcomponent;

public class ModifiabilityReportFiller 
{
    
    private ModifiabilityReportFiller()
    {
        // do nothing
    }
    
    public static void fill( EvolutionReport report , Map<String,List<String>> diff )
    {
        ReportGroup mainGroup = report.getGroup( "maintainability" );
        
        ReportGroup subGroup = new ReportGroup( "modifiability" );
        mainGroup.addSubgroup( subGroup );
        
        if( subGroup.getSubgroup( "subcomponents" ) == null )
        {
            subGroup.addSubgroup( new ReportGroup( "subcomponents" ) );
        }
        
        if( subGroup.getSubgroup( "connections" ) == null )
        {
            subGroup.addSubgroup( new ReportGroup( "connections" ) );
        }
        
        ReportGroup subs = subGroup.getSubgroup( "subcomponents" )
            .addValue( "Subcomponents Added"   , "added"   , 0 , false , false , ReportGroup.REFERENCE_NONE )
            .addValue( "Subcomponents Changed" , "changed" , 0 , false , false , ReportGroup.REFERENCE_NONE )
            .addValue( "Subcomponents Deleted" , "deleted" , 0 , false , false , ReportGroup.REFERENCE_NONE );
        
        ReportGroup cons = subGroup.getSubgroup( "connections" )
            .addValue( "Connections Added"   , "added"   , 0 , false , false , ReportGroup.REFERENCE_NONE )
            .addValue( "Connections Changed" , "changed" , 0 , false , false , ReportGroup.REFERENCE_NONE )
            .addValue( "Connections Deleted" , "deleted" , 0 , false , false , ReportGroup.REFERENCE_NONE );
        
        for( Map.Entry<String,List<String>> entry : diff.entrySet() )
        {
            for( String d : entry.getValue() )
            {
                if( d.contains( "subcomponent" ) )
                {
                    add( subs , entry.getKey() );
                }
                else if( d.contains( "connection" ) )
                {
                    add( cons , entry.getKey() );
                }
            }
        }
    }
    
    public static void fill( EvolutionReport report , Component component )
    {
        ReportGroup group = report.getGroup( "maintainability" );
        
        group.addSubgroup( createSubcomponents( component ) );
        group.addSubgroup( createConnections( component ) );
        
        report.getGroups().put( group.getName() , group );
    }
    
    private static ReportGroup createSubcomponents( Component component )
    {
        ReportGroup group = new ReportGroup( "subcomponents" );
        
        for( Subcomponent sub : component.getSubcomponentsAll().values() )
        {
            add( group , sub.getComponent().getType() );
        }
        
        group.addValue( 
            "Subcomponents Total" , 
            "total" , 
            component.getSubcomponentsAll().size() , 
            true , 
            true , 
            ReferenceUtils.get( component , "subcomponents" )
        );
        
        return group;
    }
    
    private static ReportGroup createConnections( Component component )
    {
        ReportGroup group = new ReportGroup( "connections" );
        
        for( Connection connection : component.getConnectionsAll().values() )
        {
            add( group , connection.getComponentA().getType() );
            add( group , connection.getComponentB().getType() );
        }
        
        group.addValue( 
            "Connections total" , 
            "total" , 
            component.getConnectionsAll().size() , 
            true , 
            true ,
            ReferenceUtils.get( component , "connections" )
        );
        
        return group;
    }
    
    private static void add( ReportGroup group , String name )
    {
        if( !group.getValues().containsKey( name ) )
        {
            group.addValue( 
                new ReportValue<>( name , 1 , false , false ) 
            );
        }
        else
        {
            ReportValue<Integer> value = group.getValue( name );
            value.setValue( value.getValue() + 1 );
        }
    }
    
}