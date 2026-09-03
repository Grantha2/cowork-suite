# Capture Mechanisms: the four doors

Capture is the product. Retrieval is table stakes. An empty store with brilliant retrieval is worth nothing. These four are ordered by build priority.

Every door has a classifier behind it. `[DECIDED — invariant]` Nothing enters the store unclassified.

## A. Recurring agentic phone calls (the wedge) — Phase 1

OpenClaw agents call officers on a routine loop. The agent asks, the human talks, the agent structures and files the result. The officer writes nothing.

This is the single most important design decision in the product, because it is what makes capture happen with volunteer users. Nobody declines a call. People say out loud what they would never sit down and type.

- **Cadence:** daily for a single power user (Grant, Phase 1). Weekly for officers. Daily to a treasurer will feel like harassment.
- **Agenda per role.** A treasurer is asked different questions than a marketing director. Pull the question sets from the existing IDSSO officer task tables.
- **Reuse:** the OpenClaw connector already exists (originally in `aicollab`; also planned for `cowork-shared`). The Daily Brief Apps Script pipeline has the scheduled-trigger shape. Extend those.
- **Privacy surface:** essentially zero. The officer chooses what to say. This is why it ships first.

## B. Scoped institutional email ingestion — Phase 2

Read access to organization email so relevant information is absorbed without anyone forwarding anything.

**Scoping is the entire design problem and must be solved before this ships.** A student's inbox contains grades, financial aid, medical appointments, and personal correspondence alongside org business. See `privacy.md` for the non-negotiable constraints. The short version:

- Dedicated org address only (`idsso@uic.edu` style). Never a personal inbox.
- Show officers exactly what was absorbed; let them delete any item.
- Never ingest attachments containing student records without per-item confirmation.

Build A before B. The call loop proves the concept with zero privacy surface. Email is where the legal and trust risk lives.

## C. Onboarding and best-practices guide agent — Phase 2

An agent that walks a new officer through setup: which connectors to enable, what context is worth capturing, how to use the board, what good looks like.

This solves the failure mode that kills Notion in student orgs: the person who built the workspace graduates and the successor inherits something they do not understand. A guide agent orients every new officer without a predecessor in the room. It is also the retention mechanism; most AI implementations fail on adoption, not technology.

## D. The agent-negotiated task board (the core mechanic) — Phase 2

**Nobody types a task. Ever.** Everything else bends around that constraint.

How a task gets created:

1. **Officer talks to their agent.** On a scheduled call or ad hoc. "We need the room for the panel and I don't know if marketing has the flyer ready."
2. **Agent extracts intent.** A structured understanding of what needs to happen, who else it touches, and the constraints. Not a transcript.
3. **Agent contacts the other officer's agent.** That agent already holds its user's context: workload, last call, what is in flight.
4. **The two agents negotiate a proposal.** Scope, owner, deadline, dependencies. Each argues from its own user's real constraints.
5. **Both users see the proposal and approve.** The hard gate. Nothing becomes a task until both humans said yes.
6. **The approved task lands on the board**, fully specified, with context attached, and nobody typed a word.

**The proposal must show its reasoning.** "Marketing's agent flagged two deliverables due the same week, so the proposed deadline is the 14th, not the 10th." A rubber-stamped approval gate protects nobody.

Design risks:

| Risk | Mitigation |
|---|---|
| Approval fatigue | `ApprovalLevel`: AUTOMATIC clears silently; OFFICER for anything committing money, the org's name, or an external party; GOVERNANCE for promotion to canonical. Threshold is `[OPEN]` until there is usage data. |
| Plausible but unwanted proposals | One-tap reject with a reason, fed back into both agents' context so the same bad proposal does not recur. The reject endpoint matters as much as approve. |
| Asymmetric adoption | Phase 2 onboards officers in working pairs, not all seven at once. |

**The board is an output surface, not an input surface.** It displays what agents negotiated and humans approved, organized by function: Events and Logistics, Membership, Finance, Communications, Governance. Every task consumes context and produces context. That loop is what compounds.

## The button door — exists today

The existing prompt buttons (`cowork/tasks`, `cowork/buttons`) are the fifth, already-open door: an officer presses a button, answers a short form, and the task's proposal and outcome are written back to context. Room reservation is the first button. See `workflows/room-reservation.md` and `port-list.md`.
