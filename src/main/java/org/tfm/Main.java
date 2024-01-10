package org.tfm;

import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.tfm.analysis.Analysis;
import org.tfm.reco.CypherOut;
import org.tfm.reco.ServerCypherOut;
import org.tfm.reco.TraversalOut;

import java.nio.file.Path;

import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;

public class Main {



    public static void main(String[] args) {
        final Path databaseDirectory = Path.of("target/neo4j-hello-db");
        DatabaseManagementService managementService = new DatabaseManagementServiceBuilder(databaseDirectory).build();
        GraphDatabaseService graphDb = managementService.database(DEFAULT_DATABASE_NAME);
        registerShutdownHook(managementService);
        TraversalOut traversalOut = new TraversalOut();
        CypherOut cypherOut = new CypherOut();
        ServerCypherOut serverCypherOut = new ServerCypherOut();
        long startTime;

        try (Transaction tx = graphDb.beginTx()) {

            TraversalDescription td = tx.traversalDescription();
            Result result = tx.execute("MATCH (n) WHERE ID(n) = 0 RETURN n");

//            startTime = System.nanoTime();
//            traversalOut.getData((Node) result.next().get("n"), td);
//            System.out.println((System.nanoTime() - startTime) / 1e9);

            /*startTime = System.nanoTime();
            cypherOut.getData(tx);
            System.out.println((System.nanoTime() - startTime) / 1e9);*/

            managementService.shutdown();
        }
        startTime = System.nanoTime();
        serverCypherOut.getData();
        System.out.println((System.nanoTime() - startTime) / 1e9);

        Analysis analysis = new Analysis();
        analysis.doAnalysis(true);
        System.out.println((System.nanoTime() - startTime) / 1e9);
    }


    private static void registerShutdownHook(final DatabaseManagementService managementService) {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                managementService.shutdown();
            }
        });
    }
}