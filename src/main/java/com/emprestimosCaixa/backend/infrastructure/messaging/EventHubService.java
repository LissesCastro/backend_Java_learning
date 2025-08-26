package com.emprestimosCaixa.backend.infrastructure.messaging;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.emprestimosCaixa.backend.shared.dto.response.SimulacaoResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class EventHubService {

    private final EventHubProducerClient producer;
    private final ObjectMapper objectMapper;

    public EventHubService(@Value("${azure.eventhub.connection-string}") String connectionString,
                           @Value("${azure.eventhub.name}") String eventHubName) {
        this.producer = new EventHubClientBuilder()
                .connectionString(connectionString, eventHubName)
                .buildProducerClient();
        this.objectMapper = new ObjectMapper();
    }

    public void sendEvent(SimulacaoResponse simulacaoResponse) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(simulacaoResponse);
            EventData eventData = new EventData(jsonMessage);
            producer.send(Collections.singletonList(eventData));
            System.out.println("Evento de simulação enviado para o Event Hub com sucesso.");
        } catch (JsonProcessingException e) {
            System.err.println("Erro ao converter a simulação para JSON: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro ao enviar evento para o Event Hub: " + e.getMessage());
        }
    }
}