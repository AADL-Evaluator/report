package org.osate.aadl.aadlevaluator.report.comparation;

public class ComparationReport 
{
    protected final String characteristic;
    protected final String attribute;
    protected final String result;
    protected final Object valueOriginal;
    protected final Object valueEvolution;

    public ComparationReport( String characteristic , String attribute , String result , Object valueEvolution , Object valueOriginal )
    {
        this.characteristic = characteristic;
        this.attribute = attribute;
        this.result = result;
        this.valueEvolution = valueEvolution;
        this.valueOriginal = valueOriginal;
    }

    public String getCharacteristic()
    {
        return characteristic;
    }

    public String getAttribute()
    {
        return attribute;
    }

    public String getResult()
    {
        return result;
    }

    public Object getValueEvolution()
    {
        return valueEvolution;
    }

    public Object getValueOriginal()
    {
        return valueOriginal;
    }
    
}