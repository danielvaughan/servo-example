package com.danielvaughan.example.servo; /**
 * Copyright 2013 Netflix, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.netflix.servo.monitor.Pollers;

import java.io.File;

/**
 * Utility class dealing with different settings used to run the examples.
 */
public final class Config {

    private Config() {
    }

    /**
     * Port number for the http server to listen on.
     */
    public static int getPort() {
        return Integer.parseInt(System.getProperty("com.danielvaughan.example", "12345"));
    }

    /**
     * How frequently to poll metrics in seconds and report to observers.
     */
    public static long getPollInterval() {
        return Pollers.getPollingIntervals().get(0) / 1000L;
    }

    /**
     * Should we poll the standard jvm mbeans? Default is true.
     */
    public static boolean isJvmPollerEnabled() {
        return Boolean.valueOf(System.getProperty("servo.example.isJvmPollerEnabled", "true"));
    }

}
