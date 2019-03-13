package com.zendesk.maxwell.schema.ddl;


import ColumnPosition.Position.AFTER;
import com.zendesk.maxwell.schema.columndef.ColumnDef;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class DDLParserTest {
    @Test
    public void testBasic() {
        MaxwellSQLSyntaxError e = null;
        Assert.assertThat(parseAlter("ALTER TABLE `foo` ADD col1 text"), CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
        try {
            parseAlter("ALTER TABLE foolkj `foo` lkjlkj");
        } catch (MaxwellSQLSyntaxError err) {
            e = err;
        }
        Assert.assertThat(e, CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
    }

    @Test
    public void testColumnAdd() {
        TableAlter a = parseAlter("ALTER TABLE `foo`.`bar` ADD column `col1` text AFTER `afterCol`");
        Assert.assertThat(a, CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
        AddColumnMod m = ((AddColumnMod) (a.columnMods.get(0)));
        Assert.assertThat(m.name, CoreMatchers.is("col1"));
        Assert.assertThat(m.definition, CoreMatchers.not(CoreMatchers.nullValue()));
        Assert.assertThat(m.position.position, CoreMatchers.is(AFTER));
        Assert.assertThat(m.position.afterColumn, CoreMatchers.is("afterCol"));
    }

    @Test
    public void testIntColumnTypes_1() {
        TableAlter a = parseAlter("alter table foo add column `int` int(11) unsigned not null AFTER `afterCol`");
        AddColumnMod m = ((AddColumnMod) (a.columnMods.get(0)));
        Assert.assertThat(m.name, CoreMatchers.is("int"));
        Assert.assertThat(m.definition, CoreMatchers.instanceOf(IntColumnDef.class));
        IntColumnDef i = ((IntColumnDef) (m.definition));
        Assert.assertThat(i.getName(), CoreMatchers.is("int"));
        Assert.assertThat(i.getType(), CoreMatchers.is("int"));
        Assert.assertThat(i.isSigned(), CoreMatchers.is(false));
    }

    @Test
    public void testIntColumnTypes_2() {
        TableAlter a = parseAlter("alter table `fie` add column baz bigINT null");
        AddColumnMod m = ((AddColumnMod) (a.columnMods.get(0)));
        Assert.assertThat(m.name, CoreMatchers.is("baz"));
        BigIntColumnDef b = ((BigIntColumnDef) (m.definition));
        Assert.assertThat(b.getType(), CoreMatchers.is("bigint"));
        Assert.assertThat(b.isSigned(), CoreMatchers.is(true));
        Assert.assertThat(b.getName(), CoreMatchers.is("baz"));
    }

    @Test
    public void testVarchar() {
        TableAlter a = parseAlter("alter table no.no add column mocha varchar(255) character set latin1 not null");
        AddColumnMod m = ((AddColumnMod) (a.columnMods.get(0)));
        Assert.assertThat(m.name, CoreMatchers.is("mocha"));
        StringColumnDef b = ((StringColumnDef) (m.definition));
        Assert.assertThat(b.getType(), CoreMatchers.is("varchar"));
        Assert.assertThat(b.getCharset(), CoreMatchers.is("latin1"));
    }

    @Test
    public void testText() {
        TableAlter a = parseAlter("alter table no.no add column mocha TEXT character set 'utf8' collate 'utf8_foo'");
        AddColumnMod m = ((AddColumnMod) (a.columnMods.get(0)));
        StringColumnDef b = ((StringColumnDef) (m.definition));
        Assert.assertThat(b.getType(), CoreMatchers.is("text"));
        Assert.assertThat(b.getCharset(), CoreMatchers.is("utf8"));
    }

    @Test
    public void testDefault() {
        TableAlter a = parseAlter("alter table no.no add column mocha TEXT default 'hello'''''");
        AddColumnMod m = ((AddColumnMod) (a.columnMods.get(0)));
        StringColumnDef b = ((StringColumnDef) (m.definition));
        Assert.assertThat(b.getType(), CoreMatchers.is("text"));
    }

    @Test
    public void testLots() {
        TableAlter a = parseAlter(("alter table bar add column m TEXT character set utf8 " + (((((("default null " + "auto_increment ") + "unique key ") + "primary key ") + "comment 'bar' ") + "column_format fixed ") + "storage disk")));
        AddColumnMod m = ((AddColumnMod) (a.columnMods.get(0)));
        StringColumnDef b = ((StringColumnDef) (m.definition));
        Assert.assertThat(b.getType(), CoreMatchers.is("text"));
        Assert.assertThat(b.getCharset(), CoreMatchers.is("utf8"));
    }

    @Test
    public void testMultipleColumns() {
        TableAlter a = parseAlter("alter table bar add column m int(11) unsigned not null, add p varchar(255)");
        Assert.assertThat(a.columnMods.size(), CoreMatchers.is(2));
        Assert.assertThat(a.columnMods.get(0).name, CoreMatchers.is("m"));
        Assert.assertThat(a.columnMods.get(1).name, CoreMatchers.is("p"));
    }

    @Test
    public void testMultipleColumnWithParens() {
        TableAlter a = parseAlter("alter table bar add column (m int(11) unsigned not null, p varchar(255))");
        Assert.assertThat(a.columnMods.size(), CoreMatchers.is(2));
        Assert.assertThat(a.columnMods.get(0).name, CoreMatchers.is("m"));
        Assert.assertThat(a.columnMods.get(1).name, CoreMatchers.is("p"));
    }

    @Test
    public void testParsingSomeAlters() {
        String[] testSQL = new String[]{ "alter table t add column c varchar(255) default 'string1' 'string2'", "alter table t add column mortgage_item BIT(4) NOT NULL DEFAULT 0b0000", "alter table t add column mortgage_item BIT(4) NOT NULL DEFAULT 'b'01010", "alter table t add column mortgage_item BIT(4) NOT NULL DEFAULT 'B'01010", "alter database d DEFAULT CHARACTER SET = 'utf8'", "alter database d UPGRADE DATA DIRECTORY NAME", "alter schema d COLLATE foo", "alter table t add index `foo` using btree (`a`, `cd`) key_block_size=123", "alter table t add index `foo` using btree (`a`, `cd`) invisible key_block_size=123", "alter table t add index `foo` using btree (`a`, `cd`) comment 'hello' key_block_size=12", "alter table t add key bar (d)", "alter table t add constraint `foo` primary key using btree (id)", "alter table t add primary key (`id`)", "alter table t add constraint unique key (`id`)", "alter table t add fulltext key (`id`)", "alter table t add index foo (a desc)", "alter table t add index foo (a asc)", "alter table t add index foo (a) COMMENT 'hello world'", "alter table t add spatial key (`id`)", "ALTER TABLE foo ADD feee int(11) COLLATE utf8_unicode_ci NOT NULL DEFAULT '0' COMMENT 'eee' AFTER id", "alter table t alter column `foo` SET DEFAULT 112312", "alter table t alter column `foo` SET DEFAULT 1.2", "alter table t alter column `foo` SET DEFAULT 'foo'", "alter table t alter column `foo` SET DEFAULT true", "alter table t alter column `foo` SET DEFAULT false", "alter table t alter column `foo` SET DEFAULT -1", "alter table t alter column `foo` drop default", "alter table t alter index `foo` VISIBLE", "alter table t alter index bar INVISIBLE", "alter table t CHARACTER SET latin1 COLLATE = 'utf8'", "ALTER TABLE `test` ENGINE=`InnoDB` CHARACTER SET latin1", "alter table t DROP PRIMARY KEY", "alter table t drop index `foo`", "alter table t disable keys", "alter table t enable keys", "alter table t order by `foor`, bar", "alter table tester add index (whatever(20), `f,` (2))", "create table t ( id int ) engine = innodb, auto_increment = 5", "alter table t engine=innodb", "alter table t auto_increment =5", "alter table t add column `foo` int, auto_increment = 5 engine=innodb, modify column bar int", "alter table t add column `foo` int,  ALGORITHM=copy", "alter table t add column `foo` int, algorithm copy", "alter table t add column `foo` int, algorithm instant", "alter table t add column `foo` int, algorithm copy, lock shared", "alter table t add column `foo` int, algorithm copy, lock=exclusive", "create table t (id int) engine=memory", "CREATE TABLE `t1` (id int, UNIQUE `int` (`int`))", "create table t2 (b varchar(10) not null unique) engine=MyISAM", "create TABLE shard_1.20151214foo ( r1 REAL, b2 REAL (2,2) )", "create TABLE shard_1.20151214 ( r1 REAL, b2 REAL (2,2) )", "create table `shard1.foo` ( `id.foo` int )", "create table `shard1.foo` ( `id.foo` int ) collate = `utf8_bin`", "ALTER TABLE .`users` CHANGE COLUMN `password` `password` VARCHAR(60) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL COMMENT 'Length 60 for Bcrypt'", "create table `shard1.foo` ( `id.foo` int ) collate = `utf8_bin`", "create table if not exists audit_payer_bank_details (event_time TIMESTAMP default CURRENT_TIMESTAMP())", "create table if not exists audit_bank_payer_details (event_time TIMESTAMP default LOCALTIME())", "create table nobody_pays_noone (event_time TIMESTAMP default localtimestamp)", "ALTER TABLE foo RENAME INDEX index_quote_request_follow_on_data_on_model_name TO index_quote_request_follow_on_data_on_model_class_name", "ALTER TABLE foo DROP COLUMN `ducati` CASCADE", "CREATE TABLE account_groups ( visible_to_all CHAR(1) DEFAULT 'N' NOT NULL CHECK (visible_to_all IN ('Y','N')))", "ALTER TABLE \"foo\" drop column a"// ansi-double-quoted tables
        , "create table vc11( id serial, name varchar(10) not null default \"\")" };
        for (String s : testSQL) {
            SchemaChange parsed = parse(s).get(0);
            Assert.assertThat((("Expected " + s) + "to parse"), parsed, CoreMatchers.not(CoreMatchers.nullValue()));
        }
    }

    @Test
    public void testSQLBlacklist() {
        String[] testSQL = new String[]{ "CREATE -- comment\nEVENT foo", "/*!50003 DROP FUNCTION IF EXISTS `DAY_NAME_FROM_NUMER` */", "ALTER DEFINER=foo VIEW", "CREATE VIEW foo", "CREATE TRIGGER foo", "CREATE DEFINER=`dba`@`localhost` TRIGGER `pt_osc_zd_shard485_prod_cf_values_del` ... ", "CREATE EVENT foo ", "DROP EVENT foo bar", "ALTER ALGORITHM = UNDEFINED DEFINER='view'@'localhost' SQL SECURITY DEFINER VIEW `fooview` as (SELECT * FROM FOO)" + "VIEW view_name [(alskdj lk jdlfka j dlkjd lk", "CREATE TEMPORARY TABLE 172898_16841_transmem SELECT t.* FROM map.transmem AS t", "DROP TEMPORARY TABLE IF EXISTS 172898_16841_transmem", "ALTER TEMPORARY TABLE 172898_16841_transmem ADD something VARCHAR(1)", "/* hi bob */ CREATE EVENT FOO", "DELETE FROM `foo`.`bar`", "CREATE ROLE 'administrator', 'developer'", "SET ROLE 'role1', 'role2'", "SET DEFAULT ROLE administrator, developer TO 'joe'@'10.0.0.1'", "DROP ROLE 'role1'" };
        for (String s : testSQL) {
            Assert.assertThat(SchemaChange.parse("default_db", s), CoreMatchers.is(CoreMatchers.nullValue()));
        }
    }

    @Test
    public void testChangeColumn() {
        TableAlter a = parseAlter("alter table c CHANGE column `foo` bar int(20) unsigned default 'foo' not null");
        Assert.assertThat(a.columnMods.size(), CoreMatchers.is(1));
        Assert.assertThat(a.columnMods.get(0), CoreMatchers.instanceOf(ChangeColumnMod.class));
        ChangeColumnMod c = ((ChangeColumnMod) (a.columnMods.get(0)));
        Assert.assertThat(c.name, CoreMatchers.is("foo"));
        Assert.assertThat(c.definition.getName(), CoreMatchers.is("bar"));
        Assert.assertThat(c.definition.getType(), CoreMatchers.is("int"));
    }

    @Test
    public void testModifyColumn() throws IOException {
        TableAlter a = parseAlter("alter table c MODIFY column `foo` bigint(20) unsigned default 'foo' not null");
        ChangeColumnMod c = ((ChangeColumnMod) (a.columnMods.get(0)));
        Assert.assertThat(c.name, CoreMatchers.is("foo"));
        Assert.assertThat(c.definition.getName(), CoreMatchers.is("foo"));
        Assert.assertThat(c.definition.getType(), CoreMatchers.is("bigint"));
    }

    @Test
    public void testDropColumn() {
        RemoveColumnMod remove;
        TableAlter a = parseAlter("alter table c drop column `drop`");
        Assert.assertThat(a.columnMods.size(), CoreMatchers.is(1));
        Assert.assertThat(a.columnMods.get(0), CoreMatchers.instanceOf(RemoveColumnMod.class));
        remove = ((RemoveColumnMod) (a.columnMods.get(0)));
        Assert.assertThat(remove.name, CoreMatchers.is("drop"));
    }

    @Test
    public void testRenameTable() {
        TableAlter a = parseAlter("alter table c rename to `foo`");
        Assert.assertThat(a.newTableName, CoreMatchers.is("foo"));
        a = parseAlter("alter table c rename to `foo`.`bar`");
        Assert.assertThat(a.newDatabase, CoreMatchers.is("foo"));
        Assert.assertThat(a.newTableName, CoreMatchers.is("bar"));
    }

    @Test
    public void testConvertCharset() {
        TableAlter a = parseAlter("alter table c convert to character set 'latin1'");
        Assert.assertThat(a.convertCharset, CoreMatchers.is("latin1"));
        a = parseAlter("alter table c charset=utf8");
        Assert.assertThat(a.defaultCharset, CoreMatchers.is("utf8"));
        a = parseAlter("alter table c character set = 'utf8'");
        Assert.assertThat(a.defaultCharset, CoreMatchers.is("utf8"));
    }

    @Test
    public void testCreateTable() {
        TableCreate c = parseCreate("CREATE TABLE `foo` ( id int(11) auto_increment not null, `textcol` mediumtext character set 'utf8' not null )");
        Assert.assertThat(c.database, CoreMatchers.is("default_db"));
        Assert.assertThat(c.table, CoreMatchers.is("foo"));
        Assert.assertThat(c.columns.size(), CoreMatchers.is(2));
        Assert.assertThat(c.columns.get(0).getName(), CoreMatchers.is("id"));
        Assert.assertThat(c.columns.get(1).getName(), CoreMatchers.is("textcol"));
    }

    @Test
    public void testCreateTableWithIndexes() {
        TableCreate c = parseCreate(("CREATE TABLE `bar`.`foo` (" + ((((("id int(11) auto_increment PRIMARY KEY, " + "dt datetime, ") + "KEY `index_on_datetime` (dt), ") + "KEY (`something else`), ") + "INDEX USING BTREE (yet_again)") + ")")));
        Assert.assertThat(c, CoreMatchers.not(CoreMatchers.nullValue()));
    }

    @Test
    public void testCreateTableWithOptions() {
        TableCreate c = parseCreate(("CREATE TABLE `bar`.`foo` (" + (((("id int(11) auto_increment PRIMARY KEY" + ") ") + "ENGINE=innodb ") + "CHARACTER SET='latin1' ") + "ROW_FORMAT=FIXED")));
        Assert.assertThat(c, CoreMatchers.not(CoreMatchers.nullValue()));
    }

    @Test
    public void testDecimalWithSingleDigitPrecsion() {
        TableCreate c = parseCreate("CREATE TABLE test.chk (  group_name DECIMAL(8) NOT NULL)  ");
        Assert.assertThat(c, CoreMatchers.not(CoreMatchers.nullValue()));
    }

    @Test
    public void testDecimalWithDoubleDigitPrecision() {
        TableCreate c = parseCreate("CREATE TABLE test.chk (  group_name DECIMAL(8, 2) NOT NULL)  ");
        Assert.assertThat(c, CoreMatchers.not(CoreMatchers.nullValue()));
    }

    @Test
    public void testNumericType() {
        TableCreate c = parseCreate("CREATE TABLE test.chk (  group_name NUMERIC(8) NOT NULL)  ");
        Assert.assertThat(c, CoreMatchers.not(CoreMatchers.nullValue()));
    }

    @Test
    public void testCreateTableLikeTable() {
        TableCreate c = parseCreate("CREATE TABLE `foo` LIKE `bar`.`baz`");
        Assert.assertThat(c, CoreMatchers.not(CoreMatchers.nullValue()));
        Assert.assertThat(c.table, CoreMatchers.is("foo"));
        Assert.assertThat(c.likeDB, CoreMatchers.is("bar"));
        Assert.assertThat(c.likeTable, CoreMatchers.is("baz"));
    }

    @Test
    public void testDropTable() {
        List<SchemaChange> changes = parse("DROP TABLE IF exists `foo`.bar, `bar`.baz");
        Assert.assertThat(changes.size(), CoreMatchers.is(2));
        TableDrop d = ((TableDrop) (changes.get(0)));
        Assert.assertThat(d.table, CoreMatchers.is("bar"));
        Assert.assertThat(d.database, CoreMatchers.is("foo"));
    }

    @Test
    public void testCreateDatabase() {
        List<SchemaChange> changes = parse("CREATE DATABASE if not exists `foo` default character set='latin1'");
        DatabaseCreate create = ((DatabaseCreate) (changes.get(0)));
        Assert.assertThat(create.database, CoreMatchers.is("foo"));
        Assert.assertThat(create.charset, CoreMatchers.is("latin1"));
    }

    @Test
    public void testCreateSchema() {
        List<SchemaChange> changes = parse("CREATE SCHEMA if not exists `foo`");
        DatabaseCreate create = ((DatabaseCreate) (changes.get(0)));
        Assert.assertThat(create.database, CoreMatchers.is("foo"));
    }

    @Test
    public void testCommentSyntax() {
        List<SchemaChange> changes = parse("CREATE DATABASE if not exists `foo` default character set='latin1' /* generate by server */");
        Assert.assertThat(changes.size(), CoreMatchers.is(1));
    }

    @Test
    public void testCommentSyntax2() {
        List<SchemaChange> changes = parse("CREATE DATABASE if not exists `foo` -- inline comment!\n default character # another one\nset=\'latin1\' --one at the end");
        Assert.assertThat(changes.size(), CoreMatchers.is(1));
    }

    @Test
    public void testCommentSyntax3() {
        List<SchemaChange> changes = parse("/**/ CREATE DATABASE if not exists `foo`");
        Assert.assertThat(changes.size(), CoreMatchers.is(1));
    }

    @Test
    public void testCurrentTimestamp() {
        List<SchemaChange> changes = parse("CREATE TABLE `foo` ( `id` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP )");
        Assert.assertThat(changes.size(), CoreMatchers.is(1));
    }

    @Test
    public void testBinaryChar() {
        List<SchemaChange> changes = parse("CREATE TABLE `foo` ( `id` char(16) BINARY character set 'utf8' )");
        Assert.assertThat(changes.size(), CoreMatchers.is(1));
    }

    @Test
    public void testCharsetPositionIndependence() {
        TableCreate create = parseCreate("CREATE TABLE `foo` (id varchar(1) NOT NULL character set 'foo')");
        ColumnDef c = create.columns.get(0);
        Assert.assertThat(c, CoreMatchers.is(CoreMatchers.instanceOf(StringColumnDef.class)));
        Assert.assertThat(getCharset(), CoreMatchers.is("foo"));
        create = parseCreate("CREATE TABLE `foo` (id varchar(1) character set 'foo' NOT NULL)");
        c = create.columns.get(0);
        Assert.assertThat(c, CoreMatchers.is(CoreMatchers.instanceOf(StringColumnDef.class)));
        Assert.assertThat(getCharset(), CoreMatchers.is("foo"));
    }

    @Test
    public void testCreateTableNamedPrimaryKey() {
        /* not documented, but accepted and ignored to table the primary key. */
        TableCreate create = parseCreate("CREATE TABLE db (foo char(60) binary DEFAULT '' NOT NULL, PRIMARY KEY Host (foo,Db,User))");
        Assert.assertThat(create, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(create.pks.size(), CoreMatchers.is(3));
    }

    @Test
    public void testCommentsThatAreNotComments() {
        TableCreate create = parseCreate("CREATE TABLE /*! IF NOT EXISTS */ foo (id int primary key)");
        Assert.assertThat(create, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(create.ifNotExists, CoreMatchers.is(true));
    }

    @Test
    public void testBinaryColumnDefaults() {
        Assert.assertThat(parseCreate("CREATE TABLE foo (id boolean default true)"), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(parseCreate("CREATE TABLE foo (id boolean default false)"), CoreMatchers.is(CoreMatchers.notNullValue()));
    }

    @Test
    public void testAlterOrderBy() {
        Assert.assertThat(parseAlter("ALTER TABLE t1 ORDER BY t1.id, t1.status, t1.type_id, t1.user_id, t1.body"), CoreMatchers.is(CoreMatchers.notNullValue()));
    }

    @Test
    public void testCreateSchemaCharSet() {
        List<SchemaChange> changes = parse("CREATE SCHEMA IF NOT EXISTS `tblname` CHARACTER SET = default");
        Assert.assertThat(changes.size(), CoreMatchers.is(1));
    }

    @Test
    public void testMysqlTestFixedSQL() throws Exception {
        int i = 1;
        List<String> lines = Files.readAllLines(Paths.get(((getSQLDir()) + "/ddl/mysql-test-fixed.sql")), Charset.defaultCharset());
        for (String sql : lines) {
            parse(sql);
        }
    }

    @Test
    public void testMysqlTestPartitionSQL() throws Exception {
        int i = 1;
        boolean outputFirst = false;
        List<String> lines = Files.readAllLines(Paths.get(((getSQLDir()) + "/ddl/mysql-test-partition.sql")), Charset.defaultCharset());
        for (String sql : lines) {
            try {
                parse(sql);
            } catch (Exception e) {
                Assert.assertThat((((((e.getMessage()) + "\nline: ") + i) + ": ") + sql), true, CoreMatchers.is(false));
            }
            i++;
        }
    }

    @Test
    public void testMysqlGIS() throws Exception {
        List<String> lines = Files.readAllLines(Paths.get(((getSQLDir()) + "/ddl/mysql-test-gis.sql")), Charset.defaultCharset());
        for (String sql : lines) {
            parse(sql);
        }
    }
}

