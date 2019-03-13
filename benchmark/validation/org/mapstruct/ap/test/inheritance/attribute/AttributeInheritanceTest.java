/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.inheritance.attribute;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;


/**
 * Test for setting an attribute where the target attribute of a super-type.
 *
 * @author Gunnar Morling
 */
@RunWith(AnnotationProcessorTestRunner.class)
public class AttributeInheritanceTest {
    @Test
    @WithClasses({ Source.class, Target.class, SourceTargetMapper.class })
    public void shouldMapAttributeFromSuperType() {
        Source source = new Source();
        source.setFoo("Bob");
        Target target = SourceTargetMapper.INSTANCE.sourceToTarget(source);
        assertThat(target.getFoo()).isNotNull();
        assertThat(target.getFoo().toString()).isEqualTo("Bob");
    }
}

