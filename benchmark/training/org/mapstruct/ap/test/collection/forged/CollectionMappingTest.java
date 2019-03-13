/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.collection.forged;


import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.internal.util.Collections;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;


/**
 * Test for mappings between collection types,
 *
 * @author Sjaak Derksen
 */
@IssueKey("4")
@RunWith(AnnotationProcessorTestRunner.class)
public class CollectionMappingTest {
    @Test
    @WithClasses({ CollectionMapper.class, Source.class, Target.class })
    public void shouldForgeNewIterableMappingMethod() {
        Source source = new Source();
        source.setFooSet(Collections.asSet("1", "2"));
        source.publicFooSet = Collections.asSet("3", "4");
        Target target = CollectionMapper.INSTANCE.sourceToTarget(source);
        assertThat(target).isNotNull();
        assertThat(target.getFooSet()).isEqualTo(Collections.asSet(1L, 2L));
        assertThat(target.getPublicFooSet()).isEqualTo(Collections.asSet(3L, 4L));
        Source source2 = CollectionMapper.INSTANCE.targetToSource(target);
        assertThat(source2).isNotNull();
        assertThat(source2.getFooSet()).isEqualTo(Collections.asSet("1", "2"));
        assertThat(source2.publicFooSet).isEqualTo(Collections.asSet("3", "4"));
    }

    @Test
    @WithClasses({ CollectionMapper.class, Source.class, Target.class })
    public void shouldForgeNewMapMappingMethod() {
        Map<String, Long> sourceMap = ImmutableMap.<String, Long>builder().put("rabbit", 1L).build();
        Source source = new Source();
        source.setBarMap(sourceMap);
        source.publicBarMap = ImmutableMap.<String, Long>builder().put("fox", 2L).build();
        Target target = CollectionMapper.INSTANCE.sourceToTarget(source);
        assertThat(target).isNotNull();
        Map<String, String> targetMap = ImmutableMap.<String, String>builder().put("rabbit", "1").build();
        Map<String, String> targetMap2 = ImmutableMap.<String, String>builder().put("fox", "2").build();
        assertThat(target.getBarMap()).isEqualTo(targetMap);
        assertThat(target.getPublicBarMap()).isEqualTo(targetMap2);
        Source source2 = CollectionMapper.INSTANCE.targetToSource(target);
        assertThat(source2).isNotNull();
        assertThat(source2.getBarMap()).isEqualTo(sourceMap);
        assertThat(source2.publicBarMap).isEqualTo(source.publicBarMap);
    }

    @Test
    @IssueKey("640")
    @WithClasses({ CollectionMapper.class, Source.class, Target.class })
    public void shouldForgeNewIterableMappingMethodReturnNullOnNullSource() {
        Source source = new Source();
        source.setFooSet(null);
        source.publicFooSet = null;
        Target target = CollectionMapper.INSTANCE.sourceToTarget(source);
        assertThat(target).isNotNull();
        assertThat(target.getFooSet()).isNull();
        assertThat(target.getPublicFooSet()).isNull();
        Source source2 = CollectionMapper.INSTANCE.targetToSource(target);
        assertThat(source2).isNotNull();
        assertThat(source2.getFooSet()).isNull();
        assertThat(source2.publicFooSet).isNull();
    }

    @Test
    @IssueKey("640")
    @WithClasses({ CollectionMapper.class, Source.class, Target.class })
    public void shouldForgeNewMapMappingMethodReturnNullOnNullSource() {
        Source source = new Source();
        source.setBarMap(null);
        source.publicBarMap = null;
        Target target = CollectionMapper.INSTANCE.sourceToTarget(source);
        assertThat(target).isNotNull();
        assertThat(target.getBarMap()).isNull();
        assertThat(target.getPublicBarMap()).isNull();
        Source source2 = CollectionMapper.INSTANCE.targetToSource(target);
        assertThat(source2).isNotNull();
        assertThat(source2.getBarMap()).isNull();
        assertThat(source2.publicBarMap).isNull();
    }

    @Test
    @IssueKey("640")
    @WithClasses({ CollectionMapperNullValueMappingReturnDefault.class, Source.class, Target.class })
    public void shouldForgeNewIterableMappingMethodReturnEmptyOnNullSource() {
        Source source = new Source();
        source.setFooSet(null);
        source.publicFooSet = null;
        Target target = CollectionMapperNullValueMappingReturnDefault.INSTANCE.sourceToTarget(source);
        assertThat(target).isNotNull();
        assertThat(target.getFooSet()).isEmpty();
        assertThat(target.getPublicFooSet()).isEmpty();
        target.setPublicBarMap(null);
        Source source2 = CollectionMapperNullValueMappingReturnDefault.INSTANCE.targetToSource(target);
        assertThat(source2).isNotNull();
        assertThat(source2.getFooSet()).isEmpty();
        assertThat(source2.publicBarMap).isEmpty();
    }

    @Test
    @IssueKey("640")
    @WithClasses({ CollectionMapperNullValueMappingReturnDefault.class, Source.class, Target.class })
    public void shouldForgeNewMapMappingMethodReturnEmptyOnNullSource() {
        Source source = new Source();
        source.setBarMap(null);
        source.publicBarMap = null;
        Target target = CollectionMapperNullValueMappingReturnDefault.INSTANCE.sourceToTarget(source);
        assertThat(target).isNotNull();
        assertThat(target.getBarMap()).isEmpty();
        assertThat(target.getPublicBarMap()).isEmpty();
        target.setPublicBarMap(null);
        Source source2 = CollectionMapperNullValueMappingReturnDefault.INSTANCE.targetToSource(target);
        assertThat(source2).isNotNull();
        assertThat(source2.getBarMap()).isEmpty();
        assertThat(source2.publicBarMap).isEmpty();
    }
}

