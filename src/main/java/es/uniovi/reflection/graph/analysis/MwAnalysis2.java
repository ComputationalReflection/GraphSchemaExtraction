package es.uniovi.reflection.graph.analysis;

import es.uniovi.reflection.graph.db.Data;
import es.uniovi.reflection.graph.db.Data2;
import es.uniovi.reflection.graph.models.MyNode;
import es.uniovi.reflection.graph.models.MyProperty;
import es.uniovi.reflection.graph.models.MyRelationship;
import es.uniovi.reflection.graph.models.PropertyTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MwAnalysis2 {
    private Data2 data = Data2.getInstance();

    public long getNNodes() {
        return data.getNNodes();
    }

    public long getNNodesWLabel() {
        return data.getNNodesWLabel();
    }

    public Set<String> getLabelsNames() {
        return data.getLabelsNames();
    }

    public long getLabelNumber(String label) {
        return data.getLabelNumber(label);
    }

    public Map<String, Map<PropertyTypes, List<String>>> getPropertiesGivenLabel(String label) {
        return data.getPropertiesGivenLabel(label);
    }

    public int getMaxNumberOfSimultaneousLabels() {
        return data.getMaxNumberOfSimultaneousLabels();
    }

    public Map<Iterable<String>, Integer> getMapOfSimultaneousLabels(long i) {
        return data.getMapOfSimultaneousLabels(i);
    }
}
