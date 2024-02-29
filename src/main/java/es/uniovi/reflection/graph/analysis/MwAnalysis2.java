package es.uniovi.reflection.graph.analysis;

import es.uniovi.reflection.graph.db.Data2;
import es.uniovi.reflection.graph.models.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MwAnalysis2 {
    private Data2 data = Data2.getInstance();

    public long getNNodes() {
        return data.getNNodes();
    }

    public long getNNodesWLabel() {
        return data.getNNodesWLabel();
    }

    public Set<String> getLabelsNames() {
        return data.getLabelsNames();
    }

    public long getLabelNumber(String label) {
        return data.getLabelNumber(label);
    }

    public Map<String, Map<PropertyTypes, List<String>>> getPropertiesGivenLabel(String label) {
        return data.getPropertiesGivenLabel(label);
    }

    public int getMaxNumberOfSimultaneousLabels() {
        return data.getMaxNumberOfSimultaneousLabels();
    }

    public Map<Iterable<String>, Integer> getMapOfSimultaneousLabels(long i) {
        return data.getMapOfSimultaneousLabels(i);
    }

    public Map<String, NodeRelationships> getRelationshipsOutGivenLabel(String label) {
        return data.getRelationshipsOutGivenLabel(label);
    }

    public Map<String, NodeRelationships> getRelationshipsInGivenLabel(String label) {
        return data.getRelationshipsInGivenLabel(label);
    }

    public long getNRelationships() {
        return data.getNRelationships();
    }

    public Set<String> getRelationshipsTypes() {
        return data.getRelationshipsTypes();
    }

    public long getTypeNumber(String label) {
        return data.getTypeNumber(label);
    }

    public Map<String, Map<PropertyTypes, List<String>>> getRelationshipProperties(String type) {
        return data.getRelationshipProperties(type);
    }

    public Map<String, Integer> getRelationshipNodeIn(String type) {
        return data.getRelationshipNodeIn(type);
    }

    public Map<String, Integer> getRelationshipNodeOut(String type) {
        return data.getRelationshipNodeOut(type);
    }

    public Map<String, Integer> getRelationshipSameNode(String type) {
        return data.getRelationshipSameNode(type);
    }

    public Set<String> getPropertiesGivenNodesLabel(String type) {
        return data.getPropertiesGivenNodesLabel(type);
    }

    public Set<String> getPropertiesGivenNodesLabelAndBegin(String type, String beginNodeLabel) {
        return data.getPropertiesGivenNodesLabelAndBegin(type, beginNodeLabel);
    }

    public Map<String, Map<PropertyTypes, List<String>>> getPropertiesGivenNodesLabelBeginEnd(String type, String beginNodeLabel, String endNodeLabel) {
        return data.getPropertiesGivenNodesLabelBeginEnd(type, beginNodeLabel, endNodeLabel);
    }
}
