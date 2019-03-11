/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.core.issue.tracking;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.rule.RuleKey;

import static org.sonar.api.issue.Issue.STATUS_OPEN;


public class TrackerTest {
    public static final RuleKey RULE_SYSTEM_PRINT = RuleKey.of("java", "SystemPrint");

    public static final RuleKey RULE_UNUSED_LOCAL_VARIABLE = RuleKey.of("java", "UnusedLocalVariable");

    public static final RuleKey RULE_UNUSED_PRIVATE_METHOD = RuleKey.of("java", "UnusedPrivateMethod");

    public static final RuleKey RULE_NOT_DESIGNED_FOR_EXTENSION = RuleKey.of("java", "NotDesignedForExtension");

    public static final RuleKey RULE_USE_DIAMOND = RuleKey.of("java", "UseDiamond");

    public static final RuleKey RULE_MISSING_PACKAGE_INFO = RuleKey.of("java", "MissingPackageInfo");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    Tracker<TrackerTest.Issue, TrackerTest.Issue> tracker = new Tracker();

    /**
     * Of course rule must match
     */
    @Test
    public void similar_issues_except_rule_do_not_match() {
        TrackerTest.FakeInput baseInput = new TrackerTest.FakeInput("H1");
        baseInput.createIssueOnLine(1, TrackerTest.RULE_SYSTEM_PRINT, "msg");
        TrackerTest.FakeInput rawInput = new TrackerTest.FakeInput("H1");
        TrackerTest.Issue raw = rawInput.createIssueOnLine(1, TrackerTest.RULE_UNUSED_LOCAL_VARIABLE, "msg");
        Tracking<TrackerTest.Issue, TrackerTest.Issue> tracking = tracker.trackNonClosed(rawInput, baseInput);
        assertThat(tracking.baseFor(raw)).isNull();
    }

    @Test
    public void line_hash_has_greater_priority_than_line() {
        TrackerTest.FakeInput baseInput = new TrackerTest.FakeInput("H1", "H2", "H3");
        TrackerTest.Issue base1 = baseInput.createIssueOnLine(1, TrackerTest.RULE_SYSTEM_PRINT, "msg");
        TrackerTest.Issue base2 = baseInput.createIssueOnLine(3, TrackerTest.RULE_SYSTEM_PRINT, "msg");
        TrackerTest.FakeInput rawInput = new TrackerTest.FakeInput("a", "b", "H1", "H2", "H3");
        TrackerTest.Issue raw1 = rawInput.createIssueOnLine(3, TrackerTest.RULE_SYSTEM_PRINT, "msg");
        TrackerTest.Issue raw2 = rawInput.createIssueOnLine(5, TrackerTest.RULE_SYSTEM_PRINT, "msg");
        Tracking<TrackerTest.Issue, TrackerTest.Issue> tracking = tracker.trackNonClosed(rawInput, baseInput);
        assertThat(tracking.baseFor(raw1)).isSameAs(base1);
        assertThat(tracking.baseFor(raw2)).isSameAs(base2);
    }

    /**
     * SONAR-2928
     */
    @Test
    public void no_lines_and_different_messages_match() {
        TrackerTest.FakeInput baseInput = new TrackerTest.FakeInput("H1", "H2", "H3");
        TrackerTest.Issue base = baseInput.createIssue(TrackerTest.RULE_SYSTEM_PRINT, "msg1");
        TrackerTest.FakeInput rawInput = new TrackerTest.FakeInput("H10", "H11", "H12");
        TrackerTest.Issue raw = rawInput.createIssue(TrackerTest.RULE_SYSTEM_PRINT, "msg2");
        Tracking<TrackerTest.Issue, TrackerTest.Issue> tracking = tracker.trackNonClosed(rawInput, baseInput);
        assertThat(tracking.baseFor(raw)).isSameAs(base);
    }

    @Test
    public void similar_issues_except_message_match() {
        TrackerTest.FakeInput baseInput = new TrackerTest.FakeInput("H1");
        TrackerTest.Issue base = baseInput.createIssueOnLine(1, TrackerTest.RULE_SYSTEM_PRINT, "msg1");
        TrackerTest.FakeInput rawInput = new TrackerTest.FakeInput("H1");
        TrackerTest.Issue raw = rawInput.createIssueOnLine(1, TrackerTest.RULE_SYSTEM_PRINT, "msg2");
        Tracking<TrackerTest.Issue, TrackerTest.Issue> tracking = tracker.trackNonClosed(rawInput, baseInput);
        assertThat(tracking.baseFor(raw)).isSameAs(base);
    }

    @Test
    public void similar_issues_if_trimmed_messages_match() {
        TrackerTest.FakeInput baseInput = new TrackerTest.FakeInput("H1");
        TrackerTest.Issue base = baseInput.createIssueOnLine(1, TrackerTest.RULE_SYSTEM_PRINT, "   message  ");
        TrackerTest.FakeInput rawInput = new TrackerTest.FakeInput("H2");
        TrackerTest.Issue raw = rawInput.createIssueOnLine(1, TrackerTest.RULE_SYSTEM_PRINT, "message");
        Tracking<TrackerTest.Issue, TrackerTest.Issue> tracking = tracker.trackNonClosed(rawInput, baseInput);
        assertThat(tracking.baseFor(raw)).isSameAs(base);
    }

    /**
     * Source code of this line was changed, but line and message still match
     */
    @Test
    public void similar_issues_except_line_hash_match() {
        TrackerTest.FakeInput baseInput = new TrackerTest.FakeInput("H1");
        TrackerTest.Issue base = baseInput.createIssueOnLine(1, TrackerTest.RULE_SYSTEM_PRINT, "msg");
        TrackerTest.FakeInput rawInput = new TrackerTest.FakeInput("H2");
        TrackerTest.Issue raw = rawInput.createIssueOnLine(1, TrackerTest.RULE_SYSTEM_PRINT, "msg");
        Tracking<TrackerTest.Issue, TrackerTest.Issue> tracking = tracker.trackNonClosed(rawInput, baseInput);
        assertThat(tracking.baseFor(raw)).isSameAs(base);
    }

    @Test
    public void similar_issues_except_line_match() {
        TrackerTest.FakeInput baseInput = new TrackerTest.FakeInput("H1", "H2");
        TrackerTest.Issue base = baseInput.createIssueOnLine(1, TrackerTest.RULE_SYSTEM_PRINT, "msg");
        TrackerTest.FakeInput rawInput = new TrackerTest.FakeInput("H2", "H1");
        TrackerTest.Issue raw = rawInput.createIssueOnLine(2, TrackerTest.RULE_SYSTEM_PRINT, "msg");
        Tracking<TrackerTest.Issue, TrackerTest.Issue> tracking = tracker.trackNonClosed(rawInput, baseInput);
        assertThat(tracking.baseFor(raw)).isSameAs(base);
    }

    /**
     * SONAR-2812
     */
    @Test
    public void only_same_line_hash_match_match() {
        TrackerTest.FakeInput baseInput = new TrackerTest.FakeInput("H1", "H2");
        TrackerTest.Issue base = baseInput.createIssueOnLine(1, TrackerTest.RULE_SYSTEM_PRINT, "msg");
        TrackerTest.FakeInput rawInput = new TrackerTest.FakeInput("H3", "H4", "H1");
        TrackerTest.Issue raw = rawInput.createIssueOnLine(3, TrackerTest.RULE_SYSTEM_PRINT, "other message");
        Tracking<TrackerTest.Issue, TrackerTest.Issue> tracking = tracker.trackNonClosed(rawInput, baseInput);
        assertThat(tracking.baseFor(raw)).isSameAs(base);
    }

    @Test
    public void do_not_fail_if_base_issue_without_line() {
        TrackerTest.FakeInput baseInput = new TrackerTest.FakeInput("H1", "H2");
        TrackerTest.Issue base = baseInput.createIssueOnLine(1, TrackerTest.RULE_SYSTEM_PRINT, "msg1");
        TrackerTest.FakeInput rawInput = new TrackerTest.FakeInput("H3", "H4", "H5");
        TrackerTest.Issue raw = rawInput.createIssue(TrackerTest.RULE_UNUSED_LOCAL_VARIABLE, "msg2");
        Tracking<TrackerTest.Issue, TrackerTest.Issue> tracking = tracker.trackNonClosed(rawInput, baseInput);
        assertThat(tracking.baseFor(raw)).isNull();
        assertThat(tracking.getUnmatchedBases()).containsOnly(base);
    }

    @Test
    public void do_not_fail_if_raw_issue_without_line() {
        TrackerTest.FakeInput baseInput = new TrackerTest.FakeInput("H1", "H2");
        TrackerTest.Issue base = baseInput.createIssue(TrackerTest.RULE_SYSTEM_PRINT, "msg1");
        TrackerTest.FakeInput rawInput = new TrackerTest.FakeInput("H3", "H4", "H5");
        TrackerTest.Issue raw = rawInput.createIssueOnLine(1, TrackerTest.RULE_UNUSED_LOCAL_VARIABLE, "msg2");
        Tracking<TrackerTest.Issue, TrackerTest.Issue> tracking = tracker.trackNonClosed(rawInput, baseInput);
        assertThat(tracking.baseFor(raw)).isNull();
        assertThat(tracking.getUnmatchedBases()).containsOnly(base);
    }

    @Test
    public void do_not_fail_if_raw_line_does_not_exist() {
        TrackerTest.FakeInput baseInput = new TrackerTest.FakeInput();
        TrackerTest.FakeInput rawInput = new TrackerTest.FakeInput("H1").addIssue(new TrackerTest.Issue(200, "H200", TrackerTest.RULE_SYSTEM_PRINT, "msg", STATUS_OPEN, new Date()));
        Tracking<TrackerTest.Issue, TrackerTest.Issue> tracking = tracker.trackNonClosed(rawInput, baseInput);
        assertThat(tracking.getUnmatchedRaws()).hasSize(1);
    }

    /**
     * SONAR-3072
     */
    @Test
    public void recognize_blocks_1() {
        TrackerTest.FakeInput baseInput = TrackerTest.FakeInput.createForSourceLines("package example1;", "", "public class Toto {", "", "    public void doSomething() {", "        // doSomething", "        }", "", "    public void doSomethingElse() {", "        // doSomethingElse", "        }", "}");
        TrackerTest.Issue base1 = baseInput.createIssueOnLine(7, TrackerTest.RULE_SYSTEM_PRINT, "Indentation");
        TrackerTest.Issue base2 = baseInput.createIssueOnLine(11, TrackerTest.RULE_SYSTEM_PRINT, "Indentation");
        TrackerTest.FakeInput rawInput = TrackerTest.FakeInput.createForSourceLines("package example1;", "", "public class Toto {", "", "    public Toto(){}", "", "    public void doSomethingNew() {", "        // doSomethingNew", "        }", "", "    public void doSomethingElseNew() {", "        // doSomethingElseNew", "        }", "", "    public void doSomething() {", "        // doSomething", "        }", "", "    public void doSomethingElse() {", "        // doSomethingElse", "        }", "}");
        TrackerTest.Issue raw1 = rawInput.createIssueOnLine(9, TrackerTest.RULE_SYSTEM_PRINT, "Indentation");
        TrackerTest.Issue raw2 = rawInput.createIssueOnLine(13, TrackerTest.RULE_SYSTEM_PRINT, "Indentation");
        TrackerTest.Issue raw3 = rawInput.createIssueOnLine(17, TrackerTest.RULE_SYSTEM_PRINT, "Indentation");
        TrackerTest.Issue raw4 = rawInput.createIssueOnLine(21, TrackerTest.RULE_SYSTEM_PRINT, "Indentation");
        Tracking<TrackerTest.Issue, TrackerTest.Issue> tracking = tracker.trackNonClosed(rawInput, baseInput);
        assertThat(tracking.baseFor(raw1)).isNull();
        assertThat(tracking.baseFor(raw2)).isNull();
        assertThat(tracking.baseFor(raw3)).isSameAs(base1);
        assertThat(tracking.baseFor(raw4)).isSameAs(base2);
        assertThat(tracking.getUnmatchedBases()).isEmpty();
    }

    // SONAR-10194
    @Test
    public void no_match_if_only_same_rulekey() {
        TrackerTest.FakeInput baseInput = TrackerTest.FakeInput.createForSourceLines("package aa;", "", "/**", " * Hello world", " *", " */", "public class App {", "", "    public static void main(String[] args) {", "", "        int magicNumber = 42;", "", ("        String s = new String(\"Very long line that does not meet our maximum 120 character line length criteria and should be wrapped to avoid SonarQube issues.\");\r\n" + "    }"), "}");
        TrackerTest.Issue base1 = baseInput.createIssueOnLine(11, RuleKey.of("squid", "S109"), "Assign this magic number 42 to a well-named constant, and use the constant instead.");
        TrackerTest.Issue base2 = baseInput.createIssueOnLine(13, RuleKey.of("squid", "S00103"), "Split this 163 characters long line (which is greater than 120 authorized).");
        TrackerTest.FakeInput rawInput = TrackerTest.FakeInput.createForSourceLines("package aa;", "", "/**", " * Hello world", " *", " */", "public class App {", "", "    public static void main(String[] args) {", "        ", "        System.out.println(\"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque vel diam purus. Curabitur ut nisi lacus....\");", "        ", "        int a = 0;", "        ", "        int x = a + 123;", "    }", "}");
        TrackerTest.Issue raw1 = rawInput.createIssueOnLine(11, RuleKey.of("squid", "S00103"), "Split this 139 characters long line (which is greater than 120 authorized).");
        TrackerTest.Issue raw2 = rawInput.createIssueOnLine(15, RuleKey.of("squid", "S109"), "Assign this magic number 123 to a well-named constant, and use the constant instead.");
        Tracking<TrackerTest.Issue, TrackerTest.Issue> tracking = tracker.trackNonClosed(rawInput, baseInput);
        assertThat(tracking.baseFor(raw1)).isNull();
        assertThat(tracking.baseFor(raw2)).isNull();
        assertThat(tracking.getUnmatchedBases()).hasSize(2);
    }

    /**
     * SONAR-3072
     */
    @Test
    public void recognize_blocks_2() {
        TrackerTest.FakeInput baseInput = TrackerTest.FakeInput.createForSourceLines("package example2;", "", "public class Toto {", "  void method1() {", "    System.out.println(\"toto\");", "  }", "}");
        TrackerTest.Issue base1 = baseInput.createIssueOnLine(5, TrackerTest.RULE_SYSTEM_PRINT, "SystemPrintln");
        TrackerTest.FakeInput rawInput = TrackerTest.FakeInput.createForSourceLines("package example2;", "", "public class Toto {", "", "  void method2() {", "    System.out.println(\"toto\");", "  }", "", "  void method1() {", "    System.out.println(\"toto\");", "  }", "", "  void method3() {", "    System.out.println(\"toto\");", "  }", "}");
        TrackerTest.Issue raw1 = rawInput.createIssueOnLine(6, TrackerTest.RULE_SYSTEM_PRINT, "SystemPrintln");
        TrackerTest.Issue raw2 = rawInput.createIssueOnLine(10, TrackerTest.RULE_SYSTEM_PRINT, "SystemPrintln");
        TrackerTest.Issue raw3 = rawInput.createIssueOnLine(14, TrackerTest.RULE_SYSTEM_PRINT, "SystemPrintln");
        Tracking<TrackerTest.Issue, TrackerTest.Issue> tracking = tracker.trackNonClosed(rawInput, baseInput);
        assertThat(tracking.baseFor(raw1)).isNull();
        assertThat(tracking.baseFor(raw2)).isSameAs(base1);
        assertThat(tracking.baseFor(raw3)).isNull();
    }

    @Test
    public void recognize_blocks_3() {
        TrackerTest.FakeInput baseInput = // UnusedLocalVariable
        // NotDesignedForExtension
        // UnusedPrivateMethod
        TrackerTest.FakeInput.createForSourceLines("package sample;", "", "public class Sample {", "\t", "\tpublic Sample(int i) {", "\t\tint j = i+1;", "\t}", "", "\tpublic boolean avoidUtilityClass() {", "\t\treturn true;", "\t}", "", "\tprivate String myMethod() {", "\t\treturn \"hello\";", "\t}", "}");
        TrackerTest.Issue base1 = baseInput.createIssueOnLine(6, TrackerTest.RULE_UNUSED_LOCAL_VARIABLE, "Avoid unused local variables such as 'j'.");
        TrackerTest.Issue base2 = baseInput.createIssueOnLine(13, TrackerTest.RULE_UNUSED_PRIVATE_METHOD, "Avoid unused private methods such as 'myMethod()'.");
        TrackerTest.Issue base3 = baseInput.createIssueOnLine(9, TrackerTest.RULE_NOT_DESIGNED_FOR_EXTENSION, "Method 'avoidUtilityClass' is not designed for extension - needs to be abstract, final or empty.");
        TrackerTest.FakeInput rawInput = // UnusedLocalVariable is still there
        // NotDesignedForExtension is still there
        // issue UnusedPrivateMethod is fixed because it's called at line 18
        // new issue UnusedLocalVariable
        TrackerTest.FakeInput.createForSourceLines("package sample;", "", "public class Sample {", "", "\tpublic Sample(int i) {", "\t\tint j = i+1;", "\t}", "\t", "\tpublic boolean avoidUtilityClass() {", "\t\treturn true;", "\t}", "\t", "\tprivate String myMethod() {", "\t\treturn \"hello\";", "\t}", "", "  public void newIssue() {", "    String msg = myMethod();", "  }", "}");
        TrackerTest.Issue newRaw = rawInput.createIssueOnLine(18, TrackerTest.RULE_UNUSED_LOCAL_VARIABLE, "Avoid unused local variables such as 'msg'.");
        TrackerTest.Issue rawSameAsBase1 = rawInput.createIssueOnLine(6, TrackerTest.RULE_UNUSED_LOCAL_VARIABLE, "Avoid unused local variables such as 'j'.");
        TrackerTest.Issue rawSameAsBase3 = rawInput.createIssueOnLine(9, TrackerTest.RULE_NOT_DESIGNED_FOR_EXTENSION, "Method 'avoidUtilityClass' is not designed for extension - needs to be abstract, final or empty.");
        Tracking<TrackerTest.Issue, TrackerTest.Issue> tracking = tracker.trackNonClosed(rawInput, baseInput);
        assertThat(tracking.baseFor(newRaw)).isNull();
        assertThat(tracking.baseFor(rawSameAsBase1)).isSameAs(base1);
        assertThat(tracking.baseFor(rawSameAsBase3)).isSameAs(base3);
        assertThat(tracking.getUnmatchedBases()).containsOnly(base2);
    }

    /**
     * https://jira.sonarsource.com/browse/SONAR-7595
     */
    @Test
    public void match_only_one_issue_when_multiple_blocks_match_the_same_block() {
        TrackerTest.FakeInput baseInput = TrackerTest.FakeInput.createForSourceLines("public class Toto {", "  private final Deque<Set<DataItem>> one = new ArrayDeque<Set<DataItem>>();", "  private final Deque<Set<DataItem>> two = new ArrayDeque<Set<DataItem>>();", "  private final Deque<Integer> three = new ArrayDeque<Integer>();", "  private final Deque<Set<Set<DataItem>>> four = new ArrayDeque<Set<DataItem>>();");
        TrackerTest.Issue base1 = baseInput.createIssueOnLine(2, TrackerTest.RULE_USE_DIAMOND, "Use diamond");
        baseInput.createIssueOnLine(3, TrackerTest.RULE_USE_DIAMOND, "Use diamond");
        baseInput.createIssueOnLine(4, TrackerTest.RULE_USE_DIAMOND, "Use diamond");
        baseInput.createIssueOnLine(5, TrackerTest.RULE_USE_DIAMOND, "Use diamond");
        TrackerTest.FakeInput rawInput = TrackerTest.FakeInput.createForSourceLines("public class Toto {", "  // move all lines", "  private final Deque<Set<DataItem>> one = new ArrayDeque<Set<DataItem>>();", "  private final Deque<Set<DataItem>> two = new ArrayDeque<>();", "  private final Deque<Integer> three = new ArrayDeque<>();", "  private final Deque<Set<Set<DataItem>>> four = new ArrayDeque<>();");
        TrackerTest.Issue raw1 = rawInput.createIssueOnLine(3, TrackerTest.RULE_USE_DIAMOND, "Use diamond");
        Tracking<TrackerTest.Issue, TrackerTest.Issue> tracking = tracker.trackNonClosed(rawInput, baseInput);
        assertThat(tracking.getUnmatchedBases()).hasSize(3);
        assertThat(tracking.baseFor(raw1)).isEqualTo(base1);
    }

    @Test
    public void match_issues_with_same_rule_key_on_project_level() {
        TrackerTest.FakeInput baseInput = new TrackerTest.FakeInput();
        TrackerTest.Issue base1 = baseInput.createIssue(TrackerTest.RULE_MISSING_PACKAGE_INFO, "[com.test:abc] Missing package-info.java in package.");
        TrackerTest.Issue base2 = baseInput.createIssue(TrackerTest.RULE_MISSING_PACKAGE_INFO, "[com.test:abc/def] Missing package-info.java in package.");
        TrackerTest.FakeInput rawInput = new TrackerTest.FakeInput();
        TrackerTest.Issue raw1 = rawInput.createIssue(TrackerTest.RULE_MISSING_PACKAGE_INFO, "[com.test:abc/def] Missing package-info.java in package.");
        TrackerTest.Issue raw2 = rawInput.createIssue(TrackerTest.RULE_MISSING_PACKAGE_INFO, "[com.test:abc] Missing package-info.java in package.");
        Tracking<TrackerTest.Issue, TrackerTest.Issue> tracking = tracker.trackNonClosed(rawInput, baseInput);
        assertThat(tracking.getUnmatchedBases()).hasSize(0);
        assertThat(tracking.baseFor(raw1)).isEqualTo(base2);
        assertThat(tracking.baseFor(raw2)).isEqualTo(base1);
    }

    private static class Issue implements Trackable {
        private final RuleKey ruleKey;

        private final Integer line;

        private final String message;

        private final String lineHash;

        private final String status;

        private final Date creationDate;

        Issue(@Nullable
        Integer line, String lineHash, RuleKey ruleKey, String message, String status, Date creationDate) {
            this.line = line;
            this.lineHash = lineHash;
            this.ruleKey = ruleKey;
            this.status = status;
            this.creationDate = creationDate;
            this.message = trim(message);
        }

        @Override
        public Integer getLine() {
            return line;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public String getLineHash() {
            return lineHash;
        }

        @Override
        public RuleKey getRuleKey() {
            return ruleKey;
        }

        @Override
        public String getStatus() {
            return status;
        }

        @Override
        public Date getCreationDate() {
            return creationDate;
        }
    }

    private static class FakeInput implements Input<TrackerTest.Issue> {
        private final List<TrackerTest.Issue> issues = new ArrayList<>();

        private final List<String> lineHashes;

        FakeInput(String... lineHashes) {
            this.lineHashes = Arrays.asList(lineHashes);
        }

        static TrackerTest.FakeInput createForSourceLines(String... lines) {
            String[] hashes = new String[lines.length];
            for (int i = 0; i < (lines.length); i++) {
                hashes[i] = DigestUtils.md5Hex(lines[i].replaceAll("[\t ]", ""));
            }
            return new TrackerTest.FakeInput(hashes);
        }

        TrackerTest.Issue createIssueOnLine(int line, RuleKey ruleKey, String message) {
            TrackerTest.Issue issue = new TrackerTest.Issue(line, lineHashes.get((line - 1)), ruleKey, message, STATUS_OPEN, new Date());
            issues.add(issue);
            return issue;
        }

        /**
         * No line (line 0)
         */
        TrackerTest.Issue createIssue(RuleKey ruleKey, String message) {
            TrackerTest.Issue issue = new TrackerTest.Issue(null, "", ruleKey, message, STATUS_OPEN, new Date());
            issues.add(issue);
            return issue;
        }

        TrackerTest.FakeInput addIssue(TrackerTest.Issue issue) {
            this.issues.add(issue);
            return this;
        }

        @Override
        public LineHashSequence getLineHashSequence() {
            return new LineHashSequence(lineHashes);
        }

        @Override
        public BlockHashSequence getBlockHashSequence() {
            return new BlockHashSequence(getLineHashSequence(), 2);
        }

        @Override
        public Collection<TrackerTest.Issue> getIssues() {
            return issues;
        }
    }
}

