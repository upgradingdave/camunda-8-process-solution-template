package org.example.camunda.process.solution.facade;

import io.camunda.client.CamundaClient;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CamundaApiController {

  private static final Logger LOG = LoggerFactory.getLogger(CamundaApiController.class);
  private final CamundaClient camundaClient;

  public CamundaApiController(CamundaClient client) {
    this.camundaClient = client;
  }

  @GetMapping("/camunda/userTasks")
  public List<?> getUserTasksByAssignee(JwtAuthenticationToken authentication) {
    String assignee = (String) authentication.getToken().getClaims().get("preferred_username");
    LOG.info("Fetching user tasks assigned to: {}", assignee);
    return camundaClient
        .newUserTaskSearchRequest()
        .filter(f -> f.assignee(assignee))
        .send()
        .join()
        .items();
  }
}
