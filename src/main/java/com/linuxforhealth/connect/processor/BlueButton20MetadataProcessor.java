/*
 * (C) Copyright IBM Corp. 2020
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.linuxforhealth.connect.processor;

import com.linuxforhealth.connect.support.CamelContextSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.time.Instant;
import java.util.UUID;

/**
 * Set the headers used by downstream processors and components
 */
public class BlueButton20MetadataProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {
        CamelContextSupport contextSupport = new CamelContextSupport(exchange.getContext());

        String blueButtonUri = contextSupport.getProperty("lfh.connect.bluebutton_20.rest.uri");
        String resourceType = exchange.getIn().getHeader("resource", String.class).toUpperCase();

        String kafkaDataStoreUri = contextSupport
                .getProperty("lfh.connect.dataStore.uri")
                .replaceAll("<topicName>", "FHIR_R4_" + resourceType);

        // Form the incoming route url for the message property routeUrl
        String routeUri = blueButtonUri+"/"+resourceType;
        String queryStr = exchange.getIn().getHeader("CamelHttpQuery", String.class);
        if (queryStr != null && queryStr != "") routeUri += "?"+queryStr;

        exchange.setProperty("timestamp", Instant.now().getEpochSecond());
        exchange.setProperty("routeUri", routeUri);
        exchange.setProperty("dataStoreUri", kafkaDataStoreUri);
        exchange.setProperty("dataFormat", "fhir-r4");
        exchange.setProperty("uuid", UUID.randomUUID());
        exchange.setProperty("resourceType", resourceType);
    }
}
