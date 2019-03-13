/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.annotation;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.mockito.exceptions.base.MockitoException;
import org.mockitoutil.TestBase;


public class CaptorAnnotationUnhappyPathTest extends TestBase {
    @Captor
    List<?> notACaptorField;

    @Test
    public void shouldFailIfCaptorHasWrongType() throws Exception {
        try {
            // when
            MockitoAnnotations.initMocks(this);
            Assert.fail();
        } catch (MockitoException e) {
            // then
            assertThat(e).hasMessageContaining("notACaptorField").hasMessageContaining("wrong type");
        }
    }
}

