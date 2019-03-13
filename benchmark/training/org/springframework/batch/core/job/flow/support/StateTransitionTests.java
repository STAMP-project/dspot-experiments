/**
 * Copyright 2006-2013 the original author or authors.
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
package org.springframework.batch.core.job.flow.support;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.job.flow.State;
import org.springframework.batch.core.job.flow.StateSupport;


/**
 *
 *
 * @author Dave Syer
 * @author Michael Minella
 */
public class StateTransitionTests {
    State state = new StateSupport("state1");

    @Test
    public void testIsEnd() {
        StateTransition transition = StateTransition.createEndStateTransition(state, "");
        Assert.assertTrue(transition.isEnd());
        Assert.assertNull(transition.getNext());
    }

    @Test
    public void testMatchesStar() {
        StateTransition transition = StateTransition.createStateTransition(state, "*", "start");
        Assert.assertTrue(transition.matches("CONTINUABLE"));
    }

    @Test
    public void testMatchesNull() {
        StateTransition transition = StateTransition.createStateTransition(state, null, "start");
        Assert.assertTrue(transition.matches("CONTINUABLE"));
    }

    @Test
    public void testMatchesEmpty() {
        StateTransition transition = StateTransition.createStateTransition(state, "", "start");
        Assert.assertTrue(transition.matches("CONTINUABLE"));
    }

    @Test
    public void testMatchesExact() {
        StateTransition transition = StateTransition.createStateTransition(state, "CONTINUABLE", "start");
        Assert.assertTrue(transition.matches("CONTINUABLE"));
    }

    @Test
    public void testMatchesWildcard() {
        StateTransition transition = StateTransition.createStateTransition(state, "CONTIN*", "start");
        Assert.assertTrue(transition.matches("CONTINUABLE"));
    }

    @Test
    public void testMatchesPlaceholder() {
        StateTransition transition = StateTransition.createStateTransition(state, "CONTIN???LE", "start");
        Assert.assertTrue(transition.matches("CONTINUABLE"));
    }

    @Test
    public void testToString() {
        StateTransition transition = StateTransition.createStateTransition(state, "CONTIN???LE", "start");
        String string = transition.toString();
        Assert.assertTrue(("Wrong string: " + string), string.contains("Transition"));
        Assert.assertTrue(("Wrong string: " + string), string.contains("start"));
        Assert.assertTrue(("Wrong string: " + string), string.contains("CONTIN???LE"));
        Assert.assertTrue(("Wrong string: " + string), string.contains("next="));
    }
}

