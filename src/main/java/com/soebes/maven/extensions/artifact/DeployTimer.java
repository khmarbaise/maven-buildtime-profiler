package com.soebes.maven.extensions.artifact;

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

import com.soebes.maven.extensions.TimePlusSize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.util.Map.Entry;

/**
 * @author Karl Heinz Marbaise <a href="mailto:kama@soebes.de">kama@soebes.de</a>
 */
public class DeployTimer
    extends AbstractArtifactTimer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DeployTimer.class);

    public DeployTimer()
    {
        super();
    }

    public void report()
    {
        if ( getTimerEvents().isEmpty() )
        {
            return;
        }
        LOGGER.info( "Deployment summary:" );
        long totalInstallationTime = 0;
        long totalInstallationSize = 0;
        for ( Entry<String, TimePlusSize> item : this.getTimerEvents().entrySet() )
        {
            totalInstallationTime += item.getValue().getElapsedTime();
            totalInstallationSize += item.getValue().getSize();
            String formattedTime = String.format("%8d", item.getValue().getElapsedTime());
            LOGGER.info( "{} ms : {}", formattedTime, item.getKey() );
        }
        double mibPerSeconds = calculateMegabytesPerSeconds( totalInstallationTime, totalInstallationSize );
        LOGGER.info( "{} ms  {} bytes. {} MiB / s", NumberFormat.getIntegerInstance().format( totalInstallationTime ),
                     NumberFormat.getIntegerInstance().format( totalInstallationSize ),
                     NumberFormat.getNumberInstance().format( mibPerSeconds ) );
        LOGGER.info( "------------------------------------------------------------------------" );
    }

}
