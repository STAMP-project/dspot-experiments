package dev.morphia;


import dev.morphia.annotations.AlsoLoad;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.query.FindOptions;
import java.util.List;
import java.util.Set;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class TestSingleToMultipleConversion extends TestBase {
    @Test
    public void testBasicType() {
        getDs().delete(getDs().find(TestSingleToMultipleConversion.HasSingleString.class));
        getDs().save(new TestSingleToMultipleConversion.HasSingleString());
        Assert.assertNotNull(getDs().find(TestSingleToMultipleConversion.HasSingleString.class).find(new FindOptions().limit(1)).next());
        Assert.assertEquals(1, getDs().find(TestSingleToMultipleConversion.HasSingleString.class).count());
        final TestSingleToMultipleConversion.HasManyStringsArray hms = getDs().find(TestSingleToMultipleConversion.HasManyStringsArray.class).find(new FindOptions().limit(1)).next();
        Assert.assertNotNull(hms);
        Assert.assertNotNull(hms.strings);
        Assert.assertEquals(1, hms.strings.length);
        final TestSingleToMultipleConversion.HasManyStringsList hms2 = getDs().find(TestSingleToMultipleConversion.HasManyStringsList.class).find(new FindOptions().limit(1)).next();
        Assert.assertNotNull(hms2);
        Assert.assertNotNull(hms2.strings);
        Assert.assertEquals(1, hms2.strings.size());
    }

    @Test
    public void testEmbeddedType() {
        getDs().save(new TestSingleToMultipleConversion.HasEmbeddedStringy());
        Assert.assertNotNull(getDs().find(TestSingleToMultipleConversion.HasEmbeddedStringy.class).find(new FindOptions().limit(1)).next());
        Assert.assertEquals(1, getDs().find(TestSingleToMultipleConversion.HasEmbeddedStringy.class).count());
        final TestSingleToMultipleConversion.HasEmbeddedStringyArray has = getDs().find(TestSingleToMultipleConversion.HasEmbeddedStringyArray.class).find(new FindOptions().limit(1)).next();
        Assert.assertNotNull(has);
        Assert.assertNotNull(has.hss);
        Assert.assertEquals(1, has.hss.length);
        final TestSingleToMultipleConversion.HasEmbeddedStringySet has2 = getDs().find(TestSingleToMultipleConversion.HasEmbeddedStringySet.class).find(new FindOptions().limit(1)).next();
        Assert.assertNotNull(has2);
        Assert.assertNotNull(has2.hss);
        Assert.assertEquals(1, has2.hss.size());
    }

    @Embedded
    private static class HasString {
        private String s = "foo";
    }

    @Entity(value = "B", noClassnameStored = true)
    private static class HasEmbeddedStringy {
        @Id
        private ObjectId id;

        private TestSingleToMultipleConversion.HasString hs = new TestSingleToMultipleConversion.HasString();
    }

    @Entity(value = "B", noClassnameStored = true)
    private static class HasEmbeddedStringyArray {
        @Id
        private ObjectId id;

        @AlsoLoad("hs")
        private TestSingleToMultipleConversion.HasString[] hss;
    }

    @Entity(value = "B", noClassnameStored = true)
    private static class HasEmbeddedStringySet {
        @Id
        private ObjectId id;

        @AlsoLoad("hs")
        private Set<TestSingleToMultipleConversion.HasString> hss;
    }

    @Entity(value = "A", noClassnameStored = true)
    private static class HasSingleString {
        @Id
        private ObjectId id;

        private String s = "foo";
    }

    @Entity(value = "A", noClassnameStored = true)
    private static class HasManyStringsArray {
        @Id
        private ObjectId id;

        @AlsoLoad("s")
        private String[] strings;
    }

    @Entity(value = "A", noClassnameStored = true)
    private static class HasManyStringsList {
        @Id
        private ObjectId id;

        @AlsoLoad("s")
        private List<String> strings;
    }
}

