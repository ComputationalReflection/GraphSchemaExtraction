package es.uniovi.reflection.graph.models;

import org.neo4j.driver.types.Relationship;

import java.util.Map;

public class RelationshipServer implements RelationshipAdapter {

    private Relationship relationship;

    public RelationshipServer(Relationship relationship) {
        this.relationship = relationship;
    }

    @Override
    public long getId() {
        return relationship.id();
    }

    @Override
    public String getType() {
        return relationship.type();
    }

    @Override
    public long getBeginNodeId() {
        return relationship.startNodeId();
    }

    @Override
    public long getEndNodeId() {
        return relationship.endNodeId();
    }

    @Override
    public Map<String, Object> getAllProperties() {
        return relationship.asMap();
    }

    @Override
    public Object getProperty(String key) {
        return relationship.get(key);
    }
}
