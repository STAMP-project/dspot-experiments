package org.kairosdb.core;


import java.util.Date;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.kairosdb.core.datastore.ServiceKeyValue;
import org.kairosdb.core.exception.DatastoreException;
import org.kairosdb.testing.FakeServiceKeyStore;
import org.mockito.Mock;


public class HostManagerTest {
    private static final String SERVICE = "_Hosts";

    private static final String SERVICE_KEY = "Active";

    private HostManager manager;

    private FakeServiceKeyStore keyStore = new FakeServiceKeyStore();

    @Mock
    private ScheduledExecutorService mockExecutorService;

    @Test
    public void test_checkHostChanges() throws DatastoreException {
        keyStore.setValue(HostManagerTest.SERVICE, HostManagerTest.SERVICE_KEY, "1", "host1");
        keyStore.setValue(HostManagerTest.SERVICE, HostManagerTest.SERVICE_KEY, "2", "host2");
        keyStore.setValue(HostManagerTest.SERVICE, HostManagerTest.SERVICE_KEY, "3", "host3");
        manager.checkHostChanges();
        Map<String, ServiceKeyValue> activeKairosHosts = manager.getActiveKairosHosts();
        MatcherAssert.assertThat(activeKairosHosts.size(), CoreMatchers.equalTo(4));
        MatcherAssert.assertThat(activeKairosHosts.get("1").getValue(), CoreMatchers.equalTo("host1"));
        MatcherAssert.assertThat(activeKairosHosts.get("2").getValue(), CoreMatchers.equalTo("host2"));
        MatcherAssert.assertThat(activeKairosHosts.get("3").getValue(), CoreMatchers.equalTo("host3"));
        MatcherAssert.assertThat(activeKairosHosts.get("myGuid").getValue(), CoreMatchers.equalTo("myHost"));
        MatcherAssert.assertThat(keyStore.getValue(HostManagerTest.SERVICE, HostManagerTest.SERVICE_KEY, "myGuid").getValue(), CoreMatchers.equalTo("myHost"));
    }

    @Test
    public void test_checkHostChanges_expireInactive() throws DatastoreException {
        keyStore.setValue(HostManagerTest.SERVICE, HostManagerTest.SERVICE_KEY, "1", "host1");
        keyStore.setValue(HostManagerTest.SERVICE, HostManagerTest.SERVICE_KEY, "2", "host2");
        keyStore.setValue(HostManagerTest.SERVICE, HostManagerTest.SERVICE_KEY, "3", "host3");
        manager.checkHostChanges();
        long timeChange = 1000 * 10;// 10 seconds

        keyStore.setKeyModificationTime(HostManagerTest.SERVICE, HostManagerTest.SERVICE_KEY, "1", new Date(timeChange));
        keyStore.setKeyModificationTime(HostManagerTest.SERVICE, HostManagerTest.SERVICE_KEY, "3", new Date(timeChange));
        keyStore.setKeyModificationTime(HostManagerTest.SERVICE, HostManagerTest.SERVICE_KEY, "myGuid", new Date(timeChange));
        manager.checkHostChanges();
        Map<String, ServiceKeyValue> activeKairosHosts = manager.getActiveKairosHosts();
        Assert.assertNull(activeKairosHosts.get("1"));
        MatcherAssert.assertThat(activeKairosHosts.get("2").getValue(), CoreMatchers.equalTo("host2"));
        Assert.assertNull(activeKairosHosts.get("3"));
        MatcherAssert.assertThat(activeKairosHosts.get("myGuid").getValue(), CoreMatchers.equalTo("myHost"));// current host should always be there

    }
}

