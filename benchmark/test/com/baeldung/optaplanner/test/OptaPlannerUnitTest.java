package com.baeldung.optaplanner.test;


import com.baeldung.optaplanner.CourseSchedule;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;


public class OptaPlannerUnitTest {
    static CourseSchedule unsolvedCourseSchedule;

    @Test
    public void test_whenCustomJavaSolver() {
        SolverFactory<CourseSchedule> solverFactory = SolverFactory.createFromXmlResource("courseScheduleSolverConfiguration.xml");
        Solver<CourseSchedule> solver = solverFactory.buildSolver();
        CourseSchedule solvedCourseSchedule = solver.solve(OptaPlannerUnitTest.unsolvedCourseSchedule);
        Assert.assertNotNull(solvedCourseSchedule.getScore());
        Assert.assertEquals((-4), solvedCourseSchedule.getScore().getHardScore());
    }

    @Test
    public void test_whenDroolsSolver() {
        SolverFactory<CourseSchedule> solverFactory = SolverFactory.createFromXmlResource("courseScheduleSolverConfigDrools.xml");
        Solver<CourseSchedule> solver = solverFactory.buildSolver();
        CourseSchedule solvedCourseSchedule = solver.solve(OptaPlannerUnitTest.unsolvedCourseSchedule);
        Assert.assertNotNull(solvedCourseSchedule.getScore());
        Assert.assertEquals(0, solvedCourseSchedule.getScore().getHardScore());
    }
}

