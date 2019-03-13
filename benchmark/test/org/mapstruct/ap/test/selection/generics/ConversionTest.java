/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.selection.generics;


import java.math.BigDecimal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;


/**
 * Tests for the invocation of generic methods for mapping bean properties.
 *
 * @author Sjaak Derksen
 */
@WithClasses({ GenericTypeMapper.class, Wrapper.class, ArrayWrapper.class, TwoArgHolder.class, TwoArgWrapper.class, UpperBoundWrapper.class, WildCardExtendsWrapper.class, WildCardSuperWrapper.class, WildCardExtendsMBWrapper.class, TypeA.class, TypeB.class, TypeC.class })
@IssueKey("79")
@RunWith(AnnotationProcessorTestRunner.class)
public class ConversionTest {
    @Test
    @WithClasses({ Source.class, Target.class, SourceTargetMapper.class })
    public void shouldApplyGenericTypeMapper() {
        // setup used types
        TypeB typeB = new TypeB();
        TypeC typeC = new TypeC();
        // setup source
        Source source = new Source();
        source.setFooInteger(new Wrapper<Integer>(5));
        source.setFooString(new Wrapper<String>("test"));
        source.setFooStringArray(new Wrapper<String[]>(new String[]{ "test1", "test2" }));
        source.setFooLongArray(new ArrayWrapper<Long>(new Long[]{ 5L, 3L }));
        source.setFooTwoArgs(new TwoArgWrapper<Integer, Boolean>(new TwoArgHolder<Integer, Boolean>(3, true)));
        source.setFooNested(new Wrapper<Wrapper<BigDecimal>>(new Wrapper<BigDecimal>(new BigDecimal(5))));
        source.setFooUpperBoundCorrect(new UpperBoundWrapper<TypeB>(typeB));
        source.setFooWildCardExtendsString(new WildCardExtendsWrapper<String>("test3"));
        source.setFooWildCardExtendsTypeCCorrect(new WildCardExtendsWrapper<TypeC>(typeC));
        source.setFooWildCardExtendsTypeBCorrect(new WildCardExtendsWrapper<TypeB>(typeB));
        source.setFooWildCardSuperString(new WildCardSuperWrapper<String>("test4"));
        source.setFooWildCardExtendsMBTypeCCorrect(new WildCardExtendsMBWrapper<TypeC>(typeC));
        source.setFooWildCardSuperTypeBCorrect(new WildCardSuperWrapper<TypeB>(typeB));
        // define wrapper
        Target target = SourceTargetMapper.INSTANCE.sourceToTarget(source);
        // assert results
        assertThat(target).isNotNull();
        assertThat(target.getFooInteger()).isEqualTo(5);
        assertThat(target.getFooString()).isEqualTo("test");
        assertThat(target.getFooStringArray()).isEqualTo(new String[]{ "test1", "test2" });
        assertThat(target.getFooLongArray()).isEqualTo(new Long[]{ 5L, 3L });
        assertThat(target.getFooTwoArgs().getArg1()).isEqualTo(3);
        assertThat(target.getFooTwoArgs().getArg2()).isEqualTo(true);
        assertThat(target.getFooNested()).isEqualTo(new BigDecimal(5));
        assertThat(target.getFooUpperBoundCorrect()).isEqualTo(typeB);
        assertThat(target.getFooWildCardExtendsString()).isEqualTo("test3");
        assertThat(target.getFooWildCardExtendsTypeCCorrect()).isEqualTo(typeC);
        assertThat(target.getFooWildCardExtendsTypeBCorrect()).isEqualTo(typeB);
        assertThat(target.getFooWildCardSuperString()).isEqualTo("test4");
        assertThat(target.getFooWildCardExtendsMBTypeCCorrect()).isEqualTo(typeC);
        assertThat(target.getFooWildCardSuperTypeBCorrect()).isEqualTo(typeB);
    }
}

