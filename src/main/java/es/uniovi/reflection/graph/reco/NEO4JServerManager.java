package es.uniovi.reflection.graph.reco;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.util.List;

public class NEO4JServerManager {
    //    private static final Pattern WRITE_CYPHER_KEYWORDS = Pattern.compile("\\b(START|STOP|CREATE|MERGE|SET|DELETE|REMOVE|DROP|DETACH)\\b");
    public static final String NEO4J_PROTOCOL = "bolt://";
    public static final String NEO4J_DEFAULT_DB = "neo4j";

    public Driver driver;
    private Session session;

    public NEO4JServerManager(String host, String port, String user, String password, String db_name) {
        Config config = Config.builder().withLogging(Logging.none()).build();
        driver = GraphDatabase.driver(NEO4J_PROTOCOL + host + ":" + port, AuthTokens.basic(user, password), config);
        //session = driver.session(SessionConfig.forDatabase(db_name));
    }

    public Record executeQuery(String query) {
        return session.writeTransaction(tx -> tx.run(query).next());
    }
    public Session executeQueries() {
        return session;
    }
   /* public List<NEO4JRecord> executeQuery(String query, String[] userIds, String[] programIds) {
        return session.writeTransaction(tx -> tx.run(CypherAdapter.limitQuery(true, query, userIds, programIds)).list(record -> new ServerRecord(record)));
    }*/
}