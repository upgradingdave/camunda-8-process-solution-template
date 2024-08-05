package org.example.camunda.process.solution.facade;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.AssignUserTaskResponse;
import org.example.camunda.process.solution.ProcessVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TasklistController {

  private static final Logger LOG = LoggerFactory.getLogger(TasklistController.class);
   private final ZeebeClient zeebe;

  public TasklistController(@Autowired ZeebeClient zeebe) {
    this.zeebe = zeebe;
  }

  @PostMapping("/claimTask/{jobKey}")
  public void claimTask(
      @PathVariable Long jobKey,
      @RequestBody ProcessVariables variables) {

    LOG.info("Claim Task `" + jobKey);
    AssignUserTaskResponse response =
        zeebe.newUserTaskAssignCommand(jobKey)
            .assignee("dave")
            .send().join();
  }

}
