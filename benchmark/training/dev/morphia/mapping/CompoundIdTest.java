package dev.morphia.mapping;


import dev.morphia.AdvancedDatastore;
import dev.morphia.TestBase;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import dev.morphia.annotations.Version;
import dev.morphia.dao.BasicDAO;
import dev.morphia.query.FindOptions;
import java.io.Serializable;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class CompoundIdTest extends TestBase {
    @Test
    public void testDelete() {
        final CompoundIdTest.CompoundIdEntity entity = new CompoundIdTest.CompoundIdEntity();
        entity.id = new CompoundIdTest.CompoundId("test");
        getDs().save(entity);
        getDs().delete(CompoundIdTest.CompoundIdEntity.class, entity.id);
    }

    @Test
    public void testFetchKey() {
        getDs().save(new CompoundIdTest.ConfigEntry(new CompoundIdTest.ConfigKey("env", "key", "subenv")));
        BasicDAO<CompoundIdTest.ConfigEntry, CompoundIdTest.ConfigKey> innerDAO = new BasicDAO<CompoundIdTest.ConfigEntry, CompoundIdTest.ConfigKey>(CompoundIdTest.ConfigEntry.class, getDs());
        CompoundIdTest.ConfigEntry entry = innerDAO.find().find(new FindOptions().limit(1)).next();
        entry.setValue("something");
        innerDAO.save(entry);
    }

    @Test
    public void testMapping() {
        CompoundIdTest.CompoundIdEntity entity = new CompoundIdTest.CompoundIdEntity();
        entity.id = new CompoundIdTest.CompoundId("test");
        getDs().save(entity);
        entity = getDs().get(entity);
        Assert.assertEquals("test", entity.id.name);
        Assert.assertNotNull(entity.id.id);
    }

    @Test
    public void testOtherDelete() {
        final CompoundIdTest.CompoundIdEntity entity = new CompoundIdTest.CompoundIdEntity();
        entity.id = new CompoundIdTest.CompoundId("test");
        getDs().save(entity);
        ((AdvancedDatastore) (getDs())).delete(getDs().getCollection(CompoundIdTest.CompoundIdEntity.class).getName(), CompoundIdTest.CompoundIdEntity.class, entity.id);
    }

    @Test
    public void testReference() {
        getMorphia().map(CompoundIdTest.CompoundIdEntity.class, CompoundIdTest.CompoundId.class);
        getDs().getCollection(CompoundIdTest.CompoundIdEntity.class).drop();
        final CompoundIdTest.CompoundIdEntity sibling = new CompoundIdTest.CompoundIdEntity();
        sibling.id = new CompoundIdTest.CompoundId("sibling ID");
        getDs().save(sibling);
        final CompoundIdTest.CompoundIdEntity entity = new CompoundIdTest.CompoundIdEntity();
        entity.id = new CompoundIdTest.CompoundId("entity ID");
        entity.e = "some value";
        entity.sibling = sibling;
        getDs().save(entity);
        final CompoundIdTest.CompoundIdEntity loaded = getDs().get(entity);
        Assert.assertEquals(entity, loaded);
    }

    @Embedded
    private static class CompoundId implements Serializable {
        private final ObjectId id = new ObjectId();

        private String name;

        CompoundId() {
        }

        CompoundId(final String n) {
            name = n;
        }

        @Override
        public int hashCode() {
            int result = id.hashCode();
            result = (31 * result) + (name.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof CompoundIdTest.CompoundId)) {
                return false;
            }
            final CompoundIdTest.CompoundId other = ((CompoundIdTest.CompoundId) (obj));
            return (other.id.equals(id)) && (other.name.equals(name));
        }
    }

    private static class CompoundIdEntity {
        @Id
        private CompoundIdTest.CompoundId id;

        private String e;

        @Reference
        private CompoundIdTest.CompoundIdEntity sibling;

        @Override
        public boolean equals(final Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            final CompoundIdTest.CompoundIdEntity that = ((CompoundIdTest.CompoundIdEntity) (o));
            if (!(id.equals(that.id))) {
                return false;
            }
            if ((e) != null ? !(e.equals(that.e)) : (that.e) != null) {
                return false;
            }
            return !((sibling) != null ? !(sibling.equals(that.sibling)) : (that.sibling) != null);
        }

        @Override
        public int hashCode() {
            int result = id.hashCode();
            result = (31 * result) + ((e) != null ? e.hashCode() : 0);
            result = (31 * result) + ((sibling) != null ? sibling.hashCode() : 0);
            return result;
        }
    }

    public static class ConfigKey {
        private String env;

        private String subenv;

        private String key;

        public ConfigKey() {
        }

        public ConfigKey(final String env, final String key, final String subenv) {
            this.env = env;
            this.key = key;
            this.subenv = subenv;
        }

        @Override
        public boolean equals(final Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            final CompoundIdTest.ConfigKey configKey = ((CompoundIdTest.ConfigKey) (o));
            if (!(env.equals(configKey.env))) {
                return false;
            }
            if (!(subenv.equals(configKey.subenv))) {
                return false;
            }
            return key.equals(configKey.key);
        }

        @Override
        public int hashCode() {
            int result = env.hashCode();
            result = (31 * result) + (subenv.hashCode());
            result = (31 * result) + (key.hashCode());
            return result;
        }
    }

    @Entity(noClassnameStored = true)
    public static class ConfigEntry {
        @Id
        private CompoundIdTest.ConfigKey key;

        private String value;

        @Version
        private long version;

        private String lastModifiedUser;

        private long lastModifiedMillis;

        public ConfigEntry() {
        }

        public ConfigEntry(final CompoundIdTest.ConfigKey key) {
            this.key = key;
        }

        public CompoundIdTest.ConfigKey getKey() {
            return key;
        }

        public void setKey(final CompoundIdTest.ConfigKey key) {
            this.key = key;
        }

        public long getLastModifiedMillis() {
            return lastModifiedMillis;
        }

        public void setLastModifiedMillis(final long lastModifiedMillis) {
            this.lastModifiedMillis = lastModifiedMillis;
        }

        public String getLastModifiedUser() {
            return lastModifiedUser;
        }

        public void setLastModifiedUser(final String lastModifiedUser) {
            this.lastModifiedUser = lastModifiedUser;
        }

        public String getValue() {
            return value;
        }

        public void setValue(final String value) {
            this.value = value;
        }

        public long getVersion() {
            return version;
        }

        public void setVersion(final long version) {
            this.version = version;
        }
    }
}

