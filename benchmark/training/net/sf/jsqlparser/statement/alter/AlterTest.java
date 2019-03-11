/**
 * -
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.statement.alter;


import AlterOperation.CHANGE;
import AlterOperation.MODIFY;
import java.util.Arrays;
import java.util.List;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.alter.AlterExpression.ColumnDataType;
import net.sf.jsqlparser.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;


public class AlterTest {
    @Test
    public void testAlterTableAddColumn() throws JSQLParserException {
        Statement stmt = CCJSqlParserUtil.parse("ALTER TABLE mytable ADD COLUMN mycolumn varchar (255)");
        Assert.assertTrue((stmt instanceof Alter));
        Alter alter = ((Alter) (stmt));
        Assert.assertEquals("mytable", alter.getTable().getFullyQualifiedName());
        AlterExpression alterExp = alter.getAlterExpressions().get(0);
        Assert.assertNotNull(alterExp);
        List<ColumnDataType> colDataTypes = alterExp.getColDataTypeList();
        Assert.assertEquals("mycolumn", colDataTypes.get(0).getColumnName());
        Assert.assertEquals("varchar (255)", colDataTypes.get(0).getColDataType().toString());
    }

    @Test
    public void testAlterTableAddColumn_ColumnKeyWordImplicit() throws JSQLParserException {
        Statement stmt = CCJSqlParserUtil.parse("ALTER TABLE mytable ADD mycolumn varchar (255)");
        Assert.assertTrue((stmt instanceof Alter));
        Alter alter = ((Alter) (stmt));
        Assert.assertEquals("mytable", alter.getTable().getFullyQualifiedName());
        AlterExpression alterExp = alter.getAlterExpressions().get(0);
        Assert.assertNotNull(alterExp);
        List<ColumnDataType> colDataTypes = alterExp.getColDataTypeList();
        Assert.assertEquals("mycolumn", colDataTypes.get(0).getColumnName());
        Assert.assertEquals("varchar (255)", colDataTypes.get(0).getColDataType().toString());
    }

    @Test
    public void testAlterTablePrimaryKey() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE animals ADD PRIMARY KEY (id)");
    }

    @Test
    public void testAlterTablePrimaryKeyDeferrable() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE animals ADD PRIMARY KEY (id) DEFERRABLE");
    }

    @Test
    public void testAlterTablePrimaryKeyNotDeferrable() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE animals ADD PRIMARY KEY (id) NOT DEFERRABLE");
    }

    @Test
    public void testAlterTablePrimaryKeyValidate() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE animals ADD PRIMARY KEY (id) VALIDATE");
    }

    @Test
    public void testAlterTablePrimaryKeyNoValidate() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE animals ADD PRIMARY KEY (id) NOVALIDATE");
    }

    @Test
    public void testAlterTablePrimaryKeyDeferrableValidate() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE animals ADD PRIMARY KEY (id) DEFERRABLE VALIDATE");
    }

    @Test
    public void testAlterTablePrimaryKeyDeferrableDisableNoValidate() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE animals ADD PRIMARY KEY (id) DEFERRABLE DISABLE NOVALIDATE");
    }

    @Test
    public void testAlterTableUniqueKey() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE `schema_migrations` ADD UNIQUE KEY `unique_schema_migrations` (`version`)");
    }

    @Test
    public void testAlterTableForgeignKey() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE test ADD FOREIGN KEY (user_id) REFERENCES ra_user (id) ON DELETE CASCADE");
    }

    @Test
    public void testAlterTableAddConstraint() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE RESOURCELINKTYPE ADD CONSTRAINT FK_RESOURCELINKTYPE_PARENTTYPE_PRIMARYKEY FOREIGN KEY (PARENTTYPE_PRIMARYKEY) REFERENCES RESOURCETYPE(PRIMARYKEY)");
    }

    @Test
    public void testAlterTableAddConstraintWithConstraintState() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE RESOURCELINKTYPE ADD CONSTRAINT FK_RESOURCELINKTYPE_PARENTTYPE_PRIMARYKEY FOREIGN KEY (PARENTTYPE_PRIMARYKEY) REFERENCES RESOURCETYPE(PRIMARYKEY) DEFERRABLE DISABLE NOVALIDATE");
    }

    @Test
    public void testAlterTableAddConstraintWithConstraintState2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE RESOURCELINKTYPE ADD CONSTRAINT RESOURCELINKTYPE_PRIMARYKEY PRIMARY KEY (PRIMARYKEY) DEFERRABLE NOVALIDATE");
    }

    @Test
    public void testAlterTableAddUniqueConstraint() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE Persons ADD UNIQUE (ID)");
    }

    @Test
    public void testAlterTableForgeignKey2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE test ADD FOREIGN KEY (user_id) REFERENCES ra_user (id)");
    }

    @Test
    public void testAlterTableForgeignKey3() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE test ADD FOREIGN KEY (user_id) REFERENCES ra_user (id) ON DELETE RESTRICT");
    }

    @Test
    public void testAlterTableForgeignKey4() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE test ADD FOREIGN KEY (user_id) REFERENCES ra_user (id) ON DELETE SET NULL");
    }

    @Test
    public void testAlterTableDropColumn() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE test DROP COLUMN YYY");
    }

    @Test
    public void testAlterTableDropColumn2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE mytable DROP COLUMN col1, DROP COLUMN col2");
        Statement stmt = CCJSqlParserUtil.parse("ALTER TABLE mytable DROP COLUMN col1, DROP COLUMN col2");
        Alter alter = ((Alter) (stmt));
        List<AlterExpression> alterExps = alter.getAlterExpressions();
        AlterExpression col1Exp = alterExps.get(0);
        AlterExpression col2Exp = alterExps.get(1);
        Assert.assertEquals("col1", col1Exp.getColumnName());
        Assert.assertEquals("col2", col2Exp.getColumnName());
    }

    @Test
    public void testAlterTableDropConstraint() throws JSQLParserException {
        final String sql = "ALTER TABLE test DROP CONSTRAINT YYY";
        Statement stmt = CCJSqlParserUtil.parse(sql);
        TestUtils.assertStatementCanBeDeparsedAs(stmt, sql);
        AlterExpression alterExpression = getAlterExpressions().get(0);
        Assert.assertEquals(alterExpression.getConstraintName(), "YYY");
    }

    @Test
    public void testAlterTableDropConstraintIfExists() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE Persons DROP CONSTRAINT IF EXISTS UC_Person");
    }

    @Test
    public void testAlterTablePK() throws JSQLParserException {
        final String sql = "ALTER TABLE `Author` ADD CONSTRAINT `AuthorPK` PRIMARY KEY (`ID`)";
        Statement stmt = CCJSqlParserUtil.parse(sql);
        TestUtils.assertStatementCanBeDeparsedAs(stmt, sql);
        AlterExpression alterExpression = getAlterExpressions().get(0);
        Assert.assertNull(alterExpression.getConstraintName());
        // TODO: should this pass? ==>        assertEquals(alterExpression.getPkColumns().get(0), "ID");
        Assert.assertEquals(alterExpression.getIndex().getColumnsNames().get(0), "`ID`");
    }

    @Test
    public void testAlterTableFK() throws JSQLParserException {
        String sql = "ALTER TABLE `Novels` ADD FOREIGN KEY (AuthorID) REFERENCES Author (ID)";
        Statement stmt = CCJSqlParserUtil.parse(sql);
        TestUtils.assertStatementCanBeDeparsedAs(stmt, sql);
        AlterExpression alterExpression = getAlterExpressions().get(0);
        Assert.assertEquals(alterExpression.getFkColumns().size(), 1);
        Assert.assertEquals(alterExpression.getFkColumns().get(0), "AuthorID");
        Assert.assertEquals(alterExpression.getFkSourceTable(), "Author");
        Assert.assertEquals(alterExpression.getFkSourceColumns().size(), 1);
        Assert.assertEquals(alterExpression.getFkSourceColumns().get(0), "ID");
    }

    @Test
    public void testAlterTableCheckConstraint() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE `Author` ADD CONSTRAINT name_not_empty CHECK (`NAME` <> '')");
    }

    @Test
    public void testAlterTableAddColumn2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE animals ADD (col1 integer, col2 integer)");
    }

    @Test
    public void testAlterTableAddColumn3() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE mytable ADD COLUMN mycolumn varchar (255)");
    }

    @Test
    public void testAlterTableAddColumn4() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE mytable ADD COLUMN col1 varchar (255), ADD COLUMN col2 integer");
        Statement stmt = CCJSqlParserUtil.parse("ALTER TABLE mytable ADD COLUMN col1 varchar (255), ADD COLUMN col2 integer");
        Alter alter = ((Alter) (stmt));
        List<AlterExpression> alterExps = alter.getAlterExpressions();
        AlterExpression col1Exp = alterExps.get(0);
        AlterExpression col2Exp = alterExps.get(1);
        List<ColumnDataType> col1DataTypes = col1Exp.getColDataTypeList();
        List<ColumnDataType> col2DataTypes = col2Exp.getColDataTypeList();
        Assert.assertEquals("col1", col1DataTypes.get(0).getColumnName());
        Assert.assertEquals("col2", col2DataTypes.get(0).getColumnName());
        Assert.assertEquals("varchar (255)", col1DataTypes.get(0).getColDataType().toString());
        Assert.assertEquals("integer", col2DataTypes.get(0).getColDataType().toString());
    }

    @Test
    public void testAlterTableAddColumn5() throws JSQLParserException {
        Statement stmt = CCJSqlParserUtil.parse("ALTER TABLE mytable ADD col1 timestamp (3)");
        // COLUMN keyword appears in deparsed statement
        TestUtils.assertStatementCanBeDeparsedAs(stmt, "ALTER TABLE mytable ADD COLUMN col1 timestamp (3)");
        Alter alter = ((Alter) (stmt));
        List<AlterExpression> alterExps = alter.getAlterExpressions();
        AlterExpression col1Exp = alterExps.get(0);
        List<ColumnDataType> col1DataTypes = col1Exp.getColDataTypeList();
        Assert.assertEquals("col1", col1DataTypes.get(0).getColumnName());
        Assert.assertEquals("timestamp (3)", col1DataTypes.get(0).getColDataType().toString());
    }

    @Test
    public void testAlterTableAddColumn6() throws JSQLParserException {
        final String sql = "ALTER TABLE mytable ADD COLUMN col1 timestamp (3) not null";
        Statement stmt = CCJSqlParserUtil.parse(sql);
        TestUtils.assertStatementCanBeDeparsedAs(stmt, sql);
        Alter alter = ((Alter) (stmt));
        List<AlterExpression> alterExps = alter.getAlterExpressions();
        AlterExpression col1Exp = alterExps.get(0);
        Assert.assertEquals("not", col1Exp.getColDataTypeList().get(0).getColumnSpecs().get(0));
        Assert.assertEquals("null", col1Exp.getColDataTypeList().get(0).getColumnSpecs().get(1));
    }

    @Test
    public void testAlterTableModifyColumn1() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE animals MODIFY (col1 integer, col2 number (8, 2))");
    }

    @Test
    public void testAlterTableModifyColumn2() throws JSQLParserException {
        Statement stmt = CCJSqlParserUtil.parse("ALTER TABLE mytable modify col1 timestamp (6)");
        // COLUMN keyword appears in deparsed statement, modify becomes all caps
        TestUtils.assertStatementCanBeDeparsedAs(stmt, "ALTER TABLE mytable MODIFY COLUMN col1 timestamp (6)");
        Assert.assertEquals(MODIFY, getAlterExpressions().get(0).getOperation());
    }

    @Test
    public void testAlterTableAlterColumn() throws JSQLParserException {
        // http://www.postgresqltutorial.com/postgresql-change-column-type/
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE table_name ALTER COLUMN column_name_1 TYPE TIMESTAMP, ALTER COLUMN column_name_2 TYPE BOOLEAN");
    }

    @Test
    public void testAlterTableChangeColumn1() throws JSQLParserException {
        Statement stmt = CCJSqlParserUtil.parse("ALTER TABLE tb_test CHANGE COLUMN c1 c2 INT (10)");
        Alter alter = ((Alter) (stmt));
        Assert.assertEquals(CHANGE, alter.getAlterExpressions().get(0).getOperation());
        Assert.assertEquals("c1", alter.getAlterExpressions().get(0).getColOldName());
        Assert.assertEquals("COLUMN", alter.getAlterExpressions().get(0).getOptionalSpecifier());
    }

    @Test
    public void testAlterTableChangeColumn2() throws JSQLParserException {
        Statement stmt = CCJSqlParserUtil.parse("ALTER TABLE tb_test CHANGE c1 c2 INT (10)");
        Alter alter = ((Alter) (stmt));
        Assert.assertEquals(CHANGE, alter.getAlterExpressions().get(0).getOperation());
        Assert.assertEquals("c1", alter.getAlterExpressions().get(0).getColOldName());
        Assert.assertNull(alter.getAlterExpressions().get(0).getOptionalSpecifier());
    }

    @Test
    public void testAlterTableChangeColumn3() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE tb_test CHANGE COLUMN c1 c2 INT (10)");
    }

    @Test
    public void testAlterTableChangeColumn4() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE tb_test CHANGE c1 c2 INT (10)");
    }

    @Test
    public void testAlterTableAddColumnWithZone() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE mytable ADD COLUMN col1 timestamp with time zone");
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE mytable ADD COLUMN col1 timestamp without time zone");
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE mytable ADD COLUMN col1 date with time zone");
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE mytable ADD COLUMN col1 date without time zone");
        Statement stmt = CCJSqlParserUtil.parse("ALTER TABLE mytable ADD COLUMN col1 timestamp with time zone");
        Alter alter = ((Alter) (stmt));
        List<AlterExpression> alterExps = alter.getAlterExpressions();
        AlterExpression col1Exp = alterExps.get(0);
        List<ColumnDataType> col1DataTypes = col1Exp.getColDataTypeList();
        Assert.assertEquals("timestamp with time zone", col1DataTypes.get(0).getColDataType().toString());
    }

    @Test
    public void testAlterTableAddColumnKeywordTypes() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE mytable ADD COLUMN col1 xml");
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE mytable ADD COLUMN col1 interval");
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE mytable ADD COLUMN col1 bit varying");
    }

    @Test
    public void testDropColumnRestrictIssue510() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE TABLE1 DROP COLUMN NewColumn CASCADE");
    }

    @Test
    public void testDropColumnRestrictIssue551() throws JSQLParserException {
        Statement stmt = CCJSqlParserUtil.parse("ALTER TABLE table1 DROP NewColumn");
        // COLUMN keyword appears in deparsed statement, drop becomes all caps
        TestUtils.assertStatementCanBeDeparsedAs(stmt, "ALTER TABLE table1 DROP COLUMN NewColumn");
    }

    @Test
    public void testAddConstraintKeyIssue320() throws JSQLParserException {
        String tableName = "table1";
        String columnName1 = "col1";
        String columnName2 = "col2";
        String columnName3 = "col3";
        String columnName4 = "col4";
        String constraintName1 = "table1_constraint_1";
        String constraintName2 = "table1_constraint_2";
        for (String constraintType : Arrays.asList("UNIQUE KEY", "KEY")) {
            TestUtils.assertSqlCanBeParsedAndDeparsed((((((((("ALTER TABLE " + tableName) + " ADD CONSTRAINT ") + constraintName1) + " ") + constraintType) + " (") + columnName1) + ")"));
            TestUtils.assertSqlCanBeParsedAndDeparsed((((((((((("ALTER TABLE " + tableName) + " ADD CONSTRAINT ") + constraintName1) + " ") + constraintType) + " (") + columnName1) + ", ") + columnName2) + ")"));
            TestUtils.assertSqlCanBeParsedAndDeparsed((((((((((((((((((("ALTER TABLE " + tableName) + " ADD CONSTRAINT ") + constraintName1) + " ") + constraintType) + " (") + columnName1) + ", ") + columnName2) + "), ADD CONSTRAINT ") + constraintName2) + " ") + constraintType) + " (") + columnName3) + ", ") + columnName4) + ")"));
        }
    }

    @Test
    public void testIssue633() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE team_phases ADD CONSTRAINT team_phases_id_key UNIQUE (id)");
    }

    @Test
    public void testIssue679() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("ALTER TABLE tb_session_status ADD INDEX idx_user_id_name (user_id, user_name(10)), ADD INDEX idx_user_name (user_name)");
    }

    @Test
    public void testAlterTableIndex586() throws Exception {
        Statement result = CCJSqlParserUtil.parse(("ALTER TABLE biz_add_fee DROP INDEX operation_time, " + ("ADD UNIQUE INDEX operation_time (`operation_time`, `warehouse_code`, `customerid`, `fees_type`, `external_no`) " + "USING BTREE, ALGORITHM = INPLACE")));
        Assert.assertEquals(("ALTER TABLE biz_add_fee DROP INDEX operation_time , " + ("ADD UNIQUE INDEX operation_time (`operation_time`, `warehouse_code`, `customerid`, `fees_type`, `external_no`) " + "USING BTREE, ALGORITHM = INPLACE")), result.toString());
    }
}

