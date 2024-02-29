package es.uniovi.reflection.graph.reco;

import es.uniovi.reflection.graph.db.Data2;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;

public class MwReco2 {
    private final Data2 data = Data2.getInstance();

    public void addNode(Node node) {
        data.addNode(node);
    }

    public void addRelationshipsOut(String exitLabel, String relationshipType,
                                   Iterable<java.lang.String> entryLabels) {
        data.addRelationshipsOut(exitLabel, relationshipType, entryLabels);
    }

    public void addRelationshipsIn(String entryLabel, String relationshipType, Iterable<java.lang.String> exitLabels) {
        data.addRelationshipsIn(entryLabel, relationshipType, exitLabels);
    }

    public void addRelationship(Relationship r, Iterable<String> exitLabels, Iterable<String> entryLabels) {
        data.addRelationship(r, exitLabels, entryLabels);
    }
}