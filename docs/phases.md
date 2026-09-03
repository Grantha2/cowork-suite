# Phases

Phase 1 is the only phase being built this semester. Everything else is stubbed and documented so nobody has to decide anything structural to start it. `[DECIDED]`

## Phase 1 — this semester, IDSSO only, one user

| # | Deliverable | Reuses | Notes |
|---|---|---|---|
| 1 | **Context store**, scoped per org and per function, in Postgres, with the three knowledge classes and per-function decay profiles present from the first migration | `cowork/context` (OrganizationContext, ReconciliationService, ContextChangeLog) | Schema is `[OPEN]`; ship a provisional one that satisfies the settled parts of `context-schema.md`. Schema and persistence only. Nothing else bundled into that session. |
| 2 | **One button end to end: room reservation**, through `propose()` / `commit()` | `cowork/workflows/RoomReservationWorkflow`, `RoomAvailabilityTool`, `PdfFillTool`, `EmailDraftTool` | Proves the store, the classifier, canonical constraints, and propose-approve in one artifact. See `workflows/room-reservation.md`. |
| 3 | **Grant's own daily check-in agent**, writing into the store | OpenClaw connector; Daily Brief scheduled-trigger pattern; `DailyContextUpdateFunction` | Daily cadence is right for one power user only. |
| 4 | **Basic retrieval**: ask a question, get an answer sourced from accumulated context | `TaskContext.buildTaskBlock` as the prompt-assembly ancestor | Relational queries using the class-aware retrieval rule. No vector search. |
| 5 | **Log every model call** with input type and token count | `cowork/llm/ApiRequestLog` | Frontier models for everything in Phase 1. The log decides Phase 1.5. |

**Done when:** Grant has used it for a month and it surfaced something he had forgotten.

Before Phase 1 code: the read-only audit session that fills `DEBT-INVENTORY.md`, and the NotebookLM thesis test `[REC]`.

## Phase 1.5 — after the log exists

Read the call log. Identify the highest-volume repetitive call (almost certainly ingestion classification). Move that one call to a single 3-4B local model. Measure cost delta and error rate. Do not evaluate five models.

## Phase 2 — only if Phase 1 works

| # | Deliverable |
|---|---|
| 6 | **Two officers, not seven.** A pair who actually work together (president + treasurer, or events + marketing). |
| 7 | Agent-negotiated task creation with the dual approval gate |
| 8 | Guide agent for onboarding |
| 9 | Scoped org email ingestion, dedicated address only, after the approval-path question is answered |
| 10 | **Objective capture:** one officer declares one semester goal; the system tracks against it weekly |
| 11 | Promotion-to-canonical governance flow (needs a second officer to approve) |
| 12 | SLM extended to task extraction and routing; escalation rate instrumented |

**Done when:** a task reached the board that neither officer typed, both approved, and both would say the proposal was better than what they would have coordinated themselves.

## Phase 2.5

- Remaining officers, added in working pairs
- Full function task board
- Calendar with backwards-planned kanban (needs event-type templates, which need one cycle of precedent or a seeded default set). See `workflows/calendar-kanban.md`.
- **Capability proposals:** the system suggests the next automation to build

## Phase 3 — post-graduation or with contributors

- Pre-meeting synthesis: agents exchange context before officer meetings, surface conflicts and open questions
- Second and third RSO as design partners
- **Transition briefing generator**, tested on a real May handoff. This is the demo.
- Full path decomposition in the pull engine
- Category-level SLM tuning, once there are three or more orgs and a real corpus

## What is human-gated and cannot be accelerated by agent throughput

Sequence these first because no amount of scaffolding speed moves them:

- Getting a second officer to actually use the system
- Designing the context schema with the collaborator
- UIC IT and student affairs approval
- A full annual cycle of precedent accumulating

## Open questions that must close before Phase 2

Pricing model; data ownership at turnover and dissolution; the UIC approval path. All in `open-questions.md`.
