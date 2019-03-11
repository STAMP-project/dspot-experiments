/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.master;


import alluxio.Registry;
import alluxio.Server;
import alluxio.grpc.GrpcService;
import alluxio.grpc.ServiceType;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public final class RegistryTest {
    @Rule
    public ExpectedException mThrown = ExpectedException.none();

    public abstract class TestServer implements Server<Void> {
        @Override
        @Nullable
        public Map<ServiceType, GrpcService> getServices() {
            return null;
        }

        @Override
        public void start(Void unused) throws IOException {
        }

        @Override
        public void stop() throws IOException {
        }
    }

    public class ServerA extends RegistryTest.TestServer {
        @Override
        public String getName() {
            return "A";
        }

        @Override
        public Set<Class<? extends Server>> getDependencies() {
            Set<Class<? extends Server>> deps = new HashSet<>();
            deps.add(RegistryTest.ServerB.class);
            return deps;
        }
    }

    public class ServerB extends RegistryTest.TestServer {
        @Override
        public String getName() {
            return "B";
        }

        @Override
        public Set<Class<? extends Server>> getDependencies() {
            Set<Class<? extends Server>> deps = new HashSet<>();
            deps.add(RegistryTest.ServerC.class);
            return deps;
        }
    }

    public class ServerC extends RegistryTest.TestServer {
        @Override
        public String getName() {
            return "C";
        }

        @Override
        public Set<Class<? extends Server>> getDependencies() {
            Set<Class<? extends Server>> deps = new HashSet<>();
            deps.add(RegistryTest.ServerD.class);
            return deps;
        }
    }

    public class ServerD extends RegistryTest.TestServer {
        @Override
        public String getName() {
            return "C";
        }

        @Override
        public Set<Class<? extends Server>> getDependencies() {
            Set<Class<? extends Server>> deps = new HashSet<>();
            deps.add(RegistryTest.ServerA.class);
            return deps;
        }
    }

    @Test
    public void registry() {
        List<RegistryTest.TestServer> masters = ImmutableList.of(new RegistryTest.ServerC(), new RegistryTest.ServerB(), new RegistryTest.ServerA());
        List<RegistryTest.TestServer[]> permutations = new ArrayList<>();
        computePermutations(masters.toArray(new RegistryTest.TestServer[masters.size()]), 0, permutations);
        // Make sure that the registry orders the masters independently of the order in which they
        // are registered.
        for (RegistryTest.TestServer[] permutation : permutations) {
            Registry<RegistryTest.TestServer, Void> registry = new Registry();
            for (RegistryTest.TestServer server : permutation) {
                registry.add(server.getClass(), server);
            }
            Assert.assertEquals(masters, registry.getServers());
        }
    }

    @Test
    public void cycle() {
        Registry<RegistryTest.TestServer, Void> registry = new Registry();
        registry.add(RegistryTest.ServerA.class, new RegistryTest.ServerA());
        registry.add(RegistryTest.ServerB.class, new RegistryTest.ServerB());
        registry.add(RegistryTest.ServerC.class, new RegistryTest.ServerC());
        registry.add(RegistryTest.ServerC.class, new RegistryTest.ServerD());
        mThrown.expect(RuntimeException.class);
        registry.getServers();
    }

    @Test
    public void unavailable() {
        Registry<RegistryTest.TestServer, Void> registry = new Registry();
        mThrown.expect(Exception.class);
        mThrown.expectMessage("Timed out");
        mThrown.expectMessage("ServerB");
        registry.get(RegistryTest.ServerB.class, 100);
    }
}

