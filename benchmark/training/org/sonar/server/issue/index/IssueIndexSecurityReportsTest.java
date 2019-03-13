/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.issue.index;


import Issue.RESOLUTION_FIXED;
import Issue.RESOLUTION_WONT_FIX;
import Issue.STATUS_CLOSED;
import Issue.STATUS_OPEN;
import Issue.STATUS_REOPENED;
import Issue.STATUS_RESOLVED;
import RuleType.SECURITY_HOTSPOT;
import RuleType.VULNERABILITY;
import Severity.BLOCKER;
import Severity.CRITICAL;
import Severity.MAJOR;
import Severity.MINOR;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.TimeZone;
import java.util.stream.Collectors;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.organization.OrganizationTesting;
import org.sonar.server.es.EsTester;
import org.sonar.server.issue.IssueDocTesting;
import org.sonar.server.permission.index.PermissionIndexerTester;
import org.sonar.server.permission.index.WebAuthorizationTypeSupport;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.view.index.ViewIndexer;


public class IssueIndexSecurityReportsTest {
    @Rule
    public EsTester es = EsTester.create();

    @Rule
    public UserSessionRule userSessionRule = UserSessionRule.standalone();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private System2 system2 = new TestSystem2().setNow(1500000000000L).setDefaultTimeZone(TimeZone.getTimeZone("GMT-01:00"));

    @Rule
    public DbTester db = DbTester.create(system2);

    private IssueIndexer issueIndexer = new IssueIndexer(es.client(), db.getDbClient(), new IssueIteratorFactory(db.getDbClient()));

    private ViewIndexer viewIndexer = new ViewIndexer(db.getDbClient(), es.client());

    private PermissionIndexerTester authorizationIndexer = new PermissionIndexerTester(es, issueIndexer);

    private IssueIndex underTest = new IssueIndex(es.client(), system2, userSessionRule, new WebAuthorizationTypeSupport(userSessionRule));

    @Test
    public void getOwaspTop10Report_dont_count_vulnerabilities_from_other_projects() {
        OrganizationDto org = OrganizationTesting.newOrganizationDto();
        ComponentDto project = newPrivateProjectDto(org);
        ComponentDto another = newPrivateProjectDto(org);
        indexIssues(IssueDocTesting.newDoc("anotherProject", another).setOwaspTop10(Collections.singletonList("a1")).setType(VULNERABILITY).setStatus(STATUS_OPEN).setSeverity(CRITICAL), IssueDocTesting.newDoc("openvul1", project).setOwaspTop10(Collections.singletonList("a1")).setType(VULNERABILITY).setStatus(STATUS_OPEN).setSeverity(MAJOR));
        List<SecurityStandardCategoryStatistics> owaspTop10Report = underTest.getOwaspTop10Report(project.uuid(), false, false);
        assertThat(owaspTop10Report).extracting(SecurityStandardCategoryStatistics::getCategory, SecurityStandardCategoryStatistics::getVulnerabilities, SecurityStandardCategoryStatistics::getVulnerabiliyRating).contains(/* openvul1 */
        /* MAJOR = C */
        tuple("a1", 1L, OptionalInt.of(3)));
    }

    @Test
    public void getOwaspTop10Report_dont_count_closed_vulnerabilities() {
        OrganizationDto org = OrganizationTesting.newOrganizationDto();
        ComponentDto project = newPrivateProjectDto(org);
        indexIssues(IssueDocTesting.newDoc("openvul1", project).setOwaspTop10(Arrays.asList("a1")).setType(VULNERABILITY).setStatus(STATUS_OPEN).setSeverity(MAJOR), IssueDocTesting.newDoc("notopenvul", project).setOwaspTop10(Arrays.asList("a1")).setType(VULNERABILITY).setStatus(STATUS_CLOSED).setResolution(RESOLUTION_FIXED).setSeverity(BLOCKER));
        List<SecurityStandardCategoryStatistics> owaspTop10Report = underTest.getOwaspTop10Report(project.uuid(), false, false);
        assertThat(owaspTop10Report).extracting(SecurityStandardCategoryStatistics::getCategory, SecurityStandardCategoryStatistics::getVulnerabilities, SecurityStandardCategoryStatistics::getVulnerabiliyRating).contains(/* openvul1 */
        /* MAJOR = C */
        tuple("a1", 1L, OptionalInt.of(3)));
    }

    @Test
    public void getOwaspTop10Report_dont_count_old_vulnerabilities() {
        OrganizationDto org = OrganizationTesting.newOrganizationDto();
        ComponentDto project = newPrivateProjectDto(org);
        // Previous vulnerabilities in projects that are not reanalyzed will have no owasp nor cwe attributes (not even 'unknown')
        indexIssues(IssueDocTesting.newDoc("openvulNotReindexed", project).setType(VULNERABILITY).setStatus(STATUS_OPEN).setSeverity(MAJOR));
        List<SecurityStandardCategoryStatistics> owaspTop10Report = underTest.getOwaspTop10Report(project.uuid(), false, false);
        assertThat(owaspTop10Report).extracting(SecurityStandardCategoryStatistics::getVulnerabilities, SecurityStandardCategoryStatistics::getVulnerabiliyRating).containsOnly(tuple(0L, OptionalInt.empty()));
    }

    @Test
    public void getOwaspTop10Report_dont_count_hotspots_from_other_projects() {
        OrganizationDto org = OrganizationTesting.newOrganizationDto();
        ComponentDto project = newPrivateProjectDto(org);
        ComponentDto another = newPrivateProjectDto(org);
        indexIssues(IssueDocTesting.newDoc("openhotspot1", project).setOwaspTop10(Arrays.asList("a1")).setType(SECURITY_HOTSPOT).setStatus(STATUS_OPEN), IssueDocTesting.newDoc("anotherProject", another).setOwaspTop10(Arrays.asList("a1")).setType(SECURITY_HOTSPOT).setStatus(STATUS_OPEN));
        List<SecurityStandardCategoryStatistics> owaspTop10Report = underTest.getOwaspTop10Report(project.uuid(), false, false);
        assertThat(owaspTop10Report).extracting(SecurityStandardCategoryStatistics::getCategory, SecurityStandardCategoryStatistics::getOpenSecurityHotspots).contains(/* openhotspot1 */
        tuple("a1", 1L));
    }

    @Test
    public void getOwaspTop10Report_dont_count_closed_hotspots() {
        OrganizationDto org = OrganizationTesting.newOrganizationDto();
        ComponentDto project = newPrivateProjectDto(org);
        indexIssues(IssueDocTesting.newDoc("openhotspot1", project).setOwaspTop10(Arrays.asList("a1")).setType(SECURITY_HOTSPOT).setStatus(STATUS_OPEN), IssueDocTesting.newDoc("closedHotspot", project).setOwaspTop10(Arrays.asList("a1")).setType(SECURITY_HOTSPOT).setStatus(STATUS_CLOSED).setResolution(RESOLUTION_FIXED));
        List<SecurityStandardCategoryStatistics> owaspTop10Report = underTest.getOwaspTop10Report(project.uuid(), false, false);
        assertThat(owaspTop10Report).extracting(SecurityStandardCategoryStatistics::getCategory, SecurityStandardCategoryStatistics::getOpenSecurityHotspots).contains(/* openhotspot1 */
        tuple("a1", 1L));
    }

    @Test
    public void getOwaspTop10Report_aggregation_no_cwe() {
        List<SecurityStandardCategoryStatistics> owaspTop10Report = indexIssuesAndAssertOwaspReport(false);
        assertThat(owaspTop10Report).allMatch(( category) -> category.getChildren().isEmpty());
    }

    @Test
    public void getOwaspTop10Report_aggregation_with_cwe() {
        List<SecurityStandardCategoryStatistics> owaspTop10Report = indexIssuesAndAssertOwaspReport(true);
        Map<String, List<SecurityStandardCategoryStatistics>> cweByOwasp = owaspTop10Report.stream().collect(Collectors.toMap(SecurityStandardCategoryStatistics::getCategory, SecurityStandardCategoryStatistics::getChildren));
        assertThat(cweByOwasp.get("a1")).extracting(SecurityStandardCategoryStatistics::getCategory, SecurityStandardCategoryStatistics::getVulnerabilities, SecurityStandardCategoryStatistics::getVulnerabiliyRating, SecurityStandardCategoryStatistics::getOpenSecurityHotspots, SecurityStandardCategoryStatistics::getToReviewSecurityHotspots, SecurityStandardCategoryStatistics::getWontFixSecurityHotspots).containsExactlyInAnyOrder(/* openvul1 */
        /* MAJOR = C */
        tuple("123", 1L, OptionalInt.of(3), 0L, 0L, 0L), /* openvul1 */
        /* MAJOR = C */
        tuple("456", 1L, OptionalInt.of(3), 0L, 0L, 0L), /* openhotspot1 */
        tuple("unknown", 0L, OptionalInt.empty(), 1L, 0L, 0L));
        assertThat(cweByOwasp.get("a3")).extracting(SecurityStandardCategoryStatistics::getCategory, SecurityStandardCategoryStatistics::getVulnerabilities, SecurityStandardCategoryStatistics::getVulnerabiliyRating, SecurityStandardCategoryStatistics::getOpenSecurityHotspots, SecurityStandardCategoryStatistics::getToReviewSecurityHotspots, SecurityStandardCategoryStatistics::getWontFixSecurityHotspots).containsExactlyInAnyOrder(/* openvul1, openvul2 */
        /* MAJOR = C */
        tuple("123", 2L, OptionalInt.of(3), 0L, 0L, 0L), /* openvul1 */
        /* MAJOR = C */
        /* toReviewHotspot */
        tuple("456", 1L, OptionalInt.of(3), 0L, 1L, 0L), /* openhotspot1 */
        tuple("unknown", 0L, OptionalInt.empty(), 1L, 0L, 0L));
    }

    @Test
    public void getSansTop25Report_aggregation() {
        OrganizationDto org = OrganizationTesting.newOrganizationDto();
        ComponentDto project = newPrivateProjectDto(org);
        indexIssues(IssueDocTesting.newDoc("openvul1", project).setSansTop25(Arrays.asList(SecurityStandardHelper.SANS_TOP_25_INSECURE_INTERACTION, SecurityStandardHelper.SANS_TOP_25_RISKY_RESOURCE)).setType(VULNERABILITY).setStatus(STATUS_OPEN).setSeverity(MAJOR), IssueDocTesting.newDoc("openvul2", project).setSansTop25(Arrays.asList(SecurityStandardHelper.SANS_TOP_25_RISKY_RESOURCE, SecurityStandardHelper.SANS_TOP_25_POROUS_DEFENSES)).setType(VULNERABILITY).setStatus(STATUS_REOPENED).setSeverity(MINOR), IssueDocTesting.newDoc("notopenvul", project).setSansTop25(Arrays.asList(SecurityStandardHelper.SANS_TOP_25_RISKY_RESOURCE, SecurityStandardHelper.SANS_TOP_25_POROUS_DEFENSES)).setType(VULNERABILITY).setStatus(STATUS_CLOSED).setResolution(RESOLUTION_FIXED).setSeverity(BLOCKER), IssueDocTesting.newDoc("notsansvul", project).setSansTop25(Collections.singletonList(SecurityStandardHelper.UNKNOWN_STANDARD)).setType(VULNERABILITY).setStatus(STATUS_OPEN).setSeverity(CRITICAL), IssueDocTesting.newDoc("openhotspot1", project).setSansTop25(Arrays.asList(SecurityStandardHelper.SANS_TOP_25_INSECURE_INTERACTION, SecurityStandardHelper.SANS_TOP_25_RISKY_RESOURCE)).setType(SECURITY_HOTSPOT).setStatus(STATUS_OPEN), IssueDocTesting.newDoc("openhotspot2", project).setSansTop25(Arrays.asList(SecurityStandardHelper.SANS_TOP_25_RISKY_RESOURCE, SecurityStandardHelper.SANS_TOP_25_POROUS_DEFENSES)).setType(SECURITY_HOTSPOT).setStatus(STATUS_REOPENED), IssueDocTesting.newDoc("toReviewHotspot", project).setSansTop25(Arrays.asList(SecurityStandardHelper.SANS_TOP_25_RISKY_RESOURCE)).setType(SECURITY_HOTSPOT).setStatus(STATUS_RESOLVED).setResolution(RESOLUTION_FIXED), IssueDocTesting.newDoc("WFHotspot", project).setSansTop25(Arrays.asList(SecurityStandardHelper.SANS_TOP_25_RISKY_RESOURCE)).setType(SECURITY_HOTSPOT).setStatus(STATUS_RESOLVED).setResolution(RESOLUTION_WONT_FIX), IssueDocTesting.newDoc("notowasphotspot", project).setSansTop25(Collections.singletonList(SecurityStandardHelper.UNKNOWN_STANDARD)).setType(SECURITY_HOTSPOT).setStatus(STATUS_OPEN));
        List<SecurityStandardCategoryStatistics> sansTop25Report = underTest.getSansTop25Report(project.uuid(), false, false);
        assertThat(sansTop25Report).extracting(SecurityStandardCategoryStatistics::getCategory, SecurityStandardCategoryStatistics::getVulnerabilities, SecurityStandardCategoryStatistics::getVulnerabiliyRating, SecurityStandardCategoryStatistics::getOpenSecurityHotspots, SecurityStandardCategoryStatistics::getToReviewSecurityHotspots, SecurityStandardCategoryStatistics::getWontFixSecurityHotspots).containsExactlyInAnyOrder(/* openvul1 */
        /* MAJOR = C */
        /* openhotspot1 */
        tuple(SecurityStandardHelper.SANS_TOP_25_INSECURE_INTERACTION, 1L, OptionalInt.of(3), 1L, 0L, 0L), /* openvul1,openvul2 */
        /* MAJOR = C */
        /* openhotspot1,openhotspot2 */
        /* toReviewHotspot */
        /* WFHotspot */
        tuple(SecurityStandardHelper.SANS_TOP_25_RISKY_RESOURCE, 2L, OptionalInt.of(3), 2L, 1L, 1L), /* openvul2 */
        /* MINOR = B */
        /* openhotspot2 */
        tuple(SecurityStandardHelper.SANS_TOP_25_POROUS_DEFENSES, 1L, OptionalInt.of(2), 1L, 0L, 0L));
        assertThat(sansTop25Report).allMatch(( category) -> category.getChildren().isEmpty());
    }

    @Test
    public void getSansTop25Report_aggregation_on_portfolio() {
        ComponentDto portfolio1 = db.components().insertPrivateApplication(db.getDefaultOrganization());
        ComponentDto portfolio2 = db.components().insertPrivateApplication(db.getDefaultOrganization());
        ComponentDto project1 = db.components().insertPrivateProject();
        ComponentDto project2 = db.components().insertPrivateProject();
        indexIssues(IssueDocTesting.newDoc("openvul1", project1).setSansTop25(Arrays.asList(SecurityStandardHelper.SANS_TOP_25_INSECURE_INTERACTION, SecurityStandardHelper.SANS_TOP_25_RISKY_RESOURCE)).setType(VULNERABILITY).setStatus(STATUS_OPEN).setSeverity(MAJOR), IssueDocTesting.newDoc("openvul2", project2).setSansTop25(Arrays.asList(SecurityStandardHelper.SANS_TOP_25_RISKY_RESOURCE, SecurityStandardHelper.SANS_TOP_25_POROUS_DEFENSES)).setType(VULNERABILITY).setStatus(STATUS_REOPENED).setSeverity(MINOR), IssueDocTesting.newDoc("notopenvul", project1).setSansTop25(Arrays.asList(SecurityStandardHelper.SANS_TOP_25_RISKY_RESOURCE, SecurityStandardHelper.SANS_TOP_25_POROUS_DEFENSES)).setType(VULNERABILITY).setStatus(STATUS_CLOSED).setResolution(RESOLUTION_FIXED).setSeverity(BLOCKER), IssueDocTesting.newDoc("notsansvul", project2).setSansTop25(Collections.singletonList(SecurityStandardHelper.UNKNOWN_STANDARD)).setType(VULNERABILITY).setStatus(STATUS_OPEN).setSeverity(CRITICAL), IssueDocTesting.newDoc("openhotspot1", project1).setSansTop25(Arrays.asList(SecurityStandardHelper.SANS_TOP_25_INSECURE_INTERACTION, SecurityStandardHelper.SANS_TOP_25_RISKY_RESOURCE)).setType(SECURITY_HOTSPOT).setStatus(STATUS_OPEN), IssueDocTesting.newDoc("openhotspot2", project2).setSansTop25(Arrays.asList(SecurityStandardHelper.SANS_TOP_25_RISKY_RESOURCE, SecurityStandardHelper.SANS_TOP_25_POROUS_DEFENSES)).setType(SECURITY_HOTSPOT).setStatus(STATUS_REOPENED), IssueDocTesting.newDoc("toReviewHotspot", project1).setSansTop25(Arrays.asList(SecurityStandardHelper.SANS_TOP_25_RISKY_RESOURCE)).setType(SECURITY_HOTSPOT).setStatus(STATUS_RESOLVED).setResolution(RESOLUTION_FIXED), IssueDocTesting.newDoc("WFHotspot", project2).setSansTop25(Arrays.asList(SecurityStandardHelper.SANS_TOP_25_RISKY_RESOURCE)).setType(SECURITY_HOTSPOT).setStatus(STATUS_RESOLVED).setResolution(RESOLUTION_WONT_FIX), IssueDocTesting.newDoc("notowasphotspot", project1).setSansTop25(Collections.singletonList(SecurityStandardHelper.UNKNOWN_STANDARD)).setType(SECURITY_HOTSPOT).setStatus(STATUS_OPEN));
        indexView(portfolio1.uuid(), Collections.singletonList(project1.uuid()));
        indexView(portfolio2.uuid(), Collections.singletonList(project2.uuid()));
        List<SecurityStandardCategoryStatistics> sansTop25Report = underTest.getSansTop25Report(portfolio1.uuid(), true, false);
        assertThat(sansTop25Report).extracting(SecurityStandardCategoryStatistics::getCategory, SecurityStandardCategoryStatistics::getVulnerabilities, SecurityStandardCategoryStatistics::getVulnerabiliyRating, SecurityStandardCategoryStatistics::getOpenSecurityHotspots, SecurityStandardCategoryStatistics::getToReviewSecurityHotspots, SecurityStandardCategoryStatistics::getWontFixSecurityHotspots).containsExactlyInAnyOrder(/* openvul1 */
        /* MAJOR = C */
        /* openhotspot1 */
        tuple(SecurityStandardHelper.SANS_TOP_25_INSECURE_INTERACTION, 1L, OptionalInt.of(3), 1L, 0L, 0L), /* openvul1 */
        /* MAJOR = C */
        /* openhotspot1 */
        /* toReviewHotspot */
        tuple(SecurityStandardHelper.SANS_TOP_25_RISKY_RESOURCE, 1L, OptionalInt.of(3), 1L, 1L, 0L), tuple(SecurityStandardHelper.SANS_TOP_25_POROUS_DEFENSES, 0L, OptionalInt.empty(), 0L, 0L, 0L));
        assertThat(sansTop25Report).allMatch(( category) -> category.getChildren().isEmpty());
    }
}

