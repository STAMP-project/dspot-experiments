/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.management.internal.cli.help;


import Helper.IS_AVAILABLE_NAME;
import Helper.NAME_NAME;
import Helper.OPTIONS_NAME;
import Helper.SYNONYMS_NAME;
import Helper.SYNOPSIS_NAME;
import Helper.SYNTAX_NAME;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.apache.geode.management.internal.cli.GfshParser;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;


public class HelperUnitTest {
    private Helper helper;

    private CliCommand cliCommand;

    private Method method;

    private CliAvailabilityIndicator availabilityIndicator;

    private CommandMarker commandMarker;

    private Annotation[][] annotations;

    private CliOption cliOption;

    private Class<?>[] parameterType;

    private HelpBlock optionBlock;

    @Test
    public void testGetLongHelp() {
        HelpBlock helpBlock = helper.getHelp(cliCommand, annotations, parameterType);
        String[] helpLines = helpBlock.toString().split(GfshParser.LINE_SEPARATOR);
        assertThat(helpLines.length).isEqualTo(14);
        assertThat(helpLines[0]).isEqualTo(NAME_NAME);
        assertThat(helpLines[2]).isEqualTo(IS_AVAILABLE_NAME);
        assertThat(helpLines[4]).isEqualTo(SYNONYMS_NAME);
        assertThat(helpLines[6]).isEqualTo(SYNOPSIS_NAME);
        assertThat(helpLines[8]).isEqualTo(SYNTAX_NAME);
        assertThat(helpLines[10]).isEqualTo(OPTIONS_NAME);
    }

    @Test
    public void testGetShortHelp() {
        HelpBlock helpBlock = helper.getHelp(cliCommand, null, null);
        String[] helpLines = helpBlock.toString().split(GfshParser.LINE_SEPARATOR);
        assertThat(helpLines.length).isEqualTo(2);
        assertThat(helpLines[0]).isEqualTo("test (Available)");
        assertThat(helpLines[1]).isEqualTo("This is a test description");
    }

    @Test
    public void testGetSyntaxStringWithMandatory() {
        String syntax = helper.getSyntaxString("test", annotations, parameterType);
        assertThat(syntax).isEqualTo("test --option=value");
        optionBlock = helper.getOptionDetail(cliOption);
        assertThat(optionBlock.toString()).isEqualTo(((((("option" + (GfshParser.LINE_SEPARATOR)) + "help of option") + (GfshParser.LINE_SEPARATOR)) + "Required: true") + (GfshParser.LINE_SEPARATOR)));
    }

    @Test
    public void testGetSyntaxStringWithOutMandatory() {
        Mockito.when(cliOption.mandatory()).thenReturn(false);
        String syntax = helper.getSyntaxString("test", annotations, parameterType);
        assertThat(syntax).isEqualTo("test [--option=value]");
        optionBlock = helper.getOptionDetail(cliOption);
        assertThat(optionBlock.toString()).isEqualTo(((((("option" + (GfshParser.LINE_SEPARATOR)) + "help of option") + (GfshParser.LINE_SEPARATOR)) + "Required: false") + (GfshParser.LINE_SEPARATOR)));
    }

    @Test
    public void testGetSyntaxStringWithSecondaryOptionNameIgnored() {
        Mockito.when(cliOption.key()).thenReturn("option,option2".split(","));
        String syntax = helper.getSyntaxString("test", annotations, parameterType);
        assertThat(syntax).isEqualTo("test --option=value");
        optionBlock = helper.getOptionDetail(cliOption);
        assertThat(optionBlock.toString()).isEqualTo(((((((("option" + (GfshParser.LINE_SEPARATOR)) + "help of option") + (GfshParser.LINE_SEPARATOR)) + "Synonyms: option2") + (GfshParser.LINE_SEPARATOR)) + "Required: true") + (GfshParser.LINE_SEPARATOR)));
    }

    @Test
    public void testGetSyntaxStringWithSecondaryOptionName() {
        Mockito.when(cliOption.key()).thenReturn(",option2".split(","));
        Mockito.when(cliOption.mandatory()).thenReturn(true);
        String syntax = helper.getSyntaxString("test", annotations, parameterType);
        assertThat(syntax).isEqualTo("test option2");
        optionBlock = helper.getOptionDetail(cliOption);
        assertThat(optionBlock.toString()).isEqualTo(((((("option2" + (GfshParser.LINE_SEPARATOR)) + "help of option") + (GfshParser.LINE_SEPARATOR)) + "Required: true") + (GfshParser.LINE_SEPARATOR)));
    }

    @Test
    public void testGetSyntaxStringWithOptionalSecondaryOptionName() {
        Mockito.when(cliOption.key()).thenReturn(",option2".split(","));
        Mockito.when(cliOption.mandatory()).thenReturn(false);
        String syntax = helper.getSyntaxString("test", annotations, parameterType);
        assertThat(syntax).isEqualTo("test [option2]");
        optionBlock = helper.getOptionDetail(cliOption);
        assertThat(optionBlock.toString()).isEqualTo(((((("option2" + (GfshParser.LINE_SEPARATOR)) + "help of option") + (GfshParser.LINE_SEPARATOR)) + "Required: false") + (GfshParser.LINE_SEPARATOR)));
    }

    @Test
    public void testGetSyntaxStringWithStringArray() {
        parameterType[0] = String[].class;
        String syntax = helper.getSyntaxString("test", annotations, parameterType);
        assertThat(syntax).isEqualTo("test --option=value(,value)*");
        optionBlock = helper.getOptionDetail(cliOption);
        assertThat(optionBlock.toString()).isEqualTo(((((("option" + (GfshParser.LINE_SEPARATOR)) + "help of option") + (GfshParser.LINE_SEPARATOR)) + "Required: true") + (GfshParser.LINE_SEPARATOR)));
    }

    @Test
    public void testGetSyntaxStringWithSpecifiedDefault() {
        Mockito.when(cliOption.specifiedDefaultValue()).thenReturn("true");
        String syntax = helper.getSyntaxString("test", annotations, parameterType);
        assertThat(syntax).isEqualTo("test --option(=value)?");
        optionBlock = helper.getOptionDetail(cliOption);
        assertThat(optionBlock.toString()).isEqualTo(((((((("option" + (GfshParser.LINE_SEPARATOR)) + "help of option") + (GfshParser.LINE_SEPARATOR)) + "Required: true") + (GfshParser.LINE_SEPARATOR)) + "Default (if the parameter is specified without value): true") + (GfshParser.LINE_SEPARATOR)));
    }

    @Test
    public void testGetSyntaxStringWithDefaultAndStringArray() {
        parameterType[0] = String[].class;
        Mockito.when(cliOption.specifiedDefaultValue()).thenReturn("value1,value2");
        String syntax = helper.getSyntaxString("test", annotations, parameterType);
        assertThat(syntax).isEqualTo("test --option(=value)?(,value)*");
        optionBlock = helper.getOptionDetail(cliOption);
        assertThat(optionBlock.toString()).isEqualTo(((((((("option" + (GfshParser.LINE_SEPARATOR)) + "help of option") + (GfshParser.LINE_SEPARATOR)) + "Required: true") + (GfshParser.LINE_SEPARATOR)) + "Default (if the parameter is specified without value): value1,value2") + (GfshParser.LINE_SEPARATOR)));
    }
}

