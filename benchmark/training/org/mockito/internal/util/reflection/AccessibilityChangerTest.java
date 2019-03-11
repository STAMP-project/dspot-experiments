/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.util.reflection;


import java.util.Observable;
import org.junit.Test;
import org.mockitoutil.VmArgAssumptions;


public class AccessibilityChangerTest {
    @SuppressWarnings("unused")
    private Observable whatever;

    @Test
    public void should_enable_and_safely_disable() throws Exception {
        AccessibilityChanger changer = new AccessibilityChanger();
        changer.enableAccess(field("whatever"));
        changer.safelyDisableAccess(field("whatever"));
    }

    @Test(expected = AssertionError.class)
    public void safelyDisableAccess_should_fail_when_enableAccess_not_called() throws Exception {
        VmArgAssumptions.assumeVmArgPresent("-ea");
        new AccessibilityChanger().safelyDisableAccess(field("whatever"));
    }
}

