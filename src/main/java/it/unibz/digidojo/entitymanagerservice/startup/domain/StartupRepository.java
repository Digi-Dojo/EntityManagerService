package it.unibz.digidojo.entitymanagerservice.startup.domain;

import it.unibz.digidojo.entitymanagerservice.startup.domain.model.Startup;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StartupRepository extends JpaRepository<Startup, Long> {
    Optional<Startup> findByName(String name);
}
