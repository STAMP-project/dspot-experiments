/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.cluster.integration;


import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Integration tests for JMS that tests that GeoServer configurations events and GeoServer catalog
 * events are correctly propagated and handled.
 */
public final class IntegrationTest {
    // instantiate some GeoServer instances
    private static final GeoServerInstance INSTANCE_A = new GeoServerInstance("INSTANCE-A");

    private static final GeoServerInstance INSTANCE_B = new GeoServerInstance("INSTANCE-B");

    private static final GeoServerInstance INSTANCE_C = new GeoServerInstance("INSTANCE-C");

    private static final GeoServerInstance INSTANCE_D = new GeoServerInstance("INSTANCE-D");

    private static final GeoServerInstance[] INSTANCES = new GeoServerInstance[]{ IntegrationTest.INSTANCE_A, IntegrationTest.INSTANCE_B, IntegrationTest.INSTANCE_C, IntegrationTest.INSTANCE_D };

    @Test
    public void testConfigurationMastersSlavesApplyToMaster() throws Exception {
        // assert instances are equal
        IntegrationTestsUtils.checkNoDifferences(IntegrationTest.INSTANCES);
        // use instance A for control
        IntegrationTest.INSTANCE_A.disableJmsMaster();
        IntegrationTest.INSTANCE_A.disableJmsSlave();
        // instance B will be a pure master and instances C and D pure slaves
        IntegrationTest.INSTANCE_B.disableJmsSlave();
        IntegrationTest.INSTANCE_C.disableJmsMaster();
        IntegrationTest.INSTANCE_D.disableJmsMaster();
        // apply add and modify configuration changes to master
        applyAddModifyConfigurationChanges(IntegrationTest.INSTANCE_B);
        // check instance C
        waitAndCheckEvents(IntegrationTest.INSTANCE_C, 9);
        List<InfoDiff> differences = IntegrationTestsUtils.differences(IntegrationTest.INSTANCE_B, IntegrationTest.INSTANCE_C);
        Assert.assertThat(differences.size(), CoreMatchers.is(0));
        // check instance D
        waitAndCheckEvents(IntegrationTest.INSTANCE_D, 9);
        differences = IntegrationTestsUtils.differences(IntegrationTest.INSTANCE_B, IntegrationTest.INSTANCE_D);
        Assert.assertThat(differences.size(), CoreMatchers.is(0));
        // check instance A
        waitAndCheckEvents(IntegrationTest.INSTANCE_A, 0);
        differences = IntegrationTestsUtils.differences(IntegrationTest.INSTANCE_B, IntegrationTest.INSTANCE_A);
        Assert.assertThat(differences.size(), CoreMatchers.is(4));
        // apply remove configuration changes to master
        applyDeleteConfigurationChanges(IntegrationTest.INSTANCE_B);
        // check instance C
        waitAndCheckEvents(IntegrationTest.INSTANCE_C, 4);
        differences = IntegrationTestsUtils.differences(IntegrationTest.INSTANCE_B, IntegrationTest.INSTANCE_C);
        Assert.assertThat(differences.size(), CoreMatchers.is(0));
        // check instance D
        waitAndCheckEvents(IntegrationTest.INSTANCE_D, 4);
        differences = IntegrationTestsUtils.differences(IntegrationTest.INSTANCE_B, IntegrationTest.INSTANCE_D);
        Assert.assertThat(differences.size(), CoreMatchers.is(0));
        // check instance A
        waitAndCheckEvents(IntegrationTest.INSTANCE_A, 0);
        differences = IntegrationTestsUtils.differences(IntegrationTest.INSTANCE_B, IntegrationTest.INSTANCE_A);
        Assert.assertThat(differences.size(), CoreMatchers.is(2));
    }

    @Test
    public void testConfigurationMastersSlavesApplyToSlave() throws Exception {
        // assert instances are equal
        IntegrationTestsUtils.checkNoDifferences(IntegrationTest.INSTANCES);
        // use instance A for control
        IntegrationTest.INSTANCE_A.disableJmsMaster();
        IntegrationTest.INSTANCE_A.disableJmsSlave();
        // instance B will be a pure master and instances C and D pure slaves
        IntegrationTest.INSTANCE_B.disableJmsSlave();
        IntegrationTest.INSTANCE_C.disableJmsMaster();
        IntegrationTest.INSTANCE_D.disableJmsMaster();
        // apply add and modify configuration changes to slave
        applyAddModifyConfigurationChanges(IntegrationTest.INSTANCE_C);
        // check instance A
        waitNoEvents(IntegrationTest.INSTANCE_A, 100);
        List<InfoDiff> differences = IntegrationTestsUtils.differences(IntegrationTest.INSTANCE_C, IntegrationTest.INSTANCE_A);
        Assert.assertThat(differences.size(), CoreMatchers.is(4));
        // check instance B
        waitNoEvents(IntegrationTest.INSTANCE_B, 100);
        differences = IntegrationTestsUtils.differences(IntegrationTest.INSTANCE_C, IntegrationTest.INSTANCE_B);
        Assert.assertThat(differences.size(), CoreMatchers.is(4));
        // check instance D
        waitNoEvents(IntegrationTest.INSTANCE_D, 100);
        differences = IntegrationTestsUtils.differences(IntegrationTest.INSTANCE_C, IntegrationTest.INSTANCE_D);
        Assert.assertThat(differences.size(), CoreMatchers.is(4));
        // apply remove configuration changes to slave
        applyDeleteConfigurationChanges(IntegrationTest.INSTANCE_C);
        // check instance A
        waitNoEvents(IntegrationTest.INSTANCE_A, 100);
        differences = IntegrationTestsUtils.differences(IntegrationTest.INSTANCE_C, IntegrationTest.INSTANCE_A);
        Assert.assertThat(differences.size(), CoreMatchers.is(2));
        // check instance B
        waitNoEvents(IntegrationTest.INSTANCE_C, 100);
        differences = IntegrationTestsUtils.differences(IntegrationTest.INSTANCE_C, IntegrationTest.INSTANCE_B);
        Assert.assertThat(differences.size(), CoreMatchers.is(2));
        // check instance D
        waitNoEvents(IntegrationTest.INSTANCE_D, 100);
        differences = IntegrationTestsUtils.differences(IntegrationTest.INSTANCE_C, IntegrationTest.INSTANCE_D);
        Assert.assertThat(differences.size(), CoreMatchers.is(2));
    }

    @Test
    public void testCatalogMastersSlavesApplyToMaster() throws Exception {
        // assert instances are equal
        IntegrationTestsUtils.checkNoDifferences(IntegrationTest.INSTANCES);
        // use instance A for control
        IntegrationTest.INSTANCE_A.disableJmsMaster();
        IntegrationTest.INSTANCE_A.disableJmsSlave();
        // instance B will be a pure master and instances C and D pure slaves
        IntegrationTest.INSTANCE_B.disableJmsSlave();
        IntegrationTest.INSTANCE_C.disableJmsMaster();
        IntegrationTest.INSTANCE_D.disableJmsMaster();
        // apply catalog add changes to master
        applyAddCatalogChanges(IntegrationTest.INSTANCE_B);
        // check instance C
        waitAndCheckEvents(IntegrationTest.INSTANCE_C, 25);
        List<InfoDiff> differences = IntegrationTestsUtils.differences(IntegrationTest.INSTANCE_B, IntegrationTest.INSTANCE_C);
        Assert.assertThat(differences.size(), CoreMatchers.is(0));
        // check instance D
        waitAndCheckEvents(IntegrationTest.INSTANCE_D, 25);
        differences = IntegrationTestsUtils.differences(IntegrationTest.INSTANCE_B, IntegrationTest.INSTANCE_D);
        Assert.assertThat(differences.size(), CoreMatchers.is(0));
        // check instance A
        waitAndCheckEvents(IntegrationTest.INSTANCE_A, 0);
        differences = IntegrationTestsUtils.differences(IntegrationTest.INSTANCE_B, IntegrationTest.INSTANCE_A);
        Assert.assertThat(differences.size(), CoreMatchers.is(11));
        // apply modify changes to the catalog
        applyModifyCatalogChanges(IntegrationTest.INSTANCE_B);
        // check instance C
        waitAndCheckEvents(IntegrationTest.INSTANCE_C, 20);
        differences = IntegrationTestsUtils.differences(IntegrationTest.INSTANCE_B, IntegrationTest.INSTANCE_C);
        Assert.assertThat(differences.size(), CoreMatchers.is(0));
        // check instance D
        waitAndCheckEvents(IntegrationTest.INSTANCE_D, 20);
        differences = IntegrationTestsUtils.differences(IntegrationTest.INSTANCE_B, IntegrationTest.INSTANCE_D);
        Assert.assertThat(differences.size(), CoreMatchers.is(0));
        // check instance A
        waitAndCheckEvents(IntegrationTest.INSTANCE_A, 0);
        differences = IntegrationTestsUtils.differences(IntegrationTest.INSTANCE_B, IntegrationTest.INSTANCE_A);
        Assert.assertThat(differences.size(), CoreMatchers.is(11));
        // apply catalog delete events
        applyDeleteCatalogChanges(IntegrationTest.INSTANCE_B);
        // check instance C
        waitAndCheckEvents(IntegrationTest.INSTANCE_C, 28);
        differences = IntegrationTestsUtils.differences(IntegrationTest.INSTANCE_B, IntegrationTest.INSTANCE_C);
        Assert.assertThat(differences.size(), CoreMatchers.is(0));
        // check instance D
        waitAndCheckEvents(IntegrationTest.INSTANCE_D, 28);
        differences = IntegrationTestsUtils.differences(IntegrationTest.INSTANCE_B, IntegrationTest.INSTANCE_D);
        Assert.assertThat(differences.size(), CoreMatchers.is(0));
        // check instance A
        waitAndCheckEvents(IntegrationTest.INSTANCE_A, 0);
        differences = IntegrationTestsUtils.differences(IntegrationTest.INSTANCE_B, IntegrationTest.INSTANCE_A);
        Assert.assertThat(differences.size(), CoreMatchers.is(0));
    }
}

