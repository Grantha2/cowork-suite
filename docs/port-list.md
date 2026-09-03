# Port List: what carries forward from the existing Swing app

Audit of this repository as it stood on September 2, 2026 (`main` at `1110bea`), done before deciding whether to overhaul in place or start a fresh repo. This is a scaffolding-session read, not the full debt audit; `DEBT-INVENTORY.md` should confirm or correct it.

## Verdict `[REC]`

**Overhaul in place.** The plumbing worth keeping is already UI-agnostic (the task context no longer holds a Swing panel), already has tests, and the git history is worth preserving. A fresh repo would copy these same files forward and lose the history. Reverse this cheaply by creating a new repo and copying only the rows marked **keep** below.

## Inventory

### `cowork/tasks` — the button spawner. **Keep, then change the contract.**

| File | Verdict | Notes |
|---|---|---|
| `AgenticTask` | **keep, change** | Interface as in the port spec: `getCategory()` string -> `Function` enum; `isAvailable()` bool -> `Availability(available, reason)`; `void execute(ctx)` -> `TaskProposal propose(ctx)` + `TaskResult commit(ctx, approved)`. `execute(ctx, targetFields)` overload survives as `propose(ctx, targetFields)`. |
| `AgenticTaskRegistry` | **keep as-is** | `register`, `getById`, `getByCategory` grouping. This is the board. Only the key type of `getByCategory` changes with the enum. |
| `AgenticTaskContext` | **keep, extend** | Already dropped the `AgenticRoutinesPanel` reference; `TaskOutput` replaced it. The port spec's stale version still shows the panel; this repo is ahead of it. Add `ContextStore`, `ModelRouter`, `ActorRef` per the spec. |
| `TaskOutput` | **keep** | The UI-agnostic output channel. In a web port it becomes the proposal-pending state. |
| `TaskDialogs`, `ContextUpdateDialog` | **drop at web port** | Swing input dialogs. Their question sets move into task-declared input schemas. |
| `DailyContextUpdateFunction` | **keep, extend** | The ancestor of the check-in agent's "structure and file" step. Has a test. |

**Registered tasks today: nine.** `StartYourDayTask`, `OutboundMessagesTask`, `ContextRefreshTask`, `MeetingPrepTask`, `InitiativeReviewTask`, `WeeklyReportTask`, `StakeholderBriefingTask`, `RoomReservationWorkflow`, plus one `UserWorkflowTask` per enabled user-defined workflow. Each must answer "what is the proposal here, and who approves it" during port step 5. Some will not survive that question; that is a good outcome. Only `RoomReservationWorkflow` is in Phase 1 scope.

### `cowork/context` — the memory layer's ancestor. **Keep the concepts, replace the storage.**

| File | Verdict | Notes |
|---|---|---|
| `OrganizationContext` | **keep concepts, replace shape** | Ten fixed fields, single org, JSON file. Becomes per-org, per-function Postgres entities. The `FieldSpec` idea (name, label, TTL as one source of truth) survives as the decay profile. Has tests. |
| `ContextEntry`, `ContextStatus`, `Freshness` | **keep** | value + last-updated + source + confidence + status is exactly the per-entry metadata the schema needs. Add knowledge class and function. |
| `ReconciliationService`, `ProposedChange`, `MergeDecision` | **keep** | `SAFE_AUTO` auto-apply + approval queue is the review-queue mechanic and the ancestor of `ApprovalLevel`. Has tests. Cap the queue at ten `[REC]`. |
| `ContextChangeLog` | **keep, extend** | The audit trail. Split into confirmations vs. corrections; corrections are the highest-value data. |
| `ContextSource`, `LocalContextSource` | **keep interface** | `FUTURE.md` already planned a cloud implementation behind it; Postgres is that implementation. |
| `ContextController`, `TaskContext` | **keep** | `TaskContext.buildTaskBlock` is the prompt-assembly step retrieval plugs into. |

### `cowork/workflows` — room reservation. **Keep; this is Phase 1 item 2.**

| File | Verdict | Notes |
|---|---|---|
| `RoomReservationWorkflow` | **keep, restructure** | Already never submits. Split its single Claude turn into `propose()` (lookup, interpret, validate, rank) and `commit()` (fill PDF, draft email, write outcome to context). Swing dialog moves out. |
| `RoomAvailabilityTool` | **keep, add cache** | Fixture and live modes exist. Add the scheduled scrape-and-cache `[REC]`; availability becomes "unavailable: inventory stale since <date>" when the cache ages out. Has a test. |
| `PdfFillTool`, `EmailDraftTool` | **keep as-is** | Both prepare, never send. Tests exist. |
| `ComputerUseToolProxy` | **keep, narrow** | Reserved for authenticated form steps only, not for the read step. |

### `cowork/llm` — the model client. **Keep the interfaces, replace the provider.**

| File | Verdict | Notes |
|---|---|---|
| `LlmClient`, `LlmRequest`, `ChatMessage`, `StatefulResponse`, `ToolCall`, `ToolResult`, `ToolSchema`, `ToolExecutor` | **keep** | Provider-neutral. The OpenRouter client implements `LlmClient`. |
| `AnthropicClient` | **replace with OpenRouter client** | Do not add OpenAI/Gemini siblings. `[DECIDED]` OpenRouter is the model layer. |
| `ApiRequestLog` | **keep, extend** | Becomes the every-call log with input type and token count that decides Phase 1.5. Redaction of emails and phone numbers is required before any non-button door opens. |
| `Provider` | **drop** | Provider enumeration is OpenRouter's job. |

### `cowork/config` — **keep.**

`Config`, `AppPaths`, `ClientFactory`. `ClientFactory` is the single place a client is built; the OpenRouter swap is a one-file change, which is exactly why it exists. Config moves to Spring `@ConfigurationProperties` in its own bounded migration session (playbook Template 3).

### `cowork/buttons`, `cowork/data` — **keep for now, review at web port.**

`SuiteButton`, `ButtonStore`, `CategoryColorMap` are the user-defined prompt buttons; category = colour = grouping is a principle worth keeping. `Initiative`, `Relationship`, `OperationalFeedItem`, `WorkflowDefinition` and their stores are per-concern JSON files that become entities or get folded into the context store. `RecommendationEngine` is a candidate ancestor of capability proposals; do not extend it before Phase 2.5.

### `cowork/ui` — **drop at web port.**

All Swing. `AgenticRoutinesPanel.rebuildSidebar` is the one method whose logic (iterate categories, button per task, description as tooltip, availability drives enabled state) is reimplemented against `GET /api/tasks`. Nothing else here ports.

### Root files

| File | Verdict |
|---|---|
| `pom.xml` | keep; add Spring Boot and Postgres in the migration session |
| `config.properties.example` | keep; keys become one OpenRouter key |
| `.gitignore` | keep; already excludes every runtime data file |
| `FUTURE.md` | keep for history; superseded where it conflicts with `docs/phases.md` (item 2, multi-provider routing, is superseded by OpenRouter) |
| `assets/` | keep (the RSO facility request PDF and the availability fixture) |

## Not in this repo but reusable

- **OpenClaw connector**: exists in `conductor/agents/OpenClawClient` and the old `aicollab`. `FUTURE.md` item 3 estimated it at ~60 lines. Whether it lives here or in `cowork-shared` is `[OPEN]`.
- **Daily Brief pipeline** (Apps Script): the scheduled-trigger and fetch-then-summarize shapes for the check-in agent.

## Port sequence (from the button spawner spec)

1. Audit in place (this document, then `DEBT-INVENTORY.md`)
2. Lift the interface and registry with the propose/commit split and `Function` enum
3. Port one task end to end: room reservation
4. Build the web board against `GET /api/tasks`, `POST /api/tasks/{id}/propose`, `POST /api/proposals/{id}/approve`, `POST /api/proposals/{id}/reject`
5. Migrate remaining tasks one at a time
