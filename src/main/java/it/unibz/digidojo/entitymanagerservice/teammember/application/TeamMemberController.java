package it.unibz.digidojo.entitymanagerservice.teammember.application;

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

import it.unibz.digidojo.entitymanagerservice.teammember.domain.usecases.ManageTeamMember;
import it.unibz.digidojo.entitymanagerservice.teammember.domain.TeamMember;
import it.unibz.digidojo.sharedmodel.request.TeamMemberRequest;

@RestController
@RequestMapping(path = "/v1/team-member")
public class TeamMemberController {
    private final ManageTeamMember manageTeamMember;

    @Autowired
    public TeamMemberController(ManageTeamMember manageTeamMember) {
        this.manageTeamMember = manageTeamMember;
    }

    @PostMapping
    public TeamMember createTeamMember(@Validated @RequestBody TeamMemberRequest request) {
        return manageTeamMember.createTeamMember(request.userId(), request.role(), request.startupId());
    }

    @GetMapping("/{id}")
    public TeamMember findById(@PathVariable("id") Long id) {
        return manageTeamMember.findByTeamMemberId(id);
    }

    @GetMapping
    public List<TeamMember> find(TeamMemberRequest request) {
        if (request.userId() != null && request.startupId() != null) {
            return Collections.singletonList(
                    manageTeamMember.findByUserIdAndStartupId(request.userId(), request.startupId())
            );
        }

        if (request.startupId() != null) {
            return manageTeamMember.findTeamMembersByStartupId(request.startupId());
        }

        if (request.role() != null) {
            return manageTeamMember.findByRole(request.role());
        }

        //TODO: Return all team members in the system
        return Collections.emptyList();
    }

    @PatchMapping("/{id}")
    public TeamMember updateTeamMember(@PathVariable("id") Long id, @RequestBody TeamMemberRequest request) {
        return manageTeamMember.updateTeamMemberRole(id, request.role());
    }

    @DeleteMapping("/{id}")
    public void deleteTeamMember(@PathVariable("id") Long id) {
        manageTeamMember.deleteTeamMember(id);
    }
}
