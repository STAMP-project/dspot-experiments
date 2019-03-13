/**
 * Copyright ? 2013 Esko Luontola <www.orfjackal.net>
 */
/**
 * This software is released under the Apache License 2.0.
 */
/**
 * The license text is at http://www.apache.org/licenses/LICENSE-2.0
 */
package net.orfjackal.retrolambda.test;


import java.util.concurrent.Callable;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class OptimizationsTest {
    @Test
    public void lambdas_which_capture_variables_get_a_new_instance_every_time() {
        Callable<Integer> lambda1 = OptimizationsTest.createStatefulLambda();
        Callable<Integer> lambda2 = OptimizationsTest.createStatefulLambda();
        MatcherAssert.assertThat(lambda1, is(not(sameInstance(lambda2))));
    }

    @Test
    public void lambdas_which_do_not_capture_variables_have_only_one_singleton_instance() {
        Callable<Integer> lambda1 = OptimizationsTest.createStatelessLambda();
        Callable<Integer> lambda2 = OptimizationsTest.createStatelessLambda();
        MatcherAssert.assertThat(lambda1, is(sameInstance(lambda2)));
    }
}

