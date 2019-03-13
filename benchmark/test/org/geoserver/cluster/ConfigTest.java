/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.cluster;


import java.io.IOException;
import org.geoserver.cluster.hazelcast.HzSynchronizerTest;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.platform.resource.Resource;
import org.geoserver.platform.resource.Resources;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Alessio Fabiani, GeoSolutions
 */
public class ConfigTest extends HzSynchronizerTest {
    private Resource tmpDir;

    private Resource tmpDir1;

    private Resource tmpDir2;

    @Test
    public void testConfigurationReload() throws IOException {
        GeoServerResourceLoader resourceLoader = new GeoServerResourceLoader(tmpDir.dir());
        GeoServerResourceLoader resourceLoader1 = new GeoServerResourceLoader(tmpDir1.dir());
        GeoServerResourceLoader resourceLoader2 = new GeoServerResourceLoader(tmpDir2.dir());
        Resources.directory(tmpDir.get("cluster"), true);
        Resources.directory(tmpDir1.get("cluster"), true);
        Resources.directory(tmpDir2.get("cluster"), true);
        this.cluster.setResourceStore(resourceLoader.getResourceStore());
        this.cluster.saveConfiguration(resourceLoader1);
        Assert.assertNotNull(cluster.getFileLocations());
        Assert.assertEquals(2, cluster.getFileLocations().size());
        Assert.assertTrue("The file 'cluster.properties' does not exist!", Resources.exists(tmpDir1.get("cluster/cluster.properties")));
        Assert.assertTrue("The file 'hazelcast.xml' does not exist!", Resources.exists(tmpDir1.get("cluster/hazelcast.xml")));
        this.cluster.saveConfiguration(resourceLoader2);
        Assert.assertTrue("The file 'cluster.properties' does not exist!", Resources.exists(tmpDir2.get("cluster/cluster.properties")));
        Assert.assertTrue("The file 'hazelcast.xml' does not exist!", Resources.exists(tmpDir2.get("cluster/hazelcast.xml")));
        Assert.assertEquals(ConfigTest.lines(tmpDir1, "cluster/cluster.properties"), ConfigTest.lines(tmpDir2, "cluster/cluster.properties"));
        Assert.assertEquals(ConfigTest.lines(tmpDir1, "cluster/hazelcast.xml"), ConfigTest.lines(tmpDir2, "cluster/hazelcast.xml"));
    }
}

