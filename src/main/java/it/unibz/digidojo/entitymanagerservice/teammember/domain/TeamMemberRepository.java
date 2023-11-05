package it.unibz.digidojo.entitymanagerservice.teammember.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    Optional<List<TeamMember>> findAllByUserId(Long id);

    Optional<List<TeamMember>> findAllByRole(String role);

    Optional<List<TeamMember>> findTeamMembersByStartupId(Long id);

    Optional<TeamMember> findByUserIdAndStartupId(Long userId, Long startupId);
}
