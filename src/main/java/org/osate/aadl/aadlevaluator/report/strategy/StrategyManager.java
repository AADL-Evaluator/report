package org.osate.aadl.aadlevaluator.report.strategy;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.osate.aadl.aadlevaluator.report.ReportFactor;

public class StrategyManager 
{
    private static StrategyManager instance;
    private final Map<String,Strategy> calculates;

    private StrategyManager() 
    {
        this.calculates = new LinkedHashMap<>();
        
        add( new EqualStrategy() );
        add( new MoreIsLessStrategy() );
    }
    
    private void add( Strategy calculate )
    {
        calculates.put( calculate.getName() , calculate );
    }

    public static StrategyManager getInstance() 
    {
        if( instance == null )
        {
            instance = new StrategyManager();
        }
        
        return instance;
    }

    public static void clear() 
    {
        instance = null;
    }

    public Collection<String> getCalculateNames()
    {
        return calculates.keySet();
    }
    
    public void apply( String name , Collection<ReportFactor> factors )
    {
        if( !calculates.containsKey( name )
            || factors == null
            || factors.isEmpty() )
        {
            return ;
        }
        
        calculates.get( name ).apply( factors );
    }
    
    
}
