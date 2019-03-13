/**
 * Copyright (c) [2016] [ <ether.camp> ]
 * This file is part of the ethereumJ library.
 *
 * The ethereumJ library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ethereumJ library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ethereumJ library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ethereum.datasource;


import NodeKeyCompositor.PREFIX_BYTES;
import java.util.Map;
import org.ethereum.TestUtils;
import org.ethereum.datasource.rocksdb.RocksDbDataSource;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;


@Ignore
public class RocksDbDataSourceTest {
    @Test
    public void testBatchUpdating() {
        RocksDbDataSource dataSource = new RocksDbDataSource("test");
        dataSource.reset();
        final int batchSize = 100;
        Map<byte[], byte[]> batch = RocksDbDataSourceTest.createBatch(batchSize);
        dataSource.updateBatch(batch);
        Assert.assertEquals(batchSize, dataSource.keys().size());
        for (Map.Entry<byte[], byte[]> e : batch.entrySet()) {
            Assert.assertArrayEquals(e.getValue(), dataSource.get(e.getKey()));
            Assert.assertArrayEquals(e.getValue(), dataSource.prefixLookup(e.getKey(), PREFIX_BYTES));
        }
        dataSource.close();
    }

    @Test
    public void testPutting() {
        RocksDbDataSource dataSource = new RocksDbDataSource("test");
        dataSource.reset();
        byte[] key = TestUtils.randomBytes(32);
        dataSource.put(key, TestUtils.randomBytes(32));
        Assert.assertNotNull(dataSource.get(key));
        Assert.assertEquals(1, dataSource.keys().size());
        dataSource.close();
    }

    @Test
    public void testPrefixLookup() {
        RocksDbDataSource dataSource = new RocksDbDataSource("test");
        dataSource.reset();
        byte[] k1 = Hex.decode("a9539c810cc2e8fa20785bdd78ec36cc1dab4b41f0d531e80a5e5fd25c3037ee");
        byte[] k2 = Hex.decode("b25e1b5be78dbadf6c4e817c6d170bbb47e9916f8f6cc4607c5f3819ce98497b");
        byte[] v1;
        byte[] v2;
        byte[] v3;
        v3 = v1 = "v1".getBytes();
        v2 = "v2".getBytes();
        dataSource.put(k1, v1);
        Assert.assertArrayEquals(v1, dataSource.get(k1));
        Assert.assertArrayEquals(v1, dataSource.prefixLookup(k1, PREFIX_BYTES));
        dataSource.put(k2, v2);
        Assert.assertArrayEquals(v2, dataSource.get(k2));
        Assert.assertArrayEquals(v1, dataSource.prefixLookup(k1, PREFIX_BYTES));
        Assert.assertArrayEquals(v2, dataSource.prefixLookup(k2, PREFIX_BYTES));
        byte[] k3 = Hex.decode("a9539c810cc2e8fa20785bdd78ec36ccb25e1b5be78dbadf6c4e817c6d170bbb");
        byte[] k4 = Hex.decode("a9539c810cc2e8fa20785bdd78ec36cdb25e1b5be78dbadf6c4e817c6d170bbb");
        dataSource.put(k3, v3);
        dataSource.put(k4, v3);
        Assert.assertArrayEquals(v3, dataSource.get(k3));
        Assert.assertArrayEquals(v3, dataSource.get(k4));
        Assert.assertArrayEquals(v1, dataSource.prefixLookup(k1, PREFIX_BYTES));
        Assert.assertArrayEquals(v2, dataSource.prefixLookup(k2, PREFIX_BYTES));
        Assert.assertArrayEquals(v3, dataSource.prefixLookup(k3, PREFIX_BYTES));
        Assert.assertArrayEquals(v3, dataSource.prefixLookup(Hex.decode("a9539c810cc2e8fa20785bdd78ec36cc00000000000000000000000000000000"), PREFIX_BYTES));
        Assert.assertArrayEquals(v3, dataSource.prefixLookup(Hex.decode("a9539c810cc2e8fa20785bdd78ec36ccb25e1b5be78dbadf6c4e817c6d170bb0"), PREFIX_BYTES));
        Assert.assertNull(dataSource.prefixLookup(Hex.decode("a9539c810cc2e8fa20785bdd78ec36c000000000000000000000000000000000"), PREFIX_BYTES));
        dataSource.delete(k2);
        Assert.assertNull(dataSource.prefixLookup(k2, PREFIX_BYTES));
        Assert.assertArrayEquals(v3, dataSource.get(k3));
        dataSource.delete(k3);
        Assert.assertNull(dataSource.prefixLookup(k2, PREFIX_BYTES));
        Assert.assertArrayEquals(v1, dataSource.get(k1));
        dataSource.delete(k1);
        Assert.assertNull(dataSource.prefixLookup(k1, PREFIX_BYTES));
        Assert.assertNull(dataSource.prefixLookup(k2, PREFIX_BYTES));
        Assert.assertNull(dataSource.prefixLookup(k3, PREFIX_BYTES));
        Assert.assertNull(dataSource.get(k1));
        Assert.assertNull(dataSource.get(k2));
        Assert.assertNull(dataSource.get(k3));
        Assert.assertArrayEquals(v3, dataSource.get(k4));
        dataSource.put(Hex.decode("df92d643f6f19067a6a1cac3c37332d1631be8a462f0c2c41efb60078515ed50"), v1);
        Assert.assertArrayEquals(dataSource.prefixLookup(Hex.decode("df92d643f6f19067a6a1cac3c37332d1d1b3ede7e2015c259e493a1bff2ed58c"), PREFIX_BYTES), v1);
        dataSource.close();
    }
}

