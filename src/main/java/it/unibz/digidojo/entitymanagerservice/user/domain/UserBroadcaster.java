package it.unibz.digidojo.entitymanagerservice.user.domain;

public interface UserBroadcaster {
    void emitUserCreated(User user);

    void emitUserUpdated(User user);

    void emitUserDeleted(User user);

    void emitUserLoggedIn(User user);
}
