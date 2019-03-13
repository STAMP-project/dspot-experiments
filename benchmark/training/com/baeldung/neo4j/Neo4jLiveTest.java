package com.baeldung.neo4j;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Result;


public class Neo4jLiveTest {
    private static GraphDatabaseService graphDb;

    @Test
    public void testPersonCar() {
        Neo4jLiveTest.graphDb.beginTx();
        Node car = Neo4jLiveTest.graphDb.createNode(Label.label("Car"));
        car.setProperty("make", "tesla");
        car.setProperty("model", "model3");
        Node owner = Neo4jLiveTest.graphDb.createNode(Label.label("Person"));
        owner.setProperty("firstName", "baeldung");
        owner.setProperty("lastName", "baeldung");
        owner.createRelationshipTo(car, RelationshipType.withName("owner"));
        Result result = Neo4jLiveTest.graphDb.execute(("MATCH (c:Car) <-[owner]- (p:Person) " + ("WHERE c.make = 'tesla'" + "RETURN p.firstName, p.lastName")));
        Map<String, Object> firstResult = result.next();
        Assert.assertEquals("baeldung", firstResult.get("p.firstName"));
    }

    @Test
    public void testCreateNode() {
        Neo4jLiveTest.graphDb.beginTx();
        Result result = Neo4jLiveTest.graphDb.execute(("CREATE (baeldung:Company {name:\"Baeldung\"})" + "RETURN baeldung"));
        Map<String, Object> firstResult = result.next();
        Node firstNode = ((Node) (firstResult.get("baeldung")));
        Assert.assertEquals(firstNode.getProperty("name"), "Baeldung");
    }

    @Test
    public void testCreateNodeAndLink() {
        Neo4jLiveTest.graphDb.beginTx();
        Result result = Neo4jLiveTest.graphDb.execute(("CREATE (baeldung:Company {name:\"Baeldung\"}) " + ("-[:owns]-> (tesla:Car {make: 'tesla', model: 'modelX'})" + "RETURN baeldung, tesla")));
        Map<String, Object> firstResult = result.next();
        Assert.assertTrue(firstResult.containsKey("baeldung"));
        Assert.assertTrue(firstResult.containsKey("tesla"));
    }

    @Test
    public void testFindAndReturn() {
        Neo4jLiveTest.graphDb.beginTx();
        Neo4jLiveTest.graphDb.execute(("CREATE (baeldung:Company {name:\"Baeldung\"}) " + ("-[:owns]-> (tesla:Car {make: 'tesla', model: 'modelX'})" + "RETURN baeldung, tesla")));
        Result result = Neo4jLiveTest.graphDb.execute(("MATCH (company:Company)-[:owns]-> (car:Car)" + ("WHERE car.make='tesla' and car.model='modelX'" + "RETURN company.name")));
        Map<String, Object> firstResult = result.next();
        Assert.assertEquals(firstResult.get("company.name"), "Baeldung");
    }

    @Test
    public void testUpdate() {
        Neo4jLiveTest.graphDb.beginTx();
        Neo4jLiveTest.graphDb.execute(("CREATE (baeldung:Company {name:\"Baeldung\"}) " + ("-[:owns]-> (tesla:Car {make: 'tesla', model: 'modelX'})" + "RETURN baeldung, tesla")));
        Result result = Neo4jLiveTest.graphDb.execute(("MATCH (car:Car)" + (((("WHERE car.make='tesla'" + " SET car.milage=120") + " SET car :Car:Electro") + " SET car.model=NULL") + " RETURN car")));
        Map<String, Object> firstResult = result.next();
        Node car = ((Node) (firstResult.get("car")));
        Assert.assertEquals(car.getProperty("milage"), 120L);
        Assert.assertEquals(car.getLabels(), Arrays.asList(Label.label("Car"), Label.label("Electro")));
        try {
            car.getProperty("model");
            Assert.fail();
        } catch (NotFoundException e) {
            // expected
        }
    }

    @Test
    public void testDelete() {
        Neo4jLiveTest.graphDb.beginTx();
        Neo4jLiveTest.graphDb.execute(("CREATE (baeldung:Company {name:\"Baeldung\"}) " + ("-[:owns]-> (tesla:Car {make: 'tesla', model: 'modelX'})" + "RETURN baeldung, tesla")));
        Neo4jLiveTest.graphDb.execute(("MATCH (company:Company)" + (" WHERE company.name='Baeldung'" + " DELETE company")));
        Result result = Neo4jLiveTest.graphDb.execute(("MATCH (company:Company)" + (" WHERE company.name='Baeldung'" + " RETURN company")));
        Assert.assertFalse(result.hasNext());
    }

    @Test
    public void testBindings() {
        Neo4jLiveTest.graphDb.beginTx();
        Map<String, Object> params = new HashMap<>();
        params.put("name", "baeldung");
        params.put("make", "tesla");
        params.put("model", "modelS");
        Result result = Neo4jLiveTest.graphDb.execute(("CREATE (baeldung:Company {name:$name}) " + ("-[:owns]-> (tesla:Car {make: $make, model: $model})" + "RETURN baeldung, tesla")), params);
        Map<String, Object> firstResult = result.next();
        Assert.assertTrue(firstResult.containsKey("baeldung"));
        Assert.assertTrue(firstResult.containsKey("tesla"));
        Node car = ((Node) (firstResult.get("tesla")));
        Assert.assertEquals(car.getProperty("model"), "modelS");
    }
}

