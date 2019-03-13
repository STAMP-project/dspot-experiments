/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.nestedmethodcall;


import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.namespace.QName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;


/**
 * Test for the nested invocation of mapping methods.
 *
 * @author Sjaak Derksen
 */
@IssueKey("134")
@RunWith(AnnotationProcessorTestRunner.class)
public class NestedMappingMethodInvocationTest {
    public static final QName QNAME = new QName("dont-care");

    @Test
    @WithClasses({ OrderTypeToOrderDtoMapper.class, OrderDto.class, OrderDetailsDto.class, OrderDetailsType.class, OrderType.class })
    public void shouldMapViaMethodAndMethod() throws DatatypeConfigurationException {
        OrderTypeToOrderDtoMapper instance = OrderTypeToOrderDtoMapper.INSTANCE;
        OrderDto target = instance.sourceToTarget(createOrderType());
        assertThat(target).isNotNull();
        assertThat(target.getOrderNumber()).isEqualTo(5L);
        assertThat(target.getDates()).containsExactly("02.03.1999", "28.07.2004");
        assertThat(target.getOrderDetails()).isNotNull();
        assertThat(target.getOrderDetails().getName()).isEqualTo("test");
        assertThat(target.getOrderDetails().getDescription()).containsExactly("elem1", "elem2");
    }

    @Test
    @WithClasses({ SourceTypeTargetDtoMapper.class, SourceType.class, ObjectFactory.class, TargetDto.class })
    public void shouldMapViaMethodAndConversion() throws DatatypeConfigurationException {
        SourceTypeTargetDtoMapper instance = SourceTypeTargetDtoMapper.INSTANCE;
        TargetDto target = instance.sourceToTarget(createSource());
        assertThat(target).isNotNull();
        assertThat(target.getDate()).isEqualTo(new GregorianCalendar(2013, 6, 6).getTime());
    }

    @Test
    @WithClasses({ SourceTypeTargetDtoMapper.class, SourceType.class, ObjectFactory.class, TargetDto.class })
    public void shouldMapViaConversionAndMethod() throws DatatypeConfigurationException {
        SourceTypeTargetDtoMapper instance = SourceTypeTargetDtoMapper.INSTANCE;
        SourceType source = instance.targetToSource(createTarget());
        assertThat(source).isNotNull();
        assertThat(source.getDate().getValue()).isEqualTo("06.07.2013");
        assertThat(source.getDate().getName()).isEqualTo(NestedMappingMethodInvocationTest.QNAME);
    }
}

