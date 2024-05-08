package es.uniovi.reflection.graph.models;

import org.neo4j.driver.internal.value.*;
import org.neo4j.driver.types.Entity;
import org.neo4j.driver.types.IsoDuration;
import org.neo4j.values.storable.CoordinateReferenceSystem;
import org.neo4j.values.storable.DurationValue;
import java.time.*;
import java.util.*;

public class GraphPropertiesOp {

    public static void updatePropertiesMap(Map<String, Map<GraphPropertyTypes, List<String>>> properties, Entity entity) {
        Set<String> propertiesNames = entity.asMap().keySet();
        for (String propertyName : propertiesNames) {
            if (!properties.containsKey(propertyName)) {
                properties.put(propertyName, new HashMap<>());
            }
            Map<GraphPropertyTypes, List<String>> propertiesMap = properties.get(propertyName);
            Object propertyObj = entity.get(propertyName);
            GraphPropertyTypes propertyType = getPropertyType(propertyObj);
            if (!propertiesMap.containsKey(propertyType)) {
                propertiesMap.put(propertyType, new ArrayList<>());
            }
            List<String> values = propertiesMap.get(propertyType);
            values.add(getValueInString(propertyType, propertyObj));
            propertiesMap.put(propertyType, values);
            properties.put(propertyName, propertiesMap);
        }
    }

    private static GraphPropertyTypes getPropertyType(Object propertyObj) {
        if (propertyObj instanceof Long || propertyObj instanceof IntegerValue) {
            return GraphPropertyTypes.INTEGER;
        } else if (propertyObj instanceof Double || propertyObj instanceof FloatValue) {
            return GraphPropertyTypes.FLOAT;
        } else if (propertyObj instanceof String || propertyObj instanceof StringValue) {
            return GraphPropertyTypes.STRING;
        } else if (propertyObj instanceof Boolean || propertyObj instanceof BooleanValue) {
            return GraphPropertyTypes.BOOLEAN;
        } else if (propertyObj instanceof DurationValue || propertyObj instanceof org.neo4j.driver.internal.value.DurationValue) {
            return GraphPropertyTypes.DURATION;
        } else if (propertyObj instanceof LocalDate || propertyObj instanceof DateValue) {
            return GraphPropertyTypes.DATE;
        } else if (propertyObj instanceof OffsetTime || propertyObj instanceof TimeValue) {
            return GraphPropertyTypes.TIME;
        } else if (propertyObj instanceof ZonedDateTime || propertyObj instanceof DateTimeValue) {
            return GraphPropertyTypes.DATETIME;
        } else if (propertyObj instanceof LocalDateTime || propertyObj instanceof LocalDateTimeValue) {
            return GraphPropertyTypes.LOCALDATETIME;
        } else if (propertyObj instanceof LocalTime || propertyObj instanceof LocalTimeValue) {
            return GraphPropertyTypes.LOCALTIME;
        } else if (propertyObj instanceof org.neo4j.values.storable.PointValue) {
            if (((org.neo4j.values.storable.PointValue) propertyObj).getCoordinateReferenceSystem().equals(CoordinateReferenceSystem.Cartesian)) {
                return GraphPropertyTypes.POINTCAR;
            } else if (((org.neo4j.values.storable.PointValue) propertyObj).getCoordinateReferenceSystem().equals(CoordinateReferenceSystem.Cartesian_3D)) {
                return GraphPropertyTypes.POINTCAR3D;
            } else if (((org.neo4j.values.storable.PointValue) propertyObj).getCoordinateReferenceSystem().equals(CoordinateReferenceSystem.WGS84)) {
                return GraphPropertyTypes.POINTWGS;
            } else if (((org.neo4j.values.storable.PointValue) propertyObj).getCoordinateReferenceSystem().equals(CoordinateReferenceSystem.WGS84_3D)) {
                return GraphPropertyTypes.POINTWGS3D;
            }
        } else if (propertyObj instanceof org.neo4j.driver.internal.value.PointValue) {
            if (((org.neo4j.driver.internal.value.PointValue) propertyObj).asPoint().srid() == 7203) {
                return GraphPropertyTypes.POINTCAR;
            } else if (((org.neo4j.driver.internal.value.PointValue) propertyObj).asPoint().srid() == 9157) {
                return GraphPropertyTypes.POINTCAR3D;
            } else if (((org.neo4j.driver.internal.value.PointValue) propertyObj).asPoint().srid() == 4326) {
                return GraphPropertyTypes.POINTWGS;
            } else if (((org.neo4j.driver.internal.value.PointValue) propertyObj).asPoint().srid() == 4979) {
                return GraphPropertyTypes.POINTWGS3D;
            }
        } else if (propertyObj instanceof Object[] || propertyObj instanceof ListValue) {
            Object firstElement;
            if (propertyObj instanceof Object[]) {
                firstElement = ((Object[]) propertyObj)[0];
            } else firstElement = ((ListValue) propertyObj).get(0);
            switch (getPropertyType(firstElement)) {
                case INTEGER:
                    return GraphPropertyTypes.ARRAYINTEGER;
                case FLOAT:
                    return GraphPropertyTypes.ARRAYFLOAT;
                case STRING:
                    return GraphPropertyTypes.ARRAYSTRING;
                case BOOLEAN:
                    return GraphPropertyTypes.ARRAYBOOLEAN;
                case DATE:
                    return GraphPropertyTypes.ARRAYDATE;
                case TIME:
                    return GraphPropertyTypes.ARRAYTIME;
                case DATETIME:
                    return GraphPropertyTypes.ARRAYDATETIME;
                case LOCALDATETIME:
                    return GraphPropertyTypes.ARRAYLOCALDATETIME;
                case LOCALTIME:
                    return GraphPropertyTypes.ARRAYLOCALTIME;
                case POINTCAR:
                    return GraphPropertyTypes.ARRAYPOINTCAR;
                case POINTCAR3D:
                    return GraphPropertyTypes.ARRAYPOINTCAR3D;
                case POINTWGS:
                    return GraphPropertyTypes.ARRAYPOINTWGS;
                case POINTWGS3D:
                    return GraphPropertyTypes.ARRAYPOINTWGS3D;
                case DURATION:
                    return GraphPropertyTypes.ARRAYDURATION;
            }
        }
        System.err.println("Error en tipo de propiedad. Propiedad: " + propertyObj.getClass());
        System.err.println("Error en tipo de propiedad. Propiedad: " + propertyObj);
        return null;
    }

    private static String getValueInString(GraphPropertyTypes propertyType, Object propertyObj) {
        switch (propertyType) {
            case INTEGER:
            case FLOAT:
            case STRING:
            case BOOLEAN:
            case DATE:
            case TIME:
            case DATETIME:
            case LOCALDATETIME:
            case LOCALTIME:
            case ARRAYBOOLEAN, ARRAYDATE, ARRAYDATETIME, ARRAYFLOAT, ARRAYLOCALDATETIME, ARRAYLOCALTIME,
                    ARRAYSTRING, ARRAYTIME, ARRAYINTEGER:
                return propertyObj.toString();
            case DURATION:
                if (propertyObj instanceof org.neo4j.driver.internal.value.DurationValue) {
                    return Long.toString(durationValueRemoteToLong((org.neo4j.driver.internal.value.DurationValue) propertyObj));
                }
                return Long.toString(durationValueLocalToLong((DurationValue) propertyObj));
            case POINTCAR, POINTCAR3D, POINTWGS, POINTWGS3D:
                if (propertyObj instanceof org.neo4j.values.storable.PointValue) return propertyObj.toString();
                return fromRemoteToLocal(propertyObj).toString();
            case ARRAYDURATION:
                List<Long> listOfDurations = new ArrayList<>();
                if (propertyObj instanceof Object[]) {
                    for (Object duration : ((Object[]) propertyObj)) {
                        listOfDurations.add(durationValueLocalToLong((DurationValue) duration));
                    }
                } else {
                    for (Object duration : (((ListValue) propertyObj).asList())) {
                        listOfDurations.add(durationValueRemoteToLong((org.neo4j.driver.internal.value.DurationValue) duration));
                    }
                }
                return listOfDurations.toString();
            case ARRAYPOINTCAR, ARRAYPOINTCAR3D, ARRAYPOINTWGS, ARRAYPOINTWGS3D:
                if (propertyObj instanceof Object[]) return propertyObj.toString();
                List<org.neo4j.values.storable.PointValue> listOfPoints = new ArrayList<>();
                for (Object point : ((ListValue) propertyObj).asList()) {
                    listOfPoints.add(fromRemoteToLocal(point));
                }
                return listOfPoints.toString();
            default:
                System.err.println("Error en valor de propiedad. Propiedad: " + propertyObj.getClass());
                return null;
        }
    }

    private static Long durationValueRemoteToLong(org.neo4j.driver.internal.value.DurationValue durationValue) {
        IsoDuration duration = (durationValue).asIsoDuration();
        return duration.days() * 24 * 3600 + duration.seconds();
    }

    private static Long durationValueLocalToLong(DurationValue durationValue) {
        return Duration.parse(durationValue.toString()).toSeconds();
    }

    private static org.neo4j.values.storable.PointValue fromRemoteToLocal(Object pointValue) {
        Map<String, Float> myMap = new HashMap<>();
        String[] pairs = pointValue.toString().replace("Point{", "").replace("}", "").split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            myMap.put(keyValue[0], Float.valueOf(keyValue[1]));
        }
        StringBuilder pointBuilder = new StringBuilder();
        pointBuilder.append("point({");
        switch (myMap.get("srid").intValue()) {
            case 4326 -> {
                pointBuilder.append("x: ").append(myMap.get(" x")).append(",");
                pointBuilder.append("y: ").append(myMap.get(" y")).append(",");
                pointBuilder.append("crs: 'wgs-84'})");
            }
            case 4979 -> {
                pointBuilder.append("x: ").append(myMap.get(" x")).append(",");
                pointBuilder.append("y: ").append(myMap.get(" y")).append(",");
                pointBuilder.append("z: ").append(myMap.get(" z")).append(",");
                pointBuilder.append("crs: 'wgs-84-3d'})");
            }
            case 7203 -> {
                pointBuilder.append("x: ").append(myMap.get(" x")).append(",");
                pointBuilder.append("y: ").append(myMap.get(" y")).append(",");
                pointBuilder.append("crs: 'cartesian'})");
            }
            case 9157 -> {
                pointBuilder.append("x: ").append(myMap.get(" x")).append(",");
                pointBuilder.append("y: ").append(myMap.get(" y")).append(",");
                pointBuilder.append("z: ").append(myMap.get(" z")).append(",");
                pointBuilder.append("crs: 'cartesian-3d'})");
            }
        }
        return org.neo4j.values.storable.PointValue.parse((pointBuilder.toString()));
    }
}
