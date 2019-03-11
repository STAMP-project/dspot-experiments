package com.netflix.astyanax.connectionpool.impl;


import TokenGenerator.MAXIMUM;
import com.netflix.astyanax.connectionpool.ConnectionPool;
import com.netflix.astyanax.connectionpool.Host;
import com.netflix.astyanax.connectionpool.HostConnectionPool;
import com.netflix.astyanax.connectionpool.Operation;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.retry.RetryPolicy;
import com.netflix.astyanax.retry.RunOnce;
import com.netflix.astyanax.serializers.BigIntegerSerializer;
import com.netflix.astyanax.test.TestClient;
import com.netflix.astyanax.test.TestOperation;
import com.netflix.astyanax.test.TestTokenRange;
import com.netflix.astyanax.test.TokenTestOperation;
import java.math.BigInteger;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TokenAwareConnectionPoolTest extends BaseConnectionPoolTest {
    private static Logger LOG = LoggerFactory.getLogger(TokenAwareConnectionPoolTest.class);

    private static Operation<TestClient, String> dummyOperation = new TestOperation();

    @Test
    public void testTokenMappingForMidRangeTokens() throws ConnectionException {
        ConnectionPool<TestClient> cp = createPool();
        List<Host> ring1 = makeRing(3, 1, 1);
        TokenAwareConnectionPoolTest.LOG.info(("testTokenMappingForMidRangeTokens\n" + (TestTokenRange.getRingDetails(ring1))));
        cp.setHosts(ring1);
        BigInteger threeNodeRingIncrement = MAXIMUM.divide(new BigInteger("3"));
        RetryPolicy retryPolicy = new RunOnce();
        BigInteger key = BigInteger.ZERO;
        TokenAwareConnectionPoolTest.LOG.info(((key.toString()) + " 127.0.1.2"));
        OperationResult<String> result = cp.executeWithFailover(new TokenTestOperation(key), retryPolicy);
        Assert.assertNotNull(result);
        Assert.assertEquals("127.0.1.2", result.getHost().getIpAddress());
        key = BigInteger.ONE;
        TokenAwareConnectionPoolTest.LOG.info(((key.toString()) + " 127.0.1.0"));
        result = cp.executeWithFailover(new TokenTestOperation(key), retryPolicy);
        Assert.assertNotNull(result);
        Assert.assertEquals("127.0.1.0", result.getHost().getIpAddress());
        key = threeNodeRingIncrement.subtract(BigInteger.ONE);
        TokenAwareConnectionPoolTest.LOG.info(((key.toString()) + " 127.0.1.0"));
        result = cp.executeWithFailover(new TokenTestOperation(key), retryPolicy);
        Assert.assertNotNull(result);
        Assert.assertEquals("127.0.1.0", result.getHost().getIpAddress());
        key = threeNodeRingIncrement;
        TokenAwareConnectionPoolTest.LOG.info(((key.toString()) + " 127.0.1.0"));
        result = cp.executeWithFailover(new TokenTestOperation(key), retryPolicy);
        Assert.assertNotNull(result);
        Assert.assertEquals("127.0.1.0", result.getHost().getIpAddress());
        key = threeNodeRingIncrement.add(BigInteger.ONE);
        TokenAwareConnectionPoolTest.LOG.info(((key.toString()) + " 127.0.1.1"));
        result = cp.executeWithFailover(new TokenTestOperation(key), retryPolicy);
        Assert.assertNotNull(result);
        Assert.assertEquals("127.0.1.1", result.getHost().getIpAddress());
        key = threeNodeRingIncrement.add(threeNodeRingIncrement).add(BigInteger.ONE);
        TokenAwareConnectionPoolTest.LOG.info(((key.toString()) + " 127.0.1.1"));
        result = cp.executeWithFailover(new TokenTestOperation(key), retryPolicy);
        Assert.assertNotNull(result);
        Assert.assertEquals("127.0.1.1", result.getHost().getIpAddress());
        key = threeNodeRingIncrement.add(threeNodeRingIncrement).add(BigInteger.ONE).add(BigInteger.ONE);
        TokenAwareConnectionPoolTest.LOG.info(((key.toString()) + " 127.0.1.2"));
        result = cp.executeWithFailover(new TokenTestOperation(key), retryPolicy);
        Assert.assertNotNull(result);
        Assert.assertEquals("127.0.1.2", result.getHost().getIpAddress());
    }

    @Test
    public void testTokenMappingForOrdinalTokens() throws ConnectionException {
        ConnectionPool<TestClient> cp = createPool();
        // the following will generate a ring of two nodes with the following characteristics;
        // node1 - ip = 127.0.1.0, token ownership range = (1200 , 0]
        // node1 - ip = 127.0.1.1, token ownership range = (0 , 600]
        // node1 - ip = 127.0.1.1, token ownership range = (600 , 1200]
        List<Host> ring1 = TestTokenRange.makeRing(3, 1, 1, BigInteger.ZERO, new BigInteger("1800"));
        TokenAwareConnectionPoolTest.LOG.info(("testTokenMappingForOrdinalTokens\n" + (TestTokenRange.getRingDetails(ring1))));
        cp.setHosts(ring1);
        BigInteger threeNodeRingIncrement = new BigInteger("600");
        Operation<TestClient, String> firstHostOp = new TokenTestOperation(BigInteger.ZERO);
        Operation<TestClient, String> secondHostOp = new TokenTestOperation(threeNodeRingIncrement);
        Operation<TestClient, String> thirdHostOp = new TokenTestOperation(threeNodeRingIncrement.multiply(new BigInteger("2")));
        Operation<TestClient, String> maxTokenHostOp = new TokenTestOperation(threeNodeRingIncrement.multiply(new BigInteger("3")));
        TokenAwareConnectionPoolTest.LOG.info(BigIntegerSerializer.get().fromByteBuffer(firstHostOp.getRowKey()).toString());
        TokenAwareConnectionPoolTest.LOG.info(BigIntegerSerializer.get().fromByteBuffer(secondHostOp.getRowKey()).toString());
        TokenAwareConnectionPoolTest.LOG.info(BigIntegerSerializer.get().fromByteBuffer(thirdHostOp.getRowKey()).toString());
        TokenAwareConnectionPoolTest.LOG.info(BigIntegerSerializer.get().fromByteBuffer(maxTokenHostOp.getRowKey()).toString());
        RetryPolicy retryPolicy = new RunOnce();
        OperationResult<String> result = cp.executeWithFailover(firstHostOp, retryPolicy);
        Assert.assertNotNull(result);
        Assert.assertEquals("127.0.1.2", result.getHost().getIpAddress());
        result = cp.executeWithFailover(secondHostOp, retryPolicy);
        Assert.assertNotNull(result);
        Assert.assertEquals("127.0.1.0", result.getHost().getIpAddress());
        result = cp.executeWithFailover(thirdHostOp, retryPolicy);
        Assert.assertNotNull(result);
        Assert.assertEquals("127.0.1.1", result.getHost().getIpAddress());
        result = cp.executeWithFailover(maxTokenHostOp, retryPolicy);
        Assert.assertNotNull(result);
        Assert.assertEquals("127.0.1.2", result.getHost().getIpAddress());
    }

    @Test
    public void testTokenToHostMappingInWrappedRange() throws ConnectionException {
        ConnectionPool<TestClient> cp = createPool();
        // the following will generate a ring of two nodes with the following characteristics;
        // node1 - ip = 127.0.1.0, token ownership range = (510 , 10]
        // node1 - ip = 127.0.1.1, token ownership range = (10 , 510]
        List<Host> ring1 = TestTokenRange.makeRing(2, 1, 1, BigInteger.TEN, new BigInteger("1010"));
        cp.setHosts(ring1);
        TokenAwareConnectionPoolTest.LOG.info(("testTokenToHostMappingInWrappedRange\n" + (TestTokenRange.getRingDetails(ring1))));
        Operation<TestClient, String> op = new TokenTestOperation(BigInteger.ZERO);
        RetryPolicy retryPolicy = new RunOnce();
        OperationResult<String> result = cp.executeWithFailover(op, retryPolicy);
        Assert.assertNotNull(result);
        // since token ownership wraps node2 should own token 0
        Assert.assertEquals("127.0.1.1", result.getHost().getIpAddress());
    }

    @Test
    public void testTokenToHostMappingOutsideOfRing() throws ConnectionException {
        ConnectionPool<TestClient> cp = createPool();
        // the following will generate a ring of two nodes with the following characteristics;
        // node1 - ip = 127.0.1.0, token ownership range = (500 , 0]
        // node1 - ip = 127.0.1.1, token ownership range = (0 , 500]
        List<Host> ring1 = TestTokenRange.makeRing(2, 1, 1, BigInteger.ZERO, new BigInteger("1000"));
        cp.setHosts(ring1);
        TokenAwareConnectionPoolTest.LOG.info(("testTokenToHostMappingOutsideOfRing\n" + (TestTokenRange.getRingDetails(ring1))));
        Operation<TestClient, String> op = new TokenTestOperation(new BigInteger("1250"));
        RetryPolicy retryPolicy = new RunOnce();
        OperationResult<String> result = cp.executeWithFailover(op, retryPolicy);
        Assert.assertNotNull(result);
        // requests for tokens outside the ring will be serviced by host associated with
        // 1st partition in ring
        Assert.assertEquals("127.0.1.1", result.getHost().getIpAddress());
    }

    @Test
    public void changeRingTest() {
        ConnectionPool<TestClient> cp = createPool();
        List<Host> ring1 = makeRing(6, 3, 1);
        List<Host> ring2 = makeRing(6, 3, 2);
        cp.setHosts(ring1);
        List<HostConnectionPool<TestClient>> hosts1 = cp.getActivePools();
        cp.setHosts(ring2);
        List<HostConnectionPool<TestClient>> hosts2 = cp.getActivePools();
        TokenAwareConnectionPoolTest.LOG.info(hosts1.toString());
        TokenAwareConnectionPoolTest.LOG.info(hosts2.toString());
    }
}

