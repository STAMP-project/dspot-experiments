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
package org.sonar.api.utils.dag;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class DirectAcyclicGraphTest {
    @Test(expected = CyclicDependenciesException.class)
    public void shouldFailIfCyclicDependencies() {
        DirectAcyclicGraph dag = new DirectAcyclicGraph();
        dag.add("m3", "m1");
        dag.add("m1", "m3");
        dag.sort();
    }

    @Test
    public void sortByDependencies() {
        DirectAcyclicGraph dag = new DirectAcyclicGraph();
        dag.add("m1", "m3");
        dag.add("m3", "m2");
        dag.add("m4");
        dag.add("m2", "m4");
        List result = dag.sort();
        Assert.assertEquals(4, result.size());
        Assert.assertEquals("m4", result.get(0));
        Assert.assertEquals("m2", result.get(1));
        Assert.assertEquals("m3", result.get(2));
        Assert.assertEquals("m1", result.get(3));
    }

    @Test
    public void keepInsertionOrderWhenNoDependencies() {
        DirectAcyclicGraph dag = new DirectAcyclicGraph("m3", "m2", "m1");
        List result = dag.sort();
        Assert.assertEquals(3, result.size());
        Assert.assertEquals("m3", result.get(0));
        Assert.assertEquals("m2", result.get(1));
        Assert.assertEquals("m1", result.get(2));
    }

    @Test
    public void complexGraph() {
        DirectAcyclicGraph dag = new DirectAcyclicGraph();
        dag.add("m2", "m4");
        dag.add("m1", "m2", "m3");
        dag.add("m3", "m2");
        List result = dag.sort();
        Assert.assertEquals(4, result.size());
        Assert.assertEquals("m4", result.get(0));
        Assert.assertEquals("m2", result.get(1));
        Assert.assertEquals("m3", result.get(2));
        Assert.assertEquals("m1", result.get(3));
    }

    @Test
    public void aNodeShouldDependOnItself() {
        DirectAcyclicGraph graph = new DirectAcyclicGraph();
        graph.add("m1", "m1");
        List result = graph.sort();
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("m1", result.get(0));
    }
}

