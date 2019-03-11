/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.annotation;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class InjectionOfInlinedMockDeclarationTest {
    @InjectMocks
    private InjectionOfInlinedMockDeclarationTest.Receiver receiver;

    @InjectMocks
    private InjectionOfInlinedMockDeclarationTest.Receiver spiedReceiver = Mockito.spy(new InjectionOfInlinedMockDeclarationTest.Receiver());

    private InjectionOfInlinedMockDeclarationTest.Antenna oldAntenna = Mockito.mock(InjectionOfInlinedMockDeclarationTest.Antenna.class);

    private InjectionOfInlinedMockDeclarationTest.Antenna satelliteAntenna = Mockito.mock(InjectionOfInlinedMockDeclarationTest.Antenna.class);

    private InjectionOfInlinedMockDeclarationTest.Antenna antenna = Mockito.mock(InjectionOfInlinedMockDeclarationTest.Antenna.class, "dvbtAntenna");

    private InjectionOfInlinedMockDeclarationTest.Tuner tuner = Mockito.spy(new InjectionOfInlinedMockDeclarationTest.Tuner());

    @Test
    public void mock_declared_fields_shall_be_injected_too() throws Exception {
        Assert.assertNotNull(receiver.oldAntenna);
        Assert.assertNotNull(receiver.satelliteAntenna);
        Assert.assertNotNull(receiver.dvbtAntenna);
        Assert.assertNotNull(receiver.tuner);
    }

    @Test
    public void unnamed_mocks_should_be_resolved_withe_their_field_names() throws Exception {
        Assert.assertSame(oldAntenna, receiver.oldAntenna);
        Assert.assertSame(satelliteAntenna, receiver.satelliteAntenna);
    }

    @Test
    public void named_mocks_should_be_resolved_with_their_name() throws Exception {
        Assert.assertSame(antenna, receiver.dvbtAntenna);
    }

    @Test
    public void inject_mocks_even_in_declared_spy() throws Exception {
        Assert.assertNotNull(spiedReceiver.oldAntenna);
        Assert.assertNotNull(spiedReceiver.tuner);
    }

    // note that static class is not private !!
    static class Receiver {
        InjectionOfInlinedMockDeclarationTest.Antenna oldAntenna;

        InjectionOfInlinedMockDeclarationTest.Antenna satelliteAntenna;

        InjectionOfInlinedMockDeclarationTest.Antenna dvbtAntenna;

        InjectionOfInlinedMockDeclarationTest.Tuner tuner;

        public boolean tune() {
            return true;
        }
    }

    private static class Antenna {}

    private static class Tuner {}
}

