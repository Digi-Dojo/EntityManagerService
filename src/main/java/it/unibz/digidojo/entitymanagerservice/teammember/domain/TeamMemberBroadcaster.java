package it.unibz.digidojo.entitymanagerservice.teammember.domain;

import it.unibz.digidojo.entitymanagerservice.teammember.domain.model.TeamMember;

public interface TeamMemberBroadcaster {
    void emitTeamMemberCreated(TeamMember teamMember);

    void emitTeamMemberDeleted(TeamMember teamMember);
}
