/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geoserver.authentication.auth;


import GrantType.ALLOW;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;
import org.geoserver.config.util.XStreamPersister;
import org.geoserver.geofence.core.model.Rule;
import org.geoserver.geofence.rest.xml.JaxbRule;
import org.geoserver.geofence.rest.xml.JaxbRule.Limits;
import org.geoserver.geofence.rest.xml.JaxbRuleList;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.io.WKTReader;


/**
 *
 *
 * @author Alessio Fabiani, GeoSolutions S.A.S.
 */
public class GeofencePersisterSerializationTest {
    private XStreamPersister persister;

    @Test
    public void testDeserialization() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ((((((((("<Rule>" + "<access>LIMIT</access>") + "<layer>DE_USNG_UTM18</layer>") + "<limits>") + "     <allowedArea>SRID=4326;MULTIPOLYGON (((-75 -90, -75 90, 75 90, 75 -90, -75 -90)))</allowedArea>") + "     <catalogMode>HIDDEN</catalogMode>") + "</limits>") + "<priority>1</priority>") + "<workspace>geonode</workspace>") + "</Rule>");
        ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        JaxbRule rule = persister.load(bais, JaxbRule.class);
        Assert.assertNotNull(rule);
        Assert.assertEquals("LIMIT", rule.getAccess());
        Assert.assertEquals("DE_USNG_UTM18", rule.getLayer());
        Assert.assertEquals("geonode", rule.getWorkspace());
        Assert.assertEquals(1, rule.getPriority().intValue());
        Assert.assertNotNull(rule.getLimits());
        Assert.assertEquals("SRID=4326;MULTIPOLYGON (((-75 -90, -75 90, 75 90, 75 -90, -75 -90)))", rule.getLimits().getAllowedArea());
        Assert.assertEquals("HIDDEN", rule.getLimits().getCatalogMode());
    }

    @Test
    public void testSerialization() throws Exception {
        JaxbRule rule = new JaxbRule();
        rule.setPriority(1L);
        rule.setUserName("pippo");
        rule.setRoleName("clown");
        rule.setAddressRange("127.0.0.1/32");
        rule.setService("wfs");
        rule.setRequest("getFeature");
        rule.setWorkspace("workspace");
        rule.setLayer("layer");
        rule.setAccess("ALLOW");
        Limits limits = new Limits();
        limits.setCatalogMode("HIDDEN");
        WKTReader reader = new WKTReader();
        limits.setAllowedArea(((MultiPolygon) (reader.read("MULTIPOLYGON (((-75 -90, -75 90, 75 90, 75 -90, -75 -90)))"))));
        rule.setLimits(limits);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        persister.save(rule, baos);
        baos.flush();
        String xml = new String(baos.toByteArray(), "UTF-8");
        // System.err.println(xml);
        Assert.assertTrue(xml.contains("pippo"));
        Assert.assertTrue(xml.contains("clown"));
        Assert.assertTrue(xml.contains("HIDDEN"));
        Assert.assertTrue(xml.contains("MULTIPOLYGON (((-75 -90, -75 90, 75 90, 75 -90, -75 -90)))"));
        Rule rule2 = new Rule();
        rule2.setPriority(2L);
        rule2.setUsername("topolino");
        rule2.setRolename("minnie");
        rule2.setService("wfs");
        rule2.setRequest("getFeature");
        rule2.setWorkspace("workspace");
        rule2.setLayer("layer");
        rule2.setAccess(ALLOW);
        Rule[] rules = new Rule[]{ rule2 };
        JaxbRuleList ruleList = new JaxbRuleList(((List<Rule>) (Arrays.asList(rules))));
        persister.save(ruleList, baos);
        baos.flush();
        xml = new String(baos.toByteArray(), "UTF-8");
        // System.err.println(xml);
        Assert.assertTrue(xml.contains("topolino"));
        Assert.assertTrue(xml.contains("minnie"));
    }
}

