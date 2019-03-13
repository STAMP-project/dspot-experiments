/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.builder.multiple;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.test.builder.multiple.build.Process;
import org.mapstruct.ap.test.builder.multiple.builder.Case;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.compilation.annotation.CompilationResult;
import org.mapstruct.ap.testutil.compilation.annotation.Diagnostic;
import org.mapstruct.ap.testutil.compilation.annotation.ExpectedCompilationOutcome;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;

import static javax.tools.Diagnostic.Kind.WARNING;


/**
 *
 *
 * @author Filip Hrisafov
 */
@RunWith(AnnotationProcessorTestRunner.class)
@IssueKey("1479")
@WithClasses({ Process.class, Case.class, Task.class, Source.class })
public class MultipleBuilderMapperTest {
    @WithClasses({ BuilderDefinedMapper.class })
    @Test
    public void builderMappingDefined() {
        Process map = BuilderDefinedMapper.INSTANCE.map(new Source("map"));
        Process wrongMap = BuilderDefinedMapper.INSTANCE.wrongMap(new Source("wrongMap"));
        assertThat(map.getBuildMethod()).isEqualTo("create");
        assertThat(wrongMap.getBuildMethod()).isEqualTo("wrongCreate");
    }

    @WithClasses({ BuilderMapperConfig.class, BuilderConfigDefinedMapper.class })
    @Test
    public void builderMappingMapperConfigDefined() {
        Process map = BuilderConfigDefinedMapper.INSTANCE.map(new Source("map"));
        Process wrongMap = BuilderConfigDefinedMapper.INSTANCE.wrongMap(new Source("wrongMap"));
        assertThat(map.getBuildMethod()).isEqualTo("create");
        assertThat(wrongMap.getBuildMethod()).isEqualTo("wrongCreate");
    }

    @WithClasses({ TooManyBuilderCreationMethodsMapper.class })
    @ExpectedCompilationOutcome(value = CompilationResult.SUCCEEDED, diagnostics = // We have 2 diagnostics, as we don't do caching of the types, so a type is processed multiple types
    { @Diagnostic(type = Case.class, kind = WARNING, line = 11, messageRegExp = "More than one builder creation method for \".*\\.multiple\\.builder.Case\"\\. " + ((("Found methods: " + "\".*wrongBuilder\\(\\) ?, ") + ".*builder\\(\\) ?\"\\. ") + "Builder will not be used\\. Consider implementing a custom BuilderProvider SPI\\.")), @Diagnostic(type = Case.class, kind = WARNING, line = 11, messageRegExp = "More than one builder creation method for \".*\\.multiple\\.builder.Case\"\\. " + ((("Found methods: " + "\".*wrongBuilder\\(\\) ?, ") + ".*builder\\(\\) ?\"\\. ") + "Builder will not be used\\. Consider implementing a custom BuilderProvider SPI\\.")) })
    @Test
    public void tooManyBuilderCreationMethods() {
        Case caseTarget = TooManyBuilderCreationMethodsMapper.INSTANCE.map(new Source("test"));
        assertThat(caseTarget).isNotNull();
        assertThat(caseTarget.getName()).isEqualTo("test");
        assertThat(caseTarget.getBuilderCreationMethod()).isNull();
        assertThat(caseTarget.getBuildMethod()).isEqualTo("constructor");
    }

    @WithClasses({ DefaultBuildMethodMapper.class })
    @Test
    public void defaultBuildMethod() {
        Task task = DefaultBuildMethodMapper.INSTANCE.map(new Source("test"));
        assertThat(task).isNotNull();
        assertThat(task.getName()).isEqualTo("test");
        assertThat(task.getBuilderCreationMethod()).isEqualTo("builder");
        assertThat(task.getBuildMethod()).isEqualTo("build");
    }
}

