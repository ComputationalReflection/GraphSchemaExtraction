package es.uniovi.reflection.graph.models;

import org.neo4j.driver.types.Relationship;

import java.util.HashMap;
import java.util.Map;

public class MyRelationships {

    private long nRelationships = 0;
    private Map<String, MyType> types = new HashMap<>();

    public void addRelationship(Relationship r, Iterable<String> exitLabels, Iterable<String> entryLabels) {
        nRelationships++;
        if (!types.containsKey(r.type())){
            types.put(r.type(), new MyType());
        }
        MyType myType = types.get(r.type());
        myType.addProperties(r);
        myType.addRelationshipsIn(entryLabels);
        myType.addRelationshipsOut(exitLabels);
        myType.addRelationshipsSameNode(entryLabels, exitLabels);
        myType.addPropertiesGivenNodeLabels(r, entryLabels, exitLabels);
        types.put(r.type(), myType);
    }

    public long getNRelationships() {
        return nRelationships;
    }

    public Map<String, MyType> getTypes() {
        return types;
    }
}
