package org.baeldung.spring.data.cassandra.repository;


import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.utils.UUIDs;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.baeldung.spring.data.cassandra.config.CassandraConfig;
import org.baeldung.spring.data.cassandra.model.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraAdminOperations;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CassandraConfig.class)
public class CqlQueriesIntegrationTest {
    private static final Log LOGGER = LogFactory.getLog(CqlQueriesIntegrationTest.class);

    public static final String KEYSPACE_CREATION_QUERY = "CREATE KEYSPACE IF NOT EXISTS testKeySpace " + "WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': '3' };";

    public static final String KEYSPACE_ACTIVATE_QUERY = "USE testKeySpace;";

    public static final String DATA_TABLE_NAME = "book";

    @Autowired
    private CassandraAdminOperations adminTemplate;

    @Autowired
    private CassandraOperations cassandraTemplate;

    @Test
    public void whenSavingBook_thenAvailableOnRetrieval_usingQueryBuilder() {
        final UUID uuid = UUIDs.timeBased();
        final Insert insert = QueryBuilder.insertInto(CqlQueriesIntegrationTest.DATA_TABLE_NAME).value("id", uuid).value("title", "Head First Java").value("publisher", "OReilly Media").value("tags", ImmutableSet.of("Software"));
        cassandraTemplate.execute(insert);
        final Select select = QueryBuilder.select().from("book").limit(10);
        final Book retrievedBook = cassandraTemplate.selectOne(select, Book.class);
        TestCase.assertEquals(uuid, retrievedBook.getId());
    }

    @Test
    public void whenSavingBook_thenAvailableOnRetrieval_usingCQLStatements() {
        final UUID uuid = UUIDs.timeBased();
        final String insertCql = (("insert into book (id, title, publisher, tags) values " + "(") + uuid) + ", 'Head First Java', 'OReilly Media', {'Software'})";
        cassandraTemplate.execute(insertCql);
        final Select select = QueryBuilder.select().from("book").limit(10);
        final Book retrievedBook = cassandraTemplate.selectOne(select, Book.class);
        TestCase.assertEquals(uuid, retrievedBook.getId());
    }

    @Test
    public void whenSavingBook_thenAvailableOnRetrieval_usingPreparedStatements() throws InterruptedException {
        final UUID uuid = UUIDs.timeBased();
        final String insertPreparedCql = "insert into book (id, title, publisher, tags) values (?, ?, ?, ?)";
        final List<Object> singleBookArgsList = new ArrayList<>();
        final List<List<?>> bookList = new ArrayList<>();
        singleBookArgsList.add(uuid);
        singleBookArgsList.add("Head First Java");
        singleBookArgsList.add("OReilly Media");
        singleBookArgsList.add(ImmutableSet.of("Software"));
        bookList.add(singleBookArgsList);
        cassandraTemplate.ingest(insertPreparedCql, bookList);
        // This may not be required, just added to avoid any transient issues
        Thread.sleep(5000);
        final Select select = QueryBuilder.select().from("book");
        final Book retrievedBook = cassandraTemplate.selectOne(select, Book.class);
        TestCase.assertEquals(uuid, retrievedBook.getId());
    }
}

