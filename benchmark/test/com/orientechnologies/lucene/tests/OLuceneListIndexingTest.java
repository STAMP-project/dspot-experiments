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


import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.Test;


/**
 * Created by enricorisa on 28/06/14.
 */
public class OLuceneListIndexingTest extends OLuceneBaseTest {
    @Test
    public void testIndexingList() throws Exception {
        OSchema schema = db.getMetadata().getSchema();
        // Rome
        ODocument doc = new ODocument("City");
        doc.field("name", "Rome");
        doc.field("tags", Arrays.asList("Beautiful", "Touristic", "Sunny"));
        db.save(doc);
        OIndex tagsIndex = schema.getClass("City").getClassIndex("City.tags");
        Collection<?> coll = ((Collection<?>) (tagsIndex.get("Sunny")));
        assertThat(coll).hasSize(1);
        doc = db.load(((ORID) (coll.iterator().next())));
        assertThat(doc.<String>field("name")).isEqualTo("Rome");
        // London
        doc = new ODocument("City");
        doc.field("name", "London");
        doc.field("tags", Arrays.asList("Beautiful", "Touristic", "Sunny"));
        db.save(doc);
        coll = ((Collection<?>) (tagsIndex.get("Sunny")));
        assertThat(coll).hasSize(2);
        // modify london: it is rainy
        List<String> tags = doc.field("tags");
        tags.remove("Sunny");
        tags.add("Rainy");
        db.save(doc);
        coll = ((Collection<?>) (tagsIndex.get("Rainy")));
        assertThat(coll).hasSize(1);
        coll = ((Collection<?>) (tagsIndex.get("Beautiful")));
        assertThat(coll).hasSize(2);
        coll = ((Collection<?>) (tagsIndex.get("Sunny")));
        assertThat(coll).hasSize(1);
        OResultSet query = db.query("select from City where search_class('Beautiful') =true ");
        assertThat(query).hasSize(2);
        query.close();
    }
}

