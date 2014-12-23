package ru.rfedorov.rfhome;

public class ModelUnit {
    private String name;
    private Long lastTrueValueTime;
    private Long lastFalseValueTime;
    //    private Long lastRawValueTime;
//    private String lastRawValue;
    private ModelSection section;
    private String unitType;
    private String primeUnitTitle;

    public String getSectionName() {
        if (section != null)
            return section.getName();
        return null;
    }

    public ModelSection getSection() {
        return section;
    }

    public void setSection(ModelSection section) {
        this.section = section;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public Long getLastTrueValueTime() {
//        return lastTrueValueTime;
//    }

    public void setLastTrueValueTime(Long lastTrueValueTime) {
        this.lastTrueValueTime = lastTrueValueTime;
    }

    public Boolean isTrue() {
        return (lastTrueValueTime != null && lastFalseValueTime != null && lastTrueValueTime > lastFalseValueTime) || (lastTrueValueTime != null && lastFalseValueTime == null);
    }

//    public Long getLastRawValueTime() {
//        return lastRawValueTime;
//    }
//
//    public void setLastRawValueTime(Long lastRawValueTime) {
//        this.lastRawValueTime = lastRawValueTime;
//    }
//
//    public String getLastRawValue() {
//        return lastRawValue;
//    }
//
//    public void setLastRawValue(String lastRawValue) {
//        this.lastRawValue = lastRawValue;
//    }

//    public String getUnitType() {
//        return unitType;
//    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

//    public Long getLastFalseValueTime() {
//        return lastFalseValueTime;
//    }

    public void setLastFalseValueTime(Long lastFalseValueTime) {
        this.lastFalseValueTime = lastFalseValueTime;
    }

    public Boolean isValid() {
        return true;
    }

    public String getPrimeUnitTitle() {
        return primeUnitTitle;
    }

    public void setPrimeUnitTitle(String primeUnitTitle) {
        this.primeUnitTitle = primeUnitTitle;
    }
}

//"prime_unit_title":"kitchen",
//        "false":1418350333,
//        "name":"v_disable_bright",
//        "section":"Kitchen",
//        "unit_type":"WirelessButtonStates",
//        "raw":{
//        "value":"Enabled",
//        "time":1418350333
//        }