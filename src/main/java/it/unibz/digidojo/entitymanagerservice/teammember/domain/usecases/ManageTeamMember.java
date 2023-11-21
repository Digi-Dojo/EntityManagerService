package it.unibz.digidojo.entitymanagerservice.teammember.domain.usecases;

import it.unibz.digidojo.entitymanagerservice.startup.domain.model.Startup;
import it.unibz.digidojo.entitymanagerservice.startup.domain.usecases.SearchStartup;
import it.unibz.digidojo.entitymanagerservice.teammember.domain.TeamMemberBroadcaster;
import it.unibz.digidojo.entitymanagerservice.teammember.domain.TeamMemberRepository;
import it.unibz.digidojo.entitymanagerservice.teammember.domain.model.TeamMember;
import it.unibz.digidojo.entitymanagerservice.user.domain.model.User;
import it.unibz.digidojo.entitymanagerservice.user.domain.usecases.SearchUser;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ManageTeamMember {
    private final TeamMemberRepository teamMemberRepository;
    private final SearchUser searchUsers;
    private final SearchStartup searchStartups;
    private final TeamMemberBroadcaster teamMemberBroadcaster;

    /**
     * @param userId    id of the user that wants to become a team member in a startup
     * @param role      the role of the team member in this startup
     * @param startupId the id of the startup
     * @return the newly created team member
     * @throws IllegalArgumentException if no user with the provided userId is found, or if no startup with the provided
     *                                  startupId is found, or if a team member with the provided userId and startupId already exists
     */
    public TeamMember createTeamMember(Long userId, String role, Long startupId) {
        User user = searchUsers.findById(userId);
        Startup startup = searchStartups.findById(startupId);
        Optional<TeamMember> maybeTeamMember = teamMemberRepository.findByUserIdAndStartupId(user.getId(), startup.getId());

        if (maybeTeamMember.isPresent()) {
            throw new IllegalArgumentException("A Team Member with User id #" + user.getId() + " and Startup id # "
                    + startup.getId() + " is already present");
        }

        TeamMember teamMember = teamMemberRepository.save(new TeamMember(user, startup, role));
        teamMemberBroadcaster.emitTeamMemberCreated(teamMember);
        return teamMember;
    }

    /**
     * @param id id of the team member we want to delete
     * @throws IllegalArgumentException if no team member with the provided id is found
     */
    public void deleteTeamMember(Long id) {
        Optional<TeamMember> maybeTeamMember = teamMemberRepository.findById(id);

        if (maybeTeamMember.isEmpty()) {
            throw new IllegalArgumentException("No TeamMember with id #" + id + " present yet");
        }
        teamMemberRepository.delete(maybeTeamMember.get());
        teamMemberBroadcaster.emitTeamMemberDeleted(maybeTeamMember.get());
    }

    /**
     * @param id      id of the team member, whose role we want to change
     * @param newRole the new role that will replace the current one
     * @return the newly updated team member
     * @throws IllegalArgumentException if no team member with the provided id is found
     */
    public TeamMember updateTeamMemberRole(Long id, String newRole) {
        Optional<TeamMember> maybeTeamMember = teamMemberRepository.findById(id);

        if (maybeTeamMember.isEmpty()) {
            throw new IllegalArgumentException("No User with id TeamMember #" + id + " present in any Team yet");
        }
        TeamMember teamMember = maybeTeamMember.get();
        teamMember.setRole(newRole);
        return teamMemberRepository.save(teamMember);
    }
}
