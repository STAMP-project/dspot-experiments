package org.kie.dmn.core;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.util.DMNRuntimeUtil;


public class OnlineDatingTest extends BaseInterpretedVsCompiledTest {
    public OnlineDatingTest(final boolean useExecModelCompiler) {
        super(useExecModelCompiler);
    }

    @Test
    public void testDMChallengeMarch2017() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("dmcommunity_challenge_2017_03.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_1b5a3a8f-ccf0-459b-8783-38601977e828", "DMCommunity Challenge - March 2017");
        final Map<?, ?> lonelySoul = createProfile("Bob", "Male", "Boston", 30, Arrays.asList("swimming", "cinema", "jogging", "writing"), 25, 35, Collections.singletonList("Female"), 1);
        final List<Map<?, ?>> profiles = createProfiles();
        final DMNContext ctx = runtime.newContext();
        ctx.set("Lonely Soul", lonelySoul);
        ctx.set("Potential Soul Mates", profiles);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, ctx);
        System.out.format("Matches for %s:%n", lonelySoul.get("Name"));
        int i = 0;
        for (final Map<String, Object> soulMate : ((List<Map<String, Object>>) (dmnResult.getContext().get("Sorted Souls")))) {
            System.out.format("%d. %-10s - Score = %2.0f%n", (++i), ((Map<String, Object>) (soulMate.get("Profile2"))).get("Name"), soulMate.get("Score"));
        }
    }
}

