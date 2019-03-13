/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.s300th.internal;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests to ensure parsing of binary messages works as expected
 *
 * @author Till Klocke
 * @since 1.4.0
 */
public class ParsingTest {
    private static String S300TH_DATA_1 = "K013282501B";

    private static String S300TH_DATA_2 = "K013282502E";

    private static String S300TH_DATA_3 = "K1136425012";

    private static String[] ALL_S300TH_DATA = new String[]{ "K013282501B", "K013282502E", "K1136425012", "K0132825022", "K0132825028", "K0130425128", "K1185615410", "K0130425128", "K013042512D", "K116961560E", "K013042512C", "K013042512A", "K116861580E", "K116721580C", "K0131625330", "K013392512A", "K013492512C", "K013442512D", "K116591580E", "K013442512D", "K116291580F" };

    @Test
    public void testS300THParsing() throws Exception {
        double temp = ParseUtils.parseTemperature(ParsingTest.S300TH_DATA_1);
        Assert.assertEquals(23.2, temp, 0.01);
        double humidity = ParseUtils.parseS300THHumidity(ParsingTest.S300TH_DATA_1);
        Assert.assertEquals(50.8, humidity, 0.01);
        String address = ParseUtils.parseS300THAddress(ParsingTest.S300TH_DATA_1);
        Assert.assertEquals("1", address);
        String address2 = ParseUtils.parseS300THAddress(ParsingTest.S300TH_DATA_2);
        Assert.assertEquals(address, address2);
        String address3 = ParseUtils.parseS300THAddress(ParsingTest.S300TH_DATA_3);
        Assert.assertEquals("2", address3);
        for (String s : ParsingTest.ALL_S300TH_DATA) {
            String addr = ParseUtils.parseS300THAddress(s);
            double temperature = ParseUtils.parseTemperature(s);
            // Plausibility checks. not necessary valid
            if ((temperature < 10.0) || (temperature > 25.0)) {
                Assert.fail(("Temp was " + temp));
            }
            if (!((addr.equals("1")) || (addr.equals("2")))) {
                Assert.fail(("Address was " + addr));
            }
        }
    }
}

