/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.verification;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.WantedButNotInvoked;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class OrdinaryVerificationPrintsAllInteractionsTest extends TestBase {
    @Mock
    private IMethods mock;

    @Mock
    private IMethods mockTwo;

    @Test
    public void shouldShowAllInteractionsOnMockWhenOrdinaryVerificationFail() throws Exception {
        // given
        firstInteraction();
        secondInteraction();
        Mockito.verify(mock).otherMethod();// verify 1st interaction

        try {
            // when
            Mockito.verify(mock).simpleMethod();
            Assert.fail();
        } catch (WantedButNotInvoked e) {
            // then
            assertThat(e).hasMessageContaining("However, there were exactly 2 interactions with this mock").hasMessageContaining("firstInteraction(").hasMessageContaining("secondInteraction(");
        }
    }

    @Test
    public void shouldNotShowAllInteractionsOnDifferentMock() throws Exception {
        differentMockInteraction();
        firstInteraction();
        try {
            Mockito.verify(mock).simpleMethod();
            Assert.fail();
        } catch (WantedButNotInvoked e) {
            assertThat(e.getMessage()).contains("firstInteraction(").doesNotContain("differentMockInteraction(");
        }
    }

    @Test
    public void shouldNotShowAllInteractionsHeaderWhenNoOtherInteractions() throws Exception {
        try {
            Mockito.verify(mock).simpleMethod();
            Assert.fail();
        } catch (WantedButNotInvoked e) {
            assertThat(e).hasMessageContaining("there were zero interactions with this mock.");
        }
    }
}

