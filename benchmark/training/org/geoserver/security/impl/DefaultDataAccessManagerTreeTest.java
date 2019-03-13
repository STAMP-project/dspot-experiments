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
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests parsing of the property file into a security tree, and the functionality of the tree as
 * well (building the tree by hand is tedious)
 *
 * @author Andrea Aime - TOPP
 */
public class DefaultDataAccessManagerTreeTest extends AbstractAuthorizationTest {
    @Test
    public void testWideOpen() throws Exception {
        SecureTreeNode root = buildTree("wideOpen.properties");
        Assert.assertEquals(0, root.children.size());
        // we have he "*" rules
        Assert.assertEquals(1, root.getAuthorizedRoles(READ).size());
        Assert.assertEquals(1, root.getAuthorizedRoles(WRITE).size());
        Assert.assertTrue(root.canAccess(anonymous, READ));
        Assert.assertTrue(root.canAccess(anonymous, WRITE));
    }

    @Test
    public void testLockedDown() throws Exception {
        SecureTreeNode root = buildTree("lockedDown.properties");
        Assert.assertEquals(0, root.children.size());
        final Set<String> readRoles = root.getAuthorizedRoles(READ);
        Assert.assertEquals(1, readRoles.size());
        Assert.assertTrue(readRoles.contains("WRITER"));
        final Set<String> writeRoles = root.getAuthorizedRoles(WRITE);
        Assert.assertEquals(1, writeRoles.size());
        Assert.assertTrue(writeRoles.contains("WRITER"));
        Assert.assertFalse(root.canAccess(anonymous, READ));
        Assert.assertFalse(root.canAccess(anonymous, WRITE));
        Assert.assertFalse(root.canAccess(roUser, READ));
        Assert.assertFalse(root.canAccess(roUser, WRITE));
        Assert.assertTrue(root.canAccess(rwUser, READ));
        Assert.assertTrue(root.canAccess(rwUser, WRITE));
    }

    @Test
    public void testPublicRead() throws Exception {
        SecureTreeNode root = buildTree("publicRead.properties");
        Assert.assertEquals(0, root.children.size());
        Assert.assertEquals(EVERYBODY, root.getAuthorizedRoles(READ));
        final Set<String> writeRoles = root.getAuthorizedRoles(WRITE);
        Assert.assertEquals(1, writeRoles.size());
        Assert.assertTrue(writeRoles.contains("WRITER"));
        Assert.assertTrue(root.canAccess(anonymous, READ));
        Assert.assertFalse(root.canAccess(anonymous, WRITE));
        Assert.assertTrue(root.canAccess(roUser, READ));
        Assert.assertFalse(root.canAccess(roUser, WRITE));
        Assert.assertTrue(root.canAccess(rwUser, READ));
        Assert.assertTrue(root.canAccess(rwUser, WRITE));
    }

    @Test
    public void testComplex() throws Exception {
        SecureTreeNode root = buildTree("complex.properties");
        // first off, evaluate tree structure
        Assert.assertEquals(2, root.children.size());
        SecureTreeNode topp = root.getChild("topp");
        Assert.assertNotNull(topp);
        Assert.assertEquals(3, topp.children.size());
        SecureTreeNode states = topp.getChild("states");
        SecureTreeNode landmarks = topp.getChild("landmarks");
        SecureTreeNode bases = topp.getChild("bases");
        Assert.assertNotNull(states);
        Assert.assertNotNull(landmarks);
        Assert.assertNotNull(bases);
        // perform some checks with anonymous access
        Assert.assertFalse(root.canAccess(anonymous, READ));
        Assert.assertFalse(root.canAccess(anonymous, WRITE));
        Assert.assertTrue(topp.canAccess(anonymous, READ));
        Assert.assertFalse(states.canAccess(anonymous, READ));
        Assert.assertTrue(landmarks.canAccess(anonymous, READ));
        Assert.assertFalse(landmarks.canAccess(anonymous, WRITE));
        Assert.assertFalse(bases.canAccess(anonymous, READ));
        // perform some checks with read only access
        Assert.assertTrue(root.canAccess(roUser, READ));
        Assert.assertFalse(root.canAccess(roUser, WRITE));
        Assert.assertTrue(topp.canAccess(roUser, READ));
        Assert.assertTrue(states.canAccess(roUser, READ));
        Assert.assertTrue(landmarks.canAccess(roUser, READ));
        Assert.assertFalse(landmarks.canAccess(roUser, WRITE));
        Assert.assertFalse(bases.canAccess(roUser, READ));
        // perform some checks with read write access
        Assert.assertTrue(root.canAccess(rwUser, READ));
        Assert.assertFalse(root.canAccess(rwUser, WRITE));
        Assert.assertTrue(topp.canAccess(rwUser, READ));
        Assert.assertTrue(states.canAccess(rwUser, WRITE));
        Assert.assertTrue(landmarks.canAccess(rwUser, READ));
        Assert.assertTrue(landmarks.canAccess(rwUser, WRITE));
        Assert.assertFalse(bases.canAccess(rwUser, READ));
        // military access... just access the one layer, for the rest he's like anonymous
        Assert.assertFalse(root.canAccess(milUser, READ));
        Assert.assertFalse(root.canAccess(milUser, WRITE));
        Assert.assertTrue(topp.canAccess(milUser, READ));
        Assert.assertFalse(states.canAccess(milUser, WRITE));
        Assert.assertTrue(landmarks.canAccess(milUser, READ));
        Assert.assertFalse(landmarks.canAccess(milUser, WRITE));
        Assert.assertTrue(bases.canAccess(milUser, READ));
        Assert.assertTrue(bases.canAccess(milUser, WRITE));
    }
}

