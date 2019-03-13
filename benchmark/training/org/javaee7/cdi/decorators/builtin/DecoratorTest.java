/**
 * Copyright Payara Services Limited *
 */
package org.javaee7.cdi.decorators.builtin;


import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.hamcrest.core.Is;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class DecoratorTest {
    @Inject
    private HttpServletRequest request;

    @Test
    public void test() {
        Assert.assertThat(request.getParameter("decorated"), Is.is("true"));
    }
}

