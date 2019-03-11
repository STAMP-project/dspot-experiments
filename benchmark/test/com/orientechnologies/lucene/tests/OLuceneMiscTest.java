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
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;


/**
 * Created by Enrico Risa on 18/09/15.
 */
public class OLuceneMiscTest extends OLuceneBaseTest {
    @Test
    public void testDoubleLucene() {
        db.command("create class Test extends V");
        db.command("create property Test.attr1 string");
        db.command("create index Test.attr1 on Test(attr1) FULLTEXT ENGINE LUCENE");
        db.command("create property Test.attr2 string");
        db.command("create index Test.attr2 on Test(attr2) FULLTEXT ENGINE LUCENE");
        db.command("insert into Test set attr1='foo', attr2='bar'");
        db.command("insert into Test set attr1='bar', attr2='foo'");
        OResultSet results = db.query("select from Test where search_index(\'Test.attr1\',\"foo*\") =true OR search_index(\'Test.attr2\', \"foo*\")=true  ");
        assertThat(results).hasSize(2);
        results.close();
        results = db.query("select from Test where SEARCH_FIELDS( ['attr1'], 'bar') = true OR SEARCH_FIELDS(['attr2'], 'bar*' )= true ");
        assertThat(results).hasSize(2);
        results.close();
        results = db.query("select from Test where SEARCH_FIELDS( ['attr1'], 'foo*') = true AND SEARCH_FIELDS(['attr2'], 'bar*') = true");
        assertThat(results).hasSize(1);
        results.close();
        results = db.query("select from Test where SEARCH_FIELDS( ['attr1'], 'bar*') = true AND SEARCH_FIELDS(['attr2'], 'foo*')= true");
        assertThat(results).hasSize(1);
        results.close();
    }

    @Test
    public void testSubLucene() {
        db.command("create class Person extends V");
        db.command("create property Person.name string");
        db.command("create index Person.name on Person(name) FULLTEXT ENGINE LUCENE");
        db.command("insert into Person set name='Enrico', age=18");
        String query = "select  from (select from Person where age = 18) where search_fields(['name'],'Enrico') = true";
        OResultSet results = db.query(query);
        assertThat(results).hasSize(1);
        results.close();
        // WITH PROJECTION it works using index directly
        query = "select  from (select name from Person where age = 18) where search_index('Person.name','Enrico') = true";
        results = db.query(query);
        assertThat(results).hasSize(1);
        results.close();
    }

    @Test
    public void testNamedParams() {
        db.command("create class Test extends V");
        db.command("create property Test.attr1 string");
        db.command("create index Test.attr1 on Test(attr1) FULLTEXT ENGINE LUCENE");
        db.command("insert into Test set attr1='foo', attr2='bar'");
        String query = "select from Test where  search_class( :name) =true";
        Map params = new HashMap();
        params.put("name", "FOO or");
        OResultSet results = db.command(query, params);
        assertThat(results).hasSize(1);
    }
}

