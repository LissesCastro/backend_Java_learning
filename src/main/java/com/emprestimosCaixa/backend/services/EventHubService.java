package com.emprestimosCaixa.backend.services;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.emprestimosCaixa.backend.dto.response.SimulacaoResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class EventHubService {

    private final EventHubProducerClient producer;
    private final ObjectMapper objectMapper;

    // O construtor irá ler as propriedades do application.properties
    public EventHubService(@Value("${azure.eventhub.connection-string}") String connectionString,
                           @Value("${azure.eventhub.name}") String eventHubName) {

        // Cria um cliente produtor que será reutilizado para enviar eventos
        this.producer = new EventHubClientBuilder()
                .connectionString(connectionString, eventHubName)
                .buildProducerClient();

        this.objectMapper = new ObjectMapper();
    }

    public void sendEvent(SimulacaoResponse simulacaoResponse) {
        try {
            // Converte o objeto da simulação para uma string JSON
            String jsonMessage = objectMapper.writeValueAsString(simulacaoResponse);

            // Cria o evento a ser enviado
            EventData eventData = new EventData(jsonMessage);

            // Envia o evento para o Event Hub
            producer.send(Collections.singletonList(eventData));

            System.out.println("Evento de simulação enviado para o Event Hub com sucesso.");

        } catch (JsonProcessingException e) {
            System.err.println("Erro ao converter a simulação para JSON antes de enviar para o Event Hub.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erro ao enviar evento para o Event Hub: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
