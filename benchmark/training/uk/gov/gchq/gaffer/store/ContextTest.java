/**
 * Copyright 2016-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.store;


import User.UNKNOWN_USER_ID;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.export.Exporter;
import uk.gov.gchq.gaffer.user.User;


public class ContextTest {
    @Test
    public void shouldConstructContextsWithTheSameUserAndGenerateDifferentJobIds() {
        // Given
        final User user = new User();
        // When
        final Context context1 = new Context(user);
        final Context context2 = new Context(user);
        // Then
        Assert.assertEquals(user, context1.getUser());
        Assert.assertEquals(user, context2.getUser());
        Assert.assertNotEquals(context1.getJobId(), context2.getJobId());
        Assert.assertTrue(context1.getExporters().isEmpty());
        Assert.assertTrue(context2.getExporters().isEmpty());
    }

    @Test
    public void shouldConstructContextWithUser() {
        // Given
        final User user = new User();
        // When
        final Context context = new Context.Builder().user(user).build();
        // Then
        Assert.assertEquals(user, context.getUser());
        Assert.assertTrue(context.getExporters().isEmpty());
    }

    @Test
    public void shouldConstructContextWithUnknownUser() {
        // Given
        // When
        final Context context = new Context();
        // Then
        Assert.assertEquals(UNKNOWN_USER_ID, context.getUser().getUserId());
    }

    @Test
    public void shouldThrowExceptionIfUserIsNull() {
        // Given
        final User user = null;
        // When / Then
        try {
            new Context(user);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals("User is required", e.getMessage());
        }
    }

    @Test
    public void shouldConstructContextWithContext() {
        // Given
        final Context context = new Context.Builder().user(new User()).build();
        final Exporter exporter = Mockito.mock(Exporter.class);
        context.addExporter(exporter);
        final OperationChain opChain = Mockito.mock(OperationChain.class);
        final OperationChain opChainClone = Mockito.mock(OperationChain.class);
        BDDMockito.given(opChain.shallowClone()).willReturn(opChainClone);
        context.setOriginalOpChain(opChain);
        context.setConfig("key", "value");
        // When
        final Context clone = new Context(context);
        // Then
        Assert.assertSame(context.getUser(), clone.getUser());
        Assert.assertNotEquals(context.getJobId(), clone.getJobId());
        Assert.assertNotSame(context.getOriginalOpChain(), clone.getOriginalOpChain());
        Assert.assertSame(opChainClone, clone.getOriginalOpChain());
        Assert.assertEquals(1, clone.getExporters().size());
        Assert.assertSame(exporter, clone.getExporters().iterator().next());
        Assert.assertEquals(context.getConfig("key"), clone.getConfig("key"));
    }

    @Test
    public void shouldAddAndGetExporter() {
        // Given
        final Exporter exporter = Mockito.mock(Exporter.class);
        final Context context = new Context();
        // When
        context.addExporter(exporter);
        // Then
        Assert.assertSame(exporter, context.getExporter(exporter.getClass()));
        Assert.assertSame(exporter, context.getExporter(Exporter.class));
    }

    @Test
    public void shouldSetAndGetOriginalOpChain() {
        // Given
        final OperationChain<?> opChain = Mockito.mock(OperationChain.class);
        final Context context = new Context();
        // When
        context.setOriginalOpChain(opChain);
        // Then
        Assert.assertSame(opChain, context.getOriginalOpChain());
    }
}

