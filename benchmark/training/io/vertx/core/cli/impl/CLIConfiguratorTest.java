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
package io.vertx.core.cli.impl;


import io.vertx.core.cli.Option;
import io.vertx.core.cli.annotations.Argument;
import io.vertx.core.cli.converters.Person;
import io.vertx.core.cli.converters.Person2;
import io.vertx.core.cli.converters.Person3;
import io.vertx.core.cli.converters.Person4;
import io.vertx.core.cli.converters.Person4Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;

import static java.lang.Thread.State.BLOCKED;
import static java.lang.Thread.State.NEW;
import static java.lang.Thread.State.RUNNABLE;


public class CLIConfiguratorTest {
    @Test
    public void testHelloCLIFromClass() {
        CLI command = CLIConfigurator.define(HelloClI.class);
        assertThat(command.getOptions()).hasSize(1);
        TypedOption option = ((TypedOption) (find(command.getOptions(), "name")));
        assertThat(option.getLongName()).isEqualToIgnoringCase("name");
        assertThat(option.getShortName()).isEqualToIgnoringCase("n");
        assertThat(option.getType()).isEqualTo(String.class);
        assertThat(option.getArgName()).isEqualTo("name");
        assertThat(option.getDescription()).isEqualToIgnoringCase("your name");
        assertThat(option.getDefaultValue()).isNull();
        assertThat(option.acceptValue()).isTrue();
        assertThat(option.isMultiValued()).isFalse();
        assertThat(option.isRequired()).isTrue();
    }

    @Test
    public void testUsage() {
        CLI command = CLIConfigurator.define(HelloClI.class);
        StringBuilder builder = new StringBuilder();
        command.usage(builder);
        assertThat(builder.toString()).containsIgnoringCase("Usage: hello -n <name>").containsIgnoringCase("A command saying hello.").containsIgnoringCase("A simple cli to wish you a good day. Pass your name with `--name`").containsIgnoringCase(" -n,--name <name>   your name");
    }

    @Name("test")
    public static class CommandForDefaultValueTest {
        @Option(longName = "option", shortName = "o")
        @DefaultValue("bar")
        public void setFoo(String foo) {
        }
    }

    @Test
    public void testOptionsWithDefaultValue() {
        CLI cli = CLIConfigurator.define(CLIConfiguratorTest.CommandForDefaultValueTest.class);
        assertThat(cli.getOptions()).hasSize(1);
        assertThat(find(cli.getOptions(), "option").getDefaultValue()).isEqualTo("bar");
        assertThat(find(cli.getOptions(), "option").getName()).isEqualTo("option");
    }

    @Name("test")
    public static class CommandForDescriptionTest {
        @Option(longName = "option", shortName = "o")
        @Description("This option is awesome")
        public void setFoo(String foo) {
        }
    }

    @Test
    public void testOptionsWithDescription() {
        CLI cli = CLIConfigurator.define(CLIConfiguratorTest.CommandForDescriptionTest.class);
        assertThat(cli.getOptions()).hasSize(1);
        assertThat(find(cli.getOptions(), "option").getDescription()).isEqualTo("This option is awesome");
    }

    @Name("test")
    public static class CommandForParsedAsList {
        @Option(longName = "option", shortName = "o")
        @ParsedAsList(separator = ":")
        public void setFoo(List<String> foo) {
        }
    }

    @Test
    public void testOptionsParsedAsList() {
        CLI command = CLIConfigurator.define(CLIConfiguratorTest.CommandForParsedAsList.class);
        assertThat(command.getOptions()).hasSize(1);
        assertThat(getListSeparator()).isEqualTo(":");
        assertThat(find(command.getOptions(), "option").isMultiValued()).isTrue();
        assertThat(getType()).isEqualTo(String.class);
    }

    @Name("test")
    public static class CommandForTypeExtractTest {
        @Option(longName = "list", shortName = "l")
        public void setFoo(List<String> list) {
        }

        @Option(longName = "set", shortName = "s")
        public void setFoo(Set<Character> set) {
        }

        @Option(longName = "collection", shortName = "c")
        public void setFoo(Collection<Integer> collection) {
        }

        @Option(longName = "tree", shortName = "t")
        public void setFoo(TreeSet<String> list) {
        }

        @Option(longName = "al", shortName = "al")
        public void setFoo(ArrayList<String> list) {
        }

        @Option(longName = "array", shortName = "a")
        public void setFoo(int[] list) {
        }
    }

    @Test
    public void testTypeExtraction() {
        CLI command = CLIConfigurator.define(CLIConfiguratorTest.CommandForTypeExtractTest.class);
        assertThat(command.getOptions()).hasSize(6);
        TypedOption model = ((TypedOption) (find(command.getOptions(), "list")));
        assertThat(model.getType()).isEqualTo(String.class);
        assertThat(model.isMultiValued()).isTrue();
        model = ((TypedOption) (find(command.getOptions(), "set")));
        assertThat(model.getType()).isEqualTo(Character.class);
        assertThat(model.isMultiValued()).isTrue();
        model = ((TypedOption) (find(command.getOptions(), "collection")));
        assertThat(model.getType()).isEqualTo(Integer.class);
        assertThat(model.isMultiValued()).isTrue();
        model = ((TypedOption) (find(command.getOptions(), "tree")));
        assertThat(model.getType()).isEqualTo(String.class);
        assertThat(model.isMultiValued()).isTrue();
        model = ((TypedOption) (find(command.getOptions(), "al")));
        assertThat(model.getType()).isEqualTo(String.class);
        assertThat(model.isMultiValued()).isTrue();
        model = ((TypedOption) (find(command.getOptions(), "array")));
        assertThat(model.getType()).isEqualTo(Integer.TYPE);
        assertThat(model.isMultiValued()).isTrue();
    }

    @Test
    public void testInjectionOfString() throws CLIException {
        HelloClI command = new HelloClI();
        CLI cli = CLIConfigurator.define(HelloClI.class);
        CommandLine evaluatedCLI = cli.parse(Arrays.asList("--name", "vert.x"));
        CLIConfigurator.inject(evaluatedCLI, command);
        assertThat(command.run()).isEqualToIgnoringCase("Hello vert.x");
        assertThat(command.name).isEqualToIgnoringCase("vert.x");
    }

    @Test
    public void testSingleValueInjection() throws CLIException {
        CLIConfiguratorTest.CLIWithSingleValue command = new CLIConfiguratorTest.CLIWithSingleValue();
        CLI cli = CLIConfigurator.define(command.getClass());
        CommandLine evaluatedCLI = parse(cli, "--boolean", "--short=1", "--byte=1", "--int=1", "--long=1", "--double=1.1", "--float=1.1", "--char=c", "--string=hello");
        CLIConfigurator.inject(evaluatedCLI, command);
        assertThat(command.aBoolean).isTrue();
        assertThat(command.aShort).isEqualTo(((short) (1)));
        assertThat(command.aByte).isEqualTo(((byte) (1)));
        assertThat(command.anInt).isEqualTo(1);
        assertThat(command.aLong).isEqualTo(1L);
        assertThat(command.aDouble).isEqualTo(1.1);
        assertThat(command.aFloat).isEqualTo(1.1F);
        assertThat(command.aChar).isEqualTo('c');
        assertThat(command.aString).isEqualTo("hello");
        evaluatedCLI = parse(cli, "--boolean2", "--short2=1", "--byte2=1", "--int2=1", "--long2=1", "--double2=1.1", "--float2=1.1", "--char2=c", "--string=hello");
        CLIConfigurator.inject(evaluatedCLI, command);
        assertThat(command.anotherBoolean).isTrue();
        assertThat(command.anotherShort).isEqualTo(((short) (1)));
        assertThat(command.anotherByte).isEqualTo(((byte) (1)));
        assertThat(command.anotherInt).isEqualTo(1);
        assertThat(command.anotherLong).isEqualTo(1L);
        assertThat(command.anotherDouble).isEqualTo(1.1);
        assertThat(command.anotherFloat).isEqualTo(1.1F);
        assertThat(command.anotherChar).isEqualTo('c');
        assertThat(command.aString).isEqualTo("hello");
        evaluatedCLI = parse(cli, "--state=NEW");
        CLIConfigurator.inject(evaluatedCLI, command);
        assertThat(command.aState).isEqualTo(NEW);
        evaluatedCLI = parse(cli, "--person=vert.x");
        CLIConfigurator.inject(evaluatedCLI, command);
        assertThat(command.aPerson.name).isEqualTo("vert.x");
        evaluatedCLI = parse(cli, "--person2=vert.x");
        CLIConfigurator.inject(evaluatedCLI, command);
        assertThat(command.anotherPerson.name).isEqualTo("vert.x");
        evaluatedCLI = parse(cli, "--person3=vert.x");
        CLIConfigurator.inject(evaluatedCLI, command);
        assertThat(command.aThirdPerson.name).isEqualTo("vert.x");
        evaluatedCLI = parse(cli, "--person4=bob,morane");
        CLIConfigurator.inject(evaluatedCLI, command);
        assertThat(command.aFourthPerson.first).isEqualTo("bob");
        assertThat(command.aFourthPerson.last).isEqualTo("morane");
    }

    @Test
    public void testMultiValuesInjection() throws CLIException {
        CLIConfiguratorTest.CLIWithMultipleValues command = new CLIConfiguratorTest.CLIWithMultipleValues();
        CLI cli = CLIConfigurator.define(command.getClass());
        CommandLine evaluatedCLI = parse(cli, "--persons=x", "--persons", "y", "z");
        CLIConfigurator.inject(evaluatedCLI, command);
        assertThat(command.persons).hasSize(3);
        evaluatedCLI = parse(cli, "--persons2=x", "--persons2", "y", "z");
        CLIConfigurator.inject(evaluatedCLI, command);
        assertThat(command.persons2).hasSize(3);
        evaluatedCLI = parse(cli, "--persons3=x", "--persons3", "y", "z");
        CLIConfigurator.inject(evaluatedCLI, command);
        assertThat(command.persons3).hasSize(3);
        evaluatedCLI = parse(cli, "--persons4=x:y:z");
        CLIConfigurator.inject(evaluatedCLI, command);
        assertThat(command.persons4).hasSize(3);
        evaluatedCLI = parse(cli, "--states=NEW", "--states", "BLOCKED", "RUNNABLE");
        CLIConfigurator.inject(evaluatedCLI, command);
        assertThat(command.states).hasSize(3).containsExactly(NEW, BLOCKED, RUNNABLE);
        evaluatedCLI = parse(cli, "--ints=1", "--ints", "2", "3");
        CLIConfigurator.inject(evaluatedCLI, command);
        assertThat(command.ints).hasSize(3).containsExactly(1, 2, 3);
        evaluatedCLI = parse(cli, "--shorts=1", "--shorts", "2", "3");
        CLIConfigurator.inject(evaluatedCLI, command);
        assertThat(command.shorts).hasSize(3).containsExactly(((short) (1)), ((short) (2)), ((short) (3)));
        evaluatedCLI = parse(cli, "--strings=a");
        CLIConfigurator.inject(evaluatedCLI, command);
        assertThat(command.strings).hasSize(1).containsExactly("a");
        evaluatedCLI = parse(cli, "--doubles=1", "--doubles", "2.2", "3.3");
        CLIConfigurator.inject(evaluatedCLI, command);
        assertThat(command.doubles).hasSize(3).containsExactly(1.0, 2.2, 3.3);
    }

    @Name("test")
    public static class CommandForArgumentInjectionTest {
        AtomicReference<String> reference = new AtomicReference<>();

        @Argument(index = 0)
        public void setX(String s) {
            reference.set(s);
        }
    }

    @Test
    public void testArgumentInjection() throws CLIException {
        CLIConfiguratorTest.CommandForArgumentInjectionTest command = new CLIConfiguratorTest.CommandForArgumentInjectionTest();
        CLI cli = CLIConfigurator.define(command.getClass()).setName("test");
        CommandLine evaluatedCLI = parse(cli, "foo");
        CLIConfigurator.inject(evaluatedCLI, command);
        assertThat(command.reference.get()).isEqualTo("foo");
    }

    @Name("test")
    public class CommandForConvertedValueTest {
        AtomicReference<Person4> reference = new AtomicReference<>();

        @Argument(index = 0, required = false)
        @DefaultValue("Bill,Balantine")
        @ConvertedBy(Person4Converter.class)
        public void setX(Person4 s) {
            reference.set(s);
        }
    }

    @Test
    public void testArgumentInjectionWithConvertedByAndDefaultValue() throws CLIException {
        CLIConfiguratorTest.CommandForConvertedValueTest command = new CLIConfiguratorTest.CommandForConvertedValueTest();
        CLI cli = CLIConfigurator.define(command.getClass()).setName("test");
        CommandLine evaluatedCLI = parse(cli, "Bob,Morane");
        CLIConfigurator.inject(evaluatedCLI, command);
        assertThat(command.reference.get().first).isEqualTo("Bob");
        assertThat(command.reference.get().last).isEqualTo("Morane");
        evaluatedCLI = parse(cli);
        CLIConfigurator.inject(evaluatedCLI, command);
        assertThat(command.reference.get().first).isEqualTo("Bill");
        assertThat(command.reference.get().last).isEqualTo("Balantine");
    }

    @Name("test")
    public static class CommandForMultipleArgumentTest {
        AtomicReference<String> x = new AtomicReference<>();

        AtomicReference<Integer> y = new AtomicReference<>();

        @Argument(index = 0)
        public void setX(String s) {
            x.set(s);
        }

        @Argument(index = 1)
        public void setY(int s) {
            y.set(s);
        }
    }

    @Test
    public void testArgumentInjectionWithSeveralArguments() throws CLIException {
        CLIConfiguratorTest.CommandForMultipleArgumentTest command = new CLIConfiguratorTest.CommandForMultipleArgumentTest();
        CLI cli = CLIConfigurator.define(command.getClass()).setName("test");
        CommandLine evaluatedCLI = parse(cli, "foo", "1");
        CLIConfigurator.inject(evaluatedCLI, command);
        assertThat(command.x.get()).isEqualTo("foo");
        assertThat(command.y.get()).isEqualTo(1);
    }

    @Name("test")
    public static class CommandWithDefaultValueOnArgument {
        AtomicReference<String> x = new AtomicReference<>();

        AtomicReference<Integer> y = new AtomicReference<>();

        @Argument(index = 0)
        public void setX(String s) {
            x.set(s);
        }

        @Argument(index = 1, required = false)
        @DefaultValue("25")
        public void setY(int s) {
            y.set(s);
        }
    }

    @Test
    public void testArgumentWithDefaultValue() throws CLIException {
        CLIConfiguratorTest.CommandWithDefaultValueOnArgument command = new CLIConfiguratorTest.CommandWithDefaultValueOnArgument();
        CLI cli = CLIConfigurator.define(command.getClass()).setName("test");
        CommandLine evaluatedCLI = parse(cli, "foo");
        CLIConfigurator.inject(evaluatedCLI, command);
        assertThat(command.x.get()).isEqualTo("foo");
        assertThat(command.y.get()).isEqualTo(25);
    }

    @Name("single")
    private class CLIWithSingleValue {
        String aString;

        Thread.State aState;

        boolean aBoolean;

        Boolean anotherBoolean;

        byte aByte;

        Byte anotherByte;

        char aChar;

        Character anotherChar;

        double aDouble;

        Double anotherDouble;

        float aFloat;

        Float anotherFloat;

        int anInt;

        Integer anotherInt;

        long aLong;

        Long anotherLong;

        short aShort;

        Short anotherShort;

        Person aPerson;

        Person2 anotherPerson;

        Person3 aThirdPerson;

        Person4 aFourthPerson;

        @Option(longName = "boolean", shortName = "Z", flag = true)
        public void setaBoolean(boolean aBoolean) {
            this.aBoolean = aBoolean;
        }

        @Option(longName = "byte", shortName = "B")
        public void setaByte(byte aByte) {
            this.aByte = aByte;
        }

        @Option(longName = "char", shortName = "C")
        public void setaChar(char aChar) {
            this.aChar = aChar;
        }

        @Option(longName = "double", shortName = "D")
        public void setaDouble(double aDouble) {
            this.aDouble = aDouble;
        }

        @Option(longName = "float", shortName = "F")
        public void setaFloat(float aFloat) {
            this.aFloat = aFloat;
        }

        @Option(longName = "long", shortName = "J")
        public void setaLong(long aLong) {
            this.aLong = aLong;
        }

        @Option(longName = "int", shortName = "I")
        public void setAnInt(int anInt) {
            this.anInt = anInt;
        }

        @Option(longName = "boolean2", shortName = "AZ", flag = true)
        public void setAnotherBoolean(Boolean anotherBoolean) {
            this.anotherBoolean = anotherBoolean;
        }

        @Option(longName = "byte2", shortName = "AB")
        public void setAnotherByte(Byte anotherByte) {
            this.anotherByte = anotherByte;
        }

        @Option(longName = "char2", shortName = "AC")
        public void setAnotherChar(Character anotherChar) {
            this.anotherChar = anotherChar;
        }

        @Option(longName = "double2", shortName = "AD")
        public void setAnotherDouble(Double anotherDouble) {
            this.anotherDouble = anotherDouble;
        }

        @Option(longName = "float2", shortName = "AF")
        public void setAnotherFloat(Float anotherFloat) {
            this.anotherFloat = anotherFloat;
        }

        @Option(longName = "int2", shortName = "AI")
        public void setAnotherInt(Integer anotherInt) {
            this.anotherInt = anotherInt;
        }

        @Option(longName = "long2", shortName = "AJ")
        public void setAnotherLong(Long anotherLong) {
            this.anotherLong = anotherLong;
        }

        @Option(longName = "person2", shortName = "p2")
        public void setAnotherPerson(Person2 anotherPerson) {
            this.anotherPerson = anotherPerson;
        }

        @Option(longName = "short2", shortName = "AS")
        public void setAnotherShort(Short anotherShort) {
            this.anotherShort = anotherShort;
        }

        @Option(longName = "person", shortName = "p")
        public void setaPerson(Person aPerson) {
            this.aPerson = aPerson;
        }

        @Option(longName = "person4", shortName = "p4")
        @ConvertedBy(Person4Converter.class)
        public void setAFourthPerson(Person4 aPerson) {
            this.aFourthPerson = aPerson;
        }

        @Option(longName = "short", shortName = "s")
        public void setaShort(short aShort) {
            this.aShort = aShort;
        }

        @Option(longName = "state", shortName = "st")
        public void setaState(Thread.State aState) {
            this.aState = aState;
        }

        @Option(longName = "string", shortName = "str")
        public void setaString(String aString) {
            this.aString = aString;
        }

        @Option(longName = "person3", shortName = "p3")
        public void setaThirdPerson(Person3 aThirdPerson) {
            this.aThirdPerson = aThirdPerson;
        }
    }

    @Name("multi")
    private class CLIWithMultipleValues {
        List<Person> persons;

        List<Person> persons2;

        List<Person> persons3;

        Set<Thread.State> states;

        Collection<Integer> ints;

        Set<String> strings;

        List<Short> shorts;

        double[] doubles;

        List<Person> persons4;

        @Option(longName = "doubles", shortName = "ds")
        public void setDoubles(double[] doubles) {
            this.doubles = doubles;
        }

        @Option(longName = "ints", shortName = "is")
        public void setInts(Collection<Integer> ints) {
            this.ints = ints;
        }

        @Option(longName = "persons2", shortName = "ps2")
        public void setPersons2(List<Person> persons2) {
            this.persons2 = persons2;
        }

        @Option(longName = "persons3", shortName = "ps3")
        public void setPersons3(List<Person> persons3) {
            this.persons3 = persons3;
        }

        @Option(longName = "persons4", shortName = "ps4")
        @ParsedAsList(separator = ":")
        public void setPersons4(List<Person> persons4) {
            this.persons4 = persons4;
        }

        @Option(longName = "persons", shortName = "ps")
        public void setPersons(List<Person> persons) {
            this.persons = persons;
        }

        @Option(longName = "shorts", shortName = "ss")
        public void setShorts(List<Short> shorts) {
            this.shorts = shorts;
        }

        @Option(longName = "states", shortName = "sts")
        public void setStates(Set<Thread.State> states) {
            this.states = states;
        }

        @Option(longName = "strings", shortName = "str")
        public void setStrings(Set<String> strings) {
            this.strings = strings;
        }
    }
}

