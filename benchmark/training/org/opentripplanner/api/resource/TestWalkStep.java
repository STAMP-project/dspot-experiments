package org.opentripplanner.api.resource;


import AbsoluteDirection.EAST;
import AbsoluteDirection.NORTH;
import AbsoluteDirection.SOUTHWEST;
import RelativeDirection.CONTINUE;
import RelativeDirection.HARD_LEFT;
import RelativeDirection.RIGHT;
import junit.framework.TestCase;
import org.opentripplanner.api.model.WalkStep;


public class TestWalkStep extends TestCase {
    public void testRelativeDirection() {
        WalkStep step = new WalkStep();
        double angle1 = degreesToRadians(0);
        double angle2 = degreesToRadians(90);
        step.setDirections(angle1, angle2, false);
        TestCase.assertEquals(RIGHT, step.relativeDirection);
        TestCase.assertEquals(EAST, step.absoluteDirection);
        angle1 = degreesToRadians(0);
        angle2 = degreesToRadians(5);
        step.setDirections(angle1, angle2, false);
        TestCase.assertEquals(CONTINUE, step.relativeDirection);
        TestCase.assertEquals(NORTH, step.absoluteDirection);
        angle1 = degreesToRadians(0);
        angle2 = degreesToRadians(240);
        step.setDirections(angle1, angle2, false);
        TestCase.assertEquals(HARD_LEFT, step.relativeDirection);
        TestCase.assertEquals(SOUTHWEST, step.absoluteDirection);
    }
}

