package io.crate.metadata.doc;


import ColumnPolicy.DYNAMIC;
import ColumnPolicy.IGNORED;
import ColumnPolicy.STRICT;
import Constants.DEFAULT_MAPPING_TYPE;
import DataTypes.DOUBLE;
import DataTypes.GEO_POINT;
import DataTypes.IP;
import DataTypes.STRING;
import DataTypes.TIMESTAMP;
import Reference.IndexType.ANALYZED;
import Reference.IndexType.NO;
import Reference.IndexType.NOT_ANALYZED;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.crate.expression.udf.UserDefinedFunctionService;
import io.crate.metadata.ColumnIdent;
import io.crate.metadata.Functions;
import io.crate.metadata.GeneratedReference;
import io.crate.metadata.IndexReference;
import io.crate.metadata.Reference;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SymbolMatchers;
import io.crate.types.DataType;
import io.crate.types.DataTypes;
import io.crate.types.ObjectType;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.hamcrest.Matchers;
import org.junit.Test;


// @formatter:off
public class DocIndexMetaDataTest extends CrateDummyClusterServiceUnitTest {
    private Functions functions;

    private UserDefinedFunctionService udfService;

    @Test
    public void testNestedColumnIdent() throws Exception {
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject("properties").startObject("person").startObject("properties").startObject("addresses").startObject("properties").startObject("city").field("type", "string").endObject().startObject("country").field("type", "string").endObject().endObject().endObject().endObject().endObject().endObject().endObject();
        IndexMetaData metaData = getIndexMetaData("test1", builder);
        DocIndexMetaData md = newMeta(metaData, "test1");
        Reference reference = md.references().get(new ColumnIdent("person", Arrays.asList("addresses", "city")));
        assertNotNull(reference);
    }

    @Test
    public void testExtractObjectColumnDefinitions() throws Exception {
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject("properties").startObject("implicit_dynamic").startObject("properties").startObject("name").field("type", "string").endObject().endObject().endObject().startObject("explicit_dynamic").field("dynamic", "true").startObject("properties").startObject("name").field("type", "string").endObject().startObject("age").field("type", "integer").endObject().endObject().endObject().startObject("ignored").field("dynamic", "false").startObject("properties").startObject("name").field("type", "string").endObject().startObject("age").field("type", "integer").endObject().endObject().endObject().startObject("strict").field("dynamic", "strict").startObject("properties").startObject("age").field("type", "integer").endObject().endObject().endObject().endObject().endObject();
        IndexMetaData metaData = getIndexMetaData("test1", builder);
        DocIndexMetaData md = newMeta(metaData, "test1");
        assertThat(md.columns().size(), Matchers.is(4));
        assertThat(md.references().size(), Matchers.is(17));
        assertThat(md.references().get(new ColumnIdent("implicit_dynamic")).columnPolicy(), Matchers.is(DYNAMIC));
        assertThat(md.references().get(new ColumnIdent("explicit_dynamic")).columnPolicy(), Matchers.is(DYNAMIC));
        assertThat(md.references().get(new ColumnIdent("ignored")).columnPolicy(), Matchers.is(IGNORED));
        assertThat(md.references().get(new ColumnIdent("strict")).columnPolicy(), Matchers.is(STRICT));
    }

    @Test
    public void testExtractColumnDefinitions() throws Exception {
        // @formatter:off
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject("_meta").field("primary_keys", "integerIndexed").endObject().startObject("properties").startObject("integerIndexed").field("type", "integer").endObject().startObject("integerIndexedBWC").field("type", "integer").field("index", "not_analyzed").endObject().startObject("integerNotIndexed").field("type", "integer").field("index", "false").endObject().startObject("integerNotIndexedBWC").field("type", "integer").field("index", "no").endObject().startObject("stringNotIndexed").field("type", "string").field("index", "false").endObject().startObject("stringNotIndexedBWC").field("type", "string").field("index", "no").endObject().startObject("stringNotAnalyzed").field("type", "keyword").endObject().startObject("stringNotAnalyzedBWC").field("type", "string").field("index", "not_analyzed").endObject().startObject("stringAnalyzed").field("type", "text").field("analyzer", "standard").endObject().startObject("stringAnalyzedBWC").field("type", "string").field("index", "analyzed").field("analyzer", "standard").endObject().startObject("person").startObject("properties").startObject("first_name").field("type", "string").endObject().startObject("birthday").field("type", "date").endObject().endObject().endObject().endObject().endObject();
        // @formatter:on
        IndexMetaData metaData = getIndexMetaData("test1", builder);
        DocIndexMetaData md = newMeta(metaData, "test1");
        assertThat(md.columns().size(), Matchers.is(11));
        assertThat(md.references().size(), Matchers.is(20));
        Reference birthday = md.references().get(new ColumnIdent("person", "birthday"));
        assertThat(birthday.valueType(), Matchers.is(TIMESTAMP));
        assertThat(birthday.indexType(), Matchers.is(NOT_ANALYZED));
        Reference integerIndexed = md.references().get(new ColumnIdent("integerIndexed"));
        assertThat(integerIndexed.indexType(), Matchers.is(NOT_ANALYZED));
        Reference integerIndexedBWC = md.references().get(new ColumnIdent("integerIndexedBWC"));
        assertThat(integerIndexedBWC.indexType(), Matchers.is(NOT_ANALYZED));
        Reference integerNotIndexed = md.references().get(new ColumnIdent("integerNotIndexed"));
        assertThat(integerNotIndexed.indexType(), Matchers.is(NO));
        Reference integerNotIndexedBWC = md.references().get(new ColumnIdent("integerNotIndexedBWC"));
        assertThat(integerNotIndexedBWC.indexType(), Matchers.is(NO));
        Reference stringNotIndexed = md.references().get(new ColumnIdent("stringNotIndexed"));
        assertThat(stringNotIndexed.indexType(), Matchers.is(NO));
        Reference stringNotIndexedBWC = md.references().get(new ColumnIdent("stringNotIndexedBWC"));
        assertThat(stringNotIndexedBWC.indexType(), Matchers.is(NO));
        Reference stringNotAnalyzed = md.references().get(new ColumnIdent("stringNotAnalyzed"));
        assertThat(stringNotAnalyzed.indexType(), Matchers.is(NOT_ANALYZED));
        Reference stringNotAnalyzedBWC = md.references().get(new ColumnIdent("stringNotAnalyzedBWC"));
        assertThat(stringNotAnalyzedBWC.indexType(), Matchers.is(NOT_ANALYZED));
        Reference stringAnalyzed = md.references().get(new ColumnIdent("stringAnalyzed"));
        assertThat(stringAnalyzed.indexType(), Matchers.is(ANALYZED));
        Reference stringAnalyzedBWC = md.references().get(new ColumnIdent("stringAnalyzedBWC"));
        assertThat(stringAnalyzedBWC.indexType(), Matchers.is(ANALYZED));
        ImmutableList<Reference> references = ImmutableList.copyOf(md.references().values());
        List<String> fqns = Lists.transform(references, new Function<Reference, String>() {
            @Nullable
            @Override
            public String apply(@Nullable
            Reference input) {
                return input.column().fqn();
            }
        });
        assertThat(fqns, Matchers.is(ImmutableList.of("_doc", "_fetchid", "_id", "_raw", "_score", "_uid", "_version", "integerIndexed", "integerIndexedBWC", "integerNotIndexed", "integerNotIndexedBWC", "person", "person.birthday", "person.first_name", "stringAnalyzed", "stringAnalyzedBWC", "stringNotAnalyzed", "stringNotAnalyzedBWC", "stringNotIndexed", "stringNotIndexedBWC")));
    }

    @Test
    public void testExtractPartitionedByColumns() throws Exception {
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject("_meta").field("primary_keys", "id").startArray("partitioned_by").startArray().value("datum").value("date").endArray().endArray().endObject().startObject("properties").startObject("id").field("type", "integer").endObject().startObject("title").field("type", "string").field("index", "false").endObject().startObject("content").field("type", "string").field("index", "true").field("analyzer", "standard").endObject().startObject("person").startObject("properties").startObject("first_name").field("type", "string").endObject().startObject("birthday").field("type", "date").endObject().endObject().endObject().startObject("nested").field("type", "nested").startObject("properties").startObject("inner_nested").field("type", "date").endObject().endObject().endObject().endObject().endObject();
        IndexMetaData metaData = getIndexMetaData("test1", builder);
        DocIndexMetaData md = newMeta(metaData, "test1");
        assertEquals(6, md.columns().size());
        assertEquals(16, md.references().size());
        assertEquals(1, md.partitionedByColumns().size());
        assertEquals(TIMESTAMP, md.partitionedByColumns().get(0).valueType());
        assertThat(md.partitionedByColumns().get(0).column().fqn(), Matchers.is("datum"));
        assertThat(md.partitionedBy().size(), Matchers.is(1));
        assertThat(md.partitionedBy().get(0), Matchers.is(ColumnIdent.fromPath("datum")));
    }

    @Test
    public void testExtractPartitionedByWithPartitionedByInColumns() throws Exception {
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject("_meta").startArray("partitioned_by").startArray().value("datum").value("date").endArray().endArray().endObject().startObject("properties").startObject("id").field("type", "integer").endObject().startObject("datum").field("type", "date").endObject().endObject().endObject();
        IndexMetaData metaData = getIndexMetaData("test1", builder);
        DocIndexMetaData md = newMeta(metaData, "test1");
        // partitioned by column is not added twice
        assertEquals(2, md.columns().size());
        assertEquals(9, md.references().size());
        assertEquals(1, md.partitionedByColumns().size());
    }

    @Test
    public void testExtractPartitionedByWithNestedPartitionedByInColumns() throws Exception {
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject("_meta").startArray("partitioned_by").startArray().value("nested.datum").value("date").endArray().endArray().endObject().startObject("properties").startObject("id").field("type", "integer").endObject().startObject("nested").field("type", "nested").startObject("properties").startObject("datum").field("type", "date").endObject().endObject().endObject().endObject().endObject();
        IndexMetaData metaData = getIndexMetaData("test1", builder);
        DocIndexMetaData md = newMeta(metaData, "test1");
        // partitioned by column is not added twice
        assertEquals(2, md.columns().size());
        assertEquals(10, md.references().size());
        assertEquals(1, md.partitionedByColumns().size());
    }

    @Test
    public void testExtractColumnDefinitionsFromEmptyIndex() throws Exception {
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject(DEFAULT_MAPPING_TYPE).endObject().endObject();
        IndexMetaData metaData = getIndexMetaData("test2", builder);
        DocIndexMetaData md = newMeta(metaData, "test2");
        assertThat(md.columns(), hasSize(0));
    }

    @Test
    public void testDocSysColumnReferences() throws Exception {
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject(DEFAULT_MAPPING_TYPE).startObject("properties").startObject("content").field("type", "string").endObject().endObject().endObject().endObject();
        DocIndexMetaData metaData = newMeta(getIndexMetaData("test", builder), "test");
        Reference id = metaData.references().get(new ColumnIdent("_id"));
        assertNotNull(id);
        Reference version = metaData.references().get(new ColumnIdent("_version"));
        assertNotNull(version);
        Reference score = metaData.references().get(new ColumnIdent("_score"));
        assertNotNull(score);
    }

    @Test
    public void testExtractPrimaryKey() throws Exception {
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject(DEFAULT_MAPPING_TYPE).startObject("_meta").field("primary_keys", "id").endObject().startObject("properties").startObject("id").field("type", "integer").endObject().startObject("title").field("type", "string").field("index", "false").endObject().startObject("datum").field("type", "date").endObject().startObject("content").field("type", "string").field("index", "true").field("analyzer", "standard").endObject().endObject().endObject().endObject();
        IndexMetaData metaData = getIndexMetaData("test3", builder);
        DocIndexMetaData md = newMeta(metaData, "test3");
        assertThat(md.primaryKey().size(), Matchers.is(1));
        assertThat(md.primaryKey(), Matchers.contains(new ColumnIdent("id")));
        builder = XContentFactory.jsonBuilder().startObject().startObject(DEFAULT_MAPPING_TYPE).startObject("properties").startObject("content").field("type", "string").endObject().endObject().endObject().endObject();
        md = newMeta(getIndexMetaData("test4", builder), "test4");
        assertThat(md.primaryKey().size(), Matchers.is(1));// _id is always the fallback primary key

        builder = XContentFactory.jsonBuilder().startObject().startObject(DEFAULT_MAPPING_TYPE).endObject().endObject();
        md = newMeta(getIndexMetaData("test5", builder), "test5");
        assertThat(md.primaryKey().size(), Matchers.is(1));
    }

    @Test
    public void testExtractMultiplePrimaryKeys() throws Exception {
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject(DEFAULT_MAPPING_TYPE).startObject("_meta").array("primary_keys", "id", "title").endObject().startObject("properties").startObject("id").field("type", "integer").endObject().startObject("title").field("type", "string").field("index", "false").endObject().endObject().endObject().endObject();
        IndexMetaData metaData = getIndexMetaData("test_multi_pk", builder);
        DocIndexMetaData md = newMeta(metaData, "test_multi_pk");
        assertThat(md.primaryKey().size(), Matchers.is(2));
        assertThat(md.primaryKey(), Matchers.hasItems(ColumnIdent.fromPath("id"), ColumnIdent.fromPath("title")));
    }

    @Test
    public void testExtractNoPrimaryKey() throws Exception {
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject(DEFAULT_MAPPING_TYPE).startObject("_meta").endObject().startObject("properties").startObject("id").field("type", "integer").endObject().startObject("title").field("type", "string").field("index", "false").endObject().endObject().endObject().endObject();
        IndexMetaData metaData = getIndexMetaData("test_no_pk", builder);
        DocIndexMetaData md = newMeta(metaData, "test_no_pk");
        assertThat(md.primaryKey().size(), Matchers.is(1));
        assertThat(md.primaryKey(), Matchers.hasItems(ColumnIdent.fromPath("_id")));
        builder = // results in empty list
        XContentFactory.jsonBuilder().startObject().startObject(DEFAULT_MAPPING_TYPE).startObject("_meta").array("primary_keys").endObject().startObject("properties").startObject("id").field("type", "integer").endObject().startObject("title").field("type", "string").field("index", "false").endObject().endObject().endObject().endObject();
        metaData = getIndexMetaData("test_no_pk2", builder);
        md = newMeta(metaData, "test_no_pk2");
        assertThat(md.primaryKey().size(), Matchers.is(1));
        assertThat(md.primaryKey(), Matchers.hasItems(ColumnIdent.fromPath("_id")));
    }

    @Test
    public void testSchemaWithNotNullColumns() throws Exception {
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject(DEFAULT_MAPPING_TYPE).startObject("_meta").startObject("constraints").array("not_null", "id", "title").endObject().endObject().startObject("properties").startObject("id").field("type", "integer").endObject().startObject("title").field("type", "string").field("index", "false").endObject().endObject().endObject().endObject();
        IndexMetaData metaData = getIndexMetaData("test_notnull_columns", builder);
        DocIndexMetaData md = newMeta(metaData, "test_notnull_columns");
        assertThat(md.columns().get(0).isNullable(), Matchers.is(false));
        assertThat(md.columns().get(1).isNullable(), Matchers.is(false));
    }

    @Test
    public void testSchemaWithNotNullNestedColumns() throws Exception {
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject(DEFAULT_MAPPING_TYPE).startObject("_meta").startObject("constraints").array("not_null", "nested.level1", "nested.level1.level2").endObject().endObject().startObject("properties").startObject("nested").field("type", "object").startObject("properties").startObject("level1").field("type", "object").startObject("properties").startObject("level2").field("type", "string").endObject().endObject().endObject().endObject().endObject().endObject().endObject().endObject();
        IndexMetaData metaData = getIndexMetaData("test_notnull_columns", builder);
        DocIndexMetaData md = newMeta(metaData, "test_notnull_columns");
        ColumnIdent level1 = new ColumnIdent("nested", "level1");
        ColumnIdent level2 = new ColumnIdent("nested", Arrays.asList("level1", "level2"));
        assertThat(md.notNullColumns(), Matchers.containsInAnyOrder(level1, level2));
        assertThat(md.references().get(level1).isNullable(), Matchers.is(false));
        assertThat(md.references().get(level2).isNullable(), Matchers.is(false));
    }

    @Test
    public void testSchemaWithNotNullGeneratedColumn() throws Exception {
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject("_meta").startObject("generated_columns").field("week", "date_trunc('week', ts)").endObject().startObject("constraints").array("not_null", "week").endObject().endObject().startObject("properties").startObject("ts").field("type", "date").endObject().startObject("week").field("type", "long").endObject().endObject().endObject();
        IndexMetaData metaData = getIndexMetaData("test1", builder);
        DocIndexMetaData md = newMeta(metaData, "test1");
        assertThat(md.columns().size(), Matchers.is(2));
        Reference week = md.references().get(new ColumnIdent("week"));
        assertThat(week, Matchers.notNullValue());
        assertThat(week.isNullable(), Matchers.is(false));
        assertThat(week, Matchers.instanceOf(GeneratedReference.class));
        assertThat(formattedGeneratedExpression(), Matchers.is("date_trunc('week', ts)"));
        assertThat(generatedExpression(), SymbolMatchers.isFunction("date_trunc", SymbolMatchers.isLiteral("week"), SymbolMatchers.isReference("ts")));
        assertThat(referencedReferences(), Matchers.contains(SymbolMatchers.isReference("ts")));
    }

    @Test
    public void extractRoutingColumn() throws Exception {
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject(DEFAULT_MAPPING_TYPE).startObject("_meta").field("primary_keys", "id").endObject().startObject("properties").startObject("id").field("type", "integer").endObject().startObject("title").field("type", "multi_field").field("path", "just_name").startObject("fields").startObject("title").field("type", "string").endObject().startObject("ft").field("type", "string").field("index", "true").field("analyzer", "english").endObject().endObject().endObject().startObject("datum").field("type", "date").endObject().startObject("content").field("type", "multi_field").field("path", "just_name").startObject("fields").startObject("content").field("type", "string").field("index", "false").endObject().startObject("ft").field("type", "string").field("index", "true").field("analyzer", "english").endObject().endObject().endObject().endObject().endObject().endObject();
        DocIndexMetaData md = newMeta(getIndexMetaData("test8", builder), "test8");
        assertThat(md.routingCol(), Matchers.is(new ColumnIdent("id")));
        builder = XContentFactory.jsonBuilder().startObject().startObject(DEFAULT_MAPPING_TYPE).startObject("properties").startObject("content").field("type", "string").endObject().endObject().endObject().endObject();
        md = newMeta(getIndexMetaData("test9", builder), "test8");
        assertThat(md.routingCol(), Matchers.is(new ColumnIdent("_id")));
        builder = XContentFactory.jsonBuilder().startObject().startObject(DEFAULT_MAPPING_TYPE).startObject("_meta").array("primary_keys", "id", "num").field("routing", "num").endObject().startObject("properties").startObject("id").field("type", "integer").endObject().startObject("num").field("type", "long").endObject().startObject("content").field("type", "string").field("index", "false").endObject().endObject().endObject().endObject();
        md = newMeta(getIndexMetaData("test10", builder), "test10");
        assertThat(md.routingCol(), Matchers.is(new ColumnIdent("num")));
    }

    @Test
    public void extractRoutingColumnFromEmptyIndex() throws Exception {
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject(DEFAULT_MAPPING_TYPE).endObject().endObject();
        DocIndexMetaData md = newMeta(getIndexMetaData("test11", builder), "test11");
        assertThat(md.routingCol(), Matchers.is(new ColumnIdent("_id")));
    }

    @Test
    public void testAutogeneratedPrimaryKey() throws Exception {
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject(DEFAULT_MAPPING_TYPE).endObject().endObject();
        DocIndexMetaData md = newMeta(getIndexMetaData("test11", builder), "test11");
        assertThat(md.primaryKey().size(), Matchers.is(1));
        assertThat(md.primaryKey().get(0), Matchers.is(new ColumnIdent("_id")));
        assertThat(md.hasAutoGeneratedPrimaryKey(), Matchers.is(true));
    }

    @Test
    public void testNoAutogeneratedPrimaryKey() throws Exception {
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject(DEFAULT_MAPPING_TYPE).startObject("_meta").field("primary_keys", "id").endObject().startObject("properties").startObject("id").field("type", "integer").endObject().endObject().endObject().endObject();
        DocIndexMetaData md = newMeta(getIndexMetaData("test11", builder), "test11");
        assertThat(md.primaryKey().size(), Matchers.is(1));
        assertThat(md.primaryKey().get(0), Matchers.is(new ColumnIdent("id")));
        assertThat(md.hasAutoGeneratedPrimaryKey(), Matchers.is(false));
    }

    @Test
    public void testAnalyzedColumnWithAnalyzer() throws Exception {
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject(DEFAULT_MAPPING_TYPE).startObject("properties").startObject("content_de").field("type", "text").field("index", "true").field("analyzer", "german").endObject().startObject("content_en").field("type", "text").field("analyzer", "english").endObject().endObject().endObject().endObject();
        DocIndexMetaData md = newMeta(getIndexMetaData("test_analyzer", builder), "test_analyzer");
        assertThat(md.columns().size(), Matchers.is(2));
        assertThat(md.columns().get(0).indexType(), Matchers.is(ANALYZED));
        assertThat(md.columns().get(0).column().fqn(), Matchers.is("content_de"));
        assertThat(md.columns().get(1).indexType(), Matchers.is(ANALYZED));
        assertThat(md.columns().get(1).column().fqn(), Matchers.is("content_en"));
    }

    @Test
    public void testGeoPointType() throws Exception {
        DocIndexMetaData md = getDocIndexMetaDataFromStatement("create table foo (p geo_point)");
        assertThat(md.columns().size(), Matchers.is(1));
        Reference reference = md.columns().get(0);
        assertThat(reference.valueType(), Matchers.equalTo(GEO_POINT));
    }

    @Test
    public void testCreateTableMappingGenerationAndParsingCompat() throws Exception {
        DocIndexMetaData md = getDocIndexMetaDataFromStatement(("create table foo (" + ((((((("id int primary key," + "tags array(string),") + "o object as (") + "   age int,") + "   name string") + "),") + "date timestamp primary key") + ") partitioned by (date)")));
        assertThat(md.columns().size(), Matchers.is(4));
        assertThat(md.primaryKey(), Matchers.contains(new ColumnIdent("id"), new ColumnIdent("date")));
        assertThat(md.references().get(new ColumnIdent("tags")).valueType(), Matchers.is(new io.crate.types.ArrayType(DataTypes.STRING)));
    }

    @Test
    public void testCreateTableMappingGenerationAndParsingArrayInsideObject() throws Exception {
        DocIndexMetaData md = getDocIndexMetaDataFromStatement(("create table t1 (" + (("id int primary key," + "details object as (names array(string))") + ") with (number_of_replicas=0)")));
        DataType type = md.references().get(new ColumnIdent("details", "names")).valueType();
        assertThat(type, Matchers.equalTo(new io.crate.types.ArrayType(DataTypes.STRING)));
    }

    @Test
    public void testCreateTableMappingGenerationAndParsingCompatNoMeta() throws Exception {
        DocIndexMetaData md = getDocIndexMetaDataFromStatement("create table foo (id int, name string)");
        assertThat(md.columns().size(), Matchers.is(2));
        assertThat(md.hasAutoGeneratedPrimaryKey(), Matchers.is(true));
    }

    @Test
    public void testCompoundIndexColumn() throws Exception {
        DocIndexMetaData md = getDocIndexMetaDataFromStatement(("create table t (" + (((("  id integer primary key," + "  name string,") + "  fun string index off,") + "  INDEX fun_name_ft using fulltext(name, fun)") + ")")));
        assertThat(md.indices().size(), Matchers.is(1));
        assertThat(md.columns().size(), Matchers.is(3));
        assertThat(md.indices().get(ColumnIdent.fromPath("fun_name_ft")), Matchers.instanceOf(IndexReference.class));
        IndexReference indexInfo = md.indices().get(ColumnIdent.fromPath("fun_name_ft"));
        assertThat(indexInfo.indexType(), Matchers.is(ANALYZED));
        assertThat(indexInfo.column().fqn(), Matchers.is("fun_name_ft"));
    }

    @Test
    public void testCompoundIndexColumnNested() throws Exception {
        DocIndexMetaData md = getDocIndexMetaDataFromStatement(("create table t (" + (((((("  id integer primary key," + "  name string,") + "  o object as (") + "    fun string") + "  ),") + "  INDEX fun_name_ft using fulltext(name, o['fun'])") + ")")));
        assertThat(md.indices().size(), Matchers.is(1));
        assertThat(md.columns().size(), Matchers.is(3));
        assertThat(md.indices().get(ColumnIdent.fromPath("fun_name_ft")), Matchers.instanceOf(IndexReference.class));
        IndexReference indexInfo = md.indices().get(ColumnIdent.fromPath("fun_name_ft"));
        assertThat(indexInfo.indexType(), Matchers.is(ANALYZED));
        assertThat(indexInfo.column().fqn(), Matchers.is("fun_name_ft"));
    }

    @Test
    public void testExtractColumnPolicy() throws Exception {
        XContentBuilder ignoredBuilder = XContentFactory.jsonBuilder().startObject().startObject(DEFAULT_MAPPING_TYPE).field("dynamic", false).startObject("properties").startObject("id").field("type", "integer").endObject().startObject("content").field("type", "string").field("index", "false").endObject().endObject().endObject().endObject();
        DocIndexMetaData mdIgnored = newMeta(getIndexMetaData("test_ignored", ignoredBuilder), "test_ignored");
        assertThat(mdIgnored.columnPolicy(), Matchers.is(IGNORED));
        XContentBuilder strictBuilder = XContentFactory.jsonBuilder().startObject().startObject(DEFAULT_MAPPING_TYPE).field("dynamic", "strict").startObject("properties").startObject("id").field("type", "integer").endObject().startObject("content").field("type", "string").field("index", "false").endObject().endObject().endObject().endObject();
        DocIndexMetaData mdStrict = newMeta(getIndexMetaData("test_strict", strictBuilder), "test_strict");
        assertThat(mdStrict.columnPolicy(), Matchers.is(STRICT));
        XContentBuilder dynamicBuilder = XContentFactory.jsonBuilder().startObject().startObject(DEFAULT_MAPPING_TYPE).field("dynamic", true).startObject("properties").startObject("id").field("type", "integer").endObject().startObject("content").field("type", "string").field("index", "false").endObject().endObject().endObject().endObject();
        DocIndexMetaData mdDynamic = newMeta(getIndexMetaData("test_dynamic", dynamicBuilder), "test_dynamic");
        assertThat(mdDynamic.columnPolicy(), Matchers.is(DYNAMIC));
        XContentBuilder missingBuilder = XContentFactory.jsonBuilder().startObject().startObject(DEFAULT_MAPPING_TYPE).startObject("properties").startObject("id").field("type", "integer").endObject().startObject("content").field("type", "string").field("index", "false").endObject().endObject().endObject().endObject();
        DocIndexMetaData mdMissing = newMeta(getIndexMetaData("test_missing", missingBuilder), "test_missing");
        assertThat(mdMissing.columnPolicy(), Matchers.is(DYNAMIC));
        XContentBuilder wrongBuilder = XContentFactory.jsonBuilder().startObject().startObject(DEFAULT_MAPPING_TYPE).field("dynamic", "wrong").startObject("properties").startObject("id").field("type", "integer").endObject().startObject("content").field("type", "string").field("index", "false").endObject().endObject().endObject().endObject();
        DocIndexMetaData mdWrong = newMeta(getIndexMetaData("test_wrong", wrongBuilder), "test_wrong");
        assertThat(mdWrong.columnPolicy(), Matchers.is(DYNAMIC));
    }

    @Test
    public void testCreateArrayMapping() throws Exception {
        DocIndexMetaData md = getDocIndexMetaDataFromStatement(("create table t (" + ((("  id integer primary key," + "  tags array(string),") + "  scores array(short)") + ")")));
        assertThat(md.references().get(ColumnIdent.fromPath("tags")).valueType(), Matchers.is(new io.crate.types.ArrayType(DataTypes.STRING)));
        assertThat(md.references().get(ColumnIdent.fromPath("scores")).valueType(), Matchers.is(new io.crate.types.ArrayType(DataTypes.SHORT)));
    }

    @Test
    public void testCreateObjectArrayMapping() throws Exception {
        DocIndexMetaData md = getDocIndexMetaDataFromStatement(("create table t (" + (((((("  id integer primary key," + "  tags array(object(strict) as (") + "    size double index off,") + "    numbers array(integer),") + "    quote string index using fulltext") + "  ))") + ")")));
        assertThat(md.references().get(ColumnIdent.fromPath("tags")).valueType(), Matchers.is(new io.crate.types.ArrayType(ObjectType.untyped())));
        assertThat(md.references().get(ColumnIdent.fromPath("tags")).columnPolicy(), Matchers.is(STRICT));
        assertThat(md.references().get(ColumnIdent.fromPath("tags.size")).valueType(), Matchers.is(DOUBLE));
        assertThat(md.references().get(ColumnIdent.fromPath("tags.size")).indexType(), Matchers.is(NO));
        assertThat(md.references().get(ColumnIdent.fromPath("tags.numbers")).valueType(), Matchers.is(new io.crate.types.ArrayType(DataTypes.INTEGER)));
        assertThat(md.references().get(ColumnIdent.fromPath("tags.quote")).valueType(), Matchers.is(STRING));
        assertThat(md.references().get(ColumnIdent.fromPath("tags.quote")).indexType(), Matchers.is(ANALYZED));
    }

    @Test
    public void testNoBackwardCompatibleArrayMapping() throws Exception {
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject("_meta").field("primary_keys", "id").startObject("columns").startObject("array_col").field("collection_type", "array").endObject().startObject("nested").startObject("properties").startObject("inner_nested").field("collection_type", "array").endObject().endObject().endObject().endObject().endObject().startObject("properties").startObject("id").field("type", "integer").endObject().startObject("title").field("type", "string").field("index", "false").endObject().startObject("array_col").field("type", "ip").endObject().startObject("nested").field("type", "nested").startObject("properties").startObject("inner_nested").field("type", "date").endObject().endObject().endObject().endObject().endObject();
        IndexMetaData indexMetaData = getIndexMetaData("test1", builder);
        DocIndexMetaData docIndexMetaData = newMeta(indexMetaData, "test1");
        // ARRAY TYPES NOT DETECTED
        assertThat(docIndexMetaData.references().get(ColumnIdent.fromPath("array_col")).valueType(), Matchers.is(IP));
        assertThat(docIndexMetaData.references().get(ColumnIdent.fromPath("nested.inner_nested")).valueType(), Matchers.is(TIMESTAMP));
    }

    @Test
    public void testNewArrayMapping() throws Exception {
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject("_meta").field("primary_keys", "id").endObject().startObject("properties").startObject("id").field("type", "integer").endObject().startObject("title").field("type", "string").field("index", "false").endObject().startObject("array_col").field("type", "array").startObject("inner").field("type", "ip").endObject().endObject().startObject("nested").field("type", "object").startObject("properties").startObject("inner_nested").field("type", "array").startObject("inner").field("type", "date").endObject().endObject().endObject().endObject().endObject().endObject();
        IndexMetaData indexMetaData = getIndexMetaData("test1", builder);
        DocIndexMetaData docIndexMetaData = newMeta(indexMetaData, "test1");
        assertThat(docIndexMetaData.references().get(ColumnIdent.fromPath("array_col")).valueType(), Matchers.is(new io.crate.types.ArrayType(DataTypes.IP)));
        assertThat(docIndexMetaData.references().get(ColumnIdent.fromPath("nested.inner_nested")).valueType(), Matchers.is(new io.crate.types.ArrayType(DataTypes.TIMESTAMP)));
    }

    @Test
    public void testStringArrayWithFulltextIndex() throws Exception {
        DocIndexMetaData metaData = getDocIndexMetaDataFromStatement("create table t (tags array(string) index using fulltext)");
        Reference reference = metaData.columns().get(0);
        assertThat(reference.valueType(), Matchers.equalTo(new io.crate.types.ArrayType(DataTypes.STRING)));
    }

    @Test
    public void testCreateTableWithNestedPrimaryKey() throws Exception {
        DocIndexMetaData metaData = getDocIndexMetaDataFromStatement("create table t (o object as (x int primary key))");
        assertThat(metaData.primaryKey(), Matchers.contains(new ColumnIdent("o", "x")));
        metaData = getDocIndexMetaDataFromStatement("create table t (x object as (y object as (z int primary key)))");
        assertThat(metaData.primaryKey(), Matchers.contains(new ColumnIdent("x", Arrays.asList("y", "z"))));
    }

    @Test
    public void testSchemaWithGeneratedColumn() throws Exception {
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject("_meta").startObject("generated_columns").field("week", "date_trunc('week', ts)").endObject().endObject().startObject("properties").startObject("ts").field("type", "date").endObject().startObject("week").field("type", "long").endObject().endObject().endObject();
        IndexMetaData metaData = getIndexMetaData("test1", builder);
        DocIndexMetaData md = newMeta(metaData, "test1");
        assertThat(md.columns().size(), Matchers.is(2));
        Reference week = md.references().get(new ColumnIdent("week"));
        assertThat(week, Matchers.notNullValue());
        assertThat(week, Matchers.instanceOf(GeneratedReference.class));
        assertThat(formattedGeneratedExpression(), Matchers.is("date_trunc('week', ts)"));
        assertThat(generatedExpression(), SymbolMatchers.isFunction("date_trunc", SymbolMatchers.isLiteral("week"), SymbolMatchers.isReference("ts")));
        assertThat(referencedReferences(), Matchers.contains(SymbolMatchers.isReference("ts")));
    }

    @Test
    public void testCopyToWithoutMetaIndices() throws Exception {
        // regression test... this mapping used to cause an NPE
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject("properties").startObject("description").field("type", "string").array("copy_to", "description_ft").endObject().startObject("description_ft").field("type", "string").field("analyzer", "english").endObject().endObject().endObject();
        IndexMetaData metaData = getIndexMetaData("test1", builder);
        DocIndexMetaData md = newMeta(metaData, "test1");
        assertThat(md.indices().size(), Matchers.is(1));
        assertThat(md.indices().keySet().iterator().next(), Matchers.is(new ColumnIdent("description_ft")));
    }

    @Test
    public void testArrayAsGeneratedColumn() throws Exception {
        DocIndexMetaData md = getDocIndexMetaDataFromStatement("create table t1 (x as ([10, 20]))");
        GeneratedReference generatedReference = md.generatedColumnReferences().get(0);
        assertThat(generatedReference.valueType(), Matchers.is(new io.crate.types.ArrayType(DataTypes.LONG)));
    }

    @Test
    public void testColumnStoreBooleanIsParsedCorrectly() throws Exception {
        DocIndexMetaData md = getDocIndexMetaDataFromStatement("create table t1 (x string STORAGE WITH (columnstore = false))");
        assertThat(md.columns().get(0).isColumnStoreDisabled(), Matchers.is(true));
    }
}

