package org.kairosdb.rollup;


import java.util.Date;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class RollupTaskStatusStoreImplTest extends RollupTestBase {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final String HOST1 = "host1";

    private static final String HOST2 = "host2";

    private static final String HOST3 = "host3";

    private static final Date DATE1 = new Date();

    private static final Date DATE2 = new Date();

    private static final Date DATE3 = new Date();

    private static final String ID1 = "ID1";

    private static final String ID2 = "ID2";

    private static final String ID3 = "ID3";

    private RollupTaskStatusStore store;

    @Test
    public void test_writeRead_nullId_Invalid() throws RollUpException {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("id cannot be null or empty");
        store.write(null, new RollupTaskStatus(new Date(), "host"));
    }

    @Test
    public void test_writeRead_emptyId_Invalid() throws RollUpException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("id cannot be null or empty");
        store.write("", new RollupTaskStatus(new Date(), "host"));
    }

    @Test
    public void test_writeReadRemove() throws RollUpException {
        RollupTaskStatus status1 = new RollupTaskStatus(RollupTaskStatusStoreImplTest.DATE1, RollupTaskStatusStoreImplTest.HOST1);
        RollupTaskStatus status2 = new RollupTaskStatus(RollupTaskStatusStoreImplTest.DATE2, RollupTaskStatusStoreImplTest.HOST2);
        RollupTaskStatus status3 = new RollupTaskStatus(RollupTaskStatusStoreImplTest.DATE3, RollupTaskStatusStoreImplTest.HOST3);
        store.write(RollupTaskStatusStoreImplTest.ID1, status1);
        store.write(RollupTaskStatusStoreImplTest.ID2, status2);
        store.write(RollupTaskStatusStoreImplTest.ID3, status3);
        Assert.assertThat(store.read(RollupTaskStatusStoreImplTest.ID1), CoreMatchers.equalTo(status1));
        Assert.assertThat(store.read(RollupTaskStatusStoreImplTest.ID2), CoreMatchers.equalTo(status2));
        Assert.assertThat(store.read(RollupTaskStatusStoreImplTest.ID3), CoreMatchers.equalTo(status3));
        store.remove(RollupTaskStatusStoreImplTest.ID2);
        store.remove(RollupTaskStatusStoreImplTest.ID1);
        Assert.assertNull(store.read(RollupTaskStatusStoreImplTest.ID1));
        Assert.assertNull(store.read(RollupTaskStatusStoreImplTest.ID2));
        Assert.assertThat(store.read(RollupTaskStatusStoreImplTest.ID3), CoreMatchers.equalTo(status3));
    }
}

