/**
 * Copyright 2018 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package azkaban.dag;


import Status.BLOCKED;
import Status.CANCELED;
import Status.DISABLED;
import Status.FAILURE;
import Status.KILLING;
import Status.RUNNING;
import Status.SUCCESS;
import org.junit.Test;
import org.mockito.Mockito;

import static Status.TERMINAL_STATES;


/**
 * Tests the dag state ( including its nodes' states) transitions.
 *
 * Focuses on how the dag state changes in response to one external request.
 */
public class DagTest {
    private final DagProcessor mockDagProcessor = Mockito.mock(DagProcessor.class);

    private final Dag testFlow = new Dag("fa", this.mockDagProcessor);

    @Test
    public void dag_finish_with_only_disabled_nodes() {
        final Node aNode = createAndAddNode("a");
        aNode.setStatus(DISABLED);
        this.testFlow.start();
        assertThat(aNode.getStatus()).isEqualTo(DISABLED);
        assertThat(this.testFlow.getStatus()).isEqualTo(SUCCESS);
    }

    @Test
    public void running_nodes_can_be_killed() {
        final Node aNode = createAndAddNode("a");
        aNode.setStatus(RUNNING);
        this.testFlow.setStatus(RUNNING);
        this.testFlow.kill();
        assertThat(aNode.getStatus()).isEqualTo(KILLING);
        assertThat(this.testFlow.getStatus()).isEqualTo(KILLING);
    }

    @Test
    public void kill_node_in_terminal_state_should_have_no_effect() {
        for (final Status status : TERMINAL_STATES) {
            kill_dag_in_this_state_should_have_no_effect(status);
        }
    }

    @Test
    public void kill_node_in_killing_state_should_have_no_effect() {
        kill_dag_in_this_state_should_have_no_effect(KILLING);
    }

    /**
     * Tests ready nodes are canceled when the dag is killed.
     */
    @Test
    public void waiting_nodes_are_canceled_when_killed() {
        final Node aNode = createAndAddNode("a");
        aNode.setStatus(RUNNING);
        final Node bNode = createAndAddNode("b");
        bNode.addParent(aNode);
        this.testFlow.setStatus(RUNNING);
        this.testFlow.kill();
        assertThat(aNode.getStatus()).isEqualTo(KILLING);
        assertThat(bNode.getStatus()).isEqualTo(CANCELED);
        assertThat(this.testFlow.getStatus()).isEqualTo(KILLING);
    }

    /**
     * Tests multiple ready nodes are canceled when the dag is killed.
     * <pre>
     *     a (running)
     *    / \
     *   b   c
     *        \
     *         d
     * </pre>
     */
    @Test
    public void multiple_waiting_nodes_are_canceled_when_killed() {
        final Node aNode = createAndAddNode("a");
        aNode.setStatus(RUNNING);
        final Node bNode = createAndAddNode("b");
        bNode.addParent(aNode);
        final Node cNode = createAndAddNode("c");
        cNode.addParent(aNode);
        final Node dNode = createAndAddNode("d");
        dNode.addParent(cNode);
        this.testFlow.setStatus(RUNNING);
        this.testFlow.kill();
        assertThat(aNode.getStatus()).isEqualTo(KILLING);
        assertThat(bNode.getStatus()).isEqualTo(CANCELED);
        assertThat(dNode.getStatus()).isEqualTo(CANCELED);
        assertThat(dNode.getStatus()).isEqualTo(CANCELED);
        assertThat(this.testFlow.getStatus()).isEqualTo(KILLING);
    }

    /**
     * Tests multiple ready nodes are canceled when the parent node failed.
     * <pre>
     *     a (running)
     *     |
     *     b
     *     |
     *     c
     * </pre>
     */
    @Test
    public void multiple_waiting_children_are_canceled_when_parent_failed() {
        final Node aNode = createAndAddNode("a");
        aNode.setStatus(RUNNING);
        final Node bNode = createAndAddNode("b");
        bNode.addParent(aNode);
        final Node cNode = createAndAddNode("c");
        cNode.addParent(bNode);
        this.testFlow.setStatus(RUNNING);
        aNode.markFailed();
        assertThat(bNode.getStatus()).isEqualTo(CANCELED);
        assertThat(cNode.getStatus()).isEqualTo(CANCELED);
    }

    /**
     * Tests blocked nodes are canceled when the dag is killed.
     */
    @Test
    public void blocked_nodes_are_canceled_when_killed() {
        final Node aNode = createAndAddNode("a");
        aNode.setStatus(RUNNING);
        final Node bNode = createAndAddNode("b");
        bNode.addParent(aNode);
        bNode.setStatus(BLOCKED);
        this.testFlow.setStatus(RUNNING);
        this.testFlow.kill();
        assertThat(aNode.getStatus()).isEqualTo(KILLING);
        assertThat(bNode.getStatus()).isEqualTo(CANCELED);
    }

    /**
     * Tests success nodes' states remain the same when the dag is killed.
     * <pre>
     *     a (success)
     *    /
     *   b (running)
     * </pre>
     */
    @Test
    public void success_node_state_remain_the_same_when_killed() {
        final Node aNode = createAndAddNode("a");
        aNode.setStatus(SUCCESS);
        final Node bNode = createAndAddNode("b");
        bNode.setStatus(RUNNING);
        bNode.addParent(aNode);
        this.testFlow.kill();
        assertThat(aNode.getStatus()).isEqualTo(SUCCESS);
        assertThat(bNode.getStatus()).isEqualTo(KILLING);
    }

    /**
     * Tests failed nodes' states remain the same when the dag is killed.
     * This can happen when running jobs are allowed to finish when a node fails.
     *
     * <pre>
     *  a (running)   b (failure)
     * </pre>
     */
    @Test
    public void failed_node_state_remain_the_same_when_killed() {
        final Node aNode = createAndAddNode("a");
        aNode.setStatus(RUNNING);
        final Node bNode = createAndAddNode("b");
        bNode.setStatus(FAILURE);
        this.testFlow.kill();
        assertThat(aNode.getStatus()).isEqualTo(KILLING);
        assertThat(bNode.getStatus()).isEqualTo(FAILURE);
    }
}

