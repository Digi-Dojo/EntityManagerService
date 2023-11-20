package it.unibz.digidojo.entitymanagerservice.teammember.application;

import it.unibz.digidojo.entitymanagerservice.teammember.domain.model.TeamMember;
import it.unibz.digidojo.entitymanagerservice.teammember.domain.usecases.ManageTeamMember;
import it.unibz.digidojo.sharedmodel.dto.StartupDTO;
import it.unibz.digidojo.sharedmodel.dto.TeamMemberDTO;
import it.unibz.digidojo.sharedmodel.dto.UserDTO;
import it.unibz.digidojo.sharedmodel.request.TeamMemberRequest;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/v1/team-member")
public class TeamMemberController {
    private final ManageTeamMember manageTeamMember;

    @Autowired
    public TeamMemberController(ManageTeamMember manageTeamMember) {
        this.manageTeamMember = manageTeamMember;
    }

    @PostMapping
    public TeamMemberDTO createTeamMember(@Validated @RequestBody TeamMemberRequest request) {
        return mapToDTO(manageTeamMember.createTeamMember(request.userId(), request.role(), request.startupId()));
    }

    @GetMapping("/{id}")
    public TeamMemberDTO findById(@PathVariable("id") Long id) {
        return mapToDTO(manageTeamMember.findByTeamMemberId(id));
    }

    @GetMapping
    public List<TeamMemberDTO> find(TeamMemberRequest request) {
        List<TeamMember> foundTeamMembers = null;
        if (request.userId() != null && request.startupId() != null) {
            foundTeamMembers = Collections.singletonList(
                    manageTeamMember.findByUserIdAndStartupId(request.userId(), request.startupId())
            );
        }

        if (request.startupId() != null) {
            foundTeamMembers = manageTeamMember.findTeamMembersByStartupId(request.startupId());
        }

        if (request.role() != null) {
            foundTeamMembers = manageTeamMember.findByRole(request.role());

        }

        if (foundTeamMembers == null) {
            //TODO: Return all team members in the system
            return Collections.emptyList();
        }

        return foundTeamMembers.stream()
                               .map(this::mapToDTO)
                               .toList();
    }

    @PatchMapping("/{id}")
    public TeamMemberDTO updateTeamMember(@PathVariable("id") Long id, @RequestBody TeamMemberRequest request) {
        return mapToDTO(manageTeamMember.updateTeamMemberRole(id, request.role()));
    }

    @DeleteMapping("/{id}")
    public void deleteTeamMember(@PathVariable("id") Long id) {
        manageTeamMember.deleteTeamMember(id);
    }

    private TeamMemberDTO mapToDTO(final TeamMember teamMember) {
        return new TeamMemberDTO(
                teamMember.getId(),
                teamMember.getRole(),
                new UserDTO(teamMember.getUser().getId(), teamMember.getUser().getName(), teamMember.getUser().getEmailAddress()),
                new StartupDTO(teamMember.getStartup().getId(), teamMember.getStartup().getName(), teamMember.getStartup().getDescription())
        );
    }
}
