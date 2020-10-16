package org.osate.aadl.aadlevaluator.report.util;

import java.util.Comparator;
import org.osate.aadl.aadlevaluator.report.EvolutionReport;

public class FactorComparator implements Comparator<EvolutionReport>
{

    @Override
    public int compare( EvolutionReport o1 , EvolutionReport o2 )
    {
        return o1.getFactor().compareTo( o2.getFactor() );
    }
    
}
