package com.orientechnologies.lucene.tests;


import DateTools.Resolution.MINUTE;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import org.apache.lucene.document.DateTools;
import org.junit.Test;


/**
 * Created by frank on 13/12/2016.
 */
public class OLuceneRangeTest extends OLuceneBaseTest {
    @Test
    public void shouldUseRangeQueryOnSingleFloatField() throws Exception {
        db.command("create index Person.weight on Person(weight) FULLTEXT ENGINE LUCENE");
        assertThat(db.getMetadata().getIndexManager().getIndex("Person.weight").getSize()).isEqualTo(11);
        // range
        OResultSet results = db.command("SELECT FROM Person WHERE search_class('weight:[0.0 TO 1.1]') = true");
        assertThat(results).hasSize(2);
        // single value
        results = db.command("SELECT FROM Person WHERE search_class('weight:7.1') = true");
        assertThat(results).hasSize(1);
    }

    @Test
    public void shouldUseRangeQueryOnSingleIntegerField() throws Exception {
        db.command("create index Person.age on Person(age) FULLTEXT ENGINE LUCENE");
        assertThat(db.getMetadata().getIndexManager().getIndex("Person.age").getSize()).isEqualTo(11);
        // range
        OResultSet results = db.command("SELECT FROM Person WHERE search_class('age:[5 TO 6]') = true");
        assertThat(results).hasSize(2);
        // single value
        results = db.command("SELECT FROM Person WHERE search_class('age:5') = true");
        assertThat(results).hasSize(1);
    }

    @Test
    public void shouldUseRangeQueryOnSingleDateField() throws Exception {
        db.commit();
        db.command("create index Person.date on Person(date) FULLTEXT ENGINE LUCENE");
        db.commit();
        assertThat(db.getMetadata().getIndexManager().getIndex("Person.date").getSize()).isEqualTo(11);
        String today = DateTools.timeToString(System.currentTimeMillis(), MINUTE);
        String fiveDaysAgo = DateTools.timeToString(((System.currentTimeMillis()) - (((5 * 3600) * 24) * 1000)), MINUTE);
        // range
        OResultSet results = db.command((((("SELECT FROM Person WHERE search_class('date:[" + fiveDaysAgo) + " TO ") + today) + "]')=true"));
        assertThat(results).hasSize(5);
    }

    @Test
    public void shouldUseRangeQueryMultipleField() throws Exception {
        db.command("create index Person.composite on Person(name,surname,date,age) FULLTEXT ENGINE LUCENE");
        assertThat(db.getMetadata().getIndexManager().getIndex("Person.composite").getSize()).isEqualTo(11);
        db.commit();
        String today = DateTools.timeToString(System.currentTimeMillis(), MINUTE);
        String fiveDaysAgo = DateTools.timeToString(((System.currentTimeMillis()) - (((5 * 3600) * 24) * 1000)), MINUTE);
        // name and age range
        OResultSet results = db.command("SELECT * FROM Person WHERE search_class('age:[5 TO 6] name:robert  ')=true");
        assertThat(results).hasSize(3);
        // date range
        results = db.command((((("SELECT FROM Person WHERE search_class('date:[" + fiveDaysAgo) + " TO ") + today) + "]')=true"));
        assertThat(results).hasSize(5);
        // age and date range with MUST
        results = db.command((((("SELECT FROM Person WHERE search_class('+age:[4 TO 7]  +date:[" + fiveDaysAgo) + " TO ") + today) + "]')=true"));
        assertThat(results).hasSize(2);
    }

    @Test
    public void shouldUseRangeQueryMultipleFieldWithDirectIndexAccess() throws Exception {
        db.command("create index Person.composite on Person(name,surname,date,age) FULLTEXT ENGINE LUCENE");
        assertThat(db.getMetadata().getIndexManager().getIndex("Person.composite").getSize()).isEqualTo(11);
        db.commit();
        String today = DateTools.timeToString(System.currentTimeMillis(), MINUTE);
        String fiveDaysAgo = DateTools.timeToString(((System.currentTimeMillis()) - (((5 * 3600) * 24) * 1000)), MINUTE);
        // anme and age range
        OResultSet results = db.query("SELECT * FROM index:Person.composite WHERE key ='name:luke  age:[5 TO 6]'");
        // OResultSet results = db.query("SELECT * FROM index:Person.composite WHERE key ='age:[5 TO 6]'");
        assertThat(results).hasSize(2);
        results.close();
        // date range
        results = db.query((((("SELECT FROM index:Person.composite WHERE key = 'date:[" + fiveDaysAgo) + " TO ") + today) + "]'"));
        assertThat(results).hasSize(5);
        results.close();
        // age and date range with MUST
        results = db.query((((("SELECT FROM index:Person.composite WHERE key = '+age:[4 TO 7]  +date:[" + fiveDaysAgo) + " TO ") + today) + "]'"));
        assertThat(results).hasSize(2);
        results.close();
        results = db.query("SELECT FROM index:Person.composite where key = '*:*'");
        assertThat(results).hasSize(11);
        results.close();
    }
}

