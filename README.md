# Cowork Suite

**A student organization that remembers.**

Officers serve one to two years. Every May a president graduates and takes the institutional knowledge with them: which room booking form actually works, which faculty member says yes to sponsorship, what the last three events really cost, why the constitution was amended. The successor rebuilds from scratch and repeats the mistakes. This is not a documentation problem. Volunteers do not document, because documentation is unpaid work with a payoff that arrives after the writer has left.

An AI system does not graduate. If context accumulates in a system instead of a person's head, the organization's memory outlives every member. **Context that accumulates in a system outlives every member of the organization.** That is the product.

**Positioning in one line:** Notion waits for you to write. This system calls you and asks.

---

## Who this is for

Built first for IDSSO, a UIC student org, by its founding president. The first user is the author. Phase 1 is done when he has used it for a month and it told him something he had forgotten.

The pitch to the next org president is not the task board. It is the **officer transition briefing**: a handoff document the outgoing officer never has to write, generated from a year of captured context, delivered to the successor on day one.

---

## Architecture: five layers

```
PULL ENGINE      objective -> path -> capabilities -> build   (Phase 2+)   <- the product
CONTEXT STORE    per org, per function, accumulating          (Phase 1)    <- the moat
INGESTION        calls . email . calendar . forms . buttons   (Phase 1-2)  <- the doors
AGENT LAYER      check-in . coordination . guide . briefing   (Phase 1-3)
MODEL LAYER      OpenRouter + local SLM tier                  (bought, not built)
```

The bottom layer is a commodity. The top layer is the one nobody has built, because it only works once the layers beneath it already know how the organization operates. Full description in `docs/architecture.md`.

**Stack** `[DECIDED]`: Java, Spring Boot, Postgres. Model calls through OpenRouter. Small local models for classification once the call log shows what is worth moving. See `DECISIONS.md` for the reasoning behind each of these.

---

## Design invariants `[DECIDED]`

These are not preferences. A contribution that violates one is wrong even if it works.

1. **Agents propose, humans dispose.** At every level. No agent commits its user to anything. Proposals show their reasoning. One-tap reject with a reason, and the reason is fed back into context.
2. **No door opens without a classifier behind it.** Nothing enters the store unclassified. If you cannot say which org, function, source, and time an item belongs to, do not ingest it.
3. **Officers set objectives. Agents never do.**
4. **Canonical knowledge never decays**, and promotion to canonical requires human approval, recorded with who and when. Mission, virtues, scope, bylaws, and standing constraints are always in retrieval scope.
5. **Relevance is cyclical, not linear.** Student orgs run annual cycles. Last September's playbook is highly relevant this September. Do not ship a monotonic decay function. See `docs/relevance-model.md`.
6. **Nothing is auto-submitted.** Agents prepare forms, bookings, and messages. Humans send them.
7. **Tasks propose, they do not execute.** `propose()` returns a `TaskProposal` with reasoning, sources, and an approval level. `commit()` runs only after approval. Never port `void execute(ctx)` forward as-is.
8. **Email ingestion defaults to a dedicated org address** (`idsso@uic.edu` style), never a personal student inbox. Build the phone-call door before the email door. See `docs/privacy.md`.

---

## Where things are

| Path | What it holds |
|---|---|
| `docs/architecture.md` | The five layers and what talks to what |
| `docs/context-schema.md` | **`[OPEN]`** The hard problem: how context entries relate to org, function, source, time, and authority. Strawmen and questions, not an answer. |
| `docs/relevance-model.md` | Canonical / precedent / operational knowledge classes and retrieval rules |
| `docs/capture-mechanisms.md` | The four doors: calls, email, guide agent, negotiated task board |
| `docs/workflows/room-reservation.md` | The first end-to-end workflow. The demo. |
| `docs/workflows/calendar-kanban.md` | Second surface: backwards-planned kanban from one event date |
| `docs/phases.md` | Phase plan with done-when conditions |
| `docs/privacy.md` | FERPA-adjacent handling rules |
| `docs/port-list.md` | Audit of the existing Swing app and what carries forward into the Spring Boot version |
| `docs/open-questions.md` | Every unresolved question, and who needs to answer it |
| `docs/agentic-prompt-playbook.md` | How to run agentic coding sessions against this repo without runaway cost |
| `DECISIONS.md` | Why things are the way they are, one line per decision |
| `DEBT-INVENTORY.md` | Empty until a read-only audit session fills it |
| `CLAUDE.md` | Build, test, and the rules an agent keeps violating |
| `FUTURE.md` | Pre-planning-session roadmap. Kept for history; superseded where it conflicts with `docs/phases.md`. |

---

## Current state of the code (September 2026)

This repository is the existing Cowork desktop app, a Swing application split out of the earlier `aicollab` project. It already has the highest-value assets the plan builds on:

- **`cowork/tasks`**: `AgenticTask`, `AgenticTaskRegistry`, `AgenticTaskContext`. Register a task, a button appears. Nine tasks are registered today. This *is* the button board; it is a port, not new construction.
- **`cowork/context`**: `OrganizationContext` (ten narrative fields with freshness, source, confidence, status), `ReconciliationService` (an approval queue for AI-proposed changes), `ContextChangeLog` (an audit trail). This is the memory layer's ancestor.
- **`cowork/workflows/RoomReservationWorkflow`**: availability lookup, PDF form fill, email draft. Already never auto-submits.
- **`cowork/llm`**: an Anthropic client with a tool loop and a redacting request log.

The planned package layout is stubbed alongside the existing code. Each new package has a `package-info.java` describing what belongs there and what does not:

```
src/main/java/cowork/
|-- context/       <- the store. Exists today as JSON files; becomes Postgres entities in Phase 1.
|-- ingestion/     <- the doors. Stub. Read the privacy warning in package-info before writing here.
|-- agents/        <- check-in, coordination, guide, briefing. Stub.
|-- board/         <- function-organized button board. Stub; ports from cowork/tasks.
|-- models/        <- OpenRouter client + SLM tier routing. Stub.
`-- objectives/    <- pull engine. Phase 2+. Deliberately empty.
```

`[REC]` **Overhaul in place rather than starting a new repository.** The port list in `docs/port-list.md` found that the plumbing worth keeping is already UI-agnostic and already tested, and the git history is worth preserving. This is a recommendation, not a decision. Reverse it cheaply by creating a fresh repo and copying forward exactly the files the port list marks *keep*.

---

## Build and run (the existing desktop app)

Requires Java 21 and Maven.

```bash
cp config.properties.example config.properties   # add your Anthropic key
mvn compile exec:java                             # launches the Swing GUI
mvn test                                          # no network; providers are faked
```

The Spring Boot + Postgres version does not exist yet. Phase 1 builds it. Do not start that migration without reading `DEBT-INVENTORY.md` first, and do not generate `DEBT-INVENTORY.md` in the same session that changes code.

---

## How to work on this repo

One deliverable per session. Audit before execute. Explicit out-of-scope blocks. Templates are in `docs/agentic-prompt-playbook.md`. The planning bundle that produced this scaffold lives outside the repo; `DECISIONS.md` carries forward everything from it that matters.

**Before writing any code in Phase 1**, run the cheapest possible test of the thesis: dump every IDSSO document into a NotebookLM notebook and ask it the questions a new treasurer would ask. If accumulated context cannot answer them there, it cannot answer them here either, and that is worth knowing for zero engineering cost. `[REC]`
