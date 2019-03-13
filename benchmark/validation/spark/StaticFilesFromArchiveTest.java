/**
 * Copyright 2016 - Per Wendel
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


import org.junit.Assert;
import org.junit.Test;
import spark.util.SparkTestUtil;


public class StaticFilesFromArchiveTest {
    private static SparkTestUtil testUtil;

    private static ClassLoader classLoader;

    private static ClassLoader initialClassLoader;

    @Test
    public void testCss() throws Exception {
        SparkTestUtil.UrlResponse response = StaticFilesFromArchiveTest.testUtil.doMethod("GET", "/css/style.css", null);
        String expectedContentType = response.headers.get("Content-Type");
        Assert.assertEquals(expectedContentType, "text/css");
        String body = response.body;
        Assert.assertEquals("Content of css file", body);
    }
}

