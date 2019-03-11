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


import QueryDNS.DNS_QUERY_TYPE;
import QueryDNS.DNS_RETRIES;
import QueryDNS.DNS_TIMEOUT;
import QueryDNS.NONE;
import QueryDNS.QUERY_INPUT;
import QueryDNS.QUERY_PARSER;
import QueryDNS.QUERY_PARSER_INPUT;
import QueryDNS.REGEX;
import QueryDNS.REL_FOUND;
import QueryDNS.REL_NOT_FOUND;
import QueryDNS.SPLIT;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;


public class TestQueryDNS {
    private QueryDNS queryDNS;

    private TestRunner queryDNSTestRunner;

    @Test
    public void testVanillaQueryWithoutSplit() {
        queryDNSTestRunner.setProperty(DNS_QUERY_TYPE, "PTR");
        queryDNSTestRunner.setProperty(DNS_RETRIES, "1");
        queryDNSTestRunner.setProperty(DNS_TIMEOUT, "1000 ms");
        queryDNSTestRunner.setProperty(QUERY_INPUT, ("${ip_address:getDelimitedField(4, '.'):trim()}" + (((".${ip_address:getDelimitedField(3, '.'):trim()}" + ".${ip_address:getDelimitedField(2, '.'):trim()}") + ".${ip_address:getDelimitedField(1, '.'):trim()}") + ".in-addr.arpa")));
        queryDNSTestRunner.setProperty(QUERY_PARSER, NONE.getValue());
        final Map<String, String> attributeMap = new HashMap<>();
        attributeMap.put("ip_address", "123.123.123.123");
        queryDNSTestRunner.enqueue(new byte[0], attributeMap);
        queryDNSTestRunner.enqueue("teste teste teste chocolate".getBytes());
        queryDNSTestRunner.run(1, true, false);
        List<MockFlowFile> results = queryDNSTestRunner.getFlowFilesForRelationship(REL_FOUND);
        Assert.assertTrue(((results.size()) == 1));
        String result = results.get(0).getAttribute("enrich.dns.record0.group0");
        Assert.assertTrue(result.contains("apache.nifi.org"));
    }

    @Test
    public void testValidDataWithSplit() {
        queryDNSTestRunner.setProperty(DNS_QUERY_TYPE, "TXT");
        queryDNSTestRunner.setProperty(DNS_RETRIES, "1");
        queryDNSTestRunner.setProperty(DNS_TIMEOUT, "1000 ms");
        queryDNSTestRunner.setProperty(QUERY_INPUT, ("${ip_address:getDelimitedField(4, '.'):trim()}" + (((".${ip_address:getDelimitedField(3, '.'):trim()}" + ".${ip_address:getDelimitedField(2, '.'):trim()}") + ".${ip_address:getDelimitedField(1, '.'):trim()}") + ".origin.asn.nifi.apache.org")));
        queryDNSTestRunner.setProperty(QUERY_PARSER, SPLIT.getValue());
        queryDNSTestRunner.setProperty(QUERY_PARSER_INPUT, "\\|");
        final Map<String, String> attributeMap = new HashMap<>();
        attributeMap.put("ip_address", "123.123.123.123");
        queryDNSTestRunner.enqueue(new byte[0], attributeMap);
        queryDNSTestRunner.enqueue("teste teste teste chocolate".getBytes());
        queryDNSTestRunner.run(1, true, false);
        List<MockFlowFile> results = queryDNSTestRunner.getFlowFilesForRelationship(REL_FOUND);
        Assert.assertTrue(((results.size()) == 1));
        results.get(0).assertAttributeEquals("enrich.dns.record0.group5", " Apache NiFi");
    }

    @Test
    public void testValidDataWithRegex() {
        queryDNSTestRunner.setProperty(DNS_QUERY_TYPE, "TXT");
        queryDNSTestRunner.setProperty(DNS_RETRIES, "1");
        queryDNSTestRunner.setProperty(DNS_TIMEOUT, "1000 ms");
        queryDNSTestRunner.setProperty(QUERY_INPUT, ("${ip_address:getDelimitedField(4, '.'):trim()}" + (((".${ip_address:getDelimitedField(3, '.'):trim()}" + ".${ip_address:getDelimitedField(2, '.'):trim()}") + ".${ip_address:getDelimitedField(1, '.'):trim()}") + ".origin.asn.nifi.apache.org")));
        queryDNSTestRunner.setProperty(QUERY_PARSER, REGEX.getValue());
        queryDNSTestRunner.setProperty(QUERY_PARSER_INPUT, "\\.*(\\sApache\\sNiFi)$");
        final Map<String, String> attributeMap = new HashMap<>();
        attributeMap.put("ip_address", "123.123.123.123");
        queryDNSTestRunner.enqueue(new byte[0], attributeMap);
        queryDNSTestRunner.enqueue("teste teste teste chocolate".getBytes());
        queryDNSTestRunner.run(1, true, false);
        List<MockFlowFile> results = queryDNSTestRunner.getFlowFilesForRelationship(REL_FOUND);
        Assert.assertTrue(((results.size()) == 1));
        results.get(0).assertAttributeEquals("enrich.dns.record0.group0", " Apache NiFi");
    }

    @Test
    public void testInvalidData() {
        queryDNSTestRunner.removeProperty(QUERY_PARSER_INPUT);
        queryDNSTestRunner.setProperty(DNS_QUERY_TYPE, "AAAA");
        queryDNSTestRunner.setProperty(DNS_RETRIES, "1");
        queryDNSTestRunner.setProperty(DNS_TIMEOUT, "1000 ms");
        queryDNSTestRunner.setProperty(QUERY_INPUT, "nifi.apache.org");
        final Map<String, String> attributeMap = new HashMap<>();
        attributeMap.put("ip_address", "123.123.123.123");
        queryDNSTestRunner.enqueue(new byte[0], attributeMap);
        queryDNSTestRunner.enqueue("teste teste teste chocolate".getBytes());
        queryDNSTestRunner.run(1, true, false);
        List<MockFlowFile> results = queryDNSTestRunner.getFlowFilesForRelationship(REL_NOT_FOUND);
        Assert.assertTrue(((results.size()) == 1));
    }

    @Test
    public void testCustomValidator() {
        queryDNSTestRunner.setProperty(DNS_QUERY_TYPE, "AAAA");
        queryDNSTestRunner.setProperty(DNS_RETRIES, "1");
        queryDNSTestRunner.setProperty(DNS_TIMEOUT, "1000 ms");
        queryDNSTestRunner.setProperty(QUERY_INPUT, "nifi.apache.org");
        // Note the absence of a QUERY_PARSER_INPUT value
        queryDNSTestRunner.setProperty(QUERY_PARSER, REGEX.getValue());
        queryDNSTestRunner.assertNotValid();
        // Note the presence of a QUERY_PARSER_INPUT value
        queryDNSTestRunner.setProperty(QUERY_PARSER, REGEX.getValue());
        queryDNSTestRunner.setProperty(QUERY_PARSER_INPUT, "\\|");
        queryDNSTestRunner.assertValid();
        // Note the presence of a QUERY_PARSER_INPUT value while NONE is set
        queryDNSTestRunner.setProperty(QUERY_PARSER, NONE.getValue());
        queryDNSTestRunner.setProperty(QUERY_PARSER_INPUT, "\\|");
        queryDNSTestRunner.assertNotValid();
    }
}

