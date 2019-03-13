/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.inheritance.complex;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;


/**
 * Test for propagation of attributes inherited from super types.
 *
 * @author Andreas Gudian
 */
@WithClasses({ Reference.class, SourceBase.class, SourceComposite.class, SourceExt.class, SourceExt2.class, TargetComposite.class, AdditionalFooSource.class })
@RunWith(AnnotationProcessorTestRunner.class)
public class ComplexInheritanceTest {
    @Test
    @IssueKey("34")
    @WithClasses({ StandaloneSourceCompositeTargetCompositeMapper.class })
    public void shouldMapAttributesWithSuperTypeInStandaloneMapper() {
        SourceComposite source = createComposite();
        TargetComposite target = StandaloneSourceCompositeTargetCompositeMapper.INSTANCE.sourceToTarget(source);
        assertResult(target);
        assertThat(target.getProp4()).isEqualTo(999);
        assertThat(target.getProp5()).containsOnly(42, 999);
    }

    @Test
    @IssueKey("34")
    @WithClasses({ SourceCompositeTargetCompositeMapper.class, SourceBaseMappingHelper.class })
    public void shouldMapAttributesWithSuperTypeUsingOtherMapper() {
        SourceComposite source = createComposite();
        TargetComposite target = SourceCompositeTargetCompositeMapper.INSTANCE.sourceToTarget(source);
        assertResult(target);
        assertThat(target.getProp4()).isEqualTo(1000);
        assertThat(target.getProp5()).containsOnly(43, 1000);
    }
}

