package org.tfm.models;

import java.util.Map;

public interface RelationshipAdapter {

    long getId();

    String getType();

    long getBeginNodeId();

    long getEndNodeId();

    Map<String, Object> getAllProperties();

    Object getProperty(String key);
}
