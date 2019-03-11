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
import com.orientechnologies.orient.core.sql.OCommandSQL;
import org.junit.Test;


/**
 * Created by Enrico Risa on 07/08/15.
 */
public class LuceneSpatialPointTest extends BaseSpatialLuceneTest {
    private static String PWKT = "POINT(-160.2075374 21.9029803)";

    @Test
    public void testPointWithoutIndex() {
        db.command(new OCommandSQL("Drop INDEX City.location")).execute();
        queryPoint();
    }

    @Test
    public void testIndexingPoint() {
        queryPoint();
    }

    @Test
    public void testOldNearQuery() {
        queryOldNear();
    }

    @Test
    public void testOldNearQueryWithoutIndex() {
        db.command(new OCommandSQL("Drop INDEX Place.l_lon")).execute();
        queryOldNear();
    }
}

