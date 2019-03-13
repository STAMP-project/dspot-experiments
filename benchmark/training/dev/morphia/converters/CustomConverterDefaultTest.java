package dev.morphia.converters;


import dev.morphia.TestBase;
import dev.morphia.annotations.Property;
import dev.morphia.mapping.MappedField;
import dev.morphia.query.FindOptions;
import dev.morphia.testutil.TestEntity;
import org.junit.Assert;
import org.junit.Test;


public class CustomConverterDefaultTest extends TestBase {
    @Test
    public void testConversion() {
        final CustomConverterDefaultTest.FooConverter fc = new CustomConverterDefaultTest.FooConverter();
        getMorphia().getMapper().getConverters().addConverter(fc);
        getMorphia().map(CustomConverterDefaultTest.E.class);
        CustomConverterDefaultTest.E e = new CustomConverterDefaultTest.E();
        e.foo = new CustomConverterDefaultTest.Foo("test");
        getDs().save(e);
        Assert.assertTrue(fc.didConversion());
        e = getDs().find(CustomConverterDefaultTest.E.class).find(new FindOptions().limit(1)).tryNext();
        Assert.assertNotNull(e.foo);
        Assert.assertEquals("test", e.foo.string);
    }

    @Test
    public void testRemoveConverter() {
        Converters converters = getMorphia().getMapper().getConverters();
        try {
            Assert.assertTrue(converters.isRegistered(DoubleConverter.class));
            converters.removeConverter(new DoubleConverter());
            Assert.assertFalse(converters.isRegistered(DoubleConverter.class));
        } finally {
            if (!(converters.isRegistered(DoubleConverter.class))) {
                converters.addConverter(DoubleConverter.class);
            }
        }
    }

    public static class E extends TestEntity {
        @Property
        private CustomConverterDefaultTest.Foo foo;
    }

    // unknown type to convert
    public static class Foo {
        private final String string;

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
            super(CustomConverterDefaultTest.Foo.class);
        }

        @Override
        public Object decode(final Class targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
            return new CustomConverterDefaultTest.Foo(((String) (fromDBObject)));
        }

        @Override
        public Object encode(final Object value, final MappedField optionalExtraInfo) {
            done = true;
            return value.toString();
        }

        public boolean didConversion() {
            return done;
        }
    }
}

