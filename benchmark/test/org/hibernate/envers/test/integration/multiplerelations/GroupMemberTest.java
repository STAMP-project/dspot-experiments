/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.multiplerelations;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import org.hibernate.envers.AuditMappedBy;
import org.hibernate.envers.Audited;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-7681")
public class GroupMemberTest extends BaseEnversJPAFunctionalTestCase {
    private Integer uniqueGroupId;

    private Integer groupMemberId;

    @Test
    @Priority(10)
    public void initData() {
        // Revision 1
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.multiplerelations.UniqueGroup uniqueGroup = new org.hibernate.envers.test.integration.multiplerelations.UniqueGroup();
            final org.hibernate.envers.test.integration.multiplerelations.GroupMember groupMember = new org.hibernate.envers.test.integration.multiplerelations.GroupMember();
            uniqueGroup.addMember(groupMember);
            entityManager.persist(uniqueGroup);
            entityManager.persist(groupMember);
            uniqueGroupId = uniqueGroup.getId();
            groupMemberId = groupMember.getId();
        });
        // Revision 2
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.multiplerelations.GroupMember groupMember = entityManager.find(.class, groupMemberId);
            final org.hibernate.envers.test.integration.multiplerelations.MultiGroup multiGroup = new org.hibernate.envers.test.integration.multiplerelations.MultiGroup();
            groupMember.addMultiGroup(multiGroup);
            entityManager.persist(multiGroup);
        });
    }

    @Test
    public void testUniqueGroupFound() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.multiplerelations.GroupMember groupMember = entityManager.find(.class, groupMemberId);
            assertNotNull(groupMember);
            assertNotNull(groupMember.getUniqueGroup());
        });
    }

    @Test
    public void testUniqueGroupFromAuditHistory() {
        Assert.assertEquals(uniqueGroupId, getCurrentAuditUniqueGroupId());
    }

    @Entity(name = "GroupMember")
    @Audited
    public static class GroupMember {
        @Id
        @GeneratedValue
        private Integer id;

        @ManyToOne
        @JoinColumn(name = "uniqueGroup_id", insertable = false, updatable = false)
        private GroupMemberTest.UniqueGroup uniqueGroup;

        @ManyToMany(mappedBy = "members")
        private List<GroupMemberTest.MultiGroup> multiGroups = new ArrayList<>();

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public GroupMemberTest.UniqueGroup getUniqueGroup() {
            return uniqueGroup;
        }

        public void setUniqueGroup(GroupMemberTest.UniqueGroup uniqueGroup) {
            this.uniqueGroup = uniqueGroup;
        }

        public List<GroupMemberTest.MultiGroup> getMultiGroups() {
            return multiGroups;
        }

        public void setMultiGroups(List<GroupMemberTest.MultiGroup> multiGroups) {
            this.multiGroups = multiGroups;
        }

        public void addMultiGroup(GroupMemberTest.MultiGroup multiGroup) {
            this.multiGroups.add(multiGroup);
            multiGroup.addMember(this);
        }
    }

    @Entity(name = "UniqueGroup")
    @Audited
    public static class UniqueGroup {
        @Id
        @GeneratedValue
        private Integer id;

        @OneToMany
        @JoinColumn(name = "uniqueGroup_id")
        @AuditMappedBy(mappedBy = "uniqueGroup")
        private Set<GroupMemberTest.GroupMember> members = new HashSet<>();

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Set<GroupMemberTest.GroupMember> getMembers() {
            return members;
        }

        public void setMembers(Set<GroupMemberTest.GroupMember> members) {
            this.members = members;
        }

        public void addMember(GroupMemberTest.GroupMember groupMember) {
            this.members.add(groupMember);
        }
    }

    @Entity(name = "MultiGroup")
    @Audited
    public static class MultiGroup {
        @Id
        @GeneratedValue
        private Integer id;

        @ManyToMany
        @OrderColumn
        private List<GroupMemberTest.GroupMember> members = new ArrayList<>();

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public List<GroupMemberTest.GroupMember> getMembers() {
            return members;
        }

        public void setMembers(List<GroupMemberTest.GroupMember> members) {
            this.members = members;
        }

        public void addMember(GroupMemberTest.GroupMember groupMember) {
            this.members.add(groupMember);
        }
    }
}

