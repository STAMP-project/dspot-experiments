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
    public void testLocation_literalMutationString2_failAssert0_literalMutationString1138_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", " Hello\nthere \u00a0  ");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString2 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString2_failAssert0_literalMutationString1138 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString2_failAssert0_literalMutationString1138_failAssert0_add10578_failAssert0() throws IOException {
        try {
            {
                {
                    File in = new ParseTest().getFile("");
                    Jsoup.parse(in, "UTF-8", " Hello\nthere \u00a0  ");
                    Document doc = Jsoup.parse(in, "UTF-8", " Hello\nthere \u00a0  ");
                    String location = doc.location();
                    String baseUri = doc.baseUri();
                    in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
                    doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    location = doc.location();
                    baseUri = doc.baseUri();
                    org.junit.Assert.fail("testLocation_literalMutationString2 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLocation_literalMutationString2_failAssert0_literalMutationString1138 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString2_failAssert0_literalMutationString1138_failAssert0_add10578 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString27_literalMutationString660_failAssert0_literalMutationString7611_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
                doc = Jsoup.parse(in, null, " Hello\nthere \u00a0  ");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString27_literalMutationString660 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString27_literalMutationString660_failAssert0_literalMutationString7611 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString27_literalMutationString660_failAssert0_add11090_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                doc.baseUri();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.htil?hp");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString27_literalMutationString660 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString27_literalMutationString660_failAssert0_add11090 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString20_failAssert0_literalMutationString1323_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("");
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString20 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString20_failAssert0_literalMutationString1323 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString26null2509_failAssert0_literalMutationString6650_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile(null);
                doc = Jsoup.parse(in, null, " Hello\nthere \u00a0  ");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString26null2509 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString26null2509_failAssert0_literalMutationString6650 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString10_literalMutationString648_failAssert0_add10997_failAssert0() throws IOException {
        try {
            {
                new ParseTest().getFile("/htmltests/yahoo-jp.html");
                File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
                Document doc = Jsoup.parse(in, "UTF8", "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("");
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString10_literalMutationString648 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString10_literalMutationString648_failAssert0_add10997 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocationnull45_failAssert0_literalMutationString1399_failAssert0_literalMutationString6032_failAssert0() throws IOException {
        try {
            {
                {
                    File in = new ParseTest().getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http:|//www.yahoo.co.jp/index.html");
                    String location = doc.location();
                    String baseUri = doc.baseUri();
                    in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
                    doc = Jsoup.parse(in, null, null);
                    location = doc.location();
                    baseUri = doc.baseUri();
                    org.junit.Assert.fail("testLocationnull45 should have thrown IllegalArgumentException");
                }
                org.junit.Assert.fail("testLocationnull45_failAssert0_literalMutationString1399 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testLocationnull45_failAssert0_literalMutationString1399_failAssert0_literalMutationString6032 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString2_failAssert0_literalMutationString1138_failAssert0_add10582_failAssert0() throws IOException {
        try {
            {
                {
                    File in = new ParseTest().getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", " Hello\nthere \u00a0  ");
                    String location = doc.location();
                    String baseUri = doc.baseUri();
                    in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
                    Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    location = doc.location();
                    baseUri = doc.baseUri();
                    org.junit.Assert.fail("testLocation_literalMutationString2 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLocation_literalMutationString2_failAssert0_literalMutationString1138 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString2_failAssert0_literalMutationString1138_failAssert0_add10582 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString10_literalMutationString648_failAssert0() throws IOException {
        try {
            File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
            Document doc = Jsoup.parse(in, "UTF8", "http://www.yahoo.co.jp/index.html");
            String location = doc.location();
            String baseUri = doc.baseUri();
            in = new ParseTest().getFile("");
            doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            location = doc.location();
            baseUri = doc.baseUri();
            org.junit.Assert.fail("testLocation_literalMutationString10_literalMutationString648 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString29_literalMutationString444_failAssert0() throws IOException {
        try {
            File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
            String location = doc.location();
            String baseUri = doc.baseUri();
            in = new ParseTest().getFile("");
            doc = Jsoup.parse(in, null, "http://wrww.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            location = doc.location();
            baseUri = doc.baseUri();
            org.junit.Assert.fail("testLocation_literalMutationString29_literalMutationString444 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString19_failAssert0_add2191_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("");
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                doc.location();
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString19 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString19_failAssert0_add2191 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString27_literalMutationString660_failAssert0() throws IOException {
        try {
            File in = new ParseTest().getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
            String location = doc.location();
            String baseUri = doc.baseUri();
            in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
            doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.htil?hp");
            location = doc.location();
            baseUri = doc.baseUri();
            org.junit.Assert.fail("testLocation_literalMutationString27_literalMutationString660 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString2_failAssert0_literalMutationString1138_failAssert0_literalMutationString6104_failAssert0() throws IOException {
        try {
            {
                {
                    File in = new ParseTest().getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", " Hello\nthere \u00a0  ");
                    String location = doc.location();
                    String baseUri = doc.baseUri();
                    in = new ParseTest().getFile("/htmllests/nyt-article-1.html");
                    doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    location = doc.location();
                    baseUri = doc.baseUri();
                    org.junit.Assert.fail("testLocation_literalMutationString2 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLocation_literalMutationString2_failAssert0_literalMutationString1138 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString2_failAssert0_literalMutationString1138_failAssert0_literalMutationString6104 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString2_failAssert0null2591_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", null);
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString2 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString2_failAssert0null2591 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString14_literalMutationString750_failAssert0() throws IOException {
        try {
            File in = new ParseTest().getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", " Hello\nthere \u00a0  ");
            String location = doc.location();
            String baseUri = doc.baseUri();
            in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
            doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            location = doc.location();
            baseUri = doc.baseUri();
            org.junit.Assert.fail("testLocation_literalMutationString14_literalMutationString750 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString6_failAssert0_literalMutationString1242_failAssert0_literalMutationString5779_failAssert0() throws IOException {
        try {
            {
                {
                    File in = new ParseTest().getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                    String location = doc.location();
                    String baseUri = doc.baseUri();
                    in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
                    doc = Jsoup.parse(in, null, "qoq0eDQ=abN3ud2>^fz:C?4u%,Ol/W8k4;O],pY=:`nzULRzdEVQrABzs&sRi_");
                    location = doc.location();
                    baseUri = doc.baseUri();
                    org.junit.Assert.fail("testLocation_literalMutationString6 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testLocation_literalMutationString6_failAssert0_literalMutationString1242 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString6_failAssert0_literalMutationString1242_failAssert0_literalMutationString5779 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString22_failAssert0_literalMutationString1008_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("/htmltests/nyt-article-1.hml");
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString22 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString22_failAssert0_literalMutationString1008 should have thrown FileNotFoundException");
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
    public void testLocation_literalMutationString2_failAssert0_literalMutationString1138_failAssert0null12084_failAssert0() throws IOException {
        try {
            {
                {
                    File in = new ParseTest().getFile("");
                    Document doc = Jsoup.parse(in, null, " Hello\nthere \u00a0  ");
                    String location = doc.location();
                    String baseUri = doc.baseUri();
                    in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
                    doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    location = doc.location();
                    baseUri = doc.baseUri();
                    org.junit.Assert.fail("testLocation_literalMutationString2 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLocation_literalMutationString2_failAssert0_literalMutationString1138 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString2_failAssert0_literalMutationString1138_failAssert0null12084 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString19_failAssert0_literalMutationString917_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("");
                doc = Jsoup.parse(in, null, "Tttp://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString19 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString19_failAssert0_literalMutationString917 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString2_failAssert0_add2261_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                new ParseTest().getFile("/htmltests/nyt-article-1.html");
                in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString2 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString2_failAssert0_add2261 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString29_add1725_literalMutationString4638_failAssert0() throws IOException {
        try {
            File in = new ParseTest().getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
            doc.toString();
            String location = doc.location();
            String baseUri = doc.baseUri();
            in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
            doc = Jsoup.parse(in, null, "http://wrww.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            location = doc.location();
            baseUri = doc.baseUri();
            org.junit.Assert.fail("testLocation_literalMutationString29_add1725_literalMutationString4638 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString20_failAssert0_literalMutationString1306_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile(" Hello\nthere \u00a0  ");
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString20 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString20_failAssert0_literalMutationString1306 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocationnull43_failAssert0_literalMutationString1342_failAssert0_literalMutationString6007_failAssert0() throws IOException {
        try {
            {
                {
                    File in = new ParseTest().getFile("");
                    Document doc = Jsoup.parse(in, " Hello\nthere \u00a0  ", "http://www.yahoo.co.jp/index.html");
                    String location = doc.location();
                    String baseUri = doc.baseUri();
                    in = new ParseTest().getFile(null);
                    doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    location = doc.location();
                    baseUri = doc.baseUri();
                    org.junit.Assert.fail("testLocationnull43 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testLocationnull43_failAssert0_literalMutationString1342 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testLocationnull43_failAssert0_literalMutationString1342_failAssert0_literalMutationString6007 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString10_literalMutationString648_failAssert0_literalMutationString7312_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
                Document doc = Jsoup.parse(in, "UTF8", "http://www.yahoo.co.jp/indeO.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("");
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString10_literalMutationString648 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString10_literalMutationString648_failAssert0_literalMutationString7312 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString2_failAssert0_literalMutationString1138_failAssert0_literalMutationString6099_failAssert0() throws IOException {
        try {
            {
                {
                    File in = new ParseTest().getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", " SLh`]Sm-8H1Yg2f");
                    String location = doc.location();
                    String baseUri = doc.baseUri();
                    in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
                    doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    location = doc.location();
                    baseUri = doc.baseUri();
                    org.junit.Assert.fail("testLocation_literalMutationString2 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLocation_literalMutationString2_failAssert0_literalMutationString1138 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString2_failAssert0_literalMutationString1138_failAssert0_literalMutationString6099 should have thrown FileNotFoundException");
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
    public void testLocation_literalMutationString18null2469_failAssert0_literalMutationString7954_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/indexhtml");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("");
                doc = Jsoup.parse(in, null, null);
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString18null2469 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString18null2469_failAssert0_literalMutationString7954 should have thrown FileNotFoundException");
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

