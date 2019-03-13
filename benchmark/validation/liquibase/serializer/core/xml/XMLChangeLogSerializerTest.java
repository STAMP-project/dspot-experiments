package liquibase.serializer.core.xml;


import java.math.BigInteger;
import javax.xml.parsers.DocumentBuilderFactory;
import liquibase.change.AddColumnConfig;
import liquibase.change.ColumnConfig;
import liquibase.change.ConstraintsConfig;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.statement.SequenceNextValueFunction;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class XMLChangeLogSerializerTest {
    @Test
    public void createNode_addAutoIncrementChange() throws Exception {
        AddAutoIncrementChange change = new AddAutoIncrementChange();
        change.setSchemaName("SCHEMA_NAME");
        change.setTableName("TABLE_NAME");
        change.setColumnName("COLUMN_NAME");
        change.setColumnDataType("DATATYPE(255)");
        Element node = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(change);
        Assert.assertEquals("addAutoIncrement", node.getTagName());
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < (attributes.getLength()); i++) {
            Node attribute = attributes.item(i);
            if ("schemaName".equals(attribute.getNodeName())) {
                Assert.assertEquals("SCHEMA_NAME", attribute.getNodeValue());
            } else
                if ("tableName".equals(attribute.getNodeName())) {
                    Assert.assertEquals("TABLE_NAME", attribute.getNodeValue());
                } else
                    if ("columnName".equals(attribute.getNodeName())) {
                        Assert.assertEquals("COLUMN_NAME", attribute.getNodeValue());
                    } else
                        if ("columnDataType".equals(attribute.getNodeName())) {
                            Assert.assertEquals("DATATYPE(255)", attribute.getNodeValue());
                        } else {
                            Assert.fail(("unexpected attribute " + (attribute.getNodeName())));
                        }



        }
    }

    @Test
    public void createNode_addColumnChange() throws Exception {
        AddColumnChange refactoring = new AddColumnChange();
        refactoring.setTableName("TAB");
        AddColumnConfig column = new AddColumnConfig();
        column.setName("NEWCOL");
        column.setType("TYP");
        refactoring.addColumn(column);
        Element node = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(refactoring);
        Assert.assertEquals("addColumn", node.getTagName());
        Assert.assertEquals("TAB", node.getAttribute("tableName"));
        NodeList columns = node.getElementsByTagName("column");
        Assert.assertEquals(1, columns.getLength());
        Assert.assertEquals("column", ((Element) (columns.item(0))).getTagName());
        Assert.assertEquals("NEWCOL", ((Element) (columns.item(0))).getAttribute("name"));
        Assert.assertEquals("TYP", ((Element) (columns.item(0))).getAttribute("type"));
    }

    @Test
    public void createNode_AddDefaultValueChange() throws Exception {
        AddDefaultValueChange change = new AddDefaultValueChange();
        change.setSchemaName("SCHEMA_NAME");
        change.setTableName("TABLE_NAME");
        change.setColumnName("COLUMN_NAME");
        change.setDefaultValue("DEF STRING");
        change.setDefaultValueNumeric("42");
        change.setDefaultValueBoolean(true);
        change.setDefaultValueDate("2007-01-02");
        change.setDefaultValueSequenceNext(new SequenceNextValueFunction("sampleSeq"));
        Element node = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(change);
        Assert.assertEquals("addDefaultValue", node.getTagName());
        Assert.assertEquals("SCHEMA_NAME", node.getAttribute("schemaName"));
        Assert.assertEquals("TABLE_NAME", node.getAttribute("tableName"));
        Assert.assertEquals("COLUMN_NAME", node.getAttribute("columnName"));
        Assert.assertEquals("DEF STRING", node.getAttribute("defaultValue"));
        Assert.assertEquals("42", node.getAttribute("defaultValueNumeric"));
        Assert.assertEquals("true", node.getAttribute("defaultValueBoolean"));
        Assert.assertEquals("2007-01-02", node.getAttribute("defaultValueDate"));
        Assert.assertEquals("sampleSeq", node.getAttribute("defaultValueSequenceNext"));
    }

    @Test
    public void createNode_AddForeignKeyConstraintChange() throws Exception {
        AddForeignKeyConstraintChange change = new AddForeignKeyConstraintChange();
        change.setConstraintName("FK_NAME");
        change.setBaseTableSchemaName("BASE_SCHEMA_NAME");
        change.setBaseTableName("BASE_TABLE_NAME");
        change.setBaseColumnNames("BASE_COL_NAME");
        change.setReferencedTableSchemaName("REF_SCHEMA_NAME");
        change.setReferencedTableName("REF_TABLE_NAME");
        change.setReferencedColumnNames("REF_COL_NAME");
        change.setDeferrable(true);
        change.setOnDelete("CASCADE");
        change.setOnUpdate("CASCADE");
        change.setInitiallyDeferred(true);
        change.setValidate(true);
        Element node = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(change);
        Assert.assertEquals("addForeignKeyConstraint", node.getTagName());
        Assert.assertEquals("FK_NAME", node.getAttribute("constraintName"));
        Assert.assertEquals("BASE_SCHEMA_NAME", node.getAttribute("baseTableSchemaName"));
        Assert.assertEquals("BASE_TABLE_NAME", node.getAttribute("baseTableName"));
        Assert.assertEquals("BASE_COL_NAME", node.getAttribute("baseColumnNames"));
        Assert.assertEquals("REF_SCHEMA_NAME", node.getAttribute("referencedTableSchemaName"));
        Assert.assertEquals("REF_TABLE_NAME", node.getAttribute("referencedTableName"));
        Assert.assertEquals("REF_COL_NAME", node.getAttribute("referencedColumnNames"));
        Assert.assertEquals("true", node.getAttribute("deferrable"));
        Assert.assertEquals("true", node.getAttribute("initiallyDeferred"));
        Assert.assertEquals("true", node.getAttribute("validate"));
        Assert.assertEquals("CASCADE", node.getAttribute("onDelete"));
        Assert.assertEquals("CASCADE", node.getAttribute("onUpdate"));
    }

    @Test
    public void createNode_AddNotNullConstraintChange() throws Exception {
        AddNotNullConstraintChange change = new AddNotNullConstraintChange();
        change.setTableName("TABLE_NAME");
        change.setColumnName("COL_HERE");
        change.setDefaultNullValue("DEFAULT_VALUE");
        Element node = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(change);
        Assert.assertEquals("addNotNullConstraint", node.getTagName());
        Assert.assertEquals("TABLE_NAME", node.getAttribute("tableName"));
        Assert.assertEquals("COL_HERE", node.getAttribute("columnName"));
        Assert.assertEquals("DEFAULT_VALUE", node.getAttribute("defaultNullValue"));
    }

    @Test
    public void createNode_AddPrimaryKeyChange() throws Exception {
        AddPrimaryKeyChange change = new AddPrimaryKeyChange();
        change.setSchemaName("SCHEMA_NAME");
        change.setTableName("TABLE_NAME");
        change.setColumnNames("COL_HERE");
        change.setConstraintName("PK_NAME");
        change.setTablespace("TABLESPACE_NAME");
        Element node = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(change);
        Assert.assertEquals("addPrimaryKey", node.getTagName());
        Assert.assertEquals("SCHEMA_NAME", node.getAttribute("schemaName"));
        Assert.assertEquals("TABLE_NAME", node.getAttribute("tableName"));
        Assert.assertEquals("COL_HERE", node.getAttribute("columnNames"));
        Assert.assertEquals("PK_NAME", node.getAttribute("constraintName"));
        Assert.assertEquals("TABLESPACE_NAME", node.getAttribute("tablespace"));
    }

    @Test
    public void createNode_AddUniqueConstraintChange() throws Exception {
        AddUniqueConstraintChange change = new AddUniqueConstraintChange();
        change.setSchemaName("SCHEMA_NAME");
        change.setTableName("TABLE_NAME");
        change.setColumnNames("COL_HERE");
        change.setConstraintName("PK_NAME");
        change.setTablespace("TABLESPACE_NAME");
        change.setDeferrable(true);
        change.setValidate(true);
        change.setInitiallyDeferred(true);
        change.setDisabled(true);
        Element node = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(change);
        Assert.assertEquals("addUniqueConstraint", node.getTagName());
        Assert.assertEquals("SCHEMA_NAME", node.getAttribute("schemaName"));
        Assert.assertEquals("TABLE_NAME", node.getAttribute("tableName"));
        Assert.assertEquals("COL_HERE", node.getAttribute("columnNames"));
        Assert.assertEquals("PK_NAME", node.getAttribute("constraintName"));
        Assert.assertEquals("TABLESPACE_NAME", node.getAttribute("tablespace"));
        Assert.assertEquals("TABLESPACE_NAME", node.getAttribute("tablespace"));
        Assert.assertEquals("true", node.getAttribute("deferrable"));
        Assert.assertEquals("true", node.getAttribute("initiallyDeferred"));
        Assert.assertEquals("true", node.getAttribute("validate"));
    }

    @Test
    public void createNode_AlterSequenceChange_nullValues() throws Exception {
        AlterSequenceChange refactoring = new AlterSequenceChange();
        refactoring.setSequenceName("SEQ_NAME");
        Element node = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(refactoring);
        Assert.assertEquals("alterSequence", node.getNodeName());
        Assert.assertEquals("SEQ_NAME", node.getAttribute("sequenceName"));
        Assert.assertFalse(node.hasAttribute("incrementBy"));
        Assert.assertFalse(node.hasAttribute("maxValue"));
        Assert.assertFalse(node.hasAttribute("minValue"));
        Assert.assertFalse(node.hasAttribute("ordered"));
    }

    @Test
    public void createNode_AlterSequenceChange() throws Exception {
        AlterSequenceChange refactoring = new AlterSequenceChange();
        refactoring.setSchemaName("SCHEMA_NAME");
        refactoring.setSequenceName("SEQ_NAME");
        refactoring.setIncrementBy(new BigInteger("1"));
        refactoring.setMaxValue(new BigInteger("2"));
        refactoring.setMinValue(new BigInteger("3"));
        refactoring.setOrdered(true);
        refactoring.setCacheSize(new BigInteger("2008"));
        Element node = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(refactoring);
        Assert.assertEquals("alterSequence", node.getNodeName());
        Assert.assertEquals("SCHEMA_NAME", node.getAttribute("schemaName"));
        Assert.assertEquals("SEQ_NAME", node.getAttribute("sequenceName"));
        Assert.assertEquals("1", node.getAttribute("incrementBy"));
        Assert.assertEquals("2", node.getAttribute("maxValue"));
        Assert.assertEquals("3", node.getAttribute("minValue"));
        Assert.assertEquals("true", node.getAttribute("ordered"));
        Assert.assertEquals("2008", node.getAttribute("cacheSize"));
    }

    @Test
    public void createNode_ColumnConfig() throws Exception {
        ColumnConfig column = new ColumnConfig();
        column.setName("id");
        column.setType("varchar(255)");
        column.setDefaultValue("test Value");
        column.setValue("some value here");
        ConstraintsConfig constraints = new ConstraintsConfig();
        constraints.setDeferrable(Boolean.TRUE);
        constraints.setShouldValidate(Boolean.TRUE);
        constraints.setDeleteCascade(true);
        constraints.setForeignKeyName("FK_NAME");
        constraints.setInitiallyDeferred(true);
        constraints.setNullable(false);
        constraints.setPrimaryKey(true);
        constraints.setReferences("state(id)");
        constraints.setUnique(true);
        column.setConstraints(constraints);
        Element element = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(column);
        Assert.assertEquals("column", element.getTagName());
        Assert.assertEquals("id", element.getAttribute("name"));
        Assert.assertEquals("varchar(255)", element.getAttribute("type"));
        Assert.assertEquals("test Value", element.getAttribute("defaultValue"));
        Assert.assertEquals("some value here", element.getAttribute("value"));
        Element constraintsElement = ((Element) (element.getChildNodes().item(0)));
        Assert.assertEquals(9, constraintsElement.getAttributes().getLength());
        Assert.assertEquals("true", constraintsElement.getAttribute("deferrable"));
        Assert.assertEquals("true", constraintsElement.getAttribute("validate"));
        Assert.assertEquals("true", constraintsElement.getAttribute("deleteCascade"));
        Assert.assertEquals("FK_NAME", constraintsElement.getAttribute("foreignKeyName"));
        Assert.assertEquals("true", constraintsElement.getAttribute("initiallyDeferred"));
        Assert.assertEquals("false", constraintsElement.getAttribute("nullable"));
        Assert.assertEquals("true", constraintsElement.getAttribute("primaryKey"));
        Assert.assertEquals("state(id)", constraintsElement.getAttribute("references"));
        Assert.assertEquals("true", constraintsElement.getAttribute("unique"));
    }

    @Test
    public void createNode_CreateIndexChange() throws Exception {
        CreateIndexChange refactoring = new CreateIndexChange();
        refactoring.setIndexName("IDX_TEST");
        refactoring.setTableName("TAB_NAME");
        AddColumnConfig column1 = new AddColumnConfig();
        column1.setName("COL1");
        refactoring.addColumn(column1);
        AddColumnConfig column2 = new AddColumnConfig();
        column2.setName("COL2");
        column2.setDescending(true);
        refactoring.addColumn(column2);
        Element element = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(refactoring);
        Assert.assertEquals("createIndex", element.getTagName());
        Assert.assertEquals("IDX_TEST", element.getAttribute("indexName"));
        Assert.assertEquals("TAB_NAME", element.getAttribute("tableName"));
        Assert.assertEquals(2, element.getChildNodes().getLength());
        Assert.assertEquals("column", ((Element) (element.getChildNodes().item(0))).getTagName());
        Assert.assertEquals("COL1", ((Element) (element.getChildNodes().item(0))).getAttribute("name"));
        Assert.assertEquals("column", ((Element) (element.getChildNodes().item(1))).getTagName());
        Assert.assertEquals("COL2", ((Element) (element.getChildNodes().item(1))).getAttribute("name"));
        Assert.assertEquals("true", ((Element) (element.getChildNodes().item(1))).getAttribute("descending"));
    }

    @Test
    public void createNode_CreateProcedureChange() throws Exception {
        CreateProcedureChange refactoring = new CreateProcedureChange();
        refactoring.setProcedureText("CREATE PROC PROCBODY HERE");
        refactoring.setComments("Comments go here");
        Element element = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(refactoring);
        Assert.assertEquals("createProcedure", element.getTagName());
        Assert.assertEquals("CREATE PROC PROCBODY HERE", element.getTextContent());
    }

    @Test
    public void createNode_CreateSequenceChange() throws Exception {
        CreateSequenceChange change = new CreateSequenceChange();
        change.setSequenceName("SEQ_NAME");
        Element node = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(change);
        Assert.assertEquals("createSequence", node.getNodeName());
        Assert.assertEquals("SEQ_NAME", node.getAttribute("sequenceName"));
        Assert.assertFalse(node.hasAttribute("incrementBy"));
        Assert.assertFalse(node.hasAttribute("maxValue"));
        Assert.assertFalse(node.hasAttribute("minValue"));
        Assert.assertFalse(node.hasAttribute("ordered"));
        Assert.assertFalse(node.hasAttribute("startValue"));
        Assert.assertFalse(node.hasAttribute("cycle"));
        change.setIncrementBy(new BigInteger("1"));
        change.setMaxValue(new BigInteger("2"));
        change.setMinValue(new BigInteger("3"));
        change.setOrdered(true);
        change.setStartValue(new BigInteger("4"));
        change.setCycle(true);
        node = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(change);
        Assert.assertEquals("createSequence", node.getNodeName());
        Assert.assertEquals("SEQ_NAME", node.getAttribute("sequenceName"));
        Assert.assertEquals("1", node.getAttribute("incrementBy"));
        Assert.assertEquals("2", node.getAttribute("maxValue"));
        Assert.assertEquals("3", node.getAttribute("minValue"));
        Assert.assertEquals("true", node.getAttribute("ordered"));
        Assert.assertEquals("4", node.getAttribute("startValue"));
        Assert.assertEquals("true", node.getAttribute("cycle"));
    }

    @Test
    public void createNode_CreateTableChange() throws Exception {
        CreateTableChange change = new CreateTableChange();
        change.setTableName("TABLE_NAME");
        ColumnConfig column1 = new ColumnConfig();
        column1.setName("id");
        column1.setType("int");
        ConstraintsConfig column1constraints = new ConstraintsConfig();
        column1constraints.setPrimaryKey(true);
        column1constraints.setNullable(false);
        column1.setConstraints(column1constraints);
        change.addColumn(column1);
        ColumnConfig column2 = new ColumnConfig();
        column2.setName("name");
        column2.setType("varchar(255)");
        change.addColumn(column2);
        ColumnConfig column3 = new ColumnConfig();
        column3.setName("state_id");
        ConstraintsConfig column3constraints = new ConstraintsConfig();
        column3constraints.setNullable(false);
        column3constraints.setInitiallyDeferred(true);
        column3constraints.setDeferrable(true);
        column3constraints.setForeignKeyName("fk_tab_ref");
        column3constraints.setReferences("state(id)");
        column3.setConstraints(column3constraints);
        change.addColumn(column3);
        ColumnConfig column4 = new ColumnConfig();
        column4.setName("phone");
        column4.setType("varchar(255)");
        column4.setDefaultValue("NOPHONE");
        change.addColumn(column4);
        ColumnConfig column5 = new ColumnConfig();
        column5.setName("phone2");
        column5.setType("varchar(255)");
        ConstraintsConfig column5constraints = new ConstraintsConfig();
        column5constraints.setUnique(true);
        column5.setConstraints(column5constraints);
        change.addColumn(column5);
        Element element = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(change);
        Assert.assertEquals("createTable", element.getTagName());
        Assert.assertEquals(5, element.getChildNodes().getLength());
        Element columnElement = ((Element) (element.getChildNodes().item(0)));
        Assert.assertEquals("column", columnElement.getTagName());
        Assert.assertEquals("id", columnElement.getAttribute("name"));
        Assert.assertEquals("int", columnElement.getAttribute("type"));
        Element constraintsElement = ((Element) (columnElement.getChildNodes().item(0)));
        Assert.assertEquals("constraints", constraintsElement.getTagName());
        Assert.assertEquals(2, constraintsElement.getAttributes().getLength());
        Assert.assertEquals("true", constraintsElement.getAttribute("primaryKey"));
        Assert.assertEquals("false", constraintsElement.getAttribute("nullable"));
        columnElement = ((Element) (element.getChildNodes().item(1)));
        Assert.assertEquals("column", columnElement.getTagName());
        Assert.assertEquals("name", columnElement.getAttribute("name"));
        Assert.assertEquals("varchar(255)", columnElement.getAttribute("type"));
        columnElement = ((Element) (element.getChildNodes().item(2)));
        Assert.assertEquals("column", columnElement.getTagName());
        Assert.assertEquals("state_id", columnElement.getAttribute("name"));
        constraintsElement = ((Element) (columnElement.getChildNodes().item(0)));
        Assert.assertEquals("constraints", constraintsElement.getTagName());
        Assert.assertEquals(5, constraintsElement.getAttributes().getLength());
        Assert.assertEquals("false", constraintsElement.getAttribute("nullable"));
        Assert.assertEquals("true", constraintsElement.getAttribute("deferrable"));
        Assert.assertEquals("true", constraintsElement.getAttribute("initiallyDeferred"));
        Assert.assertEquals("fk_tab_ref", constraintsElement.getAttribute("foreignKeyName"));
        Assert.assertEquals("state(id)", constraintsElement.getAttribute("references"));
        columnElement = ((Element) (element.getChildNodes().item(3)));
        Assert.assertEquals("column", columnElement.getTagName());
        Assert.assertEquals("phone", columnElement.getAttribute("name"));
        Assert.assertEquals("varchar(255)", columnElement.getAttribute("type"));
        columnElement = ((Element) (element.getChildNodes().item(4)));
        Assert.assertEquals("column", columnElement.getTagName());
        Assert.assertEquals("phone2", columnElement.getAttribute("name"));
        Assert.assertEquals("varchar(255)", columnElement.getAttribute("type"));
        constraintsElement = ((Element) (columnElement.getChildNodes().item(0)));
        Assert.assertEquals("constraints", constraintsElement.getTagName());
        Assert.assertEquals(1, constraintsElement.getAttributes().getLength());
        Assert.assertEquals("true", constraintsElement.getAttribute("unique"));
    }

    @Test
    public void createNodeDropColumnChange() throws Exception {
        DropColumnChange change = new DropColumnChange();
        change.setTableName("TABLE_NAME");
        change.setColumnName("COL_NAME");
        Element node = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(change);
        Assert.assertEquals("dropColumn", node.getTagName());
        Assert.assertFalse(node.hasAttribute("schemaName"));
        Assert.assertEquals("TABLE_NAME", node.getAttribute("tableName"));
        Assert.assertEquals("COL_NAME", node.getAttribute("columnName"));
    }

    @Test
    public void createNode_DropColumnChange_withSchema() throws Exception {
        DropColumnChange change = new DropColumnChange();
        change.setSchemaName("SCHEMA_NAME");
        change.setTableName("TABLE_NAME");
        change.setColumnName("COL_NAME");
        Element node = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(change);
        Assert.assertEquals("dropColumn", node.getTagName());
        Assert.assertEquals("SCHEMA_NAME", node.getAttribute("schemaName"));
        Assert.assertEquals("TABLE_NAME", node.getAttribute("tableName"));
        Assert.assertEquals("COL_NAME", node.getAttribute("columnName"));
    }

    @Test
    public void createNode_DropDefaultValueChange() throws Exception {
        DropDefaultValueChange change = new DropDefaultValueChange();
        change.setTableName("TABLE_NAME");
        change.setColumnName("COL_NAME");
        Element node = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(change);
        Assert.assertEquals("dropDefaultValue", node.getTagName());
        Assert.assertFalse(node.hasAttribute("schemaName"));
        Assert.assertEquals("TABLE_NAME", node.getAttribute("tableName"));
        Assert.assertEquals("COL_NAME", node.getAttribute("columnName"));
    }

    @Test
    public void createNode_DropDefaultValueChange_withSchema() throws Exception {
        DropDefaultValueChange change = new DropDefaultValueChange();
        change.setSchemaName("SCHEMA_NAME");
        change.setTableName("TABLE_NAME");
        change.setColumnName("COL_NAME");
        Element node = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(change);
        Assert.assertEquals("dropDefaultValue", node.getTagName());
        Assert.assertEquals("SCHEMA_NAME", node.getAttribute("schemaName"));
        Assert.assertEquals("TABLE_NAME", node.getAttribute("tableName"));
        Assert.assertEquals("COL_NAME", node.getAttribute("columnName"));
    }

    @Test
    public void createNode_DropForeignKeyConstraintChange() throws Exception {
        DropForeignKeyConstraintChange change = new DropForeignKeyConstraintChange();
        change.setBaseTableSchemaName("SCHEMA_NAME");
        change.setBaseTableName("TABLE_NAME");
        change.setConstraintName("FK_NAME");
        Element node = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(change);
        Assert.assertEquals("dropForeignKeyConstraint", node.getTagName());
        Assert.assertEquals("SCHEMA_NAME", node.getAttribute("baseTableSchemaName"));
        Assert.assertEquals("TABLE_NAME", node.getAttribute("baseTableName"));
        Assert.assertEquals("FK_NAME", node.getAttribute("constraintName"));
    }

    @Test
    public void createNode_DropIndexChange() throws Exception {
        DropIndexChange refactoring = new DropIndexChange();
        refactoring.setIndexName("IDX_NAME");
        Element element = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(refactoring);
        Assert.assertEquals("dropIndex", element.getTagName());
        Assert.assertEquals("IDX_NAME", element.getAttribute("indexName"));
    }

    @Test
    public void createNode_DropNotNullConstraintChange() throws Exception {
        DropNotNullConstraintChange change = new DropNotNullConstraintChange();
        change.setTableName("TABLE_NAME");
        change.setColumnName("COL_HERE");
        Element node = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(change);
        Assert.assertEquals("dropNotNullConstraint", node.getTagName());
        Assert.assertEquals("TABLE_NAME", node.getAttribute("tableName"));
        Assert.assertEquals("COL_HERE", node.getAttribute("columnName"));
    }

    @Test
    public void createNode_DropPrimaryKeyChange() throws Exception {
        DropPrimaryKeyChange change = new DropPrimaryKeyChange();
        change.setSchemaName("SCHEMA_NAME");
        change.setTableName("TABLE_NAME");
        change.setConstraintName("PK_NAME");
        Element node = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(change);
        Assert.assertEquals("dropPrimaryKey", node.getTagName());
        Assert.assertEquals("SCHEMA_NAME", node.getAttribute("schemaName"));
        Assert.assertEquals("TABLE_NAME", node.getAttribute("tableName"));
        Assert.assertEquals("PK_NAME", node.getAttribute("constraintName"));
    }

    @Test
    public void createNode_DropSequenceChange() throws Exception {
        DropSequenceChange change = new DropSequenceChange();
        change.setSequenceName("SEQ_NAME");
        Element element = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(change);
        Assert.assertEquals("dropSequence", element.getTagName());
        Assert.assertEquals("SEQ_NAME", element.getAttribute("sequenceName"));
    }

    @Test
    public void createNode_DropTableChange() throws Exception {
        DropTableChange change = new DropTableChange();
        change.setSchemaName("SCHEMA_NAME");
        change.setTableName("TAB_NAME");
        change.setCascadeConstraints(true);
        Element element = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(change);
        Assert.assertEquals("dropTable", element.getTagName());
        Assert.assertEquals("TAB_NAME", element.getAttribute("tableName"));
        Assert.assertEquals("true", element.getAttribute("cascadeConstraints"));
    }

    @Test
    public void createNode_DropTableChange_withSchema() throws Exception {
        DropTableChange change = new DropTableChange();
        change.setSchemaName("SCHEMA_NAME");
        change.setTableName("TAB_NAME");
        change.setCascadeConstraints(true);
        Element element = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(change);
        Assert.assertEquals("dropTable", element.getTagName());
        Assert.assertEquals("TAB_NAME", element.getAttribute("tableName"));
        Assert.assertEquals("true", element.getAttribute("cascadeConstraints"));
        Assert.assertTrue(element.hasAttribute("schemaName"));
    }

    @Test
    public void createNode_nullConstraint() throws Exception {
        DropTableChange change = new DropTableChange();
        change.setSchemaName("SCHEMA_NAME");
        change.setTableName("TAB_NAME");
        change.setCascadeConstraints(null);
        Element element = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(change);
        Assert.assertEquals("dropTable", element.getTagName());
        Assert.assertFalse(element.hasAttribute("cascadeConstraints"));
    }

    @Test
    public void createNode_DropUniqueConstraintChange() throws Exception {
        DropUniqueConstraintChange change = new DropUniqueConstraintChange();
        change.setSchemaName("SCHEMA_NAME");
        change.setTableName("TAB_NAME");
        change.setConstraintName("UQ_CONSTRAINT");
        Element element = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(change);
        Assert.assertEquals("dropUniqueConstraint", element.getTagName());
        Assert.assertEquals("SCHEMA_NAME", element.getAttribute("schemaName"));
        Assert.assertEquals("TAB_NAME", element.getAttribute("tableName"));
        Assert.assertEquals("UQ_CONSTRAINT", element.getAttribute("constraintName"));
    }

    @Test
    public void createNode_DropUniqueConstraintChange_noSchema() throws Exception {
        DropUniqueConstraintChange change = new DropUniqueConstraintChange();
        change.setTableName("TAB_NAME");
        change.setConstraintName("UQ_CONSTRAINT");
        Element element = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(change);
        Assert.assertEquals("dropUniqueConstraint", element.getTagName());
        Assert.assertEquals("TAB_NAME", element.getAttribute("tableName"));
        Assert.assertEquals("UQ_CONSTRAINT", element.getAttribute("constraintName"));
        Assert.assertFalse(element.hasAttribute("schemaName"));
    }

    @Test
    public void createNode_DropViewChange() throws Exception {
        DropViewChange change = new DropViewChange();
        change.setViewName("VIEW_NAME");
        Element node = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(change);
        Assert.assertEquals("dropView", node.getTagName());
        Assert.assertFalse(node.hasAttribute("schemaName"));
        Assert.assertEquals("VIEW_NAME", node.getAttribute("viewName"));
    }

    @Test
    public void createNode_DropViewChange_withSchema() throws Exception {
        DropViewChange change = new DropViewChange();
        change.setSchemaName("SCHEMA_NAME");
        change.setViewName("VIEW_NAME");
        Element node = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(change);
        Assert.assertEquals("dropView", node.getTagName());
        Assert.assertEquals("SCHEMA_NAME", node.getAttribute("schemaName"));
        Assert.assertEquals("VIEW_NAME", node.getAttribute("viewName"));
    }

    @Test
    public void createNode_InsertDataChange() throws Exception {
        InsertDataChange refactoring = new InsertDataChange();
        refactoring.setTableName("TABLE_NAME");
        ColumnConfig col1 = new ColumnConfig();
        col1.setName("id");
        col1.setValueNumeric("123");
        ColumnConfig col2 = new ColumnConfig();
        col2.setName("name");
        col2.setValue("Andrew");
        ColumnConfig col3 = new ColumnConfig();
        col3.setName("age");
        col3.setValueNumeric("21");
        ColumnConfig col4 = new ColumnConfig();
        col4.setName("height");
        col4.setValueNumeric("1.78");
        refactoring.addColumn(col1);
        refactoring.addColumn(col2);
        refactoring.addColumn(col3);
        refactoring.addColumn(col4);
        Element node = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(refactoring);
        Assert.assertEquals("insert", node.getTagName());
        Assert.assertEquals("TABLE_NAME", node.getAttribute("tableName"));
        NodeList columns = node.getChildNodes();
        Assert.assertEquals(4, columns.getLength());
        Assert.assertEquals("column", ((Element) (columns.item(0))).getTagName());
        Assert.assertEquals("id", ((Element) (columns.item(0))).getAttribute("name"));
        Assert.assertEquals("123", ((Element) (columns.item(0))).getAttribute("valueNumeric"));
        Assert.assertEquals("column", ((Element) (columns.item(1))).getTagName());
        Assert.assertEquals("name", ((Element) (columns.item(1))).getAttribute("name"));
        Assert.assertEquals("Andrew", ((Element) (columns.item(1))).getAttribute("value"));
        Assert.assertEquals("column", ((Element) (columns.item(2))).getTagName());
        Assert.assertEquals("age", ((Element) (columns.item(2))).getAttribute("name"));
        Assert.assertEquals("21", ((Element) (columns.item(2))).getAttribute("valueNumeric"));
        Assert.assertEquals("column", ((Element) (columns.item(3))).getTagName());
        Assert.assertEquals("height", ((Element) (columns.item(3))).getAttribute("name"));
        Assert.assertEquals("1.78", ((Element) (columns.item(3))).getAttribute("valueNumeric"));
    }

    @Test
    public void createNode_LoadDataChange() throws Exception {
        LoadDataChange refactoring = new LoadDataChange();
        refactoring.setSchemaName("SCHEMA_NAME");
        refactoring.setTableName("TABLE_NAME");
        refactoring.setFile("FILE_NAME");
        refactoring.setEncoding("UTF-8");
        Element node = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(refactoring);
        Assert.assertEquals("loadData", node.getNodeName());
        Assert.assertEquals("SCHEMA_NAME", node.getAttribute("schemaName"));
        Assert.assertEquals("TABLE_NAME", node.getAttribute("tableName"));
        Assert.assertEquals("FILE_NAME", node.getAttribute("file"));
        Assert.assertEquals("UTF-8", node.getAttribute("encoding"));
    }

    @Test
    public void createNode_RawSQLChange() throws Exception {
        RawSQLChange refactoring = new RawSQLChange();
        refactoring.setSql("SOME SQL HERE");
        Element element = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(refactoring);
        Assert.assertEquals("sql", element.getTagName());
        Assert.assertEquals("SOME SQL HERE", element.getTextContent());
    }

    @Test
    public void createNode_RenameColumnChange() throws Exception {
        RenameColumnChange refactoring = new RenameColumnChange();
        refactoring.setSchemaName("SCHEMA_NAME");
        refactoring.setTableName("TABLE_NAME");
        refactoring.setOldColumnName("oldColName");
        refactoring.setNewColumnName("newColName");
        Element node = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(refactoring);
        Assert.assertEquals("renameColumn", node.getTagName());
        Assert.assertEquals("SCHEMA_NAME", node.getAttribute("schemaName"));
        Assert.assertEquals("TABLE_NAME", node.getAttribute("tableName"));
        Assert.assertEquals("oldColName", node.getAttribute("oldColumnName"));
        Assert.assertEquals("newColName", node.getAttribute("newColumnName"));
    }

    @Test
    public void createNode_RenameTableChange() throws Exception {
        RenameTableChange refactoring = new RenameTableChange();
        refactoring.setSchemaName("SCHEMA_NAME");
        refactoring.setOldTableName("OLD_NAME");
        refactoring.setNewTableName("NEW_NAME");
        Element node = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(refactoring);
        Assert.assertEquals("renameTable", node.getTagName());
        Assert.assertEquals("SCHEMA_NAME", node.getAttribute("schemaName"));
        Assert.assertEquals("OLD_NAME", node.getAttribute("oldTableName"));
        Assert.assertEquals("NEW_NAME", node.getAttribute("newTableName"));
    }

    @Test
    public void createNode_RenameSequenceChange() throws Exception {
        RenameSequenceChange refactoring = new RenameSequenceChange();
        refactoring.setSchemaName("SCHEMA_NAME");
        refactoring.setOldSequenceName("OLD_NAME");
        refactoring.setNewSequenceName("NEW_NAME");
        Element node = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(refactoring);
        Assert.assertEquals("renameSequence", node.getTagName());
        Assert.assertEquals("SCHEMA_NAME", node.getAttribute("schemaName"));
        Assert.assertEquals("OLD_NAME", node.getAttribute("oldSequenceName"));
        Assert.assertEquals("NEW_NAME", node.getAttribute("newSequenceName"));
    }

    @Test
    public void createNode_RenameViewChange() throws Exception {
        RenameViewChange refactoring = new RenameViewChange();
        refactoring.setSchemaName("SCHEMA_NAME");
        refactoring.setOldViewName("OLD_NAME");
        refactoring.setNewViewName("NEW_NAME");
        Element node = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(refactoring);
        Assert.assertEquals("renameView", node.getTagName());
        Assert.assertEquals("SCHEMA_NAME", node.getAttribute("schemaName"));
        Assert.assertEquals("OLD_NAME", node.getAttribute("oldViewName"));
        Assert.assertEquals("NEW_NAME", node.getAttribute("newViewName"));
    }

    @Test
    public void createNode_SQLFileChange() throws Exception {
        String fileName = "liquibase/change/core/SQLFileTestData.sql";
        SQLFileChange change = new SQLFileChange();
        ClassLoaderResourceAccessor opener = new ClassLoaderResourceAccessor();
        change.setResourceAccessor(opener);
        change.setPath(fileName);
        Element element = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(change);
        Assert.assertEquals("sqlFile", element.getTagName());
        Assert.assertEquals(fileName, element.getAttribute("path"));
    }

    @Test
    public void createNode_TagDatabaseChange() throws Exception {
        TagDatabaseChange refactoring = new TagDatabaseChange();
        refactoring.setTag("TAG_NAME");
        Element node = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(refactoring);
        Assert.assertEquals("tagDatabase", node.getTagName());
        Assert.assertEquals("TAG_NAME", node.getAttribute("tag"));
    }

    @Test
    public void createNode_CreateViewChange() throws Exception {
        CreateViewChange change = new CreateViewChange();
        change.setSchemaName("SCHEMA_NAME");
        change.setViewName("VIEW_NAME");
        change.setSelectQuery("SELECT * FROM EXISTING_TABLE");
        Element node = new XMLChangeLogSerializer(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()).createNode(change);
        Assert.assertEquals("createView", node.getTagName());
        Assert.assertEquals("SCHEMA_NAME", node.getAttribute("schemaName"));
        Assert.assertEquals("VIEW_NAME", node.getAttribute("viewName"));
        Assert.assertEquals("SELECT * FROM EXISTING_TABLE", node.getTextContent());
    }

    @Test
    public void serialize_pretty() {
        UpdateDataChange change = new UpdateDataChange();
        change.setCatalogName("a");
        change.setSchemaName("b");
        change.setTableName("c");
        change.setWhere("Some Text");
        String out = new XMLChangeLogSerializer().serialize(change, true);
        Assert.assertEquals(("<update catalogName=\"a\"\n" + ((("        schemaName=\"b\"\n" + "        tableName=\"c\">\n") + "    <where>Some Text</where>\n") + "</update>")), out);
    }

    @Test
    public void serialize_pretty_nestedNodeWithAttributes() {
        CreateTableChange change = new CreateTableChange();
        change.setCatalogName("a");
        change.setSchemaName("b");
        change.setTableName("c");
        change.addColumn(new ColumnConfig().setName("x").setDefaultValue("x1"));
        change.addColumn(new ColumnConfig().setName("y").setDefaultValue("y1"));
        String out = new XMLChangeLogSerializer().serialize(change, true);
        Assert.assertEquals(("<createTable catalogName=\"a\"\n" + (((("        schemaName=\"b\"\n" + "        tableName=\"c\">\n") + "    <column defaultValue=\"x1\" name=\"x\"/>\n") + "    <column defaultValue=\"y1\" name=\"y\"/>\n") + "</createTable>")), out);
    }

    @Test
    public void serialize_pretty_justAttributes() {
        AddAutoIncrementChange change = new AddAutoIncrementChange();
        change.setCatalogName("a");
        change.setSchemaName("b");
        change.setTableName("c");
        String out = new XMLChangeLogSerializer().serialize(change, true);
        Assert.assertEquals(("<addAutoIncrement catalogName=\"a\"\n" + ("        schemaName=\"b\"\n" + "        tableName=\"c\"/>")), out);
    }
}

