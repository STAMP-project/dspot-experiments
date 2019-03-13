/**
 * Copyright (C) 2013 RoboVM AB
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl-2.0.html>.
 */
package org.robovm.libimobiledevice;


import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link InstallationProxyClient}.
 */
public class InstallationProxyClientTest {
    static InstallationProxyClient client;

    static AfcClient afcClient;

    @Test
    public void testBrowse() throws Exception {
        NSArray array = InstallationProxyClientTest.client.browse();
        NSDictionary safari = null;
        for (int i = 0; i < (array.count()); i++) {
            NSDictionary dict = ((NSDictionary) (array.objectAtIndex(i)));
            NSObject v = dict.objectForKey("CFBundleIdentifier");
            if ((v != null) && ("com.apple.mobilesafari".equals(v.toString()))) {
                safari = dict;
            }
        }
        Assert.assertNotNull(safari);
    }
}

