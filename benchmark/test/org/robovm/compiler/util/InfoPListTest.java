/**
 * Copyright (C) 2015 RoboVM AB
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
package org.robovm.compiler.util;


import com.dd.plist.NSDictionary;
import com.dd.plist.NSString;
import java.io.File;
import java.util.Arrays;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class InfoPListTest {
    @Test
    public void testParsePropertyList() throws Exception {
        File file = File.createTempFile(InfoPListTest.class.getSimpleName(), ".tmp");
        byte[] data = IOUtils.toByteArray(getClass().getResourceAsStream("InfoPListTest.Info.plist.xml"));
        FileUtils.writeByteArrayToFile(file, data);
        Properties props = new Properties();
        props.setProperty("prop1", "value1");
        props.setProperty("prop2", "value2");
        props.setProperty("prop3", "value3");
        props.setProperty("prop4", "value4");
        NSDictionary dict = ((NSDictionary) (InfoPList.parsePropertyList(file, props, true)));
        Assert.assertEquals(new NSString("value1"), dict.objectForKey("Prop1"));
        Assert.assertEquals(new NSString("value2foobar"), dict.objectForKey("Prop2"));
        Assert.assertEquals(new NSString("foobarvalue3"), dict.objectForKey("Prop3"));
        Assert.assertEquals(new NSString("foovalue4bar"), dict.objectForKey("Prop4"));
        Assert.assertEquals(new NSString("foovalue1value2bar"), dict.objectForKey("Prop5"));
        Assert.assertEquals(new NSString("foovalue1woovalue2bar"), dict.objectForKey("Prop6"));
        Assert.assertEquals(new NSString("value1woovalue2"), dict.objectForKey("Prop7"));
        Assert.assertEquals(new NSString("${unknown}"), dict.objectForKey("Prop8"));
        Assert.assertEquals(Arrays.asList(new NSString("value1"), new NSString("value2")), Arrays.asList(getArray()));
    }
}

