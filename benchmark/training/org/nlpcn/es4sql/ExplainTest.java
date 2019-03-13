package org.nlpcn.es4sql;


import com.alibaba.druid.support.json.JSONUtils;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.nlpcn.es4sql.exception.SqlParseException;


public class ExplainTest {
    @Test
    public void searchSanity() throws IOException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, SQLFeatureNotSupportedException, SqlParseException {
        String expectedOutput = Files.toString(new File("src/test/resources/expectedOutput/search_explain.json"), StandardCharsets.UTF_8).replaceAll("\r", "");
        String result = explain(String.format("SELECT * FROM %s WHERE firstname LIKE 'A%%' AND age > 20 GROUP BY gender order by _score", TestsConstants.TEST_INDEX_ACCOUNT));
        MatcherAssert.assertThat(result.replaceAll("\\s+", ""), equalTo(expectedOutput.replaceAll("\\s+", "")));
    }

    @Test
    public void aggregationQuery() throws IOException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, SQLFeatureNotSupportedException, SqlParseException {
        String expectedOutput = Files.toString(new File("src/test/resources/expectedOutput/aggregation_query_explain.json"), StandardCharsets.UTF_8).replaceAll("\r", "");
        String result = explain(String.format("SELECT a, CASE WHEN gender='0' then 'aaa' else 'bbb'end a2345,count(c) FROM %s GROUP BY terms('field'='a','execution_hint'='global_ordinals'),a2345", TestsConstants.TEST_INDEX_ACCOUNT));
        MatcherAssert.assertThat(result.replaceAll("\\s+", ""), equalTo(expectedOutput.replaceAll("\\s+", "")));
    }

    @Test
    public void explainScriptValue() throws IOException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, SQLFeatureNotSupportedException, SqlParseException {
        String expectedOutput = Files.toString(new File("src/test/resources/expectedOutput/script_value.json"), StandardCharsets.UTF_8).replaceAll("\r", "");
        String result = explain(String.format("SELECT  case when gender is null then 'aaa'  else gender  end  test , cust_code FROM %s", TestsConstants.TEST_INDEX_ACCOUNT));
        MatcherAssert.assertThat(result.replaceAll("\\s+", ""), equalTo(expectedOutput.replaceAll("\\s+", "")));
    }

    @Test
    public void betweenScriptValue() throws IOException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, SQLFeatureNotSupportedException, SqlParseException {
        String expectedOutput = Files.toString(new File("src/test/resources/expectedOutput/between_query.json"), StandardCharsets.UTF_8).replaceAll("\r", "");
        String result = explain(String.format("SELECT  case when value between 100 and 200 then 'aaa'  else value  end  test , cust_code FROM %s", TestsConstants.TEST_INDEX_ACCOUNT));
        MatcherAssert.assertThat(result.replaceAll("\\s+", ""), equalTo(expectedOutput.replaceAll("\\s+", "")));
    }

    @Test
    public void searchSanityFilter() throws IOException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, SQLFeatureNotSupportedException, SqlParseException {
        String expectedOutput = Files.toString(new File("src/test/resources/expectedOutput/search_explain_filter.json"), StandardCharsets.UTF_8).replaceAll("\r", "");
        String result = explain(String.format("SELECT * FROM %s WHERE firstname LIKE 'A%%' AND age > 20 GROUP BY gender", TestsConstants.TEST_INDEX_ACCOUNT));
        MatcherAssert.assertThat(result.replaceAll("\\s+", ""), equalTo(expectedOutput.replaceAll("\\s+", "")));
    }

    @Test
    public void deleteSanity() throws IOException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, SQLFeatureNotSupportedException, SqlParseException {
        String expectedOutput = Files.toString(new File("src/test/resources/expectedOutput/delete_explain.json"), StandardCharsets.UTF_8).replaceAll("\r", "");
        String result = explain(String.format("DELETE FROM %s WHERE firstname LIKE 'A%%' AND age > 20", TestsConstants.TEST_INDEX_ACCOUNT));
        MatcherAssert.assertThat(result.replaceAll("\\s+", ""), equalTo(expectedOutput.replaceAll("\\s+", "")));
    }

    @Test
    public void spatialFilterExplainTest() throws IOException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, SQLFeatureNotSupportedException, SqlParseException {
        String expectedOutput = Files.toString(new File("src/test/resources/expectedOutput/search_spatial_explain.json"), StandardCharsets.UTF_8).replaceAll("\r", "");
        String result = explain(String.format("SELECT * FROM %s WHERE GEO_INTERSECTS(place,'POLYGON ((102 2, 103 2, 103 3, 102 3, 102 2))')", TestsConstants.TEST_INDEX_LOCATION));
        MatcherAssert.assertThat(result.replaceAll("\\s+", ""), equalTo(expectedOutput.replaceAll("\\s+", "")));
    }

    @Test
    public void orderByOnNestedFieldTest() throws Exception {
        String result = explain(String.format("SELECT * FROM %s ORDER BY NESTED('message.info','message')", TestsConstants.TEST_INDEX_NESTED_TYPE));
        MatcherAssert.assertThat(result.replaceAll("\\s+", ""), equalTo("{\"from\":0,\"size\":1000,\"sort\":[{\"message.info\":{\"order\":\"asc\",\"nested\":{\"path\":\"message\"}}}]}"));
    }

    @Test
    public void multiMatchQuery() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        String expectedOutput = Files.toString(new File("src/test/resources/expectedOutput/multi_match_query.json"), StandardCharsets.UTF_8).replaceAll("\r", "");
        String result = explain(String.format("SELECT * FROM %s WHERE q=multimatch(query='this is a test',fields='subject^3,message',analyzer='standard',type='best_fields',boost=1.0,slop=0,tie_breaker=0.3,operator='and')", TestsConstants.TEST_INDEX_ACCOUNT));
        MatcherAssert.assertThat(result.replaceAll("\\s+", ""), equalTo(expectedOutput.replaceAll("\\s+", "")));
    }

    @Test
    public void termsIncludeExcludeExplainTest() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        System.out.println(explain("SELECT * FROM index GROUP BY terms(field=\'correspond_brand_name\',size=\'10\',alias=\'correspond_brand_name\',include=\'\".*sport.*\"\',exclude=\'\"water_.*\"\')"));
        System.out.println(explain("SELECT * FROM index GROUP BY terms(field=\'correspond_brand_name\',size=\'10\',alias=\'correspond_brand_name\',include=\'[\"mazda\", \"honda\"]\',exclude=\'[\"rover\", \"jensen\"]\')"));
        System.out.println(explain("SELECT * FROM index GROUP BY terms(field=\'correspond_brand_name\',size=\'10\',alias=\'correspond_brand_name\',include=\'{\"partition\":0,\"num_partitions\":20}\')"));
    }

    @Test
    public void testSpanNearQueryExplain() throws SQLFeatureNotSupportedException, SqlParseException {
        System.out.println(explain("SELECT * FROM index WHERE q=span_near(boost=10.0,slop=12,in_order=false,clauses=\'[{\"span_term\":{\"field\":\"value1\"}},{\"span_term\":{\"field\":\"value2\"}},{\"span_term\":{\"field\":\"value3\"}}]\')"));
    }

    @Test
    public void testCountDistinctExplain() throws SQLFeatureNotSupportedException, SqlParseException {
        System.out.println(explain("SELECT COUNT(DISTINCT sourceIP.keyword) AS size FROM dataflow WHERE startTime > 525757149439 AND startTime < 1525757449439 GROUP BY appName.keyword ORDER BY size DESC"));
    }

    @Test
    public void testStatsGroupsExplain() throws SQLFeatureNotSupportedException, SqlParseException {
        Map map = ((Map) (JSONUtils.parse(explain("SELECT /*! STATS(group1, group2) */ * FROM index"))));
        MatcherAssert.assertThat(map.get("stats").toString(), equalTo("[group1, group2]"));
    }

    @Test
    public void testCastInWhereExplain() throws SQLFeatureNotSupportedException, SqlParseException {
        System.out.println(explain("select * from file1 where cast(offset as int) > 20"));
    }
}

