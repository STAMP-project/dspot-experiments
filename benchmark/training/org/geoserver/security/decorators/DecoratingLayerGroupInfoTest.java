/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.decorators;


import PublishedType.GROUP;
import java.io.IOException;
import org.geoserver.catalog.LayerGroupInfo;
import org.junit.Assert;
import org.junit.Test;


public class DecoratingLayerGroupInfoTest {
    @Test
    public void testDelegateGetType() throws IOException {
        // build up the mock
        LayerGroupInfo lg = createNiceMock(LayerGroupInfo.class);
        expect(lg.getType()).andReturn(GROUP);
        replay(lg);
        DecoratingLayerGroupInfo decorator = new DecoratingLayerGroupInfo(lg);
        Assert.assertEquals(GROUP, decorator.getType());
    }
}

