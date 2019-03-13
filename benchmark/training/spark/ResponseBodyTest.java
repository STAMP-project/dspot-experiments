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
 * Validates and shows the "rules" for how response "body" is set.
 */
public class ResponseBodyTest {
    public static final String HELLO = "/hello";

    public static final String SPECIAL = "/special";

    public static final String PORAKATIKAOKAO = "/porakatikaokao";

    public static final String MAXIME = "/maxime";

    public static final String HELLO_WORLD = "Hello World!";

    public static final String XIDXUS = "xidxus";

    public static final String $11AB = "$11ab";

    public static final String GALLUS_SCANDALUM = "gallus scandalum";

    private static SparkTestUtil http;

    @Test
    public void testHELLO() {
        try {
            SparkTestUtil.UrlResponse response = ResponseBodyTest.http.get(ResponseBodyTest.HELLO);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals(ResponseBodyTest.HELLO_WORLD, response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSPECIAL() {
        try {
            SparkTestUtil.UrlResponse response = ResponseBodyTest.http.get(ResponseBodyTest.SPECIAL);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals(ResponseBodyTest.XIDXUS, response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testPORAKATIKAOKAO() {
        try {
            SparkTestUtil.UrlResponse response = ResponseBodyTest.http.get(ResponseBodyTest.PORAKATIKAOKAO);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals(ResponseBodyTest.GALLUS_SCANDALUM, response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testMAXIME() {
        try {
            SparkTestUtil.UrlResponse response = ResponseBodyTest.http.get(ResponseBodyTest.MAXIME);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals(ResponseBodyTest.$11AB, response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}

