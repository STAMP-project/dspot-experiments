package org.whispersystems.textsecuregcm.tests.limits;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.whispersystems.textsecuregcm.limits.LeakyBucket;


public class LeakyBucketTest {
    @Test
    public void testFull() {
        LeakyBucket leakyBucket = new LeakyBucket(2, (1.0 / 2.0));
        Assert.assertTrue(leakyBucket.add(1));
        Assert.assertTrue(leakyBucket.add(1));
        Assert.assertFalse(leakyBucket.add(1));
        leakyBucket = new LeakyBucket(2, (1.0 / 2.0));
        Assert.assertTrue(leakyBucket.add(2));
        Assert.assertFalse(leakyBucket.add(1));
        Assert.assertFalse(leakyBucket.add(2));
    }

    @Test
    public void testLapseRate() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String serialized = ("{\"bucketSize\":2,\"leakRatePerMillis\":8.333333333333334E-6,\"spaceRemaining\":0,\"lastUpdateTimeMillis\":" + ((System.currentTimeMillis()) - (TimeUnit.MINUTES.toMillis(2)))) + "}";
        LeakyBucket leakyBucket = LeakyBucket.fromSerialized(mapper, serialized);
        Assert.assertTrue(leakyBucket.add(1));
        String serializedAgain = leakyBucket.serialize(mapper);
        LeakyBucket leakyBucketAgain = LeakyBucket.fromSerialized(mapper, serializedAgain);
        Assert.assertFalse(leakyBucketAgain.add(1));
    }

    @Test
    public void testLapseShort() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String serialized = ("{\"bucketSize\":2,\"leakRatePerMillis\":8.333333333333334E-6,\"spaceRemaining\":0,\"lastUpdateTimeMillis\":" + ((System.currentTimeMillis()) - (TimeUnit.MINUTES.toMillis(1)))) + "}";
        LeakyBucket leakyBucket = LeakyBucket.fromSerialized(mapper, serialized);
        Assert.assertFalse(leakyBucket.add(1));
    }
}

