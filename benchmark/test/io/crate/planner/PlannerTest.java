package io.crate.planner;


import io.crate.action.sql.SessionContext;
import io.crate.planner.node.ddl.UpdateSettingsPlan;
import io.crate.planner.node.management.KillPlan;
import io.crate.sql.tree.LongLiteral;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import java.util.Collections;
import java.util.UUID;
import org.elasticsearch.common.Randomness;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;


@SuppressWarnings("ConstantConditions")
public class PlannerTest extends CrateDummyClusterServiceUnitTest {
    private SQLExecutor e;

    @Test
    public void testSetPlan() throws Exception {
        UpdateSettingsPlan plan = e.plan("set GLOBAL PERSISTENT stats.jobs_log_size=1024");
        // set transient settings too when setting persistent ones
        assertThat(plan.transientSettings().get("stats.jobs_log_size").get(0), Is.is(new LongLiteral("1024")));
        assertThat(plan.persistentSettings().get("stats.jobs_log_size").get(0), Is.is(new LongLiteral("1024")));
        plan = e.plan("set GLOBAL TRANSIENT stats.enabled=false,stats.jobs_log_size=0");
        assertThat(plan.persistentSettings().size(), Is.is(0));
        assertThat(plan.transientSettings().size(), Is.is(2));
    }

    @Test
    public void testSetSessionTransactionModeIsNoopPlan() throws Exception {
        Plan plan = e.plan("SET SESSION CHARACTERISTICS AS TRANSACTION ISOLATION LEVEL READ");
        assertThat(plan, Matchers.instanceOf(NoopPlan.class));
    }

    @Test
    public void testExecutionPhaseIdSequence() throws Exception {
        PlannerContext plannerContext = new PlannerContext(clusterService.state(), new io.crate.metadata.RoutingProvider(Randomness.get().nextInt(), Collections.emptyList()), UUID.randomUUID(), e.functions(), new io.crate.metadata.CoordinatorTxnCtx(SessionContext.systemSessionContext()), 0, 0);
        assertThat(plannerContext.nextExecutionPhaseId(), Is.is(0));
        assertThat(plannerContext.nextExecutionPhaseId(), Is.is(1));
    }

    @Test
    public void testKillPlanAll() throws Exception {
        KillPlan killPlan = e.plan("kill all");
        assertThat(killPlan, Matchers.instanceOf(KillPlan.class));
        assertThat(killPlan.jobToKill().isPresent(), Is.is(false));
    }

    @Test
    public void testKillPlanJobs() throws Exception {
        KillPlan killJobsPlan = e.plan("kill '6a3d6fb6-1401-4333-933d-b38c9322fca7'");
        assertThat(killJobsPlan.jobToKill().get().toString(), Is.is("6a3d6fb6-1401-4333-933d-b38c9322fca7"));
    }

    @Test
    public void testDeallocate() {
        assertThat(e.plan("deallocate all"), Matchers.instanceOf(NoopPlan.class));
        assertThat(e.plan("deallocate test_prep_stmt"), Matchers.instanceOf(NoopPlan.class));
    }
}

