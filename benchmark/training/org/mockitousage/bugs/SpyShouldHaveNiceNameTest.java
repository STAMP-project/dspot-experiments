/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs;


import java.util.LinkedList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockitoutil.TestBase;


// see issue 216
public class SpyShouldHaveNiceNameTest extends TestBase {
    @Spy
    List<Integer> veryCoolSpy = new LinkedList<Integer>();

    @Test
    public void shouldPrintNiceName() {
        // when
        veryCoolSpy.add(1);
        try {
            Mockito.verify(veryCoolSpy).add(2);
            Assert.fail();
        } catch (AssertionError e) {
            Assertions.assertThat(e.getMessage()).contains("veryCoolSpy");
        }
    }
}

