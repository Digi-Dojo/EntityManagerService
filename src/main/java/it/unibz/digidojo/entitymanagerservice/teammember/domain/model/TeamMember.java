package it.unibz.digidojo.entitymanagerservice.teammember.domain.model;

import it.unibz.digidojo.entitymanagerservice.startup.domain.model.Startup;
import it.unibz.digidojo.entitymanagerservice.user.domain.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class TeamMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    @OneToOne
    private Startup startup;

    private String role;

    public TeamMember(User user, Startup startup, String role) {
        this.user = user;
        this.startup = startup;
        this.role = role;
    }
}
