/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.query;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.query.impl.DateHelperTest;
import com.hazelcast.query.impl.predicates.AndPredicate;
import com.hazelcast.query.impl.predicates.GreaterLessPredicate;
import com.hazelcast.query.impl.predicates.OrPredicate;
import com.hazelcast.query.impl.predicates.RegexPredicate;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.UuidUtil;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static com.hazelcast.query.SampleTestObjects.State.STATE2;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class SqlPredicateTest {
    private static final String[] TEST_MATCHING_SQL_PREDICATES = new String[]{ "name = 'Joe' and age = 25 and (city = 'austin' or city = 'AUSTIN')", "name = 'Joe' or city = 'Athens'", "(name = 'Jane' or name = 'Joe' or city = 'AUSTIN') and age = 25", "(name = 'Jane' or name = 'Joe' or city = 'AUSTIN') and age = 25 and salary = 0", "(name = 'Jane' or name = 'Joe') and age = 25 and salary = 0 or age = 24", // correct precedence is "name = 'Jane' or (age = 25 and name = 'Joe')
    "name = 'Jane' or age = 25 and name = 'Joe'", "age = 35 or age = 24 or age = 31 or (name = 'Joe' and age = 25)" };

    private static final String[] TEST_NOT_MATCHING_SQL_PREDICATES = new String[]{ "name = 'Joe' and age = 21 and (city = 'austin' or city = 'ATHENS')", "name = 'Jane' or city = 'Athens'", "(name = 'Jane' or name = 'Catie' or city = 'San Jose') and age = 25", "(name = 'Joe' or name = 'Catie' or city = 'San Jose') and age = 21", "(name = 'Jane' or name = 'Joe' or city = 'AUSTIN') and age = 25 and salary = 10", "(name = 'Jane' or name = 'Catie' or city = 'San Jose') and age = 25 and salary = 10", "(name = 'Jane' or name = 'Joe') and age = 25 and salary = 13 or age = 24", "name = 'Jane' or age = 25 and name = 'Catie'", "age = 35 or age = 24 or age = 31 or (name = 'Joe' and age = 27)" };

    private final InternalSerializationService serializationService = new DefaultSerializationServiceBuilder().build();

    // these are used to test compound predicates flattening
    private TruePredicate leftOfOr = new TruePredicate();

    private TruePredicate rightOfOr = new TruePredicate();

    private TruePredicate leftOfAnd = new TruePredicate();

    private TruePredicate rightOfAnd = new TruePredicate();

    @Test
    public void testSqlPredicates() {
        SampleTestObjects.Employee employee = new SampleTestObjects.Employee("Joe", "AUSTIN", 25, true, 0);
        for (String s : SqlPredicateTest.TEST_MATCHING_SQL_PREDICATES) {
            assertSqlMatching(s, employee);
        }
        for (String s : SqlPredicateTest.TEST_NOT_MATCHING_SQL_PREDICATES) {
            assertSqlNotMatching(s, employee);
        }
    }

    public static class Record {
        private String str1;

        private String str2;

        private String str3;

        public Record(String str1, String str2, String str3) {
            this.str1 = str1;
            this.str2 = str2;
            this.str3 = str3;
        }

        public String getStr1() {
            return str1;
        }

        public void setStr1(String str1) {
            this.str1 = str1;
        }

        public String getStr2() {
            return str2;
        }

        public void setStr2(String str2) {
            this.str2 = str2;
        }

        public String getStr3() {
            return str3;
        }

        public void setStr3(String str3) {
            this.str3 = str3;
        }
    }

    // ZD issue 1950
    @Test
    public void testRecordPredicate() {
        SqlPredicateTest.Record record = new SqlPredicateTest.Record("ONE", "TWO", "THREE");
        SqlPredicate predicate = new SqlPredicate("str1 = 'ONE' AND str2 = 'TWO' AND (str3 = 'THREE' OR str3 = 'three')");
        Map.Entry entry = createEntry("1", record);
        Assert.assertTrue(predicate.apply(entry));
    }

    @Test
    public void testEqualsWhenSqlMatches() {
        SqlPredicate sql1 = new SqlPredicate("foo='bar'");
        SqlPredicate sql2 = new SqlPredicate("foo='bar'");
        Assert.assertEquals(sql1, sql2);
    }

    @Test
    public void testEqualsWhenSqlDifferent() {
        SqlPredicate sql1 = new SqlPredicate("foo='bar'");
        SqlPredicate sql2 = new SqlPredicate("foo='baz'");
        Assert.assertNotEquals(sql1, sql2);
    }

    @Test
    public void testEqualsNull() {
        SqlPredicate sql = new SqlPredicate("foo='bar'");
        Assert.assertNotEquals(sql, null);
    }

    @Test
    public void testEqualsSameObject() {
        SqlPredicate sql = new SqlPredicate("foo='bar'");
        Assert.assertEquals(sql, sql);
    }

    @Test
    public void testHashCode() {
        SqlPredicate sql = new SqlPredicate("foo='bar'");
        Assert.assertEquals("foo='bar'".hashCode(), sql.hashCode());
    }

    @Test
    public void testSql_withEnum() {
        SampleTestObjects.Employee value = createValue();
        value.setState(STATE2);
        SampleTestObjects.Employee nullNameValue = createValue(null);
        assertSqlMatching("state == TestUtil.State.STATE2", value);
        assertSqlMatching(("state == " + (STATE2)), value);
        assertSqlNotMatching("state == TestUtil.State.STATE1", value);
        assertSqlNotMatching("state == TestUtil.State.STATE1", nullNameValue);
        assertSqlMatching("state == NULL", nullNameValue);
    }

    @Test
    public void testSql_withDate() {
        Date date = new Date();
        SampleTestObjects.ObjectWithDate value = new SampleTestObjects.ObjectWithDate(date);
        SimpleDateFormat format = new SimpleDateFormat(DateHelperTest.DATE_FORMAT, Locale.US);
        assertSqlMatching((("attribute > '" + (format.format(new Date(0)))) + "'"), value);
        assertSqlMatching((("attribute >= '" + (format.format(new Date(0)))) + "'"), value);
        assertSqlMatching((("attribute < '" + (format.format(new Date(((date.getTime()) + 1000))))) + "'"), value);
        assertSqlMatching((("attribute <= '" + (format.format(new Date(((date.getTime()) + 1000))))) + "'"), value);
    }

    @Test
    public void testSql_withSqlDate() {
        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
        SampleTestObjects.ObjectWithSqlDate value = new SampleTestObjects.ObjectWithSqlDate(date);
        SimpleDateFormat format = new SimpleDateFormat(DateHelperTest.SQL_DATE_FORMAT, Locale.US);
        assertSqlMatching((("attribute > '" + (format.format(new java.sql.Date(0)))) + "'"), value);
        assertSqlMatching((("attribute >= '" + (format.format(new java.sql.Date(0)))) + "'"), value);
        assertSqlMatching((("attribute < '" + (format.format(new java.sql.Date(((date.getTime()) + (TimeUnit.DAYS.toMillis(2))))))) + "'"), value);
        assertSqlMatching((("attribute <= '" + (format.format(new java.sql.Date(((date.getTime()) + (TimeUnit.DAYS.toMillis(2))))))) + "'"), value);
    }

    @Test
    public void testSql_withTimestamp() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SampleTestObjects.ObjectWithSqlTimestamp value = new SampleTestObjects.ObjectWithSqlTimestamp(timestamp);
        SimpleDateFormat format = new SimpleDateFormat(DateHelperTest.TIMESTAMP_FORMAT, Locale.US);
        assertSqlMatching((("attribute > '" + (format.format(new Timestamp(0)))) + "'"), value);
        assertSqlMatching((("attribute >= '" + (format.format(new Timestamp(0)))) + "'"), value);
        assertSqlMatching((("attribute < '" + (format.format(new Timestamp(((timestamp.getTime()) + 1000))))) + "'"), value);
        assertSqlMatching((("attribute <= '" + (format.format(new Timestamp(((timestamp.getTime()) + 1000))))) + "'"), value);
    }

    @Test
    public void testSql_withBigDecimal() {
        SampleTestObjects.ObjectWithBigDecimal value = new SampleTestObjects.ObjectWithBigDecimal(new BigDecimal("1.23E3"));
        assertSqlMatching((("attribute > '" + (new BigDecimal("1.23E2"))) + "'"), value);
        assertSqlMatching((("attribute >= '" + (new BigDecimal("1.23E3"))) + "'"), value);
        assertSqlNotMatching((("attribute = '" + (new BigDecimal("1.23"))) + "'"), value);
        assertSqlMatching("attribute = '1.23E3'", value);
        assertSqlMatching("attribute = 1.23E3", value);
        assertSqlNotMatching("attribute = 1.23", value);
    }

    @Test
    public void testSql_withBigInteger() {
        SampleTestObjects.ObjectWithBigInteger value = new SampleTestObjects.ObjectWithBigInteger(new BigInteger("123"));
        assertSqlMatching((("attribute > '" + (new BigInteger("122"))) + "'"), value);
        assertSqlMatching((("attribute >= '" + (new BigInteger("123"))) + "'"), value);
        assertSqlMatching((("attribute = '" + (new BigInteger("123"))) + "'"), value);
        assertSqlNotMatching((("attribute = '" + (new BigInteger("122"))) + "'"), value);
        assertSqlMatching((("attribute < '" + (new BigInteger("124"))) + "'"), value);
        assertSqlMatching((("attribute <= '" + (new BigInteger("123"))) + "'"), value);
        assertSqlMatching("attribute = 123", value);
        assertSqlMatching("attribute = '123'", value);
        assertSqlMatching("attribute != 124", value);
        assertSqlMatching("attribute <> 124", value);
        assertSqlNotMatching("attribute = 124", value);
        assertSqlMatching("attribute between 122 and 124", value);
        assertSqlMatching("attribute in (122, 123, 124)", value);
    }

    @Test
    public void testSql_withString() {
        SampleTestObjects.Employee value = createValue();
        SampleTestObjects.Employee nullNameValue = new SampleTestObjects.Employee(null, 34, true, 10.0);
        assertSqlNotMatching("name = 'null'", nullNameValue);
        assertSqlMatching("name = null", nullNameValue);
        assertSqlMatching("name = NULL", nullNameValue);
        assertSqlMatching("name != null", value);
        assertSqlMatching("name != NULL", value);
        assertSqlMatching("name <> null", value);
        assertSqlMatching("name <> NULL", value);
        assertSqlMatching(((" (name LIKE 'abc-%') AND (age <= " + 40) + ")"), value);
        assertSqlMatching(((" (name REGEX 'abc-.*') AND (age <= " + 40) + ")"), value);
        assertSqlMatching("name='abc-123-xvz'", value);
        assertSqlMatching("name='abc 123-xvz'", createValue("abc 123-xvz"));
        assertSqlMatching("name='abc 123-xvz+(123)'", createValue("abc 123-xvz+(123)"));
        assertSqlNotMatching("name='abc 123-xvz+(123)'", createValue("abc123-xvz+(123)"));
        assertSqlMatching("name LIKE 'abc-%'", createValue("abc-123"));
        assertSqlMatching("name REGEX \'^\\w{3}-\\d{3}-\\w{3}$\'", value);
        assertSqlNotMatching("name REGEX \'^[^\\w]{3}-\\d{3}-\\w{3}$\'", value);
        assertSqlMatching(((" (name ILIKE 'ABC-%') AND (age <= " + 40) + ")"), value);
    }

    @Test
    public void testSql_withInteger() {
        SampleTestObjects.ObjectWithInteger value = new SampleTestObjects.ObjectWithInteger(34);
        assertSqlMatching("(attribute >= 20) AND (attribute <= 40)", value);
        assertSqlMatching("(attribute >= 20 ) AND (attribute <= 34)", value);
        assertSqlMatching("(attribute >= 34) AND (attribute <= 35)", value);
        assertSqlMatching("attribute IN (34, 35)", value);
        assertSqlNotMatching("attribute IN (33,35)", value);
        assertSqlNotMatching("attribute = 33", value);
        assertSqlMatching("attribute = 34", value);
        assertSqlMatching("attribute > 5", value);
        assertSqlMatching("attribute = -33", new SampleTestObjects.ObjectWithInteger((-33)));
    }

    @Test
    public void testSql_withLong() {
        SampleTestObjects.ObjectWithLong value = new SampleTestObjects.ObjectWithLong(34L);
        assertSqlMatching("(attribute >= 20) AND (attribute <= 40)", value);
        assertSqlMatching("(attribute >= 20 ) AND (attribute <= 34)", value);
        assertSqlMatching("(attribute >= 34) AND (attribute <= 35)", value);
        assertSqlMatching("attribute IN (34, 35)", value);
        assertSqlNotMatching("attribute IN (33,35)", value);
        assertSqlNotMatching("attribute = 33", value);
        assertSqlMatching("attribute = 34", value);
        assertSqlMatching("attribute > 5", value);
        assertSqlMatching("attribute = -33", new SampleTestObjects.ObjectWithLong((-33)));
    }

    @Test
    public void testSql_withDouble() {
        SampleTestObjects.ObjectWithDouble value = new SampleTestObjects.ObjectWithDouble(10.001);
        assertSqlMatching("attribute > 5", value);
        assertSqlMatching("attribute > 5 and attribute < 11", value);
        assertSqlNotMatching("attribute > 15 or attribute < 10", value);
        assertSqlMatching("attribute between 9.99 and 10.01", value);
        assertSqlMatching("attribute between 5 and 15", value);
    }

    @Test
    public void testSql_withFloat() {
        SampleTestObjects.ObjectWithFloat value = new SampleTestObjects.ObjectWithFloat(10.001F);
        assertSqlMatching("attribute > 5", value);
        assertSqlMatching("attribute > 5 and attribute < 11", value);
        assertSqlNotMatching("attribute > 15 or attribute < 10", value);
        assertSqlMatching("attribute between 9.99 and 10.01", value);
        assertSqlMatching("attribute between 5 and 15", value);
    }

    @Test
    public void testSql_withShort() {
        SampleTestObjects.ObjectWithShort value = new SampleTestObjects.ObjectWithShort(((short) (10)));
        assertSqlMatching("attribute = 10", value);
        assertSqlNotMatching("attribute = 11", value);
        assertSqlMatching("attribute >= 10", value);
        assertSqlMatching("attribute <= 10", value);
        assertSqlMatching("attribute > 5", value);
        assertSqlMatching("attribute > 5 and attribute < 11", value);
        assertSqlNotMatching("attribute > 15 or attribute < 10", value);
        assertSqlMatching("attribute between 5 and 15", value);
        assertSqlMatching("attribute in (5, 10, 15)", value);
        assertSqlNotMatching("attribute in (5, 11, 15)", value);
    }

    @Test
    public void testSql_withByte() {
        SampleTestObjects.ObjectWithByte value = new SampleTestObjects.ObjectWithByte(((byte) (10)));
        assertSqlMatching("attribute = 10", value);
        assertSqlNotMatching("attribute = 11", value);
        assertSqlMatching("attribute >= 10", value);
        assertSqlMatching("attribute <= 10", value);
        assertSqlMatching("attribute > 5", value);
        assertSqlMatching("attribute > 5 and attribute < 11", value);
        assertSqlNotMatching("attribute > 15 or attribute < 10", value);
        assertSqlMatching("attribute between 5 and 15", value);
        assertSqlMatching("attribute in (5, 10, 15)", value);
        assertSqlNotMatching("attribute in (5, 11, 15)", value);
    }

    @Test
    public void testSql_withChar() {
        SampleTestObjects.ObjectWithChar value = new SampleTestObjects.ObjectWithChar('%');
        assertSqlMatching("attribute = '%'", value);
        assertSqlNotMatching("attribute = '$'", value);
        assertSqlMatching("attribute in ('A', '#', '%')", value);
        assertSqlNotMatching("attribute in ('A', '#', '&')", value);
    }

    @Test
    public void testSql_withBoolean() {
        SampleTestObjects.ObjectWithBoolean value = new SampleTestObjects.ObjectWithBoolean(true);
        assertSqlMatching("attribute", value);
        assertSqlMatching("attribute = true", value);
        assertSqlNotMatching("attribute = false", value);
        assertSqlNotMatching("not attribute", value);
    }

    @Test
    public void testSql_withUUID() {
        UUID uuid = UuidUtil.newUnsecureUUID();
        SampleTestObjects.ObjectWithUUID value = new SampleTestObjects.ObjectWithUUID(uuid);
        assertSqlMatching((("attribute = '" + (uuid.toString())) + "'"), value);
        assertSqlNotMatching((("attribute = '" + (UuidUtil.newUnsecureUuidString())) + "'"), value);
    }

    @Test
    public void testSqlPredicate() {
        Assert.assertEquals("name IN (name0,name2)", sql("name in ('name0', 'name2')"));
        Assert.assertEquals("(name LIKE 'joe' AND id=5)", sql("name like 'joe' AND id = 5"));
        Assert.assertEquals("(name REGEX \'\\w*\' AND id=5)", sql("name regex \'\\w*\' AND id = 5"));
        Assert.assertEquals("active=true", sql("active"));
        Assert.assertEquals("(active=true AND name=abc xyz 123)", sql("active AND name='abc xyz 123'"));
        Assert.assertEquals("(name LIKE 'abc-xyz+(123)' AND name=abc xyz 123)", sql("name like 'abc-xyz+(123)' AND name='abc xyz 123'"));
        Assert.assertEquals("(name REGEX \'\\w{3}-\\w{3}+\\(\\d{3}\\)\' AND name=abc xyz 123)", sql("name regex \'\\w{3}-\\w{3}+\\(\\d{3}\\)\' AND name=\'abc xyz 123\'"));
        Assert.assertEquals("(active=true AND age>4)", sql("active and age > 4"));
        Assert.assertEquals("(active=true AND age>4)", sql("active and age>4"));
        Assert.assertEquals("(active=false AND age<=4)", sql("active=false AND age<=4"));
        Assert.assertEquals("(active=false AND age<=4)", sql("active= false and age <= 4"));
        Assert.assertEquals("(active=false AND age>=4)", sql("active=false AND (age>=4)"));
        Assert.assertEquals("(active=false OR age>=4)", sql("active =false or (age>= 4)"));
        Assert.assertEquals("name LIKE 'J%'", sql("name like 'J%'"));
        Assert.assertEquals("name REGEX 'J.*'", sql("name regex 'J.*'"));
        Assert.assertEquals("NOT(name LIKE 'J%')", sql("name not like 'J%'"));
        Assert.assertEquals("NOT(name REGEX 'J.*')", sql("name not regex 'J.*'"));
        Assert.assertEquals("(active=false OR name LIKE 'J%')", sql("active =false or name like 'J%'"));
        Assert.assertEquals("(active=false OR name LIKE 'Java World')", sql("active =false or name like 'Java World'"));
        Assert.assertEquals("(active=false OR name LIKE 'Java W% Again')", sql("active =false or name like 'Java W% Again'"));
        Assert.assertEquals("(active=false OR name REGEX 'J.*')", sql("active =false or name regex 'J.*'"));
        Assert.assertEquals("(active=false OR name REGEX 'Java World')", sql("active =false or name regex 'Java World'"));
        Assert.assertEquals("(active=false OR name REGEX 'Java W.* Again')", sql("active =false or name regex 'Java W.* Again'"));
        Assert.assertEquals("i<=-1", sql("i<= -1"));
        Assert.assertEquals("age IN (-1)", sql("age in (-1)"));
        Assert.assertEquals("age IN (10,15)", sql("age in (10, 15)"));
        Assert.assertEquals("NOT(age IN (10,15))", sql("age not in ( 10 , 15 )"));
        Assert.assertEquals("(active=true AND age BETWEEN 10 AND 15)", sql("active and age between 10 and 15"));
        Assert.assertEquals("(age IN (10,15) AND active=true)", sql("age IN (10, 15) and active"));
        Assert.assertEquals("(active=true OR age IN (10,15))", sql("active or (age in ( 10,15))"));
        Assert.assertEquals("(age>10 AND (active=true OR age IN (10,15)))", sql("age>10 AND (active or (age IN (10, 15 )))"));
        Assert.assertEquals("(age<=10 AND (active=true OR NOT(age IN (10,15))))", sql("age<=10 AND (active or (age not in (10 , 15)))"));
        Assert.assertEquals("age BETWEEN 10 AND 15", sql("age between 10 and 15"));
        Assert.assertEquals("NOT(age BETWEEN 10 AND 15)", sql("age not between 10 and 15"));
        Assert.assertEquals("(active=true AND age BETWEEN 10 AND 15)", sql("active and age between 10 and 15"));
        Assert.assertEquals("(age BETWEEN 10 AND 15 AND active=true)", sql("age between 10 and 15 and active"));
        Assert.assertEquals("(active=true OR age BETWEEN 10 AND 15)", sql("active or (age between 10 and 15)"));
        Assert.assertEquals("(age>10 AND (active=true OR age BETWEEN 10 AND 15))", sql("age>10 AND (active or (age between 10 and 15))"));
        Assert.assertEquals("(age<=10 AND (active=true OR NOT(age BETWEEN 10 AND 15)))", sql("age<=10 AND (active or (age not between 10 and 15))"));
        Assert.assertEquals("name ILIKE 'J%'", sql("name ilike 'J%'"));
        // issue #594
        Assert.assertEquals("(name IN (name0,name2) AND age IN (2,5,8))", sql("name in('name0', 'name2') and age   IN ( 2, 5  ,8)"));
    }

    @Test
    public void testSqlPredicateEscape() {
        Assert.assertEquals("(active=true AND name=abc x'yz 1'23)", sql("active AND name='abc x''yz 1''23'"));
        Assert.assertEquals("(active=true AND name=)", sql("active AND name=''"));
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidSqlPredicate1() {
        new SqlPredicate("invalid sql");
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidSqlPredicate2() {
        new SqlPredicate("");
    }

    // This test used to fail with a stack overflow exception, due to the way SQL predicates were
    // nested, missing an opportunity to flatten.
    // https://github.com/hazelcast/hazelcast/issues/7583
    @Test
    public void testLongPredicate() {
        TestHazelcastInstanceFactory factory = new TestHazelcastInstanceFactory(1);
        HazelcastInstance hazelcastInstance = factory.newHazelcastInstance();
        IMap<Integer, Integer> map = hazelcastInstance.getMap(HazelcastTestSupport.randomString());
        for (int i = 0; i < 8000; i++) {
            map.put(i, i);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8000; i++) {
            sb.append("intValue() == ").append(i).append(" or ");
        }
        sb.append(" intValue() == -1");
        SqlPredicate predicate = new SqlPredicate(sb.toString());
        // all entries must match
        Set<Map.Entry<Integer, Integer>> entries = map.entrySet(predicate);
        Assert.assertEquals(map.size(), entries.size());
        factory.terminateAll();
    }

    @Test
    public void testOr_whenBothPredicatesOr() {
        OrPredicate predicate1 = new OrPredicate(new SqlPredicate("a == 1"), new SqlPredicate("a == 2"));
        OrPredicate predicate2 = new OrPredicate(new SqlPredicate("a == 3"));
        OrPredicate concatenatedOr = SqlPredicate.flattenCompound(predicate1, predicate2, OrPredicate.class);
        Assert.assertEquals(3, concatenatedOr.getPredicates().length);
    }

    @Test
    public void testOr_whenLeftPredicateOr() {
        OrPredicate predicate1 = new OrPredicate(new SqlPredicate("a == 1"), new SqlPredicate("a == 2"));
        TruePredicate predicate2 = new TruePredicate();
        OrPredicate concatenatedOr = SqlPredicate.flattenCompound(predicate1, predicate2, OrPredicate.class);
        Assert.assertEquals(3, concatenatedOr.getPredicates().length);
        HazelcastTestSupport.assertInstanceOf(SqlPredicate.class, concatenatedOr.getPredicates()[0]);
        HazelcastTestSupport.assertInstanceOf(SqlPredicate.class, concatenatedOr.getPredicates()[1]);
        Assert.assertSame(predicate2, concatenatedOr.getPredicates()[2]);
    }

    @Test
    public void testOr_whenRightPredicateOr() {
        TruePredicate predicate1 = new TruePredicate();
        OrPredicate predicate2 = new OrPredicate(new SqlPredicate("a == 1"), new SqlPredicate("a == 2"));
        OrPredicate concatenatedOr = SqlPredicate.flattenCompound(predicate1, predicate2, OrPredicate.class);
        Assert.assertEquals(3, concatenatedOr.getPredicates().length);
        Assert.assertSame(predicate1, concatenatedOr.getPredicates()[0]);
        HazelcastTestSupport.assertInstanceOf(SqlPredicate.class, concatenatedOr.getPredicates()[1]);
        HazelcastTestSupport.assertInstanceOf(SqlPredicate.class, concatenatedOr.getPredicates()[2]);
    }

    @Test
    public void testOr_whenNoPredicateOr() {
        TruePredicate predicate1 = new TruePredicate();
        TruePredicate predicate2 = new TruePredicate();
        OrPredicate concatenatedOr = SqlPredicate.flattenCompound(predicate1, predicate2, OrPredicate.class);
        Assert.assertEquals(2, concatenatedOr.getPredicates().length);
        Assert.assertSame(predicate1, concatenatedOr.getPredicates()[0]);
        Assert.assertSame(predicate2, concatenatedOr.getPredicates()[1]);
    }

    @Test
    public void testAnd_whenBothPredicatesAnd() {
        AndPredicate predicate1 = new AndPredicate(new SqlPredicate("a == 1"), new SqlPredicate("a == 2"));
        AndPredicate predicate2 = new AndPredicate(new SqlPredicate("a == 3"));
        AndPredicate concatenatedOr = SqlPredicate.flattenCompound(predicate1, predicate2, AndPredicate.class);
        Assert.assertEquals(3, concatenatedOr.getPredicates().length);
    }

    @Test
    public void testAnd_whenLeftPredicateAnd() {
        AndPredicate predicate1 = new AndPredicate(new SqlPredicate("a == 1"), new SqlPredicate("a == 2"));
        TruePredicate predicate2 = new TruePredicate();
        AndPredicate concatenatedOr = SqlPredicate.flattenCompound(predicate1, predicate2, AndPredicate.class);
        Assert.assertEquals(3, concatenatedOr.getPredicates().length);
        HazelcastTestSupport.assertInstanceOf(SqlPredicate.class, concatenatedOr.getPredicates()[0]);
        HazelcastTestSupport.assertInstanceOf(SqlPredicate.class, concatenatedOr.getPredicates()[1]);
        Assert.assertSame(predicate2, concatenatedOr.getPredicates()[2]);
    }

    @Test
    public void testAnd_whenRightPredicateAnd() {
        TruePredicate predicate1 = new TruePredicate();
        AndPredicate predicate2 = new AndPredicate(new SqlPredicate("a == 1"), new SqlPredicate("a == 2"));
        AndPredicate concatenatedOr = SqlPredicate.flattenCompound(predicate1, predicate2, AndPredicate.class);
        Assert.assertEquals(3, concatenatedOr.getPredicates().length);
        Assert.assertSame(predicate1, concatenatedOr.getPredicates()[0]);
        HazelcastTestSupport.assertInstanceOf(SqlPredicate.class, concatenatedOr.getPredicates()[1]);
        HazelcastTestSupport.assertInstanceOf(SqlPredicate.class, concatenatedOr.getPredicates()[2]);
    }

    @Test
    public void testAnd_whenNoPredicateAnd() {
        TruePredicate predicate1 = new TruePredicate();
        TruePredicate predicate2 = new TruePredicate();
        AndPredicate concatenatedOr = SqlPredicate.flattenCompound(predicate1, predicate2, AndPredicate.class);
        Assert.assertEquals(2, concatenatedOr.getPredicates().length);
        Assert.assertSame(predicate1, concatenatedOr.getPredicates()[0]);
        Assert.assertSame(predicate2, concatenatedOr.getPredicates()[1]);
    }

    // (AND (OR A B) (AND C D)) is flattened to (AND (OR A B) C D)
    @Test
    public void testFlattenAnd_withOrAndPredicates() {
        OrPredicate orPredicate = new OrPredicate(leftOfOr, rightOfOr);
        AndPredicate andPredicate = new AndPredicate(leftOfAnd, rightOfAnd);
        AndPredicate flattenedCompoundAnd = SqlPredicate.flattenCompound(orPredicate, andPredicate, AndPredicate.class);
        Assert.assertSame(orPredicate, flattenedCompoundAnd.getPredicates()[0]);
        Assert.assertSame(leftOfAnd, flattenedCompoundAnd.getPredicates()[1]);
        Assert.assertSame(rightOfAnd, flattenedCompoundAnd.getPredicates()[2]);
    }

    // (AND (AND A B) (OR C D)) is flattened to (AND A B (OR C D))
    @Test
    public void testFlattenAnd_withAndORPredicates() {
        OrPredicate orPredicate = new OrPredicate(leftOfOr, rightOfOr);
        AndPredicate andPredicate = new AndPredicate(leftOfAnd, rightOfAnd);
        AndPredicate flattenedCompoundAnd = SqlPredicate.flattenCompound(andPredicate, orPredicate, AndPredicate.class);
        Assert.assertSame(leftOfAnd, flattenedCompoundAnd.getPredicates()[0]);
        Assert.assertSame(rightOfAnd, flattenedCompoundAnd.getPredicates()[1]);
        Assert.assertSame(orPredicate, flattenedCompoundAnd.getPredicates()[2]);
    }

    // (OR (OR A B) (AND C D)) is flattened to (OR A B (AND C D))
    @Test
    public void testFlattenOr_withOrAndPredicates() {
        OrPredicate orPredicate = new OrPredicate(leftOfOr, rightOfOr);
        AndPredicate andPredicate = new AndPredicate(leftOfAnd, rightOfAnd);
        OrPredicate flattenedCompoundOr = SqlPredicate.flattenCompound(orPredicate, andPredicate, OrPredicate.class);
        Assert.assertSame(leftOfOr, flattenedCompoundOr.getPredicates()[0]);
        Assert.assertSame(rightOfOr, flattenedCompoundOr.getPredicates()[1]);
        Assert.assertSame(andPredicate, flattenedCompoundOr.getPredicates()[2]);
    }

    // (OR (AND A B) (OR C D)) is flattened to (OR (AND A B) C D)
    @Test
    public void testFlattenOr_withAndOrPredicates() {
        OrPredicate orPredicate = new OrPredicate(leftOfOr, rightOfOr);
        AndPredicate andPredicate = new AndPredicate(leftOfAnd, rightOfAnd);
        OrPredicate flattenedCompoundOr = SqlPredicate.flattenCompound(andPredicate, orPredicate, OrPredicate.class);
        Assert.assertSame(andPredicate, flattenedCompoundOr.getPredicates()[0]);
        Assert.assertSame(leftOfOr, flattenedCompoundOr.getPredicates()[1]);
        Assert.assertSame(rightOfOr, flattenedCompoundOr.getPredicates()[2]);
    }

    // (OR (AND A B) (AND C D)) is not flattened
    @Test
    public void testFlattenOr_withTwoAndPredicates() {
        AndPredicate andLeft = new AndPredicate(leftOfOr, rightOfOr);
        AndPredicate andRight = new AndPredicate(leftOfAnd, rightOfAnd);
        OrPredicate flattenedCompoundOr = SqlPredicate.flattenCompound(andLeft, andRight, OrPredicate.class);
        Assert.assertSame(andLeft, flattenedCompoundOr.getPredicates()[0]);
        Assert.assertSame(andRight, flattenedCompoundOr.getPredicates()[1]);
    }

    // (AND (OR A B) (OR C D)) is not flattened
    @Test
    public void testFlattenAnd_withTwoOrPredicates() {
        OrPredicate orLeft = new OrPredicate(leftOfOr, rightOfOr);
        OrPredicate orRight = new OrPredicate(leftOfAnd, rightOfAnd);
        AndPredicate flattenedCompoundOr = SqlPredicate.flattenCompound(orLeft, orRight, AndPredicate.class);
        Assert.assertSame(orLeft, flattenedCompoundOr.getPredicates()[0]);
        Assert.assertSame(orRight, flattenedCompoundOr.getPredicates()[1]);
    }

    // http://stackoverflow.com/questions/37382505/hazelcast-imap-valuespredicate-miss-data
    @Test
    public void testAndWithRegex_stackOverflowIssue() {
        String sqlPredicate = "nextExecuteTime < 1463975296703 AND autoIncrementId REGEX '.*[5,6,7,8,9]$'";
        Predicate predicate = new SqlPredicate(sqlPredicate).predicate;
        AndPredicate andPredicate = ((AndPredicate) (predicate));
        Assert.assertEquals(GreaterLessPredicate.class, andPredicate.getPredicates()[0].getClass());
        Assert.assertEquals(RegexPredicate.class, andPredicate.getPredicates()[1].getClass());
    }
}

