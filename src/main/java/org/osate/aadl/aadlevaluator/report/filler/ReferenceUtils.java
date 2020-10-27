package org.osate.aadl.aadlevaluator.report.filler;

import java.util.Collection;
import org.osate.aadl.evaluator.project.Component;
import org.osate.aadl.evaluator.reqspec.SystemRequirements;

public class ReferenceUtils 
{
    
    private ReferenceUtils()
    {
        // do nothing
    }
    
    public static String[] get( Component component , String... names )
    {
        return get( component , getValName( names ) );
    }
    
    public static String[] get( Component component , String nameBased )
    {
        String valMin = nameBased + "Min";
        String valMax = nameBased + "Max";
        
        System.out.println( "[REFERENCE] Looking for " 
            + valMin 
            + " and " 
            + valMax 
            + " in " 
            + component.getFullName() 
        );
        
        if( component.getRequirements().isEmpty() )
        {
            System.out.println( "[REFERENCE][ERROR] Reqspec doesn't not exist." );
            return null;
        }
        
        String[] result = new String[ 2 ];
        
        for( SystemRequirements req : component.getRequirements() )
        {
            System.out.println( "[REFERENCE]      looking in: " + req.getName() );
            
            if( req.getComputeds().containsKey( valMin ) )
            {
                result[0] = req.getComputeds().get( valMin ).getValue();
            }
            else if( req.getConstants().containsKey( valMin ) )
            {
                result[0] = req.getConstants().get( valMin ).getValue();
            }
            
            if( req.getComputeds().containsKey( valMax ) )
            {
                result[1] = req.getComputeds().get( valMax ).getValue();
            }
            else if( req.getConstants().containsKey( valMax ) )
            {
                result[1] = req.getConstants().get( valMax ).getValue();
            }
        }
        
        System.out.println( result[0] == null 
            ? "[REFERENCE][ERROR] val " + valMin + " doesn't not exist." 
            : "[REFERENCE] val " + valMin + " is " + result[0] 
        );
        
        System.out.println( result[1] == null 
            ? "[REFERENCE][ERROR] val " + valMax + " doesn't not exist." 
            : "[REFERENCE] val " + valMax + " is " + result[1] 
        );
        
        return result;
    }
    
    public static String[] add( String[] arr , String newest ) 
    { 
        String newarr[] = new String[ arr.length + 1 ]; 
        
        for (int i = 0 ; i < arr.length ; i++ )
        {
            newarr[  i] = arr[ i ]; 
        }
  
        newarr[ newarr.length - 1 ] = newest; 
  
        return newarr; 
    } 
    
    public static String getValName( String... names )
    {
        StringBuilder val = new StringBuilder();
        
        for( String name : names )
        {
            val.append( val.length() == 0 
                ? name.toLowerCase() 
                : upperFirst( name )
            );
        }
        
        return val.toString();
    }
    
    public static String getValName( String[] names , String end )
    {
        StringBuilder val = new StringBuilder();
        
        for( String name : names )
        {
            val.append( val.length() == 0 
                ? name.toLowerCase() 
                : upperFirst( name )
            );
        }
        
        val.append( upperFirst( end ) );
        
        return val.toString();
    }
    
    public static String getValName( Collection<String> names , String end )
    {
        StringBuilder val = new StringBuilder();
        
        for( String name : names )
        {
            val.append( val.length() == 0 
                ? name.toLowerCase() 
                : upperFirst( name )
            );
        }
        
        val.append( upperFirst( end ) );
        
        return val.toString();
    }
    
    public static String upperFirst( String str )
    {
        if( str == null || str.trim().isEmpty() )
        {
            return "";
        }
        else
        {
            return str.substring( 0 , 1 ).toUpperCase() 
                + str.substring( 1 );
        }
    }
    
}