package dev.morphia.ext;


import com.mongodb.DBObject;
import dev.morphia.TestBase;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Id;
import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


/**
 * Example converter which stores the enum value instead of string (name)
 *
 * @author scotthernandez
 */
public class EnumValueConverterTest extends TestBase {
    @Test
    public void testEnum() {
        final EnumValueConverterTest.EnumEntity ee = new EnumValueConverterTest.EnumEntity();
        getDs().save(ee);
        final DBObject dbObj = getDs().getCollection(EnumValueConverterTest.EnumEntity.class).findOne();
        Assert.assertEquals(1, dbObj.get("val"));
    }

    private enum AEnum {

        One,
        Two;}

    private static class AEnumConverter extends TypeConverter implements SimpleValueConverter {
        AEnumConverter() {
            super(EnumValueConverterTest.AEnum.class);
        }

        @Override
        public Object decode(final Class targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
            if (fromDBObject == null) {
                return null;
            }
            return EnumValueConverterTest.AEnum.values()[((Integer) (fromDBObject))];
        }

        @Override
        public Object encode(final Object value, final MappedField optionalExtraInfo) {
            if (value == null) {
                return null;
            }
            return ((Enum) (value)).ordinal();
        }
    }

    @Converters(EnumValueConverterTest.AEnumConverter.class)
    private static class EnumEntity {
        @Id
        private ObjectId id = new ObjectId();

        private EnumValueConverterTest.AEnum val = EnumValueConverterTest.AEnum.Two;
    }
}

