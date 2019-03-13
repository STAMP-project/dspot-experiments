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
package org.apache.ambari.server.api.services;


import MediaType.APPLICATION_JSON_TYPE;
import MediaType.TEXT_PLAIN_TYPE;
import Resource.Type;
import Resource.Type.Cluster;
import ResultStatus.STATUS;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.ambari.server.api.resources.ResourceInstance;
import org.apache.ambari.server.api.services.parsers.RequestBodyParser;
import org.apache.ambari.server.api.services.serializers.ResultSerializer;
import org.apache.ambari.server.api.services.views.ViewSubResourceService;
import org.apache.ambari.server.api.util.TreeNode;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.orm.entities.ViewInstanceEntity;
import org.apache.ambari.server.orm.entities.ViewInstanceEntityTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * ViewSubResourceService tests
 */
public class ViewSubResourceServiceTest extends BaseServiceTest {
    @Test
    public void testGetResultSerializer_Text() throws Exception {
        UriInfo uriInfo = createMock(UriInfo.class);
        Resource resource = createMock(Resource.class);
        Result result = new ResultImpl(true);
        result.setResultStatus(new ResultStatus(STATUS.OK));
        TreeNode<Resource> tree = result.getResultTree();
        TreeNode<Resource> child = tree.addChild(resource, "resource1");
        child.setProperty("href", "this is an href");
        // resource properties
        Map<String, Object> mapRootProps = new LinkedHashMap<>();
        mapRootProps.put("prop2", "value2");
        mapRootProps.put("prop1", "value1");
        Map<String, Object> mapCategoryProps = new LinkedHashMap<>();
        mapCategoryProps.put("catProp1", "catValue1");
        mapCategoryProps.put("catProp2", "catValue2");
        Map<String, Map<String, Object>> propertyMap = new LinkedHashMap<>();
        propertyMap.put(null, mapRootProps);
        propertyMap.put("category", mapCategoryProps);
        // expectations
        expect(resource.getPropertiesMap()).andReturn(propertyMap).anyTimes();
        expect(resource.getType()).andReturn(Cluster).anyTimes();
        replay(uriInfo, resource);
        // execute test
        ViewInstanceEntity viewInstanceEntity = ViewInstanceEntityTest.getViewInstanceEntity();
        Resource.Type type = new Resource.Type("subResource");
        // get resource
        ViewSubResourceService service = new ViewSubResourceService(type, viewInstanceEntity);
        ResultSerializer serializer = service.getResultSerializer(TEXT_PLAIN_TYPE);
        Object o = serializer.serialize(result);
        String expected = "{\n" + ((((((("  \"href\" : \"this is an href\",\n" + "  \"prop2\" : \"value2\",\n") + "  \"prop1\" : \"value1\",\n") + "  \"category\" : {\n") + "    \"catProp1\" : \"catValue1\",\n") + "    \"catProp2\" : \"catValue2\"\n") + "  }\n") + "}");
        Assert.assertEquals(expected, o.toString().replace("\r", ""));
        verify(uriInfo, resource);
    }

    @Test
    public void testGetResultSerializer_Json() throws Exception {
        UriInfo uriInfo = createMock(UriInfo.class);
        Resource resource = createMock(Resource.class);
        Result result = new ResultImpl(true);
        result.setResultStatus(new ResultStatus(STATUS.OK));
        TreeNode<Resource> tree = result.getResultTree();
        TreeNode<Resource> child = tree.addChild(resource, "resource1");
        child.setProperty("href", "this is an href");
        // resource properties
        HashMap<String, Object> mapRootProps = new HashMap<>();
        mapRootProps.put("prop1", "value1");
        mapRootProps.put("prop2", "value2");
        HashMap<String, Object> mapCategoryProps = new HashMap<>();
        mapCategoryProps.put("catProp1", "catValue1");
        mapCategoryProps.put("catProp2", "catValue2");
        Map<String, Map<String, Object>> propertyMap = new HashMap<>();
        propertyMap.put(null, mapRootProps);
        propertyMap.put("category", mapCategoryProps);
        // expectations
        expect(resource.getPropertiesMap()).andReturn(propertyMap).anyTimes();
        expect(resource.getType()).andReturn(Cluster).anyTimes();
        replay(uriInfo, resource);
        // execute test
        ViewInstanceEntity viewInstanceEntity = ViewInstanceEntityTest.getViewInstanceEntity();
        Resource.Type type = new Resource.Type("subResource");
        // get resource
        ViewSubResourceService service = new ViewSubResourceService(type, viewInstanceEntity);
        ResultSerializer serializer = service.getResultSerializer(APPLICATION_JSON_TYPE);
        Object o = serializer.serialize(result);
        Assert.assertTrue((o instanceof Map));
        Map map = ((Map) (o));
        Assert.assertEquals(4, map.size());
        Assert.assertEquals("value1", map.get("prop1"));
        Assert.assertEquals("value2", map.get("prop2"));
        Assert.assertEquals("this is an href", map.get("href"));
        Object o2 = map.get("category");
        Assert.assertNotNull(o2);
        Assert.assertTrue((o2 instanceof Map));
        Map subMap = ((Map) (o2));
        Assert.assertEquals(2, subMap.size());
        Assert.assertEquals("catValue1", subMap.get("catProp1"));
        Assert.assertEquals("catValue2", subMap.get("catProp2"));
        verify(uriInfo, resource);
    }

    private class TestViewSubResourceService extends ViewSubResourceService {
        /**
         * Construct a view sub-resource service.
         */
        public TestViewSubResourceService(Resource.Type type, ViewInstanceEntity viewInstanceDefinition) {
            super(type, viewInstanceDefinition);
        }

        public Response getSubResource1(@Context
        HttpHeaders headers, @Context
        UriInfo ui, @PathParam("resourceId")
        String resourceId) {
            return handleRequest(headers, ui, resourceId);
        }

        public Response getSubResource2(@Context
        HttpHeaders headers, @Context
        UriInfo ui, @PathParam("resourceId")
        String resourceId) {
            return handleRequest(headers, ui, RequestType.GET, MediaType.TEXT_PLAIN, resourceId);
        }

        public Response postSubResource(@Context
        HttpHeaders headers, @Context
        UriInfo ui, @PathParam("resourceId")
        String resourceId) {
            return handleRequest(headers, ui, RequestType.POST, MediaType.TEXT_PLAIN, resourceId);
        }

        public Response putSubResource(@Context
        HttpHeaders headers, @Context
        UriInfo ui, @PathParam("resourceId")
        String resourceId) {
            return handleRequest(headers, ui, RequestType.PUT, MediaType.TEXT_PLAIN, resourceId);
        }

        public Response deleteSubResource(@Context
        HttpHeaders headers, @Context
        UriInfo ui, @PathParam("resourceId")
        String resourceId) {
            return handleRequest(headers, ui, RequestType.DELETE, MediaType.TEXT_PLAIN, resourceId);
        }

        @Override
        protected ResourceInstance createResource(String resourceId) {
            return getTestResource();
        }

        @Override
        RequestFactory getRequestFactory() {
            return getTestRequestFactory();
        }

        @Override
        protected RequestBodyParser getBodyParser() {
            return getTestBodyParser();
        }

        @Override
        protected ResultSerializer getResultSerializer() {
            return getTestResultSerializer();
        }

        @Override
        protected ResultSerializer getResultSerializer(MediaType mediaType) {
            return getTestResultSerializer();
        }
    }
}

