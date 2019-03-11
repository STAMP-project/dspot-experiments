/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.event.service.internal;


import DuplicationStrategy.Action;
import org.hibernate.event.internal.DefaultMergeEventListener;
import org.hibernate.event.service.spi.DuplicationStrategy;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

import static Action.REPLACE_ORIGINAL;


/**
 *
 *
 * @author Frank Doherty
 */
@TestForIssue(jiraKey = "HHH-13070")
public class EventListenerGroupAppendListenerTest extends BaseCoreFunctionalTestCase {
    private static final DuplicationStrategy DUPLICATION_STRATEGY_REPLACE_ORIGINAL = new DuplicationStrategy() {
        @Override
        public boolean areMatch(Object added, Object existing) {
            return true;
        }

        @Override
        public Action getAction() {
            return REPLACE_ORIGINAL;
        }
    };

    @Test
    public void testAppendListenerWithNoStrategy() {
        EventListenerGroupAppendListenerTest.SpecificMergeEventListener1 mergeEventListener = new EventListenerGroupAppendListenerTest.SpecificMergeEventListener1();
        runAppendListenerTest(null, mergeEventListener);
    }

    @Test
    public void testAppendListenerWithReplaceOriginalStrategy() {
        EventListenerGroupAppendListenerTest.SpecificMergeEventListener2 mergeEventListener = new EventListenerGroupAppendListenerTest.SpecificMergeEventListener2();
        runAppendListenerTest(EventListenerGroupAppendListenerTest.DUPLICATION_STRATEGY_REPLACE_ORIGINAL, mergeEventListener);
    }

    // we need a specific class, otherwise the default duplication strategy avoiding listeners from the same classes
    // will be triggered.
    private static class SpecificMergeEventListener1 extends DefaultMergeEventListener {}

    // we need a specific class, otherwise the default duplication strategy avoiding listeners from the same classes
    // will be triggered.
    private static class SpecificMergeEventListener2 extends DefaultMergeEventListener {}
}

