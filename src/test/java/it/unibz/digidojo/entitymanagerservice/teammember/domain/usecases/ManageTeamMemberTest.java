package it.unibz.digidojo.entitymanagerservice.teammember.domain.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import it.unibz.digidojo.entitymanagerservice.startup.domain.model.Startup;
import it.unibz.digidojo.entitymanagerservice.startup.domain.usecases.SearchStartup;
import it.unibz.digidojo.entitymanagerservice.teammember.domain.TeamMemberBroadcaster;
import it.unibz.digidojo.entitymanagerservice.teammember.domain.TeamMemberRepository;
import it.unibz.digidojo.entitymanagerservice.teammember.domain.model.TeamMember;
import it.unibz.digidojo.entitymanagerservice.user.domain.model.User;
import it.unibz.digidojo.entitymanagerservice.user.domain.usecases.SearchUser;
import it.unibz.digidojo.entitymanagerservice.util.NumberGenerator;
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
}
