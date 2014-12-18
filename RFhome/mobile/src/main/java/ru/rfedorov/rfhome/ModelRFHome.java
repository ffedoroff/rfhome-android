package ru.rfedorov.rfhome;

import java.util.AbstractMap;
import java.util.TreeMap;

public class ModelRFHome {
    private String lastAPICallTimeStamp;
    private AbstractMap<String, ModelUnit> units;
    private AbstractMap<String, ModelSection> sections;
    private AbstractMap<String, ModelUnit> primeUnits;

    public ModelRFHome(String lastAPICallTimeStamp) {
        this.lastAPICallTimeStamp = lastAPICallTimeStamp;
        units = new TreeMap<String, ModelUnit>();
        sections = new TreeMap<String, ModelSection>();
        primeUnits = new TreeMap<String, ModelUnit>();
    }

    public String getlastAPICallTimeStamp() {
        return lastAPICallTimeStamp;
    }

    public void setlastAPICallTimeStamp(String lastAPICall) {
        this.lastAPICallTimeStamp = lastAPICall;
    }

    public AbstractMap<String, ModelUnit> getUnits() {
        return units;
    }

    public AbstractMap<String, ModelSection> getSections() {
        return sections;
    }

    public AbstractMap<String, ModelUnit> getPrimeUnits() {
        return primeUnits;
    }
}