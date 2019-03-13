package dev.morphia.mapping;


import dev.morphia.TestBase;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import dev.morphia.query.FindOptions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class ClassMappingTest extends TestBase {
    @Test
    public void testClassQueries() {
        ClassMappingTest.E e = new ClassMappingTest.E();
        e.testClass2 = LinkedList.class;
        getDs().save(e);
        Assert.assertNull(getDs().find(ClassMappingTest.E.class).field("testClass2").equal(ArrayList.class).find(new FindOptions().limit(1)).tryNext());
    }

    @Test
    public void testMapping() {
        ClassMappingTest.E e = new ClassMappingTest.E();
        e.testClass = LinkedList.class;
        getDs().save(e);
        e = getDs().get(e);
        Assert.assertEquals(LinkedList.class, e.testClass);
    }

    @Test
    public void testMappingWithoutAnnotation() {
        ClassMappingTest.E e = new ClassMappingTest.E();
        e.testClass2 = LinkedList.class;
        getDs().save(e);
        e = getDs().get(e);
        Assert.assertEquals(LinkedList.class, e.testClass2);
    }

    public static class E {
        @Id
        private ObjectId id;

        @Property
        private Class<? extends Collection> testClass;

        private Class<? extends Collection> testClass2;
    }
}

