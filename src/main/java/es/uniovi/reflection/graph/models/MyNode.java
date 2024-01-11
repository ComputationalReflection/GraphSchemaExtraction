package es.uniovi.reflection.graph.models;

import java.util.ArrayList;

public class MyNode {
    private final ArrayList<String> etiquetas = new ArrayList<>();
    private final Long id;

    public MyNode(Long id) {
        this.id = id;
    }

    public ArrayList<String> getEtiquetas() {
        return etiquetas;
    }

    public Long getId() {
        return id;
    }

    public void addEtiqueta(String etiqueta) {
        etiquetas.add(etiqueta);
    }
}