/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.enums;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.compilation.annotation.CompilationResult;
import org.mapstruct.ap.testutil.compilation.annotation.Diagnostic;
import org.mapstruct.ap.testutil.compilation.annotation.ExpectedCompilationOutcome;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;

import static javax.tools.Diagnostic.Kind.WARNING;


/**
 * Test for the generation and invocation of enum mapping methods.
 *
 * @author Gunnar Morling
 */
@IssueKey("128")
@WithClasses({ OrderEntity.class, OrderType.class, OrderDto.class, ExternalOrderType.class })
@RunWith(AnnotationProcessorTestRunner.class)
public class EnumMappingTest {
    @Test
    @WithClasses(OrderMapper.class)
    @ExpectedCompilationOutcome(value = CompilationResult.SUCCEEDED, diagnostics = { @Diagnostic(type = OrderMapper.class, kind = WARNING, line = 28, messageRegExp = "Mapping of Enums via @Mapping is going to be removed in future versions of " + "MapStruct\\. Please use @ValueMapping instead!") })
    public void shouldGenerateEnumMappingMethod() {
        ExternalOrderType target = OrderMapper.INSTANCE.orderTypeToExternalOrderType(OrderType.B2B);
        assertThat(target).isEqualTo(ExternalOrderType.B2B);
        target = OrderMapper.INSTANCE.orderTypeToExternalOrderType(OrderType.RETAIL);
        assertThat(target).isEqualTo(ExternalOrderType.RETAIL);
    }

    @Test
    @WithClasses(OrderMapper.class)
    @ExpectedCompilationOutcome(value = CompilationResult.SUCCEEDED, diagnostics = { @Diagnostic(type = OrderMapper.class, kind = WARNING, line = 28, messageRegExp = "Mapping of Enums via @Mapping is going to be removed in future versions of " + "MapStruct\\. Please use @ValueMapping instead!") })
    public void shouldConsiderConstantMappings() {
        ExternalOrderType target = OrderMapper.INSTANCE.orderTypeToExternalOrderType(OrderType.EXTRA);
        assertThat(target).isEqualTo(ExternalOrderType.SPECIAL);
        target = OrderMapper.INSTANCE.orderTypeToExternalOrderType(OrderType.STANDARD);
        assertThat(target).isEqualTo(ExternalOrderType.DEFAULT);
        target = OrderMapper.INSTANCE.orderTypeToExternalOrderType(OrderType.NORMAL);
        assertThat(target).isEqualTo(ExternalOrderType.DEFAULT);
    }

    @Test
    @WithClasses(OrderMapper.class)
    @ExpectedCompilationOutcome(value = CompilationResult.SUCCEEDED, diagnostics = { @Diagnostic(type = OrderMapper.class, kind = WARNING, line = 28, messageRegExp = "Mapping of Enums via @Mapping is going to be removed in future versions of " + "MapStruct\\. Please use @ValueMapping instead!") })
    public void shouldInvokeEnumMappingMethodForPropertyMapping() {
        OrderEntity order = new OrderEntity();
        order.setOrderType(OrderType.EXTRA);
        OrderDto orderDto = OrderMapper.INSTANCE.orderEntityToDto(order);
        assertThat(orderDto).isNotNull();
        assertThat(orderDto.getOrderType()).isEqualTo(ExternalOrderType.SPECIAL);
    }
}

