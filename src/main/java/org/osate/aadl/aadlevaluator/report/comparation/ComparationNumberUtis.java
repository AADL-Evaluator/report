package org.osate.aadl.aadlevaluator.report.comparation;

import java.text.MessageFormat;

public class ComparationNumberUtis
{

    private ComparationNumberUtis() 
    {
        // do nothing
    }
    
    public static String compare( String name , double valueA , double valueB ) 
    {
        if( valueA == valueB )
        {
            return MessageFormat.format(
                "The {0} are equals."
                , name
            );
        }
        
        double diff = getDiference( valueA , valueB );
        double perc = getPercentage( valueA , valueB );
        
        perc = perc < 1 
            ? 1 - perc      // 1 - 0,89 = 11%
            : perc - 1;     // 1,09 - 1 =  9%
        
        perc *= 100;
        
        return MessageFormat.format( diff < 0
            ? "The {0} has {1} more than original (equivalent to {2}%)."
            : "The {0} has {1} less than original (equivalent to {2}%)."
            , name , (diff < 0 ? diff * -1 : diff) , perc
        );
    }
    
    public static double getDiference( double valueA , double valueB )
    {
        return valueB - valueA;
    }
    
    public static double getPercentage( double valueA , double valueB )
    {
        return valueA == 0 
            ? 0
            : valueB / valueA;
    }
    
}