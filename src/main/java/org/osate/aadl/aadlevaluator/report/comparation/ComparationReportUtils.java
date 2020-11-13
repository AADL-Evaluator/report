package org.osate.aadl.aadlevaluator.report.comparation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import org.osate.aadl.aadlevaluator.report.EvolutionReport;
import org.osate.aadl.aadlevaluator.report.ReportGroup;
import org.osate.aadl.aadlevaluator.report.ReportValue;

public class ComparationReportUtils 
{
    
    private ComparationReportUtils()
    {
        // do nothing
    }
    
    public static Collection<ComparationReport> compare( EvolutionReport r1 , EvolutionReport r2 )
    {
        Map<String,ComparationReport> r = new TreeMap<>();
        
        r.put( "factors" , new ComparationReport( 
            "General" , 
            "Rank" , 
            ComparationUtils.compare( "factor" , r1.getRanking() , r2.getRanking() ) ,
            r1.getRanking() ,
            r2.getRanking()
        ) );
        
        r.put( "factors" , new ComparationReport( 
            "General" , 
            "Factor" , 
            ComparationUtils.compare( "factor" , r1.getFactor( "total" ) , r2.getFactor( "total" ) ) ,
            r1.getFactor( "total" ).setScale( 5 , RoundingMode.HALF_UP ) ,
            r2.getFactor( "total" ).setScale( 5 , RoundingMode.HALF_UP )
        ) );
        
        compare( r , r1 , r2 );     // procurar por grupos em comuns
        
        return r.values();
    }
    
    private static void compare( Map<String,ComparationReport> r , EvolutionReport r1 , EvolutionReport r2 )
    {
        for( ReportGroup group : r1.getGroups().values() )
        {
            compare( r , group , r2.getGroup( group.getName() ) , group.getName() );
        }
        
        for( ReportGroup group : r2.getGroups().values() )
        {
            onlyInOriginal( r , group , r1.getGroup( group.getName() ) , group.getName() );
        }
    }
    
    private static void compare( Map<String,ComparationReport> r , ReportGroup g1 , ReportGroup g2 , String characteristic )
    {
        if( g2 == null )
        {
            g2 = new ReportGroup( g1.getName() );
        }
        
        for( ReportGroup group : g1.getSubgroups().values() )
        {
            compare( r , group , g2.getSubgroup( group.getName() ) , characteristic );
        }
        
        for( ReportValue value : g1.getValues().values() )
        {
            if( !value.isImportant() )
            {
                continue ;
            }
            
            String key = key( characteristic , value );
            
            if( r.containsKey( key ) )
            {
                continue ;
            }
            
            Object value2 = g2.getValue( value.getName() ) == null
                ? zero( value.getValue() )
                : g2.getValue( value.getName() ).getValue();
            
            r.put( 
                key , 
                new ComparationReport( 
                    characteristic , 
                    value.getTitle() , 
                    ComparationUtils.compare( value.getTitle() , value.getValue() , value2 ) ,
                    value.getValue() , 
                    value2
                )
            );
        }
    }
    
    private static void onlyInOriginal( Map<String,ComparationReport> r , ReportGroup g1 , ReportGroup g2 , String characteristic )
    {
        if( g2 == null )
        {
            g2 = new ReportGroup( g1.getName() );
        }
        
        for( ReportGroup group : g1.getSubgroups().values() )
        {
            onlyInOriginal( r , group , g2.getSubgroup( group.getName() ) , characteristic );
        }
        
        for( ReportValue value : g1.getValues().values() )
        {
            if( !value.isImportant() )
            {
                continue ;
            }
            
            String key = key( characteristic , value );
            
            if( r.containsKey( key ) )
            {
                continue ;
            }
            
            Object value2 = zero( value.getValue() );
            
            r.put( 
                key , 
                new ComparationReport( 
                    characteristic , 
                    value.getTitle() , 
                    ComparationUtils.compare( value.getTitle() , value.getValue() , value2 ) ,
                    value2 ,
                    value.getValue()
                )
            );
        }
    }
    
    private static Object zero( Object value )
    {
        if( value instanceof String ) return "";
        if( value instanceof Double ) return 0.0;
        if( value instanceof Float  ) return 0.0f;
        if( value instanceof Byte   ) return (byte) 0;
        if( value instanceof Short  ) return (short) 0;
        if( value instanceof Integer ) return 0;
        if( value instanceof Long   ) return 0l;
        if( value instanceof BigDecimal ) return BigDecimal.ZERO;
        else return "";
    }
    
    private static String key( String characteristic , ReportValue value )
    {
        return characteristic + "." + value.getTitle();
    }
    
}