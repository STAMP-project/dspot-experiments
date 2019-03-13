package dev.morphia;


import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class TestInheritance extends TestBase {
    @Test
    public void testSavingAndLoadingAClassWithDeepInheritance() {
        // given
        final TestInheritance.Child jimmy = new TestInheritance.Child();
        jimmy.setName("jimmy");
        getDs().save(jimmy);
        // when
        final TestInheritance.Child loaded = getDs().get(TestInheritance.Child.class, jimmy.getId());
        // then
        Assert.assertNotNull(loaded);
        Assert.assertEquals(jimmy.getName(), loaded.getName());
    }

    @Entity
    public static class Child extends TestInheritance.Father {}

    @Entity
    public static class Father extends TestInheritance.GrandFather {}

    @Entity
    public static class GrandFather {
        @Id
        private ObjectId id;

        private String name;

        public ObjectId getId() {
            return id;
        }

        public void setId(final ObjectId id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }
    }
}

