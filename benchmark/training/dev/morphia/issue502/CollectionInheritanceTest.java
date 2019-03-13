package dev.morphia.issue502;


import com.mongodb.DBObject;
import dev.morphia.TestBase;
import dev.morphia.annotations.Id;
import java.util.HashSet;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


/**
 * Can't inherit HashSet : generic is lost...
 */
public class CollectionInheritanceTest extends TestBase {
    /**
     * Issue's details...
     */
    @Test
    public void testMappingBook() throws Exception {
        // Mapping...
        /* , Authors.class, Author.class */
        getMorphia().map(CollectionInheritanceTest.Book.class);
        // Test mapping : author objects must be converted into DBObject (but wasn't)
        final DBObject dbBook = getMorphia().getMapper().toDBObject(CollectionInheritanceTest.newBook());
        final Object firstBook = ((List<?>) (dbBook.get("authors"))).iterator().next();
        Assert.assertTrue((("Author wasn't converted : expected instanceof <DBObject>, but was <" + (firstBook.getClass())) + ">"), (firstBook instanceof DBObject));
    }

    /**
     * Real test
     */
    @Test
    public void testSavingBook() throws Exception {
        // Test saving
        getDs().save(CollectionInheritanceTest.newBook());
        Assert.assertEquals(1, getDs().getCollection(CollectionInheritanceTest.Book.class).count());
    }

    private static class Author {
        private String name;

        Author(final String name) {
            this.name = name;
        }
    }

    private static class Authors extends HashSet<CollectionInheritanceTest.Author> {}

    private static class Book {
        @Id
        private ObjectId id;

        private CollectionInheritanceTest.Authors authors = new CollectionInheritanceTest.Authors();
    }
}

