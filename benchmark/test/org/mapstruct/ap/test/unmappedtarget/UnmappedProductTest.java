/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.unmappedtarget;


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
 * Tests expected diagnostics for unmapped target properties.
 *
 * @author Gunnar Morling
 */
@IssueKey("35")
@RunWith(AnnotationProcessorTestRunner.class)
public class UnmappedProductTest {
    @Test
    @WithClasses({ Source.class, Target.class, SourceTargetMapper.class })
    @ExpectedCompilationOutcome(value = CompilationResult.SUCCEEDED, diagnostics = { @Diagnostic(type = SourceTargetMapper.class, kind = WARNING, line = 16, messageRegExp = "Unmapped target property: \"bar\""), @Diagnostic(type = SourceTargetMapper.class, kind = WARNING, line = 18, messageRegExp = "Unmapped target property: \"qux\"") })
    public void shouldLeaveUnmappedTargetPropertyUnset() {
        Source source = new Source();
        source.setFoo(42L);
        Target target = SourceTargetMapper.INSTANCE.sourceToTarget(source);
        assertThat(target).isNotNull();
        assertThat(target.getFoo()).isEqualTo(42L);
        assertThat(target.getBar()).isEqualTo(0);
    }
}

