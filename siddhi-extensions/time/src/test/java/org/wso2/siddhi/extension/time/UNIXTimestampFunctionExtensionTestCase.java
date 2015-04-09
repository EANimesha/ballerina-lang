/*
 *
 *  * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *  *
 *  * WSO2 Inc. licenses this file to you under the Apache License,
 *  * Version 2.0 (the "License"); you may not use this file except
 *  * in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.wso2.siddhi.extension.time;

import org.apache.log4j.Logger;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.wso2.siddhi.core.ExecutionPlanRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.query.output.callback.QueryCallback;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.util.EventPrinter;

public class UNIXTimestampFunctionExtensionTestCase {

    static final Logger log = Logger.getLogger(UNIXTimestampFunctionExtensionTestCase.class);
    private volatile int count;
    private volatile boolean eventArrived;

    @Before
    public void init() {
        count = 0;
        eventArrived = false;
    }

    @Test
    public void UNIXTimestampFunctionExtension() throws InterruptedException {

        log.info("UNIXTimestampFunctionExtensionTestCase");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inStreamDefinition = "@config(async = 'true')define stream inputStream (symbol string, price long, volume long);";
        String query = ("@info(name = 'query1') from inputStream select symbol , " +
                "str:unixTimestamp('2007-11-30 10:30:19','yyyy-MM-DD HH:MM:SS') as unixTimestampWithArguments, " +
                "str:unixTimestamp('2007-11-30 10:30:19') as unixTimestampWithoutSendingFormat, " +
                "str:unixTimestamp() as unixTimestampWithoutArguments" +" insert into outputStream;");
        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(inStreamDefinition + query);

        executionPlanRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                count = count + inEvents.length;
                if (count == 1) {
                    log.info("Event : " + count + ",unixTimestampWithArguments : " + inEvents[0].getData(1) + "," +
                            "unixTimestampWithoutSendingFormat :" +inEvents[0].getData(2));
                    System.out.println("Event : " + count + ",unixTimestampWithArguments : " + inEvents[0].getData(1)+ "," +
                            "unixTimestampWithoutSendingFormat :" +inEvents[0].getData(2));
                    eventArrived = true;
                }
                if (count == 2) {
                    log.info("Event : " + count + ",unixTimestampWithArguments : " + inEvents[0].getData(1) + "," +
                            "unixTimestampWithoutSendingFormat :" +inEvents[0].getData(2));
                    System.out.println("Event : " + count + ",unixTimestampWithArguments : " + inEvents[0].getData(1)+ "," +
                            "unixTimestampWithoutSendingFormat :" +inEvents[0].getData(2));
                    eventArrived = true;
                }
                if (count == 3) {
                    log.info("Event : " + count + ",unixTimestampWithArguments : " + inEvents[0].getData(1) + "," +
                            "unixTimestampWithoutSendingFormat :" +inEvents[0].getData(2));
                    System.out.println("Event : " + count + ",unixTimestampWithArguments : " + inEvents[0].getData(1)+ "," +
                            "unixTimestampWithoutSendingFormat :" +inEvents[0].getData(2));
                    eventArrived = true;
                }
            }
        });

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();
        inputHandler.send(new Object[]{"IBM", 700f, 100l});
        Thread.sleep(100);
        inputHandler.send(new Object[]{"WSO2", 60.5f, 200l});
        Thread.sleep(100);
        inputHandler.send(new Object[]{"XYZ", 60.5f, 200l});
        Thread.sleep(100);
        Assert.assertEquals(3, count);
        Assert.assertTrue(eventArrived);
        executionPlanRuntime.shutdown();
    }
}
