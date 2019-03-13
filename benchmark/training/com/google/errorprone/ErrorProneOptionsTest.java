/**
 * Copyright 2014 The Error Prone Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.errorprone;


import ImportOrganizer.ANDROID_STATIC_FIRST_ORGANIZER;
import ImportOrganizer.ANDROID_STATIC_LAST_ORGANIZER;
import ImportOrganizer.STATIC_FIRST_ORGANIZER;
import ImportOrganizer.STATIC_LAST_ORGANIZER;
import Severity.DEFAULT;
import Severity.ERROR;
import Severity.OFF;
import Severity.WARN;
import com.google.common.collect.ImmutableMap;
import com.google.errorprone.ErrorProneOptions.Severity;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@code ErrorProneOptions}.
 *
 * @author eaftan@google.com (Eddie Aftandilian)
 */
@RunWith(JUnit4.class)
public class ErrorProneOptionsTest {
    @Test
    public void nonErrorProneFlagsPlacedInRemainingArgs() {
        String[] args = new String[]{ "-nonErrorProneFlag", "value" };
        ErrorProneOptions options = ErrorProneOptions.processArgs(args);
        assertThat(options.getRemainingArgs()).isEqualTo(args);
    }

    @Test
    public void malformedOptionThrowsProperException() {
        List<String> badArgs = // too many parts
        // no check name
        Arrays.asList("-Xep:Foo:WARN:jfkdlsdf", "-Xep:", "-Xep:Foo:FJDKFJSD");// nonexistent severity level

        badArgs.forEach(( arg) -> {
            InvalidCommandLineOptionException expected = Assert.assertThrows(InvalidCommandLineOptionException.class, () -> ErrorProneOptions.processArgs(Arrays.asList(arg)));
            assertThat(expected).hasMessageThat().contains("invalid flag");
        });
    }

    @Test
    public void handlesErrorProneSeverityFlags() {
        String[] args1 = new String[]{ "-Xep:Check1" };
        ErrorProneOptions options = ErrorProneOptions.processArgs(args1);
        Map<String, Severity> expectedSeverityMap = ImmutableMap.of("Check1", DEFAULT);
        assertThat(options.getSeverityMap()).isEqualTo(expectedSeverityMap);
        String[] args2 = new String[]{ "-Xep:Check1", "-Xep:Check2:OFF", "-Xep:Check3:WARN" };
        options = ErrorProneOptions.processArgs(args2);
        expectedSeverityMap = ImmutableMap.<String, Severity>builder().put("Check1", DEFAULT).put("Check2", OFF).put("Check3", WARN).build();
        assertThat(options.getSeverityMap()).isEqualTo(expectedSeverityMap);
    }

    @Test
    public void handlesErrorProneCustomFlags() {
        String[] args = new String[]{ "-XepOpt:Flag1", "-XepOpt:Flag2=Value2", "-XepOpt:Flag3=a,b,c" };
        ErrorProneOptions options = ErrorProneOptions.processArgs(args);
        Map<String, String> expectedFlagsMap = ImmutableMap.<String, String>builder().put("Flag1", "true").put("Flag2", "Value2").put("Flag3", "a,b,c").build();
        assertThat(options.getFlags().getFlagsMap()).isEqualTo(expectedFlagsMap);
    }

    @Test
    public void combineErrorProneFlagsWithNonErrorProneFlags() {
        String[] args = new String[]{ "-classpath", "/this/is/classpath", "-verbose", "-Xep:Check1:WARN", "-XepOpt:Check1:Flag1=Value1", "-Xep:Check2:ERROR" };
        ErrorProneOptions options = ErrorProneOptions.processArgs(args);
        String[] expectedRemainingArgs = new String[]{ "-classpath", "/this/is/classpath", "-verbose" };
        assertThat(options.getRemainingArgs()).isEqualTo(expectedRemainingArgs);
        Map<String, Severity> expectedSeverityMap = ImmutableMap.<String, Severity>builder().put("Check1", WARN).put("Check2", ERROR).build();
        assertThat(options.getSeverityMap()).isEqualTo(expectedSeverityMap);
        Map<String, String> expectedFlagsMap = ImmutableMap.of("Check1:Flag1", "Value1");
        assertThat(options.getFlags().getFlagsMap()).containsExactlyEntriesIn(expectedFlagsMap);
    }

    @Test
    public void lastSeverityFlagWins() {
        String[] args = new String[]{ "-Xep:Check1:ERROR", "-Xep:Check1:OFF" };
        ErrorProneOptions options = ErrorProneOptions.processArgs(args);
        Map<String, Severity> expectedSeverityMap = ImmutableMap.of("Check1", OFF);
        assertThat(options.getSeverityMap()).isEqualTo(expectedSeverityMap);
    }

    @Test
    public void lastCustomFlagWins() {
        String[] args = new String[]{ "-XepOpt:Flag1=First", "-XepOpt:Flag1=Second" };
        ErrorProneOptions options = ErrorProneOptions.processArgs(args);
        Map<String, String> expectedFlagsMap = ImmutableMap.of("Flag1", "Second");
        assertThat(options.getFlags().getFlagsMap()).containsExactlyEntriesIn(expectedFlagsMap);
    }

    @Test
    public void recognizesAllChecksAsWarnings() {
        ErrorProneOptions options = ErrorProneOptions.processArgs(new String[]{ "-XepAllDisabledChecksAsWarnings" });
        assertThat(options.isEnableAllChecksAsWarnings()).isTrue();
    }

    @Test
    public void recognizesDemoteErrorToWarning() {
        ErrorProneOptions options = ErrorProneOptions.processArgs(new String[]{ "-XepAllErrorsAsWarnings" });
        assertThat(options.isDropErrorsToWarnings()).isTrue();
    }

    @Test
    public void recognizesDisableAllChecks() {
        ErrorProneOptions options = ErrorProneOptions.processArgs(new String[]{ "-XepDisableAllChecks" });
        assertThat(options.isDisableAllChecks()).isTrue();
    }

    @Test
    public void recognizesCompilingTestOnlyCode() {
        ErrorProneOptions options = ErrorProneOptions.processArgs(new String[]{ "-XepCompilingTestOnlyCode" });
        assertThat(options.isTestOnlyTarget()).isTrue();
    }

    @Test
    public void recognizesVisitSuppressedCode() {
        ErrorProneOptions options = ErrorProneOptions.processArgs(new String[]{ "-XepIgnoreSuppressionAnnotations" });
        assertThat(options.isIgnoreSuppressionAnnotations()).isTrue();
    }

    @Test
    public void recognizesExcludedPaths() {
        ErrorProneOptions options = ErrorProneOptions.processArgs(new String[]{ "-XepExcludedPaths:(.*/)?(build/generated|other_output)/.*\\.java" });
        Pattern excludedPattern = options.getExcludedPattern();
        assertThat(excludedPattern).isNotNull();
        assertThat(excludedPattern.matcher("fizz/build/generated/Gen.java").matches()).isTrue();
        assertThat(excludedPattern.matcher("fizz/bazz/generated/Gen.java").matches()).isFalse();
        assertThat(excludedPattern.matcher("fizz/abuild/generated/Gen.java").matches()).isFalse();
        assertThat(excludedPattern.matcher("other_output/Gen.java").matches()).isTrue();
        assertThat(excludedPattern.matcher("foo/other_output/subdir/Gen.java").matches()).isTrue();
        assertThat(excludedPattern.matcher("foo/other_output/subdir/Gen.cpp").matches()).isFalse();
    }

    @Test
    public void recognizesPatch() {
        ErrorProneOptions options = ErrorProneOptions.processArgs(new String[]{ "-XepPatchLocation:IN_PLACE", "-XepPatchChecks:FooBar,MissingOverride" });
        assertThat(options.patchingOptions().doRefactor()).isTrue();
        assertThat(options.patchingOptions().inPlace()).isTrue();
        assertThat(options.patchingOptions().namedCheckers()).containsExactly("MissingOverride", "FooBar");
        assertThat(options.patchingOptions().customRefactorer()).isAbsent();
        options = ErrorProneOptions.processArgs(new String[]{ "-XepPatchLocation:/some/base/dir", "-XepPatchChecks:FooBar,MissingOverride" });
        assertThat(options.patchingOptions().doRefactor()).isTrue();
        assertThat(options.patchingOptions().inPlace()).isFalse();
        assertThat(options.patchingOptions().baseDirectory()).isEqualTo("/some/base/dir");
        assertThat(options.patchingOptions().namedCheckers()).containsExactly("MissingOverride", "FooBar");
        assertThat(options.patchingOptions().customRefactorer()).isAbsent();
        options = ErrorProneOptions.processArgs(new String[]{  });
        assertThat(options.patchingOptions().doRefactor()).isFalse();
    }

    @Test
    public void throwsExceptionWithBadPatchArgs() {
        Assert.assertThrows(InvalidCommandLineOptionException.class, () -> ErrorProneOptions.processArgs(new String[]{ "-XepPatchLocation:IN_PLACE" }));
        Assert.assertThrows(InvalidCommandLineOptionException.class, () -> ErrorProneOptions.processArgs(new String[]{ "-XepPatchChecks:FooBar,MissingOverride" }));
    }

    @Test
    public void recognizesRefaster() {
        ErrorProneOptions options = ErrorProneOptions.processArgs(new String[]{ "-XepPatchChecks:refaster:/foo/bar", "-XepPatchLocation:IN_PLACE" });
        assertThat(options.patchingOptions().doRefactor()).isTrue();
        assertThat(options.patchingOptions().inPlace()).isTrue();
        assertThat(options.patchingOptions().customRefactorer()).isPresent();
    }

    @Test
    public void importOrder_staticFirst() {
        ErrorProneOptions options = ErrorProneOptions.processArgs(new String[]{ "-XepPatchImportOrder:static-first" });
        assertThat(options.patchingOptions().importOrganizer()).isSameAs(STATIC_FIRST_ORGANIZER);
    }

    @Test
    public void importOrder_staticLast() {
        ErrorProneOptions options = ErrorProneOptions.processArgs(new String[]{ "-XepPatchImportOrder:static-last" });
        assertThat(options.patchingOptions().importOrganizer()).isSameAs(STATIC_LAST_ORGANIZER);
    }

    @Test
    public void importOrder_androidStaticFirst() {
        ErrorProneOptions options = ErrorProneOptions.processArgs(new String[]{ "-XepPatchImportOrder:android-static-first" });
        assertThat(options.patchingOptions().importOrganizer()).isSameAs(ANDROID_STATIC_FIRST_ORGANIZER);
    }

    @Test
    public void importOrder_androidStaticLast() {
        ErrorProneOptions options = ErrorProneOptions.processArgs(new String[]{ "-XepPatchImportOrder:android-static-last" });
        assertThat(options.patchingOptions().importOrganizer()).isSameAs(ANDROID_STATIC_LAST_ORGANIZER);
    }
}

