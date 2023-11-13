package it.unibz.digidojo.entitymanagerservice.teammember.domain.usecases;

import static java.util.Comparator.comparing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import it.unibz.digidojo.entitymanagerservice.startup.domain.Startup;
import it.unibz.digidojo.entitymanagerservice.startup.domain.usecases.SearchStartup;
import it.unibz.digidojo.entitymanagerservice.teammember.domain.TeamMember;
import it.unibz.digidojo.entitymanagerservice.teammember.domain.TeamMemberBroadcaster;
import it.unibz.digidojo.entitymanagerservice.teammember.domain.TeamMemberRepository;
import it.unibz.digidojo.entitymanagerservice.user.domain.User;
import it.unibz.digidojo.entitymanagerservice.user.domain.usecases.SearchUser;
import it.unibz.digidojo.entitymanagerservice.util.NumberGenerator;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ManageTeamMemberTest {
    private final User user = new User("Pippo", "pippo@unibz.it", "password");
    private final Startup startup = new Startup("DigiDojo", "Startup for digital services");
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private SearchUser searchUsers;
    @Mock
    private SearchStartup searchStartups;
    @Mock
    private TeamMemberBroadcaster teamMemberBroadcaster;
    private ManageTeamMember useCase;

    @BeforeEach
    void setUp() {
        useCase = new ManageTeamMember(teamMemberRepository, searchUsers, searchStartups, teamMemberBroadcaster);
        user.setId(NumberGenerator.randomPositiveLong());
        startup.setId(NumberGenerator.randomPositiveLong());
        when(searchUsers.findById(user.getId())).thenReturn(user);
        when(searchStartups.findById(startup.getId())).thenReturn(startup);
    }

    @Test
    public void itCreatesATeamMember() {
        String role = "Software Developer";
        TeamMember teamMember = new TeamMember(user, startup, role);

        when(teamMemberRepository.findByUserIdAndStartupId(user.getId(), startup.getId()))
                .thenReturn(Optional.empty());
        when(teamMemberRepository.save(any()))
                .thenReturn(new TeamMember(user, startup, teamMember.getRole()));

        TeamMember result = useCase.createTeamMember(user.getId(), role, startup.getId());

        assertThat(result).isInstanceOf(TeamMember.class);
        Assertions.assertThat(result.getUser().getName()).isEqualTo(user.getName());
        Assertions.assertThat(result.getUser().getId())
                  .isNotNull()
                  .isGreaterThan(0);
    }

    @Test
    public void itThrowsForDuplicationInId() {
        String role = "Software Developer";
        when(teamMemberRepository.findByUserIdAndStartupId(anyLong(), anyLong()))
                .thenReturn(Optional.of(new TeamMember(user, startup, role)));

        assertThatThrownBy(() -> useCase.createTeamMember(user.getId(), role, startup.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void updateTeamMemberRoleUpdatesTheRole() {
        String oldRole = "Assistant";
        String newRole = "Manager";
        Long id = NumberGenerator.randomPositiveLong();

        when(teamMemberRepository.findById(id))
                .thenReturn(Optional.of(new TeamMember(id, user, startup, oldRole)));
        when(teamMemberRepository.save(any()))
                .thenReturn(new TeamMember(id, user, startup, newRole));

        TeamMember teamMember = useCase.updateTeamMemberRole(id, newRole);
        assertThat(teamMember.getRole()).isEqualTo(newRole);
    }

    @Test
    public void updateTeamMemberRoleThrowsForNonExistingTeamMember() {
        when(teamMemberRepository.findById(anyLong())).thenReturn(Optional.empty());
        Long id = NumberGenerator.randomPositiveLong();
        assertThatThrownBy(() -> useCase.updateTeamMemberRole(id, "role"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void deleteThrowsForNonExistingTeamMember() {
        when(teamMemberRepository.findById(anyLong())).thenReturn(Optional.empty());
        Long id = NumberGenerator.randomPositiveLong();
        assertThatThrownBy(() -> useCase.deleteTeamMember(id))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void findByUserIdAndStartupIdReturnsTheTeamMember() {
        when(teamMemberRepository.findByUserIdAndStartupId(user.getId(), startup.getId()))
                .thenReturn(Optional.of(new TeamMember(user, startup, "role")));

        TeamMember result = useCase.findByUserIdAndStartupId(user.getId(), startup.getId());

        assertThat(result).isInstanceOf(TeamMember.class);
        Assertions.assertThat(result.getUser().getId())
                  .isNotNull()
                  .isEqualTo(user.getId());
        Assertions.assertThat(result.getStartup().getId())
                  .isNotNull()
                  .isEqualTo(startup.getId());
        assertThat(result.getRole())
                .isEqualTo("role");
    }

    @Test
    public void findByUserIdAndStartupIdThrowsForNonExistingUser() {
        when(searchStartups.findById(user.getId())).thenThrow(IllegalArgumentException.class);
        assertThatThrownBy(() -> useCase.findByUserIdAndStartupId(user.getId(), startup.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void findByUserIdAndStartupIdThrowsForNonExistingStartup() {
        when(searchStartups.findById(startup.getId())).thenThrow(IllegalArgumentException.class);
        assertThatThrownBy(() -> useCase.findByUserIdAndStartupId(user.getId(), startup.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void findByUserIdAndStartupIdThrowsForNonExistingTeamMember() {
        when(teamMemberRepository.findByUserIdAndStartupId(user.getId(), startup.getId()))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.findByUserIdAndStartupId(user.getId(), startup.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void findUsersByStartupIdReturnsListOfUsers() {
        User user1 = new User("Matteo", "malarcher@unibz.it", "pass");
        do {
            user1.setId(NumberGenerator.randomPositiveLong());
        } while (Objects.equals(user1.getId(), user.getId()));
        when(teamMemberRepository.findTeamMembersByStartupId(startup.getId()))
                .thenReturn(Optional.of(new ArrayList<>() {{
                    add(new TeamMember(user, startup, "designer"));
                    add(new TeamMember(user1, startup, "developer"));
                }}));
        when(searchUsers.findById(user1.getId())).thenReturn(user1);
        List<User> users = new ArrayList<>() {{
            add(user);
            add(user1);
        }};
        users = users.stream()
                     .sorted(comparing(User::getId))
                     .toList();

        List<User> result = useCase.findUsersByStartupId(startup.getId()).stream()
                                   .sorted(comparing(User::getId))
                                   .toList();

        List<User> finalUsers = users;
        assertThat(result).isNotNull();
        result.forEach(o -> assertEquals(o.getId(), finalUsers.get(result.indexOf(o)).getId()));
    }

    @Test
    public void findTeamMembersByStartupIdReturnsListOfTeamMembers() {
        User user1 = new User("Matteo", "malarcher@unibz.it", "pass");
        do {
            user1.setId(NumberGenerator.randomPositiveLong());
        } while (Objects.equals(user1.getId(), user.getId()));
        List<TeamMember> teamMembers = new ArrayList<>() {{
            add(new TeamMember(user.getId(), user, startup, "designer"));
            add(new TeamMember(user1.getId(), user1, startup, "developer"));
        }};

        teamMembers = teamMembers.stream()
                                 .sorted(comparing(TeamMember::getId))
                                 .toList();

        when(teamMemberRepository.findTeamMembersByStartupId(startup.getId()))
                .thenReturn(Optional.of(teamMembers));

        List<TeamMember> result = useCase.findTeamMembersByStartupId(startup.getId());
        List<TeamMember> finalTeamMembers = teamMembers;
        assertThat(result).isNotNull();
        result.forEach(o -> assertEquals(o.getId(), finalTeamMembers.get(result.indexOf(o)).getId()));
    }

    @Test
    public void findTeamMembersByStartupIdThrowsForNonExistingTeamMembersOfSuchStartup() {
        when(teamMemberRepository.findTeamMembersByStartupId(startup.getId()))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.findTeamMembersByStartupId(startup.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void findByRoleReturnsListOfTeamMembers() {
        Startup startup1 = new Startup("startup1", "description1");
        do {
            startup1.setId(NumberGenerator.randomPositiveLong());
        } while (Objects.equals(startup1.getId(), startup.getId()));
        List<TeamMember> teamMembers = new ArrayList<>() {{
            add(new TeamMember(startup.getId(), user, startup, "designer"));
            add(new TeamMember(startup1.getId(), user, startup1, "designer"));
        }};
        teamMembers = teamMembers.stream()
                                 .sorted(comparing(TeamMember::getId))
                                 .toList();
        when(teamMemberRepository.findAllByRole("designer"))
                .thenReturn(Optional.of(teamMembers));

        List<TeamMember> result = useCase.findByRole("designer");

        List<TeamMember> finalTeamMembers = teamMembers;
        assertThat(result).isNotNull();
        result.forEach(o -> assertEquals(o.getId(), finalTeamMembers.get(result.indexOf(o)).getId()));
    }

    @Test
    public void findByRoleThrowsForNonExistingTeamMembersWithSuchRole() {
        when(teamMemberRepository.findAllByRole(anyString()))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.findByRole("designer"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void findAllByUserIdReturnsListOfTeamMembers() {
        Startup startup1 = new Startup("startup1", "description1");
        do {
            startup1.setId(NumberGenerator.randomPositiveLong());
        } while (Objects.equals(startup1.getId(), startup.getId()));
        List<TeamMember> teamMembers = new ArrayList<>() {{
            add(new TeamMember(startup.getId(), user, startup, "designer"));
            add(new TeamMember(startup1.getId(), user, startup1, "developer"));
        }};
        teamMembers = teamMembers.stream()
                                 .sorted(comparing(TeamMember::getId))
                                 .toList();

        when(teamMemberRepository.findAllByUserId(user.getId()))
                .thenReturn(Optional.of(teamMembers));

        List<TeamMember> result = useCase.findAllByUserId(user.getId());

        List<TeamMember> finalTeamMembers = teamMembers;
        assertThat(result).isNotNull();
        result.forEach(o -> assertEquals(o.getId(), finalTeamMembers.get(result.indexOf(o)).getId()));
    }

    @Test
    public void findAllByUserIdThrowsForNonExistingTeamMembers() {
        when(searchUsers.findById(anyLong())).thenReturn(user);
        when(teamMemberRepository.findAllByUserId(anyLong()))
                .thenReturn(Optional.empty());
        Long id = NumberGenerator.randomPositiveLong();
        assertThatThrownBy(() -> useCase.findAllByUserId(id))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void findByTeamMemberIdReturnsTeamMember() {
        TeamMember teamMember = new TeamMember(NumberGenerator.randomPositiveLong(), user, startup, "designer");
        when(teamMemberRepository.findById(teamMember.getId()))
                .thenReturn(Optional.of(teamMember));
        TeamMember result = useCase.findByTeamMemberId(teamMember.getId());
        assertThat(result)
                .isInstanceOf(TeamMember.class)
                .isNotNull();
        assertEquals(result.getId(), teamMember.getId());
        org.junit.jupiter.api.Assertions.assertEquals(result.getUser().getId(), teamMember.getUser().getId());
        assertEquals(result.getRole(), teamMember.getRole());
        org.junit.jupiter.api.Assertions.assertEquals(result.getStartup().getId(), teamMember.getStartup().getId());
    }

    @Test
    public void findByTeamMemberIdThrowsForNonExistingTeamMembers() {
        when(teamMemberRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Long id = NumberGenerator.randomPositiveLong();
        assertThatThrownBy(() -> useCase.findByTeamMemberId(id))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
