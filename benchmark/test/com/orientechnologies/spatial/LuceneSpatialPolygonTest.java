/**
 * Copyright 2010-2016 OrientDB LTD (http://orientdb.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * For more information: http://www.orientdb.com
 */
package com.orientechnologies.spatial;


import com.orientechnologies.lucene.test.BaseLuceneTest;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Enrico Risa on 07/08/15.
 */
public class LuceneSpatialPolygonTest extends BaseSpatialLuceneTest {
    @Test
    public void testPolygonWithoutIndex() throws IOException {
        testIndexingPolygon();
        db.command(new OCommandSQL("drop index Place.location")).execute();
        queryPolygon();
    }

    @Test
    public void testIndexingPolygon() throws IOException {
        InputStream systemResourceAsStream = ClassLoader.getSystemResourceAsStream("germany.json");
        ODocument doc = new ODocument().fromJSON(systemResourceAsStream);
        Map geometry = doc.field("geometry");
        String type = ((String) (geometry.get("type")));
        ODocument location = new ODocument(("O" + type));
        location.field("coordinates", geometry.get("coordinates"));
        ODocument germany = new ODocument("Place");
        germany.field("name", "Germany");
        germany.field("location", location);
        db.save(germany);
        OIndex<?> index = db.getMetadata().getIndexManager().getIndex("Place.location");
        Assert.assertEquals(2, index.getSize());
        queryPolygon();
    }
}

