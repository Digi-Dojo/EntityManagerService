package it.unibz.digidojo.entitymanagerservice.user.application;

import it.unibz.digidojo.entitymanagerservice.common.kafka.BaseProducer;
import it.unibz.digidojo.entitymanagerservice.user.domain.UserBroadcaster;
import it.unibz.digidojo.entitymanagerservice.user.domain.model.User;
import it.unibz.digidojo.entitymanagerservice.util.CRUD;
import it.unibz.digidojo.sharedmodel.dto.UserDTO;
import it.unibz.digidojo.sharedmodel.event.user.UserCreatedEvent;
import it.unibz.digidojo.sharedmodel.event.user.UserDeletedEvent;
import it.unibz.digidojo.sharedmodel.event.user.UserLoggedInEvent;
import it.unibz.digidojo.sharedmodel.event.user.UserUpdatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserProducer extends BaseProducer implements UserBroadcaster {
    public UserProducer(final KafkaTemplate<String, String> sender) {
        super(sender);
    }

    @Override
    public void emitUserCreated(User user) {
        UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmailAddress()
        );
        UserCreatedEvent userCreatedEvent = new UserCreatedEvent(userDTO);
        this.sendEvent(CRUD.CREATE, userCreatedEvent);
    }

    @Override
    public void emitUserUpdated(User user) {
        UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmailAddress()
        );
        UserUpdatedEvent userUpdatedEvent = new UserUpdatedEvent(userDTO);
        this.sendEvent(CRUD.UPDATE, userUpdatedEvent);
    }

    @Override
    public void emitUserDeleted(User user) {
        UserDeletedEvent userDeletedEvent = new UserDeletedEvent(user.getId());
        this.sendEvent(CRUD.DELETE, userDeletedEvent);
    }

    @Override
    public void emitUserLoggedIn(User user) {
        UserLoggedInEvent userLogInEvent = new UserLoggedInEvent(user.getId());
        this.sendEvent(CRUD.CREATE, userLogInEvent);
    }
}
