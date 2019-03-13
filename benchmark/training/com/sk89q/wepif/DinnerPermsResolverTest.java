/**
 * WorldEdit, a Minecraft world manipulation toolkit
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldEdit team and contributors
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.sk89q.wepif;


import org.junit.Assert;
import org.junit.Test;


public class DinnerPermsResolverTest {
    private DinnerPermsResolver resolver;

    @Test
    public void testBasicResolving() {
        final TestOfflinePermissible permissible = new TestOfflinePermissible();
        permissible.setPermission("testperm.test1", true);
        Assert.assertTrue(resolver.hasPermission(permissible, "testperm.test1"));
        Assert.assertFalse(resolver.hasPermission(permissible, "testperm.somethingelse"));
        Assert.assertFalse(resolver.hasPermission(permissible, "testperm.test1.anything"));
        Assert.assertFalse(resolver.hasPermission(permissible, "completely.unrelated"));
        permissible.clearPermissions();
    }

    @Test
    public void testBasicWildcardResolution() {
        final TestOfflinePermissible permissible = new TestOfflinePermissible();
        permissible.setPermission("commandbook.spawnmob.*", true);
        Assert.assertTrue(resolver.hasPermission(permissible, "commandbook.spawnmob.pig"));
        Assert.assertTrue(resolver.hasPermission(permissible, "commandbook.spawnmob.spider"));
        Assert.assertTrue(resolver.hasPermission(permissible, "commandbook.spawnmob.spider.skeleton"));
        permissible.clearPermissions();
    }

    @Test
    public void testNegatingNodes() {
        final TestOfflinePermissible permissible = new TestOfflinePermissible();
        permissible.setPermission("commandbook.*", true);
        permissible.setPermission("commandbook.cuteasianboys", false);
        permissible.setPermission("commandbook.warp.*", false);
        permissible.setPermission("commandbook.warp.create", true);
        Assert.assertTrue(resolver.hasPermission(permissible, "commandbook.motd"));
        Assert.assertFalse(resolver.hasPermission(permissible, "commandbook.cuteasianboys"));
        Assert.assertFalse(resolver.hasPermission(permissible, "commandbook.warp.remove"));
        Assert.assertTrue(resolver.hasPermission(permissible, "commandbook.warp.create"));
        permissible.clearPermissions();
    }

    @Test
    public void testInGroup() {
        final TestOfflinePermissible permissible = new TestOfflinePermissible();
        permissible.setPermission("group.a", true);
        permissible.setPermission("group.b", true);
        Assert.assertTrue(resolver.inGroup(permissible, "a"));
        Assert.assertTrue(resolver.inGroup(permissible, "b"));
        Assert.assertFalse(resolver.inGroup(permissible, "c"));
    }
}

