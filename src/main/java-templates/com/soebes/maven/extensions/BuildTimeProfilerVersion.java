package com.soebes.maven.extensions;

public final class BuildTimeProfilerVersion {

    private static final String VERSION = "${project.version}";
    private static final String GROUPID = "${project.groupId}";
    private static final String REVISION = "${buildNumber}";

    
    private BuildTimeProfilerVersion () {
        // no one should create an instance of this class.
    }

    public static String getVersion() {
        return VERSION;
    }

    public static String getGroupId() {
        return GROUPID;
    }

    public static String getRevision() {
        return REVISION;
    }

}
