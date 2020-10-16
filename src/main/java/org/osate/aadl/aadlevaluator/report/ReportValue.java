package org.osate.aadl.aadlevaluator.report;

public class ReportValue<T> implements Cloneable
{
    private boolean important;
    private boolean lessIsBetter;
    private String name;
    private T value;
    private T reference;

    public ReportValue( String name , T value )
    {
        this.name  = name;
        this.value = value;
        this.important = false;
    }

    public ReportValue( String name , T value , boolean important , boolean lessIsBetter )
    {
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

    public T getValue()
    {
        return value;
    }

    public ReportValue<T> setValue( T value )
    {
        this.value = value;
        return this;
    }

    public T getReference()
    {
        return reference;
    }

    public ReportValue<T> setReference( T reference )
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
        builder.append( reference );
        builder.append( "\n" );
        
        return builder.toString();
    }
    
}