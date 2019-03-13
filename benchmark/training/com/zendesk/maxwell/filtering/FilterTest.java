package com.zendesk.maxwell.filtering;


import FilterPatternType.BLACKLIST;
import FilterPatternType.INCLUDE;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;


public class FilterTest {
    private List<FilterPattern> filters;

    @Test
    public void TestParser() throws Exception {
        filters = runParserTest("include:/foo.*/.bar");
        Assert.assertEquals(1, filters.size());
        Assert.assertEquals(INCLUDE, filters.get(0).getType());
        Assert.assertEquals(Pattern.compile("foo.*").toString(), filters.get(0).getDatabasePattern().toString());
        Assert.assertEquals(Pattern.compile("^bar$").toString(), filters.get(0).getTablePattern().toString());
    }

    @Test
    public void TestBlacklists() throws Exception {
        filters = runParserTest("blacklist:foo.*");
        Assert.assertEquals(1, filters.size());
        Assert.assertEquals(BLACKLIST, filters.get(0).getType());
        Assert.assertEquals(Pattern.compile("^foo$").toString(), filters.get(0).getDatabasePattern().toString());
    }

    @Test
    public void TestQuoting() throws Exception {
        String[] tests = new String[]{ "include:`foo`.*", "include:'foo'.*", "include:\"foo\".*" };
        for (String test : tests) {
            filters = runParserTest(test);
            Assert.assertEquals(1, filters.size());
            Assert.assertEquals(Pattern.compile("^foo$").toString(), filters.get(0).getDatabasePattern().toString());
        }
    }

    @Test
    public void TestOddNames() throws Exception {
        runParserTest("include: 1foo.bar");
        runParserTest("include: _foo._bar");
    }

    @Test
    public void TestAdvancedRegexp() throws Exception {
        String pattern = "\\w+ \\/[a-z]*1";
        filters = runParserTest((("include: /" + pattern) + "/.*"));
        Assert.assertEquals(1, filters.size());
        Assert.assertEquals(Pattern.compile(pattern).toString(), filters.get(0).getDatabasePattern().toString());
    }

    @Test
    public void TestColumnFilterParse() throws Exception {
        String input = "include: 1foo.bar.column=foo";
        filters = runParserTest(input);
        Assert.assertEquals(1, filters.size());
        Assert.assertEquals(input, filters.get(0).toString());
    }

    @Test
    public void TestExcludeAll() throws Exception {
        Filter f = new Filter("exclude: *.*, include: foo.bar");
        Assert.assertTrue(f.includes("foo", "bar"));
        Assert.assertFalse(f.includes("anything", "else"));
    }

    @Test
    public void TestBlacklist() throws Exception {
        Filter f = new Filter("blacklist: seria.*");
        Assert.assertTrue(f.includes("foo", "bar"));
        Assert.assertFalse(f.includes("seria", "var"));
        Assert.assertTrue(f.isDatabaseBlacklisted("seria"));
        Assert.assertTrue(f.isTableBlacklisted("seria", "anything"));
    }

    @Test
    public void TestColumnFiltersAreIgnoredByIncludes() throws Exception {
        Filter f = new Filter("exclude: *.*, include: foo.bar.col=val");
        Assert.assertFalse(f.includes("foo", "bar"));
    }

    @Test
    public void TestColumnFilters() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("col", "val");
        Filter f = new Filter("exclude: *.*, include: foo.bar.col=val");
        Assert.assertFalse(f.includes("foo", "bar"));
        Assert.assertTrue(f.includes("foo", "bar", map));
    }

    @Test
    public void TestColumnFiltersFromOtherTables() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("col", "val");
        Filter f = new Filter("exclude: *.*, include: no.go.col=val");
        Assert.assertFalse(f.includes("foo", "bar"));
        Assert.assertFalse(f.includes("foo", "bar", map));
    }

    @Test
    public void TestNullValuesInData() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("col", null);
        Filter f = new Filter("exclude: *.*, include: foo.bar.col=null");
        Assert.assertFalse(f.includes("foo", "bar"));
        Assert.assertTrue(f.includes("foo", "bar", map));
        map.put("col", "null");
        Assert.assertTrue(f.includes("foo", "bar", map));
        map.remove("col");
        Assert.assertFalse(f.includes("foo", "bar", map));
    }

    @Test
    public void TestOldFiltersExcludeDB() throws Exception {
        Filter f = Filter.fromOldFormat("maxwell", null, "foo, /bar/", null, null, null, null, null);
        List<FilterPattern> rules = f.getRules();
        Assert.assertEquals(2, rules.size());
        Assert.assertEquals("exclude: foo.*", rules.get(0).toString());
        Assert.assertEquals("exclude: /bar/.*", rules.get(1).toString());
    }

    @Test
    public void TestOldFiltersIncludeDB() throws Exception {
        Filter f = Filter.fromOldFormat("maxwell", "foo", null, null, null, null, null, null);
        List<FilterPattern> rules = f.getRules();
        Assert.assertEquals(2, rules.size());
        Assert.assertEquals("exclude: *.*", rules.get(0).toString());
        Assert.assertEquals("include: foo.*", rules.get(1).toString());
    }

    @Test
    public void TestOldFiltersExcludeTable() throws Exception {
        Filter f = Filter.fromOldFormat("maxwell", null, null, null, "tbl", null, null, null);
        List<FilterPattern> rules = f.getRules();
        Assert.assertEquals(1, rules.size());
        Assert.assertEquals("exclude: *.tbl", rules.get(0).toString());
    }

    @Test
    public void TestOldFiltersIncludeTable() throws Exception {
        Filter f = Filter.fromOldFormat("maxwell", null, null, "/foo.*bar/", null, null, null, null);
        List<FilterPattern> rules = f.getRules();
        Assert.assertEquals(2, rules.size());
        Assert.assertEquals("exclude: *.*", rules.get(0).toString());
        Assert.assertEquals("include: *./foo.*bar/", rules.get(1).toString());
    }

    @Test
    public void TestOldFiltersIncludeColumnValues() throws Exception {
        Filter f = Filter.fromOldFormat("maxwell", null, null, null, null, null, null, "foo=bar");
        List<FilterPattern> rules = f.getRules();
        Assert.assertEquals(2, rules.size());
        Assert.assertEquals("exclude: *.*.foo=*", rules.get(0).toString());
        Assert.assertEquals("include: *.*.foo=bar", rules.get(1).toString());
    }
}

