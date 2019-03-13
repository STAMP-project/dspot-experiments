/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.testsuite.federation.storage;


import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MapCollectTest {
    public static class UserSessionObject {
        public String id;

        public String realm;

        public Set<String> clients = new HashSet<>();

        public UserSessionObject(String realm, String... clients) {
            this.id = UUID.randomUUID().toString();
            this.realm = realm;
            for (String c : clients)
                this.clients.add(c);

        }
    }

    public static class RealmFilter implements Predicate<MapCollectTest.UserSessionObject> {
        protected String realm;

        public RealmFilter(String realm) {
            this.realm = realm;
        }

        @Override
        public boolean test(MapCollectTest.UserSessionObject entry) {
            return entry.realm.equals(realm);
        }

        public static MapCollectTest.RealmFilter create(String realm) {
            return new MapCollectTest.RealmFilter(realm);
        }
    }

    @Test
    public void testMe() throws Exception {
        List<MapCollectTest.UserSessionObject> list = Arrays.asList(new MapCollectTest.UserSessionObject("realm1", "a", "b"), new MapCollectTest.UserSessionObject("realm1", "a", "c"), new MapCollectTest.UserSessionObject("realm1", "a", "d"), new MapCollectTest.UserSessionObject("realm1", "a", "b"), new MapCollectTest.UserSessionObject("realm2", "a", "b"), new MapCollectTest.UserSessionObject("realm2", "a", "c"), new MapCollectTest.UserSessionObject("realm2", "a", "b"));
        Map<String, Long> result = list.stream().collect(Collectors.groupingBy(( s) -> s.realm, Collectors.summingLong(( i) -> 1)));
        for (Map.Entry<String, Long> entry : result.entrySet()) {
            System.out.println((((entry.getKey()) + ":") + (entry.getValue())));
        }
        result = list.stream().filter(MapCollectTest.RealmFilter.create("realm1")).map(( s) -> s.clients).flatMap(( c) -> c.stream()).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        for (Map.Entry<String, Long> entry : result.entrySet()) {
            System.out.println((((entry.getKey()) + ":") + (entry.getValue())));
        }
    }
}

