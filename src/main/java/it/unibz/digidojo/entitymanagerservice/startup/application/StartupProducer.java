package it.unibz.digidojo.entitymanagerservice.startup.application;

import it.unibz.digidojo.entitymanagerservice.common.kafka.BaseProducer;
import it.unibz.digidojo.entitymanagerservice.startup.domain.StartupBroadcaster;
import it.unibz.digidojo.entitymanagerservice.startup.domain.model.Startup;
import it.unibz.digidojo.entitymanagerservice.util.CRUD;
import it.unibz.digidojo.sharedmodel.dto.StartupDTO;
import it.unibz.digidojo.sharedmodel.event.startup.StartupCreatedEvent;
import it.unibz.digidojo.sharedmodel.event.startup.StartupDeletedEvent;
import it.unibz.digidojo.sharedmodel.event.startup.StartupUpdatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class StartupProducer extends BaseProducer implements StartupBroadcaster {
    @Autowired
    public StartupProducer(final KafkaTemplate<String, String> sender) {
        super(sender);
    }

    @Override
    public void emitStartupCreated(Startup startup) {
        StartupDTO startupDTO = new StartupDTO(
                startup.getId(),
                startup.getName(),
                startup.getDescription()
        );
        StartupCreatedEvent startupCreatedEvent = new StartupCreatedEvent(startupDTO);
        this.sendEvent(CRUD.CREATE, startupCreatedEvent);
    }

    @Override
    public void emitStartupUpdated(Startup startup) {
        StartupDTO startupDTO = new StartupDTO(
                startup.getId(),
                startup.getName(),
                startup.getDescription()
        );
        StartupUpdatedEvent startupCreatedEvent = new StartupUpdatedEvent(startupDTO);
        this.sendEvent(CRUD.UPDATE, startupCreatedEvent);
    }

    @Override
    public void emitStartupDeleted(Startup startup) {
        StartupDeletedEvent startupDeletedEvent = new StartupDeletedEvent(startup.getId());
        this.sendEvent(CRUD.DELETE, startupDeletedEvent);
    }
}
