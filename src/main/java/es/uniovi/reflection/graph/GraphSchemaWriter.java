package es.uniovi.reflection.graph;

import es.uniovi.reflection.graph.models.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.values.storable.PointValue;

import java.io.FileWriter;
import java.io.IOException;
import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GraphSchemaWriter {

    private GraphData graphData;
    private String outputFile;

    public GraphSchemaWriter(GraphData graphData, String outputFile){
        this.graphData = graphData;
        this.outputFile = outputFile;
    }
    private boolean doColumnArray;

    public void write(boolean doColumnArray) {

        this.doColumnArray = doColumnArray;

        JSONObject jsonObject = new JSONObject();

        //Every node
        JSONArray jsonArrayNodes = new JSONArray();
        JSONObject jsonNodesObject = new JSONObject();
        long nNodes = graphData.getNNodes(); //Numero de nodos
        jsonNodesObject.put("nNodes", nNodes);
        jsonNodesObject.put("nNodesWLabel", graphData.getNNodesWLabel()); //Numero de nodos con, al menos, una etiqueta
        Set<String> labelsNames = graphData.getLabelsNames(); //Lista de etiquetas

        //For each node label
        for (String label : labelsNames) {
            JSONObject jsonObject2 = new JSONObject();
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("absolute", graphData.getLabelNumber(label));
            jsonObject1.put("relative", (double) graphData.getLabelNumber(label) / nNodes);

            jsonObject1.put("nodeProperties", propertiesStatistics(graphData.getPropertiesGivenLabel(label), graphData.getLabelNumber(label)));

            JSONArray jsonArrayOut = getNodeRelationshipsJSON(graphData.getRelationshipsOutGivenLabel(label));
            jsonObject1.put("relationshipsOut", jsonArrayOut);

            JSONArray jsonArrayIn = getNodeRelationshipsJSON(graphData.getRelationshipsInGivenLabel(label));
            jsonObject1.put("relationshipsIn", jsonArrayIn);

            jsonObject2.put(label, jsonObject1);
            jsonArrayNodes.put(jsonObject2);
        }
        jsonNodesObject.put("dataLabels", jsonArrayNodes);
        jsonObject.put("nodes", jsonNodesObject);

        //Multi label nodes
        JSONArray jsonArray1 = new JSONArray();
        int nSimLabels = graphData.getMaxNumberOfSimultaneousLabels();
        if (nSimLabels > 1) {
            JSONObject jsonObject1 = new JSONObject();
            for (long i = 2; i <= nSimLabels; i++) {
                JSONArray jsonArray2 = new JSONArray();
                Map<Iterable<String>, Integer> simultaneousLabelsWNumber = graphData.getMapOfSimultaneousLabels(i);
                JSONObject jsonObject3 = new JSONObject();
                for (Iterable<String> simLabelList : simultaneousLabelsWNumber.keySet()) {
                    JSONArray jsonArray3 = new JSONArray();
                    JSONObject jsonObject4 = new JSONObject();
                    jsonObject4.put("absolute", simultaneousLabelsWNumber.get(simLabelList));
                    jsonObject4.put("relative", (double) simultaneousLabelsWNumber.get(simLabelList) / nNodes);
                    for (String simLabel : simLabelList) {
                        jsonObject4.put(simLabel, (double) simultaneousLabelsWNumber.get(simLabelList) / graphData.getLabelNumber(simLabel));
                    }
                    jsonArray3.put(jsonObject4);
                    jsonObject3.put(simLabelList.toString(), jsonArray3);
                }
                if (!jsonObject3.isEmpty()) {
                    jsonArray2.put(jsonObject3);
                    jsonObject1.put(i + " labels", jsonArray2);
                }
            }
            jsonArray1.put(jsonObject1);
        }
        jsonObject.put("multiLabelNodes", jsonArray1);

        //Every relationship
        JSONArray jsonArrayRelationships = new JSONArray();
        JSONObject jsonObjectRelationship = new JSONObject();
        long nRelationships = graphData.getNRelationships();
        jsonObjectRelationship.put("nRelationships", nRelationships);
        jsonObjectRelationship.put("nRelationshipsWLab", nRelationships); //Numero de relaciones con tipo
        Set<String> relationshipsTypes = graphData.getRelationshipsTypes(); //Lista de tipos de relaciones

        //For each relationship type
        for (String type : relationshipsTypes) {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("absolute", graphData.getTypeNumber(type));
            jsonObject1.put("relative", (double) graphData.getTypeNumber(type) / nRelationships);

            jsonObject1.put("relationshipProperties", propertiesStatistics(graphData.getRelationshipProperties(type), graphData.getTypeNumber(type)));
            jsonObject1.put("relationshipsWithInNode", getJSONOfNodesOfRelationship(graphData.getRelationshipNodeIn(type), graphData.getTypeNumber(type)));
            jsonObject1.put("relationshipsWithOutNode", getJSONOfNodesOfRelationship(graphData.getRelationshipNodeOut(type), graphData.getTypeNumber(type)));
            jsonObject1.put("relationshipsWithSameNode", getJSONOfNodesOfRelationship(graphData.getRelationshipSameNode(type), graphData.getTypeNumber(type)));

            JSONArray jsonArray = new JSONArray();
            for (String beginNodeLabel : graphData.getPropertiesGivenNodesLabel(type)) {
                JSONObject jsonObject2 = new JSONObject();
                JSONArray jsonArray2 = new JSONArray();
                for (String endNodeLabel : graphData.getPropertiesGivenNodesLabelAndBegin(type, beginNodeLabel)) {
                    JSONObject jsonObject3 = new JSONObject();
                    jsonObject3.put(endNodeLabel, propertiesStatistics(graphData.getPropertiesGivenNodesLabelBeginEnd(type, beginNodeLabel, endNodeLabel), graphData.getTypeNumber(type)));
                    jsonArray2.put(jsonObject3);
                }
                jsonObject2.put(beginNodeLabel, jsonArray2);
                jsonArray.put(jsonObject2);
            }
            jsonObject1.put("relationshipGivenNodeLabelsProperties", jsonArray);


            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put(type, jsonObject1);
            jsonArrayRelationships.put(jsonObject2);
        }
        jsonObjectRelationship.put("dataLabels", jsonArrayRelationships);
        jsonObject.put("relationships", jsonObjectRelationship);

        FileWriter file;
        try {
            file = new FileWriter(outputFile);
            file.write(jsonObject.toString());
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JSONArray getJSONOfNodesOfRelationship(Map<String, Integer> nodeTypeNumber, long nRelationshipsGivenLabel) {
        JSONArray jsonArrayOut = new JSONArray();
        for (String nodeLabel : nodeTypeNumber.keySet()) {
            JSONObject jsonObject2 = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("absolute", nodeTypeNumber.get(nodeLabel));
            jsonObject3.put("relative", (double) nodeTypeNumber.get(nodeLabel) / nRelationshipsGivenLabel);
            jsonArray.put(jsonObject3);
            jsonObject2.put(nodeLabel, jsonArray);
            jsonArrayOut.put(jsonObject2);
        }
        return jsonArrayOut;
    }

    private JSONArray getNodeRelationshipsJSON(Map<String, GraphNodeRelationships> nodeRelationships) {
        JSONArray jsonArrayIn = new JSONArray();
        for (String relationshipOutLabel : nodeRelationships.keySet()) {
            JSONObject jsonObject2 = new JSONObject();
            JSONArray jsonArray1 = new JSONArray();
            JSONObject jsonObject3 = new JSONObject();
            long absolute = nodeRelationships.get(relationshipOutLabel).getnRelationships();
            jsonObject3.put("absolute", absolute);
            jsonObject3.put("relative", (double) absolute / graphData.getNRelationships());
            JSONArray jsonArray2 = new JSONArray();
            for (String relationshipOutInLabel : nodeRelationships.get(relationshipOutLabel).getLabels().keySet()) {
                JSONObject jsonObject4 = new JSONObject();
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                jsonObject4.put("absolute", nodeRelationships.get(relationshipOutLabel).getLabels().get(relationshipOutInLabel));
                jsonObject4.put("relative", (double) nodeRelationships.get(relationshipOutLabel).getLabels().get(relationshipOutInLabel) / absolute);
                jsonArray.put(jsonObject4);
                jsonObject.put(relationshipOutInLabel, jsonArray);
                jsonArray2.put(jsonObject);
            }
            jsonObject3.put("byType", jsonArray2);
            jsonArray1.put(jsonObject3);
            jsonObject2.put(relationshipOutLabel, jsonArray1);
            jsonArrayIn.put(jsonObject2);
        }
        return jsonArrayIn;
    }

    private JSONArray propertiesStatistics(Map<String, Map<GraphPropertyTypes, List<String>>> propertyWNumber, long nNodes) {
        JSONArray jsonArray = new JSONArray();
        for (String propertyName : propertyWNumber.keySet()) {
            JSONObject jsonObject3 = new JSONObject();
            JSONArray jsonArray3 = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            Long absolute = propertyWNumber.get(propertyName).values().stream().mapToLong(List::size).sum();
            jsonObject.put("absolute", absolute);
            jsonObject.put("relative", (double) absolute / nNodes);
            JSONArray jsonArray1 = new JSONArray();
            for (GraphPropertyTypes propertyType : propertyWNumber.get(propertyName).keySet()) {
                JSONObject jsonObject2 = new JSONObject();
                jsonObject2.put("absolute", propertyWNumber.get(propertyName).get(propertyType).size());
                jsonObject2.put("relative", (double) propertyWNumber.get(propertyName).get(propertyType).size() / absolute);
                JSONArray jsonArray2 = new JSONArray();
                ArrayList<Object> propertiesWCorrectType = stringToCorrectType(propertyType, propertyWNumber.get(propertyName).get(propertyType));
                jsonObject2.put("analysis", doStatistics(propertyType, propertiesWCorrectType));
                jsonArray2.put(jsonObject2);
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put(propertyType.toString(), jsonArray2);
                jsonArray1.put(jsonObject1);
            }
            jsonObject.put("byType", jsonArray1);
            jsonArray3.put(jsonObject);
            jsonObject3.put(propertyName, jsonArray3);
            jsonArray.put(jsonObject3);
        }
        return jsonArray;
    }

    //Dado el tipo de valor de la propiedad, junto a la lista de valores, devuelve la lista de valores con los tipos correctos.
    private ArrayList<Object> stringToCorrectType(GraphPropertyTypes propertyType, List<String> propertyValues) {
        ArrayList<Object> values = new ArrayList<>();
        for (String propertyValue : propertyValues) {
            switch (propertyType) {
                case INTEGER, DURATION:
                    values.add(Long.parseLong(propertyValue));
                    break;
                case FLOAT:
                    values.add(Double.parseDouble(propertyValue));
                    break;
                case STRING:
                    values.add(propertyValue);
                    break;
                case BOOLEAN:
                    values.add(Boolean.parseBoolean(propertyValue));
                    break;
                case DATE:
                    values.add(LocalDate.parse(propertyValue));
                    break;
                case TIME:
                    values.add(OffsetTime.parse(propertyValue));
                    break;
                case DATETIME:
                    values.add(ZonedDateTime.parse(propertyValue));
                    break;
                case LOCALDATETIME:
                    values.add(LocalDateTime.parse(propertyValue));
                    break;
                case LOCALTIME:
                    values.add(LocalTime.parse(propertyValue));
                    break;
                case POINTCAR, POINTCAR3D, POINTWGS, POINTWGS3D:
                    values.add(PointValue.parse(propertyValue));
                    break;
                case ARRAYINTEGER, ARRAYFLOAT, ARRAYBOOLEAN, ARRAYSTRING, ARRAYDATE, ARRAYTIME, ARRAYDATETIME,
                        ARRAYLOCALTIME, ARRAYLOCALDATETIME, ARRAYDURATION:
                    values.add(List.of(propertyValue.replaceAll("^\\[|]$", "").split(",")));
                    break;
                case ARRAYPOINTCAR, ARRAYPOINTCAR3D, ARRAYPOINTWGS, ARRAYPOINTWGS3D:
                    values.add(List.of(propertyValue.replaceAll("^\\[|]$", "").split("\\),")));
                    break;
            }
        }
        return values;
    }

    //Dado el nombre de la propiedad, el tipo, y los valores de la misma; realiza las estadisticas necesarias del tipo y las añade al JSON.
    private JSONArray doStatistics(GraphPropertyTypes propertyType, ArrayList<Object> propertiesWCorrectType) {
        JSONArray jsonArray = new JSONArray();
        switch (propertyType) {
            case INTEGER, DURATION: {
                List<Long> propertiesLong = new ArrayList<>(propertiesWCorrectType.stream().map(val -> (Long) val).toList());
                Collections.sort(propertiesLong);
                return integerAnalysis(propertiesLong);
            }
            case FLOAT: {
                List<Double> propertiesDouble = new ArrayList<>(propertiesWCorrectType.stream().map(val -> (Double) val).toList());
                Collections.sort(propertiesDouble);
                return floatAnalysis(propertiesDouble);
            }
            case STRING: {
                List<String> propertiesString = new ArrayList<>(propertiesWCorrectType.stream().map(val -> (String) val).toList());
                Collections.sort(propertiesString);
                Map<String, Long> counted = propertiesString.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                for (String name : counted.keySet()) {
                    jsonArray.put(getFrequencies(counted, propertiesString.size(), name));
                }
                jsonArray.put(getModa(counted, propertiesString.size()));
                return jsonArray;
            }
            case BOOLEAN: {
                List<Boolean> propertiesBoolean = new ArrayList<>(propertiesWCorrectType.stream().map(val -> (Boolean) val).toList());
                Collections.sort(propertiesBoolean);
                Map<Boolean, Long> counted = propertiesBoolean.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                for (Boolean bool : counted.keySet()) {
                    jsonArray.put(getFrequencies(counted, propertiesBoolean.size(), bool));
                }

                jsonArray.put(getModa(counted, propertiesBoolean.size()));
                return jsonArray;
            }
            case DATE: {
                JSONObject jsonObject1 = new JSONObject();
                List<Long> propertiesDate = new ArrayList<>(propertiesWCorrectType.stream().map(val -> ((LocalDate) val).toEpochDay()).toList());
                Collections.sort(propertiesDate);
                long average = (long) propertiesDate.stream().mapToLong(val -> val).average().orElse(0.0);
                jsonObject1.put("average", LocalDate.ofEpochDay(average));
                double deviation = 0.0;
                for (Long value : propertiesDate) {
                    double delta = value - average;
                    deviation += delta * delta;
                }
                deviation = Math.sqrt(deviation / propertiesDate.size());
                jsonObject1.put("deviation", LocalDate.ofEpochDay((long) deviation));

                long max = propertiesDate.stream().mapToLong(val -> val).max().orElse(0);
                jsonObject1.put("max", LocalDate.ofEpochDay(max));
                long min = propertiesDate.stream().mapToLong(val -> val).min().orElse(0);
                jsonObject1.put("min", LocalDate.ofEpochDay(min));
                long percentile25 = percentileLong(propertiesDate, 25);
                jsonObject1.put("percentile25", LocalDate.ofEpochDay(percentile25));
                long percentile50 = percentileLong(propertiesDate, 50);
                jsonObject1.put("percentile50", LocalDate.ofEpochDay(percentile50));
                long percentile75 = percentileLong(propertiesDate, 75);
                jsonObject1.put("percentile75", LocalDate.ofEpochDay(percentile75));

                List<Long> listOfMax = getListOfMax(propertiesDate);
                jsonObject1.put("moda", listOfMax.stream().map(LocalDate::ofEpochDay).toList());
                jsonArray.put(jsonObject1);
                return jsonArray;
            }
            case TIME: {
                JSONObject jsonObject1 = new JSONObject();
                List<Long> propertiesTime = new ArrayList<>(propertiesWCorrectType.stream().map(val -> ((OffsetTime) val).toLocalTime().toNanoOfDay()).toList());
                Collections.sort(propertiesTime);
                long average = (long) propertiesTime.stream().mapToLong(val -> val).average().orElse(0.0);
                jsonObject1.put("average", LocalTime.ofNanoOfDay(average).atOffset(ZoneOffset.of("+00:00")));
                double deviation = 0.0;
                for (Long value : propertiesTime) {
                    double delta = value - average;
                    deviation += delta * delta;
                }
                deviation = Math.sqrt(deviation / propertiesTime.size());
                jsonObject1.put("deviation", LocalTime.ofNanoOfDay((long) deviation).atOffset(ZoneOffset.of("+00:00")));

                long max = propertiesTime.stream().mapToLong(val -> val).max().orElse(0);
                jsonObject1.put("max", LocalTime.ofNanoOfDay(max).atOffset(ZoneOffset.of("+00:00")));
                long min = propertiesTime.stream().mapToLong(val -> val).min().orElse(0);
                jsonObject1.put("min", LocalTime.ofNanoOfDay(min).atOffset(ZoneOffset.of("+00:00")));
                long percentile25 = percentileLong(propertiesTime, 25);
                jsonObject1.put("percentile25", LocalTime.ofNanoOfDay(percentile25).atOffset(ZoneOffset.of("+00:00")));
                long percentile50 = percentileLong(propertiesTime, 50);
                jsonObject1.put("percentile50", LocalTime.ofNanoOfDay(percentile50).atOffset(ZoneOffset.of("+00:00")));
                long percentile75 = percentileLong(propertiesTime, 75);
                jsonObject1.put("percentile75", LocalTime.ofNanoOfDay(percentile75).atOffset(ZoneOffset.of("+00:00")));

                List<Long> listOfMax = getListOfMax(propertiesTime);
                jsonObject1.put("moda", listOfMax.stream().map(val -> LocalTime.ofNanoOfDay(val).atOffset(ZoneOffset.of("+00:00"))).toList());
                jsonArray.put(jsonObject1);
                return jsonArray;
            }
            case DATETIME: {
                JSONObject jsonObject1 = new JSONObject();
                List<Long> propertiesDateTime = new ArrayList<>(propertiesWCorrectType.stream().map(val -> ((ZonedDateTime) val).toInstant().toEpochMilli()).toList());
                Collections.sort(propertiesDateTime);
                long average = (long) propertiesDateTime.stream().mapToLong(val -> val).average().orElse(0.0);
                jsonObject1.put("average", ZonedDateTime.ofInstant(Instant.ofEpochMilli(average), ZoneId.systemDefault()));
                double deviation = 0.0;
                for (Long value : propertiesDateTime) {
                    double delta = value - average;
                    deviation += delta * delta;
                }
                deviation = Math.sqrt(deviation / propertiesDateTime.size());
                jsonObject1.put("deviation", ZonedDateTime.ofInstant(Instant.ofEpochMilli((long) deviation), ZoneId.systemDefault()));

                long max = propertiesDateTime.stream().mapToLong(val -> val).max().orElse(0);
                jsonObject1.put("max", ZonedDateTime.ofInstant(Instant.ofEpochMilli(max), ZoneId.systemDefault()));
                long min = propertiesDateTime.stream().mapToLong(val -> val).min().orElse(0);
                jsonObject1.put("min", ZonedDateTime.ofInstant(Instant.ofEpochMilli(min), ZoneId.systemDefault()));
                long percentile25 = percentileLong(propertiesDateTime, 25);
                jsonObject1.put("percentile25", ZonedDateTime.ofInstant(Instant.ofEpochMilli(percentile25), ZoneId.systemDefault()));
                long percentile50 = percentileLong(propertiesDateTime, 50);
                jsonObject1.put("percentile50", ZonedDateTime.ofInstant(Instant.ofEpochMilli(percentile50), ZoneId.systemDefault()));
                long percentile75 = percentileLong(propertiesDateTime, 75);
                jsonObject1.put("percentile75", ZonedDateTime.ofInstant(Instant.ofEpochMilli(percentile75), ZoneId.systemDefault()));

                List<Long> listOfMax = getListOfMax(propertiesDateTime);
                jsonObject1.put("moda", listOfMax.stream().map(val -> ZonedDateTime.ofInstant(Instant.ofEpochMilli(val), ZoneId.systemDefault())).toList());
                jsonArray.put(jsonObject1);
                return jsonArray;
            }
            case LOCALDATETIME: {
                JSONObject jsonObject1 = new JSONObject();
                List<Long> propertiesLocalDateTime = new ArrayList<>(propertiesWCorrectType.stream().map(val -> ((LocalDateTime) val).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).toList());
                Collections.sort(propertiesLocalDateTime);
                long average = (long) propertiesLocalDateTime.stream().mapToLong(val -> val).average().orElse(0.0);
                jsonObject1.put("average", LocalDateTime.ofInstant(Instant.ofEpochMilli(average), ZoneId.systemDefault()));
                double deviation = 0.0;
                for (Long value : propertiesLocalDateTime) {
                    double delta = value - average;
                    deviation += delta * delta;
                }
                deviation = Math.sqrt(deviation / propertiesLocalDateTime.size());
                jsonObject1.put("deviation", LocalDateTime.ofInstant(Instant.ofEpochMilli((long) deviation), ZoneId.systemDefault()));

                long max = propertiesLocalDateTime.stream().mapToLong(val -> val).max().orElse(0);
                jsonObject1.put("max", LocalDateTime.ofInstant(Instant.ofEpochMilli(max), ZoneId.systemDefault()));
                long min = propertiesLocalDateTime.stream().mapToLong(val -> val).min().orElse(0);
                jsonObject1.put("min", LocalDateTime.ofInstant(Instant.ofEpochMilli(min), ZoneId.systemDefault()));
                long percentile25 = percentileLong(propertiesLocalDateTime, 25);
                jsonObject1.put("percentile25", LocalDateTime.ofInstant(Instant.ofEpochMilli(percentile25), ZoneId.systemDefault()));
                long percentile50 = percentileLong(propertiesLocalDateTime, 50);
                jsonObject1.put("percentile50", LocalDateTime.ofInstant(Instant.ofEpochMilli(percentile50), ZoneId.systemDefault()));
                long percentile75 = percentileLong(propertiesLocalDateTime, 75);
                jsonObject1.put("percentile75", LocalDateTime.ofInstant(Instant.ofEpochMilli(percentile75), ZoneId.systemDefault()));

                List<Long> listOfMax = getListOfMax(propertiesLocalDateTime);
                jsonObject1.put("moda", listOfMax.stream().map(val -> LocalDateTime.ofInstant(Instant.ofEpochMilli(val), ZoneId.systemDefault())).toList());
                jsonArray.put(jsonObject1);
                return jsonArray;
            }
            case LOCALTIME: {
                JSONObject jsonObject1 = new JSONObject();
                List<Long> propertiesLocalTime = new ArrayList<>(propertiesWCorrectType.stream().map(val -> ((LocalTime) val).toNanoOfDay()).toList());
                Collections.sort(propertiesLocalTime);
                long average = (long) propertiesLocalTime.stream().mapToLong(val -> val).average().orElse(0.0);
                jsonObject1.put("average", LocalTime.ofNanoOfDay(average));
                double deviation = 0.0;
                for (Long value : propertiesLocalTime) {
                    double delta = value - average;
                    deviation += delta * delta;
                }
                deviation = Math.sqrt(deviation / propertiesLocalTime.size());
                jsonObject1.put("deviation", LocalTime.ofNanoOfDay((long) deviation));

                long max = propertiesLocalTime.stream().mapToLong(val -> val).max().orElse(0);
                jsonObject1.put("max", LocalTime.ofNanoOfDay(max));
                long min = propertiesLocalTime.stream().mapToLong(val -> val).min().orElse(0);
                jsonObject1.put("min", LocalTime.ofNanoOfDay(min));
                long percentile25 = percentileLong(propertiesLocalTime, 25);
                jsonObject1.put("percentile25", LocalTime.ofNanoOfDay(percentile25));
                long percentile50 = percentileLong(propertiesLocalTime, 50);
                jsonObject1.put("percentile50", LocalTime.ofNanoOfDay(percentile50));
                long percentile75 = percentileLong(propertiesLocalTime, 75);
                jsonObject1.put("percentile75", LocalTime.ofNanoOfDay(percentile75));

                List<Long> listOfMax = getListOfMax(propertiesLocalTime);
                jsonObject1.put("moda", listOfMax.stream().map(LocalTime::ofNanoOfDay).toList());
                jsonArray.put(jsonObject1);
                return jsonArray;
            }
            case POINTCAR: {
                jsonArray.put(pointAnalysis("x", propertiesWCorrectType));
                jsonArray.put(pointAnalysis("y", propertiesWCorrectType));
                return jsonArray;
            }
            case POINTCAR3D: {
                jsonArray.put(pointAnalysis("x", propertiesWCorrectType));
                jsonArray.put(pointAnalysis("y", propertiesWCorrectType));
                jsonArray.put(pointAnalysis("z", propertiesWCorrectType));
                return jsonArray;
            }
            case POINTWGS: {
                jsonArray.put(pointAnalysis("latitude", propertiesWCorrectType));
                jsonArray.put(pointAnalysis("longitude", propertiesWCorrectType));
                return jsonArray;
            }
            case POINTWGS3D: {
                jsonArray.put(pointAnalysis("latitude", propertiesWCorrectType));
                jsonArray.put(pointAnalysis("longitude", propertiesWCorrectType));
                jsonArray.put(pointAnalysis("height", propertiesWCorrectType));
                return jsonArray;
            }
            case ARRAYINTEGER, ARRAYDURATION: {
                //Size statistics
                jsonArray.put(arraySizeAnalysis(propertiesWCorrectType));
                //All elements statistics
                List<Long> arraysElements = new ArrayList<>();
                for (Object list : propertiesWCorrectType) {
                    ((List<Object>) list).forEach(val -> arraysElements.add(Long.parseLong(((String) val).replace(" ", ""))));
                }
                JSONObject elementsStats = new JSONObject();
                elementsStats.put("elementsStats", integerAnalysis(arraysElements));
                jsonArray.put(elementsStats);
                //Column elements statistics
                if (doColumnArray) {
                    int maxSize = 0;
                    for (Object list : propertiesWCorrectType) {
                        if (((List<Object>) list).size() > maxSize) maxSize = ((List<Object>) list).size();
                    }
                    JSONObject columnStats = new JSONObject();
                    for (int i = 0; i < maxSize; i++) {
                        List<Long> columnElements = new ArrayList<>();
                        for (Object list : propertiesWCorrectType) {
                            if (i < ((List<Object>) list).size()) {
                                columnElements.add(Long.parseLong(((String) ((List<Object>) list).get(i)).replace(" ", "")));
                            }
                        }
                        columnStats.put(String.valueOf(i), integerAnalysis(columnElements));
                    }
                    jsonArray.put(columnStats);
                }
                return jsonArray;
            }
            case ARRAYFLOAT: {
                //Size statistics
                jsonArray.put(arraySizeAnalysis(propertiesWCorrectType));
                //All elements statistics
                List<Double> arraysElements = new ArrayList<>();
                for (Object list : propertiesWCorrectType) {
                    ((List<Object>) list).forEach(val -> arraysElements.add(Double.parseDouble(((String) val).replace(" ", ""))));
                }
                JSONObject elementsStats = new JSONObject();
                elementsStats.put("elementsStats", floatAnalysis(arraysElements));
                jsonArray.put(elementsStats);
                //Column elements statistics
                if (doColumnArray) {
                    int maxSize = 0;
                    for (Object list : propertiesWCorrectType) {
                        if (((List<Object>) list).size() > maxSize) maxSize = ((List<Object>) list).size();
                    }
                    JSONObject columnStats = new JSONObject();
                    for (int i = 0; i < maxSize; i++) {
                        List<Double> columnElements = new ArrayList<>();
                        for (Object list : propertiesWCorrectType) {
                            if (i < ((List<Object>) list).size()) {
                                columnElements.add(Double.parseDouble(((String) ((List<Object>) list).get(i)).replace(" ", "")));
                            }
                        }
                        columnStats.put(String.valueOf(i), floatAnalysis(columnElements));
                    }
                    jsonArray.put(columnStats);
                }
                return jsonArray;
            }
            case ARRAYSTRING, ARRAYBOOLEAN, ARRAYDATE, ARRAYTIME, ARRAYDATETIME, ARRAYLOCALDATETIME, ARRAYLOCALTIME
                    , ARRAYPOINTCAR, ARRAYPOINTCAR3D, ARRAYPOINTWGS, ARRAYPOINTWGS3D: {
                //Size statistics
                jsonArray.put(arraySizeAnalysis(propertiesWCorrectType));
                //All elements statistics
                ArrayList<Object> arraysElements = createBigArray(propertyType, propertiesWCorrectType);
                JSONObject elementsStats = new JSONObject();
                elementsStats.put("elementsStats", doStatistics(getArrayType(propertyType), arraysElements));
                jsonArray.put(elementsStats);
                //Column elements statistics
                if (doColumnArray) {
                    int maxSize = 0;
                    for (Object list : propertiesWCorrectType) {
                        if (((List<Object>) list).size() > maxSize) maxSize = ((List<Object>) list).size();
                    }
                    JSONObject columnStats = new JSONObject();
                    for (int i = 0; i < maxSize; i++) {
                        ArrayList<Object> columnElements = createColumnBigArray(propertyType, propertiesWCorrectType, i);
                        columnStats.put(String.valueOf(i), doStatistics(getArrayType(propertyType), columnElements));
                    }
                    jsonArray.put(columnStats);
                }
                return jsonArray;
            }
        }
        return jsonArray;
    }

    private JSONObject arraySizeAnalysis(ArrayList<Object> propertiesWCorrectType) {
        List<Long> sizeLong = new ArrayList<>(propertiesWCorrectType.stream().map(val -> (long) ((List<Object>) val).size()).toList());
        Collections.sort(sizeLong);
        JSONObject sizeStats = new JSONObject();
        sizeStats.put("sizeStats", integerAnalysis(sizeLong));
        return sizeStats;
    }

    private ArrayList<Object> createBigArray(GraphPropertyTypes graphPropertyTypes, ArrayList<Object> propertiesWCorrectType) {
        ArrayList<Object> arraysElements = new ArrayList<>();
        switch (graphPropertyTypes) {
            case ARRAYSTRING -> {
                propertiesWCorrectType.forEach(list -> arraysElements.addAll((List<Object>) list));
            }
            case ARRAYBOOLEAN -> {
                for (Object list : propertiesWCorrectType) {
                    ((List<Object>) list).forEach(val -> arraysElements.add(Boolean.parseBoolean(((String) val).replace(" ", ""))));
                }
            }
            case ARRAYDATE -> {
                for (Object list : propertiesWCorrectType) {
                    ((List<Object>) list).forEach(val -> arraysElements.add(LocalDate.parse(((String) val).replace(" ", ""))));
                }
            }
            case ARRAYTIME -> {
                for (Object list : propertiesWCorrectType) {
                    ((List<Object>) list).forEach(val -> arraysElements.add(OffsetTime.parse(((String) val).replace(" ", ""))));
                }
            }
            case ARRAYDATETIME -> {
                for (Object list : propertiesWCorrectType) {
                    ((List<Object>) list).forEach(val -> arraysElements.add(ZonedDateTime.parse(((String) val).replace(" ", ""))));
                }
            }
            case ARRAYLOCALDATETIME -> {
                for (Object list : propertiesWCorrectType) {
                    ((List<Object>) list).forEach(val -> arraysElements.add(LocalDateTime.parse(((String) val).replace(" ", ""))));
                }
            }
            case ARRAYLOCALTIME -> {
                for (Object list : propertiesWCorrectType) {
                    ((List<Object>) list).forEach(val -> arraysElements.add(LocalTime.parse(((String) val).replace(" ", ""))));
                }
            }
            case ARRAYPOINTCAR, ARRAYPOINTCAR3D, ARRAYPOINTWGS, ARRAYPOINTWGS3D -> {
                for (Object list : propertiesWCorrectType) {
                    ((List<Object>) list).forEach(val -> {
                        arraysElements.add(PointValue.parse(((String) val)));
                    });
                }
            }
        }
        return arraysElements;
    }

    private ArrayList<Object> createColumnBigArray(GraphPropertyTypes graphPropertyTypes, ArrayList<Object> propertiesWCorrectType, int column) {
        ArrayList<Object> columnElements = new ArrayList<>();
        switch (graphPropertyTypes) {
            case ARRAYSTRING -> {
                for (Object list : propertiesWCorrectType) {
                    if (column < ((List<Object>) list).size()) {
                        columnElements.add(((List<Object>) list).get(column));
                    }
                }
            }
            case ARRAYBOOLEAN -> {
                for (Object list : propertiesWCorrectType) {
                    if (column < ((List<Object>) list).size()) {
                        columnElements.add(Boolean.parseBoolean(((String) ((List<Object>) list).get(column)).replace(" ", "")));
                    }
                }
            }
            case ARRAYDATE -> {
                for (Object list : propertiesWCorrectType) {
                    if (column < ((List<Object>) list).size()) {
                        columnElements.add(LocalDate.parse(((String) ((List<Object>) list).get(column)).replace(" ", "")));
                    }
                }
            }
            case ARRAYTIME -> {
                for (Object list : propertiesWCorrectType) {
                    if (column < ((List<Object>) list).size()) {
                        columnElements.add(OffsetTime.parse(((String) ((List<Object>) list).get(column)).replace(" ", "")));
                    }
                }
            }
            case ARRAYDATETIME -> {
                for (Object list : propertiesWCorrectType) {
                    if (column < ((List<Object>) list).size()) {
                        columnElements.add(ZonedDateTime.parse(((String) ((List<Object>) list).get(column)).replace(" ", "")));
                    }
                }
            }
            case ARRAYLOCALDATETIME -> {
                for (Object list : propertiesWCorrectType) {
                    if (column < ((List<Object>) list).size()) {
                        columnElements.add(LocalDateTime.parse(((String) ((List<Object>) list).get(column)).replace(" ", "")));
                    }
                }
            }
            case ARRAYLOCALTIME -> {
                for (Object list : propertiesWCorrectType) {
                    if (column < ((List<Object>) list).size()) {
                        columnElements.add(LocalTime.parse(((String) ((List<Object>) list).get(column)).replace(" ", "")));
                    }
                }
            }
            case ARRAYPOINTCAR, ARRAYPOINTCAR3D, ARRAYPOINTWGS, ARRAYPOINTWGS3D -> {
                for (Object list : propertiesWCorrectType) {
                    if (column < ((List<Object>) list).size()) {
                        columnElements.add(PointValue.parse(((String) ((List<Object>) list).get(column)).replace(" ", "")));
                    }
                }
            }
        }
        return columnElements;
    }

    private GraphPropertyTypes getArrayType(GraphPropertyTypes propertyType) {
        switch (propertyType) {
            case ARRAYSTRING -> {
                return GraphPropertyTypes.STRING;
            }
            case ARRAYBOOLEAN -> {
                return GraphPropertyTypes.BOOLEAN;
            }
            case ARRAYDATE -> {
                return GraphPropertyTypes.DATE;
            }
            case ARRAYTIME -> {
                return GraphPropertyTypes.TIME;
            }
            case ARRAYDATETIME -> {
                return GraphPropertyTypes.DATETIME;
            }
            case ARRAYLOCALDATETIME -> {
                return GraphPropertyTypes.LOCALDATETIME;
            }
            case ARRAYLOCALTIME -> {
                return GraphPropertyTypes.LOCALTIME;
            }
            case ARRAYPOINTCAR -> {
                return GraphPropertyTypes.POINTCAR;
            }
            case ARRAYPOINTCAR3D -> {
                return GraphPropertyTypes.POINTCAR3D;
            }
            case ARRAYPOINTWGS -> {
                return GraphPropertyTypes.POINTWGS;
            }
            case ARRAYPOINTWGS3D -> {
                return GraphPropertyTypes.POINTWGS3D;
            }
            default -> {
                System.err.println("Error en tipo de propiedad. Propiedad: " + propertyType);
                return null;
            }
        }
    }

    private JSONArray integerAnalysis(List<Long> propertiesLong) {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        double average = propertiesLong.stream().mapToLong(val -> val).average().orElse(0.0);
        jsonObject.put("average", average);
        double deviation = 0.0;
        for (Long value : propertiesLong) {
            double delta = value - average;
            deviation += delta * delta;
        }
        jsonObject.put("deviation", Math.sqrt(deviation / propertiesLong.size()));
        jsonObject.put("max", propertiesLong.stream().mapToLong(val -> val).max().orElse(0));
        jsonObject.put("min", propertiesLong.stream().mapToLong(val -> val).min().orElse(0));
        jsonObject.put("percentile25", percentileLong(propertiesLong, 25));
        jsonObject.put("percentile50", percentileLong(propertiesLong, 50));
        jsonObject.put("percentile75", percentileLong(propertiesLong, 75));
        jsonObject.put("moda", getListOfMax(propertiesLong));
        jsonArray.put(jsonObject);
        return jsonArray;
    }

    private JSONArray floatAnalysis(List<Double> propertiesDouble) {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        double average = propertiesDouble.stream().mapToDouble(val -> val).average().orElse(0.0);
        jsonObject.put("average", average);
        double deviation = 0.0;
        for (Double value : propertiesDouble) {
            double delta = value - average;
            deviation += delta * delta;
        }
        jsonObject.put("deviation", Math.sqrt(deviation / propertiesDouble.size()));
        jsonObject.put("max", propertiesDouble.stream().mapToDouble(val -> val).max().orElse(0));
        jsonObject.put("min", propertiesDouble.stream().mapToDouble(val -> val).min().orElse(0));
        jsonObject.put("percentile25", percentileDouble(propertiesDouble, 25));
        jsonObject.put("percentile50", percentileDouble(propertiesDouble, 50));
        jsonObject.put("percentile75", percentileDouble(propertiesDouble, 75));
        jsonArray.put(jsonObject);
        return jsonArray;
    }

    private List<Long> getListOfMax(List<Long> propertiesList) {
        Map<Long, Long> counted = propertiesList.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        long maxAppear = counted.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getValue();

        return counted.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), maxAppear)).map(Map.Entry::getKey).toList();
    }

    private double percentileDouble(List<Double> propertiesDouble, int percentile) {
        int index = (int) Math.ceil(percentile / 100.0 * propertiesDouble.size());
        return propertiesDouble.get(index - 1);
    }

    private long percentileLong(List<Long> propertiesLong, int percentile) {
        int index = (int) Math.ceil(percentile / 100.0 * propertiesLong.size());
        return propertiesLong.get(index - 1);
    }

    private JSONObject pointAnalysis(String position, ArrayList<Object> propertiesWCorrectType) {
        JSONObject jsonObject = new JSONObject();
        List<Double> propertiesPoint = new ArrayList<>(propertiesWCorrectType.stream().map(val -> ((Double) ((PointValue) val).get(position).asObject())).toList());
        Collections.sort(propertiesPoint);
        jsonObject.put(position, floatAnalysis(propertiesPoint));
        return jsonObject;
    }

    private JSONObject getFrequencies(Map<?, Long> counted, int size, Object name) {
        JSONArray jsonArray1 = new JSONArray();
        JSONObject jsonObject1 = new JSONObject();
        JSONObject jsonObject2 = new JSONObject();
        jsonObject1.put("absolute", counted.get(name));
        jsonObject1.put("relative", (double) counted.get(name) / size);
        jsonArray1.put(jsonObject1);
        jsonObject2.put(name.toString(), jsonArray1);
        return jsonObject2;
    }

    private JSONObject getModa(Map<?, Long> counted, int size) {
        long maxAppear = counted.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getValue();

        JSONArray jsonArray1 = new JSONArray();
        JSONObject jsonObject1 = new JSONObject();
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("percentage", (double) maxAppear / size);
        jsonObject2.put("values", counted.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), maxAppear)).map(Map.Entry::getKey).toList());
        jsonArray1.put(jsonObject2);
        jsonObject1.put("moda", jsonArray1);
        return jsonObject1;
    }
}