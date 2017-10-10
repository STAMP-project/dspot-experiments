

package org.traccar.database;


public class AmplGroupTreeTest {
    private static org.traccar.model.Group createGroup(long id, java.lang.String name, long parent) {
        org.traccar.model.Group group = new org.traccar.model.Group();
        group.setId(id);
        group.setName(name);
        group.setGroupId(parent);
        return group;
    }

    private static org.traccar.model.Device createDevice(long id, java.lang.String name, long parent) {
        org.traccar.model.Device device = new org.traccar.model.Device();
        device.setId(id);
        device.setName(name);
        device.setGroupId(parent);
        return device;
    }

    @org.junit.Test
    public void testGetDescendants() {
        java.util.Collection<org.traccar.model.Group> groups = new java.util.ArrayList<>();
        groups.add(org.traccar.database.AmplGroupTreeTest.createGroup(1, "First", 0));
        groups.add(org.traccar.database.AmplGroupTreeTest.createGroup(2, "Second", 1));
        groups.add(org.traccar.database.AmplGroupTreeTest.createGroup(3, "Third", 2));
        groups.add(org.traccar.database.AmplGroupTreeTest.createGroup(4, "Fourth", 2));
        groups.add(org.traccar.database.AmplGroupTreeTest.createGroup(5, "Fifth", 4));
        java.util.Collection<org.traccar.model.Device> devices = new java.util.ArrayList<>();
        devices.add(org.traccar.database.AmplGroupTreeTest.createDevice(1, "One", 3));
        devices.add(org.traccar.database.AmplGroupTreeTest.createDevice(2, "Two", 5));
        devices.add(org.traccar.database.AmplGroupTreeTest.createDevice(3, "One", 5));
        org.traccar.database.GroupTree groupTree = new org.traccar.database.GroupTree(groups, devices);
        org.junit.Assert.assertEquals(4, groupTree.getGroups(1).size());
        org.junit.Assert.assertEquals(3, groupTree.getGroups(2).size());
        org.junit.Assert.assertEquals(0, groupTree.getGroups(3).size());
        org.junit.Assert.assertEquals(1, groupTree.getGroups(4).size());
        org.junit.Assert.assertEquals(3, groupTree.getDevices(1).size());
        org.junit.Assert.assertEquals(1, groupTree.getDevices(3).size());
        org.junit.Assert.assertEquals(2, groupTree.getDevices(4).size());
    }

    /* amplification of org.traccar.database.GroupTreeTest#testGetDescendants */
    @org.junit.Test(timeout = 1000)
    public void testGetDescendants_cf38() {
        java.util.Collection<org.traccar.model.Group> groups = new java.util.ArrayList<>();
        groups.add(org.traccar.database.AmplGroupTreeTest.createGroup(1, "First", 0));
        groups.add(org.traccar.database.AmplGroupTreeTest.createGroup(2, "Second", 1));
        groups.add(org.traccar.database.AmplGroupTreeTest.createGroup(3, "Third", 2));
        groups.add(org.traccar.database.AmplGroupTreeTest.createGroup(4, "Fourth", 2));
        groups.add(org.traccar.database.AmplGroupTreeTest.createGroup(5, "Fifth", 4));
        java.util.Collection<org.traccar.model.Device> devices = new java.util.ArrayList<>();
        devices.add(org.traccar.database.AmplGroupTreeTest.createDevice(1, "One", 3));
        devices.add(org.traccar.database.AmplGroupTreeTest.createDevice(2, "Two", 5));
        devices.add(org.traccar.database.AmplGroupTreeTest.createDevice(3, "One", 5));
        org.traccar.database.GroupTree groupTree = new org.traccar.database.GroupTree(groups, devices);
        org.junit.Assert.assertEquals(4, groupTree.getGroups(1).size());
        org.junit.Assert.assertEquals(3, groupTree.getGroups(2).size());
        org.junit.Assert.assertEquals(0, groupTree.getGroups(3).size());
        org.junit.Assert.assertEquals(1, groupTree.getGroups(4).size());
        org.junit.Assert.assertEquals(3, groupTree.getDevices(1).size());
        org.junit.Assert.assertEquals(1, groupTree.getDevices(3).size());
        // StatementAdderOnAssert create random local variable
        long vc_2 = -7645544759533712594L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2, -7645544759533712594L);
        // AssertGenerator replace invocation
        java.util.Collection<org.traccar.model.Device> o_testGetDescendants_cf38__43 = // StatementAdderMethod cloned existing statement
groupTree.getDevices(vc_2);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1872880415 = new java.util.ArrayList<Object>();
	junit.framework.Assert.assertEquals(collection_1872880415, o_testGetDescendants_cf38__43);;
        org.junit.Assert.assertEquals(2, groupTree.getDevices(4).size());
    }

    /* amplification of org.traccar.database.GroupTreeTest#testGetDescendants */
    @org.junit.Test(timeout = 1000)
    public void testGetDescendants_add5_cf251_add1406() {
        java.util.Collection<org.traccar.model.Group> groups = new java.util.ArrayList<>();
        groups.add(org.traccar.database.AmplGroupTreeTest.createGroup(1, "First", 0));
        groups.add(org.traccar.database.AmplGroupTreeTest.createGroup(2, "Second", 1));
        groups.add(org.traccar.database.AmplGroupTreeTest.createGroup(3, "Third", 2));
        groups.add(org.traccar.database.AmplGroupTreeTest.createGroup(4, "Fourth", 2));
        // AssertGenerator replace invocation
        boolean o_testGetDescendants_add5__11 = // MethodCallAdder
groups.add(org.traccar.database.GroupTreeTest.createGroup(5, "Fifth", 4));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testGetDescendants_add5__11);
        groups.add(org.traccar.database.AmplGroupTreeTest.createGroup(5, "Fifth", 4));
        java.util.Collection<org.traccar.model.Device> devices = new java.util.ArrayList<>();
        devices.add(org.traccar.database.AmplGroupTreeTest.createDevice(1, "One", 3));
        // AssertGenerator replace invocation
        boolean o_testGetDescendants_add5_cf251_add1406__21 = // MethodCallAdder
devices.add(org.traccar.database.GroupTreeTest.createDevice(2, "Two", 5));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testGetDescendants_add5_cf251_add1406__21);
        devices.add(org.traccar.database.AmplGroupTreeTest.createDevice(2, "Two", 5));
        devices.add(org.traccar.database.AmplGroupTreeTest.createDevice(3, "One", 5));
        org.traccar.database.GroupTree groupTree = new org.traccar.database.GroupTree(groups, devices);
        org.junit.Assert.assertEquals(4, groupTree.getGroups(1).size());
        org.junit.Assert.assertEquals(3, groupTree.getGroups(2).size());
        org.junit.Assert.assertEquals(0, groupTree.getGroups(3).size());
        org.junit.Assert.assertEquals(1, groupTree.getGroups(4).size());
        org.junit.Assert.assertEquals(3, groupTree.getDevices(1).size());
        org.junit.Assert.assertEquals(1, groupTree.getDevices(3).size());
        // StatementAdderOnAssert create random local variable
        long vc_35 = 26929798315112628L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_35, 26929798315112628L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_35, 26929798315112628L);
        // AssertGenerator replace invocation
        java.util.Collection<org.traccar.model.Group> o_testGetDescendants_add5_cf251__47 = // StatementAdderMethod cloned existing statement
groupTree.getGroups(vc_35);
        // AssertGenerator add assertion
        java.util.ArrayList collection_888802974 = new java.util.ArrayList<Object>();
	junit.framework.Assert.assertEquals(collection_888802974, o_testGetDescendants_add5_cf251__47);;
        org.junit.Assert.assertEquals(2, groupTree.getDevices(4).size());
    }
}

