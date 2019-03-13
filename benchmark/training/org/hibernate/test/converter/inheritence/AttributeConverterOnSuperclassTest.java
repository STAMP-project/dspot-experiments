package org.hibernate.test.converter.inheritence;


import java.util.List;
import javax.persistence.AttributeConverter;
import org.hibernate.cfg.AttributeConverterDefinition;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the ability to interpret and understand AttributeConverter impls when the base class does not
 * explicitly implement AttributeConverter but implements it via an interface or superclass. This also
 * involves resolving any TypeVariables to Class or ParameterizedType.
 *
 * @author Svein Baardsen
 */
@TestForIssue(jiraKey = "HHH-8854")
public class AttributeConverterOnSuperclassTest extends BaseUnitTestCase {
    public static class StringIntegerAttributeConverter implements AttributeConverter<String, Integer> {
        @Override
        public Integer convertToDatabaseColumn(String attribute) {
            return Integer.valueOf(attribute);
        }

        @Override
        public String convertToEntityAttribute(Integer dbData) {
            return String.valueOf(dbData);
        }
    }

    public static class StringIntegerConverterSubclass extends AttributeConverterOnSuperclassTest.StringIntegerAttributeConverter {}

    @Test
    public void testAttributeConverterOnSuperclass() {
        AttributeConverterDefinition def = AttributeConverterDefinition.from(AttributeConverterOnSuperclassTest.StringIntegerConverterSubclass.class);
        Assert.assertEquals(String.class, def.getEntityAttributeType());
    }

    public interface StringLongAttributeConverter extends AttributeConverter<String, Long> {}

    public static class StringLongAttributeConverterImpl implements AttributeConverterOnSuperclassTest.StringLongAttributeConverter {
        @Override
        public Long convertToDatabaseColumn(String attribute) {
            return Long.valueOf(attribute);
        }

        @Override
        public String convertToEntityAttribute(Long dbData) {
            return String.valueOf(dbData);
        }
    }

    @Test
    public void testAttributeConverterOnInterface() {
        AttributeConverterDefinition def = AttributeConverterDefinition.from(AttributeConverterOnSuperclassTest.StringLongAttributeConverterImpl.class);
        Assert.assertEquals(String.class, def.getEntityAttributeType());
    }

    public static class NoopAttributeConverter<T> implements AttributeConverter<T, T> {
        @Override
        public T convertToDatabaseColumn(T attribute) {
            return attribute;
        }

        @Override
        public T convertToEntityAttribute(T dbData) {
            return dbData;
        }
    }

    public static class StringNoopAttributeConverter extends AttributeConverterOnSuperclassTest.NoopAttributeConverter<String> {}

    @Test
    public void testTypeVariableAttributeConverterTypeArguments() {
        AttributeConverterDefinition def = AttributeConverterDefinition.from(AttributeConverterOnSuperclassTest.StringNoopAttributeConverter.class);
        Assert.assertEquals(String.class, def.getEntityAttributeType());
    }

    public static class ListNoopAttributeConverter<T> extends AttributeConverterOnSuperclassTest.NoopAttributeConverter<List<T>> {}

    public static class StringListNoopAttributeConverter extends AttributeConverterOnSuperclassTest.ListNoopAttributeConverter<String> {}

    @Test
    public void testParameterizedTypeWithTypeVariableAttributeConverterTypeArguments() {
        AttributeConverterDefinition def = AttributeConverterDefinition.from(AttributeConverterOnSuperclassTest.StringListNoopAttributeConverter.class);
        Assert.assertEquals(List.class, def.getEntityAttributeType());
    }
}

