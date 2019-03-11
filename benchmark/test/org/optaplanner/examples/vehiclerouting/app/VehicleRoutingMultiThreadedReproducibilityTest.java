package org.optaplanner.examples.vehiclerouting.app;


import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.junit.Test;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.common.app.AbstractTurtleTest;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;


/**
 * The idea is to verify one of the basic requirements of Multithreaded Solving - the reproducibility of results. After
 * a constant number of steps, every iteration must finish with the same score.
 */
public class VehicleRoutingMultiThreadedReproducibilityTest extends AbstractTurtleTest {
    private static final int REPETITION_COUNT = 10;

    private static final int STEP_LIMIT = 5000;

    private static final String MOVE_THREAD_COUNT = "4";

    private static final String DATA_SET = "import/belgium/basic/air/belgium-n50-k10.vrp";

    private final VehicleRoutingApp vehicleRoutingApp = new VehicleRoutingApp();

    private VehicleRoutingSolution[] vehicleRoutingSolutions = new VehicleRoutingSolution[VehicleRoutingMultiThreadedReproducibilityTest.REPETITION_COUNT];

    private SolverFactory<VehicleRoutingSolution> solverFactory;

    @Test
    public void multiThreadedSolvingIsReproducible() {
        AbstractTurtleTest.checkRunTurtleTests();
        IntStream.range(0, VehicleRoutingMultiThreadedReproducibilityTest.REPETITION_COUNT).forEach(( iteration) -> solveAndCompareWithPrevious(iteration));
    }
}

