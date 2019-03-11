package com.netflix.eureka.registry;


import Key.EntityType;
import Key.KeyType;
import com.netflix.appinfo.EurekaAccept;
import com.netflix.eureka.AbstractTester;
import com.netflix.eureka.Version;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Nitesh Kant
 */
public class ResponseCacheTest extends AbstractTester {
    private static final String REMOTE_REGION = "myremote";

    private PeerAwareInstanceRegistry testRegistry;

    @Test
    public void testInvalidate() throws Exception {
        ResponseCacheImpl cache = ((ResponseCacheImpl) (testRegistry.getResponseCache()));
        Key key = new Key(EntityType.Application, AbstractTester.REMOTE_REGION_APP_NAME, KeyType.JSON, Version.V1, EurekaAccept.full);
        String response = cache.get(key, false);
        Assert.assertNotNull("Cache get returned null.", response);
        testRegistry.cancel(AbstractTester.REMOTE_REGION_APP_NAME, AbstractTester.REMOTE_REGION_INSTANCE_1_HOSTNAME, true);
        Assert.assertNull("Cache after invalidate did not return null for write view.", cache.get(key, true));
    }

    @Test
    public void testInvalidateWithRemoteRegion() throws Exception {
        ResponseCacheImpl cache = ((ResponseCacheImpl) (testRegistry.getResponseCache()));
        Key key = new Key(EntityType.Application, AbstractTester.REMOTE_REGION_APP_NAME, KeyType.JSON, Version.V1, EurekaAccept.full, new String[]{ ResponseCacheTest.REMOTE_REGION });
        Assert.assertNotNull("Cache get returned null.", cache.get(key, false));
        testRegistry.cancel(AbstractTester.REMOTE_REGION_APP_NAME, AbstractTester.REMOTE_REGION_INSTANCE_1_HOSTNAME, true);
        Assert.assertNull("Cache after invalidate did not return null.", cache.get(key));
    }

    @Test
    public void testInvalidateWithMultipleRemoteRegions() throws Exception {
        ResponseCacheImpl cache = ((ResponseCacheImpl) (testRegistry.getResponseCache()));
        Key key1 = new Key(EntityType.Application, AbstractTester.REMOTE_REGION_APP_NAME, KeyType.JSON, Version.V1, EurekaAccept.full, new String[]{ ResponseCacheTest.REMOTE_REGION, "myregion2" });
        Key key2 = new Key(EntityType.Application, AbstractTester.REMOTE_REGION_APP_NAME, KeyType.JSON, Version.V1, EurekaAccept.full, new String[]{ ResponseCacheTest.REMOTE_REGION });
        Assert.assertNotNull("Cache get returned null.", cache.get(key1, false));
        Assert.assertNotNull("Cache get returned null.", cache.get(key2, false));
        testRegistry.cancel(AbstractTester.REMOTE_REGION_APP_NAME, AbstractTester.REMOTE_REGION_INSTANCE_1_HOSTNAME, true);
        Assert.assertNull("Cache after invalidate did not return null.", cache.get(key1, true));
        Assert.assertNull("Cache after invalidate did not return null.", cache.get(key2, true));
    }
}

