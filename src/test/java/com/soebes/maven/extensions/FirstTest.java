package com.soebes.maven.extensions;

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
