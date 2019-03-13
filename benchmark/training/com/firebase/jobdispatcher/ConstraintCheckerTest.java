/**
 * Copyright 2018 Google, Inc.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
/**
 *
 */
/**
 * //////////////////////////////////////////////////////////////////////////////
 */
package com.firebase.jobdispatcher;


import Constraint.DEVICE_IDLE;
import Constraint.ON_ANY_NETWORK;
import Constraint.ON_UNMETERED_NETWORK;
import JobInvocation.Builder;
import android.content.Context;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowConnectivityManager;
import org.robolectric.shadows.ShadowNetworkInfo;

import static Constraint.DEVICE_CHARGING;
import static Constraint.DEVICE_IDLE;
import static Constraint.ON_ANY_NETWORK;


/**
 * Tests for the {@link com.firebase.jobdispatcher.ConstraintChecker} class.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public final class ConstraintCheckerTest {
    private static final String JOB_TAG = "JobTag";

    private static final String JOB_SERVICE = "JobService";

    private Context context;

    private Builder jobBuilder;

    private ConstraintChecker constraintChecker;

    private ShadowConnectivityManager shadowConnectivityManager;

    private ShadowNetworkInfo shadowNetworkInfo;

    @Test
    public void testAreConstraintsSatisfied_anyNetworkRequired_satisfied() {
        JobInvocation job = jobBuilder.setConstraints(Constraint.uncompact(ON_ANY_NETWORK)).build();
        /* isConnected= */
        shadowNetworkInfo.setConnectionStatus(true);
        assertThat(constraintChecker.areConstraintsSatisfied(job)).isTrue();
    }

    @Test
    public void testAreConstraintsSatisfied_anyNetworkRequired_unsatisfied_notConnected() {
        JobInvocation job = jobBuilder.setConstraints(Constraint.uncompact(ON_ANY_NETWORK)).build();
        /* isConnected= */
        shadowNetworkInfo.setConnectionStatus(false);
        assertThat(constraintChecker.areConstraintsSatisfied(job)).isFalse();
    }

    @Test
    public void testAreConstraintsSatisfied_anyNetworkRequired_unsatisfied_nullNetworkInfo() {
        JobInvocation job = jobBuilder.setConstraints(Constraint.uncompact(ON_ANY_NETWORK)).build();
        shadowConnectivityManager.setActiveNetworkInfo(null);
        assertThat(constraintChecker.areConstraintsSatisfied(job)).isFalse();
    }

    @Test
    public void testAreConstraintsSatisfied_unmeteredNetworkRequired_satisfied() {
        JobInvocation job = jobBuilder.setConstraints(Constraint.uncompact(ON_UNMETERED_NETWORK)).build();
        /* isConnected= */
        shadowNetworkInfo.setConnectionStatus(true);
        setNetworkMetered(false);
        assertThat(constraintChecker.areConstraintsSatisfied(job)).isTrue();
    }

    @Test
    public void testAreConstraintsSatisfied_unmeteredNetworkRequired_unsatisfied_networkDisconnected() {
        JobInvocation job = jobBuilder.setConstraints(Constraint.uncompact(ON_UNMETERED_NETWORK)).build();
        /* isConnected= */
        shadowNetworkInfo.setConnectionStatus(false);
        setNetworkMetered(false);
        assertThat(constraintChecker.areConstraintsSatisfied(job)).isFalse();
    }

    @Test
    public void testAreConstraintsSatisfied_unmeteredNetworkRequired_unsatisfied_networkMetered() {
        JobInvocation job = jobBuilder.setConstraints(Constraint.uncompact(ON_UNMETERED_NETWORK)).build();
        /* isConnected= */
        shadowNetworkInfo.setConnectionStatus(true);
        setNetworkMetered(true);
        assertThat(constraintChecker.areConstraintsSatisfied(job)).isFalse();
    }

    @Test
    public void testAreConstraintsSatisfied_nonNetworkConstraint() {
        JobInvocation job = jobBuilder.setConstraints(Constraint.uncompact(DEVICE_IDLE)).build();
        assertThat(constraintChecker.areConstraintsSatisfied(job)).isTrue();
    }

    @Test
    public void testAreConstraintsSatisfied_nonNetworkConstraints() {
        JobInvocation job = jobBuilder.setConstraints(Constraint.uncompact(((DEVICE_IDLE) | (DEVICE_CHARGING)))).build();
        assertThat(constraintChecker.areConstraintsSatisfied(job)).isTrue();
    }

    @Test
    public void testAreConstraintsSatisfied_anyNetworkRequired_satisfied_includesNonNetworkConstraints() {
        JobInvocation job = jobBuilder.setConstraints(Constraint.uncompact((((DEVICE_IDLE) | (DEVICE_CHARGING)) | (ON_ANY_NETWORK)))).build();
        /* isConnected= */
        shadowNetworkInfo.setConnectionStatus(true);
        assertThat(constraintChecker.areConstraintsSatisfied(job)).isTrue();
    }
}

