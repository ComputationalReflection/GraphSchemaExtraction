package es.uniovi.reflection.graph.models;

import org.neo4j.driver.types.Node;
import java.util.*;

public class GraphLabel {

    private long nNodesWLab = 0;
    private Map<String, GraphNodeRelationships> relationshipsOut = new HashMap<>();
    private Map<String, GraphNodeRelationships> relationshipsIn = new HashMap<>();
    private Map<String, Map<GraphPropertyTypes, List<String>>> properties = new HashMap<>();

    public void updateNumber() {
        nNodesWLab++;
    }

    public void addProperties(Node node) {
        GraphPropertiesOp.updatePropertiesMap(properties, node);
    }

    public void addRelationshipsOut(String relationshipType, Iterable<java.lang.String> entryLabels) {
        if (!relationshipsOut.containsKey(relationshipType)) {
            relationshipsOut.put(relationshipType, new GraphNodeRelationships());
        }
        GraphNodeRelationships graphNodeRelationships = relationshipsOut.get(relationshipType);
        graphNodeRelationships.addLabels(entryLabels);
    }

    public void addRelationshipsIn(String relationshipType, Iterable<java.lang.String> exitLabels) {
        if (!relationshipsIn.containsKey(relationshipType)) {
            relationshipsIn.put(relationshipType, new GraphNodeRelationships());
        }
        GraphNodeRelationships graphNodeRelationships = relationshipsIn.get(relationshipType);
        graphNodeRelationships.addLabels(exitLabels);
    }

    public long getnNodesWLab() {
        return nNodesWLab;
    }

    public Map<String, GraphNodeRelationships> getRelationshipsOut() {
        return relationshipsOut;
    }

    public Map<String, GraphNodeRelationships> getRelationshipsIn() {
        return relationshipsIn;
    }

    public Map<String, Map<GraphPropertyTypes, List<String>>> getProperties() {
        return properties;
    }
}
