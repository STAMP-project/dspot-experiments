/**
 * *  Copyright 2015 OrientDB LTD (info(at)orientdb.com)
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *  *
 *  * For more information: http://orientdb.com
 */
package com.orientechnologies.orient.core.sql;


import OType.ANY;
import OType.BYTE;
import OType.STRING;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;


public class OCommandExecutorSQLSelectTest {
    static ODatabaseDocumentTx db;

    private static String DB_STORAGE = "memory";

    private static String DB_NAME = "OCommandExecutorSQLSelectTest";

    private static int ORDER_SKIP_LIMIT_ITEMS = 100 * 1000;

    @Test
    public void testUseIndexWithOrderBy2() throws Exception {
        long idxUsagesBefore = indexUsages(OCommandExecutorSQLSelectTest.db);
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select * from foo where address.city = 'NY' order by name ASC")).execute();
        Assert.assertEquals(qResult.size(), 1);
    }

    @Test
    public void testUseIndexWithOr() throws Exception {
        long idxUsagesBefore = indexUsages(OCommandExecutorSQLSelectTest.db);
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select * from foo where bar = 2 or name ='a' and bar >= 0")).execute();
        Assert.assertEquals(qResult.size(), 2);
        Assert.assertEquals(indexUsages(OCommandExecutorSQLSelectTest.db), (idxUsagesBefore + 2));
    }

    @Test
    public void testDoNotUseIndexWithOrNotIndexed() throws Exception {
        long idxUsagesBefore = indexUsages(OCommandExecutorSQLSelectTest.db);
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select * from foo where bar = 2 or notIndexed = 3")).execute();
        Assert.assertEquals(indexUsages(OCommandExecutorSQLSelectTest.db), idxUsagesBefore);
    }

    @Test
    public void testCompositeIndex() {
        long idxUsagesBefore = indexUsages(OCommandExecutorSQLSelectTest.db);
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select * from foo where comp = 'a' and osite = 1")).execute();
        Assert.assertEquals(qResult.size(), 1);
        Assert.assertEquals(indexUsages(OCommandExecutorSQLSelectTest.db), (idxUsagesBefore + 1));
    }

    @Test
    public void testProjection() {
        long idxUsagesBefore = indexUsages(OCommandExecutorSQLSelectTest.db);
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select a from foo where name = 'a' or bar = 1")).execute();
        Assert.assertEquals(qResult.size(), 1);
        Assert.assertEquals(indexUsages(OCommandExecutorSQLSelectTest.db), (idxUsagesBefore + 2));
    }

    @Test
    public void testProjection2() {
        long idxUsagesBefore = indexUsages(OCommandExecutorSQLSelectTest.db);
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select a from foo where name = 'a' or bar = 2")).execute();
        Assert.assertEquals(qResult.size(), 2);
        Assert.assertEquals(indexUsages(OCommandExecutorSQLSelectTest.db), (idxUsagesBefore + 2));
    }

    @Test
    public void testCompositeIndex2() {
        long idxUsagesBefore = indexUsages(OCommandExecutorSQLSelectTest.db);
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select * from foo where (comp = 'a' and osite = 1) or name = 'a'")).execute();
        Assert.assertEquals(qResult.size(), 2);
        Assert.assertEquals(indexUsages(OCommandExecutorSQLSelectTest.db), (idxUsagesBefore + 2));
    }

    @Test
    public void testOperatorPriority() {
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select * from foo where name ='a' and bar = 1000 or name = 'b'")).execute();
        List<ODocument> qResult2 = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select * from foo where name = 'b' or name ='a' and bar = 1000")).execute();
        List<ODocument> qResult3 = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select * from foo where name = 'b' or (name ='a' and bar = 1000)")).execute();
        List<ODocument> qResult4 = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select * from foo where (name ='a' and bar = 1000) or name = 'b'")).execute();
        List<ODocument> qResult5 = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select * from foo where ((name ='a' and bar = 1000) or name = 'b')")).execute();
        List<ODocument> qResult6 = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select * from foo where ((name ='a' and (bar = 1000)) or name = 'b')")).execute();
        List<ODocument> qResult7 = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select * from foo where (((name ='a' and bar = 1000)) or name = 'b')")).execute();
        List<ODocument> qResult8 = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select * from foo where (((name ='a' and bar = 1000)) or (name = 'b'))")).execute();
        Assert.assertEquals(qResult.size(), qResult2.size());
        Assert.assertEquals(qResult.size(), qResult3.size());
        Assert.assertEquals(qResult.size(), qResult4.size());
        Assert.assertEquals(qResult.size(), qResult5.size());
        Assert.assertEquals(qResult.size(), qResult6.size());
        Assert.assertEquals(qResult.size(), qResult7.size());
        Assert.assertEquals(qResult.size(), qResult8.size());
    }

    @Test
    public void testOperatorPriority2() {
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select * from bar where name ='a' and foo = 1 or name='b' or name='c' and foo = 3 and other = 4 or name = 'e' and foo = 5 or name = 'm' and foo > 2 ")).execute();
        List<ODocument> qResult2 = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select * from bar where (name ='a' and foo = 1) or name='b' or (name='c' and foo = 3 and other = 4) or (name = 'e' and foo = 5) or (name = 'm' and foo > 2)")).execute();
        List<ODocument> qResult3 = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select * from bar where (name ='a' and foo = 1) or (name='b') or (name='c' and foo = 3 and other = 4) or (name ='e' and foo = 5) or (name = 'm' and foo > 2)")).execute();
        List<ODocument> qResult4 = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select * from bar where (name ='a' and foo = 1) or ((name='b') or (name='c' and foo = 3 and other = 4)) or (name = 'e' and foo = 5) or (name = 'm' and foo > 2)")).execute();
        List<ODocument> qResult5 = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select * from bar where (name ='a' and foo = 1) or ((name='b') or (name='c' and foo = 3 and other = 4) or (name = 'e' and foo = 5)) or (name = 'm' and foo > 2)")).execute();
        Assert.assertEquals(qResult.size(), qResult2.size());
        Assert.assertEquals(qResult.size(), qResult3.size());
        Assert.assertEquals(qResult.size(), qResult4.size());
        Assert.assertEquals(qResult.size(), qResult5.size());
    }

    @Test
    public void testOperatorPriority3() {
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select * from bar where name <> 'a' and foo = 1 or name='b' or name='c' and foo = 3 and other = 4 or name = 'e' and foo = 5 or name = 'm' and foo > 2 ")).execute();
        List<ODocument> qResult2 = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select * from bar where (name <> 'a' and foo = 1) or name='b' or (name='c' and foo = 3 and other <>  4) or (name = 'e' and foo = 5) or (name = 'm' and foo > 2)")).execute();
        List<ODocument> qResult3 = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select * from bar where ( name <> 'a' and foo = 1) or (name='b') or (name='c' and foo = 3 and other <>  4) or (name ='e' and foo = 5) or (name = 'm' and foo > 2)")).execute();
        List<ODocument> qResult4 = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select * from bar where (name <> 'a' and foo = 1) or ( (name='b') or (name='c' and foo = 3 and other <>  4)) or  (name = 'e' and foo = 5) or (name = 'm' and foo > 2)")).execute();
        List<ODocument> qResult5 = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select * from bar where (name <> 'a' and foo = 1) or ((name='b') or (name='c' and foo = 3 and other <>  4) or (name = 'e' and foo = 5)) or (name = 'm' and foo > 2)")).execute();
        Assert.assertEquals(qResult.size(), qResult2.size());
        Assert.assertEquals(qResult.size(), qResult3.size());
        Assert.assertEquals(qResult.size(), qResult4.size());
        Assert.assertEquals(qResult.size(), qResult5.size());
    }

    @Test
    public void testExpandOnEmbedded() {
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select expand(address) from foo where name = 'a'")).execute();
        Assert.assertEquals(qResult.size(), 1);
        Assert.assertEquals(qResult.get(0).field("city"), "NY");
    }

    @Test
    public void testFlattenOnEmbedded() {
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select flatten(address) from foo where name = 'a'")).execute();
        Assert.assertEquals(qResult.size(), 1);
        Assert.assertEquals(qResult.get(0).field("city"), "NY");
    }

    @Test
    public void testLimit() {
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select from foo limit 3")).execute();
        Assert.assertEquals(qResult.size(), 3);
    }

    @Test
    public void testLimitWithMetadataQuery() {
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select expand(classes) from metadata:schema limit 3")).execute();
        Assert.assertEquals(qResult.size(), 3);
    }

    @Test
    public void testOrderByWithMetadataQuery() {
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select expand(classes) from metadata:schema order by name")).execute();
        Assert.assertTrue(((qResult.size()) > 0));
    }

    @Test
    public void testLimitWithUnnamedParam() {
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select from foo limit ?")).execute(3);
        Assert.assertEquals(qResult.size(), 3);
    }

    @Test
    public void testLimitWithNamedParam() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("lim", 2);
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select from foo limit :lim")).execute(params);
        Assert.assertEquals(qResult.size(), 2);
    }

    @Test
    public void testLimitWithNamedParam2() {
        // issue #5493
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("limit", 2);
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select from foo limit :limit")).execute(params);
        Assert.assertEquals(qResult.size(), 2);
    }

    @Test
    public void testParamsInLetSubquery() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "foo");
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select from TestParams let $foo = (select name from TestParams where surname = :name) where surname in $foo.name ")).execute(params);
        Assert.assertEquals(qResult.size(), 1);
    }

    @Test
    public void testBooleanParams() {
        // issue #4224
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select name from TestParams where name = ? and active = ?")).execute("foo", true);
        Assert.assertEquals(qResult.size(), 1);
    }

    @Test
    public void testFromInSquareBrackets() {
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select tags[' from '] as a from TestFromInSquare")).execute();
        Assert.assertEquals(qResult.size(), 1);
        Assert.assertEquals(qResult.get(0).field("a"), "foo");
    }

    @Test
    public void testNewline() {
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select\n1 as ACTIVE\nFROM foo")).execute();
        Assert.assertEquals(qResult.size(), 5);
    }

    @Test
    public void testOrderByRid() {
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select from ridsorttest order by @rid ASC")).execute();
        Assert.assertTrue(((qResult.size()) > 0));
        ODocument prev = qResult.get(0);
        for (int i = 1; i < (qResult.size()); i++) {
            Assert.assertTrue(((prev.getIdentity().compareTo(qResult.get(i).getIdentity())) <= 0));
            prev = qResult.get(i);
        }
        qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select from ridsorttest order by @rid DESC")).execute();
        Assert.assertTrue(((qResult.size()) > 0));
        prev = qResult.get(0);
        for (int i = 1; i < (qResult.size()); i++) {
            Assert.assertTrue(((prev.getIdentity().compareTo(qResult.get(i).getIdentity())) >= 0));
            prev = qResult.get(i);
        }
        qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select from ridsorttest where name > 3 order by @rid DESC")).execute();
        Assert.assertTrue(((qResult.size()) > 0));
        prev = qResult.get(0);
        for (int i = 1; i < (qResult.size()); i++) {
            Assert.assertTrue(((prev.getIdentity().compareTo(qResult.get(i).getIdentity())) >= 0));
            prev = qResult.get(i);
        }
    }

    @Test
    public void testUnwind() {
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select from unwindtest unwind coll")).execute();
        Assert.assertEquals(qResult.size(), 4);
        for (ODocument doc : qResult) {
            String name = doc.field("name");
            String coll = doc.field("coll");
            Assert.assertTrue(coll.startsWith(name));
            Assert.assertFalse(doc.getIdentity().isPersistent());
        }
    }

    @Test
    public void testUnwind2() {
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select from unwindtest2 unwind coll")).execute();
        Assert.assertEquals(qResult.size(), 1);
        for (ODocument doc : qResult) {
            String name = doc.field("name");
            Object coll = doc.field("coll");
            Assert.assertNull(coll);
            Assert.assertFalse(doc.getIdentity().isPersistent());
        }
    }

    @Test
    public void testUnwindOrder() {
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select from unwindtest order by coll unwind coll")).execute();
        Assert.assertEquals(qResult.size(), 4);
        for (ODocument doc : qResult) {
            String name = doc.field("name");
            String coll = doc.field("coll");
            Assert.assertTrue(coll.startsWith(name));
            Assert.assertFalse(doc.getIdentity().isPersistent());
        }
    }

    @Test
    public void testUnwindSkip() {
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select from unwindtest unwind coll skip 1")).execute();
        Assert.assertEquals(qResult.size(), 3);
        for (ODocument doc : qResult) {
            String name = doc.field("name");
            String coll = doc.field("coll");
            Assert.assertTrue(coll.startsWith(name));
        }
    }

    @Test
    public void testUnwindLimit() {
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select from unwindtest unwind coll limit 1")).execute();
        Assert.assertEquals(qResult.size(), 1);
        for (ODocument doc : qResult) {
            String name = doc.field("name");
            String coll = doc.field("coll");
            Assert.assertTrue(coll.startsWith(name));
        }
    }

    @Test
    public void testUnwindLimit3() {
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select from unwindtest unwind coll limit 3")).execute();
        Assert.assertEquals(qResult.size(), 3);
        for (ODocument doc : qResult) {
            String name = doc.field("name");
            String coll = doc.field("coll");
            Assert.assertTrue(coll.startsWith(name));
        }
    }

    @Test
    public void testUnwindSkipAndLimit() {
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select from unwindtest unwind coll skip 1 limit 1")).execute();
        Assert.assertEquals(qResult.size(), 1);
        for (ODocument doc : qResult) {
            String name = doc.field("name");
            String coll = doc.field("coll");
            Assert.assertTrue(coll.startsWith(name));
        }
    }

    @Test
    public void testMultipleClusters() {
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select from cluster:[testmultipleclusters1]")).execute();
        Assert.assertEquals(qResult.size(), 1);
        qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select from cluster:[testmultipleclusters1, testmultipleclusters2]")).execute();
        Assert.assertEquals(qResult.size(), 2);
    }

    @Test
    public void testMatches() {
        List<?> result = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<Object>("select from foo where name matches \'(?i)(^\\\\Qa\\\\E$)|(^\\\\Qname2\\\\E$)|(^\\\\Qname3\\\\E$)\' and bar = 1"));
        Assert.assertEquals(result.size(), 1);
    }

    @Test
    public void testStarPosition() {
        List<ODocument> result = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<Object>("select *, name as blabla from foo where name = 'a'"));
        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals(result.get(0).field("blabla"), "a");
        result = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<Object>("select name as blabla, * from foo where name = 'a'"));
        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals(result.get(0).field("blabla"), "a");
        result = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<Object>("select name as blabla, *, fff as zzz from foo where name = 'a'"));
        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals(result.get(0).field("blabla"), "a");
    }

    @Test
    public void testQuotedClassName() {
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select from `edge`")).execute();
        Assert.assertEquals(qResult.size(), 0);
    }

    @Test
    public void testUnwindSkipAndLimit2() {
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select from unwindtest unwind coll skip 1 limit 2")).execute();
        Assert.assertEquals(qResult.size(), 2);
        for (ODocument doc : qResult) {
            String name = doc.field("name");
            String coll = doc.field("coll");
            Assert.assertTrue(coll.startsWith(name));
        }
    }

    @Test
    public void testMultipleParamsWithSameName() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("param1", "foo");
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select from TestParams where name like '%' + :param1 + '%'")).execute(params);
        Assert.assertEquals(qResult.size(), 2);
        qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select from TestParams where name like '%' + :param1 + '%' and surname like '%' + :param1 + '%'")).execute(params);
        Assert.assertEquals(qResult.size(), 1);
        params = new HashMap<String, Object>();
        params.put("param1", "bar");
        qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select from TestParams where surname like '%' + :param1 + '%'")).execute(params);
        Assert.assertEquals(qResult.size(), 1);
    }

    // /*** from issue #2743
    @Test
    public void testBasicQueryOrdered() {
        com.orientechnologies.orient.core.sql.query.OSQLSynchQuery sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("SELECT from alphabet ORDER BY letter");
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(26, results.size());
    }

    @Test
    public void testSkipZeroOrdered() {
        com.orientechnologies.orient.core.sql.query.OSQLSynchQuery sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("SELECT from alphabet ORDER BY letter SKIP 0");
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(26, results.size());
    }

    @Test
    public void testSkipOrdered() {
        com.orientechnologies.orient.core.sql.query.OSQLSynchQuery sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("SELECT from alphabet ORDER BY letter SKIP 7");
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(19, results.size());// FAILURE - actual 0

    }

    @Test
    public void testLimitOrdered() {
        com.orientechnologies.orient.core.sql.query.OSQLSynchQuery sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("SELECT from alphabet ORDER BY letter LIMIT 9");
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(9, results.size());
    }

    @Test
    public void testLimitMinusOneOrdered() {
        com.orientechnologies.orient.core.sql.query.OSQLSynchQuery sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("SELECT from alphabet ORDER BY letter LIMIT -1");
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(26, results.size());
    }

    @Test
    public void testSkipAndLimitOrdered() {
        com.orientechnologies.orient.core.sql.query.OSQLSynchQuery sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("SELECT from alphabet ORDER BY letter SKIP 7 LIMIT 9");
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(9, results.size());
    }

    @Test
    public void testSkipAndLimitMinusOneOrdered() {
        com.orientechnologies.orient.core.sql.query.OSQLSynchQuery sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("SELECT from alphabet ORDER BY letter SKIP 7 LIMIT -1");
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(19, results.size());
    }

    @Test
    public void testLetAsListAsString() {
        com.orientechnologies.orient.core.sql.query.OSQLSynchQuery sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("SELECT $ll as lll from unwindtest let $ll = coll.asList().asString() where name = 'bar'");
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(1, results.size());
        Assert.assertNotNull(results.get(0).field("lll"));
        Assert.assertEquals("[bar1, bar2]", results.get(0).field("lll"));
    }

    @Test
    public void testAggregations() {
        com.orientechnologies.orient.core.sql.query.OSQLSynchQuery sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("select data.size as collection_content, data.size() as collection_size, min(data.size) as collection_min, max(data.size) as collection_max, sum(data.size) as collection_sum, avg(data.size) as collection_avg from OCommandExecutorSQLSelectTest_aggregations");
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(1, results.size());
        ODocument doc = results.get(0);
        Assert.assertThat(doc.<Integer>field("collection_size")).isEqualTo(5);
        Assert.assertThat(doc.<Integer>field("collection_sum")).isEqualTo(130);
        Assert.assertThat(doc.<Integer>field("collection_avg")).isEqualTo(26);
        Assert.assertThat(doc.<Integer>field("collection_min")).isEqualTo(0);
        Assert.assertThat(doc.<Integer>field("collection_max")).isEqualTo(50);
        // 
        // assertEquals(5, doc.field("collection_size"));
        // assertEquals(130, doc.field("collection_sum"));
        // assertEquals(26, doc.field("collection_avg"));
        // assertEquals(0, doc.field("collection_min"));
        // assertEquals(50, doc.field("collection_max"));
    }

    @Test
    public void testLetOrder() {
        com.orientechnologies.orient.core.sql.query.OSQLSynchQuery sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery(("SELECT" + ((((((((((((("      source," + "  $maxYear as maxYear") + "              FROM") + "      (") + "          SELECT expand( $union ) ") + "  LET") + "      $a = (SELECT 'A' as source, 2013 as year),") + "  $b = (SELECT 'B' as source, 2012 as year),") + "  $union = unionAll($a,$b) ") + "  ) ") + "  LET ") + "      $maxYear = max(year)") + "  GROUP BY") + "  source")));
        try {
            List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(sql);
            Assert.fail("Invalid query, usage of LET, aggregate functions and GROUP BY together is not supported");
        } catch (OCommandSQLParsingException x) {
        }
    }

    @Test
    public void testNullProjection() {
        com.orientechnologies.orient.core.sql.query.OSQLSynchQuery sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("SELECT 1 AS integer, 'Test' AS string, NULL AS nothing, [] AS array, {} AS object");
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(results.size(), 1);
        ODocument doc = results.get(0);
        Assert.assertThat(doc.<Integer>field("integer")).isEqualTo(1);
        Assert.assertEquals(doc.field("string"), "Test");
        Assert.assertNull(doc.field("nothing"));
        boolean nullFound = false;
        for (String s : doc.fieldNames()) {
            if (s.equals("nothing")) {
                nullFound = true;
                break;
            }
        }
        Assert.assertTrue(nullFound);
    }

    @Test
    public void testExpandSkipLimit() {
        // issue #4985
        com.orientechnologies.orient.core.sql.query.OSQLSynchQuery sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("SELECT expand(linked) from ExpandSkipLimit where parent = true order by nnum skip 1 limit 1");
        List<OIdentifiable> results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(results.size(), 1);
        ODocument doc = results.get(0).getRecord();
        // assertEquals(doc.field("nnum"), 1);
        Assert.assertThat(doc.<Integer>field("nnum")).isEqualTo(1);
    }

    @Test
    public void testBacktick() {
        com.orientechnologies.orient.core.sql.query.OSQLSynchQuery sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("SELECT `foo-bar` as r from TestBacktick");
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(results.size(), 1);
        ODocument doc = results.get(0);
        // assertEquals(doc.field("r"), 10);
        Assert.assertThat(doc.<Integer>field("r")).isEqualTo(10);
    }

    @Test
    public void testOrderByEmbeddedParams() {
        // issue #4949
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("paramvalue", "count");
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select from TestParamsEmbedded order by emb[:paramvalue] DESC")).execute(parameters);
        Assert.assertEquals(qResult.size(), 2);
        Map embedded = qResult.get(0).field("emb");
        Assert.assertEquals(embedded.get("count"), 1);
    }

    @Test
    public void testOrderByEmbeddedParams2() {
        // issue #4949
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("paramvalue", "count");
        List<ODocument> qResult = OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("select from TestParamsEmbedded order by emb[:paramvalue] ASC")).execute(parameters);
        Assert.assertEquals(qResult.size(), 2);
        Map embedded = qResult.get(0).field("emb");
        Assert.assertEquals(embedded.get("count"), 0);
    }

    @Test
    public void testMassiveOrderAscSkipLimit() {
        int skip = 1000;
        com.orientechnologies.orient.core.sql.query.OSQLSynchQuery sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery((("SELECT from MassiveOrderSkipLimit order by nnum asc skip " + skip) + " limit 5"));
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(results.size(), 5);
        for (int i = 0; i < (results.size()); i++) {
            ODocument doc = results.get(i);
            // assertEquals(doc.field("nnum"), skip + i);
            Assert.assertThat(doc.<Integer>field("nnum")).isEqualTo((skip + i));
        }
    }

    @Test
    public void testMassiveOrderDescSkipLimit() {
        int skip = 1000;
        com.orientechnologies.orient.core.sql.query.OSQLSynchQuery sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery((("SELECT from MassiveOrderSkipLimit order by nnum desc skip " + skip) + " limit 5"));
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(results.size(), 5);
        for (int i = 0; i < (results.size()); i++) {
            ODocument doc = results.get(i);
            // assertEquals(doc.field("nnum"), ORDER_SKIP_LIMIT_ITEMS - 1 - skip - i);
            Assert.assertThat(doc.<Integer>field("nnum")).isEqualTo(((((OCommandExecutorSQLSelectTest.ORDER_SKIP_LIMIT_ITEMS) - 1) - skip) - i));
        }
    }

    @Test
    public void testIntersectExpandLet() {
        // issue #5121
        com.orientechnologies.orient.core.sql.query.OSQLSynchQuery sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery(("select expand(intersect($q1, $q2)) " + ("let $q1 = (select from OUser where name ='admin')," + "$q2 = (select from OUser where name ='admin')")));
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(results.size(), 1);
        for (int i = 0; i < (results.size()); i++) {
            ODocument doc = results.get(i);
            Assert.assertEquals(doc.field("name"), "admin");
        }
    }

    @Test
    public void testDatesListContainsString() {
        // issue #3526
        com.orientechnologies.orient.core.sql.query.OSQLSynchQuery sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("select from OCommandExecutorSQLSelectTest_datesSet where foo contains '2015-10-21'");
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(results.size(), 1);
    }

    @Test
    public void testParamWithMatches() {
        // issue #5229
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("param1", "adm.*");
        com.orientechnologies.orient.core.sql.query.OSQLSynchQuery sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("select from OUser where name matches :param1");
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(sql, params);
        Assert.assertEquals(results.size(), 1);
    }

    @Test
    public void testParamWithMatchesQuoteRegex() {
        // issue #5229
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("param1", ".*admin[name].*");// will not work

        com.orientechnologies.orient.core.sql.query.OSQLSynchQuery sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("select from matchesstuff where name matches :param1");
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(sql, params);
        Assert.assertEquals(results.size(), 0);
        params.put("param1", ((Pattern.quote("admin[name]")) + ".*"));// should work

        results = OCommandExecutorSQLSelectTest.db.query(sql, params);
        Assert.assertEquals(results.size(), 1);
    }

    @Test
    public void testMatchesWithQuotes() {
        // issue #5229
        String pattern = (Pattern.quote("adm")) + ".*";
        com.orientechnologies.orient.core.sql.query.OSQLSynchQuery sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("SELECT FROM matchesstuff WHERE (name matches ?)");
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(sql, pattern);
        Assert.assertEquals(results.size(), 1);
    }

    @Test
    public void testMatchesWithQuotes2() {
        // issue #5229
        com.orientechnologies.orient.core.sql.query.OSQLSynchQuery sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("SELECT FROM matchesstuff WHERE (name matches \'\\\\Qadm\\\\E.*\' and not ( name matches \'(.*)foo(.*)\' ) )");
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(results.size(), 1);
    }

    @Test
    public void testMatchesWithQuotes3() {
        // issue #5229
        com.orientechnologies.orient.core.sql.query.OSQLSynchQuery sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("SELECT FROM matchesstuff WHERE (name matches \'\\\\Qadm\\\\E.*\' and  ( name matches \'\\\\Qadmin\\\\E.*\' ) )");
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(results.size(), 1);
    }

    @Test
    public void testParamWithMatchesAndNot() {
        // issue #5229
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("param1", "adm.*");
        params.put("param2", "foo.*");
        com.orientechnologies.orient.core.sql.query.OSQLSynchQuery sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("select from OUser where (name matches :param1 and not (name matches :param2))");
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(sql, params);
        Assert.assertEquals(results.size(), 1);
        params.put("param1", ((Pattern.quote("adm")) + ".*"));
        results = OCommandExecutorSQLSelectTest.db.query(sql, params);
        Assert.assertEquals(results.size(), 1);
    }

    @Test
    public void testDistinctLimit() {
        com.orientechnologies.orient.core.sql.query.OSQLSynchQuery sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("select distinct(name) from DistinctLimit limit 1");
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(results.size(), 1);
        sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("select distinct(name) from DistinctLimit limit 2");
        results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(results.size(), 2);
        sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("select distinct(name) from DistinctLimit limit 3");
        results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(results.size(), 2);
        sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("select distinct(name) from DistinctLimit limit -1");
        results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(results.size(), 2);
    }

    @Test
    public void testSelectFromClusterNumber() {
        OClass clazz = OCommandExecutorSQLSelectTest.db.getMetadata().getSchema().getClass("DistinctLimit");
        int clusterId = clazz.getClusterIds()[0];
        com.orientechnologies.orient.core.sql.query.OSQLSynchQuery sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery((("select from cluster:" + clusterId) + " limit 1"));
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(results.size(), 1);
    }

    @Test
    public void testLinkListSequence1() {
        com.orientechnologies.orient.core.sql.query.OSQLSynchQuery sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("select expand(children.children.children) from LinkListSequence where name = 'root'");
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(results.size(), 4);
        for (ODocument result : results) {
            String value = result.field("name");
            Assert.assertEquals(value.length(), 5);
        }
    }

    @Test
    public void testLinkListSequence2() {
        com.orientechnologies.orient.core.sql.query.OSQLSynchQuery sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("select expand(children[0].children.children) from LinkListSequence where name = 'root'");
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(results.size(), 4);
        for (ODocument result : results) {
            String value = result.field("name");
            Assert.assertEquals(value.length(), 5);
        }
    }

    @Test
    public void testLinkListSequence3() {
        com.orientechnologies.orient.core.sql.query.OSQLSynchQuery sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("select expand(children[0].children[0].children) from LinkListSequence where name = 'root'");
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(results.size(), 2);
        for (ODocument result : results) {
            String value = result.field("name");
            Assert.assertTrue(((value.equals("1.1.1")) || (value.equals("1.1.2"))));
        }
    }

    @Test
    public void testMaxLongNumber() {
        // issue #5664
        com.orientechnologies.orient.core.sql.query.OSQLSynchQuery sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("select from MaxLongNumberTest WHERE last < 10 OR last is null");
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(results.size(), 3);
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("update MaxLongNumberTest set last = max(91,ifnull(last,0))")).execute();
        sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("select from MaxLongNumberTest WHERE last < 10 OR last is null");
        results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(results.size(), 0);
    }

    @Test
    public void testFilterAndOrderBy() {
        // issue http://www.prjhub.com/#/issues/6199
        com.orientechnologies.orient.core.sql.query.OSQLSynchQuery sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("SELECT FROM FilterAndOrderByTest WHERE active = true ORDER BY dc DESC");
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(results.size(), 3);
        Calendar cal = new GregorianCalendar();
        Date date = results.get(0).field("dc");
        cal.setTime(date);
        Assert.assertEquals(cal.get(Calendar.YEAR), 2016);
        date = results.get(1).field("dc");
        cal.setTime(date);
        Assert.assertEquals(cal.get(Calendar.YEAR), 2010);
        date = results.get(2).field("dc");
        cal.setTime(date);
        Assert.assertEquals(cal.get(Calendar.YEAR), 2009);
    }

    @Test
    public void testComplexFilterInSquareBrackets() {
        // issues #513 #5451
        com.orientechnologies.orient.core.sql.query.OSQLSynchQuery sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("SELECT expand(collection[name = 'n1']) FROM ComplexFilterInSquareBrackets2");
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(results.size(), 1);
        Assert.assertEquals(results.iterator().next().field("name"), "n1");
        sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("SELECT expand(collection[name = 'n1' and value = 1]) FROM ComplexFilterInSquareBrackets2");
        results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(results.size(), 1);
        Assert.assertEquals(results.iterator().next().field("name"), "n1");
        sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("SELECT expand(collection[name = 'n1' and value > 1]) FROM ComplexFilterInSquareBrackets2");
        results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(results.size(), 0);
        sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("SELECT expand(collection[name = 'n1' or value = -1]) FROM ComplexFilterInSquareBrackets2");
        results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(results.size(), 2);
        for (ODocument doc : results) {
            Assert.assertTrue(((doc.field("name").equals("n1")) || (doc.field("value").equals((-1)))));
        }
        sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("SELECT expand(collection[name = 'n1' and not value = 1]) FROM ComplexFilterInSquareBrackets2");
        results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(results.size(), 0);
        sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("SELECT expand(collection[value < 0]) FROM ComplexFilterInSquareBrackets2");
        results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(results.size(), 1);
        // assertEquals(results.iterator().next().field("value"), -1);
        Assert.assertThat(results.iterator().next().<Integer>field("value")).isEqualTo((-1));
        sql = new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("SELECT expand(collection[2]) FROM ComplexFilterInSquareBrackets2");
        results = OCommandExecutorSQLSelectTest.db.query(sql);
        Assert.assertEquals(results.size(), 1);
    }

    @Test
    public void testCollateOnCollections() {
        // issue #4851
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("create class OCommandExecutorSqlSelectTest_collateOnCollections")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("create property OCommandExecutorSqlSelectTest_collateOnCollections.categories EMBEDDEDLIST string")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("insert into OCommandExecutorSqlSelectTest_collateOnCollections set categories=['a','b']")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("alter property OCommandExecutorSqlSelectTest_collateOnCollections.categories COLLATE ci")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("insert into OCommandExecutorSqlSelectTest_collateOnCollections set categories=['Math','English']")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("insert into OCommandExecutorSqlSelectTest_collateOnCollections set categories=['a','b','c']")).execute();
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select from OCommandExecutorSqlSelectTest_collateOnCollections where 'Math' in categories"));
        Assert.assertEquals(results.size(), 1);
        results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select from OCommandExecutorSqlSelectTest_collateOnCollections where 'math' in categories"));
        Assert.assertEquals(results.size(), 1);
    }

    @Test
    public void testCountUniqueIndex() {
        // issue http://www.prjhub.com/#/issues/6419
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("create class OCommandExecutorSqlSelectTest_testCountUniqueIndex")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("create property OCommandExecutorSqlSelectTest_testCountUniqueIndex.AAA String")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("create index OCommandExecutorSqlSelectTest_testCountUniqueIndex.AAA unique")).execute();
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select count(*) from OCommandExecutorSqlSelectTest_testCountUniqueIndex where AAA='missing'"));
        Assert.assertEquals(results.size(), 1);
        // assertEquals(results.iterator().next().field("count"), 0l);
        Assert.assertThat(results.iterator().next().<Long>field("count")).isEqualTo(0L);
    }

    @Test
    public void testEvalLong() {
        // http://www.prjhub.com/#/issues/6472
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("SELECT EVAL(\"86400000 * 26\") AS value"));
        Assert.assertEquals(results.size(), 1);
        // assertEquals(results.get(0).field("value"), 86400000l * 26);
        Assert.assertThat(results.get(0).<Long>field("value")).isEqualTo((86400000L * 26));
    }

    @Test
    public void testCollateOnLinked() {
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select from CollateOnLinked2 where linked.name = 'foo' "));
        Assert.assertEquals(results.size(), 1);
        results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select from CollateOnLinked2 where linked.name = 'FOO' "));
        Assert.assertEquals(results.size(), 1);
    }

    @Test
    public void testParamConcat() {
        // issue #6049
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select from TestParams where surname like ? + '%'"), "fo");
        Assert.assertEquals(results.size(), 1);
    }

    @Test
    public void testCompositeIndexWithoutNullValues() {
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("create class CompositeIndexWithoutNullValues")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("create property CompositeIndexWithoutNullValues.one String")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("create property CompositeIndexWithoutNullValues.two String")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("create index CompositeIndexWithoutNullValues.one_two on CompositeIndexWithoutNullValues (one, two) NOTUNIQUE METADATA {ignoreNullValues: true}")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("insert into CompositeIndexWithoutNullValues set one = 'foo'")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("insert into CompositeIndexWithoutNullValues set one = 'foo', two = 'bar'")).execute();
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select from CompositeIndexWithoutNullValues where one = ?"), "foo");
        Assert.assertEquals(results.size(), 2);
        results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select from CompositeIndexWithoutNullValues where one = ? and two = ?"), "foo", "bar");
        Assert.assertEquals(results.size(), 1);
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("create class CompositeIndexWithoutNullValues2")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("create property CompositeIndexWithoutNullValues2.one String")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("create property CompositeIndexWithoutNullValues2.two String")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("create index CompositeIndexWithoutNullValues2.one_two on CompositeIndexWithoutNullValues2 (one, two) NOTUNIQUE METADATA {ignoreNullValues: false}")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("insert into CompositeIndexWithoutNullValues2 set one = 'foo'")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("insert into CompositeIndexWithoutNullValues2 set one = 'foo', two = 'bar'")).execute();
        results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select from CompositeIndexWithoutNullValues2 where one = ?"), "foo");
        Assert.assertEquals(results.size(), 2);
        results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select from CompositeIndexWithoutNullValues where one = ? and two = ?"), "foo", "bar");
        Assert.assertEquals(results.size(), 1);
    }

    @Test
    public void testDateFormat() {
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select date('2015-07-20', 'yyyy-MM-dd').format('dd.MM.yyyy') as dd"));
        Assert.assertEquals(results.size(), 1);
        Assert.assertEquals(results.get(0).field("dd"), "20.07.2015");
    }

    @Test
    public void testConcatenateNamedParams() {
        // issue #5572
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select from TestMultipleClusters where name like :p1 + '%'"), "fo");
        Assert.assertEquals(results.size(), 1);
        results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select from TestMultipleClusters where name like :p1 "), "fo");
        Assert.assertEquals(results.size(), 0);
    }

    @Test
    public void testMethodsOnStrings() {
        // issue #5671
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select '1'.asLong() as long"));
        Assert.assertEquals(results.size(), 1);
        // assertEquals(results.get(0).field("long"), 1L);
        Assert.assertThat(results.get(0).<Long>field("long")).isEqualTo(1L);
    }

    @Test
    public void testDifferenceOfInlineCollections() {
        // issue #5294
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select difference([1,2,3],[1,2]) as difference"));
        Assert.assertEquals(results.size(), 1);
        Object differenceFieldValue = results.get(0).field("difference");
        Assert.assertTrue((differenceFieldValue instanceof Collection));
        Assert.assertEquals(((Collection) (differenceFieldValue)).size(), 1);
        Assert.assertEquals(((Collection) (differenceFieldValue)).iterator().next(), 3);
    }

    @Test
    public void testFoo() {
        // dispose it!
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("create class testFoo")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("insert into testFoo set val = 1, name = 'foo'")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("insert into testFoo set val = 3, name = 'foo'")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("insert into testFoo set val = 5, name = 'bar'")).execute();
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select sum(val), name from testFoo group by name"));
        Assert.assertEquals(results.size(), 2);
    }

    @Test
    public void testDateComparison() {
        // issue #6389
        byte[] array = new byte[]{ 1, 4, 5, 74, 3, 45, 6, 127, -120, 2 };
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("create class TestDateComparison")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("create property TestDateComparison.dateProp DATE")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("insert into TestDateComparison set dateProp = '2016-05-01'")).execute();
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("SELECT from TestDateComparison WHERE dateProp >= '2016-05-01'"));
        Assert.assertEquals(results.size(), 1);
        results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("SELECT from TestDateComparison WHERE dateProp <= '2016-05-01'"));
        Assert.assertEquals(results.size(), 1);
    }

    // <<<<<<< HEAD
    // =======
    @Test
    public void testOrderByRidDescMultiCluster() {
        // issue #6694
        OClass clazz = OCommandExecutorSQLSelectTest.db.getMetadata().getSchema().createClass("TestOrderByRidDescMultiCluster");
        if ((clazz.getClusterIds().length) < 2) {
            clazz.addCluster("TestOrderByRidDescMultiCluster_11111");
        }
        for (int i = 0; i < 100; i++) {
            OCommandExecutorSQLSelectTest.db.command(new OCommandSQL(("insert into TestOrderByRidDescMultiCluster set foo = " + i))).execute();
        }
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("SELECT from TestOrderByRidDescMultiCluster order by @rid desc"));
        Assert.assertEquals(results.size(), 100);
        ODocument lastDoc = null;
        for (ODocument doc : results) {
            if (lastDoc != null) {
                Assert.assertTrue(((doc.getIdentity().compareTo(lastDoc.getIdentity())) < 0));
            }
            lastDoc = doc;
        }
        results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("SELECT from TestOrderByRidDescMultiCluster order by @rid asc"));
        Assert.assertEquals(results.size(), 100);
        lastDoc = null;
        for (ODocument doc : results) {
            if (lastDoc != null) {
                Assert.assertTrue(((doc.getIdentity().compareTo(lastDoc.getIdentity())) > 0));
            }
            lastDoc = doc;
        }
    }

    @Test
    public void testCountOnSubclassIndexes() {
        // issue #6737
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("create class testCountOnSubclassIndexes_superclass")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("create property testCountOnSubclassIndexes_superclass.foo boolean")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("create index testCountOnSubclassIndexes_superclass.foo on testCountOnSubclassIndexes_superclass (foo) notunique")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("create class testCountOnSubclassIndexes_sub1 extends testCountOnSubclassIndexes_superclass")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("create index testCountOnSubclassIndexes_sub1.foo on testCountOnSubclassIndexes_sub1 (foo) notunique")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("create class testCountOnSubclassIndexes_sub2 extends testCountOnSubclassIndexes_superclass")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("create index testCountOnSubclassIndexes_sub2.foo on testCountOnSubclassIndexes_sub2 (foo) notunique")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("insert into testCountOnSubclassIndexes_sub1 set name = 'a', foo = true")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("insert into testCountOnSubclassIndexes_sub1 set name = 'b', foo = false")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("insert into testCountOnSubclassIndexes_sub2 set name = 'c', foo = true")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("insert into testCountOnSubclassIndexes_sub2 set name = 'd', foo = true")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("insert into testCountOnSubclassIndexes_sub2 set name = 'e', foo = false")).execute();
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("SELECT count(*) from testCountOnSubclassIndexes_sub1 where foo = true"));
        Assert.assertEquals(results.size(), 1);
        Assert.assertEquals(results.get(0).field("count"), ((Object) (1L)));
        results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("SELECT count(*) from testCountOnSubclassIndexes_sub2 where foo = true"));
        Assert.assertEquals(results.size(), 1);
        Assert.assertEquals(results.get(0).field("count"), ((Object) (2L)));
        results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("SELECT count(*) from testCountOnSubclassIndexes_superclass where foo = true"));
        Assert.assertEquals(results.size(), 1);
        Assert.assertEquals(results.get(0).field("count"), ((Object) (3L)));
    }

    @Test
    public void testDoubleExponentNotation() {
        // issue #7013
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select 1e-2 as a"));
        Assert.assertEquals(results.size(), 1);
        Assert.assertEquals(results.get(0).field("a"), ((Object) (0.01)));
    }

    @Test
    public void testConvertDouble() {
        // issue #7234
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("create class testConvertDouble")).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL("insert into testConvertDouble set num = 100000")).execute();
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("SELECT FROM testConvertDouble WHERE num >= 50000 AND num <=300000000"));
        Assert.assertEquals(results.size(), 1);
    }

    @Test
    public void testFilterListsOfMaps() {
        String className = "testFilterListaOfMaps";
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL(("create class " + className))).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL((("create property " + className) + ".tagz embeddedmap"))).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL((("insert into " + className) + " set tagz = {}"))).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL((("update " + className) + " SET tagz.foo = [{name:'a', surname:'b'}, {name:'c', surname:'d'}]"))).execute();
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>(("select tagz.values()[0][name = 'a'] as t from " + className)));
        Assert.assertEquals(results.size(), 1);
        Map map = results.get(0).field("t");
        Assert.assertEquals(map.get("surname"), "b");
    }

    @Test
    public void testComparisonOfShorts() {
        // issue #7578
        String className = "testComparisonOfShorts";
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL(("create class " + className))).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL((("create property " + className) + ".state Short"))).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL((("INSERT INTO " + className) + " set state = 1"))).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL((("INSERT INTO " + className) + " set state = 1"))).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL((("INSERT INTO " + className) + " set state = 2"))).execute();
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>((("select from " + className) + " where state in [1]")));
        Assert.assertEquals(results.size(), 2);
        results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>((("select from " + className) + " where [1] contains state")));
        Assert.assertEquals(results.size(), 2);
    }

    @Test
    public void testEnumAsParams() {
        // issue #7418
        String className = "testEnumAsParams";
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL(("create class " + className))).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL((("INSERT INTO " + className) + " set status = ?"))).execute(STRING);
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL((("INSERT INTO " + className) + " set status = ?"))).execute(ANY);
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL((("INSERT INTO " + className) + " set status = ?"))).execute(BYTE);
        Map<String, Object> params = new HashMap<String, Object>();
        List enums = new ArrayList();
        enums.add(STRING);
        enums.add(BYTE);
        params.put("status", enums);
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>((("select from " + className) + " where status in :status")), params);
        Assert.assertEquals(results.size(), 2);
    }

    @Test
    public void testEmbeddedMapOfMapsContainsValue() {
        // issue #7793
        String className = "testEmbeddedMapOfMapsContainsValue";
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL(("create class " + className))).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL((("create property " + className) + ".embedded_map EMBEDDEDMAP"))).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL((("create property " + className) + ".id INTEGER"))).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL((("INSERT INTO " + className) + " SET id = 0, embedded_map = {\"key_2\" : {\"name\" : \"key_2\", \"id\" : \"0\"}}"))).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL((("INSERT INTO " + className) + " SET id = 1, embedded_map = {\"key_1\" : {\"name\" : \"key_1\", \"id\" : \"1\" }}"))).execute();
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>((("select from " + className) + " where embedded_map CONTAINSVALUE {\"name\":\"key_2\", \"id\":\"0\"}")));
        Assert.assertEquals(results.size(), 1);
    }

    @Test
    public void testInvertedIndexedCondition() {
        // issue #7820
        String className = "testInvertedIndexedCondition";
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL(("create class " + className))).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL((("create property " + className) + ".name STRING"))).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL((("insert into " + className) + " SET name = \"1\""))).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL((("insert into " + className) + " SET name = \"2\""))).execute();
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>((("SELECT * FROM " + className) + " WHERE name >= \"0\"")));
        Assert.assertEquals(results.size(), 2);
        results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>((("SELECT * FROM " + className) + " WHERE \"0\" <= name")));
        Assert.assertEquals(results.size(), 2);
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL((((("CREATE INDEX " + className) + ".name on ") + className) + " (name) UNIQUE"))).execute();
        results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>((("SELECT * FROM " + className) + " WHERE \"0\" <= name")));
        Assert.assertEquals(results.size(), 2);
        results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>((("SELECT * FROM " + className) + " WHERE \"2\" <= name")));
        Assert.assertEquals(results.size(), 1);
        results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>((("SELECT * FROM " + className) + " WHERE name >= \"0\"")));
        Assert.assertEquals(results.size(), 2);
    }

    @Test
    public void testIsDefinedOnNull() {
        // issue #7879
        String className = "testIsDefinedOnNull";
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL(("create class " + className))).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL((("create property " + className) + ".name STRING"))).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL((("insert into " + className) + " SET name = null, x = 1"))).execute();
        OCommandExecutorSQLSelectTest.db.command(new OCommandSQL((("insert into " + className) + " SET x = 2"))).execute();
        List<ODocument> results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>((("SELECT * FROM " + className) + " WHERE name is defined")));
        Assert.assertEquals(results.size(), 1);
        Assert.assertEquals(((int) (results.get(0).field("x"))), 1);
        results = OCommandExecutorSQLSelectTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>((("SELECT * FROM " + className) + " WHERE name is not defined")));
        Assert.assertEquals(results.size(), 1);
        Assert.assertEquals(((int) (results.get(0).field("x"))), 2);
    }
}

