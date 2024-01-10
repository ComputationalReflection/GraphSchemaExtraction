package org.tfm.reco;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.tfm.models.NodeServer;
import org.tfm.models.RelationshipServer;

import java.util.List;
import java.util.stream.Collectors;

public class ServerCypherOut {

    public void getData() {
        GetInfo getInfo = new GetInfo();
        NEO4JServerManager neo4JServerManager = new NEO4JServerManager("192.168.137.100", "7687", "neo4j", "secreto", "biojava");

        try (Session session = neo4JServerManager.driver.session(SessionConfig.forDatabase("biojava"))) {
            Result result = session.run("MATCH (n) RETURN n");
            while (result.hasNext()) {
                Record record = result.next();
                NodeServer nodeServer = new NodeServer((record.get("n").asNode()));
                getInfo.getInfoNode(nodeServer);
            }
        }

        try (Session session = neo4JServerManager.driver.session(SessionConfig.forDatabase("biojava"))) {
            Result result = session.run("MATCH ()-[r]->() RETURN r");
            while (result.hasNext()) {
                Record record = result.next();
                RelationshipServer relationshipServer = new RelationshipServer(record.get("r").asRelationship());
                getInfo.getInfoRelationship(relationshipServer);
            }
        }
    }
}