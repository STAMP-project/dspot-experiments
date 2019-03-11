/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.annotation;


import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


@SuppressWarnings("unchecked")
public class CaptorAnnotationBasicTest extends TestBase {
    public class Person {
        private final String name;

        private final String surname;

        public Person(String name, String surname) {
            this.name = name;
            this.surname = surname;
        }

        public String getName() {
            return name;
        }

        public String getSurname() {
            return surname;
        }
    }

    public interface PeopleRepository {
        void save(CaptorAnnotationBasicTest.Person capture);
    }

    @Mock
    CaptorAnnotationBasicTest.PeopleRepository peopleRepository;

    @Test
    public void shouldUseCaptorInOrdinaryWay() {
        // when
        createPerson("Wes", "Williams");
        // then
        ArgumentCaptor<CaptorAnnotationBasicTest.Person> captor = ArgumentCaptor.forClass(CaptorAnnotationBasicTest.Person.class);
        Mockito.verify(peopleRepository).save(captor.capture());
        Assert.assertEquals("Wes", captor.getValue().getName());
        Assert.assertEquals("Williams", captor.getValue().getSurname());
    }

    @Captor
    ArgumentCaptor<CaptorAnnotationBasicTest.Person> captor;

    @Test
    public void shouldUseAnnotatedCaptor() {
        // when
        createPerson("Wes", "Williams");
        // then
        Mockito.verify(peopleRepository).save(captor.capture());
        Assert.assertEquals("Wes", captor.getValue().getName());
        Assert.assertEquals("Williams", captor.getValue().getSurname());
    }

    @SuppressWarnings("rawtypes")
    @Captor
    ArgumentCaptor genericLessCaptor;

    @Test
    public void shouldUseGenericlessAnnotatedCaptor() {
        // when
        createPerson("Wes", "Williams");
        // then
        Mockito.verify(peopleRepository).save(((CaptorAnnotationBasicTest.Person) (genericLessCaptor.capture())));
        Assert.assertEquals("Wes", ((CaptorAnnotationBasicTest.Person) (genericLessCaptor.getValue())).getName());
        Assert.assertEquals("Williams", ((CaptorAnnotationBasicTest.Person) (genericLessCaptor.getValue())).getSurname());
    }

    @Captor
    ArgumentCaptor<List<String>> genericListCaptor;

    @Mock
    IMethods mock;

    @Test
    public void shouldCaptureGenericList() {
        // given
        List<String> list = new LinkedList<String>();
        mock.listArgMethod(list);
        // when
        Mockito.verify(mock).listArgMethod(genericListCaptor.capture());
        // then
        Assert.assertSame(list, genericListCaptor.getValue());
    }
}

