# Architecture

Five layers. Each is a package under `src/main/java/cowork/`. The ordering is deliberate: every layer only works if the one below it already knows how the organization operates.

```
+-------------------------------------------------------------+
|  PULL ENGINE           cowork/objectives      Phase 2+      |  <- the product
|  objective -> path -> capabilities -> build                 |
+-------------------------------------------------------------+
|  CONTEXT STORE         cowork/context         Phase 1       |  <- the moat
|  per org, per function, accumulating; three knowledge       |
|  classes; relational, not vector                            |
+-------------------------------------------------------------+
|  INGESTION             cowork/ingestion       Phase 1-2     |  <- the doors
|  calls . email . calendar . forms . buttons                 |
|  every door has a classifier behind it (SLM)                |
+-------------------------------------------------------------+
|  AGENT LAYER           cowork/agents          Phase 1-3     |
|  check-in . coordination . guide . briefing                 |
|  SLM routes, frontier reasons                               |
+-------------------------------------------------------------+
|  MODEL LAYER           cowork/models          bought        |
|  local SLM tier + OpenRouter for escalation                 |
+-------------------------------------------------------------+
```

The button board (`cowork/board`) is a surface across the agent layer and the store, not a layer of its own. It displays what agents negotiated and humans approved.

---

## Layer by layer

### Model layer — `cowork/models` (bought, not built)

Two tiers, one interface (`ModelRouter` in the port spec).

| Tier | Handles | Where |
|---|---|---|
| Local SLM (one 3-4B model) | Classification, extraction, routing, tagging, single-meeting summaries | On device |
| Frontier via OpenRouter | Negotiation, synthesis, briefings, pull-engine reasoning, explaining reasoning to a human | API |

Rules: start small, escalate only when you can name the reason. Cascade on low confidence. Instrument the escalation rate from day one; that number says whether the split is set correctly. Never use an SLM for an agent loop.

**Phase 1 uses frontier for everything.** The only model-layer work in Phase 1 is logging every call with its input type and token count. The log decides what moves to the SLM in Phase 1.5. The existing `cowork/llm/ApiRequestLog` is the starting point for that log.

Not built here: a provider router, per-provider clients, fallback logic. OpenRouter owns all of that, including per-request data policies that restrict which providers may receive FERPA-adjacent prompts.

### Agent layer — `cowork/agents`

Four agents, added across three phases.

| Agent | Phase | Job |
|---|---|---|
| Check-in | 1 | Calls the officer on a schedule (daily for Grant in Phase 1, weekly for officers later), asks a role-specific agenda, structures and files what it hears. The officer writes nothing. |
| Coordination | 2 | Extracts task intent from a conversation, contacts the other officer's agent, negotiates scope/owner/deadline from each user's real constraints, produces a proposal both humans must approve. |
| Guide | 2 | Onboards a new officer: which connectors to enable, what context is worth capturing, how to use the board. The retention mechanism; it survives turnover because it is not a person. |
| Briefing | 3 | Pre-meeting synthesis across agents; officer transition briefing from a year of context. |

The check-in agent reuses two things that already exist outside this repo: the OpenClaw connector for the call loop, and the Daily Brief scheduled-trigger pattern (Apps Script) for the cadence. Point a session at those and say "extend this."

### Ingestion — `cowork/ingestion` (the doors)

Every item that enters the store passes a classifier that assigns org, function, knowledge class, decay profile, source, time, and authority, and lands **marked unconfirmed**. Humans confirm, correct, or promote only when they notice something wrong. Corrections are logged separately from confirmations and are the highest-value training signal in the system.

Doors, in build order: buttons (exists), phone calls (Phase 1), calendar and forms (Phase 1-2), email (Phase 2, dedicated org address only). See `capture-mechanisms.md` and `privacy.md`.

### Context store — `cowork/context` (the moat)

Per org, per function, accumulating. Relational (Postgres). Three knowledge classes with different decay behavior; see `relevance-model.md`. The schema is `[OPEN]`; see `context-schema.md`.

What exists today: `OrganizationContext` (ten narrative fields as `ContextEntry<String>` with freshness, source, confidence, status, persisted to JSON), `ReconciliationService` (auto-applies safe changes, queues the rest for approval), `ContextChangeLog` (audit trail). These are the ancestors of the store, the review queue, and the correction log respectively.

### Pull engine — `cowork/objectives` (Phase 2+, empty)

The officer declares an objective. The system decomposes it into a path, identifies which capabilities the path needs, proposes which agents and automations to stand up next, helps build them from templates and org context, and re-proposes as reality changes.

Same principle as the task board, one level up:

| Level | Agent does | Human does |
|---|---|---|
| Task | Proposes a task from conversation | Approves or rejects |
| Capability | Proposes an agent or automation to build | Approves or rejects |
| Objective | Nothing. Reports progress. | **Sets it** |

Deliberately an empty package. It only works over accumulated context; generic proposals kill trust on first contact. The cheapest test, runnable this semester with no code in this package: declare one IDSSO objective and have the check-in agent report weekly against it. If the report tells Grant nothing new, the store is not deep enough yet.

---

## What talks to what

```
Officer <--call--> Check-in agent --items--> Classifier (SLM) --unconfirmed--> Context store
                                                                                    |
Officer <--proposal/approve--> Board <--propose()/commit()--> AgenticTask <--reads--+
                                                                   |
                                                            ModelRouter
                                                           /          \
                                                     local SLM     OpenRouter
```

Every task both consumes existing context and produces new context (its proposal, the chosen option, the outcome). That loop is what compounds, and it is why the memory layer and the task board are one system rather than two features.

---

## What this architecture refuses

- **Vector search** before retrieval demands it. Relational first.
- **A per-org fine-tuned model.** Tune on the category, retrieve per org.
- **A provider router.** OpenRouter.
- **A registry or capability catalog.** Owned by AWS, Google, Kong, ARD.
- **Rebuilding Asana.** The calendar-kanban generates the plan from institutional knowledge; a project tool asks you to enter it. If a feature only makes sense for a team that already knows its plan, it does not belong here.
- **Auto-submission of anything.** Prepare, never send.
