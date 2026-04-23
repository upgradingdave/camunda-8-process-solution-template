package org.example.camunda.process.solution.facade;

import io.camunda.client.CamundaClient;
import io.camunda.client.CredentialsProvider;
import java.net.URI;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userApi/")
public class CamundaUserApiController {

  private static final Logger LOG = LoggerFactory.getLogger(CamundaUserApiController.class);

  @Value("${camunda.client.rest-address}")
  private String restAddress;

  @Value("${camunda.client.grpc-address}")
  private String grpcAddress;

  @GetMapping("/camunda/userTasks")
  public List<?> getUserTasksByAssignee(JwtAuthenticationToken authentication) throws Exception {
    String token = authentication.getToken().getTokenValue();
    String username = (String) authentication.getToken().getClaims().get("preferred_username");
    LOG.info("Fetching user tasks for {} using user token passthrough", username);

    try (CamundaClient userClient = buildUserClient(token)) {
      return userClient
          .newUserTaskSearchRequest()
          .filter(f -> f.assignee(username))
          .send()
          .join()
          .items();
    }
  }

  private CamundaClient buildUserClient(String token) {
    return CamundaClient.newClientBuilder()
        .restAddress(URI.create(restAddress))
        .grpcAddress(URI.create(grpcAddress))
        .preferRestOverGrpc(true)
        .credentialsProvider(
            new CredentialsProvider() {
              @Override
              public void applyCredentials(CredentialsProvider.CredentialsApplier applier) {
                applier.put("Authorization", "Bearer " + token);
              }

              @Override
              public boolean shouldRetryRequest(CredentialsProvider.StatusCode status) {
                return false;
              }
            })
        .build();
  }
}
