/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.callbacks.returning;


import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;


/**
 * Test case for https://github.com/mapstruct/mapstruct/issues/469
 *
 * @author Pascal Gr?n
 */
@IssueKey("469")
@WithClasses({ Attribute.class, AttributeDto.class, Node.class, NodeDto.class, NodeMapperDefault.class, NodeMapperWithContext.class, NodeMapperContext.class, Number.class, NumberMapperDefault.class, NumberMapperContext.class, NumberMapperWithContext.class })
@RunWith(AnnotationProcessorTestRunner.class)
public class CallbacksWithReturnValuesTest {
    @Test(expected = StackOverflowError.class)
    public void mappingWithDefaultHandlingRaisesStackOverflowError() {
        Node root = CallbacksWithReturnValuesTest.buildNodes();
        NodeMapperDefault.INSTANCE.nodeToNodeDto(root);
    }

    @Test(expected = StackOverflowError.class)
    public void updatingWithDefaultHandlingRaisesStackOverflowError() {
        Node root = CallbacksWithReturnValuesTest.buildNodes();
        NodeMapperDefault.INSTANCE.nodeToNodeDto(root, new NodeDto());
    }

    @Test
    public void mappingWithContextCorrectlyResolvesCycles() {
        final AtomicReference<Integer> contextLevel = new AtomicReference<Integer>(null);
        NodeMapperContext.ContextListener contextListener = new NodeMapperContext.ContextListener() {
            @Override
            public void methodCalled(Integer level, String method, Object source, Object target) {
                contextLevel.set(level);
            }
        };
        NodeMapperContext.addContextListener(contextListener);
        try {
            Node root = CallbacksWithReturnValuesTest.buildNodes();
            NodeDto rootDto = NodeMapperWithContext.INSTANCE.nodeToNodeDto(root);
            assertThat(rootDto).isNotNull();
            assertThat(contextLevel.get()).isEqualTo(Integer.valueOf(1));
        } finally {
            NodeMapperContext.removeContextListener(contextListener);
        }
    }

    @Test
    public void numberMappingWithoutContextDoesNotUseCache() {
        Number n1 = NumberMapperDefault.INSTANCE.integerToNumber(2342);
        Number n2 = NumberMapperDefault.INSTANCE.integerToNumber(2342);
        assertThat(n1).isEqualTo(n2);
        assertThat(n1).isNotSameAs(n2);
    }

    @Test
    public void numberMappingWithContextUsesCache() {
        NumberMapperContext.putCache(new Number(2342));
        Number n1 = NumberMapperWithContext.INSTANCE.integerToNumber(2342);
        Number n2 = NumberMapperWithContext.INSTANCE.integerToNumber(2342);
        assertThat(n1).isEqualTo(n2);
        assertThat(n1).isSameAs(n2);
        NumberMapperContext.clearCache();
    }

    @Test
    public void numberMappingWithContextCallsVisitNumber() {
        Number n1 = NumberMapperWithContext.INSTANCE.integerToNumber(1234);
        Number n2 = NumberMapperWithContext.INSTANCE.integerToNumber(5678);
        assertThat(NumberMapperContext.getVisited()).isEqualTo(Arrays.asList(n1, n2));
        NumberMapperContext.clearVisited();
    }
}

