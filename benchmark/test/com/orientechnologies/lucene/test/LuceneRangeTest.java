package com.orientechnologies.lucene.test;


import DateTools.Resolution.MINUTE;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import java.util.Collection;
import org.apache.lucene.document.DateTools;
import org.junit.Test;


/**
 * Created by frank on 13/12/2016.
 */
public class LuceneRangeTest extends BaseLuceneTest {
    @Test
    public void shouldUseRangeQueryOnSingleIntegerField() throws Exception {
        db.command(new OCommandSQL("create index Person.age on Person(age) FULLTEXT ENGINE LUCENE")).execute();
        assertThat(db.getMetadata().getIndexManager().getIndex("Person.age").getSize()).isEqualTo(11);
        // range
        Collection<ODocument> results = db.command(new OCommandSQL("SELECT FROM Person WHERE age LUCENE 'age:[5 TO 6]'")).execute();
        assertThat(results).hasSize(2);
        // single value
        results = db.command(new OCommandSQL("SELECT FROM Person WHERE age LUCENE 'age:5'")).execute();
        assertThat(results).hasSize(1);
    }

    @Test
    public void shouldUseRangeQueryOnSingleDateField() throws Exception {
        db.commit();
        db.command(new OCommandSQL("create index Person.date on Person(date) FULLTEXT ENGINE LUCENE")).execute();
        db.commit();
        assertThat(db.getMetadata().getIndexManager().getIndex("Person.date").getSize()).isEqualTo(11);
        String today = DateTools.timeToString(System.currentTimeMillis(), MINUTE);
        String fiveDaysAgo = DateTools.timeToString(((System.currentTimeMillis()) - (((5 * 3600) * 24) * 1000)), MINUTE);
        // range
        Collection<ODocument> results = db.command(new OCommandSQL((((("SELECT FROM Person WHERE date LUCENE 'date:[" + fiveDaysAgo) + " TO ") + today) + "]'"))).execute();
        assertThat(results).hasSize(5);
    }

    @Test
    public void shouldUseRangeQueryMultipleField() throws Exception {
        db.command(new OCommandSQL("create index Person.composite on Person(name,surname,date,age) FULLTEXT ENGINE LUCENE")).execute();
        assertThat(db.getMetadata().getIndexManager().getIndex("Person.composite").getSize()).isEqualTo(11);
        db.commit();
        String today = DateTools.timeToString(System.currentTimeMillis(), MINUTE);
        String fiveDaysAgo = DateTools.timeToString(((System.currentTimeMillis()) - (((5 * 3600) * 24) * 1000)), MINUTE);
        // name and age range
        Collection<ODocument> results = db.command(new OCommandSQL("SELECT * FROM Person WHERE [name,surname,date,age] LUCENE 'age:[5 TO 6] name:robert  '")).execute();
        assertThat(results).hasSize(3);
        // date range
        results = db.command(new OCommandSQL((((("SELECT FROM Person WHERE [name,surname,date,age] LUCENE 'date:[" + fiveDaysAgo) + " TO ") + today) + "]'"))).execute();
        assertThat(results).hasSize(5);
        // age and date range with MUST
        results = db.command(new OCommandSQL((((("SELECT FROM Person WHERE [name,surname,date,age] LUCENE '+age:[4 TO 7]  +date:[" + fiveDaysAgo) + " TO ") + today) + "]'"))).execute();
        assertThat(results).hasSize(2);
    }

    @Test
    public void shouldUseRangeQueryMultipleFieldWithDirectIndexAccess() throws Exception {
        db.command(new OCommandSQL("create index Person.composite on Person(name,surname,date,age) FULLTEXT ENGINE LUCENE")).execute();
        assertThat(db.getMetadata().getIndexManager().getIndex("Person.composite").getSize()).isEqualTo(11);
        db.commit();
        String today = DateTools.timeToString(System.currentTimeMillis(), MINUTE);
        String fiveDaysAgo = DateTools.timeToString(((System.currentTimeMillis()) - (((5 * 3600) * 24) * 1000)), MINUTE);
        // anme and age range
        Collection<ODocument> results = db.command(new OCommandSQL("SELECT * FROM index:Person.composite WHERE key ='name:luke  age:[5 TO 6]'")).execute();
        assertThat(results).hasSize(2);
        // date range
        results = db.command(new OCommandSQL((((("SELECT FROM index:Person.composite WHERE key = 'date:[" + fiveDaysAgo) + " TO ") + today) + "]'"))).execute();
        assertThat(results).hasSize(5);
        // age and date range with MUST
        results = db.command(new OCommandSQL((((("SELECT FROM index:Person.composite WHERE key = '+age:[4 TO 7]  +date:[" + fiveDaysAgo) + " TO ") + today) + "]'"))).execute();
        assertThat(results).hasSize(2);
    }

    @Test
    public void shouldFetchOnlyFromACluster() throws Exception {
        db.command(new OCommandSQL("create index Person.name on Person(name) FULLTEXT ENGINE LUCENE")).execute();
        assertThat(db.getMetadata().getIndexManager().getIndex("Person.name").getSize()).isEqualTo(11);
        int cluster = db.getMetadata().getSchema().getClass("Person").getClusterIds()[1];
        db.commit();
        Collection<ODocument> results = db.command(new OCommandSQL((("SELECT FROM Person WHERE name LUCENE '+_CLUSTER:" + cluster) + "'"))).execute();
        assertThat(results).hasSize(2);
    }
}

