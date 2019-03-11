/**
 * Copyright 2015 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigquery;


import FormatOptions.AVRO;
import FormatOptions.CSV;
import FormatOptions.DATASTORE_BACKUP;
import FormatOptions.GOOGLE_SHEETS;
import FormatOptions.JSON;
import org.junit.Assert;
import org.junit.Test;

import static FormatOptions.AVRO;
import static FormatOptions.CSV;
import static FormatOptions.DATASTORE_BACKUP;
import static FormatOptions.JSON;


public class FormatOptionsTest {
    @Test
    public void testConstructor() {
        FormatOptions options = new FormatOptions(CSV);
        Assert.assertEquals(CSV, options.getType());
        options = new FormatOptions(JSON);
        Assert.assertEquals(JSON, options.getType());
        options = new FormatOptions(DATASTORE_BACKUP);
        Assert.assertEquals(DATASTORE_BACKUP, options.getType());
        options = new FormatOptions(AVRO);
        Assert.assertEquals(AVRO, options.getType());
    }

    @Test
    public void testFactoryMethods() {
        Assert.assertEquals(CSV, FormatOptions.csv().getType());
        Assert.assertEquals(JSON, FormatOptions.json().getType());
        Assert.assertEquals(DATASTORE_BACKUP, FormatOptions.datastoreBackup().getType());
        Assert.assertEquals(AVRO, FormatOptions.avro().getType());
        Assert.assertEquals(GOOGLE_SHEETS, FormatOptions.googleSheets().getType());
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(FormatOptions.csv(), FormatOptions.csv());
        Assert.assertEquals(FormatOptions.csv().hashCode(), FormatOptions.csv().hashCode());
        Assert.assertEquals(FormatOptions.json(), FormatOptions.json());
        Assert.assertEquals(FormatOptions.json().hashCode(), FormatOptions.json().hashCode());
        Assert.assertEquals(FormatOptions.datastoreBackup(), FormatOptions.datastoreBackup());
        Assert.assertEquals(FormatOptions.datastoreBackup().hashCode(), FormatOptions.datastoreBackup().hashCode());
        Assert.assertEquals(FormatOptions.googleSheets(), FormatOptions.googleSheets());
    }
}

