package com.orientechnologies.orient.object.db;


import com.orientechnologies.orient.core.db.OPartitionedDatabasePool;
import com.orientechnologies.orient.object.db.entity.Person;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 16/12/15.
 */
public class OPersistentEmbeddedMapTest {
    private OPartitionedDatabasePool pool;

    private OObjectDatabaseTx createdDb;

    @Test
    public void embeddedMapShouldContainCorrectValues() {
        Person person = createTestPerson();
        Person retrievedPerson;
        OObjectDatabaseTx db = new OObjectDatabaseTx(pool.acquire());
        try {
            db.save(person);
            retrievedPerson = db.browseClass(Person.class).next();
            retrievedPerson = db.detachAll(retrievedPerson, true);
        } finally {
            db.close();
        }
        Assert.assertEquals(person, retrievedPerson);
    }
}

