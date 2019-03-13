package com.test.kryo;


import com.esotericsoftware.kryo.Kryo;
import com.jarvis.cache.serializer.kryo.CacheWrapperSerializer;
import com.jarvis.cache.serializer.kryo.DefaultKryoContext;
import com.jarvis.cache.serializer.kryo.KryoClassRegistration;
import com.jarvis.cache.serializer.kryo.KryoContext;
import com.jarvis.cache.to.CacheWrapper;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author stevie.wong
 */
public class KryoTest {
    @Test
    public void checkKryoThreadSafetyWithKryoPool() {
        // kryo pool factory context.
        KryoContext kryoContext = DefaultKryoContext.newKryoContextFactory(( kryo) -> {
            new com.test.kryo.CustomKKryoClassRegistration().register(kryo);
        });
        // run multiple threads.
        runExecutor(new KryoTest.KryoWorkerThread(kryoContext));
    }

    // ???????
    private static class CustomKKryoClassRegistration implements KryoClassRegistration {
        @Override
        public void register(Kryo kryo) {
            kryo.register(CacheWrapper.class, new CacheWrapperSerializer());
        }
    }

    private static class KryoWorkerThread implements Runnable {
        private int MAX = 10;

        private KryoContext kryoContext;

        public KryoWorkerThread(KryoContext kryoContext) {
            this.kryoContext = kryoContext;
        }

        @Override
        public void run() {
            for (int i = 0; i < (MAX); i++) {
                List<Map<String, Object>> list = new ArrayList<>();
                for (int k = 0; k < 3; k++) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("any-prop1", ("any-value1-" + k));
                    map.put("any-prop2", ("any-value2-" + k));
                    map.put("any-prop3", ("any-value3-" + k));
                    list.add(map);
                }
                // serialize list.
                byte[] listBytes = kryoContext.serialize(list);
                KryoTest.IdEntity entity1 = new KryoTest.IdEntity(1L, "test1", LocalDateTime.now());
                KryoTest.IdEntity entity2 = new KryoTest.IdEntity(2L, "test2", LocalDateTime.now());
                KryoTest.IdEntity entity3 = new KryoTest.IdEntity(3L, "test3", LocalDateTime.now());
                List<KryoTest.IdEntity> entities = new ArrayList<>();
                entities.add(entity1);
                entities.add(entity2);
                entities.add(entity3);
                CacheWrapper<List<KryoTest.IdEntity>> wrapper = new CacheWrapper(entities, 0);
                // serialize cache wrapper
                byte[] wrapperBytes = kryoContext.serialize(wrapper);
                // deserialize list.
                List<Map<String, Object>> retList = ((List<Map<String, Object>>) (kryoContext.deserialize(listBytes)));
                Assert.assertEquals(list.size(), retList.size());
                Assert.assertEquals(list.get(0), retList.get(0));
                // deserialize cache wrapper
                CacheWrapper<List<KryoTest.IdEntity>> retWrapper = ((CacheWrapper<List<KryoTest.IdEntity>>) (kryoContext.deserialize(wrapperBytes)));
                Assert.assertEquals(wrapper.getCacheObject().size(), retWrapper.getCacheObject().size());
                Assert.assertEquals(wrapper.getCacheObject().get(0).id, retWrapper.getCacheObject().get(0).id);
            }
        }
    }

    private static class IdEntity implements Serializable {
        private Long id;

        private String name;

        private LocalDateTime time;

        private IdEntity() {
            // ?????
        }

        public IdEntity(Long id, String name, LocalDateTime time) {
            this.id = id;
            this.name = name;
            this.time = time;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setTime(LocalDateTime time) {
            this.time = time;
        }
    }
}

