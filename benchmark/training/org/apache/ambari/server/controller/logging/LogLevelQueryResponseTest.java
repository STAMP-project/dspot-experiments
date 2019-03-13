/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.controller.logging;


import JsonSerialize.Inclusion.NON_NULL;
import java.io.StringReader;
import java.util.List;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;
import org.junit.Assert;
import org.junit.Test;


public class LogLevelQueryResponseTest {
    private static final String TEST_JSON_INPUT_LOG_LEVEL_QUERY = "{\"pageSize\":\"0\",\"queryTimeMS\":\"1459970731998\",\"resultSize\":\"6\",\"startIndex\":\"0\",\"totalCount\":\"0\"," + (("\"vNameValues\":[{\"name\":\"FATAL\",\"value\":\"0\"},{\"name\":\"ERROR\",\"value\":\"0\"}," + "{\"name\":\"WARN\",\"value\":\"41\"},{\"name\":\"INFO\",\"value\":\"186\"},{\"name\":\"DEBUG\",\"value\":\"0\"},") + "{\"name\":\"TRACE\",\"value\":\"0\"}]}");

    @Test
    public void testBasicParsing() throws Exception {
        // setup a reader for the test JSON data
        StringReader stringReader = new StringReader(LogLevelQueryResponseTest.TEST_JSON_INPUT_LOG_LEVEL_QUERY);
        // setup the Jackson mapper/reader to read in the data structure
        ObjectMapper mapper = new ObjectMapper();
        AnnotationIntrospector introspector = new JacksonAnnotationIntrospector();
        mapper.setAnnotationIntrospector(introspector);
        mapper.getSerializationConfig().setSerializationInclusion(NON_NULL);
        ObjectReader logLevelQueryResponseReader = mapper.reader(LogLevelQueryResponse.class);
        LogLevelQueryResponse result = logLevelQueryResponseReader.readValue(stringReader);
        // expected values taken from JSON input string declared above
        Assert.assertEquals("startIndex not parsed properly", "0", result.getStartIndex());
        Assert.assertEquals("pageSize not parsed properly", "0", result.getPageSize());
        Assert.assertEquals("totalCount not parsed properly", "0", result.getTotalCount());
        Assert.assertEquals("resultSize not parsed properly", "6", result.getResultSize());
        Assert.assertEquals("queryTimeMS not parsed properly", "1459970731998", result.getQueryTimeMS());
        Assert.assertEquals("Incorrect number of log level count items parsed", 6, result.getNameValueList().size());
        List<NameValuePair> resultList = result.getNameValueList();
        LogLevelQueryResponseTest.assertNameValuePair("FATAL", "0", resultList.get(0));
        LogLevelQueryResponseTest.assertNameValuePair("ERROR", "0", resultList.get(1));
        LogLevelQueryResponseTest.assertNameValuePair("WARN", "41", resultList.get(2));
        LogLevelQueryResponseTest.assertNameValuePair("INFO", "186", resultList.get(3));
        LogLevelQueryResponseTest.assertNameValuePair("DEBUG", "0", resultList.get(4));
        LogLevelQueryResponseTest.assertNameValuePair("TRACE", "0", resultList.get(5));
    }
}

