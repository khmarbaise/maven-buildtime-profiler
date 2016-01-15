package com.soebes.maven.extensions;

public final class BuildTimeProfilerVersion {

    private static final String VERSION = "${project.version}";
    private static final String GROUPID = "${project.groupId}";
    private static final String SVN = "${project.scm.developerConnection}";
    private static final String SVN_BRANCH = "${scmBranch}";
    private static final String REVISION = "${buildNumber}";

    public static String getVersion() {
        return VERSION;
    }

    public static String getGroupId() {
        return GROUPID;
    }

    public static String getSVN() {
        return SVN;
    }

    public static String getRevision() {
        return REVISION;
    }

    public static String getSVNBranch() {
        return SVN_BRANCH;
    }
}
