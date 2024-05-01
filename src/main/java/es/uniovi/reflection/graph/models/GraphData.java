package es.uniovi.reflection.graph.models;

import es.uniovi.reflection.graph.models.GraphNodes;
import es.uniovi.reflection.graph.models.GraphRelationships;
import es.uniovi.reflection.graph.models.GraphNodeRelationships;
import es.uniovi.reflection.graph.models.GraphPropertyTypes;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GraphData {
    private GraphNodes nodesData = new GraphNodes();
    private GraphRelationships relationshipsData = new GraphRelationships();
    private Map<Long, Map<Iterable<String>, Integer>> multiNodesData = new HashMap<>();

    public void addNode(Node node) {
        nodesData.addNode(node);
        if (node.labels().spliterator().getExactSizeIfKnown() >= 2) {
            if (!multiNodesData.containsKey(node.labels().spliterator().getExactSizeIfKnown())) {
                multiNodesData.put(node.labels().spliterator().getExactSizeIfKnown(), new HashMap<>());
            }
            Map<Iterable<String>, Integer> multiNodes = multiNodesData.get(node.labels().spliterator().getExactSizeIfKnown());
            multiNodes.put(node.labels(), multiNodes.getOrDefault(node.labels(), 0) + 1);
            multiNodesData.put(node.labels().spliterator().getExactSizeIfKnown(), multiNodes);
        }
    }

    public void addRelationshipsOut(String exitLabel, String relationshipType, Iterable<java.lang.String> entryLabels) {
        nodesData.addRelationshipsOut(exitLabel, relationshipType, entryLabels);
    }

    public void addRelationshipsIn(String entryLabel, String relationshipType, Iterable<java.lang.String> exitLabels) {
        nodesData.addRelationshipsIn(entryLabel, relationshipType, exitLabels);
    }

    public long getNNodes() {
        return nodesData.getnNodes();
    }

    public long getNNodesWLabel() {
        return nodesData.getnNodesWLabel();
    }

    public Set<String> getLabelsNames() {
        return nodesData.getLabels().keySet();
    }

    public long getLabelNumber(String label) {
        return nodesData.getLabels().get(label).getnNodesWLab();
    }

    public Map<String, Map<GraphPropertyTypes, List<String>>> getPropertiesGivenLabel(String label) {
        return nodesData.getLabels().get(label).getProperties();
    }

    public int getMaxNumberOfSimultaneousLabels() {
        return (int) multiNodesData.keySet().stream().mapToLong(val -> val).max().orElse(0);
    }

    public Map<Iterable<String>, Integer> getMapOfSimultaneousLabels(long i) {
        return multiNodesData.getOrDefault(i, new HashMap<>());
    }

    public Map<String, GraphNodeRelationships> getRelationshipsOutGivenLabel(String label) {
        return nodesData.getLabels().get(label).getRelationshipsOut();
    }

    public Map<String, GraphNodeRelationships> getRelationshipsInGivenLabel(String label) {
        return nodesData.getLabels().get(label).getRelationshipsIn();
    }

    public long getNRelationships() {
        return relationshipsData.getNRelationships();
    }

    public void addRelationship(Relationship r, Iterable<String> exitLabels, Iterable<String> entryLabels) {
        relationshipsData.addRelationship(r, exitLabels, entryLabels);
    }

    public Set<String> getRelationshipsTypes() {
        return relationshipsData.getTypes().keySet();
    }

    public long getTypeNumber(String label) {
        return relationshipsData.getTypes().get(label).getNRelationshipsWType();
    }

    public Map<String, Map<GraphPropertyTypes, List<String>>> getRelationshipProperties(String type) {
        return relationshipsData.getTypes().get(type).getProperties();
    }

    public Map<String, Integer> getRelationshipNodeIn(String type) {
        return relationshipsData.getTypes().get(type).getRelationshipsWithInNode();
    }

    public Map<String, Integer> getRelationshipNodeOut(String type) {
        return relationshipsData.getTypes().get(type).getRelationshipsWithOutNode();
    }

    public Map<String, Integer> getRelationshipSameNode(String type) {
        return relationshipsData.getTypes().get(type).getRelationshipsWithSameNode();
    }

    public Set<String> getPropertiesGivenNodesLabel(String type) {
        return relationshipsData.getTypes().get(type).getRelationshipGivenNodeLabelsProperties().keySet();
    }

    public Set<String> getPropertiesGivenNodesLabelAndBegin(String type, String beginNodeLabel) {
        return relationshipsData.getTypes().get(type).getRelationshipGivenNodeLabelsProperties().get(beginNodeLabel).keySet();
    }

    public Map<String, Map<GraphPropertyTypes, List<String>>> getPropertiesGivenNodesLabelBeginEnd(String type, String beginNodeLabel, String endNodeLabel) {
        return relationshipsData.getTypes().get(type).getRelationshipGivenNodeLabelsProperties().get(beginNodeLabel).get(endNodeLabel);
    }
}
