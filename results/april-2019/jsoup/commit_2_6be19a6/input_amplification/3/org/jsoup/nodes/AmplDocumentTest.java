package org.jsoup.nodes;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.integration.ParseTest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import static org.jsoup.nodes.Document.OutputSettings.Syntax.xml;


public class AmplDocumentTest {
    private static final String charsetUtf8 = "UTF-8";

    private static final String charsetIso8859 = "ISO-8859-1";

    @Test(timeout = 10000)
    public void testLocation_literalMutationString10_literalMutationString384_failAssert0_literalMutationString5457_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("");
                Document doc = Jsoup.parse(in, "", "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString10_literalMutationString384 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString10_literalMutationString384_failAssert0_literalMutationString5457 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString25null2402_failAssert0_literalMutationString5064_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", null);
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
                doc = Jsoup.parse(in, null, "");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString25null2402 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString25null2402_failAssert0_literalMutationString5064 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString25_literalMutationString328_failAssert0_literalMutationString5928_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
                doc = Jsoup.parse(in, null, "");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString25_literalMutationString328 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString25_literalMutationString328_failAssert0_literalMutationString5928 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString19_failAssert0_literalMutationString1003_failAssert0_add10839_failAssert0() throws IOException {
        try {
            {
                {
                    File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
                    Document doc = Jsoup.parse(in, "UTF-8", " Hello\nthere \u00a0  ");
                    String location = doc.location();
                    String baseUri = doc.baseUri();
                    in = new ParseTest().getFile("");
                    doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    doc.location();
                    location = doc.location();
                    baseUri = doc.baseUri();
                    org.junit.Assert.fail("testLocation_literalMutationString19 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLocation_literalMutationString19_failAssert0_literalMutationString1003 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString19_failAssert0_literalMutationString1003_failAssert0_add10839 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString23_failAssert0_literalMutationString1165_failAssert0_add11344_failAssert0() throws IOException {
        try {
            {
                {
                    File in = new ParseTest().getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                    String location = doc.location();
                    String baseUri = doc.baseUri();
                    in = new ParseTest().getFile("/htmltests/ny`-article-1.html");
                    doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    doc.location();
                    location = doc.location();
                    baseUri = doc.baseUri();
                    org.junit.Assert.fail("testLocation_literalMutationString23 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testLocation_literalMutationString23_failAssert0_literalMutationString1165 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString23_failAssert0_literalMutationString1165_failAssert0_add11344 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString19_failAssert0_add2225_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                new ParseTest().getFile("");
                in = new ParseTest().getFile("");
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString19 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString19_failAssert0_add2225 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString23_failAssert0_literalMutationString1165_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("/htmltests/ny`-article-1.html");
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString23 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString23_failAssert0_literalMutationString1165 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString26_literalMutationString552_failAssert0() throws IOException {
        try {
            File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
            String location = doc.location();
            String baseUri = doc.baseUri();
            in = new ParseTest().getFile("");
            doc = Jsoup.parse(in, null, " Hello\nthere \u00a0  ");
            location = doc.location();
            baseUri = doc.baseUri();
            org.junit.Assert.fail("testLocation_literalMutationString26_literalMutationString552 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString19_failAssert0_literalMutationString1003_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
                Document doc = Jsoup.parse(in, "UTF-8", " Hello\nthere \u00a0  ");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("");
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString19 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString19_failAssert0_literalMutationString1003 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString15_literalMutationString288_failAssert0() throws IOException {
        try {
            File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
            Document doc = Jsoup.parse(in, "UTF-8", "http://wzw.yahoo.co.jp/index.html");
            String location = doc.location();
            String baseUri = doc.baseUri();
            in = new ParseTest().getFile("");
            doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            location = doc.location();
            baseUri = doc.baseUri();
            org.junit.Assert.fail("testLocation_literalMutationString15_literalMutationString288 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString2_failAssert0_add2299_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                doc.location();
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString2 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString2_failAssert0_add2299 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString23_failAssert0_literalMutationString1165_failAssert0_literalMutationString7872_failAssert0() throws IOException {
        try {
            {
                {
                    File in = new ParseTest().getFile("");
                    Document doc = Jsoup.parse(in, "", "http://www.yahoo.co.jp/index.html");
                    String location = doc.location();
                    String baseUri = doc.baseUri();
                    in = new ParseTest().getFile("/htmltests/ny`-article-1.html");
                    doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    location = doc.location();
                    baseUri = doc.baseUri();
                    org.junit.Assert.fail("testLocation_literalMutationString23 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testLocation_literalMutationString23_failAssert0_literalMutationString1165 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString23_failAssert0_literalMutationString1165_failAssert0_literalMutationString7872 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString2_failAssert0_literalMutationString1249_failAssert0_add10753_failAssert0() throws IOException {
        try {
            {
                {
                    File in = new ParseTest().getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                    String location = doc.location();
                    String baseUri = doc.baseUri();
                    new ParseTest().getFile("/htmltests/nyt-article-1.html");
                    in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
                    doc = Jsoup.parse(in, null, "http://www.nytimeb.com/2010/07/26/business/global/26bp.html?hp");
                    location = doc.location();
                    baseUri = doc.baseUri();
                    org.junit.Assert.fail("testLocation_literalMutationString2 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLocation_literalMutationString2_failAssert0_literalMutationString1249 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString2_failAssert0_literalMutationString1249_failAssert0_add10753 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString2_failAssert0_literalMutationString1249_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
                doc = Jsoup.parse(in, null, "http://www.nytimeb.com/2010/07/26/business/global/26bp.html?hp");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString2 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString2_failAssert0_literalMutationString1249 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString2_failAssert0() throws IOException {
        try {
            File in = new ParseTest().getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
            String location = doc.location();
            String baseUri = doc.baseUri();
            in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
            doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            location = doc.location();
            baseUri = doc.baseUri();
            org.junit.Assert.fail("testLocation_literalMutationString2 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString15_literalMutationString288_failAssert0_add11225_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
                Document doc = Jsoup.parse(in, "UTF-8", "http://wzw.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                new ParseTest().getFile("");
                in = new ParseTest().getFile("");
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString15_literalMutationString288 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString15_literalMutationString288_failAssert0_add11225 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString10_literalMutationString384_failAssert0_add10554_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("");
                Document doc = Jsoup.parse(in, "UTF8", "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                doc.baseUri();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString10_literalMutationString384 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString10_literalMutationString384_failAssert0_add10554 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_add31_literalMutationString738_failAssert0() throws IOException {
        try {
            new ParseTest().getFile("/htmltests/yahoo-jp.html");
            File in = new ParseTest().getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
            String location = doc.location();
            String baseUri = doc.baseUri();
            in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
            doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            location = doc.location();
            baseUri = doc.baseUri();
            org.junit.Assert.fail("testLocation_add31_literalMutationString738 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString2_failAssert0null2618_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
                doc = Jsoup.parse(in, null, null);
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString2 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString2_failAssert0null2618 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString10_literalMutationString384_failAssert0null12110_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("");
                Document doc = Jsoup.parse(in, "UTF8", "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
                doc = Jsoup.parse(in, null, null);
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString10_literalMutationString384 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString10_literalMutationString384_failAssert0null12110 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString10_literalMutationString384_failAssert0() throws IOException {
        try {
            File in = new ParseTest().getFile("");
            Document doc = Jsoup.parse(in, "UTF8", "http://www.yahoo.co.jp/index.html");
            String location = doc.location();
            String baseUri = doc.baseUri();
            in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
            doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            location = doc.location();
            baseUri = doc.baseUri();
            org.junit.Assert.fail("testLocation_literalMutationString10_literalMutationString384 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_add31_literalMutationString738_failAssert0null12300_failAssert0() throws IOException {
        try {
            {
                new ParseTest().getFile("/htmltests/yahoo-jp.html");
                File in = new ParseTest().getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_add31_literalMutationString738 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_add31_literalMutationString738_failAssert0null12300 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString23_failAssert0_literalMutationString1165_failAssert0null12691_failAssert0() throws IOException {
        try {
            {
                {
                    File in = new ParseTest().getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                    String location = doc.location();
                    String baseUri = doc.baseUri();
                    in = new ParseTest().getFile(null);
                    doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    location = doc.location();
                    baseUri = doc.baseUri();
                    org.junit.Assert.fail("testLocation_literalMutationString23 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testLocation_literalMutationString23_failAssert0_literalMutationString1165 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString23_failAssert0_literalMutationString1165_failAssert0null12691 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_add31_literalMutationString738_failAssert0_add10814_failAssert0() throws IOException {
        try {
            {
                new ParseTest().getFile("/htmltests/yahoo-jp.html");
                File in = new ParseTest().getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_add31_literalMutationString738 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_add31_literalMutationString738_failAssert0_add10814 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString2_failAssert0_literalMutationString1249_failAssert0null12259_failAssert0() throws IOException {
        try {
            {
                {
                    File in = new ParseTest().getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                    String location = doc.location();
                    String baseUri = doc.baseUri();
                    in = new ParseTest().getFile(null);
                    doc = Jsoup.parse(in, null, "http://www.nytimeb.com/2010/07/26/business/global/26bp.html?hp");
                    location = doc.location();
                    baseUri = doc.baseUri();
                    org.junit.Assert.fail("testLocation_literalMutationString2 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLocation_literalMutationString2_failAssert0_literalMutationString1249 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString2_failAssert0_literalMutationString1249_failAssert0null12259 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString18_literalMutationString372_failAssert0() throws IOException {
        try {
            File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/indexhtml");
            String location = doc.location();
            String baseUri = doc.baseUri();
            in = new ParseTest().getFile("");
            doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            location = doc.location();
            baseUri = doc.baseUri();
            org.junit.Assert.fail("testLocation_literalMutationString18_literalMutationString372 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString19_failAssert0() throws IOException {
        try {
            File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
            String location = doc.location();
            String baseUri = doc.baseUri();
            in = new ParseTest().getFile("");
            doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            location = doc.location();
            baseUri = doc.baseUri();
            org.junit.Assert.fail("testLocation_literalMutationString19 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString19_failAssert0_literalMutationString1003_failAssert0_literalMutationString6373_failAssert0() throws IOException {
        try {
            {
                {
                    File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
                    Document doc = Jsoup.parse(in, "UTF-8", " Hello\nthere \u00a0  ");
                    String location = doc.location();
                    String baseUri = doc.baseUri();
                    in = new ParseTest().getFile("");
                    doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26,bp.html?hp");
                    location = doc.location();
                    baseUri = doc.baseUri();
                    org.junit.Assert.fail("testLocation_literalMutationString19 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLocation_literalMutationString19_failAssert0_literalMutationString1003 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString19_failAssert0_literalMutationString1003_failAssert0_literalMutationString6373 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Ignore
    @Test
    public void testOverflowClone() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 100000; i++) {
            builder.insert(0, "<i>");
            builder.append("</i>");
        }
        Document doc = Jsoup.parse(builder.toString());
        doc.clone();
    }

    private Document createHtmlDocument(String charset) {
        final Document doc = Document.createShell("");
        doc.head().appendElement("meta").attr("charset", charset);
        doc.head().appendElement("meta").attr("name", "charset").attr("content", charset);
        return doc;
    }

    private Document createXmlDocument(String version, String charset, boolean addDecl) {
        final Document doc = new Document("");
        doc.appendElement("root").text("node");
        doc.outputSettings().syntax(xml);
        if (addDecl) {
            XmlDeclaration decl = new XmlDeclaration("xml", false);
            decl.attr("version", version);
            decl.attr("encoding", charset);
            doc.prependChild(decl);
        }
        return doc;
    }
}

