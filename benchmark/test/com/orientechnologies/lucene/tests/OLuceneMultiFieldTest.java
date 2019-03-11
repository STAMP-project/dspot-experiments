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
package com.orientechnologies.lucene.tests;


import com.orientechnologies.orient.core.sql.executor.OResultSet;
import org.junit.Test;


/**
 * Created by enricorisa on 19/09/14.
 */
public class OLuceneMultiFieldTest extends OLuceneBaseTest {
    @Test
    public void testSelectSingleDocumentWithAndOperator() {
        OResultSet docs = db.query("select * from Song where  search_fields(['title','author'] ,'title:mountain AND author:Fabbio')=true");
        assertThat(docs).hasSize(1);
        docs.close();
    }

    @Test
    public void testSelectMultipleDocumentsWithOrOperator() {
        OResultSet docs = db.query("select * from Song where  search_fields(['title','author'] ,'title:mountain OR author:Fabbio')=true");
        assertThat(docs).hasSize(91);
        docs.close();
    }

    @Test
    public void testSelectOnTitleAndAuthorWithMatchOnTitle() {
        OResultSet docs = db.query("select * from  Song where search_fields(['title','author'] ,'title:mountain')=true");
        assertThat(docs).hasSize(5);
        docs.close();
    }

    @Test
    public void testSelectOnTitleAndAuthorWithMatchOnAuthor() {
        OResultSet docs = db.query("select * from Song where search_class('author:fabbio')=true");
        assertThat(docs).hasSize(87);
        docs.close();
        docs = db.query("select * from Song where search_class('fabbio')=true");
        assertThat(docs).hasSize(87);
        docs.close();
    }

    @Test
    public void testSelectOnIndexWithIgnoreNullValuesToFalse() {
        // #5579
        String script = "create class Item;\n" + ((((("create property Item.title string;\n" + "create property Item.summary string;\n") + "create property Item.content string;\n") + "create index Item.fulltext on Item(title, summary, content) FULLTEXT ENGINE LUCENE METADATA {\'ignoreNullValues\':false};\n") + "insert into Item set title = \'wrong\', content = \'not me please\';\n") + "insert into Item set title = \'test\', content = \'this is a test\';\n");
        db.execute("sql", script).close();
        OResultSet docs;
        docs = db.query("select * from Item where search_class('te*')=true");
        assertThat(docs).hasSize(1);
        docs.close();
        docs = db.query("select * from Item where search_class('test')=true");
        assertThat(docs).hasSize(1);
        docs.close();
        docs = db.query("select * from Item where search_class('title:test')=true");
        assertThat(docs).hasSize(1);
        docs.close();
        // index
        docs = db.query(" SELECT expand(rid) FROM index:Item.fulltext where key = 'title:test'");
        assertThat(docs).hasSize(1);
        docs.close();
    }
}

