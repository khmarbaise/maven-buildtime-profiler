package com.soebes.maven.extensions.metadata;

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

import java.text.NumberFormat;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soebes.maven.extensions.TimePlusSize;

/**
 * @author Karl Heinz Marbaise <khmarbaise@apache.org>
 *
 */
public class MetadataInstallTimer
    extends AbstractMetadataTimer
{
    private final Logger LOGGER = LoggerFactory.getLogger( getClass() );

    public MetadataInstallTimer()
    {
        super();
    }

    public void report()
    {
        if ( getTimerEvents().isEmpty() )
        {
            return;
        }
        long totalInstallationTime = 0;
        long totalInstallationSize = 0;
        for ( Entry<String, TimePlusSize> item : this.getTimerEvents().entrySet() )
        {
            totalInstallationTime += item.getValue().getElapsedTime();
            // Hint: The getSize() will not return something different than zero
            // I don't know why we don't get the information about the downloaded size here?
            totalInstallationSize += item.getValue().getSize();
            // LOGGER.info( "{} ms : {}", String.format( "%8d", item.getValue().getElapsedTime() ), item.getKey() );
        }
        LOGGER.info( "Metadata Install summary: {} ms", NumberFormat.getIntegerInstance().format( totalInstallationTime ) );
        // LOGGER.info( "{} ms {} bytes.", NumberFormat.getIntegerInstance().format( totalInstallationTime ),
        // NumberFormat.getIntegerInstance().format( totalInstallationSize ) );
        LOGGER.info( "------------------------------------------------------------------------" );
    }

}
