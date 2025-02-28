package it.unibz.digidojo.entitymanagerservice.user.domain;

import it.unibz.digidojo.entitymanagerservice.user.domain.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAddress(String emailAddress);
}
