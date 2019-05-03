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
    public void testLocation_literalMutationString22_failAssert0_literalMutationString1426_failAssert0_literalMutationString7450_failAssert0() throws IOException {
        try {
            {
                {
                    File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                    String location = doc.location();
                    String baseUri = doc.baseUri();
                    in = new ParseTest().getFile("");
                    doc = Jsoup.parse(in, null, "http://ww.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    location = doc.location();
                    baseUri = doc.baseUri();
                    org.junit.Assert.fail("testLocation_literalMutationString22 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testLocation_literalMutationString22_failAssert0_literalMutationString1426 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString22_failAssert0_literalMutationString1426_failAssert0_literalMutationString7450 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_add37_add1919_literalMutationString4024_failAssert0() throws IOException {
        try {
            File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
            doc.hasParent();
            String location = doc.location();
            String baseUri = doc.baseUri();
            in = new ParseTest().getFile("");
            doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            String o_testLocation_add37__15 = doc.location();
            location = doc.location();
            baseUri = doc.baseUri();
            org.junit.Assert.fail("testLocation_add37_add1919_literalMutationString4024 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString20_failAssert0_literalMutationString1216_failAssert0null12446_failAssert0() throws IOException {
        try {
            {
                {
                    File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
                    Document doc = Jsoup.parse(in, null, "http://www.yahoo.co.jp/index.html");
                    String location = doc.location();
                    String baseUri = doc.baseUri();
                    in = new ParseTest().getFile("");
                    doc = Jsoup.parse(in, null, " Hello\nthere \u00a0  ");
                    location = doc.location();
                    baseUri = doc.baseUri();
                    org.junit.Assert.fail("testLocation_literalMutationString20 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLocation_literalMutationString20_failAssert0_literalMutationString1216 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString20_failAssert0_literalMutationString1216_failAssert0null12446 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString20_failAssert0_literalMutationString1216_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("");
                doc = Jsoup.parse(in, null, " Hello\nthere \u00a0  ");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString20 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString20_failAssert0_literalMutationString1216 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString1_failAssert0_literalMutationString1005_failAssert0_literalMutationString5991_failAssert0() throws IOException {
        try {
            {
                {
                    File in = new ParseTest().getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", " Hello\nthere \u00a0  ");
                    String location = doc.location();
                    String baseUri = doc.baseUri();
                    in = new ParseTest().getFile(" Hello\nthere \u00a0  ");
                    doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    location = doc.location();
                    baseUri = doc.baseUri();
                    org.junit.Assert.fail("testLocation_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLocation_literalMutationString1_failAssert0_literalMutationString1005 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString1_failAssert0_literalMutationString1005_failAssert0_literalMutationString5991 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString1_failAssert0_add2198_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("");
                Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString1_failAssert0_add2198 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString20_failAssert0() throws IOException {
        try {
            File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
            String location = doc.location();
            String baseUri = doc.baseUri();
            in = new ParseTest().getFile("");
            doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            location = doc.location();
            baseUri = doc.baseUri();
            org.junit.Assert.fail("testLocation_literalMutationString20 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString20_failAssert0_add2266_failAssert0() throws IOException {
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
                org.junit.Assert.fail("testLocation_literalMutationString20 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString20_failAssert0_add2266 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_add31_literalMutationString737_failAssert0_add10683_failAssert0() throws IOException {
        try {
            {
                new ParseTest().getFile("/htmltests/yahoo-jp.html");
                File in = new ParseTest().getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                doc.location();
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_add31_literalMutationString737 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_add31_literalMutationString737_failAssert0_add10683 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString1_failAssert0_literalMutationString1005_failAssert0() throws IOException {
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
                org.junit.Assert.fail("testLocation_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString1_failAssert0_literalMutationString1005 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString20_failAssert0_add2263_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                doc.baseUri();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("");
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString20 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString20_failAssert0_add2263 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_add31_literalMutationString737_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testLocation_add31_literalMutationString737 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString20_failAssert0null2583_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
                Document doc = Jsoup.parse(in, null, "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("");
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString20 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString20_failAssert0null2583 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString20_failAssert0_add2263_failAssert0_add11239_failAssert0() throws IOException {
        try {
            {
                {
                    new ParseTest().getFile("/htmltests/yahoo-jp.html");
                    File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                    String location = doc.location();
                    doc.baseUri();
                    String baseUri = doc.baseUri();
                    in = new ParseTest().getFile("");
                    doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    location = doc.location();
                    baseUri = doc.baseUri();
                    org.junit.Assert.fail("testLocation_literalMutationString20 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLocation_literalMutationString20_failAssert0_add2263 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString20_failAssert0_add2263_failAssert0_add11239 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString16_literalMutationString491_failAssert0() throws IOException {
        try {
            File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.ahoo.co.jp/index.html");
            String location = doc.location();
            String baseUri = doc.baseUri();
            in = new ParseTest().getFile("");
            doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            location = doc.location();
            baseUri = doc.baseUri();
            org.junit.Assert.fail("testLocation_literalMutationString16_literalMutationString491 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString20_failAssert0_literalMutationString1216_failAssert0_literalMutationString7535_failAssert0() throws IOException {
        try {
            {
                {
                    File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www#.yahoo.co.jp/index.html");
                    String location = doc.location();
                    String baseUri = doc.baseUri();
                    in = new ParseTest().getFile("");
                    doc = Jsoup.parse(in, null, " Hello\nthere \u00a0  ");
                    location = doc.location();
                    baseUri = doc.baseUri();
                    org.junit.Assert.fail("testLocation_literalMutationString20 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLocation_literalMutationString20_failAssert0_literalMutationString1216 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString20_failAssert0_literalMutationString1216_failAssert0_literalMutationString7535 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString1_failAssert0null2534_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString1_failAssert0null2534 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString20_failAssert0_literalMutationString1216_failAssert0_add11115_failAssert0() throws IOException {
        try {
            {
                {
                    File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                    String location = doc.location();
                    String baseUri = doc.baseUri();
                    in = new ParseTest().getFile("");
                    Jsoup.parse(in, null, " Hello\nthere \u00a0  ");
                    doc = Jsoup.parse(in, null, " Hello\nthere \u00a0  ");
                    location = doc.location();
                    baseUri = doc.baseUri();
                    org.junit.Assert.fail("testLocation_literalMutationString20 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLocation_literalMutationString20_failAssert0_literalMutationString1216 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString20_failAssert0_literalMutationString1216_failAssert0_add11115 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString1_failAssert0() throws IOException {
        try {
            File in = new ParseTest().getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
            String location = doc.location();
            String baseUri = doc.baseUri();
            in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
            doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            location = doc.location();
            baseUri = doc.baseUri();
            org.junit.Assert.fail("testLocation_literalMutationString1 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString20_failAssert0_add2266_failAssert0_add10752_failAssert0() throws IOException {
        try {
            {
                {
                    new ParseTest().getFile("/htmltests/yahoo-jp.html");
                    File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                    String location = doc.location();
                    String baseUri = doc.baseUri();
                    in = new ParseTest().getFile("");
                    doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    doc.location();
                    location = doc.location();
                    baseUri = doc.baseUri();
                    org.junit.Assert.fail("testLocation_literalMutationString20 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLocation_literalMutationString20_failAssert0_add2266 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString20_failAssert0_add2266_failAssert0_add10752 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString7_failAssert0_literalMutationString1136_failAssert0() throws IOException {
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
                org.junit.Assert.fail("testLocation_literalMutationString7 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString7_failAssert0_literalMutationString1136 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString19_failAssert0_literalMutationString1124_failAssert0() throws IOException {
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
                org.junit.Assert.fail("testLocation_literalMutationString19 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString19_failAssert0_literalMutationString1124 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString29null2395_failAssert0_literalMutationString5131_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("");
                doc = Jsoup.parse(in, null, null);
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString29null2395 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString29null2395_failAssert0_literalMutationString5131 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString1_failAssert0_literalMutationString1005_failAssert0_add10625_failAssert0() throws IOException {
        try {
            {
                {
                    File in = new ParseTest().getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                    String location = doc.location();
                    String baseUri = doc.baseUri();
                    in = new ParseTest().getFile(" Hello\nthere \u00a0  ");
                    doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    doc.location();
                    location = doc.location();
                    baseUri = doc.baseUri();
                    org.junit.Assert.fail("testLocation_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLocation_literalMutationString1_failAssert0_literalMutationString1005 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString1_failAssert0_literalMutationString1005_failAssert0_add10625 should have thrown FileNotFoundException");
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

