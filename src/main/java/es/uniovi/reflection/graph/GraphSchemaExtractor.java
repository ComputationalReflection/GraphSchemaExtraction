package es.uniovi.reflection.graph;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;

public class GraphSchemaExtractor {
    public static final String NEO4J_PROTOCOL = "bolt://";
    private String NEO4J_DATABASE;
    private String NEO4J_HOST;
    private String NEO4J_PORT;
    private String NEO4J_USER;
    private String NEO4J_PASSWORD;

    public GraphSchemaExtractor(String NEO4J_DATABASE, String NEO4J_HOST, String NEO4J_PORT, String NEO4J_USER, String NEO4J_PASSWORD) {
        this.NEO4J_DATABASE = NEO4J_DATABASE;
        this.NEO4J_HOST = NEO4J_HOST;
        this.NEO4J_PORT = NEO4J_PORT;
        this.NEO4J_USER = NEO4J_USER;
        this.NEO4J_PASSWORD = NEO4J_PASSWORD;
    }

    public GraphData getGraphData() {
        GraphData graphData = new GraphData();
        Config config = Config.builder().withLogging(Logging.none()).build();
        Driver driver = GraphDatabase.driver(NEO4J_PROTOCOL + NEO4J_HOST + ":" + NEO4J_PORT, AuthTokens.basic(NEO4J_USER, NEO4J_PASSWORD), config);

        try (Session session = driver.session(SessionConfig.forDatabase(NEO4J_DATABASE))) {
            Result result = session.run("MATCH (n) RETURN n");
            while (result.hasNext()) {
                graphData.addNode(result.next().get("n").asNode());
            }
        }

        try (Session session = driver.session(SessionConfig.forDatabase(NEO4J_DATABASE))) {
            Result result = session.run("MATCH (n)-[r]->(m) RETURN n,r,m");
            while (result.hasNext()) {
                Record record = result.next();
                graphData.addRelationship(record.get("r").asRelationship(), record.get("n").asNode().labels(), record.get("m").asNode().labels());
                for (String exitLabel : record.get("n").asNode().labels()) {
                    graphData.addRelationshipsOut(exitLabel, record.get("r").asRelationship().type(), record.get("m").asNode().labels());
                }
                for (String entryLabel : record.get("n").asNode().labels()) {
                    graphData.addRelationshipsIn(entryLabel, record.get("r").asRelationship().type(), record.get("m").asNode().labels());
                }
            }
        }
        return graphData;
    }
}