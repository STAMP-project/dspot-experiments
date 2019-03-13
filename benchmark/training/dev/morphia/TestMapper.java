package dev.morphia;


import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PostLoad;
import dev.morphia.annotations.Property;
import dev.morphia.annotations.Reference;
import dev.morphia.mapping.EmbeddedMappingTest;
import dev.morphia.mapping.MappedClass;
import dev.morphia.mapping.Mapper;
import dev.morphia.mapping.lazy.LazyFeatureDependencies;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests mapper functions; this is tied to some of the internals.
 *
 * @author scotthernandez
 */
public class TestMapper extends TestBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestMapper.class);

    @Test
    public void serializableId() throws Exception {
        final TestMapper.CustomId cId = new TestMapper.CustomId();
        cId.id = new ObjectId();
        cId.type = "banker";
        final TestMapper.UsesCustomIdObject object = new TestMapper.UsesCustomIdObject();
        object.id = cId;
        object.text = "hllo";
        getDs().save(object);
    }

    @Test
    public void singleLookup() throws Exception {
        TestMapper.A.loadCount = 0;
        final TestMapper.A a = new TestMapper.A();
        TestMapper.HoldsMultipleA holder = new TestMapper.HoldsMultipleA();
        holder.a1 = a;
        holder.a2 = a;
        getDs().save(Arrays.asList(a, holder));
        holder = getDs().get(TestMapper.HoldsMultipleA.class, holder.id);
        Assert.assertEquals(1, TestMapper.A.loadCount);
        Assert.assertTrue(((holder.a1) == (holder.a2)));
    }

    @Test
    public void singleProxy() throws Exception {
        // TODO us: exclusion does not work properly with maven + junit4
        if (!(LazyFeatureDependencies.testDependencyFullFilled())) {
            return;
        }
        TestMapper.A.loadCount = 0;
        final TestMapper.A a = new TestMapper.A();
        TestMapper.HoldsMultipleALazily holder = new TestMapper.HoldsMultipleALazily();
        holder.a1 = a;
        holder.a2 = a;
        holder.a3 = a;
        getDs().save(Arrays.asList(a, holder));
        Assert.assertEquals(0, TestMapper.A.loadCount);
        holder = getDs().get(TestMapper.HoldsMultipleALazily.class, holder.id);
        Assert.assertNotNull(holder.a2);
        Assert.assertEquals(1, TestMapper.A.loadCount);
        Assert.assertFalse(((holder.a1) == (holder.a2)));
        // FIXME currently not guaranteed:
        // Assert.assertTrue(holder.a1 == holder.a3);
        // A.loadCount=0;
        // Assert.assertEquals(holder.a1.getId(), holder.a2.getId());
    }

    @Test
    public void subTypes() {
        getMorphia().map(EmbeddedMappingTest.NestedImpl.class, EmbeddedMappingTest.AnotherNested.class);
        Mapper mapper = getMorphia().getMapper();
        List<MappedClass> subTypes = mapper.getSubTypes(mapper.getMappedClass(EmbeddedMappingTest.Nested.class));
        Assert.assertTrue(subTypes.contains(mapper.getMappedClass(EmbeddedMappingTest.NestedImpl.class)));
        Assert.assertTrue(subTypes.contains(mapper.getMappedClass(EmbeddedMappingTest.AnotherNested.class)));
    }

    public static class A {
        private static int loadCount;

        @Id
        private ObjectId id;

        @PostLoad
        protected void postConstruct() {
            if ((TestMapper.A.loadCount) > 1) {
                throw new RuntimeException();
            }
            (TestMapper.A.loadCount)++;
        }

        String getId() {
            return id.toString();
        }
    }

    @Entity("holders")
    public static class HoldsMultipleA {
        @Id
        private ObjectId id;

        @Reference
        private TestMapper.A a1;

        @Reference
        private TestMapper.A a2;
    }

    @Entity("holders")
    public static class HoldsMultipleALazily {
        @Id
        private ObjectId id;

        @Reference(lazy = true)
        private TestMapper.A a1;

        @Reference
        private TestMapper.A a2;

        @Reference(lazy = true)
        private TestMapper.A a3;
    }

    public static class CustomId implements Serializable {
        @Property("v")
        private ObjectId id;

        @Property("t")
        private String type;

        public ObjectId getId() {
            return id;
        }

        public void setId(final ObjectId id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(final String type) {
            this.type = type;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + ((id) == null ? 0 : id.hashCode());
            result = (prime * result) + ((type) == null ? 0 : type.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if ((this) == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof TestMapper.CustomId)) {
                return false;
            }
            final TestMapper.CustomId other = ((TestMapper.CustomId) (obj));
            if ((id) == null) {
                if ((other.id) != null) {
                    return false;
                }
            } else
                if (!(id.equals(other.id))) {
                    return false;
                }

            if ((type) == null) {
                if ((other.type) != null) {
                    return false;
                }
            } else
                if (!(type.equals(other.type))) {
                    return false;
                }

            return true;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("CustomId [");
            if ((id) != null) {
                builder.append("id=").append(id).append(", ");
            }
            if ((type) != null) {
                builder.append("type=").append(type);
            }
            builder.append("]");
            return builder.toString();
        }
    }

    public static class UsesCustomIdObject {
        @Id
        private TestMapper.CustomId id;

        private String text;

        public TestMapper.CustomId getId() {
            return id;
        }

        public void setId(final TestMapper.CustomId id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(final String text) {
            this.text = text;
        }
    }

    private static class Customer {
        private int id;

        private String name;

        public int getId() {
            return id;
        }

        public void setId(final int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }
    }

    private static class Container {
        @Embedded
        private List<TestMapper.Customer> values;

        @Id
        private ObjectId id;

        public List<TestMapper.Customer> getValues() {
            return values;
        }

        public void setValues(final List<TestMapper.Customer> values) {
            this.values = values;
        }

        public ObjectId getId() {
            return id;
        }

        public void setId(final ObjectId id) {
            this.id = id;
        }
    }
}

