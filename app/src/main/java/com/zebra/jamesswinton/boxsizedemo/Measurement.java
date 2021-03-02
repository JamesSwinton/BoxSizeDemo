package com.zebra.jamesswinton.boxsizedemo;

import java.text.DecimalFormat;

public class Measurement {

    // Identifier
    public enum MEASURE_TYPE { SINGLE, VOLUME }

    // Formatting
    private static final DecimalFormat DecimalFormat = new DecimalFormat("##.# cm");
    private static final DecimalFormat CsvFormat = new DecimalFormat("##.#");

    // Values
    private MEASURE_TYPE measure_type;
    private double measurement1, measurement2, measurement3;
    private double total;

    public Measurement(double measurement1, double measurement2, double measurement3, double total) {
        this.measure_type = MEASURE_TYPE.VOLUME;
        this.measurement1 = measurement1;
        this.measurement2 = measurement2;
        this.measurement3 = measurement3;
        this.total = Double.parseDouble(getCsvFormattedMeasurement1())
                * Double.parseDouble(getCsvFormattedMeasurement2())
                * Double.parseDouble(getCsvFormattedMeasurement3());
    }

    public Measurement(double total) {
        this.measure_type = MEASURE_TYPE.SINGLE;
        this.total = total;
    }

    public MEASURE_TYPE getMeasure_type() {
        return measure_type;
    }

    public void setMeasure_type(MEASURE_TYPE measure_type) {
        this.measure_type = measure_type;
    }

    public double getMeasurement1() {
        return measurement1;
    }

    public String getFormattedMeasurement1() {
        return DecimalFormat.format(measurement1);
    }

    public String getCsvFormattedMeasurement1() {
        return CsvFormat.format(measurement1);
    }

    public void setMeasurement1(double measurement1) {
        this.measurement1 = measurement1;
    }

    public double getMeasurement2() {
        return measurement2;
    }

    public String getFormattedMeasurement2() {
        return DecimalFormat.format(measurement2);
    }

    public String getCsvFormattedMeasurement2() {
        return CsvFormat.format(measurement2);
    }

    public void setMeasurement2(double measurement2) {
        this.measurement2 = measurement2;
    }

    public double getMeasurement3() {
        return measurement3;
    }

    public String getFormattedMeasurement3() {
        return DecimalFormat.format(measurement3);
    }

    public String getCsvFormattedMeasurement3() {
        return CsvFormat.format(measurement3);
    }

    public void setMeasurement3(double measurement3) {
        this.measurement3 = measurement3;
    }

    public double getTotal() {
        return total;
    }

    public String getFormattedTotal() {
        return DecimalFormat.format(total);
    }

    public String getCsvFormattedTotal() {
        return CsvFormat.format(total);
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
