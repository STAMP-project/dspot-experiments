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


public class LogQueryResponseTest {
    private static final String TEST_JSON_INPUT_TWO_LIST_ENTRIES = "{" + ((((((((((((((((((((((((((((((((((((((((((((((((((("  \"startIndex\" : 0," + "  \"pageSize\" : 5,") + "  \"totalCount\" : 10452,") + "  \"resultSize\" : 5,") + "  \"queryTimeMS\" : 1458148754113,") + "  \"logList\" : [ {") + "    \"cluster\" : \"clusterone\",") + "    \"method\" : \"chooseUnderReplicatedBlocks\",") + "    \"level\" : \"INFO\",") + "    \"event_count\" : 1,") + "    \"ip\" : \"192.168.1.1\",") + "    \"type\" : \"hdfs_namenode\",") + "    \"seq_num\" : 10584,") + "    \"path\" : \"/var/log/hadoop/hdfs/hadoop-hdfs-namenode-c6401.ambari.apache.org.log\",") + "    \"file\" : \"UnderReplicatedBlocks.java\",") + "    \"line_number\" : 394,") + "    \"host\" : \"c6401.ambari.apache.org\",") + "    \"log_message\" : \"chooseUnderReplicatedBlocks selected 2 blocks at priority level 0;  Total=2 Reset bookmarks? false\",") + "    \"logger_name\" : \"BlockStateChange\",") + "    \"id\" : \"9c5562fb-123f-47c8-aaf5-b5e407326c08\",") + "    \"message_md5\" : \"-3892769501348410581\",") + "    \"logtime\" : 1458148749036,") + "    \"event_md5\" : \"1458148749036-2417481968206345035\",") + "    \"logfile_line_number\" : 2084,") + "    \"_ttl_\" : \"+7DAYS\",") + "    \"_expire_at_\" : 1458753550322,") + "    \"_version_\" : 1528979784023932928") + "  }, {") + "    \"cluster\" : \"clusterone\",") + "    \"method\" : \"putMetrics\",") + "    \"level\" : \"WARN\",") + "    \"event_count\" : 1,") + "    \"ip\" : \"192.168.1.1\",") + "    \"type\" : \"yarn_resourcemanager\",") + "    \"seq_num\" : 10583,") + "    \"path\" : \"/var/log/hadoop-yarn/yarn/yarn-yarn-resourcemanager-c6401.ambari.apache.org.log\",") + "    \"file\" : \"HadoopTimelineMetricsSink.java\",") + "    \"line_number\" : 262,") + "    \"host\" : \"c6401.ambari.apache.org\",") + "    \"log_message\" : \"Unable to send metrics to collector by address:http://c6401.ambari.apache.org:6188/ws/v1/timeline/metrics\",") + "    \"logger_name\" : \"timeline.HadoopTimelineMetricsSink\",") + "    \"id\" : \"8361c5a9-5b1c-4f44-bc8f-4c6f07d94228\",") + "    \"message_md5\" : \"5942185045779825717\",") + "    \"logtime\" : 1458148746937,") + "    \"event_md5\" : \"14581487469371427138486123628676\",") + "    \"logfile_line_number\" : 549,") + "    \"_ttl_\" : \"+7DAYS\",") + "    \"_expire_at_\" : 1458753550322,") + "    \"_version_\" : 1528979784022884357") + "  }") + "]") + "}");

    @Test
    public void testBasicParsing() throws Exception {
        // setup a reader for the test JSON data
        StringReader stringReader = new StringReader(LogQueryResponseTest.TEST_JSON_INPUT_TWO_LIST_ENTRIES);
        // setup the Jackson mapper/reader to read in the data structure
        ObjectMapper mapper = new ObjectMapper();
        AnnotationIntrospector introspector = new JacksonAnnotationIntrospector();
        mapper.setAnnotationIntrospector(introspector);
        mapper.getSerializationConfig().setSerializationInclusion(NON_NULL);
        ObjectReader logQueryResponseReader = mapper.reader(LogQueryResponse.class);
        LogQueryResponse result = logQueryResponseReader.readValue(stringReader);
        Assert.assertEquals("startIndex not parsed properly", "0", result.getStartIndex());
        Assert.assertEquals("pageSize not parsed properly", "5", result.getPageSize());
        Assert.assertEquals("totalCount not parsed properly", "10452", result.getTotalCount());
        Assert.assertEquals("resultSize not parsed properly", "5", result.getResultSize());
        Assert.assertEquals("queryTimeMS not parsed properly", "1458148754113", result.getQueryTimeMS());
        Assert.assertEquals("incorrect number of LogLineResult items parsed", 2, result.getListOfResults().size());
        List<LogLineResult> listOfLineResults = result.getListOfResults();
        LogQueryResponseTest.verifyFirstLine(listOfLineResults);
        LogQueryResponseTest.verifySecondLine(listOfLineResults);
    }
}

