/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.acl;


import AclPermissionType.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;

import static AclPermissionType.ALLOW;
import static AclPermissionType.ANY;
import static AclPermissionType.DENY;
import static AclPermissionType.UNKNOWN;


public class AclPermissionTypeTest {
    private static class AclPermissionTypeTestInfo {
        private final AclPermissionType ty;

        private final int code;

        private final String name;

        private final boolean unknown;

        AclPermissionTypeTestInfo(AclPermissionType ty, int code, String name, boolean unknown) {
            this.ty = ty;
            this.code = code;
            this.name = name;
            this.unknown = unknown;
        }
    }

    private static final AclPermissionTypeTest.AclPermissionTypeTestInfo[] INFOS = new AclPermissionTypeTest.AclPermissionTypeTestInfo[]{ new AclPermissionTypeTest.AclPermissionTypeTestInfo(UNKNOWN, 0, "unknown", true), new AclPermissionTypeTest.AclPermissionTypeTestInfo(ANY, 1, "any", false), new AclPermissionTypeTest.AclPermissionTypeTestInfo(DENY, 2, "deny", false), new AclPermissionTypeTest.AclPermissionTypeTestInfo(ALLOW, 3, "allow", false) };

    @Test
    public void testIsUnknown() throws Exception {
        for (AclPermissionTypeTest.AclPermissionTypeTestInfo info : AclPermissionTypeTest.INFOS) {
            Assert.assertEquals((((info.ty) + " was supposed to have unknown == ") + (info.unknown)), info.unknown, info.ty.isUnknown());
        }
    }

    @Test
    public void testCode() throws Exception {
        Assert.assertEquals(AclPermissionType.values().length, AclPermissionTypeTest.INFOS.length);
        for (AclPermissionTypeTest.AclPermissionTypeTestInfo info : AclPermissionTypeTest.INFOS) {
            Assert.assertEquals((((info.ty) + " was supposed to have code == ") + (info.code)), info.code, info.ty.code());
            Assert.assertEquals(((("AclPermissionType.fromCode(" + (info.code)) + ") was supposed to be ") + (info.ty)), info.ty, AclPermissionType.fromCode(((byte) (info.code))));
        }
        Assert.assertEquals(UNKNOWN, AclPermissionType.fromCode(((byte) (120))));
    }

    @Test
    public void testName() throws Exception {
        for (AclPermissionTypeTest.AclPermissionTypeTestInfo info : AclPermissionTypeTest.INFOS) {
            Assert.assertEquals(((("AclPermissionType.fromString(" + (info.name)) + ") was supposed to be ") + (info.ty)), info.ty, AclPermissionType.fromString(info.name));
        }
        Assert.assertEquals(UNKNOWN, AclPermissionType.fromString("something"));
    }

    @Test
    public void testExhaustive() throws Exception {
        Assert.assertEquals(AclPermissionTypeTest.INFOS.length, AclPermissionType.values().length);
        for (int i = 0; i < (AclPermissionTypeTest.INFOS.length); i++) {
            Assert.assertEquals(AclPermissionTypeTest.INFOS[i].ty, AclPermissionType.values()[i]);
        }
    }
}

