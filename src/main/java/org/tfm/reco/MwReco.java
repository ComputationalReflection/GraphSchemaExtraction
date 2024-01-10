package org.tfm.reco;

import org.tfm.db.Data;
import org.tfm.models.MyNode;
import org.tfm.models.MyProperty;
import org.tfm.models.MyRelationship;

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