package org.jsoup.integration;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;


public class AmplParseTest {
    @Test(timeout = 10000)
    public void testBaidu_literalMutationString9833_failAssert0_add17094_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                Element submit = doc.select("#su").first();
                submit.attr("value");
                submit = doc.select("input[value=百度一下]").first();
                submit.id();
                doc.select("a:contains(新)").first();
                Element newsLink = doc.select("a:contains(新)").first();
                newsLink.absUrl("href");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                doc.outputSettings().charset("ascii");
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaidu_literalMutationString9833 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString9833_failAssert0_add17094 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString9833_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testBaidu_literalMutationString9833 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString9833_failAssert0null18327_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                Element submit = doc.select("#su").first();
                submit.attr("value");
                submit = doc.select(null).first();
                submit.id();
                Element newsLink = doc.select("a:contains(新)").first();
                newsLink.absUrl("href");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                doc.outputSettings().charset("ascii");
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaidu_literalMutationString9833 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString9833_failAssert0null18327 should have thrown FileNotFoundException");
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
    public void testBaiduVariant_literalMutationString1_failAssert0_literalMutationString522_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_literalMutationString522 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_add806_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset();
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_add806 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_add810_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_add810 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_literalMutationString528_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("ti;tle").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_literalMutationString528 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString3834_failAssert0_literalMutationString5950_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
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
                org.junit.Assert.fail("testHtml5Charset_literalMutationString3834 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString3834_failAssert0_literalMutationString5950 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString3834_failAssert0_add7660_failAssert0() throws IOException {
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
                org.junit.Assert.fail("testHtml5Charset_literalMutationString3834 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString3834_failAssert0_add7660 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString3858_literalMutationString5320_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_literalMutationString3858__7 = doc.text();
            String o_testHtml5Charset_literalMutationString3858__8 = doc.outputSettings().charset().displayName();
            in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            String o_testHtml5Charset_literalMutationString3858__18 = doc.outputSettings().charset().displayName();
            boolean o_testHtml5Charset_literalMutationString3858__21 = "".equals(doc.text());
            in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
            doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_literalMutationString3858__29 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_literalMutationString3858__32 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_literalMutationString3858__35 = doc.text();
            String o_testHtml5Charset_literalMutationString3858__36 = doc.text();
            org.junit.Assert.fail("testHtml5Charset_literalMutationString3858_literalMutationString5320 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString3834_failAssert0_literalMutationString5951_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                doc.text();
                doc.outputSettings().charset().displayName();
                in = AmplParseTest.getFile("/htmltests/meta-cparset-2.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.outputSettings().charset().displayName();
                "新".equals(doc.text());
                in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString3834 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString3834_failAssert0_literalMutationString5951 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString3852_literalMutationString4201_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
            Document doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_literalMutationString3852__7 = doc.text();
            String o_testHtml5Charset_literalMutationString3852__8 = doc.outputSettings().charset().displayName();
            in = AmplParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example+com");
            String o_testHtml5Charset_literalMutationString3852__18 = doc.outputSettings().charset().displayName();
            boolean o_testHtml5Charset_literalMutationString3852__21 = "新".equals(doc.text());
            in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
            doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_literalMutationString3852__29 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_literalMutationString3852__32 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_literalMutationString3852__35 = doc.text();
            String o_testHtml5Charset_literalMutationString3852__36 = doc.text();
            org.junit.Assert.fail("testHtml5Charset_literalMutationString3852_literalMutationString4201 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString3834_failAssert0null8442_failAssert0() throws IOException {
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
                in = AmplParseTest.getFile(null);
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString3834 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString3834_failAssert0null8442 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString3834_failAssert0_add7667_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                doc.text();
                doc.outputSettings().charset().displayName();
                in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                "新".equals(doc.text());
                in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString3834 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString3834_failAssert0_add7667 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString3834_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testHtml5Charset_literalMutationString3834 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString3847_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testHtml5Charset_literalMutationString3847 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString3847_failAssert0_add7759_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                doc.text();
                doc.outputSettings().charset();
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
                org.junit.Assert.fail("testHtml5Charset_literalMutationString3847 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString3847_failAssert0_add7759 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString3861_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testHtml5Charset_literalMutationString3861 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_add8801_literalMutationString9047_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            Element headline = doc.select("nyt_headline[version=1.0]").first();
            String o_testNytArticle_add8801__9 = headline.text();
            String o_testNytArticle_add8801__10 = headline.text();
            org.junit.Assert.fail("testNytArticle_add8801_literalMutationString9047 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString8780_failAssert0_literalMutationString9248_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString8780 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString8780_failAssert0_literalMutationString9248 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString8779_failAssert0_literalMutationString9197_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString8779 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString8779_failAssert0_literalMutationString9197 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString8782_failAssert0_literalMutationString9286_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString8782 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString8782_failAssert0_literalMutationString9286 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString8779_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            Element headline = doc.select("nyt_headline[version=1.0]").first();
            headline.text();
            org.junit.Assert.fail("testNytArticle_literalMutationString8779 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString8779_failAssert0_add9468_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString8779 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString8779_failAssert0_add9468 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString1192_failAssert0_literalMutationString1447_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#Morm").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString1192 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString1192_failAssert0_literalMutationString1447 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString1186_failAssert0_literalMutationString1390_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString1186 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString1186_failAssert0_literalMutationString1390 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString1184_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null);
            Element form = doc.select("#form").first();
            form.children().size();
            doc.outputSettings().charset().name();
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString1184 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString1184_failAssert0_add1760_failAssert0() throws IOException {
        try {
            {
                AmplParseTest.getFile("");
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString1184 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString1184_failAssert0_add1760 should have thrown FileNotFoundException");
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

