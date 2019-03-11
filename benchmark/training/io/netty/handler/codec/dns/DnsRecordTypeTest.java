/**
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.dns;


import java.util.HashSet;
import org.junit.Assert;
import org.junit.Test;


public class DnsRecordTypeTest {
    @Test
    public void testSanity() throws Exception {
        Assert.assertEquals("More than one type has the same int value", DnsRecordTypeTest.allTypes().size(), new HashSet<DnsRecordType>(DnsRecordTypeTest.allTypes()).size());
    }

    /**
     * Test of hashCode method, of class DnsRecordType.
     */
    @Test
    public void testHashCode() throws Exception {
        for (DnsRecordType t : DnsRecordTypeTest.allTypes()) {
            Assert.assertEquals(t.intValue(), t.hashCode());
        }
    }

    /**
     * Test of equals method, of class DnsRecordType.
     */
    @Test
    public void testEquals() throws Exception {
        for (DnsRecordType t1 : DnsRecordTypeTest.allTypes()) {
            for (DnsRecordType t2 : DnsRecordTypeTest.allTypes()) {
                if (t1 != t2) {
                    Assert.assertNotEquals(t1, t2);
                }
            }
        }
    }

    /**
     * Test of find method, of class DnsRecordType.
     */
    @Test
    public void testFind() throws Exception {
        for (DnsRecordType t : DnsRecordTypeTest.allTypes()) {
            DnsRecordType found = DnsRecordType.valueOf(t.intValue());
            Assert.assertSame(t, found);
            found = DnsRecordType.valueOf(t.name());
            Assert.assertSame(t.name(), t, found);
        }
    }
}

