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

import org.testng.annotations.Test;

public class FirstTest
{
 
    public class X {
        public String get (String project, String phase, String mojo, String goal) {
            return null;
        }
    }
    /*
     * Project : phase : mojo : goal
     */
    @Test
    public void firstTest()
    {
        // definition however..
        X result = new X();
        String value = result.get("p", "clean", "mojo", "exec");
        
    }
    
    @Test
    public void secondTest() {
        long l = 10456;
        String s = String.format( "%6d", l);
        String x = String.format( "%-12s", "X" );
        System.out.println( "S=" + s + " X: '" + x + "'");
    }
}
