package dev.morphia.converters;


import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import dev.morphia.TestBase;
import dev.morphia.annotations.Converters;
import dev.morphia.mapping.MappedField;
import dev.morphia.query.FindOptions;
import dev.morphia.testutil.TestEntity;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Uwe Schaefer
 */
public class CustomConverterInEmbedTest extends TestBase {
    @Test
    public void testConversionInList() {
        final CustomConverterInEmbedTest.FooConverter fc = new CustomConverterInEmbedTest.FooConverter();
        getMorphia().getMapper().getConverters().addConverter(fc);
        final CustomConverterInEmbedTest.E1 e = new CustomConverterInEmbedTest.E1();
        e.foo.add(new CustomConverterInEmbedTest.Foo("bar"));
        getDs().save(e);
        Assert.assertTrue(fc.didConversion());
    }

    @Test
    public void testConversionInMap() {
        final CustomConverterInEmbedTest.FooConverter fc = new CustomConverterInEmbedTest.FooConverter();
        getMorphia().getMapper().getConverters().addConverter(fc);
        CustomConverterInEmbedTest.E2 e = new CustomConverterInEmbedTest.E2();
        e.foo.put("bar", new CustomConverterInEmbedTest.Foo("bar"));
        getDs().save(e);
        Assert.assertTrue(fc.didConversion());
        e = getDs().find(CustomConverterInEmbedTest.E2.class).find(new FindOptions().limit(1)).next();
        Assert.assertNotNull(e.foo);
        Assert.assertFalse(e.foo.isEmpty());
        Assert.assertTrue(e.foo.containsKey("bar"));
        Assert.assertEquals("bar", e.foo.get("bar").string);
    }

    @Test
    public void testEmbeddedComplexArrayType() {
        CustomConverterInEmbedTest.ArrayBar bar = new CustomConverterInEmbedTest.ArrayBar();
        bar.foo = new CustomConverterInEmbedTest.ArrayFoo("firstValue", "secondValue");
        getDs().save(bar);
        CustomConverterInEmbedTest.ArrayBar fromDb = getDs().get(CustomConverterInEmbedTest.ArrayBar.class, bar.getId());
        Assert.assertThat("bar is not null", fromDb, CoreMatchers.notNullValue());
        Assert.assertThat("foo is not null", fromDb.foo, CoreMatchers.notNullValue());
        Assert.assertThat("foo has the correct first value", fromDb.foo.first(), CoreMatchers.equalTo("firstValue"));
        Assert.assertThat("foo has the correct second value", fromDb.foo.second(), CoreMatchers.equalTo("secondValue"));
    }

    @Test
    public void testEmbeddedComplexType() {
        CustomConverterInEmbedTest.ComplexBar bar = new CustomConverterInEmbedTest.ComplexBar();
        bar.foo = new CustomConverterInEmbedTest.ComplexFoo("firstValue", "secondValue");
        getDs().save(bar);
        CustomConverterInEmbedTest.ComplexBar fromDb = getDs().get(CustomConverterInEmbedTest.ComplexBar.class, bar.getId());
        Assert.assertThat("bar is not null", fromDb, CoreMatchers.notNullValue());
        Assert.assertThat("foo is not null", fromDb.foo, CoreMatchers.notNullValue());
        Assert.assertThat("foo has the correct first value", fromDb.foo.first(), CoreMatchers.equalTo("firstValue"));
        Assert.assertThat("foo has the correct second value", fromDb.foo.second(), CoreMatchers.equalTo("secondValue"));
    }

    // FIXME issue 101
    public static class E1 extends TestEntity {
        private final List<CustomConverterInEmbedTest.Foo> foo = new LinkedList<CustomConverterInEmbedTest.Foo>();
    }

    public static class E2 extends TestEntity {
        private final Map<String, CustomConverterInEmbedTest.Foo> foo = new HashMap<String, CustomConverterInEmbedTest.Foo>();
    }

    // unknown type to convert
    public static class Foo {
        private String string;

        Foo() {
        }

        public Foo(final String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }
    }

    public static class FooConverter extends TypeConverter implements SimpleValueConverter {
        private boolean done;

        public FooConverter() {
            super(CustomConverterInEmbedTest.Foo.class);
        }

        @Override
        public Object decode(final Class targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
            return new CustomConverterInEmbedTest.Foo(((String) (fromDBObject)));
        }

        public boolean didConversion() {
            return done;
        }

        @Override
        public Object encode(final Object value, final MappedField optionalExtraInfo) {
            done = true;
            return value.toString();
        }
    }

    /**
     * A type that contains a complex custom type, represented as an object.
     *
     * @author Christian Trimble
     */
    @Converters(CustomConverterInEmbedTest.ComplexFooConverter.class)
    public static class ComplexBar extends TestEntity {
        private CustomConverterInEmbedTest.ComplexFoo foo;
    }

    /**
     * A type that contains a complex custom type, represented as an array.
     *
     * @author Christian Trimble
     */
    @Converters(CustomConverterInEmbedTest.ComplexArrayFooConverter.class)
    public static class ArrayBar extends TestEntity {
        private CustomConverterInEmbedTest.ArrayFoo foo;
    }

    /**
     * A complex embedded type, represented as an object
     *
     * @author Christian Trimble
     */
    public static class ComplexFoo {
        private String first;

        private String second;

        ComplexFoo() {
        }

        public ComplexFoo(final String first, final String second) {
            this.first = first;
            this.second = second;
        }

        String first() {
            return first;
        }

        String second() {
            return second;
        }
    }

    /**
     * A complex embedded type, represented as an array
     *
     * @author Christian Trimble
     */
    public static class ArrayFoo {
        private String first;

        private String second;

        ArrayFoo() {
        }

        public ArrayFoo(final String first, final String second) {
            this.first = first;
            this.second = second;
        }

        String first() {
            return first;
        }

        String second() {
            return second;
        }
    }

    /**
     * A converter that does not implement SimpleValueConverter and converts ComplexFoo into an object type.
     *
     * @author Christian Trimble
     */
    public static class ComplexFooConverter extends TypeConverter {
        public ComplexFooConverter() {
            super(CustomConverterInEmbedTest.ComplexFoo.class);
        }

        @Override
        public Object decode(final Class targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
            DBObject dbObject = ((DBObject) (fromDBObject));
            return new CustomConverterInEmbedTest.ComplexFoo(((String) (dbObject.get("first"))), ((String) (dbObject.get("second"))));
        }

        @Override
        public Object encode(final Object value, final MappedField optionalExtraInfo) {
            CustomConverterInEmbedTest.ComplexFoo complex = ((CustomConverterInEmbedTest.ComplexFoo) (value));
            BasicDBObject dbObject = new BasicDBObject();
            dbObject.put("first", complex.first());
            dbObject.put("second", complex.second());
            return dbObject;
        }
    }

    /**
     * A converter that does not implement SimpleValueConverter and converts ArrayFoo into an array type.
     *
     * @author Christian Trimble
     */
    public static class ComplexArrayFooConverter extends TypeConverter {
        public ComplexArrayFooConverter() {
            super(CustomConverterInEmbedTest.ArrayFoo.class);
        }

        @Override
        public Object decode(final Class targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
            BasicDBList dbObject = ((BasicDBList) (fromDBObject));
            return new CustomConverterInEmbedTest.ArrayFoo(((String) (dbObject.get(1))), ((String) (dbObject.get(2))));
        }

        @Override
        public Object encode(final Object value, final MappedField optionalExtraInfo) {
            CustomConverterInEmbedTest.ArrayFoo complex = ((CustomConverterInEmbedTest.ArrayFoo) (value));
            BasicDBList dbObject = new BasicDBList();
            dbObject.put(1, complex.first());
            dbObject.put(2, complex.second());
            return dbObject;
        }
    }
}

