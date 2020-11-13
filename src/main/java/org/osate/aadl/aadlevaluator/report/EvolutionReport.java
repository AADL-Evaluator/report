package org.osate.aadl.aadlevaluator.report;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;
import org.osate.aadl.evaluator.evolution.Evolution;

public class EvolutionReport implements Cloneable
{
    private int ranking;
    private Evolution evolution;
    
    private final String name;
    private final Map<String,ReportGroup> groups;
    private final Map<String,BigDecimal> factor;
    
    public EvolutionReport( final String name ) 
    {
        this.name = name;
        this.factor = new TreeMap<>( String.CASE_INSENSITIVE_ORDER );
        this.groups = new TreeMap<>( String.CASE_INSENSITIVE_ORDER );
    }

    public Evolution getEvolution()
    {
        return evolution;
    }

    public EvolutionReport setEvolution( Evolution evolution )
    {
        this.evolution = evolution;
        return this;
    }

    public int getRanking()
    {
        return ranking;
    }

    public void setRanking( int ranking )
    {
        this.ranking = ranking;
    }

    public EvolutionReport setFactor( Map<String,BigDecimal> factor )
    {
        this.factor.clear();
        this.factor.putAll( factor );
        
        return this;
    }

    public BigDecimal getFactor( String name )
    {
        return factor.containsKey( name ) 
            ? factor.get( name )
            : BigDecimal.ZERO;
    }

    public Map<String,BigDecimal> getFactor()
    {
        return factor;
    }
    
    public String getName() 
    {
        return name;
    }

    public Map<String,ReportGroup> getGroups()
    {
        return groups;
    }
    
    public ReportGroup getGroup( String name )
    {
        if( groups.containsKey( name ) && groups.get( name ) != null )
        {
            return groups.get( name );
        }
        else
        {
            ReportGroup g = new ReportGroup( name );
            groups.put( name , g );
            
            return g;
        }
    }
    
    @Override
    public EvolutionReport clone()
    {
        try
        {
            return (EvolutionReport) super.clone();
        }
        catch( Exception err )
        {
            EvolutionReport cloned = new EvolutionReport( name );
            cloned.setFactor( factor );
            cloned.setEvolution( evolution );
            cloned.getGroups().putAll( groups );
            
            return cloned;
        }
    }

    @Override
    public String toString() 
    {
        StringBuilder builder = new StringBuilder();
        
        builder.append( "name: " );
        builder.append( name );
        builder.append( "\n" );
        
        for( ReportGroup group : groups.values() )
        {
            builder.append( group.toString( " " ) );
        }
        
        return builder.toString();
    }
    
}