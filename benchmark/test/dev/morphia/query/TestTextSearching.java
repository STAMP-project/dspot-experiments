package dev.morphia.query;


import dev.morphia.TestBase;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.Indexes;
import dev.morphia.utils.IndexType;
import java.util.Arrays;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class TestTextSearching extends TestBase {
    @Test
    public void testTextSearch() {
        getMorphia().map(TestTextSearching.Greeting.class);
        getDs().ensureIndexes();
        getDs().save(new TestTextSearching.Greeting("good morning", "english"));
        getDs().save(new TestTextSearching.Greeting("good afternoon", "english"));
        getDs().save(new TestTextSearching.Greeting("good night", "english"));
        getDs().save(new TestTextSearching.Greeting("good riddance", "english"));
        getDs().save(new TestTextSearching.Greeting("guten Morgen", "german"));
        getDs().save(new TestTextSearching.Greeting("guten Tag", "german"));
        getDs().save(new TestTextSearching.Greeting("gute Nacht", "german"));
        getDs().save(new TestTextSearching.Greeting("buenos d?as", "spanish"));
        getDs().save(new TestTextSearching.Greeting("buenas tardes", "spanish"));
        getDs().save(new TestTextSearching.Greeting("buenos noches", "spanish"));
        List<TestTextSearching.Greeting> good = TestBase.toList(getDs().find(TestTextSearching.Greeting.class).search("good").order("_id").find());
        Assert.assertEquals(4, good.size());
        Assert.assertEquals("good morning", good.get(0).value);
        Assert.assertEquals("good afternoon", good.get(1).value);
        Assert.assertEquals("good night", good.get(2).value);
        Assert.assertEquals("good riddance", good.get(3).value);
        good = TestBase.toList(getDs().find(TestTextSearching.Greeting.class).search("good", "english").order("_id").find());
        Assert.assertEquals(4, good.size());
        Assert.assertEquals("good morning", good.get(0).value);
        Assert.assertEquals("good afternoon", good.get(1).value);
        Assert.assertEquals("good night", good.get(2).value);
        Assert.assertEquals("good riddance", good.get(3).value);
        Assert.assertEquals(1, TestBase.toList(getDs().find(TestTextSearching.Greeting.class).search("riddance").find()).size());
        Assert.assertEquals(1, TestBase.toList(getDs().find(TestTextSearching.Greeting.class).search("noches", "spanish").find()).size());
        Assert.assertEquals(1, TestBase.toList(getDs().find(TestTextSearching.Greeting.class).search("Tag").find()).size());
    }

    @Test
    public void testTextSearchSorting() {
        getMorphia().map(TestTextSearching.Book.class);
        getDs().ensureIndexes();
        getDs().save(Arrays.asList(new TestTextSearching.Book("The Banquet", "Dante"), new TestTextSearching.Book("Divine Comedy", "Dante"), new TestTextSearching.Book("Eclogues", "Dante"), new TestTextSearching.Book("The Odyssey", "Homer"), new TestTextSearching.Book("Iliad", "Homer")));
        List<TestTextSearching.Book> books = TestBase.toList(getDs().find(TestTextSearching.Book.class).search("Dante Comedy").project(Meta.textScore("score")).order(Meta.textScore("score")).find());
        Assert.assertEquals(3, books.size());
        Assert.assertEquals("Divine Comedy", books.get(0).title);
    }

    @Test
    public void testTextSearchValidationFailed() {
        getMorphia().map(TestTextSearching.Book.class);
        getDs().ensureIndexes();
        getDs().save(Arrays.asList(new TestTextSearching.Book("The Banquet", "Dante"), new TestTextSearching.Book("Divine Comedy", "Dante"), new TestTextSearching.Book("Eclogues", "Dante"), new TestTextSearching.Book("The Odyssey", "Homer"), new TestTextSearching.Book("Iliad", "Homer")));
        List<TestTextSearching.Book> books = TestBase.toList(getDs().find(TestTextSearching.Book.class).search("Dante").project(Meta.textScore()).order(Meta.textScore()).find());
        Assert.assertEquals(3, books.size());
        Assert.assertEquals("Dante", books.get(0).author);
    }

    @Test
    public void testTextSearchWithMeta() {
        getMorphia().map(TestTextSearching.Book.class);
        getDs().ensureIndexes();
        getDs().save(Arrays.asList(new TestTextSearching.Book("The Banquet", "Dante"), new TestTextSearching.Book("Divine Comedy", "Dante"), new TestTextSearching.Book("Eclogues", "Dante"), new TestTextSearching.Book("The Odyssey", "Homer"), new TestTextSearching.Book("Iliad", "Homer")));
        List<TestTextSearching.Book> books = TestBase.toList(getDs().find(TestTextSearching.Book.class).search("Dante").project(Meta.textScore("score")).order(Meta.textScore("score")).find());
        Assert.assertEquals(3, books.size());
        for (TestTextSearching.Book book : books) {
            Assert.assertEquals("Dante", book.author);
        }
    }

    @Indexes(@Index(fields = @Field(value = "$**", type = IndexType.TEXT)))
    private static class Greeting {
        @Id
        private ObjectId id;

        private String value;

        private String language;

        Greeting() {
        }

        private Greeting(final String value, final String language) {
            this.language = language;
            this.value = value;
        }
    }

    @Indexes(@Index(fields = @Field(value = "$**", type = IndexType.TEXT)))
    private static class Book {
        @Id
        private ObjectId id;

        private String title;

        private String author;

        Book() {
        }

        private Book(final String title, final String author) {
            this.author = author;
            this.title = title;
        }
    }
}

