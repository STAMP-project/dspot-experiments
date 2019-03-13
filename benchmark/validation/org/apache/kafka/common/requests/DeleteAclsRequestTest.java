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


import org.apache.kafka.common.acl.AclBindingFilter;
import org.apache.kafka.common.acl.AclOperation;
import org.apache.kafka.common.acl.AclPermissionType;
import org.apache.kafka.common.errors.UnsupportedVersionException;
import org.apache.kafka.common.protocol.types.Struct;
import org.apache.kafka.common.resource.PatternType;
import org.apache.kafka.common.resource.ResourceType;
import org.junit.Test;


public class DeleteAclsRequestTest {
    private static final short V0 = 0;

    private static final short V1 = 1;

    private static final AclBindingFilter LITERAL_FILTER = new AclBindingFilter(new org.apache.kafka.common.resource.ResourcePatternFilter(ResourceType.TOPIC, "foo", PatternType.LITERAL), new org.apache.kafka.common.acl.AccessControlEntryFilter("User:ANONYMOUS", "127.0.0.1", AclOperation.READ, AclPermissionType.DENY));

    private static final AclBindingFilter PREFIXED_FILTER = new AclBindingFilter(new org.apache.kafka.common.resource.ResourcePatternFilter(ResourceType.GROUP, "prefix", PatternType.PREFIXED), new org.apache.kafka.common.acl.AccessControlEntryFilter("User:*", "127.0.0.1", AclOperation.CREATE, AclPermissionType.ALLOW));

    private static final AclBindingFilter ANY_FILTER = new AclBindingFilter(new org.apache.kafka.common.resource.ResourcePatternFilter(ResourceType.GROUP, "bar", PatternType.ANY), new org.apache.kafka.common.acl.AccessControlEntryFilter("User:*", "127.0.0.1", AclOperation.CREATE, AclPermissionType.ALLOW));

    private static final AclBindingFilter UNKNOWN_FILTER = new AclBindingFilter(new org.apache.kafka.common.resource.ResourcePatternFilter(ResourceType.UNKNOWN, "prefix", PatternType.PREFIXED), new org.apache.kafka.common.acl.AccessControlEntryFilter("User:*", "127.0.0.1", AclOperation.CREATE, AclPermissionType.ALLOW));

    @Test(expected = UnsupportedVersionException.class)
    public void shouldThrowOnV0IfPrefixed() {
        new DeleteAclsRequest(DeleteAclsRequestTest.V0, DeleteAclsRequestTest.aclFilters(DeleteAclsRequestTest.PREFIXED_FILTER));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnUnknownElements() {
        new DeleteAclsRequest(DeleteAclsRequestTest.V1, DeleteAclsRequestTest.aclFilters(DeleteAclsRequestTest.UNKNOWN_FILTER));
    }

    @Test
    public void shouldRoundTripLiteralV0() {
        final DeleteAclsRequest original = new DeleteAclsRequest(DeleteAclsRequestTest.V0, DeleteAclsRequestTest.aclFilters(DeleteAclsRequestTest.LITERAL_FILTER));
        final Struct struct = original.toStruct();
        final DeleteAclsRequest result = new DeleteAclsRequest(struct, DeleteAclsRequestTest.V0);
        DeleteAclsRequestTest.assertRequestEquals(original, result);
    }

    @Test
    public void shouldRoundTripAnyV0AsLiteral() {
        final DeleteAclsRequest original = new DeleteAclsRequest(DeleteAclsRequestTest.V0, DeleteAclsRequestTest.aclFilters(DeleteAclsRequestTest.ANY_FILTER));
        final DeleteAclsRequest expected = new DeleteAclsRequest(DeleteAclsRequestTest.V0, DeleteAclsRequestTest.aclFilters(new AclBindingFilter(new org.apache.kafka.common.resource.ResourcePatternFilter(DeleteAclsRequestTest.ANY_FILTER.patternFilter().resourceType(), DeleteAclsRequestTest.ANY_FILTER.patternFilter().name(), PatternType.LITERAL), DeleteAclsRequestTest.ANY_FILTER.entryFilter())));
        final DeleteAclsRequest result = new DeleteAclsRequest(original.toStruct(), DeleteAclsRequestTest.V0);
        DeleteAclsRequestTest.assertRequestEquals(expected, result);
    }

    @Test
    public void shouldRoundTripV1() {
        final DeleteAclsRequest original = new DeleteAclsRequest(DeleteAclsRequestTest.V1, DeleteAclsRequestTest.aclFilters(DeleteAclsRequestTest.LITERAL_FILTER, DeleteAclsRequestTest.PREFIXED_FILTER, DeleteAclsRequestTest.ANY_FILTER));
        final Struct struct = original.toStruct();
        final DeleteAclsRequest result = new DeleteAclsRequest(struct, DeleteAclsRequestTest.V1);
        DeleteAclsRequestTest.assertRequestEquals(original, result);
    }
}

