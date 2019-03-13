/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.geoserver.config.util.XStreamPersister;
import org.geotools.data.wfs.internal.v2_0.storedquery.ParameterMappingDefaultValue;
import org.geotools.data.wfs.internal.v2_0.storedquery.ParameterMappingExpressionValue;
import org.geotools.data.wfs.internal.v2_0.storedquery.StoredQueryConfiguration;
import org.junit.Assert;
import org.junit.Test;


public class WFSCascadedStoredQueryConfigurationParserTest {
    private XStreamPersister persister;

    @Test
    public void testDeserialization() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + (((((((((((("<storedQueryConfiguration>\n" + "  <storedQueryParameterMappings>\n") + "    <storedQueryParameterMappingExpressionValue>\n") + "      <parameterName>bbox</parameterName>\n") + "      <expressionLanguage>CQL</expressionLanguage>\n") + "      <expression>strConCat(numberFormat(&apos;0.00000000000&apos;, bboxMinY), strConCat(&apos;,&apos;, strConCat(numberFormat(&apos;0.00000000000&apos;, bboxMinX), strConCat(&apos;,&apos;, strConCat(numberFormat(&apos;0.00000000000&apos;, bboxMaxY), strConCat(&apos;,&apos;, strConCat(numberFormat(&apos;0.00000000000&apos;, bboxMaxX), &apos;,EPSG:4258&apos;)))))))</expression>\n") + "    </storedQueryParameterMappingExpressionValue>\n") + "    <storedQueryParameterMappingDefaultValue>\n") + "      <parameterName>timestep</parameterName>\n") + "      <defaultValue>720</defaultValue>\n") + "    </storedQueryParameterMappingDefaultValue>\n") + "  </storedQueryParameterMappings>\n") + "</storedQueryConfiguration>");
        ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        StoredQueryConfiguration configuration = persister.load(bais, StoredQueryConfiguration.class);
        Assert.assertNotNull(configuration);
        Assert.assertEquals(2, configuration.getStoredQueryParameterMappings().size());
        Assert.assertEquals(ParameterMappingExpressionValue.class, configuration.getStoredQueryParameterMappings().get(0).getClass());
        Assert.assertEquals(ParameterMappingDefaultValue.class, configuration.getStoredQueryParameterMappings().get(1).getClass());
        ParameterMappingExpressionValue map1 = ((ParameterMappingExpressionValue) (configuration.getStoredQueryParameterMappings().get(0)));
        Assert.assertEquals("bbox", map1.getParameterName());
        Assert.assertEquals("CQL", map1.getExpressionLanguage());
        Assert.assertEquals("strConCat(numberFormat('0.00000000000', bboxMinY), strConCat(',', strConCat(numberFormat('0.00000000000', bboxMinX), strConCat(',', strConCat(numberFormat('0.00000000000', bboxMaxY), strConCat(',', strConCat(numberFormat('0.00000000000', bboxMaxX), ',EPSG:4258')))))))", map1.getExpression());
        ParameterMappingDefaultValue map2 = ((ParameterMappingDefaultValue) (configuration.getStoredQueryParameterMappings().get(1)));
        Assert.assertEquals("timestep", map2.getParameterName());
        Assert.assertEquals("720", map2.getDefaultValue());
    }

    @Test
    public void testSerialization() throws Exception {
        StoredQueryConfiguration mockConfiguration = new StoredQueryConfiguration();
        ParameterMappingExpressionValue param1 = new ParameterMappingExpressionValue();
        param1.setParameterName("bbox");
        param1.setExpressionLanguage("CQL");
        param1.setExpression(("strConCat(" + (((((("numberFormat('0.00000000000', bboxMinY), strConCat(" + "',', strConCat(") + "numberFormat('0.00000000000', bboxMinX), strConCat(") + "',', strConCat(") + "numberFormat('0.00000000000', bboxMaxY), strConCat(") + "',', strConCat(") + "numberFormat('0.00000000000', bboxMaxX), ',EPSG:4258')))))))")));
        mockConfiguration.getStoredQueryParameterMappings().add(param1);
        ParameterMappingDefaultValue param2 = new ParameterMappingDefaultValue();
        param2.setParameterName("timestep");
        param2.setDefaultValue("720");
        mockConfiguration.getStoredQueryParameterMappings().add(param2);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        persister.save(mockConfiguration, baos);
        baos.flush();
        String xml = new String(baos.toByteArray(), "UTF-8");
        // System.err.println(xml);
        Assert.assertTrue(xml.contains("numberFormat"));
    }
}

