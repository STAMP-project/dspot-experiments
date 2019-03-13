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
package org.apache.ambari.server.controller.internal;


import Request.REQUEST_INFO_BODY_PROPERTY;
import RequestStatus.Status.Complete;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.persistence.EntityManager;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.RequestStatus;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.utilities.PredicateBuilder;
import org.apache.ambari.server.orm.dao.ArtifactDAO;
import org.apache.ambari.server.orm.entities.ArtifactEntity;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.easymock.Capture;
import org.easymock.IAnswer;
import org.junit.Assert;
import org.junit.Test;


/**
 * ArtifactResourceProvider unit tests.
 */
@SuppressWarnings("unchecked")
public class ArtifactResourceProviderTest {
    private ArtifactDAO dao = createStrictMock(ArtifactDAO.class);

    private EntityManager em = createStrictMock(EntityManager.class);

    private AmbariManagementController controller = createStrictMock(AmbariManagementController.class);

    private Request request = createMock(Request.class);

    private Clusters clusters = createMock(Clusters.class);

    private Cluster cluster = createMock(Cluster.class);

    private ArtifactEntity entity = createMock(ArtifactEntity.class);

    private ArtifactEntity entity2 = createMock(ArtifactEntity.class);

    ArtifactResourceProvider resourceProvider;

    @Test
    public void testGetResources_instance() throws Exception {
        Set<String> propertyIds = new HashSet<>();
        TreeMap<String, String> foreignKeys = new TreeMap<>();
        foreignKeys.put("cluster", "500");
        Map<String, Object> childMap = new TreeMap<>();
        childMap.put("childKey", "childValue");
        Map<String, Object> child2Map = new TreeMap<>();
        childMap.put("child2", child2Map);
        child2Map.put("child2Key", "child2Value");
        Map<String, Object> child3Map = new TreeMap<>();
        child2Map.put("child3", child3Map);
        Map<String, Object> child4Map = new TreeMap<>();
        child3Map.put("child4", child4Map);
        child4Map.put("child4Key", "child4Value");
        Map<String, Object> artifact_data = new TreeMap<>();
        artifact_data.put("foo", "bar");
        artifact_data.put("child", childMap);
        Map<String, String> responseForeignKeys = new HashMap<>();
        responseForeignKeys.put("cluster", "500");
        // expectations
        expect(controller.getClusters()).andReturn(clusters).anyTimes();
        expect(clusters.getCluster("test-cluster")).andReturn(cluster).anyTimes();
        expect(clusters.getClusterById(500L)).andReturn(cluster).anyTimes();
        expect(cluster.getClusterId()).andReturn(500L).anyTimes();
        expect(cluster.getClusterName()).andReturn("test-cluster").anyTimes();
        expect(request.getPropertyIds()).andReturn(propertyIds).anyTimes();
        expect(dao.findByNameAndForeignKeys(eq("test-artifact"), eq(foreignKeys))).andReturn(entity).once();
        expect(entity.getArtifactName()).andReturn("test-artifact").anyTimes();
        expect(entity.getForeignKeys()).andReturn(responseForeignKeys).anyTimes();
        expect(entity.getArtifactData()).andReturn(artifact_data).anyTimes();
        // end of expectation setting
        replay(dao, em, controller, request, clusters, cluster, entity, entity2);
        // test
        PredicateBuilder pb = new PredicateBuilder();
        Predicate predicate = pb.begin().property("Artifacts/cluster_name").equals("test-cluster").and().property("Artifacts/artifact_name").equals("test-artifact").end().toPredicate();
        Set<Resource> response = resourceProvider.getResources(request, predicate);
        Assert.assertEquals(1, response.size());
        Resource resource = response.iterator().next();
        Map<String, Map<String, Object>> responseProperties = resource.getPropertiesMap();
        Assert.assertEquals(5, responseProperties.size());
        Map<String, Object> artifactDataMap = responseProperties.get("artifact_data");
        Assert.assertEquals("bar", artifactDataMap.get("foo"));
        Assert.assertEquals("test-artifact", resource.getPropertyValue("Artifacts/artifact_name"));
        Assert.assertEquals("test-cluster", resource.getPropertyValue("Artifacts/cluster_name"));
        Assert.assertEquals("bar", resource.getPropertyValue("artifact_data/foo"));
        Assert.assertEquals("childValue", resource.getPropertyValue("artifact_data/child/childKey"));
        Assert.assertEquals("child2Value", resource.getPropertyValue("artifact_data/child/child2/child2Key"));
        Assert.assertEquals("child4Value", resource.getPropertyValue("artifact_data/child/child2/child3/child4/child4Key"));
    }

    @Test
    public void testGetResources_collection() throws Exception {
        Set<String> propertyIds = new HashSet<>();
        TreeMap<String, String> foreignKeys = new TreeMap<>();
        foreignKeys.put("cluster", "500");
        List<ArtifactEntity> entities = new ArrayList<>();
        entities.add(entity);
        entities.add(entity2);
        Map<String, Object> artifact_data = Collections.singletonMap("foo", "bar");
        Map<String, Object> artifact_data2 = Collections.singletonMap("foo2", "bar2");
        Map<String, String> responseForeignKeys = new HashMap<>();
        responseForeignKeys.put("cluster", "500");
        // expectations
        expect(controller.getClusters()).andReturn(clusters).anyTimes();
        expect(clusters.getCluster("test-cluster")).andReturn(cluster).anyTimes();
        expect(clusters.getClusterById(500L)).andReturn(cluster).anyTimes();
        expect(cluster.getClusterId()).andReturn(500L).anyTimes();
        expect(cluster.getClusterName()).andReturn("test-cluster").anyTimes();
        expect(request.getPropertyIds()).andReturn(propertyIds).anyTimes();
        expect(dao.findByForeignKeys(eq(foreignKeys))).andReturn(entities).anyTimes();
        expect(entity.getArtifactName()).andReturn("test-artifact").anyTimes();
        expect(entity.getForeignKeys()).andReturn(responseForeignKeys).anyTimes();
        expect(entity.getArtifactData()).andReturn(artifact_data).anyTimes();
        expect(entity2.getArtifactName()).andReturn("test-artifact2").anyTimes();
        expect(entity2.getForeignKeys()).andReturn(responseForeignKeys).anyTimes();
        expect(entity2.getArtifactData()).andReturn(artifact_data2).anyTimes();
        // end of expectation setting
        replay(dao, em, controller, request, clusters, cluster, entity, entity2);
        // test
        PredicateBuilder pb = new PredicateBuilder();
        Predicate predicate = pb.begin().property("Artifacts/cluster_name").equals("test-cluster").end().toPredicate();
        Set<Resource> response = resourceProvider.getResources(request, predicate);
        Assert.assertEquals(2, response.size());
        boolean artifact1Returned = false;
        boolean artifact2Returned = false;
        for (Resource resource : response) {
            if (resource.getPropertyValue("Artifacts/artifact_name").equals("test-artifact")) {
                artifact1Returned = true;
                Assert.assertEquals("bar", resource.getPropertyValue("artifact_data/foo"));
                Assert.assertEquals("test-cluster", resource.getPropertyValue("Artifacts/cluster_name"));
            } else
                if (resource.getPropertyValue("Artifacts/artifact_name").equals("test-artifact2")) {
                    artifact2Returned = true;
                    Assert.assertEquals("bar2", resource.getPropertyValue("artifact_data/foo2"));
                    Assert.assertEquals("test-cluster", resource.getPropertyValue("Artifacts/cluster_name"));
                } else {
                    Assert.fail("unexpected artifact name");
                }

        }
        Assert.assertTrue(artifact1Returned);
        Assert.assertTrue(artifact2Returned);
    }

    @Test
    public void testCreateResource() throws Exception {
        Capture<ArtifactEntity> createEntityCapture = newCapture();
        Map<String, Object> outerMap = new TreeMap<>();
        Map<String, Object> childMap = new TreeMap<>();
        outerMap.put("child", childMap);
        childMap.put("childKey", "childValue");
        Map<String, Object> child2Map = new TreeMap<>();
        childMap.put("child2", child2Map);
        child2Map.put("child2Key", "child2Value");
        Map<String, Object> child3Map = new TreeMap<>();
        child2Map.put("child3", child3Map);
        Map<String, Object> child4Map = new TreeMap<>();
        child3Map.put("child4", child4Map);
        child4Map.put("child4Key", "child4Value");
        Set<Map<String, Object>> propertySet = new HashSet<>();
        propertySet.add(outerMap);
        propertySet.add(child4Map);
        Map<String, Object> artifact_data = new TreeMap<>();
        artifact_data.put("foo", "bar");
        artifact_data.put("child", childMap);
        artifact_data.put("collection", propertySet);
        TreeMap<String, String> foreignKeys = new TreeMap<>();
        foreignKeys.put("cluster", "500");
        Map<String, String> requestInfoProps = new HashMap<>();
        requestInfoProps.put(REQUEST_INFO_BODY_PROPERTY, bodyJson);
        // map with flattened properties
        Map<String, Object> properties = new HashMap<>();
        properties.put("Artifacts/artifact_name", "test-artifact");
        properties.put("Artifacts/cluster_name", "test-cluster");
        properties.put("artifact_data/foo", "bar");
        properties.put("artifact_data/child/childKey", "childValue");
        properties.put("artifact_data/child/child2/child2Key", "child2Value");
        properties.put("artifact_data/child/child2/child3/child4/child4Key", "child4Value");
        Collection<Object> collectionProperties = new HashSet<>();
        properties.put("artifact_data/collection", collectionProperties);
        // collection with maps of flattened properties
        Map<String, Object> map1 = new TreeMap<>();
        collectionProperties.add(map1);
        map1.put("foo", "bar");
        map1.put("child/childKey", "childValue");
        map1.put("child/child2/child2Key", "child2Value");
        map1.put("child/child2/child3/child4/child4Key", "child4Value");
        Map<String, Object> map2 = new TreeMap<>();
        collectionProperties.add(map2);
        map2.put("child4Key", "child4Value");
        Set<Map<String, Object>> requestProperties = Collections.singleton(properties);
        // expectations
        expect(request.getRequestInfoProperties()).andReturn(requestInfoProps).anyTimes();
        expect(request.getProperties()).andReturn(requestProperties).anyTimes();
        expect(controller.getClusters()).andReturn(clusters).anyTimes();
        expect(clusters.getCluster("test-cluster")).andReturn(cluster).anyTimes();
        expect(clusters.getClusterById(500L)).andReturn(cluster).anyTimes();
        expect(cluster.getClusterId()).andReturn(500L).anyTimes();
        expect(cluster.getClusterName()).andReturn("test-cluster").anyTimes();
        // check to see if entity already exists
        expect(dao.findByNameAndForeignKeys(eq("test-artifact"), eq(foreignKeys))).andReturn(null).once();
        // create
        dao.create(capture(createEntityCapture));
        // end of expectation setting
        replay(dao, em, controller, request, clusters, cluster, entity, entity2);
        resourceProvider.createResources(request);
        ArtifactEntity createEntity = createEntityCapture.getValue();
        Assert.assertEquals("test-artifact", createEntity.getArtifactName());
        Map<String, Object> actualArtifactData = createEntity.getArtifactData();
        // need to decompose actualArtifactData because actualArtifactData.get("collection") returns a set
        // implementation that does not equal an identical(same elements) HashSet instance
        Assert.assertEquals(artifact_data.size(), actualArtifactData.size());
        Assert.assertEquals(artifact_data.get("foo"), actualArtifactData.get("foo"));
        Assert.assertEquals(artifact_data.get("child"), actualArtifactData.get("child"));
        Assert.assertEquals(artifact_data.get("collection"), new HashSet(((Collection) (actualArtifactData.get("collection")))));
        Assert.assertEquals(foreignKeys, createEntity.getForeignKeys());
    }

    @Test
    public void testUpdateResources() throws Exception {
        Map<String, String> requestInfoProps = new HashMap<>();
        requestInfoProps.put(REQUEST_INFO_BODY_PROPERTY, bodyJson);
        Capture<ArtifactEntity> updateEntityCapture = newCapture();
        Capture<ArtifactEntity> updateEntityCapture2 = newCapture();
        Set<String> propertyIds = new HashSet<>();
        TreeMap<String, String> foreignKeys = new TreeMap<>();
        foreignKeys.put("cluster", "500");
        List<ArtifactEntity> entities = new ArrayList<>();
        entities.add(entity);
        entities.add(entity2);
        Map<String, Object> artifact_data = Collections.singletonMap("foo", "bar");
        Map<String, Object> artifact_data2 = Collections.singletonMap("foo2", "bar2");
        Map<String, String> responseForeignKeys = new HashMap<>();
        responseForeignKeys.put("cluster", "500");
        // map with flattened properties
        Map<String, Object> properties = new HashMap<>();
        properties.put("Artifacts/artifact_name", "test-artifact");
        properties.put("Artifacts/cluster_name", "test-cluster");
        properties.put("artifact_data/foo", "bar");
        properties.put("artifact_data/child/childKey", "childValue");
        properties.put("artifact_data/child/child2/child2Key", "child2Value");
        properties.put("artifact_data/child/child2/child3/child4/child4Key", "child4Value");
        Set<Map<String, Object>> requestProperties = Collections.singleton(properties);
        properties.put("artifact_data/collection", Collections.emptySet());
        // expectations
        expect(request.getProperties()).andReturn(requestProperties).anyTimes();
        expect(request.getRequestInfoProperties()).andReturn(requestInfoProps).anyTimes();
        expect(controller.getClusters()).andReturn(clusters).anyTimes();
        expect(clusters.getCluster("test-cluster")).andReturn(cluster).anyTimes();
        expect(clusters.getClusterById(500L)).andReturn(cluster).anyTimes();
        expect(cluster.getClusterId()).andReturn(500L).anyTimes();
        expect(cluster.getClusterName()).andReturn("test-cluster").anyTimes();
        expect(request.getPropertyIds()).andReturn(propertyIds).anyTimes();
        expect(dao.findByForeignKeys(eq(foreignKeys))).andReturn(entities).anyTimes();
        expect(entity.getArtifactName()).andReturn("test-artifact").anyTimes();
        expect(entity.getForeignKeys()).andReturn(responseForeignKeys).anyTimes();
        expect(entity.getArtifactData()).andReturn(artifact_data).anyTimes();
        expect(entity2.getArtifactName()).andReturn("test-artifact2").anyTimes();
        expect(entity2.getForeignKeys()).andReturn(responseForeignKeys).anyTimes();
        expect(entity2.getArtifactData()).andReturn(artifact_data2).anyTimes();
        expect(dao.merge(capture(updateEntityCapture))).andReturn(entity).once();
        expect(dao.merge(capture(updateEntityCapture2))).andReturn(entity2).once();
        // end of expectation setting
        replay(dao, em, controller, request, clusters, cluster, entity, entity2);
        PredicateBuilder pb = new PredicateBuilder();
        Predicate predicate = pb.begin().property("Artifacts/cluster_name").equals("test-cluster").end().toPredicate();
        RequestStatus response = resourceProvider.updateResources(request, predicate);
        ArtifactEntity updateEntity = updateEntityCapture.getValue();
        ArtifactEntity updateEntity2 = updateEntityCapture2.getValue();
        Gson serializer = new Gson();
        ArtifactEntity expected = new ArtifactEntity();
        expected.setArtifactData(((Map<String, Object>) (serializer.<Map<String, Object>>fromJson(bodyJson, Map.class).get("artifact_data"))));
        Assert.assertEquals(expected.getArtifactData(), updateEntity.getArtifactData());
        Assert.assertEquals(expected.getArtifactData(), updateEntity2.getArtifactData());
        if (updateEntity.getArtifactName().equals("test-artifact")) {
            Assert.assertEquals("test-artifact2", updateEntity2.getArtifactName());
        } else
            if (updateEntity.getArtifactName().equals("test-artifact2")) {
                Assert.assertEquals("test-artifact", updateEntity2.getArtifactName());
            } else {
                Assert.fail(("Unexpected artifact name: " + (updateEntity.getArtifactName())));
            }

        Assert.assertEquals(foreignKeys, updateEntity.getForeignKeys());
        Assert.assertEquals(foreignKeys, updateEntity2.getForeignKeys());
        Assert.assertEquals(Complete, response.getStatus());
        verify(dao, em, controller, request, clusters, cluster, entity, entity2);
    }

    @Test
    public void testDeleteResources() throws Exception {
        Capture<ArtifactEntity> deleteEntityCapture = newCapture();
        Capture<ArtifactEntity> deleteEntityCapture2 = newCapture();
        TreeMap<String, String> foreignKeys = new TreeMap<>();
        foreignKeys.put("cluster", "500");
        List<ArtifactEntity> entities = new ArrayList<>();
        entities.add(entity);
        entities.add(entity2);
        Map<String, Object> artifact_data = Collections.singletonMap("foo", "bar");
        Map<String, Object> artifact_data2 = Collections.singletonMap("foo2", "bar2");
        Map<String, String> responseForeignKeys = new HashMap<>();
        responseForeignKeys.put("cluster", "500");
        // expectations
        expect(controller.getClusters()).andReturn(clusters).anyTimes();
        expect(clusters.getCluster("test-cluster")).andReturn(cluster).anyTimes();
        expect(clusters.getClusterById(500L)).andReturn(cluster).anyTimes();
        expect(cluster.getClusterId()).andReturn(500L).anyTimes();
        expect(cluster.getClusterName()).andReturn("test-cluster").anyTimes();
        expect(entity.getArtifactName()).andReturn("test-artifact").anyTimes();
        expect(entity.getForeignKeys()).andReturn(responseForeignKeys).anyTimes();
        expect(entity.getArtifactData()).andReturn(artifact_data).anyTimes();
        expect(entity2.getArtifactName()).andReturn("test-artifact2").anyTimes();
        expect(entity2.getForeignKeys()).andReturn(responseForeignKeys).anyTimes();
        expect(entity2.getArtifactData()).andReturn(artifact_data2).anyTimes();
        IAnswer<ArtifactEntity> findByNameAndForeignKeys = new IAnswer<ArtifactEntity>() {
            @Override
            public ArtifactEntity answer() throws Throwable {
                String artifactName = ((String) (getCurrentArguments()[0]));
                if ("test-artifact".equals(artifactName)) {
                    return entity;
                } else
                    if ("test-artifact2".equals(artifactName)) {
                        return entity2;
                    } else {
                        return null;
                    }

            }
        };
        expect(dao.findByForeignKeys(eq(foreignKeys))).andReturn(entities).once();
        expect(dao.findByNameAndForeignKeys(anyString(), eq(foreignKeys))).andAnswer(findByNameAndForeignKeys).once();
        dao.remove(capture(deleteEntityCapture));
        expect(dao.findByNameAndForeignKeys(anyString(), eq(foreignKeys))).andAnswer(findByNameAndForeignKeys).once();
        dao.remove(capture(deleteEntityCapture2));
        // end of expectation setting
        replay(dao, em, controller, request, clusters, cluster, entity, entity2);
        PredicateBuilder pb = new PredicateBuilder();
        Predicate predicate = pb.begin().property("Artifacts/cluster_name").equals("test-cluster").end().toPredicate();
        RequestStatus response = resourceProvider.deleteResources(new RequestImpl(null, null, null, null), predicate);
        ArtifactEntity deleteEntity = deleteEntityCapture.getValue();
        ArtifactEntity deleteEntity2 = deleteEntityCapture2.getValue();
        if (deleteEntity.getArtifactName().equals("test-artifact")) {
            Assert.assertEquals("test-artifact2", deleteEntity2.getArtifactName());
        } else
            if (deleteEntity.getArtifactName().equals("test-artifact2")) {
                Assert.assertEquals("test-artifact", deleteEntity2.getArtifactName());
            } else {
                Assert.fail(("Unexpected artifact name: " + (deleteEntity.getArtifactName())));
            }

        Assert.assertEquals(foreignKeys, deleteEntity.getForeignKeys());
        Assert.assertEquals(foreignKeys, deleteEntity2.getForeignKeys());
        Assert.assertEquals(Complete, response.getStatus());
        verify(dao, em, controller, request, clusters, cluster, entity, entity2);
    }

    private String bodyJson = "{ " + (((((((((((((((((((((((((((((((("  \"artifact_data\" : {" + "    \"foo\" : \"bar\",") + "    \"child\" : {") + "      \"childKey\" : \"childValue\",") + "      \"child2\" : {") + "        \"child2Key\" : \"child2Value\",") + "        \"child3\" : {") + "          \"child4\" : {") + "            \"child4Key\" : \"child4Value\"") + "          }") + "        }") + "      }") + "    },") + "    \"collection\" : [") + "      {") + "        \"child\" : {") + "          \"childKey\" : \"childValue\",") + "          \"child2\" : {") + "            \"child2Key\" : \"child2Value\",") + "            \"child3\" : {") + "              \"child4\" : {") + "                \"child4Key\" : \"child4Value\"") + "              }") + "            }") + "          }") + "        }") + "      },") + "      {") + "        \"child4Key\" : \"child4Value\"") + "      } ") + "    ]") + "  }") + "}");
}

