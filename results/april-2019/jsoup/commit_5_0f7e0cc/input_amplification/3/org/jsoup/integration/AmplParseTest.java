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
    public void testSmhBizArticle_literalMutationString36904_failAssert0_add39192_failAssert0_add47516_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
                    doc.title();
                    doc.select("html").attr("xml:lang");
                    doc.select("html").attr("xml:lang");
                    Elements articleBody = doc.select(".articleBody > *");
                    articleBody.size();
                    articleBody.size();

                    org.junit.Assert.fail("testSmhBizArticle_literalMutationString36904 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testSmhBizArticle_literalMutationString36904_failAssert0_add39192 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSmhBizArticle_literalMutationString36904_failAssert0_add39192_failAssert0_add47516 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSmhBizArticle_literalMutationString36924_literalMutationString37526_literalMutationString42678_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "htt://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
            String o_testSmhBizArticle_literalMutationString36924__5 = doc.title();
            String o_testSmhBizArticle_literalMutationString36924__6 = doc.select("Jay").attr("xml:lang");
            Elements articleBody = doc.select(".articleBody > *");
            int o_testSmhBizArticle_literalMutationString36924__10 = articleBody.size();

            org.junit.Assert.fail("testSmhBizArticle_literalMutationString36924_literalMutationString37526_literalMutationString42678 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSmhBizArticle_literalMutationString36904_failAssert0_add39192_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
                doc.title();
                doc.select("html").attr("xml:lang");
                doc.select("html").attr("xml:lang");
                Elements articleBody = doc.select(".articleBody > *");
                articleBody.size();

                org.junit.Assert.fail("testSmhBizArticle_literalMutationString36904 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSmhBizArticle_literalMutationString36904_failAssert0_add39192 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSmhBizArticle_literalMutationString36904_failAssert0_literalMutationString38709_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
                doc.title();
                doc.select("").attr("xml:lang");
                Elements articleBody = doc.select(".articleBody > *");
                articleBody.size();

                org.junit.Assert.fail("testSmhBizArticle_literalMutationString36904 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSmhBizArticle_literalMutationString36904_failAssert0_literalMutationString38709 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSmhBizArticle_literalMutationString36904_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
            doc.title();
            doc.select("html").attr("xml:lang");
            Elements articleBody = doc.select(".articleBody > *");
            articleBody.size();

            org.junit.Assert.fail("testSmhBizArticle_literalMutationString36904 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSmhBizArticle_literalMutationString36904_failAssert0_add39192_failAssert0_literalMutationString43219_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
                    doc.title();
                    doc.select("html").attr("xml:lang");
                    doc.select("htl").attr("xml:lang");
                    Elements articleBody = doc.select(".articleBody > *");
                    articleBody.size();

                    org.junit.Assert.fail("testSmhBizArticle_literalMutationString36904 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testSmhBizArticle_literalMutationString36904_failAssert0_add39192 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSmhBizArticle_literalMutationString36904_failAssert0_add39192_failAssert0_literalMutationString43219 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNewsHomepage_literalMutationString1_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.news.com.au/");
            doc.title();
            doc.select(".id1225817868581 h4").text().trim();
            Element a = doc.select("a[href=/entertainment/horoscopes]").first();
            a.attr("href");
            a.attr("abs:href");
            Element hs = doc.select("a[href*=naughty-corners-are-a-bad-idea]").first();
            hs.attr("href");
            hs.attr("href");
            hs.attr("abs:href");
            org.junit.Assert.fail("testNewsHomepage_literalMutationString1 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNewsHomepage_literalMutationString52null8251_failAssert0_literalMutationString17292_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.news.com.au/");
                String o_testNewsHomepage_literalMutationString52__5 = doc.title();
                String o_testNewsHomepage_literalMutationString52__6 = doc.select(".id1225817868581 h4").text().trim();
                Element a = doc.select("a[href=/entertainment/horoscopes]").first();
                a.attr("href");
                String o_testNewsHomepage_literalMutationString52__13 = a.attr("abs:href");
                Element hs = doc.select("a[href*=naughty-corners-are-a-bad-idea]").first();
                String o_testNewsHomepage_literalMutationString52__17 = hs.attr(null);
                String o_testNewsHomepage_literalMutationString52__18 = hs.attr("href");
                String o_testNewsHomepage_literalMutationString52__19 = hs.attr("abs:href");
                org.junit.Assert.fail("testNewsHomepage_literalMutationString52null8251 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testNewsHomepage_literalMutationString52null8251_failAssert0_literalMutationString17292 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_add57688null61352_literalMutationString64144_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document o_testGoogleSearchIpod_add57688__3 = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
            Document doc = Jsoup.parse(in, null, "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
            String o_testGoogleSearchIpod_add57688__6 = doc.title();
            Elements results = doc.select("h3.r > a");
            int o_testGoogleSearchIpod_add57688__9 = results.size();
            String o_testGoogleSearchIpod_add57688__10 = results.get(0).attr("href");
            String o_testGoogleSearchIpod_add57688__12 = results.get(1).attr("href");
            org.junit.Assert.fail("testGoogleSearchIpod_add57688null61352_literalMutationString64144 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationString57685_literalMutationString58666_failAssert0_literalMutationString68549_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
                String o_testGoogleSearchIpod_literalMutationString57685__5 = doc.title();
                Elements results = doc.select("Jay");
                int o_testGoogleSearchIpod_literalMutationString57685__8 = results.size();
                String o_testGoogleSearchIpod_literalMutationString57685__9 = results.get(0).attr("href");
                String o_testGoogleSearchIpod_literalMutationString57685__11 = results.get(1).attr("hef");
                org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57685_literalMutationString58666 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57685_literalMutationString58666_failAssert0_literalMutationString68549 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationString57684_literalMutationNumber59174_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("/htmltests/google-ipod.html");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
            String o_testGoogleSearchIpod_literalMutationString57684__5 = doc.title();
            Elements results = doc.select("h3.r > a");
            int o_testGoogleSearchIpod_literalMutationString57684__8 = results.size();
            String o_testGoogleSearchIpod_literalMutationString57684__9 = results.get(-1).attr("href");
            String o_testGoogleSearchIpod_literalMutationString57684__11 = results.get(1).attr("hrsef");
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57684_literalMutationNumber59174 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpodnull57702_failAssert0_literalMutationString59606_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
                doc.title();
                Elements results = doc.select("h3.r > a");
                results.size();
                results.get(0).attr("href");
                results.get(1).attr(null);
                org.junit.Assert.fail("testGoogleSearchIpodnull57702 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testGoogleSearchIpodnull57702_failAssert0_literalMutationString59606 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationString57660_failAssert0_add61187_failAssert0_literalMutationString66496_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
                    doc.title();
                    Elements results = doc.select("h3.rL> a");
                    results.size();
                    results.size();
                    results.get(0).attr("href");
                    results.get(1).attr("href");
                    org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57660 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57660_failAssert0_add61187 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57660_failAssert0_add61187_failAssert0_literalMutationString66496 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationString57651_failAssert0_literalMutationString59944_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
                doc.title();
                Elements results = doc.select("h3.r > a");
                results.size();
                results.get(0).attr("href");
                results.get(1).attr("href");
                org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57651 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57651_failAssert0_literalMutationString59944 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationString57642_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
            doc.title();
            Elements results = doc.select("h3.r > a");
            results.size();
            results.get(0).attr("href");
            results.get(1).attr("href");
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57642 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString151608_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8");
            doc.text().contains("gd-jpeg");
            org.junit.Assert.fail("testBinary_literalMutationString151608 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString151608_failAssert0_add152216_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.text();
                doc.text().contains("gd-jpeg");
                org.junit.Assert.fail("testBinary_literalMutationString151608 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString151608_failAssert0_add152216 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString151608_failAssert0_add152217_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.text().contains("gd-jpeg");
                org.junit.Assert.fail("testBinary_literalMutationString151608 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString151608_failAssert0_add152217 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString151608_failAssert0_add152215_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.text().contains("gd-jpeg");
                doc.text().contains("gd-jpeg");
                org.junit.Assert.fail("testBinary_literalMutationString151608 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString151608_failAssert0_add152215 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString151623_add152181_literalMutationString152907_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8");
            String o_testBinary_literalMutationString151623_add152181__5 = doc.text();
            boolean o_testBinary_literalMutationString151623__5 = doc.text().contains("Jay");
            org.junit.Assert.fail("testBinary_literalMutationString151623_add152181_literalMutationString152907 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString151608_failAssert0_add152215_failAssert0_literalMutationString154852_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8");
                    doc.text().contains("F=0#4%?");
                    doc.text().contains("gd-jpeg");
                    org.junit.Assert.fail("testBinary_literalMutationString151608 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBinary_literalMutationString151608_failAssert0_add152215 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString151608_failAssert0_add152215_failAssert0_literalMutationString154852 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString151608_failAssert0_literalMutationString151958_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.text().contains("GrJX@3%");
                org.junit.Assert.fail("testBinary_literalMutationString151608 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString151608_failAssert0_literalMutationString151958 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString151608_failAssert0_add152216_failAssert0_add155548_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8");
                    doc.text();
                    doc.text().contains("gd-jpeg");
                    doc.text().contains("gd-jpeg");
                    org.junit.Assert.fail("testBinary_literalMutationString151608 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBinary_literalMutationString151608_failAssert0_add152216 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString151608_failAssert0_add152216_failAssert0_add155548 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString151617_failAssert0_literalMutationString152047_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UF-8");
                doc.text().contains("gd-jpeg");
                org.junit.Assert.fail("testBinary_literalMutationString151617 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString151617_failAssert0_literalMutationString152047 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString151608_failAssert0_literalMutationString151950_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UKTF-8");
                doc.text().contains("gd-jpeg");
                org.junit.Assert.fail("testBinary_literalMutationString151608 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString151608_failAssert0_literalMutationString151950 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString151608_failAssert0_add152217_failAssert0_add155554_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8");
                    doc.text();
                    doc.text().contains("gd-jpeg");
                    org.junit.Assert.fail("testBinary_literalMutationString151608 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBinary_literalMutationString151608_failAssert0_add152217 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString151608_failAssert0_add152217_failAssert0_add155554 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinarynull151632_literalMutationString151787_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null);
            boolean o_testBinarynull151632__5 = doc.text().contains("gd-jpeg");
            org.junit.Assert.fail("testBinarynull151632_literalMutationString151787 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString151608_failAssert0null152328_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                doc.text().contains("gd-jpeg");
                org.junit.Assert.fail("testBinary_literalMutationString151608 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString151608_failAssert0null152328 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_add151629_literalMutationString151724_failAssert0_literalMutationString155334_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                String o_testBinary_add151629__5 = doc.text();
                boolean o_testBinary_add151629__6 = doc.text().contains("gd-jpeg");
                org.junit.Assert.fail("testBinary_add151629_literalMutationString151724 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testBinary_add151629_literalMutationString151724_failAssert0_literalMutationString155334 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString151608_failAssert0_add152215_failAssert0_add156083_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8");
                    doc.text();
                    doc.text().contains("gd-jpeg");
                    doc.text().contains("gd-jpeg");
                    org.junit.Assert.fail("testBinary_literalMutationString151608 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBinary_literalMutationString151608_failAssert0_add152215 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString151608_failAssert0_add152215_failAssert0_add156083 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString151608_failAssert0_add152217_failAssert0_literalMutationString153271_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "=TF-8");
                    doc.text().contains("gd-jpeg");
                    org.junit.Assert.fail("testBinary_literalMutationString151608 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBinary_literalMutationString151608_failAssert0_add152217 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString151608_failAssert0_add152217_failAssert0_literalMutationString153271 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString151608_failAssert0_add152217_failAssert0null156365_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8");
                    doc.text().contains(null);
                    org.junit.Assert.fail("testBinary_literalMutationString151608 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBinary_literalMutationString151608_failAssert0_add152217 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString151608_failAssert0_add152217_failAssert0null156365 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString151614_failAssert0_literalMutationString152014_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "");
                doc.text().contains("gd-jpeg");
                org.junit.Assert.fail("testBinary_literalMutationString151614 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString151614_failAssert0_literalMutationString152014 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString151619_failAssert0null152333_failAssert0_literalMutationString153804_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTL-8");
                    doc.text().contains(null);
                    org.junit.Assert.fail("testBinary_literalMutationString151619 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("testBinary_literalMutationString151619_failAssert0null152333 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString151619_failAssert0null152333_failAssert0_literalMutationString153804 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString151608_failAssert0_add152216_failAssert0_literalMutationString153250_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8");
                    doc.text();
                    doc.text().contains("gd-jpeg");
                    org.junit.Assert.fail("testBinary_literalMutationString151608 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBinary_literalMutationString151608_failAssert0_add152216 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString151608_failAssert0_add152216_failAssert0_literalMutationString153250 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString80698_failAssert0_add82304_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                doc.title();
                Element a = doc.select("a[href=t/2322m2]").first();
                a.attr("abs:href");
                a.text();
                org.junit.Assert.fail("testYahooJp_literalMutationString80698 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooJp_literalMutationString80698_failAssert0_add82304 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString80716_failAssert0_literalMutationString81863_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                doc.title();
                Element a = doc.select("").first();
                a.attr("abs:href");
                a.text();
                org.junit.Assert.fail("testYahooJp_literalMutationString80716 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testYahooJp_literalMutationString80716_failAssert0_literalMutationString81863 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString80698_failAssert0_add82304_failAssert0null90763_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                    Document doc = Jsoup.parse(in, "UTF-8", null);
                    doc.title();
                    Element a = doc.select("a[href=t/2322m2]").first();
                    a.attr("abs:href");
                    a.text();
                    org.junit.Assert.fail("testYahooJp_literalMutationString80698 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testYahooJp_literalMutationString80698_failAssert0_add82304 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooJp_literalMutationString80698_failAssert0_add82304_failAssert0null90763 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString80716_failAssert0_literalMutationString81863_failAssert0_add89639_failAssert0() throws IOException {
        try {
            {
                {
                    AmplParseTest.getFile("");
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                    doc.title();
                    Element a = doc.select("").first();
                    a.attr("abs:href");
                    a.text();
                    org.junit.Assert.fail("testYahooJp_literalMutationString80716 should have thrown IllegalArgumentException");
                }
                org.junit.Assert.fail("testYahooJp_literalMutationString80716_failAssert0_literalMutationString81863 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooJp_literalMutationString80716_failAssert0_literalMutationString81863_failAssert0_add89639 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString80698_failAssert0_literalMutationString81566_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                doc.title();
                Element a = doc.select("a[href=t/2322m2]").first();
                a.attr("abs:href");
                a.text();
                org.junit.Assert.fail("testYahooJp_literalMutationString80698 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooJp_literalMutationString80698_failAssert0_literalMutationString81566 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString80698_failAssert0_add82304_failAssert0_add89340_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                    Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                    doc.title();
                    Element a = doc.select("a[href=t/2322m2]").first();
                    a.attr("abs:href");
                    a.text();
                    org.junit.Assert.fail("testYahooJp_literalMutationString80698 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testYahooJp_literalMutationString80698_failAssert0_add82304 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooJp_literalMutationString80698_failAssert0_add82304_failAssert0_add89340 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString80716_failAssert0_literalMutationString81863_failAssert0_literalMutationString86322_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.ahoo.co.jp/index.html");
                    doc.title();
                    Element a = doc.select("").first();
                    a.attr("abs:href");
                    a.text();
                    org.junit.Assert.fail("testYahooJp_literalMutationString80716 should have thrown IllegalArgumentException");
                }
                org.junit.Assert.fail("testYahooJp_literalMutationString80716_failAssert0_literalMutationString81863 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooJp_literalMutationString80716_failAssert0_literalMutationString81863_failAssert0_literalMutationString86322 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString80698_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
            doc.title();
            Element a = doc.select("a[href=t/2322m2]").first();
            a.attr("abs:href");
            a.text();
            org.junit.Assert.fail("testYahooJp_literalMutationString80698 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString80727null82534_literalMutationString84632_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.yahoo.co.jp/index.html");
            String o_testYahooJp_literalMutationString80727__6 = doc.title();
            Element a = doc.select("a[href=t/2322m2]").first();
            String o_testYahooJp_literalMutationString80727__10 = a.attr("ab:href");
            String o_testYahooJp_literalMutationString80727__11 = a.text();
            org.junit.Assert.fail("testYahooJp_literalMutationString80727null82534_literalMutationString84632 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString80716_failAssert0_literalMutationString81863_failAssert0_add89642_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                    doc.title();
                    doc.select("").first();
                    Element a = doc.select("").first();
                    a.attr("abs:href");
                    a.text();
                    org.junit.Assert.fail("testYahooJp_literalMutationString80716 should have thrown IllegalArgumentException");
                }
                org.junit.Assert.fail("testYahooJp_literalMutationString80716_failAssert0_literalMutationString81863 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooJp_literalMutationString80716_failAssert0_literalMutationString81863_failAssert0_add89642 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString80698_failAssert0null82596_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.yahoo.co.jp/index.html");
                doc.title();
                Element a = doc.select("a[href=t/2322m2]").first();
                a.attr("abs:href");
                a.text();
                org.junit.Assert.fail("testYahooJp_literalMutationString80698 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooJp_literalMutationString80698_failAssert0null82596 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString80698_failAssert0_add82304_failAssert0_literalMutationString85389_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                    Document doc = Jsoup.parse(in, "UTF-8", "Jay");
                    doc.title();
                    Element a = doc.select("a[href=t/2322m2]").first();
                    a.attr("abs:href");
                    a.text();
                    org.junit.Assert.fail("testYahooJp_literalMutationString80698 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testYahooJp_literalMutationString80698_failAssert0_add82304 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooJp_literalMutationString80698_failAssert0_add82304_failAssert0_literalMutationString85389 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString118242_failAssert0null126488_failAssert0_literalMutationString132208_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    Element submit = doc.select(null).first();
                    submit.attr("value");
                    submit = doc.select("input[value=????]").first();
                    submit.id();
                    Element newsLink = doc.select("a:contains(?)").first();
                    newsLink.absUrl("href");
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    doc.outputSettings().charset("ascii");
                    doc.select("tit0le").outerHtml();
                    org.junit.Assert.fail("testBaidu_literalMutationString118242 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaidu_literalMutationString118242_failAssert0null126488 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString118242_failAssert0null126488_failAssert0_literalMutationString132208 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString118242_failAssert0null126488_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                Element submit = doc.select(null).first();
                submit.attr("value");
                submit = doc.select("input[value=????]").first();
                submit.id();
                Element newsLink = doc.select("a:contains(?)").first();
                newsLink.absUrl("href");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                doc.outputSettings().charset("ascii");
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaidu_literalMutationString118242 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString118242_failAssert0null126488 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString118242_failAssert0null126488_failAssert0_add140080_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    Element submit = doc.select(null).first();
                    submit.attr("value");
                    submit = doc.select("input[value=????]").first();
                    submit.id();
                    Element newsLink = doc.select("a:contains(?)").first();
                    newsLink.absUrl("href");
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    doc.outputSettings().charset("ascii");
                    doc.outputSettings().charset("ascii");
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaidu_literalMutationString118242 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaidu_literalMutationString118242_failAssert0null126488 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString118242_failAssert0null126488_failAssert0_add140080 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString118242_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testBaidu_literalMutationString118242 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString118242_failAssert0null126488_failAssert0null143202_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    Element submit = doc.select(null).first();
                    submit.attr("value");
                    submit = doc.select(null).first();
                    submit.id();
                    Element newsLink = doc.select("a:contains(?)").first();
                    newsLink.absUrl("href");
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    doc.outputSettings().charset("ascii");
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaidu_literalMutationString118242 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaidu_literalMutationString118242_failAssert0null126488 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString118242_failAssert0null126488_failAssert0null143202 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString118242_failAssert0_literalMutationString122127_failAssert0() throws IOException {
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
                doc.select("Jay").outerHtml();
                org.junit.Assert.fail("testBaidu_literalMutationString118242 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString118242_failAssert0_literalMutationString122127 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString118242_failAssert0_add125186_failAssert0() throws IOException {
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
                org.junit.Assert.fail("testBaidu_literalMutationString118242 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString118242_failAssert0_add125186 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidunull118325_failAssert0_add124463_failAssert0_literalMutationString128302_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, null);
                    Element submit = doc.select("#su").first();
                    submit.attr("value");
                    submit = doc.select("input[value=????]").first();
                    submit.id();
                    Element newsLink = doc.select("a:contains(?)").first();
                    newsLink.absUrl("href");
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    doc.outputSettings().charset("ascii");
                    doc.select("title");
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaidunull118325 should have thrown IllegalArgumentException");
                }
                org.junit.Assert.fail("testBaidunull118325_failAssert0_add124463 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testBaidunull118325_failAssert0_add124463_failAssert0_literalMutationString128302 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50219_literalMutationString50579_failAssert0null57092_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, null);
                String o_testBaiduVariant_literalMutationString50219__7 = doc.outputSettings().charset().displayName();
                String o_testBaiduVariant_literalMutationString50219__10 = doc.select("ttle").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50219_literalMutationString50579 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50219_literalMutationString50579_failAssert0null57092 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50203_failAssert0_add51024_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings();
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50203 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50203_failAssert0_add51024 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50203_failAssert0_add51020_failAssert0() throws IOException {
        try {
            {
                AmplParseTest.getFile("");
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50203 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50203_failAssert0_add51020 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50219_literalMutationString50579_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
            String o_testBaiduVariant_literalMutationString50219__7 = doc.outputSettings().charset().displayName();
            String o_testBaiduVariant_literalMutationString50219__10 = doc.select("ttle").outerHtml();
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50219_literalMutationString50579 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50203_failAssert0null51151_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select(null).outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50203 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50203_failAssert0null51151 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50203_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
            doc.outputSettings().charset().displayName();
            doc.select("title").outerHtml();
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50203 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50203_failAssert0null51150_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, null);
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50203 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50203_failAssert0null51150 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50203_failAssert0_add51020_failAssert0_add55985_failAssert0() throws IOException {
        try {
            {
                {
                    AmplParseTest.getFile("");
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    doc.outputSettings().charset();
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString50203 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50203_failAssert0_add51020 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50203_failAssert0_add51020_failAssert0_add55985 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50203_failAssert0_add51020_failAssert0_add55989_failAssert0() throws IOException {
        try {
            {
                {
                    AmplParseTest.getFile("");
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString50203 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50203_failAssert0_add51020 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50203_failAssert0_add51020_failAssert0_add55989 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50203_failAssert0null51150_failAssert0_literalMutationString53458_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, null);
                    doc.outputSettings().charset().displayName();
                    doc.select("tittle").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString50203 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50203_failAssert0null51150 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50203_failAssert0null51150_failAssert0_literalMutationString53458 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50212_add50893_literalMutationString51864_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.baidBu.com/");
            String o_testBaiduVariant_literalMutationString50212__7 = doc.outputSettings().charset().displayName();
            Elements o_testBaiduVariant_literalMutationString50212_add50893__12 = doc.select("title");
            String o_testBaiduVariant_literalMutationString50212__10 = doc.select("title").outerHtml();
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50212_add50893_literalMutationString51864 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50215_literalMutationString50561_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
            String o_testBaiduVariant_literalMutationString50215__7 = doc.outputSettings().charset().displayName();
            String o_testBaiduVariant_literalMutationString50215__10 = doc.select("tmtle").outerHtml();
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50215_literalMutationString50561 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50203_failAssert0_add51024_failAssert0_literalMutationString53144_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "Jay");
                    doc.outputSettings();
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString50203 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50203_failAssert0_add51024 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50203_failAssert0_add51024_failAssert0_literalMutationString53144 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_add50221_literalMutationString50354_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document o_testBaiduVariant_add50221__4 = Jsoup.parse(in, null, "http://www.baidu.com/");
            Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
            String o_testBaiduVariant_add50221__8 = doc.outputSettings().charset().displayName();
            String o_testBaiduVariant_add50221__11 = doc.select("title").outerHtml();
            org.junit.Assert.fail("testBaiduVariant_add50221_literalMutationString50354 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_add50222_literalMutationString50414_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
            String o_testBaiduVariant_add50222__7 = doc.outputSettings().charset().displayName();
            String o_testBaiduVariant_add50222__10 = doc.outputSettings().charset().displayName();
            String o_testBaiduVariant_add50222__13 = doc.select("title").outerHtml();
            org.junit.Assert.fail("testBaiduVariant_add50222_literalMutationString50414 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50203_failAssert0_literalMutationString50770_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("ti!le").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50203 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50203_failAssert0_literalMutationString50770 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50203_failAssert0null51150_failAssert0_add55730_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, null);
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString50203 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50203_failAssert0null51150 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50203_failAssert0null51150_failAssert0_add55730 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50207_failAssert0_literalMutationString50687_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50207 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50207_failAssert0_literalMutationString50687 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50203_failAssert0null51151_failAssert0_add56161_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Jsoup.parse(in, null, "http://www.baidu.com/");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    doc.outputSettings().charset().displayName();
                    doc.select(null).outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString50203 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50203_failAssert0null51151 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50203_failAssert0null51151_failAssert0_add56161 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50203_failAssert0_add51020_failAssert0null57009_failAssert0() throws IOException {
        try {
            {
                {
                    AmplParseTest.getFile("");
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    doc.outputSettings().charset().displayName();
                    doc.select(null).outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString50203 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50203_failAssert0_add51020 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50203_failAssert0_add51020_failAssert0null57009 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50203_failAssert0_add51020_failAssert0_literalMutationString53954_failAssert0() throws IOException {
        try {
            {
                {
                    AmplParseTest.getFile("");
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString50203 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50203_failAssert0_add51020 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50203_failAssert0_add51020_failAssert0_literalMutationString53954 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50203_failAssert0null51151_failAssert0_literalMutationString54231_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http:/www.baidu.com/");
                    doc.outputSettings().charset().displayName();
                    doc.select(null).outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString50203 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50203_failAssert0null51151 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50203_failAssert0null51151_failAssert0_literalMutationString54231 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50203_failAssert0_add51024_failAssert0_add55521_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Jsoup.parse(in, null, "http://www.baidu.com/");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    doc.outputSettings();
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString50203 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50203_failAssert0_add51024 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50203_failAssert0_add51024_failAssert0_add55521 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50203_failAssert0null51151_failAssert0null57076_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, null);
                    doc.outputSettings().charset().displayName();
                    doc.select(null).outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString50203 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50203_failAssert0null51151 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50203_failAssert0null51151_failAssert0null57076 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50206_failAssert0_literalMutationString50723_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50206 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50206_failAssert0_literalMutationString50723 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50203_failAssert0_add51024_failAssert0_add55527_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    doc.outputSettings();
                    doc.outputSettings().charset().displayName();
                    doc.select("title");
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString50203 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50203_failAssert0_add51024 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50203_failAssert0_add51024_failAssert0_add55527 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50211_literalMutationString50633_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://w(w.baidu.com/");
            String o_testBaiduVariant_literalMutationString50211__7 = doc.outputSettings().charset().displayName();
            String o_testBaiduVariant_literalMutationString50211__10 = doc.select("title").outerHtml();
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50211_literalMutationString50633 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50219_literalMutationString50579_failAssert0_literalMutationString54299_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                String o_testBaiduVariant_literalMutationString50219__7 = doc.outputSettings().charset().displayName();
                String o_testBaiduVariant_literalMutationString50219__10 = doc.select("tmle").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50219_literalMutationString50579 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50219_literalMutationString50579_failAssert0_literalMutationString54299 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50203_failAssert0_add51024_failAssert0_literalMutationString53150_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    doc.outputSettings();
                    doc.outputSettings().charset().displayName();
                    doc.select("Vitle").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString50203 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50203_failAssert0_add51024 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50203_failAssert0_add51024_failAssert0_literalMutationString53150 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50219_literalMutationString50579_failAssert0_add56201_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Jsoup.parse(in, null, "http://www.baidu.com/");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                String o_testBaiduVariant_literalMutationString50219__7 = doc.outputSettings().charset().displayName();
                String o_testBaiduVariant_literalMutationString50219__10 = doc.select("ttle").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50219_literalMutationString50579 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50219_literalMutationString50579_failAssert0_add56201 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100795_literalMutationString101973_failAssert0null117666_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
                Document doc = Jsoup.parse(in, null, "http://example.zom/");
                String o_testHtml5Charset_literalMutationString100795__7 = doc.text();
                String o_testHtml5Charset_literalMutationString100795__8 = doc.outputSettings().charset().displayName();
                in = AmplParseTest.getFile("");
                doc = Jsoup.parse(in, null, "http://example.com");
                String o_testHtml5Charset_literalMutationString100795__18 = doc.outputSettings().charset().displayName();
                boolean o_testHtml5Charset_literalMutationString100795__21 = "?".equals(doc.text());
                in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                doc = Jsoup.parse(in, null, null);
                String o_testHtml5Charset_literalMutationString100795__29 = doc.outputSettings().charset().displayName();
                String o_testHtml5Charset_literalMutationString100795__32 = doc.outputSettings().charset().displayName();
                String o_testHtml5Charset_literalMutationString100795__35 = doc.text();
                String o_testHtml5Charset_literalMutationString100795__36 = doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100795_literalMutationString101973 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100795_literalMutationString101973_failAssert0null117666 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100795_literalMutationString101973_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
            Document doc = Jsoup.parse(in, null, "http://example.zom/");
            String o_testHtml5Charset_literalMutationString100795__7 = doc.text();
            String o_testHtml5Charset_literalMutationString100795__8 = doc.outputSettings().charset().displayName();
            in = AmplParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example.com");
            String o_testHtml5Charset_literalMutationString100795__18 = doc.outputSettings().charset().displayName();
            boolean o_testHtml5Charset_literalMutationString100795__21 = "?".equals(doc.text());
            in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
            doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_literalMutationString100795__29 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_literalMutationString100795__32 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_literalMutationString100795__35 = doc.text();
            String o_testHtml5Charset_literalMutationString100795__36 = doc.text();
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100795_literalMutationString101973 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_add100831_literalMutationString101433_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_add100831__7 = doc.text();
            String o_testHtml5Charset_add100831__8 = doc.outputSettings().charset().displayName();
            AmplParseTest.getFile("/htmltests/meta-charset-2.html");
            in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            String o_testHtml5Charset_add100831__19 = doc.outputSettings().charset().displayName();
            boolean o_testHtml5Charset_add100831__22 = "?".equals(doc.text());
            in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
            doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_add100831__30 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_add100831__33 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_add100831__36 = doc.text();
            String o_testHtml5Charset_add100831__37 = doc.text();
            org.junit.Assert.fail("testHtml5Charset_add100831_literalMutationString101433 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100798_failAssert0_add104594_failAssert0() throws IOException {
        try {
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
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100798 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100798_failAssert0_add104594 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100813_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100813 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_add100831_literalMutationString101433_failAssert0null117519_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                String o_testHtml5Charset_add100831__7 = doc.text();
                String o_testHtml5Charset_add100831__8 = doc.outputSettings().charset().displayName();
                AmplParseTest.getFile("/htmltests/meta-charset-2.html");
                in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
                doc = Jsoup.parse(in, null, null);
                String o_testHtml5Charset_add100831__19 = doc.outputSettings().charset().displayName();
                boolean o_testHtml5Charset_add100831__22 = "?".equals(doc.text());
                in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                doc = Jsoup.parse(in, null, "http://example.com/");
                String o_testHtml5Charset_add100831__30 = doc.outputSettings().charset().displayName();
                String o_testHtml5Charset_add100831__33 = doc.outputSettings().charset().displayName();
                String o_testHtml5Charset_add100831__36 = doc.text();
                String o_testHtml5Charset_add100831__37 = doc.text();
                org.junit.Assert.fail("testHtml5Charset_add100831_literalMutationString101433 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_add100831_literalMutationString101433_failAssert0null117519 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100787_failAssert0null105466_failAssert0() throws IOException {
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
                in = AmplParseTest.getFile(null);
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100787 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100787_failAssert0null105466 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100798_failAssert0_add104594_failAssert0null117709_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
                    Document doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.text();
                    doc.outputSettings().charset().displayName();
                    in = AmplParseTest.getFile("");
                    Jsoup.parse(in, null, "http://example.com");
                    doc = Jsoup.parse(in, null, null);
                    doc.outputSettings().charset().displayName();
                    "?".equals(doc.text());
                    in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                    doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.text();
                    doc.text();
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString100798 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100798_failAssert0_add104594 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100798_failAssert0_add104594_failAssert0null117709 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100787_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100787 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100798_failAssert0_literalMutationString102878_failAssert0() throws IOException {
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
                in = AmplParseTest.getFile("Jay");
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100798 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100798_failAssert0_literalMutationString102878 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100798_failAssert0_add104594_failAssert0_add115991_failAssert0() throws IOException {
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
                    AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                    in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                    doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.text();
                    doc.text();
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString100798 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100798_failAssert0_add104594 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100798_failAssert0_add104594_failAssert0_add115991 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100798_failAssert0_add104606_failAssert0() throws IOException {
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
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100798 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100798_failAssert0_add104606 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_add100827_literalMutationString101129_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
            Document doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_add100827__7 = doc.text();
            String o_testHtml5Charset_add100827__8 = doc.text();
            String o_testHtml5Charset_add100827__9 = doc.outputSettings().charset().displayName();
            in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            String o_testHtml5Charset_add100827__19 = doc.outputSettings().charset().displayName();
            boolean o_testHtml5Charset_add100827__22 = "?".equals(doc.text());
            in = AmplParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_add100827__30 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_add100827__33 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_add100827__36 = doc.text();
            String o_testHtml5Charset_add100827__37 = doc.text();
            org.junit.Assert.fail("testHtml5Charset_add100827_literalMutationString101129 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100813_failAssert0_add104586_failAssert0() throws IOException {
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
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100813 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100813_failAssert0_add104586 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100818_failAssert0_literalMutationString102887_failAssert0() throws IOException {
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
                in = AmplParseTest.getFile("FmZ,57Mf[}?4zof7@/lQKl[1<}n$r%");
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100818 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100818_failAssert0_literalMutationString102887 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100798_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100798 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_add100828_add103574_literalMutationString107158_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
            Document doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_add100828__7 = doc.text();
            String o_testHtml5Charset_add100828__8 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_add100828__11 = doc.outputSettings().charset().displayName();
            in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
            Document o_testHtml5Charset_add100828_add103574__24 = Jsoup.parse(in, null, "http://example.com");
            doc = Jsoup.parse(in, null, "http://example.com");
            String o_testHtml5Charset_add100828__21 = doc.outputSettings().charset().displayName();
            boolean o_testHtml5Charset_add100828__24 = "?".equals(doc.text());
            in = AmplParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_add100828__32 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_add100828__35 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_add100828__38 = doc.text();
            String o_testHtml5Charset_add100828__39 = doc.text();
            org.junit.Assert.fail("testHtml5Charset_add100828_add103574_literalMutationString107158 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_add100831_literalMutationString101433_failAssert0_literalMutationString110931_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                String o_testHtml5Charset_add100831__7 = doc.text();
                String o_testHtml5Charset_add100831__8 = doc.outputSettings().charset().displayName();
                AmplParseTest.getFile("/htmltests/meta-kcharset-2.html");
                in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                String o_testHtml5Charset_add100831__19 = doc.outputSettings().charset().displayName();
                boolean o_testHtml5Charset_add100831__22 = "?".equals(doc.text());
                in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                doc = Jsoup.parse(in, null, "http://example.com/");
                String o_testHtml5Charset_add100831__30 = doc.outputSettings().charset().displayName();
                String o_testHtml5Charset_add100831__33 = doc.outputSettings().charset().displayName();
                String o_testHtml5Charset_add100831__36 = doc.text();
                String o_testHtml5Charset_add100831__37 = doc.text();
                org.junit.Assert.fail("testHtml5Charset_add100831_literalMutationString101433 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_add100831_literalMutationString101433_failAssert0_literalMutationString110931 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100787_failAssert0_literalMutationString103221_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                doc.text();
                doc.outputSettings().charset().displayName();
                in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.outputSettings().charset().displayName();
                "Jay".equals(doc.text());
                in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100787 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100787_failAssert0_literalMutationString103221 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_add100843_literalMutationString101699_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
            Document doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_add100843__7 = doc.text();
            String o_testHtml5Charset_add100843__8 = doc.outputSettings().charset().displayName();
            in = AmplParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example.com");
            String o_testHtml5Charset_add100843__18 = doc.outputSettings().charset().displayName();
            boolean o_testHtml5Charset_add100843__21 = "?".equals(doc.text());
            in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
            doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_add100843__29 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_add100843__32 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_add100843__35 = doc.text();
            String o_testHtml5Charset_add100843__36 = doc.text();
            String o_testHtml5Charset_add100843__37 = doc.text();
            org.junit.Assert.fail("testHtml5Charset_add100843_literalMutationString101699 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100795_literalMutationString101973_failAssert0_add115859_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
                Document doc = Jsoup.parse(in, null, "http://example.zom/");
                String o_testHtml5Charset_literalMutationString100795__7 = doc.text();
                String o_testHtml5Charset_literalMutationString100795__8 = doc.outputSettings().charset().displayName();
                AmplParseTest.getFile("");
                in = AmplParseTest.getFile("");
                doc = Jsoup.parse(in, null, "http://example.com");
                String o_testHtml5Charset_literalMutationString100795__18 = doc.outputSettings().charset().displayName();
                boolean o_testHtml5Charset_literalMutationString100795__21 = "?".equals(doc.text());
                in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                doc = Jsoup.parse(in, null, "http://example.com/");
                String o_testHtml5Charset_literalMutationString100795__29 = doc.outputSettings().charset().displayName();
                String o_testHtml5Charset_literalMutationString100795__32 = doc.outputSettings().charset().displayName();
                String o_testHtml5Charset_literalMutationString100795__35 = doc.text();
                String o_testHtml5Charset_literalMutationString100795__36 = doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100795_literalMutationString101973 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100795_literalMutationString101973_failAssert0_add115859 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100798_failAssert0_add104594_failAssert0_literalMutationString111739_failAssert0() throws IOException {
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
                    "".equals(doc.text());
                    in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                    doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.text();
                    doc.text();
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString100798 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100798_failAssert0_add104594 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100798_failAssert0_add104594_failAssert0_literalMutationString111739 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100808_literalMutationString102488_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
            Document doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_literalMutationString100808__7 = doc.text();
            String o_testHtml5Charset_literalMutationString100808__8 = doc.outputSettings().charset().displayName();
            in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
            doc = Jsoup.parse(in, null, ",?%3%w[+srZH)FGzQQ");
            String o_testHtml5Charset_literalMutationString100808__18 = doc.outputSettings().charset().displayName();
            boolean o_testHtml5Charset_literalMutationString100808__21 = "?".equals(doc.text());
            in = AmplParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_literalMutationString100808__29 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_literalMutationString100808__32 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_literalMutationString100808__35 = doc.text();
            String o_testHtml5Charset_literalMutationString100808__36 = doc.text();
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100808_literalMutationString102488 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100795_literalMutationString101973_failAssert0_literalMutationString111549_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
                Document doc = Jsoup.parse(in, null, "http://example.zom/");
                String o_testHtml5Charset_literalMutationString100795__7 = doc.text();
                String o_testHtml5Charset_literalMutationString100795__8 = doc.outputSettings().charset().displayName();
                in = AmplParseTest.getFile("");
                doc = Jsoup.parse(in, null, "http://exampl.com");
                String o_testHtml5Charset_literalMutationString100795__18 = doc.outputSettings().charset().displayName();
                boolean o_testHtml5Charset_literalMutationString100795__21 = "?".equals(doc.text());
                in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                doc = Jsoup.parse(in, null, "http://example.com/");
                String o_testHtml5Charset_literalMutationString100795__29 = doc.outputSettings().charset().displayName();
                String o_testHtml5Charset_literalMutationString100795__32 = doc.outputSettings().charset().displayName();
                String o_testHtml5Charset_literalMutationString100795__35 = doc.text();
                String o_testHtml5Charset_literalMutationString100795__36 = doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100795_literalMutationString101973 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100795_literalMutationString101973_failAssert0_literalMutationString111549 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100787_failAssert0_add104823_failAssert0() throws IOException {
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
                doc.outputSettings();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100787 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100787_failAssert0_add104823 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_add100831_literalMutationString101433_failAssert0_add115472_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                String o_testHtml5Charset_add100831__7 = doc.text();
                doc.outputSettings();
                String o_testHtml5Charset_add100831__8 = doc.outputSettings().charset().displayName();
                AmplParseTest.getFile("/htmltests/meta-charset-2.html");
                in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                String o_testHtml5Charset_add100831__19 = doc.outputSettings().charset().displayName();
                boolean o_testHtml5Charset_add100831__22 = "?".equals(doc.text());
                in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                doc = Jsoup.parse(in, null, "http://example.com/");
                String o_testHtml5Charset_add100831__30 = doc.outputSettings().charset().displayName();
                String o_testHtml5Charset_add100831__33 = doc.outputSettings().charset().displayName();
                String o_testHtml5Charset_add100831__36 = doc.text();
                String o_testHtml5Charset_add100831__37 = doc.text();
                org.junit.Assert.fail("testHtml5Charset_add100831_literalMutationString101433 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_add100831_literalMutationString101433_failAssert0_add115472 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString144710_failAssert0null145541_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select(null).first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString144710 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString144710_failAssert0null145541 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString144716_add145330_literalMutationString146532_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "");
            Element o_testNytArticle_literalMutationString144716_add145330__6 = doc.select("nyt_headline[version=1.0]").first();
            Element headline = doc.select("nyt_headline[version=1.0]").first();
            String o_testNytArticle_literalMutationString144716__9 = headline.text();
            org.junit.Assert.fail("testNytArticle_literalMutationString144716_add145330_literalMutationString146532 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString144710_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            Element headline = doc.select("nyt_headline[version=1.0]").first();
            headline.text();
            org.junit.Assert.fail("testNytArticle_literalMutationString144710 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticlenull144735_failAssert0_literalMutationString145032_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, null);
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticlenull144735 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testNytArticlenull144735_failAssert0_literalMutationString145032 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString144710_failAssert0_add145428_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                doc.select("nyt_headline[version=1.0]");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString144710 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString144710_failAssert0_add145428 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString144710_failAssert0null145541_failAssert0_add149713_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    Element headline = doc.select(null).first();
                    headline.text();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString144710 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString144710_failAssert0null145541 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString144710_failAssert0null145541_failAssert0_add149713 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString144710_failAssert0_add145428_failAssert0_add149990_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    doc.select("nyt_headline[version=1.0]");
                    Element headline = doc.select("nyt_headline[version=1.0]").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString144710 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString144710_failAssert0_add145428 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString144710_failAssert0_add145428_failAssert0_add149990 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticlenull144736_failAssert0null145501_failAssert0_literalMutationString147184_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, null);
                    Element headline = doc.select(null).first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticlenull144736 should have thrown IllegalArgumentException");
                }
                org.junit.Assert.fail("testNytArticlenull144736_failAssert0null145501 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testNytArticlenull144736_failAssert0null145501_failAssert0_literalMutationString147184 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString144710_failAssert0_literalMutationString145224_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("nyt_headline[version=.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString144710 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString144710_failAssert0_literalMutationString145224 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString144710_failAssert0null145541_failAssert0_literalMutationString147266_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "Jay");
                    Element headline = doc.select(null).first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString144710 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString144710_failAssert0null145541 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString144710_failAssert0null145541_failAssert0_literalMutationString147266 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticlenull144735_failAssert0_literalMutationString145032_failAssert0_literalMutationString147511_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, null);
                    Element headline = doc.select("").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticlenull144735 should have thrown IllegalArgumentException");
                }
                org.junit.Assert.fail("testNytArticlenull144735_failAssert0_literalMutationString145032 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticlenull144735_failAssert0_literalMutationString145032_failAssert0_literalMutationString147511 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString144710_failAssert0_add145430_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString144710 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString144710_failAssert0_add145430 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString144713_failAssert0_literalMutationString145197_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString144713 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString144713_failAssert0_literalMutationString145197 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString144710_failAssert0_literalMutationString145219_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "Jay");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString144710 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString144710_failAssert0_literalMutationString145219 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticlenull144735_failAssert0_literalMutationString145032_failAssert0_add149808_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, null);
                    doc.select("nyt_headline[version=1.0]");
                    Element headline = doc.select("nyt_headline[version=1.0]").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticlenull144735 should have thrown IllegalArgumentException");
                }
                org.junit.Assert.fail("testNytArticlenull144735_failAssert0_literalMutationString145032 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticlenull144735_failAssert0_literalMutationString145032_failAssert0_add149808 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString144723_failAssert0null145504_failAssert0_literalMutationString148170_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, null);
                    Element headline = doc.select("").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString144723 should have thrown IllegalArgumentException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString144723_failAssert0null145504 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString144723_failAssert0null145504_failAssert0_literalMutationString148170 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString144710_failAssert0_add145428_failAssert0null150936_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    doc.select(null);
                    Element headline = doc.select("nyt_headline[version=1.0]").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString144710 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString144710_failAssert0_add145428 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString144710_failAssert0_add145428_failAssert0null150936 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString144710_failAssert0_add145428_failAssert0_literalMutationString148014_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    doc.select("nyt_headline[version=1.0]");
                    Element headline = doc.select("nyt_headlin[version=1.0]").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString144710 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString144710_failAssert0_add145428 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString144710_failAssert0_add145428_failAssert0_literalMutationString148014 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString28097_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
            Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
            p.text();
            org.junit.Assert.fail("testYahooArticle_literalMutationString28097 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString28097_failAssert0_add29209_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Jsoup.parse(in, "UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                Document doc = Jsoup.parse(in, "UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
                p.text();
                org.junit.Assert.fail("testYahooArticle_literalMutationString28097 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString28097_failAssert0_add29209 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString28098_failAssert0_add29165_failAssert0_literalMutationString31390_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                    Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
                    p.text();
                    org.junit.Assert.fail("testYahooArticle_literalMutationString28098 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testYahooArticle_literalMutationString28098_failAssert0_add29165 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString28098_failAssert0_add29165_failAssert0_literalMutationString31390 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString28114_add29088_literalMutationString30349_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "K[*dix&$[yVV#}s>|?*jwEFpt]PFx)Upk{Qh]4*IzB3]I2F12PnEN");
            Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
            String o_testYahooArticle_literalMutationString28114_add29088__8 = p.text();
            String o_testYahooArticle_literalMutationString28114__8 = p.text();
            org.junit.Assert.fail("testYahooArticle_literalMutationString28114_add29088_literalMutationString30349 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString28098_failAssert0_add29162_failAssert0_literalMutationString33059_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                    doc.select("p:contains(Volt will be sold in the United States)").first();
                    Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
                    p.text();
                    org.junit.Assert.fail("testYahooArticle_literalMutationString28098 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testYahooArticle_literalMutationString28098_failAssert0_add29162 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString28098_failAssert0_add29162_failAssert0_literalMutationString33059 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString28108_failAssert0_add29171_failAssert0_literalMutationString32948_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "d*g|#", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                    Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
                    p.text();
                    org.junit.Assert.fail("testYahooArticle_literalMutationString28108 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("testYahooArticle_literalMutationString28108_failAssert0_add29171 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString28108_failAssert0_add29171_failAssert0_literalMutationString32948 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString28097_failAssert0_literalMutationString28989_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "kMnQ6-LsLimNJC,}:5cu1L={LeBW]x1EQ(0G]pH^m#-]&=SMGL h#");
                Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
                p.text();
                org.junit.Assert.fail("testYahooArticle_literalMutationString28097 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString28097_failAssert0_literalMutationString28989 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString28097_failAssert0_literalMutationString28985_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "");
                Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
                p.text();
                org.junit.Assert.fail("testYahooArticle_literalMutationString28097 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString28097_failAssert0_literalMutationString28985 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString28118_failAssert0_literalMutationString28904_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                Element p = doc.select("P:=1pl2k2x6##J JQppxNh7eU[aG:aiLQYd {qn&(Pjv{Kc|_[").first();
                p.text();
                org.junit.Assert.fail("testYahooArticle_literalMutationString28118 should have thrown Selector$SelectorParseException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString28118_failAssert0_literalMutationString28904 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString28097_failAssert0_add29211_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                doc.select("p:contains(Volt will be sold in the United States)");
                Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
                p.text();
                org.junit.Assert.fail("testYahooArticle_literalMutationString28097 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString28097_failAssert0_add29211 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString28097_failAssert0null29380_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", null);
                Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
                p.text();
                org.junit.Assert.fail("testYahooArticle_literalMutationString28097 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString28097_failAssert0null29380 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString28097_failAssert0null29381_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                Element p = doc.select(null).first();
                p.text();
                org.junit.Assert.fail("testYahooArticle_literalMutationString28097 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString28097_failAssert0null29381 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_add74729_literalMutationString74822_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null);
            Element form = doc.select("#form").first();
            int o_testLowercaseUtf8Charset_add74729__8 = form.children().size();
            String o_testLowercaseUtf8Charset_add74729__10 = doc.outputSettings().charset().name();
            String o_testLowercaseUtf8Charset_add74729__13 = doc.outputSettings().charset().name();
            org.junit.Assert.fail("testLowercaseUtf8Charset_add74729_literalMutationString74822 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString74711_failAssert0_add75301_failAssert0_add78344_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select("#form").first();
                    form.children();
                    form.children().size();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74711 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74711_failAssert0_add75301 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74711_failAssert0_add75301_failAssert0_add78344 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString74711_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null);
            Element form = doc.select("#form").first();
            form.children().size();
            doc.outputSettings().charset().name();
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74711 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString74711_failAssert0_add75301_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                form.children().size();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74711 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74711_failAssert0_add75301 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString74711_failAssert0_add75306_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74711 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74711_failAssert0_add75306 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_add74727null75328_failAssert0_literalMutationString76692_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select(null).first();
                int o_testLowercaseUtf8Charset_add74727__8 = form.children().size();
                int o_testLowercaseUtf8Charset_add74727__10 = form.children().size();
                String o_testLowercaseUtf8Charset_add74727__12 = doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_add74727null75328 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_add74727null75328_failAssert0_literalMutationString76692 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString74711_failAssert0_add75297_failAssert0() throws IOException {
        try {
            {
                AmplParseTest.getFile("");
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74711 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74711_failAssert0_add75297 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString74711_failAssert0null75376_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select(null).first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74711 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74711_failAssert0null75376 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_add74729_literalMutationString74822_failAssert0_add79726_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Jsoup.parse(in, null);
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                int o_testLowercaseUtf8Charset_add74729__8 = form.children().size();
                String o_testLowercaseUtf8Charset_add74729__10 = doc.outputSettings().charset().name();
                String o_testLowercaseUtf8Charset_add74729__13 = doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_add74729_literalMutationString74822 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_add74729_literalMutationString74822_failAssert0_add79726 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString74721_failAssert0null75346_failAssert0_literalMutationString76608_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select(null).first();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74721 should have thrown Selector$SelectorParseException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74721_failAssert0null75346 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74721_failAssert0null75346_failAssert0_literalMutationString76608 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString74721_failAssert0_literalMutationString74929_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("|5$M:").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74721 should have thrown Selector$SelectorParseException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74721_failAssert0_literalMutationString74929 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString74717_failAssert0_literalMutationString75001_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74717 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74717_failAssert0_literalMutationString75001 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString74712_failAssert0_literalMutationString74956_failAssert0_literalMutationString77013_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select("#form").first();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74712 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74712_failAssert0_literalMutationString74956 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74712_failAssert0_literalMutationString74956_failAssert0_literalMutationString77013 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString74722_failAssert0_literalMutationString74990_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#orm").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74722 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74722_failAssert0_literalMutationString74990 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_add74729_literalMutationString74822_failAssert0_literalMutationString77699_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#f?orm").first();
                int o_testLowercaseUtf8Charset_add74729__8 = form.children().size();
                String o_testLowercaseUtf8Charset_add74729__10 = doc.outputSettings().charset().name();
                String o_testLowercaseUtf8Charset_add74729__13 = doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_add74729_literalMutationString74822 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_add74729_literalMutationString74822_failAssert0_literalMutationString77699 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_add74728_literalMutationString74820_failAssert0_literalMutationString77073_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#frm").first();
                Elements o_testLowercaseUtf8Charset_add74728__8 = form.children();
                int o_testLowercaseUtf8Charset_add74728__9 = form.children().size();
                String o_testLowercaseUtf8Charset_add74728__11 = doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_add74728_literalMutationString74820 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_add74728_literalMutationString74820_failAssert0_literalMutationString77073 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_add74729_literalMutationString74822_failAssert0_add79732_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                int o_testLowercaseUtf8Charset_add74729__8 = form.children().size();
                doc.outputSettings().charset();
                String o_testLowercaseUtf8Charset_add74729__10 = doc.outputSettings().charset().name();
                String o_testLowercaseUtf8Charset_add74729__13 = doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_add74729_literalMutationString74822 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_add74729_literalMutationString74822_failAssert0_add79732 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_add74729_literalMutationString74822_failAssert0_literalMutationString77694_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                int o_testLowercaseUtf8Charset_add74729__8 = form.children().size();
                String o_testLowercaseUtf8Charset_add74729__10 = doc.outputSettings().charset().name();
                String o_testLowercaseUtf8Charset_add74729__13 = doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_add74729_literalMutationString74822 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_add74729_literalMutationString74822_failAssert0_literalMutationString77694 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString74713_failAssert0_literalMutationString74976_failAssert0_literalMutationString76959_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select("#Tform").first();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74713 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74713_failAssert0_literalMutationString74976 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74713_failAssert0_literalMutationString74976_failAssert0_literalMutationString76959 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString74711_failAssert0_literalMutationString75046_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74711 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74711_failAssert0_literalMutationString75046 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString74711_failAssert0_literalMutationString75049_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#fom").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74711 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74711_failAssert0_literalMutationString75049 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_add74729_literalMutationString74822_failAssert0null80304_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select(null).first();
                int o_testLowercaseUtf8Charset_add74729__8 = form.children().size();
                String o_testLowercaseUtf8Charset_add74729__10 = doc.outputSettings().charset().name();
                String o_testLowercaseUtf8Charset_add74729__13 = doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_add74729_literalMutationString74822 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_add74729_literalMutationString74822_failAssert0null80304 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString74720_failAssert0_literalMutationString75034_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("Jay").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74720 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74720_failAssert0_literalMutationString75034 should have thrown FileNotFoundException");
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

