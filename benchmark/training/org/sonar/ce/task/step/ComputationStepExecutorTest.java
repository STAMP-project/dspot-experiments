/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.ce.task.step;


import ComputationStepExecutor.Listener;
import LoggerLevel.INFO;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.sonar.api.utils.log.LogTester;
import org.sonar.api.utils.log.LoggerLevel;
import org.sonar.ce.task.CeTaskInterrupter;
import org.sonar.ce.task.ChangeLogLevel;


public class ComputationStepExecutorTest {
    @Rule
    public LogTester logTester = new LogTester();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final Listener listener = Mockito.mock(Listener.class);

    private final CeTaskInterrupter taskInterrupter = Mockito.mock(CeTaskInterrupter.class);

    private final ComputationStep computationStep1 = ComputationStepExecutorTest.mockComputationStep("step1");

    private final ComputationStep computationStep2 = ComputationStepExecutorTest.mockComputationStep("step2");

    private final ComputationStep computationStep3 = ComputationStepExecutorTest.mockComputationStep("step3");

    @Test
    public void execute_call_execute_on_each_ComputationStep_in_order_returned_by_instances_method() {
        execute();
        InOrder inOrder = Mockito.inOrder(computationStep1, computationStep2, computationStep3);
        inOrder.verify(computationStep1).execute(ArgumentMatchers.any());
        inOrder.verify(computationStep1).getDescription();
        inOrder.verify(computationStep2).execute(ArgumentMatchers.any());
        inOrder.verify(computationStep2).getDescription();
        inOrder.verify(computationStep3).execute(ArgumentMatchers.any());
        inOrder.verify(computationStep3).getDescription();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void execute_let_exception_thrown_by_ComputationStep_go_up_as_is() {
        String message = "Exception should go up";
        ComputationStep computationStep = ComputationStepExecutorTest.mockComputationStep("step1");
        Mockito.doThrow(new RuntimeException(message)).when(computationStep).execute(ArgumentMatchers.any());
        ComputationStepExecutor computationStepExecutor = new ComputationStepExecutor(ComputationStepExecutorTest.mockComputationSteps(computationStep), taskInterrupter);
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(message);
        computationStepExecutor.execute();
    }

    @Test
    public void execute_logs_end_timing_and_statistics_for_each_ComputationStep_in_INFO_level() {
        ComputationStep step1 = new ComputationStepExecutorTest.StepWithStatistics("Step One", "foo", "100", "bar", "20");
        ComputationStep step2 = new ComputationStepExecutorTest.StepWithStatistics("Step Two", "foo", "50", "baz", "10");
        ComputationStep step3 = new ComputationStepExecutorTest.StepWithStatistics("Step Three");
        try (ChangeLogLevel executor = new ChangeLogLevel(ComputationStepExecutor.class, LoggerLevel.INFO);ChangeLogLevel logLevel1 = new ChangeLogLevel(step1.getClass(), LoggerLevel.INFO);ChangeLogLevel logLevel2 = new ChangeLogLevel(step2.getClass(), LoggerLevel.INFO);ChangeLogLevel logLevel3 = new ChangeLogLevel(step3.getClass(), LoggerLevel.INFO)) {
            execute();
            List<String> infoLogs = logTester.logs(INFO);
            assertThat(infoLogs).hasSize(3);
            assertThat(infoLogs.get(0)).contains("Step One | foo=100 | bar=20 | status=SUCCESS | time=");
            assertThat(infoLogs.get(1)).contains("Step Two | foo=50 | baz=10 | status=SUCCESS | time=");
            assertThat(infoLogs.get(2)).contains("Step Three | status=SUCCESS | time=");
        }
    }

    @Test
    public void execute_logs_end_timing_and_statistics_for_each_ComputationStep_in_INFO_level_even_if_failed() {
        RuntimeException expected = new RuntimeException("faking step failing with RuntimeException");
        ComputationStep step1 = new ComputationStepExecutorTest.StepWithStatistics("Step One", "foo", "100", "bar", "20");
        ComputationStep step2 = new ComputationStepExecutorTest.StepWithStatistics("Step Two", "foo", "50", "baz", "10");
        ComputationStep step3 = new ComputationStepExecutorTest.StepWithStatistics("Step Three", "donut", "crash") {
            @Override
            public void execute(Context context) {
                super.execute(context);
                throw expected;
            }
        };
        try (ChangeLogLevel executor = new ChangeLogLevel(ComputationStepExecutor.class, LoggerLevel.INFO);ChangeLogLevel logLevel1 = new ChangeLogLevel(step1.getClass(), LoggerLevel.INFO);ChangeLogLevel logLevel2 = new ChangeLogLevel(step2.getClass(), LoggerLevel.INFO);ChangeLogLevel logLevel3 = new ChangeLogLevel(step3.getClass(), LoggerLevel.INFO)) {
            try {
                execute();
                fail("a RuntimeException should have been thrown");
            } catch (RuntimeException e) {
                List<String> infoLogs = logTester.logs(INFO);
                assertThat(infoLogs).hasSize(3);
                assertThat(infoLogs.get(0)).contains("Step One | foo=100 | bar=20 | status=SUCCESS | time=");
                assertThat(infoLogs.get(1)).contains("Step Two | foo=50 | baz=10 | status=SUCCESS | time=");
                assertThat(infoLogs.get(2)).contains("Step Three | donut=crash | status=FAILED | time=");
            }
        }
    }

    @Test
    public void execute_throws_IAE_if_step_adds_time_statistic() {
        ComputationStep step = new ComputationStepExecutorTest.StepWithStatistics("A Step", "foo", "100", "time", "20");
        try (ChangeLogLevel executor = new ChangeLogLevel(ComputationStepExecutor.class, LoggerLevel.INFO)) {
            expectedException.expect(IllegalArgumentException.class);
            expectedException.expectMessage("Statistic with key [time] is not accepted");
            execute();
        }
    }

    @Test
    public void execute_throws_IAE_if_step_adds_statistic_multiple_times() {
        ComputationStep step = new ComputationStepExecutorTest.StepWithStatistics("A Step", "foo", "100", "foo", "20");
        try (ChangeLogLevel executor = new ChangeLogLevel(ComputationStepExecutor.class, LoggerLevel.INFO)) {
            expectedException.expect(IllegalArgumentException.class);
            expectedException.expectMessage("Statistic with key [foo] is already present");
            execute();
        }
    }

    @Test
    public void execute_throws_NPE_if_step_adds_statistic_with_null_key() {
        ComputationStep step = new ComputationStepExecutorTest.StepWithStatistics("A Step", "foo", "100", null, "bar");
        try (ChangeLogLevel executor = new ChangeLogLevel(ComputationStepExecutor.class, LoggerLevel.INFO)) {
            expectedException.expect(NullPointerException.class);
            expectedException.expectMessage("Statistic has null key");
            execute();
        }
    }

    @Test
    public void execute_throws_NPE_if_step_adds_statistic_with_null_value() {
        ComputationStep step = new ComputationStepExecutorTest.StepWithStatistics("A Step", "foo", "100", "bar", null);
        try (ChangeLogLevel executor = new ChangeLogLevel(ComputationStepExecutor.class, LoggerLevel.INFO)) {
            expectedException.expect(NullPointerException.class);
            expectedException.expectMessage("Statistic with key [bar] has null value");
            execute();
        }
    }

    @Test
    public void execute_calls_listener_finished_method_with_all_step_runs() {
        execute();
        Mockito.verify(listener).finished(true);
        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void execute_calls_listener_finished_method_even_if_a_step_throws_an_exception() {
        RuntimeException toBeThrown = new RuntimeException("simulating failing execute Step method");
        Mockito.doThrow(toBeThrown).when(computationStep1).execute(ArgumentMatchers.any());
        try {
            execute();
            fail("exception toBeThrown should have been raised");
        } catch (RuntimeException e) {
            assertThat(e).isSameAs(toBeThrown);
            Mockito.verify(listener).finished(false);
            Mockito.verifyNoMoreInteractions(listener);
        }
    }

    @Test
    public void execute_does_not_fail_if_listener_throws_Throwable() {
        ComputationStepExecutor.Listener listener = Mockito.mock(Listener.class);
        Mockito.doThrow(new Error("Facking error thrown by Listener")).when(listener).finished(ArgumentMatchers.anyBoolean());
        execute();
    }

    @Test
    public void execute_fails_with_exception_thrown_by_interrupter() throws Throwable {
        executeFailsWithExceptionThrownByInterrupter();
        Mockito.reset(computationStep1, computationStep2, computationStep3, taskInterrupter);
        runInOtherThread(this::executeFailsWithExceptionThrownByInterrupter);
    }

    @Test
    public void execute_calls_interrupter_with_current_thread_before_each_step() throws Throwable {
        executeCallsInterrupterWithCurrentThreadBeforeEachStep();
        Mockito.reset(computationStep1, computationStep2, computationStep3, taskInterrupter);
        runInOtherThread(this::executeCallsInterrupterWithCurrentThreadBeforeEachStep);
    }

    private static class StepWithStatistics implements ComputationStep {
        private final String description;

        private final String[] statistics;

        private StepWithStatistics(String description, String... statistics) {
            this.description = description;
            this.statistics = statistics;
        }

        @Override
        public void execute(Context context) {
            for (int i = 0; i < (statistics.length); i += 2) {
                context.getStatistics().add(statistics[i], statistics[(i + 1)]);
            }
        }

        @Override
        public String getDescription() {
            return description;
        }
    }
}

