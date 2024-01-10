package org.tfm.models;

import org.neo4j.graphdb.Relationship;

import java.util.Map;

public class RelationshipLocal implements RelationshipAdapter {

    private Relationship relationship;

    public RelationshipLocal(Relationship relationship) {
        this.relationship = relationship;
    }

    @Override
    public long getId() {
        return Long.parseLong(relationship.getElementId().split(":")[2]);
    }

    @Override
    public String getType() {
        return relationship.getType().name();
    }

    @Override
    public long getBeginNodeId() {
        return Long.parseLong(relationship.getStartNode().getElementId().split(":")[2]);
    }

    @Override
    public long getEndNodeId() {
        return Long.parseLong(relationship.getEndNode().getElementId().split(":")[2]);
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
