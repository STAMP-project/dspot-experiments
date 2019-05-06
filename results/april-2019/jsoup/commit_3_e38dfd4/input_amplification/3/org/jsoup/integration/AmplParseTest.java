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
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;


public class AmplParseTest {
    @Test(timeout = 10000)
    public void testBaidu_literalMutationString27493_failAssert0_add34643_failAssert0_literalMutationString39016_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    Element submit = doc.select("#su").first();
                    submit.attr("value");
                    submit = doc.select("input[value=百度一下]").first();
                    submit.id();
                    Element newsLink = doc.select("a:contains(新)").first();
                    newsLink.absUrl("href");
                    doc.outputSettings();
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    doc.outputSettings().charset("asci");
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaidu_literalMutationString27493 should have thrown UnsupportedCharsetException");
                }
                org.junit.Assert.fail("testBaidu_literalMutationString27493_failAssert0_add34643 should have thrown UnsupportedCharsetException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString27493_failAssert0_add34643_failAssert0_literalMutationString39016 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString27440_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testBaidu_literalMutationString27440 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_add20_literalMutationString128_failAssert0_add3619_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document o_testBaiduVariant_add20__4 = Jsoup.parse(in, null, "http://www.baidu.com/");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                String o_testBaiduVariant_add20__8 = doc.outputSettings().charset().displayName();
                String o_testBaiduVariant_add20__11 = doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_add20_literalMutationString128 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_add20_literalMutationString128_failAssert0_add3619 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_add20_literalMutationString128_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document o_testBaiduVariant_add20__4 = Jsoup.parse(in, null, "http://www.baidu.com/");
            Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
            String o_testBaiduVariant_add20__8 = doc.outputSettings().charset().displayName();
            String o_testBaiduVariant_add20__11 = doc.select("title").outerHtml();
            org.junit.Assert.fail("testBaiduVariant_add20_literalMutationString128 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_add20_literalMutationString128_failAssert0_literalMutationString2745_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document o_testBaiduVariant_add20__4 = Jsoup.parse(in, null, "httpn//www.baidu.com/");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                String o_testBaiduVariant_add20__8 = doc.outputSettings().charset().displayName();
                String o_testBaiduVariant_add20__11 = doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_add20_literalMutationString128 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_add20_literalMutationString128_failAssert0_literalMutationString2745 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString2_failAssert0_add827_failAssert0() throws IOException {
        try {
            {
                AmplParseTest.getFile("");
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString2 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString2_failAssert0_add827 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString2_failAssert0_add828_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Jsoup.parse(in, null, "http://www.baidu.com/");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString2 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString2_failAssert0_add828 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString8_literalMutationString290_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "");
            String o_testBaiduVariant_literalMutationString8__7 = doc.outputSettings().charset().displayName();
            String o_testBaiduVariant_literalMutationString8__10 = doc.select("title").outerHtml();
            org.junit.Assert.fail("testBaiduVariant_literalMutationString8_literalMutationString290 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString2_failAssert0_literalMutationString584_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("/ZAJ8").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString2 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString2_failAssert0_literalMutationString584 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString1_failAssert0_literalMutationString554_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString1 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString1_failAssert0_literalMutationString554 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString2_failAssert0_literalMutationString581_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString2 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString2_failAssert0_literalMutationString581 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString2_failAssert0_add827_failAssert0_add3566_failAssert0() throws IOException {
        try {
            {
                {
                    AmplParseTest.getFile("");
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString2 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString2_failAssert0_add827 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString2_failAssert0_add827_failAssert0_add3566 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString2_failAssert0_literalMutationString582_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("ti}tle").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString2 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString2_failAssert0_literalMutationString582 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_add20_literalMutationString147_failAssert0_literalMutationString2277_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document o_testBaiduVariant_add20__4 = Jsoup.parse(in, null, "http://www.baidu.com/");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                String o_testBaiduVariant_add20__8 = doc.outputSettings().charset().displayName();
                String o_testBaiduVariant_add20__11 = doc.select("").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_add20_literalMutationString147 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testBaiduVariant_add20_literalMutationString147_failAssert0_literalMutationString2277 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_add20_literalMutationString128_failAssert0null4019_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document o_testBaiduVariant_add20__4 = Jsoup.parse(in, null, "http://www.baidu.com/");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                String o_testBaiduVariant_add20__8 = doc.outputSettings().charset().displayName();
                String o_testBaiduVariant_add20__11 = doc.select(null).outerHtml();
                org.junit.Assert.fail("testBaiduVariant_add20_literalMutationString128 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_add20_literalMutationString128_failAssert0null4019 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString2_failAssert0_add828_failAssert0_add3249_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Jsoup.parse(in, null, "http://www.baidu.com/");
                    Jsoup.parse(in, null, "http://www.baidu.com/");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString2 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString2_failAssert0_add828 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString2_failAssert0_add828_failAssert0_add3249 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_add20_literalMutationString128_failAssert0null4018_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document o_testBaiduVariant_add20__4 = Jsoup.parse(in, null, "http://www.baidu.com/");
                Document doc = Jsoup.parse(in, null, null);
                String o_testBaiduVariant_add20__8 = doc.outputSettings().charset().displayName();
                String o_testBaiduVariant_add20__11 = doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_add20_literalMutationString128 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_add20_literalMutationString128_failAssert0null4018 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_add25null848_failAssert0_literalMutationString1926_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                String o_testBaiduVariant_add25__7 = doc.outputSettings().charset().displayName();
                Elements o_testBaiduVariant_add25__10 = doc.select(null);
                String o_testBaiduVariant_add25__11 = doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_add25null848 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testBaiduVariant_add25null848_failAssert0_literalMutationString1926 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_add20_literalMutationString128_failAssert0_literalMutationString2759_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document o_testBaiduVariant_add20__4 = Jsoup.parse(in, null, "http://www.baidu.com/");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                String o_testBaiduVariant_add20__8 = doc.outputSettings().charset().displayName();
                String o_testBaiduVariant_add20__11 = doc.select("Jay").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_add20_literalMutationString128 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_add20_literalMutationString128_failAssert0_literalMutationString2759 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString2_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
            doc.outputSettings().charset().displayName();
            doc.select("title").outerHtml();
            org.junit.Assert.fail("testBaiduVariant_literalMutationString2 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString2_failAssert0null953_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, null);
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString2 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString2_failAssert0null953 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString2_failAssert0_add828_failAssert0_literalMutationString2048_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Jsoup.parse(in, null, "http://www.baidu.com/");
                    Document doc = Jsoup.parse(in, null, "http://ww.baidu.com/");
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString2 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString2_failAssert0_add828 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString2_failAssert0_add828_failAssert0_literalMutationString2048 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString2_failAssert0null954_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select(null).outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString2 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString2_failAssert0null954 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString2_failAssert0_add828_failAssert0null3843_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Jsoup.parse(in, null, "http://www.baidu.com/");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    doc.outputSettings().charset().displayName();
                    doc.select(null).outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString2 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString2_failAssert0_add828 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString2_failAssert0_add828_failAssert0null3843 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString2_failAssert0_add827_failAssert0_literalMutationString2655_failAssert0() throws IOException {
        try {
            {
                {
                    AmplParseTest.getFile("");
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.co/");
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString2 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString2_failAssert0_add827 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString2_failAssert0_add827_failAssert0_literalMutationString2655 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString8_literalMutationString290_failAssert0_add3363_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "");
                String o_testBaiduVariant_literalMutationString8__7 = doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                String o_testBaiduVariant_literalMutationString8__10 = doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString8_literalMutationString290 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString8_literalMutationString290_failAssert0_add3363 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_add20_literalMutationString128_failAssert0_add3612_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Jsoup.parse(in, null, "http://www.baidu.com/");
                Document o_testBaiduVariant_add20__4 = Jsoup.parse(in, null, "http://www.baidu.com/");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                String o_testBaiduVariant_add20__8 = doc.outputSettings().charset().displayName();
                String o_testBaiduVariant_add20__11 = doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_add20_literalMutationString128 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_add20_literalMutationString128_failAssert0_add3612 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString12406_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testHtml5Charset_literalMutationString12406 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString12406_failAssert0_literalMutationString14921_failAssert0() throws IOException {
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
                in = AmplParseTest.getFile("");
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString12406 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString12406_failAssert0_literalMutationString14921 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString12424_add15613_literalMutationString18809_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
            Document doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_literalMutationString12424__7 = doc.text();
            String o_testHtml5Charset_literalMutationString12424__8 = doc.outputSettings().charset().displayName();
            in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
            doc = Jsoup.parse(in, null, "");
            String o_testHtml5Charset_literalMutationString12424__18 = doc.outputSettings().charset().displayName();
            boolean o_testHtml5Charset_literalMutationString12424__21 = "新".equals(doc.text());
            AmplParseTest.getFile("/htmltests/meta-charset-3.html");
            in = AmplParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_literalMutationString12424__29 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_literalMutationString12424__32 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_literalMutationString12424__35 = doc.text();
            String o_testHtml5Charset_literalMutationString12424__36 = doc.text();
            org.junit.Assert.fail("testHtml5Charset_literalMutationString12424_add15613_literalMutationString18809 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString12406_failAssert0_add16472_failAssert0() throws IOException {
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
                org.junit.Assert.fail("testHtml5Charset_literalMutationString12406 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString12406_failAssert0_add16472 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString12418_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testHtml5Charset_literalMutationString12418 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString12433_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testHtml5Charset_literalMutationString12433 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString12406_failAssert0null17104_failAssert0() throws IOException {
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
                org.junit.Assert.fail("testHtml5Charset_literalMutationString12406 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString12406_failAssert0null17104 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_add12453_literalMutationString12962_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
            Document doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_add12453__7 = doc.text();
            String o_testHtml5Charset_add12453__8 = doc.outputSettings().charset().displayName();
            in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            String o_testHtml5Charset_add12453__18 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_add12453__21 = doc.outputSettings().charset().displayName();
            boolean o_testHtml5Charset_add12453__24 = "新".equals(doc.text());
            in = AmplParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_add12453__32 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_add12453__35 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_add12453__38 = doc.text();
            String o_testHtml5Charset_add12453__39 = doc.text();
            org.junit.Assert.fail("testHtml5Charset_add12453_literalMutationString12962 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString23612_failAssert0_add24295_failAssert0_literalMutationString25985_failAssert0() throws IOException {
        try {
            {
                {
                    AmplParseTest.getFile("/htmltests/nyt-article-1.html");
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    Element headline = doc.select("Jay").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString23612 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString23612_failAssert0_add24295 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString23612_failAssert0_add24295_failAssert0_literalMutationString25985 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString23599_failAssert0_literalMutationString23985_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, " U_gMjG:!n#`q-C,_-zK&Pj:U2S$bx{RQ:*#*iL+&h|eP&,`CZk6s:|qicp_XV");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString23599 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString23599_failAssert0_literalMutationString23985 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString23602_failAssert0_add24269_failAssert0_literalMutationString26003_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    Element headline = doc.select("nyt_headline[version=1.0]").first();
                    headline.text();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString23602 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString23602_failAssert0_add24269 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString23602_failAssert0_add24269_failAssert0_literalMutationString26003 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString23599_failAssert0_add24276_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString23599 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString23599_failAssert0_add24276 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString23599_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            Element headline = doc.select("nyt_headline[version=1.0]").first();
            headline.text();
            org.junit.Assert.fail("testNytArticle_literalMutationString23599 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString23599_failAssert0_add24276_failAssert0_add26563_failAssert0() throws IOException {
        try {
            {
                {
                    AmplParseTest.getFile("");
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    Element headline = doc.select("nyt_headline[version=1.0]").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString23599 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString23599_failAssert0_add24276 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString23599_failAssert0_add24276_failAssert0_add26563 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString23614_failAssert0_literalMutationString24028_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("nyt_headline[version1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString23614 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString23614_failAssert0_literalMutationString24028 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charsetnull4263_failAssert0_add4708_failAssert0_literalMutationString5392_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    doc.select(null).first();
                    Element form = doc.select(null).first();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charsetnull4263 should have thrown IllegalArgumentException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charsetnull4263_failAssert0_add4708 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charsetnull4263_failAssert0_add4708_failAssert0_literalMutationString5392 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString4240_failAssert0_literalMutationString4497_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("Jay").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4240 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4240_failAssert0_literalMutationString4497 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString4244_failAssert0_literalMutationString4449_failAssert0_literalMutationString5605_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select("#form").first();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4244 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4244_failAssert0_literalMutationString4449 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4244_failAssert0_literalMutationString4449_failAssert0_literalMutationString5605 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString4246_failAssert0_literalMutationString4470_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4246 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4246_failAssert0_literalMutationString4470 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString4241_failAssert0_literalMutationString4559_failAssert0_literalMutationString5746_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select("#f?orm").first();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4241 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4241_failAssert0_literalMutationString4559 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4241_failAssert0_literalMutationString4559_failAssert0_literalMutationString5746 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString4241_failAssert0_add4810_failAssert0_literalMutationString5500_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select("#form").first();
                    form.children().size();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4241 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4241_failAssert0_add4810 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4241_failAssert0_add4810_failAssert0_literalMutationString5500 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString4240_failAssert0_literalMutationString4497_failAssert0null7162_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select(null).first();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4240 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4240_failAssert0_literalMutationString4497 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4240_failAssert0_literalMutationString4497_failAssert0null7162 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString4243_failAssert0_literalMutationString4524_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4243 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4243_failAssert0_literalMutationString4524 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString4240_failAssert0_add4758_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                doc.select("#form").first();
                Element form = doc.select("#form").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4240 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4240_failAssert0_add4758 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString4251_failAssert0_literalMutationString4500_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#frm").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4251 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4251_failAssert0_literalMutationString4500 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charsetnull4263_failAssert0_literalMutationString4440_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select(null).first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charsetnull4263 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charsetnull4263_failAssert0_literalMutationString4440 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString4240_failAssert0_literalMutationString4497_failAssert0_literalMutationString5794_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select("").first();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4240 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4240_failAssert0_literalMutationString4497 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4240_failAssert0_literalMutationString4497_failAssert0_literalMutationString5794 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString4240_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null);
            Element form = doc.select("#form").first();
            form.children().size();
            doc.outputSettings().charset().name();
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4240 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString4240_failAssert0_literalMutationString4497_failAssert0_add6686_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Jsoup.parse(in, null);
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select("Jay").first();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4240 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4240_failAssert0_literalMutationString4497 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString4240_failAssert0_literalMutationString4497_failAssert0_add6686 should have thrown FileNotFoundException");
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

