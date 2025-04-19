package com.soebes.maven.extensions;

import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProjectTimerTest {

  @Test
  void shouldMeasureTheTime() {
    ProjectTimer t = new ProjectTimer();

    ExecutionEvent event = createEvent("Anton");

    t.projectStart(event);

    await()
        .pollInterval(Duration.ofMillis(10))
        .atLeast(Duration.ofMillis(10)).until(() -> true);

    t.projectStop(event);

    assertThat(t.getTimeForProject(event.getProject())).isGreaterThanOrEqualTo(10L);
  }

  @Test
  void projectStopShouldFailWithIllegalArgumentExceptionBasedOnUnknownProject() {
    ProjectTimer t = new ProjectTimer();

    ExecutionEvent event = createEvent("Anton");

    t.projectStart(event);

    ExecutionEvent unknownEvent = createEvent("Egon");

    assertThatIllegalArgumentException().isThrownBy(() -> t.projectStop(unknownEvent)).withMessage("Unknown projectId (Egon)");
  }

  @Test
  void getTimeForProjectShouldFailWithIllegalArgumentExceptionBasedOnUnknownProject() {
    ProjectTimer t = new ProjectTimer();

    ExecutionEvent event = createEvent("Anton");

    assertThatIllegalArgumentException().isThrownBy(() -> t.getTimeForProject(event.getProject())).withMessage("Unknown projectId (Anton)");
  }

  private ExecutionEvent createEvent(String id) {
    MavenProject project = mock(MavenProject.class);
    when(project.getId()).thenReturn(id);

    ExecutionEvent event = mock(ExecutionEvent.class);
    when(event.getProject()).thenReturn(project);
    return event;
  }
}
