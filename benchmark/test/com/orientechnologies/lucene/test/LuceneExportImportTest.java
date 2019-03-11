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


import OLuceneIndexFactory.LUCENE_ALGORITHM;
import com.orientechnologies.orient.core.index.OIndex;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Enrico Risa on 07/07/15.
 */
public class LuceneExportImportTest extends BaseLuceneTest {
    @Test
    public void testExportImport() {
        String file = "./target/exportTest.json";
        List<?> query = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<Object>("select from City where name lucene 'Rome'"));
        Assert.assertEquals(query.size(), 1);
        try {
            // export
            exportDatabase();
            // import
            db.drop();
            db.create();
            GZIPInputStream stream = new GZIPInputStream(new FileInputStream((file + ".gz")));
            importDatabase();
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
        assertThat(db.countClass("City")).isEqualTo(1);
        OIndex<?> index = db.getMetadata().getIndexManager().getIndex("City.name");
        assertThat(index.getType()).isEqualTo(FULLTEXT.toString());
        assertThat(index.getAlgorithm()).isEqualTo(LUCENE_ALGORITHM);
        // redo the query
        query = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<Object>("select from City where name lucene 'Rome'"));
        assertThat(query).hasSize(1);
    }
}

