package es.uniovi.reflection.graph.reco;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import es.uniovi.reflection.graph.models.NodeLocal;
import es.uniovi.reflection.graph.models.RelationshipLocal;

public class CypherOut {

    public void getData(Transaction tx) {
        GetInfo getInfo = new GetInfo();
        //Info nodos
        Result result = tx.execute("MATCH (n) RETURN count(n) as count");
        long nNodes = (long) result.next().get("count");
        for (int i = 0; i < nNodes; i++) {
            result = tx.execute("MATCH (n WHERE ID(n) = " + i + ") RETURN n");
            getInfo.getInfoNode(new NodeLocal((Node) result.next().get("n")));
        }
        //Info relaciones
        result = tx.execute("MATCH ()-[n]->() RETURN count(n) as count");
        long nRelationships = (long) result.next().get("count");
        for (int i = 0; i < nRelationships; i++) {
            result = tx.execute("MATCH ()-[n]->() WHERE ID(n)=" + i + " RETURN n");
            getInfo.getInfoRelationship(new RelationshipLocal((Relationship) result.next().get("n")));
        }
    }
}