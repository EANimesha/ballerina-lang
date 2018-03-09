/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.ballerinalang.net.http.compiler;

import org.ballerinalang.compiler.plugins.AbstractCompilerPlugin;
import org.ballerinalang.compiler.plugins.SupportEndpointTypes;
import org.ballerinalang.model.tree.AnnotationAttachmentNode;
import org.ballerinalang.model.tree.ServiceNode;
import org.ballerinalang.util.diagnostic.DiagnosticLog;
import org.wso2.ballerinalang.compiler.tree.BLangAnnotationAttachment;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangArrayLiteral;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangExpression;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangRecordLiteral;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangSimpleVarRef;

import java.util.List;

/**
 * Compiler plugin for validating HTTP service.
 *
 * @since 0.965.0
 */
@SupportEndpointTypes(
        value = {@SupportEndpointTypes.EndpointType(packageName = "ballerina.net.http", name = "ServiceEndpoint")}
)
public class HTTPServiceCompilerPlugin extends AbstractCompilerPlugin {

    @Override
    public void init(DiagnosticLog diagnosticLog) {
    }

    @Override
    public void process(ServiceNode serviceNode, List<AnnotationAttachmentNode> annotations) {
        for (AnnotationAttachmentNode annotation : annotations) {
            if (!"ballerina.net.http".equals(
                    ((BLangAnnotationAttachment) annotation).annotationSymbol.pkgID.name.value)) {
                return;
            }
            if (annotation.getAnnotationName().getValue().equals("serviceConfig")) {
                handleServiceConfigAnnotation(serviceNode, (BLangAnnotationAttachment) annotation);
            }
        }
        // Validation resources.
    }

    private void handleServiceConfigAnnotation(ServiceNode serviceNode, BLangAnnotationAttachment annotation) {
        final BLangRecordLiteral expression = (BLangRecordLiteral) annotation.expr;
        for (BLangRecordLiteral.BLangRecordKeyValue valueNode : expression.getKeyValuePairs()) {
            final String key = ((BLangSimpleVarRef) valueNode.getKey()).variableName.value;
            if (!key.equals("endpoints")) {
                continue;
            }
            final List<BLangExpression> endpoints = ((BLangArrayLiteral) valueNode.getValue()).exprs;
            for (BLangExpression endpoint : endpoints) {
                if (endpoint instanceof BLangSimpleVarRef) {
                    serviceNode.bindToEndpoint((BLangSimpleVarRef) endpoint);
                }
            }
        }
    }
}
