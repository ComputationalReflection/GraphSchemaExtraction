package es.uniovi.reflection;

import es.uniovi.reflection.codeanalysis.core.ProgQueryClient;

import javax.tools.Diagnostic;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    private static String NEO4J_HOST = "progquery.uniovi.es";
    private static String NEO4J_PORT_NUMBER = "7687";
    private static String NEO4J_USER = "neo4j";
    private static String NEO4J_PASSWORD = "secreto";
    private static String NEO4J_DATABASE = "maniega";
    private static String GRAPH_SCHEMA_EXTRACTOR_JAR = "GraphSchemaExtractor-1.0.0.jar";
    private static String OUTPUT_FILE = "output.txt";

    private static int GRAPH_SCHEMA_EXTRACTOR_MEMORY = 80;


    public static void main(String[] args) throws IOException {
        if(args.length < 2) {
            System.out.println("Program path, iterations and memory are required as argument");
            System.exit(1);
        }
        String sourceFolder = args[0];
        int iterations = Integer.parseInt(args[1]);
        if (args.length == 3)
            GRAPH_SCHEMA_EXTRACTOR_MEMORY = Integer.parseInt(args[2]);
        ProgQueryClient progQueryClient =
                        new ProgQueryClient(NEO4J_HOST, NEO4J_PORT_NUMBER, NEO4J_USER, NEO4J_PASSWORD, NEO4J_DATABASE, "80000", false);
        writeToFile("Iteration;Nodes;Relationships;GettingGraphDataTime;GettingGraphDataMemoryUsage;WritingSchemaTime;TotalTime;TotalMemory\n");
        for (int i = 0; i < iterations; i++) {
            System.out.print("Iteration: " + i + "\n");
            String programId = "P" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
            System.out.print("Inserting " + programId + "... ");
            progQueryClient.insert(sourceFolder, NEO4J_DATABASE, programId);
            System.out.println("completed");
            System.out.println("Getting Graph Schema " + NEO4J_HOST + ":" + NEO4J_PORT_NUMBER + ":" + NEO4J_USER + ":" + NEO4J_DATABASE + "... ");
            String output = graphSchemaExtraction();
            List<String> values = new ArrayList<>();
            values.add(""+ i);
            Arrays.asList(output.split("\n")).stream().forEach(l -> values.add(l.split(": ")[1]));
            writeToFile(String.join(";", values) + "\n");
        }
    }

    private static String graphSchemaExtraction(){
        try {
            String javaCommand = "java -Xmx" + GRAPH_SCHEMA_EXTRACTOR_MEMORY + "g -jar " + GRAPH_SCHEMA_EXTRACTOR_JAR + " -nh=\""+ NEO4J_HOST + "\" -np=\"" + NEO4J_PASSWORD + "\" -ndb=\"" + NEO4J_DATABASE + "\"";
            Process process = null;
            if (System.getProperty("os.name").toLowerCase().contains("linux"))
                process =  Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c", javaCommand});
            else
                process =  Runtime.getRuntime().exec(javaCommand);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String linea;
            while ((linea = bufferedReader.readLine()) != null) {
                System.out.println(linea);
                output.append(linea).append("\n");
            }
            process.waitFor();
            System.out.println("Command completed!!");
            return output.toString();
        } catch (IOException | InterruptedException e) {
            System.err.println("Graph Schema Extraction failed!");
            e.printStackTrace();
            return "";
        }
    }

    private static void writeToFile(String content) {
        try {
            FileWriter fileWriter = new FileWriter(OUTPUT_FILE,true);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}