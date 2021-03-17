/*
 * Copyright 2006-2017 the original author or authors.
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

package com.consol.citrus.validation;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.message.Message;
import com.consol.citrus.validation.json.JsonPathMessageValidationContext;
import com.consol.citrus.validation.xml.XmlNamespaceAware;
import com.consol.citrus.variable.VariableExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

/**
 * Generic extractor implementation delegating to JSONPath or XPath variable extractor based on given expression
 * type. Delegate extractor implementations are referenced through resource path lookup.
 *
 * @author Simon Hofmann
 * @since 2.7.3
 */
public class DelegatingPayloadVariableExtractor implements VariableExtractor {

    /** Map defines path expressions and target variable names */
    private Map<String, Object> pathExpressions;

    private Map<String, String> namespaces;

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(DelegatingPayloadVariableExtractor.class);

    public DelegatingPayloadVariableExtractor() {
        this(new Builder());
    }

    public DelegatingPayloadVariableExtractor(Builder builder) {
        this.pathExpressions = builder.expressions;
        this.namespaces = builder.namespaces;
    }


    @Override
    public void extractVariables(Message message, TestContext context) {
        if (CollectionUtils.isEmpty(pathExpressions)) {return;}

        if (LOG.isDebugEnabled()) {
            LOG.debug("Reading path elements.");
        }

        Map<String, Object> jsonPathExpressions = new LinkedHashMap<>();
        Map<String, Object> xpathExpressions = new LinkedHashMap<>();

        for (Map.Entry<String, Object> pathExpression : pathExpressions.entrySet()) {
            final String path = context.replaceDynamicContentInString(pathExpression.getKey());
            final Object variable = pathExpression.getValue();

            if (JsonPathMessageValidationContext.isJsonPathExpression(path)) {
                jsonPathExpressions.put(path, variable);
            } else {
                xpathExpressions.put(path, variable);
            }
        }

        if (!jsonPathExpressions.isEmpty()) {
            final VariableExtractor.Builder<?, ?> jsonPathExtractor = VariableExtractor.lookup("jsonPath")
                    .orElseThrow(() -> new CitrusRuntimeException("Missing proper Json Path extractor implementation for resource 'jsonPath' - " +
                            "consider adding proper json validation module to the project"));

            jsonPathExtractor
                    .expressions(jsonPathExpressions)
                    .build()
                    .extractVariables(message, context);
        }

        if (!xpathExpressions.isEmpty()) {
            final VariableExtractor.Builder<?, ?> xpathExtractor = VariableExtractor.lookup("xpath")
                    .orElseThrow(() -> new CitrusRuntimeException("Missing proper Xpath extractor implementation for resource 'xpath' - " +
                            "consider adding proper xml validation module to the project"));

            if (!this.namespaces.isEmpty() && xpathExtractor instanceof XmlNamespaceAware) {
                ((XmlNamespaceAware) xpathExtractor).setNamespaces(this.namespaces);
            }

            xpathExtractor
                    .expressions(xpathExpressions)
                    .build()
                    .extractVariables(message, context);
        }
    }

    /**
     * Fluent builder.
     */
    public static final class Builder implements VariableExtractor.Builder<DelegatingPayloadVariableExtractor, Builder> {

        private final Map<String, Object> expressions = new LinkedHashMap<>();
        private final Map<String, String> namespaces = new HashMap<>();

        /**
         * Static entry method for builder.
         * @return
         */
        public static Builder fromBody() {
            return new Builder();
        }

        public Builder namespaces(Map<String, String> namespaces) {
            this.namespaces.putAll(namespaces);
            return this;
        }

        public Builder namespace(final String prefix, final String namespace) {
            this.namespaces.put(prefix, namespace);
            return this;
        }

        @Override
        public Builder expressions(Map<String, Object> expressions) {
            this.expressions.putAll(expressions);
            return this;
        }

        @Override
        public Builder expression(final String path, final Object variableName) {
            this.expressions.put(path, variableName);
            return this;
        }

        @Override
        public DelegatingPayloadVariableExtractor build() {
            return new DelegatingPayloadVariableExtractor(this);
        }
    }

    /**
     * Sets the JSONPath / XPath expressions.
     * @param pathExpressions
     */
    public void setPathExpressions(Map<String, Object> pathExpressions) {
        this.pathExpressions = pathExpressions;
    }

    /**
     * Gets the JSONPath / XPath expressions.
     * @return
     */
    public Map<String, Object> getPathExpressions() {
        return pathExpressions;
    }

    /**
     * Gets the XPath namespaces
     * @return the namespaces
     */
    public Map<String, String> getNamespaces() {
        return namespaces;
    }

    /**
     * Sets the namespaces
     * @param namespaces the namespaces
     */
    public void setNamespaces(Map<String, String> namespaces) {
        this.namespaces = namespaces;
    }

}
