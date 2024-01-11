package es.uniovi.reflection.graph.db;

import es.uniovi.reflection.graph.models.MyProperty;
import es.uniovi.reflection.graph.models.MyRelationship;
import es.uniovi.reflection.graph.models.MyNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Data {
    private static Data instance;
    private final ArrayList<MyNode> myNodes = new ArrayList<>();
    private final ArrayList<MyRelationship> relaciones = new ArrayList<>();
    private final ArrayList<MyProperty> propiedades = new ArrayList<>();

    public static Data getInstance() {
        if (instance == null) {
            instance = new Data();
        }
        return instance;
    }

    public void addNode(MyNode myNode) {
        myNodes.add(myNode);
    }

    public void addRelationship(MyRelationship myRelationship) {
        relaciones.add(myRelationship);
    }

    public void addProperty(MyProperty myProperty) {
        propiedades.add(myProperty);
    }

    public int getNNodes() {
        return myNodes.size();
    }

    public int getNNodesWLab() {
        int cont = 0;
        for (MyNode node : myNodes) {
            if (!node.getEtiquetas().isEmpty()) cont = cont + 1;
        }
        return cont;
    }

    public Map<String, Integer> getLabels() {
        Map<String, Integer> labels = new HashMap<>();
        for (MyNode node : myNodes) {
            for (String label : node.getEtiquetas()) {
                labels.put(label, labels.getOrDefault(label, 0) + 1);
            }
        }
        return labels;
    }

    public ArrayList<MyNode> getNodesGivenLabel(String label) {
        ArrayList<MyNode> nodes = new ArrayList<>();
        for (MyNode node : myNodes) {
            if (node.getEtiquetas().contains(label)) nodes.add(node);
        }
        return nodes;
    }

    public int getNNodesGivenLabel(String label) {
        return getNodesGivenLabel(label).size();
    }

    public ArrayList<MyProperty> getPropertiesGivenNode(Long id) {
        ArrayList<MyProperty> properties = new ArrayList<>();
        for (MyProperty property : propiedades) {
            if (property.getFatherId().equals(id) && property.getFatherNode()) properties.add(property);
        }
        return properties;
    }

    public ArrayList<MyRelationship> getRelationshipsOut(Long id) {
        ArrayList<MyRelationship> relationshipsOut = new ArrayList<>();
        for (MyRelationship myRelationship : relaciones) {
            if (Objects.equals(myRelationship.getBeginNodeId(), id)) relationshipsOut.add(myRelationship);
        }
        return relationshipsOut;
    }

    public ArrayList<MyRelationship> getRelationshipsIn(Long id) {
        ArrayList<MyRelationship> relationshipsOut = new ArrayList<>();
        for (MyRelationship myRelationship : relaciones) {
            if (Objects.equals(myRelationship.getEndNodeId(), id)) relationshipsOut.add(myRelationship);
        }
        return relationshipsOut;
    }

    public MyNode getNode(Long nodeId) {
        for (MyNode node : myNodes) {
            if (Objects.equals(node.getId(), nodeId)) {
                return node;
            }
        }
        return null;
    }

    public int getMaxNumberOfSimultaneousLabels() {
        int number = 1;
        for (MyNode node : myNodes) if (node.getEtiquetas().size() > number) number = node.getEtiquetas().size();
        return number;
    }

    public Map<ArrayList<String>, Integer> getMapOfSimultaneousLabels(int i) {
        Map<ArrayList<String>, Integer> mapOfLabels = new HashMap<>();
        for (MyNode node : myNodes) {
            if (node.getEtiquetas().size() == i) {
                mapOfLabels.put(node.getEtiquetas(), mapOfLabels.getOrDefault(node.getEtiquetas(), 0) + 1);
            }
        }
        return mapOfLabels;
    }

    public int getNRelationships() {
        return relaciones.size();
    }

    public Map<String, Integer> getRelationshipsLabels() {
        Map<String, Integer> labels = new HashMap<>();
        for (MyRelationship myRelationship : relaciones) {
            labels.put(myRelationship.getEtiqueta(), labels.getOrDefault(myRelationship.getEtiqueta(), 0) + 1);
        }
        return labels;
    }

    public int getNRelationshipsWLab() {
        int count = 0;
        for (MyRelationship myRelationship : relaciones) if (myRelationship.getEtiqueta() != null) count++;
        return count;
    }

    public ArrayList<MyRelationship> getRelationshipsGivenLabel(String label) {
        ArrayList<MyRelationship> relationships = new ArrayList<>();
        for (MyRelationship myRelationship : relaciones) {
            if (myRelationship.getEtiqueta().equals(label)) relationships.add(myRelationship);
        }
        return relationships;
    }

    public int getNRelationshipsGivenLabel(String label) {
        return getRelationshipsGivenLabel(label).size();
    }

    public ArrayList<MyProperty> getPropertiesGivenRelationship(Long id) {
        ArrayList<MyProperty> properties = new ArrayList<>();
        for (MyProperty property : propiedades) {
            if (property.getFatherId().equals(id) && !property.getFatherNode()) properties.add(property);
        }
        return properties;
    }

    public ArrayList<MyRelationship> getRelationshipsGivenLabelAndNodeLabels(String label, String beginNodeLabel, String endNodeLabel) {
        ArrayList<MyRelationship> relationships = new ArrayList<>();
        for (MyRelationship myRelationship : getRelationshipsGivenLabel(label)) {
            if (Objects.requireNonNull(getNode(myRelationship.getBeginNodeId())).getEtiquetas().contains(beginNodeLabel)
                    && Objects.requireNonNull(getNode(myRelationship.getEndNodeId())).getEtiquetas().contains(endNodeLabel))
                relationships.add(myRelationship);
        }
        return relationships;
    }
}
