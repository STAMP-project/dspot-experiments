/**
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package okhttp3;


import java.util.List;
import okhttp3.internal.Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Runs the web platform URL tests against Java URL models.
 */
@RunWith(Parameterized.class)
public final class WebPlatformUrlTest {
    @Parameterized.Parameter
    public WebPlatformUrlTestData testData;

    private static final List<String> HTTP_URL_SCHEMES = Util.immutableList("http", "https");

    private static final List<String> KNOWN_FAILURES = Util.immutableList("Parsing: <http://example\t.\norg> against <http://example.org/foo/bar>", "Parsing: <http://f:0/c> against <http://example.org/foo/bar>", "Parsing: <http://f:00000000000000/c> against <http://example.org/foo/bar>", "Parsing: <http://f:\n/c> against <http://example.org/foo/bar>", "Parsing: <http://f:999999/c> against <http://example.org/foo/bar>", "Parsing: <http://192.0x00A80001> against <about:blank>", "Parsing: <http://%30%78%63%30%2e%30%32%35%30.01> against <http://other.com/>", "Parsing: <http://192.168.0.257> against <http://other.com/>", "Parsing: <http://????????????> against <http://other.com/>");

    /**
     * Test how {@link HttpUrl} does against the web platform test suite.
     */
    @Test
    public void httpUrl() throws Exception {
        if ((!(testData.scheme.isEmpty())) && (!(WebPlatformUrlTest.HTTP_URL_SCHEMES.contains(testData.scheme)))) {
            System.err.println(("Ignoring unsupported scheme " + (testData.scheme)));
            return;
        }
        if (((!(testData.base.startsWith("https:"))) && (!(testData.base.startsWith("http:")))) && (!(testData.base.equals("about:blank")))) {
            System.err.println(("Ignoring unsupported base " + (testData.base)));
            return;
        }
        try {
            testHttpUrl();
            if (WebPlatformUrlTest.KNOWN_FAILURES.contains(testData.toString())) {
                System.err.println(("Expected failure but was success: " + (testData)));
            }
        } catch (Throwable e) {
            if (WebPlatformUrlTest.KNOWN_FAILURES.contains(testData.toString())) {
                System.err.println(("Ignoring known failure: " + (testData)));
                e.printStackTrace();
            } else {
                throw e;
            }
        }
    }
}

