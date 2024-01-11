package es.uniovi.reflection.graph.models;

public class MyRelationship {
    private String etiqueta;
    private Long id;
    private Long beginNodeId;
    private Long endNodeId;

    public MyRelationship(Long id, String label, Long beginNodeId, Long endNodeId) {
        this.id = id;
        this.etiqueta = label;
        this.beginNodeId=beginNodeId;
        this.endNodeId=endNodeId;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public Long getId() {
        return id;
    }

    public Long getBeginNodeId() {
        return beginNodeId;
    }

    public Long getEndNodeId() {
        return endNodeId;
    }
}
