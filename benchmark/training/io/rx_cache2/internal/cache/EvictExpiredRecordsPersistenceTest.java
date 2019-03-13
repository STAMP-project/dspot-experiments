package io.rx_cache2.internal.cache;


import io.reactivex.observers.TestObserver;
import io.rx_cache2.internal.Memory;
import io.rx_cache2.internal.Mock;
import io.rx_cache2.internal.Record;
import io.rx_cache2.internal.common.BaseTest;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


/**
 * Created by victor on 03/03/16.
 */
public class EvictExpiredRecordsPersistenceTest extends BaseTest {
    private EvictExpiredRecordsPersistence evictExpiredRecordsPersistenceUT;

    private HasRecordExpired hasRecordExpired;

    private TwoLayersCache twoLayersCache;

    private Memory memory;

    private static final long ONE_SECOND_LIFE = 1000;

    private static final long THIRTY_SECOND_LIFE = 30000;

    private static final long MORE_THAN_ONE_SECOND_LIFE = 1250;

    @Test
    public void Evict_Just_Expired_Records() {
        int recordsCount = 100;
        for (int i = 0; i < (recordsCount / 2); i++) {
            twoLayersCache.save((i + "_expired"), "", "", new Mock((i + "_expired")), EvictExpiredRecordsPersistenceTest.ONE_SECOND_LIFE, true, false);
            twoLayersCache.save((i + "_live"), "", "", new Mock((i + "_live")), EvictExpiredRecordsPersistenceTest.THIRTY_SECOND_LIFE, true, false);
        }
        waitTime(EvictExpiredRecordsPersistenceTest.MORE_THAN_ONE_SECOND_LIFE);
        MatcherAssert.assertThat(disk.allKeys().size(), Is.is(recordsCount));
        TestObserver<Integer> testObserver = evictExpiredRecordsPersistenceUT.startEvictingExpiredRecords().test();
        testObserver.awaitTerminalEvent();
        testObserver.assertNoErrors();
        List<String> allKeys = disk.allKeys();
        MatcherAssert.assertThat(allKeys.size(), Is.is((recordsCount / 2)));
        for (String key : allKeys) {
            key = key.substring(0, key.indexOf("$"));
            Record<Mock> record = twoLayersCache.retrieve(key, "", "", false, EvictExpiredRecordsPersistenceTest.THIRTY_SECOND_LIFE, false);
            assert record.getData().getMessage().contains("live");
            assert !(record.getData().getMessage().contains("expired"));
        }
    }
}

