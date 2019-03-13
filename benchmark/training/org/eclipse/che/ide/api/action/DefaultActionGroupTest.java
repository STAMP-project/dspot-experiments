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
package org.eclipse.che.ide.api.action;


import Constraints.FIRST;
import Constraints.LAST;
import com.google.gwtmockito.GwtMockitoTestRunner;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Mihail Kuznyetsov.
 */
@RunWith(GwtMockitoTestRunner.class)
public class DefaultActionGroupTest {
    @Mock
    BaseAction firstAction;

    @Mock
    BaseAction secondAction;

    @Mock
    BaseAction thirdAction;

    @Mock
    BaseAction fourthAction;

    @Mock
    BaseAction fifthAction;

    @Mock
    BaseAction sixthAction;

    @Mock
    ActionManager actionManager;

    DefaultActionGroup defaultActionGroup;

    @Test
    public void shouldNotAddSameActionTwice() {
        BaseAction action = Mockito.mock(BaseAction.class);
        defaultActionGroup.add(action, new org.eclipse.che.ide.api.constraints.Constraints(AFTER, "someAction"));
        defaultActionGroup.add(action, new org.eclipse.che.ide.api.constraints.Constraints(BEFORE, "someAction"));
        assertThat(defaultActionGroup.getChildrenCount()).isEqualTo(1);
    }

    @Test
    public void shouldReturnEmptyArrayWhenThereIsNoActions() {
        // when
        Action[] result = defaultActionGroup.getChildren(Mockito.mock(ActionEvent.class));
        // then
        assertThat(Arrays.asList(result)).isEmpty();
    }

    @Test
    public void addActionsWithNoExplicitConstraints() {
        // given
        mockRegisterActions();
        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction);
        defaultActionGroup.add(thirdAction);
        defaultActionGroup.add(fourthAction);
        defaultActionGroup.add(fifthAction);
        defaultActionGroup.add(sixthAction);
        // when
        Action[] result = defaultActionGroup.getChildren(Mockito.mock(ActionEvent.class));
        // then
        assertThat(Arrays.asList(result)).hasSize(6).containsExactly(firstAction, secondAction, thirdAction, fourthAction, fifthAction, sixthAction);
    }

    @Test
    public void addOneFirst() {
        // given
        mockRegisterActions();
        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction);
        defaultActionGroup.add(thirdAction, FIRST);
        defaultActionGroup.add(fourthAction);
        defaultActionGroup.add(fifthAction);
        defaultActionGroup.add(sixthAction);
        // when
        Action[] result = defaultActionGroup.getChildren(Mockito.mock(ActionEvent.class));
        // then
        assertThat(Arrays.asList(result)).hasSize(6).containsExactly(thirdAction, firstAction, secondAction, fourthAction, fifthAction, sixthAction);
    }

    @Test
    public void addTwoFirst() {
        // given
        mockRegisterActions();
        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction);
        defaultActionGroup.add(thirdAction, FIRST);
        defaultActionGroup.add(fourthAction);
        defaultActionGroup.add(fifthAction, FIRST);
        defaultActionGroup.add(sixthAction);
        // when
        Action[] result = defaultActionGroup.getChildren(Mockito.mock(ActionEvent.class));
        // then
        assertThat(Arrays.asList(result)).hasSize(6).containsExactly(fifthAction, thirdAction, firstAction, secondAction, fourthAction, sixthAction);
    }

    @Test
    public void addOneLast() {
        // given
        mockRegisterActions();
        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction);
        defaultActionGroup.add(thirdAction, LAST);
        defaultActionGroup.add(fourthAction);
        defaultActionGroup.add(fifthAction);
        defaultActionGroup.add(sixthAction);
        // when
        Action[] result = defaultActionGroup.getChildren(Mockito.mock(ActionEvent.class));
        // then
        assertThat(Arrays.asList(result)).hasSize(6).containsExactly(firstAction, secondAction, thirdAction, fourthAction, fifthAction, sixthAction);
    }

    @Test
    public void addOneBefore() {
        // given
        mockRegisterActions();
        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction);
        defaultActionGroup.add(thirdAction, new org.eclipse.che.ide.api.constraints.Constraints(BEFORE, "secondAction"));
        defaultActionGroup.add(fourthAction);
        defaultActionGroup.add(fifthAction);
        defaultActionGroup.add(sixthAction);
        // when
        Action[] result = defaultActionGroup.getChildren(Mockito.mock(ActionEvent.class));
        // then
        assertThat(Arrays.asList(result)).hasSize(6).containsExactly(firstAction, thirdAction, secondAction, fourthAction, fifthAction, sixthAction);
    }

    @Test
    public void addTwoBefore() {
        // given
        mockRegisterActions();
        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction);
        defaultActionGroup.add(thirdAction, new org.eclipse.che.ide.api.constraints.Constraints(BEFORE, "secondAction"));
        defaultActionGroup.add(fourthAction);
        defaultActionGroup.add(fifthAction, new org.eclipse.che.ide.api.constraints.Constraints(BEFORE, "secondAction"));
        defaultActionGroup.add(sixthAction);
        // when
        Action[] result = defaultActionGroup.getChildren(Mockito.mock(ActionEvent.class));
        // then
        assertThat(Arrays.asList(result)).hasSize(6).containsExactly(firstAction, thirdAction, fifthAction, secondAction, fourthAction, sixthAction);
    }

    @Test
    public void addOneBeforeNotAdded() {
        // given
        mockRegisterActions();
        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction);
        defaultActionGroup.add(thirdAction, new org.eclipse.che.ide.api.constraints.Constraints(BEFORE, "fifthAction"));
        defaultActionGroup.add(fourthAction);
        defaultActionGroup.add(fifthAction);
        defaultActionGroup.add(sixthAction);
        // when
        Action[] result = defaultActionGroup.getChildren(Mockito.mock(ActionEvent.class));
        // then
        assertThat(Arrays.asList(result)).hasSize(6).containsExactly(firstAction, secondAction, fourthAction, thirdAction, fifthAction, sixthAction);
    }

    @Test
    public void addComplexBefore() {
        // given
        mockRegisterActions();
        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction, new org.eclipse.che.ide.api.constraints.Constraints(BEFORE, "fourthAction"));
        defaultActionGroup.add(thirdAction);
        defaultActionGroup.add(fourthAction, new org.eclipse.che.ide.api.constraints.Constraints(BEFORE, "sixthAction"));
        defaultActionGroup.add(fifthAction);
        defaultActionGroup.add(sixthAction);
        // when
        Action[] result = defaultActionGroup.getChildren(Mockito.mock(ActionEvent.class));
        // then
        assertThat(Arrays.asList(result)).hasSize(6).containsExactly(firstAction, thirdAction, fifthAction, secondAction, fourthAction, sixthAction);
    }

    @Test
    public void addOneAfter() {
        // given
        mockRegisterActions();
        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction);
        defaultActionGroup.add(thirdAction);
        defaultActionGroup.add(fourthAction, new org.eclipse.che.ide.api.constraints.Constraints(AFTER, "firstAction"));
        defaultActionGroup.add(fifthAction);
        defaultActionGroup.add(sixthAction);
        // when
        Action[] result = defaultActionGroup.getChildren(Mockito.mock(ActionEvent.class));
        // then
        assertThat(Arrays.asList(result)).hasSize(6).containsExactly(firstAction, fourthAction, secondAction, thirdAction, fifthAction, sixthAction);
    }

    @Test
    public void addTwoAfter() {
        // given
        mockRegisterActions();
        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction, new org.eclipse.che.ide.api.constraints.Constraints(AFTER, "fifthAction"));
        defaultActionGroup.add(thirdAction, new org.eclipse.che.ide.api.constraints.Constraints(AFTER, "fifthAction"));
        defaultActionGroup.add(fourthAction);
        defaultActionGroup.add(fifthAction);
        defaultActionGroup.add(sixthAction);
        // when
        Action[] result = defaultActionGroup.getChildren(Mockito.mock(ActionEvent.class));
        // then
        assertThat(Arrays.asList(result)).hasSize(6).containsExactly(firstAction, fourthAction, fifthAction, thirdAction, secondAction, sixthAction);
    }

    @Test
    public void addComplexAfter() {
        // given
        mockRegisterActions();
        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction, new org.eclipse.che.ide.api.constraints.Constraints(AFTER, "fifthAction"));
        defaultActionGroup.add(thirdAction);
        defaultActionGroup.add(fourthAction);
        defaultActionGroup.add(fifthAction, new org.eclipse.che.ide.api.constraints.Constraints(AFTER, "firstAction"));
        defaultActionGroup.add(sixthAction);
        // when
        Action[] result = defaultActionGroup.getChildren(Mockito.mock(ActionEvent.class));
        // then
        assertThat(Arrays.asList(result)).hasSize(6).containsExactly(firstAction, fifthAction, secondAction, thirdAction, fourthAction, sixthAction);
    }

    @Test
    public void addOneAfterNotAdded() {
        // given
        mockRegisterActions();
        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction, new org.eclipse.che.ide.api.constraints.Constraints(AFTER, "fifthAction"));
        defaultActionGroup.add(thirdAction);
        defaultActionGroup.add(fourthAction);
        defaultActionGroup.add(fifthAction);
        defaultActionGroup.add(sixthAction);
        // when
        Action[] result = defaultActionGroup.getChildren(Mockito.mock(ActionEvent.class));
        // then
        assertThat(Arrays.asList(result)).hasSize(6).containsExactly(firstAction, thirdAction, fourthAction, fifthAction, secondAction, sixthAction);
    }

    @Test
    public void addActionToTheEndWhenConstraintUnsatisfied() {
        // given
        mockRegisterActions();
        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction, new org.eclipse.che.ide.api.constraints.Constraints(AFTER, "tenthAction"));
        defaultActionGroup.add(thirdAction);
        defaultActionGroup.add(fourthAction);
        defaultActionGroup.add(fifthAction);
        defaultActionGroup.add(sixthAction);
        // when
        Action[] result = defaultActionGroup.getChildren(Mockito.mock(ActionEvent.class));
        // then
        assertThat(Arrays.asList(result)).hasSize(6).containsExactly(firstAction, thirdAction, fourthAction, fifthAction, sixthAction, secondAction);
    }

    @Test
    public void shouldResortAllActionsAfterAddingOne() {
        // add some actions
        defaultActionGroup.add(firstAction);
        Mockito.when(actionManager.getId(ArgumentMatchers.eq(firstAction))).thenReturn("firstAction");
        defaultActionGroup.add(secondAction, FIRST);
        Mockito.when(actionManager.getId(ArgumentMatchers.eq(secondAction))).thenReturn("secondAction");
        defaultActionGroup.add(thirdAction, new org.eclipse.che.ide.api.constraints.Constraints(AFTER, "fourthAction"));
        // verify order
        Action[] result = defaultActionGroup.getChildren(Mockito.mock(ActionEvent.class));
        assertThat(Arrays.asList(result)).containsExactly(secondAction, firstAction, thirdAction);
        // add other actions
        defaultActionGroup.add(fourthAction);
        defaultActionGroup.add(fifthAction, FIRST);
        Mockito.when(actionManager.getId(ArgumentMatchers.eq(fifthAction))).thenReturn("fifthAction");
        defaultActionGroup.add(sixthAction, new org.eclipse.che.ide.api.constraints.Constraints(BEFORE, "firstAction"));
        Mockito.when(actionManager.getId(ArgumentMatchers.eq(sixthAction))).thenReturn("sixthAction");
        // verify that actions have been resorted
        Action[] newResult = defaultActionGroup.getChildren(Mockito.mock(ActionEvent.class));
        assertThat(Arrays.asList(newResult)).hasSize(6).containsExactly(fifthAction, secondAction, sixthAction, firstAction, fourthAction, thirdAction);
    }

    @Test
    public void getChildrenCount() {
        // given
        mockRegisterActions();
        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction, new org.eclipse.che.ide.api.constraints.Constraints(AFTER, "fifthAction"));
        defaultActionGroup.add(thirdAction);
        defaultActionGroup.add(fourthAction);
        defaultActionGroup.add(fifthAction);
        defaultActionGroup.add(sixthAction);
        // when
        int count = defaultActionGroup.getChildrenCount();
        // then
        assertThat(count).isEqualTo(6);
    }

    @Test
    public void addActionsAndSeparators() {
        // given
        mockRegisterActions();
        defaultActionGroup.addSeparator();
        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction);
        defaultActionGroup.addSeparator();
        defaultActionGroup.add(thirdAction);
        defaultActionGroup.addSeparator();
        defaultActionGroup.add(fourthAction);
        defaultActionGroup.add(fifthAction);
        defaultActionGroup.add(sixthAction);
        defaultActionGroup.addSeparator();
        // when
        Action[] newResult = defaultActionGroup.getChildren(Mockito.mock(ActionEvent.class));
        // then
        assertThat(Arrays.asList(newResult)).hasSize(10).containsExactly(Separator.getInstance(), firstAction, secondAction, Separator.getInstance(), thirdAction, Separator.getInstance(), fourthAction, fifthAction, sixthAction, Separator.getInstance());
    }

    @Test
    public void addActionsFromActionGroup() {
        // given
        mockRegisterActions();
        DefaultActionGroup newGroup = new DefaultActionGroup(actionManager);
        newGroup.add(firstAction);
        newGroup.add(secondAction);
        newGroup.add(thirdAction);
        newGroup.add(fourthAction);
        newGroup.add(fifthAction);
        newGroup.add(sixthAction);
        defaultActionGroup.addAll(newGroup);
        // when
        Action[] result = defaultActionGroup.getChildren(Mockito.mock(ActionEvent.class));
        // then
        assertThat(Arrays.asList(result)).hasSize(6).containsExactly(firstAction, secondAction, thirdAction, fourthAction, fifthAction, sixthAction);
    }

    @Test
    public void addCollectionOfActions() {
        // given
        mockRegisterActions();
        List<Action> actions = new java.util.ArrayList(Arrays.asList(new Action[]{ firstAction, secondAction, thirdAction, fourthAction, fifthAction, sixthAction }));
        defaultActionGroup.addAll(actions);
        // when
        Action[] result = defaultActionGroup.getChildren(Mockito.mock(ActionEvent.class));
        // then
        assertThat(Arrays.asList(result)).hasSize(6).containsExactly(firstAction, secondAction, thirdAction, fourthAction, fifthAction, sixthAction);
    }

    @Test
    public void addActionsWithVarArg() {
        // given
        mockRegisterActions();
        defaultActionGroup.addAll(firstAction, secondAction, thirdAction, fourthAction, fifthAction, sixthAction);
        // when
        Action[] result = defaultActionGroup.getChildren(Mockito.mock(ActionEvent.class));
        // then
        assertThat(Arrays.asList(result)).hasSize(6).containsExactly(firstAction, secondAction, thirdAction, fourthAction, fifthAction, sixthAction);
    }

    @Test
    public void removeOneAction() {
        // given
        mockRegisterActions();
        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction);
        defaultActionGroup.add(thirdAction);
        defaultActionGroup.add(fourthAction);
        defaultActionGroup.add(fifthAction);
        defaultActionGroup.add(sixthAction);
        defaultActionGroup.remove(thirdAction);
        // when
        Action[] result = defaultActionGroup.getChildren(Mockito.mock(ActionEvent.class));
        // then
        assertThat(Arrays.asList(result)).containsExactly(firstAction, secondAction, fourthAction, fifthAction, sixthAction);
    }

    @Test
    public void removeOneActionAndResortConstraints() {
        // given
        mockRegisterActions();
        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction);
        defaultActionGroup.add(thirdAction);
        defaultActionGroup.add(fourthAction);
        defaultActionGroup.add(fifthAction);
        defaultActionGroup.add(sixthAction);
        // when
        defaultActionGroup.remove(fourthAction);
        // then
        Action[] result = defaultActionGroup.getChildren(Mockito.mock(ActionEvent.class));
        assertThat(Arrays.asList(result)).containsExactly(firstAction, secondAction, thirdAction, fifthAction, sixthAction);
    }

    @Test
    public void removeAllActions() {
        // given
        mockRegisterActions();
        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction);
        defaultActionGroup.add(thirdAction);
        defaultActionGroup.add(fourthAction);
        defaultActionGroup.add(fifthAction);
        defaultActionGroup.add(sixthAction);
        // when
        defaultActionGroup.removeAll();
        // then
        Action[] result = defaultActionGroup.getChildren(Mockito.mock(ActionEvent.class));
        assertThat(Arrays.asList(result)).isEmpty();
    }
}

