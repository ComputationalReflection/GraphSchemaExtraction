package es.uniovi.reflection.graph.reco;

import es.uniovi.reflection.graph.db.Data2;
import org.neo4j.driver.types.Node;

public class MwReco2 {
    private Data2 data = Data2.getInstance();

    public void addNode(Node node) {
        data.addNode(node);
    }
}