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
    public void testBaidu_literalMutationString47168_failAssert0_literalMutationString49712_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                Element submit = doc.select("#su").first();
                submit.attr("value");
                submit = doc.select("input[value=????]").first();
                submit.id();
                Element newsLink = doc.select("qp81xS@NGo[Wl").first();
                newsLink.absUrl("href");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                doc.outputSettings().charset("ascii");
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaidu_literalMutationString47168 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString47168_failAssert0_literalMutationString49712 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString47168_failAssert0_add53637_failAssert0() throws IOException {
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
                doc.outputSettings();
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                doc.outputSettings().charset("ascii");
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaidu_literalMutationString47168 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString47168_failAssert0_add53637 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString47168_failAssert0_add53637_failAssert0_literalMutationString59156_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    Element submit = doc.select("#su").first();
                    submit.attr("value");
                    submit = doc.select("input[value=????]").first();
                    submit.id();
                    Element newsLink = doc.select("a:contains(?)").first();
                    newsLink.absUrl("href");
                    doc.outputSettings();
                    doc.outputSettings().charset().displayName();
                    doc.select("").outerHtml();
                    doc.outputSettings().charset("ascii");
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaidu_literalMutationString47168 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaidu_literalMutationString47168_failAssert0_add53637 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString47168_failAssert0_add53637_failAssert0_literalMutationString59156 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString47168_failAssert0_literalMutationString49712_failAssert0_add69176_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    Element submit = doc.select("#su").first();
                    submit.attr("value");
                    doc.select("input[value=????]");
                    submit = doc.select("input[value=????]").first();
                    submit.id();
                    Element newsLink = doc.select("qp81xS@NGo[Wl").first();
                    newsLink.absUrl("href");
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    doc.outputSettings().charset("ascii");
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaidu_literalMutationString47168 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaidu_literalMutationString47168_failAssert0_literalMutationString49712 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString47168_failAssert0_literalMutationString49712_failAssert0_add69176 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString47168_failAssert0_add53637_failAssert0null71555_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    Element submit = doc.select("#su").first();
                    submit.attr("value");
                    submit = doc.select("input[value=????]").first();
                    submit.id();
                    Element newsLink = doc.select("a:contains(?)").first();
                    newsLink.absUrl(null);
                    doc.outputSettings();
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    doc.outputSettings().charset("ascii");
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaidu_literalMutationString47168 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaidu_literalMutationString47168_failAssert0_add53637 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString47168_failAssert0_add53637_failAssert0null71555 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString47168_failAssert0_add53637_failAssert0_add68104_failAssert0() throws IOException {
        try {
            {
                {
                    AmplParseTest.getFile("");
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    Element submit = doc.select("#su").first();
                    submit.attr("value");
                    submit = doc.select("input[value=????]").first();
                    submit.id();
                    Element newsLink = doc.select("a:contains(?)").first();
                    newsLink.absUrl("href");
                    doc.outputSettings();
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    doc.outputSettings().charset("ascii");
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaidu_literalMutationString47168 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaidu_literalMutationString47168_failAssert0_add53637 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString47168_failAssert0_add53637_failAssert0_add68104 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString47168_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testBaidu_literalMutationString47168 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString47168_failAssert0_literalMutationString49712_failAssert0_literalMutationString62148_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    Element submit = doc.select("#su").first();
                    submit.attr("value");
                    submit = doc.select("input[value=????]").first();
                    submit.id();
                    Element newsLink = doc.select("qp81xS@NGo[Wl").first();
                    newsLink.absUrl("Jay");
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    doc.outputSettings().charset("ascii");
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaidu_literalMutationString47168 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaidu_literalMutationString47168_failAssert0_literalMutationString49712 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString47168_failAssert0_literalMutationString49712_failAssert0_literalMutationString62148 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString47168_failAssert0null55175_failAssert0() throws IOException {
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
                org.junit.Assert.fail("testBaidu_literalMutationString47168 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString47168_failAssert0null55175 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_literalMutationString452_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "Jay");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_literalMutationString452 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_add765_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_add765 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_add763_failAssert0() throws IOException {
        try {
            {
                AmplParseTest.getFile("");
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_add763 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_add769_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("title");
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_add769 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_add765_failAssert0_literalMutationString4299_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "Lih6.a91rOe/.DWjG#&a[");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_add765 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_add765_failAssert0_literalMutationString4299 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_literalMutationString452_failAssert0_literalMutationString3685_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "Jay");
                    doc.outputSettings().charset().displayName();
                    doc.select("Jay").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_literalMutationString452 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_literalMutationString452_failAssert0_literalMutationString3685 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_add765_failAssert0_literalMutationString4297_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "Jay");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_add765 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_add765_failAssert0_literalMutationString4297 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_add765_failAssert0null6958_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, null);
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_add765 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_add765_failAssert0null6958 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_add765_failAssert0null6959_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.select(null).outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_add765 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_add765_failAssert0null6959 should have thrown FileNotFoundException");
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
    public void testBaiduVariant_literalMutationString8_literalMutationString343_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "Jay");
            String o_testBaiduVariant_literalMutationString8__7 = doc.outputSettings().charset().displayName();
            String o_testBaiduVariant_literalMutationString8__10 = doc.select("title").outerHtml();
            org.junit.Assert.fail("testBaiduVariant_literalMutationString8_literalMutationString343 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_add769_failAssert0_add5187_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.select("title");
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_add769 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_add769_failAssert0_add5187 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_add763_failAssert0_literalMutationString3467_failAssert0() throws IOException {
        try {
            {
                {
                    AmplParseTest.getFile("");
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    doc.outputSettings().charset().displayName();
                    doc.select("gitle").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_add763 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_add763_failAssert0_literalMutationString3467 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_literalMutationString452_failAssert0_literalMutationString3678_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "Bay");
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_literalMutationString452 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_literalMutationString452_failAssert0_literalMutationString3678 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_literalMutationString453_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "yV&; ua:!|.|md_l8${fL");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_literalMutationString453 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_literalMutationString452_failAssert0null6809_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "Jay");
                    doc.outputSettings().charset().displayName();
                    doc.select(null).outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_literalMutationString452 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_literalMutationString452_failAssert0null6809 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString13_failAssert0null936_failAssert0_literalMutationString3312_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, null);
                    doc.outputSettings().charset().displayName();
                    doc.select("").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString13 should have thrown IllegalArgumentException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString13_failAssert0null936 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString13_failAssert0null936_failAssert0_literalMutationString3312 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariantnull28_failAssert0_add832_failAssert0_literalMutationString2832_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, null);
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaiduVariantnull28 should have thrown IllegalArgumentException");
                }
                org.junit.Assert.fail("testBaiduVariantnull28_failAssert0_add832 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testBaiduVariantnull28_failAssert0_add832_failAssert0_literalMutationString2832 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_literalMutationString461_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("x#s[h").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_literalMutationString461 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_literalMutationString452_failAssert0_add5751_failAssert0() throws IOException {
        try {
            {
                {
                    AmplParseTest.getFile("");
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "Jay");
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_literalMutationString452 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_literalMutationString452_failAssert0_add5751 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0null925_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select(null).outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0null925 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_add763_failAssert0_add5647_failAssert0() throws IOException {
        try {
            {
                {
                    AmplParseTest.getFile("");
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    doc.outputSettings().charset().displayName();
                    doc.select("title");
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_add763 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_add763_failAssert0_add5647 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_add763_failAssert0_add5646_failAssert0() throws IOException {
        try {
            {
                {
                    AmplParseTest.getFile("");
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_add763 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_add763_failAssert0_add5646 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString17_failAssert0_literalMutationString430_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("t%itle").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString17 should have thrown Selector$SelectorParseException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString17_failAssert0_literalMutationString430 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_literalMutationString452_failAssert0_add5754_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "Jay");
                    doc.outputSettings().charset();
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_literalMutationString452 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_literalMutationString452_failAssert0_add5754 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString5_failAssert0_literalMutationString481_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString5 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString5_failAssert0_literalMutationString481 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_add20null868_failAssert0_literalMutationString3258_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document o_testBaiduVariant_add20__4 = Jsoup.parse(in, null, "http://www.baidu.com/");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                String o_testBaiduVariant_add20__8 = doc.outputSettings().charset().displayName();
                String o_testBaiduVariant_add20__11 = doc.select(null).outerHtml();
                org.junit.Assert.fail("testBaiduVariant_add20null868 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testBaiduVariant_add20null868_failAssert0_literalMutationString3258 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_add765_failAssert0_add6111_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Jsoup.parse(in, null, "http://www.baidu.com/");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_add765 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_add765_failAssert0_add6111 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_add765_failAssert0_add6112_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_add765 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_add765_failAssert0_add6112 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString22092_failAssert0_literalMutationString24433_failAssert0null39266_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.text();
                    doc.outputSettings().charset().displayName();
                    in = AmplParseTest.getFile("/htmltestssmeta-charset-2.html");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.outputSettings().charset().displayName();
                    "?".equals(doc.text());
                    in = AmplParseTest.getFile(null);
                    doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.text();
                    doc.text();
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString22092 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString22092_failAssert0_literalMutationString24433 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString22092_failAssert0_literalMutationString24433_failAssert0null39266 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString22118_failAssert0_literalMutationString24301_failAssert0() throws IOException {
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
                doc = Jsoup.parse(in, null, "http://example.co/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString22118 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString22118_failAssert0_literalMutationString24301 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString22092_failAssert0null26749_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                doc.text();
                doc.outputSettings().charset().displayName();
                in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
                doc = Jsoup.parse(in, null, null);
                doc.outputSettings().charset().displayName();
                "?".equals(doc.text());
                in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString22092 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString22092_failAssert0null26749 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString22118_failAssert0_add25940_failAssert0() throws IOException {
        try {
            {
                AmplParseTest.getFile("/htmltests/meta-charset-1.html");
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
                org.junit.Assert.fail("testHtml5Charset_literalMutationString22118 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString22118_failAssert0_add25940 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString22103_failAssert0_add25791_failAssert0() throws IOException {
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
                in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString22103 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString22103_failAssert0_add25791 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString22092_failAssert0_literalMutationString24433_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                doc.text();
                doc.outputSettings().charset().displayName();
                in = AmplParseTest.getFile("/htmltestssmeta-charset-2.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.outputSettings().charset().displayName();
                "?".equals(doc.text());
                in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString22092 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString22092_failAssert0_literalMutationString24433 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString22118_failAssert0_literalMutationString24278_failAssert0() throws IOException {
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
                org.junit.Assert.fail("testHtml5Charset_literalMutationString22118 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString22118_failAssert0_literalMutationString24278 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString22112_literalMutationString23742_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
            Document doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_literalMutationString22112__7 = doc.text();
            String o_testHtml5Charset_literalMutationString22112__8 = doc.outputSettings().charset().displayName();
            in = AmplParseTest.getFile("");
            doc = Jsoup.parse(in, null, "Jay");
            String o_testHtml5Charset_literalMutationString22112__18 = doc.outputSettings().charset().displayName();
            boolean o_testHtml5Charset_literalMutationString22112__21 = "?".equals(doc.text());
            in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
            doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_literalMutationString22112__29 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_literalMutationString22112__32 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_literalMutationString22112__35 = doc.text();
            String o_testHtml5Charset_literalMutationString22112__36 = doc.text();
            org.junit.Assert.fail("testHtml5Charset_literalMutationString22112_literalMutationString23742 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_add22141_add24899_literalMutationString28320_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
            Document doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_add22141_add24899__7 = doc.text();
            String o_testHtml5Charset_add22141__7 = doc.text();
            String o_testHtml5Charset_add22141__8 = doc.outputSettings().charset().displayName();
            in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            String o_testHtml5Charset_add22141__18 = doc.outputSettings().charset().displayName();
            boolean o_testHtml5Charset_add22141__21 = "?".equals(doc.text());
            boolean o_testHtml5Charset_add22141__23 = "?".equals(doc.text());
            in = AmplParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_add22141__31 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_add22141__34 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_add22141__37 = doc.text();
            String o_testHtml5Charset_add22141__38 = doc.text();
            org.junit.Assert.fail("testHtml5Charset_add22141_add24899_literalMutationString28320 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString22103_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testHtml5Charset_literalMutationString22103 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString22114_literalMutationString23640_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
            Document doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_literalMutationString22114__7 = doc.text();
            String o_testHtml5Charset_literalMutationString22114__8 = doc.outputSettings().charset().displayName();
            in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
            doc = Jsoup.parse(in, null, "htt://example.com");
            String o_testHtml5Charset_literalMutationString22114__18 = doc.outputSettings().charset().displayName();
            boolean o_testHtml5Charset_literalMutationString22114__21 = "?".equals(doc.text());
            in = AmplParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_literalMutationString22114__29 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_literalMutationString22114__32 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_literalMutationString22114__35 = doc.text();
            String o_testHtml5Charset_literalMutationString22114__36 = doc.text();
            org.junit.Assert.fail("testHtml5Charset_literalMutationString22114_literalMutationString23640 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString22118_failAssert0_literalMutationString24278_failAssert0_add37856_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
                    Document doc = Jsoup.parse(in, null, "Jay");
                    doc.text();
                    doc.outputSettings().charset().displayName();
                    AmplParseTest.getFile("/htmltests/meta-charset-2.html");
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
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString22118 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString22118_failAssert0_literalMutationString24278 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString22118_failAssert0_literalMutationString24278_failAssert0_add37856 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString22118_failAssert0_add25940_failAssert0_add35508_failAssert0() throws IOException {
        try {
            {
                {
                    AmplParseTest.getFile("/htmltests/meta-charset-1.html");
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
                    doc.text();
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString22118 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString22118_failAssert0_add25940 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString22118_failAssert0_add25940_failAssert0_add35508 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString22092_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testHtml5Charset_literalMutationString22092 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString22092_failAssert0_literalMutationString24433_failAssert0_literalMutationString31757_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.text();
                    doc.outputSettings().charset().displayName();
                    in = AmplParseTest.getFile("/htmltestssmea-charset-2.html");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.outputSettings().charset().displayName();
                    "?".equals(doc.text());
                    in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                    doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.text();
                    doc.text();
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString22092 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString22092_failAssert0_literalMutationString24433 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString22092_failAssert0_literalMutationString24433_failAssert0_literalMutationString31757 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString22116_literalMutationString23274_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
            Document doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_literalMutationString22116__7 = doc.text();
            String o_testHtml5Charset_literalMutationString22116__8 = doc.outputSettings().charset().displayName();
            in = AmplParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example.com");
            String o_testHtml5Charset_literalMutationString22116__18 = doc.outputSettings().charset().displayName();
            boolean o_testHtml5Charset_literalMutationString22116__21 = "Jay".equals(doc.text());
            in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
            doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_literalMutationString22116__29 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_literalMutationString22116__32 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_literalMutationString22116__35 = doc.text();
            String o_testHtml5Charset_literalMutationString22116__36 = doc.text();
            org.junit.Assert.fail("testHtml5Charset_literalMutationString22116_literalMutationString23274 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString22092_failAssert0_add26050_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                doc.text();
                doc.outputSettings().charset().displayName();
                in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.outputSettings().charset().displayName();
                "?".equals(doc.text());
                in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                Jsoup.parse(in, null, "http://example.com/");
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString22092 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString22092_failAssert0_add26050 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_add22138_literalMutationString22335_literalMutationString29802_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
            Document doc = Jsoup.parse(in, null, "http://=xample.com/");
            String o_testHtml5Charset_add22138__7 = doc.text();
            String o_testHtml5Charset_add22138__8 = doc.outputSettings().charset().displayName();
            in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            String o_testHtml5Charset_add22138__18 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_add22138__21 = doc.outputSettings().charset().displayName();
            boolean o_testHtml5Charset_add22138__24 = "?".equals(doc.text());
            in = AmplParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_add22138__32 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_add22138__35 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_add22138__38 = doc.text();
            String o_testHtml5Charset_add22138__39 = doc.text();
            org.junit.Assert.fail("testHtml5Charset_add22138_literalMutationString22335_literalMutationString29802 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString22092_failAssert0null26749_failAssert0null39551_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.text();
                    doc.outputSettings().charset().displayName();
                    in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
                    doc = Jsoup.parse(in, null, null);
                    doc.outputSettings().charset().displayName();
                    "?".equals(doc.text());
                    in = AmplParseTest.getFile(null);
                    doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.text();
                    doc.text();
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString22092 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString22092_failAssert0null26749 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString22092_failAssert0null26749_failAssert0null39551 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString22118_failAssert0_literalMutationString24301_failAssert0_add36131_failAssert0() throws IOException {
        try {
            {
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
                    Jsoup.parse(in, null, "http://example.co/");
                    doc = Jsoup.parse(in, null, "http://example.co/");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.text();
                    doc.text();
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString22118 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString22118_failAssert0_literalMutationString24301 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString22118_failAssert0_literalMutationString24301_failAssert0_add36131 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString22103_failAssert0_literalMutationString24023_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                doc.text();
                doc.outputSettings().charset().displayName();
                in = AmplParseTest.getFile("");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.outputSettings().charset().displayName();
                "$".equals(doc.text());
                in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString22103 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString22103_failAssert0_literalMutationString24023 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString22118_failAssert0_literalMutationString24278_failAssert0_literalMutationString33382_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
                    Document doc = Jsoup.parse(in, null, "Jay");
                    doc.text();
                    doc.outputSettings().charset().displayName();
                    in = AmplParseTest.getFile("");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.outputSettings().charset().displayName();
                    "?".equals(doc.text());
                    in = AmplParseTest.getFile("");
                    doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.text();
                    doc.text();
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString22118 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString22118_failAssert0_literalMutationString24278 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString22118_failAssert0_literalMutationString24278_failAssert0_literalMutationString33382 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString22092_failAssert0_add26058_failAssert0() throws IOException {
        try {
            {
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
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString22092 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString22092_failAssert0_add26058 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString22092_failAssert0_literalMutationString24433_failAssert0_add36699_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.text();
                    doc.outputSettings().charset().displayName();
                    in = AmplParseTest.getFile("/htmltestssmeta-charset-2.html");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.outputSettings().charset().displayName();
                    "?".equals(doc.text());
                    in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                    doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.text();
                    doc.text();
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString22092 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString22092_failAssert0_literalMutationString24433 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString22092_failAssert0_literalMutationString24433_failAssert0_add36699 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString22118_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testHtml5Charset_literalMutationString22118 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString22092_failAssert0null26749_failAssert0_literalMutationString32934_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.text();
                    doc.outputSettings().charset().displayName();
                    in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
                    doc = Jsoup.parse(in, null, null);
                    doc.outputSettings().charset().displayName();
                    "".equals(doc.text());
                    in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                    doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.text();
                    doc.text();
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString22092 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString22092_failAssert0null26749 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString22092_failAssert0null26749_failAssert0_literalMutationString32934 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString22118_failAssert0_literalMutationString24301_failAssert0_add36130_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
                    Document doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.text();
                    doc.outputSettings().charset().displayName();
                    in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.outputSettings().charset().displayName();
                    "?".equals(doc.text());
                    AmplParseTest.getFile("");
                    in = AmplParseTest.getFile("");
                    doc = Jsoup.parse(in, null, "http://example.co/");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.text();
                    doc.text();
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString22118 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString22118_failAssert0_literalMutationString24301 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString22118_failAssert0_literalMutationString24301_failAssert0_add36130 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString22092_failAssert0null26749_failAssert0_add37551_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.text();
                    doc.outputSettings().charset().displayName();
                    in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
                    doc = Jsoup.parse(in, null, null);
                    doc.outputSettings().charset().displayName();
                    "?".equals(doc.text());
                    in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                    doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.text();
                    doc.text();
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString22092 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString22092_failAssert0null26749 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString22092_failAssert0null26749_failAssert0_add37551 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString22103_failAssert0_add25791_failAssert0_add35774_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
                    Document doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.text();
                    doc.outputSettings().charset().displayName();
                    in = AmplParseTest.getFile("");
                    Jsoup.parse(in, null, "http://example.com");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.outputSettings().charset().displayName();
                    "?".equals(doc.text());
                    in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                    doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset();
                    doc.outputSettings().charset().displayName();
                    doc.text();
                    doc.text();
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString22103 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString22103_failAssert0_add25791 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString22103_failAssert0_add25791_failAssert0_add35774 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString22103_failAssert0_add25791_failAssert0_literalMutationString30331_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
                    Document doc = Jsoup.parse(in, null, ",XE^)c[ddi|}@RD9(#2");
                    doc.text();
                    doc.outputSettings().charset().displayName();
                    in = AmplParseTest.getFile("");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.outputSettings().charset().displayName();
                    "?".equals(doc.text());
                    in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                    doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset();
                    doc.outputSettings().charset().displayName();
                    doc.text();
                    doc.text();
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString22103 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString22103_failAssert0_add25791 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString22103_failAssert0_add25791_failAssert0_literalMutationString30331 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString22118_failAssert0_add25943_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                doc.text();
                doc.outputSettings().charset().displayName();
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
                org.junit.Assert.fail("testHtml5Charset_literalMutationString22118 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString22118_failAssert0_add25943 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_add22135_remove26256_literalMutationString29013_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
            Document doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_add22135__7 = doc.text();
            String o_testHtml5Charset_add22135__9 = doc.outputSettings().charset().displayName();
            in = AmplParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example.com");
            String o_testHtml5Charset_add22135__19 = doc.outputSettings().charset().displayName();
            boolean o_testHtml5Charset_add22135__22 = "?".equals(doc.text());
            in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
            doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_add22135__30 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_add22135__33 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_add22135__36 = doc.text();
            String o_testHtml5Charset_add22135__37 = doc.text();
            org.junit.Assert.fail("testHtml5Charset_add22135_remove26256_literalMutationString29013 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString22103_failAssert0null26651_failAssert0() throws IOException {
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
                in = AmplParseTest.getFile(null);
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString22103 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString22103_failAssert0null26651 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40223_literalMutationString40513_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "");
            Element headline = doc.select("nyt_headline[version=1.0]").first();
            String o_testNytArticle_literalMutationString40223__9 = headline.text();
            org.junit.Assert.fail("testNytArticle_literalMutationString40223_literalMutationString40513 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40220_failAssert0_literalMutationString40725_failAssert0_literalMutationString44367_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "");
                    Element headline = doc.select("nyt_headline[version=1.0]").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString40220 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString40220_failAssert0_literalMutationString40725 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString40220_failAssert0_literalMutationString40725_failAssert0_literalMutationString44367 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40218_failAssert0_add40899_failAssert0null46661_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    doc.select("nyt_headline[version=1.0]");
                    Element headline = doc.select(null).first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString40218 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_add40899 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_add40899_failAssert0null46661 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40218_failAssert0_add40896_failAssert0() throws IOException {
        try {
            {
                AmplParseTest.getFile("");
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString40218 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_add40896 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40218_failAssert0_literalMutationString40639_failAssert0_add45593_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.comZ2010/07/26/business/global/26bp.html?hp");
                    Element headline = doc.select("nyt_headline[version=1.0]").first();
                    headline.text();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString40218 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_literalMutationString40639 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_literalMutationString40639_failAssert0_add45593 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40218_failAssert0_add40899_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                doc.select("nyt_headline[version=1.0]");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString40218 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_add40899 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40218_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            Element headline = doc.select("nyt_headline[version=1.0]").first();
            headline.text();
            org.junit.Assert.fail("testNytArticle_literalMutationString40218 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40223_literalMutationString40513_failAssert0null46322_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "");
                Element headline = doc.select(null).first();
                String o_testNytArticle_literalMutationString40223__9 = headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString40223_literalMutationString40513 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString40223_literalMutationString40513_failAssert0null46322 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40218_failAssert0_add40897_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString40218 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_add40897 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40218_failAssert0_add40897_failAssert0_add45094_failAssert0() throws IOException {
        try {
            {
                {
                    AmplParseTest.getFile("");
                    File in = AmplParseTest.getFile("");
                    Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    Element headline = doc.select("nyt_headline[version=1.0]").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString40218 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_add40897 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_add40897_failAssert0_add45094 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40218_failAssert0_literalMutationString40639_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.comZ2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString40218 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_literalMutationString40639 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40218_failAssert0_add40896_failAssert0_add45476_failAssert0() throws IOException {
        try {
            {
                {
                    AmplParseTest.getFile("");
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    Element headline = doc.select("nyt_headline[version=1.0]").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString40218 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_add40896 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_add40896_failAssert0_add45476 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40224null40991_failAssert0_literalMutationString44094_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select(null).first();
                String o_testNytArticle_literalMutationString40224__9 = headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString40224null40991 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString40224null40991_failAssert0_literalMutationString44094 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40218_failAssert0_add40896_failAssert0_add45474_failAssert0() throws IOException {
        try {
            {
                {
                    AmplParseTest.getFile("");
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    doc.select("nyt_headline[version=1.0]");
                    Element headline = doc.select("nyt_headline[version=1.0]").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString40218 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_add40896 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_add40896_failAssert0_add45474 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40218_failAssert0_add40897_failAssert0_literalMutationString42512_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    Element headline = doc.select("Jay").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString40218 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_add40897 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_add40897_failAssert0_literalMutationString42512 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40218_failAssert0_add40899_failAssert0_literalMutationString44293_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    doc.select("nyt_headline[version=1.0]");
                    Element headline = doc.select("nyt_headline[version=1.0]").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString40218 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_add40899 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_add40899_failAssert0_literalMutationString44293 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_add40237_literalMutationString40332_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            Element o_testNytArticle_add40237__6 = doc.select("nyt_headline[version=1.0]").first();
            Element headline = doc.select("nyt_headline[version=1.0]").first();
            String o_testNytArticle_add40237__11 = headline.text();
            org.junit.Assert.fail("testNytArticle_add40237_literalMutationString40332 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40218_failAssert0_add40897_failAssert0null46229_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    Document doc = Jsoup.parse(in, null, null);
                    Element headline = doc.select("nyt_headline[version=1.0]").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString40218 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_add40897 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_add40897_failAssert0null46229 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_add40235_literalMutationString40314_failAssert0() throws IOException {
        try {
            AmplParseTest.getFile("/htmltests/nyt-article-1.html");
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            Element headline = doc.select("nyt_headline[version=1.0]").first();
            String o_testNytArticle_add40235__10 = headline.text();
            org.junit.Assert.fail("testNytArticle_add40235_literalMutationString40314 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40223_literalMutationString40513_failAssert0_add45244_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "");
                doc.select("nyt_headline[version=1.0]");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                String o_testNytArticle_literalMutationString40223__9 = headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString40223_literalMutationString40513 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString40223_literalMutationString40513_failAssert0_add45244 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40228_literalMutationString40460_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "[2?FM_3V0U8BZpDyTy`TNv?w >Z3[]L:c$0FDF;[9mH]OWwfiWTuNCERVsS?%;");
            Element headline = doc.select("nyt_headline[version=1.0]").first();
            String o_testNytArticle_literalMutationString40228__9 = headline.text();
            org.junit.Assert.fail("testNytArticle_literalMutationString40228_literalMutationString40460 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40232_failAssert0_literalMutationString40702_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("nyt_heGdline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString40232 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString40232_failAssert0_literalMutationString40702 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40223_literalMutationString40513_failAssert0_add45243_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "");
                doc.select("nyt_headline[version=1.0]").first();
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                String o_testNytArticle_literalMutationString40223__9 = headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString40223_literalMutationString40513 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString40223_literalMutationString40513_failAssert0_add45243 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40225_literalMutationString40440_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "Jay");
            Element headline = doc.select("nyt_headline[version=1.0]").first();
            String o_testNytArticle_literalMutationString40225__9 = headline.text();
            org.junit.Assert.fail("testNytArticle_literalMutationString40225_literalMutationString40440 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40218_failAssert0_literalMutationString40632_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString40218 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_literalMutationString40632 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40218_failAssert0_add40899_failAssert0_add45820_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    doc.select("nyt_headline[version=1.0]");
                    doc.select("nyt_headline[version=1.0]").first();
                    Element headline = doc.select("nyt_headline[version=1.0]").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString40218 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_add40899 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_add40899_failAssert0_add45820 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40226_literalMutationString40424_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/businCess/global/26bp.html?hp");
            Element headline = doc.select("nyt_headline[version=1.0]").first();
            String o_testNytArticle_literalMutationString40226__9 = headline.text();
            org.junit.Assert.fail("testNytArticle_literalMutationString40226_literalMutationString40424 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40218_failAssert0_add40896_failAssert0_literalMutationString43436_failAssert0() throws IOException {
        try {
            {
                {
                    AmplParseTest.getFile("");
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    Element headline = doc.select("Jay").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString40218 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_add40896 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_add40896_failAssert0_literalMutationString43436 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40218_failAssert0_literalMutationString40639_failAssert0_literalMutationString43760_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "w#f[&@};&sY_ijLrI*n?Hi.vd[U#RY|HYc0q{RoUBb7*a{}Wow%4qe,uJI?_:7");
                    Element headline = doc.select("nyt_headline[version=1.0]").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString40218 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_literalMutationString40639 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_literalMutationString40639_failAssert0_literalMutationString43760 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40218_failAssert0_add40896_failAssert0_literalMutationString43433_failAssert0() throws IOException {
        try {
            {
                {
                    AmplParseTest.getFile("");
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.htl?hp");
                    Element headline = doc.select("nyt_headline[version=1.0]").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString40218 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_add40896 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_add40896_failAssert0_literalMutationString43433 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40218_failAssert0_add40896_failAssert0null46455_failAssert0() throws IOException {
        try {
            {
                {
                    AmplParseTest.getFile("");
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    Element headline = doc.select(null).first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString40218 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_add40896 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_add40896_failAssert0null46455 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40218_failAssert0null41027_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select(null).first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString40218 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0null41027 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40223_literalMutationString40513_failAssert0_literalMutationString42873_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                String o_testNytArticle_literalMutationString40223__9 = headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString40223_literalMutationString40513 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString40223_literalMutationString40513_failAssert0_literalMutationString42873 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40218_failAssert0_literalMutationString40639_failAssert0null46531_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.comZ2010/07/26/business/global/26bp.html?hp");
                    Element headline = doc.select(null).first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString40218 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_literalMutationString40639 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_literalMutationString40639_failAssert0null46531 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString40218_failAssert0_literalMutationString40644_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("ny_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString40218 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString40218_failAssert0_literalMutationString40644 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString7456_failAssert0null8117_failAssert0_add12407_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select(null).first();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7456 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7456_failAssert0null8117 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7456_failAssert0null8117_failAssert0_add12407 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString7456_failAssert0_add8028_failAssert0_literalMutationString9257_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select("n2k+K").first();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7456 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7456_failAssert0_add8028 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7456_failAssert0_add8028_failAssert0_literalMutationString9257 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString7456_failAssert0_add8026_failAssert0_add11109_failAssert0() throws IOException {
        try {
            {
                {
                    AmplParseTest.getFile("");
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select("#form").first();
                    form.children().size();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7456 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7456_failAssert0_add8026 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7456_failAssert0_add8026_failAssert0_add11109 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_add7476null8066_failAssert0_literalMutationString10394_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select(null).first();
                int o_testLowercaseUtf8Charset_add7476__8 = form.children().size();
                doc.outputSettings();
                String o_testLowercaseUtf8Charset_add7476__11 = doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_add7476null8066 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_add7476null8066_failAssert0_literalMutationString10394 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString7456_failAssert0_add8028_failAssert0_add11108_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select("#form").first();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7456 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7456_failAssert0_add8028 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7456_failAssert0_add8028_failAssert0_add11108 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString7456_failAssert0_add8028_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                form.children().size();
                doc.outputSettings().charset().name();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7456 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7456_failAssert0_add8028 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString7456_failAssert0null8117_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select(null).first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7456 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7456_failAssert0null8117 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString7464_failAssert0_literalMutationString7720_failAssert0_literalMutationString10155_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select("Jy").first();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7464 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7464_failAssert0_literalMutationString7720 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7464_failAssert0_literalMutationString7720_failAssert0_literalMutationString10155 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString7456_failAssert0_add8026_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                form.children().size();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7456 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7456_failAssert0_add8026 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString7464_failAssert0_literalMutationString7720_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("Jay").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7464 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7464_failAssert0_literalMutationString7720 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString7456_failAssert0_literalMutationString7782_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#fom").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7456 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7456_failAssert0_literalMutationString7782 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString7456_failAssert0_literalMutationString7783_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7456 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7456_failAssert0_literalMutationString7783 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString7456_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null);
            Element form = doc.select("#form").first();
            form.children().size();
            doc.outputSettings().charset().name();
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7456 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString7460_failAssert0_literalMutationString7653_failAssert0_literalMutationString9496_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select("#form").first();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7460 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7460_failAssert0_literalMutationString7653 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7460_failAssert0_literalMutationString7653_failAssert0_literalMutationString9496 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString7463_failAssert0_literalMutationString7732_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("o<g]N").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7463 should have thrown Selector$SelectorParseException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7463_failAssert0_literalMutationString7732 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_add7476_literalMutationString7578_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null);
            Element form = doc.select("#form").first();
            int o_testLowercaseUtf8Charset_add7476__8 = form.children().size();
            doc.outputSettings();
            String o_testLowercaseUtf8Charset_add7476__11 = doc.outputSettings().charset().name();
            org.junit.Assert.fail("testLowercaseUtf8Charset_add7476_literalMutationString7578 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString7458_failAssert0_literalMutationString7743_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7458 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7458_failAssert0_literalMutationString7743 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString7456_failAssert0_add8023_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Jsoup.parse(in, null);
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7456 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7456_failAssert0_add8023 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString7464_failAssert0_literalMutationString7720_failAssert0_add12083_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    doc.select("Jay");
                    Element form = doc.select("Jay").first();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7464 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7464_failAssert0_literalMutationString7720 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7464_failAssert0_literalMutationString7720_failAssert0_add12083 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_add7468_literalMutationString7608_failAssert0() throws IOException {
        try {
            AmplParseTest.getFile("/htmltests/lowercase-charset-test.html");
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null);
            Element form = doc.select("#form").first();
            int o_testLowercaseUtf8Charset_add7468__9 = form.children().size();
            String o_testLowercaseUtf8Charset_add7468__11 = doc.outputSettings().charset().name();
            org.junit.Assert.fail("testLowercaseUtf8Charset_add7468_literalMutationString7608 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString7462_failAssert0_literalMutationString7665_failAssert0_literalMutationString10162_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select("").first();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7462 should have thrown IllegalArgumentException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7462_failAssert0_literalMutationString7665 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString7462_failAssert0_literalMutationString7665_failAssert0_literalMutationString10162 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_add7469_literalMutationString7620_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document o_testLowercaseUtf8Charset_add7469__3 = Jsoup.parse(in, null);
            Document doc = Jsoup.parse(in, null);
            Element form = doc.select("#form").first();
            int o_testLowercaseUtf8Charset_add7469__9 = form.children().size();
            String o_testLowercaseUtf8Charset_add7469__11 = doc.outputSettings().charset().name();
            org.junit.Assert.fail("testLowercaseUtf8Charset_add7469_literalMutationString7620 should have thrown FileNotFoundException");
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

