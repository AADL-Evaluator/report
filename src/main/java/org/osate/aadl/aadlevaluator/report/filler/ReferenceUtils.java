package org.osate.aadl.aadlevaluator.report.filler;

import org.osate.aadl.evaluator.project.Component;
import org.osate.aadl.evaluator.reqspec.SystemRequirement;
import org.osate.aadl.evaluator.reqspec.SystemRequirements;

public class ReferenceUtils 
{
    
    private ReferenceUtils()
    {
        // do nothing
    }
    
    public static String getMax( Component component , String... names )
    {
        return get( component , add( names , "max" ) );
    }
    
    public static String getMin( Component component , String... names )
    {
        return get( component , add( names , "min" ) );
    }
    
    private static String get( Component component , String... names )
    {
        String name = val( names );
        System.out.println( "[REFERENCE] Looking for " + name + " in " + component.getFullName() );
        
        if( component.getRequirements().isEmpty() )
        {
            System.out.println( "[REFERENCE][ERROR] Reqspec doesn't not exist." );
            return null;
        }
        
        for( SystemRequirements req : component.getRequirements() )
        {
            System.out.println( "[REFERENCE]      looking in:"
                + "\n\tname..: " + req.getName()
                + "\n\ttitle.: " + req.getTitle()
                + "\n\ttarget: " + req.getDescription() 
            );
            
            if( req.getComputeds().containsKey( name ) )
            {
                return req.getComputeds().get( name ).getValue();
            }
            else if( req.getConstants().containsKey( name ) )
            {
                return req.getConstants().get( name ).getValue();
            }
        }
        
        System.out.println( "[REFERENCE][ERROR] val/computed/constants " + name + " doesn't not exist." );
        return null;
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
    
    private static String val( String... names )
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
    
    private static String upperFirst( String str )
    {
        return str.substring( 0 , 1 ).toUpperCase() 
            + str.substring( 1 );
    }
    
}