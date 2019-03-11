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


import AclOperation.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;

import static AclOperation.ALL;
import static AclOperation.ALTER;
import static AclOperation.ALTER_CONFIGS;
import static AclOperation.ANY;
import static AclOperation.CLUSTER_ACTION;
import static AclOperation.CREATE;
import static AclOperation.DELETE;
import static AclOperation.DESCRIBE;
import static AclOperation.DESCRIBE_CONFIGS;
import static AclOperation.IDEMPOTENT_WRITE;
import static AclOperation.READ;
import static AclOperation.UNKNOWN;
import static AclOperation.WRITE;


public class AclOperationTest {
    private static class AclOperationTestInfo {
        private final AclOperation operation;

        private final int code;

        private final String name;

        private final boolean unknown;

        AclOperationTestInfo(AclOperation operation, int code, String name, boolean unknown) {
            this.operation = operation;
            this.code = code;
            this.name = name;
            this.unknown = unknown;
        }
    }

    private static final AclOperationTest.AclOperationTestInfo[] INFOS = new AclOperationTest.AclOperationTestInfo[]{ new AclOperationTest.AclOperationTestInfo(UNKNOWN, 0, "unknown", true), new AclOperationTest.AclOperationTestInfo(ANY, 1, "any", false), new AclOperationTest.AclOperationTestInfo(ALL, 2, "all", false), new AclOperationTest.AclOperationTestInfo(READ, 3, "read", false), new AclOperationTest.AclOperationTestInfo(WRITE, 4, "write", false), new AclOperationTest.AclOperationTestInfo(CREATE, 5, "create", false), new AclOperationTest.AclOperationTestInfo(DELETE, 6, "delete", false), new AclOperationTest.AclOperationTestInfo(ALTER, 7, "alter", false), new AclOperationTest.AclOperationTestInfo(DESCRIBE, 8, "describe", false), new AclOperationTest.AclOperationTestInfo(CLUSTER_ACTION, 9, "cluster_action", false), new AclOperationTest.AclOperationTestInfo(DESCRIBE_CONFIGS, 10, "describe_configs", false), new AclOperationTest.AclOperationTestInfo(ALTER_CONFIGS, 11, "alter_configs", false), new AclOperationTest.AclOperationTestInfo(IDEMPOTENT_WRITE, 12, "idempotent_write", false) };

    @Test
    public void testIsUnknown() throws Exception {
        for (AclOperationTest.AclOperationTestInfo info : AclOperationTest.INFOS) {
            Assert.assertEquals((((info.operation) + " was supposed to have unknown == ") + (info.unknown)), info.unknown, info.operation.isUnknown());
        }
    }

    @Test
    public void testCode() throws Exception {
        Assert.assertEquals(AclOperation.values().length, AclOperationTest.INFOS.length);
        for (AclOperationTest.AclOperationTestInfo info : AclOperationTest.INFOS) {
            Assert.assertEquals((((info.operation) + " was supposed to have code == ") + (info.code)), info.code, info.operation.code());
            Assert.assertEquals(((("AclOperation.fromCode(" + (info.code)) + ") was supposed to be ") + (info.operation)), info.operation, AclOperation.fromCode(((byte) (info.code))));
        }
        Assert.assertEquals(UNKNOWN, AclOperation.fromCode(((byte) (120))));
    }

    @Test
    public void testName() throws Exception {
        for (AclOperationTest.AclOperationTestInfo info : AclOperationTest.INFOS) {
            Assert.assertEquals(((("AclOperation.fromString(" + (info.name)) + ") was supposed to be ") + (info.operation)), info.operation, AclOperation.fromString(info.name));
        }
        Assert.assertEquals(UNKNOWN, AclOperation.fromString("something"));
    }

    @Test
    public void testExhaustive() throws Exception {
        Assert.assertEquals(AclOperationTest.INFOS.length, AclOperation.values().length);
        for (int i = 0; i < (AclOperationTest.INFOS.length); i++) {
            Assert.assertEquals(AclOperationTest.INFOS[i].operation, AclOperation.values()[i]);
        }
    }
}

