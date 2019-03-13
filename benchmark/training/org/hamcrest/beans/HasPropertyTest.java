package org.hamcrest.beans;


import org.hamcrest.AbstractMatcherTest;
import org.hamcrest.Matcher;
import org.junit.Test;


/**
 *
 *
 * @author Iain McGinniss
 * @author Nat Pryce
 * @author Steve Freeman
 * @author Tom Denley
 * @since 1.1.0
 */
public final class HasPropertyTest {
    private final HasPropertyWithValueTest.BeanWithoutInfo bean = new HasPropertyWithValueTest.BeanWithoutInfo("a bean", false);

    @Test
    public void copesWithNullsAndUnknownTypes() {
        Matcher<Object> matcher = HasProperty.hasProperty("irrelevant");
        AbstractMatcherTest.assertNullSafe(matcher);
        AbstractMatcherTest.assertUnknownTypeSafe(matcher);
    }

    @Test
    public void matchesWhenThePropertyExists() {
        AbstractMatcherTest.assertMatches(HasProperty.hasProperty("writeOnlyProperty"), bean);
    }

    @Test
    public void doesNotMatchIfPropertyDoesNotExist() {
        AbstractMatcherTest.assertDoesNotMatch(HasProperty.hasProperty("aNonExistentProp"), bean);
    }

    @Test
    public void describesItself() {
        AbstractMatcherTest.assertDescription("hasProperty(\"property\")", HasProperty.hasProperty("property"));
    }

    @Test
    public void describesAMismatch() {
        AbstractMatcherTest.assertMismatchDescription("no \"aNonExistentProp\" in <[Person: a bean]>", HasProperty.hasProperty("aNonExistentProp"), bean);
    }
}

