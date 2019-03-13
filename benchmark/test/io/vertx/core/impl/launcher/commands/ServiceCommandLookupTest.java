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


import io.vertx.core.impl.launcher.ServiceCommandFactoryLoader;
import io.vertx.core.spi.launcher.CommandFactory;
import java.util.Collection;
import org.junit.Test;


public class ServiceCommandLookupTest {
    ServiceCommandFactoryLoader loader = new ServiceCommandFactoryLoader();

    @Test
    public void testLookup() throws Exception {
        Collection<CommandFactory<?>> commands = loader.lookup();
        ensureCommand(commands, "run");
        ensureCommand(commands, "bare");
        ensureCommand(commands, "version");
        ensureCommand(commands, "list");
        ensureCommand(commands, "start");
        ensureCommand(commands, "stop");
    }
}

