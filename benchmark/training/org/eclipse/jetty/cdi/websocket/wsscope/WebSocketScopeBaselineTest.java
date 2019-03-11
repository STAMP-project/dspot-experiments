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
package org.eclipse.jetty.cdi.websocket.wsscope;


import org.eclipse.jetty.cdi.core.ScopedInstance;
import org.eclipse.jetty.cdi.websocket.WebSocketScopeContext;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.Test;


public class WebSocketScopeBaselineTest {
    private static Weld weld;

    private static WeldContainer container;

    /**
     * Test behavior of {@link WebSocketScope} in basic operation.
     * <p>
     * Food is declared as part of WebSocketScope, and as such, only 1 instance of it can exist.
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testScopeBehavior() throws Exception {
        ScopedInstance<WebSocketScopeContext> wsScopeBean = WebSocketScopeBaselineTest.newInstance(WebSocketScopeContext.class);
        WebSocketScopeContext wsScope = wsScopeBean.instance;
        wsScope.create();
        Meal meal1;
        try {
            wsScope.begin();
            ScopedInstance<Meal> meal1Bean = WebSocketScopeBaselineTest.newInstance(Meal.class);
            meal1 = meal1Bean.instance;
            ScopedInstance<Meal> meal2Bean = WebSocketScopeBaselineTest.newInstance(Meal.class);
            Meal meal2 = meal2Bean.instance;
            MatcherAssert.assertThat("Meals are not the same", meal1, Matchers.not(Matchers.sameInstance(meal2)));
            MatcherAssert.assertThat("Meal 1 Entree Constructed", meal1.getEntree().isConstructed(), Matchers.is(true));
            MatcherAssert.assertThat("Meal 1 Side Constructed", meal1.getSide().isConstructed(), Matchers.is(true));
            /* Since Food is annotated with @WebSocketScope, there can only be one instance of it
            in use with the 2 Meal objects.
             */
            MatcherAssert.assertThat("Meal parts not the same", meal1.getEntree(), Matchers.sameInstance(meal1.getSide()));
            MatcherAssert.assertThat("Meal entrees are the same", meal1.getEntree(), Matchers.sameInstance(meal2.getEntree()));
            MatcherAssert.assertThat("Meal sides are the same", meal1.getSide(), Matchers.sameInstance(meal2.getSide()));
            meal1Bean.destroy();
            meal2Bean.destroy();
        } finally {
            wsScope.end();
        }
        Food entree1 = meal1.getEntree();
        Food side1 = meal1.getSide();
        MatcherAssert.assertThat("Meal 1 entree destroyed", entree1.isDestroyed(), Matchers.is(false));
        MatcherAssert.assertThat("Meal 1 side destroyed", side1.isDestroyed(), Matchers.is(false));
        wsScope.destroy();
        // assertThat("Meal 1 entree destroyed",entree1.isDestroyed(),is(true));
        // assertThat("Meal 1 side destroyed",side1.isDestroyed(),is(true));
        wsScopeBean.destroy();
    }
}

