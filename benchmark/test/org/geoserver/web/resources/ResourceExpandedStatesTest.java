/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.resources;


import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;
import org.geoserver.platform.resource.Resource;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Niels Charlier
 */
public class ResourceExpandedStatesTest extends GeoServerWicketTestSupport {
    protected final ResourceExpandedStates expandedStates = new ResourceExpandedStates();

    @Test
    public void testExpandedStates() throws Exception {
        try (OutputStream os = store().get("/temp/dir/something").out()) {
            os.write("unimportant".getBytes());
        }
        Resource res = store().get("/temp/dir");
        ResourceNode nodeOne = new ResourceNode(res, expandedStates);
        ResourceNode nodeTwo = new ResourceNode(res, expandedStates);
        Assert.assertTrue(nodeOne.isSameAs(nodeTwo));
        // test synchronous
        nodeOne.getExpanded().setObject(true);
        Assert.assertTrue(nodeTwo.getExpanded().getObject());
        Assert.assertTrue(expandedStates.expanded.contains(res.path()));
        nodeTwo.getExpanded().setObject(false);
        Assert.assertFalse(nodeOne.getExpanded().getObject());
        Assert.assertFalse(expandedStates.expanded.contains(res.path()));
        nodeOne.getExpanded().setObject(true);
        Assert.assertTrue(expandedStates.expanded.contains(res.path()));
        // automatic removal
        CountDownLatch lock = new CountDownLatch(1);
        store().get("/temp").addListener(( notify) -> {
            lock.countDown();
        });
        // delete resource
        res.delete();
        // wait until listeners have been called
        lock.await();
        Assert.assertFalse(expandedStates.expanded.contains(res.path()));
    }
}

