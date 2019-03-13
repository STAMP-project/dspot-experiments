package org.hamcrest.core;


import org.hamcrest.AbstractMatcherTest;
import org.hamcrest.Matcher;
import org.junit.Test;


public final class CombinableTest {
    private static final CombinableMatcher<Integer> EITHER_3_OR_4 = CombinableMatcher.either(IsEqual.equalTo(3)).or(IsEqual.equalTo(4));

    private static final CombinableMatcher<Integer> NOT_3_AND_NOT_4 = CombinableMatcher.both(IsNot.not(IsEqual.equalTo(3))).and(IsNot.not(IsEqual.equalTo(4)));

    @Test
    public void copesWithNullsAndUnknownTypes() {
        AbstractMatcherTest.assertNullSafe(CombinableTest.EITHER_3_OR_4);
        AbstractMatcherTest.assertNullSafe(CombinableTest.NOT_3_AND_NOT_4);
        AbstractMatcherTest.assertUnknownTypeSafe(CombinableTest.EITHER_3_OR_4);
        AbstractMatcherTest.assertUnknownTypeSafe(CombinableTest.NOT_3_AND_NOT_4);
    }

    @Test
    public void bothAcceptsAndRejects() {
        AbstractMatcherTest.assertMatches("both didn't pass", CombinableTest.NOT_3_AND_NOT_4, 2);
        AbstractMatcherTest.assertDoesNotMatch("both didn't fail", CombinableTest.NOT_3_AND_NOT_4, 3);
    }

    @Test
    public void acceptsAndRejectsThreeAnds() {
        CombinableMatcher<? super Integer> tripleAnd = CombinableTest.NOT_3_AND_NOT_4.and(IsEqual.equalTo(2));
        AbstractMatcherTest.assertMatches("tripleAnd didn't pass", tripleAnd, 2);
        AbstractMatcherTest.assertDoesNotMatch("tripleAnd didn't fail", tripleAnd, 3);
    }

    @Test
    public void bothDescribesItself() {
        AbstractMatcherTest.assertDescription("(not <3> and not <4>)", CombinableTest.NOT_3_AND_NOT_4);
        AbstractMatcherTest.assertMismatchDescription("not <3> was <3>", CombinableTest.NOT_3_AND_NOT_4, 3);
    }

    @Test
    public void eitherAcceptsAndRejects() {
        AbstractMatcherTest.assertMatches("either didn't pass", CombinableTest.EITHER_3_OR_4, 3);
        AbstractMatcherTest.assertDoesNotMatch("either didn't fail", CombinableTest.EITHER_3_OR_4, 6);
    }

    @Test
    public void acceptsAndRejectsThreeOrs() {
        final CombinableMatcher<Integer> tripleOr = CombinableTest.EITHER_3_OR_4.or(IsEqual.equalTo(11));
        AbstractMatcherTest.assertMatches("tripleOr didn't pass", tripleOr, 11);
        AbstractMatcherTest.assertDoesNotMatch("tripleOr didn't fail", tripleOr, 9);
    }

    @Test
    public void eitherDescribesItself() {
        AbstractMatcherTest.assertDescription("(<3> or <4>)", CombinableTest.EITHER_3_OR_4);
        AbstractMatcherTest.assertMismatchDescription("was <6>", CombinableTest.EITHER_3_OR_4, 6);
    }

    @Test
    public void picksUpTypeFromLeftHandSideOfExpression() {
        @SuppressWarnings("unused")
        Matcher<String> matcher = CombinableMatcher.both(IsEqual.equalTo("yellow")).and(IsNull.notNullValue(String.class));
    }
}

