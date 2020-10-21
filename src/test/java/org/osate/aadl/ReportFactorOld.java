package org.osate.aadl;

public class ReportFactorOld implements Cloneable
{
    private String characteristic;
    private String subcharacteristic;
    private String name;
    private double min;
    private double max;
    private String unit;
    private double userFactor;
    private boolean lessIsBetter;
    
    public ReportFactorOld() {
        this.lessIsBetter = false;
    }
    
    public String getCharacteristic() {
        return this.characteristic;
    }
    
    public ReportFactorOld setCharacteristic(final String characteristic) {
        this.characteristic = characteristic;
        return this;
    }
    
    public String getSubcharacteristic() {
        return this.subcharacteristic;
    }
    
    public ReportFactorOld setSubcharacteristic(final String subcharacteristic) {
        this.subcharacteristic = subcharacteristic;
        return this;
    }
    
    public String getName() {
        return this.name;
    }
    
    public ReportFactorOld setName(final String name) {
        this.name = name;
        return this;
    }
    
    public double getMin() {
        return this.min;
    }
    
    public ReportFactorOld setMin(final double min) {
        this.min = min;
        return this;
    }
    
    public double getMax() {
        return this.max;
    }
    
    public ReportFactorOld setMax(final double max) {
        this.max = max;
        return this;
    }
    
    public String getUnit() {
        return this.unit;
    }
    
    public ReportFactorOld setUnit(final String unit) {
        this.unit = unit;
        return this;
    }
    
    public double getUserFactor() {
        return this.userFactor;
    }
    
    public ReportFactorOld setUserFactor(final double userFactor) {
        this.userFactor = userFactor;
        return this;
    }
    
    public boolean isLessIsBetter() {
        return this.lessIsBetter;
    }
    
    public ReportFactorOld setLessIsBetter(final boolean lessIsBetter) {
        this.lessIsBetter = lessIsBetter;
        return this;
    }
    
    public double getPropertyFactor() {
        final double result = this.max - this.min;
        return (result == 0.0) ? 0.0 : (1.0 / result);
    }
    
    public double getValueFactor(final double value) {
        return (value - this.min) * this.getPropertyFactor();
    }
    
    public double getGlobalFactor(final double value) {
        if (this.userFactor == 0.0 || this.getPropertyFactor() == 0.0) {
            return 0.0;
        }
        final double result = this.getValueFactor(value);
        return this.lessIsBetter ? ((1.0 - result) * this.userFactor) : (result * this.userFactor);
    }
    
    @Override
    public String toString() {
        return this.getName();
    }
    
    public ReportFactorOld clone() {
        try {
            return (ReportFactorOld)super.clone();
        }
        catch (Exception err) {
            return new ReportFactorOld()
                .setLessIsBetter(this.lessIsBetter)
                .setMax(this.max)
                .setMin(this.min)
                .setName(this.name)
                .setCharacteristic(this.characteristic)
                .setSubcharacteristic(this.subcharacteristic)
                .setUnit(this.unit)
                .setUserFactor(this.userFactor);
        }
    }
}