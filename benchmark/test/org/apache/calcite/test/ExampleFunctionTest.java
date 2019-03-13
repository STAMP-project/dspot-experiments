/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.test;


import java.lang.reflect.Method;
import java.sql.SQLException;
import org.apache.calcite.example.maze.MazeTable;
import org.apache.calcite.linq4j.tree.Types;
import org.junit.Test;


/**
 * Unit tests for example user-defined functions.
 */
public class ExampleFunctionTest {
    public static final Method MAZE_METHOD = Types.lookupMethod(MazeTable.class, "generate", int.class, int.class, int.class);

    public static final Method SOLVE_METHOD = Types.lookupMethod(MazeTable.class, "solve", int.class, int.class, int.class);

    /**
     * Unit test for {@link MazeTable}.
     */
    @Test
    public void testMazeTableFunction() throws ClassNotFoundException, SQLException {
        final String maze = "" + (((((("+--+--+--+--+--+\n" + "|        |     |\n") + "+--+  +--+--+  +\n") + "|     |  |     |\n") + "+  +--+  +--+  +\n") + "|              |\n") + "+--+--+--+--+--+\n");
        checkMazeTableFunction(false, maze);
    }

    /**
     * Unit test for {@link MazeTable}.
     */
    @Test
    public void testMazeTableFunctionWithSolution() throws ClassNotFoundException, SQLException {
        final String maze = "" + (((((("+--+--+--+--+--+\n" + "|*  *    |     |\n") + "+--+  +--+--+  +\n") + "|*  * |  |     |\n") + "+  +--+  +--+  +\n") + "|*  *  *  *  * |\n") + "+--+--+--+--+--+\n");
        checkMazeTableFunction(true, maze);
    }
}

/**
 * End ExampleFunctionTest.java
 */
