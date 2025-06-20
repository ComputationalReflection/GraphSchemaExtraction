package es.uniovi.reflection.graph.models;

import java.util.HashMap;
import java.util.Map;

public class GraphNodeRelationships {

    private long nRelationships = 0;
    private Map<String, Integer> labels = new HashMap<>();

    public void addLabels(Iterable<java.lang.String> labels) {
        nRelationships++;
        for (String label : labels) {
            this.labels.put(label, this.labels.getOrDefault(label, 0) + 1);
        }
    }

    public long getnRelationships() {
        return nRelationships;
    }

    public Map<String, Integer> getLabels() {
        return labels;
    }
}
