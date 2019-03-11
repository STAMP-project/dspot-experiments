/**
 * Copyright (C) 2013-2015 RoboVM AB
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
package org.robovm.objc;


import ObjCObject.ObjectOwnershipHelper;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.robovm.apple.corefoundation.CFString;
import org.robovm.apple.foundation.NSCache;
import org.robovm.apple.foundation.NSCacheDelegate;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.uikit.UIView;


public class ObjCObjectOwnershipHelperTest {
    class CustomNSObject extends NSObject {
        String stringValue;

        long longValue;

        public CustomNSObject(String stringValue, long longValue) {
            this.stringValue = stringValue;
            this.longValue = longValue;
        }
    }

    class CustomUIView extends UIView {}

    class CustomNSCacheDelegateAdapter extends NSObject implements NSCacheDelegate {
        @Override
        public void willEvictObject(NSCache cache, NSObject obj) {
        }
    }

    class CustomCFString extends CFString {
        public CustomCFString(String str) {
            super(str);
        }
    }

    /**
     * Every custom class extending from NSObject should be retained on the Java
     * side when retained on the Obj-C side.
     */
    @Test
    public void testIfCustomObjectsAreRetained() {
        Assume.assumeTrue(System.getProperty("os.name").contains("iOS"));
        ObjCObjectOwnershipHelperTest.CustomUIView c1 = new ObjCObjectOwnershipHelperTest.CustomUIView();
        ObjCObjectOwnershipHelperTest.CustomNSCacheDelegateAdapter c2 = new ObjCObjectOwnershipHelperTest.CustomNSCacheDelegateAdapter();
        ObjCObjectOwnershipHelperTest.CustomCFString c3 = new ObjCObjectOwnershipHelperTest.CustomCFString("");
        NSCache cache = new NSCache();
        cache.put("1", c1);
        cache.put("2", c2);
        cache.put("3", as(NSObject.class));
        Assert.assertEquals(true, ObjectOwnershipHelper.isObjectRetained(c1));
        Assert.assertEquals(true, ObjectOwnershipHelper.isObjectRetained(c2));
        Assert.assertEquals(false, ObjectOwnershipHelper.isObjectRetained(as(NSObject.class)));
    }

    /**
     * Only custom objects should get the hooks to retain and release.
     */
    @Test
    public void testIfOnlyCustomObjectsAreRetained() {
        Assume.assumeTrue(System.getProperty("os.name").contains("iOS"));
        ObjCObjectOwnershipHelperTest.CustomUIView custom = new ObjCObjectOwnershipHelperTest.CustomUIView();
        NSObject default1 = new NSObject();
        UIView default2 = new UIView();
        NSCache cache = new NSCache();
        cache.put("1", custom);
        cache.put("2", default1);
        cache.put("3", default2);
        Assert.assertEquals(true, ObjectOwnershipHelper.isObjectRetained(custom));
        Assert.assertEquals(false, ObjectOwnershipHelper.isObjectRetained(default1));
        Assert.assertEquals(false, ObjectOwnershipHelper.isObjectRetained(default2));
    }

    /**
     * Every retained object should not be GCed and lose the values of its variables.
     */
    @Test
    public void testIfCustomObjectsVariablesAreRetained() {
        final String stringValue = "ABC";
        final long longValue = 123;
        NSCache cache = new NSCache();
        // We don't want to have any reference to the CustomNSObject.
        // If it were not stored with the ownership helper, it would get GCed immediately.
        addNewCustomObjectToCache(cache, "1", stringValue, longValue);
        ObjCObjectOwnershipHelperTest.forceGC();
        ObjCObjectOwnershipHelperTest.CustomNSObject custom = ((ObjCObjectOwnershipHelperTest.CustomNSObject) (cache.get("1")));
        Assert.assertEquals(stringValue, custom.stringValue);
        Assert.assertEquals(longValue, custom.longValue);
    }

    /**
     * Custom objects that have been released shouldn't be referenced any longer
     * on the Java side. But if they are retained again, also reference them
     * again.
     */
    @Test
    public void testIfCustomObjectsAreCorrectlyReferenced() {
        Assume.assumeTrue(System.getProperty("os.name").contains("iOS"));
        ObjCObjectOwnershipHelperTest.CustomUIView custom = new ObjCObjectOwnershipHelperTest.CustomUIView();
        NSCache cache = new NSCache();
        cache.put("1", custom);
        Assert.assertEquals(true, ObjectOwnershipHelper.isObjectRetained(custom));
        cache.remove("1");
        Assert.assertEquals(false, ObjectOwnershipHelper.isObjectRetained(custom));
        cache.put("2", custom);
        Assert.assertEquals(true, ObjectOwnershipHelper.isObjectRetained(custom));
        cache.remove("2");
        Assert.assertEquals(false, ObjectOwnershipHelper.isObjectRetained(custom));
    }
}

