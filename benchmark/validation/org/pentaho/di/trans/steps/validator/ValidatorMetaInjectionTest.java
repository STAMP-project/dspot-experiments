/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.trans.steps.validator;


import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.injection.BaseMetadataInjectionTest;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


public class ValidatorMetaInjectionTest extends BaseMetadataInjectionTest<ValidatorMeta> {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void test() throws Exception {
        check("VALIDATE_ALL", new BaseMetadataInjectionTest.BooleanGetter() {
            public boolean get() {
                return meta.isValidatingAll();
            }
        });
        check("CONCATENATE_ERRORS", new BaseMetadataInjectionTest.BooleanGetter() {
            public boolean get() {
                return meta.isConcatenatingErrors();
            }
        });
        check("CONCATENATION_SEPARATOR", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getConcatenationSeparator();
            }
        });
        check("NAME", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getValidations().get(0).getName();
            }
        });
        check("FIELD_NAME", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getValidations().get(0).getFieldName();
            }
        });
        check("MAX_LENGTH", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getValidations().get(0).getMaximumLength();
            }
        });
        check("MIN_LENGTH", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getValidations().get(0).getMinimumLength();
            }
        });
        check("NULL_ALLOWED", new BaseMetadataInjectionTest.BooleanGetter() {
            public boolean get() {
                return meta.getValidations().get(0).isNullAllowed();
            }
        });
        check("ONLY_NULL_ALLOWED", new BaseMetadataInjectionTest.BooleanGetter() {
            public boolean get() {
                return meta.getValidations().get(0).isOnlyNullAllowed();
            }
        });
        check("ONLY_NUMERIC_ALLOWED", new BaseMetadataInjectionTest.BooleanGetter() {
            public boolean get() {
                return meta.getValidations().get(0).isOnlyNumericAllowed();
            }
        });
        skipPropertyTest("DATA_TYPE");
        check("DATA_TYPE_VERIFIED", new BaseMetadataInjectionTest.BooleanGetter() {
            public boolean get() {
                return meta.getValidations().get(0).isDataTypeVerified();
            }
        });
        check("CONVERSION_MASK", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getValidations().get(0).getConversionMask();
            }
        });
        check("DECIMAL_SYMBOL", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getValidations().get(0).getDecimalSymbol();
            }
        });
        check("GROUPING_SYMBOL", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getValidations().get(0).getGroupingSymbol();
            }
        });
        check("MIN_VALUE", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getValidations().get(0).getMinimumValue();
            }
        });
        check("MAX_VALUE", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getValidations().get(0).getMaximumValue();
            }
        });
        check("SOURCING_VALUES", new BaseMetadataInjectionTest.BooleanGetter() {
            public boolean get() {
                return meta.getValidations().get(0).isSourcingValues();
            }
        });
        check("SOURCING_STEP_NAME", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getValidations().get(0).getSourcingStepName();
            }
        });
        check("SOURCING_FIELD", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getValidations().get(0).getSourcingField();
            }
        });
        check("START_STRING", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getValidations().get(0).getStartString();
            }
        });
        check("START_STRING_NOT_ALLOWED", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getValidations().get(0).getStartStringNotAllowed();
            }
        });
        check("END_STRING", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getValidations().get(0).getEndString();
            }
        });
        check("END_STRING_NOT_ALLOWED", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getValidations().get(0).getEndStringNotAllowed();
            }
        });
        check("REGULAR_EXPRESSION_EXPECTED", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getValidations().get(0).getRegularExpression();
            }
        });
        check("REGULAR_EXPRESSION_NOT_ALLOWED", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getValidations().get(0).getRegularExpressionNotAllowed();
            }
        });
        check("ERROR_CODE", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getValidations().get(0).getErrorCode();
            }
        });
        check("ERROR_CODE_DESCRIPTION", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getValidations().get(0).getErrorDescription();
            }
        });
    }
}

