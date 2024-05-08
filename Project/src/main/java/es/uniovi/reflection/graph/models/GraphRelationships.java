package es.uniovi.reflection.graph.models;

import org.neo4j.driver.types.Relationship;

import java.util.HashMap;
import java.util.Map;

public class GraphRelationships {

    private long nRelationships = 0;
    private Map<String, GraphType> types = new HashMap<>();

    public void addRelationship(Relationship r, Iterable<String> exitLabels, Iterable<String> entryLabels) {
        nRelationships++;
        if (!types.containsKey(r.type())){
            types.put(r.type(), new GraphType());
        }
        GraphType graphType = types.get(r.type());
        graphType.addProperties(r);
        graphType.addRelationshipsIn(entryLabels);
        graphType.addRelationshipsOut(exitLabels);
        graphType.addRelationshipsSameNode(entryLabels, exitLabels);
        graphType.addPropertiesGivenNodeLabels(r, entryLabels, exitLabels);
        types.put(r.type(), graphType);
    }

    public long getNRelationships() {
        return nRelationships;
    }

    public Map<String, GraphType> getTypes() {
        return types;
    }
}
