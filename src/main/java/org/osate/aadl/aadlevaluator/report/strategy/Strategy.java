package org.osate.aadl.aadlevaluator.report.strategy;

import java.util.Collection;
import org.osate.aadl.aadlevaluator.report.ReportFactor;

public abstract class Strategy 
{
    private final String name;

    public Strategy( String name )
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    
    public abstract void apply( Collection<ReportFactor> factors );
    
}
