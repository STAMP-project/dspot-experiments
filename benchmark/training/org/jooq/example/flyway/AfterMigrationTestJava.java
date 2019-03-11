package org.jooq.example.flyway;


import AUTHOR.FIRST_NAME;
import AUTHOR.LAST_NAME;
import BOOK.AUTHOR_ID;
import BOOK.ID;
import BOOK.TITLE;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Lukas Eder
 */
public class AfterMigrationTestJava {
    @Test
    public void testQueryingAfterMigration() throws Exception {
        try (Connection c = DriverManager.getConnection("jdbc:h2:~/flyway-test", "sa", "")) {
            Result<?> result = DSL.using(c).select(FIRST_NAME, LAST_NAME, ID, TITLE).from(AUTHOR).join(BOOK).on(AUTHOR.ID.eq(AUTHOR_ID)).orderBy(ID.asc()).fetch();
            Assert.assertEquals(4, result.size());
            Assert.assertEquals(Arrays.asList(1, 2, 3, 4), result.getValues(ID));
        }
    }
}

