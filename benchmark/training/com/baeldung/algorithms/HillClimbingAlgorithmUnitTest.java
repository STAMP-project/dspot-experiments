package com.baeldung.algorithms;


import com.baeldung.algorithms.hillclimbing.HillClimbing;
import com.baeldung.algorithms.hillclimbing.State;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.junit.Assert;
import org.junit.Test;


public class HillClimbingAlgorithmUnitTest {
    private Stack<String> initStack;

    private Stack<String> goalStack;

    @Test
    public void givenInitAndGoalState_whenGetPathWithHillClimbing_thenPathFound() {
        HillClimbing hillClimbing = new HillClimbing();
        List<State> path;
        try {
            path = hillClimbing.getRouteWithHillClimbing(initStack, goalStack);
            Assert.assertNotNull(path);
            Assert.assertEquals(path.get(((path.size()) - 1)).getState().get(0), goalStack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenCurrentState_whenFindNextState_thenBetterHeuristics() {
        HillClimbing hillClimbing = new HillClimbing();
        List<Stack<String>> initList = new ArrayList<>();
        initList.add(initStack);
        State currentState = new State(initList);
        currentState.setHeuristics(hillClimbing.getHeuristicsValue(initList, goalStack));
        State nextState = hillClimbing.findNextState(currentState, goalStack);
        Assert.assertTrue(((nextState.getHeuristics()) > (currentState.getHeuristics())));
    }
}

