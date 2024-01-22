package es.uniovi.reflection.graph.db;

import es.uniovi.reflection.graph.models.MyNodes;
import es.uniovi.reflection.graph.models.PropertyTypes;
import org.neo4j.driver.types.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Data2 {

    private static Data2 instance;

    private MyNodes nodesData = new MyNodes();

    //private MyRelationships relationshipsData;
    private Map<Long, Map<Iterable<String>, Integer>> multiNodesData = new HashMap<>();


    public static Data2 getInstance() {
        if (instance == null) {
            instance = new Data2();
        }
        return instance;
    }

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

    public Map<String, Map<PropertyTypes, List<String>>> getPropertiesGivenLabel(String label) {
        return nodesData.getLabels().get(label).getProperties();
    }

    public int getMaxNumberOfSimultaneousLabels() {
        return (int) multiNodesData.keySet().stream().mapToLong(val -> val).max().orElse(0);
    }

    public Map<Iterable<String>, Integer> getMapOfSimultaneousLabels(long i) {
        return multiNodesData.getOrDefault(i, new HashMap<>());
    }
}
