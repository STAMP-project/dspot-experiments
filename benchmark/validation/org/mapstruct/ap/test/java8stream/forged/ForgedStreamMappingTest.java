/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.java8stream.forged;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.internal.util.Collections;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;
import org.mapstruct.ap.testutil.runner.GeneratedSource;


/**
 * Test for mappings between collection and stream types,
 *
 * @author Filip Hrisafov
 */
@IssueKey("962")
@RunWith(AnnotationProcessorTestRunner.class)
public class ForgedStreamMappingTest {
    @Rule
    public final GeneratedSource generatedSource = new GeneratedSource();

    @Test
    @WithClasses({ StreamMapper.class, Source.class, Target.class })
    public void shouldForgeNewIterableMappingMethod() {
        Source source = new Source();
        source.setFooStream(Collections.asSet("1", "2").stream());
        Target target = StreamMapper.INSTANCE.sourceToTarget(source);
        assertThat(target).isNotNull();
        assertThat(target.getFooStream()).contains(1L, 2L);
        Source source2 = StreamMapper.INSTANCE.targetToSource(target);
        assertThat(source2).isNotNull();
        assertThat(source2.getFooStream()).contains("1", "2");
        generatedSource.forMapper(StreamMapper.class).content().as("Mapper should not uas addAll").doesNotContain("addAll( ").as("Mapper should not use Stream.empty()").doesNotContain("Stream.empty()");
    }

    @Test
    @WithClasses({ StreamMapper.class, Source.class, Target.class })
    public void shouldForgeNewIterableMappingMethodReturnNullOnNullSource() {
        Source source = new Source();
        source.setFooStream(null);
        source.setFooStream3(null);
        Target target = StreamMapper.INSTANCE.sourceToTarget(source);
        assertThat(target).isNotNull();
        assertThat(target.getFooStream()).isNull();
        Source source2 = StreamMapper.INSTANCE.targetToSource(target);
        assertThat(source2).isNotNull();
        assertThat(source2.getFooStream()).isNull();
        assertThat(source2.getFooStream3()).isNull();
    }

    @Test
    @WithClasses({ StreamMapperNullValueMappingReturnDefault.class, Source.class, Target.class })
    public void shouldForgeNewIterableMappingMethodReturnEmptyOnNullSource() {
        Source source = new Source();
        source.setFooStream(null);
        source.setFooStream3(null);
        Target target = StreamMapperNullValueMappingReturnDefault.INSTANCE.sourceToTarget(source);
        assertThat(target).isNotNull();
        assertThat(target.getFooStream()).isEmpty();
        assertThat(target.getFooStream3()).isEmpty();
        // The empty stream was already consumed so need to set a new one
        target.setFooStream3(null);
        Source source2 = StreamMapperNullValueMappingReturnDefault.INSTANCE.targetToSource(target);
        assertThat(source2).isNotNull();
        assertThat(source2.getFooStream()).isEmpty();
        assertThat(source2.getFooStream3()).isEmpty();
    }
}

