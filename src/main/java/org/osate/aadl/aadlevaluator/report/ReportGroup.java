package org.osate.aadl.aadlevaluator.report;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReportGroup implements Cloneable
{
    private final String name;
    private final Map<String,ReportGroup> subgroups;
    private final Map<String,ReportValue> values;
    
    public ReportGroup( String name )
    {
        this.name = name;
        this.subgroups = new LinkedHashMap<>();
        this.values = new LinkedHashMap<>();
    }
    
    public ReportGroup addSubgroup( ReportGroup group )
    {
        this.subgroups.put( group.getName() , group );
        return this;
    }
    
    public ReportGroup addValue( ReportValue value )
    {
        this.values.put( value.getName() , value );
        return this;
    }
    
    public ReportGroup addValue( String name , Object value , boolean important , boolean lessIsBetter )
    {
        return addValue( new ReportValue( name , value , important , lessIsBetter ) );
    }
    
    public ReportGroup addValue( String name , Object value , boolean important , boolean lessIsBetter , String reference )
    {
        return addValue( 
            new ReportValue( name , value , important , lessIsBetter )
                .setReference( reference ) 
        );
    }
    
    public String getName() 
    {
        return name;
    }
    
    public ReportGroup getSubgroup( String name )
    {
        return subgroups.get( name );
    }
    
    public ReportValue getValue( String name )
    {
        return values.get( name );
    }
    
    public Map<String,ReportGroup> getSubgroups()
    {
        return subgroups;
    }

    public Map<String,ReportValue> getValues()
    {
        return values;
    }

    @Override
    public ReportGroup clone() 
    {
        try
        {
            return (ReportGroup) super.clone();
        }
        catch( Exception err )
        {
            return clone( this , new ReportGroup( name ) );
        }
    }
    
    protected static ReportGroup clone( ReportGroup from , ReportGroup target )
    {
        for( ReportGroup g : from.getSubgroups().values() )
        {
            target.addSubgroup( g.clone() );
        }

        for( ReportValue g : from.getValues().values() )
        {
            target.addValue( g.clone() );
        }
        
        return target;
    }
    
    @Override
    public String toString()
    {
        return toString( "" );
    }
    
    public String toString( final String espaco )
    {
        StringBuilder builder = new StringBuilder();
        builder.append( espaco );
        builder.append( "Group: " );
        builder.append( name );
        builder.append( "\n" );
        
        for( ReportGroup group : subgroups.values() )
        {
            builder.append( group.toString( espaco + "  " ) );
        }
        
        for( ReportValue value : values.values() )
        {
            builder.append( espaco );
            builder.append( "  "   );
            builder.append( value  );
        }
        
        return builder.toString();
    }
    
}