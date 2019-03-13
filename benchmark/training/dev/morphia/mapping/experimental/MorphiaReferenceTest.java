package dev.morphia.mapping.experimental;


import dev.morphia.TestBase;
import dev.morphia.annotations.Id;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class MorphiaReferenceTest extends TestBase {
    @Test
    public void basicReference() {
        final MorphiaReferenceTest.Author author = new MorphiaReferenceTest.Author("Jane Austen");
        getDs().save(author);
        final MorphiaReferenceTest.Book book = new MorphiaReferenceTest.Book("Pride and Prejudice");
        book.setAuthor(author);
        getDs().save(book);
        final MorphiaReferenceTest.Book loaded = getDs().find(MorphiaReferenceTest.Book.class).filter("_id", book.id).first();
        Assert.assertFalse(loaded.author.isResolved());
        Assert.assertEquals(author, loaded.author.get());
        Assert.assertTrue(loaded.author.isResolved());
    }

    @Test
    public void listReference() {
        final MorphiaReferenceTest.Author author = new MorphiaReferenceTest.Author("Jane Austen");
        getDs().save(author);
        List<MorphiaReferenceTest.Book> list = new ArrayList<MorphiaReferenceTest.Book>();
        list.add(new MorphiaReferenceTest.Book("Sense and Sensibility"));
        list.add(new MorphiaReferenceTest.Book("Pride and Prejudice"));
        list.add(new MorphiaReferenceTest.Book("Mansfield Park"));
        list.add(new MorphiaReferenceTest.Book("Emma"));
        list.add(new MorphiaReferenceTest.Book("Northanger Abbey"));
        for (final MorphiaReferenceTest.Book book : list) {
            book.setAuthor(author);
            getDs().save(book);
        }
        author.setList(list);
        getDs().save(author);
        final MorphiaReferenceTest.Author loaded = getDs().find(MorphiaReferenceTest.Author.class).filter("_id", author.getId()).first();
        Assert.assertFalse(loaded.list.isResolved());
        Assert.assertEquals(list, loaded.getList());
        Assert.assertTrue(loaded.list.isResolved());
    }

    @Test
    public void setReference() {
        final MorphiaReferenceTest.Author author = new MorphiaReferenceTest.Author("Jane Austen");
        getDs().save(author);
        Set<MorphiaReferenceTest.Book> set = new HashSet<MorphiaReferenceTest.Book>(5);
        set.add(new MorphiaReferenceTest.Book("Sense and Sensibility"));
        set.add(new MorphiaReferenceTest.Book("Pride and Prejudice"));
        set.add(new MorphiaReferenceTest.Book("Mansfield Park"));
        set.add(new MorphiaReferenceTest.Book("Emma"));
        set.add(new MorphiaReferenceTest.Book("Northanger Abbey"));
        for (final MorphiaReferenceTest.Book book : set) {
            book.setAuthor(author);
            getDs().save(book);
        }
        author.setSet(set);
        getDs().save(author);
        final MorphiaReferenceTest.Author loaded = getDs().find(MorphiaReferenceTest.Author.class).filter("_id", author.getId()).first();
        Assert.assertFalse(loaded.set.isResolved());
        final Set<MorphiaReferenceTest.Book> set1 = loaded.getSet();
        Assert.assertEquals(set.size(), set1.size());
        for (final MorphiaReferenceTest.Book book : set) {
            Assert.assertTrue(((("Looking for " + book) + " in ") + set1), set1.contains(book));
        }
        Assert.assertTrue(loaded.set.isResolved());
    }

    @Test
    public void mapReference() {
        final MorphiaReferenceTest.Author author = new MorphiaReferenceTest.Author("Jane Austen");
        getDs().save(author);
        Map<String, MorphiaReferenceTest.Book> books = new LinkedHashMap<String, MorphiaReferenceTest.Book>();
        for (final MorphiaReferenceTest.Book book : new MorphiaReferenceTest.Book[]{ new MorphiaReferenceTest.Book("Sense and Sensibility"), new MorphiaReferenceTest.Book("Pride and Prejudice"), new MorphiaReferenceTest.Book("Mansfield Park"), new MorphiaReferenceTest.Book("Emma"), new MorphiaReferenceTest.Book("Northanger Abbey") }) {
            book.setAuthor(author);
            getDs().save(book);
            books.put(book.name, book);
        }
        author.setMap(books);
        getDs().save(author);
        final MorphiaReferenceTest.Author loaded = getDs().find(MorphiaReferenceTest.Author.class).filter("_id", author.getId()).first();
        Assert.assertFalse(loaded.map.isResolved());
        Assert.assertEquals(books, loaded.getMap());
        Assert.assertTrue(loaded.map.isResolved());
    }

    @Test
    public void basicReferenceWithCollection() {
        final MorphiaReferenceTest.Author author = new MorphiaReferenceTest.Author("Jane Austen");
        getAds().save("jane", author);
        final MorphiaReferenceTest.Book book = new MorphiaReferenceTest.Book("Pride and Prejudice");
        book.setAuthor("jane", author);
        getDs().save(book);
        Assert.assertNull(getDs().find(MorphiaReferenceTest.Author.class).first());
        Assert.assertNotNull(getAds().find("jane", MorphiaReferenceTest.Author.class).first());
        final MorphiaReferenceTest.Book loaded = getDs().find(MorphiaReferenceTest.Book.class).filter("_id", book.id).first();
        Assert.assertFalse(loaded.author.isResolved());
        Assert.assertEquals(author, loaded.author.get());
        Assert.assertTrue(loaded.author.isResolved());
    }

    @Test
    public void listReferenceWithCollection() {
        final MorphiaReferenceTest.Author author = new MorphiaReferenceTest.Author("Jane Austen");
        getAds().save("jane", author);
        List<MorphiaReferenceTest.Book> list = new ArrayList<MorphiaReferenceTest.Book>();
        list.add(new MorphiaReferenceTest.Book("Sense and Sensibility"));
        list.add(new MorphiaReferenceTest.Book("Pride and Prejudice"));
        list.add(new MorphiaReferenceTest.Book("Mansfield Park"));
        list.add(new MorphiaReferenceTest.Book("Emma"));
        list.add(new MorphiaReferenceTest.Book("Northanger Abbey"));
        for (final MorphiaReferenceTest.Book book : list) {
            book.setAuthor("jane", author);
            getAds().save("books", book);
        }
        author.setList("books", list);
        getAds().save("jane", author);
        Assert.assertNull(getDs().find(MorphiaReferenceTest.Author.class).first());
        Assert.assertNotNull(getAds().find("jane", MorphiaReferenceTest.Author.class).first());
        Assert.assertNull(getDs().find(MorphiaReferenceTest.Book.class).first());
        Assert.assertNotNull(getAds().find("books", MorphiaReferenceTest.Book.class).first());
        final MorphiaReferenceTest.Author loaded = getAds().find("jane", MorphiaReferenceTest.Author.class).filter("_id", author.getId()).first();
        Assert.assertFalse(loaded.list.isResolved());
        Assert.assertEquals(list, loaded.getList());
        Assert.assertTrue(loaded.list.isResolved());
    }

    @Test
    public void mapReferenceWithCollection() {
        final MorphiaReferenceTest.Author author = new MorphiaReferenceTest.Author("Jane Austen");
        getAds().save("jane", author);
        Map<String, MorphiaReferenceTest.Book> books = new LinkedHashMap<String, MorphiaReferenceTest.Book>();
        for (final MorphiaReferenceTest.Book book : new MorphiaReferenceTest.Book[]{ new MorphiaReferenceTest.Book("Sense and Sensibility"), new MorphiaReferenceTest.Book("Pride and Prejudice"), new MorphiaReferenceTest.Book("Mansfield Park"), new MorphiaReferenceTest.Book("Emma"), new MorphiaReferenceTest.Book("Northanger Abbey") }) {
            book.setAuthor("jane", author);
            getAds().save("books", book);
            books.put(book.name, book);
        }
        author.setMap("books", books);
        getAds().save("jane", author);
        Assert.assertNull(getDs().find(MorphiaReferenceTest.Author.class).first());
        Assert.assertNotNull(getAds().find("jane", MorphiaReferenceTest.Author.class).first());
        Assert.assertNull(getDs().find(MorphiaReferenceTest.Book.class).first());
        Assert.assertNotNull(getAds().find("books", MorphiaReferenceTest.Book.class).first());
        final MorphiaReferenceTest.Author loaded = getAds().find("jane", MorphiaReferenceTest.Author.class).filter("_id", author.getId()).first();
        Assert.assertFalse(loaded.map.isResolved());
        Assert.assertEquals(books, loaded.getMap());
        Assert.assertTrue(loaded.map.isResolved());
    }

    @Test
    public void setReferenceWithCollection() {
        final MorphiaReferenceTest.Author author = new MorphiaReferenceTest.Author("Jane Austen");
        getAds().save("jane", author);
        Set<MorphiaReferenceTest.Book> set = new HashSet<MorphiaReferenceTest.Book>(5);
        set.add(new MorphiaReferenceTest.Book("Sense and Sensibility"));
        set.add(new MorphiaReferenceTest.Book("Pride and Prejudice"));
        set.add(new MorphiaReferenceTest.Book("Mansfield Park"));
        set.add(new MorphiaReferenceTest.Book("Emma"));
        set.add(new MorphiaReferenceTest.Book("Northanger Abbey"));
        for (final MorphiaReferenceTest.Book book : set) {
            book.setAuthor("jane", author);
            getAds().save("books", book);
        }
        author.setSet("books", set);
        getAds().save("jane", author);
        final MorphiaReferenceTest.Author loaded = getAds().find("jane", MorphiaReferenceTest.Author.class).filter("_id", author.getId()).first();
        Assert.assertFalse(loaded.set.isResolved());
        final Set<MorphiaReferenceTest.Book> set1 = loaded.getSet();
        Assert.assertEquals(set.size(), set1.size());
        for (final MorphiaReferenceTest.Book book : set) {
            Assert.assertTrue(((("Looking for " + book) + " in ") + set1), set1.contains(book));
        }
        Assert.assertTrue(loaded.set.isResolved());
    }

    private static class Author {
        @Id
        private ObjectId id;

        private String name;

        private MorphiaReference<List<MorphiaReferenceTest.Book>> list;

        private MorphiaReference<Set<MorphiaReferenceTest.Book>> set;

        private MorphiaReference<Map<String, MorphiaReferenceTest.Book>> map;

        public Author() {
        }

        public Author(final String name) {
            this.name = name;
        }

        public ObjectId getId() {
            return id;
        }

        public void setId(final ObjectId id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public List<MorphiaReferenceTest.Book> getList() {
            return list.get();
        }

        public void setList(final List<MorphiaReferenceTest.Book> list) {
            this.list = MorphiaReference.wrap(list);
        }

        public void setList(final String collection, final List<MorphiaReferenceTest.Book> list) {
            this.list = MorphiaReference.wrap(collection, list);
        }

        public Set<MorphiaReferenceTest.Book> getSet() {
            return set.get();
        }

        public void setSet(final Set<MorphiaReferenceTest.Book> set) {
            this.set = MorphiaReference.wrap(set);
        }

        public void setSet(final String collection, final Set<MorphiaReferenceTest.Book> set) {
            this.set = MorphiaReference.wrap(collection, set);
        }

        public Map<String, MorphiaReferenceTest.Book> getMap() {
            return map.get();
        }

        public void setMap(final Map<String, MorphiaReferenceTest.Book> map) {
            this.map = MorphiaReference.wrap(map);
        }

        public void setMap(final String collection, final Map<String, MorphiaReferenceTest.Book> map) {
            this.map = MorphiaReference.wrap(collection, map);
        }

        @Override
        public boolean equals(final Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            final MorphiaReferenceTest.Author author = ((MorphiaReferenceTest.Author) (o));
            if ((id) != null ? !(id.equals(author.id)) : (author.id) != null) {
                return false;
            }
            return (name) != null ? name.equals(author.name) : (author.name) == null;
        }

        @Override
        public int hashCode() {
            int result = ((id) != null) ? id.hashCode() : 0;
            result = (31 * result) + ((name) != null ? name.hashCode() : 0);
            return result;
        }
    }

    private static class Book {
        @Id
        private ObjectId id;

        private String name;

        private MorphiaReference<MorphiaReferenceTest.Author> author;

        public Book() {
        }

        public Book(final String name) {
            this.name = name;
        }

        public ObjectId getId() {
            return id;
        }

        public void setId(final ObjectId id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public MorphiaReferenceTest.Author getAuthor() {
            return author.get();
        }

        public void setAuthor(final MorphiaReferenceTest.Author author) {
            this.author = MorphiaReference.wrap(author);
        }

        public void setAuthor(final String collection, final MorphiaReferenceTest.Author author) {
            this.author = MorphiaReference.wrap(collection, author);
        }

        @Override
        public boolean equals(final Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            final MorphiaReferenceTest.Book book = ((MorphiaReferenceTest.Book) (o));
            if ((id) != null ? !(id.equals(book.id)) : (book.id) != null) {
                return false;
            }
            return (name) != null ? name.equals(book.name) : (book.name) == null;
        }

        @Override
        public int hashCode() {
            int result = ((id) != null) ? id.hashCode() : 0;
            result = (31 * result) + ((name) != null ? name.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return ((((("Book{" + "name='") + (name)) + "', ") + "hash=") + (hashCode())) + '}';
        }
    }
}

