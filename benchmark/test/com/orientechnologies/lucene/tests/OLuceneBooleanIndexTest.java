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


import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;


/**
 * Created by Enrico Risa on 29/04/15.
 */
public class OLuceneBooleanIndexTest extends OLuceneBaseTest {
    @Test
    public void shouldQueryBooleanField() {
        OResultSet docs = db.query("select from Person where search_class('false') = true");
        List<OResult> results = docs.stream().collect(Collectors.toList());
        assertThat(results).hasSize(500);
        assertThat(results.get(0).<Boolean>getProperty("isDeleted")).isFalse();
        docs.close();
        docs = db.query("select from Person where search_class('true') = true");
        results = docs.stream().collect(Collectors.toList());
        assertThat(results).hasSize(500);
        assertThat(results.get(0).<Boolean>getProperty("isDeleted")).isTrue();
        docs.close();
    }
}

