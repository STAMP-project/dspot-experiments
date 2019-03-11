/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.customization;


import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.misusing.NotAMockException;
import org.mockito.exceptions.verification.NoInteractionsWanted;
import org.mockito.exceptions.verification.VerificationInOrderFailure;
import org.mockito.exceptions.verification.WantedButNotInvoked;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockitousage.IMethods;
import org.mockitousage.MethodsImpl;
import org.mockitoutil.TestBase;


public class BDDMockitoTest extends TestBase {
    @Mock
    IMethods mock;

    @Test
    public void should_stub() throws Exception {
        BDDMockito.given(mock.simpleMethod("foo")).willReturn("bar");
        Assertions.assertThat(mock.simpleMethod("foo")).isEqualTo("bar");
        Assertions.assertThat(mock.simpleMethod("whatever")).isEqualTo(null);
    }

    @Test
    public void should_stub_with_throwable() throws Exception {
        BDDMockito.given(mock.simpleMethod("foo")).willThrow(new BDDMockitoTest.SomethingWasWrong());
        try {
            Assertions.assertThat(mock.simpleMethod("foo")).isEqualTo("foo");
            Assert.fail();
        } catch (BDDMockitoTest.SomethingWasWrong expected) {
        }
    }

    @Test
    public void should_stub_with_throwable_class() throws Exception {
        BDDMockito.given(mock.simpleMethod("foo")).willThrow(BDDMockitoTest.SomethingWasWrong.class);
        try {
            Assertions.assertThat(mock.simpleMethod("foo")).isEqualTo("foo");
            Assert.fail();
        } catch (BDDMockitoTest.SomethingWasWrong expected) {
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void should_stub_with_throwable_classes() throws Exception {
        // unavoidable 'unchecked generic array creation' warning (from JDK7 onward)
        BDDMockito.given(mock.simpleMethod("foo")).willThrow(BDDMockitoTest.SomethingWasWrong.class, BDDMockitoTest.AnotherThingWasWrong.class);
        try {
            Assertions.assertThat(mock.simpleMethod("foo")).isEqualTo("foo");
            Assert.fail();
        } catch (BDDMockitoTest.SomethingWasWrong expected) {
        }
    }

    @Test
    public void should_stub_with_answer() throws Exception {
        BDDMockito.given(mock.simpleMethod(ArgumentMatchers.anyString())).willAnswer(new Answer<String>() {
            public String answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgument(0);
            }
        });
        Assertions.assertThat(mock.simpleMethod("foo")).isEqualTo("foo");
    }

    @Test
    public void should_stub_with_will_answer_alias() throws Exception {
        BDDMockito.given(mock.simpleMethod(ArgumentMatchers.anyString())).will(new Answer<String>() {
            public String answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgument(0);
            }
        });
        Assertions.assertThat(mock.simpleMethod("foo")).isEqualTo("foo");
    }

    @Test
    public void should_stub_consecutively() throws Exception {
        BDDMockito.given(mock.simpleMethod(ArgumentMatchers.anyString())).willReturn("foo").willReturn("bar");
        Assertions.assertThat(mock.simpleMethod("whatever")).isEqualTo("foo");
        Assertions.assertThat(mock.simpleMethod("whatever")).isEqualTo("bar");
    }

    @Test
    public void should_return_consecutively() throws Exception {
        BDDMockito.given(mock.objectReturningMethodNoArgs()).willReturn("foo", "bar", 12L, new byte[0]);
        Assertions.assertThat(mock.objectReturningMethodNoArgs()).isEqualTo("foo");
        Assertions.assertThat(mock.objectReturningMethodNoArgs()).isEqualTo("bar");
        Assertions.assertThat(mock.objectReturningMethodNoArgs()).isEqualTo(12L);
        Assertions.assertThat(mock.objectReturningMethodNoArgs()).isEqualTo(new byte[0]);
    }

    @Test
    public void should_stub_consecutively_with_call_real_method() throws Exception {
        MethodsImpl mock = Mockito.mock(MethodsImpl.class);
        BDDMockito.willReturn("foo").willCallRealMethod().given(mock).simpleMethod();
        Assertions.assertThat(mock.simpleMethod()).isEqualTo("foo");
        Assertions.assertThat(mock.simpleMethod()).isEqualTo(null);
    }

    @Test
    public void should_stub_void() throws Exception {
        BDDMockito.willThrow(new BDDMockitoTest.SomethingWasWrong()).given(mock).voidMethod();
        try {
            mock.voidMethod();
            Assert.fail();
        } catch (BDDMockitoTest.SomethingWasWrong expected) {
        }
    }

    @Test
    public void should_stub_void_with_exception_class() throws Exception {
        BDDMockito.willThrow(BDDMockitoTest.SomethingWasWrong.class).given(mock).voidMethod();
        try {
            mock.voidMethod();
            Assert.fail();
        } catch (BDDMockitoTest.SomethingWasWrong expected) {
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void should_stub_void_with_exception_classes() throws Exception {
        BDDMockito.willThrow(BDDMockitoTest.SomethingWasWrong.class, BDDMockitoTest.AnotherThingWasWrong.class).given(mock).voidMethod();
        try {
            mock.voidMethod();
            Assert.fail();
        } catch (BDDMockitoTest.SomethingWasWrong expected) {
        }
    }

    @Test
    public void should_stub_void_consecutively() throws Exception {
        BDDMockito.willDoNothing().willThrow(new BDDMockitoTest.SomethingWasWrong()).given(mock).voidMethod();
        mock.voidMethod();
        try {
            mock.voidMethod();
            Assert.fail();
        } catch (BDDMockitoTest.SomethingWasWrong expected) {
        }
    }

    @Test
    public void should_stub_void_consecutively_with_exception_class() throws Exception {
        BDDMockito.willDoNothing().willThrow(BDDMockitoTest.SomethingWasWrong.class).given(mock).voidMethod();
        mock.voidMethod();
        try {
            mock.voidMethod();
            Assert.fail();
        } catch (BDDMockitoTest.SomethingWasWrong expected) {
        }
    }

    @Test
    public void should_stub_using_do_return_style() throws Exception {
        BDDMockito.willReturn("foo").given(mock).simpleMethod("bar");
        Assertions.assertThat(mock.simpleMethod("boooo")).isEqualTo(null);
        Assertions.assertThat(mock.simpleMethod("bar")).isEqualTo("foo");
    }

    @Test
    public void should_stub_using_do_answer_style() throws Exception {
        BDDMockito.willAnswer(new Answer<String>() {
            public String answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgument(0);
            }
        }).given(mock).simpleMethod(ArgumentMatchers.anyString());
        Assertions.assertThat(mock.simpleMethod("foo")).isEqualTo("foo");
    }

    @Test
    public void should_stub_by_delegating_to_real_method() throws Exception {
        // given
        BDDMockitoTest.Dog dog = Mockito.mock(BDDMockitoTest.Dog.class);
        // when
        BDDMockito.willCallRealMethod().given(dog).bark();
        // then
        Assertions.assertThat(dog.bark()).isEqualTo("woof");
    }

    @Test
    public void should_stub_by_delegating_to_real_method_using_typical_stubbing_syntax() throws Exception {
        // given
        BDDMockitoTest.Dog dog = Mockito.mock(BDDMockitoTest.Dog.class);
        // when
        BDDMockito.given(dog.bark()).willCallRealMethod();
        // then
        Assertions.assertThat(dog.bark()).isEqualTo("woof");
    }

    @Test
    public void should_all_stubbed_mock_reference_access() throws Exception {
        Set<?> expectedMock = Mockito.mock(Set.class);
        Set<?> returnedMock = BDDMockito.given(expectedMock.isEmpty()).willReturn(false).getMock();
        Assertions.assertThat(returnedMock).isEqualTo(expectedMock);
    }

    @Test(expected = NotAMockException.class)
    public void should_validate_mock_when_verifying() {
        BDDMockito.then("notMock").should();
    }

    @Test(expected = NotAMockException.class)
    public void should_validate_mock_when_verifying_with_expected_number_of_invocations() {
        BDDMockito.then("notMock").should(Mockito.times(19));
    }

    @Test(expected = NotAMockException.class)
    public void should_validate_mock_when_verifying_no_more_interactions() {
        BDDMockito.then("notMock").should();
    }

    @Test(expected = WantedButNotInvoked.class)
    public void should_fail_for_expected_behavior_that_did_not_happen() {
        BDDMockito.then(mock).should().booleanObjectReturningMethod();
    }

    @Test
    public void should_pass_for_expected_behavior_that_happened() {
        mock.booleanObjectReturningMethod();
        BDDMockito.then(mock).should().booleanObjectReturningMethod();
        BDDMockito.then(mock).shouldHaveNoMoreInteractions();
    }

    @Test
    public void should_validate_that_mock_did_not_have_any_interactions() {
        BDDMockito.then(mock).shouldHaveZeroInteractions();
    }

    @Test
    public void should_fail_when_mock_had_unwanted_interactions() {
        mock.booleanObjectReturningMethod();
        try {
            BDDMockito.then(mock).shouldHaveZeroInteractions();
            Assert.fail("should have reported this interaction wasn't wanted");
        } catch (NoInteractionsWanted expected) {
        }
    }

    @Test
    public void should_fail_when_mock_had_more_interactions_than_expected() {
        mock.booleanObjectReturningMethod();
        mock.byteObjectReturningMethod();
        BDDMockito.then(mock).should().booleanObjectReturningMethod();
        try {
            BDDMockito.then(mock).shouldHaveNoMoreInteractions();
            Assert.fail("should have reported that no more interactions were wanted");
        } catch (NoInteractionsWanted expected) {
        }
    }

    @Test
    public void should_pass_for_interactions_that_happened_in_correct_order() {
        mock.booleanObjectReturningMethod();
        mock.arrayReturningMethod();
        InOrder inOrder = Mockito.inOrder(mock);
        BDDMockito.then(mock).should(inOrder).booleanObjectReturningMethod();
        BDDMockito.then(mock).should(inOrder).arrayReturningMethod();
    }

    @Test
    public void should_fail_for_interactions_that_were_in_wrong_order() {
        InOrder inOrder = Mockito.inOrder(mock);
        mock.arrayReturningMethod();
        mock.booleanObjectReturningMethod();
        BDDMockito.then(mock).should(inOrder).booleanObjectReturningMethod();
        try {
            BDDMockito.then(mock).should(inOrder).arrayReturningMethod();
            Assert.fail("should have raise in order verification failure on second verify call");
        } catch (VerificationInOrderFailure expected) {
        }
    }

    @Test(expected = WantedButNotInvoked.class)
    public void should_fail_when_checking_order_of_interactions_that_did_not_happen() {
        BDDMockito.then(mock).should(Mockito.inOrder(mock)).booleanObjectReturningMethod();
    }

    @Test
    public void should_pass_fluent_bdd_scenario() {
        BDDMockitoTest.Bike bike = new BDDMockitoTest.Bike();
        BDDMockitoTest.Person person = Mockito.mock(BDDMockitoTest.Person.class);
        BDDMockitoTest.Police police = Mockito.mock(BDDMockitoTest.Police.class);
        person.ride(bike);
        person.ride(bike);
        BDDMockito.then(person).should(Mockito.times(2)).ride(bike);
        BDDMockito.then(police).shouldHaveZeroInteractions();
    }

    @Test
    public void should_pass_fluent_bdd_scenario_with_ordered_verification() {
        BDDMockitoTest.Bike bike = new BDDMockitoTest.Bike();
        BDDMockitoTest.Car car = new BDDMockitoTest.Car();
        BDDMockitoTest.Person person = Mockito.mock(BDDMockitoTest.Person.class);
        person.drive(car);
        person.ride(bike);
        person.ride(bike);
        InOrder inOrder = Mockito.inOrder(person);
        BDDMockito.then(person).should(inOrder).drive(car);
        BDDMockito.then(person).should(inOrder, Mockito.times(2)).ride(bike);
    }

    @Test
    public void should_pass_fluent_bdd_scenario_with_ordered_verification_for_two_mocks() {
        BDDMockitoTest.Car car = new BDDMockitoTest.Car();
        BDDMockitoTest.Person person = Mockito.mock(BDDMockitoTest.Person.class);
        BDDMockitoTest.Police police = Mockito.mock(BDDMockitoTest.Police.class);
        person.drive(car);
        person.drive(car);
        police.chase(car);
        InOrder inOrder = Mockito.inOrder(person, police);
        BDDMockito.then(person).should(inOrder, Mockito.times(2)).drive(car);
        BDDMockito.then(police).should(inOrder).chase(car);
    }

    static class Person {
        void ride(BDDMockitoTest.Bike bike) {
        }

        void drive(BDDMockitoTest.Car car) {
        }
    }

    static class Bike {}

    static class Car {}

    static class Police {
        void chase(BDDMockitoTest.Car car) {
        }
    }

    class Dog {
        public String bark() {
            return "woof";
        }
    }

    private class SomethingWasWrong extends RuntimeException {}

    private class AnotherThingWasWrong extends RuntimeException {}
}

