/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/**
 * Portions Copyright (c) 2017, Steven Haehn.
 */
package org.opengrok.indexer.util;


import OptionParser.Option;
import java.lang.reflect.Field;
import java.text.ParseException;
import org.junit.Assert;
import org.junit.Test;
import org.opengrok.indexer.index.Indexer;


/**
 *
 *
 * @author shaehn
 */
public class OptionParserTest {
    int actionCounter;

    public OptionParserTest() {
    }

    // Scan parser should ignore all options it does not recognize.
    @Test
    public void scanParserIgnoreUnrecognizableOptions() {
        String configPath = "/the/config/path";
        OptionParser scanner = OptionParser.scan(( parser) -> {
            parser.on("-R configPath").Do(( v) -> {
                assertEquals(v, configPath);
                (actionCounter)++;
            });
        });
        try {
            String[] args = new String[]{ "-a", "-b", "-R", configPath };
            scanner.parse(args);
            Assert.assertEquals(actionCounter, 1);
        } catch (ParseException e) {
            Assert.fail(e.getMessage());
        }
    }

    // Validate that option can have multiple names
    // with both short and long versions.
    @Test
    public void optionNameAliases() {
        OptionParser opts = OptionParser.Do(( parser) -> {
            parser.on("-?", "--help").Do(( v) -> {
                assertEquals(v, "");
                (actionCounter)++;
            });
        });
        try {
            String[] args = new String[]{ "-?" };
            opts.parse(args);
            Assert.assertEquals(actionCounter, 1);
            String[] args2 = new String[]{ "--help" };
            opts.parse(args2);
            Assert.assertEquals(actionCounter, 2);
        } catch (ParseException e) {
            Assert.fail(e.getMessage());
        }
    }

    // Show that parser will throw exception
    // when option is not recognized.
    @Test
    public void unrecognizedOption() {
        OptionParser opts = OptionParser.Do(( parser) -> {
            parser.on("-?", "--help").Do(( v) -> {
            });
        });
        try {
            String[] args = new String[]{ "--unrecognizedOption" };
            opts.parse(args);
        } catch (ParseException e) {
            String msg = e.getMessage();
            Assert.assertEquals(msg, "Unknown option: --unrecognizedOption");
        }
    }

    // Show that parser will throw exception when missing option value
    @Test
    public void missingOptionValue() {
        OptionParser opts = OptionParser.Do(( parser) -> {
            parser.on("-a=VALUE").Do(( v) -> {
            });
        });
        try {
            String[] args = new String[]{ "-a" };// supply option without value

            opts.parse(args);
        } catch (ParseException e) {
            String msg = e.getMessage();
            Assert.assertEquals(msg, "Option -a requires a value.");
        }
    }

    // Test parser ability to find short option value whether
    // it is glued next to the option (eg. -xValue), or comes
    // as a following argument (eg. -x Value)
    @Test
    public void shortOptionValue() {
        OptionParser opts = OptionParser.Do(( parser) -> {
            parser.on("-a=VALUE").Do(( v) -> {
                assertEquals(v, "3");
                (actionCounter)++;
            });
        });
        try {
            String[] separateValue = new String[]{ "-a", "3" };
            opts.parse(separateValue);
            Assert.assertEquals(actionCounter, 1);
            String[] joinedValue = new String[]{ "-a3" };
            opts.parse(joinedValue);
            Assert.assertEquals(actionCounter, 2);
        } catch (ParseException e) {
            Assert.fail(e.getMessage());
        }
    }

    // Validate the ability of parser to convert
    // string option values into internal data types.
    @Test
    public void testSupportedDataCoercion() {
        OptionParser opts = OptionParser.Do(( parser) -> {
            parser.on("--int=VALUE", .class).Do(( v) -> {
                assertEquals(v, 3);
                (actionCounter)++;
            });
            parser.on("--float=VALUE", .class).Do(( v) -> {
                assertEquals(v, ((float) (3.23)));
                (actionCounter)++;
            });
            parser.on("--double=VALUE", .class).Do(( v) -> {
                assertEquals(v, 3.23);
                (actionCounter)++;
            });
            parser.on("-t", "--truth", "=VALUE", .class).Do(( v) -> {
                assertTrue(((Boolean) (v)));
                (actionCounter)++;
            });
            parser.on("-f VALUE", .class).Do(( v) -> {
                assertFalse(((Boolean) (v)));
                (actionCounter)++;
            });
            parser.on("-a array", .class).Do(( v) -> {
                String[] x = { "a", "b", "c" };
                assertArrayEquals(x, ((String[]) (v)));
                (actionCounter)++;
            });
        });
        try {
            String[] integer = new String[]{ "--int", "3" };
            opts.parse(integer);
            Assert.assertEquals(actionCounter, 1);
            String[] floats = new String[]{ "--float", "3.23" };
            opts.parse(floats);
            Assert.assertEquals(actionCounter, 2);
            String[] doubles = new String[]{ "--double", "3.23" };
            opts.parse(doubles);
            Assert.assertEquals(actionCounter, 3);
            actionCounter = 0;
            String[] verity = new String[]{ "-t", "true", "-t", "True", "-t", "on", "-t", "ON", "-t", "yeS" };
            opts.parse(verity);
            Assert.assertEquals(actionCounter, 5);
            actionCounter = 0;
            String[] falsehood = new String[]{ "-f", "false", "-f", "FALSE", "-f", "oFf", "-f", "no", "-f", "NO" };
            opts.parse(falsehood);
            Assert.assertEquals(actionCounter, 5);
            try {
                // test illegal value to Boolean
                String[] liar = new String[]{ "--truth", "liar" };
                opts.parse(liar);
            } catch (ParseException e) {
                String msg = e.getMessage();
                Assert.assertEquals(msg, "Failed to parse (liar) as value of [-t, --truth]");
            }
            actionCounter = 0;
            String[] array = new String[]{ "-a", "a,b,c" };
            opts.parse(array);
            Assert.assertEquals(actionCounter, 1);
        } catch (ParseException e) {
            Assert.fail(e.getMessage());
        }
    }

    // Make sure that option can take specific addOption of values
    // and when an candidate values is seen, an exception is given.
    @Test
    public void specificOptionValues() {
        OptionParser opts = OptionParser.Do(( parser) -> {
            String[] onOff = { "on", "off" };
            parser.on("--setTest on/off", onOff).Do(( v) -> {
                (actionCounter)++;
            });
        });
        try {
            String[] args1 = new String[]{ "--setTest", "on" };
            opts.parse(args1);
            Assert.assertEquals(actionCounter, 1);
            String[] args2 = new String[]{ "--setTest", "off" };
            opts.parse(args2);
            Assert.assertEquals(actionCounter, 2);
            String[] args3 = new String[]{ "--setTest", "nono" };
            opts.parse(args3);
        } catch (ParseException e) {
            String msg = e.getMessage();
            Assert.assertEquals(msg, "'nono' is unknown value for option [--setTest]. Must be one of [on, off]");
        }
    }

    // See that option value matches a regular expression
    @Test
    public void optionValuePatternMatch() {
        OptionParser opts = OptionParser.Do(( parser) -> {
            parser.on("--pattern PERCENT", "/[0-9]+%?/").Do(( v) -> {
                (actionCounter)++;
            });
        });
        try {
            String[] args1 = new String[]{ "--pattern", "3%" };
            opts.parse(args1);
            Assert.assertEquals(actionCounter, 1);
            String[] args2 = new String[]{ "--pattern", "120%" };
            opts.parse(args2);
            Assert.assertEquals(actionCounter, 2);
            String[] args3 = new String[]{ "--pattern", "75" };
            opts.parse(args3);
            Assert.assertEquals(actionCounter, 3);
            String[] args4 = new String[]{ "--pattern", "NotNumber" };
            opts.parse(args4);
        } catch (ParseException e) {
            String msg = e.getMessage();
            Assert.assertEquals(msg, ("Value \'NotNumber\' for option [--pattern]PERCENT\n" + " does not match pattern [0-9]+%?"));
        }
    }

    // Verify option may have non-required value
    @Test
    public void missingValueOnOptionAllowed() {
        OptionParser opts = OptionParser.Do(( parser) -> {
            parser.on("--value=[optional]").Do(( v) -> {
                (actionCounter)++;
                if (v.equals("")) {
                    assertEquals(v, "");
                } else {
                    assertEquals(v, "hasOne");
                }
            });
            parser.on("-o[=optional]").Do(( v) -> {
                (actionCounter)++;
                if (v.equals("")) {
                    assertEquals(v, "");
                } else {
                    assertEquals(v, "hasOne");
                }
            });
            parser.on("-v[optional]").Do(( v) -> {
                (actionCounter)++;
                if (v.equals("")) {
                    assertEquals(v, "");
                } else {
                    assertEquals(v, "hasOne");
                }
            });
        });
        try {
            String[] args1 = new String[]{ "--value", "hasOne" };
            opts.parse(args1);
            Assert.assertEquals(actionCounter, 1);
            String[] args2 = new String[]{ "--value" };
            opts.parse(args2);
            Assert.assertEquals(actionCounter, 2);
            String[] args3 = new String[]{ "-ohasOne" };
            opts.parse(args3);
            Assert.assertEquals(actionCounter, 3);
            String[] args4 = new String[]{ "-o" };
            opts.parse(args4);
            Assert.assertEquals(actionCounter, 4);
            String[] args5 = new String[]{ "-v", "hasOne" };
            opts.parse(args5);
            Assert.assertEquals(actionCounter, 5);
            String[] args6 = new String[]{ "-v" };
            opts.parse(args6);
            Assert.assertEquals(actionCounter, 6);
            String[] args7 = new String[]{ "--value", "-o", "hasOne" };
            opts.parse(args7);
            Assert.assertEquals(actionCounter, 8);
        } catch (ParseException e) {
            Assert.fail(e.getMessage());
        }
    }

    // Verify default option summary
    @Test
    public void defaultOptionSummary() {
        OptionParser opts = OptionParser.Do(( parser) -> {
            parser.on("--help").Do(( v) -> {
                String summary = parser.getUsage();
                // assertTrue(summary.startsWith("Usage: JUnitTestRunner [options]"));  // fails on travis
                assertTrue(summary.matches("(?s)Usage: \\w+ \\[options\\].*"));
            });
        });
        try {
            String[] args = new String[]{ "--help" };
            opts.parse(args);
        } catch (ParseException e) {
            String msg = e.getMessage();
            Assert.assertEquals(msg, "Unknown option: --unrecognizedOption");
        }
    }

    // Allowing user entry of initial substrings to long option names.
    // Therefore, must be able to catch when option entry matches more
    // than one entry.
    @Test
    public void catchAmbigousOptions() {
        OptionParser opts = OptionParser.Do(( parser) -> {
            parser.on("--help");
            parser.on("--help-me-out");
        });
        try {
            String[] args = new String[]{ "--he" };
            opts.parse(args);
        } catch (ParseException e) {
            String msg = e.getMessage();
            Assert.assertEquals(msg, "Ambiguous option --he matches [--help-me-out, --help]");
        }
    }

    // Allow user to enter an initial substring to long option names
    @Test
    public void allowInitialSubstringOptionNames() {
        OptionParser opts = OptionParser.Do(( parser) -> {
            parser.on("--help-me-out").Do(( v) -> {
                (actionCounter)++;
            });
        });
        try {
            String[] args = new String[]{ "--help" };
            opts.parse(args);
            Assert.assertEquals(actionCounter, 1);
        } catch (ParseException e) {
            Assert.fail(e.getMessage());
        }
    }

    // Specific test to evalutate the internal option candidate method
    @Test
    public void testInitialSubstringOptionNames() {
        OptionParser opts = OptionParser.Do(( parser) -> {
            parser.on("--help-me-out");
            parser.on("--longOption");
        });
        try {
            Assert.assertEquals(opts.candidate("--l", 0), "--longOption");
            Assert.assertEquals(opts.candidate("--h", 0), "--help-me-out");
            Assert.assertNull(opts.candidate("--thisIsUnknownOption", 0));
        } catch (ParseException e) {
            Assert.fail(e.getMessage());
        }
    }

    // Catch duplicate option names in parser construction.
    @Test
    public void catchDuplicateOptionNames() {
        try {
            OptionParser opts = OptionParser.Do(( parser) -> {
                parser.on("--duplicate");
                parser.on("--duplicate");
            });
        } catch (IllegalArgumentException e) {
            String msg = e.getMessage();
            Assert.assertEquals(msg, "** Programmer error! Option --duplicate already defined");
        }
    }

    // Catch single '-' in argument list
    @Test
    public void catchNamelessOption() {
        OptionParser opts = OptionParser.Do(( parser) -> {
            parser.on("--help-me-out");
        });
        try {
            String[] args = new String[]{ "-", "data" };
            opts.parse(args);
        } catch (ParseException e) {
            String msg = e.getMessage();
            Assert.assertEquals(msg, "Stand alone '-' found in arguments, not allowed");
        }
    }

    // Fail options put into Indexer.java that do not have a description.
    @Test
    public void catchIndexerOptionsWithoutDescription() throws IllegalAccessException, NoSuchFieldException {
        String[] argv = new String[]{ "---unitTest" };
        try {
            Indexer.parseOptions(argv);
            // Use reflection to get the option parser from Indexer.
            Field f = Indexer.class.getDeclaredField("optParser");
            f.setAccessible(true);
            OptionParser op = ((OptionParser) (f.get(Indexer.class)));
            for (OptionParser.Option o : op.getOptionList()) {
                if ((o.description) == null) {
                    Assert.fail((("'" + (o.names.get(0))) + "' option needs description"));
                } else
                    if (o.description.equals("")) {
                        Assert.fail((("'" + (o.names.get(0))) + "' option needs non-empty description"));
                    }

            }
            // This just tests that the description is actually null.
            op = OptionParser.Do(( parser) -> {
                parser.on("--help-me-out");
            });
            for (OptionParser.Option o : op.getOptionList()) {
                Assert.assertNull(o.description);
            }
        } catch (ParseException e) {
            Assert.fail(e.getMessage());
        }
    }
}

