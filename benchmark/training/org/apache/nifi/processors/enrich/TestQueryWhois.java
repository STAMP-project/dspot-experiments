/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.enrich;


import QueryWhois.BATCH_SIZE;
import QueryWhois.BEGIN_END;
import QueryWhois.BULK_PROTOCOL;
import QueryWhois.KEY_GROUP;
import QueryWhois.NONE;
import QueryWhois.QUERY_INPUT;
import QueryWhois.QUERY_PARSER;
import QueryWhois.QUERY_PARSER_INPUT;
import QueryWhois.REGEX;
import QueryWhois.REL_FOUND;
import QueryWhois.REL_NOT_FOUND;
import QueryWhois.SPLIT;
import QueryWhois.WHOIS_QUERY_TYPE;
import QueryWhois.WHOIS_SERVER;
import QueryWhois.WHOIS_TIMEOUT;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.net.whois.WhoisClient;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ WhoisClient.class })
public class TestQueryWhois {
    private QueryWhois queryWhois;

    private TestRunner queryWhoisTestRunner;

    @Test
    public void testCustomValidator() {
        queryWhoisTestRunner.setProperty(WHOIS_SERVER, "127.0.0.1");
        queryWhoisTestRunner.setProperty(WHOIS_QUERY_TYPE, "peer");
        queryWhoisTestRunner.setProperty(WHOIS_TIMEOUT, "1000 ms");
        queryWhoisTestRunner.setProperty(QUERY_INPUT, "nifi.apache.org");
        // Note the absence of a QUERY_PARSER_INPUT value
        queryWhoisTestRunner.setProperty(QUERY_PARSER, REGEX.getValue());
        queryWhoisTestRunner.assertNotValid();
        // Note the presence of a QUERY_PARSER_INPUT value
        queryWhoisTestRunner.setProperty(BATCH_SIZE, "1");
        queryWhoisTestRunner.setProperty(QUERY_PARSER, REGEX.getValue());
        queryWhoisTestRunner.setProperty(QUERY_PARSER_INPUT, "\\|");
        queryWhoisTestRunner.assertValid();
        // Note BULK_PROTOCOL and BATCH_SIZE
        queryWhoisTestRunner.setProperty(BATCH_SIZE, "1");
        queryWhoisTestRunner.setProperty(QUERY_PARSER, REGEX.getValue());
        queryWhoisTestRunner.setProperty(QUERY_PARSER_INPUT, "\\|");
        queryWhoisTestRunner.setProperty(BULK_PROTOCOL, BEGIN_END.getValue());
        queryWhoisTestRunner.assertNotValid();
        // Note the presence of a QUERY_PARSER_INPUT value while NONE is set
        queryWhoisTestRunner.setProperty(BATCH_SIZE, "1");
        queryWhoisTestRunner.setProperty(QUERY_PARSER, NONE.getValue());
        queryWhoisTestRunner.setProperty(QUERY_PARSER_INPUT, "\\|");
        queryWhoisTestRunner.assertNotValid();
        // 
        queryWhoisTestRunner.setProperty(BATCH_SIZE, "10");
        queryWhoisTestRunner.setProperty(QUERY_PARSER, NONE.getValue());
        queryWhoisTestRunner.removeProperty(QUERY_PARSER_INPUT);
        queryWhoisTestRunner.assertNotValid();
        queryWhoisTestRunner.setProperty(BATCH_SIZE, "10");
        queryWhoisTestRunner.setProperty(QUERY_PARSER, REGEX.getValue());
        queryWhoisTestRunner.setProperty(QUERY_PARSER_INPUT, "\\|");
        queryWhoisTestRunner.removeProperty(KEY_GROUP);
        queryWhoisTestRunner.assertNotValid();
        queryWhoisTestRunner.setProperty(BATCH_SIZE, "10");
        queryWhoisTestRunner.setProperty(QUERY_PARSER, REGEX.getValue());
        queryWhoisTestRunner.setProperty(QUERY_PARSER_INPUT, "\\|");
        queryWhoisTestRunner.removeProperty(KEY_GROUP);
        queryWhoisTestRunner.assertNotValid();
        queryWhoisTestRunner.setProperty(QUERY_PARSER, NONE.getValue());
        queryWhoisTestRunner.assertNotValid();
    }

    @Test
    public void testValidDataWithSplit() {
        queryWhoisTestRunner.setProperty(WHOIS_SERVER, "127.0.0.1");
        queryWhoisTestRunner.setProperty(WHOIS_QUERY_TYPE, "origin");
        queryWhoisTestRunner.setProperty(WHOIS_TIMEOUT, "1000 ms");
        queryWhoisTestRunner.setProperty(QUERY_INPUT, ("${ip_address:getDelimitedField(4, '.'):trim()}" + ((".${ip_address:getDelimitedField(3, '.'):trim()}" + ".${ip_address:getDelimitedField(2, '.'):trim()}") + ".${ip_address:getDelimitedField(1, '.'):trim()}")));
        queryWhoisTestRunner.setProperty(QUERY_PARSER, SPLIT.getValue());
        queryWhoisTestRunner.setProperty(QUERY_PARSER_INPUT, "\\s+\\|\\s+");
        queryWhoisTestRunner.setProperty(KEY_GROUP, "2");
        final Map<String, String> attributeMap1 = new HashMap<>();
        final Map<String, String> attributeMap2 = new HashMap<>();
        final Map<String, String> attributeMap3 = new HashMap<>();
        attributeMap1.put("ip_address", "123.123.123.123");
        attributeMap2.put("ip_address", "124.124.124.124");
        attributeMap3.put("ip_address", "125.125.125.125");
        queryWhoisTestRunner.enqueue(new byte[0], attributeMap1);
        queryWhoisTestRunner.enqueue(new byte[0], attributeMap2);
        queryWhoisTestRunner.enqueue(new byte[0], attributeMap3);
        queryWhoisTestRunner.run();
        List<MockFlowFile> matchingResults = queryWhoisTestRunner.getFlowFilesForRelationship(REL_FOUND);
        Assert.assertTrue(((matchingResults.size()) == 2));
        List<MockFlowFile> nonMatchingResults = queryWhoisTestRunner.getFlowFilesForRelationship(REL_NOT_FOUND);
        Assert.assertTrue(((nonMatchingResults.size()) == 1));
        matchingResults.get(0).assertAttributeEquals("enrich.whois.record0.group7", "Apache NiFi");
    }

    @Test
    public void testValidDataWithRegex() {
        queryWhoisTestRunner.setProperty(WHOIS_SERVER, "127.0.0.1");
        queryWhoisTestRunner.setProperty(WHOIS_QUERY_TYPE, "origin");
        queryWhoisTestRunner.setProperty(WHOIS_TIMEOUT, "1000 ms");
        queryWhoisTestRunner.setProperty(KEY_GROUP, "2");
        queryWhoisTestRunner.setProperty(QUERY_INPUT, ("${ip_address:getDelimitedField(4, '.'):trim()}" + ((".${ip_address:getDelimitedField(3, '.'):trim()}" + ".${ip_address:getDelimitedField(2, '.'):trim()}") + ".${ip_address:getDelimitedField(1, '.'):trim()}")));
        queryWhoisTestRunner.setProperty(QUERY_PARSER, REGEX.getValue());
        queryWhoisTestRunner.setProperty(QUERY_PARSER_INPUT, "\n^([^\\|]*)\\|\\s+(\\S+)\\s+\\|([^\\|]*)\\|([^\\|]*)\\|([^\\|]*)\\|([^\\|]*)\\|([^\\|]*)\\|(.*)$");
        final Map<String, String> attributeMap1 = new HashMap<>();
        final Map<String, String> attributeMap2 = new HashMap<>();
        final Map<String, String> attributeMap3 = new HashMap<>();
        attributeMap1.put("ip_address", "123.123.123.123");
        attributeMap2.put("ip_address", "124.124.124.124");
        attributeMap3.put("ip_address", "125.125.125.125");
        queryWhoisTestRunner.enqueue(new byte[0], attributeMap1);
        queryWhoisTestRunner.enqueue(new byte[0], attributeMap2);
        queryWhoisTestRunner.enqueue(new byte[0], attributeMap3);
        queryWhoisTestRunner.run();
        List<MockFlowFile> matchingResults = queryWhoisTestRunner.getFlowFilesForRelationship(REL_FOUND);
        Assert.assertTrue(((matchingResults.size()) == 2));
        List<MockFlowFile> nonMatchingResults = queryWhoisTestRunner.getFlowFilesForRelationship(REL_NOT_FOUND);
        Assert.assertTrue(((nonMatchingResults.size()) == 1));
        matchingResults.get(0).assertAttributeEquals("enrich.whois.record0.group8", " Apache NiFi");
    }

    @Test
    public void testValidDataWithRegexButInvalidCaptureGroup() {
        queryWhoisTestRunner.setProperty(WHOIS_SERVER, "127.0.0.1");
        queryWhoisTestRunner.setProperty(WHOIS_QUERY_TYPE, "origin");
        queryWhoisTestRunner.setProperty(WHOIS_TIMEOUT, "1000 ms");
        queryWhoisTestRunner.setProperty(KEY_GROUP, "9");
        queryWhoisTestRunner.setProperty(QUERY_INPUT, ("${ip_address:getDelimitedField(4, '.'):trim()}" + ((".${ip_address:getDelimitedField(3, '.'):trim()}" + ".${ip_address:getDelimitedField(2, '.'):trim()}") + ".${ip_address:getDelimitedField(1, '.'):trim()}")));
        queryWhoisTestRunner.setProperty(QUERY_PARSER, REGEX.getValue());
        queryWhoisTestRunner.setProperty(QUERY_PARSER_INPUT, "\n^([^\\|]*)\\|\\s+(\\S+)\\s+\\|([^\\|]*)\\|([^\\|]*)\\|([^\\|]*)\\|([^\\|]*)\\|([^\\|]*)\\|(.*)$");
        final Map<String, String> attributeMap1 = new HashMap<>();
        final Map<String, String> attributeMap2 = new HashMap<>();
        final Map<String, String> attributeMap3 = new HashMap<>();
        attributeMap1.put("ip_address", "123.123.123.123");
        attributeMap2.put("ip_address", "124.124.124.124");
        attributeMap3.put("ip_address", "125.125.125.125");
        queryWhoisTestRunner.enqueue(new byte[0], attributeMap1);
        queryWhoisTestRunner.enqueue(new byte[0], attributeMap2);
        queryWhoisTestRunner.enqueue(new byte[0], attributeMap3);
        queryWhoisTestRunner.run();
        List<MockFlowFile> matchingResults = queryWhoisTestRunner.getFlowFilesForRelationship(REL_FOUND);
        Assert.assertTrue(((matchingResults.size()) == 0));
        List<MockFlowFile> nonMatchingResults = queryWhoisTestRunner.getFlowFilesForRelationship(REL_NOT_FOUND);
        Assert.assertTrue(((nonMatchingResults.size()) == 3));
    }
}

