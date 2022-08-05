/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.xml.container;

import com.consol.citrus.TestCase;
import com.consol.citrus.TestCaseMetaInfo;
import com.consol.citrus.actions.EchoAction;
import com.consol.citrus.actions.FailAction;
import com.consol.citrus.container.Conditional;
import com.consol.citrus.xml.XmlTestLoader;
import com.consol.citrus.xml.actions.AbstractXmlActionTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Christoph Deppisch
 */
public class ConditionalTest extends AbstractXmlActionTest {

    @Test
    public void shouldLoadConditional() {
        XmlTestLoader testLoader = createTestLoader("classpath:com/consol/citrus/xml/container/conditional-test.xml");

        testLoader.load();
        TestCase result = testLoader.getTestCase();
        Assert.assertEquals(result.getName(), "ConditionalTest");
        Assert.assertEquals(result.getMetaInfo().getAuthor(), "Christoph");
        Assert.assertEquals(result.getMetaInfo().getStatus(), TestCaseMetaInfo.Status.FINAL);
        Assert.assertEquals(result.getActionCount(), 2L);
        Assert.assertEquals(result.getTestAction(0).getClass(), Conditional.class);
        Assert.assertEquals(((Conditional) result.getTestAction(0)).getCondition(), "${shouldRun}");
        Assert.assertEquals(((Conditional) result.getTestAction(0)).getActionCount(), 1L);
        Assert.assertEquals(((Conditional) result.getTestAction(0)).getTestAction(0).getClass(), EchoAction.class);

        Assert.assertEquals(result.getTestAction(1).getClass(), Conditional.class);
        Assert.assertEquals(((Conditional) result.getTestAction(1)).getCondition(), "${shouldNotRun}");
        Assert.assertEquals(((Conditional) result.getTestAction(1)).getActionCount(), 1L);
        Assert.assertEquals(((Conditional) result.getTestAction(1)).getTestAction(0).getClass(), FailAction.class);
    }

}
