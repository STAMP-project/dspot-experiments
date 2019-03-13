/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.context;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;


/**
 * Tests the usage of the {@link Context} annotation in the following situations:
 * <ul>
 * <li>passing the parameter to property mapping methods (create and update)
 * <li>passing the parameter to forged iterable methods
 * <li>passing the parameter to forged bean mapping methods
 * <li>passing the parameter to factory methods (with and without {@link ObjectFactory})
 * <li>passing the parameter to lifecycle methods (in this case, {@link BeforeMapping}
 * <li>passing multiple parameters, with varied order of context params and mapping source params
 * <li>calling lifecycle methods on context params
 * </ul>
 *
 * @author Andreas Gudian
 */
@IssueKey("975")
@WithClasses({ Node.class, NodeDto.class, NodeMapperWithContext.class, AutomappingNodeMapperWithContext.class, AutomappingNodeMapperWithSelfContainingContext.class, CycleContext.class, FactoryContext.class, CycleContextLifecycleMethods.class, FactoryContextMethods.class, SelfContainingCycleContext.class })
@RunWith(AnnotationProcessorTestRunner.class)
public class ContextParameterTest {
    private static final int MAGIC_NUMBER_OFFSET = 10;

    @Test
    public void mappingWithContextCorrectlyResolvesCycles() {
        Node root = ContextParameterTest.buildNodes();
        NodeDto rootDto = NodeMapperWithContext.INSTANCE.nodeToNodeDto(new FactoryContext(0, 10), root, new CycleContext());
        assertResult(rootDto);
        NodeDto updated = new NodeDto(0);
        NodeMapperWithContext.INSTANCE.nodeToNodeDto(new FactoryContext(1, 10), root, updated, new CycleContext());
        assertResult(updated);
    }

    @Test
    public void automappingWithContextCorrectlyResolvesCycles() {
        Node root = ContextParameterTest.buildNodes();
        NodeDto rootDto = AutomappingNodeMapperWithContext.INSTANCE.nodeToNodeDto(root, new CycleContext(), new FactoryContext(0, ContextParameterTest.MAGIC_NUMBER_OFFSET));
        assertResult(rootDto);
        NodeDto updated = new NodeDto(0);
        AutomappingNodeMapperWithContext.INSTANCE.nodeToNodeDto(root, updated, new CycleContext(), new FactoryContext(1, 10));
        assertResult(updated);
    }

    @Test
    public void automappingWithSelfContainingContextCorrectlyResolvesCycles() {
        Node root = ContextParameterTest.buildNodes();
        NodeDto rootDto = AutomappingNodeMapperWithSelfContainingContext.INSTANCE.nodeToNodeDto(root, new SelfContainingCycleContext(), new FactoryContext(0, ContextParameterTest.MAGIC_NUMBER_OFFSET));
        assertResult(rootDto);
        NodeDto updated = new NodeDto(0);
        AutomappingNodeMapperWithSelfContainingContext.INSTANCE.nodeToNodeDto(root, updated, new SelfContainingCycleContext(), new FactoryContext(1, 10));
        assertResult(updated);
    }
}

