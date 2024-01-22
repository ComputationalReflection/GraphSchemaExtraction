package es.uniovi.reflection.graph.models;

import org.neo4j.driver.types.Node;

import java.util.HashMap;
import java.util.Map;

public class MyNodes {
    private long nNodes = 0;
    private long nNodesWLabel = 0;
    private Map<String, MyLabel> labels = new HashMap<>();

    public void addNode(Node node) {
        nNodes++;
        if (node.labels().spliterator().getExactSizeIfKnown() > 0) {
            nNodesWLabel++;
            node.labels().forEach(label -> {
                if (!labels.containsKey(label)) {
                    labels.put(label, new MyLabel());
                }
                MyLabel myLabel = labels.get(label);
                myLabel.updateNumber();
                myLabel.addProperties(node);
                labels.put(label, myLabel);
            });
        }
    }

    public long getnNodes() {
        return nNodes;
    }

    public long getnNodesWLabel() {
        return nNodesWLabel;
    }

    public Map<String, MyLabel> getLabels() {
        return labels;
    }
}
