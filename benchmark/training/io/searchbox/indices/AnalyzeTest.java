package io.searchbox.indices;


import ElasticsearchVersion.UNKNOWN;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;


/**
 *
 *
 * @author cihat keser
 */
public class AnalyzeTest {
    @Test
    public void testBasicUrlGeneration() {
        Analyze analyze = new Analyze.Builder().analyzer("standard").tokenizer("keyword").filter("lowercase").text("this is a test").build();
        Assert.assertEquals("/_analyze", analyze.getURI(UNKNOWN));
    }

    @Test
    public void testPayloadWithAListTextEntry() throws Exception {
        Analyze analyze = new Analyze.Builder().text(ImmutableList.of("foo", "bar")).text("baz").build();
        String expectedJSON = "{\n" + (("\"filter\": [],\n" + "\"text\": [\"foo\", \"bar\", \"baz\"]\n") + "}");
        JSONAssert.assertEquals(expectedJSON, analyze.getData(new Gson()), false);
    }

    @Test
    public void equalsReturnsTrueForSameSource() {
        Analyze analyze1 = new Analyze.Builder().index("test").analyzer("whitespace").text("source").build();
        Analyze analyze1Duplicate = new Analyze.Builder().index("test").analyzer("whitespace").text("source").build();
        Assert.assertEquals(analyze1, analyze1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentSources() {
        Analyze analyze1 = new Analyze.Builder().index("test").analyzer("whitespace").text("source").build();
        Analyze analyze2 = new Analyze.Builder().index("test").analyzer("whitespace").text("source2").build();
        Assert.assertNotEquals(analyze1, analyze2);
    }
}

