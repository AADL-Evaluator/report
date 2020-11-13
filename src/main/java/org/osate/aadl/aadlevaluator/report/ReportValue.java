package org.osate.aadl.aadlevaluator.report;

import java.math.BigDecimal;
import org.osate.aadl.evaluator.unit.UnitUtils;

public class ReportValue<T> implements Cloneable
{
    private boolean important;
    private boolean lessIsBetter;
    private String name;
    private String title;
    private T value;
    private T[] reference;

    public ReportValue( String name , T value )
    {
        this.title = name;
        this.name  = name;
        this.value = value;
        this.important = false;
    }

    public ReportValue( String name , T value , boolean important , boolean lessIsBetter )
    {
        this.title = name;
        this.name  = name;
        this.value = value;
        this.important = important;
        this.lessIsBetter = lessIsBetter;
    }

    public boolean isLessIsBetter()
    {
        return lessIsBetter;
    }

    public ReportValue<T> setLessIsBetter( boolean lessIsBetter )
    {
        this.lessIsBetter = lessIsBetter;
        return this;
    }

    public boolean isImportant()
    {
        return important;
    }

    public ReportValue<T> setImportant( boolean important )
    {
        this.important = important;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public ReportValue<T> setName( String name )
    {
        this.name = name;
        return this;
    }

    public String getTitle()
    {
        return title;
    }

    public ReportValue<T> setTitle( String title )
    {
        this.title = title;
        return this;
    }

    public T getValue()
    {
        return value;
    }

    public double getValueNumber()
    {
        if( value instanceof String )
        {
            String number = UnitUtils.getValueAndUnit( (String) value )[0];
            
            return number == null || number.trim().isEmpty() 
                ? 0 
                : Double.parseDouble( number );
        }
        else if( value instanceof Double )
        {
            return (Double) value;
        }
        else if( value instanceof Integer )
        {
            return (Integer) value;
        }
        else if( value instanceof BigDecimal )
        {
            return ((BigDecimal) value).doubleValue();
        }
        else
        {
            return 0;
        }
    }

    public String getValueUnit()
    {
        if( value instanceof String )
        {
            return UnitUtils.getValueAndUnit( (String) value )[1];
        }
        else
        {
            return "";
        }
    }

    public ReportValue<T> setValue( T value )
    {
        this.value = value;
        return this;
    }

    public T[] getReference()
    {
        return reference;
    }

    public T getReferenceMin()
    {
        return reference == null ? null : reference[0];
    }

    public T getReferenceMax()
    {
        return reference == null ? null : reference[1];
    }

    public ReportValue<T> setReference( T[] reference )
    {
        this.reference = reference;
        return this;
    }
    
    @Override
    public ReportValue clone()
    {
        try
        {
            return (ReportValue) super.clone();
        }
        catch( Exception err )
        {
            Object valueNew = value == null
                ? null
                : value instanceof ReportFactor 
                    ? ((ReportFactor) value).clone()
                    : value;
            
            return new ReportValue( 
                name , 
                valueNew , 
                important , 
                lessIsBetter
            ).setReference( reference );
        }
    }
    
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        
        builder.append( name );
        builder.append( ": " );
        builder.append( value );
        
        builder.append( "                less is better: " );
        builder.append( lessIsBetter );
        
        builder.append( ", important: " );
        builder.append( important );
        
        builder.append( ", reference: " );
        
        if( reference == null )
        {
            builder.append( "is null" );
        }
        else
        {
            builder.append( "{ min: " );
            builder.append( reference[0] );
            builder.append( ", max: " );
            builder.append( reference[1] );
            builder.append( " }" );
        }
        
        builder.append( "\n" );
        
        return builder.toString();
    }
    
}