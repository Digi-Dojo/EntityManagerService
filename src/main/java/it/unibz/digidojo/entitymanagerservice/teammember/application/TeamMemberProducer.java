package it.unibz.digidojo.entitymanagerservice.teammember.application;

import it.unibz.digidojo.entitymanagerservice.common.kafka.BaseProducer;
import it.unibz.digidojo.entitymanagerservice.teammember.domain.TeamMemberBroadcaster;
import it.unibz.digidojo.entitymanagerservice.teammember.domain.model.TeamMember;
import it.unibz.digidojo.entitymanagerservice.util.CRUD;
import it.unibz.digidojo.sharedmodel.dto.StartupDTO;
import it.unibz.digidojo.sharedmodel.dto.TeamMemberDTO;
import it.unibz.digidojo.sharedmodel.dto.UserDTO;
import it.unibz.digidojo.sharedmodel.event.teammember.StartupTeamMemberCreatedEvent;
import it.unibz.digidojo.sharedmodel.event.teammember.StartupTeamMemberDeletedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TeamMemberProducer extends BaseProducer implements TeamMemberBroadcaster {
    @Autowired
    public TeamMemberProducer(final KafkaTemplate<String, String> sender) {
        super(sender);
    }

    @Override
    public void emitTeamMemberCreated(TeamMember teamMember) {
        TeamMemberDTO teamMemberDTO = new TeamMemberDTO(
                teamMember.getId(),
                teamMember.getRole(),
                new UserDTO(teamMember.getUser().getId(), teamMember.getUser().getName(), teamMember.getUser().getEmailAddress()),
                new StartupDTO(teamMember.getStartup().getId(), teamMember.getStartup().getName(), teamMember.getStartup().getDescription())
        );
        StartupTeamMemberCreatedEvent event = new StartupTeamMemberCreatedEvent(teamMemberDTO);
        this.sendEvent(CRUD.CREATE, event);
    }

    @Override
    public void emitTeamMemberDeleted(TeamMember teamMember) {
        StartupTeamMemberDeletedEvent event = new StartupTeamMemberDeletedEvent(teamMember.getId());
        this.sendEvent(CRUD.DELETE, event);
    }
}
