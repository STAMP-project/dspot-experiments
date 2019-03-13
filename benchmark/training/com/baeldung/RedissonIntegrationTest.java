package com.baeldung;


import RScript.Mode.READ_ONLY;
import RScript.ReturnType.VALUE;
import RedisCommands.GET;
import RedisCommands.SET;
import StringCodec.INSTANCE;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.junit.Assert;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.RedissonMultiLock;
import org.redisson.client.RedisClient;
import org.redisson.client.RedisConnection;
import redis.embedded.RedisServer;


public class RedissonIntegrationTest {
    private static RedisServer redisServer;

    private static RedissonClient client;

    @Test
    public void givenMultipleKeysInRedis_thenGetAllKeys() {
        RedissonIntegrationTest.client.getBucket("key1").set("key1");
        RedissonIntegrationTest.client.getBucket("key2").set("key2");
        RedissonIntegrationTest.client.getBucket("key3").set("key3");
        RKeys keys = RedissonIntegrationTest.client.getKeys();
        Assert.assertTrue(((keys.count()) >= 3));
    }

    @Test
    public void givenKeysWithPatternInRedis_thenGetPatternKeys() {
        RedissonIntegrationTest.client.getBucket("key1").set("key1");
        RedissonIntegrationTest.client.getBucket("key2").set("key2");
        RedissonIntegrationTest.client.getBucket("key3").set("key3");
        RedissonIntegrationTest.client.getBucket("id4").set("id4");
        RKeys keys = RedissonIntegrationTest.client.getKeys();
        Iterable<String> keysWithPattern = keys.getKeysByPattern("key*");
        List keyWithPatternList = StreamSupport.stream(keysWithPattern.spliterator(), false).collect(Collectors.toList());
        Assert.assertTrue(((keyWithPatternList.size()) == 3));
    }

    @Test
    public void givenAnObject_thenSaveToRedis() {
        RBucket<Ledger> bucket = RedissonIntegrationTest.client.getBucket("ledger");
        Ledger ledger = new Ledger();
        ledger.setName("ledger1");
        bucket.set(ledger);
        Ledger returnedLedger = bucket.get();
        Assert.assertTrue(((returnedLedger != null) && (returnedLedger.getName().equals("ledger1"))));
    }

    @Test
    public void givenALong_thenSaveLongToRedisAndAtomicallyIncrement() {
        Long value = 5L;
        RAtomicLong atomicLong = RedissonIntegrationTest.client.getAtomicLong("myAtomicLong");
        atomicLong.set(value);
        Long returnValue = atomicLong.incrementAndGet();
        Assert.assertTrue((returnValue == 6L));
    }

    @Test
    public void givenTopicSubscribedToAChannel_thenReceiveMessageFromChannel() throws InterruptedException, ExecutionException {
        CompletableFuture<String> future = new CompletableFuture<>();
        RTopic<CustomMessage> subscribeTopic = RedissonIntegrationTest.client.getTopic("baeldung");
        subscribeTopic.addListener(( channel, customMessage) -> future.complete(customMessage.getMessage()));
        RTopic<CustomMessage> publishTopic = RedissonIntegrationTest.client.getTopic("baeldung");
        long clientsReceivedMessage = publishTopic.publish(new CustomMessage("This is a message"));
        Assert.assertEquals("This is a message", future.get());
    }

    @Test
    public void givenAMap_thenSaveMapToRedis() {
        RMap<String, Ledger> map = RedissonIntegrationTest.client.getMap("ledger");
        map.put("123", new Ledger("ledger"));
        Assert.assertTrue(map.get("123").getName().equals("ledger"));
    }

    @Test
    public void givenASet_thenSaveSetToRedis() {
        RSet<Ledger> ledgerSet = RedissonIntegrationTest.client.getSet("ledgerSet");
        ledgerSet.add(new Ledger("ledger"));
        Assert.assertTrue(ledgerSet.contains(new Ledger("ledger")));
    }

    @Test
    public void givenAList_thenSaveListToRedis() {
        RList<Ledger> ledgerList = RedissonIntegrationTest.client.getList("ledgerList");
        ledgerList.add(new Ledger("ledger"));
        Assert.assertTrue(ledgerList.contains(new Ledger("ledger")));
    }

    @Test
    public void givenLockSet_thenEnsureCanUnlock() {
        RLock lock = RedissonIntegrationTest.client.getLock("lock");
        lock.lock();
        Assert.assertTrue(lock.isLocked());
        lock.unlock();
        Assert.assertTrue((!(lock.isLocked())));
    }

    @Test
    public void givenMultipleLocksSet_thenEnsureAllCanUnlock() {
        RedissonClient clientInstance1 = Redisson.create();
        RedissonClient clientInstance2 = Redisson.create();
        RedissonClient clientInstance3 = Redisson.create();
        RLock lock1 = clientInstance1.getLock("lock1");
        RLock lock2 = clientInstance2.getLock("lock2");
        RLock lock3 = clientInstance3.getLock("lock3");
        RedissonMultiLock lock = new RedissonMultiLock(lock1, lock2, lock3);
        lock.lock();
        Assert.assertTrue((((lock1.isLocked()) && (lock2.isLocked())) && (lock3.isLocked())));
        lock.unlock();
        Assert.assertTrue((!(((lock1.isLocked()) || (lock2.isLocked())) || (lock3.isLocked()))));
    }

    @Test
    public void givenRemoteServiceMethodRegistered_thenInvokeMethod() {
        RRemoteService remoteService = RedissonIntegrationTest.client.getRemoteService();
        LedgerServiceImpl ledgerServiceImpl = new LedgerServiceImpl();
        remoteService.register(LedgerServiceInterface.class, ledgerServiceImpl);
        LedgerServiceInterface ledgerService = remoteService.get(LedgerServiceInterface.class);
        List<String> entries = ledgerService.getEntries(10);
        Assert.assertTrue((((entries.size()) == 3) && (entries.contains("entry1"))));
    }

    @Test
    public void givenLiveObjectPersisted_thenGetLiveObject() {
        RLiveObjectService service = RedissonIntegrationTest.client.getLiveObjectService();
        LedgerLiveObject ledger = new LedgerLiveObject();
        ledger.setName("ledger1");
        ledger = service.persist(ledger);
        LedgerLiveObject returnLedger = service.get(LedgerLiveObject.class, "ledger1");
        Assert.assertTrue(ledger.getName().equals(returnLedger.getName()));
    }

    @Test
    public void givenMultipleOperations_thenDoAllAtomically() {
        RBatch batch = RedissonIntegrationTest.client.createBatch();
        batch.getMap("ledgerMap").fastPutAsync("1", "2");
        batch.getMap("ledgerMap").putAsync("2", "5");
        List<?> result = batch.execute();
        RMap<String, String> map = RedissonIntegrationTest.client.getMap("ledgerMap");
        Assert.assertTrue((((result.size()) > 0) && (map.get("1").equals("2"))));
    }

    @Test
    public void givenLUAScript_thenExecuteScriptOnRedis() {
        RedissonIntegrationTest.client.getBucket("foo").set("bar");
        String result = RedissonIntegrationTest.client.getScript().eval(READ_ONLY, "return redis.call('get', 'foo')", VALUE);
        Assert.assertTrue(result.equals("bar"));
    }

    @Test
    public void givenLowLevelRedisCommands_thenExecuteLowLevelCommandsOnRedis() {
        RedisClient client = new RedisClient("localhost", 6379);
        RedisConnection conn = client.connect();
        conn.sync(INSTANCE, SET, "test", 0);
        String testValue = conn.sync(INSTANCE, GET, "test");
        conn.closeAsync();
        client.shutdown();
        Assert.assertTrue(testValue.equals("0"));
    }
}

