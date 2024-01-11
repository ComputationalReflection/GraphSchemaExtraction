package es.uniovi.reflection.graph.models;


import org.neo4j.driver.types.Node;

import java.util.ArrayList;
import java.util.Map;

public class NodeServer implements NodeAdapter {

    private Node node;

    public NodeServer(Node node) {
        this.node = node;
    }

    @Override
    public long getId() {
        return node.id();
    }

    @Override
    public ArrayList<String> getLabels() {
        ArrayList<String> list = new ArrayList<>();
        node.labels().forEach(list::add);
        return list;
    }

    @Override
    public Map<String, Object> getAllProperties() {
        return node.asMap();
    }

    @Override
    public Object getProperty(String key) {
        return node.get(key);
    }
}
