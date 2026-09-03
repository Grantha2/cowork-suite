# Decisions

One line per decision, with date and the reasoning that will be invisible in six months. Newest at the bottom of each section. Tags: `[DECIDED]` settled by Grant; `[REC]` advisory, build to it but reversible; `[OPEN]` unresolved, see `docs/open-questions.md`.

## Product

- 2026-09-02 `[DECIDED]` — The product is institutional memory for student organizations, not a task launcher with AI features. Reason: officers turn over every one to two years and nothing they know survives them; a system that accumulates context does not graduate.
- 2026-09-02 `[DECIDED]` — Capture is active, not passive. Agents call officers on a schedule and file what they say. Reason: every RSO has been told to document and none do; passive tools like Notion go stale because nobody prompts anyone. Nobody declines a call.
- 2026-09-02 `[DECIDED]` — Nobody types a task, ever. Tasks are extracted from conversation, negotiated between officers' agents, and land on the board only after both humans approve. Reason: typing a task is friction paid now for a benefit later by a volunteer with a midterm tomorrow. Every RSO task tool that requires typing sits empty.
- 2026-09-02 `[DECIDED]` — First user is Grant, first org is IDSSO, first workflow is room reservation. Phase 1 done-when: used for a month and it surfaced something he had forgotten.
- 2026-09-02 `[DECIDED]` — Phase 2 onboards two officers who actually work together, not all seven. Reason: agent-to-agent negotiation needs both sides live; asymmetric adoption breaks it; two is enough to prove it.
- 2026-09-02 `[DECIDED]` — The demo for other orgs is the officer transition briefing, not the task board. Reason: it is the payoff the memory thesis promises, made visible.

## Architecture

- 2026-09-02 `[DECIDED]` — Spring Boot + Postgres. Relational first. No vector search until retrieval demands it. Reason: an empty store with brilliant retrieval is worth nothing; a messy store with a year of real content is worth everything. Add retrieval sophistication after there is something to retrieve.
- 2026-09-02 `[DECIDED]` — OpenRouter is the model layer. Do not build a provider router, per-provider clients, or custom fallback. Reason: OpenRouter already does fallback, cost routing, and per-request data policies (which matter for FERPA-adjacent data); model churn stops being our problem; "routed through OpenRouter" reads as an architecture decision in a demo, "I wrote my own router" reads as scope creep. Supersedes the deleted `ai-build-kit.md` and the router section of `ai-collab-three-repo-plan.md`.
- 2026-09-02 `[DECIDED]` — Two model tiers: local SLM for classification, extraction, routing, tagging; frontier via OpenRouter for negotiation, synthesis, briefings, and pull-engine reasoning. Rule: start small, escalate only when you can name the reason. Do not use SLMs for agent loops; sub-1B models fail reliably at function calling. Reason: ingestion classification is high-volume, narrow, and repetitive, which is exactly what small models are best at and what frontier models are most wasteful at; a local classifier also means raw sensitive input never crosses a provider boundary.
- 2026-09-02 `[DECIDED]` — Phase 1 uses frontier models for everything and logs every call with input type and token count. SLM work starts in Phase 1.5 by reading that log. Reason: optimizing before the log exists is guessing.
- 2026-09-02 `[DECIDED]` — Do not fine-tune a model per organization. Tune on the category (how student orgs operate) once there are several orgs; per-org specificity lives in retrieval. Reason: one org generates too little text; per-org models are invisible-failure training runs to babysit; retrieval is auditable and deletable, which survives a data-ownership conversation with a university.
- 2026-09-02 `[DECIDED]` — The pull engine (objective -> path -> capabilities -> build) is Phase 2+ and is scaffolded as an empty package only. Reason: it only works on top of accumulated context; generic proposals kill trust on first contact. Cheapest test this semester: declare one IDSSO objective and have the system report weekly against it; if the report says nothing new, the store is not deep enough yet.
- 2026-09-02 `[DECIDED]` — Do not build on NotebookLM. Consumer has no public API; Enterprise is alpha and licensed; relevance weighting is the feature and it is a black box there. Use it to prototype the thesis only.
- 2026-09-02 `[DECIDED]` — Do not build a registry, catalog, or capability-discovery API. That layer belongs to AWS, Google, Kong, and the ARD spec.

## Knowledge model

- 2026-09-02 `[DECIDED]` — Three knowledge classes: canonical (no decay, always in scope), precedent (cyclical, resurfaces annually), operational (fast decay). Reason: recency weighting is backwards for student orgs in two ways; it buries the constitution under last week's invoice, and it gets "what did we do last time we did this" exactly wrong.
- 2026-09-02 `[DECIDED — invariant]` — Promotion to canonical requires explicit human approval and records who and when. Everything else the classifier decides and a human can quietly fix later.
- 2026-09-02 `[REC]` — Per-function decay profiles as config, shipped with sane defaults, not hand-tuned. Revise once there are retrieval failures to learn from.
- 2026-09-02 `[REC]` — Review queue for unconfirmed classifications capped at ten items. An unbounded queue is unpaid work and gets abandoned, which is the failure this product exists to solve.
- 2026-09-02 `[DECIDED]` — Log corrections separately from confirmations. A correction is the highest-value data in the store.

## Button board and tasks

- 2026-09-02 `[DECIDED]` — The button board is a port of the existing `AgenticTaskRegistry` + sidebar spawn loop, not new construction. Register a task, a button appears, zero per-task UI code. That property is what makes the board scale.
- 2026-09-02 `[DECIDED]` — Split `execute()` into `propose()` and `commit()`. `propose()` returns a `TaskProposal` (summary, options, reasoning, constraints applied, sources used, approval level) with no side effects outside the log. `commit()` runs only after approval. Reason: a void method cannot express a proposal; the invariant would live in every implementation's discipline instead of the contract.
- 2026-09-02 `[DECIDED]` — `Function` becomes an enum: EVENTS, FINANCE, MEMBERSHIP, COMMUNICATIONS, GOVERNANCE. Reason: string categories drift into "Finance", "finance", and "Budget" across contributors; the taxonomy is the product's spine.
- 2026-09-02 `[DECIDED]` — `ApprovalLevel` is AUTOMATIC | OFFICER | GOVERNANCE. Reason: approval fatigue. Trivial coordination clears silently; anything committing money, the org's name, or an external party needs an officer; promotion to canonical needs governance.
- 2026-09-02 `[DECIDED]` — `propose()` is async. Never call a model or the network on the UI thread.
- 2026-09-02 `[DECIDED]` — `AgenticTaskContext` must not reference a UI panel. Already true in this repo: `TaskOutput` replaced the panel reference. Keep it that way.
- 2026-09-02 `[REC]` — Room lookup scrapes and caches on a schedule; browser automation is reserved for authenticated form steps. Reason: a per-query browser session is slow, fragile, and rate-limit visible; a cache is debuggable when it breaks.

## Privacy

- 2026-09-02 `[DECIDED]` — Email ingestion reads a dedicated org address only, never a personal student inbox. If personal inboxes are ever supported: label-scoped or sender-allowlisted, opt-in per source, never a whole-inbox grant. Build the phone-call door before the email door. Reason: a student's inbox holds grades, financial aid, and medical mail; absorbing it is a FERPA-adjacent problem on a university campus.
- 2026-09-02 `[DECIDED]` — Officers can see exactly what was absorbed and delete any item. Attachments containing student records are never ingested without per-item confirmation.

## Repository

- 2026-09-02 `[DECIDED]` — `aicollab` is split into `debate-engine`, `conductor`, and `cowork-suite`. `cowork-shared` exists as a `[REC]` shared library, not yet ratified.
- 2026-09-02 `[REC]` — Overhaul this repo in place rather than starting a fresh one. See `docs/port-list.md` for the audit. Reverse by creating a new repo and copying forward only the files marked *keep*.
- 2026-09-02 `[DECIDED]` — Scaffolding session creates structure and documentation only. No feature work, no Phase 2 or 3 code, no resolution of the context schema.
