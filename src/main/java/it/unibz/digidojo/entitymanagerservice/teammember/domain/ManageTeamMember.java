package it.unibz.digidojo.entitymanagerservice.teammember.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.unibz.digidojo.entitymanagerservice.startup.domain.SearchStartup;
import it.unibz.digidojo.entitymanagerservice.startup.domain.Startup;
import it.unibz.digidojo.entitymanagerservice.startup.domain.StartupRepository;
import it.unibz.digidojo.entitymanagerservice.user.domain.SearchUser;
import it.unibz.digidojo.entitymanagerservice.user.domain.User;
import it.unibz.digidojo.entitymanagerservice.user.domain.UserRepository;


@Service
public class ManageTeamMember {
    private final TeamMemberRepository teamMemberRepository;
    private final SearchUser searchUsers;
    private final SearchStartup searchStartups;
    private final TeamMemberBroadcaster teamMemberBroadcaster;


    @Autowired
    public ManageTeamMember(TeamMemberRepository teamMemberRepository, UserRepository userRepository,
                            StartupRepository startupRepository, TeamMemberBroadcaster teamMemberBroadcaster) {
        this.teamMemberRepository = teamMemberRepository;
        searchUsers = new SearchUser(userRepository);
        searchStartups = new SearchStartup(startupRepository);
        this.teamMemberBroadcaster = teamMemberBroadcaster;
    }

    /**
     * @param id id of the team member we want to find
     * @return the team member with the provided id
     * @throws IllegalArgumentException if no team member with the provided id is found
     */
    public TeamMember findByTeamMemberId(Long id) {
        Optional<TeamMember> maybeTeamMember = teamMemberRepository.findById(id);

        if (maybeTeamMember.isEmpty()) {
            throw new IllegalArgumentException("Team Member with the id #" + id + " not found");
        }
        return maybeTeamMember.get();
    }

    /**
     * @param id id of the user
     * @return a list containing all the instances in the different startups of team member the user with the provided id belongs to
     * @throws IllegalArgumentException if no team member with the provided user id is found
     */
    public List<TeamMember> findAllByUserId(Long id) {
        searchUsers.findById(id);
        Optional<List<TeamMember>> maybeTeamMember = teamMemberRepository.findAllByUserId(id);

        if (maybeTeamMember.isEmpty()) {
            throw new IllegalArgumentException("Team Members with user id #" + id + " not found");
        }
        return maybeTeamMember.get();
    }

    /**
     * @param role role covered by the team members we want to find
     * @return a list containing all the team members that cover the provided role
     * @throws IllegalArgumentException if no team member with the provided role is found
     */
    public List<TeamMember> findByRole(String role) {
        Optional<List<TeamMember>> maybeExistRole = teamMemberRepository.findAllByRole(role);

        if (maybeExistRole.isEmpty()) {
            throw new IllegalArgumentException("Team Member with role #" + role + " not found");
        }
        return maybeExistRole.get();
    }

    /**
     * @param startupId the id of the startup, of which we want to find all team members
     * @return a list containing all the team members in the startup with the provided startupId
     * @throws IllegalArgumentException if no team member belonging to the startup is found
     */
    public List<TeamMember> findTeamMembersByStartupId(Long startupId) {
        searchStartups.findById(startupId);
        Optional<List<TeamMember>> maybeTeamMembers = teamMemberRepository.findTeamMembersByStartupId(startupId);

        if (maybeTeamMembers.isEmpty()) {
            throw new IllegalArgumentException("No Team Members belonging to startup with Id " + startupId);
        }

        return maybeTeamMembers.get();
    }

    /**
     * @param startupId the id of the startup, of which we want to find all affiliated users
     * @return a list containing all the users that are team members in the startup with the provided startupId
     * @throws IllegalArgumentException if no team member belonging to the startup is found
     */
    public List<User> findUsersByStartupId(Long startupId) {
        List<TeamMember> teamMembers = findTeamMembersByStartupId(startupId);
        List<User> users = new ArrayList<>();
        Set<Long> userIds = new HashSet<>();
        for (TeamMember t : teamMembers) {
            userIds.add(t.getUser().getId());
        }
        for (Long id : userIds) {
            users.add(searchUsers.findById(id));
        }
        return users;
    }

    /**
     * @param userId    the id of the user, who is a team member in the startup
     * @param startupId the id of the startup
     * @return the team member who is the user with id userId in the startup with startupId
     * @throws IllegalArgumentException if no user with the provided userId is found, or if no startup with the provided
     *                                  startupId is found, or if no team member with the provided userId and startupId is found
     */
    public TeamMember findByUserIdAndStartupId(Long userId, Long startupId) {
        searchStartups.findById(startupId);
        searchUsers.findById(userId);

        Optional<TeamMember> maybeTeamMember = teamMemberRepository.findByUserIdAndStartupId(userId, startupId);

        if (maybeTeamMember.isEmpty()) {
            throw new IllegalArgumentException("No User with Id " + userId + " belonging to startup with Id " + startupId);
        }

        return maybeTeamMember.get();
    }

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
