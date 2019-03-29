/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.integration.cs;

import org.apache.nifi.components.ValidationResult;
import org.apache.nifi.components.validation.ValidationStatus;
import org.apache.nifi.controller.ControllerService;
import org.apache.nifi.controller.ProcessorNode;
import org.apache.nifi.controller.service.ControllerServiceNode;
import org.apache.nifi.integration.DirectInjectionExtensionManager;
import org.apache.nifi.integration.FrameworkIntegrationTest;
import org.apache.nifi.processor.Processor;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.testng.Assert.assertSame;

public class ControllerServiceReferenceIT extends FrameworkIntegrationTest {
    @Override
    protected void injectExtensionTypes(final DirectInjectionExtensionManager extensionManager) {
        extensionManager.injectExtensionType(ControllerService.class, CounterControllerService.class);
        extensionManager.injectExtensionType(ControllerService.class, LongValidatingControllerService.class);
        extensionManager.injectExtensionType(Processor.class, ControllerServiceReferencingProcessor.class);
    }


    @Test
    public void testCallingControllerService() throws ExecutionException, InterruptedException {
        final ProcessorNode counter = createProcessorNode(ControllerServiceReferencingProcessor.class.getName());

        final ControllerServiceNode serviceNode = createControllerServiceNode(CounterControllerService.class.getName());
        assertSame(ValidationStatus.VALID, serviceNode.performValidation());
        getFlowController().getControllerServiceProvider().enableControllerService(serviceNode).get();

        counter.setAutoTerminatedRelationships(Collections.singleton(REL_SUCCESS));
        counter.setProperties(Collections.singletonMap("Counter Service", serviceNode.getIdentifier()));

        triggerOnce(counter);

        assertEquals(1, ((Counter) serviceNode.getControllerServiceImplementation()).getValue());
    }

    @Test
    public void testLongValidatingControllerService() {
        final ControllerServiceNode serviceNode = createControllerServiceNode(LongValidatingControllerService.class.getName());
        serviceNode.setProperties(Collections.singletonMap(LongValidatingControllerService.DELAY.getName(), "250 millis"));
        final ValidationStatus validationStatus = serviceNode.performValidation();
        final Collection<ValidationResult> validationErrors = serviceNode.getValidationErrors();
        assertSame(validationStatus, ValidationStatus.VALID);
        assertEquals(0, validationErrors.size());
    }
}
