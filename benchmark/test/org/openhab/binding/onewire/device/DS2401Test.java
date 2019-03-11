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
package org.openhab.binding.onewire.device;


import OnOffType.OFF;
import OnOffType.ON;
import org.junit.Test;
import org.openhab.binding.onewire.test.AbstractDeviceTest;


/**
 * Tests cases for {@link DS2401}.
 *
 * @author Jan N. Klug - Initial contribution
 */
public class DS2401Test extends AbstractDeviceTest {
    @Test
    public void presenceTestOn() {
        presenceTest(ON);
    }

    @Test
    public void presenceTestOff() {
        presenceTest(OFF);
    }
}

