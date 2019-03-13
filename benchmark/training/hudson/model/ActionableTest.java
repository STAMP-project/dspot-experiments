/**
 * The MIT License
 *
 * Copyright 2014 Jesse Glick.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.model;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;


public class ActionableTest {
    private Actionable thing = new ActionableTest.ActionableImpl();

    @SuppressWarnings("deprecation")
    @Test
    public void replaceAction() {
        CauseAction a1 = new CauseAction();
        ParametersAction a2 = new ParametersAction();
        thing.addAction(a1);
        thing.addAction(a2);
        CauseAction a3 = new CauseAction();
        thing.replaceAction(a3);
        Assert.assertEquals(Arrays.asList(a2, a3), thing.getActions());
    }

    static class ActionableOverride extends Actionable {
        ArrayList<Action> specialActions = new ArrayList<Action>();

        @Override
        public String getDisplayName() {
            return "nope";
        }

        @Override
        public String getSearchUrl() {
            return "morenope";
        }

        @Override
        public List<Action> getActions() {
            return specialActions;
        }
    }

    @SuppressWarnings("deprecation")
    @Issue("JENKINS-39555")
    @Test
    public void testExtensionOverrides() throws Exception {
        ActionableTest.ActionableOverride myOverridden = new ActionableTest.ActionableOverride();
        InvisibleAction invis = new InvisibleAction() {};
        myOverridden.addAction(invis);
        Assert.assertArrayEquals(new Object[]{ invis }, myOverridden.specialActions.toArray());
        Assert.assertArrayEquals(new Object[]{ invis }, myOverridden.getActions().toArray());
        myOverridden.getActions().remove(invis);
        Assert.assertArrayEquals(new Object[]{  }, myOverridden.specialActions.toArray());
        Assert.assertArrayEquals(new Object[]{  }, myOverridden.getActions().toArray());
        myOverridden.addAction(invis);
        myOverridden.removeAction(invis);
        Assert.assertArrayEquals(new Object[]{  }, myOverridden.specialActions.toArray());
        Assert.assertArrayEquals(new Object[]{  }, myOverridden.getActions().toArray());
        InvisibleAction invis2 = new InvisibleAction() {};
        myOverridden.addOrReplaceAction(invis2);
        Assert.assertArrayEquals(new Object[]{ invis2 }, myOverridden.specialActions.toArray());
        Assert.assertArrayEquals(new Object[]{ invis2 }, myOverridden.getActions().toArray());
        myOverridden.addOrReplaceAction(invis);
        myOverridden.addOrReplaceAction(invis);
        Assert.assertArrayEquals(new Object[]{ invis2, invis }, myOverridden.specialActions.toArray());
        Assert.assertArrayEquals(new Object[]{ invis2, invis }, myOverridden.getActions().toArray());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void addOrReplaceAction() {
        CauseAction a1 = new CauseAction();
        ParametersAction a2 = new ParametersAction();
        thing.addAction(a1);
        thing.addAction(a2);
        CauseAction a3 = new CauseAction();
        Assert.assertTrue(thing.addOrReplaceAction(a3));
        Assert.assertEquals(Arrays.asList(a2, a3), thing.getActions());
        Assert.assertFalse(thing.addOrReplaceAction(a3));
        Assert.assertEquals(Arrays.asList(a2, a3), thing.getActions());
        thing.addAction(a1);
        Assert.assertEquals(Arrays.asList(a2, a3, a1), thing.getActions());
        Assert.assertTrue(thing.addOrReplaceAction(a3));
        Assert.assertEquals(Arrays.asList(a2, a3), thing.getActions());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void replaceActions() {
        CauseAction a1 = new CauseAction();
        ParametersAction a2 = new ParametersAction();
        thing.addAction(a1);
        thing.addAction(a2);
        CauseAction a3 = new CauseAction();
        Assert.assertTrue(thing.replaceActions(CauseAction.class, a3));
        Assert.assertEquals(Arrays.asList(a2, a3), thing.getActions());
        Assert.assertFalse(thing.replaceActions(CauseAction.class, a3));
        Assert.assertEquals(Arrays.asList(a2, a3), thing.getActions());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void removeAction() {
        CauseAction a1 = new CauseAction();
        ParametersAction a2 = new ParametersAction();
        thing.addAction(a1);
        thing.addAction(a2);
        Assert.assertEquals(Arrays.asList(a1, a2), thing.getActions());
        Assert.assertThat(thing.removeAction(a1), CoreMatchers.is(true));
        Assert.assertEquals(Arrays.asList(a2), thing.getActions());
        Assert.assertThat(thing.removeAction(a1), CoreMatchers.is(false));
        Assert.assertEquals(Arrays.asList(a2), thing.getActions());
        Assert.assertThat(thing.removeAction(null), CoreMatchers.is(false));
        Assert.assertEquals(Arrays.asList(a2), thing.getActions());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void removeActions() {
        CauseAction a1 = new CauseAction();
        ParametersAction a2 = new ParametersAction();
        thing.addAction(a1);
        thing.addAction(a2);
        Assert.assertEquals(Arrays.asList(a1, a2), thing.getActions());
        Assert.assertThat(thing.removeActions(CauseAction.class), CoreMatchers.is(true));
        Assert.assertEquals(Arrays.asList(a2), thing.getActions());
        Assert.assertThat(thing.removeActions(CauseAction.class), CoreMatchers.is(false));
        Assert.assertEquals(Arrays.asList(a2), thing.getActions());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void addAction() {
        CauseAction a1 = new CauseAction();
        ParametersAction a2 = new ParametersAction();
        Assert.assertEquals(Collections.<Action>emptyList(), thing.getActions());
        thing.addAction(a1);
        Assert.assertEquals(Collections.singletonList(a1), thing.getActions());
        thing.addAction(a2);
        Assert.assertEquals(Arrays.asList(a1, a2), thing.getActions());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addAction_null() {
        thing.addAction(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void replaceAction_null() {
        thing.replaceAction(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void replaceActions_null() {
        thing.replaceActions(CauseAction.class, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void replaceActions_null_null() {
        thing.replaceActions(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addOrReplaceAction_null() {
        thing.addOrReplaceAction(null);
    }

    @Test
    public void removeAction_null() {
        Assert.assertFalse(thing.removeAction(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeActions_null() {
        thing.removeActions(null);
    }

    private static class ActionableImpl extends Actionable {
        @Override
        public String getDisplayName() {
            return null;
        }

        @Override
        public String getSearchUrl() {
            return null;
        }
    }
}

