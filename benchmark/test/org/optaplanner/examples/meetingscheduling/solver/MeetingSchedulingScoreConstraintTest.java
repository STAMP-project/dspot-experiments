/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.optaplanner.examples.meetingscheduling.solver;


import MeetingSchedulingApp.SOLVER_CONFIG;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.meetingscheduling.domain.Attendance;
import org.optaplanner.examples.meetingscheduling.domain.Meeting;
import org.optaplanner.examples.meetingscheduling.domain.MeetingAssignment;
import org.optaplanner.examples.meetingscheduling.domain.MeetingConstraintConfiguration;
import org.optaplanner.examples.meetingscheduling.domain.MeetingSchedule;
import org.optaplanner.examples.meetingscheduling.domain.RequiredAttendance;
import org.optaplanner.test.impl.score.buildin.hardmediumsoft.HardMediumSoftScoreVerifier;


public class MeetingSchedulingScoreConstraintTest {
    private HardMediumSoftScoreVerifier<MeetingSchedule> scoreVerifier = new HardMediumSoftScoreVerifier(SolverFactory.createFromXmlResource(SOLVER_CONFIG));

    @Test
    public void roomStability() {
        MeetingSchedule solution = getMeetingSchedule(6);
        MeetingConstraintConfiguration constraintConfiguration = solution.getConstraintConfiguration();
        List<Attendance> aList = new ArrayList<>();
        for (int i = 0; i < (solution.getMeetingList().size()); i++) {
            Meeting m = solution.getMeetingList().get(i);
            m.setDurationInGrains(2);
            solution.getMeetingAssignmentList().get(i).setMeeting(m);
            RequiredAttendance ra = new RequiredAttendance();
            ra.setId(((long) (i)));
            ra.setPerson(solution.getPersonList().get(0));
            ra.setMeeting(m);
            aList.add(ra);
            m.setPreferredAttendanceList(new ArrayList());
            m.setRequiredAttendanceList(Collections.singletonList(ra));
        }
        solution.setAttendanceList(aList);
        /* Scenario 1: should penalize
        t0  t1  t2  t3  t4  t5
        --- --- --- --- --- ---
        r0 |  m0   |
        r1         |  m1   |
         */
        MeetingAssignment ma0 = solution.getMeetingAssignmentList().get(0);
        ma0.setStartingTimeGrain(solution.getTimeGrainList().get(0));
        ma0.setRoom(solution.getRoomList().get(0));
        MeetingAssignment ma1 = solution.getMeetingAssignmentList().get(1);
        ma1.setStartingTimeGrain(solution.getTimeGrainList().get(2));
        ma1.setRoom(solution.getRoomList().get(1));
        scoreVerifier.assertSoftWeight("Room stability", (-(constraintConfiguration.getRoomStability().getSoftScore())), solution);
        /* Scenario 2: should penalize
        t0  t1  t2  t3  t4  t5
        --- --- --- --- --- ---
        r0 |  m0   |
        r1              |  m1   |
         */
        ma1.setStartingTimeGrain(solution.getTimeGrainList().get(3));
        scoreVerifier.assertSoftWeight("Room stability", (-(constraintConfiguration.getRoomStability().getSoftScore())), solution);
        /* Scenario 3: should penalize
        t0  t1  t2  t3  t4  t5
        --- --- --- --- --- ---
        r0 |  m0   |
        r1                 |  m1   |
         */
        ma1.setStartingTimeGrain(solution.getTimeGrainList().get(4));
        scoreVerifier.assertSoftWeight("Room stability", (-(constraintConfiguration.getRoomStability().getSoftScore())), solution);
        /* Scenario 4: shouldn't penalize
        t0  t1  t2  t3  t4  t5
        --- --- --- --- --- ---
        r0 |  m0   |
        r1                     |  m1   |
         */
        ma1.setStartingTimeGrain(solution.getTimeGrainList().get(5));
        scoreVerifier.assertSoftWeight("Room stability", 0, solution);
        /* Scenario 5: shouldn't penalize
        t0  t1  t2  t3  t4  t5
        --- --- --- --- --- ---
        r0 |  m0   ||  m1   |
        r1
         */
        ma1.setStartingTimeGrain(solution.getTimeGrainList().get(2));
        ma1.setRoom(solution.getRoomList().get(0));
        scoreVerifier.assertSoftWeight("Room stability", 0, solution);
        /* Scenario 1: should penalize twice
        t0  t1  t2  t3  t4  t5
        --- --- --- --- --- ---
        r0 |  m0   |       |  m2   |
        r1         |  m1   |
         */
        ma1.setRoom(solution.getRoomList().get(1));
        MeetingAssignment ma2 = solution.getMeetingAssignmentList().get(2);
        ma2.setStartingTimeGrain(solution.getTimeGrainList().get(4));
        ma2.setRoom(solution.getRoomList().get(0));
        scoreVerifier.assertSoftWeight("Room stability", ((-(constraintConfiguration.getRoomStability().getSoftScore())) * 2), solution);
    }
}

