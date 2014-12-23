package ru.rfedorov.rfhome;

import java.util.ArrayList;
import java.util.List;

public class ModelSection {
    private String name;
    private List<ModelUnit> units;

    public ModelSection(String Name) {
        this.name = Name;
        units = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<ModelUnit> getUnits() {
        return units;
    }

//    public Boolean isValid() {
//        return getName() != null && !getName().isEmpty();
//    }
}