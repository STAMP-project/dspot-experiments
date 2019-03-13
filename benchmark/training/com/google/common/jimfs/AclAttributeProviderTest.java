/**
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.common.jimfs;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryFlag;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link AclAttributeProvider}.
 *
 * @author Colin Decker
 */
@RunWith(JUnit4.class)
public class AclAttributeProviderTest extends AbstractAttributeProviderTest<AclAttributeProvider> {
    private static final UserPrincipal USER = UserLookupService.createUserPrincipal("user");

    private static final UserPrincipal FOO = UserLookupService.createUserPrincipal("foo");

    private static final ImmutableList<AclEntry> defaultAcl = new ImmutableList.Builder<AclEntry>().add(AclEntry.newBuilder().setType(AclEntryType.ALLOW).setFlags(AclEntryFlag.DIRECTORY_INHERIT).setPermissions(AclEntryPermission.DELETE, AclEntryPermission.APPEND_DATA).setPrincipal(AclAttributeProviderTest.USER).build()).add(AclEntry.newBuilder().setType(AclEntryType.ALLOW).setFlags(AclEntryFlag.DIRECTORY_INHERIT).setPermissions(AclEntryPermission.DELETE, AclEntryPermission.APPEND_DATA).setPrincipal(AclAttributeProviderTest.FOO).build()).build();

    @Test
    public void testInitialAttributes() {
        assertThat(provider.get(file, "acl")).isEqualTo(AclAttributeProviderTest.defaultAcl);
    }

    @Test
    public void testSet() {
        assertSetAndGetSucceeds("acl", ImmutableList.of());
        assertSetFailsOnCreate("acl", ImmutableList.of());
        assertSetFails("acl", ImmutableSet.of());
        assertSetFails("acl", ImmutableList.of("hello"));
    }

    @Test
    public void testView() throws IOException {
        AclFileAttributeView view = provider.view(fileLookup(), ImmutableMap.<String, FileAttributeView>of("owner", new OwnerAttributeProvider().view(fileLookup(), AbstractAttributeProviderTest.NO_INHERITED_VIEWS)));
        Assert.assertNotNull(view);
        assertThat(view.name()).isEqualTo("acl");
        assertThat(view.getAcl()).isEqualTo(AclAttributeProviderTest.defaultAcl);
        view.setAcl(ImmutableList.<AclEntry>of());
        view.setOwner(AclAttributeProviderTest.FOO);
        assertThat(view.getAcl()).isEqualTo(ImmutableList.<AclEntry>of());
        assertThat(view.getOwner()).isEqualTo(AclAttributeProviderTest.FOO);
        assertThat(file.getAttribute("acl", "acl")).isEqualTo(ImmutableList.<AclEntry>of());
    }
}

