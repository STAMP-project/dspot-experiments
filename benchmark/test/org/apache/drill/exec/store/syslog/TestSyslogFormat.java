/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.store.syslog;


import TypeProtos.DataMode.OPTIONAL;
import TypeProtos.MinorType.INT;
import TypeProtos.MinorType.TIMESTAMP;
import TypeProtos.MinorType.VARCHAR;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.exec.rpc.RpcException;
import org.apache.drill.test.BaseDirTestWatcher;
import org.apache.drill.test.ClusterTest;
import org.apache.drill.test.rowSet.RowSet;
import org.junit.ClassRule;
import org.junit.Test;


public class TestSyslogFormat extends ClusterTest {
    @ClassRule
    public static final BaseDirTestWatcher dirTestWatcher = new BaseDirTestWatcher();

    @Test
    public void testNonComplexFields() throws RpcException {
        String sql = "SELECT event_date," + (((((((("severity_code," + "severity,") + "facility_code,") + "facility,") + "ip,") + "process_id,") + "message_id,") + "structured_data_text ") + "FROM cp.`syslog/logs.syslog`");
        RowSet results = client.queryBuilder().sql(sql).rowSet();
        TupleMetadata expectedSchema = new SchemaBuilder().add("event_date", TIMESTAMP, OPTIONAL).add("severity_code", INT, OPTIONAL).add("severity", VARCHAR, OPTIONAL).add("facility_code", INT, OPTIONAL).add("facility", VARCHAR, OPTIONAL).add("ip", VARCHAR, OPTIONAL).add("process_id", VARCHAR, OPTIONAL).add("message_id", VARCHAR, OPTIONAL).add("structured_data_text", VARCHAR, OPTIONAL).buildSchema();
        RowSet expected = new org.apache.drill.test.rowSet.RowSetBuilder(client.allocator(), expectedSchema).addRow(1065910455003L, 2, "CRIT", 4, "AUTH", "mymachine.example.com", null, "", "").addRow(482196050520L, 2, "CRIT", 4, "AUTH", "mymachine.example.com", null, "", "").addRow(482196050520L, 2, "CRIT", 4, "AUTH", "mymachine.example.com", null, "", "").addRow(1065910455003L, 2, "CRIT", 4, "AUTH", "mymachine.example.com", null, "", "").addRow(1061727255000L, 2, "CRIT", 4, "AUTH", "mymachine.example.com", null, "", "").addRow(1061727255000L, 5, "NOTICE", 20, "LOCAL4", "192.0.2.1", "8710", "", "").addRow(1065910455003L, 5, "NOTICE", 20, "LOCAL4", "mymachine.example.com", null, "", "{examplePriority@32473=[class=high], exampleSDID@32473=[iut=3, eventSource=Application, eventID=1011]}").addRow(1065910455003L, 5, "NOTICE", 20, "LOCAL4", "mymachine.example.com", null, "", "{examplePriority@32473=[class=high], exampleSDID@32473=[iut=3, eventSource=Application, eventID=1011]}").build();
        new org.apache.drill.test.rowSet.RowSetComparison(expected).verifyAndClearAll(results);
    }

    @Test
    public void testStarQuery() throws RpcException {
        String sql = "SELECT * FROM cp.`syslog/logs1.syslog`";
        RowSet results = client.queryBuilder().sql(sql).rowSet();
        TupleMetadata expectedSchema = new SchemaBuilder().add("event_date", TIMESTAMP, OPTIONAL).add("severity_code", INT, OPTIONAL).add("facility_code", INT, OPTIONAL).add("severity", VARCHAR, OPTIONAL).add("facility", VARCHAR, OPTIONAL).add("ip", VARCHAR, OPTIONAL).add("app_name", VARCHAR, OPTIONAL).add("message_id", VARCHAR, OPTIONAL).add("message", VARCHAR, OPTIONAL).add("process_id", VARCHAR, OPTIONAL).buildSchema();
        RowSet expected = new org.apache.drill.test.rowSet.RowSetBuilder(client.allocator(), expectedSchema).addRow(1065910455003L, 2, 4, "CRIT", "AUTH", "mymachine.example.com", "su", "ID47", "BOM'su root' failed for lonvick on /dev/pts/8", null).addRow(482196050520L, 2, 4, "CRIT", "AUTH", "mymachine.example.com", "su", "ID47", "BOM'su root' failed for lonvick on /dev/pts/8", null).addRow(482196050520L, 2, 4, "CRIT", "AUTH", "mymachine.example.com", "su", "ID47", "BOM'su root' failed for lonvick on /dev/pts/8", null).addRow(1065910455003L, 2, 4, "CRIT", "AUTH", "mymachine.example.com", "su", "ID47", "BOM'su root' failed for lonvick on /dev/pts/8", null).addRow(1061727255000L, 2, 4, "CRIT", "AUTH", "mymachine.example.com", "su", "ID47", "BOM'su root' failed for lonvick on /dev/pts/8", null).addRow(1061727255000L, 5, 20, "NOTICE", "LOCAL4", "192.0.2.1", "myproc", null, "%% It's time to make the do-nuts.", "8710").build();
        new org.apache.drill.test.rowSet.RowSetComparison(expected).verifyAndClearAll(results);
    }

    @Test
    public void testRawQuery() throws RpcException {
        String sql = "SELECT _raw FROM cp.`syslog/logs.syslog`";
        RowSet results = client.queryBuilder().sql(sql).rowSet();
        TupleMetadata expectedSchema = new SchemaBuilder().add("_raw", VARCHAR, OPTIONAL).buildSchema();
        RowSet expected = new org.apache.drill.test.rowSet.RowSetBuilder(client.allocator(), expectedSchema).addRow("<34>1 2003-10-11T22:14:15.003Z mymachine.example.com su - ID47 - BOM'su root' failed for lonvick on /dev/pts/8").addRow("<34>1 1985-04-12T19:20:50.52-04:00 mymachine.example.com su - ID47 - BOM'su root' failed for lonvick on /dev/pts/8").addRow("<34>1 1985-04-12T23:20:50.52Z mymachine.example.com su - ID47 - BOM'su root' failed for lonvick on /dev/pts/8").addRow("<34>1 2003-10-11T22:14:15.003Z mymachine.example.com su - ID47 - BOM'su root' failed for lonvick on /dev/pts/8").addRow("<34>1 2003-08-24T05:14:15.000003-07:00 mymachine.example.com su - ID47 - BOM'su root' failed for lonvick on /dev/pts/8").addRow("<165>1 2003-08-24T05:14:15.000003-07:00 192.0.2.1 myproc 8710 - - %% It's time to make the do-nuts.").addRow("<165>1 2003-10-11T22:14:15.003Z mymachine.example.com evntslog - ID47 [exampleSDID@32473 iut=\"3\" eventSource=\"Application\" eventID=\"1011\"][examplePriority@32473 class=\"high\"]").addRow("<165>1 2003-10-11T22:14:15.003Z mymachine.example.com evntslog - ID47 [exampleSDID@32473 iut=\"3\" eventSource=\"Application\" eventID=\"1011\"][examplePriority@32473 class=\"high\"] - and thats a wrap!").build();
        new org.apache.drill.test.rowSet.RowSetComparison(expected).verifyAndClearAll(results);
    }

    @Test
    public void testStructuredDataQuery() throws RpcException {
        String sql = "SELECT syslog_data.`structured_data`.`UserAgent` AS UserAgent, " + ((((((((((((("syslog_data.`structured_data`.`UserHostAddress` AS UserHostAddress," + "syslog_data.`structured_data`.`BrowserSession` AS BrowserSession,") + "syslog_data.`structured_data`.`Realm` AS Realm,") + "syslog_data.`structured_data`.`Appliance` AS Appliance,") + "syslog_data.`structured_data`.`Company` AS Company,") + "syslog_data.`structured_data`.`UserID` AS UserID,") + "syslog_data.`structured_data`.`PEN` AS PEN,") + "syslog_data.`structured_data`.`HostName` AS HostName,") + "syslog_data.`structured_data`.`Category` AS Category,") + "syslog_data.`structured_data`.`Priority` AS Priority ") + "FROM (") + "SELECT structured_data ") + "FROM cp.`syslog/test.syslog`") + ") AS syslog_data");
        RowSet results = client.queryBuilder().sql(sql).rowSet();
        TupleMetadata expectedSchema = new SchemaBuilder().add("UserAgent", VARCHAR, OPTIONAL).add("UserHostAddress", VARCHAR, OPTIONAL).add("BrowserSession", VARCHAR, OPTIONAL).add("Realm", VARCHAR, OPTIONAL).add("Appliance", VARCHAR, OPTIONAL).add("Company", VARCHAR, OPTIONAL).add("UserID", VARCHAR, OPTIONAL).add("PEN", VARCHAR, OPTIONAL).add("HostName", VARCHAR, OPTIONAL).add("Category", VARCHAR, OPTIONAL).add("Priority", VARCHAR, OPTIONAL).buildSchema();
        RowSet expected = new org.apache.drill.test.rowSet.RowSetBuilder(client.allocator(), expectedSchema).addRow("Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko", "192.168.2.132", "0gvhdi5udjuqtweprbgoxilc", "SecureAuth0", "secureauthqa.gosecureauth.com", "SecureAuth Corporation", "Tester2", "27389", "192.168.2.132", "AUDIT", "4").build();
        new org.apache.drill.test.rowSet.RowSetComparison(expected).verifyAndClearAll(results);
    }

    @Test
    public void testStarFlattenedStructuredDataQuery() throws RpcException {
        String sql = "SELECT * FROM cp.`syslog/test.syslog1`";
        RowSet results = client.queryBuilder().sql(sql).rowSet();
        TupleMetadata expectedSchema = new SchemaBuilder().add("event_date", TIMESTAMP, OPTIONAL).add("severity_code", INT, OPTIONAL).add("facility_code", INT, OPTIONAL).add("severity", VARCHAR, OPTIONAL).add("facility", VARCHAR, OPTIONAL).add("ip", VARCHAR, OPTIONAL).add("app_name", VARCHAR, OPTIONAL).add("process_id", VARCHAR, OPTIONAL).add("message_id", VARCHAR, OPTIONAL).add("structured_data_text", VARCHAR, OPTIONAL).add("structured_data_UserAgent", VARCHAR, OPTIONAL).add("structured_data_UserHostAddress", VARCHAR, OPTIONAL).add("structured_data_BrowserSession", VARCHAR, OPTIONAL).add("structured_data_Realm", VARCHAR, OPTIONAL).add("structured_data_Appliance", VARCHAR, OPTIONAL).add("structured_data_Company", VARCHAR, OPTIONAL).add("structured_data_UserID", VARCHAR, OPTIONAL).add("structured_data_PEN", VARCHAR, OPTIONAL).add("structured_data_HostName", VARCHAR, OPTIONAL).add("structured_data_Category", VARCHAR, OPTIONAL).add("structured_data_Priority", VARCHAR, OPTIONAL).add("message", VARCHAR, OPTIONAL).buildSchema();
        RowSet expected = addRow(1438811939693L, 6, 10, "INFO", "AUTHPRIV", "192.168.2.132", "SecureAuth0", "23108", "ID52020", "{SecureAuth@27389=[UserAgent=Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko, UserHostAddress=192.168.2.132, BrowserSession=0gvhdi5udjuqtweprbgoxilc, Realm=SecureAuth0, Appliance=secureauthqa.gosecureauth.com, Company=SecureAuth Corporation, UserID=Tester2, PEN=27389, HostName=192.168.2.132, Category=AUDIT, Priority=4]}", "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko", "192.168.2.132", "0gvhdi5udjuqtweprbgoxilc", "SecureAuth0", "secureauthqa.gosecureauth.com", "SecureAuth Corporation", "Tester2", "27389", "192.168.2.132", "AUDIT", "4", "Found the user for retrieving user's profile").build();
        new org.apache.drill.test.rowSet.RowSetComparison(expected).verifyAndClearAll(results);
    }

    @Test
    public void testExplicitFlattenedStructuredDataQuery() throws RpcException {
        String sql = "SELECT event_date," + ((((((((((((((((((((("severity_code," + "facility_code,") + "severity,") + "facility,") + "ip,") + "app_name,") + "process_id,") + "message_id,") + "structured_data_text,") + "structured_data_UserAgent,") + "structured_data_UserHostAddress,") + "structured_data_BrowserSession,") + "structured_data_Realm,") + "structured_data_Appliance,") + "structured_data_Company,") + "structured_data_UserID,") + "structured_data_PEN,") + "structured_data_HostName,") + "structured_data_Category,") + "structured_data_Priority,") + "message ") + "FROM cp.`syslog/test.syslog1`");
        RowSet results = client.queryBuilder().sql(sql).rowSet();
        TupleMetadata expectedSchema = new SchemaBuilder().add("event_date", TIMESTAMP, OPTIONAL).add("severity_code", INT, OPTIONAL).add("facility_code", INT, OPTIONAL).add("severity", VARCHAR, OPTIONAL).add("facility", VARCHAR, OPTIONAL).add("ip", VARCHAR, OPTIONAL).add("app_name", VARCHAR, OPTIONAL).add("process_id", VARCHAR, OPTIONAL).add("message_id", VARCHAR, OPTIONAL).add("structured_data_text", VARCHAR, OPTIONAL).add("structured_data_UserAgent", VARCHAR, OPTIONAL).add("structured_data_UserHostAddress", VARCHAR, OPTIONAL).add("structured_data_BrowserSession", VARCHAR, OPTIONAL).add("structured_data_Realm", VARCHAR, OPTIONAL).add("structured_data_Appliance", VARCHAR, OPTIONAL).add("structured_data_Company", VARCHAR, OPTIONAL).add("structured_data_UserID", VARCHAR, OPTIONAL).add("structured_data_PEN", VARCHAR, OPTIONAL).add("structured_data_HostName", VARCHAR, OPTIONAL).add("structured_data_Category", VARCHAR, OPTIONAL).add("structured_data_Priority", VARCHAR, OPTIONAL).add("message", VARCHAR, OPTIONAL).buildSchema();
        RowSet expected = addRow(1438811939693L, 6, 10, "INFO", "AUTHPRIV", "192.168.2.132", "SecureAuth0", "23108", "", "{SecureAuth@27389=[UserAgent=Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko, UserHostAddress=192.168.2.132, BrowserSession=0gvhdi5udjuqtweprbgoxilc, Realm=SecureAuth0, Appliance=secureauthqa.gosecureauth.com, Company=SecureAuth Corporation, UserID=Tester2, PEN=27389, HostName=192.168.2.132, Category=AUDIT, Priority=4]}", "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko", "192.168.2.132", "0gvhdi5udjuqtweprbgoxilc", "SecureAuth0", "secureauthqa.gosecureauth.com", "SecureAuth Corporation", "Tester2", "27389", "192.168.2.132", "AUDIT", "4", "Found the user for retrieving user's profile").build();
        new org.apache.drill.test.rowSet.RowSetComparison(expected).verifyAndClearAll(results);
    }
}

