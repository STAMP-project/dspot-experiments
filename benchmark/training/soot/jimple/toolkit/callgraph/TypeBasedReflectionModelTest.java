/**
 * -
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vall?e-Rai and others
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package soot.jimple.toolkit.callgraph;


import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


/**
 * This class contains tests for {@link soot.jimple.toolkits.callgraph.OnFlyCallGraphBuilder.TypeBasedReflectionModel}.
 *
 * @author Manuel Benz
created on 01.08.17
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TypeBasedReflectionModelTest {
    /**
     * Checks if the reflection model accepts a string constant as base of a call to Method.invoke.
     */
    @Test
    public void constantBase() {
        genericLocalVsStringConstantTest(true);
    }

    /**
     * Checks if the reflection model accepts a local as base of a call to Method.invoke.
     */
    @Test
    public void localBase() {
        genericLocalVsStringConstantTest(false);
    }

    @Test
    public void staticBase() {
        // TODO
    }
}

