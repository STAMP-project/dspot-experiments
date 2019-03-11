/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.converter.literal;


import javax.persistence.AttributeConverter;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.QueryException;
import org.hibernate.Session;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Janario Oliveira
 */
public class QueryLiteralTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testIntegerWrapper() {
        QueryLiteralTest.EntityConverter entity = new QueryLiteralTest.EntityConverter();
        entity.setIntegerWrapper(new QueryLiteralTest.IntegerWrapper(10));
        save(entity);
        entity = find(entity.getId(), "e.integerWrapper=10");
        Assert.assertNotNull(entity);
        Assert.assertEquals(10, entity.getIntegerWrapper().getValue());
    }

    @Test
    public void testIntegerWrapperThrowsException() {
        QueryLiteralTest.EntityConverter entity = new QueryLiteralTest.EntityConverter();
        entity.setIntegerWrapper(new QueryLiteralTest.IntegerWrapper(10));
        save(entity);
        try {
            find(entity.getId(), "e.integerWrapper='10'");
            Assert.fail("Should throw QueryException!");
        } catch (IllegalArgumentException e) {
            ExtraAssertions.assertTyping(QueryException.class, e.getCause());
            Assert.assertTrue(e.getMessage().contains("AttributeConverter domain-model attribute type [org.hibernate.test.converter.literal.QueryLiteralTest$IntegerWrapper] and JDBC type [java.lang.Integer] did not match query literal type [java.lang.String]"));
        }
    }

    @Test
    public void testStringWrapper() {
        QueryLiteralTest.EntityConverter entity = new QueryLiteralTest.EntityConverter();
        entity.setStringWrapper(new QueryLiteralTest.StringWrapper("TEN"));
        save(entity);
        entity = find(entity.getId(), "e.stringWrapper='TEN'");
        Assert.assertNotNull(entity);
        Assert.assertEquals("TEN", entity.getStringWrapper().getValue());
    }

    @Test
    public void testSameTypeConverter() {
        QueryLiteralTest.EntityConverter entity = new QueryLiteralTest.EntityConverter();
        entity.setSameTypeConverter("HUNDRED");
        save(entity);
        entity = find(entity.getId(), "e.sameTypeConverter='HUNDRED'");
        Assert.assertNotNull(entity);
        Assert.assertEquals("HUNDRED", entity.getSameTypeConverter());
        Session session = openSession();
        String value = ((String) (session.createSQLQuery("select e.same_type_converter from entity_converter e where e.id=:id").setParameter("id", entity.getId()).uniqueResult()));
        Assert.assertEquals("VALUE_HUNDRED", value);
        session.close();
    }

    @Test
    public void testEnumOrdinal() {
        QueryLiteralTest.EntityConverter entity = new QueryLiteralTest.EntityConverter();
        entity.setLetterOrdinal(QueryLiteralTest.Letter.B);
        save(entity);
        entity = find(entity.getId(), ("e.letterOrdinal=" + (QueryLiteralTest.Letter.B.ordinal())));
        Assert.assertNotNull(entity);
        Assert.assertEquals(QueryLiteralTest.Letter.B, entity.getLetterOrdinal());
    }

    @Test
    public void testEnumString() {
        QueryLiteralTest.EntityConverter entity = new QueryLiteralTest.EntityConverter();
        entity.setLetterString(QueryLiteralTest.Letter.C);
        save(entity);
        entity = find(entity.getId(), (("e.letterString='" + (QueryLiteralTest.Letter.C.name())) + "'"));
        Assert.assertNotNull(entity);
        Assert.assertEquals(QueryLiteralTest.Letter.C, entity.getLetterString());
    }

    @Test
    public void testNumberImplicit() {
        QueryLiteralTest.EntityConverter entity = new QueryLiteralTest.EntityConverter();
        entity.setNumbersImplicit(QueryLiteralTest.Numbers.THREE);
        save(entity);
        entity = find(entity.getId(), ("e.numbersImplicit=" + ((QueryLiteralTest.Numbers.THREE.ordinal()) + 1)));
        Assert.assertNotNull(entity);
        Assert.assertEquals(QueryLiteralTest.Numbers.THREE, entity.getNumbersImplicit());
    }

    @Test
    public void testNumberImplicitOverrided() {
        QueryLiteralTest.EntityConverter entity = new QueryLiteralTest.EntityConverter();
        entity.setNumbersImplicitOverrided(QueryLiteralTest.Numbers.TWO);
        save(entity);
        entity = find(entity.getId(), (("e.numbersImplicitOverrided='" + ((QueryLiteralTest.Numbers.TWO.ordinal()) + 1)) + "'"));
        Assert.assertNotNull(entity);
        Assert.assertEquals(QueryLiteralTest.Numbers.TWO, entity.getNumbersImplicitOverrided());
    }

    public enum Letter {

        A,
        B,
        C;}

    public enum Numbers {

        ONE,
        TWO,
        THREE;}

    @Converter(autoApply = true)
    public static class NumberIntegerConverter implements AttributeConverter<QueryLiteralTest.Numbers, Integer> {
        @Override
        public Integer convertToDatabaseColumn(QueryLiteralTest.Numbers attribute) {
            return attribute == null ? null : (attribute.ordinal()) + 1;
        }

        @Override
        public QueryLiteralTest.Numbers convertToEntityAttribute(Integer dbData) {
            return dbData == null ? null : QueryLiteralTest.Numbers.values()[(dbData - 1)];
        }
    }

    @Converter
    public static class NumberStringConverter implements AttributeConverter<QueryLiteralTest.Numbers, String> {
        @Override
        public String convertToDatabaseColumn(QueryLiteralTest.Numbers attribute) {
            return attribute == null ? null : Integer.toString(((attribute.ordinal()) + 1));
        }

        @Override
        public QueryLiteralTest.Numbers convertToEntityAttribute(String dbData) {
            return dbData == null ? null : QueryLiteralTest.Numbers.values()[((Integer.parseInt(dbData)) - 1)];
        }
    }

    @Converter(autoApply = true)
    public static class IntegerWrapperConverter implements AttributeConverter<QueryLiteralTest.IntegerWrapper, Integer> {
        @Override
        public Integer convertToDatabaseColumn(QueryLiteralTest.IntegerWrapper attribute) {
            return attribute == null ? null : attribute.getValue();
        }

        @Override
        public QueryLiteralTest.IntegerWrapper convertToEntityAttribute(Integer dbData) {
            return dbData == null ? null : new QueryLiteralTest.IntegerWrapper(dbData);
        }
    }

    @Converter(autoApply = true)
    public static class StringWrapperConverter implements AttributeConverter<QueryLiteralTest.StringWrapper, String> {
        @Override
        public String convertToDatabaseColumn(QueryLiteralTest.StringWrapper attribute) {
            return attribute == null ? null : attribute.getValue();
        }

        @Override
        public QueryLiteralTest.StringWrapper convertToEntityAttribute(String dbData) {
            return dbData == null ? null : new QueryLiteralTest.StringWrapper(dbData);
        }
    }

    @Converter
    public static class PreFixedStringConverter implements AttributeConverter<String, String> {
        @Override
        public String convertToDatabaseColumn(String attribute) {
            return attribute == null ? null : "VALUE_" + attribute;
        }

        @Override
        public String convertToEntityAttribute(String dbData) {
            return dbData == null ? null : dbData.substring(6);
        }
    }

    @Entity(name = "EntityConverter")
    @Table(name = "entity_converter")
    public static class EntityConverter {
        @Id
        @GeneratedValue
        private Integer id;

        private QueryLiteralTest.Letter letterOrdinal;

        @Enumerated(EnumType.STRING)
        private QueryLiteralTest.Letter letterString;

        private QueryLiteralTest.Numbers numbersImplicit;

        @Convert(converter = QueryLiteralTest.NumberStringConverter.class)
        private QueryLiteralTest.Numbers numbersImplicitOverrided;

        private QueryLiteralTest.IntegerWrapper integerWrapper;

        private QueryLiteralTest.StringWrapper stringWrapper;

        @Convert(converter = QueryLiteralTest.PreFixedStringConverter.class)
        @Column(name = "same_type_converter")
        private String sameTypeConverter;

        public Integer getId() {
            return id;
        }

        public QueryLiteralTest.Letter getLetterOrdinal() {
            return letterOrdinal;
        }

        public void setLetterOrdinal(QueryLiteralTest.Letter letterOrdinal) {
            this.letterOrdinal = letterOrdinal;
        }

        public QueryLiteralTest.Letter getLetterString() {
            return letterString;
        }

        public void setLetterString(QueryLiteralTest.Letter letterString) {
            this.letterString = letterString;
        }

        public QueryLiteralTest.Numbers getNumbersImplicit() {
            return numbersImplicit;
        }

        public void setNumbersImplicit(QueryLiteralTest.Numbers numbersImplicit) {
            this.numbersImplicit = numbersImplicit;
        }

        public QueryLiteralTest.Numbers getNumbersImplicitOverrided() {
            return numbersImplicitOverrided;
        }

        public void setNumbersImplicitOverrided(QueryLiteralTest.Numbers numbersImplicitOverrided) {
            this.numbersImplicitOverrided = numbersImplicitOverrided;
        }

        public QueryLiteralTest.IntegerWrapper getIntegerWrapper() {
            return integerWrapper;
        }

        public void setIntegerWrapper(QueryLiteralTest.IntegerWrapper integerWrapper) {
            this.integerWrapper = integerWrapper;
        }

        public QueryLiteralTest.StringWrapper getStringWrapper() {
            return stringWrapper;
        }

        public void setStringWrapper(QueryLiteralTest.StringWrapper stringWrapper) {
            this.stringWrapper = stringWrapper;
        }

        public String getSameTypeConverter() {
            return sameTypeConverter;
        }

        public void setSameTypeConverter(String sameTypeConverter) {
            this.sameTypeConverter = sameTypeConverter;
        }
    }

    public static class IntegerWrapper {
        private final int value;

        public IntegerWrapper(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.format("IntegerWrapper{value=%d}", value);
        }
    }

    public static class StringWrapper {
        private final String value;

        public StringWrapper(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}

