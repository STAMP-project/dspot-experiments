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
import com.orientechnologies.orient.core.sql.OCommandSQL;
import java.io.InputStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.junit.Test;


/**
 * Created by enricorisa on 26/09/14.
 */
public class LuceneCreateIndexTest extends BaseLuceneTest {
    @Test
    public void loadAndTest() {
        InputStream stream = ClassLoader.getSystemResourceAsStream("testLuceneIndex.sql");
        db.command(new OCommandScript("sql", getScriptFromStream(stream))).execute();
        db.command(new OCommandSQL((("create index Song.title on Song (title) FULLTEXT ENGINE LUCENE METADATA {\"analyzer\":\"" + (StandardAnalyzer.class.getName())) + "\"}"))).execute();
        db.command(new OCommandSQL((("create index Song.author on Song (author) FULLTEXT ENGINE LUCENE METADATA {\"analyzer\":\"" + (StandardAnalyzer.class.getName())) + "\"}"))).execute();
        ODocument doc = new ODocument("Song");
        doc.field("title", "Local");
        doc.field("author", "Local");
        db.save(doc);
        testMetadata();
        assertQuery();
        assertNewQuery();
        db.close();
        db.open("admin", "admin");
        assertQuery();
        assertNewQuery();
    }
}

