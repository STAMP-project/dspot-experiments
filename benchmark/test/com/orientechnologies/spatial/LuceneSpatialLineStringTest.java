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
import com.orientechnologies.orient.core.sql.OCommandSQL;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Enrico Risa on 07/08/15.
 */
public class LuceneSpatialLineStringTest extends BaseSpatialLuceneTest {
    public static String LINEWKT = "LINESTRING(-149.8871332 61.1484656,-149.8871655 61.1489556,-149.8871569 61.15043,-149.8870366 61.1517722)";

    @Test
    public void testLineStringWithoutIndex() throws IOException {
        db.command(new OCommandSQL("drop index Place.location")).execute();
        queryLineString();
    }

    @Test
    public void testIndexingLineString() throws IOException {
        OIndex<?> index = db.getMetadata().getIndexManager().getIndex("Place.location");
        // Assert.assertEquals(index.getSize(), 2);
        Assert.assertEquals(4, index.getSize());
        queryLineString();
    }
}

