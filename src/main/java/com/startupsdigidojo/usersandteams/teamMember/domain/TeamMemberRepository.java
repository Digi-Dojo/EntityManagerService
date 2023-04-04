package com.startupsdigidojo.usersandteams.teamMember.domain;

import com.startupsdigidojo.usersandteams.startup.domain.Startup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    Optional<TeamMember> findByPuserId(Long id);
    List<TeamMember> findAllByPuserId(Long id);
    Optional<List<TeamMember>> findByRole(String role);
    Optional<List<TeamMember>> findUsersByRole(String role);
    Optional<TeamMember> findByPuserName(String name);
    Optional<List<TeamMember>> findTeamMembersByStartupId(Long id);
}
