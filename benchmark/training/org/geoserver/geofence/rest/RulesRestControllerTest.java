/**
 * (c) 2015 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.rest;


import HttpStatus.OK;
import java.util.UUID;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.geoserver.geofence.GeofenceBaseTest;
import org.geoserver.geofence.core.dao.DuplicateKeyException;
import org.geoserver.geofence.core.model.LayerAttribute;
import org.geoserver.geofence.core.model.Rule;
import org.geoserver.geofence.core.model.enums.GrantType;
import org.geoserver.geofence.rest.xml.JaxbRule;
import org.geoserver.geofence.rest.xml.JaxbRuleList;
import org.geoserver.geofence.server.rest.RulesRestController;
import org.geoserver.geofence.services.RuleAdminService;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;
import org.geoserver.rest.RestBaseController;
import org.geotools.gml3.bindings.GML3MockData;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;


public class RulesRestControllerTest extends GeofenceBaseTest {
    protected RulesRestController controller;

    protected RuleAdminService adminService;

    @Test
    public void testInsertUpdateDelete() {
        JaxbRule rule = new JaxbRule();
        rule.setPriority(5L);
        rule.setUserName("pippo");
        rule.setRoleName("clown");
        rule.setAddressRange("127.0.0.1/32");
        rule.setService("wfs");
        rule.setRequest("getFeature");
        rule.setWorkspace("workspace");
        rule.setLayer("layer");
        rule.setAccess("ALLOW");
        long id = prepareGeoFenceTestRules(rule);
        Rule realRule = adminService.get(id);
        Assert.assertEquals(rule.getPriority().longValue(), realRule.getPriority());
        Assert.assertEquals(rule.getUserName(), realRule.getUsername());
        Assert.assertEquals(rule.getRoleName(), realRule.getRolename());
        Assert.assertEquals(rule.getAddressRange(), realRule.getAddressRange().getCidrSignature());
        Assert.assertEquals(rule.getService().toUpperCase(), realRule.getService().toUpperCase());
        Assert.assertEquals(rule.getRequest().toUpperCase(), realRule.getRequest().toUpperCase());
        Assert.assertEquals(rule.getWorkspace(), realRule.getWorkspace());
        Assert.assertEquals(rule.getLayer(), realRule.getLayer());
        Assert.assertEquals(rule.getAccess(), realRule.getAccess().toString());
        JaxbRule ruleMods = new JaxbRule();
        ruleMods.setRoleName("acrobaat");
        controller.update(id, ruleMods);
        realRule = adminService.get(id);
        Assert.assertEquals(rule.getUserName(), realRule.getUsername());
        Assert.assertEquals(ruleMods.getRoleName(), realRule.getRolename());
        JaxbRule rule2 = new JaxbRule();
        rule2.setPriority(5L);
        rule2.setAccess("DENY");
        long id2 = controller.insert(rule2).getBody();
        realRule = adminService.get(id);
        Assert.assertEquals(6L, realRule.getPriority());
        // test changing to non-existing priority
        JaxbRule rule2Mods = new JaxbRule();
        rule2Mods.setPriority(3L);
        controller.update(id2, rule2Mods);
        realRule = adminService.get(id2);
        Assert.assertEquals(3L, realRule.getPriority());
        // test changing to existing priority
        rule2Mods = new JaxbRule();
        rule2Mods.setPriority(6L);
        controller.update(id2, rule2Mods);
        realRule = adminService.get(id2);
        Assert.assertEquals(6L, realRule.getPriority());
        realRule = adminService.get(id);
        Assert.assertEquals(7L, realRule.getPriority());
        // not found - will be translated by spring exception handler to code 404
        controller.delete(id);
        boolean notfound = false;
        try {
            adminService.get(id);
        } catch (NotFoundServiceEx e) {
            notfound = true;
        }
        Assert.assertTrue(notfound);
        // conflict - will be translated by spring exception handler to code 409
        boolean conflict = false;
        try {
            controller.insert(rule2);
        } catch (DuplicateKeyException e) {
            conflict = true;
        }
        Assert.assertTrue(conflict);
    }

    @Test
    public void testLimits() {
        JaxbRule rule = new JaxbRule();
        rule.setPriority(5L);
        rule.setUserName("pippo");
        rule.setRoleName("clown");
        rule.setAddressRange("127.0.0.1/32");
        rule.setService("wfs");
        rule.setRequest("getFeature");
        rule.setWorkspace("workspace");
        rule.setLayer("layer");
        rule.setAccess("LIMIT");
        rule.setLimits(new JaxbRule.Limits());
        rule.getLimits().setAllowedArea(GML3MockData.multiPolygon());
        rule.getLimits().setCatalogMode("MIXED");
        long id = prepareGeoFenceTestRules(rule);
        Rule realRule = adminService.get(id);
        Assert.assertEquals(rule.getLimits().getCatalogMode(), realRule.getRuleLimits().getCatalogMode().toString());
        try {
            Assert.assertEquals(new WKTReader().read(rule.getLimits().getAllowedArea()), realRule.getRuleLimits().getAllowedArea());
        } catch (ParseException e) {
            Assert.assertFalse(e.getLocalizedMessage(), true);
        }
        rule.getLimits().setCatalogMode("HIDE");
        controller.update(id, rule);
        realRule = adminService.get(id);
        Assert.assertEquals(rule.getLimits().getCatalogMode(), realRule.getRuleLimits().getCatalogMode().toString());
        rule.setLimits(null);
        controller.clearAndUpdate(id, rule);
        realRule = adminService.get(id);
        Assert.assertNull(realRule.getRuleLimits());
    }

    @Test
    public void testLayerDetails() {
        JaxbRule rule = new JaxbRule();
        rule.setPriority(5L);
        rule.setUserName("pippo");
        rule.setRoleName("clown");
        rule.setAddressRange("127.0.0.1/32");
        rule.setService("wfs");
        rule.setRequest("getFeature");
        rule.setWorkspace("workspace");
        rule.setLayer("layer");
        rule.setAccess("ALLOW");
        rule.setLayerDetails(new JaxbRule.LayerDetails());
        rule.getLayerDetails().setAllowedArea(GML3MockData.multiPolygon());
        rule.getLayerDetails().getAllowedStyles().add("style1");
        rule.getLayerDetails().getAllowedStyles().add("style2");
        JaxbRule.LayerAttribute att = new JaxbRule.LayerAttribute();
        att.setName("layerAttribute1");
        att.setAccessType("READONLY");
        att.setDataType("dataType");
        rule.getLayerDetails().getAttributes().add(att);
        att = new JaxbRule.LayerAttribute();
        att.setName("layerAttribute2");
        att.setAccessType("READONLY");
        att.setDataType("dataType2");
        rule.getLayerDetails().getAttributes().add(att);
        rule.getLayerDetails().setCatalogMode("MIXED");
        rule.getLayerDetails().setCqlFilterRead("myFilterRead");
        rule.getLayerDetails().setCqlFilterWrite("myFilterWrite");
        rule.getLayerDetails().setDefaultStyle("myDefaultStyle");
        rule.getLayerDetails().setLayerType("VECTOR");
        long id = prepareGeoFenceTestRules(rule);
        Rule realRule = adminService.get(id);
        try {
            Assert.assertEquals(new WKTReader().read(rule.getLayerDetails().getAllowedArea()), realRule.getLayerDetails().getArea());
        } catch (ParseException e) {
            Assert.assertFalse(e.getLocalizedMessage(), true);
        }
        Assert.assertEquals(rule.getLayerDetails().getCatalogMode(), realRule.getLayerDetails().getCatalogMode().toString());
        Assert.assertEquals(rule.getLayerDetails().getAllowedStyles(), realRule.getLayerDetails().getAllowedStyles());
        Assert.assertEquals(2, realRule.getLayerDetails().getAttributes().size());
        for (LayerAttribute la : realRule.getLayerDetails().getAttributes()) {
            if (la.getName().equals("layerAttribute2")) {
                Assert.assertEquals("READONLY", la.getAccess().toString());
            }
        }
        Assert.assertEquals(rule.getLayerDetails().getCqlFilterRead(), realRule.getLayerDetails().getCqlFilterRead());
        Assert.assertEquals(rule.getLayerDetails().getCqlFilterWrite(), realRule.getLayerDetails().getCqlFilterWrite());
        Assert.assertEquals(rule.getLayerDetails().getDefaultStyle(), realRule.getLayerDetails().getDefaultStyle());
        Assert.assertEquals(rule.getLayerDetails().getLayerType(), realRule.getLayerDetails().getType().toString());
        rule.getLayerDetails().setDefaultStyle("myDefaultStyle2");
        rule.getLayerDetails().getAttributes().clear();
        att = new JaxbRule.LayerAttribute();
        att.setName("layerAttribute2");
        att.setAccessType("READWRITE");
        att.setDataType("dataType");
        rule.getLayerDetails().getAttributes().add(att);
        att = new JaxbRule.LayerAttribute();
        att.setName("layerAttribute3");
        att.setAccessType("READWRITE");
        att.setDataType("dataType");
        rule.getLayerDetails().getAttributes().add(att);
        rule.getLayerDetails().getAllowedStyles().clear();
        rule.getLayerDetails().getAllowedStyles().add("style3");
        controller.update(id, rule);
        realRule = adminService.get(id);
        Assert.assertEquals(rule.getLayerDetails().getDefaultStyle(), realRule.getLayerDetails().getDefaultStyle());
        Assert.assertEquals(1, realRule.getLayerDetails().getAllowedStyles().size());
        Assert.assertEquals(2, realRule.getLayerDetails().getAttributes().size());
        for (LayerAttribute la : realRule.getLayerDetails().getAttributes()) {
            if (la.getName().equals("layerAttribute2")) {
                Assert.assertEquals("READWRITE", la.getAccess().toString());
            }
        }
        controller.clearAndUpdate(id, rule);
        realRule = adminService.get(id);
        Assert.assertEquals(rule.getLayerDetails().getAllowedStyles(), realRule.getLayerDetails().getAllowedStyles());
        Assert.assertEquals(2, realRule.getLayerDetails().getAttributes().size());
    }

    @Test
    public void testMovingRules() {
        // create some rules for the test
        String prefix = UUID.randomUUID().toString();
        adminService.insert(new Rule(5, (prefix + "-user5"), (prefix + "-role1"), null, null, null, null, null, null, GrantType.ALLOW));
        adminService.insert(new Rule(2, (prefix + "-user2"), (prefix + "-role1"), null, null, null, null, null, null, GrantType.ALLOW));
        adminService.insert(new Rule(1, (prefix + "-user1"), (prefix + "-role1"), null, null, null, null, null, null, GrantType.ALLOW));
        adminService.insert(new Rule(4, (prefix + "-user4"), (prefix + "-role2"), null, null, null, null, null, null, GrantType.ALLOW));
        adminService.insert(new Rule(3, (prefix + "-user3"), (prefix + "-role2"), null, null, null, null, null, null, GrantType.ALLOW));
        adminService.insert(new Rule(6, (prefix + "-user6"), (prefix + "-role6"), null, null, null, null, null, null, GrantType.ALLOW));
        // get the rules so we can access their id
        JaxbRuleList originalRules = controller.get(0, 6, false, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        validateRules(originalRules, prefix, "user1", "user2", "user3", "user4", "user5", "user6");
        // check rules per page
        validateRules(0, prefix, "user1", "user2");
        validateRules(0, 1, 2);
        validateRules(1, prefix, "user3", "user4");
        validateRules(1, 3, 4);
        validateRules(2, prefix, "user5", "user6");
        validateRules(2, 5, 6);
        // moving rules for user1 and user2 to the last page
        ResponseEntity<JaxbRuleList> result = controller.move(7, (((originalRules.getRules().get(0).getId()) + ",") + (originalRules.getRules().get(1).getId())));
        validateResult(result, OK, 2);
        validateRules(result.getBody(), prefix, "user1", "user2");
        validateRules(result.getBody(), 7L, 8L);
        // check rules per page
        validateRules(0, prefix, "user3", "user4");
        validateRules(0, 3, 4);
        validateRules(1, prefix, "user5", "user6");
        validateRules(1, 5, 6);
        validateRules(2, prefix, "user1", "user2");
        validateRules(2, 7, 8);
        // moving rules for user3 and user4 to the second page
        result = controller.move(7, (((originalRules.getRules().get(2).getId()) + ",") + (originalRules.getRules().get(3).getId())));
        validateResult(result, OK, 2);
        validateRules(result.getBody(), prefix, "user3", "user4");
        validateRules(result.getBody(), 7L, 8L);
        // check rules per page
        validateRules(0, prefix, "user5", "user6");
        validateRules(0, 5, 6);
        validateRules(1, prefix, "user3", "user4");
        validateRules(1, 7, 8);
        validateRules(2, prefix, "user1", "user2");
        validateRules(2, 9, 10);
        // moving rule for user1 to first page
        result = controller.move(5, String.valueOf(originalRules.getRules().get(0).getId()));
        validateResult(result, OK, 1);
        validateRules(result.getBody(), prefix, "user1");
        validateRules(result.getBody(), 5L);
        // check rules per page
        validateRules(0, prefix, "user1", "user5");
        validateRules(0, 5, 6);
        validateRules(1, prefix, "user6", "user3");
        validateRules(1, 7, 8);
        validateRules(2, prefix, "user4", "user2");
        validateRules(2, 9, 11);
        // moving rules for user2 and user 3 to first and second page
        result = controller.move(6, (((originalRules.getRules().get(1).getId()) + ",") + (originalRules.getRules().get(2).getId())));
        validateResult(result, OK, 2);
        validateRules(result.getBody(), prefix, "user3", "user2");
        validateRules(result.getBody(), 6L, 7L);
        // check rules per page
        validateRules(0, prefix, "user1", "user3");
        validateRules(0, 5, 6);
        validateRules(1, prefix, "user2", "user5");
        validateRules(1, 7, 8);
        validateRules(2, prefix, "user6", "user4");
        validateRules(2, 9, 11);
    }

    @Test
    public void testRestControllerPaths() throws Exception {
        JaxbRule rule = new JaxbRule();
        rule.setPriority(5L);
        rule.setUserName("pippo");
        rule.setRoleName("clown");
        rule.setAddressRange("127.0.0.1/32");
        rule.setService("wfs");
        rule.setRequest("getFeature");
        rule.setWorkspace("workspace");
        rule.setLayer("layer");
        rule.setAccess("ALLOW");
        long id = prepareGeoFenceTestRules(rule);
        JSONObject json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/geofence/rules.json"), 200)));
        // print(json);
        Assert.assertNotNull(id);
        Assert.assertEquals(1, json.getInt("count"));
        json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/geofence/rules.json"), 200)));
        // print(json);
        Assert.assertEquals(1, json.getInt("count"));
        JSONArray jsonRules = json.getJSONArray("rules");
        // print(jsonRules);
        Assert.assertNotNull(jsonRules);
        Assert.assertEquals(1, jsonRules.size());
        final String jsonRuleBody = "{\n" + ((((((((((((((((((((((((((((((((((((((((((((((((((((("  \'Rule\': {\n" + "    \'priority\': 0,\n") + "    \'userName\': null,\n") + "    \'roleName\': null,\n") + "    \'addressRange\': null,\n") + "    \'workspace\': \'geonode\',\n") + "    \'layer\': \'DE_USNG_UTM18\',\n") + "    \'service\': null,\n") + "    \'request\': null,\n") + "    \'access\': \'ALLOW\',\n") + "    \'limits\': null,\n") + "    \'layerDetails\': {\n") + "      \'layerType\': \'VECTOR\',\n") + "      \'defaultStyle\': \'DE_USNG_UTM18\',\n") + "      \'cqlFilterRead\': \'Northings >= 100\',\n") + "      \'cqlFilterWrite\': null,\n") + "      \'allowedArea\': \'MULTIPOLYGON (((-180 -90, -180 90, 180 90, 180 -90, -180 -90)))\',\n") + "      \'catalogMode\': null,\n") + "      \'allowedStyles\': [],\n") + "      \'attributes\': [\n") + "        {\n") + "          \'name\': \'Eastings\',\n") + "          \'dataType\': \'java.lang.String\',\n") + "          \'accessType\': \'READWRITE\'\n") + "        },\n") + "        {\n") + "          \'name\': \'the_geom\',\n") + "          \'dataType\': \'org.locationtech.jts.geom.MultiPolygon\',\n") + "          \'accessType\': \'READONLY\'\n") + "        },\n") + "        {\n") + "          \'name\': \'GRID1MIL\',\n") + "          \'dataType\': \'java.lang.String\',\n") + "          \'accessType\': \'NONE\'\n") + "        },\n") + "        {\n") + "          \'name\': \'GRID100K\',\n") + "          \'dataType\': \'java.lang.String\',\n") + "          \'accessType\': \'READONLY\'\n") + "        },\n") + "        {\n") + "          \'name\': \'Northings\',\n") + "          \'dataType\': \'java.lang.String\',\n") + "          \'accessType\': \'NONE\'\n") + "        },\n") + "        {\n") + "          \'name\': \'USNG\',\n") + "          \'dataType\': \'java.lang.String\',\n") + "          \'accessType\': \'NONE\'\n") + "        }\n") + "      ]\n") + "    }\n") + "  }\n") + "}");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/geofence/rules"), jsonRuleBody, "text/json");
        Assert.assertEquals(201, response.getStatus());
        json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/geofence/rules.json"), 200)));
        // print(json);
        Assert.assertEquals(2, json.getInt("count"));
        jsonRules = json.getJSONArray("rules");
        // print(jsonRules);
        Assert.assertNotNull(jsonRules);
        Assert.assertEquals(2, jsonRules.size());
        JSONObject jsonRule = null;
        for (Object jsonObj : jsonRules) {
            Assert.assertNotNull(jsonObj);
            Assert.assertTrue((jsonObj instanceof JSONObject));
            jsonRule = ((JSONObject) (jsonObj));
            print(jsonRule);
            if (jsonRule.getString("layer").equals("DE_USNG_UTM18")) {
                Assert.assertEquals("geonode", jsonRule.getString("workspace"));
                Assert.assertEquals("DE_USNG_UTM18", jsonRule.getString("layer"));
                Assert.assertEquals("ALLOW", jsonRule.getString("access"));
                JSONObject layerDetails = jsonRule.getJSONObject("layerDetails");
                Assert.assertNotNull(layerDetails);
                Assert.assertEquals("VECTOR", layerDetails.getString("layerType"));
                Assert.assertEquals("DE_USNG_UTM18", layerDetails.getString("defaultStyle"));
                Assert.assertEquals("Northings >= 100", layerDetails.getString("cqlFilterRead"));
                Assert.assertEquals("MULTIPOLYGON (((-180 -90, -180 90, 180 90, 180 -90, -180 -90)))", layerDetails.getString("allowedArea"));
                break;
            } else {
                jsonRule = null;
            }
        }
        Assert.assertNotNull(jsonRule);
        json = ((JSONObject) (getAsJSON(((((RestBaseController.ROOT_PATH) + "/geofence/rules/id/") + (jsonRule.getInt("id"))) + ".json"), 200)));
        // print(json);
        Assert.assertEquals(json.toString(), jsonRule.toString());
        response = deleteAsServletResponse((((RestBaseController.ROOT_PATH) + "/geofence/rules/id/") + (jsonRule.getInt("id"))));
        Assert.assertEquals(200, response.getStatus());
        json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/geofence/rules.json"), 200)));
        // print(json);
        Assert.assertEquals(1, json.getInt("count"));
    }
}

