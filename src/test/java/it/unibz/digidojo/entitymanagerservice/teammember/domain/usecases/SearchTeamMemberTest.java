package it.unibz.digidojo.entitymanagerservice.teammember.domain.usecases;

import static java.util.Comparator.comparing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import it.unibz.digidojo.entitymanagerservice.startup.domain.model.Startup;
import it.unibz.digidojo.entitymanagerservice.teammember.domain.TeamMemberRepository;
import it.unibz.digidojo.entitymanagerservice.teammember.domain.model.TeamMember;
import it.unibz.digidojo.entitymanagerservice.user.domain.model.User;
import it.unibz.digidojo.entitymanagerservice.util.NumberGenerator;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
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

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SearchTeamMemberTest {
    private final User user = new User("Pippo", "pippo@unibz.it", "password");
    private final Startup startup = new Startup("DigiDojo", "Startup for digital services");
    @Mock
    private TeamMemberRepository teamMemberRepository;
    private SearchTeamMember useCase;

    @BeforeEach
    void setUp() {
        useCase = new SearchTeamMember(teamMemberRepository);
        user.setId(NumberGenerator.randomPositiveLong());
        startup.setId(NumberGenerator.randomPositiveLong());
    }

    @Test
    public void shouldGetById() {
        TeamMember teamMember = new TeamMember(
                NumberGenerator.randomPositiveLong(),
                user,
                startup,
                "Designer");

        when(teamMemberRepository.getReferenceById(teamMember.getId())).thenReturn(teamMember);
        TeamMember result = useCase.getById(teamMember.getId());
        assertThat(result)
                .isInstanceOf(TeamMember.class)
                .isNotNull();
        assertEquals(result.getId(), teamMember.getId());
        assertEquals(result.getUser().getId(), teamMember.getUser().getId());
        assertEquals(result.getRole(), teamMember.getRole());
        assertEquals(result.getStartup().getId(), teamMember.getStartup().getId());
    }

    @Test
    public void getByIdShouldThrowsForNonExistingTeamMembers() {
        Long id = NumberGenerator.randomPositiveLong();
        when(teamMemberRepository.getReferenceById(id)).thenThrow(EntityNotFoundException.class);
        assertThatThrownBy(() -> useCase.getById(id)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    public void shouldFindByUserId() {
        Startup startup1 = new Startup("startup1", "description1");
        do {
            startup1.setId(NumberGenerator.randomPositiveLong());
        } while (Objects.equals(startup1.getId(), startup.getId()));
        List<TeamMember> teamMembers = new ArrayList<>() {{
            add(new TeamMember(startup.getId(), user, startup, "Designer"));
            add(new TeamMember(startup1.getId(), user, startup1, "developer"));
        }};
        teamMembers = teamMembers.stream()
                                 .sorted(comparing(TeamMember::getId))
                                 .toList();

        when(teamMemberRepository.findAllByUserId(user.getId())).thenReturn(Optional.of(teamMembers));
        assertEquals(teamMembers, useCase.findByUserId(user.getId()));
    }

    @Test
    public void findByUserIdShouldNotThrowsForNonExistingTeamMembers() {
        Long id = NumberGenerator.randomPositiveLong();
        when(teamMemberRepository.findAllByUserId(id)).thenReturn(Optional.empty());
        assertEquals(Collections.emptyList(), useCase.findByUserId(id));
    }

    @Test
    public void shouldFindByStartupId() {
        User user1 = new User("Matteo", "malarcher@unibz.it", "pass");
        do {
            user1.setId(NumberGenerator.randomPositiveLong());
        } while (Objects.equals(user1.getId(), user.getId()));
        List<TeamMember> teamMembers = new ArrayList<>() {{
            add(new TeamMember(user.getId(), user, startup, "Designer"));
            add(new TeamMember(user1.getId(), user1, startup, "developer"));
        }};

        teamMembers = teamMembers.stream()
                                 .sorted(comparing(TeamMember::getId))
                                 .toList();

        when(teamMemberRepository.findTeamMembersByStartupId(startup.getId()))
                .thenReturn(Optional.of(teamMembers));
        assertEquals(teamMembers, useCase.findByStartupId(startup.getId()));
    }

    @Test
    public void findByStartupIdShouldNotThrowsForNonExistingTeamMembers() {
        when(teamMemberRepository.findTeamMembersByStartupId(startup.getId()))
                .thenReturn(Optional.empty());
        assertEquals(Collections.emptyList(), useCase.findByUserId(startup.getId()));
    }

    @Test
    public void shouldFindByUserIdAndStartupId() {
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
    public void findByUserIdAndStartupIdShouldNotThrowsForNonExistingTeamMember() {
        when(teamMemberRepository.findByUserIdAndStartupId(user.getId(), startup.getId()))
                .thenReturn(Optional.empty());
        assertNull(useCase.findByUserIdAndStartupId(user.getId(), startup.getId()));
    }

    @Test
    public void shouldFindByRole() {
        Startup startup1 = new Startup("startup1", "description1");
        do {
            startup1.setId(NumberGenerator.randomPositiveLong());
        } while (Objects.equals(startup1.getId(), startup.getId()));
        List<TeamMember> teamMembers = new ArrayList<>() {{
            add(new TeamMember(startup.getId(), user, startup, "Designer"));
            add(new TeamMember(startup1.getId(), user, startup1, "Designer"));
        }};
        teamMembers = teamMembers.stream()
                                 .sorted(comparing(TeamMember::getId))
                                 .toList();
        when(teamMemberRepository.findAllByRole("Designer"))
                .thenReturn(Optional.of(teamMembers));
        assertEquals(teamMembers, useCase.findByRole("Designer"));
    }

    @Test
    public void findByRoleShouldNotThrowsForNonExistingTeamMembersWithSuchRole() {
        when(teamMemberRepository.findAllByRole(anyString())).thenReturn(Optional.empty());
        assertEquals(Collections.emptyList(), useCase.findByRole("Designer"));
    }
}
