package com.baeldung.hibernate.ogm;


import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.junit.Test;


public class EditorUnitTest {
    /* @Test
    public void givenMongoDB_WhenEntitiesCreated_thenCanBeRetrieved() throws Exception {
    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("ogm-mongodb");
    Editor editor = generateTestData();
    persistTestData(entityManagerFactory, editor);
    loadAndVerifyTestData(entityManagerFactory, editor);
    }
     */
    @Test
    public void givenNeo4j_WhenEntitiesCreated_thenCanBeRetrieved() throws Exception {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("ogm-neo4j");
        Editor editor = generateTestData();
        persistTestData(entityManagerFactory, editor);
        loadAndVerifyTestData(entityManagerFactory, editor);
    }
}

