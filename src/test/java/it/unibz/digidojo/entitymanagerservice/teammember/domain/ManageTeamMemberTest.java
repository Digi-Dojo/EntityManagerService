package it.unibz.digidojo.entitymanagerservice.teammember.domain;

import it.unibz.digidojo.entitymanagerservice.startup.domain.Startup;
import it.unibz.digidojo.entitymanagerservice.startup.domain.StartupRepository;
import it.unibz.digidojo.entitymanagerservice.user.domain.User;
import it.unibz.digidojo.entitymanagerservice.user.domain.UserRepository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ManageTeamMemberTest {
    private ManageTeamMember underTest;
    User user;
    Startup startup;

    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private StartupRepository startupRepository;
    @Mock
    private TeamMemberBroadcaster teamMemberBroadcaster;

    @BeforeEach
    void setUp() {
        underTest = new ManageTeamMember(teamMemberRepository, userRepository, startupRepository, teamMemberBroadcaster);
        user = new User("Pippo", "pippo@unibz.it", "password");
        user.setId(randomPositiveLong());
        startup = new Startup("DigiDojo", "Startup for digital services");
        startup.setId(randomPositiveLong());

    }

    @Test
    public void itCreatesATeamMember() {
        String role = "Software Developer";

        TeamMember teamMember = new TeamMember(user, startup, role);

        when(teamMemberRepository.findByUserIdAndStartupId(user.getId(), startup.getId()))
                .thenReturn(Optional.empty());
        when(teamMemberRepository.save(any()))
                .thenReturn(new TeamMember(user, startup, teamMember.getRole()));

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(startupRepository.findById(startup.getId()))
                .thenReturn(Optional.of(startup));
        TeamMember result = underTest.createTeamMember(user.getId(), role, startup.getId());

        assertThat(result).isInstanceOf(TeamMember.class);
        Assertions.assertThat(result.getUser().getName()).isEqualTo(user.getName());
        Assertions.assertThat(result.getUser().getId())
                  .isNotNull()
                  .isGreaterThan(0);
    }

    @Test
    public void itThrowsForDuplicationInId() {
        String role = "Software Developer";
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(startupRepository.findById(startup.getId()))
                .thenReturn(Optional.of(startup));
        when(teamMemberRepository.findByUserIdAndStartupId(anyLong(), anyLong()))
                .thenReturn(Optional.of(new TeamMember(user, startup, role)));


        assertThatThrownBy(() -> underTest.createTeamMember(user.getId(), role, startup.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A Team Member with User id #" + user.getId() + " and Startup id # "
                        + startup.getId() + " is already present");
    }

    @Test
    public void updateTeamMemberRoleUpdatesTheRole() {
        String oldRole = "Assistant";
        String newRole = "Manager";
        Long id = randomPositiveLong();


        when(teamMemberRepository.findById(id))
                .thenReturn(Optional.of(new TeamMember(id, user, startup, oldRole)));
        when(teamMemberRepository.save(any()))
                .thenReturn(new TeamMember(id, user, startup, newRole));

        TeamMember teamMember = underTest.updateTeamMemberRole(id, newRole);

        assertThat(teamMember.getRole()).isEqualTo(newRole);
    }

    @Test
    public void updateTeamMemberRoleThrowsForNonExistingTeamMember() {
        when(teamMemberRepository.findById(anyLong())).thenReturn(Optional.empty());
        Long id = randomPositiveLong();
        assertThatThrownBy(() -> underTest.updateTeamMemberRole(id, "role"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No User with id TeamMember #" + id + " present in any Team yet");
    }

    @Test
    public void deleteThrowsForNonExistingTeamMember() {
        when(teamMemberRepository.findById(anyLong())).thenReturn(Optional.empty());
        Long id = randomPositiveLong();
        assertThatThrownBy(() -> underTest.deleteTeamMember(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No TeamMember with id #" + id + " present yet");
    }

    @Test
    public void findByUserIdAndStartupIdReturnsTheTeamMember() {
        when(teamMemberRepository.findByUserIdAndStartupId(user.getId(), startup.getId()))
                .thenReturn(Optional.of(new TeamMember(user, startup, "role")));
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(startupRepository.findById(startup.getId()))
                .thenReturn(Optional.of(startup));

        TeamMember result = underTest.findByUserIdAndStartupId(user.getId(), startup.getId());

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
        when(startupRepository.findById(startup.getId()))
                .thenReturn(Optional.of(startup));
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> underTest.findByUserIdAndStartupId(user.getId(), startup.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No user found with id: "+user.getId());
    }

    @Test
    public void findByUserIdAndStartupIdThrowsForNonExistingStartup() {
        when(startupRepository.findById(startup.getId()))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> underTest.findByUserIdAndStartupId(user.getId(), startup.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Startup with id " + startup.getId() + " is not present in the database");
    }

    @Test
    public void findByUserIdAndStartupIdThrowsForNonExistingTeamMember() {
        when(startupRepository.findById(startup.getId()))
                .thenReturn(Optional.of(startup));
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(teamMemberRepository.findByUserIdAndStartupId(user.getId(), startup.getId()))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> underTest.findByUserIdAndStartupId(user.getId(), startup.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No User with Id " + user.getId() + " belonging to startup with Id " + startup.getId());
    }

    @Test
    public void findUsersByStartupIdReturnsListOfUsers() {
        User user1 = new User("Matteo", "malarcher@unibz.it", "pass");
        do {
            user1.setId(randomPositiveLong());
        } while (Objects.equals(user1.getId(), user.getId()));
        when(startupRepository.findById(startup.getId()))
                .thenReturn(Optional.of(startup));
        when(teamMemberRepository.findTeamMembersByStartupId(startup.getId()))
                .thenReturn(Optional.of(new ArrayList<>() {{
                    add(new TeamMember(user, startup, "designer"));
                    add(new TeamMember(user1, startup, "developer"));
                }}));
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));
        List<User> users = new ArrayList<>() {{
            add(user);
            add(user1);
        }};
        users = users.stream()
                .sorted(comparing(User::getId))
                .toList();

        List<User> result = underTest.findUsersByStartupId(startup.getId()).stream()
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
            user1.setId(randomPositiveLong());
        } while (Objects.equals(user1.getId(), user.getId()));
        List<TeamMember> teamMembers = new ArrayList<>() {{
            add(new TeamMember(user.getId(), user, startup, "designer"));
            add(new TeamMember(user1.getId(), user1, startup, "developer"));
        }};

        teamMembers = teamMembers.stream()
                .sorted(comparing(TeamMember::getId))
                .toList();

        when(startupRepository.findById(startup.getId()))
                .thenReturn(Optional.of(startup));
        when(teamMemberRepository.findTeamMembersByStartupId(startup.getId()))
                .thenReturn(Optional.of(teamMembers));

        List<TeamMember> result = underTest.findTeamMembersByStartupId(startup.getId());

        List<TeamMember> finalTeamMembers = teamMembers;
        assertThat(result).isNotNull();
        result.forEach(o -> assertEquals(o.getId(), finalTeamMembers.get(result.indexOf(o)).getId()));
    }

    @Test
    public void findTeamMembersByStartupIdThrowsForNonExistingTeamMembersOfSuchStartup() {
        when(startupRepository.findById(startup.getId()))
                .thenReturn(Optional.of(startup));
        when(teamMemberRepository.findTeamMembersByStartupId(startup.getId()))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> underTest.findTeamMembersByStartupId(startup.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No Team Members belonging to startup with Id " + startup.getId());
    }

    @Test
    public void findByRoleReturnsListOfTeamMembers() {
        Startup startup1 = new Startup("startup1", "description1");
        do {
            startup1.setId(randomPositiveLong());
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

        List<TeamMember> result = underTest.findByRole("designer");

        List<TeamMember> finalTeamMembers = teamMembers;
        assertThat(result).isNotNull();
        result.forEach(o -> assertEquals(o.getId(), finalTeamMembers.get(result.indexOf(o)).getId()));
    }

    @Test
    public void findByRoleThrowsForNonExistingTeamMembersWithSuchRole() {
        when(teamMemberRepository.findAllByRole(anyString()))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> underTest.findByRole("designer"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Team Member with role #designer not found");
    }

    @Test
    public void findAllByUserIdReturnsListOfTeamMembers() {
        Startup startup1 = new Startup("startup1", "description1");
        do {
            startup1.setId(randomPositiveLong());
        } while (Objects.equals(startup1.getId(), startup.getId()));
        List<TeamMember> teamMembers = new ArrayList<>() {{
            add(new TeamMember(startup.getId(), user, startup, "designer"));
            add(new TeamMember(startup1.getId(), user, startup1, "developer"));
        }};
        teamMembers = teamMembers.stream()
                .sorted(comparing(TeamMember::getId))
                .toList();

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(teamMemberRepository.findAllByUserId(user.getId()))
                .thenReturn(Optional.of(teamMembers));

        List<TeamMember> result = underTest.findAllByUserId(user.getId());

        List<TeamMember> finalTeamMembers = teamMembers;
        assertThat(result).isNotNull();
        result.forEach(o -> assertEquals(o.getId(), finalTeamMembers.get(result.indexOf(o)).getId()));
    }

    @Test
    public void findAllByUserIdThrowsForNonExistingTeamMembers() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(teamMemberRepository.findAllByUserId(anyLong()))
                .thenReturn(Optional.empty());
        Long id = randomPositiveLong();
        assertThatThrownBy(() -> underTest.findAllByUserId(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Team Members with user id #" + id + " not found");
    }

    @Test
    public void findByTeamMemberIdReturnsTeamMember() {
        TeamMember teamMember = new TeamMember(randomPositiveLong(), user, startup, "designer");
        when(teamMemberRepository.findById(teamMember.getId()))
                .thenReturn(Optional.of(teamMember));
        TeamMember result = underTest.findByTeamMemberId(teamMember.getId());
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
        Long id = randomPositiveLong();
        assertThatThrownBy(() -> underTest.findByTeamMemberId(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Team Member with the id #" + id + " not found");
    }

    private Long randomPositiveLong() {
        long leftLimit = 1L;
        long rightLimit = 1000L;
        return leftLimit + (long) (Math.random() * (rightLimit - leftLimit));
    }
}
