/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shiro.session.mgt;


import java.io.IOException;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


public class SimpleSessionTest {
    @Test
    public void testDefaultSerialization() throws Exception {
        SimpleSession session = new SimpleSession();
        long timeout = session.getTimeout();
        Date start = session.getStartTimestamp();
        Date lastAccess = session.getLastAccessTime();
        SimpleSession deserialized = serializeAndDeserialize(session);
        Assert.assertEquals(timeout, deserialized.getTimeout());
        Assert.assertEquals(start, deserialized.getStartTimestamp());
        Assert.assertEquals(lastAccess, deserialized.getLastAccessTime());
    }

    @Test
    public void serializeHost() throws IOException, ClassNotFoundException {
        SimpleSession session = new SimpleSession("localhost");
        Assert.assertEquals("localhost", serializeAndDeserialize(session).getHost());
    }

    @Test
    public void serializeExpired() throws IOException, ClassNotFoundException {
        SimpleSession session = new SimpleSession();
        session.setExpired(true);
        Assert.assertTrue(serializeAndDeserialize(session).isExpired());
    }
}

