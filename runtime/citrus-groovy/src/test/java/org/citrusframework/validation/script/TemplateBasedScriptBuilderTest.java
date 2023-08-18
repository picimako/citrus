/*
 * Copyright 2006-2010 the original author or authors.
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

package org.citrusframework.validation.script;

import org.citrusframework.util.TestUtils;
import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Christoph Deppisch
 */
public class TemplateBasedScriptBuilderTest {

    @Test
    public void testTemplateScriptResource() {
        Assert.assertEquals(
            TestUtils.normalizeLineEndings(
                TemplateBasedScriptBuilder.fromTemplateResource(
                        new ClassPathResource(
                            "org/citrusframework/validation/script/script-template.groovy")
                    ).withCode("BODY")
                    .build()
            ),
            "+++HEAD+++\n" +
                "\n" +
                "BODY\n" +
                "\n" +
                "+++TAIL+++\n"
        );
    }
}
