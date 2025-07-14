package es.uniovi.reflection.graph.models;

import org.neo4j.driver.types.Relationship;

import java.util.*;

public class GraphType {

    private long nRelationshipsWType = 0;
    private Map<String, Map<GraphPropertyTypes, List<String>>> properties = new HashMap<>();
    private Map<String, Integer> relationshipsWithInNode = new HashMap<>();
    private Map<String, Integer> relationshipsWithOutNode = new HashMap<>();
    private Map<String, Integer> relationshipsWithSameNode = new HashMap<>();
    private Map<String, Map<String, Map<String, Map<GraphPropertyTypes, List<String>>>>> relationshipGivenNodeLabelsProperties = new HashMap<>();

    public void addProperties(Relationship relationship) {
        nRelationshipsWType++;
        GraphPropertiesOp.updatePropertiesMap(properties, relationship);
    }

    public void addRelationshipsIn(Iterable<String> entryLabels) {
        for (String label : entryLabels) {
            relationshipsWithInNode.put(label, relationshipsWithInNode.getOrDefault(label, 0) + 1);
        }
    }

    public void addRelationshipsOut(Iterable<String> exitLabels) {
        for (String label : exitLabels) {
            relationshipsWithOutNode.put(label, relationshipsWithOutNode.getOrDefault(label, 0) + 1);
        }
    }

    public void addRelationshipsSameNode(Iterable<String> entryLabels, Iterable<String> exitLabels) {
        for (String entryLabel : entryLabels) {
            for (String exitLabel : exitLabels) {
                if (entryLabel.equals(exitLabel)) {
                    relationshipsWithSameNode.put(exitLabel, relationshipsWithSameNode.getOrDefault(exitLabel, 0) + 1);
                }
            }
        }
    }

    public void addPropertiesGivenNodeLabels(Relationship relationship, Iterable<String> entryLabels, Iterable<String> exitLabels) {
        for (String exitLabel : exitLabels) {
            if (!relationshipGivenNodeLabelsProperties.containsKey(exitLabel)) {
                relationshipGivenNodeLabelsProperties.put(exitLabel, new HashMap<>());
            }
            Map<String, Map<String, Map<GraphPropertyTypes, List<String>>>> exitMap = relationshipGivenNodeLabelsProperties.get(exitLabel);
            for (String entryLabel : entryLabels) {
                if (!exitMap.containsKey(entryLabel)) {
                    exitMap.put(entryLabel, new HashMap<>());
                }
                Map<String, Map<GraphPropertyTypes, List<String>>> entryMap = exitMap.get(entryLabel);
                GraphPropertiesOp.updatePropertiesMap(entryMap, relationship);
                exitMap.put(entryLabel, entryMap);
            }
            relationshipGivenNodeLabelsProperties.put(exitLabel, exitMap);
        }
    }

    public long getNRelationshipsWType() {
        return nRelationshipsWType;
    }

    public Map<String, Map<GraphPropertyTypes, List<String>>> getProperties() {
        return properties;
    }

    public Map<String, Integer> getRelationshipsWithInNode() {
        return relationshipsWithInNode;
    }

    public Map<String, Integer> getRelationshipsWithOutNode() {
        return relationshipsWithOutNode;
    }

    public Map<String, Integer> getRelationshipsWithSameNode() {
        return relationshipsWithSameNode;
    }

    public Map<String, Map<String, Map<String, Map<GraphPropertyTypes, List<String>>>>> getRelationshipGivenNodeLabelsProperties() {
        return relationshipGivenNodeLabelsProperties;
    }
}
