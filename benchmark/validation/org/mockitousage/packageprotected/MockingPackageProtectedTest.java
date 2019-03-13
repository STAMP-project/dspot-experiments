/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.packageprotected;


import org.junit.Test;
import org.mockito.Mockito;
import org.mockitoutil.TestBase;


public class MockingPackageProtectedTest extends TestBase {
    static class Foo {}

    class Bar {}

    @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
    @Test
    public void shouldMockPackageProtectedClasses() {
        Mockito.mock(PackageProtected.class);
        Mockito.mock(MockingPackageProtectedTest.Foo.class);
        Mockito.mock(MockingPackageProtectedTest.Bar.class);
    }
}

