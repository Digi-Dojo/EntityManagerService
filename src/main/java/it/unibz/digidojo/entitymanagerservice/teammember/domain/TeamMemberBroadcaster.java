package it.unibz.digidojo.entitymanagerservice.teammember.domain;

public interface TeamMemberBroadcaster {
    void emitTeamMemberCreated(TeamMember teamMember);

    void emitTeamMemberDeleted(TeamMember teamMember);
}
