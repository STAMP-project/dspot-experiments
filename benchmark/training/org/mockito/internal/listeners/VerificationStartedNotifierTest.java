/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.listeners;


import VerificationStartedNotifier.Event;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockingDetails;
import org.mockito.Mockito;
import org.mockitoutil.TestBase;


public class VerificationStartedNotifierTest extends TestBase {
    MockingDetails mockingDetails = Mockito.mockingDetails(Mockito.mock(List.class));

    @Test
    public void does_not_do_anything_when_list_is_empty() throws Exception {
        // expect nothing to happen
        VerificationStartedNotifier.notifyVerificationStarted(((List) (Collections.emptyList())), mockingDetails);
    }

    @Test
    public void decent_exception_when_setting_non_mock() throws Exception {
        VerificationStartedNotifier.Event event = new VerificationStartedNotifier.Event(mockingDetails);
        try {
            // when
            event.setMock("not a mock");
            Assert.fail();
        } catch (Exception e) {
            // then
            Assert.assertEquals(("VerificationStartedEvent.setMock() does not accept parameter which is not a Mockito mock.\n" + ("  Received parameter: \"not a mock\".\n" + "  See the Javadoc.")), e.getMessage());
        }
    }

    @Test
    public void shows_clean_exception_message_when_illegal_null_arg_is_used() throws Exception {
        VerificationStartedNotifier.Event event = new VerificationStartedNotifier.Event(mockingDetails);
        try {
            // when
            event.setMock(null);
            Assert.fail();
        } catch (Exception e) {
            // then
            Assert.assertEquals("VerificationStartedEvent.setMock() does not accept null parameter. See the Javadoc.", e.getMessage());
        }
    }

    @Test
    public void decent_exception_when_setting_mock_of_wrong_type() throws Exception {
        final Set differentTypeMock = Mockito.mock(Set.class);
        VerificationStartedNotifier.Event event = new VerificationStartedNotifier.Event(mockingDetails);
        try {
            // when
            event.setMock(differentTypeMock);
            Assert.fail();
        } catch (Exception e) {
            // then
            Assert.assertEquals(TestBase.filterHashCode(("VerificationStartedEvent.setMock() does not accept parameter which is not the same type as the original mock.\n" + (("  Required type: java.util.List\n" + "  Received parameter: Mock for Set, hashCode: xxx.\n") + "  See the Javadoc."))), TestBase.filterHashCode(e.getMessage()));
        }
    }

    @Test
    public void decent_exception_when_setting_mock_that_does_not_implement_all_desired_interfaces() throws Exception {
        final Set mock = Mockito.mock(Set.class, Mockito.withSettings().extraInterfaces(List.class));
        final Set missingExtraInterface = Mockito.mock(Set.class);
        VerificationStartedNotifier.Event event = new VerificationStartedNotifier.Event(Mockito.mockingDetails(mock));
        try {
            // when setting mock that does not have all necessary interfaces
            event.setMock(missingExtraInterface);
            Assert.fail();
        } catch (Exception e) {
            // then
            Assert.assertEquals(TestBase.filterHashCode(("VerificationStartedEvent.setMock() does not accept parameter which does not implement all extra interfaces of the original mock.\n" + ((("  Required type: java.util.Set\n" + "  Required extra interface: java.util.List\n") + "  Received parameter: Mock for Set, hashCode: xxx.\n") + "  See the Javadoc."))), TestBase.filterHashCode(e.getMessage()));
        }
    }

    @Test
    public void accepts_replacement_mock_if_all_types_are_compatible() throws Exception {
        final Set mock = Mockito.mock(Set.class, Mockito.withSettings().extraInterfaces(List.class, Map.class));
        final Set compatibleMock = Mockito.mock(Set.class, Mockito.withSettings().extraInterfaces(List.class, Map.class));
        VerificationStartedNotifier.Event event = new VerificationStartedNotifier.Event(Mockito.mockingDetails(mock));
        // when
        event.setMock(compatibleMock);
        // then
        Assert.assertEquals(compatibleMock, event.getMock());
    }
}

