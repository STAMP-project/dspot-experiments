package com.zendesk.maxwell.replication;


import com.zendesk.maxwell.MaxwellTestWithIsolatedServer;
import com.zendesk.maxwell.filtering.Filter;
import com.zendesk.maxwell.schema.Schema;
import org.junit.Test;


public class TableCacheTest extends MaxwellTestWithIsolatedServer {
    @Test
    public void testHaTables() throws Exception {
        Schema schema = capture();
        TableCache cache = new TableCache("maxwell");
        // ensure we don't crash on not-really-existant alibaba tables
        cache.processEvent(schema, new Filter(), 1L, "mysql", "ha_health_check");
    }
}

