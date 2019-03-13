/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.tplinksmarthome.internal;


import TPLinkSmartHomeThingType.HS100;
import TPLinkSmartHomeThingType.LB130;
import TPLinkSmartHomeThingType.RE270K;
import com.google.gson.Gson;
import java.io.IOException;
import org.junit.Test;
import org.openhab.binding.tplinksmarthome.internal.model.GsonUtil;


/**
 * Test class for {@link PropertiesCollector} class.
 *
 * @author Hilbrand Bouwkamp - Initial contribution
 */
public class PropertiesCollectorTest {
    private final Gson gson = GsonUtil.createGson();

    /**
     * Tests if properties for a bulb device are correctly parsed.
     *
     * @throws IOException
     * 		exception in case device not reachable
     */
    @Test
    public void testBulbProperties() throws IOException {
        assertProperties("bulb_get_sysinfo_response_on", LB130, 11);
    }

    /**
     * Tests if properties for a switch device are correctly parsed.
     *
     * @throws IOException
     * 		exception in case device not reachable
     */
    @Test
    public void testSwitchProperties() throws IOException {
        assertProperties("plug_get_sysinfo_response", HS100, 12);
    }

    /**
     * Tests if properties for a range extender device are correctly parsed.
     *
     * @throws IOException
     * 		exception in case device not reachable
     */
    @Test
    public void testRangeExtenderProperties() throws IOException {
        assertProperties("rangeextender_get_sysinfo_response", RE270K, 11);
    }
}

