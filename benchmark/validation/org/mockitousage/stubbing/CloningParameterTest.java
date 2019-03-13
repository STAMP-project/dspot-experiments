/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.stubbing;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.ClonesArguments;
import org.mockitoutil.TestBase;


public class CloningParameterTest extends TestBase {
    @Test
    public void shouldVerifyEvenIfArgumentsWereMutated() throws Exception {
        // given
        CloningParameterTest.EmailSender emailSender = Mockito.mock(CloningParameterTest.EmailSender.class, new ClonesArguments());
        // when
        businessLogic(emailSender);
        // then
        Mockito.verify(emailSender).sendEmail(1, new CloningParameterTest.Person("Wes"));
    }

    @Test
    public void shouldReturnDefaultValueWithCloningAnswer() throws Exception {
        // given
        CloningParameterTest.EmailSender emailSender = Mockito.mock(CloningParameterTest.EmailSender.class, new ClonesArguments());
        Mockito.when(emailSender.getAllEmails(new CloningParameterTest.Person("Wes"))).thenAnswer(new ClonesArguments());
        // when
        List<?> emails = emailSender.getAllEmails(new CloningParameterTest.Person("Wes"));
        // then
        Assert.assertNotNull(emails);
    }

    public class Person {
        private final String name;

        private boolean emailSent;

        public Person(String name) {
            this.name = name;
        }

        public void emailSent() {
            emailSent = true;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + (getOuterType().hashCode());
            result = (prime * result) + (emailSent ? 1231 : 1237);
            result = (prime * result) + ((name) == null ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj)
                return true;

            if (obj == null)
                return false;

            if ((getClass()) != (obj.getClass()))
                return false;

            CloningParameterTest.Person other = ((CloningParameterTest.Person) (obj));
            if (!(getOuterType().equals(other.getOuterType())))
                return false;

            if ((emailSent) != (other.emailSent))
                return false;

            if ((name) == null) {
                if ((other.name) != null)
                    return false;

            } else
                if (!(name.equals(other.name)))
                    return false;


            return true;
        }

        private CloningParameterTest getOuterType() {
            return CloningParameterTest.this;
        }
    }

    public interface EmailSender {
        void sendEmail(int i, CloningParameterTest.Person person);

        List<?> getAllEmails(CloningParameterTest.Person person);
    }
}

