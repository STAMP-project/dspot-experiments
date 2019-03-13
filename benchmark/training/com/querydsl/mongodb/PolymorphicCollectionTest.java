package com.querydsl.mongodb;


import QFish.fish.breed;
import QFood.food;
import QFood.food.name;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.querydsl.core.testutil.MongoDB;
import com.querydsl.mongodb.domain.Chips;
import com.querydsl.mongodb.domain.Fish;
import com.querydsl.mongodb.domain.Food;
import java.net.UnknownHostException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;


@Category(MongoDB.class)
public class PolymorphicCollectionTest {
    private final Morphia morphia;

    private final Datastore ds;

    private final Fish f1 = new Fish("f1");

    private final Fish f2 = new Fish("f2");

    private final Chips c1 = new Chips("c1");

    public PolymorphicCollectionTest() throws MongoException, UnknownHostException {
        final MongoClient mongo = new MongoClient();
        morphia = new Morphia().map(Food.class);
        ds = morphia.createDatastore(mongo, "testdb");
    }

    @Test
    public void basicCount() {
        Assert.assertEquals(where().fetchCount(), 3);
    }

    @Test
    public void countFishFromName() {
        Assert.assertEquals(where(name.eq("f1")).fetchCount(), 1);
    }

    @Test
    public void countFishFromNameAndBreed() {
        Assert.assertEquals(where(name.eq("f1").and(breed.eq("unknown"))).fetchCount(), 1);
    }

    @Test
    public void countFishFromNameAndBreedWithCast() {
        Assert.assertEquals(where(name.eq("f1").and(food.as(QFish.class).breed.eq("unknown"))).fetchCount(), 1);
    }

    @Test
    public void countFishes() {
        Assert.assertEquals(where(isFish()).fetchCount(), 2);
    }
}

