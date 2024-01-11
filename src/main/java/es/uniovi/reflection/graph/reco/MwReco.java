package es.uniovi.reflection.graph.reco;

import es.uniovi.reflection.graph.models.MyProperty;
import es.uniovi.reflection.graph.db.Data;
import es.uniovi.reflection.graph.models.MyNode;
import es.uniovi.reflection.graph.models.MyRelationship;

public class MwReco {
    private Data data = Data.getInstance();

    public void addNode(MyNode myNode) {
        data.addNode(myNode);
    }

    public void addRelationship(MyRelationship myRelationship) {
        data.addRelationship(myRelationship);
    }

    public void addProperty(MyProperty myProperty) {
        data.addProperty(myProperty);
    }
}