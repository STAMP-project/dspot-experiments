/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.impl;


import AccessMode.READ;
import AccessMode.WRITE;
import SecureTreeNode.EVERYBODY;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;


public class SecureTreeNodeTest {
    private TestingAuthenticationToken anonymous;

    @Test
    public void testEmptyRoot() {
        SecureTreeNode root = new SecureTreeNode();
        // smoke tests
        Assert.assertNull(root.getChild("NotThere"));
        Assert.assertEquals(EVERYBODY, root.getAuthorizedRoles(READ));
        Assert.assertEquals(EVERYBODY, root.getAuthorizedRoles(WRITE));
        // empty, deepest node is itself
        SecureTreeNode node = root.getDeepestNode(new String[]{ "a", "b" });
        Assert.assertSame(root, node);
        // allows access to everyone
        Assert.assertTrue(root.canAccess(anonymous, WRITE));
        Assert.assertTrue(root.canAccess(anonymous, READ));
        // make sure this includes not having a current user as well
        Assert.assertTrue(root.canAccess(null, WRITE));
        Assert.assertTrue(root.canAccess(null, READ));
    }
}

