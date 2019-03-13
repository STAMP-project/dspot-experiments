/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.config;


import com.hazelcast.nio.IOUtil;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestCollectionUtils;
import com.hazelcast.test.annotation.SlowTest;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;


@RunWith(HazelcastSerialClassRunner.class)
@Category(SlowTest.class)
public class XmlConfigSchemaLocationTest extends HazelcastTestSupport {
    // list of schema location URLs which we do not want to check
    private static final Set<String> WHITELIST = // ServiceConfigTest has example of a custom Hazelcast service.
    // The example defines own schema, but the schema is not available anywhere
    // on the internet. It's just an example after all -> the test has to skip it.
    TestCollectionUtils.setOf("hazelcast-sample-service.xsd");

    private static final String XML_SCHEMA_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";

    private static final String XML_SCHEMA_LOCATION_ATTRIBUTE = "schemaLocation";

    private CloseableHttpClient httpClient;

    private DocumentBuilderFactory documentBuilderFactory;

    private Set<String> validUrlsCache;

    @Rule
    public TestName testName = new TestName();

    @Test
    public void testSchemaLocationsExist() throws Exception {
        assumeTls12Available();
        ResourcesScanner scanner = new ResourcesScanner();
        Reflections reflections = new Reflections(scanner);
        Set<String> resources = reflections.getResources(Pattern.compile(".*\\.xml"));
        ClassLoader classLoader = getClass().getClassLoader();
        for (String resource : resources) {
            URL resourceUrl = classLoader.getResource(resource);
            String protocol = resourceUrl.getProtocol();
            // do not validate schemas from JARs (libraries). we are interested in local project files only.
            if (protocol.startsWith("jar")) {
                continue;
            }
            InputStream stream = null;
            try {
                stream = classLoader.getResourceAsStream(resource);
                validateSchemaLocationUrl(stream, resource);
            } finally {
                IOUtil.closeResource(stream);
            }
        }
    }
}

