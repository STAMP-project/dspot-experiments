/**
 * Copyright 2011 The Error Prone Authors.
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
package com.google.errorprone.fixes;


import Position.NOPOS;
import com.sun.tools.javac.tree.EndPosTable;
import com.sun.tools.javac.tree.JCTree;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
@RunWith(JUnit4.class)
public class AppliedFixTest {
    final EndPosTable endPositions = new EndPosTable() {
        final Map<JCTree, Integer> map = new HashMap<>();

        @Override
        public void storeEnd(JCTree tree, int endpos) {
            map.put(tree, endpos);
        }

        @Override
        public int replaceTree(JCTree oldtree, JCTree newtree) {
            Integer endpos = map.getOrDefault(oldtree, NOPOS);
            map.put(newtree, endpos);
            return endpos;
        }

        @Override
        public int getEndPos(JCTree tree) {
            Integer result = map.getOrDefault(tree, NOPOS);
            return result;
        }
    };

    @Test
    public void shouldApplySingleFixOnALine() {
        JCTree node = node(11, 14);
        AppliedFix fix = AppliedFix.fromSource("import org.me.B;", endPositions).apply(SuggestedFix.delete(node));
        Assert.assertThat(fix.getNewCodeSnippet().toString(), CoreMatchers.equalTo("import org.B;"));
    }

    @Test
    public void shouldReportOnlyTheChangedLineInNewSnippet() {
        JCTree node = node(25, 26);
        AppliedFix fix = AppliedFix.fromSource(("public class Foo {\n" + ("  int 3;\n" + "}")), endPositions).apply(SuggestedFix.builder().prefixWith(node, "three").postfixWith(node, "tres").build());
        Assert.assertThat(fix.getNewCodeSnippet().toString()).isEqualTo("int three3tres;");
    }

    @Test
    public void shouldReturnNullOnEmptyFix() {
        AppliedFix fix = AppliedFix.fromSource("public class Foo {}", endPositions).apply(SuggestedFix.builder().build());
        Assert.assertThat(fix).isNull();
    }

    @Test
    public void shouldReturnNullOnImportOnlyFix() {
        AppliedFix fix = AppliedFix.fromSource("public class Foo {}", endPositions).apply(SuggestedFix.builder().addImport("foo.bar.Baz").build());
        Assert.assertThat(fix).isNull();
    }

    @Test
    public void shouldThrowExceptionOnIllegalRange() {
        Assert.assertThrows(IllegalArgumentException.class, () -> SuggestedFix.replace(0, (-1), ""));
        Assert.assertThrows(IllegalArgumentException.class, () -> SuggestedFix.replace((-1), (-1), ""));
        Assert.assertThrows(IllegalArgumentException.class, () -> SuggestedFix.replace((-1), 1, ""));
    }

    @Test
    public void shouldSuggestToRemoveLastLineIfAsked() {
        JCTree node = node(21, 42);
        AppliedFix fix = AppliedFix.fromSource(("package com.example;\n" + "import java.util.Map;\n"), endPositions).apply(SuggestedFix.delete(node));
        Assert.assertThat(fix.getNewCodeSnippet().toString(), CoreMatchers.equalTo("to remove this line"));
    }

    @Test
    public void shouldApplyFixesInReverseOrder() {
        // Have to use a mock Fix here in order to intentionally return Replacements in wrong order.
        Set<Replacement> replacements = new LinkedHashSet<>();
        replacements.add(Replacement.create(0, 1, ""));
        replacements.add(Replacement.create(1, 1, ""));
        Fix mockFix = Mockito.mock(Fix.class);
        Mockito.when(mockFix.getReplacements(ArgumentMatchers.any())).thenReturn(replacements);
        // If the fixes had been applied in the wrong order, this would fail.
        // But it succeeds, so they were applied in the right order.
        AppliedFix.fromSource(" ", endPositions).apply(mockFix);
    }

    @Test
    public void shouldThrowIfReplacementOutsideSource() {
        AppliedFix.Applier applier = AppliedFix.fromSource("Hello", endPositions);
        SuggestedFix fix = SuggestedFix.replace(0, 6, "World!");
        Assert.assertThrows(IllegalArgumentException.class, () -> applier.apply(fix));
    }
}

