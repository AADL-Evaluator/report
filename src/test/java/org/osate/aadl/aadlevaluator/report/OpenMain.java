package org.osate.aadl.aadlevaluator.report;

import java.io.File;
import org.osate.aadl.aadlevaluator.report.generator.ReportTableHtml;

public class OpenMain 
{
    
    public static void main( String[] args ) throws Exception
    {
        String dir = "/tmp/report8347720886805554089/";
        
        ReportTableHtml.create( new File( dir ) , null );
    }
    
}
