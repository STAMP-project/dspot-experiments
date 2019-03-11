package org.activiti.editor.language.xml;


import java.util.List;
import org.activiti.bpmn.converter.util.CommaSplitter;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class CommaSplitterTest {
    @Test
    public void testNoComma() {
        String testString = "Test String";
        List<String> result = CommaSplitter.splitCommas(testString);
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(testString, result.get(0));
    }

    @Test
    public void testOneComa() {
        String testString = "Test,String";
        List<String> result = CommaSplitter.splitCommas(testString);
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("Test", result.get(0));
        Assert.assertEquals("String", result.get(1));
    }

    @Test
    public void testManyCommas() {
        String testString = "does,anybody,realy,reads,this,nonsense";
        List<String> result = CommaSplitter.splitCommas(testString);
        Assert.assertNotNull(result);
        Assert.assertEquals(6, result.size());
        Assert.assertEquals("does", result.get(0));
        Assert.assertEquals("anybody", result.get(1));
        Assert.assertEquals("realy", result.get(2));
        Assert.assertEquals("reads", result.get(3));
        Assert.assertEquals("this", result.get(4));
        Assert.assertEquals("nonsense", result.get(5));
    }

    @Test
    public void testCommaAtStart() {
        String testString = ",first,second";
        List<String> result = CommaSplitter.splitCommas(testString);
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("first", result.get(0));
        Assert.assertEquals("second", result.get(1));
    }

    @Test
    public void testCommaAtEnd() {
        String testString = "first,second,";
        List<String> result = CommaSplitter.splitCommas(testString);
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("first", result.get(0));
        Assert.assertEquals("second", result.get(1));
    }

    @Test
    public void testCommaAtStartAndEnd() {
        String testString = ",first,second,";
        List<String> result = CommaSplitter.splitCommas(testString);
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("first", result.get(0));
        Assert.assertEquals("second", result.get(1));
    }

    @Test
    public void testOneComaInExpression() {
        String testString = "${first,second}";
        List<String> result = CommaSplitter.splitCommas(testString);
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(testString, result.get(0));
    }

    @Test
    public void testOManyComaInExpression() {
        String testString = "${Everything,should,be,made,as,simple,as,possible},but,no,simpler";
        List<String> result = CommaSplitter.splitCommas(testString);
        Assert.assertNotNull(result);
        Assert.assertEquals(4, result.size());
        Assert.assertEquals("${Everything,should,be,made,as,simple,as,possible}", result.get(0));
        Assert.assertEquals("but", result.get(1));
        Assert.assertEquals("no", result.get(2));
        Assert.assertEquals("simpler", result.get(3));
    }
}

