package es.uniovi.reflection.graph.reco;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import es.uniovi.reflection.graph.models.NodeServer;
import es.uniovi.reflection.graph.models.RelationshipServer;

public class ServerCypherOut {

    public void getData() {
        GetInfo getInfo = new GetInfo();
        final String DB_NAME = "progquery";
        NEO4JServerManager neo4JServerManager =
                new NEO4JServerManager("192.168.137.100", "7687", "neo4j", "secreto", DB_NAME);


        try (Session session = neo4JServerManager.driver.session(SessionConfig.forDatabase(DB_NAME))) {
            Result result = session.run("MATCH (n) RETURN n");
            while (result.hasNext()) {
                Record record = result.next();
                NodeServer nodeServer = new NodeServer((record.get("n").asNode()));
                getInfo.getInfoNode(nodeServer);
            }
        }

        try (Session session = neo4JServerManager.driver.session(SessionConfig.forDatabase(DB_NAME))) {
            Result result = session.run("MATCH (n)-[r]->(m) RETURN n,r,m");
            while (result.hasNext()) {
                Record record = result.next();
                NodeServer nodeStart = new NodeServer((record.get("n").asNode()));
                RelationshipServer relationshipServer = new RelationshipServer(record.get("r").asRelationship());
                NodeServer nodeDest = new NodeServer((record.get("m").asNode()));
                getInfo.getInfoRelationship(relationshipServer);
            }
        }
    }
}