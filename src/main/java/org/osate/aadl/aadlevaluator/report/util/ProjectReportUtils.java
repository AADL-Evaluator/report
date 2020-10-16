package org.osate.aadl.aadlevaluator.report.util;

import java.io.File;
import java.nio.file.Files;
import org.osate.aadl.aadlevaluator.report.EvolutionReport;
import org.osate.aadl.aadlevaluator.report.ProjectReport;
import org.osate.aadl.aadlevaluator.report.filler.ReportFiller;
import org.osate.aadl.aadlevaluator.report.generator.ReportCSV;
import org.osate.aadl.aadlevaluator.report.generator.ReportHtml;
import org.osate.aadl.aadlevaluator.report.generator.ReportJSON;
import org.osate.aadl.aadlevaluator.report.generator.ReportYAML;
import org.osate.aadl.evaluator.evolution.Evolution;
import org.osate.aadl.evaluator.evolution.EvolutionUtils;
import org.osate.aadl.evaluator.project.Component;

public class ProjectReportUtils 
{
    
    private ProjectReportUtils()
    {
        // do nothing
    }
    
    public static EvolutionReport create( String name , Component original )
    {
        EvolutionReport report = new EvolutionReport( name );
        ReportFiller.fill( report , original );
        
        return report;
    }
    
    public static EvolutionReport create( String name , Evolution evolution ) throws Exception
    {
        Component original = evolution.getSystem();
        Component evoluted = evolution.getSystemWidthChanges();
        
        EvolutionReport report = new EvolutionReport( name );
        report.setEvolution( evolution );
        
        ReportFiller.fill( report , evoluted );
        ReportFiller.fill( report , EvolutionUtils.diff( original , evoluted ) );
        
        return report;
    }
    
    public static File generator( ProjectReport report ) throws Exception
    {
        File dir = Files.createTempDirectory( "report" ).toFile();
        
        ReportCSV .create( dir , report );
        ReportJSON.create( dir , report );
        ReportYAML.create( dir , report );
        ReportHtml.create( dir , report );
        
        return dir;
    }
    
}