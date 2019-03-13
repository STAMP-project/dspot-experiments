/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.security.authorization;


import AclAction.EXECUTE;
import AclAction.READ;
import AclAction.WRITE;
import AclEntryType.NAMED_GROUP;
import AclEntryType.NAMED_USER;
import AclEntryType.OTHER;
import AclEntryType.OWNING_GROUP;
import AclEntryType.OWNING_USER;
import Mode.Bits.ALL;
import Mode.Bits.READ_EXECUTE;
import Mode.Bits.WRITE_EXECUTE;
import com.google.common.collect.Lists;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the {@link AccessControlList} class.
 */
public class AccessControlListTest {
    private static final String OWNING_USER = "owning_user";

    private static final String OWNING_GROUP = "owning_group";

    private static final String NAMED_USER = "named_user";

    private static final String NAMED_GROUP = "named_group";

    private static final String NAMED_GROUP2 = "named_group2";

    private static final String OTHER_USER = "other_user";

    private static final String OTHER_GROUP = "other_group";

    /**
     * Tests the constructor contract.
     */
    @Test
    public void constructor() {
        AccessControlList acl = new AccessControlList();
        Assert.assertEquals("", acl.getOwningUser());
        Assert.assertEquals("", acl.getOwningGroup());
    }

    /**
     * Tests getting and setting owner and group.
     */
    @Test
    public void ownerGroup() {
        AccessControlList acl = new AccessControlList();
        acl.setOwningUser(AccessControlListTest.OWNING_USER);
        acl.setOwningGroup(AccessControlListTest.OWNING_GROUP);
        Assert.assertEquals(AccessControlListTest.OWNING_USER, acl.getOwningUser());
        Assert.assertEquals(AccessControlListTest.OWNING_GROUP, acl.getOwningGroup());
    }

    /**
     * Tests setting and getting permitted actions.
     */
    @Test
    public void actions() {
        AccessControlList acl = new AccessControlList();
        // owning user: rwx
        // owning group: -rx
        // other: ---
        // named user: rwx
        // named group: w-x
        acl.setOwningUser(AccessControlListTest.OWNING_USER);
        acl.setOwningGroup(AccessControlListTest.OWNING_GROUP);
        acl.setEntry(new AclEntry.Builder().setType(AclEntryType.OWNING_USER).setSubject(AccessControlListTest.OWNING_USER).addAction(READ).addAction(WRITE).addAction(EXECUTE).build());
        acl.setEntry(new AclEntry.Builder().setType(AclEntryType.OWNING_GROUP).setSubject(AccessControlListTest.OWNING_GROUP).addAction(READ).addAction(EXECUTE).build());
        acl.setEntry(new AclEntry.Builder().setType(OTHER).build());
        acl.setEntry(new AclEntry.Builder().setType(AclEntryType.NAMED_USER).setSubject(AccessControlListTest.NAMED_USER).addAction(READ).addAction(WRITE).addAction(EXECUTE).build());
        acl.setEntry(new AclEntry.Builder().setType(AclEntryType.NAMED_GROUP).setSubject(AccessControlListTest.NAMED_GROUP).addAction(WRITE).addAction(EXECUTE).build());
        acl.updateMask();
        // Verify mode.
        // owning user
        Assert.assertTrue(checkMode(acl, AccessControlListTest.OWNING_USER, Collections.emptyList(), ALL));
        // owning group
        Assert.assertTrue(checkMode(acl, AccessControlListTest.OTHER_USER, Lists.newArrayList(AccessControlListTest.OWNING_GROUP), READ_EXECUTE));
        Assert.assertFalse(checkMode(acl, AccessControlListTest.OTHER_USER, Lists.newArrayList(AccessControlListTest.OWNING_GROUP), Mode.Bits.WRITE));
        // other
        Assert.assertFalse(checkMode(acl, AccessControlListTest.OTHER_USER, Collections.emptyList(), Mode.Bits.READ));
        Assert.assertFalse(checkMode(acl, AccessControlListTest.OTHER_USER, Collections.emptyList(), Mode.Bits.WRITE));
        Assert.assertFalse(checkMode(acl, AccessControlListTest.OTHER_USER, Collections.emptyList(), Mode.Bits.EXECUTE));
        // named user
        Assert.assertTrue(checkMode(acl, AccessControlListTest.NAMED_USER, Collections.emptyList(), ALL));
        // named group
        Assert.assertTrue(checkMode(acl, AccessControlListTest.OTHER_USER, Lists.newArrayList(AccessControlListTest.NAMED_GROUP), WRITE_EXECUTE));
        Assert.assertFalse(checkMode(acl, AccessControlListTest.OTHER_USER, Lists.newArrayList(AccessControlListTest.NAMED_GROUP), Mode.Bits.READ));
    }

    /**
     * Tests {@link AccessControlList#getMode()}.
     */
    @Test
    public void getMode() {
        AccessControlList acl = new AccessControlList();
        Assert.assertEquals(0, acl.getMode());
        acl.setEntry(new AclEntry.Builder().setType(AclEntryType.OWNING_USER).setSubject(AccessControlListTest.OWNING_USER).addAction(READ).build());
        acl.setEntry(new AclEntry.Builder().setType(AclEntryType.OWNING_GROUP).setSubject(AccessControlListTest.OWNING_GROUP).addAction(WRITE).build());
        acl.setEntry(new AclEntry.Builder().setType(OTHER).addAction(EXECUTE).build());
        Assert.assertEquals(toShort(), acl.getMode());
    }

    /**
     * Tests {@link AccessControlList#setMode(short)}.
     */
    @Test
    public void setMode() {
        AccessControlList acl = new AccessControlList();
        short mode = new Mode(Mode.Bits.EXECUTE, Mode.Bits.WRITE, Mode.Bits.READ).toShort();
        acl.setMode(mode);
        Assert.assertEquals(mode, acl.getMode());
    }

    /**
     * Tests {@link AccessControlList#checkPermission(String, List, AclAction)}.
     */
    @Test
    public void checkPermission() {
        AccessControlList acl = new AccessControlList();
        setPermissions(acl);
        Assert.assertTrue(checkMode(acl, AccessControlListTest.OWNING_USER, Collections.emptyList(), ALL));
        Assert.assertTrue(checkMode(acl, AccessControlListTest.NAMED_USER, Collections.emptyList(), READ_EXECUTE));
        Assert.assertFalse(checkMode(acl, AccessControlListTest.NAMED_USER, Collections.emptyList(), Mode.Bits.WRITE));
        Assert.assertTrue(checkMode(acl, AccessControlListTest.OTHER_USER, Lists.newArrayList(AccessControlListTest.OWNING_GROUP), READ_EXECUTE));
        Assert.assertFalse(checkMode(acl, AccessControlListTest.OTHER_USER, Lists.newArrayList(AccessControlListTest.OWNING_GROUP), Mode.Bits.WRITE));
        Assert.assertTrue(checkMode(acl, AccessControlListTest.OTHER_USER, Lists.newArrayList(AccessControlListTest.NAMED_GROUP), Mode.Bits.READ));
        Assert.assertFalse(checkMode(acl, AccessControlListTest.OTHER_USER, Lists.newArrayList(AccessControlListTest.NAMED_GROUP), Mode.Bits.WRITE));
        Assert.assertFalse(checkMode(acl, AccessControlListTest.OTHER_USER, Lists.newArrayList(AccessControlListTest.NAMED_GROUP), Mode.Bits.EXECUTE));
        Assert.assertTrue(checkMode(acl, AccessControlListTest.OTHER_USER, Lists.newArrayList(AccessControlListTest.OTHER_GROUP), Mode.Bits.EXECUTE));
        Assert.assertFalse(checkMode(acl, AccessControlListTest.OTHER_USER, Lists.newArrayList(AccessControlListTest.OTHER_GROUP), Mode.Bits.READ));
        Assert.assertFalse(checkMode(acl, AccessControlListTest.OTHER_USER, Lists.newArrayList(AccessControlListTest.OTHER_GROUP), Mode.Bits.WRITE));
    }

    /**
     * Tests {@link AccessControlList#getPermission(String, List)}.
     */
    @Test
    public void getPermission() {
        AccessControlList acl = new AccessControlList();
        setPermissions(acl);
        assertMode(ALL, acl, AccessControlListTest.OWNING_USER, Collections.emptyList());
        assertMode(READ_EXECUTE, acl, AccessControlListTest.NAMED_USER, Collections.emptyList());
        assertMode(READ_EXECUTE, acl, AccessControlListTest.OTHER_USER, Lists.newArrayList(AccessControlListTest.OWNING_GROUP));
        assertMode(Mode.Bits.READ, acl, AccessControlListTest.OTHER_USER, Lists.newArrayList(AccessControlListTest.NAMED_GROUP));
        assertMode(WRITE_EXECUTE, acl, AccessControlListTest.OTHER_USER, Lists.newArrayList(AccessControlListTest.NAMED_GROUP2));
        assertMode(ALL, acl, AccessControlListTest.OTHER_USER, Lists.newArrayList(AccessControlListTest.NAMED_GROUP, AccessControlListTest.NAMED_GROUP2));
        assertMode(Mode.Bits.EXECUTE, acl, AccessControlListTest.OTHER_USER, Collections.emptyList());
        assertMode(Mode.Bits.EXECUTE, acl, AccessControlListTest.OTHER_USER, Lists.newArrayList(AccessControlListTest.OTHER_GROUP));
    }
}

