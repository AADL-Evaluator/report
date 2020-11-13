package org.osate.aadl.aadlevaluator.report.filler;

import java.util.List;
import java.util.Map;
import org.osate.aadl.aadlevaluator.report.EvolutionReport;
import org.osate.aadl.evaluator.project.Component;

public class ReportFiller 
{
    
    private ReportFiller()
    {
        // do nothing
    }
    
    public static EvolutionReport fill( EvolutionReport report , Component component )
    {
        System.out.println( "[REPORT FILLER]" );
        
        ModifiabilityReportFiller.fill( report , component );
        FuncionalityReportFiller.fill( report , component );
        PropertyReportFiller.fill( report , component );
        PeformanceReportFiller.fill( report , component );
        
        return report;
    }
    
    public static EvolutionReport fill( EvolutionReport report , Map<String,List<String>> diff )
    {
        ModifiabilityReportFiller.fill( report , diff );
        
        return report;
    }
    
}