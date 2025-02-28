package it.unibz.digidojo.entitymanagerservice.teammember.application;

import it.unibz.digidojo.entitymanagerservice.teammember.domain.model.TeamMember;
import it.unibz.digidojo.entitymanagerservice.teammember.domain.usecases.ManageTeamMember;
import it.unibz.digidojo.entitymanagerservice.teammember.domain.usecases.SearchTeamMember;
import it.unibz.digidojo.sharedmodel.dto.StartupDTO;
import it.unibz.digidojo.sharedmodel.dto.TeamMemberDTO;
import it.unibz.digidojo.sharedmodel.dto.UserDTO;
import it.unibz.digidojo.sharedmodel.request.TeamMemberRequest;
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
    private final SearchTeamMember searchTeamMember;

    @Autowired
    public TeamMemberController(ManageTeamMember manageTeamMember, final SearchTeamMember searchTeamMember) {
        this.manageTeamMember = manageTeamMember;
        this.searchTeamMember = searchTeamMember;
    }

    @PostMapping
    public TeamMemberDTO createTeamMember(@Validated @RequestBody TeamMemberRequest request) {
        return mapToDTO(manageTeamMember.createTeamMember(request.userId(), request.role(), request.startupId()));
    }

    @GetMapping("/{id}")
    public TeamMemberDTO getById(@PathVariable("id") Long id) {
        return mapToDTO(searchTeamMember.getById(id));
    }

    @GetMapping
    public List<TeamMemberDTO> findAll(TeamMemberRequest request) {
        return searchTeamMember.findAll(request)
                               .stream()
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
