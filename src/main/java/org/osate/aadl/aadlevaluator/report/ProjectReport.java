package org.osate.aadl.aadlevaluator.report;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProjectReport 
{
    private final String name;
    private final Map<String,EvolutionReport> reports;
    private EvolutionReport resume;
    
    public ProjectReport( String name )
    {
        this.name    = name;
        this.reports = new LinkedHashMap<>();
        this.resume  = new EvolutionReport( "resume" );
    }
    
    public String getName()
    {
        return name;
    }

    public Map<String, EvolutionReport> getReports() 
    {
        return reports;
    }
    
    public ProjectReport add( EvolutionReport report )
    {
        this.reports.put( report.getName() , report );
        return this;
    }

    public EvolutionReport getResume() 
    {
        return resume;
    }

    public ProjectReport setResume( EvolutionReport resume )
    {
        this.resume = resume;
        return this;
    }
    
}