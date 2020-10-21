package org.osate.aadl.aadlevaluator.report;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ReportFactor implements Cloneable
{
    private String characteristic;
    private String subcharacteristic;
    private String name;
    private String title;
    
    private BigDecimal min;
    private BigDecimal max;
    private String unit;
    private String reference;
    private BigDecimal weightDefined;
    private boolean lessIsBetter;
    
    public ReportFactor() 
    {
        this.lessIsBetter  = true;
        this.weightDefined = new BigDecimal( 0 );
    }

    public String getCharacteristic() 
    {
        return characteristic;
    }

    public ReportFactor setCharacteristic( String characteristic )
    {
        this.characteristic = characteristic;
        return this;
    }

    public String getSubcharacteristic() 
    {
        return subcharacteristic;
    }

    public ReportFactor setSubcharacteristic( String subcharacteristic )
    {
        this.subcharacteristic = subcharacteristic;
        return this;
    }

    public String getName() 
    {
        return name;
    }

    public ReportFactor setName( String name )
    {
        this.name = name;
        return this;
    }

    public String getTitle()
    {
        return title;
    }

    public ReportFactor setTitle( String title )
    {
        this.title = title;
        return this;
    }
    
    public BigDecimal getMin()
    {
        return min;
    }

    public ReportFactor setMin( BigDecimal min )
    {
        this.min = min;
        return this;
    }

    public BigDecimal getMax() 
    {
        return max;
    }

    public ReportFactor setMax( BigDecimal max )
    {
        this.max = max;
        return this;
    }

    public String getUnit()
    {
        return unit;
    }

    public ReportFactor setUnit( String unit )
    {
        this.unit = unit;
        return this;
    }

    public BigDecimal getWeightDefined()
    {
        if( weightDefined == null )
        {
            weightDefined = new BigDecimal( 0 );
        }
        
        return weightDefined;
    }

    public ReportFactor setWeightDefined( BigDecimal weightDefined )
    {
        this.weightDefined = weightDefined;
        return this;
    }

    public boolean isLessIsBetter()
    {
        return lessIsBetter;
    }

    public ReportFactor setLessIsBetter( boolean lessIsBetter )
    {
        this.lessIsBetter = lessIsBetter;
        return this;
    }

    public String getReference()
    {
        return reference == null ? "" : reference;
    }

    public ReportFactor setReference( String reference )
    {
        this.reference = reference;
        return this;
    }
    
    // -------- //
    // -------- //
    // -------- //
    
    public String getMinUnit()
    {
        return min.setScale( 4 , RoundingMode.HALF_EVEN ) + " " + unit;
    }
    
    public String getMaxUnit()
    {
        return max.setScale( 4 , RoundingMode.HALF_EVEN ) + " " + unit;
    }
    
    public BigDecimal getWeightCalculated()
    {
        BigDecimal result = this.max.subtract( this.min );
        
        return result.doubleValue() == 0 
            ? new BigDecimal( 0.0 )
            : new BigDecimal( 1.0 ).divide( result , 20 , RoundingMode.HALF_UP );
    }
    
    public BigDecimal getWeightByValue( BigDecimal value )
    {
        // (value - this.min) / getWeightCalculated()
        return value.subtract( this.min )
                .multiply( getWeightCalculated() );
    }
    
    public BigDecimal getWeightGlobal( BigDecimal value )
    {
        if( weightDefined == null 
            || weightDefined.doubleValue() == 0
            || getWeightCalculated().doubleValue() == 0 )
        {
            return new BigDecimal( 0 );
        }
        
        BigDecimal result = getWeightByValue( value );
        
        return lessIsBetter 
            ? new BigDecimal( 1 )
                .subtract( result )
                .multiply( weightDefined )             //(1 - result) * userFactor
            : result.multiply( weightDefined );        // result * userFactor
    }

    @Override
    public String toString() 
    {
        return getTitle();
    }

    @Override
    public ReportFactor clone() 
    {
        try
        {
            return (ReportFactor) super.clone();
        }
        catch( Exception err )
        {
            return new ReportFactor()
                .setLessIsBetter( lessIsBetter )
                .setMax( max )
                .setMin( min )
                .setTitle( title )
                .setCharacteristic( characteristic )
                .setUnit( unit )
                .setWeightDefined( weightDefined );
        }
    }
    
}