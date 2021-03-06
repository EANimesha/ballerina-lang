/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.ballerinalang.langserver.completions.providers.contextproviders;

import org.ballerinalang.annotation.JavaSPIService;
import org.ballerinalang.langserver.common.utils.CommonUtil;
import org.ballerinalang.langserver.commons.LSContext;
import org.ballerinalang.langserver.commons.completion.LSCompletionItem;
import org.ballerinalang.langserver.completions.providers.AbstractCompletionProvider;
import org.wso2.ballerinalang.compiler.parser.antlr4.BallerinaParser;

import java.util.List;

/**
 * Parser rule based worker send sync expression context resolver.
 */
@JavaSPIService("org.ballerinalang.langserver.commons.completion.spi.LSCompletionProvider")
public class WorkerSendSyncExpressionContextProvider extends AbstractCompletionProvider {

    public WorkerSendSyncExpressionContextProvider() {
        this.attachmentPoints.add(BallerinaParser.WorkerSendSyncExpressionContext.class);
    }
    @Override
    public List<LSCompletionItem> getCompletions(LSContext context) {
        return this.getCompletionItemList(CommonUtil.getWorkerSymbols(context), context);
    }
}
