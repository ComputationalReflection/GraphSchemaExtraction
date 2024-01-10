package org.tfm.reco;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.tfm.models.NodeLocal;
import org.tfm.models.RelationshipLocal;


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
