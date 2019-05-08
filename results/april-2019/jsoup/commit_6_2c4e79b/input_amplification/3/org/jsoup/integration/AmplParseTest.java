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
    public void testSmhBizArticle_literalMutationString23329_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
            doc.title();
            doc.select("html").attr("xml:lang");
            Elements articleBody = doc.select(".articleBody > *");
            articleBody.size();

            org.junit.Assert.fail("testSmhBizArticle_literalMutationString23329 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNewsHomepage_literalMutationString59_literalMutationString4098_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.news.com.au/");
            String o_testNewsHomepage_literalMutationString59__5 = doc.title();
            String o_testNewsHomepage_literalMutationString59__6 = doc.select(".id1225817868581 h4").text().trim();
            Element a = doc.select("a[href=/entertainment/horoscopes]").first();
            a.attr("href");
            String o_testNewsHomepage_literalMutationString59__13 = a.attr("abs:href");
            Element hs = doc.select("a[href*=naughty-corners-are-a-bad-idea]").first();
            String o_testNewsHomepage_literalMutationString59__17 = hs.attr("href");
            String o_testNewsHomepage_literalMutationString59__18 = hs.attr("hr,ef");
            String o_testNewsHomepage_literalMutationString59__19 = hs.attr("abs:href");
            org.junit.Assert.fail("testNewsHomepage_literalMutationString59_literalMutationString4098 should have thrown FileNotFoundException");
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
    public void testNewsHomepage_literalMutationString17_literalMutationString601_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", " -D%jZ/&tFt5<NjiZVgAMM}");
            String o_testNewsHomepage_literalMutationString17__5 = doc.title();
            String o_testNewsHomepage_literalMutationString17__6 = doc.select(".id1225817868581 h4").text().trim();
            Element a = doc.select("a[href=/entertainment/horoscopes]").first();
            a.attr("href");
            String o_testNewsHomepage_literalMutationString17__13 = a.attr("abs:href");
            Element hs = doc.select("a[href*=naughty-corners-are-a-bad-idea]").first();
            String o_testNewsHomepage_literalMutationString17__17 = hs.attr("href");
            String o_testNewsHomepage_literalMutationString17__18 = hs.attr("href");
            String o_testNewsHomepage_literalMutationString17__19 = hs.attr("abs:href");
            org.junit.Assert.fail("testNewsHomepage_literalMutationString17_literalMutationString601 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationString35453_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
            doc.title();
            Elements results = doc.select("h3.r > a");
            results.size();
            results.get(0).attr("href");
            results.get(1).attr("href");
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString35453 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationString35453_failAssert0null39383_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
                doc.title();
                Elements results = doc.select("h3.r > a");
                results.size();
                results.get(0).attr("href");
                results.get(1).attr(null);
                org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString35453 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString35453_failAssert0null39383 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationString35453_failAssert0null39383_failAssert0_add44969_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
                    doc.title();
                    doc.select("h3.r > a");
                    Elements results = doc.select("h3.r > a");
                    results.size();
                    results.get(0).attr("href");
                    results.get(1).attr(null);
                    org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString35453 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString35453_failAssert0null39383 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString35453_failAssert0null39383_failAssert0_add44969 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationString35453_failAssert0_literalMutationString37474_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.hom/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
                doc.title();
                Elements results = doc.select("h3.r > a");
                results.size();
                results.get(0).attr("href");
                results.get(1).attr("href");
                org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString35453 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString35453_failAssert0_literalMutationString37474 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationString35453_failAssert0_add38878_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
                doc.title();
                Elements results = doc.select("h3.r > a");
                results.size();
                results.size();
                results.get(0).attr("href");
                results.get(1).attr("href");
                org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString35453 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString35453_failAssert0_add38878 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationString35453_failAssert0_literalMutationString37474_failAssert0_literalMutationString41606_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.hom/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
                    doc.title();
                    Elements results = doc.select("h3.r > a");
                    results.size();
                    results.get(0).attr("href");
                    results.get(1).attr("h!ref");
                    org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString35453 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString35453_failAssert0_literalMutationString37474 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString35453_failAssert0_literalMutationString37474_failAssert0_literalMutationString41606 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_add35499_literalMutationString36817_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document o_testGoogleSearchIpod_add35499__3 = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
            String o_testGoogleSearchIpod_add35499__6 = doc.title();
            Elements results = doc.select("h3.r > a");
            int o_testGoogleSearchIpod_add35499__9 = results.size();
            String o_testGoogleSearchIpod_add35499__10 = results.get(0).attr("href");
            String o_testGoogleSearchIpod_add35499__12 = results.get(1).attr("href");
            org.junit.Assert.fail("testGoogleSearchIpod_add35499_literalMutationString36817 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationString35453_failAssert0null39383_failAssert0_literalMutationNumber43158_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
                    doc.title();
                    Elements results = doc.select("h3.r > a");
                    results.size();
                    results.get(0).attr("href");
                    results.get(1).attr(null);
                    org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString35453 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString35453_failAssert0null39383 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString35453_failAssert0null39383_failAssert0_literalMutationNumber43158 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString94131_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8");
            doc.text().contains("gd-jpeg");
            org.junit.Assert.fail("testBinary_literalMutationString94131 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString94145_literalMutationString94238_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8");
            boolean o_testBinary_literalMutationString94145__5 = doc.text().contains("gd-jeg");
            org.junit.Assert.fail("testBinary_literalMutationString94145_literalMutationString94238 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString94131_failAssert0_literalMutationString94474_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.text().contains("gd-jpAeg");
                org.junit.Assert.fail("testBinary_literalMutationString94131 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString94131_failAssert0_literalMutationString94474 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString94140_failAssert0_add94723_failAssert0_literalMutationString95775_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "=TF-8");
                    doc.text().contains("gd-jpeg");
                    doc.text().contains("gd-jpeg");
                    org.junit.Assert.fail("testBinary_literalMutationString94140 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("testBinary_literalMutationString94140_failAssert0_add94723 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString94140_failAssert0_add94723_failAssert0_literalMutationString95775 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString94131_failAssert0_add94735_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.text().contains("gd-jpeg");
                org.junit.Assert.fail("testBinary_literalMutationString94131 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString94131_failAssert0_add94735 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString49358_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
            doc.title();
            Element a = doc.select("a[href=t/2322m2]").first();
            a.attr("abs:href");
            a.text();
            org.junit.Assert.fail("testYahooJp_literalMutationString49358 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString76169_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testBaidu_literalMutationString76169 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString31168_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
            doc.outputSettings().charset().displayName();
            doc.select("title").outerHtml();
            org.junit.Assert.fail("testBaiduVariant_literalMutationString31168 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_add31187_literalMutationString31471_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
            String o_testBaiduVariant_add31187__7 = doc.outputSettings().charset().displayName();
            String o_testBaiduVariant_add31187__10 = doc.outputSettings().charset().displayName();
            String o_testBaiduVariant_add31187__13 = doc.select("title").outerHtml();
            org.junit.Assert.fail("testBaiduVariant_add31187_literalMutationString31471 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString61400_literalMutationString62994_failAssert0_literalMutationString69200_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                String o_testHtml5Charset_literalMutationString61400__7 = doc.text();
                String o_testHtml5Charset_literalMutationString61400__8 = doc.outputSettings().charset().displayName();
                in = AmplParseTest.getFile("");
                doc = Jsoup.parse(in, null, "");
                String o_testHtml5Charset_literalMutationString61400__18 = doc.outputSettings().charset().displayName();
                boolean o_testHtml5Charset_literalMutationString61400__21 = "新".equals(doc.text());
                in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                doc = Jsoup.parse(in, null, "httf://example.com/");
                String o_testHtml5Charset_literalMutationString61400__29 = doc.outputSettings().charset().displayName();
                String o_testHtml5Charset_literalMutationString61400__32 = doc.outputSettings().charset().displayName();
                String o_testHtml5Charset_literalMutationString61400__35 = doc.text();
                String o_testHtml5Charset_literalMutationString61400__36 = doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString61400_literalMutationString62994 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString61400_literalMutationString62994_failAssert0_literalMutationString69200 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString61400_literalMutationString62994_failAssert0_add71278_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                String o_testHtml5Charset_literalMutationString61400__7 = doc.text();
                String o_testHtml5Charset_literalMutationString61400__8 = doc.outputSettings().charset().displayName();
                in = AmplParseTest.getFile("");
                doc = Jsoup.parse(in, null, "");
                String o_testHtml5Charset_literalMutationString61400__18 = doc.outputSettings().charset().displayName();
                boolean o_testHtml5Charset_literalMutationString61400__21 = "新".equals(doc.text());
                in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                doc = Jsoup.parse(in, null, "http://example.com/");
                String o_testHtml5Charset_literalMutationString61400__29 = doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                String o_testHtml5Charset_literalMutationString61400__32 = doc.outputSettings().charset().displayName();
                String o_testHtml5Charset_literalMutationString61400__35 = doc.text();
                String o_testHtml5Charset_literalMutationString61400__36 = doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString61400_literalMutationString62994 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString61400_literalMutationString62994_failAssert0_add71278 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString61400_literalMutationString62994_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
            Document doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_literalMutationString61400__7 = doc.text();
            String o_testHtml5Charset_literalMutationString61400__8 = doc.outputSettings().charset().displayName();
            in = AmplParseTest.getFile("");
            doc = Jsoup.parse(in, null, "");
            String o_testHtml5Charset_literalMutationString61400__18 = doc.outputSettings().charset().displayName();
            boolean o_testHtml5Charset_literalMutationString61400__21 = "新".equals(doc.text());
            in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
            doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_literalMutationString61400__29 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_literalMutationString61400__32 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_literalMutationString61400__35 = doc.text();
            String o_testHtml5Charset_literalMutationString61400__36 = doc.text();
            org.junit.Assert.fail("testHtml5Charset_literalMutationString61400_literalMutationString62994 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString61384_failAssert0_literalMutationString63868_failAssert0() throws IOException {
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
                doc = Jsoup.parse(in, null, "http://ex-mple.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString61384 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString61384_failAssert0_literalMutationString63868 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charsetnull61448_failAssert0_literalMutationString63601_failAssert0_literalMutationString67808_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://example.cTom/");
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
                    org.junit.Assert.fail("testHtml5Charsetnull61448 should have thrown IllegalArgumentException");
                }
                org.junit.Assert.fail("testHtml5Charsetnull61448_failAssert0_literalMutationString63601 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testHtml5Charsetnull61448_failAssert0_literalMutationString63601_failAssert0_literalMutationString67808 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString61384_failAssert0_literalMutationString63868_failAssert0_add70065_failAssert0() throws IOException {
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
                    "新".equals(doc.text());
                    in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                    doc = Jsoup.parse(in, null, "http://ex-mple.com/");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.text();
                    doc.text();
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString61384 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString61384_failAssert0_literalMutationString63868 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString61384_failAssert0_literalMutationString63868_failAssert0_add70065 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString61384_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testHtml5Charset_literalMutationString61384 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString61394_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testHtml5Charset_literalMutationString61394 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString61394_failAssert0_literalMutationString63350_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                doc.text();
                doc.outputSettings().charset().displayName();
                in = AmplParseTest.getFile("");
                doc = Jsoup.parse(in, null, "http://)example.com");
                doc.outputSettings().charset().displayName();
                "新".equals(doc.text());
                in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString61394 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString61394_failAssert0_literalMutationString63350 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString61397_failAssert0_add65020_failAssert0_literalMutationString68127_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.text();
                    doc.outputSettings();
                    doc.outputSettings().charset().displayName();
                    in = AmplParseTest.getFile("/htmlests/meta-charset-2.html");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.outputSettings().charset().displayName();
                    "新".equals(doc.text());
                    in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                    doc = Jsoup.parse(in, null, "http://example.com/");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.text();
                    doc.text();
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString61397 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString61397_failAssert0_add65020 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString61397_failAssert0_add65020_failAssert0_literalMutationString68127 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString61394_failAssert0_add65090_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
                Document doc = Jsoup.parse(in, null, "http://example.com/");
                doc.text();
                doc.outputSettings().charset().displayName();
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
                org.junit.Assert.fail("testHtml5Charset_literalMutationString61394 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString61394_failAssert0_add65090 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString61384_failAssert0_literalMutationString63868_failAssert0_literalMutationString67399_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://exampe.com/");
                    doc.text();
                    doc.outputSettings().charset().displayName();
                    in = AmplParseTest.getFile("/htmltests/meta-charset-2.html");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.outputSettings().charset().displayName();
                    "新".equals(doc.text());
                    in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
                    doc = Jsoup.parse(in, null, "http://ex-mple.com/");
                    doc.outputSettings().charset().displayName();
                    doc.outputSettings().charset().displayName();
                    doc.text();
                    doc.text();
                    org.junit.Assert.fail("testHtml5Charset_literalMutationString61384 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testHtml5Charset_literalMutationString61384_failAssert0_literalMutationString63868 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString61384_failAssert0_literalMutationString63868_failAssert0_literalMutationString67399 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString61391_literalMutationString61681_literalMutationString66375_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("/htmltests/meta-charset-1.html");
            Document doc = Jsoup.parse(in, null, "http://example.zom/");
            String o_testHtml5Charset_literalMutationString61391__7 = doc.text();
            String o_testHtml5Charset_literalMutationString61391__8 = doc.outputSettings().charset().displayName();
            in = AmplParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://examle.com");
            String o_testHtml5Charset_literalMutationString61391__18 = doc.outputSettings().charset().displayName();
            boolean o_testHtml5Charset_literalMutationString61391__21 = "新".equals(doc.text());
            in = AmplParseTest.getFile("/htmltests/meta-charset-3.html");
            doc = Jsoup.parse(in, null, "http://example.com/");
            String o_testHtml5Charset_literalMutationString61391__29 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_literalMutationString61391__32 = doc.outputSettings().charset().displayName();
            String o_testHtml5Charset_literalMutationString61391__35 = doc.text();
            String o_testHtml5Charset_literalMutationString61391__36 = doc.text();
            org.junit.Assert.fail("testHtml5Charset_literalMutationString61391_literalMutationString61681_literalMutationString66375 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString61384_failAssert0_add65445_failAssert0() throws IOException {
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
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString61384 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString61384_failAssert0_add65445 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString61409_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testHtml5Charset_literalMutationString61409 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString72382_failAssert0null73165_failAssert0_literalMutationString74847_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    Element headline = doc.select(null).first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString72382 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString72382_failAssert0null73165 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString72382_failAssert0null73165_failAssert0_literalMutationString74847 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString72368_failAssert0_literalMutationString72884_failAssert0_literalMutationString73975_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    Element headline = doc.select("Jay").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString72368 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString72368_failAssert0_literalMutationString72884 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString72368_failAssert0_literalMutationString72884_failAssert0_literalMutationString73975 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString72380_failAssert0null73184_failAssert0_literalMutationString74448_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, null);
                    Element headline = doc.select("").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString72380 should have thrown IllegalArgumentException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString72380_failAssert0null73184 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString72380_failAssert0null73184_failAssert0_literalMutationString74448 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString72368_failAssert0_literalMutationString72884_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("nyt_headl4ine[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString72368 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString72368_failAssert0_literalMutationString72884 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString72368_failAssert0_literalMutationString72884_failAssert0_add75198_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    doc.select("nyt_headl4ine[version=1.0]").first();
                    Element headline = doc.select("nyt_headl4ine[version=1.0]").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString72368 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString72368_failAssert0_literalMutationString72884 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString72368_failAssert0_literalMutationString72884_failAssert0_add75198 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString72368_failAssert0_add73079_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                doc.select("nyt_headline[version=1.0]");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString72368 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString72368_failAssert0_add73079 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString72380_failAssert0_literalMutationString72821_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString72380 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString72380_failAssert0_literalMutationString72821 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString72368_failAssert0_literalMutationString72884_failAssert0null75732_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, null);
                    Element headline = doc.select("nyt_headl4ine[version=1.0]").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString72368 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString72368_failAssert0_literalMutationString72884 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString72368_failAssert0_literalMutationString72884_failAssert0null75732 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString72368_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            Element headline = doc.select("nyt_headline[version=1.0]").first();
            headline.text();
            org.junit.Assert.fail("testNytArticle_literalMutationString72368 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString72368_failAssert0_add73079_failAssert0_add75321_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    doc.select("nyt_headline[version=1.0]");
                    doc.select("nyt_headline[version=1.0]");
                    Element headline = doc.select("nyt_headline[version=1.0]").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString72368 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString72368_failAssert0_add73079 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString72368_failAssert0_add73079_failAssert0_add75321 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString72368_failAssert0_add73079_failAssert0_literalMutationString74313_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    doc.select("nyt_headline[version=1.0]");
                    Element headline = doc.select("nyt_headline[version=1.0]").first();
                    headline.text();
                    org.junit.Assert.fail("testNytArticle_literalMutationString72368 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNytArticle_literalMutationString72368_failAssert0_add73079 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString72368_failAssert0_add73079_failAssert0_literalMutationString74313 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString72369_failAssert0_literalMutationString72785_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString72369 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString72369_failAssert0_literalMutationString72785 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString18537_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
            Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
            p.text();
            org.junit.Assert.fail("testYahooArticle_literalMutationString18537 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString18537_failAssert0_literalMutationString19188_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                Element p = doc.select("").first();
                p.text();
                org.junit.Assert.fail("testYahooArticle_literalMutationString18537 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString18537_failAssert0_literalMutationString19188 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString18537_failAssert0_add19582_failAssert0_literalMutationString21792_failAssert0() throws IOException {
        try {
            {
                {
                    AmplParseTest.getFile("");
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTc-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                    Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
                    p.text();
                    org.junit.Assert.fail("testYahooArticle_literalMutationString18537 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testYahooArticle_literalMutationString18537_failAssert0_add19582 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString18537_failAssert0_add19582_failAssert0_literalMutationString21792 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString18537_failAssert0_literalMutationString19188_failAssert0_literalMutationString21636_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                    Element p = doc.select("").first();
                    p.text();
                    org.junit.Assert.fail("testYahooArticle_literalMutationString18537 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testYahooArticle_literalMutationString18537_failAssert0_literalMutationString19188 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString18537_failAssert0_literalMutationString19188_failAssert0_literalMutationString21636 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString18538_failAssert0_literalMutationString19149_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
                p.text();
                org.junit.Assert.fail("testYahooArticle_literalMutationString18538 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString18538_failAssert0_literalMutationString19149 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString18537_failAssert0_literalMutationString19188_failAssert0_add22550_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                    Element p = doc.select("").first();
                    p.text();
                    p.text();
                    org.junit.Assert.fail("testYahooArticle_literalMutationString18537 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testYahooArticle_literalMutationString18537_failAssert0_literalMutationString19188 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString18537_failAssert0_literalMutationString19188_failAssert0_add22550 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString18537_failAssert0_add19585_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                doc.select("p:contains(Volt will be sold in the United States)");
                Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
                p.text();
                org.junit.Assert.fail("testYahooArticle_literalMutationString18537 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString18537_failAssert0_add19585 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString18537_failAssert0_add19582_failAssert0() throws IOException {
        try {
            {
                AmplParseTest.getFile("");
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
                p.text();
                org.junit.Assert.fail("testYahooArticle_literalMutationString18537 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString18537_failAssert0_add19582 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString18537_failAssert0_literalMutationString19191_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                Element p = doc.select("p:contains(Volt will be Lold in the United States)").first();
                p.text();
                org.junit.Assert.fail("testYahooArticle_literalMutationString18537 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString18537_failAssert0_literalMutationString19191 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString18537_failAssert0null19767_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
                p.text();
                org.junit.Assert.fail("testYahooArticle_literalMutationString18537 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString18537_failAssert0null19767 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString46113_failAssert0_literalMutationString46350_failAssert0_literalMutationString47623_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select("#form").first();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString46113 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString46113_failAssert0_literalMutationString46350 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString46113_failAssert0_literalMutationString46350_failAssert0_literalMutationString47623 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString46110_failAssert0null46752_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select(null).first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString46110 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString46110_failAssert0null46752 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_add46127_literalMutationString46202_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null);
            Element form = doc.select("#form").first();
            int o_testLowercaseUtf8Charset_add46127__8 = form.children().size();
            String o_testLowercaseUtf8Charset_add46127__10 = doc.outputSettings().charset().name();
            String o_testLowercaseUtf8Charset_add46127__13 = doc.outputSettings().charset().name();
            org.junit.Assert.fail("testLowercaseUtf8Charset_add46127_literalMutationString46202 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_add46127_literalMutationString46202_failAssert0_literalMutationString47257_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("").first();
                int o_testLowercaseUtf8Charset_add46127__8 = form.children().size();
                String o_testLowercaseUtf8Charset_add46127__10 = doc.outputSettings().charset().name();
                String o_testLowercaseUtf8Charset_add46127__13 = doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_add46127_literalMutationString46202 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_add46127_literalMutationString46202_failAssert0_literalMutationString47257 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString46110_failAssert0_add46619_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                form.children().size();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString46110 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString46110_failAssert0_add46619 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString46110_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null);
            Element form = doc.select("#form").first();
            form.children().size();
            doc.outputSettings().charset().name();
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString46110 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString46113_failAssert0_literalMutationString46353_failAssert0_literalMutationString47143_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select("#form").first();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString46113 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString46113_failAssert0_literalMutationString46353 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString46113_failAssert0_literalMutationString46353_failAssert0_literalMutationString47143 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString46110_failAssert0_add46619_failAssert0_literalMutationString47780_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select("#fZorm").first();
                    form.children().size();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString46110 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString46110_failAssert0_add46619 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString46110_failAssert0_add46619_failAssert0_literalMutationString47780 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString46115_failAssert0_literalMutationString46327_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#fom").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString46115 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString46115_failAssert0_literalMutationString46327 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString46110_failAssert0_add46619_failAssert0_add48732_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select("#form").first();
                    form.children().size();
                    form.children().size();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString46110 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString46110_failAssert0_add46619 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString46110_failAssert0_add46619_failAssert0_add48732 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_add46127_literalMutationString46202_failAssert0_literalMutationString47261_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("*%EC`").first();
                int o_testLowercaseUtf8Charset_add46127__8 = form.children().size();
                String o_testLowercaseUtf8Charset_add46127__10 = doc.outputSettings().charset().name();
                String o_testLowercaseUtf8Charset_add46127__13 = doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_add46127_literalMutationString46202 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_add46127_literalMutationString46202_failAssert0_literalMutationString47261 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString46110_failAssert0null46752_failAssert0_add48827_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select(null).first();
                    form.children().size();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString46110 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString46110_failAssert0null46752 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString46110_failAssert0null46752_failAssert0_add48827 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_add46127_literalMutationString46202_failAssert0_add48176_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                form.children().size();
                int o_testLowercaseUtf8Charset_add46127__8 = form.children().size();
                String o_testLowercaseUtf8Charset_add46127__10 = doc.outputSettings().charset().name();
                String o_testLowercaseUtf8Charset_add46127__13 = doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_add46127_literalMutationString46202 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_add46127_literalMutationString46202_failAssert0_add48176 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_add46127_literalMutationString46202_failAssert0_add48172_failAssert0() throws IOException {
        try {
            {
                AmplParseTest.getFile("");
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                int o_testLowercaseUtf8Charset_add46127__8 = form.children().size();
                String o_testLowercaseUtf8Charset_add46127__10 = doc.outputSettings().charset().name();
                String o_testLowercaseUtf8Charset_add46127__13 = doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_add46127_literalMutationString46202 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_add46127_literalMutationString46202_failAssert0_add48172 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString46110_failAssert0_literalMutationString46368_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#frm").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString46110 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString46110_failAssert0_literalMutationString46368 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString46110_failAssert0null46752_failAssert0_add48828_failAssert0() throws IOException {
        try {
            {
                {
                    File in = AmplParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    Element form = doc.select(null).first();
                    form.children();
                    form.children().size();
                    doc.outputSettings().charset().name();
                    org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString46110 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString46110_failAssert0null46752 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString46110_failAssert0null46752_failAssert0_add48828 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_add46125_literalMutationString46189_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null);
            Element form = doc.select("#form").first();
            int o_testLowercaseUtf8Charset_add46125__8 = form.children().size();
            int o_testLowercaseUtf8Charset_add46125__10 = form.children().size();
            String o_testLowercaseUtf8Charset_add46125__12 = doc.outputSettings().charset().name();
            org.junit.Assert.fail("testLowercaseUtf8Charset_add46125_literalMutationString46189 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString46110_failAssert0_add46624_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString46110 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString46110_failAssert0_add46624 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_add46127_literalMutationString46202_failAssert0null48976_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select(null).first();
                int o_testLowercaseUtf8Charset_add46127__8 = form.children().size();
                String o_testLowercaseUtf8Charset_add46127__10 = doc.outputSettings().charset().name();
                String o_testLowercaseUtf8Charset_add46127__13 = doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_add46127_literalMutationString46202 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_add46127_literalMutationString46202_failAssert0null48976 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_add46129_literalMutationString46256_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null);
            Element form = doc.select("#form").first();
            int o_testLowercaseUtf8Charset_add46129__8 = form.children().size();
            doc.outputSettings();
            String o_testLowercaseUtf8Charset_add46129__11 = doc.outputSettings().charset().name();
            org.junit.Assert.fail("testLowercaseUtf8Charset_add46129_literalMutationString46256 should have thrown FileNotFoundException");
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

