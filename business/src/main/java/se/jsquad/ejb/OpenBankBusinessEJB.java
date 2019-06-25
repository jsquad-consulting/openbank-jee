/*
 * Copyright 2019 JSquad AB
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

package se.jsquad.ejb;

import se.jsquad.batch.SlowMockBatch;
import se.jsquad.batch.status.BatchStatus;
import se.jsquad.interceptor.LoggerInterceptor;
import se.jsquad.qualifier.Log;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.concurrent.Future;
import java.util.logging.Logger;

@Stateless
@LoggerInterceptor
public class OpenBankBusinessEJB {
    private  static final String HELLO_WORLD = "Hello world!";

    @Inject @Log
    private Logger logger;

    @Inject
    private SlowMockBatch slowMockBatch;

    public String getHelloWorld() {
        return HELLO_WORLD;
    }

    @Asynchronous
    public Future<BatchStatus> startSlowBatch() throws InterruptedException {
        return new AsyncResult<>(slowMockBatch.startBatch());
    }
}
