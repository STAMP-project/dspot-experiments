/**
 * * Copyright 2010-2016 OrientDB LTD (http://orientdb.com)
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 */
package com.orientechnologies.lucene.test;


import com.orientechnologies.orient.core.command.script.OCommandScript;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import java.util.List;
import org.junit.Test;


/**
 * Created by enricorisa on 19/09/14.
 */
public class LuceneMultiFieldTest extends BaseLuceneTest {
    public LuceneMultiFieldTest() {
        super();
    }

    @Test
    public void testSelectSingleDocumentWithAndOperator() {
        List<ODocument> docs = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select * from Song where [title,author] LUCENE \"(title:mountain AND author:Fabbio)\""));
        // List<ODocument> docs = databaseDocumentTx.query(
        // new OSQLSynchQuery<ODocument>("select * from Song where [title,author] LUCENE \"(title:mountains)\""));
        assertThat(docs).hasSize(1);
    }

    @Test
    public void testSelectSingleDocumentWithAndOperatorNEwExec() {
        OResultSet docs = db.query("select * from Song where [title,author] LUCENE \"(title:mountain AND author:Fabbio)\"");
        assertThat(docs.hasNext()).isTrue();
        docs.next();
        assertThat(docs.hasNext()).isFalse();
    }

    @Test
    public void testSelectMultipleDocumentsWithOrOperator() {
        List<ODocument> docs = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select * from Song where [title,author] LUCENE \"(title:mountain OR author:Fabbio)\""));
        assertThat(docs).hasSize(91);
    }

    @Test
    public void testSelectOnTitleAndAuthorWithMatchOnTitle() {
        List<ODocument> docs = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select * from Song where [title,author] LUCENE \"mountain\""));
        assertThat(docs).hasSize(5);
    }

    @Test
    public void testSelectOnTitleAndAuthorWithMatchOnAuthor() {
        List<ODocument> docs = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select * from Song where [title,author] LUCENE \"author:fabbio\""));
        assertThat(docs).hasSize(87);
    }

    @Test
    public void testSelectOnIndexWithIgnoreNullValuesToFalse() {
        // #5579
        String script = "create class Item\n" + ((((("create property Item.Title string\n" + "create property Item.Summary string\n") + "create property Item.Content string\n") + "create index Item.i_lucene on Item(Title, Summary, Content) fulltext engine lucene METADATA {ignoreNullValues:false}\n") + "insert into Item set Title = \'wrong\', content = \'not me please\'\n") + "insert into Item set Title = \'test\', content = \'this is a test\'\n");
        db.command(new OCommandScript("sql", script)).execute();
        List<ODocument> docs = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select * from Item where Title lucene 'te*'"));
        assertThat(docs).hasSize(1);
        docs = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select * from Item where [Title, Summary, Content] lucene 'test'"));
        assertThat(docs).hasSize(1);
        // nidex api
        docs = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>(" SELECT expand(rid) FROM index:Item.i_lucene where key = \"(Title:test )\""));
        assertThat(docs).hasSize(1);
    }
}

