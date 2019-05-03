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
    public void testSmhBizArticle_literalMutationString36772_failAssert0_add39002_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Jsoup.parse(in, "UTF-8", "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
                doc.title();
                doc.select("html").attr("xml:lang");
                Elements articleBody = doc.select(".articleBody > *");
                articleBody.size();

                org.junit.Assert.fail("testSmhBizArticle_literalMutationString36772 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSmhBizArticle_literalMutationString36772_failAssert0_add39002 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSmhBizArticle_literalMutationString36772_failAssert0_add39002_failAssert0null49501_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Jsoup.parse(in, "UTF-8", "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
                    doc.title();
                    doc.select(null).attr("xml:lang");
                    Elements articleBody = doc.select(".articleBody > *");
                    articleBody.size();

                    org.junit.Assert.fail("testSmhBizArticle_literalMutationString36772 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testSmhBizArticle_literalMutationString36772_failAssert0_add39002 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSmhBizArticle_literalMutationString36772_failAssert0_add39002_failAssert0null49501 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSmhBizArticle_literalMutationString36772_failAssert0_add39002_failAssert0_literalMutationString45455_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Jsoup.parse(in, "UTF-8", "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
                    doc.title();
                    doc.select("html").attr("xml:lang");
                    Elements articleBody = doc.select("application/x-www-form-urlencoded; charset=UTF-8");
                    articleBody.size();

                    org.junit.Assert.fail("testSmhBizArticle_literalMutationString36772 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testSmhBizArticle_literalMutationString36772_failAssert0_add39002 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSmhBizArticle_literalMutationString36772_failAssert0_add39002_failAssert0_literalMutationString45455 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSmhBizArticle_literalMutationString36772_failAssert0_add39002_failAssert0_literalMutationString45418_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Jsoup.parse(in, "", "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
                    doc.title();
                    doc.select("html").attr("xml:lang");
                    Elements articleBody = doc.select(".articleBody > *");
                    articleBody.size();

                    org.junit.Assert.fail("testSmhBizArticle_literalMutationString36772 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testSmhBizArticle_literalMutationString36772_failAssert0_add39002 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSmhBizArticle_literalMutationString36772_failAssert0_add39002_failAssert0_literalMutationString45418 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSmhBizArticle_literalMutationString36772_failAssert0null39367_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
                doc.title();
                doc.select(null).attr("xml:lang");
                Elements articleBody = doc.select(".articleBody > *");
                articleBody.size();

                org.junit.Assert.fail("testSmhBizArticle_literalMutationString36772 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSmhBizArticle_literalMutationString36772_failAssert0null39367 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSmhBizArticle_literalMutationString36806_failAssert0_literalMutationString37877_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
                doc.title();
                doc.select("html").attr("xml:lang");
                Elements articleBody = doc.select("Um]s[rYASScD+cqF");
                articleBody.size();

                org.junit.Assert.fail("testSmhBizArticle_literalMutationString36806 should have thrown Selector$SelectorParseException");
            }
            org.junit.Assert.fail("testSmhBizArticle_literalMutationString36806_failAssert0_literalMutationString37877 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSmhBizArticle_literalMutationString36789_literalMutationString37303_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "F8Xt}:(n-hC8qKYb gjDVYnHoBE0Ay01aK@<M|[ w[9Y+{G[< a/o^?f{%<8,T:G8cX&tx64&f=DQDrgk}^[<40");
            String o_testSmhBizArticle_literalMutationString36789__5 = doc.title();
            String o_testSmhBizArticle_literalMutationString36789__6 = doc.select("html").attr("xml:lang");
            Elements articleBody = doc.select(".articleBody > *");
            int o_testSmhBizArticle_literalMutationString36789__10 = articleBody.size();

            org.junit.Assert.fail("testSmhBizArticle_literalMutationString36789_literalMutationString37303 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSmhBizArticle_literalMutationString36772_failAssert0_add39002_failAssert0_add48073_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Jsoup.parse(in, "UTF-8", "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
                    doc.title();
                    doc.select("html");
                    doc.select("html").attr("xml:lang");
                    Elements articleBody = doc.select(".articleBody > *");
                    articleBody.size();

                    org.junit.Assert.fail("testSmhBizArticle_literalMutationString36772 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testSmhBizArticle_literalMutationString36772_failAssert0_add39002 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSmhBizArticle_literalMutationString36772_failAssert0_add39002_failAssert0_add48073 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSmhBizArticle_literalMutationString36772_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
            doc.title();
            doc.select("html").attr("xml:lang");
            Elements articleBody = doc.select(".articleBody > *");
            articleBody.size();

            org.junit.Assert.fail("testSmhBizArticle_literalMutationString36772 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSmhBizArticle_literalMutationString36772_failAssert0_add39002_failAssert0null49496_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Jsoup.parse(in, null, "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
                    doc.title();
                    doc.select("html").attr("xml:lang");
                    Elements articleBody = doc.select(".articleBody > *");
                    articleBody.size();

                    org.junit.Assert.fail("testSmhBizArticle_literalMutationString36772 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testSmhBizArticle_literalMutationString36772_failAssert0_add39002 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSmhBizArticle_literalMutationString36772_failAssert0_add39002_failAssert0null49496 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSmhBizArticle_literalMutationString36772_failAssert0_literalMutationString38358_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
                doc.title();
                doc.select("application/x-www-form-urlencoded; charset=UTF-8").attr("xml:lang");
                Elements articleBody = doc.select(".articleBody > *");
                articleBody.size();

                org.junit.Assert.fail("testSmhBizArticle_literalMutationString36772 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSmhBizArticle_literalMutationString36772_failAssert0_literalMutationString38358 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSmhBizArticle_literalMutationString36801_literalMutationString37243_literalMutationString41725_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "");
            String o_testSmhBizArticle_literalMutationString36801__5 = doc.title();
            String o_testSmhBizArticle_literalMutationString36801__6 = doc.select("html").attr("xm]:lang");
            Elements articleBody = doc.select(".articleBody > *");
            int o_testSmhBizArticle_literalMutationString36801__10 = articleBody.size();

            org.junit.Assert.fail("testSmhBizArticle_literalMutationString36801_literalMutationString37243_literalMutationString41725 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSmhBizArticle_literalMutationString36772_failAssert0_add39002_failAssert0_add48069_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Jsoup.parse(in, "UTF-8", "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
                    Jsoup.parse(in, "UTF-8", "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
                    doc.title();
                    doc.select("html").attr("xml:lang");
                    Elements articleBody = doc.select(".articleBody > *");
                    articleBody.size();

                    org.junit.Assert.fail("testSmhBizArticle_literalMutationString36772 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testSmhBizArticle_literalMutationString36772_failAssert0_add39002 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSmhBizArticle_literalMutationString36772_failAssert0_add39002_failAssert0_add48069 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNewsHomepage_literalMutationString61_literalMutationString598_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.news.com.au/");
            String o_testNewsHomepage_literalMutationString61__5 = doc.title();
            String o_testNewsHomepage_literalMutationString61__6 = doc.select(".id1225817868581 h4").text().trim();
            Element a = doc.select("a[href=/entertainment/horoscopes]").first();
            a.attr("href");
            String o_testNewsHomepage_literalMutationString61__13 = a.attr("abs:href");
            Element hs = doc.select("a[href*=naughty-corners-are-a-bad-idea]").first();
            String o_testNewsHomepage_literalMutationString61__17 = hs.attr("href");
            String o_testNewsHomepage_literalMutationString61__18 = hs.attr("href");
            String o_testNewsHomepage_literalMutationString61__19 = hs.attr("");
            org.junit.Assert.fail("testNewsHomepage_literalMutationString61_literalMutationString598 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNewsHomepage_literalMutationString57_add6444_literalMutationString11760_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.news.com.au/");
            String o_testNewsHomepage_literalMutationString57_add6444__5 = doc.title();
            String o_testNewsHomepage_literalMutationString57__5 = doc.title();
            String o_testNewsHomepage_literalMutationString57__6 = doc.select(".id1225817868581 h4").text().trim();
            Element a = doc.select("a[href=/entertainment/horoscopes]").first();
            a.attr("href");
            String o_testNewsHomepage_literalMutationString57__13 = a.attr("abs:href");
            Element hs = doc.select("a[href*=naughty-corners-are-a-bad-idea]").first();
            String o_testNewsHomepage_literalMutationString57__17 = hs.attr("href");
            String o_testNewsHomepage_literalMutationString57__18 = hs.attr("application/x-www-form-urlencoded; charset=UTF-8");
            String o_testNewsHomepage_literalMutationString57__19 = hs.attr("abs:href");
            org.junit.Assert.fail("testNewsHomepage_literalMutationString57_add6444_literalMutationString11760 should have thrown FileNotFoundException");
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
    public void testGoogleSearchIpod_literalMutationString57622_failAssert0_literalMutationNumber60024_failAssert0_literalMutationString67311_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
                    doc.title();
                    Elements results = doc.select("application/x-www-form-urlencoded; charset=UTF-8");
                    results.size();
                    results.get(-1).attr("href");
                    results.get(1).attr("href");
                    org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57622 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57622_failAssert0_literalMutationNumber60024 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57622_failAssert0_literalMutationNumber60024_failAssert0_literalMutationString67311 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_add57676_literalMutationString59411_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
            String o_testGoogleSearchIpod_add57676__5 = doc.title();
            Elements results = doc.select("h3.r > a");
            int o_testGoogleSearchIpod_add57676__8 = results.size();
            String o_testGoogleSearchIpod_add57676__9 = results.get(0).attr("href");
            Element o_testGoogleSearchIpod_add57676__11 = results.get(1);
            String o_testGoogleSearchIpod_add57676__12 = results.get(1).attr("href");
            org.junit.Assert.fail("testGoogleSearchIpod_add57676_literalMutationString59411 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationString57622_failAssert0_literalMutationNumber60024_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
                doc.title();
                Elements results = doc.select("h3.r > a");
                results.size();
                results.get(-1).attr("href");
                results.get(1).attr("href");
                org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57622 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57622_failAssert0_literalMutationNumber60024 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationNumber57659_literalMutationNumber59022_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("/htmltests/google-ipod.html");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
            String o_testGoogleSearchIpod_literalMutationNumber57659__5 = doc.title();
            Elements results = doc.select("h3.r > a");
            int o_testGoogleSearchIpod_literalMutationNumber57659__8 = results.size();
            String o_testGoogleSearchIpod_literalMutationNumber57659__9 = results.get(0).attr("href");
            String o_testGoogleSearchIpod_literalMutationNumber57659__11 = results.get(-1).attr("href");
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationNumber57659_literalMutationNumber59022 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationString57622_failAssert0_add61131_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
                doc.title();
                Elements results = doc.select("h3.r > a");
                results.size();
                results.get(0).attr("href");
                results.get(1).attr("href");
                results.get(1).attr("href");
                org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57622 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57622_failAssert0_add61131 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationString57622_failAssert0_literalMutationNumber60024_failAssert0null73724_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
                    doc.title();
                    Elements results = doc.select(null);
                    results.size();
                    results.get(-1).attr("href");
                    results.get(1).attr("href");
                    org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57622 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57622_failAssert0_literalMutationNumber60024 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57622_failAssert0_literalMutationNumber60024_failAssert0null73724 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationString57636_literalMutationString58576_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aq=g10");
            String o_testGoogleSearchIpod_literalMutationString57636__5 = doc.title();
            Elements results = doc.select("h3.r > a");
            int o_testGoogleSearchIpod_literalMutationString57636__8 = results.size();
            String o_testGoogleSearchIpod_literalMutationString57636__9 = results.get(0).attr("href");
            String o_testGoogleSearchIpod_literalMutationString57636__11 = results.get(1).attr("href");
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57636_literalMutationString58576 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationNumber57647_failAssert0null61647_failAssert0_literalMutationString70729_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("/htmltests/google-ipod.html");
                    Document doc = Jsoup.parse(in, null, "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
                    doc.title();
                    Elements results = doc.select("h3.r > a");
                    results.size();
                    results.get(-1).attr("href");
                    results.get(1).attr("h>ef");
                    org.junit.Assert.fail("testGoogleSearchIpod_literalMutationNumber57647 should have thrown ArrayIndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testGoogleSearchIpod_literalMutationNumber57647_failAssert0null61647 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationNumber57647_failAssert0null61647_failAssert0_literalMutationString70729 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_add57676_literalMutationString59411_failAssert0_literalMutationNumber66631_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
                String o_testGoogleSearchIpod_add57676__5 = doc.title();
                Elements results = doc.select("h3.r > a");
                int o_testGoogleSearchIpod_add57676__8 = results.size();
                String o_testGoogleSearchIpod_add57676__9 = results.get(0).attr("href");
                Element o_testGoogleSearchIpod_add57676__11 = results.get(1);
                String o_testGoogleSearchIpod_add57676__12 = results.get(1).attr("href");
                org.junit.Assert.fail("testGoogleSearchIpod_add57676_literalMutationString59411 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testGoogleSearchIpod_add57676_literalMutationString59411_failAssert0_literalMutationNumber66631 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpodnull57683_failAssert0_literalMutationString60453_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
                doc.title();
                Elements results = doc.select("h3.r > a");
                results.size();
                results.get(0).attr("href");
                results.get(1).attr(null);
                org.junit.Assert.fail("testGoogleSearchIpodnull57683 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testGoogleSearchIpodnull57683_failAssert0_literalMutationString60453 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationString57622_failAssert0_add61131_failAssert0null74088_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
                    doc.title();
                    Elements results = doc.select("h3.r > a");
                    results.size();
                    results.get(0).attr("href");
                    results.get(1).attr("href");
                    results.get(1).attr("href");
                    org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57622 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57622_failAssert0_add61131 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57622_failAssert0_add61131_failAssert0null74088 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationString57622_failAssert0_literalMutationNumber60024_failAssert0_add72081_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
                    doc.title();
                    doc.title();
                    Elements results = doc.select("h3.r > a");
                    results.size();
                    results.get(-1).attr("href");
                    results.get(1).attr("href");
                    org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57622 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57622_failAssert0_literalMutationNumber60024 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57622_failAssert0_literalMutationNumber60024_failAssert0_add72081 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_add57676_literalMutationString59411_failAssert0_add71929_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
                String o_testGoogleSearchIpod_add57676__5 = doc.title();
                Elements results = doc.select("h3.r > a");
                results.size();
                int o_testGoogleSearchIpod_add57676__8 = results.size();
                String o_testGoogleSearchIpod_add57676__9 = results.get(0).attr("href");
                Element o_testGoogleSearchIpod_add57676__11 = results.get(1);
                String o_testGoogleSearchIpod_add57676__12 = results.get(1).attr("href");
                org.junit.Assert.fail("testGoogleSearchIpod_add57676_literalMutationString59411 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testGoogleSearchIpod_add57676_literalMutationString59411_failAssert0_add71929 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationString57622_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
            doc.title();
            Elements results = doc.select("h3.r > a");
            results.size();
            results.get(0).attr("href");
            results.get(1).attr("href");
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57622 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationString57622_failAssert0_add61131_failAssert0_literalMutationString69761_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
                    doc.title();
                    Elements results = doc.select("h3.r > a");
                    results.size();
                    results.get(0).attr("href");
                    results.get(1).attr("hr#ef");
                    results.get(1).attr("href");
                    org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57622 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57622_failAssert0_add61131 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57622_failAssert0_add61131_failAssert0_literalMutationString69761 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationString57622_failAssert0_add61131_failAssert0_add72679_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
                    doc.title();
                    Elements results = doc.select("h3.r > a");
                    results.size();
                    results.size();
                    results.get(0).attr("href");
                    results.get(1).attr("href");
                    results.get(1).attr("href");
                    org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57622 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57622_failAssert0_add61131 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57622_failAssert0_add61131_failAssert0_add72679 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationString57622_failAssert0null61609_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
                doc.title();
                Elements results = doc.select("h3.r > a");
                results.size();
                results.get(0).attr("href");
                results.get(1).attr(null);
                org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57622 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString57622_failAssert0null61609 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString152022_failAssert0_add152644_failAssert0_literalMutationString155661_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "{TF-8");
                    doc.text().contains("gd-jpeg");
                    org.junit.Assert.fail("testBinary_literalMutationString152022 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("testBinary_literalMutationString152022_failAssert0_add152644 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString152022_failAssert0_add152644_failAssert0_literalMutationString155661 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString152016_failAssert0_add152655_failAssert0_literalMutationString154909_failAssert0() throws IOException {
        try {
            {
                {
                    AmplParseTest.getFile("");
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8");
                    doc.text().contains("gdEjpeg");
                    org.junit.Assert.fail("testBinary_literalMutationString152016 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBinary_literalMutationString152016_failAssert0_add152655 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString152016_failAssert0_add152655_failAssert0_literalMutationString154909 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString152016_failAssert0_add152656_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Jsoup.parse(in, "UTF-8");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.text().contains("gd-jpeg");
                org.junit.Assert.fail("testBinary_literalMutationString152016 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString152016_failAssert0_add152656 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString152016_failAssert0_add152655_failAssert0() throws IOException {
        try {
            {
                AmplParseTest.getFile("");
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.text().contains("gd-jpeg");
                org.junit.Assert.fail("testBinary_literalMutationString152016 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString152016_failAssert0_add152655 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinarynull152039_add152576_literalMutationString153606_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null);
            String o_testBinarynull152039_add152576__5 = doc.text();
            boolean o_testBinarynull152039__5 = doc.text().contains("gd-jpeg");
            org.junit.Assert.fail("testBinarynull152039_add152576_literalMutationString153606 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString152016_failAssert0_add152655_failAssert0_add156419_failAssert0() throws IOException {
        try {
            {
                {
                    AmplParseTest.getFile("");
                    AmplParseTest.getFile("");
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8");
                    doc.text().contains("gd-jpeg");
                    org.junit.Assert.fail("testBinary_literalMutationString152016 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBinary_literalMutationString152016_failAssert0_add152655 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString152016_failAssert0_add152655_failAssert0_add156419 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString152016_failAssert0_add152658_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.text();
                doc.text().contains("gd-jpeg");
                org.junit.Assert.fail("testBinary_literalMutationString152016 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString152016_failAssert0_add152658 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString152016_failAssert0_add152658_failAssert0null157099_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    doc.text();
                    doc.text().contains("gd-jpeg");
                    org.junit.Assert.fail("testBinary_literalMutationString152016 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBinary_literalMutationString152016_failAssert0_add152658 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString152016_failAssert0_add152658_failAssert0null157099 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString152016_failAssert0_add152656_failAssert0null157274_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Jsoup.parse(in, "UTF-8");
                    Document doc = Jsoup.parse(in, "UTF-8");
                    doc.text().contains(null);
                    org.junit.Assert.fail("testBinary_literalMutationString152016 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBinary_literalMutationString152016_failAssert0_add152656 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString152016_failAssert0_add152656_failAssert0null157274 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString152016_failAssert0_add152656_failAssert0_literalMutationString155624_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Jsoup.parse(in, "UT-8");
                    Document doc = Jsoup.parse(in, "UTF-8");
                    doc.text().contains("gd-jpeg");
                    org.junit.Assert.fail("testBinary_literalMutationString152016 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBinary_literalMutationString152016_failAssert0_add152656 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString152016_failAssert0_add152656_failAssert0_literalMutationString155624 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString152021_failAssert0_literalMutationString152419_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "");
                doc.text().contains("gd-jpeg");
                org.junit.Assert.fail("testBinary_literalMutationString152021 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString152021_failAssert0_literalMutationString152419 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString152032_literalMutationString152140_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8");
            boolean o_testBinary_literalMutationString152032__5 = doc.text().contains("gE-jpeg");
            org.junit.Assert.fail("testBinary_literalMutationString152032_literalMutationString152140 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinarynull152039_literalMutationString152227_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null);
            boolean o_testBinarynull152039__5 = doc.text().contains("gd-jpeg");
            org.junit.Assert.fail("testBinarynull152039_literalMutationString152227 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString152016_failAssert0null152766_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.text().contains(null);
                org.junit.Assert.fail("testBinary_literalMutationString152016 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString152016_failAssert0null152766 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString152016_failAssert0_add152658_failAssert0_literalMutationString154893_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8");
                    doc.text();
                    doc.text().contains("g_-jpeg");
                    org.junit.Assert.fail("testBinary_literalMutationString152016 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBinary_literalMutationString152016_failAssert0_add152658 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString152016_failAssert0_add152658_failAssert0_literalMutationString154893 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString152016_failAssert0_literalMutationString152491_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "KTF-8");
                doc.text().contains("gd-jpeg");
                org.junit.Assert.fail("testBinary_literalMutationString152016 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString152016_failAssert0_literalMutationString152491 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString152024_failAssert0null152730_failAssert0_literalMutationString155532_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "application/x-www-form-urlencoded; charset=UTF-8");
                    doc.text().contains(null);
                    org.junit.Assert.fail("testBinary_literalMutationString152024 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("testBinary_literalMutationString152024_failAssert0null152730 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString152024_failAssert0null152730_failAssert0_literalMutationString155532 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString152016_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8");
            doc.text().contains("gd-jpeg");
            org.junit.Assert.fail("testBinary_literalMutationString152016 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString152016_failAssert0_literalMutationString152501_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.text().contains("gd-jeg");
                org.junit.Assert.fail("testBinary_literalMutationString152016 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString152016_failAssert0_literalMutationString152501 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString152016_failAssert0_literalMutationString152502_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.text().contains("5d-jpeg");
                org.junit.Assert.fail("testBinary_literalMutationString152016 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString152016_failAssert0_literalMutationString152502 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString152019_failAssert0null152742_failAssert0_literalMutationString154755_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8");
                    doc.text().contains(null);
                    org.junit.Assert.fail("testBinary_literalMutationString152019 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testBinary_literalMutationString152019_failAssert0null152742 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString152019_failAssert0null152742_failAssert0_literalMutationString154755 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString152017_failAssert0null152745_failAssert0_literalMutationString154596_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    doc.text().contains("gd-jpeg");
                    org.junit.Assert.fail("testBinary_literalMutationString152017 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testBinary_literalMutationString152017_failAssert0null152745 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString152017_failAssert0null152745_failAssert0_literalMutationString154596 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString152016_failAssert0_add152658_failAssert0_add156412_failAssert0() throws IOException {
        try {
            {
                {
                    AmplParseTest.getFile("");
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8");
                    doc.text();
                    doc.text().contains("gd-jpeg");
                    org.junit.Assert.fail("testBinary_literalMutationString152016 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBinary_literalMutationString152016_failAssert0_add152658 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString152016_failAssert0_add152658_failAssert0_add156412 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString152016_failAssert0_add152656_failAssert0_add156650_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Jsoup.parse(in, "UTF-8");
                    Document doc = Jsoup.parse(in, "UTF-8");
                    doc.text().contains("gd-jpeg");
                    org.junit.Assert.fail("testBinary_literalMutationString152016 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testBinary_literalMutationString152016_failAssert0_add152656 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString152016_failAssert0_add152656_failAssert0_add156650 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString80710_failAssert0_literalMutationString81586_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                doc.title();
                Element a = doc.select("iE*Ah[QZ,Iz9wY*Y").first();
                a.attr("abs:href");
                a.text();
                org.junit.Assert.fail("testYahooJp_literalMutationString80710 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooJp_literalMutationString80710_failAssert0_literalMutationString81586 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString80710_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
            doc.title();
            Element a = doc.select("a[href=t/2322m2]").first();
            a.attr("abs:href");
            a.text();
            org.junit.Assert.fail("testYahooJp_literalMutationString80710 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString80736_add82131_literalMutationString84133_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
            String o_testYahooJp_literalMutationString80736_add82131__6 = doc.title();
            String o_testYahooJp_literalMutationString80736__6 = doc.title();
            Element a = doc.select("a[href=t/2322m2]").first();
            String o_testYahooJp_literalMutationString80736__10 = a.attr("B1?)(V,g");
            String o_testYahooJp_literalMutationString80736__11 = a.text();
            org.junit.Assert.fail("testYahooJp_literalMutationString80736_add82131_literalMutationString84133 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString80729_failAssert0_literalMutationString81742_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                doc.title();
                Element a = doc.select("a[href=t/2322m]").first();
                a.attr("abs:href");
                a.text();
                org.junit.Assert.fail("testYahooJp_literalMutationString80729 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testYahooJp_literalMutationString80729_failAssert0_literalMutationString81742 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString80712_failAssert0_literalMutationString81786_failAssert0_literalMutationString85200_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "bB4+mPCh!1J`fk|NU Xhdh={5uZ&#Cl{f");
                    doc.title();
                    Element a = doc.select("a[href=t/2322m2]").first();
                    a.attr("abs:href");
                    a.text();
                    org.junit.Assert.fail("testYahooJp_literalMutationString80712 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testYahooJp_literalMutationString80712_failAssert0_literalMutationString81786 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testYahooJp_literalMutationString80712_failAssert0_literalMutationString81786_failAssert0_literalMutationString85200 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString80709_failAssert0_literalMutationString81892_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                doc.title();
                Element a = doc.select("a[href=t/2322m2]").first();
                a.attr("abs:href");
                a.text();
                org.junit.Assert.fail("testYahooJp_literalMutationString80709 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testYahooJp_literalMutationString80709_failAssert0_literalMutationString81892 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString80710_failAssert0_literalMutationString81586_failAssert0_literalMutationString85618_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.wtml");
                    doc.title();
                    Element a = doc.select("iE*Ah[QZ,Iz9wY*Y").first();
                    a.attr("abs:href");
                    a.text();
                    org.junit.Assert.fail("testYahooJp_literalMutationString80710 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testYahooJp_literalMutationString80710_failAssert0_literalMutationString81586 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooJp_literalMutationString80710_failAssert0_literalMutationString81586_failAssert0_literalMutationString85618 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString80710_failAssert0_literalMutationString81586_failAssert0null90837_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                    doc.title();
                    Element a = doc.select("iE*Ah[QZ,Iz9wY*Y").first();
                    a.attr(null);
                    a.text();
                    org.junit.Assert.fail("testYahooJp_literalMutationString80710 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testYahooJp_literalMutationString80710_failAssert0_literalMutationString81586 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooJp_literalMutationString80710_failAssert0_literalMutationString81586_failAssert0null90837 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString80710_failAssert0_add82308_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                doc.title();
                doc.title();
                Element a = doc.select("a[href=t/2322m2]").first();
                a.attr("abs:href");
                a.text();
                org.junit.Assert.fail("testYahooJp_literalMutationString80710 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooJp_literalMutationString80710_failAssert0_add82308 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString80713_failAssert0_add82336_failAssert0_literalMutationString86817_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                    doc.title();
                    Element a = doc.select("a[href=t/2322m2]").first();
                    a.attr("abs:href");
                    a.text();
                    a.text();
                    org.junit.Assert.fail("testYahooJp_literalMutationString80713 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testYahooJp_literalMutationString80713_failAssert0_add82336 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testYahooJp_literalMutationString80713_failAssert0_add82336_failAssert0_literalMutationString86817 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString80710_failAssert0_literalMutationString81586_failAssert0_add89395_failAssert0() throws IOException {
        try {
            {
                {
                    AmplParseTest.getFile("");
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                    doc.title();
                    Element a = doc.select("iE*Ah[QZ,Iz9wY*Y").first();
                    a.attr("abs:href");
                    a.text();
                    org.junit.Assert.fail("testYahooJp_literalMutationString80710 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testYahooJp_literalMutationString80710_failAssert0_literalMutationString81586 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooJp_literalMutationString80710_failAssert0_literalMutationString81586_failAssert0_add89395 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString118444_failAssert0_add124902_failAssert0_literalMutationString137918_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    doc.select("application/x-www-form-urlencoded; charset=UTF-8");
                    Element submit = doc.select("application/x-www-form-urlencoded; charset=UTF-8").first();
                    submit.attr("value");
                    submit = doc.select("input[value=????]").first();
                    submit.id();
                    Element newsLink = doc.select("a:contains(?)").first();
                    newsLink.absUrl("href");
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    doc.outputSettings().charset("ascii");
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaidu_literalMutationString118444 should have thrown Selector$SelectorParseException");
                }
                org.junit.Assert.fail("testBaidu_literalMutationString118444_failAssert0_add124902 should have thrown Selector$SelectorParseException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString118444_failAssert0_add124902_failAssert0_literalMutationString137918 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidunull118519_failAssert0_literalMutationString121788_failAssert0() throws IOException {
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
                doc.select(null).outerHtml();
                doc.outputSettings().charset("ascii");
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaidunull118519 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testBaidunull118519_failAssert0_literalMutationString121788 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString118463_failAssert0null126561_failAssert0_literalMutationString137475_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    Element submit = doc.select("#su").first();
                    submit.attr(null);
                    submit = doc.select("input[value=????]").first();
                    submit.id();
                    Element newsLink = doc.select("application/x-www-form-urlencoded; charset=UTF-8").first();
                    newsLink.absUrl("href");
                    doc.outputSettings().charset().displayName();
                    doc.select("title").outerHtml();
                    doc.outputSettings().charset("ascii");
                    doc.select("title").outerHtml();
                    org.junit.Assert.fail("testBaidu_literalMutationString118463 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testBaidu_literalMutationString118463_failAssert0null126561 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString118463_failAssert0null126561_failAssert0_literalMutationString137475 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString118430_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testBaidu_literalMutationString118430 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_add50193_literalMutationString50538_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document o_testBaiduVariant_add50193__4 = Jsoup.parse(in, null, "http://www.baidu.com/");
            Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
            String o_testBaiduVariant_add50193__8 = doc.outputSettings().charset().displayName();
            String o_testBaiduVariant_add50193__11 = doc.select("title").outerHtml();
            org.junit.Assert.fail("testBaiduVariant_add50193_literalMutationString50538 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50182_literalMutationString50295_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "o[$3I51MivI#&aM}mhf#(");
            String o_testBaiduVariant_literalMutationString50182__7 = doc.outputSettings().charset().displayName();
            String o_testBaiduVariant_literalMutationString50182__10 = doc.select("title").outerHtml();
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50182_literalMutationString50295 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50188_failAssert0_literalMutationString50604_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("(SvK(").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50188 should have thrown Selector$SelectorParseException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50188_failAssert0_literalMutationString50604 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50182_literalMutationString50295_failAssert0_add55931_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "o[$3I51MivI#&aM}mhf#(");
                doc.outputSettings().charset();
                String o_testBaiduVariant_literalMutationString50182__7 = doc.outputSettings().charset().displayName();
                String o_testBaiduVariant_literalMutationString50182__10 = doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50182_literalMutationString50295 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50182_literalMutationString50295_failAssert0_add55931 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50188_failAssert0_literalMutationString50604_failAssert0_literalMutationString53606_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "htatp://www.baidu.com/");
                    doc.outputSettings().charset().displayName();
                    doc.select("(SvK(").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString50188 should have thrown Selector$SelectorParseException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50188_failAssert0_literalMutationString50604 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50188_failAssert0_literalMutationString50604_failAssert0_literalMutationString53606 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_add50193_literalMutationString50538_failAssert0_add55378_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document o_testBaiduVariant_add50193__4 = Jsoup.parse(in, null, "http://www.baidu.com/");
                Jsoup.parse(in, null, "http://www.baidu.com/");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                String o_testBaiduVariant_add50193__8 = doc.outputSettings().charset().displayName();
                String o_testBaiduVariant_add50193__11 = doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_add50193_literalMutationString50538 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_add50193_literalMutationString50538_failAssert0_add55378 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_add50197_literalMutationString50495_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
            String o_testBaiduVariant_add50197__7 = doc.outputSettings().charset().displayName();
            String o_testBaiduVariant_add50197__10 = doc.select("title").outerHtml();
            String o_testBaiduVariant_add50197__12 = doc.select("title").outerHtml();
            org.junit.Assert.fail("testBaiduVariant_add50197_literalMutationString50495 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_add50193_literalMutationString50538_failAssert0_literalMutationString52906_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document o_testBaiduVariant_add50193__4 = Jsoup.parse(in, null, "httep://www.baidu.com/");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                String o_testBaiduVariant_add50193__8 = doc.outputSettings().charset().displayName();
                String o_testBaiduVariant_add50193__11 = doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_add50193_literalMutationString50538 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_add50193_literalMutationString50538_failAssert0_literalMutationString52906 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50175_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
            doc.outputSettings().charset().displayName();
            doc.select("title").outerHtml();
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50175 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50191_literalMutationString50390_literalMutationString52661_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.badu.com/");
            String o_testBaiduVariant_literalMutationString50191__7 = doc.outputSettings().charset().displayName();
            String o_testBaiduVariant_literalMutationString50191__10 = doc.select("ttle").outerHtml();
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50191_literalMutationString50390_literalMutationString52661 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_add50193_literalMutationString50538_failAssert0_add55376_failAssert0() throws IOException {
        try {
            {
                AmplParseTest.getFile("");
                File in = AmplParseTest.getFile("");
                Document o_testBaiduVariant_add50193__4 = Jsoup.parse(in, null, "http://www.baidu.com/");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                String o_testBaiduVariant_add50193__8 = doc.outputSettings().charset().displayName();
                String o_testBaiduVariant_add50193__11 = doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_add50193_literalMutationString50538 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_add50193_literalMutationString50538_failAssert0_add55376 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50175_failAssert0_add50965_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50175 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50175_failAssert0_add50965 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50188_failAssert0_literalMutationString50604_failAssert0null56927_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, null);
                    doc.outputSettings().charset().displayName();
                    doc.select("(SvK(").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString50188 should have thrown Selector$SelectorParseException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50188_failAssert0_literalMutationString50604 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50188_failAssert0_literalMutationString50604_failAssert0null56927 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50182_literalMutationString50295_failAssert0null56987_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "o[$3I51MivI#&aM}mhf#(");
                String o_testBaiduVariant_literalMutationString50182__7 = doc.outputSettings().charset().displayName();
                String o_testBaiduVariant_literalMutationString50182__10 = doc.select(null).outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50182_literalMutationString50295 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50182_literalMutationString50295_failAssert0null56987 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50175_failAssert0_add50966_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("title");
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50175 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50175_failAssert0_add50966 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50175_failAssert0null51110_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select(null).outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50175 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50175_failAssert0null51110 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50175_failAssert0_literalMutationString50679_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "application/x-www-form-urlencoded; charset=UTF-8");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50175 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50175_failAssert0_literalMutationString50679 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50175_failAssert0_literalMutationString50678_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50175 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50175_failAssert0_literalMutationString50678 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50188_failAssert0_literalMutationString50604_failAssert0_add55817_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                    doc.outputSettings().charset().displayName();
                    doc.select("(SvK(");
                    doc.select("(SvK(").outerHtml();
                    org.junit.Assert.fail("testBaiduVariant_literalMutationString50188 should have thrown Selector$SelectorParseException");
                }
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50188_failAssert0_literalMutationString50604 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50188_failAssert0_literalMutationString50604_failAssert0_add55817 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50183_literalMutationString50417_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "htt#://www.baidu.com/");
            String o_testBaiduVariant_literalMutationString50183__7 = doc.outputSettings().charset().displayName();
            String o_testBaiduVariant_literalMutationString50183__10 = doc.select("title").outerHtml();
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50183_literalMutationString50417 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50177_failAssert0_literalMutationString50705_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50177 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50177_failAssert0_literalMutationString50705 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_add50193_literalMutationString50538_failAssert0null56754_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document o_testBaiduVariant_add50193__4 = Jsoup.parse(in, null, "http://www.baidu.com/");
                Document doc = Jsoup.parse(in, null, null);
                String o_testBaiduVariant_add50193__8 = doc.outputSettings().charset().displayName();
                String o_testBaiduVariant_add50193__11 = doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_add50193_literalMutationString50538 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_add50193_literalMutationString50538_failAssert0null56754 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_add50198_literalMutationString50446_literalMutationString51677_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http:/L/www.baidu.com/");
            String o_testBaiduVariant_add50198__7 = doc.outputSettings().charset().displayName();
            Elements o_testBaiduVariant_add50198__10 = doc.select("title");
            String o_testBaiduVariant_add50198__11 = doc.select("title").outerHtml();
            org.junit.Assert.fail("testBaiduVariant_add50198_literalMutationString50446_literalMutationString51677 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString50182_literalMutationString50295_failAssert0_literalMutationString53854_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "o[$3I51MivI#&aM}mhf#(");
                String o_testBaiduVariant_literalMutationString50182__7 = doc.outputSettings().charset().displayName();
                String o_testBaiduVariant_literalMutationString50182__10 = doc.select("application/x-www-form-urlencoded; charset=UTF-8").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString50182_literalMutationString50295 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString50182_literalMutationString50295_failAssert0_literalMutationString53854 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_add50193_literalMutationString50538_failAssert0_literalMutationString52915_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document o_testBaiduVariant_add50193__4 = Jsoup.parse(in, null, "http://www.baidu.com/");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                String o_testBaiduVariant_add50193__8 = doc.outputSettings().charset().displayName();
                String o_testBaiduVariant_add50193__11 = doc.select("ti<le").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_add50193_literalMutationString50538 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_add50193_literalMutationString50538_failAssert0_literalMutationString52915 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100522_failAssert0_literalMutationString102639_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                doc.text();
                doc.outputSettings().charset().displayName();
                in = AmplParseTest.getFile("/htmltests/meta-chrset-2.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.outputSettings().charset().displayName();
                "?".equals(doc.text());
                in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100522 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100522_failAssert0_literalMutationString102639 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100505_failAssert0_add104187_failAssert0() throws IOException {
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
                "?".equals(doc.text());
                in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100505 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100505_failAssert0_add104187 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100505_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100505 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100522_failAssert0_literalMutationString102639_failAssert0_add113632_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.text();
                    doc.outputSettings().charset().displayName();
                    in = AmplParseTest.getFile("/htmltests/meta-chrset-2.html");
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
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString100522 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100522_failAssert0_literalMutationString102639 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100522_failAssert0_literalMutationString102639_failAssert0_add113632 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100532_failAssert0_add104245_failAssert0() throws IOException {
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
                "?".equals(doc.text());
                in = AmplParseTest.getFile("");
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100532 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100532_failAssert0_add104245 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100526null104824_failAssert0_literalMutationString109720_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                String o_testHtml5Charset_literalMutationString100526__7 = doc.text();
                String o_testHtml5Charset_literalMutationString100526__8 = doc.outputSettings().charset().displayName();
                in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
                doc = Jsoup.parse(in, null, "http://]xample.com");
                String o_testHtml5Charset_literalMutationString100526__18 = doc.outputSettings().charset().displayName();
                boolean o_testHtml5Charset_literalMutationString100526__21 = "?".equals(doc.text());
                in = AmplParseTest.getFile("");
                doc = Jsoup.parse(in, null, null);
                String o_testHtml5Charset_literalMutationString100526__29 = doc.outputSettings().charset().displayName();
                String o_testHtml5Charset_literalMutationString100526__32 = doc.outputSettings().charset().displayName();
                String o_testHtml5Charset_literalMutationString100526__35 = doc.text();
                String o_testHtml5Charset_literalMutationString100526__36 = doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100526null104824 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100526null104824_failAssert0_literalMutationString109720 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100522_failAssert0_literalMutationString102639_failAssert0_literalMutationString108035_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.text();
                    doc.outputSettings().charset().displayName();
                    in = AmplParseTest.getFile("/htmltests/meta-chrset-2.html");
                    doc = Jsoup.parse(in, null, "http://eample.com");
                    doc.outputSettings().charset().displayName();
                    "?".equals(doc.text());
                    in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                    doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.text();
                    doc.text();
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString100522 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100522_failAssert0_literalMutationString102639 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100522_failAssert0_literalMutationString102639_failAssert0_literalMutationString108035 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100505_failAssert0_add104187_failAssert0_add116392_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Jsoup.parse(in, null, "http://example.com/");
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
                    doc.outputSettings().charset().displayName();
                    doc.text();
                    doc.text();
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString100505 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100505_failAssert0_add104187 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100505_failAssert0_add104187_failAssert0_add116392 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100505_failAssert0_add104187_failAssert0_add116387_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Jsoup.parse(in, null, "http://example.com/");
                    Document doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.text();
                    doc.outputSettings().charset().displayName();
                    in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.outputSettings();
                    doc.outputSettings().charset().displayName();
                    "?".equals(doc.text());
                    in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                    doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.text();
                    doc.text();
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString100505 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100505_failAssert0_add104187 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100505_failAssert0_add104187_failAssert0_add116387 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100505_failAssert0_literalMutationString102429_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                doc.text();
                doc.outputSettings().charset().displayName();
                in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
                doc = Jsoup.parse(in, null, "application/x-www-form-urlencoded; charset=UTF-8");
                doc.outputSettings().charset().displayName();
                "?".equals(doc.text());
                in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100505 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100505_failAssert0_literalMutationString102429 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100532_failAssert0_add104245_failAssert0_add115772_failAssert0() throws IOException {
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
                    "?".equals(doc.text());
                    AmplParseTest.getFile("");
                    in = AmplParseTest.getFile("");
                    doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.text();
                    doc.text();
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString100532 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100532_failAssert0_add104245 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100532_failAssert0_add104245_failAssert0_add115772 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100505_failAssert0null105065_failAssert0() throws IOException {
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
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100505 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100505_failAssert0null105065 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100505_failAssert0_add104187_failAssert0_literalMutationString112055_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Jsoup.parse(in, null, "http://example.com/");
                    Document doc = Jsoup.parse(in, null, "http://xample.com/");
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
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString100505 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100505_failAssert0_add104187 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100505_failAssert0_add104187_failAssert0_literalMutationString112055 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100505_failAssert0null105062_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                doc.text();
                doc.outputSettings().charset().displayName();
                in = AmplParseTest.getFile(null);
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.outputSettings().charset().displayName();
                "?".equals(doc.text());
                in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100505 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100505_failAssert0null105062 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100522_failAssert0_literalMutationString102639_failAssert0_add113640_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.text();
                    doc.outputSettings().charset().displayName();
                    in = AmplParseTest.getFile("/htmltests/meta-chrset-2.html");
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
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString100522 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100522_failAssert0_literalMutationString102639 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100522_failAssert0_literalMutationString102639_failAssert0_add113640 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100532_failAssert0_add104245_failAssert0_literalMutationString111152_failAssert0() throws IOException {
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
                    "?".equals(doc.text());
                    in = AmplParseTest.getFile("");
                    doc = Jsoup.parse(in, null, "");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.text();
                    doc.text();
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString100532 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100532_failAssert0_add104245 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100532_failAssert0_add104245_failAssert0_literalMutationString111152 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100535_failAssert0_add104118_failAssert0_literalMutationString111313_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.text();
                    doc.outputSettings().charset();
                    doc.outputSettings().charset().displayName();
                    in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.outputSettings().charset().displayName();
                    "?".equals(doc.text());
                    in = AmplParseTest.getFile("/htmltests/meta-charset-3.htl");
                    doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.text();
                    doc.text();
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString100535 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100535_failAssert0_add104118 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100535_failAssert0_add104118_failAssert0_literalMutationString111313 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_add100545_literalMutationString101556_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document o_testHtml5Charset_add100545__4 = Jsoup.parse(in, null, "http://example.com/");
            Document doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_add100545__8 = doc.text();
            String o_testHtml5Charset_add100545__9 = doc.outputSettings().charset().displayName();
            in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            String o_testHtml5Charset_add100545__19 = doc.outputSettings().charset().displayName();
            boolean o_testHtml5Charset_add100545__22 = "?".equals(doc.text());
            in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
            doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_add100545__30 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_add100545__33 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_add100545__36 = doc.text();
            String o_testHtml5Charset_add100545__37 = doc.text();
            org.junit.Assert.fail("testHtml5Charset_add100545_literalMutationString101556 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100522_failAssert0_literalMutationString102639_failAssert0null117016_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, null);
                    doc.text();
                    doc.outputSettings().charset().displayName();
                    in = AmplParseTest.getFile("/htmltests/meta-chrset-2.html");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.outputSettings().charset().displayName();
                    "?".equals(doc.text());
                    in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                    doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.text();
                    doc.text();
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString100522 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100522_failAssert0_literalMutationString102639 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100522_failAssert0_literalMutationString102639_failAssert0null117016 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100505_failAssert0_literalMutationString102414_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "");
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
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100505 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100505_failAssert0_literalMutationString102414 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100521_failAssert0null105106_failAssert0_literalMutationString109429_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, null);
                    doc.text();
                    doc.outputSettings().charset().displayName();
                    in = AmplParseTest.getFile("/htmltests/meta-cha+set-2.html");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.outputSettings().charset().displayName();
                    "?".equals(doc.text());
                    in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                    doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.text();
                    doc.text();
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString100521 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100521_failAssert0null105106 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100521_failAssert0null105106_failAssert0_literalMutationString109429 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100522_failAssert0_literalMutationString102639_failAssert0_literalMutationString108026_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.text();
                    doc.outputSettings().charset().displayName();
                    in = AmplParseTest.getFile("`q`R8H%qk/vT3gZ},&1(R)d$=;HF@");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.outputSettings().charset().displayName();
                    "?".equals(doc.text());
                    in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                    doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.text();
                    doc.text();
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString100522 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100522_failAssert0_literalMutationString102639 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100522_failAssert0_literalMutationString102639_failAssert0_literalMutationString108026 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100532_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100532 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100522_failAssert0_literalMutationString102639_failAssert0null117020_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.text();
                    doc.outputSettings().charset().displayName();
                    in = AmplParseTest.getFile("/htmltests/meta-chrset-2.html");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.outputSettings().charset().displayName();
                    "?".equals(doc.text());
                    in = AmplParseTest.getFile(null);
                    doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.text();
                    doc.text();
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString100522 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100522_failAssert0_literalMutationString102639 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100522_failAssert0_literalMutationString102639_failAssert0null117020 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100505_failAssert0_add104195_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                doc.text();
                doc.outputSettings().charset().displayName();
                in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.outputSettings().charset();
                doc.outputSettings().charset().displayName();
                "?".equals(doc.text());
                in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100505 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100505_failAssert0_add104195 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100517_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100517 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString100505_failAssert0_add104187_failAssert0_literalMutationString112061_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Jsoup.parse(in, null, "http://example.com/");
                    Document doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.text();
                    doc.outputSettings().charset().displayName();
                    in = AmplParseTest.getFile("/htmltests/meta-char!et-2.html");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.outputSettings().charset().displayName();
                    "?".equals(doc.text());
                    in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                    doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.text();
                    doc.text();
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString100505 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString100505_failAssert0_add104187 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString100505_failAssert0_add104187_failAssert0_literalMutationString112061 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_add145280_add145853_literalMutationString146703_failAssert0() throws IOException {
        try {
            AmplParseTest.getFile("/htmltests/nyt-article-1.html");
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            Element headline = doc.select("nyt_headline[version=1.0]").first();
            String o_testNytArticle_add145280_add145853__10 = headline.text();
            String o_testNytArticle_add145280__10 = headline.text();
            org.junit.Assert.fail("testNytArticle_add145280_add145853_literalMutationString146703 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString145274_failAssert0_literalMutationString145662_failAssert0_literalMutationString147267_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.nWytimes.com/2010/07/26/business/global/26bp.html?hp");
                    Element headline = doc.select("").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString145274 should have thrown IllegalArgumentException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString145274_failAssert0_literalMutationString145662 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString145274_failAssert0_literalMutationString145662_failAssert0_literalMutationString147267 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString145274_failAssert0_literalMutationString145662_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString145274 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString145274_failAssert0_literalMutationString145662 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString145262_failAssert0null146088_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select(null).first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString145262 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString145262_failAssert0null146088 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString145277_failAssert0_literalMutationString145764_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("nyt_headl>ne[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString145277 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString145277_failAssert0_literalMutationString145764 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString145262_failAssert0_literalMutationString145763_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("s-5gW#vC[oc6poG>3FX?z>6>f").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString145262 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString145262_failAssert0_literalMutationString145763 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString145277_failAssert0_literalMutationString145764_failAssert0_add150316_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    doc.select("nyt_headl>ne[version=1.0]");
                    Element headline = doc.select("nyt_headl>ne[version=1.0]").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString145277 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString145277_failAssert0_literalMutationString145764 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString145277_failAssert0_literalMutationString145764_failAssert0_add150316 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString145279_failAssert0_literalMutationString145638_failAssert0_literalMutationString148530_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    Element headline = doc.select("yaQgfe4Z=+ZjXqg}B}[P1&}8").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString145279 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString145279_failAssert0_literalMutationString145638 should have thrown Selector$SelectorParseException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString145279_failAssert0_literalMutationString145638_failAssert0_literalMutationString148530 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString145262_failAssert0_literalMutationString145752_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytime.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString145262 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString145262_failAssert0_literalMutationString145752 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString145277_failAssert0_literalMutationString145764_failAssert0_literalMutationString148212_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    Element headline = doc.select("nyt_headl>ne[version=1.0]").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString145277 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString145277_failAssert0_literalMutationString145764 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString145277_failAssert0_literalMutationString145764_failAssert0_literalMutationString148212 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString145262_failAssert0_add145966_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString145262 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString145262_failAssert0_add145966 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString145270_literalMutationString145422_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "71`{()0 6Hs5[,o6!6:-|?{cw!dt!y7T1e}{>;P>i%|bgNSBPuAQB8]EvUA_b`");
            Element headline = doc.select("nyt_headline[version=1.0]").first();
            String o_testNytArticle_literalMutationString145270__9 = headline.text();
            org.junit.Assert.fail("testNytArticle_literalMutationString145270_literalMutationString145422 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString145262_failAssert0_add145968_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                doc.select("nyt_headline[version=1.0]");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString145262 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString145262_failAssert0_add145968 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString145274_failAssert0_literalMutationString145662_failAssert0_add149935_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    Element headline = doc.select("").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString145274 should have thrown IllegalArgumentException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString145274_failAssert0_literalMutationString145662 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString145274_failAssert0_literalMutationString145662_failAssert0_add149935 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString145262_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            Element headline = doc.select("nyt_headline[version=1.0]").first();
            headline.text();
            org.junit.Assert.fail("testNytArticle_literalMutationString145262 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString145262_failAssert0_add145970_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString145262 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString145262_failAssert0_add145970 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString145269_literalMutationString145405_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.nytimes.co8/2010/07/26/business/global/26bp.html?hp");
            Element headline = doc.select("nyt_headline[version=1.0]").first();
            String o_testNytArticle_literalMutationString145269__9 = headline.text();
            org.junit.Assert.fail("testNytArticle_literalMutationString145269_literalMutationString145405 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString145278_failAssert0_literalMutationString145696_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("application/x-www-form-urlencoded; charset=UTF-8").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString145278 should have thrown Selector$SelectorParseException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString145278_failAssert0_literalMutationString145696 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString145262_failAssert0null146088_failAssert0_add150745_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    doc.select(null);
                    Element headline = doc.select(null).first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString145262 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString145262_failAssert0null146088 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString145262_failAssert0null146088_failAssert0_add150745 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString27965_failAssert0_add28983_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                doc.select("p:contains(Volt will be sold in the United States)");
                Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
                p.text();
                org.junit.Assert.fail("testYahooArticle_literalMutationString27965 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString27965_failAssert0_add28983 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString27965_failAssert0_add28983_failAssert0_add35321_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                    doc.select("p:contains(Volt will be sold in the United States)");
                    doc.select("p:contains(Volt will be sold in the United States)").first();
                    Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
                    p.text();
                    org.junit.Assert.fail("testYahooArticle_literalMutationString27965 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testYahooArticle_literalMutationString27965_failAssert0_add28983 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString27965_failAssert0_add28983_failAssert0_add35321 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString27965_failAssert0_add28983_failAssert0_literalMutationString33755_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "application/x-www-form-urlencoded; charset=UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                    doc.select("p:contains(Volt will be sold in the United States)");
                    Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
                    p.text();
                    org.junit.Assert.fail("testYahooArticle_literalMutationString27965 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testYahooArticle_literalMutationString27965_failAssert0_add28983 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString27965_failAssert0_add28983_failAssert0_literalMutationString33755 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString27965_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
            Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
            p.text();
            org.junit.Assert.fail("testYahooArticle_literalMutationString27965 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString27965_failAssert0_add28983_failAssert0_add35324_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                    doc.select("p:contains(Volt will be sold in the United States)");
                    Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
                    p.text();
                    org.junit.Assert.fail("testYahooArticle_literalMutationString27965 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testYahooArticle_literalMutationString27965_failAssert0_add28983 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString27965_failAssert0_add28983_failAssert0_add35324 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString27965_failAssert0_literalMutationString28483_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
                p.text();
                org.junit.Assert.fail("testYahooArticle_literalMutationString27965 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString27965_failAssert0_literalMutationString28483 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString27965_failAssert0_literalMutationString28483_failAssert0_add35042_failAssert0() throws IOException {
        try {
            {
                {
                    AmplParseTest.getFile("");
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                    Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
                    p.text();
                    org.junit.Assert.fail("testYahooArticle_literalMutationString27965 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testYahooArticle_literalMutationString27965_failAssert0_literalMutationString28483 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString27965_failAssert0_literalMutationString28483_failAssert0_add35042 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString27965_failAssert0null29170_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
                p.text();
                org.junit.Assert.fail("testYahooArticle_literalMutationString27965 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString27965_failAssert0null29170 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString27974_failAssert0_literalMutationString28693_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "cJ-ur", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
                p.text();
                org.junit.Assert.fail("testYahooArticle_literalMutationString27974 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString27974_failAssert0_literalMutationString28693 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString27965_failAssert0_add28983_failAssert0_literalMutationString33775_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                    doc.select("p:contains(Volt will be sold in the United States)");
                    Element p = doc.select("p:contains(Volt will be sold in the UniteYd States)").first();
                    p.text();
                    org.junit.Assert.fail("testYahooArticle_literalMutationString27965 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testYahooArticle_literalMutationString27965_failAssert0_add28983 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString27965_failAssert0_add28983_failAssert0_literalMutationString33775 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString27988_failAssert0_literalMutationString28624_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                Element p = doc.select("p:contains(Volt will be sold in the United OStates)").first();
                p.text();
                org.junit.Assert.fail("testYahooArticle_literalMutationString27988 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString27988_failAssert0_literalMutationString28624 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString27965_failAssert0_literalMutationString28483_failAssert0_literalMutationString32839_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                    Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
                    p.text();
                    org.junit.Assert.fail("testYahooArticle_literalMutationString27965 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testYahooArticle_literalMutationString27965_failAssert0_literalMutationString28483 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString27965_failAssert0_literalMutationString28483_failAssert0_literalMutationString32839 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString27965_failAssert0_add28984_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
                p.text();
                p.text();
                org.junit.Assert.fail("testYahooArticle_literalMutationString27965 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString27965_failAssert0_add28984 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_add74721_literalMutationString74787_failAssert0null79912_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element o_testLowercaseUtf8Charset_add74721__5 = doc.select("#form").first();
                Element form = doc.select(null).first();
                int o_testLowercaseUtf8Charset_add74721__10 = form.children().size();
                String o_testLowercaseUtf8Charset_add74721__12 = doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_add74721_literalMutationString74787 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_add74721_literalMutationString74787_failAssert0null79912 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString74707_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null);
            Element form = doc.select("#form").first();
            form.children().size();
            doc.outputSettings().charset().name();
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74707 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_add74721_literalMutationString74787_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null);
            Element o_testLowercaseUtf8Charset_add74721__5 = doc.select("#form").first();
            Element form = doc.select("#form").first();
            int o_testLowercaseUtf8Charset_add74721__10 = form.children().size();
            String o_testLowercaseUtf8Charset_add74721__12 = doc.outputSettings().charset().name();
            org.junit.Assert.fail("testLowercaseUtf8Charset_add74721_literalMutationString74787 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_add74721_literalMutationString74787_failAssert0_add78233_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element o_testLowercaseUtf8Charset_add74721__5 = doc.select("#form").first();
                Element form = doc.select("#form").first();
                int o_testLowercaseUtf8Charset_add74721__10 = form.children().size();
                doc.outputSettings();
                String o_testLowercaseUtf8Charset_add74721__12 = doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_add74721_literalMutationString74787 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_add74721_literalMutationString74787_failAssert0_add78233 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charsetnull74730_failAssert0_add75297_failAssert0_literalMutationString77035_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select(null).first();
                    form.children().size();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charsetnull74730 should have thrown IllegalArgumentException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charsetnull74730_failAssert0_add75297 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charsetnull74730_failAssert0_add75297_failAssert0_literalMutationString77035 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString74711_failAssert0null75359_failAssert0_literalMutationString76813_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select(null).first();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74711 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74711_failAssert0null75359 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74711_failAssert0null75359_failAssert0_literalMutationString76813 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charsetnull74730_failAssert0_add75302_failAssert0_literalMutationString77644_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select(null).first();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charsetnull74730 should have thrown IllegalArgumentException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charsetnull74730_failAssert0_add75302 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charsetnull74730_failAssert0_add75302_failAssert0_literalMutationString77644 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString74707_failAssert0_literalMutationString74973_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74707 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74707_failAssert0_literalMutationString74973 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString74707_failAssert0_literalMutationString74976_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("application/x-www-form-urlencoded; charset=UTF-8").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74707 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74707_failAssert0_literalMutationString74976 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString74710_failAssert0_literalMutationString74948_failAssert0_literalMutationString76481_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select("Qfr{l").first();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74710 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74710_failAssert0_literalMutationString74948 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74710_failAssert0_literalMutationString74948_failAssert0_literalMutationString76481 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString74717_failAssert0_literalMutationString74958_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("}CdBN").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74717 should have thrown Selector$SelectorParseException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74717_failAssert0_literalMutationString74958 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charsetnull74730_failAssert0_add75293_failAssert0_literalMutationString77029_failAssert0() throws IOException {
        try {
            {
                {
                    AmplParseTest.getFile("/htmltests/lowercase-charset-test.html");
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select(null).first();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charsetnull74730 should have thrown IllegalArgumentException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charsetnull74730_failAssert0_add75293 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charsetnull74730_failAssert0_add75293_failAssert0_literalMutationString77029 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charsetnull74730_failAssert0_literalMutationString75045_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select(null).first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charsetnull74730 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charsetnull74730_failAssert0_literalMutationString75045 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString74707_failAssert0null75353_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select(null).first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74707 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74707_failAssert0null75353 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString74709_failAssert0_literalMutationString75004_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74709 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74709_failAssert0_literalMutationString75004 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString74707_failAssert0_add75229_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                form.children().size();
                doc.outputSettings().charset().name();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74707 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74707_failAssert0_add75229 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_add74727_add75158_literalMutationString75973_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null);
            Element form = doc.select("#form").first();
            Elements o_testLowercaseUtf8Charset_add74727_add75158__8 = form.children();
            int o_testLowercaseUtf8Charset_add74727__8 = form.children().size();
            doc.outputSettings();
            String o_testLowercaseUtf8Charset_add74727__11 = doc.outputSettings().charset().name();
            org.junit.Assert.fail("testLowercaseUtf8Charset_add74727_add75158_literalMutationString75973 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_add74721_literalMutationString74787_failAssert0_literalMutationString76327_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element o_testLowercaseUtf8Charset_add74721__5 = doc.select("#form").first();
                Element form = doc.select("").first();
                int o_testLowercaseUtf8Charset_add74721__10 = form.children().size();
                String o_testLowercaseUtf8Charset_add74721__12 = doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_add74721_literalMutationString74787 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_add74721_literalMutationString74787_failAssert0_literalMutationString76327 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString74707_failAssert0_add75227_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                form.children().size();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74707 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74707_failAssert0_add75227 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString74707_failAssert0_add75226_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                doc.select("#form");
                Element form = doc.select("#form").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74707 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString74707_failAssert0_add75226 should have thrown FileNotFoundException");
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

