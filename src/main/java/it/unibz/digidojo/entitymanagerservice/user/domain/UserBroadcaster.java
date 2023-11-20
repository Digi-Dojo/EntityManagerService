package it.unibz.digidojo.entitymanagerservice.user.domain;

import it.unibz.digidojo.entitymanagerservice.user.domain.model.User;

public interface UserBroadcaster {
    void emitUserCreated(User user);

    void emitUserUpdated(User user);

    void emitUserDeleted(User user);

    void emitUserLoggedIn(User user);
}
