package com.orientechnologies.orient.server.distributed.sequence;


import OGlobalConfiguration.DISTRIBUTED_ATOMIC_LOCK_TIMEOUT;
import org.junit.Test;


/**
 *
 *
 * @author Matan Shukry (matanshukry@gmail.com)
 * @since 3/2/2015
 */
public class ServerClusterLocalSequenceIT extends AbstractServerClusterSequenceTest {
    @Test
    public void test() throws Exception {
        final long previous = DISTRIBUTED_ATOMIC_LOCK_TIMEOUT.getValueAsLong();
        try {
            DISTRIBUTED_ATOMIC_LOCK_TIMEOUT.setValue(0);
            // OGlobalConfiguration.DISTRIBUTED_REPLICATION_PROTOCOL_VERSION.setValue(2);
            init(2);
            prepare(false);
            execute();
        } finally {
            DISTRIBUTED_ATOMIC_LOCK_TIMEOUT.setValue(previous);
        }
    }
}

