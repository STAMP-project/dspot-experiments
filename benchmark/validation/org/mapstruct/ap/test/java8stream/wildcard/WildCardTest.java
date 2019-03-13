/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.java8stream.wildcard;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;


/**
 *
 *
 * @author Filip Hrisafov
 */
@IssueKey("962")
@RunWith(AnnotationProcessorTestRunner.class)
public class WildCardTest {
    @Test
    @WithClasses({ ExtendsBoundSourceTargetMapper.class, ExtendsBoundSource.class, Target.class, Plan.class, Idea.class })
    public void shouldGenerateExtendsBoundSourceForgedStreamMethod() {
        ExtendsBoundSource source = new ExtendsBoundSource();
        Target target = ExtendsBoundSourceTargetMapper.STM.map(source);
        assertThat(target).isNotNull();
        assertThat(target.getElements()).isNull();
        assertThat(target.getListElements()).isNull();
    }

    @Test
    @WithClasses({ SourceSuperBoundTargetMapper.class, Source.class, SuperBoundTarget.class, Plan.class, Idea.class })
    public void shouldGenerateSuperBoundTargetForgedIterableMethod() {
        Source source = new Source();
        SuperBoundTarget target = SourceSuperBoundTargetMapper.STM.map(source);
        assertThat(target).isNotNull();
        assertThat(target.getElements()).isNull();
        assertThat(target.getListElements()).isNull();
    }
}

