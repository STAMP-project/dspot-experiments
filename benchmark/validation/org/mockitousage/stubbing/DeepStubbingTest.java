/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.stubbing;


import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Locale;
import javax.net.SocketFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.TooManyActualInvocations;
import org.mockitoutil.TestBase;


public class DeepStubbingTest extends TestBase {
    static class Person {
        DeepStubbingTest.Address address;

        public DeepStubbingTest.Address getAddress() {
            return address;
        }

        public DeepStubbingTest.Address getAddress(String addressName) {
            return address;
        }

        public DeepStubbingTest.FinalClass getFinalClass() {
            return null;
        }
    }

    static class Address {
        DeepStubbingTest.Street street;

        public DeepStubbingTest.Street getStreet() {
            return street;
        }

        public DeepStubbingTest.Street getStreet(Locale locale) {
            return street;
        }
    }

    static class Street {
        String name;

        public String getName() {
            return name;
        }

        public String getLongName() {
            return name;
        }
    }

    static final class FinalClass {}

    interface First {
        DeepStubbingTest.Second getSecond();

        String getString();
    }

    interface Second extends List<String> {}

    class BaseClassGenerics<A, B> {}

    class ReversedGenerics<A, B> extends DeepStubbingTest.BaseClassGenerics<A, B> {
        DeepStubbingTest.ReversedGenerics<B, A> reverse() {
            return null;
        }

        A finalMethod() {
            return null;
        }
    }

    class SuperOfReversedGenerics extends DeepStubbingTest.ReversedGenerics<String, Long> {}

    @Test
    public void myTest() throws Exception {
        SocketFactory sf = Mockito.mock(SocketFactory.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(sf.createSocket(ArgumentMatchers.anyString(), ArgumentMatchers.eq(80))).thenReturn(null);
        sf.createSocket("what", 80);
    }

    @Test
    public void simpleCase() throws Exception {
        OutputStream out = new ByteArrayOutputStream();
        Socket socket = Mockito.mock(Socket.class);
        Mockito.when(socket.getOutputStream()).thenReturn(out);
        Assert.assertSame(out, socket.getOutputStream());
    }

    /**
     * Test that deep stubbing works for one intermediate level
     */
    @Test
    public void oneLevelDeep() throws Exception {
        OutputStream out = new ByteArrayOutputStream();
        SocketFactory socketFactory = Mockito.mock(SocketFactory.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(socketFactory.createSocket().getOutputStream()).thenReturn(out);
        Assert.assertSame(out, socketFactory.createSocket().getOutputStream());
    }

    /**
     * Test that stubbing of two mocks stubs don't interfere
     */
    @Test
    public void interactions() throws Exception {
        OutputStream out1 = new ByteArrayOutputStream();
        OutputStream out2 = new ByteArrayOutputStream();
        SocketFactory sf1 = Mockito.mock(SocketFactory.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(sf1.createSocket().getOutputStream()).thenReturn(out1);
        SocketFactory sf2 = Mockito.mock(SocketFactory.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(sf2.createSocket().getOutputStream()).thenReturn(out2);
        Assert.assertSame(out1, sf1.createSocket().getOutputStream());
        Assert.assertSame(out2, sf2.createSocket().getOutputStream());
    }

    /**
     * Test that stubbing of methods of different arguments don't interfere
     */
    @Test
    public void withArguments() throws Exception {
        OutputStream out1 = new ByteArrayOutputStream();
        OutputStream out2 = new ByteArrayOutputStream();
        OutputStream out3 = new ByteArrayOutputStream();
        SocketFactory sf = Mockito.mock(SocketFactory.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(sf.createSocket().getOutputStream()).thenReturn(out1);
        Mockito.when(sf.createSocket("google.com", 80).getOutputStream()).thenReturn(out2);
        Mockito.when(sf.createSocket("stackoverflow.com", 80).getOutputStream()).thenReturn(out3);
        Assert.assertSame(out1, sf.createSocket().getOutputStream());
        Assert.assertSame(out2, sf.createSocket("google.com", 80).getOutputStream());
        Assert.assertSame(out3, sf.createSocket("stackoverflow.com", 80).getOutputStream());
    }

    /**
     * Test that deep stubbing work with argument patterns
     */
    @Test
    public void withAnyPatternArguments() throws Exception {
        OutputStream out = new ByteArrayOutputStream();
        // TODO: should not use javax in case it changes
        SocketFactory sf = Mockito.mock(SocketFactory.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(sf.createSocket(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt()).getOutputStream()).thenReturn(out);
        Assert.assertSame(out, sf.createSocket("google.com", 80).getOutputStream());
        Assert.assertSame(out, sf.createSocket("stackoverflow.com", 8080).getOutputStream());
    }

    /**
     * Test that deep stubbing work with argument patterns
     */
    @Test
    public void withComplexPatternArguments() throws Exception {
        OutputStream out1 = new ByteArrayOutputStream();
        OutputStream out2 = new ByteArrayOutputStream();
        SocketFactory sf = Mockito.mock(SocketFactory.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(sf.createSocket(ArgumentMatchers.anyString(), ArgumentMatchers.eq(80)).getOutputStream()).thenReturn(out1);
        Mockito.when(sf.createSocket(ArgumentMatchers.anyString(), ArgumentMatchers.eq(8080)).getOutputStream()).thenReturn(out2);
        Assert.assertSame(out2, sf.createSocket("stackoverflow.com", 8080).getOutputStream());
        Assert.assertSame(out1, sf.createSocket("google.com", 80).getOutputStream());
        Assert.assertSame(out2, sf.createSocket("google.com", 8080).getOutputStream());
        Assert.assertSame(out1, sf.createSocket("stackoverflow.com", 80).getOutputStream());
    }

    /**
     * Test that deep stubbing work with primitive expected values
     */
    @Test
    public void withSimplePrimitive() throws Exception {
        int a = 32;
        SocketFactory sf = Mockito.mock(SocketFactory.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(sf.createSocket().getPort()).thenReturn(a);
        Assert.assertEquals(a, sf.createSocket().getPort());
    }

    /**
     * Test that deep stubbing work with primitive expected values with
     * pattern method arguments
     */
    @Test
    public void withPatternPrimitive() throws Exception {
        int a = 12;
        int b = 23;
        int c = 34;
        SocketFactory sf = Mockito.mock(SocketFactory.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(sf.createSocket(ArgumentMatchers.eq("stackoverflow.com"), ArgumentMatchers.eq(80)).getPort()).thenReturn(a);
        Mockito.when(sf.createSocket(ArgumentMatchers.eq("google.com"), ArgumentMatchers.anyInt()).getPort()).thenReturn(b);
        Mockito.when(sf.createSocket(ArgumentMatchers.eq("stackoverflow.com"), ArgumentMatchers.eq(8080)).getPort()).thenReturn(c);
        Assert.assertEquals(b, sf.createSocket("google.com", 80).getPort());
        Assert.assertEquals(c, sf.createSocket("stackoverflow.com", 8080).getPort());
        Assert.assertEquals(a, sf.createSocket("stackoverflow.com", 80).getPort());
    }

    DeepStubbingTest.Person person = Mockito.mock(DeepStubbingTest.Person.class, Mockito.RETURNS_DEEP_STUBS);

    @Test
    public void shouldStubbingBasicallyWorkFine() {
        // given
        BDDMockito.given(person.getAddress().getStreet().getName()).willReturn("Norymberska");
        // when
        String street = person.getAddress().getStreet().getName();
        // then
        Assert.assertEquals("Norymberska", street);
    }

    @Test
    public void shouldVerificationBasicallyWorkFine() {
        // given
        person.getAddress().getStreet().getName();
        // then
        Mockito.verify(person.getAddress().getStreet()).getName();
    }

    @Test
    public void verification_work_with_argument_Matchers_in_nested_calls() {
        // given
        person.getAddress("111 Mock Lane").getStreet();
        person.getAddress("111 Mock Lane").getStreet(Locale.ITALIAN).getName();
        // then
        Mockito.verify(person.getAddress(ArgumentMatchers.anyString())).getStreet();
        Mockito.verify(person.getAddress(ArgumentMatchers.anyString()).getStreet(Locale.CHINESE), Mockito.never()).getName();
        Mockito.verify(person.getAddress(ArgumentMatchers.anyString()).getStreet(ArgumentMatchers.eq(Locale.ITALIAN))).getName();
    }

    @Test
    public void deep_stub_return_same_mock_instance_if_invocation_matchers_matches() {
        Mockito.when(person.getAddress(ArgumentMatchers.anyString()).getStreet().getName()).thenReturn("deep");
        person.getAddress("the docks").getStreet().getName();
        Assert.assertSame(person.getAddress("the docks").getStreet(), person.getAddress(ArgumentMatchers.anyString()).getStreet());
        Assert.assertSame(person.getAddress(ArgumentMatchers.anyString()).getStreet(), person.getAddress(ArgumentMatchers.anyString()).getStreet());
        Assert.assertSame(person.getAddress("the docks").getStreet(), person.getAddress("the docks").getStreet());
        Assert.assertSame(person.getAddress(ArgumentMatchers.anyString()).getStreet(), person.getAddress("the docks").getStreet());
        Assert.assertSame(person.getAddress("111 Mock Lane").getStreet(), person.getAddress("the docks").getStreet());
    }

    @Test
    public void times_never_atLeast_atMost_verificationModes_should_work() {
        Mockito.when(person.getAddress(ArgumentMatchers.anyString()).getStreet().getName()).thenReturn("deep");
        person.getAddress("the docks").getStreet().getName();
        person.getAddress("the docks").getStreet().getName();
        person.getAddress("the docks").getStreet().getName();
        person.getAddress("the docks").getStreet(Locale.ITALIAN).getName();
        Mockito.verify(person.getAddress("the docks").getStreet(), Mockito.times(3)).getName();
        Mockito.verify(person.getAddress("the docks").getStreet(Locale.CHINESE), Mockito.never()).getName();
        Mockito.verify(person.getAddress("the docks").getStreet(Locale.ITALIAN), Mockito.atMost(1)).getName();
    }

    @Test
    public void inOrder_only_work_on_the_very_last_mock_but_it_works() {
        Mockito.when(person.getAddress(ArgumentMatchers.anyString()).getStreet().getName()).thenReturn("deep");
        Mockito.when(person.getAddress(ArgumentMatchers.anyString()).getStreet(Locale.ITALIAN).getName()).thenReturn("deep");
        Mockito.when(person.getAddress(ArgumentMatchers.anyString()).getStreet(Locale.CHINESE).getName()).thenReturn("deep");
        person.getAddress("the docks").getStreet().getName();
        person.getAddress("the docks").getStreet().getLongName();
        person.getAddress("the docks").getStreet(Locale.ITALIAN).getName();
        person.getAddress("the docks").getStreet(Locale.CHINESE).getName();
        InOrder inOrder = Mockito.inOrder(person.getAddress("the docks").getStreet(), person.getAddress("the docks").getStreet(Locale.CHINESE), person.getAddress("the docks").getStreet(Locale.ITALIAN));
        inOrder.verify(person.getAddress("the docks").getStreet(), Mockito.times(1)).getName();
        inOrder.verify(person.getAddress("the docks").getStreet()).getLongName();
        inOrder.verify(person.getAddress("the docks").getStreet(Locale.ITALIAN), Mockito.atLeast(1)).getName();
        inOrder.verify(person.getAddress("the docks").getStreet(Locale.CHINESE)).getName();
    }

    @Test
    public void verificationMode_only_work_on_the_last_returned_mock() {
        // 1st invocation on Address mock (stubbing)
        Mockito.when(person.getAddress("the docks").getStreet().getName()).thenReturn("deep");
        // 2nd invocation on Address mock (real)
        person.getAddress("the docks").getStreet().getName();
        // 3rd invocation on Address mock (verification)
        // (Address mock is not in verification mode)
        Mockito.verify(person.getAddress("the docks").getStreet()).getName();
        try {
            Mockito.verify(person.getAddress("the docks"), Mockito.times(1)).getStreet();
            Assert.fail();
        } catch (TooManyActualInvocations e) {
            assertThat(e.getMessage()).contains("Wanted 1 time").contains("But was 3 times");
        }
    }

    @Test
    public void shouldFailGracefullyWhenClassIsFinal() {
        // when
        DeepStubbingTest.FinalClass value = new DeepStubbingTest.FinalClass();
        BDDMockito.given(person.getFinalClass()).willReturn(value);
        // then
        Assert.assertEquals(value, person.getFinalClass());
    }

    @Test
    public void deep_stub_does_not_try_to_mock_generic_final_classes() {
        DeepStubbingTest.First first = Mockito.mock(DeepStubbingTest.First.class, Mockito.RETURNS_DEEP_STUBS);
        Assert.assertNull(first.getString());
        Assert.assertNull(first.getSecond().get(0));
    }

    @Test
    public void deep_stub_does_not_stack_overflow_on_reversed_generics() {
        DeepStubbingTest.SuperOfReversedGenerics mock = Mockito.mock(DeepStubbingTest.SuperOfReversedGenerics.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(((Object) (mock.reverse().finalMethod()))).thenReturn(5L);
        assertThat(mock.reverse().finalMethod()).isEqualTo(5L);
    }
}

