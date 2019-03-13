/**
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer2.upstream;


import RuntimeEnvironment.application;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;


/**
 * Unit test for {@link DefaultBandwidthMeter}.
 */
@RunWith(RobolectricTestRunner.class)
public final class DefaultBandwidthMeterTest {
    private static final String FAST_COUNTRY_ISO = "EE";

    private static final String SLOW_COUNTRY_ISO = "PG";

    private TelephonyManager telephonyManager;

    private ConnectivityManager connectivityManager;

    private NetworkInfo networkInfoOffline;

    private NetworkInfo networkInfoWifi;

    private NetworkInfo networkInfo2g;

    private NetworkInfo networkInfo3g;

    private NetworkInfo networkInfo4g;

    private NetworkInfo networkInfoEthernet;

    @Test
    public void defaultInitialBitrateEstimate_forWifi_isGreaterThanEstimateFor2G() {
        setActiveNetworkInfo(networkInfoWifi);
        DefaultBandwidthMeter bandwidthMeterWifi = build();
        long initialEstimateWifi = bandwidthMeterWifi.getBitrateEstimate();
        setActiveNetworkInfo(networkInfo2g);
        DefaultBandwidthMeter bandwidthMeter2g = build();
        long initialEstimate2g = bandwidthMeter2g.getBitrateEstimate();
        assertThat(initialEstimateWifi).isGreaterThan(initialEstimate2g);
    }

    @Test
    public void defaultInitialBitrateEstimate_forWifi_isGreaterThanEstimateFor3G() {
        setActiveNetworkInfo(networkInfoWifi);
        DefaultBandwidthMeter bandwidthMeterWifi = build();
        long initialEstimateWifi = bandwidthMeterWifi.getBitrateEstimate();
        setActiveNetworkInfo(networkInfo3g);
        DefaultBandwidthMeter bandwidthMeter3g = build();
        long initialEstimate3g = bandwidthMeter3g.getBitrateEstimate();
        assertThat(initialEstimateWifi).isGreaterThan(initialEstimate3g);
    }

    @Test
    public void defaultInitialBitrateEstimate_forEthernet_isGreaterThanEstimateFor2G() {
        setActiveNetworkInfo(networkInfoEthernet);
        DefaultBandwidthMeter bandwidthMeterEthernet = build();
        long initialEstimateEthernet = bandwidthMeterEthernet.getBitrateEstimate();
        setActiveNetworkInfo(networkInfo2g);
        DefaultBandwidthMeter bandwidthMeter2g = build();
        long initialEstimate2g = bandwidthMeter2g.getBitrateEstimate();
        assertThat(initialEstimateEthernet).isGreaterThan(initialEstimate2g);
    }

    @Test
    public void defaultInitialBitrateEstimate_forEthernet_isGreaterThanEstimateFor3G() {
        setActiveNetworkInfo(networkInfoEthernet);
        DefaultBandwidthMeter bandwidthMeterEthernet = build();
        long initialEstimateEthernet = bandwidthMeterEthernet.getBitrateEstimate();
        setActiveNetworkInfo(networkInfo3g);
        DefaultBandwidthMeter bandwidthMeter3g = build();
        long initialEstimate3g = bandwidthMeter3g.getBitrateEstimate();
        assertThat(initialEstimateEthernet).isGreaterThan(initialEstimate3g);
    }

    @Test
    public void defaultInitialBitrateEstimate_for4G_isGreaterThanEstimateFor2G() {
        setActiveNetworkInfo(networkInfo4g);
        DefaultBandwidthMeter bandwidthMeter4g = build();
        long initialEstimate4g = bandwidthMeter4g.getBitrateEstimate();
        setActiveNetworkInfo(networkInfo2g);
        DefaultBandwidthMeter bandwidthMeter2g = build();
        long initialEstimate2g = bandwidthMeter2g.getBitrateEstimate();
        assertThat(initialEstimate4g).isGreaterThan(initialEstimate2g);
    }

    @Test
    public void defaultInitialBitrateEstimate_for4G_isGreaterThanEstimateFor3G() {
        setActiveNetworkInfo(networkInfo4g);
        DefaultBandwidthMeter bandwidthMeter4g = build();
        long initialEstimate4g = bandwidthMeter4g.getBitrateEstimate();
        setActiveNetworkInfo(networkInfo3g);
        DefaultBandwidthMeter bandwidthMeter3g = build();
        long initialEstimate3g = bandwidthMeter3g.getBitrateEstimate();
        assertThat(initialEstimate4g).isGreaterThan(initialEstimate3g);
    }

    @Test
    public void defaultInitialBitrateEstimate_for3G_isGreaterThanEstimateFor2G() {
        setActiveNetworkInfo(networkInfo3g);
        DefaultBandwidthMeter bandwidthMeter3g = build();
        long initialEstimate3g = bandwidthMeter3g.getBitrateEstimate();
        setActiveNetworkInfo(networkInfo2g);
        DefaultBandwidthMeter bandwidthMeter2g = build();
        long initialEstimate2g = bandwidthMeter2g.getBitrateEstimate();
        assertThat(initialEstimate3g).isGreaterThan(initialEstimate2g);
    }

    @Test
    public void defaultInitialBitrateEstimate_forOffline_isReasonable() {
        setActiveNetworkInfo(networkInfoOffline);
        DefaultBandwidthMeter bandwidthMeter = build();
        long initialEstimate = bandwidthMeter.getBitrateEstimate();
        assertThat(initialEstimate).isGreaterThan(100000L);
        assertThat(initialEstimate).isLessThan(50000000L);
    }

    @Test
    public void defaultInitialBitrateEstimate_forWifi_forFastCountry_isGreaterThanEstimateForSlowCountry() {
        setActiveNetworkInfo(networkInfoWifi);
        setNetworkCountryIso(DefaultBandwidthMeterTest.FAST_COUNTRY_ISO);
        DefaultBandwidthMeter bandwidthMeterFast = build();
        long initialEstimateFast = bandwidthMeterFast.getBitrateEstimate();
        setNetworkCountryIso(DefaultBandwidthMeterTest.SLOW_COUNTRY_ISO);
        DefaultBandwidthMeter bandwidthMeterSlow = build();
        long initialEstimateSlow = bandwidthMeterSlow.getBitrateEstimate();
        assertThat(initialEstimateFast).isGreaterThan(initialEstimateSlow);
    }

    @Test
    public void defaultInitialBitrateEstimate_forEthernet_forFastCountry_isGreaterThanEstimateForSlowCountry() {
        setActiveNetworkInfo(networkInfoEthernet);
        setNetworkCountryIso(DefaultBandwidthMeterTest.FAST_COUNTRY_ISO);
        DefaultBandwidthMeter bandwidthMeterFast = build();
        long initialEstimateFast = bandwidthMeterFast.getBitrateEstimate();
        setNetworkCountryIso(DefaultBandwidthMeterTest.SLOW_COUNTRY_ISO);
        DefaultBandwidthMeter bandwidthMeterSlow = build();
        long initialEstimateSlow = bandwidthMeterSlow.getBitrateEstimate();
        assertThat(initialEstimateFast).isGreaterThan(initialEstimateSlow);
    }

    @Test
    public void defaultInitialBitrateEstimate_for2G_forFastCountry_isGreaterThanEstimateForSlowCountry() {
        setActiveNetworkInfo(networkInfo2g);
        setNetworkCountryIso(DefaultBandwidthMeterTest.FAST_COUNTRY_ISO);
        DefaultBandwidthMeter bandwidthMeterFast = build();
        long initialEstimateFast = bandwidthMeterFast.getBitrateEstimate();
        setNetworkCountryIso(DefaultBandwidthMeterTest.SLOW_COUNTRY_ISO);
        DefaultBandwidthMeter bandwidthMeterSlow = build();
        long initialEstimateSlow = bandwidthMeterSlow.getBitrateEstimate();
        assertThat(initialEstimateFast).isGreaterThan(initialEstimateSlow);
    }

    @Test
    public void defaultInitialBitrateEstimate_for3G_forFastCountry_isGreaterThanEstimateForSlowCountry() {
        setActiveNetworkInfo(networkInfo3g);
        setNetworkCountryIso(DefaultBandwidthMeterTest.FAST_COUNTRY_ISO);
        DefaultBandwidthMeter bandwidthMeterFast = build();
        long initialEstimateFast = bandwidthMeterFast.getBitrateEstimate();
        setNetworkCountryIso(DefaultBandwidthMeterTest.SLOW_COUNTRY_ISO);
        DefaultBandwidthMeter bandwidthMeterSlow = build();
        long initialEstimateSlow = bandwidthMeterSlow.getBitrateEstimate();
        assertThat(initialEstimateFast).isGreaterThan(initialEstimateSlow);
    }

    @Test
    public void defaultInitialBitrateEstimate_for4g_forFastCountry_isGreaterThanEstimateForSlowCountry() {
        setActiveNetworkInfo(networkInfo4g);
        setNetworkCountryIso(DefaultBandwidthMeterTest.FAST_COUNTRY_ISO);
        DefaultBandwidthMeter bandwidthMeterFast = build();
        long initialEstimateFast = bandwidthMeterFast.getBitrateEstimate();
        setNetworkCountryIso(DefaultBandwidthMeterTest.SLOW_COUNTRY_ISO);
        DefaultBandwidthMeter bandwidthMeterSlow = build();
        long initialEstimateSlow = bandwidthMeterSlow.getBitrateEstimate();
        assertThat(initialEstimateFast).isGreaterThan(initialEstimateSlow);
    }

    @Test
    public void initialBitrateEstimateOverwrite_whileConnectedToNetwork_setsInitialEstimate() {
        setActiveNetworkInfo(networkInfoWifi);
        DefaultBandwidthMeter bandwidthMeter = build();
        long initialEstimate = bandwidthMeter.getBitrateEstimate();
        assertThat(initialEstimate).isEqualTo(123456789);
    }

    @Test
    public void initialBitrateEstimateOverwrite_whileOffline_setsInitialEstimate() {
        setActiveNetworkInfo(networkInfoOffline);
        DefaultBandwidthMeter bandwidthMeter = build();
        long initialEstimate = bandwidthMeter.getBitrateEstimate();
        assertThat(initialEstimate).isEqualTo(123456789);
    }

    @Test
    public void initialBitrateEstimateOverwrite_forWifi_whileConnectedToWifi_setsInitialEstimate() {
        setActiveNetworkInfo(networkInfoWifi);
        DefaultBandwidthMeter bandwidthMeter = build();
        long initialEstimate = bandwidthMeter.getBitrateEstimate();
        assertThat(initialEstimate).isEqualTo(123456789);
    }

    @Test
    public void initialBitrateEstimateOverwrite_forWifi_whileConnectedToOtherNetwork_doesNotSetInitialEstimate() {
        setActiveNetworkInfo(networkInfo2g);
        DefaultBandwidthMeter bandwidthMeter = build();
        long initialEstimate = bandwidthMeter.getBitrateEstimate();
        assertThat(initialEstimate).isNotEqualTo(123456789);
    }

    @Test
    public void initialBitrateEstimateOverwrite_forEthernet_whileConnectedToEthernet_setsInitialEstimate() {
        setActiveNetworkInfo(networkInfoEthernet);
        DefaultBandwidthMeter bandwidthMeter = build();
        long initialEstimate = bandwidthMeter.getBitrateEstimate();
        assertThat(initialEstimate).isEqualTo(123456789);
    }

    @Test
    public void initialBitrateEstimateOverwrite_forEthernet_whileConnectedToOtherNetwork_doesNotSetInitialEstimate() {
        setActiveNetworkInfo(networkInfo2g);
        DefaultBandwidthMeter bandwidthMeter = build();
        long initialEstimate = bandwidthMeter.getBitrateEstimate();
        assertThat(initialEstimate).isNotEqualTo(123456789);
    }

    @Test
    public void initialBitrateEstimateOverwrite_for2G_whileConnectedTo2G_setsInitialEstimate() {
        setActiveNetworkInfo(networkInfo2g);
        DefaultBandwidthMeter bandwidthMeter = build();
        long initialEstimate = bandwidthMeter.getBitrateEstimate();
        assertThat(initialEstimate).isEqualTo(123456789);
    }

    @Test
    public void initialBitrateEstimateOverwrite_for2G_whileConnectedToOtherNetwork_doesNotSetInitialEstimate() {
        setActiveNetworkInfo(networkInfoWifi);
        DefaultBandwidthMeter bandwidthMeter = build();
        long initialEstimate = bandwidthMeter.getBitrateEstimate();
        assertThat(initialEstimate).isNotEqualTo(123456789);
    }

    @Test
    public void initialBitrateEstimateOverwrite_for3G_whileConnectedTo3G_setsInitialEstimate() {
        setActiveNetworkInfo(networkInfo3g);
        DefaultBandwidthMeter bandwidthMeter = build();
        long initialEstimate = bandwidthMeter.getBitrateEstimate();
        assertThat(initialEstimate).isEqualTo(123456789);
    }

    @Test
    public void initialBitrateEstimateOverwrite_for3G_whileConnectedToOtherNetwork_doesNotSetInitialEstimate() {
        setActiveNetworkInfo(networkInfoWifi);
        DefaultBandwidthMeter bandwidthMeter = build();
        long initialEstimate = bandwidthMeter.getBitrateEstimate();
        assertThat(initialEstimate).isNotEqualTo(123456789);
    }

    @Test
    public void initialBitrateEstimateOverwrite_for4G_whileConnectedTo4G_setsInitialEstimate() {
        setActiveNetworkInfo(networkInfo4g);
        DefaultBandwidthMeter bandwidthMeter = build();
        long initialEstimate = bandwidthMeter.getBitrateEstimate();
        assertThat(initialEstimate).isEqualTo(123456789);
    }

    @Test
    public void initialBitrateEstimateOverwrite_for4G_whileConnectedToOtherNetwork_doesNotSetInitialEstimate() {
        setActiveNetworkInfo(networkInfoWifi);
        DefaultBandwidthMeter bandwidthMeter = build();
        long initialEstimate = bandwidthMeter.getBitrateEstimate();
        assertThat(initialEstimate).isNotEqualTo(123456789);
    }

    @Test
    public void initialBitrateEstimateOverwrite_forOffline_whileOffline_setsInitialEstimate() {
        setActiveNetworkInfo(networkInfoOffline);
        DefaultBandwidthMeter bandwidthMeter = build();
        long initialEstimate = bandwidthMeter.getBitrateEstimate();
        assertThat(initialEstimate).isEqualTo(123456789);
    }

    @Test
    public void initialBitrateEstimateOverwrite_forOffline_whileConnectedToNetwork_doesNotSetInitialEstimate() {
        setActiveNetworkInfo(networkInfoWifi);
        DefaultBandwidthMeter bandwidthMeter = build();
        long initialEstimate = bandwidthMeter.getBitrateEstimate();
        assertThat(initialEstimate).isNotEqualTo(123456789);
    }

    @Test
    public void initialBitrateEstimateOverwrite_forCountry_usesDefaultValuesForCountry() {
        setNetworkCountryIso(DefaultBandwidthMeterTest.SLOW_COUNTRY_ISO);
        DefaultBandwidthMeter bandwidthMeterSlow = build();
        long initialEstimateSlow = bandwidthMeterSlow.getBitrateEstimate();
        setNetworkCountryIso(DefaultBandwidthMeterTest.FAST_COUNTRY_ISO);
        DefaultBandwidthMeter bandwidthMeterFastWithSlowOverwrite = build();
        long initialEstimateFastWithSlowOverwrite = bandwidthMeterFastWithSlowOverwrite.getBitrateEstimate();
        assertThat(initialEstimateFastWithSlowOverwrite).isEqualTo(initialEstimateSlow);
    }

    @Test
    public void defaultInitialBitrateEstimate_withoutContext_isReasonable() {
        DefaultBandwidthMeter bandwidthMeterWithBuilder = /* context= */
        new DefaultBandwidthMeter.Builder(null).build();
        long initialEstimateWithBuilder = bandwidthMeterWithBuilder.getBitrateEstimate();
        DefaultBandwidthMeter bandwidthMeterWithoutBuilder = new DefaultBandwidthMeter();
        long initialEstimateWithoutBuilder = bandwidthMeterWithoutBuilder.getBitrateEstimate();
        assertThat(initialEstimateWithBuilder).isGreaterThan(100000L);
        assertThat(initialEstimateWithBuilder).isLessThan(50000000L);
        assertThat(initialEstimateWithoutBuilder).isGreaterThan(100000L);
        assertThat(initialEstimateWithoutBuilder).isLessThan(50000000L);
    }

    @Test
    public void defaultInitialBitrateEstimate_withoutAccessNetworkStatePermission_isReasonable() {
        Shadows.shadowOf(application).denyPermissions(ACCESS_NETWORK_STATE);
        DefaultBandwidthMeter bandwidthMeter = build();
        long initialEstimate = bandwidthMeter.getBitrateEstimate();
        assertThat(initialEstimate).isGreaterThan(100000L);
        assertThat(initialEstimate).isLessThan(50000000L);
    }
}

