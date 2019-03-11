package com.baeldung.ormlite;


import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ORMLiteIntegrationTest {
    private static JdbcPooledConnectionSource connectionSource;

    private static Dao<Library, Long> libraryDao;

    private static Dao<Book, Long> bookDao;

    @Test
    public void givenDAO_whenCRUD_thenOk() throws SQLException {
        Library library = new Library();
        library.setName("My Library");
        ORMLiteIntegrationTest.libraryDao.create(library);
        Library result = ORMLiteIntegrationTest.libraryDao.queryForId(library.getLibraryId());
        Assert.assertEquals("My Library", result.getName());
        library.setName("My Other Library");
        ORMLiteIntegrationTest.libraryDao.update(library);
        ORMLiteIntegrationTest.libraryDao.delete(library);
    }

    @Test
    public void whenLoopDao_thenOk() throws SQLException {
        Library library1 = new Library();
        library1.setName("My Library");
        ORMLiteIntegrationTest.libraryDao.create(library1);
        Library library2 = new Library();
        library2.setName("My Other Library");
        ORMLiteIntegrationTest.libraryDao.create(library2);
        ORMLiteIntegrationTest.libraryDao.forEach(( lib) -> {
            System.out.println(lib.getName());
        });
    }

    @Test
    public void givenIterator_whenLoop_thenOk() throws IOException, SQLException {
        Library library1 = new Library();
        library1.setName("My Library");
        ORMLiteIntegrationTest.libraryDao.create(library1);
        Library library2 = new Library();
        library2.setName("My Other Library");
        ORMLiteIntegrationTest.libraryDao.create(library2);
        try (CloseableWrappedIterable<Library> wrappedIterable = ORMLiteIntegrationTest.libraryDao.getWrappedIterable()) {
            wrappedIterable.forEach(( lib) -> {
                System.out.println(lib.getName());
            });
        }
    }

    @Test
    public void givenCustomDao_whenSave_thenOk() throws IOException, SQLException {
        Library library = new Library();
        library.setName("My Library");
        LibraryDao customLibraryDao = DaoManager.createDao(ORMLiteIntegrationTest.connectionSource, Library.class);
        customLibraryDao.create(library);
        Assert.assertEquals(1, customLibraryDao.findByName("My Library").size());
    }

    @Test
    public void whenSaveForeignField_thenOk() throws IOException, SQLException {
        Library library = new Library();
        library.setName("My Library");
        library.setAddress(new Address("Main Street nr 20"));
        ORMLiteIntegrationTest.libraryDao.create(library);
        Dao<Address, Long> addressDao = DaoManager.createDao(ORMLiteIntegrationTest.connectionSource, Address.class);
        Assert.assertEquals(1, addressDao.queryForEq("addressLine", "Main Street nr 20").size());
    }

    @Test
    public void whenSaveForeignCollection_thenOk() throws IOException, SQLException {
        Library library = new Library();
        library.setName("My Library");
        ORMLiteIntegrationTest.libraryDao.create(library);
        ORMLiteIntegrationTest.libraryDao.refresh(library);
        library.getBooks().add(new Book("1984"));
        Book book = new Book("It");
        book.setLibrary(library);
        ORMLiteIntegrationTest.bookDao.create(book);
        Assert.assertEquals(2, ORMLiteIntegrationTest.bookDao.queryForEq("library_id", library).size());
    }

    @Test
    public void whenGetLibrariesWithMoreThanOneBook_thenOk() throws IOException, SQLException {
        Library library = new Library();
        library.setName("My Library");
        ORMLiteIntegrationTest.libraryDao.create(library);
        Library library2 = new Library();
        library2.setName("My Other Library");
        ORMLiteIntegrationTest.libraryDao.create(library2);
        ORMLiteIntegrationTest.libraryDao.refresh(library);
        ORMLiteIntegrationTest.libraryDao.refresh(library2);
        library.getBooks().add(new Book("Book1"));
        library2.getBooks().add(new Book("Book2"));
        library2.getBooks().add(new Book("Book3"));
        List<Library> libraries = ORMLiteIntegrationTest.libraryDao.queryBuilder().where().in("libraryId", ORMLiteIntegrationTest.bookDao.queryBuilder().selectColumns("library_id").groupBy("library_id").having("count(*) > 1")).query();
        Assert.assertEquals(1, libraries.size());
    }
}

