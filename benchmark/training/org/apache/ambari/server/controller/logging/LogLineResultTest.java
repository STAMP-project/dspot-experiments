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
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;
import org.junit.Assert;
import org.junit.Test;


public class LogLineResultTest {
    private static final String TEST_JSON_DATA_SINGLE_ENTRY = "{" + (((((((((((((((((((((("    \"cluster\" : \"clusterone\"," + "    \"method\" : \"chooseUnderReplicatedBlocks\",") + "    \"level\" : \"INFO\",") + "    \"event_count\" : 1,") + "    \"ip\" : \"192.168.1.1\",") + "    \"type\" : \"hdfs_namenode\",") + "    \"thread_name\" : \"thread-id-one\",") + "    \"seq_num\" : 10584,") + "    \"path\" : \"/var/log/hadoop/hdfs/hadoop-hdfs-namenode-c6401.ambari.apache.org.log\",") + "    \"file\" : \"UnderReplicatedBlocks.java\",") + "    \"line_number\" : 394,") + "    \"host\" : \"c6401.ambari.apache.org\",") + "    \"log_message\" : \"chooseUnderReplicatedBlocks selected 2 blocks at priority level 0;  Total=2 Reset bookmarks? false\",") + "    \"logger_name\" : \"BlockStateChange\",") + "    \"id\" : \"9c5562fb-123f-47c8-aaf5-b5e407326c08\",") + "    \"message_md5\" : \"-3892769501348410581\",") + "    \"logtime\" : 1458148749036,") + "    \"event_md5\" : \"1458148749036-2417481968206345035\",") + "    \"logfile_line_number\" : 2084,") + "    \"_ttl_\" : \"+7DAYS\",") + "    \"_expire_at_\" : 1458753550322,") + "    \"_version_\" : 1528979784023932928") + "  }");

    @Test
    public void testBasicParsing() throws Exception {
        // setup a reader for the test JSON data
        StringReader stringReader = new StringReader(LogLineResultTest.TEST_JSON_DATA_SINGLE_ENTRY);
        // setup the Jackson mapper/reader to read in the data structure
        ObjectMapper mapper = new ObjectMapper();
        AnnotationIntrospector introspector = new JacksonAnnotationIntrospector();
        mapper.setAnnotationIntrospector(introspector);
        mapper.getSerializationConfig().setSerializationInclusion(NON_NULL);
        ObjectReader logLineResultReader = mapper.reader(LogLineResult.class);
        LogLineResult result = logLineResultReader.readValue(stringReader);
        // verify that all fields in this class are parsed as expected
        Assert.assertEquals("Cluster name not parsed properly", "clusterone", result.getClusterName());
        Assert.assertEquals("Method Name not parsed properly", "chooseUnderReplicatedBlocks", result.getLogMethod());
        Assert.assertEquals("Log Level not parsed properly", "INFO", result.getLogLevel());
        Assert.assertEquals("event_count not parsed properly", "1", result.getEventCount());
        Assert.assertEquals("ip address not parsed properly", "192.168.1.1", result.getIpAddress());
        Assert.assertEquals("component type not parsed properly", "hdfs_namenode", result.getComponentType());
        Assert.assertEquals("sequence number not parsed properly", "10584", result.getSequenceNumber());
        Assert.assertEquals("log file path not parsed properly", "/var/log/hadoop/hdfs/hadoop-hdfs-namenode-c6401.ambari.apache.org.log", result.getLogFilePath());
        Assert.assertEquals("log src file name not parsed properly", "UnderReplicatedBlocks.java", result.getSourceFile());
        Assert.assertEquals("log src line number not parsed properly", "394", result.getSourceFileLineNumber());
        Assert.assertEquals("host name not parsed properly", "c6401.ambari.apache.org", result.getHostName());
        Assert.assertEquals("log message not parsed properly", "chooseUnderReplicatedBlocks selected 2 blocks at priority level 0;  Total=2 Reset bookmarks? false", result.getLogMessage());
        Assert.assertEquals("logger name not parsed properly", "BlockStateChange", result.getLoggerName());
        Assert.assertEquals("id not parsed properly", "9c5562fb-123f-47c8-aaf5-b5e407326c08", result.getId());
        Assert.assertEquals("message MD5 not parsed properly", "-3892769501348410581", result.getMessageMD5());
        Assert.assertEquals("log time not parsed properly", "1458148749036", result.getLogTime());
        Assert.assertEquals("event MD5 not parsed properly", "1458148749036-2417481968206345035", result.getEventMD5());
        Assert.assertEquals("logfile line number not parsed properly", "2084", result.getLogFileLineNumber());
        Assert.assertEquals("ttl not parsed properly", "+7DAYS", result.getTtl());
        Assert.assertEquals("expire at not parsed properly", "1458753550322", result.getExpirationTime());
        Assert.assertEquals("version not parsed properly", "1528979784023932928", result.getVersion());
        Assert.assertEquals("thread_name not parsed properly", "thread-id-one", result.getThreadName());
    }
}

