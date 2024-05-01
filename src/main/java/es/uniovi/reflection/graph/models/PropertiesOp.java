package es.uniovi.reflection.graph.models;

import es.uniovi.reflection.graph.models.PropertyTypes;
import org.neo4j.driver.internal.value.*;
import org.neo4j.driver.types.Entity;
import org.neo4j.driver.types.IsoDuration;
import org.neo4j.values.storable.CoordinateReferenceSystem;
import org.neo4j.values.storable.DurationValue;

import java.time.*;
import java.util.*;

public class PropertiesOp {

    public static void updatePropertiesMap(Map<String, Map<PropertyTypes, List<String>>> properties, Entity entity) {
        Set<String> propertiesNames = entity.asMap().keySet();
        for (String propertyName : propertiesNames) {
            if (!properties.containsKey(propertyName)) {
                properties.put(propertyName, new HashMap<>());
            }
            Map<PropertyTypes, List<String>> propertiesMap = properties.get(propertyName);
            Object propertyObj = entity.get(propertyName);
            PropertyTypes propertyType = getPropertyType(propertyObj);
            if (!propertiesMap.containsKey(propertyType)) {
                propertiesMap.put(propertyType, new ArrayList<>());
            }
            List<String> values = propertiesMap.get(propertyType);
            values.add(getValueInString(propertyType, propertyObj));
            propertiesMap.put(propertyType, values);
            properties.put(propertyName, propertiesMap);
        }
    }

    private static PropertyTypes getPropertyType(Object propertyObj) {
        if (propertyObj instanceof Long || propertyObj instanceof IntegerValue) {
            return PropertyTypes.INTEGER;
        } else if (propertyObj instanceof Double || propertyObj instanceof FloatValue) {
            return PropertyTypes.FLOAT;
        } else if (propertyObj instanceof String || propertyObj instanceof StringValue) {
            return PropertyTypes.STRING;
        } else if (propertyObj instanceof Boolean || propertyObj instanceof BooleanValue) {
            return PropertyTypes.BOOLEAN;
        } else if (propertyObj instanceof DurationValue || propertyObj instanceof org.neo4j.driver.internal.value.DurationValue) {
            return PropertyTypes.DURATION;
        } else if (propertyObj instanceof LocalDate || propertyObj instanceof DateValue) {
            return PropertyTypes.DATE;
        } else if (propertyObj instanceof OffsetTime || propertyObj instanceof TimeValue) {
            return PropertyTypes.TIME;
        } else if (propertyObj instanceof ZonedDateTime || propertyObj instanceof DateTimeValue) {
            return PropertyTypes.DATETIME;
        } else if (propertyObj instanceof LocalDateTime || propertyObj instanceof LocalDateTimeValue) {
            return PropertyTypes.LOCALDATETIME;
        } else if (propertyObj instanceof LocalTime || propertyObj instanceof LocalTimeValue) {
            return PropertyTypes.LOCALTIME;
        } else if (propertyObj instanceof org.neo4j.values.storable.PointValue) {
            if (((org.neo4j.values.storable.PointValue) propertyObj).getCoordinateReferenceSystem().equals(CoordinateReferenceSystem.CARTESIAN)) {
                return PropertyTypes.POINTCAR;
            } else if (((org.neo4j.values.storable.PointValue) propertyObj).getCoordinateReferenceSystem().equals(CoordinateReferenceSystem.CARTESIAN_3D)) {
                return PropertyTypes.POINTCAR3D;
            } else if (((org.neo4j.values.storable.PointValue) propertyObj).getCoordinateReferenceSystem().equals(CoordinateReferenceSystem.WGS_84)) {
                return PropertyTypes.POINTWGS;
            } else if (((org.neo4j.values.storable.PointValue) propertyObj).getCoordinateReferenceSystem().equals(CoordinateReferenceSystem.WGS_84_3D)) {
                return PropertyTypes.POINTWGS3D;
            }
        } else if (propertyObj instanceof org.neo4j.driver.internal.value.PointValue) {
            if (((org.neo4j.driver.internal.value.PointValue) propertyObj).asPoint().srid() == 7203) {
                return PropertyTypes.POINTCAR;
            } else if (((org.neo4j.driver.internal.value.PointValue) propertyObj).asPoint().srid() == 9157) {
                return PropertyTypes.POINTCAR3D;
            } else if (((org.neo4j.driver.internal.value.PointValue) propertyObj).asPoint().srid() == 4326) {
                return PropertyTypes.POINTWGS;
            } else if (((org.neo4j.driver.internal.value.PointValue) propertyObj).asPoint().srid() == 4979) {
                return PropertyTypes.POINTWGS3D;
            }
        } else if (propertyObj instanceof Object[] || propertyObj instanceof ListValue) {
            Object firstElement;
            if (propertyObj instanceof Object[]) {
                firstElement = ((Object[]) propertyObj)[0];
            } else firstElement = ((ListValue) propertyObj).get(0);
            switch (getPropertyType(firstElement)) {
                case INTEGER:
                    return PropertyTypes.ARRAYINTEGER;
                case FLOAT:
                    return PropertyTypes.ARRAYFLOAT;
                case STRING:
                    return PropertyTypes.ARRAYSTRING;
                case BOOLEAN:
                    return PropertyTypes.ARRAYBOOLEAN;
                case DATE:
                    return PropertyTypes.ARRAYDATE;
                case TIME:
                    return PropertyTypes.ARRAYTIME;
                case DATETIME:
                    return PropertyTypes.ARRAYDATETIME;
                case LOCALDATETIME:
                    return PropertyTypes.ARRAYLOCALDATETIME;
                case LOCALTIME:
                    return PropertyTypes.ARRAYLOCALTIME;
                case POINTCAR:
                    return PropertyTypes.ARRAYPOINTCAR;
                case POINTCAR3D:
                    return PropertyTypes.ARRAYPOINTCAR3D;
                case POINTWGS:
                    return PropertyTypes.ARRAYPOINTWGS;
                case POINTWGS3D:
                    return PropertyTypes.ARRAYPOINTWGS3D;
                case DURATION:
                    return PropertyTypes.ARRAYDURATION;
            }
        }
        System.err.println("Error en tipo de propiedad. Propiedad: " + propertyObj.getClass());
        System.err.println("Error en tipo de propiedad. Propiedad: " + propertyObj);
        return null;
    }

    private static String getValueInString(PropertyTypes propertyType, Object propertyObj) {
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
