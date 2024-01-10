package org.tfm.analysis;

import org.tfm.db.Data;
import org.tfm.models.MyNode;
import org.tfm.models.MyProperty;
import org.tfm.models.MyRelationship;

import java.util.ArrayList;
import java.util.Map;

public class MwAnalysis {
    private Data data = Data.getInstance();

    public int getNNodes() {
        return data.getNNodes();
    }

    public Map<String, Integer> getLabels() {
        return data.getLabels();
    }

    public int getNNodesWLab() {
        return data.getNNodesWLab();
    }

    public ArrayList<MyNode> getNodesGivenLabel(String label) {
        return data.getNodesGivenLabel(label);
    }

    public int getNNodesGivenLabel(String label) {
        return data.getNNodesGivenLabel(label);
    }

    public ArrayList<MyProperty> getPropertiesGivenNode(Long id) {
        return data.getPropertiesGivenNode(id);
    }

    public ArrayList<MyRelationship> getRelationshipsOut(Long id) {
        return data.getRelationshipsOut(id);
    }

    public ArrayList<MyRelationship> getRelationshipsIn(Long id) {
        return data.getRelationshipsIn(id);
    }

    public MyNode getNode(Long nodeId) {
        return data.getNode(nodeId);
    }

    public int getMaxNumberOfSimultaneousLabels() {
        return data.getMaxNumberOfSimultaneousLabels();
    }

    public Map<ArrayList<String>, Integer> getMapOfSimultaneousLabels(int i) {
        return data.getMapOfSimultaneousLabels(i);
    }

    public int getNRelationships() {
        return data.getNRelationships();
    }

    public Map<String, Integer> getRelationshipsLabels() {
        return data.getRelationshipsLabels();
    }

    public int getNRelationshipsWLab() {
        return data.getNRelationshipsWLab();
    }

    public ArrayList<MyRelationship> getRelationshipsGivenLabel(String label) {
        return data.getRelationshipsGivenLabel(label);
    }

    public int getNRelationshipsGivenLabel(String label) {
        return data.getNRelationshipsGivenLabel(label);
    }

    public ArrayList<MyProperty> getPropertiesGivenRelationship(Long id) {
        return data.getPropertiesGivenRelationship(id);
    }

    public ArrayList<MyRelationship> getRelationshipsGivenLabelAndNodeLabels(String label, String beginNodeLabel, String endNodeLabel) {
        return data.getRelationshipsGivenLabelAndNodeLabels(label, beginNodeLabel, endNodeLabel);
    }
}
