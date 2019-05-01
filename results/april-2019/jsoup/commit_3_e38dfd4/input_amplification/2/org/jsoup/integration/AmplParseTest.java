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
    public void testBaidu_literalMutationString10934_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
            Element submit = doc.select("#su").first();
            submit.attr("value");
            submit = doc.select("input[value=????]").first();
            submit.id();
            Element newsLink = doc.select("a:contains(?)").first();
            newsLink.absUrl("href");
            doc.outputSettings().charset().displayName();
            doc.select("title").outerHtml();
            doc.outputSettings().charset("ascii");
            doc.select("title").outerHtml();
            org.junit.Assert.fail("testBaidu_literalMutationString10934 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString10934_failAssert0_add17582_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                Element submit = doc.select("#su").first();
                submit.attr("value");
                submit.attr("value");
                submit = doc.select("input[value=????]").first();
                submit.id();
                Element newsLink = doc.select("a:contains(?)").first();
                newsLink.absUrl("href");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                doc.outputSettings().charset("ascii");
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaidu_literalMutationString10934 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString10934_failAssert0_add17582 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString10934_failAssert0null19036_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                Element submit = doc.select("#su").first();
                submit.attr(null);
                submit = doc.select("input[value=????]").first();
                submit.id();
                Element newsLink = doc.select("a:contains(?)").first();
                newsLink.absUrl("href");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                doc.outputSettings().charset("ascii");
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaidu_literalMutationString10934 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString10934_failAssert0null19036 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString10934_failAssert0_literalMutationString13992_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                Element submit = doc.select("#u").first();
                submit.attr("value");
                submit = doc.select("input[value=????]").first();
                submit.id();
                Element newsLink = doc.select("a:contains(?)").first();
                newsLink.absUrl("href");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                doc.outputSettings().charset("ascii");
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaidu_literalMutationString10934 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString10934_failAssert0_literalMutationString13992 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString10934_failAssert0null19039_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                Element submit = doc.select("#su").first();
                submit.attr("value");
                submit = doc.select("input[value=????]").first();
                submit.id();
                Element newsLink = doc.select("a:contains(?)").first();
                newsLink.absUrl(null);
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                doc.outputSettings().charset("ascii");
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaidu_literalMutationString10934 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString10934_failAssert0null19039 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString10934_failAssert0_literalMutationString14037_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                Element submit = doc.select("#su").first();
                submit.attr("value");
                submit = doc.select("input[value=????]").first();
                submit.id();
                Element newsLink = doc.select("a:contains(?)").first();
                newsLink.absUrl("href");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                doc.outputSettings().charset("ascii");
                doc.select("tqitle").outerHtml();
                org.junit.Assert.fail("testBaidu_literalMutationString10934 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString10934_failAssert0_literalMutationString14037 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString10934_failAssert0_add17585_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                Element submit = doc.select("#su").first();
                submit.attr("value");
                submit = doc.select("input[value=????]").first();
                submit.id();
                submit.id();
                Element newsLink = doc.select("a:contains(?)").first();
                newsLink.absUrl("href");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                doc.outputSettings().charset("ascii");
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaidu_literalMutationString10934 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString10934_failAssert0_add17585 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_add22_literalMutationString193_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
            Charset o_testBaiduVariant_add22__7 = doc.outputSettings().charset();
            String o_testBaiduVariant_add22__9 = doc.outputSettings().charset().displayName();
            String o_testBaiduVariant_add22__12 = doc.select("title").outerHtml();
            org.junit.Assert.fail("testBaiduVariant_add22_literalMutationString193 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_literalMutationString533_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("tile").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_literalMutationString533 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_literalMutationString527_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "FWMNQqzZj3Vz*likaUWOo");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_literalMutationString527 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString18_literalMutationString361_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
            String o_testBaiduVariant_literalMutationString18__7 = doc.outputSettings().charset().displayName();
            String o_testBaiduVariant_literalMutationString18__10 = doc.select("ttle").outerHtml();
            org.junit.Assert.fail("testBaiduVariant_literalMutationString18_literalMutationString361 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_literalMutationString525_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baid^u.com/");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_literalMutationString525 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString2_failAssert0_literalMutationString466_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString2 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString2_failAssert0_literalMutationString466 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString8_literalMutationString253_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http&//www.baidu.com/");
            String o_testBaiduVariant_literalMutationString8__7 = doc.outputSettings().charset().displayName();
            String o_testBaiduVariant_literalMutationString8__10 = doc.select("title").outerHtml();
            org.junit.Assert.fail("testBaiduVariant_literalMutationString8_literalMutationString253 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_add795_failAssert0() throws IOException {
        try {
            {
                AmplParseTest.getFile("");
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_add795 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_add796_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Jsoup.parse(in, null, "http://www.baidu.com/");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_add796 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariantnull29_failAssert0_literalMutationString592_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select(null).outerHtml();
                org.junit.Assert.fail("testBaiduVariantnull29 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testBaiduVariantnull29_failAssert0_literalMutationString592 should have thrown FileNotFoundException");
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
    public void testBaiduVariant_literalMutationString1_failAssert0_add797_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_add797 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0null941_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select(null).outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0null941 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString4522_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
            Document doc = Jsoup.parse(in, null, "http://example.com/");
            doc.text();
            doc.outputSettings().charset().displayName();
            in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            doc.outputSettings().charset().displayName();
            "?".equals(doc.text());
            in = AmplParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example.com/");
            doc.outputSettings().charset().displayName();
            doc.outputSettings().charset().displayName();
            doc.text();
            doc.text();
            org.junit.Assert.fail("testHtml5Charset_literalMutationString4522 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString4531_literalMutationString5822_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_literalMutationString4531__7 = doc.text();
            String o_testHtml5Charset_literalMutationString4531__8 = doc.outputSettings().charset().displayName();
            in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            String o_testHtml5Charset_literalMutationString4531__18 = doc.outputSettings().charset().displayName();
            boolean o_testHtml5Charset_literalMutationString4531__21 = "?".equals(doc.text());
            in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
            doc = Jsoup.parse(in, null, "Jay");
            String o_testHtml5Charset_literalMutationString4531__29 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_literalMutationString4531__32 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_literalMutationString4531__35 = doc.text();
            String o_testHtml5Charset_literalMutationString4531__36 = doc.text();
            org.junit.Assert.fail("testHtml5Charset_literalMutationString4531_literalMutationString5822 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString4508_failAssert0_literalMutationString6575_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                doc.text();
                doc.outputSettings().charset().displayName();
                in = AmplParseTest.getFile("");
                doc = Jsoup.parse(in, null, "Jay");
                doc.outputSettings().charset().displayName();
                "?".equals(doc.text());
                in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString4508 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString4508_failAssert0_literalMutationString6575 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString4508_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
            Document doc = Jsoup.parse(in, null, "http://example.com/");
            doc.text();
            doc.outputSettings().charset().displayName();
            in = AmplParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example.com");
            doc.outputSettings().charset().displayName();
            "?".equals(doc.text());
            in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
            doc = Jsoup.parse(in, null, "http://example.com/");
            doc.outputSettings().charset().displayName();
            doc.outputSettings().charset().displayName();
            doc.text();
            doc.text();
            org.junit.Assert.fail("testHtml5Charset_literalMutationString4508 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString4509_failAssert0_literalMutationString6747_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                doc.text();
                doc.outputSettings().charset().displayName();
                in = AmplParseTest.getFile("[Ps BWE:$@r`tGaPB6j^)/Oi{wan]6");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.outputSettings().charset().displayName();
                "?".equals(doc.text());
                in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString4509 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString4509_failAssert0_literalMutationString6747 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_add4540_literalMutationString5261_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_add4540__7 = doc.text();
            String o_testHtml5Charset_add4540__8 = doc.outputSettings().charset().displayName();
            AmplParseTest.getFile("/htmltests/meta-charset-2.html");
            in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            String o_testHtml5Charset_add4540__19 = doc.outputSettings().charset().displayName();
            boolean o_testHtml5Charset_add4540__22 = "?".equals(doc.text());
            in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
            doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_add4540__30 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_add4540__33 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_add4540__36 = doc.text();
            String o_testHtml5Charset_add4540__37 = doc.text();
            org.junit.Assert.fail("testHtml5Charset_add4540_literalMutationString5261 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString4522_failAssert0_literalMutationString6728_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                doc.text();
                doc.outputSettings().charset().displayName();
                in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
                doc = Jsoup.parse(in, null, "");
                doc.outputSettings().charset().displayName();
                "?".equals(doc.text());
                in = AmplParseTest.getFile("");
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString4522 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString4522_failAssert0_literalMutationString6728 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString4522_failAssert0_literalMutationString6717_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
                Document doc = Jsoup.parse(in, null, "Jay");
                doc.text();
                doc.outputSettings().charset().displayName();
                in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.outputSettings().charset().displayName();
                "?".equals(doc.text());
                in = AmplParseTest.getFile("");
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString4522 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString4522_failAssert0_literalMutationString6717 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString4522_failAssert0_add8373_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                doc.text();
                doc.outputSettings();
                doc.outputSettings().charset().displayName();
                in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.outputSettings().charset().displayName();
                "?".equals(doc.text());
                in = AmplParseTest.getFile("");
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString4522 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString4522_failAssert0_add8373 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString4508_failAssert0_add8285_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                doc.text();
                doc.outputSettings().charset().displayName();
                in = AmplParseTest.getFile("");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.outputSettings().charset().displayName();
                "?".equals(doc.text());
                AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString4508 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString4508_failAssert0_add8285 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString4522_failAssert0_add8385_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                doc.text();
                doc.outputSettings().charset().displayName();
                in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.outputSettings().charset().displayName();
                "?".equals(doc.text());
                in = AmplParseTest.getFile("");
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings();
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString4522 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString4522_failAssert0_add8385 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_add4548_literalMutationString4799_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
            Document doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_add4548__7 = doc.text();
            String o_testHtml5Charset_add4548__8 = doc.outputSettings().charset().displayName();
            in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            String o_testHtml5Charset_add4548__18 = doc.outputSettings().charset().displayName();
            boolean o_testHtml5Charset_add4548__21 = "?".equals(doc.text());
            in = AmplParseTest.getFile("");
            Document o_testHtml5Charset_add4548__26 = Jsoup.parse(in, null, "http://example.com/");
            doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_add4548__30 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_add4548__33 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_add4548__36 = doc.text();
            String o_testHtml5Charset_add4548__37 = doc.text();
            org.junit.Assert.fail("testHtml5Charset_add4548_literalMutationString4799 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_add4541_literalMutationString5351_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_add4541__7 = doc.text();
            String o_testHtml5Charset_add4541__8 = doc.outputSettings().charset().displayName();
            in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
            Document o_testHtml5Charset_add4541__15 = Jsoup.parse(in, null, "http://example.com");
            doc = Jsoup.parse(in, null, "http://example.com");
            String o_testHtml5Charset_add4541__19 = doc.outputSettings().charset().displayName();
            boolean o_testHtml5Charset_add4541__22 = "?".equals(doc.text());
            in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
            doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_add4541__30 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_add4541__33 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_add4541__36 = doc.text();
            String o_testHtml5Charset_add4541__37 = doc.text();
            org.junit.Assert.fail("testHtml5Charset_add4541_literalMutationString5351 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString4524_failAssert0_literalMutationString6929_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                doc.text();
                doc.outputSettings().charset().displayName();
                in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.outputSettings().charset().displayName();
                "?".equals(doc.text());
                in = AmplParseTest.getFile("");
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString4524 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString4524_failAssert0_literalMutationString6929 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString4528_literalMutationString5786_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_literalMutationString4528__7 = doc.text();
            String o_testHtml5Charset_literalMutationString4528__8 = doc.outputSettings().charset().displayName();
            in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            String o_testHtml5Charset_literalMutationString4528__18 = doc.outputSettings().charset().displayName();
            boolean o_testHtml5Charset_literalMutationString4528__21 = "?".equals(doc.text());
            in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
            doc = Jsoup.parse(in, null, "");
            String o_testHtml5Charset_literalMutationString4528__29 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_literalMutationString4528__32 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_literalMutationString4528__35 = doc.text();
            String o_testHtml5Charset_literalMutationString4528__36 = doc.text();
            org.junit.Assert.fail("testHtml5Charset_literalMutationString4528_literalMutationString5786 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString4495_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://example.com/");
            doc.text();
            doc.outputSettings().charset().displayName();
            in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            doc.outputSettings().charset().displayName();
            "?".equals(doc.text());
            in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
            doc = Jsoup.parse(in, null, "http://example.com/");
            doc.outputSettings().charset().displayName();
            doc.outputSettings().charset().displayName();
            doc.text();
            doc.text();
            org.junit.Assert.fail("testHtml5Charset_literalMutationString4495 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString4505_literalMutationString5474_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "ZlIEE2Rz8sLs!2v9_?a");
            String o_testHtml5Charset_literalMutationString4505__7 = doc.text();
            String o_testHtml5Charset_literalMutationString4505__8 = doc.outputSettings().charset().displayName();
            in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            String o_testHtml5Charset_literalMutationString4505__18 = doc.outputSettings().charset().displayName();
            boolean o_testHtml5Charset_literalMutationString4505__21 = "?".equals(doc.text());
            in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
            doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_literalMutationString4505__29 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_literalMutationString4505__32 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_literalMutationString4505__35 = doc.text();
            String o_testHtml5Charset_literalMutationString4505__36 = doc.text();
            org.junit.Assert.fail("testHtml5Charset_literalMutationString4505_literalMutationString5474 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString9668_failAssert0null10489_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, null);
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString9668 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString9668_failAssert0null10489 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticlenull9693_failAssert0_literalMutationString10212_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, null);
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticlenull9693 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testNytArticlenull9693_failAssert0_literalMutationString10212 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString9668_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            Element headline = doc.select("nyt_headline[version=1.0]").first();
            headline.text();
            org.junit.Assert.fail("testNytArticle_literalMutationString9668 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString9668_failAssert0_literalMutationString10148_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("nyt_headline[versionz=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString9668 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString9668_failAssert0_literalMutationString10148 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString9668_failAssert0_literalMutationString10137_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString9668 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString9668_failAssert0_literalMutationString10137 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticlenull9694_failAssert0_literalMutationString10188_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select(null).first();
                headline.text();
                org.junit.Assert.fail("testNytArticlenull9694 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testNytArticlenull9694_failAssert0_literalMutationString10188 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString9668_failAssert0_add10365_failAssert0() throws IOException {
        try {
            {
                AmplParseTest.getFile("");
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString9668 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString9668_failAssert0_add10365 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString9668_failAssert0null10490_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select(null).first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString9668 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString9668_failAssert0null10490 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString9668_failAssert0_add10369_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString9668 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString9668_failAssert0_add10369 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString9668_failAssert0_add10368_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                doc.select("nyt_headline[version=1.0]");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString9668 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString9668_failAssert0_add10368 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString1393_failAssert0_add1947_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString1393 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString1393_failAssert0_add1947 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString1394_failAssert0_literalMutationString1632_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString1394 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString1394_failAssert0_literalMutationString1632 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString1393_failAssert0_literalMutationString1691_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString1393 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString1393_failAssert0_literalMutationString1691 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString1393_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null);
            Element form = doc.select("#form").first();
            form.children().size();
            doc.outputSettings().charset().name();
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString1393 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString1393_failAssert0_literalMutationString1699_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#;form").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString1393 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString1393_failAssert0_literalMutationString1699 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString1393_failAssert0_add1945_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                form.children().size();
                doc.outputSettings().charset();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString1393 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString1393_failAssert0_add1945 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString1393_failAssert0_add1941_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                doc.select("#form");
                Element form = doc.select("#form").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString1393 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString1393_failAssert0_add1941 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString1393_failAssert0_literalMutationString1696_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("s:x[l").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString1393 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString1393_failAssert0_literalMutationString1696 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_add1408_literalMutationString1503_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null);
            Element form = doc.select("#form").first();
            int o_testLowercaseUtf8Charset_add1408__8 = form.children().size();
            int o_testLowercaseUtf8Charset_add1408__10 = form.children().size();
            String o_testLowercaseUtf8Charset_add1408__12 = doc.outputSettings().charset().name();
            org.junit.Assert.fail("testLowercaseUtf8Charset_add1408_literalMutationString1503 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString1393_failAssert0null2047_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select(null).first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString1393 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString1393_failAssert0null2047 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString1393_failAssert0_add1938_failAssert0() throws IOException {
        try {
            {
                AmplParseTest.getFile("");
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString1393 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString1393_failAssert0_add1938 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_add1412_literalMutationString1574_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null);
            Element form = doc.select("#form").first();
            int o_testLowercaseUtf8Charset_add1412__8 = form.children().size();
            doc.outputSettings();
            String o_testLowercaseUtf8Charset_add1412__11 = doc.outputSettings().charset().name();
            org.junit.Assert.fail("testLowercaseUtf8Charset_add1412_literalMutationString1574 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString1397_failAssert0_literalMutationString1655_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString1397 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString1397_failAssert0_literalMutationString1655 should have thrown FileNotFoundException");
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

