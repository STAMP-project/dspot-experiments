package cucumber.api;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static Type.AMBIGUOUS;
import static Type.FAILED;
import static Type.PASSED;
import static Type.PENDING;
import static Type.SKIPPED;
import static Type.UNDEFINED;


public class ResultTest {
    @Test
    public void severity_from_low_to_high_is_passed_skipped_pending_undefined_ambiguous_failed() {
        Result passed = new Result(PASSED, 0L, null);
        Result skipped = new Result(SKIPPED, 0L, null);
        Result pending = new Result(PENDING, 0L, null);
        Result ambiguous = new Result(AMBIGUOUS, 0L, null);
        Result undefined = new Result(UNDEFINED, 0L, null);
        Result failed = new Result(FAILED, 0L, null);
        List<Result> results = Arrays.asList(pending, passed, skipped, failed, ambiguous, undefined);
        Collections.sort(results, Result.SEVERITY);
        Assert.assertThat(results, CoreMatchers.equalTo(Arrays.asList(passed, skipped, pending, undefined, ambiguous, failed)));
    }

    @Test
    public void passed_result_is_always_ok() {
        Result passedResult = new Result(PASSED, 0L, null);
        Assert.assertTrue(passedResult.isOk(isStrict(false)));
        Assert.assertTrue(passedResult.isOk(isStrict(true)));
    }

    @Test
    public void skipped_result_is_always_ok() {
        Result skippedResult = new Result(SKIPPED, 0L, null);
        Assert.assertTrue(skippedResult.isOk(isStrict(false)));
        Assert.assertTrue(skippedResult.isOk(isStrict(true)));
    }

    @Test
    public void failed_result_is_never_ok() {
        Result failedResult = new Result(FAILED, 0L, null);
        Assert.assertFalse(failedResult.isOk(isStrict(false)));
        Assert.assertFalse(failedResult.isOk(isStrict(true)));
    }

    @Test
    public void undefined_result_is_only_ok_when_not_strict() {
        Result undefinedResult = new Result(UNDEFINED, 0L, null);
        Assert.assertTrue(undefinedResult.isOk(isStrict(false)));
        Assert.assertFalse(undefinedResult.isOk(isStrict(true)));
    }

    @Test
    public void pending_result_is_only_ok_when_not_strict() {
        Result pendingResult = new Result(PENDING, 0L, null);
        Assert.assertTrue(pendingResult.isOk(isStrict(false)));
        Assert.assertFalse(pendingResult.isOk(isStrict(true)));
    }

    @Test
    public void is_query_returns_true_for_the_status_of_the_result_object() {
        int checkCount = 0;
        for (Result.Type status : Result.Type.values()) {
            Result result = new Result(status, 0L, null);
            Assert.assertTrue(result.is(result.getStatus()));
            checkCount += 1;
        }
        Assert.assertTrue("No checks performed", (checkCount > 0));
    }

    @Test
    public void is_query_returns_false_for_statuses_different_from_the_status_of_the_result_object() {
        int checkCount = 0;
        for (Result.Type resultStatus : Result.Type.values()) {
            Result result = new Result(resultStatus, 0L, null);
            for (Result.Type status : Result.Type.values()) {
                if (status != resultStatus) {
                    Assert.assertFalse(result.is(status));
                    checkCount += 1;
                }
            }
        }
        Assert.assertTrue("No checks performed", (checkCount > 0));
    }
}

