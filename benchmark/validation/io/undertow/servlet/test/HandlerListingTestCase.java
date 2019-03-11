/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.servlet.test;


import io.undertow.attribute.ExchangeAttribute;
import io.undertow.predicate.PredicateBuilder;
import io.undertow.server.handlers.builder.HandlerBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import org.junit.Test;


/**
 * not a real test, but used to generate documentation
 *
 * @author Stuart Douglas
 */
public class HandlerListingTestCase {
    @Test
    public void listHandlers() {
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("handlers");
        ArrayList<HandlerBuilder> builds = new ArrayList<>();
        for (HandlerBuilder i : ServiceLoader.load(HandlerBuilder.class, getClass().getClassLoader())) {
            builds.add(i);
        }
        Collections.sort(builds, new Comparator<HandlerBuilder>() {
            @Override
            public int compare(HandlerBuilder o1, HandlerBuilder o2) {
                return o1.name().compareTo(o2.name());
            }
        });
        for (HandlerBuilder handler : builds) {
            System.out.print(("|" + (handler.name())));
            System.out.print("\t|");
            List<String> parms = new ArrayList(handler.parameters().keySet());
            Collections.sort(parms);
            Iterator<String> it = parms.iterator();
            while (it.hasNext()) {
                String paramName = it.next();
                System.out.print((paramName + ": "));
                Class<?> obj = handler.parameters().get(paramName);
                if (obj == (ExchangeAttribute.class)) {
                    System.out.print("attribute");
                } else
                    if (obj.equals(ExchangeAttribute[].class)) {
                        System.out.print("attribute[]");
                    } else
                        if (obj.equals(String.class)) {
                            System.out.print("String");
                        } else
                            if (obj.equals(String[].class)) {
                                System.out.print("String[]");
                            } else
                                if (obj.equals(Long.class)) {
                                    System.out.print("Long");
                                } else
                                    if (obj.equals(Long[].class)) {
                                        System.out.print("Long[]");
                                    } else
                                        if (obj.equals(Boolean.class)) {
                                            System.out.print("Boolean");
                                        } else {
                                            System.out.print(obj);
                                        }






                if (((handler.requiredParameters()) != null) && (handler.requiredParameters().contains(paramName))) {
                    System.out.print(" (required)");
                }
                if (it.hasNext()) {
                    System.out.print(", ");
                }
            } 
            System.out.print("\t|");
            if ((handler.defaultParameter()) != null) {
                System.out.print(handler.defaultParameter());
            }
            System.out.print("\t|\n");
        }
    }

    @Test
    public void listPredicates() {
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("predicates");
        ArrayList<PredicateBuilder> builds = new ArrayList<PredicateBuilder>();
        for (PredicateBuilder i : ServiceLoader.load(PredicateBuilder.class, getClass().getClassLoader())) {
            builds.add(i);
        }
        Collections.sort(builds, new Comparator<PredicateBuilder>() {
            @Override
            public int compare(PredicateBuilder o1, PredicateBuilder o2) {
                return o1.name().compareTo(o2.name());
            }
        });
        for (PredicateBuilder handler : builds) {
            System.out.print(("|" + (handler.name())));
            System.out.print("\t|");
            List<String> parms = new ArrayList(handler.parameters().keySet());
            Collections.sort(parms);
            Iterator<String> it = parms.iterator();
            while (it.hasNext()) {
                String paramName = it.next();
                System.out.print((paramName + ": "));
                Class<?> obj = handler.parameters().get(paramName);
                if (obj == (ExchangeAttribute.class)) {
                    System.out.print("attribute");
                } else
                    if (obj.equals(ExchangeAttribute[].class)) {
                        System.out.print("attribute[]");
                    } else
                        if (obj.equals(String.class)) {
                            System.out.print("String");
                        } else
                            if (obj.equals(String[].class)) {
                                System.out.print("String[]");
                            } else
                                if (obj.equals(Long.class)) {
                                    System.out.print("Long");
                                } else
                                    if (obj.equals(Long[].class)) {
                                        System.out.print("Long[]");
                                    } else
                                        if (obj.equals(Boolean.class)) {
                                            System.out.print("Boolean");
                                        } else {
                                            System.out.print(obj);
                                        }






                if (handler.requiredParameters().contains(paramName)) {
                    System.out.print(" (required)");
                }
                if (it.hasNext()) {
                    System.out.print(", ");
                }
            } 
            System.out.print("\t|");
            if ((handler.defaultParameter()) != null) {
                System.out.print(handler.defaultParameter());
            }
            System.out.print("\t|\n");
        }
    }
}

