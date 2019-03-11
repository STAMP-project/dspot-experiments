/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.cdi.websocket.basicscope;


import org.eclipse.jetty.cdi.core.ScopedInstance;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.Test;


public class ScopeBasicsTest {
    private static Weld weld;

    private static WeldContainer container;

    /**
     * Validation of Scope / Inject logic on non-websocket-scoped classes
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testBasicBehavior() throws Exception {
        ScopedInstance<Meal> meal1Bean = ScopeBasicsTest.newInstance(Meal.class);
        Meal meal1 = meal1Bean.instance;
        ScopedInstance<Meal> meal2Bean = ScopeBasicsTest.newInstance(Meal.class);
        Meal meal2 = meal2Bean.instance;
        MatcherAssert.assertThat("Meals are not the same", meal1, Matchers.not(Matchers.sameInstance(meal2)));
        MatcherAssert.assertThat("Meal 1 Entree Constructed", meal1.getEntree().isConstructed(), Matchers.is(true));
        MatcherAssert.assertThat("Meal 1 Side Constructed", meal1.getSide().isConstructed(), Matchers.is(true));
        MatcherAssert.assertThat("Meal parts not the same", meal1.getEntree(), Matchers.not(Matchers.sameInstance(meal1.getSide())));
        MatcherAssert.assertThat("Meal entrees are the same", meal1.getEntree(), Matchers.not(Matchers.sameInstance(meal2.getEntree())));
        MatcherAssert.assertThat("Meal sides are the same", meal1.getSide(), Matchers.not(Matchers.sameInstance(meal2.getSide())));
    }
}

