package com.thinkaurelius.titan.diskstorage.util;


import com.thinkaurelius.titan.diskstorage.keycolumnvalue.StandardStoreFeatures;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.StoreFeatures;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class StorageFeaturesTest {
    @Test
    public void testFeaturesImplementation() {
        StoreFeatures features;
        features = new StandardStoreFeatures.Builder().build();
        Assert.assertFalse(features.hasMultiQuery());
        Assert.assertFalse(features.hasLocking());
        Assert.assertFalse(features.isDistributed());
        Assert.assertFalse(features.hasScan());
        features = new StandardStoreFeatures.Builder().locking(true).build();
        Assert.assertFalse(features.hasMultiQuery());
        Assert.assertTrue(features.hasLocking());
        Assert.assertFalse(features.isDistributed());
        features = new StandardStoreFeatures.Builder().multiQuery(true).unorderedScan(true).build();
        Assert.assertTrue(features.hasMultiQuery());
        Assert.assertTrue(features.hasUnorderedScan());
        Assert.assertFalse(features.hasOrderedScan());
        Assert.assertTrue(features.hasScan());
        Assert.assertFalse(features.isDistributed());
        Assert.assertFalse(features.hasLocking());
    }
}

