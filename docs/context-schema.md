# Context Schema — `[OPEN]`

**Status: unresolved. Do not finalize this in an agent session.** Grant has flagged this for design with his collaborator. This document frames the problem, lays out the entities that are settled, states the questions that are not, and offers three strawmen with tradeoffs. Then it stops.

Why it is the hard problem: users and tasks are straightforward. How a context entry relates to *organization, function, source, time, and authority* is not, and the answer determines what retrieval can and cannot do for the life of the product. Retrofitting a knowledge classification onto a populated store is painful, so the relevance model (three classes, per-function decay) must exist in the first migration even while the rest is provisional.

---

## What is settled

These are `[DECIDED]` and any strawman must accommodate them.

- **Scope axes.** Every entry belongs to exactly one organization and one function (EVENTS, FINANCE, MEMBERSHIP, COMMUNICATIONS, GOVERNANCE).
- **Knowledge class.** Every entry is CANONICAL, PRECEDENT, or OPERATIONAL. Canonical never decays and is versioned, never overwritten. Precedent resurfaces cyclically. Operational decays fast. See `relevance-model.md`.
- **Decay profile per function**, stored as config, not hand-tuned.
- **Provenance.** Every entry records its source (which door, which call, which email, which task) and the time it refers to (not only the time it was ingested).
- **Classification status.** Entries land UNCONFIRMED. A human can CONFIRM, CORRECT, or PROMOTE. Corrections are logged separately from confirmations.
- **Promotion to canonical** is a governance act: it records who approved and when.
- **Relational storage.** Postgres. No vector column in Phase 1.
- **Retrieval usage is a relevance signal.** `TaskProposal.sourcesUsed` records which entries informed a proposal. Entries that repeatedly inform good proposals are demonstrably relevant; entries that never surface are decay candidates.

---

## What already exists in the code

`cowork/context/OrganizationContext` holds ten fixed narrative fields, each a `ContextEntry<String>` carrying value, last-updated, source, confidence, and `ContextStatus`, with a per-field TTL (3 to 30 days) that yields a `Freshness`. `ReconciliationService` auto-applies `SAFE_AUTO` proposals and queues the rest. `ContextChangeLog` records changes.

This is a **fixed-field, single-org, operational-only** design. It is a good ancestor and a bad schema for the product: it cannot hold a founding principle (no canonical class), cannot resurface last September (no cyclical relevance), cannot scope to a function (no function axis), and cannot hold more than ten things. The per-field TTL idea survives as the per-function decay profile. The confidence, source, and status fields survive as-is.

---

## The entities nobody disputes

```
Organization      id, name, founded, status
Officer           id, org, role, term start/end       <- roles are per term, not per person
Function          enum: EVENTS FINANCE MEMBERSHIP COMMUNICATIONS GOVERNANCE
Source            id, kind (CALL | EMAIL | CALENDAR | FORM | BUTTON | MANUAL), ref, ingested_at
ContextEntry      id, org, function, class, body, refers_to_time, ingested_at,
                  source, confidence, status (UNCONFIRMED | CONFIRMED | CORRECTED | PROMOTED)
Correction        entry, by whom, from -> to, when, reason        <- highest-value table
CanonicalVersion  entry, version, approved_by, approved_at, supersedes
DecayProfile      function, class, half-life or curve params
```

---

## The questions that are open

1. **Granularity.** Is a `ContextEntry` a sentence-sized fact ("the 4th-floor lounge fits 40 with AV"), a document ("fall 2025 recruitment playbook"), or both with a parent-child link? Facts retrieve precisely but classify expensively; documents classify cheaply but retrieve coarsely.
2. **Authority.** Who said it, and does that change how much the system trusts it? A treasurer's statement about the budget outranks a new member's. Is authority a property of the entry, of the source, or of the officer at the time of saying it? Officers change roles.
3. **Time has two meanings.** When it was ingested vs. what period it describes. "Last year's banquet cost $1,200" ingested today refers to April 2025. Precedent retrieval needs the referred-to time; operational decay needs the ingested time. Both must be stored. Which one is canonical for ordering?
4. **Cross-function entries.** A budget for an event is both FINANCE and EVENTS. One function with tags? Two entries? A primary function plus secondary links? Single-function is simpler and forces a decision at classification; the classifier will get it wrong sometimes and corrections will teach it.
5. **Supersession vs. contradiction.** New precedent supersedes old precedent but old versions are kept. What happens when two operational entries contradict ("room booked for the 12th" / "room booked for the 14th")? `ReconciliationService` handles this today for fixed fields. What is the general rule?
6. **Per-officer vs. per-org context.** The check-in agent hears things that are one officer's workload and things that are the org's knowledge. Is there a personal scope, and does it ever promote to org scope?
7. **What the entry references.** Rooms, vendors, faculty, events, forms. Are these first-class entities (normalized tables) or names inside entry bodies? Normalizing early is the classic over-design trap; never normalizing makes "which rooms does this org actually pick" a text search forever.

---

## Three strawmen

### A. One wide table

One `context_entry` table with all axes as columns; tags as a JSONB column; relationships as free text in the body.

- **For:** fastest to ship Phase 1. The classifier's output maps 1:1 onto a row. Retrieval is one query with a class-aware ORDER BY.
- **Against:** cross-function and referenced-entity questions get answered "search the body," which is fine at 500 entries and a problem at 50,000. Authority has nowhere to live except a column that means different things for different sources.
- **Reversible?** Mostly. Adding link tables later is additive.

### B. Entry plus typed links

`context_entry` as in A, plus an `entry_link` table (`from`, `to`, `kind`: SUPERSEDES | CONTRADICTS | DERIVED_FROM | ABOUT_SAME_EVENT) and an `entity` table (rooms, vendors, people, events) with `entry_entity` mentions.

- **For:** answers questions 5 and 7 structurally. "Which rooms does this org actually pick" is a join. Supersession is explicit, so canonical versioning and precedent cycles share one mechanism.
- **Against:** the classifier now has to emit links and entity mentions, which is a harder extraction task and more places to be wrong. More tables to migrate before there is a single real entry in any of them.
- **Reversible?** Links and entities can start empty and be populated by a later pass over A-shaped rows, so this can be A first, B when needed.

### C. Document-and-fact layering

Two tables: `document` (the raw ingested thing: a call transcript, an email, a form) and `fact` (a classified, sentence-sized claim extracted from a document). Facts carry the axes; documents carry provenance and authority. Retrieval is over facts; audit and "show me what was absorbed" is over documents.

- **For:** answers question 1 directly and makes the privacy requirement ("show officers exactly what was absorbed, let them delete it") a delete on the document with cascading fact removal. Authority attaches naturally to the document (who sent the email, who was on the call).
- **Against:** doubles the ingestion work in Phase 1: every door produces a document *and* runs extraction. Fact extraction quality becomes the product's ceiling from day one, and a bad extractor with a good store looks like a bad product.
- **Reversible?** Hard to undo once facts and documents have diverged.

---

## What the collaborator conversation needs to produce

Not a finished schema. Three answers:

1. Granularity: fact, document, or both? (Question 1; decides between A/B and C.)
2. Where authority lives. (Question 2.)
3. Whether referenced entities are first-class in Phase 1 or a later pass. (Question 7; decides A vs. B.)

Everything else can be provisional in the first migration, with the three knowledge classes and per-function decay profiles present from the start.

**Recommended next step before the conversation** `[REC]`: run the NotebookLM thesis test (dump every IDSSO document in, ask a new treasurer's questions). The kinds of questions it answers well and badly will say more about the right granularity than any whiteboard session.
