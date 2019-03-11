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


import RestUrls.URL_TABLE_DATA;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.List;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.flowable.rest.service.BaseSpringRestTestCase;
import org.flowable.rest.service.api.RestUrls;
import org.flowable.task.api.Task;
import org.flowable.variable.service.impl.persistence.entity.VariableInstanceEntity;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for all REST-operations related to the Table columns.
 *
 * @author Frederik Heremans
 */
public class TableDataResourceTest extends BaseSpringRestTestCase {
    /**
     * Test getting a single table's row data. GET management/tables/{tableName}/data
     */
    @Test
    public void testGetTableColumns() throws Exception {
        try {
            Task task = taskService.newTask();
            taskService.saveTask(task);
            taskService.setVariable(task.getId(), "var1", 123);
            taskService.setVariable(task.getId(), "var2", 456);
            taskService.setVariable(task.getId(), "var3", 789);
            // We use variable-table as a reference
            String tableName = managementService.getTableName(VariableInstanceEntity.class);
            CloseableHttpResponse response = executeRequest(new HttpGet(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_TABLE_DATA, tableName)))), HttpStatus.SC_OK);
            // Check paging result
            JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
            closeResponse(response);
            Assert.assertNotNull(responseNode);
            Assert.assertEquals(3, responseNode.get("total").intValue());
            Assert.assertEquals(3, responseNode.get("size").intValue());
            Assert.assertEquals(0, responseNode.get("start").intValue());
            Assert.assertTrue(responseNode.get("order").isNull());
            Assert.assertTrue(responseNode.get("sort").isNull());
            // Check variables are actually returned
            ArrayNode rows = ((ArrayNode) (responseNode.get("data")));
            Assert.assertNotNull(rows);
            Assert.assertEquals(3, rows.size());
            // Check sorting, ascending
            response = executeRequest(new HttpGet((((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_TABLE_DATA, tableName))) + "?orderAscendingColumn=LONG_")), HttpStatus.SC_OK);
            responseNode = objectMapper.readTree(response.getEntity().getContent());
            closeResponse(response);
            Assert.assertNotNull(responseNode);
            Assert.assertEquals(3, responseNode.get("total").intValue());
            Assert.assertEquals(3, responseNode.get("size").intValue());
            Assert.assertEquals(0, responseNode.get("start").intValue());
            Assert.assertEquals("asc", responseNode.get("order").textValue());
            Assert.assertEquals("LONG_", responseNode.get("sort").textValue());
            rows = ((ArrayNode) (responseNode.get("data")));
            Assert.assertNotNull(rows);
            Assert.assertEquals(3, rows.size());
            Assert.assertEquals("var1", rows.get(0).get("NAME_").textValue());
            Assert.assertEquals("var2", rows.get(1).get("NAME_").textValue());
            Assert.assertEquals("var3", rows.get(2).get("NAME_").textValue());
            // Check sorting, descending
            response = executeRequest(new HttpGet((((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_TABLE_DATA, tableName))) + "?orderDescendingColumn=LONG_")), HttpStatus.SC_OK);
            responseNode = objectMapper.readTree(response.getEntity().getContent());
            closeResponse(response);
            Assert.assertNotNull(responseNode);
            Assert.assertEquals(3, responseNode.get("total").intValue());
            Assert.assertEquals(3, responseNode.get("size").intValue());
            Assert.assertEquals(0, responseNode.get("start").intValue());
            Assert.assertEquals("desc", responseNode.get("order").textValue());
            Assert.assertEquals("LONG_", responseNode.get("sort").textValue());
            rows = ((ArrayNode) (responseNode.get("data")));
            Assert.assertNotNull(rows);
            Assert.assertEquals(3, rows.size());
            Assert.assertEquals("var3", rows.get(0).get("NAME_").textValue());
            Assert.assertEquals("var2", rows.get(1).get("NAME_").textValue());
            Assert.assertEquals("var1", rows.get(2).get("NAME_").textValue());
            // Finally, check result limiting
            response = executeRequest(new HttpGet((((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_TABLE_DATA, tableName))) + "?orderAscendingColumn=LONG_&start=1&size=1")), HttpStatus.SC_OK);
            responseNode = objectMapper.readTree(response.getEntity().getContent());
            closeResponse(response);
            Assert.assertNotNull(responseNode);
            Assert.assertEquals(3, responseNode.get("total").intValue());
            Assert.assertEquals(1, responseNode.get("size").intValue());
            Assert.assertEquals(1, responseNode.get("start").intValue());
            rows = ((ArrayNode) (responseNode.get("data")));
            Assert.assertNotNull(rows);
            Assert.assertEquals(1, rows.size());
            Assert.assertEquals("var2", rows.get(0).get("NAME_").textValue());
        } finally {
            // Clean adhoc-tasks even if test fails
            List<Task> tasks = taskService.createTaskQuery().list();
            for (Task task : tasks) {
                taskService.deleteTask(task.getId(), true);
            }
        }
    }

    @Test
    public void testGetDataForUnexistingTable() throws Exception {
        closeResponse(executeRequest(new HttpGet(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_TABLE_DATA, "unexisting")))), HttpStatus.SC_NOT_FOUND));
    }

    @Test
    public void testGetDataSortByIllegalColumn() throws Exception {
        // We use variable-table as a reference
        String tableName = managementService.getTableName(VariableInstanceEntity.class);
        closeResponse(executeRequest(new HttpGet((((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_TABLE_DATA, tableName))) + "?orderAscendingColumn=unexistingColumn")), HttpStatus.SC_INTERNAL_SERVER_ERROR));
    }
}

