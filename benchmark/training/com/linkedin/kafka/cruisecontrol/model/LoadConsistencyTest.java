/**
 * Copyright 2017 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */
package com.linkedin.kafka.cruisecontrol.model;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Unit test for making sure that loads in the deterministically generated cluster / rack / broker / replica are
 * consistent with each other.
 */
@RunWith(Parameterized.class)
public class LoadConsistencyTest {
    private ClusterModel _clusterModel;

    private boolean _shouldPassSanityCheck;

    /**
     * Constructor of LoadConsistencyTest.
     */
    public LoadConsistencyTest(ClusterModel clusterModel, boolean shouldPassSanityCheck) {
        _clusterModel = clusterModel;
        _shouldPassSanityCheck = shouldPassSanityCheck;
    }

    @Test
    public void test() {
        if (_shouldPassSanityCheck) {
            _clusterModel.sanityCheck();
        } else {
            try {
                _clusterModel.sanityCheck();
                Assert.fail("Should throw IllegalArgumentException");
            } catch (IllegalArgumentException mie) {
                // Let it go.
            }
        }
    }
}

