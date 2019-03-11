/**
 * Copyright 2015 - Per Wendel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spark;


import java.io.File;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.util.SparkTestUtil;


/**
 * Test static files
 */
public class StaticFilesMemberTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(StaticFilesMemberTest.class);

    private static final String FO_SHIZZY = "Fo shizzy";

    private static final String NOT_FOUND_BRO = "Not found bro";

    private static final String EXTERNAL_FILE_NAME_HTML = "externalFile.html";

    private static final String CONTENT_OF_EXTERNAL_FILE = "Content of external file";

    private static SparkTestUtil testUtil;

    private static File tmpExternalFile;

    @Test
    public void testStaticFileCssStyleCss() throws Exception {
        SparkTestUtil.UrlResponse response = StaticFilesMemberTest.testUtil.doMethod("GET", "/css/style.css", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Content of css file", response.body);
        StaticFilesMemberTest.testGet();
    }

    @Test
    public void testStaticFileMjs() throws Exception {
        SparkTestUtil.UrlResponse response = StaticFilesMemberTest.testUtil.doMethod("GET", "/js/module.mjs", null);
        String expectedContentType = response.headers.get("Content-Type");
        Assert.assertEquals(expectedContentType, "application/javascript");
        String body = response.body;
        Assert.assertEquals("export default function () { console.log(\"Hello, I\'m a .mjs file\"); }\n", body);
    }

    @Test
    public void testStaticFilePagesIndexHtml() throws Exception {
        SparkTestUtil.UrlResponse response = StaticFilesMemberTest.testUtil.doMethod("GET", "/pages/index.html", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("<html><body>Hello Static World!</body></html>", response.body);
        StaticFilesMemberTest.testGet();
    }

    @Test
    public void testStaticFilePageHtml() throws Exception {
        SparkTestUtil.UrlResponse response = StaticFilesMemberTest.testUtil.doMethod("GET", "/page.html", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("<html><body>Hello Static Files World!</body></html>", response.body);
        StaticFilesMemberTest.testGet();
    }

    @Test
    public void testExternalStaticFile() throws Exception {
        SparkTestUtil.UrlResponse response = StaticFilesMemberTest.testUtil.doMethod("GET", "/externalFile.html", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Content of external file", response.body);
        StaticFilesMemberTest.testGet();
    }

    @Test
    public void testStaticFileHeaders() throws Exception {
        Spark.staticFiles.headers(new HashMap() {
            {
                put("Server", "Microsoft Word");
                put("Cache-Control", "private, max-age=600");
            }
        });
        SparkTestUtil.UrlResponse response = StaticFilesMemberTest.testUtil.doMethod("GET", "/pages/index.html", null);
        Assert.assertEquals("Microsoft Word", response.headers.get("Server"));
        Assert.assertEquals("private, max-age=600", response.headers.get("Cache-Control"));
        StaticFilesMemberTest.testGet();
    }

    @Test
    public void testStaticFileExpireTime() throws Exception {
        Spark.staticFiles.expireTime(600);
        SparkTestUtil.UrlResponse response = StaticFilesMemberTest.testUtil.doMethod("GET", "/pages/index.html", null);
        Assert.assertEquals("private, max-age=600", response.headers.get("Cache-Control"));
        StaticFilesMemberTest.testGet();
    }

    @Test
    public void testExceptionMapping404() throws Exception {
        SparkTestUtil.UrlResponse response = StaticFilesMemberTest.testUtil.doMethod("GET", "/filethatdoesntexist.html", null);
        Assert.assertEquals(404, response.status);
        Assert.assertEquals(StaticFilesMemberTest.NOT_FOUND_BRO, response.body);
    }
}

