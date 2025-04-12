package com.soebes.maven.extensions;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Karl Heinz Marbaise <a href="mailto:kama@soebes.de">kama@soebes.de</a>
 */
class DiscoveryTimer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoveryTimer.class);

    private final SystemTime time;

    public DiscoveryTimer()
    {
        this.time = new SystemTime();
    }

    public void discoveryStart()
    {
        time.start();
    }

    public void discoveryStop()
    {
        time.stop();
    }

    public void report()
    {
        String format = String.format("%8d", time.getElapsedTime());
        LOGGER.info( "Project discovery time: {} ms", format);
        LOGGER.info( "------------------------------------------------------------------------");
    }

}
