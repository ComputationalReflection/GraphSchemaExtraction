package es.uniovi.reflection.graph.reco;

import es.uniovi.reflection.graph.models.NodeLocal;
import es.uniovi.reflection.graph.models.RelationshipLocal;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.graphdb.traversal.Uniqueness;


public class TraversalOut {

    public void getData(Node startNode, TraversalDescription td) {
        Traverser traverser = td.traverse(startNode);
        GetInfo getInfo = new GetInfo();
        //Info nodos
        for (Node node : traverser.nodes()) {
            getInfo.getInfoNode(new NodeLocal(node));
        }
        //Info relaciones
        traverser=td.uniqueness(Uniqueness.RELATIONSHIP_GLOBAL).traverse(startNode);
        for (Relationship relationship : traverser.relationships()) {
            getInfo.getInfoRelationship(new RelationshipLocal(relationship));
        }
    }
}
