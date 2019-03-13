package org.junit.rules;


import java.util.ArrayList;
import java.util.List;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.runners.rules.RuleMemberValidator;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;


public class RuleMemberValidatorTest {
    private final List<Throwable> errors = new ArrayList<Throwable>();

    @Test
    public void rejectProtectedClassRule() {
        TestClass target = new TestClass(RuleMemberValidatorTest.TestWithProtectedClassRule.class);
        RuleMemberValidator.CLASS_RULE_VALIDATOR.validate(target, errors);
        assertOneErrorWithMessage("The @ClassRule 'temporaryFolder' must be public.");
    }

    public static class TestWithProtectedClassRule {
        @ClassRule
        protected static TestRule temporaryFolder = new TemporaryFolder();
    }

    @Test
    public void rejectNonStaticClassRule() {
        TestClass target = new TestClass(RuleMemberValidatorTest.TestWithNonStaticClassRule.class);
        RuleMemberValidator.CLASS_RULE_VALIDATOR.validate(target, errors);
        assertOneErrorWithMessage("The @ClassRule 'temporaryFolder' must be static.");
    }

    public static class TestWithNonStaticClassRule {
        @ClassRule
        public TestRule temporaryFolder = new TemporaryFolder();
    }

    @Test
    public void acceptStaticTestRuleThatIsAlsoClassRule() {
        TestClass target = new TestClass(RuleMemberValidatorTest.TestWithStaticClassAndTestRule.class);
        RuleMemberValidator.CLASS_RULE_VALIDATOR.validate(target, errors);
        assertNumberOfErrors(0);
    }

    public static class TestWithStaticClassAndTestRule {
        @ClassRule
        @Rule
        public static TestRule temporaryFolder = new TemporaryFolder();
    }

    @Test
    public void rejectClassRuleInNonPublicClass() {
        TestClass target = new TestClass(RuleMemberValidatorTest.NonPublicTestWithClassRule.class);
        RuleMemberValidator.CLASS_RULE_VALIDATOR.validate(target, errors);
        assertOneErrorWithMessage("The @ClassRule 'temporaryFolder' must be declared in a public class.");
    }

    static class NonPublicTestWithClassRule {
        @ClassRule
        public static TestRule temporaryFolder = new TemporaryFolder();
    }

    /**
     * If there is any property annotated with @ClassRule then it must implement
     * {@link TestRule}
     *
     * <p>This case has been added with
     * <a href="https://github.com/junit-team/junit4/issues/1019">Issue #1019</a>
     */
    @Test
    public void rejectClassRuleThatIsImplemetationOfMethodRule() {
        TestClass target = new TestClass(RuleMemberValidatorTest.TestWithClassRuleIsImplementationOfMethodRule.class);
        RuleMemberValidator.CLASS_RULE_VALIDATOR.validate(target, errors);
        assertOneErrorWithMessage("The @ClassRule 'classRule' must implement TestRule.");
    }

    public static class TestWithClassRuleIsImplementationOfMethodRule {
        @ClassRule
        public static MethodRule classRule = new MethodRule() {
            public Statement apply(Statement base, FrameworkMethod method, Object target) {
                return base;
            }
        };
    }

    /**
     * If there is any method annotated with @ClassRule then it must return an
     * implementation of {@link TestRule}
     *
     * <p>This case has been added with
     * <a href="https://github.com/junit-team/junit4/issues/1019">Issue #1019</a>
     */
    @Test
    public void rejectClassRuleThatReturnsImplementationOfMethodRule() {
        TestClass target = new TestClass(RuleMemberValidatorTest.TestWithClassRuleMethodThatReturnsMethodRule.class);
        RuleMemberValidator.CLASS_RULE_METHOD_VALIDATOR.validate(target, errors);
        assertOneErrorWithMessage("The @ClassRule 'methodRule' must return an implementation of TestRule.");
    }

    public static class TestWithClassRuleMethodThatReturnsMethodRule {
        @ClassRule
        public static MethodRule methodRule() {
            return new MethodRule() {
                public Statement apply(Statement base, FrameworkMethod method, Object target) {
                    return base;
                }
            };
        }
    }

    /**
     * If there is any property annotated with @ClassRule then it must implement
     * {@link TestRule}
     *
     * <p>This case has been added with
     * <a href="https://github.com/junit-team/junit4/issues/1019">Issue #1019</a>
     */
    @Test
    public void rejectClassRuleIsAnArbitraryObject() throws Exception {
        TestClass target = new TestClass(RuleMemberValidatorTest.TestWithClassRuleIsAnArbitraryObject.class);
        RuleMemberValidator.CLASS_RULE_VALIDATOR.validate(target, errors);
        assertOneErrorWithMessage("The @ClassRule 'arbitraryObject' must implement TestRule.");
    }

    public static class TestWithClassRuleIsAnArbitraryObject {
        @ClassRule
        public static Object arbitraryObject = 1;
    }

    /**
     * If there is any method annotated with @ClassRule then it must return an
     * implementation of {@link TestRule}
     *
     * <p>This case has been added with
     * <a href="https://github.com/junit-team/junit4/issues/1019">Issue #1019</a>
     */
    @Test
    public void rejectClassRuleMethodReturnsAnArbitraryObject() throws Exception {
        TestClass target = new TestClass(RuleMemberValidatorTest.TestWithClassRuleMethodReturnsAnArbitraryObject.class);
        RuleMemberValidator.CLASS_RULE_METHOD_VALIDATOR.validate(target, errors);
        assertOneErrorWithMessage("The @ClassRule 'arbitraryObject' must return an implementation of TestRule.");
    }

    public static class TestWithClassRuleMethodReturnsAnArbitraryObject {
        @ClassRule
        public static Object arbitraryObject() {
            return 1;
        }
    }

    @Test
    public void acceptNonStaticTestRule() {
        TestClass target = new TestClass(RuleMemberValidatorTest.TestWithNonStaticTestRule.class);
        RuleMemberValidator.RULE_VALIDATOR.validate(target, errors);
        assertNumberOfErrors(0);
    }

    public static class TestWithNonStaticTestRule {
        @Rule
        public TestRule temporaryFolder = new TemporaryFolder();
    }

    @Test
    public void rejectStaticTestRule() {
        TestClass target = new TestClass(RuleMemberValidatorTest.TestWithStaticTestRule.class);
        RuleMemberValidator.RULE_VALIDATOR.validate(target, errors);
        assertOneErrorWithMessage("The @Rule 'temporaryFolder' must not be static or it must be annotated with @ClassRule.");
    }

    public static class TestWithStaticTestRule {
        @Rule
        public static TestRule temporaryFolder = new TemporaryFolder();
    }

    @Test
    public void rejectStaticMethodRule() {
        TestClass target = new TestClass(RuleMemberValidatorTest.TestWithStaticMethodRule.class);
        RuleMemberValidator.RULE_VALIDATOR.validate(target, errors);
        assertOneErrorWithMessage("The @Rule 'someMethodRule' must not be static.");
    }

    public static class TestWithStaticMethodRule {
        @Rule
        public static MethodRule someMethodRule = new RuleMemberValidatorTest.SomeMethodRule();
    }

    @Test
    public void acceptMethodRule() throws Exception {
        TestClass target = new TestClass(RuleMemberValidatorTest.TestWithMethodRule.class);
        RuleMemberValidator.RULE_VALIDATOR.validate(target, errors);
        assertNumberOfErrors(0);
    }

    public static class TestWithMethodRule {
        @Rule
        public MethodRule temporaryFolder = new MethodRule() {
            public Statement apply(Statement base, FrameworkMethod method, Object target) {
                return null;
            }
        };
    }

    @Test
    public void rejectArbitraryObjectWithRuleAnnotation() throws Exception {
        TestClass target = new TestClass(RuleMemberValidatorTest.TestWithArbitraryObjectWithRuleAnnotation.class);
        RuleMemberValidator.RULE_VALIDATOR.validate(target, errors);
        assertOneErrorWithMessage("The @Rule 'arbitraryObject' must implement MethodRule or TestRule.");
    }

    public static class TestWithArbitraryObjectWithRuleAnnotation {
        @Rule
        public Object arbitraryObject = 1;
    }

    @Test
    public void methodRejectProtectedClassRule() {
        TestClass target = new TestClass(RuleMemberValidatorTest.MethodTestWithProtectedClassRule.class);
        RuleMemberValidator.CLASS_RULE_METHOD_VALIDATOR.validate(target, errors);
        assertOneErrorWithMessage("The @ClassRule 'getTemporaryFolder' must be public.");
    }

    public static class MethodTestWithProtectedClassRule {
        @ClassRule
        protected static TestRule getTemporaryFolder() {
            return new TemporaryFolder();
        }
    }

    @Test
    public void methodRejectNonStaticClassRule() {
        TestClass target = new TestClass(RuleMemberValidatorTest.MethodTestWithNonStaticClassRule.class);
        RuleMemberValidator.CLASS_RULE_METHOD_VALIDATOR.validate(target, errors);
        assertOneErrorWithMessage("The @ClassRule 'getTemporaryFolder' must be static.");
    }

    public static class MethodTestWithNonStaticClassRule {
        @ClassRule
        public TestRule getTemporaryFolder() {
            return new TemporaryFolder();
        }
    }

    @Test
    public void acceptMethodStaticTestRuleThatIsAlsoClassRule() {
        TestClass target = new TestClass(RuleMemberValidatorTest.MethodTestWithStaticClassAndTestRule.class);
        RuleMemberValidator.CLASS_RULE_METHOD_VALIDATOR.validate(target, errors);
        assertNumberOfErrors(0);
    }

    public static class MethodTestWithStaticClassAndTestRule {
        @ClassRule
        @Rule
        public static TestRule getTemporaryFolder() {
            return new TemporaryFolder();
        }
    }

    @Test
    public void acceptMethodNonStaticTestRule() {
        TestClass target = new TestClass(RuleMemberValidatorTest.TestMethodWithNonStaticTestRule.class);
        RuleMemberValidator.RULE_METHOD_VALIDATOR.validate(target, errors);
        assertNumberOfErrors(0);
    }

    public static class TestMethodWithNonStaticTestRule {
        @Rule
        public TestRule getTemporaryFolder() {
            return new TemporaryFolder();
        }
    }

    @Test
    public void rejectMethodStaticTestRule() {
        TestClass target = new TestClass(RuleMemberValidatorTest.TestMethodWithStaticTestRule.class);
        RuleMemberValidator.RULE_METHOD_VALIDATOR.validate(target, errors);
        assertOneErrorWithMessage("The @Rule 'getTemporaryFolder' must not be static or it must be annotated with @ClassRule.");
    }

    public static class TestMethodWithStaticTestRule {
        @Rule
        public static TestRule getTemporaryFolder() {
            return new TemporaryFolder();
        }
    }

    @Test
    public void rejectMethodStaticMethodRule() {
        TestClass target = new TestClass(RuleMemberValidatorTest.TestMethodWithStaticMethodRule.class);
        RuleMemberValidator.RULE_METHOD_VALIDATOR.validate(target, errors);
        assertOneErrorWithMessage("The @Rule 'getSomeMethodRule' must not be static.");
    }

    public static class TestMethodWithStaticMethodRule {
        @Rule
        public static MethodRule getSomeMethodRule() {
            return new RuleMemberValidatorTest.SomeMethodRule();
        }
    }

    @Test
    public void methodAcceptMethodRuleMethod() throws Exception {
        TestClass target = new TestClass(RuleMemberValidatorTest.MethodTestWithMethodRule.class);
        RuleMemberValidator.RULE_METHOD_VALIDATOR.validate(target, errors);
        assertNumberOfErrors(0);
    }

    public static class MethodTestWithMethodRule {
        @Rule
        public MethodRule getTemporaryFolder() {
            return new MethodRule() {
                public Statement apply(Statement base, FrameworkMethod method, Object target) {
                    return null;
                }
            };
        }
    }

    @Test
    public void methodRejectArbitraryObjectWithRuleAnnotation() throws Exception {
        TestClass target = new TestClass(RuleMemberValidatorTest.MethodTestWithArbitraryObjectWithRuleAnnotation.class);
        RuleMemberValidator.RULE_METHOD_VALIDATOR.validate(target, errors);
        assertOneErrorWithMessage("The @Rule 'getArbitraryObject' must return an implementation of MethodRule or TestRule.");
    }

    public static class MethodTestWithArbitraryObjectWithRuleAnnotation {
        @Rule
        public Object getArbitraryObject() {
            return 1;
        }
    }

    private static final class SomeMethodRule implements MethodRule {
        public Statement apply(Statement base, FrameworkMethod method, Object target) {
            return base;
        }
    }
}

