package org.example.camunda.process.solution.worker;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.CustomHeaders;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.VariablesAsType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserTaskWorker {

  @Autowired
  private ZeebeClient zeebe;

  private static final Logger LOG = LoggerFactory.getLogger(UserTaskWorker.class);

  /*@JobWorker(
      type = "io.camunda.zeebe:userTask",
      autoComplete = false,
      timeout = 2592000000L) // set timeout to 30 days
  public void listenUserTask(
      final JobClient client,
      final ActivatedJob job,
      @VariablesAsType Map<String, Object> variables,
      @CustomHeaders Map<String, String> headers) {

    try {

      LOG.info("User Task Worker triggered with variables: " + variables);

      //Do stuff


    } catch (Exception e) {
      LOG.error("Exception occurred in UserTaskWorker", e);
      client
          .newFailCommand(job.getKey())
          .retries(0)
          .errorMessage("Exception occurred in UserTaskWorker - " + e.getMessage())
          .send();
    }
  }*/

}
