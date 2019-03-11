package com.zendesk.maxwell.schema;


import com.zendesk.maxwell.MaxwellContext;
import com.zendesk.maxwell.MaxwellTestWithIsolatedServer;
import com.zendesk.maxwell.replication.BinlogPosition;
import com.zendesk.maxwell.replication.Position;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class PositionStoreThreadTest extends MaxwellTestWithIsolatedServer {
    @Test
    public void testStoresFinalPosition() throws Exception {
        MaxwellContext context = buildContext();
        MysqlPositionStore store = buildStore(context);
        Position initialPosition = new Position(new BinlogPosition(4L, "file"), 0L);
        Position finalPosition = new Position(new BinlogPosition(88L, "file"), 1L);
        PositionStoreThread thread = new PositionStoreThread(store, context);
        thread.setPosition(initialPosition);
        thread.setPosition(finalPosition);
        thread.storeFinalPosition();
        Assert.assertThat(store.get(), CoreMatchers.is(finalPosition));
    }

    @Test
    public void testDoesNotStoreUnchangedPosition() throws Exception {
        MaxwellContext context = buildContext();
        MysqlPositionStore store = buildStore(context);
        Position initialPosition = new Position(new BinlogPosition(4L, "file"), 0L);
        PositionStoreThread thread = new PositionStoreThread(store, context);
        thread.setPosition(initialPosition);
        thread.storeFinalPosition();
        Assert.assertThat(store.get(), CoreMatchers.nullValue());
    }
}

