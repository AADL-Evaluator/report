package org.osate.aadl.aadlevaluator.report;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReportFactor implements Cloneable
{
    private static final Logger LOG = Logger.getLogger( ReportFactor.class.getName() );
    
    private String characteristic;
    private String subcharacteristic;
    private String name;
    private String title;
    
    private BigDecimal min;
    private BigDecimal max;
    private String unit;
    private Object[] references;
    private BigDecimal weightDefined;
    private boolean lessIsBetter;
    
    public ReportFactor() 
    {
        this.lessIsBetter  = true;
        this.weightDefined = BigDecimal.ZERO;
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

    public ReportFactor setMin( double min )
    {
        this.min = BigDecimal.valueOf( min );
        return this;
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

    public ReportFactor setMax( double max )
    {
        this.max = BigDecimal.valueOf( max );
        return this;
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
            weightDefined = BigDecimal.ZERO;
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

    public Object[] getReferences()
    {
        return references;
    }

    public ReportFactor setReferences( Object[] references )
    {
        this.references = references;
        return this;
    }
    
    // -------- //
    // -------- //
    // -------- //
    
    public String getReferencesToString()
    {
        if( references == null ) return "";
        else if( references[0] == null && references[1] == null ) return "";
        else if( references[0] == null ) return "x <= " + references[1];
        else if( references[1] == null ) return references[ 0 ] + " <= x";
        else return references[ 0 ] + " <= x <= " + references[ 1 ];
    }
    
    public String getMinAndMaxToString()
    {
        if( getMin().compareTo( getMax() ) == 0 )
            {
                return getMinUnit();
            }
            else
            {
                return "[ " + getMinUnit() 
                    + " , " 
                    + getMaxUnit() + " ]";
            }
    }
    
    public String getMinUnit()
    {
        return min.setScale( 5 , RoundingMode.HALF_UP ) + " " + unit;
    }
    
    public String getMaxUnit()
    {
        return max.setScale( 5 , RoundingMode.HALF_UP ) + " " + unit;
    }
    
    public BigDecimal getWeightCalculated()
    {
        BigDecimal result = this.max.subtract( this.min );
        
        return result.doubleValue() == 0 
            ? new BigDecimal( 0.0 )
            : new BigDecimal( 1.0 ).divide( result , MathContext.DECIMAL128 );
    }
    
    public BigDecimal getWeightByValue( BigDecimal value )
    {
        // (value - this.min) / getWeightCalculated()
        return value
            .subtract( this.min )
            .multiply( getWeightCalculated() ).abs();
    }
    
    public BigDecimal getWeightGlobal( BigDecimal value )
    {
        if( weightDefined == null 
            || weightDefined.doubleValue() == 0
            || getWeightCalculated().doubleValue() == 0 )
        {
            return BigDecimal.ZERO;
        }
        
        BigDecimal result = getWeightByValue( value );
        
        LOG.log( Level.INFO , "[SCORE] score: {0} , value : {1} , min: {2} , max: {3} , lessIsBetter: {4}, subresult: {5}" , new Object[]{
            weightDefined , 
            value ,
            min ,
            max ,
            lessIsBetter ,
            result.setScale( 20 , RoundingMode.HALF_UP ).doubleValue()
        });
        
        return lessIsBetter 
            ? new BigDecimal( 1 )
                .subtract( result )
                .multiply( weightDefined ).abs()           //(1 - result) * userFactor
            : result.multiply( weightDefined ).abs();      // result * userFactor
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