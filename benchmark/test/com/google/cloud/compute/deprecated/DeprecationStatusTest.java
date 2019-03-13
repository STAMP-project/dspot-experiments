/**
 * Copyright 2016 Google LLC
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
package com.google.cloud.compute.deprecated;


import DateTimeFormatter.ISO_INSTANT;
import DeprecationStatus.Status;
import DiskTypeId.FROM_URL_FUNCTION;
import ZoneOffset.UTC;
import org.junit.Assert;
import org.junit.Test;
import org.threeten.bp.Instant;
import org.threeten.bp.format.DateTimeFormatter;


public class DeprecationStatusTest {
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = ISO_INSTANT.withZone(UTC);

    private static final Long DELETED_MILLIS = 1453293540000L;

    private static final Long DEPRECATED_MILLIS = 1453293420000L;

    private static final Long OBSOLETE_MILLIS = 1453293480000L;

    private static final String DELETED = DeprecationStatusTest.TIMESTAMP_FORMATTER.format(Instant.ofEpochMilli(DeprecationStatusTest.DELETED_MILLIS));

    private static final String DEPRECATED = DeprecationStatusTest.TIMESTAMP_FORMATTER.format(Instant.ofEpochMilli(DeprecationStatusTest.DEPRECATED_MILLIS));

    private static final String OBSOLETE = DeprecationStatusTest.TIMESTAMP_FORMATTER.format(Instant.ofEpochMilli(DeprecationStatusTest.OBSOLETE_MILLIS));

    private static final DiskTypeId DISK_TYPE_ID = DiskTypeId.of("project", "zone", "diskType");

    private static final MachineTypeId MACHINE_TYPE_ID = MachineTypeId.of("project", "zone", "machineType");

    private static final Status STATUS = Status.DELETED;

    private static final DeprecationStatus<DiskTypeId> DISK_TYPE_STATUS = DeprecationStatus.<DiskTypeId>newBuilder(DeprecationStatusTest.STATUS).setReplacement(DeprecationStatusTest.DISK_TYPE_ID).setDeprecated(DeprecationStatusTest.DEPRECATED).setObsolete(DeprecationStatusTest.OBSOLETE).setDeleted(DeprecationStatusTest.DELETED).build();

    private static final DeprecationStatus<DiskTypeId> DISK_TYPE_STATUS_MILLIS = DeprecationStatus.<DiskTypeId>newBuilder(DeprecationStatusTest.STATUS).setReplacement(DeprecationStatusTest.DISK_TYPE_ID).setDeprecated(DeprecationStatusTest.DEPRECATED_MILLIS).setObsolete(DeprecationStatusTest.OBSOLETE_MILLIS).setDeleted(DeprecationStatusTest.DELETED_MILLIS).build();

    private static final DeprecationStatus<MachineTypeId> MACHINE_TYPE_STATUS = DeprecationStatus.newBuilder(DeprecationStatusTest.STATUS, DeprecationStatusTest.MACHINE_TYPE_ID).setDeprecated(DeprecationStatusTest.DEPRECATED).setObsolete(DeprecationStatusTest.OBSOLETE).setDeleted(DeprecationStatusTest.DELETED).build();

    @Test
    public void testBuilder() {
        compareDeprecationStatus(DeprecationStatusTest.DISK_TYPE_STATUS, DeprecationStatusTest.DISK_TYPE_STATUS_MILLIS);
        Assert.assertEquals(DeprecationStatusTest.DELETED, DeprecationStatusTest.DISK_TYPE_STATUS.getDeleted());
        Assert.assertEquals(DeprecationStatusTest.DEPRECATED, DeprecationStatusTest.DISK_TYPE_STATUS.getDeprecated());
        Assert.assertEquals(DeprecationStatusTest.OBSOLETE, DeprecationStatusTest.DISK_TYPE_STATUS.getObsolete());
        Assert.assertEquals(DeprecationStatusTest.DISK_TYPE_ID, DeprecationStatusTest.DISK_TYPE_STATUS.getReplacement());
        Assert.assertEquals(DeprecationStatusTest.DEPRECATED_MILLIS, DeprecationStatusTest.DISK_TYPE_STATUS.getDeprecatedMillis());
        Assert.assertEquals(DeprecationStatusTest.DELETED_MILLIS, DeprecationStatusTest.DISK_TYPE_STATUS.getDeletedMillis());
        Assert.assertEquals(DeprecationStatusTest.OBSOLETE_MILLIS, DeprecationStatusTest.DISK_TYPE_STATUS.getObsoleteMillis());
        Assert.assertEquals(DeprecationStatusTest.STATUS, DeprecationStatusTest.DISK_TYPE_STATUS.getStatus());
        Assert.assertEquals(DeprecationStatusTest.DELETED, DeprecationStatusTest.DISK_TYPE_STATUS_MILLIS.getDeleted());
        Assert.assertEquals(DeprecationStatusTest.DEPRECATED, DeprecationStatusTest.DISK_TYPE_STATUS_MILLIS.getDeprecated());
        Assert.assertEquals(DeprecationStatusTest.OBSOLETE, DeprecationStatusTest.DISK_TYPE_STATUS_MILLIS.getObsolete());
        Assert.assertEquals(DeprecationStatusTest.DISK_TYPE_ID, DeprecationStatusTest.DISK_TYPE_STATUS_MILLIS.getReplacement());
        Assert.assertEquals(DeprecationStatusTest.DEPRECATED_MILLIS, DeprecationStatusTest.DISK_TYPE_STATUS_MILLIS.getDeprecatedMillis());
        Assert.assertEquals(DeprecationStatusTest.DELETED_MILLIS, DeprecationStatusTest.DISK_TYPE_STATUS_MILLIS.getDeletedMillis());
        Assert.assertEquals(DeprecationStatusTest.OBSOLETE_MILLIS, DeprecationStatusTest.DISK_TYPE_STATUS_MILLIS.getObsoleteMillis());
        Assert.assertEquals(DeprecationStatusTest.STATUS, DeprecationStatusTest.DISK_TYPE_STATUS.getStatus());
        Assert.assertEquals(DeprecationStatusTest.DELETED, DeprecationStatusTest.MACHINE_TYPE_STATUS.getDeleted());
        Assert.assertEquals(DeprecationStatusTest.DEPRECATED, DeprecationStatusTest.MACHINE_TYPE_STATUS.getDeprecated());
        Assert.assertEquals(DeprecationStatusTest.OBSOLETE, DeprecationStatusTest.MACHINE_TYPE_STATUS.getObsolete());
        Assert.assertEquals(DeprecationStatusTest.DEPRECATED_MILLIS, DeprecationStatusTest.MACHINE_TYPE_STATUS.getDeprecatedMillis());
        Assert.assertEquals(DeprecationStatusTest.DELETED_MILLIS, DeprecationStatusTest.MACHINE_TYPE_STATUS.getDeletedMillis());
        Assert.assertEquals(DeprecationStatusTest.OBSOLETE_MILLIS, DeprecationStatusTest.MACHINE_TYPE_STATUS.getObsoleteMillis());
        Assert.assertEquals(DeprecationStatusTest.MACHINE_TYPE_ID, DeprecationStatusTest.MACHINE_TYPE_STATUS.getReplacement());
        Assert.assertEquals(DeprecationStatusTest.STATUS, DeprecationStatusTest.MACHINE_TYPE_STATUS.getStatus());
    }

    @Test
    public void testGettersIllegalArgument() {
        DeprecationStatus<MachineTypeId> deprecationStatus = DeprecationStatus.newBuilder(DeprecationStatusTest.STATUS, DeprecationStatusTest.MACHINE_TYPE_ID).setDeprecated("deprecated").setObsolete("obsolete").setDeleted("delete").build();
        Assert.assertEquals("deprecated", deprecationStatus.getDeprecated());
        try {
            deprecationStatus.getDeprecatedMillis();
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalStateException ex) {
            // never reached
        }
        Assert.assertEquals("obsolete", deprecationStatus.getObsolete());
        try {
            deprecationStatus.getObsoleteMillis();
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalStateException ex) {
            // never reached
        }
        Assert.assertEquals("delete", deprecationStatus.getDeleted());
        try {
            deprecationStatus.getDeletedMillis();
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalStateException ex) {
            // never reached
        }
    }

    @Test
    public void testToBuilder() {
        compareDeprecationStatus(DeprecationStatusTest.DISK_TYPE_STATUS, DeprecationStatusTest.DISK_TYPE_STATUS.toBuilder().build());
        compareDeprecationStatus(DeprecationStatusTest.MACHINE_TYPE_STATUS, DeprecationStatusTest.MACHINE_TYPE_STATUS.toBuilder().build());
        DeprecationStatus<DiskTypeId> deprecationStatus = DeprecationStatusTest.DISK_TYPE_STATUS.toBuilder().setDeleted(DeprecationStatusTest.DEPRECATED).build();
        Assert.assertEquals(DeprecationStatusTest.DEPRECATED, deprecationStatus.getDeleted());
        deprecationStatus = deprecationStatus.toBuilder().setDeleted(DeprecationStatusTest.DELETED).build();
        compareDeprecationStatus(DeprecationStatusTest.DISK_TYPE_STATUS, deprecationStatus);
    }

    @Test
    public void testToBuilderIncomplete() {
        DeprecationStatus<DiskTypeId> diskStatus = DeprecationStatus.of(DeprecationStatusTest.STATUS, DeprecationStatusTest.DISK_TYPE_ID);
        Assert.assertEquals(diskStatus, diskStatus.toBuilder().build());
    }

    @Test
    public void testOf() {
        DeprecationStatus<DiskTypeId> diskStatus = DeprecationStatus.of(DeprecationStatusTest.STATUS, DeprecationStatusTest.DISK_TYPE_ID);
        Assert.assertNull(diskStatus.getDeleted());
        Assert.assertNull(diskStatus.getDeprecated());
        Assert.assertNull(diskStatus.getObsolete());
        Assert.assertEquals(DeprecationStatusTest.DISK_TYPE_ID, diskStatus.getReplacement());
        Assert.assertEquals(DeprecationStatusTest.STATUS, diskStatus.getStatus());
    }

    @Test
    public void testToAndFromPb() {
        DeprecationStatus<DiskTypeId> diskStatus = DeprecationStatus.fromPb(DeprecationStatusTest.DISK_TYPE_STATUS.toPb(), FROM_URL_FUNCTION);
        compareDeprecationStatus(DeprecationStatusTest.DISK_TYPE_STATUS, diskStatus);
        DeprecationStatus<MachineTypeId> machineStatus = DeprecationStatus.fromPb(DeprecationStatusTest.MACHINE_TYPE_STATUS.toPb(), MachineTypeId.FROM_URL_FUNCTION);
        compareDeprecationStatus(DeprecationStatusTest.MACHINE_TYPE_STATUS, machineStatus);
        diskStatus = DeprecationStatus.newBuilder(DeprecationStatusTest.STATUS, DeprecationStatusTest.DISK_TYPE_ID).setDeprecated(DeprecationStatusTest.DEPRECATED).build();
        Assert.assertEquals(diskStatus, DeprecationStatus.fromPb(diskStatus.toPb(), FROM_URL_FUNCTION));
        machineStatus = DeprecationStatus.newBuilder(DeprecationStatusTest.STATUS, DeprecationStatusTest.MACHINE_TYPE_ID).setDeprecated(DeprecationStatusTest.DEPRECATED).build();
        Assert.assertEquals(machineStatus, DeprecationStatus.fromPb(machineStatus.toPb(), MachineTypeId.FROM_URL_FUNCTION));
        diskStatus = DeprecationStatus.of(DeprecationStatusTest.STATUS, DeprecationStatusTest.DISK_TYPE_ID);
        Assert.assertEquals(diskStatus, DeprecationStatus.fromPb(diskStatus.toPb(), FROM_URL_FUNCTION));
    }
}

