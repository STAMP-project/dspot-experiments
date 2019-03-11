/**
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.cluster.hazelcast;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import org.geoserver.platform.resource.Resource.Lock;
import org.junit.Test;


public class HzLockProviderTest {
    private HazelcastInstance hz;

    private HzLockProvider lockProvider;

    private HzCluster cluster;

    @Test
    public void testAqcuire() {
        ILock lock = createMock(ILock.class);
        expect(this.hz.getLock(eq("path1"))).andReturn(lock);
        expect(lock.isLockedByCurrentThread()).andStubReturn(true);
        lock.lock();
        expectLastCall();
        expect(lock.isLocked()).andReturn(true).times(1);
        lock.unlock();
        expectLastCall();
        replay(lock, hz, cluster);
        Lock gsLock = lockProvider.acquire("path1");
        gsLock.release();
        verify(hz);
        verify(cluster);
        verify(lock);
    }
}

