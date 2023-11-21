package it.unibz.digidojo.entitymanagerservice.teammember.domain.usecases;

import it.unibz.digidojo.entitymanagerservice.teammember.domain.TeamMemberRepository;
import it.unibz.digidojo.entitymanagerservice.teammember.domain.model.TeamMember;
import it.unibz.digidojo.sharedmodel.request.TeamMemberRequest;
import jakarta.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SearchTeamMember {
    private final TeamMemberRepository teamMemberRepository;

    /**
     * @param id id of the team member we want to find
     * @return the team member with the provided id
     * @throws IllegalArgumentException if the id is null
     * @throws EntityNotFoundException  if no team member with the provided id is found
     */
    public TeamMember getById(Long id) {
        return teamMemberRepository.getReferenceById(id);
    }

    /**
     * @param userId id of the user
     * @return a list of all team members associated with the user with userId
     * @throws IllegalArgumentException if the userId is null
     */
    public List<TeamMember> findByUserId(Long userId) {
        return teamMemberRepository.findAllByUserId(userId).orElse(Collections.emptyList());
    }

    /**
     * @param startupId the id of the startup
     * @return a list of all team members associated with the startup with startupId
     * @throws IllegalArgumentException if startupId is null
     */
    public List<TeamMember> findByStartupId(Long startupId) {
        return teamMemberRepository.findTeamMembersByStartupId(startupId).orElse(Collections.emptyList());
    }

    /**
     * @param userId    the id of the user
     * @param startupId the id of the startup
     * @return the team member who is the user with id userId in the startup with startupId or null if it doesn't exist
     * @throws IllegalArgumentException if userId or startupId is null
     */
    public TeamMember findByUserIdAndStartupId(Long userId, Long startupId) {
        return teamMemberRepository.findByUserIdAndStartupId(userId, startupId).orElse(null);
    }

    /**
     * @param role role of the team members we want to find
     * @return a list containing all the team members that have the provided role
     * @throws IllegalArgumentException if role is null
     */
    public List<TeamMember> findByRole(String role) {
        return teamMemberRepository.findAllByRole(role).orElse(Collections.emptyList());
    }

    /**
     * @param filters a shared record with possible filters to apply to the search
     * @return all the team members in the database that meet the filters
     */
    public List<TeamMember> findAll(TeamMemberRequest filters) {
        if (filters.userId() != null && filters.startupId() != null) {
            return Collections.singletonList(this.findByUserIdAndStartupId(filters.userId(), filters.startupId()));
        }

        if (filters.userId() != null) {
            return this.findByUserId(filters.userId());
        }

        if (filters.startupId() != null) {
            return this.findByStartupId(filters.startupId());
        }

        if (filters.role() != null) {
            return this.findByRole(filters.role());
        }

        return teamMemberRepository.findAll();
    }
}
