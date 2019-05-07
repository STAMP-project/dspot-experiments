package org.jsoup.integration;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;


public class AmplParseTest {
    @Test(timeout = 10000)
    public void testBaidu_literalMutationString27990_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
            Element submit = doc.select("#su").first();
            submit.attr("value");
            submit = doc.select("input[value=百度一下]").first();
            submit.id();
            Element newsLink = doc.select("a:contains(新)").first();
            newsLink.absUrl("href");
            doc.outputSettings().charset().displayName();
            doc.select("title").outerHtml();
            doc.outputSettings().charset("ascii");
            doc.select("title").outerHtml();
            org.junit.Assert.fail("testBaidu_literalMutationString27990 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString27990_failAssert0_add35104_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                Element submit = doc.select("#su").first();
                submit.attr("value");
                submit = doc.select("input[value=百度一下]").first();
                submit.id();
                doc.select("a:contains(新)");
                Element newsLink = doc.select("a:contains(新)").first();
                newsLink.absUrl("href");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                doc.outputSettings().charset("ascii");
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaidu_literalMutationString27990 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString27990_failAssert0_add35104 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString27997_literalMutationString30787_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www(baidu.com/");
            Element submit = doc.select("#su").first();
            String o_testBaidu_literalMutationString27997__10 = submit.attr("value");
            submit = doc.select("input[value=百度一下]").first();
            String o_testBaidu_literalMutationString27997__15 = submit.id();
            Element newsLink = doc.select("a:contains(新)").first();
            String o_testBaidu_literalMutationString27997__19 = newsLink.absUrl("href");
            String o_testBaidu_literalMutationString27997__20 = doc.outputSettings().charset().displayName();
            String o_testBaidu_literalMutationString27997__23 = doc.select("title").outerHtml();
            doc.outputSettings().charset("ascii");
            String o_testBaidu_literalMutationString27997__27 = doc.select("title").outerHtml();
            org.junit.Assert.fail("testBaidu_literalMutationString27997_literalMutationString30787 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString27999_literalMutationString29781_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://wwwbaidu.com/");
            Element submit = doc.select("#su").first();
            String o_testBaidu_literalMutationString27999__10 = submit.attr("value");
            submit = doc.select("input[value=百度一下]").first();
            String o_testBaidu_literalMutationString27999__15 = submit.id();
            Element newsLink = doc.select("a:contains(新)").first();
            String o_testBaidu_literalMutationString27999__19 = newsLink.absUrl("href");
            String o_testBaidu_literalMutationString27999__20 = doc.outputSettings().charset().displayName();
            String o_testBaidu_literalMutationString27999__23 = doc.select("title").outerHtml();
            doc.outputSettings().charset("ascii");
            String o_testBaidu_literalMutationString27999__27 = doc.select("title").outerHtml();
            org.junit.Assert.fail("testBaidu_literalMutationString27999_literalMutationString29781 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString27999_literalMutationString29781_failAssert0_literalMutationString40974_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://wwwbaidu.com/");
                Element submit = doc.select("#su").first();
                String o_testBaidu_literalMutationString27999__10 = submit.attr("value");
                submit = doc.select("input[value=百度一下]").first();
                String o_testBaidu_literalMutationString27999__15 = submit.id();
                Element newsLink = doc.select("a:contains(新)").first();
                String o_testBaidu_literalMutationString27999__19 = newsLink.absUrl("href");
                String o_testBaidu_literalMutationString27999__20 = doc.outputSettings().charset().displayName();
                String o_testBaidu_literalMutationString27999__23 = doc.select("Jay").outerHtml();
                doc.outputSettings().charset("ascii");
                String o_testBaidu_literalMutationString27999__27 = doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaidu_literalMutationString27999_literalMutationString29781 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString27999_literalMutationString29781_failAssert0_literalMutationString40974 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString27999_literalMutationString29781_failAssert0null45076_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://wwwbaidu.com/");
                Element submit = doc.select("#su").first();
                String o_testBaidu_literalMutationString27999__10 = submit.attr(null);
                submit = doc.select("input[value=百度一下]").first();
                String o_testBaidu_literalMutationString27999__15 = submit.id();
                Element newsLink = doc.select("a:contains(新)").first();
                String o_testBaidu_literalMutationString27999__19 = newsLink.absUrl("href");
                String o_testBaidu_literalMutationString27999__20 = doc.outputSettings().charset().displayName();
                String o_testBaidu_literalMutationString27999__23 = doc.select("title").outerHtml();
                doc.outputSettings().charset("ascii");
                String o_testBaidu_literalMutationString27999__27 = doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaidu_literalMutationString27999_literalMutationString29781 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString27999_literalMutationString29781_failAssert0null45076 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString27990_failAssert0_literalMutationString32216_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                Element submit = doc.select("#su").first();
                submit.attr("value");
                submit = doc.select("input[value=百度一下]").first();
                submit.id();
                Element newsLink = doc.select("a:contains(新)").first();
                newsLink.absUrl("href");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                doc.outputSettings().charset("=iM[`");
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaidu_literalMutationString27990 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString27990_failAssert0_literalMutationString32216 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString27997_literalMutationString30787_failAssert0_add44301_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www(baidu.com/");
                Element submit = doc.select("#su").first();
                String o_testBaidu_literalMutationString27997__10 = submit.attr("value");
                submit = doc.select("input[value=百度一下]").first();
                String o_testBaidu_literalMutationString27997__15 = submit.id();
                Element newsLink = doc.select("a:contains(新)").first();
                String o_testBaidu_literalMutationString27997__19 = newsLink.absUrl("href");
                doc.outputSettings().charset().displayName();
                String o_testBaidu_literalMutationString27997__20 = doc.outputSettings().charset().displayName();
                String o_testBaidu_literalMutationString27997__23 = doc.select("title").outerHtml();
                doc.outputSettings().charset("ascii");
                String o_testBaidu_literalMutationString27997__27 = doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaidu_literalMutationString27997_literalMutationString30787 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString27997_literalMutationString30787_failAssert0_add44301 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString27998_remove35677_literalMutationString37676_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.bTaidu.com/");
            Element submit = doc.select("#su").first();
            String o_testBaidu_literalMutationString27998__10 = submit.attr("value");
            submit = doc.select("input[value=百度一下]").first();
            String o_testBaidu_literalMutationString27998__15 = submit.id();
            Element newsLink = doc.select("a:contains(新)").first();
            String o_testBaidu_literalMutationString27998__19 = newsLink.absUrl("href");
            String o_testBaidu_literalMutationString27998__20 = doc.outputSettings().charset().displayName();
            String o_testBaidu_literalMutationString27998__23 = doc.select("title").outerHtml();
            String o_testBaidu_literalMutationString27998__27 = doc.select("title").outerHtml();
            org.junit.Assert.fail("testBaidu_literalMutationString27998_remove35677_literalMutationString37676 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString27990_failAssert0_add35104_failAssert0_add42997_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    Element submit = doc.select("#su").first();
                    submit.attr("value");
                    submit = doc.select("input[value=百度一下]").first();
                    submit.id();
                    doc.select("a:contains(新)");
                    Element newsLink = doc.select("a:contains(新)").first();
                    newsLink.absUrl("href");
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    doc.outputSettings().charset("ascii");
                    doc.outputSettings().charset("ascii");
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaidu_literalMutationString27990 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaidu_literalMutationString27990_failAssert0_add35104 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString27990_failAssert0_add35104_failAssert0_add42997 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString27990_failAssert0_add35104_failAssert0_literalMutationString38733_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    Element submit = doc.select("xsu").first();
                    submit.attr("value");
                    submit = doc.select("input[value=百度一下]").first();
                    submit.id();
                    doc.select("a:contains(新)");
                    Element newsLink = doc.select("a:contains(新)").first();
                    newsLink.absUrl("href");
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    doc.outputSettings().charset("ascii");
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaidu_literalMutationString27990 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaidu_literalMutationString27990_failAssert0_add35104 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString27990_failAssert0_add35104_failAssert0_literalMutationString38733 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString27999_literalMutationString29781_failAssert0_add43808_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://wwwbaidu.com/");
                Element submit = doc.select("#su").first();
                String o_testBaidu_literalMutationString27999__10 = submit.attr("value");
                submit = doc.select("input[value=百度一下]").first();
                submit.id();
                String o_testBaidu_literalMutationString27999__15 = submit.id();
                Element newsLink = doc.select("a:contains(新)").first();
                String o_testBaidu_literalMutationString27999__19 = newsLink.absUrl("href");
                String o_testBaidu_literalMutationString27999__20 = doc.outputSettings().charset().displayName();
                String o_testBaidu_literalMutationString27999__23 = doc.select("title").outerHtml();
                doc.outputSettings().charset("ascii");
                String o_testBaidu_literalMutationString27999__27 = doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaidu_literalMutationString27999_literalMutationString29781 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString27999_literalMutationString29781_failAssert0_add43808 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString27997_literalMutationString30787_failAssert0_literalMutationString42316_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www(baidu.com/");
                Element submit = doc.select("#su").first();
                String o_testBaidu_literalMutationString27997__10 = submit.attr("value");
                submit = doc.select("Jay").first();
                String o_testBaidu_literalMutationString27997__15 = submit.id();
                Element newsLink = doc.select("a:contains(新)").first();
                String o_testBaidu_literalMutationString27997__19 = newsLink.absUrl("href");
                String o_testBaidu_literalMutationString27997__20 = doc.outputSettings().charset().displayName();
                String o_testBaidu_literalMutationString27997__23 = doc.select("title").outerHtml();
                doc.outputSettings().charset("ascii");
                String o_testBaidu_literalMutationString27997__27 = doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaidu_literalMutationString27997_literalMutationString30787 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString27997_literalMutationString30787_failAssert0_literalMutationString42316 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString27990_failAssert0_add35104_failAssert0_add42980_failAssert0() throws IOException {
        try {
            {
                {
                    AmplParseTest.getFile("");
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    Element submit = doc.select("#su").first();
                    submit.attr("value");
                    submit = doc.select("input[value=百度一下]").first();
                    submit.id();
                    doc.select("a:contains(新)");
                    Element newsLink = doc.select("a:contains(新)").first();
                    newsLink.absUrl("href");
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    doc.outputSettings().charset("ascii");
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaidu_literalMutationString27990 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaidu_literalMutationString27990_failAssert0_add35104 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString27990_failAssert0_add35104_failAssert0_add42980 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString6_failAssert0_literalMutationString468_failAssert0null4029_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, null);
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString6 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString6_failAssert0_literalMutationString468 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString6_failAssert0_literalMutationString468_failAssert0null4029 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString6_failAssert0_literalMutationString468_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString6 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString6_failAssert0_literalMutationString468 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString6_failAssert0_literalMutationString468_failAssert0_add3553_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString6 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString6_failAssert0_literalMutationString468 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString6_failAssert0_literalMutationString468_failAssert0_add3553 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_add24_literalMutationString152_failAssert0_literalMutationString2498_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                String o_testBaiduVariant_add24__7 = doc.outputSettings().charset().displayName();
                String o_testBaiduVariant_add24__10 = doc.select("title").outerHtml();
                String o_testBaiduVariant_add24__12 = doc.select("tNtle").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_add24_literalMutationString152 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_add24_literalMutationString152_failAssert0_literalMutationString2498 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_add24_literalMutationString152_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
            String o_testBaiduVariant_add24__7 = doc.outputSettings().charset().displayName();
            String o_testBaiduVariant_add24__10 = doc.select("title").outerHtml();
            String o_testBaiduVariant_add24__12 = doc.select("title").outerHtml();
            org.junit.Assert.fail("testBaiduVariant_add24_literalMutationString152 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString6_failAssert0_literalMutationString468_failAssert0null4030_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    doc.outputSettings().charset().displayName();
                    doc.select(null).outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString6 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString6_failAssert0_literalMutationString468 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString6_failAssert0_literalMutationString468_failAssert0null4030 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_add24_literalMutationString152_failAssert0null4018_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                String o_testBaiduVariant_add24__7 = doc.outputSettings().charset().displayName();
                String o_testBaiduVariant_add24__10 = doc.select("title").outerHtml();
                String o_testBaiduVariant_add24__12 = doc.select(null).outerHtml();
                org.junit.Assert.fail("testBaiduVariant_add24_literalMutationString152 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_add24_literalMutationString152_failAssert0null4018 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString6_failAssert0_literalMutationString468_failAssert0_literalMutationString2545_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    doc.outputSettings().charset().displayName();
                    doc.select("2.9&n").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString6 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString6_failAssert0_literalMutationString468 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString6_failAssert0_literalMutationString468_failAssert0_literalMutationString2545 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
            doc.outputSettings().charset().displayName();
            doc.select("title").outerHtml();
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString6_failAssert0_literalMutationString468_failAssert0_add3547_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Jsoup.parse(in, null, "http://www.baidu.com/");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString6 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString6_failAssert0_literalMutationString468 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString6_failAssert0_literalMutationString468_failAssert0_add3547 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString16_failAssert0_literalMutationString536_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("&Zibr").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString16 should have thrown Selector$SelectorParseException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString16_failAssert0_literalMutationString536 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_add24_literalMutationString152_failAssert0_add3525_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                String o_testBaiduVariant_add24__7 = doc.outputSettings().charset().displayName();
                doc.select("title");
                String o_testBaiduVariant_add24__10 = doc.select("title").outerHtml();
                String o_testBaiduVariant_add24__12 = doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_add24_literalMutationString152 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_add24_literalMutationString152_failAssert0_add3525 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_add22_literalMutationString212_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
            Charset o_testBaiduVariant_add22__7 = doc.outputSettings().charset();
            String o_testBaiduVariant_add22__9 = doc.outputSettings().charset().displayName();
            String o_testBaiduVariant_add22__12 = doc.select("title").outerHtml();
            org.junit.Assert.fail("testBaiduVariant_add22_literalMutationString212 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_add820_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Jsoup.parse(in, null, "http://www.baidu.com/");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_add820 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_literalMutationString562_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "Xr0;<Z| b&dmB;FJGz=fe");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_literalMutationString562 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0null950_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select(null).outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0null950 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString6_failAssert0_literalMutationString468_failAssert0_literalMutationString2539_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "Jay");
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString6 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString6_failAssert0_literalMutationString468 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString6_failAssert0_literalMutationString468_failAssert0_literalMutationString2539 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_add825_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("title");
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_add825 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString12521_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
            Document doc = Jsoup.parse(in, null, "http://example.com/");
            doc.text();
            doc.outputSettings().charset().displayName();
            in = AmplParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example.com");
            doc.outputSettings().charset().displayName();
            "新".equals(doc.text());
            in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
            doc = Jsoup.parse(in, null, "http://example.com/");
            doc.outputSettings().charset().displayName();
            doc.outputSettings().charset().displayName();
            doc.text();
            doc.text();
            org.junit.Assert.fail("testHtml5Charset_literalMutationString12521 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString12509_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://example.com/");
            doc.text();
            doc.outputSettings().charset().displayName();
            in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            doc.outputSettings().charset().displayName();
            "新".equals(doc.text());
            in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
            doc = Jsoup.parse(in, null, "http://example.com/");
            doc.outputSettings().charset().displayName();
            doc.outputSettings().charset().displayName();
            doc.text();
            doc.text();
            org.junit.Assert.fail("testHtml5Charset_literalMutationString12509 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString12509_failAssert0_literalMutationString14674_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                doc.text();
                doc.outputSettings().charset().displayName();
                in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
                doc = Jsoup.parse(in, null, "http://exampe.com");
                doc.outputSettings().charset().displayName();
                "新".equals(doc.text());
                in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString12509 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString12509_failAssert0_literalMutationString14674 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString12509_failAssert0_add16359_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Jsoup.parse(in, null, "http://example.com/");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                doc.text();
                doc.outputSettings().charset().displayName();
                in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.outputSettings().charset().displayName();
                "新".equals(doc.text());
                in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString12509 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString12509_failAssert0_add16359 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString12521_failAssert0_add16394_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                doc.text();
                doc.outputSettings().charset().displayName();
                in = AmplParseTest.getFile("");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.outputSettings().charset().displayName();
                doc.text();
                "新".equals(doc.text());
                in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString12521 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString12521_failAssert0_add16394 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString12537_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
            Document doc = Jsoup.parse(in, null, "http://example.com/");
            doc.text();
            doc.outputSettings().charset().displayName();
            in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            doc.outputSettings().charset().displayName();
            "新".equals(doc.text());
            in = AmplParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example.com/");
            doc.outputSettings().charset().displayName();
            doc.outputSettings().charset().displayName();
            doc.text();
            doc.text();
            org.junit.Assert.fail("testHtml5Charset_literalMutationString12537 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString12521_failAssert0_literalMutationString14722_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                doc.text();
                doc.outputSettings().charset().displayName();
                in = AmplParseTest.getFile("");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.outputSettings().charset().displayName();
                "新".equals(doc.text());
                in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                doc = Jsoup.parse(in, null, "Jay");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString12521 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString12521_failAssert0_literalMutationString14722 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString12521_failAssert0null17135_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                doc.text();
                doc.outputSettings().charset().displayName();
                in = AmplParseTest.getFile("");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.outputSettings().charset().displayName();
                "新".equals(doc.text());
                in = AmplParseTest.getFile(null);
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString12521 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString12521_failAssert0null17135 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString12509_failAssert0null17128_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                doc.text();
                doc.outputSettings().charset().displayName();
                in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.outputSettings().charset().displayName();
                "新".equals(doc.text());
                in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                doc = Jsoup.parse(in, null, null);
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString12509 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString12509_failAssert0null17128 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString12546_literalMutationString13605_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_literalMutationString12546__7 = doc.text();
            String o_testHtml5Charset_literalMutationString12546__8 = doc.outputSettings().charset().displayName();
            in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            String o_testHtml5Charset_literalMutationString12546__18 = doc.outputSettings().charset().displayName();
            boolean o_testHtml5Charset_literalMutationString12546__21 = "新".equals(doc.text());
            in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
            doc = Jsoup.parse(in, null, "M3@ )4Q>dASdL =a# J");
            String o_testHtml5Charset_literalMutationString12546__29 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_literalMutationString12546__32 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_literalMutationString12546__35 = doc.text();
            String o_testHtml5Charset_literalMutationString12546__36 = doc.text();
            org.junit.Assert.fail("testHtml5Charset_literalMutationString12546_literalMutationString13605 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString24138_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            Element headline = doc.select("nyt_headline[version=1.0]").first();
            headline.text();
            org.junit.Assert.fail("testNytArticle_literalMutationString24138 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString24138_failAssert0_add24854_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString24138 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString24138_failAssert0_add24854 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString24146_literalMutationString24380_failAssert0_literalMutationString26174_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "Jay");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                String o_testNytArticle_literalMutationString24146__9 = headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString24146_literalMutationString24380 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString24146_literalMutationString24380_failAssert0_literalMutationString26174 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString24138_failAssert0_add24854_failAssert0null27517_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Jsoup.parse(in, null, null);
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    Element headline = doc.select("nyt_headline[version=1.0]").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString24138 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString24138_failAssert0_add24854 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString24138_failAssert0_add24854_failAssert0null27517 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString24138_failAssert0_add24854_failAssert0_add26963_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    doc.select("nyt_headline[version=1.0]").first();
                    Element headline = doc.select("nyt_headline[version=1.0]").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString24138 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString24138_failAssert0_add24854 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString24138_failAssert0_add24854_failAssert0_add26963 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString24138_failAssert0_add24854_failAssert0_literalMutationString25701_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    Element headline = doc.select("").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString24138 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString24138_failAssert0_add24854 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString24138_failAssert0_add24854_failAssert0_literalMutationString25701 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString24144_literalMutationString24344_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "");
            Element headline = doc.select("nyt_headline[version=1.0]").first();
            String o_testNytArticle_literalMutationString24144__9 = headline.text();
            org.junit.Assert.fail("testNytArticle_literalMutationString24144_literalMutationString24344 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString4313_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null);
            Element form = doc.select("#form").first();
            form.children().size();
            doc.outputSettings().charset().name();
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4313 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charsetnull4336_failAssert0_add4776_failAssert0_literalMutationString5416_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select(null).first();
                    form.children().size();
                    doc.outputSettings().charset();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charsetnull4336 should have thrown IllegalArgumentException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charsetnull4336_failAssert0_add4776 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charsetnull4336_failAssert0_add4776_failAssert0_literalMutationString5416 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString4313_failAssert0_add4822_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                doc.select("#form");
                Element form = doc.select("#form").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4313 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4313_failAssert0_add4822 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString4313_failAssert0_literalMutationString4561_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#gorm").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4313 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4313_failAssert0_literalMutationString4561 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString4313_failAssert0_literalMutationString4561_failAssert0_literalMutationString6034_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select("").first();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4313 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4313_failAssert0_literalMutationString4561 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4313_failAssert0_literalMutationString4561_failAssert0_literalMutationString6034 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString4317_failAssert0null4966_failAssert0_literalMutationString5591_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select(null).first();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4317 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4317_failAssert0null4966 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4317_failAssert0null4966_failAssert0_literalMutationString5591 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charsetnull4336_failAssert0_literalMutationString4507_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select(null).first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charsetnull4336 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charsetnull4336_failAssert0_literalMutationString4507 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString4313_failAssert0null4954_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select(null).first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4313 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4313_failAssert0null4954 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString4322_failAssert0null4975_failAssert0_literalMutationString5522_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select(null).first();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4322 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4322_failAssert0null4975 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4322_failAssert0null4975_failAssert0_literalMutationString5522 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString4314_failAssert0null4948_failAssert0_literalMutationString5609_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select(null).first();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4314 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4314_failAssert0null4948 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4314_failAssert0null4948_failAssert0_literalMutationString5609 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString4313_failAssert0_literalMutationString4561_failAssert0_add6955_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    doc.select("#gorm");
                    Element form = doc.select("#gorm").first();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4313 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4313_failAssert0_literalMutationString4561 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4313_failAssert0_literalMutationString4561_failAssert0_add6955 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString4313_failAssert0_add4822_failAssert0_add6282_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    doc.select("#form");
                    Element form = doc.select("#form").first();
                    form.children().size();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4313 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4313_failAssert0_add4822 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4313_failAssert0_add4822_failAssert0_add6282 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString4320_failAssert0_literalMutationString4645_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("8C <<").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4320 should have thrown Selector$SelectorParseException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4320_failAssert0_literalMutationString4645 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString4313_failAssert0_literalMutationString4555_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4313 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4313_failAssert0_literalMutationString4555 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString4313_failAssert0_add4819_failAssert0() throws IOException {
        try {
            {
                AmplParseTest.getFile("");
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4313 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4313_failAssert0_add4819 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    public static File getFile(String resourceName) {
        try {
            File file = new File(AmplParseTest.class.getResource(resourceName).toURI());
            return file;
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    public static InputStream inputStreamFrom(String s) {
        try {
            return new ByteArrayInputStream(s.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}

