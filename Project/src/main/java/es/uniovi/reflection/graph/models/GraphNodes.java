package es.uniovi.reflection.graph.models;

import org.neo4j.driver.types.Node;
import java.util.HashMap;
import java.util.Map;

public class GraphNodes {
    private long nNodes = 0;
    private long nNodesWLabel = 0;
    private Map<String, GraphLabel> labels = new HashMap<>();

    public void addNode(Node node) {
        nNodes++;
        if (node.labels().spliterator().getExactSizeIfKnown() > 0) {
            nNodesWLabel++;
            node.labels().forEach(label -> {
                if (!labels.containsKey(label)) {
                    labels.put(label, new GraphLabel());
                }
                GraphLabel graphLabel = labels.get(label);
                graphLabel.updateNumber();
                graphLabel.addProperties(node);
                labels.put(label, graphLabel);
            });
        }
    }

    public void addRelationshipsOut(String exitLabel, String relationshipType, Iterable<java.lang.String> entryLabels) {
        GraphLabel graphLabelExit = labels.get(exitLabel);
        graphLabelExit.addRelationshipsOut(relationshipType, entryLabels);
        labels.put(exitLabel, graphLabelExit);
    }

    public void addRelationshipsIn(String entryLabel, String relationshipType, Iterable<java.lang.String> exitLabels) {
        GraphLabel graphLabelEntry = labels.get(entryLabel);
        graphLabelEntry.addRelationshipsIn(relationshipType, exitLabels);
        labels.put(entryLabel, graphLabelEntry);
    }

    public long getnNodes() {
        return nNodes;
    }

    public long getnNodesWLabel() {
        return nNodesWLabel;
    }

    public Map<String, GraphLabel> getLabels() {
        return labels;
    }
}
