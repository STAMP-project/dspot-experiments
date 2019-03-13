/**
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.core.env;


import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.util.StringUtils;


/**
 * Tests for {@link Profiles}.
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 * @author Sam Brannen
 * @since 5.1
 */
public class ProfilesTests {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void ofWhenNullThrowsException() {
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage("Must specify at least one profile");
        Profiles.of(((String[]) (null)));
    }

    @Test
    public void ofWhenEmptyThrowsException() {
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage("Must specify at least one profile");
        Profiles.of();
    }

    @Test
    public void ofNullElement() {
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage("must contain text");
        Profiles.of(((String) (null)));
    }

    @Test
    public void ofEmptyElement() {
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage("must contain text");
        Profiles.of("  ");
    }

    @Test
    public void ofSingleElement() {
        Profiles profiles = Profiles.of("spring");
        Assert.assertTrue(profiles.matches(ProfilesTests.activeProfiles("spring")));
        Assert.assertFalse(profiles.matches(ProfilesTests.activeProfiles("framework")));
    }

    @Test
    public void ofSingleInvertedElement() {
        Profiles profiles = Profiles.of("!spring");
        Assert.assertFalse(profiles.matches(ProfilesTests.activeProfiles("spring")));
        Assert.assertTrue(profiles.matches(ProfilesTests.activeProfiles("framework")));
    }

    @Test
    public void ofMultipleElements() {
        Profiles profiles = Profiles.of("spring", "framework");
        Assert.assertTrue(profiles.matches(ProfilesTests.activeProfiles("spring")));
        Assert.assertTrue(profiles.matches(ProfilesTests.activeProfiles("framework")));
        Assert.assertFalse(profiles.matches(ProfilesTests.activeProfiles("java")));
    }

    @Test
    public void ofMultipleElementsWithInverted() {
        Profiles profiles = Profiles.of("!spring", "framework");
        Assert.assertFalse(profiles.matches(ProfilesTests.activeProfiles("spring")));
        Assert.assertTrue(profiles.matches(ProfilesTests.activeProfiles("spring", "framework")));
        Assert.assertTrue(profiles.matches(ProfilesTests.activeProfiles("framework")));
        Assert.assertTrue(profiles.matches(ProfilesTests.activeProfiles("java")));
    }

    @Test
    public void ofMultipleElementsAllInverted() {
        Profiles profiles = Profiles.of("!spring", "!framework");
        Assert.assertTrue(profiles.matches(ProfilesTests.activeProfiles("spring")));
        Assert.assertTrue(profiles.matches(ProfilesTests.activeProfiles("framework")));
        Assert.assertTrue(profiles.matches(ProfilesTests.activeProfiles("java")));
        Assert.assertFalse(profiles.matches(ProfilesTests.activeProfiles("spring", "framework")));
        Assert.assertFalse(profiles.matches(ProfilesTests.activeProfiles("spring", "framework", "java")));
    }

    @Test
    public void ofSingleExpression() {
        Profiles profiles = Profiles.of("(spring)");
        Assert.assertTrue(profiles.matches(ProfilesTests.activeProfiles("spring")));
        Assert.assertFalse(profiles.matches(ProfilesTests.activeProfiles("framework")));
    }

    @Test
    public void ofSingleExpressionInverted() {
        Profiles profiles = Profiles.of("!(spring)");
        Assert.assertFalse(profiles.matches(ProfilesTests.activeProfiles("spring")));
        Assert.assertTrue(profiles.matches(ProfilesTests.activeProfiles("framework")));
    }

    @Test
    public void ofSingleInvertedExpression() {
        Profiles profiles = Profiles.of("(!spring)");
        Assert.assertFalse(profiles.matches(ProfilesTests.activeProfiles("spring")));
        Assert.assertTrue(profiles.matches(ProfilesTests.activeProfiles("framework")));
    }

    @Test
    public void ofOrExpression() {
        Profiles profiles = Profiles.of("(spring | framework)");
        assertOrExpression(profiles);
    }

    @Test
    public void ofOrExpressionWithoutSpaces() {
        Profiles profiles = Profiles.of("(spring|framework)");
        assertOrExpression(profiles);
    }

    @Test
    public void ofAndExpression() {
        Profiles profiles = Profiles.of("(spring & framework)");
        assertAndExpression(profiles);
    }

    @Test
    public void ofAndExpressionWithoutSpaces() {
        Profiles profiles = Profiles.of("spring&framework)");
        assertAndExpression(profiles);
    }

    @Test
    public void ofAndExpressionWithoutParentheses() {
        Profiles profiles = Profiles.of("spring & framework");
        assertAndExpression(profiles);
    }

    @Test
    public void ofNotAndExpression() {
        Profiles profiles = Profiles.of("!(spring & framework)");
        assertOfNotAndExpression(profiles);
    }

    @Test
    public void ofNotAndExpressionWithoutSpaces() {
        Profiles profiles = Profiles.of("!(spring&framework)");
        assertOfNotAndExpression(profiles);
    }

    @Test
    public void ofAndExpressionWithInvertedSingleElement() {
        Profiles profiles = Profiles.of("!spring & framework");
        assertOfAndExpressionWithInvertedSingleElement(profiles);
    }

    @Test
    public void ofAndExpressionWithInBracketsInvertedSingleElement() {
        Profiles profiles = Profiles.of("(!spring) & framework");
        assertOfAndExpressionWithInvertedSingleElement(profiles);
    }

    @Test
    public void ofAndExpressionWithInvertedSingleElementInBrackets() {
        Profiles profiles = Profiles.of("! (spring) & framework");
        assertOfAndExpressionWithInvertedSingleElement(profiles);
    }

    @Test
    public void ofAndExpressionWithInvertedSingleElementInBracketsWithoutSpaces() {
        Profiles profiles = Profiles.of("!(spring)&framework");
        assertOfAndExpressionWithInvertedSingleElement(profiles);
    }

    @Test
    public void ofAndExpressionWithInvertedSingleElementWithoutSpaces() {
        Profiles profiles = Profiles.of("!spring&framework");
        assertOfAndExpressionWithInvertedSingleElement(profiles);
    }

    @Test
    public void ofOrExpressionWithInvertedSingleElementWithoutSpaces() {
        Profiles profiles = Profiles.of("!spring|framework");
        assertOfOrExpressionWithInvertedSingleElement(profiles);
    }

    @Test
    public void ofNotOrExpression() {
        Profiles profiles = Profiles.of("!(spring | framework)");
        assertOfNotOrExpression(profiles);
    }

    @Test
    public void ofNotOrExpressionWithoutSpaces() {
        Profiles profiles = Profiles.of("!(spring|framework)");
        assertOfNotOrExpression(profiles);
    }

    @Test
    public void ofComplexExpression() {
        Profiles profiles = Profiles.of("(spring & framework) | (spring & java)");
        assertComplexExpression(profiles);
    }

    @Test
    public void ofComplexExpressionWithoutSpaces() {
        Profiles profiles = Profiles.of("(spring&framework)|(spring&java)");
        assertComplexExpression(profiles);
    }

    @Test
    public void malformedExpressions() {
        assertMalformed(() -> Profiles.of("("));
        assertMalformed(() -> Profiles.of(")"));
        assertMalformed(() -> Profiles.of("a & b | c"));
    }

    @Test
    public void sensibleToString() {
        Assert.assertEquals("spring & framework or java | kotlin", Profiles.of("spring & framework", "java | kotlin").toString());
    }

    private static class MockActiveProfiles implements Predicate<String> {
        private final List<String> activeProfiles;

        MockActiveProfiles(String[] activeProfiles) {
            this.activeProfiles = Arrays.asList(activeProfiles);
        }

        @Override
        public boolean test(String profile) {
            // The following if-condition (which basically mimics
            // AbstractEnvironment#validateProfile(String)) is necessary in order
            // to ensure that the Profiles implementation returned by Profiles.of()
            // never passes an invalid (parsed) profile name to the active profiles
            // predicate supplied to Profiles#matches(Predicate<String>).
            if ((!(StringUtils.hasText(profile))) || ((profile.charAt(0)) == '!')) {
                throw new IllegalArgumentException((("Invalid profile [" + profile) + "]"));
            }
            return this.activeProfiles.contains(profile);
        }
    }
}

