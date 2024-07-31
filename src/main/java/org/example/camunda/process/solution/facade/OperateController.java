package org.example.camunda.process.solution.facade;

import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.exception.OperateException;
import io.camunda.operate.model.Variable;
import io.camunda.operate.search.SearchQuery;
import io.camunda.operate.search.VariableFilter;
import io.camunda.zeebe.client.ZeebeClient;
import org.example.camunda.process.solution.ProcessConstants;
import org.example.camunda.process.solution.ProcessVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/operate")
public class OperateController {

  private static final Logger LOG = LoggerFactory.getLogger(OperateController.class);
  private final CamundaOperateClient operateClient;

  public OperateController(@Autowired CamundaOperateClient operateClient)
  {
    this.operateClient = operateClient;
  }

  @GetMapping("/searchVariables1")
  public List<Variable> searchVariables1() throws OperateException {
    SearchQuery searchQuery = new SearchQuery();
    VariableFilter filter= new VariableFilter();
    searchQuery.setFilter(filter);
    List<Variable> result = operateClient.searchVariables(searchQuery);
    return result;
  }

  @GetMapping("/searchVariables2")
  public List<Variable> searchVariables2() throws OperateException {
    SearchQuery searchQuery = new SearchQuery();
    VariableFilter filter= new VariableFilter();
    filter.setName("businessKey");
    filter.setValue("123");
    searchQuery.setFilter(filter);
    List<Variable> result = operateClient.searchVariables(searchQuery);
    return result;
  }

  @GetMapping("/searchVariables3")
  public List<Variable> searchVariables3() throws OperateException {
    SearchQuery searchQuery = new SearchQuery();
    VariableFilter filter= new VariableFilter();
    filter.setName("businessKey");
    filter.setValue("\"123\"");
    searchQuery.setFilter(filter);
    List<Variable> result = operateClient.searchVariables(searchQuery);
    return result;
  }

}
