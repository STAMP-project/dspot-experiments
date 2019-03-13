package dev.morphia.mapping;


import dev.morphia.Datastore;
import dev.morphia.Key;
import dev.morphia.TestBase;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.query.FindOptions;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class KeyMappingTest extends TestBase {
    @Test
    public void keyMapping() {
        getMorphia().map(KeyMappingTest.User.class, KeyMappingTest.Channel.class);
        insertData();
        final Datastore datastore = getDs();
        KeyMappingTest.User user = datastore.find(KeyMappingTest.User.class).find(new FindOptions().limit(1)).tryNext();
        List<Key<KeyMappingTest.Channel>> followedChannels = user.followedChannels;
        KeyMappingTest.Channel channel = datastore.find(KeyMappingTest.Channel.class).filter("name", "Sport channel").find(new FindOptions().limit(1)).tryNext();
        Key<KeyMappingTest.Channel> key = datastore.getKey(channel);
        Assert.assertTrue(followedChannels.contains(key));
    }

    @Test
    public void testKeyComparisons() {
        final KeyMappingTest.User user = new KeyMappingTest.User("Luke Skywalker");
        getDs().save(user);
        final Key<KeyMappingTest.User> k1 = new Key<KeyMappingTest.User>(KeyMappingTest.User.class, "User", user.id);
        final Key<KeyMappingTest.User> k2 = getDs().getKey(user);
        Assert.assertTrue(k1.equals(k2));
        Assert.assertTrue(k2.equals(k1));
    }

    @Entity(noClassnameStored = true)
    static class User {
        @Id
        private ObjectId id;

        private Key<KeyMappingTest.Channel> favoriteChannels;

        private List<Key<KeyMappingTest.Channel>> followedChannels = new ArrayList<Key<KeyMappingTest.Channel>>();

        private String name;

        User() {
        }

        User(final String name, final Key<KeyMappingTest.Channel> favoriteChannels, final List<Key<KeyMappingTest.Channel>> followedChannels) {
            this.name = name;
            this.favoriteChannels = favoriteChannels;
            this.followedChannels = followedChannels;
        }

        User(final String name) {
            this.name = name;
        }
    }

    @Entity(noClassnameStored = true)
    static class Channel {
        @Id
        private ObjectId id;

        private String name;

        Channel() {
        }

        Channel(final String name) {
            this.name = name;
        }
    }
}

