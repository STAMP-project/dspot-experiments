/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.shardingproxy.transport.mysql.packet.command.query.binary.execute;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class MySQLNullBitmapTest {
    @Test
    public void assertGetNullBitmap() {
        MySQLNullBitmap actual = new MySQLNullBitmap(8, 0);
        Assert.assertThat(actual.getNullBitmap().length, CoreMatchers.is(1));
        actual = new MySQLNullBitmap(9, 0);
        Assert.assertThat(actual.getNullBitmap().length, CoreMatchers.is(2));
    }

    @Test
    public void assertIsNotNullParameter() {
        MySQLNullBitmap actual = new MySQLNullBitmap(8, 0);
        Assert.assertFalse(actual.isNullParameter(0));
    }

    @Test
    public void assertIsNullParameter() {
        MySQLNullBitmap actual = new MySQLNullBitmap(8, 0);
        actual.setNullBit(0);
        Assert.assertTrue(actual.isNullParameter(0));
    }
}

