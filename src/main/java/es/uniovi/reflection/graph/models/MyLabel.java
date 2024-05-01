package es.uniovi.reflection.graph.models;

import org.neo4j.driver.types.Node;
import java.util.*;

public class MyLabel {

    private long nNodesWLab = 0;
    private Map<String, NodeRelationships> relationshipsOut = new HashMap<>();
    private Map<String, NodeRelationships> relationshipsIn = new HashMap<>();
    private Map<String, Map<PropertyTypes, List<String>>> properties = new HashMap<>();

    public void updateNumber() {
        nNodesWLab++;
    }

    public void addProperties(Node node) {
        PropertiesOp.updatePropertiesMap(properties, node);
    }

    public void addRelationshipsOut(String relationshipType, Iterable<java.lang.String> entryLabels) {
        if (!relationshipsOut.containsKey(relationshipType)) {
            relationshipsOut.put(relationshipType, new NodeRelationships());
        }
        NodeRelationships nodeRelationships = relationshipsOut.get(relationshipType);
        nodeRelationships.addLabels(entryLabels);
    }

    public void addRelationshipsIn(String relationshipType, Iterable<java.lang.String> exitLabels) {
        if (!relationshipsIn.containsKey(relationshipType)) {
            relationshipsIn.put(relationshipType, new NodeRelationships());
        }
        NodeRelationships nodeRelationships = relationshipsIn.get(relationshipType);
        nodeRelationships.addLabels(exitLabels);
    }

    public long getnNodesWLab() {
        return nNodesWLab;
    }

    public Map<String, NodeRelationships> getRelationshipsOut() {
        return relationshipsOut;
    }

    public Map<String, NodeRelationships> getRelationshipsIn() {
        return relationshipsIn;
    }

    public Map<String, Map<PropertyTypes, List<String>>> getProperties() {
        return properties;
    }
}
