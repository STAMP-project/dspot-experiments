package com.alibaba.json.bvt.parser;


import Feature.AllowComment;
import Feature.AllowSingleQuotes;
import Feature.AllowUnQuotedFieldNames;
import Feature.AutoCloseSource;
import Feature.InternFieldNames;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.util.IOUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class FeatureTest extends TestCase {
    public void test_default() throws Exception {
        DefaultJSONParser parser = new DefaultJSONParser("");
        Assert.assertEquals(false, parser.isEnabled(AllowComment));
        Assert.assertEquals(true, parser.isEnabled(AllowSingleQuotes));
        Assert.assertEquals(true, parser.isEnabled(AllowUnQuotedFieldNames));
        Assert.assertEquals(true, parser.isEnabled(AutoCloseSource));
        Assert.assertEquals(true, parser.isEnabled(InternFieldNames));
    }

    public void test_config() throws Exception {
        new IOUtils();
        DefaultJSONParser parser = new DefaultJSONParser("");
        Assert.assertEquals(false, parser.isEnabled(AllowComment));
        Assert.assertEquals(true, parser.isEnabled(AllowSingleQuotes));
        Assert.assertEquals(true, parser.isEnabled(AllowUnQuotedFieldNames));
        Assert.assertEquals(true, parser.isEnabled(AutoCloseSource));
        Assert.assertEquals(true, parser.isEnabled(InternFieldNames));
        parser.config(AllowComment, true);
        Assert.assertEquals(true, parser.isEnabled(AllowComment));
        parser.config(InternFieldNames, false);
        Assert.assertEquals(false, parser.isEnabled(InternFieldNames));
    }
}

