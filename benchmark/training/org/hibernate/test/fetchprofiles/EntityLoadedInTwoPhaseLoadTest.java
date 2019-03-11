/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.fetchprofiles;


import FetchProfile.FetchOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.LazyInitializationException;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.FetchProfile;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


@TestForIssue(jiraKey = "HHH-12297")
public class EntityLoadedInTwoPhaseLoadTest extends BaseCoreFunctionalTestCase {
    static final String FETCH_PROFILE_NAME = "fp1";

    @Test
    public void testIfAllRelationsAreInitialized() {
        long startId = this.createSampleData();
        sessionFactory().getStatistics().clear();
        try {
            EntityLoadedInTwoPhaseLoadTest.Start start = this.loadStartWithFetchProfile(startId);
            @SuppressWarnings("unused")
            String value = start.getVia2().getMid().getFinish().getValue();
            Assert.assertEquals(4, sessionFactory().getStatistics().getEntityLoadCount());
        } catch (LazyInitializationException e) {
            Assert.fail("Everything should be initialized");
        }
    }

    @Entity(name = "FinishEntity")
    public static class Finish {
        @Id
        @GeneratedValue
        private long id;

        @Column(name = "value", nullable = false)
        private String value;

        public Finish() {
        }

        public Finish(String value) {
            this.value = value;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @Entity(name = "MidEntity")
    @FetchProfile(name = EntityLoadedInTwoPhaseLoadTest.FETCH_PROFILE_NAME, fetchOverrides = { @FetchOverride(entity = EntityLoadedInTwoPhaseLoadTest.Mid.class, association = "finish", mode = FetchMode.JOIN) })
    public static class Mid {
        @Id
        @GeneratedValue
        private long id;

        @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
        private EntityLoadedInTwoPhaseLoadTest.Finish finish;

        public Mid() {
        }

        public Mid(EntityLoadedInTwoPhaseLoadTest.Finish finish) {
            this.finish = finish;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public EntityLoadedInTwoPhaseLoadTest.Finish getFinish() {
            return finish;
        }

        public void setFinish(EntityLoadedInTwoPhaseLoadTest.Finish finish) {
            this.finish = finish;
        }
    }

    @Entity(name = "StartEntity")
    @FetchProfile(name = EntityLoadedInTwoPhaseLoadTest.FETCH_PROFILE_NAME, fetchOverrides = { @FetchOverride(entity = EntityLoadedInTwoPhaseLoadTest.Start.class, association = "via1", mode = FetchMode.JOIN), @FetchOverride(entity = EntityLoadedInTwoPhaseLoadTest.Start.class, association = "via2", mode = FetchMode.JOIN) })
    public static class Start {
        @Id
        @GeneratedValue
        private long id;

        @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
        private EntityLoadedInTwoPhaseLoadTest.Via1 via1;

        @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
        private EntityLoadedInTwoPhaseLoadTest.Via2 via2;

        public Start() {
        }

        public Start(EntityLoadedInTwoPhaseLoadTest.Via1 via1, EntityLoadedInTwoPhaseLoadTest.Via2 via2) {
            this.via1 = via1;
            this.via2 = via2;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public EntityLoadedInTwoPhaseLoadTest.Via1 getVia1() {
            return via1;
        }

        public void setVia1(EntityLoadedInTwoPhaseLoadTest.Via1 via1) {
            this.via1 = via1;
        }

        public EntityLoadedInTwoPhaseLoadTest.Via2 getVia2() {
            return via2;
        }

        public void setVia2(EntityLoadedInTwoPhaseLoadTest.Via2 via2) {
            this.via2 = via2;
        }
    }

    @Entity(name = "Via1Entity")
    @FetchProfile(name = EntityLoadedInTwoPhaseLoadTest.FETCH_PROFILE_NAME, fetchOverrides = { @FetchOverride(entity = EntityLoadedInTwoPhaseLoadTest.Via1.class, association = "mid", mode = FetchMode.JOIN) })
    public static class Via1 {
        @Id
        @GeneratedValue
        private long id;

        @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
        private EntityLoadedInTwoPhaseLoadTest.Mid mid;

        public Via1() {
        }

        public Via1(EntityLoadedInTwoPhaseLoadTest.Mid mid) {
            this.mid = mid;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public EntityLoadedInTwoPhaseLoadTest.Mid getMid() {
            return mid;
        }

        public void setMid(EntityLoadedInTwoPhaseLoadTest.Mid mid) {
            this.mid = mid;
        }
    }

    @Entity(name = "Via2Entity")
    @FetchProfile(name = EntityLoadedInTwoPhaseLoadTest.FETCH_PROFILE_NAME, fetchOverrides = { @FetchOverride(entity = EntityLoadedInTwoPhaseLoadTest.Via2.class, association = "mid", mode = FetchMode.JOIN) })
    public static class Via2 {
        @Id
        @GeneratedValue
        private long id;

        @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
        private EntityLoadedInTwoPhaseLoadTest.Mid mid;

        public Via2() {
        }

        public Via2(EntityLoadedInTwoPhaseLoadTest.Mid mid) {
            this.mid = mid;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public EntityLoadedInTwoPhaseLoadTest.Mid getMid() {
            return mid;
        }

        public void setMid(EntityLoadedInTwoPhaseLoadTest.Mid mid) {
            this.mid = mid;
        }
    }
}

