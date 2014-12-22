package ru.rfedorov.rfhome;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class ModelRFHome {
    private String lastAPICallTimeStamp;
    private AbstractMap<String, ModelUnit> units;
    private List<ModelSection> sections;
    private AbstractMap<String, ModelUnit> primeUnits;
    private List<ModelUnit> primeUnitsSorted;

    public ModelRFHome(String lastAPICallTimeStamp) {
        this.lastAPICallTimeStamp = lastAPICallTimeStamp;
        units = new TreeMap<>();
        sections = new ArrayList<>();
        primeUnitsSorted = new ArrayList<>();
        primeUnits = new TreeMap<>();
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

    public List<ModelSection> getSections() {
        return sections;
    }

    public AbstractMap<String, ModelUnit> getPrimeUnits() {
        return primeUnits;
    }
}