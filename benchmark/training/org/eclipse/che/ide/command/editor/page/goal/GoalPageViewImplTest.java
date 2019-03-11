/**
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.ide.command.editor.page.goal;


import com.google.gwtmockito.GwtMockitoTestRunner;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.che.ide.api.command.CommandGoal;
import org.eclipse.che.ide.command.editor.page.goal.GoalPageView.ActionDelegate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link GoalPageViewImpl}.
 */
@RunWith(GwtMockitoTestRunner.class)
public class GoalPageViewImplTest {
    @Mock
    private ActionDelegate actionDelegate;

    @InjectMocks
    private GoalPageViewImpl view;

    @Test
    public void shouldSetAvailableGoals() throws Exception {
        // given
        CommandGoal goal1 = Mockito.mock(CommandGoal.class);
        Mockito.when(goal1.getId()).thenReturn("g1");
        CommandGoal goal2 = Mockito.mock(CommandGoal.class);
        Mockito.when(goal2.getId()).thenReturn("g2");
        Set<CommandGoal> goals = new HashSet<>();
        goals.add(goal1);
        goals.add(goal2);
        // when
        view.setAvailableGoals(goals);
        // then
        Mockito.verify(view.goalsList).clear();
        Mockito.verify(view.goalsList).addItem(ArgumentMatchers.eq("g1"));
        Mockito.verify(view.goalsList).addItem(ArgumentMatchers.eq("g2"));
    }

    @Test
    public void shouldSetGoal() throws Exception {
        String goalId = "new goal";
        view.setGoal(goalId);
        Mockito.verify(view.goalsList).select(ArgumentMatchers.eq(goalId));
    }

    @Test
    public void shouldCallOnCreateGoal() throws Exception {
        Mockito.when(view.goalsList.getValue()).thenReturn(GoalPageViewImpl.CREATE_GOAL_ITEM);
        view.onGoalChanged(null);
        Mockito.verify(actionDelegate).onCreateGoal();
    }

    @Test
    public void shouldCallOnGoalChanged() throws Exception {
        String chosenGoalId = "g1";
        Mockito.when(view.goalsList.getValue()).thenReturn(chosenGoalId);
        view.onGoalChanged(null);
        Mockito.verify(view.goalsList).getValue();
        Mockito.verify(actionDelegate).onGoalChanged(ArgumentMatchers.eq(chosenGoalId));
    }
}

