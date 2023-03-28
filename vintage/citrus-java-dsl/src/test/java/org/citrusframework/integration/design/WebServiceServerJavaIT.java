/*
 * Copyright 2006-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.citrusframework.integration.design;

import org.citrusframework.dsl.testng.TestNGCitrusTestDesigner;
import org.citrusframework.annotations.CitrusTest;
import org.testng.annotations.Test;

/**
 * @author Christoph Deppisch
 */
@Test
public class WebServiceServerJavaIT extends TestNGCitrusTestDesigner {

    @CitrusTest
    public void soapServer() {
        variable("correlationId", "citrus:randomNumber(10)");
        variable("messageId", "citrus:randomNumber(10)");
        variable("user", "Christoph");

        parallel().actions(
        send("webServiceClient")
            .payload("<ns0:HelloRequest xmlns:ns0=\"http://citrusframework.org/schemas/samples/sayHello.xsd\">" +
                          "<ns0:MessageId>${messageId}</ns0:MessageId>" +
                          "<ns0:CorrelationId>${correlationId}</ns0:CorrelationId>" +
                          "<ns0:User>${user}</ns0:User>" +
                          "<ns0:Text>Hello WebServer</ns0:Text>" +
                      "</ns0:HelloRequest>")
            .header("{http://citrusframework.org/test}Operation", "sayHello"),
            sequential().actions(
                receive("webServiceRequestReceiver")
                    .payload("<ns0:HelloRequest xmlns:ns0=\"http://citrusframework.org/schemas/samples/sayHello.xsd\">" +
                                  "<ns0:MessageId>${messageId}</ns0:MessageId>" +
                                  "<ns0:CorrelationId>${correlationId}</ns0:CorrelationId>" +
                                  "<ns0:User>${user}</ns0:User>" +
                                  "<ns0:Text>Hello WebServer</ns0:Text>" +
                              "</ns0:HelloRequest>")
                    .header("Operation", "sayHello")
                    .schemaValidation(false)
                    .extractFromHeader("citrus_jms_messageId", "internal_correlation_id"),
                send("webServiceResponseSender")
                    .payload("<ns0:HelloResponse xmlns:ns0=\"http://citrusframework.org/schemas/samples/sayHello.xsd\">" +
                                    "<ns0:MessageId>${messageId}</ns0:MessageId>" +
                                    "<ns0:CorrelationId>${correlationId}</ns0:CorrelationId>" +
                                    "<ns0:User>WebServer</ns0:User>" +
                                    "<ns0:Text>Hello ${user}</ns0:Text>" +
                                "</ns0:HelloResponse>")
                    .header("citrus_jms_correlationId", "${internal_correlation_id}")
            )
        );

        receive("webServiceClient")
            .payload("<ns0:HelloResponse xmlns:ns0=\"http://citrusframework.org/schemas/samples/sayHello.xsd\">" +
                            "<ns0:MessageId>${messageId}</ns0:MessageId>" +
                            "<ns0:CorrelationId>${correlationId}</ns0:CorrelationId>" +
                            "<ns0:User>WebServer</ns0:User>" +
                            "<ns0:Text>Hello ${user}</ns0:Text>" +
                        "</ns0:HelloResponse>")
            .schemaValidation(false);
    }
}
