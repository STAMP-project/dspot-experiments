/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.matchers;


import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.exceptions.verification.WantedButNotInvoked;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class CapturingArgumentsTest extends TestBase {
    class Person {
        private final Integer age;

        public Person(Integer age) {
            this.age = age;
        }

        public int getAge() {
            return age;
        }
    }

    class BulkEmailService {
        private CapturingArgumentsTest.EmailService service;

        public BulkEmailService(CapturingArgumentsTest.EmailService service) {
            this.service = service;
        }

        public void email(Integer... personId) {
            for (Integer i : personId) {
                CapturingArgumentsTest.Person person = new CapturingArgumentsTest.Person(i);
                service.sendEmailTo(person);
            }
        }
    }

    interface EmailService {
        boolean sendEmailTo(CapturingArgumentsTest.Person person);
    }

    CapturingArgumentsTest.EmailService emailService = Mockito.mock(CapturingArgumentsTest.EmailService.class);

    CapturingArgumentsTest.BulkEmailService bulkEmailService = new CapturingArgumentsTest.BulkEmailService(emailService);

    IMethods mock = Mockito.mock(IMethods.class);

    @SuppressWarnings("deprecation")
    @Test
    public void should_allow_assertions_on_captured_argument() {
        // given
        ArgumentCaptor<CapturingArgumentsTest.Person> argument = ArgumentCaptor.forClass(CapturingArgumentsTest.Person.class);
        // when
        bulkEmailService.email(12);
        // then
        Mockito.verify(emailService).sendEmailTo(argument.capture());
        Assert.assertEquals(12, argument.getValue().getAge());
    }

    @Test
    public void should_allow_assertions_on_all_captured_arguments() {
        // given
        ArgumentCaptor<CapturingArgumentsTest.Person> argument = ArgumentCaptor.forClass(CapturingArgumentsTest.Person.class);
        // when
        bulkEmailService.email(11, 12);
        // then
        Mockito.verify(emailService, Mockito.times(2)).sendEmailTo(argument.capture());
        Assert.assertEquals(11, argument.getAllValues().get(0).getAge());
        Assert.assertEquals(12, argument.getAllValues().get(1).getAge());
    }

    @Test
    public void should_allow_assertions_on_last_argument() {
        // given
        ArgumentCaptor<CapturingArgumentsTest.Person> argument = ArgumentCaptor.forClass(CapturingArgumentsTest.Person.class);
        // when
        bulkEmailService.email(11, 12, 13);
        // then
        Mockito.verify(emailService, Mockito.times(3)).sendEmailTo(argument.capture());
        Assert.assertEquals(13, argument.getValue().getAge());
    }

    @Test
    public void should_print_captor_matcher() {
        // given
        ArgumentCaptor<CapturingArgumentsTest.Person> person = ArgumentCaptor.forClass(CapturingArgumentsTest.Person.class);
        try {
            // when
            Mockito.verify(emailService).sendEmailTo(person.capture());
            Assert.fail();
        } catch (WantedButNotInvoked e) {
            // then
            Assert.assertThat(e).hasMessageContaining("<Capturing argument>");
        }
    }

    @Test
    public void should_allow_assertions_on_captured_null() {
        // given
        ArgumentCaptor<CapturingArgumentsTest.Person> argument = ArgumentCaptor.forClass(CapturingArgumentsTest.Person.class);
        // when
        emailService.sendEmailTo(null);
        // then
        Mockito.verify(emailService).sendEmailTo(argument.capture());
        Assert.assertEquals(null, argument.getValue());
    }

    @Test
    public void should_allow_construction_of_captor_for_parameterized_type_in_a_convenient_way() {
        // the test passes if this expression compiles
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<CapturingArgumentsTest.Person>> argument = ArgumentCaptor.forClass(List.class);
        Assert.assertNotNull(argument);
    }

    @Test
    public void should_allow_construction_of_captor_for_a_more_specific_type() {
        // the test passes if this expression compiles
        ArgumentCaptor<List<?>> argument = ArgumentCaptor.forClass(ArrayList.class);
        Assert.assertNotNull(argument);
    }

    @Test
    public void should_allow_capturing_for_stubbing() {
        // given
        ArgumentCaptor<CapturingArgumentsTest.Person> argument = ArgumentCaptor.forClass(CapturingArgumentsTest.Person.class);
        Mockito.when(emailService.sendEmailTo(argument.capture())).thenReturn(false);
        // when
        emailService.sendEmailTo(new CapturingArgumentsTest.Person(10));
        // then
        Assert.assertEquals(10, argument.getValue().getAge());
    }

    @Test
    public void should_capture_when_stubbing_only_when_entire_invocation_matches() {
        // given
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        Mockito.when(mock.simpleMethod(argument.capture(), ArgumentMatchers.eq(2))).thenReturn("blah");
        // when
        mock.simpleMethod("foo", 200);
        mock.simpleMethod("bar", 2);
        // then
        Assertions.assertThat(argument.getAllValues()).containsOnly("bar");
    }

    @Test
    public void should_say_something_smart_when_misused() {
        ArgumentCaptor<CapturingArgumentsTest.Person> argument = ArgumentCaptor.forClass(CapturingArgumentsTest.Person.class);
        try {
            argument.getValue();
            Assert.fail();
        } catch (MockitoException expected) {
        }
    }

    @Test
    public void should_capture_when_full_arg_list_matches() throws Exception {
        // given
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        // when
        mock.simpleMethod("foo", 1);
        mock.simpleMethod("bar", 2);
        // then
        Mockito.verify(mock).simpleMethod(captor.capture(), ArgumentMatchers.eq(1));
        Assert.assertEquals(1, captor.getAllValues().size());
        Assert.assertEquals("foo", captor.getValue());
    }

    @Test
    public void should_capture_int_by_creating_captor_with_primitive_wrapper() {
        // given
        ArgumentCaptor<Integer> argument = ArgumentCaptor.forClass(Integer.class);
        // when
        mock.intArgumentMethod(10);
        // then
        Mockito.verify(mock).intArgumentMethod(argument.capture());
        Assert.assertEquals(10, ((int) (argument.getValue())));
    }

    @Test
    public void should_capture_int_by_creating_captor_with_primitive() throws Exception {
        // given
        ArgumentCaptor<Integer> argument = ArgumentCaptor.forClass(int.class);
        // when
        mock.intArgumentMethod(10);
        // then
        Mockito.verify(mock).intArgumentMethod(argument.capture());
        Assert.assertEquals(10, ((int) (argument.getValue())));
    }

    @Test
    public void should_capture_byte_vararg_by_creating_captor_with_primitive() throws Exception {
        // given
        ArgumentCaptor<Byte> argumentCaptor = ArgumentCaptor.forClass(byte.class);
        // when
        mock.varargsbyte(((byte) (1)), ((byte) (2)));
        // then
        Mockito.verify(mock).varargsbyte(argumentCaptor.capture());
        Assert.assertEquals(((byte) (2)), ((byte) (argumentCaptor.getValue())));
        Assertions.assertThat(argumentCaptor.getAllValues()).containsExactly(((byte) (1)), ((byte) (2)));
    }

    @Test
    public void should_capture_byte_vararg_by_creating_captor_with_primitive_wrapper() throws Exception {
        // given
        ArgumentCaptor<Byte> argumentCaptor = ArgumentCaptor.forClass(Byte.class);
        // when
        mock.varargsbyte(((byte) (1)), ((byte) (2)));
        // then
        Mockito.verify(mock).varargsbyte(argumentCaptor.capture());
        Assert.assertEquals(((byte) (2)), ((byte) (argumentCaptor.getValue())));
        Assertions.assertThat(argumentCaptor.getAllValues()).containsExactly(((byte) (1)), ((byte) (2)));
    }

    @Test
    public void should_capture_vararg() throws Exception {
        // given
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        // when
        mock.mixedVarargs(42, "a", "b", "c");
        // then
        Mockito.verify(mock).mixedVarargs(ArgumentMatchers.any(), argumentCaptor.capture());
        Assertions.assertThat(argumentCaptor.getAllValues()).containsExactly("a", "b", "c");
    }

    @Test
    public void should_capture_all_vararg() throws Exception {
        // given
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        // when
        mock.mixedVarargs(42, "a", "b", "c");
        mock.mixedVarargs(42, "again ?!");
        // then
        Mockito.verify(mock, Mockito.times(2)).mixedVarargs(ArgumentMatchers.any(), argumentCaptor.capture());
        Assertions.assertThat(argumentCaptor.getAllValues()).containsExactly("a", "b", "c", "again ?!");
    }

    @Test
    public void should_capture_one_arg_even_when_using_vararg_captor_on_nonvararg_method() throws Exception {
        // given
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        // when
        mock.simpleMethod("a", 2);
        // then
        Mockito.verify(mock).simpleMethod(argumentCaptor.capture(), ArgumentMatchers.eq(2));
        Assertions.assertThat(argumentCaptor.getAllValues()).containsExactly("a");
    }

    @Test
    public void captures_correctly_when_captor_used_multiple_times() throws Exception {
        // given
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        // when
        mock.mixedVarargs(42, "a", "b", "c");
        // then
        // this is only for backwards compatibility. It does not make sense in real to do so.
        Mockito.verify(mock).mixedVarargs(ArgumentMatchers.any(), argumentCaptor.capture(), argumentCaptor.capture(), argumentCaptor.capture());
        Assertions.assertThat(argumentCaptor.getAllValues()).containsExactly("a", "b", "c");
    }

    @Test
    public void captures_correctly_when_captor_used_on_pure_vararg_method() throws Exception {
        // given
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        // when
        mock.varargs(42, "capturedValue");
        // then
        Mockito.verify(mock).varargs(ArgumentMatchers.eq(42), argumentCaptor.capture());
        Assertions.assertThat(argumentCaptor.getValue()).contains("capturedValue");
    }
}

