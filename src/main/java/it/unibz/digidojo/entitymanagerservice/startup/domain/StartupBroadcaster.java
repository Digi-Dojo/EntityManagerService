package it.unibz.digidojo.entitymanagerservice.startup.domain;

import it.unibz.digidojo.entitymanagerservice.startup.domain.model.Startup;

public interface StartupBroadcaster {
    void emitStartupCreated(Startup startup);       //used to throw the event

    void emitStartupUpdated(Startup startup);

    void emitStartupDeleted(Startup startup);
}
