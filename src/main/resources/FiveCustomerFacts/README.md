## Five Customer Facts

A Camunda 8.9 process application demonstrating agentic AI with long-term memory. The process uses an AI agent to collect five facts about a customer
through conversation, stores them as vector embeddings in Elasticsearch, and retrieves them automatically on future runs — so the agent knows what
it already learned and doesn't ask again.

### What It Does

1. Starts with the customer's email address (used to namespace their memory in the vector store).
2. Queries Elasticsearch for any facts previously stored about this customer. First-time customers have no index yet; that is handled gracefully.
3. Runs an AI agent loop (Claude Sonnet via AWS Bedrock) that reads any prior facts and then converses with the customer — one question at a time via a Camunda user task form — until exactly 5 unique facts are collected.
4. Saves the agent's final response back to Elasticsearch as a vector embedding under an index named conversation-{customerEmail}.
5. Displays the 5 facts in a summary table for review.

On repeat runs with the same email, the agent finds 5 facts already stored and stops immediately without asking anything — demonstrating persistent long-term memory across separate process instances.

### Secrets

Configure the following secrets in your Camunda Self-Managed installation:

  | Secret | Description |
  |---|---|
  | `AWS_ACCESS_KEY` | AWS access key with Bedrock permissions |
  | `AWS_SECRET_KEY` | AWS secret key |
  | `AWS_REGION` | AWS region where Bedrock is enabled (e.g. `us-east-1`) |

### Infrastructure

  | Service | Configuration |
  |---|---|
  | AWS Bedrock | Model: `us.anthropic.claude-sonnet-4-6` (agent), `TitanEmbedTextV2` (embeddings) |
  | Elasticsearch | `http://camunda-elasticsearch:9200`, credentials `elastic` / `elastic` |

The Elasticsearch instance bundled with the Camunda Self-Managed installation is used directly. Vector indices are created automatically on first
write, named conversation-{customerEmail}.

### Element Templates

  The *.json files in this directory are element templates that must be uploaded to your Web Modeler organization before opening the BPMN:

  | File | Used By |
  |---|---|
  | `AI Agent Sub-process.json` | "Learn about Customer" ad-hoc sub-process |
  | `AI Agent Task.json` | Reference template (not used directly in this process) |
  | `Embeddings Vector DB Outbound Connector.json` | "Query Customer Facts" and "Save Customer Facts" tasks |

  To upload: In Web Modeler, go to Organization → Connector templates and upload each file.

### How to Run

1. Upload the element templates to Web Modeler.
2. Open Five Customer Facts.bpmn in Web Modeler and deploy it to your cluster.
3. Start a new process instance — the start form will prompt for an email address.
4. Complete the "Ask Customer" user tasks as they appear in Tasklist, entering one fact per prompt.
5. After 5 facts are collected, the "Summary" task displays them in a table.
6. Start a second instance with the same email to observe the agent recognising the stored facts and completing without asking any questions.
