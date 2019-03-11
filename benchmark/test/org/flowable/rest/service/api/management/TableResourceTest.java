/**
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
package org.flowable.rest.service.api.management;


import RestUrls.URL_TABLE;
import RestUrls.URL_TABLES_COLLECTION;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.flowable.rest.service.BaseSpringRestTestCase;
import org.flowable.rest.service.api.RestUrls;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for all REST-operations related to the Table collection and a single table resource.
 *
 * @author Frederik Heremans
 */
public class TableResourceTest extends BaseSpringRestTestCase {
    /**
     * Test getting tables. GET management/tables
     */
    @Test
    public void testGetTables() throws Exception {
        Map<String, Long> tableCounts = managementService.getTableCount();
        CloseableHttpResponse response = executeRequest(new HttpGet(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_TABLES_COLLECTION)))), HttpStatus.SC_OK);
        // Check table array
        JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
        closeResponse(response);
        Assert.assertNotNull(responseNode);
        Assert.assertTrue(responseNode.isArray());
        Assert.assertEquals(tableCounts.size(), responseNode.size());
        for (int i = 0; i < (responseNode.size()); i++) {
            ObjectNode table = ((ObjectNode) (responseNode.get(i)));
            Assert.assertNotNull(table.get("name").textValue());
            Assert.assertNotNull(table.get("count").longValue());
            Assert.assertTrue(table.get("url").textValue().endsWith(RestUrls.createRelativeResourceUrl(URL_TABLE, table.get("name").textValue())));
            Assert.assertEquals(tableCounts.get(table.get("name").textValue()).longValue(), table.get("count").longValue());
        }
    }

    /**
     * Test getting a single table. GET management/tables/{tableName}
     */
    @Test
    public void testGetTable() throws Exception {
        Map<String, Long> tableCounts = managementService.getTableCount();
        String tableNameToGet = tableCounts.keySet().iterator().next();
        CloseableHttpResponse response = executeRequest(new HttpGet(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_TABLE, tableNameToGet)))), HttpStatus.SC_OK);
        // Check table
        JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
        closeResponse(response);
        Assert.assertNotNull(responseNode);
        Assert.assertEquals(tableNameToGet, responseNode.get("name").textValue());
        Assert.assertEquals(tableCounts.get(responseNode.get("name").textValue()).longValue(), responseNode.get("count").longValue());
        Assert.assertTrue(responseNode.get("url").textValue().endsWith(RestUrls.createRelativeResourceUrl(URL_TABLE, tableNameToGet)));
    }

    @Test
    public void testGetUnexistingTable() throws Exception {
        closeResponse(executeRequest(new HttpGet(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_TABLE, "unexisting")))), HttpStatus.SC_NOT_FOUND));
    }
}

