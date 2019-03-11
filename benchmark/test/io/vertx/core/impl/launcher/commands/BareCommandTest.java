/**
 * Copyright (c) 2011-2017 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package io.vertx.core.impl.launcher.commands;


import java.io.IOException;
import java.util.function.BooleanSupplier;
import org.junit.Test;


/**
 * Test the bare command.
 */
public class BareCommandTest extends CommandTestBase {
    @Test
    public void testRegularBareCommand() throws IOException, InterruptedException {
        record();
        cli.dispatch(new String[]{ "bare" });
        assertWaitUntil(() -> error.toString().contains("A quorum has been obtained."));
        assertThatVertxInstanceHasBeenCreated();
        stop();
        assertThat(error.toString()).contains("Starting clustering...").contains("No cluster-host specified").contains("Any deploymentIDs waiting on a quorum will now be deployed");
    }

    @Test
    public void testOldBare() throws IOException, InterruptedException {
        record();
        cli.dispatch(new String[]{ "-ha" });
        assertWaitUntil(() -> error.toString().contains("A quorum has been obtained."));
        stop();
        assertThat(error.toString()).contains("Starting clustering...").contains("No cluster-host specified").contains("Any deploymentIDs waiting on a quorum will now be deployed");
    }

    @Test
    public void testRegularBareCommandWithClusterHost() {
        record();
        cli.dispatch(new String[]{ "bare", "-cluster-host", "127.0.0.1" });
        assertWaitUntil(() -> error.toString().contains("A quorum has been obtained."));
        assertThatVertxInstanceHasBeenCreated();
        stop();
        assertThat(error.toString()).contains("Starting clustering...").doesNotContain("No cluster-host specified").contains("Any deploymentIDs waiting on a quorum will now be deployed");
    }

    @Test
    public void testOldBareWithClusterHost() throws IOException, InterruptedException {
        record();
        cli.dispatch(new String[]{ "-ha", "-cluster-host", "127.0.0.1" });
        assertWaitUntil(() -> error.toString().contains("A quorum has been obtained."));
        stop();
        assertThat(error.toString()).contains("Starting clustering...").doesNotContain("No cluster-host specified").contains("Any deploymentIDs waiting on a quorum will now be deployed");
    }
}

