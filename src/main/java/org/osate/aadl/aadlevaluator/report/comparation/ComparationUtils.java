package org.osate.aadl.aadlevaluator.report.comparation;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Objects;
import org.osate.aadl.evaluator.unit.UnitUtils;

public class ComparationUtils 
{
    
    private ComparationUtils()
    {
        // do nothing
    }
    
    public static String compare( String name , Object valueA , Object valueB )
    {
        if( valueA == null && valueB == null )
        {
            return MessageFormat.format( "The {0} is equal." , name );
        }
        else if( valueA == null || valueB == null )
        {
            return MessageFormat.format( "One of them is null." , name );
        }
        else if( valueA instanceof String )
        {
            return compare( name , (String) valueA , (String) valueB );
        }
        else if( valueA instanceof Double )
        {
            return compare( name , (Double) valueA , (Double) valueB );
        }
        else if( valueA instanceof Float )
        {
            return compare( name , (Float) valueA , (Float) valueB );
        }
        else if( valueA instanceof Byte )
        {
            return compare( name , (Byte) valueA , (Byte) valueB );
        }
        else if( valueA instanceof Short )
        {
            return compare( name , (Short) valueA , (Short) valueB );
        }
        else if( valueA instanceof Integer )
        {
            return compare( name , (Integer) valueA , (Integer) valueB );
        }
        else if( valueA instanceof Long )
        {
            return compare( name , (Long) valueA , (Long) valueB );
        }
        else if( valueA instanceof BigDecimal )
        {
            return compare( 
                name , 
                ((BigDecimal) valueA).doubleValue() , 
                ((BigDecimal) valueB).doubleValue() 
            );
        }
        else
        {
            return MessageFormat.format( areTheyEquals( valueA , valueB ) 
                    ? "The {0} is equal." 
                    : "The {0} is difference." 
                , name
            );
        }
    }
    
    public static String compare( String name , Double valueA , Double valueB )
    {
        return ComparationNumberUtis.compare( name , valueA , valueB );
    }
    
    public static String compare( String name , Float valueA , Float valueB )
    {
        return ComparationNumberUtis.compare( name , valueA , valueB );
    }
    
    public static String compare( String name , Byte valueA , Byte valueB )
    {
        return ComparationNumberUtis.compare( name , valueA , valueB );
    }
    
    public static String compare( String name , Short valueA , Short valueB )
    {
        return ComparationNumberUtis.compare( name , valueA , valueB );
    }
    
    public static String compare( String name , Integer valueA , Integer valueB )
    {
        return ComparationNumberUtis.compare( name , valueA , valueB );
    }
    
    public static String compare( String name , Long valueA , Long valueB )
    {
        return ComparationNumberUtis.compare( name , valueA , valueB );
    }
    
    public static String compare( String name, String valueA , String valueB ) 
    {
        final String partsA[] = UnitUtils.getValueAndUnit( valueA );
        final String partsB[] = UnitUtils.getValueAndUnit( valueB );
        
        if( partsA[0].trim().isEmpty() || partsB[0].trim().isEmpty() )
        {
            return MessageFormat.format( areTheyEquals( valueA , valueB ) 
                    ? "The {0} is equal." 
                    : "The {0} is difference." 
                , name
            );
        }
        else
        {
            if( partsA[0].trim().isEmpty() ) partsA[0] = "0"; 
            if( partsB[0].trim().isEmpty() ) partsB[0] = "0"; 
            
            return compare( 
                name , 
                Double.parseDouble( partsA[0] ) , 
                Double.parseDouble( partsB[0] ) 
            );
        }
    }
    
    private static boolean areTheyEquals( Object valueA , Object valueB ) 
    {
        return Objects.equals( valueB , valueA );
    }
    
}
