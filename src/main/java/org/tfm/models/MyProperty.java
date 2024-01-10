package org.tfm.models;

public class MyProperty {
    private final String name;
    private final Long fatherId;
    private final PropertyTypes propertyType;
    private final String value;
    private final Boolean isFatherNode;

    public MyProperty(String name, Long fatherId, PropertyTypes propertyType, String value, Boolean isFatherNode) {
        this.name = name;
        this.fatherId = fatherId;
        this.propertyType = propertyType;
        this.value=value;
        this.isFatherNode = isFatherNode;
    }

    public String getName() {
        return name;
    }

    public Long getFatherId() {
        return fatherId;
    }

    public PropertyTypes getPropertyType() {
        return propertyType;
    }

    public String getValue() {
        return value;
    }

    public Boolean getFatherNode() {
        return isFatherNode;
    }
}