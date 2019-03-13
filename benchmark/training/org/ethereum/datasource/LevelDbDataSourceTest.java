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


import DbSettings.DEFAULT;
import java.util.Map;
import org.ethereum.TestUtils;
import org.ethereum.datasource.leveldb.LevelDbDataSource;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public class LevelDbDataSourceTest {
    @Test
    public void testBatchUpdating() {
        LevelDbDataSource dataSource = new LevelDbDataSource("test");
        dataSource.init(DEFAULT);
        final int batchSize = 100;
        Map<byte[], byte[]> batch = LevelDbDataSourceTest.createBatch(batchSize);
        dataSource.updateBatch(batch);
        Assert.assertEquals(batchSize, dataSource.keys().size());
        dataSource.close();
    }

    @Test
    public void testPutting() {
        LevelDbDataSource dataSource = new LevelDbDataSource("test");
        dataSource.init(DEFAULT);
        byte[] key = TestUtils.randomBytes(32);
        dataSource.put(key, TestUtils.randomBytes(32));
        Assert.assertNotNull(dataSource.get(key));
        Assert.assertEquals(1, dataSource.keys().size());
        dataSource.close();
    }
}

