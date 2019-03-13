package org.ethereum.datasource;


import org.ethereum.crypto.HashUtil;
import org.ethereum.util.ByteUtil;
import org.junit.Test;


/**
 *
 *
 * @author Mikhail Kalinin
 * @since 15.02.2018
 */
public class CountingQuotientFilterTest {
    @Test
    public void simpleTest() {
        CountingQuotientFilter f = CountingQuotientFilter.create(1000000, 1000000);
        f.insert(HashUtil.sha3(ByteUtil.intToBytes(0)));
        assert f.maybeContains(HashUtil.sha3(ByteUtil.intToBytes(0)));
        f.insert(HashUtil.sha3(ByteUtil.intToBytes(1)));
        f.remove(HashUtil.sha3(ByteUtil.intToBytes(0)));
        assert f.maybeContains(HashUtil.sha3(ByteUtil.intToBytes(1)));
        assert !(f.maybeContains(HashUtil.sha3(ByteUtil.intToBytes(0))));
        for (int i = 0; i < 10; i++) {
            f.insert(HashUtil.sha3(ByteUtil.intToBytes(2)));
        }
        assert f.maybeContains(HashUtil.sha3(ByteUtil.intToBytes(2)));
        for (int i = 0; i < 8; i++) {
            f.remove(HashUtil.sha3(ByteUtil.intToBytes(2)));
        }
        assert f.maybeContains(HashUtil.sha3(ByteUtil.intToBytes(2)));
        f.remove(HashUtil.sha3(ByteUtil.intToBytes(2)));
        assert f.maybeContains(HashUtil.sha3(ByteUtil.intToBytes(2)));
        f.remove(HashUtil.sha3(ByteUtil.intToBytes(2)));
        assert !(f.maybeContains(HashUtil.sha3(ByteUtil.intToBytes(2))));
        f.remove(HashUtil.sha3(ByteUtil.intToBytes(2)));// check that it breaks nothing

        assert !(f.maybeContains(HashUtil.sha3(ByteUtil.intToBytes(2))));
    }

    // elements have same fingerprint, but different hash
    @Test
    public void softCollisionTest() {
        CountingQuotientFilter f = CountingQuotientFilter.create(1000000, 1000000);
        f.insert((-1L));
        f.insert(Long.MAX_VALUE);
        f.insert(((Long.MAX_VALUE) - 1));
        assert f.maybeContains((-1L));
        assert f.maybeContains(Long.MAX_VALUE);
        assert f.maybeContains(((Long.MAX_VALUE) - 1));
        f.remove((-1L));
        assert f.maybeContains(Long.MAX_VALUE);
        assert f.maybeContains(((Long.MAX_VALUE) - 1));
        f.remove(Long.MAX_VALUE);
        assert f.maybeContains(((Long.MAX_VALUE) - 1));
        f.remove(((Long.MAX_VALUE) - 1));
        assert !(f.maybeContains((-1L)));
        assert !(f.maybeContains(Long.MAX_VALUE));
        assert !(f.maybeContains(((Long.MAX_VALUE) - 1)));
    }

    // elements have same fingerprint, but different hash
    @Test
    public void softCollisionTest2() {
        CountingQuotientFilter f = CountingQuotientFilter.create(1000000, 1000000);
        f.insert(-2291752425797372865L);
        f.insert(-59306787014888395L);
        f.insert(-59306787014888395L);
        f.remove(-2291752425797372865L);
        f.remove(-59306787014888395L);
        assert f.maybeContains(-59306787014888395L);
    }

    // elements have same hash
    @Test
    public void hardCollisionTest() {
        CountingQuotientFilter f = CountingQuotientFilter.create(10000000, 10000000);
        f.insert(Long.MAX_VALUE);
        f.insert(Long.MAX_VALUE);
        f.insert(Long.MAX_VALUE);
        assert f.maybeContains(Long.MAX_VALUE);
        f.remove(Long.MAX_VALUE);
        f.remove(Long.MAX_VALUE);
        assert f.maybeContains(Long.MAX_VALUE);
        f.remove((-1L));
        assert !(f.maybeContains((-1L)));
    }

    @Test
    public void resizeTest() {
        CountingQuotientFilter f = CountingQuotientFilter.create(1000, 1000);
        f.insert(Long.MAX_VALUE);
        f.insert(Long.MAX_VALUE);
        f.insert(Long.MAX_VALUE);
        f.insert(((Long.MAX_VALUE) - 1));
        for (int i = 100000; i < 200000; i++) {
            f.insert(ByteUtil.intToBytes(i));
        }
        assert f.maybeContains(Long.MAX_VALUE);
        for (int i = 100000; i < 200000; i++) {
            assert f.maybeContains(ByteUtil.intToBytes(i));
        }
        assert f.maybeContains(Long.MAX_VALUE);
        assert f.maybeContains(((Long.MAX_VALUE) - 1));
        f.remove(Long.MAX_VALUE);
        f.remove(Long.MAX_VALUE);
        f.remove(((Long.MAX_VALUE) - 1));
        assert f.maybeContains(Long.MAX_VALUE);
        f.remove(Long.MAX_VALUE);
        assert !(f.maybeContains(Long.MAX_VALUE));
        f.remove(((Long.MAX_VALUE) - 1));
        assert !(f.maybeContains(((Long.MAX_VALUE) - 1)));
    }
}

