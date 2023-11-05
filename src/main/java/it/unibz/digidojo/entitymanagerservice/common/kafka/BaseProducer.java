package it.unibz.digidojo.entitymanagerservice.common.kafka;

import java.util.Collections;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;

import it.unibz.digidojo.entitymanagerservice.util.CRUD;
import it.unibz.digidojo.sharedmodel.event.BaseEvent;
import it.unibz.digidojo.sharedmodel.marshaller.Marshaller;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseProducer {
    private final KafkaTemplate<String, String> sender;
    private final Marshaller marshaller = new Marshaller();

    protected <T extends BaseEvent> void sendEvent(final CRUD operation, final T event) {
        try {
            final String topic = KafkaConfig.topics.get(operation);

            if (topic.isEmpty()) {
                log.warn("CRUD operation does not have a topic mapped. operation={}", operation);
                log.info("Skipping event. event={}", event);
                return;
            }

            final ProducerRecord<String, String> kafkaRecord = new ProducerRecord<>(
                    topic,
                    null,
                    null,
                    null, // TODO: Investigate the need to set a key on update events
                    marshaller.marshal(event),
                    Collections.singletonList(new RecordHeader(KafkaConfig.EVENT_TYPE_KEY, event.getEventType().getBytes()))
            );

            sender.send(kafkaRecord);
            log.info("Event produced on Kafka. topic={} event={}", topic, event);
        } catch (ClassCastException e) {
            throw new RuntimeException(String.format("Could not parse the event as a json. %s", e.getMessage()), e);
        }
    }
}
