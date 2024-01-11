package es.uniovi.reflection.graph.models;

import java.util.ArrayList;
import java.util.Map;

public interface NodeAdapter {

    long getId();

    ArrayList<String> getLabels();

    Map<String, Object> getAllProperties();

    Object getProperty(String key);
}
