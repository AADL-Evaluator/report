package org.osate.aadl.aadlevaluator.report.filler;

import org.osate.aadl.aadlevaluator.report.EvolutionReport;
import org.osate.aadl.aadlevaluator.report.ReportGroup;
import org.osate.aadl.aadlevaluator.report.evaluate.BandwidthEvaluate;
import org.osate.aadl.aadlevaluator.report.evaluate.MipsEvaluate;
import org.osate.aadl.aadlevaluator.report.evaluate.PriceEvaluate;
import org.osate.aadl.aadlevaluator.report.evaluate.WeightEvaluate;
import org.osate.aadl.evaluator.project.Component;
import org.osate.aadl.evaluator.project.Subcomponent;
import org.osate.aadl.evaluator.unit.UnitUtils;

public class Peformance 
{
    private Peformance()
    {
        // do nothing
    }
    
    public static void fill( EvolutionReport report , Component component )
    {
        System.out.println( "[PERFOMANCE]" );
        
        ReportGroup group = report.getGroup( "performance" );
        
        group.addSubgroup( createWeight( component ) );
        group.addSubgroup( createPrice ( component ) );
        group.addSubgroup( createCpu   ( component ) );
        group.addSubgroup( createBus   ( component ) );
    }
    
    private static ReportGroup createWeight( Component component )
    {
        ReportGroup group = new ReportGroup( "weight" );
        
        WeightEvaluate.WeightResult<Component> result = new WeightEvaluate( component )
            .evaluate();
        
        for( WeightEvaluate.WeightResult<Subcomponent> sub : result.getSubcomponents() )
        {
            group.addValue( sub.getComponent().getName() , sub.getTotal() , false , false );
        }
        
        group.addValue( "total" , result.getTotal() , true , true );
        
        return group;
    }
    
    private static ReportGroup createPrice( Component component )
    {
        System.out.println( "[PERFOMANCE][PRICE] evaluating..." );
        ReportGroup group = new ReportGroup( "price" );
        
        PriceEvaluate.PriceResult<Component> result = new PriceEvaluate( component )
            .evaluate();
        
        for( PriceEvaluate.PriceResult<Subcomponent> sub : result.getSubcomponents() )
        {
            group.addValue( sub.getComponent().getName() , sub.getTotal() , false , false );
        }
        
        group.addValue( "total" , result.getTotal() , true , true );
        
        return group;
    }
    
    private static ReportGroup createBus( Component component )
    {
        ReportGroup group = new ReportGroup( "bus" );
        
        BandwidthEvaluate eva = new BandwidthEvaluate( component );
        for( BandwidthEvaluate.BusResult result : eva.evaluate() )
        {
            ReportGroup busGroup = new ReportGroup( result.getSubcomponent().getName() );
            
            BandwidthEvaluate.IndividualResult total = result.getTotal();
            
            for( BandwidthEvaluate.IndividualResult sub : result.getResults() )
            {
                String name = sub.getDevice() == null 
                    ? "" 
                    : sub.getDevice() instanceof String
                        ? sub.getDevice().toString()
                        : ((Component) sub.getDevice()).getName();
                
                ReportGroup subGroup = new ReportGroup( sub.getConnection() );
                
                if( sub.getMinParameter() != null ) subGroup.addValue( "latency_min" , sub.getMinParameter().get( "latency" ) , false , false );
                if( sub.getMaxParameter() != null ) subGroup.addValue( "latency_max" , sub.getMaxParameter().get( "latency" ) , false , false );
                
                subGroup.addValue( "usage_min" , sub.getMin() , false , false );
                subGroup.addValue( "usage_max" , sub.getMax() , false , false );
                subGroup.addValue( "device" , name            , false , false );
                subGroup.addValue( "error"  , sub.getErrors() , false , false );
                
                busGroup.addSubgroup( subGroup );
            }
            
            
            
            busGroup.addValue( "connections_number" , result.getResults().size() , false , false );
            busGroup.addValue( "total_usage_min" , total.getMin() , true , true );
            busGroup.addValue( "total_usage_max" , total.getMax() , true , true );
            
            if( !UnitUtils.isEmpty( result.getBandwidth() ) )
            {
                double bandwidth = UnitUtils.getValue( result.getBandwidth() );
                
                busGroup.addValue( 
                    "total_latency_min" , 
                    UnitUtils.getValue( total.getMin() ) / bandwidth + " s" , 
                    true , 
                    true 
                );
                
                busGroup.addValue( 
                    "total_latency_max" , 
                    UnitUtils.getValue( total.getMax() ) / bandwidth + " s" , 
                    true , 
                    true 
                );
            }
            
            group.addSubgroup( busGroup );
        }
        
        return group;
    }
    
    private static ReportGroup createCpu( Component component )
    {
        ReportGroup group = new ReportGroup( "cpu" );
        
        MipsEvaluate eva = new MipsEvaluate( component );
        for( MipsEvaluate.MipsResult result : eva.evaluate() )
        {
            String name = result.getElement() instanceof Subcomponent
                ? ((Subcomponent) result.getElement()).getName()
                : result.getElement().toString();
            
            // ---- //
            
            ReportGroup cpuGroup = new ReportGroup( name );
            
            for( MipsEvaluate.MipsResult sub : result.getResults() )
            {
                cpuGroup.addValue( sub.getElement().toString() + "_min" , sub.getValueMinStr() , false , false );
                cpuGroup.addValue( sub.getElement().toString() + "_max" , sub.getValueMaxStr() , false , false );
            }
            
            cpuGroup.addValue( "connections_number" , result.getResults().size() , false , false );
            cpuGroup.addValue( "usage_min"          , result.getValueMinStr() , true  , true  );
            cpuGroup.addValue( "usage_max"          , result.getValueMaxStr() , true  , true  );
            
            group.addSubgroup( cpuGroup );
        }
        
        return group;
    }
    
}