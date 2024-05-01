package es.uniovi.reflection.graph;

import es.uniovi.reflection.graph.models.GraphData;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

public class Main {

    //-nh=192.168.137.100 -np=secreto -ndb=maniega
    public static void main(String[] args) {
        Parameters parameters = Parameters.parseArguments(args);
        GraphSchemaExtractor graphSchemaExtractor = new GraphSchemaExtractor(parameters.neo4j_database, parameters.neo4j_host, parameters.neo4j_port_number, parameters.neo4j_user, parameters.neo4j_password);
        long startTime = System.nanoTime();
        GraphData graphData = graphSchemaExtractor.getGraphData();
        long gettingDataTime = System.nanoTime();
        System.out.println("Getting Graph Data time: " + (gettingDataTime - startTime) / 1e9);
        GraphSchemaWriter graphSchemaWriter = new GraphSchemaWriter(graphData, parameters.output_file);
        graphSchemaWriter.write(true);
        long writingTime = System.nanoTime();
        System.out.println("Writing Schema time : " + (writingTime - gettingDataTime) / 1e9);
        System.out.println("Total Graph Schema Extraction Time: " + (writingTime - startTime) / 1e9);
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        System.out.println("Memory Usage: " + memoryMXBean.getHeapMemoryUsage().getUsed() / 1e6);
    }
}