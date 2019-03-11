package com.baeldung.value;


import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
public class ClassNotManagedBySpringIntegrationTest {
    @MockBean
    private InitializerBean initializerBean;

    @Test
    public void givenInitializerBean_whenInvokedInitClass_shouldInitialize() throws Exception {
        // given
        ClassNotManagedBySpring classNotManagedBySpring = initializerBean.initClass();
        // when
        String initializedValue = classNotManagedBySpring.getCustomVariable();
        String anotherCustomVariable = classNotManagedBySpring.getAnotherCustomVariable();
        // then
        TestCase.assertEquals("This is only sample value", initializedValue);
        TestCase.assertEquals("Another configured value", anotherCustomVariable);
    }
}

