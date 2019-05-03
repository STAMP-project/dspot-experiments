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
    public void testSmhBizArticle_literalMutationString11221_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
            doc.title();
            doc.select("html").attr("xml:lang");
            Elements articleBody = doc.select(".articleBody > *");
            articleBody.size();

            org.junit.Assert.fail("testSmhBizArticle_literalMutationString11221 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSmhBizArticlenull11266_literalMutationString11397_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
            String o_testSmhBizArticlenull11266__5 = doc.title();
            String o_testSmhBizArticlenull11266__6 = doc.select("html").attr("xml:lang");
            Elements articleBody = doc.select(".articleBody > *");
            int o_testSmhBizArticlenull11266__10 = articleBody.size();

            org.junit.Assert.fail("testSmhBizArticlenull11266_literalMutationString11397 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSmhBizArticle_literalMutationString11221_failAssert0_literalMutationString12651_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
                doc.title();
                doc.select("html").attr("xml:lang");
                Elements articleBody = doc.select("s7m,]V`f/,z(aS$ ");
                articleBody.size();

                org.junit.Assert.fail("testSmhBizArticle_literalMutationString11221 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSmhBizArticle_literalMutationString11221_failAssert0_literalMutationString12651 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSmhBizArticle_literalMutationString11221_failAssert0_literalMutationString12625_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "Jay", "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
                doc.title();
                doc.select("html").attr("xml:lang");
                Elements articleBody = doc.select(".articleBody > *");
                articleBody.size();

                org.junit.Assert.fail("testSmhBizArticle_literalMutationString11221 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSmhBizArticle_literalMutationString11221_failAssert0_literalMutationString12625 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSmhBizArticle_literalMutationString11221_failAssert0_add13424_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
                doc.title();
                doc.select("html").attr("xml:lang");
                Elements articleBody = doc.select(".articleBody > *");
                articleBody.size();
                articleBody.size();

                org.junit.Assert.fail("testSmhBizArticle_literalMutationString11221 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSmhBizArticle_literalMutationString11221_failAssert0_add13424 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSmhBizArticle_literalMutationString11221_failAssert0_add13418_failAssert0() throws IOException {
        try {
            {
                AmplParseTest.getFile("");
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
                doc.title();
                doc.select("html").attr("xml:lang");
                Elements articleBody = doc.select(".articleBody > *");
                articleBody.size();

                org.junit.Assert.fail("testSmhBizArticle_literalMutationString11221 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSmhBizArticle_literalMutationString11221_failAssert0_add13418 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSmhBizArticle_literalMutationString11221_failAssert0null13781_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.smh.com.au/business/the-boards-next-fear-the-female-quota-20100106-lteq.html");
                doc.title();
                doc.select("html").attr("xml:lang");
                Elements articleBody = doc.select(".articleBody > *");
                articleBody.size();

                org.junit.Assert.fail("testSmhBizArticle_literalMutationString11221 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSmhBizArticle_literalMutationString11221_failAssert0null13781 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSmhBizArticle_literalMutationString11221_failAssert0null13782_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", null);
                doc.title();
                doc.select("html").attr("xml:lang");
                Elements articleBody = doc.select(".articleBody > *");
                articleBody.size();

                org.junit.Assert.fail("testSmhBizArticle_literalMutationString11221 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSmhBizArticle_literalMutationString11221_failAssert0null13782 should have thrown FileNotFoundException");
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
    public void testGoogleSearchIpod_literalMutationString15732_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
            doc.title();
            Elements results = doc.select("h3.r > a");
            results.size();
            results.get(0).attr("href");
            results.get(1).attr("href");
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString15732 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationString15732_failAssert0null19689_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", null);
                doc.title();
                Elements results = doc.select("h3.r > a");
                results.size();
                results.get(0).attr("href");
                results.get(1).attr("href");
                org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString15732 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString15732_failAssert0null19689 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationString15732_failAssert0_literalMutationString17956_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "lORk*hkWhIdcRaOH]seP?#ge:PD@5vK]B{y)net6Ly{pc4.<r]3kh&5Oy#");
                doc.title();
                Elements results = doc.select("h3.r > a");
                results.size();
                results.get(0).attr("href");
                results.get(1).attr("href");
                org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString15732 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString15732_failAssert0_literalMutationString17956 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationString15732_failAssert0_add19210_failAssert0() throws IOException {
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
                org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString15732 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString15732_failAssert0_add19210 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationString15732_failAssert0_add19206_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
                doc.title();
                doc.select("h3.r > a");
                Elements results = doc.select("h3.r > a");
                results.size();
                results.get(0).attr("href");
                results.get(1).attr("href");
                org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString15732 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString15732_failAssert0_add19206 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationString15765_literalMutationString16037_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
            String o_testGoogleSearchIpod_literalMutationString15765__5 = doc.title();
            Elements results = doc.select("h3.r > a");
            int o_testGoogleSearchIpod_literalMutationString15765__8 = results.size();
            String o_testGoogleSearchIpod_literalMutationString15765__9 = results.get(0).attr("hrJef");
            String o_testGoogleSearchIpod_literalMutationString15765__11 = results.get(1).attr("href");
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString15765_literalMutationString16037 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationNumber15770_literalMutationString16724_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
            String o_testGoogleSearchIpod_literalMutationNumber15770__5 = doc.title();
            Elements results = doc.select("h3.r > a");
            int o_testGoogleSearchIpod_literalMutationNumber15770__8 = results.size();
            String o_testGoogleSearchIpod_literalMutationNumber15770__9 = results.get(0).attr("href");
            String o_testGoogleSearchIpod_literalMutationNumber15770__11 = results.get(1).attr("href");
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationNumber15770_literalMutationString16724 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationNumber15756_failAssert0_add19360_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("/htmltests/google-ipod.html");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
                doc.title();
                Elements results = doc.select("h3.r > a");
                results.size();
                results.get(-1).attr("href");
                results.get(1).attr("href");
                results.get(1).attr("href");
                org.junit.Assert.fail("testGoogleSearchIpod_literalMutationNumber15756 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationNumber15756_failAssert0_add19360 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationString15732_failAssert0_literalMutationNumber17967_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
                doc.title();
                Elements results = doc.select("h3.r > a");
                results.size();
                results.get(0).attr("href");
                results.get(1).attr("href");
                org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString15732 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationString15732_failAssert0_literalMutationNumber17967 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString41228_failAssert0_add41845_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.text().contains("gd-jpeg");
                doc.text().contains("gd-jpeg");
                org.junit.Assert.fail("testBinary_literalMutationString41228 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString41228_failAssert0_add41845 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString41228_failAssert0_add41844_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Jsoup.parse(in, "UTF-8");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.text().contains("gd-jpeg");
                org.junit.Assert.fail("testBinary_literalMutationString41228 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString41228_failAssert0_add41844 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString41228_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8");
            doc.text().contains("gd-jpeg");
            org.junit.Assert.fail("testBinary_literalMutationString41228 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_add41248_literalMutationString41451_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8");
            boolean o_testBinary_add41248__5 = doc.text().contains("gd-jpeg");
            boolean o_testBinary_add41248__7 = doc.text().contains("gd-jpeg");
            org.junit.Assert.fail("testBinary_add41248_literalMutationString41451 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString41232_failAssert0_literalMutationString41566_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.text().contains("gd-jpeg");
                org.junit.Assert.fail("testBinary_literalMutationString41232 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString41232_failAssert0_literalMutationString41566 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString41240_literalMutationString41328_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8");
            boolean o_testBinary_literalMutationString41240__5 = doc.text().contains("");
            org.junit.Assert.fail("testBinary_literalMutationString41240_literalMutationString41328 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString41228_failAssert0_literalMutationString41611_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.text().contains("gdjpeg");
                org.junit.Assert.fail("testBinary_literalMutationString41228 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString41228_failAssert0_literalMutationString41611 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString41228_failAssert0null41957_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.text().contains(null);
                org.junit.Assert.fail("testBinary_literalMutationString41228 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString41228_failAssert0null41957 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinarynull41253_failAssert0_literalMutationString41536_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.text().contains(null);
                org.junit.Assert.fail("testBinarynull41253 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testBinarynull41253_failAssert0_literalMutationString41536 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_add41247_literalMutationString41499_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document o_testBinary_add41247__3 = Jsoup.parse(in, "UTF-8");
            Document doc = Jsoup.parse(in, "UTF-8");
            boolean o_testBinary_add41247__6 = doc.text().contains("gd-jpeg");
            org.junit.Assert.fail("testBinary_add41247_literalMutationString41499 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBinary_literalMutationString41235_failAssert0_literalMutationString41722_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "Jay");
                doc.text().contains("gd-jpeg");
                org.junit.Assert.fail("testBinary_literalMutationString41235 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testBinary_literalMutationString41235_failAssert0_literalMutationString41722 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString21352_failAssert0_literalMutationString22292_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                doc.title();
                Element a = doc.select("a[href=t/2322m[2]").first();
                a.attr("abs:href");
                a.text();
                org.junit.Assert.fail("testYahooJp_literalMutationString21352 should have thrown Selector$SelectorParseException");
            }
            org.junit.Assert.fail("testYahooJp_literalMutationString21352_failAssert0_literalMutationString22292 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString21337_failAssert0_literalMutationString22232_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                doc.title();
                Element a = doc.select("a[href=t/2322m2]").first();
                a.attr("abs:href");
                a.text();
                org.junit.Assert.fail("testYahooJp_literalMutationString21337 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testYahooJp_literalMutationString21337_failAssert0_literalMutationString22232 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString21333_failAssert0_literalMutationString22571_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "Jay");
                doc.title();
                Element a = doc.select("a[href=t/2322m2]").first();
                a.attr("abs:href");
                a.text();
                org.junit.Assert.fail("testYahooJp_literalMutationString21333 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooJp_literalMutationString21333_failAssert0_literalMutationString22571 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString21333_failAssert0_literalMutationString22562_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "fO(8k", "http://www.yahoo.co.jp/index.html");
                doc.title();
                Element a = doc.select("a[href=t/2322m2]").first();
                a.attr("abs:href");
                a.text();
                org.junit.Assert.fail("testYahooJp_literalMutationString21333 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooJp_literalMutationString21333_failAssert0_literalMutationString22562 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString21333_failAssert0_add23036_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                doc.title();
                doc.title();
                Element a = doc.select("a[href=t/2322m2]").first();
                a.attr("abs:href");
                a.text();
                org.junit.Assert.fail("testYahooJp_literalMutationString21333 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooJp_literalMutationString21333_failAssert0_add23036 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString21333_failAssert0_add23037_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                doc.title();
                doc.select("a[href=t/2322m2]").first();
                Element a = doc.select("a[href=t/2322m2]").first();
                a.attr("abs:href");
                a.text();
                org.junit.Assert.fail("testYahooJp_literalMutationString21333 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooJp_literalMutationString21333_failAssert0_add23037 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString21333_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
            doc.title();
            Element a = doc.select("a[href=t/2322m2]").first();
            a.attr("abs:href");
            a.text();
            org.junit.Assert.fail("testYahooJp_literalMutationString21333 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString21357_literalMutationString21595_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
            String o_testYahooJp_literalMutationString21357__6 = doc.title();
            Element a = doc.select("a[href=t/2322m2]").first();
            String o_testYahooJp_literalMutationString21357__10 = a.attr("");
            String o_testYahooJp_literalMutationString21357__11 = a.text();
            org.junit.Assert.fail("testYahooJp_literalMutationString21357_literalMutationString21595 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString21333_failAssert0null23305_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                doc.title();
                Element a = doc.select(null).first();
                a.attr("abs:href");
                a.text();
                org.junit.Assert.fail("testYahooJp_literalMutationString21333 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooJp_literalMutationString21333_failAssert0null23305 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString21333_failAssert0null23306_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                doc.title();
                Element a = doc.select("a[href=t/2322m2]").first();
                a.attr(null);
                a.text();
                org.junit.Assert.fail("testYahooJp_literalMutationString21333 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooJp_literalMutationString21333_failAssert0null23306 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooJp_literalMutationString21353_failAssert0_literalMutationString22615_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                doc.title();
                Element a = doc.select("=2}gIduo0M1J!{B/").first();
                a.attr("abs:href");
                a.text();
                org.junit.Assert.fail("testYahooJp_literalMutationString21353 should have thrown Selector$SelectorParseException");
            }
            org.junit.Assert.fail("testYahooJp_literalMutationString21353_failAssert0_literalMutationString22615 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString32254_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testBaidu_literalMutationString32254 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString32254_failAssert0_add38287_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                Element submit = doc.select("#su").first();
                submit.attr("value");
                doc.select("input[value=????]").first();
                submit = doc.select("input[value=????]").first();
                submit.id();
                Element newsLink = doc.select("a:contains(?)").first();
                newsLink.absUrl("href");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                doc.outputSettings().charset("ascii");
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaidu_literalMutationString32254 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString32254_failAssert0_add38287 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaidu_literalMutationString32254_failAssert0_literalMutationString33532_failAssert0() throws IOException {
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
                doc.select("Jay").outerHtml();
                doc.outputSettings().charset("ascii");
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaidu_literalMutationString32254 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaidu_literalMutationString32254_failAssert0_literalMutationString33532 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString14331_failAssert0_literalMutationString14908_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "htp://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString14331 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString14331_failAssert0_literalMutationString14908 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString14331_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
            doc.outputSettings().charset().displayName();
            doc.select("title").outerHtml();
            org.junit.Assert.fail("testBaiduVariant_literalMutationString14331 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString14335_failAssert0_literalMutationString14796_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString14335 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString14335_failAssert0_literalMutationString14796 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString14331_failAssert0_add15164_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString14331 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString14331_failAssert0_add15164 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString14331_failAssert0_add15163_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select("title");
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString14331 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString14331_failAssert0_add15163 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString14331_failAssert0null15284_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
                doc.outputSettings().charset().displayName();
                doc.select(null).outerHtml();
                org.junit.Assert.fail("testBaiduVariant_literalMutationString14331 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testBaiduVariant_literalMutationString14331_failAssert0null15284 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_literalMutationString14348_literalMutationString14505_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
            String o_testBaiduVariant_literalMutationString14348__7 = doc.outputSettings().charset().displayName();
            String o_testBaiduVariant_literalMutationString14348__10 = doc.select("tGtle").outerHtml();
            org.junit.Assert.fail("testBaiduVariant_literalMutationString14348_literalMutationString14505 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariant_add14349_literalMutationString14683_failAssert0() throws IOException {
        try {
            AmplParseTest.getFile("/htmltests/baidu-variant.html");
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.baidu.com/");
            String o_testBaiduVariant_add14349__8 = doc.outputSettings().charset().displayName();
            String o_testBaiduVariant_add14349__11 = doc.select("title").outerHtml();
            org.junit.Assert.fail("testBaiduVariant_add14349_literalMutationString14683 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBaiduVariantnull14358_failAssert0_literalMutationString14755_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, null);
                doc.outputSettings().charset().displayName();
                doc.select("title").outerHtml();
                org.junit.Assert.fail("testBaiduVariantnull14358 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testBaiduVariantnull14358_failAssert0_literalMutationString14755 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString25832_failAssert0_literalMutationString28309_failAssert0() throws IOException {
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
                in = AmplParseTest.getFile("");
                doc = Jsoup.parse(in, null, "http://example.com/");
                doc.outputSettings().charset().displayName();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString25832 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString25832_failAssert0_literalMutationString28309 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString25832_failAssert0_add29890_failAssert0() throws IOException {
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
                doc.outputSettings();
                doc.outputSettings().charset().displayName();
                doc.text();
                doc.text();
                org.junit.Assert.fail("testHtml5Charset_literalMutationString25832 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testHtml5Charset_literalMutationString25832_failAssert0_add29890 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString25832_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testHtml5Charset_literalMutationString25832 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString25818_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testHtml5Charset_literalMutationString25818 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHtml5Charset_literalMutationString25806_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testHtml5Charset_literalMutationString25806 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString30990_failAssert0_literalMutationString31487_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString30990 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString30990_failAssert0_literalMutationString31487 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString30985_failAssert0_literalMutationString31360_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("nyt_headline[vJrsion=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString30985 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString30985_failAssert0_literalMutationString31360 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString30985_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            Element headline = doc.select("nyt_headline[version=1.0]").first();
            headline.text();
            org.junit.Assert.fail("testNytArticle_literalMutationString30985 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString30992_literalMutationString31166_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "");
            Element headline = doc.select("nyt_headline[version=1.0]").first();
            String o_testNytArticle_literalMutationString30992__9 = headline.text();
            org.junit.Assert.fail("testNytArticle_literalMutationString30992_literalMutationString31166 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString30985_failAssert0_add31653_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString30985 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString30985_failAssert0_add31653 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString30985_failAssert0_add31656_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString30985 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString30985_failAssert0_add31656 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString30985_failAssert0null31783_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, null);
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString30985 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString30985_failAssert0null31783 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticlenull31010_failAssert0_literalMutationString31308_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, null);
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticlenull31010 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testNytArticlenull31010_failAssert0_literalMutationString31308 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_add31004_literalMutationString31205_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document o_testNytArticle_add31004__4 = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            Element headline = doc.select("nyt_headline[version=1.0]").first();
            String o_testNytArticle_add31004__10 = headline.text();
            org.junit.Assert.fail("testNytArticle_add31004_literalMutationString31205 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString30989_failAssert0_literalMutationString31331_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("nyt_headline[version=1.0]").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString30989 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString30989_failAssert0_literalMutationString31331 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString30993_literalMutationString31130_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.[ytimes.com/2010/07/26/business/global/26bp.html?hp");
            Element headline = doc.select("nyt_headline[version=1.0]").first();
            String o_testNytArticle_literalMutationString30993__9 = headline.text();
            org.junit.Assert.fail("testNytArticle_literalMutationString30993_literalMutationString31130 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_add31006_literalMutationString31229_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            Elements o_testNytArticle_add31006__6 = doc.select("nyt_headline[version=1.0]");
            Element headline = doc.select("nyt_headline[version=1.0]").first();
            String o_testNytArticle_add31006__10 = headline.text();
            org.junit.Assert.fail("testNytArticle_add31006_literalMutationString31229 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNytArticle_literalMutationString30985_failAssert0_literalMutationString31358_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                Element headline = doc.select("").first();
                headline.text();
                org.junit.Assert.fail("testNytArticle_literalMutationString30985 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNytArticle_literalMutationString30985_failAssert0_literalMutationString31358 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString9518_failAssert0_literalMutationString10013_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
                p.text();
                org.junit.Assert.fail("testYahooArticle_literalMutationString9518 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString9518_failAssert0_literalMutationString10013 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString9513_failAssert0_literalMutationString10069_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UUTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
                p.text();
                org.junit.Assert.fail("testYahooArticle_literalMutationString9513 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString9513_failAssert0_literalMutationString10069 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString9513_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
            Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
            p.text();
            org.junit.Assert.fail("testYahooArticle_literalMutationString9513 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString9513_failAssert0_literalMutationString10068_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
                p.text();
                org.junit.Assert.fail("testYahooArticle_literalMutationString9513 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString9513_failAssert0_literalMutationString10068 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString9513_failAssert0_add10544_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
                p.text();
                p.text();
                org.junit.Assert.fail("testYahooArticle_literalMutationString9513 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString9513_failAssert0_add10544 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString9513_failAssert0_add10542_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                doc.select("p:contains(Volt will be sold in the United States)").first();
                Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
                p.text();
                org.junit.Assert.fail("testYahooArticle_literalMutationString9513 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString9513_failAssert0_add10542 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString9521_failAssert0_literalMutationString10319_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, "Jay", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
                p.text();
                org.junit.Assert.fail("testYahooArticle_literalMutationString9521 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString9521_failAssert0_literalMutationString10319 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testYahooArticle_literalMutationString9513_failAssert0null10725_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
                Element p = doc.select("p:contains(Volt will be sold in the United States)").first();
                p.text();
                org.junit.Assert.fail("testYahooArticle_literalMutationString9513 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testYahooArticle_literalMutationString9513_failAssert0null10725 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString20262_failAssert0_literalMutationString20474_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#orm").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString20262 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString20262_failAssert0_literalMutationString20474 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_add20277_literalMutationString20365_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null);
            Element form = doc.select("#form").first();
            int o_testLowercaseUtf8Charset_add20277__8 = form.children().size();
            int o_testLowercaseUtf8Charset_add20277__10 = form.children().size();
            String o_testLowercaseUtf8Charset_add20277__12 = doc.outputSettings().charset().name();
            org.junit.Assert.fail("testLowercaseUtf8Charset_add20277_literalMutationString20365 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString20262_failAssert0_literalMutationString20472_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("Jay").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString20262 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString20262_failAssert0_literalMutationString20472 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString20262_failAssert0_literalMutationString20470_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString20262 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString20262_failAssert0_literalMutationString20470 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charsetnull20284_failAssert0_literalMutationString20455_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select(null).first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charsetnull20284 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charsetnull20284_failAssert0_literalMutationString20455 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString20262_failAssert0_add20744_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                form.children().size();
                doc.outputSettings().charset();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString20262 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString20262_failAssert0_add20744 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString20262_failAssert0_add20741_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                form.children().size();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString20262 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString20262_failAssert0_add20741 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString20262_failAssert0() throws IOException {
        try {
            File in = AmplParseTest.getFile("");
            Document doc = Jsoup.parse(in, null);
            Element form = doc.select("#form").first();
            form.children().size();
            doc.outputSettings().charset().name();
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString20262 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString20262_failAssert0null20893_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select(null).first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString20262 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString20262_failAssert0null20893 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLowercaseUtf8Charset_literalMutationString20262_failAssert0_add20746_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                Element form = doc.select("#form").first();
                form.children().size();
                doc.outputSettings().charset().name();
                org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString20262 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLowercaseUtf8Charset_literalMutationString20262_failAssert0_add20746 should have thrown FileNotFoundException");
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

