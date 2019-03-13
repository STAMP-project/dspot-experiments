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


/**
 * Created by Per Wendel on 2016-02-18.
 */
public class MultipleServicesTest {
    private static Service first;

    private static Service second;

    private static SparkTestUtil firstClient;

    private static SparkTestUtil secondClient;

    @Test
    public void testGetHello() throws Exception {
        SparkTestUtil.UrlResponse response = MultipleServicesTest.firstClient.doMethod("GET", "/hello", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Hello World!", response.body);
    }

    @Test
    public void testGetRedirectedHi() throws Exception {
        SparkTestUtil.UrlResponse response = MultipleServicesTest.secondClient.doMethod("GET", "/hi", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Hello World!", response.body);
    }

    @Test
    public void testGetUniqueForSecondWithFirst() throws Exception {
        SparkTestUtil.UrlResponse response = MultipleServicesTest.firstClient.doMethod("GET", "/uniqueforsecond", null);
        Assert.assertEquals(404, response.status);
    }

    @Test
    public void testGetUniqueForSecondWithSecond() throws Exception {
        SparkTestUtil.UrlResponse response = MultipleServicesTest.secondClient.doMethod("GET", "/uniqueforsecond", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Bompton", response.body);
    }

    @Test
    public void testStaticFileCssStyleCssWithFirst() throws Exception {
        SparkTestUtil.UrlResponse response = MultipleServicesTest.firstClient.doMethod("GET", "/css/style.css", null);
        Assert.assertEquals(404, response.status);
    }

    @Test
    public void testStaticFileCssStyleCssWithSecond() throws Exception {
        SparkTestUtil.UrlResponse response = MultipleServicesTest.secondClient.doMethod("GET", "/css/style.css", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Content of css file", response.body);
    }
}

