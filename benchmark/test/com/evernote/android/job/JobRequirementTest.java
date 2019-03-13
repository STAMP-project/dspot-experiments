package com.evernote.android.job;


import ConnectivityManager.TYPE_MOBILE;
import ConnectivityManager.TYPE_WIFI;
import JobRequest.NetworkType.ANY;
import JobRequest.NetworkType.CONNECTED;
import JobRequest.NetworkType.METERED;
import JobRequest.NetworkType.NOT_ROAMING;
import JobRequest.NetworkType.UNMETERED;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;


/**
 *
 *
 * @author rwondratschek
 */
@FixMethodOrder(MethodSorters.JVM)
@SuppressWarnings("deprecation")
public class JobRequirementTest {
    @Test
    public void verifyRequirementNetworkMeteredOnRoaming() {
        Job job = createMockedJob();
        setupNetworkRequirement(job, METERED, true, TYPE_MOBILE, true);
        assertThat(job.isRequirementNetworkTypeMet()).isTrue();
    }

    @Test
    public void verifyRequirementNetworkMeteredOnMobile() {
        Job job = createMockedJob();
        setupNetworkRequirement(job, METERED, true, TYPE_MOBILE, false);
        assertThat(job.isRequirementNetworkTypeMet()).isTrue();
    }

    @Test
    public void verifyRequirementNetworkMeteredOnWifi() {
        Job job = createMockedJob();
        setupNetworkRequirement(job, METERED, true, TYPE_WIFI, false);
        assertThat(job.isRequirementNetworkTypeMet()).isFalse();
    }

    @Test
    public void verifyRequirementNetworkMeteredNoConnection() {
        Job job = createMockedJob();
        setupNetworkRequirement(job, METERED, false, TYPE_WIFI, false);
        assertThat(job.isRequirementNetworkTypeMet()).isFalse();
    }

    @Test
    public void verifyRequirementNetworkNotRoamingOnRoaming() {
        Job job = createMockedJob();
        setupNetworkRequirement(job, NOT_ROAMING, true, TYPE_MOBILE, true);
        assertThat(job.isRequirementNetworkTypeMet()).isFalse();
    }

    @Test
    public void verifyRequirementNetworkNotRoamingOnMobile() {
        Job job = createMockedJob();
        setupNetworkRequirement(job, NOT_ROAMING, true, TYPE_MOBILE, false);
        assertThat(job.isRequirementNetworkTypeMet()).isTrue();
    }

    @Test
    public void verifyRequirementNetworkNotRoamingOnWifi() {
        Job job = createMockedJob();
        setupNetworkRequirement(job, NOT_ROAMING, true, TYPE_WIFI, true);
        assertThat(job.isRequirementNetworkTypeMet()).isTrue();
    }

    @Test
    public void verifyRequirementNetworkNotRoamingNoConnection() {
        Job job = createMockedJob();
        setupNetworkRequirement(job, NOT_ROAMING, false, TYPE_WIFI, true);
        assertThat(job.isRequirementNetworkTypeMet()).isFalse();
    }

    @Test
    public void verifyRequirementNetworkUnmeteredOnRoaming() {
        Job job = createMockedJob();
        setupNetworkRequirement(job, UNMETERED, true, TYPE_MOBILE, true);
        assertThat(job.isRequirementNetworkTypeMet()).isFalse();
    }

    @Test
    public void verifyRequirementNetworkUnmeteredOnMobile() {
        Job job = createMockedJob();
        setupNetworkRequirement(job, UNMETERED, true, TYPE_MOBILE, false);
        assertThat(job.isRequirementNetworkTypeMet()).isFalse();
    }

    @Test
    public void verifyRequirementNetworkUnmeteredOnWifi() {
        Job job = createMockedJob();
        setupNetworkRequirement(job, UNMETERED, true, TYPE_WIFI, true);
        assertThat(job.isRequirementNetworkTypeMet()).isTrue();
    }

    @Test
    public void verifyRequirementNetworkUnmeteredNoConnection() {
        Job job = createMockedJob();
        setupNetworkRequirement(job, UNMETERED, false, TYPE_WIFI, true);
        assertThat(job.isRequirementNetworkTypeMet()).isFalse();
    }

    @Test
    public void verifyRequirementNetworkConnectedOnRoaming() {
        Job job = createMockedJob();
        setupNetworkRequirement(job, CONNECTED, true, TYPE_MOBILE, true);
        assertThat(job.isRequirementNetworkTypeMet()).isTrue();
    }

    @Test
    public void verifyRequirementNetworkConnectedOnMobile() {
        Job job = createMockedJob();
        setupNetworkRequirement(job, CONNECTED, true, TYPE_MOBILE, false);
        assertThat(job.isRequirementNetworkTypeMet()).isTrue();
    }

    @Test
    public void verifyRequirementNetworkConnectedWifi() {
        Job job = createMockedJob();
        setupNetworkRequirement(job, CONNECTED, true, TYPE_WIFI, true);
        assertThat(job.isRequirementNetworkTypeMet()).isTrue();
    }

    @Test
    public void verifyRequirementNetworkConnectedNoConnection() {
        Job job = createMockedJob();
        setupNetworkRequirement(job, CONNECTED, false, TYPE_WIFI, true);
        assertThat(job.isRequirementNetworkTypeMet()).isFalse();
    }

    @Test
    public void verifyRequirementNetworkAnyOnRoaming() {
        Job job = createMockedJob();
        setupNetworkRequirement(job, ANY, true, TYPE_MOBILE, true);
        assertThat(job.isRequirementNetworkTypeMet()).isTrue();
    }

    @Test
    public void verifyRequirementNetworkAnyOnMobile() {
        Job job = createMockedJob();
        setupNetworkRequirement(job, ANY, true, TYPE_MOBILE, false);
        assertThat(job.isRequirementNetworkTypeMet()).isTrue();
    }

    @Test
    public void verifyRequirementNetworkAnyWifi() {
        Job job = createMockedJob();
        setupNetworkRequirement(job, ANY, true, TYPE_WIFI, true);
        assertThat(job.isRequirementNetworkTypeMet()).isTrue();
    }

    @Test
    public void verifyRequirementNetworkAnyNoConnection() {
        Job job = createMockedJob();
        setupNetworkRequirement(job, ANY, false, TYPE_WIFI, true);
        assertThat(job.isRequirementNetworkTypeMet()).isTrue();
    }

    @Test
    public void verifyRequirementDeviceIdleIsIdle() {
        Job job = createMockedJob();
        setupDeviceIdle(job, true, true);
        assertThat(job.isRequirementDeviceIdleMet()).isTrue();
    }

    @Test
    public void verifyRequirementDeviceIdleIsNotIdle() {
        Job job = createMockedJob();
        setupDeviceIdle(job, true, false);
        assertThat(job.isRequirementDeviceIdleMet()).isFalse();
    }

    @Test
    public void verifyRequirementDeviceNoRequirement() {
        Job job = createMockedJob();
        setupDeviceIdle(job, false, false);
        assertThat(job.isRequirementDeviceIdleMet()).isTrue();
        setupDeviceIdle(job, false, true);
        assertThat(job.isRequirementDeviceIdleMet()).isTrue();
    }

    @Test
    public void verifyMeetsRequirementsAllMet() {
        Job job = createMockedJob();
        setupDeviceIdle(job, true, true);
        setupNetworkRequirement(job, CONNECTED, true, TYPE_WIFI, false);
        assertThat(job.isRequirementDeviceIdleMet()).isTrue();
        assertThat(job.isRequirementNetworkTypeMet()).isTrue();
        assertThat(job.meetsRequirements()).isTrue();
    }

    @Test
    public void verifyMeetsRequirementsOnlyIdle() {
        Job job = createMockedJob();
        setupDeviceIdle(job, true, true);
        setupNetworkRequirement(job, CONNECTED, false, TYPE_WIFI, false);
        assertThat(job.isRequirementDeviceIdleMet()).isTrue();
        assertThat(job.isRequirementNetworkTypeMet()).isFalse();
        assertThat(job.meetsRequirements()).isFalse();
    }

    @Test
    public void verifyMeetsRequirementsOnlyNetwork() {
        Job job = createMockedJob();
        setupDeviceIdle(job, true, false);
        setupNetworkRequirement(job, CONNECTED, true, TYPE_WIFI, false);
        assertThat(job.isRequirementDeviceIdleMet()).isFalse();
        assertThat(job.isRequirementNetworkTypeMet()).isTrue();
        assertThat(job.meetsRequirements()).isFalse();
    }

    @Test
    public void verifyMeetsRequirementsEnforcedIgnored() {
        Job job = createMockedJob();
        Mockito.when(job.getParams().getRequest().requirementsEnforced()).thenReturn(false);
        setupDeviceIdle(job, true, false);
        setupNetworkRequirement(job, CONNECTED, false, TYPE_WIFI, false);
        assertThat(job.isRequirementDeviceIdleMet()).isFalse();
        assertThat(job.isRequirementNetworkTypeMet()).isFalse();
        assertThat(job.meetsRequirements()).isFalse();
        assertThat(job.meetsRequirements(true)).isTrue();
    }
}

