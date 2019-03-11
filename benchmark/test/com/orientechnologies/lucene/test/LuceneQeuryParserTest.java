package com.orientechnologies.lucene.test;


import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import java.util.List;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.junit.Test;


/**
 * Created by frank on 19/05/2016.
 */
public class LuceneQeuryParserTest extends BaseLuceneTest {
    @Test
    public void shouldSearchWithLeadingWildcard() {
        // enabling leading wildcard
        db.command(new OCommandSQL("create index Song.title on Song (title) FULLTEXT ENGINE LUCENE metadata {\"allowLeadingWildcard\": true}")).execute();
        // querying with leading wildcard
        List<ODocument> docs = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select * from Song where [title] LUCENE \"(title:*tain)\""));
        assertThat(docs).hasSize(4);
    }

    @Test
    public void shouldSearchWithLowercaseExpandedTerms() {
        // enabling leading wildcard
        db.command(new OCommandSQL((("create index Song.author on Song (author) FULLTEXT ENGINE LUCENE metadata {\"default\": \"" + (KeywordAnalyzer.class.getCanonicalName())) + "\", \"lowercaseExpandedTerms\": false}"))).execute();
        List<ODocument> docs = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select * from Song where [author] LUCENE \"Hunter\""));
        assertThat(docs).hasSize(97);
        docs = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select * from Song where [author] LUCENE \"HUNTER\""));
        assertThat(docs).hasSize(0);
    }
}

