package org.ethereum.mine;


import org.junit.Assert;
import org.junit.Test;

import static CacheOrder.direct;
import static CacheOrder.reverse;


/**
 *
 *
 * @author Mikhail Kalinin
 * @since 11.07.2018
 */
public class EthashValidationHelperTest {
    static class EthashAlgoMock extends org.ethereum.mine.EthashAlgo {
        @Override
        public int[] makeCache(long cacheSize, byte[] seed) {
            return new int[0];
        }
    }

    // sequential direct caching
    @Test
    public void testRegularFlow() {
        EthashValidationHelper ethash = new EthashValidationHelper(direct);
        ethash.ethashAlgo = new EthashValidationHelperTest.EthashAlgoMock();
        // init on block 193, 0 and 1st epochs are cached
        ethash.preCache(193);
        Assert.assertNotNull(ethash.getCachedFor(193));
        Assert.assertNotNull(ethash.getCachedFor(30000));
        Assert.assertEquals(ethash.caches.size(), 2);
        ethash = new EthashValidationHelper(direct);
        ethash.ethashAlgo = new EthashValidationHelperTest.EthashAlgoMock();
        // genesis
        ethash.preCache(0);
        Assert.assertNotNull(ethash.getCachedFor(0));
        Assert.assertEquals(ethash.caches.size(), 1);
        Assert.assertNull(ethash.getCachedFor(30000));
        // block 100, nothing has changed
        ethash.preCache(100);
        Assert.assertNotNull(ethash.getCachedFor(100));
        Assert.assertEquals(ethash.caches.size(), 1);
        // block 193, next epoch must be added
        ethash.preCache(193);
        Assert.assertNotNull(ethash.getCachedFor(193));
        Assert.assertNotNull(ethash.getCachedFor(30000));
        Assert.assertEquals(ethash.caches.size(), 2);
        // block  30_192, nothing has changed
        ethash.preCache(30192);
        Assert.assertNotNull(ethash.getCachedFor(30192));
        Assert.assertNotNull(ethash.getCachedFor(192));
        Assert.assertEquals(ethash.caches.size(), 2);
        // block  30_193, two epochs are kept: 1st and 2nd
        ethash.preCache(30193);
        Assert.assertNotNull(ethash.getCachedFor(30193));
        Assert.assertNotNull(ethash.getCachedFor(60000));
        Assert.assertNull(ethash.getCachedFor(193));
        Assert.assertEquals(ethash.caches.size(), 2);
    }

    // sequential direct caching with gap between 0 and K block, K and N block
    @Test
    public void testRegularFlowWithGap() {
        EthashValidationHelper ethash = new EthashValidationHelper(direct);
        ethash.ethashAlgo = new EthashValidationHelperTest.EthashAlgoMock();
        // genesis
        ethash.preCache(0);
        Assert.assertNotNull(ethash.getCachedFor(0));
        Assert.assertEquals(ethash.caches.size(), 1);
        Assert.assertNull(ethash.getCachedFor(30000));
        // block 100_000, cache must have been reset
        ethash.preCache(100000);
        Assert.assertNotNull(ethash.getCachedFor(100000));
        Assert.assertNotNull(ethash.getCachedFor(120000));
        Assert.assertNull(ethash.getCachedFor(0));
        Assert.assertEquals(ethash.caches.size(), 2);
        // block 120_193, caches shifted by one epoch
        ethash.preCache(120193);
        Assert.assertNotNull(ethash.getCachedFor(120000));
        Assert.assertNotNull(ethash.getCachedFor(150000));
        Assert.assertNull(ethash.getCachedFor(100000));
        Assert.assertEquals(ethash.caches.size(), 2);
        // block 300_000, caches have been reset once again
        ethash.preCache(300000);
        Assert.assertNotNull(ethash.getCachedFor(300000));
        Assert.assertNotNull(ethash.getCachedFor(299000));
        Assert.assertNull(ethash.getCachedFor(120000));
        Assert.assertNull(ethash.getCachedFor(150000));
        Assert.assertEquals(ethash.caches.size(), 2);
    }

    // sequential reverse flow, like a flow that is used in reverse header downloading
    @Test
    public void testReverseFlow() {
        EthashValidationHelper ethash = new EthashValidationHelper(reverse);
        ethash.ethashAlgo = new EthashValidationHelperTest.EthashAlgoMock();
        // init on 15_000 block, 0 and 1st epochs are cached
        ethash.preCache(15000);
        Assert.assertNotNull(ethash.getCachedFor(15000));
        Assert.assertNotNull(ethash.getCachedFor(30000));
        Assert.assertEquals(ethash.caches.size(), 2);
        ethash = new EthashValidationHelper(reverse);
        ethash.ethashAlgo = new EthashValidationHelperTest.EthashAlgoMock();
        // init on 14_999 block, only 0 epoch is cached
        ethash.preCache(14999);
        Assert.assertNotNull(ethash.getCachedFor(14999));
        Assert.assertNull(ethash.getCachedFor(30000));
        Assert.assertEquals(ethash.caches.size(), 1);
        ethash = new EthashValidationHelper(reverse);
        ethash.ethashAlgo = new EthashValidationHelperTest.EthashAlgoMock();
        // init on 100_000 block, 2nd and 3rd epochs are cached
        ethash.preCache(100000);
        Assert.assertNotNull(ethash.getCachedFor(100000));
        Assert.assertNotNull(ethash.getCachedFor(80000));
        Assert.assertNull(ethash.getCachedFor(120000));
        Assert.assertEquals(ethash.caches.size(), 2);
        // block 75_000, nothing has changed
        ethash.preCache(75000);
        Assert.assertNotNull(ethash.getCachedFor(100000));
        Assert.assertNotNull(ethash.getCachedFor(75000));
        Assert.assertNull(ethash.getCachedFor(120000));
        Assert.assertNull(ethash.getCachedFor(59000));
        Assert.assertEquals(ethash.caches.size(), 2);
        // block 74_999, caches are shifted by 1 epoch toward 0 epoch
        ethash.preCache(74999);
        Assert.assertNotNull(ethash.getCachedFor(74999));
        Assert.assertNotNull(ethash.getCachedFor(59000));
        Assert.assertNull(ethash.getCachedFor(100000));
        Assert.assertEquals(ethash.caches.size(), 2);
        // block 44_999, caches are shifted by 1 epoch toward 0 epoch
        ethash.preCache(44999);
        Assert.assertNotNull(ethash.getCachedFor(44999));
        Assert.assertNotNull(ethash.getCachedFor(19000));
        Assert.assertNull(ethash.getCachedFor(80000));
        Assert.assertEquals(ethash.caches.size(), 2);
        // block 14_999, caches are shifted by 1 epoch toward 0 epoch
        ethash.preCache(14999);
        Assert.assertNotNull(ethash.getCachedFor(14999));
        Assert.assertNotNull(ethash.getCachedFor(0));
        Assert.assertNotNull(ethash.getCachedFor(30000));
        Assert.assertEquals(ethash.caches.size(), 2);
        // block 1, nothing has changed
        ethash.preCache(1);
        Assert.assertNotNull(ethash.getCachedFor(1));
        Assert.assertNotNull(ethash.getCachedFor(0));
        Assert.assertNotNull(ethash.getCachedFor(30000));
        Assert.assertEquals(ethash.caches.size(), 2);
        // block 0, nothing has changed
        ethash.preCache(0);
        Assert.assertNotNull(ethash.getCachedFor(0));
        Assert.assertNotNull(ethash.getCachedFor(30000));
        Assert.assertEquals(ethash.caches.size(), 2);
    }

    // sequential reverse flow with gap
    @Test
    public void testReverseFlowWithGap() {
        EthashValidationHelper ethash = new EthashValidationHelper(reverse);
        ethash.ethashAlgo = new EthashValidationHelperTest.EthashAlgoMock();
        // init on 300_000 block
        ethash.preCache(300000);
        Assert.assertNotNull(ethash.getCachedFor(300000));
        Assert.assertNotNull(ethash.getCachedFor(275000));
        Assert.assertNull(ethash.getCachedFor(330000));
        Assert.assertEquals(ethash.caches.size(), 2);
        // jump to 100_000 block, 2nd and 3rd epochs are cached
        ethash.preCache(100000);
        Assert.assertNotNull(ethash.getCachedFor(100000));
        Assert.assertNotNull(ethash.getCachedFor(80000));
        Assert.assertNull(ethash.getCachedFor(120000));
        Assert.assertEquals(ethash.caches.size(), 2);
        // block 74_999, caches are shifted by 1 epoch toward 0 epoch
        ethash.preCache(74999);
        Assert.assertNotNull(ethash.getCachedFor(74999));
        Assert.assertNotNull(ethash.getCachedFor(59000));
        Assert.assertNull(ethash.getCachedFor(100000));
        Assert.assertEquals(ethash.caches.size(), 2);
        // jump to 14_999, caches are shifted by 1 epoch toward 0 epoch
        ethash.preCache(14999);
        Assert.assertNotNull(ethash.getCachedFor(14999));
        Assert.assertNotNull(ethash.getCachedFor(0));
        Assert.assertEquals(ethash.caches.size(), 1);
    }
}

