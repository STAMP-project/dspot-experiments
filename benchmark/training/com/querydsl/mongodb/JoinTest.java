package com.querydsl.mongodb;


import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.querydsl.core.testutil.MongoDB;
import com.querydsl.mongodb.domain.Item;
import com.querydsl.mongodb.domain.QUser;
import com.querydsl.mongodb.domain.User;
import java.net.UnknownHostException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;


@Category(MongoDB.class)
public class JoinTest {
    private final MongoClient mongo;

    private final Morphia morphia;

    private final Datastore ds;

    private final String dbname = "testdb";

    private final QUser user = QUser.user;

    private final QUser friend = new QUser("friend");

    private final QUser friend2 = new QUser("friend2");

    private final QUser enemy = new QUser("enemy");

    public JoinTest() throws MongoException, UnknownHostException {
        mongo = new MongoClient();
        morphia = new Morphia().map(User.class).map(Item.class);
        ds = morphia.createDatastore(mongo, dbname);
    }

    @Test
    public void count() {
        Assert.assertEquals(1, where().join(user.friend(), friend).on(friend.firstName.eq("Max")).fetchCount());
        Assert.assertEquals(1, where(user.firstName.eq("Jane")).join(user.friend(), friend).on(friend.firstName.eq("Max")).fetchCount());
        Assert.assertEquals(0, where(user.firstName.eq("Mary")).join(user.friend(), friend).on(friend.firstName.eq("Max")).fetchCount());
        Assert.assertEquals(0, where(user.firstName.eq("Jane")).join(user.friend(), friend).on(friend.firstName.eq("Jack")).fetchCount());
    }

    @Test
    public void count_collection() {
        Assert.assertEquals(1, where().join(user.friends, friend).on(friend.firstName.eq("Mary")).fetchCount());
        Assert.assertEquals(1, where().join(user.friends, friend).on(friend.firstName.eq("Ann")).fetchCount());
        Assert.assertEquals(1, where().join(user.friends, friend).on(friend.firstName.eq("Ann").or(friend.firstName.eq("Mary"))).fetchCount());
        Assert.assertEquals(1, where(user.firstName.eq("Bart")).join(user.friends, friend).on(friend.firstName.eq("Mary")).fetchCount());
        Assert.assertEquals(0, where().join(user.friends, friend).on(friend.firstName.eq("Max")).fetchCount());
    }

    @Test
    public void exists() {
        Assert.assertTrue(((where().join(user.friend(), friend).on(friend.firstName.eq("Max")).fetchCount()) > 0));
        Assert.assertTrue(((where(user.firstName.eq("Jane")).join(user.friend(), friend).on(friend.firstName.eq("Max")).fetchCount()) > 0));
        Assert.assertFalse(((where(user.firstName.eq("Mary")).join(user.friend(), friend).on(friend.firstName.eq("Max")).fetchCount()) > 0));
        Assert.assertFalse(((where(user.firstName.eq("Jane")).join(user.friend(), friend).on(friend.firstName.eq("Jack")).fetchCount()) > 0));
    }

    @Test
    public void exists_collection() {
        Assert.assertTrue(((where().join(user.friends, friend).on(friend.firstName.eq("Mary")).fetchCount()) > 0));
        Assert.assertTrue(((where(user.firstName.eq("Bart")).join(user.friends, friend).on(friend.firstName.eq("Mary")).fetchCount()) > 0));
    }

    @Test
    public void list() {
        Assert.assertEquals(1, where().join(user.friend(), friend).on(friend.firstName.eq("Max")).fetch().size());
        Assert.assertEquals(1, where(user.firstName.eq("Jane")).join(user.friend(), friend).on(friend.firstName.eq("Max")).fetch().size());
        Assert.assertEquals(0, where(user.firstName.eq("Mary")).join(user.friend(), friend).on(friend.firstName.eq("Max")).fetch().size());
        Assert.assertEquals(0, where(user.firstName.eq("Jane")).join(user.friend(), friend).on(friend.firstName.eq("Jack")).fetch().size());
    }

    @Test
    public void single() {
        Assert.assertEquals("Jane", where().join(user.friend(), friend).on(friend.firstName.eq("Max")).fetchFirst().getFirstName());
        Assert.assertEquals("Jane", where(user.firstName.eq("Jane")).join(user.friend(), friend).on(friend.firstName.eq("Max")).fetchFirst().getFirstName());
        Assert.assertNull(where(user.firstName.eq("Mary")).join(user.friend(), friend).on(friend.firstName.eq("Max")).fetchFirst());
        Assert.assertNull(where(user.firstName.eq("Jane")).join(user.friend(), friend).on(friend.firstName.eq("Jack")).fetchFirst());
    }

    @Test
    public void single_collection() {
        Assert.assertEquals("Bart", where().join(user.friends, friend).on(friend.firstName.eq("Mary")).fetchFirst().getFirstName());
    }

    @Test
    public void double1() {
        Assert.assertEquals("Mike", where().join(user.friend(), friend).on(friend.firstName.isNotNull()).join(user.enemy(), enemy).on(enemy.firstName.isNotNull()).fetchFirst().getFirstName());
    }

    @Test
    public void double2() {
        Assert.assertEquals("Mike", where().join(user.friend(), friend).on(friend.firstName.eq("Mary")).join(user.enemy(), enemy).on(enemy.firstName.eq("Ann")).fetchFirst().getFirstName());
    }

    @Test
    public void deep() {
        // Mike -> Mary -> Jane
        Assert.assertEquals("Mike", where().join(user.friend(), friend).on(friend.firstName.isNotNull()).join(friend.friend(), friend2).on(friend2.firstName.eq("Jane")).fetchFirst().getFirstName());
    }
}

