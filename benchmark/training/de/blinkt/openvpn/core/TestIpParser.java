/**
 * Copyright (c) 2012-2016 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */
package de.blinkt.openvpn.core;


import java.net.UnknownHostException;
import org.junit.Test;


/**
 * Created by arne on 23.07.16.
 */
public class TestIpParser {
    @Test
    public void parseIPv6Zeros() throws UnknownHostException {
        testAddress("2020:0:1234::", 45, "2020:0:1234::/45");
        testAddress("::", 0, "::/0");
        testAddress("2a02:2e0:3fe:1001:302::", 128, "2a02:2e0:3fe:1001:302::/128");
        testAddress("2a02:2e0:3fe:1001:302::70", 128, "2a02:2e0:3fe:1001:302:0:0:70/128");
    }
}

