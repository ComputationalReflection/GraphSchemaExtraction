package org.tfm.models;

import org.neo4j.graphdb.Node;

import java.util.ArrayList;
import java.util.Map;

public class NodeLocal implements NodeAdapter {

    private Node node;

    public NodeLocal(Node node) {
        this.node = node;
    }

    @Override
    public long getId() {
        return node.getId();
    }

    @Override
    public ArrayList<String> getLabels() {
        ArrayList<String> list = new ArrayList<>();
        node.getLabels().forEach(label -> {
            list.add(label.name());
        });
        return list;
    }

    @Override
    public Map<String, Object> getAllProperties() {
        return node.getAllProperties();
    }

    @Override
    public Object getProperty(String key) {
        return node.getProperty(key);
    }
}
