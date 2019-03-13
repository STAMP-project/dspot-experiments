package dev.morphia.mapping;


import com.mongodb.BasicDBList;
import dev.morphia.TestBase;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import dev.morphia.mapping.cache.DefaultEntityCache;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class MappedFieldTest extends TestBase {
    @Test
    public void arrayFieldMapping() {
        final MappedField field = new MappedField(getField(MappedFieldTest.TestEntity.class, "arrayOfInt"), MappedFieldTest.TestEntity.class, getMorphia().getMapper());
        Assert.assertFalse(field.isSingleValue());
        Assert.assertTrue(field.isMultipleValues());
        Assert.assertTrue(field.isArray());
        Assert.assertTrue(field.getType().isArray());
        Assert.assertEquals("arrayOfInt", field.getJavaFieldName());
        Assert.assertEquals("arrayOfInt", field.getNameToStore());
    }

    @Test
    public void basicFieldMapping() {
        final MappedField field = new MappedField(getField(MappedFieldTest.TestEntity.class, "name"), MappedFieldTest.TestEntity.class, getMorphia().getMapper());
        Assert.assertTrue(field.isSingleValue());
        Assert.assertTrue(((String.class) == (field.getType())));
        Assert.assertEquals("name", field.getJavaFieldName());
        Assert.assertEquals("n", field.getNameToStore());
    }

    @Test
    public void collectionFieldMapping() {
        final MappedField field = new MappedField(getField(MappedFieldTest.TestEntity.class, "listOfString"), MappedFieldTest.TestEntity.class, getMorphia().getMapper());
        Assert.assertFalse(field.isSingleValue());
        Assert.assertTrue(field.isMultipleValues());
        Assert.assertFalse(field.isArray());
        Assert.assertTrue(((List.class) == (field.getType())));
        Assert.assertTrue(((String.class) == (field.getSubType())));
        Assert.assertEquals("listOfString", field.getJavaFieldName());
        Assert.assertEquals("listOfString", field.getNameToStore());
    }

    @Test
    public void idFieldMapping() {
        final MappedField field = new MappedField(getField(MappedFieldTest.TestEntity.class, "id"), MappedFieldTest.TestEntity.class, getMorphia().getMapper());
        Assert.assertTrue(field.isSingleValue());
        Assert.assertTrue(((ObjectId.class) == (field.getType())));
        Assert.assertEquals("id", field.getJavaFieldName());
        Assert.assertEquals("_id", field.getNameToStore());
    }

    @Test
    public void nestedCollectionsMapping() {
        final MappedField field = new MappedField(getField(MappedFieldTest.TestEntity.class, "listOfListOfString"), MappedFieldTest.TestEntity.class, getMorphia().getMapper());
        Assert.assertFalse(field.isSingleValue());
        Assert.assertTrue(field.isMultipleValues());
        Assert.assertFalse(field.isArray());
        Assert.assertTrue(((List.class) == (field.getType())));
        final List<MappedField> level1Types = field.getTypeParameters();
        final MappedField typeParameter = level1Types.get(0);
        Assert.assertTrue(((List.class) == (typeParameter.getConcreteType())));
        final List<MappedField> level2Types = typeParameter.getTypeParameters();
        final MappedField nested = level2Types.get(0);
        Assert.assertTrue(((String.class) == (nested.getConcreteType())));
        Assert.assertEquals("listOfListOfString", field.getJavaFieldName());
        Assert.assertEquals("listOfListOfString", field.getNameToStore());
        final BasicDBList list = new BasicDBList();
        list.add(dbList("a", "b", "c"));
        list.add(dbList("d", "e", "f"));
        final MappedFieldTest.TestEntity entity = getMorphia().getMapper().fromDb(getDs(), new com.mongodb.BasicDBObject("listOfListOfString", list), new MappedFieldTest.TestEntity(), new DefaultEntityCache());
        final List<String> strings = Arrays.asList("a", "b", "c");
        final List<String> strings1 = Arrays.asList("d", "e", "f");
        final List<List<String>> expected = new ArrayList<List<String>>();
        expected.add(strings);
        expected.add(strings1);
        Assert.assertEquals(expected, entity.listOfListOfString);
    }

    @Entity
    private static class TestEntity {
        @Id
        private ObjectId id;

        @Property("n")
        private String name;

        private List<String> listOfString;

        private List<List<String>> listOfListOfString;

        private int[] arrayOfInt;

        private Map<String, Integer> mapOfInts;

        private List<MappedFieldTest.Embed> listOfEmbeds;
    }

    @Embedded
    private static class Embed {
        private String embedName;

        private List<MappedFieldTest.Embed> embeddeds;
    }
}

