package es.uniovi.reflection.graph.models;

import org.neo4j.graphdb.Relationship;

import java.util.Map;

public class RelationshipLocal implements RelationshipAdapter {

    private Relationship relationship;

    public RelationshipLocal(Relationship relationship) {
        this.relationship = relationship;
    }

    @Override
    public long getId() {
        return relationship.getId();
    }

    @Override
    public String getType() {
        return relationship.getType().name();
    }

    @Override
    public long getBeginNodeId() {
        return relationship.getStartNode().getId();
    }

    @Override
    public long getEndNodeId() {
        return relationship.getEndNode().getId();
    }

    @Override
    public Map<String, Object> getAllProperties() {
        return relationship.getAllProperties();
    }

    @Override
    public Object getProperty(String key) {
        return relationship.getProperty(key);
    }
}
