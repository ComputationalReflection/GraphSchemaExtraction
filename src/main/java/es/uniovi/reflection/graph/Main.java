package es.uniovi.reflection.graph;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

public class Main {
    private static String NEO4J_DATABASE = "maniega";
    //private static String NEO4J_HOST = "progquery.uniovi.es";
    private static String NEO4J_HOST = "192.168.137.100";
    private static String NEO4J_PORT = "7687";
    private static String NEO4J_USER = "neo4j";
    private static String NEO4J_PASSWORD = "secreto";

    public static void main(String[] args) {
        GraphSchemaExtractor graphSchemaExtractor = new GraphSchemaExtractor(NEO4J_DATABASE, NEO4J_HOST, NEO4J_PORT, NEO4J_USER, NEO4J_PASSWORD);
        long startTime = System.nanoTime();
        GraphData graphData = graphSchemaExtractor.getGraphData();
        long dataTime = System.nanoTime();
        System.out.println("DataTime: " + (dataTime - startTime) / 1e9);
        GraphDataWriter graphDataWriter = new GraphDataWriter(graphData, "output2.json");
        graphDataWriter.write(true);
        long analysisTime = System.nanoTime();
        System.out.println("AnalysisTime: " + (analysisTime - dataTime) / 1e9);
        System.out.println("TotalTime: " + (analysisTime - startTime) / 1e9);
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        System.out.println("Memory: " + memoryMXBean.getHeapMemoryUsage().getUsed() / 1e6);
    }
}