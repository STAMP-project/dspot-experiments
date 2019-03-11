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
package org.apache.kafka.common.requests;


import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.acl.AclOperation;
import org.apache.kafka.common.acl.AclPermissionType;
import org.apache.kafka.common.errors.UnsupportedVersionException;
import org.apache.kafka.common.protocol.types.Struct;
import org.apache.kafka.common.resource.PatternType;
import org.apache.kafka.common.resource.ResourceType;
import org.junit.Test;

import static ApiError.NONE;


public class DescribeAclsResponseTest {
    private static final short V0 = 0;

    private static final short V1 = 1;

    private static final AclBinding LITERAL_ACL1 = new AclBinding(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "foo", PatternType.LITERAL), new org.apache.kafka.common.acl.AccessControlEntry("User:ANONYMOUS", "127.0.0.1", AclOperation.READ, AclPermissionType.DENY));

    private static final AclBinding LITERAL_ACL2 = new AclBinding(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.GROUP, "group", PatternType.LITERAL), new org.apache.kafka.common.acl.AccessControlEntry("User:*", "127.0.0.1", AclOperation.WRITE, AclPermissionType.ALLOW));

    private static final AclBinding PREFIXED_ACL1 = new AclBinding(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.GROUP, "prefix", PatternType.PREFIXED), new org.apache.kafka.common.acl.AccessControlEntry("User:*", "127.0.0.1", AclOperation.CREATE, AclPermissionType.ALLOW));

    private static final AclBinding UNKNOWN_ACL = new AclBinding(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.UNKNOWN, "foo", PatternType.LITERAL), new org.apache.kafka.common.acl.AccessControlEntry("User:ANONYMOUS", "127.0.0.1", AclOperation.READ, AclPermissionType.DENY));

    @Test(expected = UnsupportedVersionException.class)
    public void shouldThrowOnV0IfNotLiteral() {
        toStruct(DescribeAclsResponseTest.V0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfUnknown() {
        toStruct(DescribeAclsResponseTest.V0);
    }

    @Test
    public void shouldRoundTripV0() {
        final DescribeAclsResponse original = new DescribeAclsResponse(10, NONE, DescribeAclsResponseTest.aclBindings(DescribeAclsResponseTest.LITERAL_ACL1, DescribeAclsResponseTest.LITERAL_ACL2));
        final Struct struct = original.toStruct(DescribeAclsResponseTest.V0);
        final DescribeAclsResponse result = new DescribeAclsResponse(struct);
        DescribeAclsResponseTest.assertResponseEquals(original, result);
    }

    @Test
    public void shouldRoundTripV1() {
        final DescribeAclsResponse original = new DescribeAclsResponse(100, NONE, DescribeAclsResponseTest.aclBindings(DescribeAclsResponseTest.LITERAL_ACL1, DescribeAclsResponseTest.PREFIXED_ACL1));
        final Struct struct = original.toStruct(DescribeAclsResponseTest.V1);
        final DescribeAclsResponse result = new DescribeAclsResponse(struct);
        DescribeAclsResponseTest.assertResponseEquals(original, result);
    }
}

