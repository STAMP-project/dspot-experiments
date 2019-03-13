/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.abstractclass;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;


/**
 * Test for the generation of implementation of abstract base classes.
 *
 * @author Gunnar Morling
 */
@WithClasses({ Source.class, Target.class, SourceTargetMapper.class, AbstractBaseMapper.class, BaseMapperInterface.class, ReferencedMapper.class, AbstractReferencedMapper.class, ReferencedMapperInterface.class, AbstractDto.class, Identifiable.class, HasId.class, AlsoHasId.class, Measurable.class, Holder.class })
@RunWith(AnnotationProcessorTestRunner.class)
public class AbstractClassTest {
    @Test
    @IssueKey("64")
    public void shouldCreateImplementationOfAbstractMethod() {
        Source source = new Source();
        assertResult(SourceTargetMapper.INSTANCE.sourceToTarget(source));
    }

    @Test
    @IssueKey("165")
    public void shouldCreateImplementationOfMethodFromSuper() {
        Source source = new Source();
        assertResult(SourceTargetMapper.INSTANCE.sourceToTargetFromBaseMapper(source));
    }

    @Test
    @IssueKey("165")
    public void shouldCreateImplementationOfMethodFromInterface() {
        Source source = new Source();
        assertResult(SourceTargetMapper.INSTANCE.sourceToTargetFromBaseMapperInterface(source));
    }
}

