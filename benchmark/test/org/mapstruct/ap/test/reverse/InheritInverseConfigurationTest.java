/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.reverse;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;


/**
 *
 *
 * @author Sjaak Derksen
 */
@IssueKey("252")
@WithClasses({ Source.class, Target.class })
@RunWith(AnnotationProcessorTestRunner.class)
public class InheritInverseConfigurationTest {
    @Test
    @WithClasses({ SourceTargetMapper.class })
    public void shouldInheritInverseConfigurationMultipleCandidates() {
        Source source = new Source();
        source.setPropertyToIgnoreDownstream("propToIgnoreDownStream");
        source.setStringPropX("1");
        source.setIntegerPropX(2);
        Target target = SourceTargetMapper.INSTANCE.forward(source);
        assertThat(target).isNotNull();
        assertThat(target.getStringPropY()).isEqualTo("1");
        assertThat(target.getIntegerPropY()).isEqualTo(2);
        assertThat(target.getPropertyNotToIgnoreUpstream()).isEqualTo("propToIgnoreDownStream");
        source = SourceTargetMapper.INSTANCE.reverse(target);
        assertThat(source).isNotNull();
        assertThat(source.getStringPropX()).isEqualTo("1");
        assertThat(source.getIntegerPropX()).isEqualTo(2);
        assertThat(source.getSomeConstantDownstream()).isEqualTo("test");
        assertThat(source.getPropertyToIgnoreDownstream()).isNull();
    }
}

