/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.processors.query.h2.sql;


import QueryIndex.DFLT_INLINE_SIZE;
import QueryIndexType.GEOSPATIAL;
import QueryIndexType.SORTED;
import Value.INT;
import Value.STRING;
import java.io.Serializable;
import java.sql.Date;
import org.apache.ignite.Ignite;
import org.apache.ignite.cache.affinity.AffinityKeyMapped;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.processors.cache.index.AbstractIndexingCommonTest;
import org.apache.ignite.internal.processors.query.IgniteSQLException;
import org.apache.ignite.internal.util.typedef.F;
import org.h2.command.Prepared;
import org.h2.message.DbException;
import org.junit.Test;


/**
 *
 */
public class GridQueryParsingTest extends AbstractIndexingCommonTest {
    /**
     *
     */
    private static Ignite ignite;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testParseSelectAndUnion() throws Exception {
        checkQuery("select 1 from Person p where addrIds in ((1,2,3), (3,4,5))");
        checkQuery("select 1 from Person p where addrId in ((1,))");
        checkQuery(("select 1 from Person p " + "where p.addrId in (select a.id from sch2.Address a)"));
        checkQuery(("select 1 from Person p " + "where exists(select 1 from sch2.Address a where p.addrId = a.id)"));
        checkQuery("select 42");
        checkQuery("select ()");
        checkQuery("select (1)");
        checkQuery("select (1 + 1)");
        checkQuery("select (1,)");
        checkQuery("select (?)");
        checkQuery("select (?,)");
        checkQuery("select (1, 2)");
        checkQuery("select (?, ? + 1, 2 + 2) as z");
        checkQuery("select (1,(1,(1,(1,(1,?)))))");
        checkQuery("select (select 1)");
        checkQuery("select (select 1, select ?)");
        checkQuery("select ((select 1), select ? + ?)");
        checkQuery("select CURRENT_DATE");
        checkQuery("select CURRENT_DATE()");
        checkQuery("select extract(year from ?)");
        checkQuery("select convert(?, timestamp)");
        checkQuery("select * from table(id bigint = 1)");
        checkQuery("select * from table(id bigint = (1))");
        checkQuery("select * from table(id bigint = (1,))");
        checkQuery("select * from table(id bigint = (1,), name varchar = 'asd')");
        checkQuery("select * from table(id bigint = (1,2), name varchar = 'asd')");
        checkQuery("select * from table(id bigint = (1,2), name varchar = ('asd',))");
        checkQuery("select * from table(id bigint = (1,2), name varchar = ?)");
        checkQuery("select * from table(id bigint = (1,2), name varchar = (?,))");
        checkQuery("select * from table(id bigint = ?, name varchar = ('abc', 'def', 100, ?)) t");
        checkQuery("select ? limit ? offset ?");
        checkQuery("select cool1()");
        checkQuery("select cool1() z");
        checkQuery("select b,a from table0('aaa', 100)");
        checkQuery("select * from table0('aaa', 100)");
        checkQuery("select * from table0('aaa', 100) t0");
        checkQuery("select x.a, y.b from table0('aaa', 100) x natural join table0('bbb', 100) y");
        checkQuery("select * from table0('aaa', 100) x join table0('bbb', 100) y on x.a=y.a and x.b = 1");
        checkQuery("select * from table0('aaa', 100) x left join table0('bbb', 100) y on x.a=y.a and x.b = 1");
        checkQuery("select * from table0('aaa', 100) x left join table0('bbb', 100) y on x.a=y.a where x.b = 1");
        checkQuery("select * from table0('aaa', 100) x left join table0('bbb', 100) y where x.b = 1");
        checkQuery(("select avg(old) from Person left join sch2.Address on Person.addrId = Address.id " + "where lower(Address.street) = lower(?)"));
        checkQuery(("select avg(old) from sch1.Person join sch2.Address on Person.addrId = Address.id " + "where lower(Address.street) = lower(?)"));
        checkQuery(("select avg(old) from Person left join sch2.Address where Person.addrId = Address.id " + "and lower(Address.street) = lower(?)"));
        checkQuery(("select avg(old) from Person right join sch2.Address where Person.addrId = Address.id " + "and lower(Address.street) = lower(?)"));
        checkQuery(("select avg(old) from Person, sch2.Address where Person.addrId = Address.id " + "and lower(Address.street) = lower(?)"));
        checkQuery("select name, name, date, date d from Person");
        checkQuery("select distinct name, date from Person");
        checkQuery("select * from Person p");
        checkQuery("select * from Person");
        checkQuery("select distinct * from Person");
        checkQuery("select p.name, date from Person p");
        checkQuery("select p.name, date from Person p for update");
        checkQuery("select * from Person p, sch2.Address a");
        checkQuery("select * from Person, sch2.Address");
        checkQuery("select p.* from Person p, sch2.Address a");
        checkQuery("select person.* from Person, sch2.Address a");
        checkQuery("select p.*, street from Person p, sch2.Address a");
        checkQuery("select p.name, a.street from Person p, sch2.Address a");
        checkQuery("select p.name, a.street from sch2.Address a, Person p");
        checkQuery("select distinct p.name, a.street from Person p, sch2.Address a");
        checkQuery("select distinct name, street from Person, sch2.Address group by old");
        checkQuery("select distinct name, street from Person, sch2.Address");
        checkQuery("select p1.name, a2.street from Person p1, sch2.Address a1, Person p2, sch2.Address a2");
        checkQuery("select p.name n, a.street s from Person p, sch2.Address a");
        checkQuery("select p.name, 1 as i, 'aaa' s from Person p");
        checkQuery("select p.name + 'a', 1 * 3 as i, 'aaa' s, -p.old, -p.old as old from Person p");
        checkQuery(("select p.name || 'a' + p.name, (p.old * 3) % p.old - p.old / p.old, p.name = 'aaa', " + ((" p.name is p.name, p.old > 0, p.old >= 0, p.old < 0, p.old <= 0, p.old <> 0, p.old is not p.old, " + " p.old is null, p.old is not null ") + " from Person p")));
        checkQuery("select p.name from Person p where name <> 'ivan'");
        checkQuery("select p.name from Person p where name like 'i%'");
        checkQuery("select p.name from Person p where name regexp 'i%'");
        checkQuery(("select p.name from Person p, sch2.Address a " + "where p.name <> 'ivan' and a.id > 10 or not (a.id = 100)"));
        checkQuery("select case p.name when 'a' then 1 when 'a' then 2 end as a from Person p");
        checkQuery("select case p.name when 'a' then 1 when 'a' then 2 else -1 end as a from Person p");
        checkQuery("select abs(p.old)  from Person p");
        checkQuery("select cast(p.old as numeric(10, 2)) from Person p");
        checkQuery("select cast(p.old as numeric(10, 2)) z from Person p");
        checkQuery("select cast(p.old as numeric(10, 2)) as z from Person p");
        checkQuery("select * from Person p where p.name in ('a', 'b', '_' + RAND())");// test ConditionIn

        checkQuery("select * from Person p where p.name in ('a', 'b', 'c')");// test ConditionInConstantSet

        // test ConditionInConstantSet
        checkQuery("select * from Person p where p.name in (select a.street from sch2.Address a)");
        // test ConditionInConstantSet
        checkQuery("select (select a.street from sch2.Address a where a.id = p.addrId) from Person p");
        checkQuery("select p.name, ? from Person p where name regexp ? and p.old < ?");
        checkQuery("select count(*) as a from Person having a > 10");
        checkQuery("select count(*) as a, count(p.*), count(p.name) from Person p");
        checkQuery("select count(distinct p.name) from Person p");
        checkQuery("select name, count(*) cnt from Person group by name order by cnt desc limit 10");
        checkQuery("select p.name, avg(p.old), max(p.old) from Person p group by p.name");
        checkQuery("select p.name n, avg(p.old) a, max(p.old) m from Person p group by p.name");
        checkQuery("select p.name n, avg(p.old) a, max(p.old) m from Person p group by n");
        checkQuery("select p.name n, avg(p.old) a, max(p.old) m from Person p group by p.addrId, p.name");
        checkQuery("select p.name n, avg(p.old) a, max(p.old) m from Person p group by p.name, p.addrId");
        checkQuery("select p.name n, max(p.old) + min(p.old) / count(distinct p.old) from Person p group by p.name");
        checkQuery(("select p.name n, max(p.old) maxOld, min(p.old) minOld from Person p " + "group by p.name having maxOld > 10 and min(p.old) < 1"));
        checkQuery("select p.name n, avg(p.old) a, max(p.old) m from Person p group by p.name order by n");
        checkQuery("select p.name n, avg(p.old) a, max(p.old) m from Person p group by p.name order by p.name");
        checkQuery("select p.name n, avg(p.old) a, max(p.old) m from Person p group by p.name order by p.name, m");
        checkQuery(("select p.name n, avg(p.old) a, max(p.old) m from Person p " + "group by p.name order by p.name, max(p.old) desc"));
        checkQuery(("select p.name n, avg(p.old) a, max(p.old) m from Person p " + "group by p.name order by p.name nulls first"));
        checkQuery(("select p.name n, avg(p.old) a, max(p.old) m from Person p " + "group by p.name order by p.name nulls last"));
        checkQuery("select p.name n from Person p order by p.old + 10");
        checkQuery("select p.name n from Person p order by p.old + 10, p.name");
        checkQuery("select p.name n from Person p order by p.old + 10, p.name desc");
        checkQuery(("select p.name n from Person p, (select a.street from sch2.Address a " + "where a.street is not null) "));
        checkQuery(("select street from Person p, (select a.street from sch2.Address a " + "where a.street is not null) "));
        checkQuery(("select addr.street from Person p, (select a.street from sch2.Address a " + "where a.street is not null) addr"));
        checkQuery("select p.name n from sch1.Person p order by p.old + 10");
        checkQuery("select case when p.name is null then 'Vasya' end x from sch1.Person p");
        checkQuery("select case when p.name like 'V%' then 'Vasya' else 'Other' end x from sch1.Person p");
        checkQuery(("select case when upper(p.name) = 'VASYA' then 'Vasya' " + "when p.name is not null then p.name else 'Other' end x from sch1.Person p"));
        checkQuery("select case p.name when 'Vasya' then 1 end z from sch1.Person p");
        checkQuery("select case p.name when 'Vasya' then 1 when 'Petya' then 2 end z from sch1.Person p");
        checkQuery("select case p.name when 'Vasya' then 1 when 'Petya' then 2 else 3 end z from sch1.Person p");
        checkQuery("select case p.name when 'Vasya' then 1 else 3 end z from sch1.Person p");
        checkQuery("select count(*) as a from Person union select count(*) as a from sch2.Address");
        checkQuery(("select old, count(*) as a from Person group by old union select 1, count(*) as a " + "from sch2.Address"));
        checkQuery("select name from Person MINUS select street from sch2.Address");
        checkQuery("select name from Person EXCEPT select street from sch2.Address");
        checkQuery("select name from Person INTERSECT select street from sch2.Address");
        checkQuery("select name from Person UNION select street from sch2.Address limit 5");
        checkQuery("select name from Person UNION select street from sch2.Address limit ?");
        checkQuery("select name from Person UNION select street from sch2.Address limit ? offset ?");
        checkQuery(("(select name from Person limit 4) " + "UNION (select street from sch2.Address limit 1) limit ? offset ?"));
        checkQuery("(select 2 a) union all (select 1) order by 1");
        checkQuery("(select 2 a) union all (select 1) order by a desc nulls first limit ? offset ?");
        checkQuery("select public.\"#\".\"@\" from (select 1 as \"@\") \"#\"");
        // checkQuery("select sch.\"#\".\"@\" from (select 1 as \"@\") \"#\""); // Illegal query.
        checkQuery("select \"#\".\"@\" from (select 1 as \"@\") \"#\"");
        checkQuery("select \"@\" from (select 1 as \"@\") \"#\"");
        checkQuery("select sch1.\"#\".old from sch1.Person \"#\"");
        checkQuery("select sch1.\"#\".old from Person \"#\"");
        checkQuery("select \"#\".old from Person \"#\"");
        checkQuery("select old from Person \"#\"");
        // checkQuery("select Person.old from Person \"#\""); // Illegal query.
        checkQuery("select \"#\".* from Person \"#\"");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testUseIndexHints() throws Exception {
        checkQuery("select * from Person use index (\"PERSON_NAME_IDX\")");
        checkQuery("select * from Person use index (\"PERSON_PARENTNAME_IDX\")");
        checkQuery("select * from Person use index (\"PERSON_NAME_IDX\", \"PERSON_PARENTNAME_IDX\")");
        checkQuery("select * from Person use index ()");
        checkQuery("select * from Person p use index (\"PERSON_NAME_IDX\")");
        checkQuery("select * from Person p use index (\"PERSON_PARENTNAME_IDX\")");
        checkQuery("select * from Person p use index (\"PERSON_NAME_IDX\", \"PERSON_PARENTNAME_IDX\")");
        checkQuery("select * from Person p use index ()");
    }

    /**
     * Query AST transformation heavily depends on this behavior.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testParseTableFilter() throws Exception {
        Prepared prepared = parse(("select Person.old, p1.old, p1.addrId from Person, Person p1 " + "where exists(select 1 from sch2.Address a where a.id = p1.addrId)"));
        GridSqlSelect select = ((GridSqlSelect) (new GridSqlQueryParser(false).parse(prepared)));
        GridSqlJoin join = ((GridSqlJoin) (select.from()));
        GridSqlTable tbl1 = ((GridSqlTable) (join.leftTable()));
        GridSqlAlias tbl2Alias = ((GridSqlAlias) (join.rightTable()));
        GridSqlTable tbl2 = tbl2Alias.child();
        // Must be distinct objects, even if it is the same table.
        assertNotSame(tbl1, tbl2);
        assertNotNull(tbl1.dataTable());
        assertNotNull(tbl2.dataTable());
        assertSame(tbl1.dataTable(), tbl2.dataTable());
        GridSqlColumn col1 = ((GridSqlColumn) (select.column(0)));
        GridSqlColumn col2 = ((GridSqlColumn) (select.column(1)));
        assertSame(tbl1, col1.expressionInFrom());
        // Alias in FROM must be included in column.
        assertSame(tbl2Alias, col2.expressionInFrom());
        // In EXISTS we must correctly reference the column from the outer query.
        GridSqlAst exists = select.where();
        GridSqlSubquery subqry = exists.child();
        GridSqlSelect subSelect = subqry.child();
        GridSqlColumn p1AddrIdCol = ((GridSqlColumn) (select.column(2)));
        assertEquals("ADDRID", p1AddrIdCol.column().getName());
        assertSame(tbl2Alias, p1AddrIdCol.expressionInFrom());
        GridSqlColumn p1AddrIdColExists = subSelect.where().child(1);
        assertEquals("ADDRID", p1AddrIdCol.column().getName());
        assertSame(tbl2Alias, p1AddrIdColExists.expressionInFrom());
    }

    /**
     *
     */
    @Test
    public void testParseMerge() throws Exception {
        /* Plain rows w/functions, operators, defaults, and placeholders. */
        checkQuery("merge into Person(old, name) values(5, 'John')");
        checkQuery("merge into Person(name) values(DEFAULT)");
        checkQuery("merge into Person(name) values(DEFAULT), (null)");
        checkQuery("merge into Person(name, parentName) values(DEFAULT, null), (?, ?)");
        checkQuery("merge into Person(old, name) values(5, 'John',), (6, 'Jack')");
        checkQuery("merge into Person(old, name) values(5 * 3, DEFAULT,)");
        checkQuery("merge into Person(old, name) values(ABS(-8), 'Max')");
        checkQuery("merge into Person(old, name) values(5, 'Jane'), (DEFAULT, DEFAULT), (6, 'Jill')");
        checkQuery("merge into Person(old, name, parentName) values(8 * 7, DEFAULT, 'Unknown')");
        checkQuery(("merge into Person(old, name, parentName) values" + (("(2016 - 1828, CONCAT('Leo', 'Tolstoy'), CONCAT(?, 'Tolstoy'))," + "(?, 'AlexanderPushkin', null),") + "(ABS(1821 - 2016), CONCAT('Fyodor', null, UPPER(CONCAT(SQRT(?), 'dostoevsky'))), DEFAULT)")));
        checkQuery(("merge into Person(date, old, name, parentName, addrId) values " + "('20160112', 1233, 'Ivan Ivanov', 'Peter Ivanov', 123)"));
        checkQuery(("merge into Person(date, old, name, parentName, addrId) values " + "(CURRENT_DATE(), RAND(), ASCII('Hi'), INSERT('Leo Tolstoy', 4, 4, 'Max'), ASCII('HI'))"));
        checkQuery(("merge into Person(date, old, name, parentName, addrId) values " + "(TRUNCATE(TIMESTAMP '2015-12-31 23:59:59'), POWER(3,12), NULL, DEFAULT, DEFAULT)"));
        checkQuery(("merge into Person(old, name) select ASCII(parentName), INSERT(parentName, 4, 4, 'Max') from " + "Person where date='2011-03-12'"));
        /* KEY clause. */
        checkQuery("merge into Person(_key, old, name) key(_key) values('a', 5, 'John')");
        checkQuery("merge into SCH3.Person(id, old, name) key(id) values(1, 5, 'John')");
        checkQuery("merge into SCH3.Person(_key, old, name) key(_key) values(?, 5, 'John')");
        checkQuery("merge into SCH3.Person(_key, id, old, name) key(_key, id) values(?, ?, 5, 'John')");
        assertParseThrows("merge into Person(old, name) key(name) values(5, 'John')", IgniteSQLException.class, "Invalid column name in KEYS clause of MERGE - it may include only key and/or affinity columns: NAME");
        assertParseThrows("merge into SCH3.Person(id, stuff, old, name) key(stuff) values(1, 'x', 5, 'John')", IgniteSQLException.class, ("Invalid column name in KEYS clause of MERGE - it may include only key and/or " + "affinity columns: STUFF"));
        /* Subqueries. */
        checkQuery("merge into Person(old, name) select old, parentName from Person");
        checkQuery("merge into Person(old, name) select old, parentName from Person where old > 5");
        checkQuery("merge into Person(old, name) select 5, 'John'");
        checkQuery(("merge into Person(old, name) select p1.old, 'Name' from person p1 join person p2 on " + "p2.name = p1.parentName where p2.old > 30"));
        checkQuery(("merge into Person(old) select 5 from Person UNION select street from sch2.Address limit ? " + "offset ?"));
    }

    /**
     *
     */
    @Test
    public void testParseInsert() throws Exception {
        /* Plain rows w/functions, operators, defaults, and placeholders. */
        checkQuery("insert into Person(old, name) values(5, 'John')");
        checkQuery("insert into Person(name) values(DEFAULT)");
        checkQuery("insert into Person default values");
        checkQuery("insert into Person() values()");
        checkQuery("insert into Person(name) values(DEFAULT), (null)");
        checkQuery("insert into Person(name) values(DEFAULT),");
        checkQuery("insert into Person(name, parentName) values(DEFAULT, null), (?, ?)");
        checkQuery("insert into Person(old, name) values(5, 'John',), (6, 'Jack')");
        checkQuery("insert into Person(old, name) values(5 * 3, DEFAULT,)");
        checkQuery("insert into Person(old, name) values(ABS(-8), 'Max')");
        checkQuery("insert into Person(old, name) values(5, 'Jane'), (DEFAULT, DEFAULT), (6, 'Jill')");
        checkQuery("insert into Person(old, name, parentName) values(8 * 7, DEFAULT, 'Unknown')");
        checkQuery(("insert into Person(old, name, parentName) values" + (("(2016 - 1828, CONCAT('Leo', 'Tolstoy'), CONCAT(?, 'Tolstoy'))," + "(?, 'AlexanderPushkin', null),") + "(ABS(1821 - 2016), CONCAT('Fyodor', null, UPPER(CONCAT(SQRT(?), 'dostoevsky'))), DEFAULT),")));
        checkQuery(("insert into Person(date, old, name, parentName, addrId) values " + "('20160112', 1233, 'Ivan Ivanov', 'Peter Ivanov', 123)"));
        checkQuery(("insert into Person(date, old, name, parentName, addrId) values " + "(CURRENT_DATE(), RAND(), ASCII('Hi'), INSERT('Leo Tolstoy', 4, 4, 'Max'), ASCII('HI'))"));
        checkQuery(("insert into Person(date, old, name, parentName, addrId) values " + "(TRUNCATE(TIMESTAMP '2015-12-31 23:59:59'), POWER(3,12), NULL, DEFAULT, DEFAULT)"));
        checkQuery("insert into Person SET old = 5, name = 'John'");
        checkQuery(("insert into Person SET name = CONCAT('Fyodor', null, UPPER(CONCAT(SQRT(?), 'dostoevsky'))), " + "old = select (5, 6)"));
        checkQuery(("insert into Person(old, name) select ASCII(parentName), INSERT(parentName, 4, 4, 'Max') from " + "Person where date='2011-03-12'"));
        /* Subqueries. */
        checkQuery("insert into Person(old, name) select old, parentName from Person");
        checkQuery("insert into Person(old, name) direct sorted select old, parentName from Person");
        checkQuery("insert into Person(old, name) sorted select old, parentName from Person where old > 5");
        checkQuery("insert into Person(old, name) select 5, 'John'");
        checkQuery(("insert into Person(old, name) select p1.old, 'Name' from person p1 join person p2 on " + "p2.name = p1.parentName where p2.old > 30"));
        checkQuery(("insert into Person(old) select 5 from Person UNION select street from sch2.Address limit ? " + "offset ?"));
    }

    /**
     *
     */
    @Test
    public void testParseDelete() throws Exception {
        checkQuery("delete from Person");
        checkQuery("delete from Person p where p.old > ?");
        checkQuery("delete from Person where old in (select (40, 41, 42))");
        checkQuery("delete top 5 from Person where old in (select (40, 41, 42))");
        checkQuery("delete top ? from Person where old > 5 and length(name) < ?");
        checkQuery("delete from Person where name in ('Ivan', 'Peter') limit 20");
        checkQuery("delete from Person where name in ('Ivan', ?) limit ?");
    }

    /**
     *
     */
    @Test
    public void testParseUpdate() throws Exception {
        checkQuery("update Person set name='Peter'");
        checkQuery("update Person per set name='Peter', old = 5");
        checkQuery("update Person p set name='Peter' limit 20");
        checkQuery("update Person p set name='Peter', old = length('zzz') limit 20");
        checkQuery("update Person p set name=DEFAULT, old = null limit ?");
        checkQuery("update Person p set name=? where old >= ? and old < ? limit ?");
        checkQuery(("update Person p set name=(select a.Street from sch2.Address a where a.id=p.addrId), old = " + "(select 42) where old = sqrt(?)"));
        checkQuery("update Person p set (name, old) = (select 'Peter', 42)");
        checkQuery("update Person p set (name, old) = (select street, id from sch2.Address where id > 5 and id <= ?)");
    }

    /**
     *
     */
    @Test
    public void testParseCreateIndex() throws Exception {
        assertCreateIndexEquals(GridQueryParsingTest.buildCreateIndex(null, "Person", "sch1", false, SORTED, DFLT_INLINE_SIZE, "name", true), "create index on Person (name)");
        assertCreateIndexEquals(GridQueryParsingTest.buildCreateIndex("idx", "Person", "sch1", false, SORTED, DFLT_INLINE_SIZE, "name", true), "create index idx on Person (name ASC)");
        assertCreateIndexEquals(GridQueryParsingTest.buildCreateIndex("idx", "Person", "sch1", false, GEOSPATIAL, DFLT_INLINE_SIZE, "name", true), "create spatial index sch1.idx on sch1.Person (name ASC)");
        assertCreateIndexEquals(GridQueryParsingTest.buildCreateIndex("idx", "Person", "sch1", true, SORTED, DFLT_INLINE_SIZE, "name", true), "create index if not exists sch1.idx on sch1.Person (name)");
        // When we specify schema for the table and don't specify it for the index, resulting schema is table's
        assertCreateIndexEquals(GridQueryParsingTest.buildCreateIndex("idx", "Person", "sch1", true, SORTED, DFLT_INLINE_SIZE, "name", false), "create index if not exists idx on sch1.Person (name dEsC)");
        assertCreateIndexEquals(GridQueryParsingTest.buildCreateIndex("idx", "Person", "sch1", true, GEOSPATIAL, DFLT_INLINE_SIZE, "old", true, "name", false), "create spatial index if not exists idx on Person (old, name desc)");
        // Schemas for index and table must match
        assertParseThrows("create index if not exists sch2.idx on sch1.Person (name)", DbException.class, "Schema name must match");
        assertParseThrows("create hash index if not exists idx on Person (name)", IgniteSQLException.class, "Only SPATIAL modifier is supported for CREATE INDEX");
        assertParseThrows("create unique index if not exists idx on Person (name)", IgniteSQLException.class, "Only SPATIAL modifier is supported for CREATE INDEX");
        assertParseThrows("create primary key on Person (name)", IgniteSQLException.class, "Only SPATIAL modifier is supported for CREATE INDEX");
        assertParseThrows("create primary key hash on Person (name)", IgniteSQLException.class, "Only SPATIAL modifier is supported for CREATE INDEX");
        assertParseThrows("create index on Person (name nulls first)", IgniteSQLException.class, "NULLS FIRST and NULLS LAST modifiers are not supported for index columns");
        assertParseThrows("create index on Person (name desc nulls last)", IgniteSQLException.class, "NULLS FIRST and NULLS LAST modifiers are not supported for index columns");
    }

    /**
     *
     */
    @Test
    public void testParseDropIndex() throws Exception {
        // Schema that is not set defaults to default schema of connection which is sch1
        assertDropIndexEquals(GridQueryParsingTest.buildDropIndex("idx", "sch1", false), "drop index idx");
        assertDropIndexEquals(GridQueryParsingTest.buildDropIndex("idx", "sch1", true), "drop index if exists idx");
        assertDropIndexEquals(GridQueryParsingTest.buildDropIndex("idx", "sch1", true), "drop index if exists sch1.idx");
        assertDropIndexEquals(GridQueryParsingTest.buildDropIndex("idx", "sch1", false), "drop index sch1.idx");
        // Message is null as long as it may differ from system to system, so we just check for exceptions
        assertParseThrows("drop index schema2.", DbException.class, null);
        assertParseThrows("drop index", DbException.class, null);
        assertParseThrows("drop index if exists", DbException.class, null);
        assertParseThrows("drop index if exists schema2.", DbException.class, null);
    }

    /**
     *
     */
    @Test
    public void testParseDropTable() throws Exception {
        // Schema that is not set defaults to default schema of connection which is sch1
        assertDropTableEquals(GridQueryParsingTest.buildDropTable("sch1", "tbl", false), "drop table tbl");
        assertDropTableEquals(GridQueryParsingTest.buildDropTable("sch1", "tbl", true), "drop table if exists tbl");
        assertDropTableEquals(GridQueryParsingTest.buildDropTable("sch1", "tbl", true), "drop table if exists sch1.tbl");
        assertDropTableEquals(GridQueryParsingTest.buildDropTable("sch1", "tbl", false), "drop table sch1.tbl");
        // Message is null as long as it may differ from system to system, so we just check for exceptions
        assertParseThrows("drop table schema2.", DbException.class, null);
        assertParseThrows("drop table", DbException.class, null);
        assertParseThrows("drop table if exists", DbException.class, null);
        assertParseThrows("drop table if exists schema2.", DbException.class, null);
    }

    /**
     *
     */
    @Test
    public void testParseCreateTable() throws Exception {
        assertCreateTableEquals(GridQueryParsingTest.buildCreateTable("sch1", "Person", "cache", F.asList("id", "city"), true, GridQueryParsingTest.c("id", INT), GridQueryParsingTest.c("city", STRING), GridQueryParsingTest.c("name", STRING), GridQueryParsingTest.c("surname", STRING), GridQueryParsingTest.c("age", INT)), ("CREATE TABLE IF NOT EXISTS sch1.\"Person\" (\"id\" integer, \"city\" varchar," + (" \"name\" varchar, \"surname\" varchar, \"age\" integer, PRIMARY KEY (\"id\", \"city\")) WITH " + "\"template=cache\"")));
        assertCreateTableEquals(GridQueryParsingTest.buildCreateTable("sch1", "Person", "cache", F.asList("id"), false, GridQueryParsingTest.c("id", INT), GridQueryParsingTest.c("city", STRING), GridQueryParsingTest.c("name", STRING), GridQueryParsingTest.c("surname", STRING), GridQueryParsingTest.cn("age", INT)), ("CREATE TABLE sch1.\"Person\" (\"id\" integer PRIMARY KEY, \"city\" varchar," + (" \"name\" varchar, \"surname\" varchar, \"age\" integer NOT NULL) WITH " + "\"template=cache\"")));
        assertParseThrows("create table Person (id int)", IgniteSQLException.class, "No PRIMARY KEY defined for CREATE TABLE");
        assertParseThrows("create table Person (id int) AS SELECT 2 * 2", IgniteSQLException.class, "CREATE TABLE ... AS ... syntax is not supported");
        assertParseThrows("create table Person (id int primary key)", IgniteSQLException.class, "Table must have at least one non PRIMARY KEY column.");
        assertParseThrows("create table Person (id int primary key, age int unique) WITH \"template=cache\"", IgniteSQLException.class, "Too many constraints - only PRIMARY KEY is supported for CREATE TABLE");
        assertParseThrows("create table Person (id int auto_increment primary key, age int) WITH \"template=cache\"", IgniteSQLException.class, "AUTO_INCREMENT columns are not supported");
        assertParseThrows("create table Person (id int primary key check id > 0, age int) WITH \"template=cache\"", IgniteSQLException.class, "Column CHECK constraints are not supported [colName=ID]");
        assertParseThrows("create table Person (id int as age * 2 primary key, age int) WITH \"template=cache\"", IgniteSQLException.class, "Computed columns are not supported [colName=ID]");
        assertParseThrows("create table Int (_key int primary key, _val int) WITH \"template=cache\"", IgniteSQLException.class, "Direct specification of _KEY and _VAL columns is forbidden");
        assertParseThrows(("create table Person (" + (((("unquoted_id LONG, " + "\"quoted_id\" LONG, ") + "PERSON_NAME VARCHAR(255), ") + "PRIMARY KEY (UNQUOTED_ID, quoted_id)) ") + "WITH \"template=cache\"")), IgniteSQLException.class, "PRIMARY KEY column is not defined: QUOTED_ID");
        assertParseThrows(("create table Person (" + (((("unquoted_id LONG, " + "\"quoted_id\" LONG, ") + "PERSON_NAME VARCHAR(255), ") + "PRIMARY KEY (\"unquoted_id\", \"quoted_id\")) ") + "WITH \"template=cache\"")), IgniteSQLException.class, "PRIMARY KEY column is not defined: unquoted_id");
    }

    /**
     *
     */
    @Test
    public void testParseCreateTableWithDefaults() {
        assertParseThrows(("create table Person (id int primary key, age int, " + "ts TIMESTAMP default CURRENT_TIMESTAMP()) WITH \"template=cache\""), IgniteSQLException.class, "Non-constant DEFAULT expressions are not supported [colName=TS]");
        assertParseThrows(("create table Person (id int primary key, age int default 'test') " + "WITH \"template=cache\""), IgniteSQLException.class, ("Invalid default value for column. " + "[colName=AGE, colType=INTEGER, dfltValueType=VARCHAR]"));
        assertParseThrows(("create table Person (id int primary key, name varchar default 1) " + "WITH \"template=cache\""), IgniteSQLException.class, ("Invalid default value for column. " + "[colName=NAME, colType=VARCHAR, dfltValueType=INTEGER]"));
    }

    /**
     *
     */
    @Test
    public void testParseAlterTableAddColumn() throws Exception {
        assertAlterTableAddColumnEquals(GridQueryParsingTest.buildAlterTableAddColumn("SCH2", "Person", false, false, GridQueryParsingTest.c("COMPANY", STRING)), "ALTER TABLE SCH2.Person ADD company varchar");
        assertAlterTableAddColumnEquals(GridQueryParsingTest.buildAlterTableAddColumn("SCH2", "Person", true, true, GridQueryParsingTest.c("COMPANY", STRING)), "ALTER TABLE IF EXISTS SCH2.Person ADD if not exists company varchar");
        assertAlterTableAddColumnEquals(GridQueryParsingTest.buildAlterTableAddColumn("SCH2", "Person", false, true, GridQueryParsingTest.c("COMPANY", STRING), GridQueryParsingTest.c("city", STRING)), "ALTER TABLE IF EXISTS SCH2.Person ADD (company varchar, \"city\" varchar)");
        assertAlterTableAddColumnEquals(GridQueryParsingTest.buildAlterTableAddColumn("SCH2", "City", false, true, GridQueryParsingTest.c("POPULATION", INT)), "ALTER TABLE IF EXISTS SCH2.\"City\" ADD (population int)");
        assertAlterTableAddColumnEquals(GridQueryParsingTest.buildAlterTableAddColumn("SCH2", "City", false, true, GridQueryParsingTest.cn("POPULATION", INT)), "ALTER TABLE IF EXISTS SCH2.\"City\" ADD (population int NOT NULL)");
        // There's no table with such name, but H2 parsing does not fail just yet.
        assertAlterTableAddColumnEquals(GridQueryParsingTest.buildAlterTableAddColumn("SCH2", "City", false, false, GridQueryParsingTest.c("POPULATION", INT)), "ALTER TABLE SCH2.\"City\" ADD (population int)");
        assertAlterTableAddColumnEquals(GridQueryParsingTest.buildAlterTableAddColumn("SCH2", "Person", true, false, GridQueryParsingTest.c("NAME", STRING)), "ALTER TABLE SCH2.Person ADD if not exists name varchar");
        // There's a column with such name, but H2 parsing does not fail just yet.
        assertAlterTableAddColumnEquals(GridQueryParsingTest.buildAlterTableAddColumn("SCH2", "Person", false, false, GridQueryParsingTest.c("NAME", STRING)), "ALTER TABLE SCH2.Person ADD name varchar");
        // IF NOT EXISTS with multiple columns.
        assertParseThrows("ALTER TABLE IF EXISTS SCH2.Person ADD if not exists (company varchar, city varchar)", DbException.class, null);
        // Both BEFORE and AFTER keywords.
        assertParseThrows("ALTER TABLE IF EXISTS SCH2.Person ADD if not exists company varchar before addrid", IgniteSQLException.class, "ALTER TABLE ADD COLUMN BEFORE/AFTER is not supported");
        // No such schema.
        assertParseThrows("ALTER TABLE SCH5.\"Person\" ADD (city varchar)", DbException.class, null);
    }

    /**
     *
     */
    public static class PersonKey implements Serializable {
        /**
         *
         */
        @QuerySqlField
        @AffinityKeyMapped
        public int id;

        /**
         * Should not be allowed in KEY clause of MERGE.
         */
        @QuerySqlField
        public String stuff;
    }

    /**
     *
     */
    public static class Person implements Serializable {
        @QuerySqlField(index = true)
        public Date date = new Date(System.currentTimeMillis());

        @QuerySqlField(index = true)
        public String name = "Ivan";

        @QuerySqlField(index = true)
        public String parentName;

        @QuerySqlField(index = true)
        public int addrId;

        @QuerySqlField
        public Integer[] addrIds;

        @QuerySqlField(index = true)
        public int old;
    }

    /**
     *
     */
    public static class Address implements Serializable {
        @QuerySqlField(index = true)
        public int id;

        @QuerySqlField(index = true)
        public int streetNumber;

        @QuerySqlField(index = true)
        public String street = "Nevskiy";
    }
}

