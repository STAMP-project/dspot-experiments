package dev.morphia.mapping;


import dev.morphia.TestBase;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class ConcreteClassEmbeddedOverrideTest extends TestBase {
    @Test
    public void test() throws Exception {
        final ConcreteClassEmbeddedOverrideTest.E e1 = new ConcreteClassEmbeddedOverrideTest.E();
        Assert.assertEquals("A", e1.a1.s);
        Assert.assertEquals("A", e1.a2.s);
        getDs().save(e1);
        final ConcreteClassEmbeddedOverrideTest.E e2 = getDs().get(e1);
        Assert.assertEquals("A", e2.a1.s);
        Assert.assertEquals("A", e2.a2.s);
        Assert.assertEquals(ConcreteClassEmbeddedOverrideTest.B.class, e2.a2.getClass());
        Assert.assertEquals(ConcreteClassEmbeddedOverrideTest.A.class, e2.a1.getClass());
    }

    public static class E {
        @Embedded
        private final ConcreteClassEmbeddedOverrideTest.A a1 = new ConcreteClassEmbeddedOverrideTest.A();

        @Embedded(concreteClass = ConcreteClassEmbeddedOverrideTest.B.class)
        private final ConcreteClassEmbeddedOverrideTest.A a2 = new ConcreteClassEmbeddedOverrideTest.A();

        @Id
        private ObjectId id;
    }

    public static class A {
        private String s = "A";

        public String getS() {
            return s;
        }

        public void setS(final String s) {
            this.s = s;
        }
    }

    public static class B extends ConcreteClassEmbeddedOverrideTest.A {
        public B() {
            setS("B");
        }
    }
}

