package com.soebes.maven.extensions;

import org.apache.maven.execution.MavenExecutionRequest;
import org.json.JSONArray;
import org.json.JSONObject;

public class Execution
{

  private MavenExecutionRequest execution;

  private String command;

  public Execution()
  {

  }

  public Execution(MavenExecutionRequest executionRequest)
  {
    setExecutionRequest(executionRequest);
  }

  public void setExecutionRequest(MavenExecutionRequest executionRequest)
  {
    this.execution = executionRequest;
    // initCommand();
  }

  private void initCommand() {
    StringBuilder cmd = new StringBuilder("mvn");

    if (this.execution.isUpdateSnapshots())
    {
      cmd.append(" ").append("-U");
    }

    if (!this.execution.isRecursive())
    {
      cmd.append(" ").append("-N");
    }

    this.execution.getActiveProfiles()
        .forEach(profile -> cmd.append(" -P").append(profile));

    this.execution.getUserProperties()
        .keySet()
        .forEach(prop -> cmd.append(" -D").append(prop.toString()));

    this.execution.getGoals().forEach(goal -> cmd.append(" ").append(goal));

    this.command = cmd.toString();
  }

  public JSONObject toJSON()
  {
    JSONObject execution = new JSONObject();

    JSONArray goals = new JSONArray();
    goals.put(this.execution.getGoals());

    JSONObject userProperties = new JSONObject();
    this.execution.getUserProperties()
        .keySet()
        .forEach(key -> userProperties.put((String) key, this.execution.getUserProperties().get(key)));

    JSONArray selectedProjects = new JSONArray();
    selectedProjects.put(this.execution.getSelectedProjects());

    JSONArray profiles = new JSONArray();
    this.execution.getActiveProfiles().forEach(profiles::put);

    execution.put("goals", goals);
    execution.put("user-properties", userProperties);
    execution.put("selected-projects", selectedProjects);
    execution.put("active-profiles", profiles);
    // execution.put("command", this.command);

    return execution;
  }
}
