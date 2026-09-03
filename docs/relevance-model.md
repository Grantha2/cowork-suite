# Relevance Model

## Why recency weighting is wrong here

The default retrieval instinct is that newer is more relevant. For a student organization that is backwards in two specific ways.

**Founding material does not decay.** Mission, virtues, scope, why the org exists, what it refuses to do. This is the oldest content in the store and the most important. A recency-weighted system buries the constitution under last week's catering invoice.

**Student orgs run in annual cycles, not linear time.** Last September's recruitment playbook is highly relevant this September and nearly irrelevant in March. Relevance is cyclical. A linear decay function gets the single most valuable retrieval case exactly wrong: "what did we do last time we did this."

So the model is three classes, not one curve. `[DECIDED]`

## Three knowledge classes

| Class | Examples | Decay | Changed by |
|---|---|---|---|
| **CANONICAL** | Mission, virtues, founding principles, scope, bylaws, brand, standing constraints ("we need AV", "nothing past 9pm", "over $200 needs treasurer approval") | **None.** Always in retrieval scope. | Explicit governance action only. Versioned, never overwritten. |
| **PRECEDENT** | Last year's fall recruitment, how the spring banquet ran, budget cycle patterns, event lead-time templates | **Cyclical.** Boosted by calendar proximity to its original date. | Superseded by the next cycle's version; prior versions retained. |
| **OPERATIONAL** | This week's tasks, current contacts, open invoices, active bookings | **Fast.** Weeks to months. | Continuously. |

**Retrieval rule:** canonical is always in scope. Precedent is boosted by calendar proximity to the time it refers to. Operational is recency-weighted normally.

That one rule is the difference between answering "how do we run recruitment" with a playbook and answering it with an email from Tuesday.

## Decay varies by function

Even within operational, functions differ. Finance rules change slowly; event logistics change fast. Store a decay profile per function and class, not one global constant.

`[REC]` Do not hand-tune these. Ship sane defaults as config; revise once there are retrieval failures to learn from. The existing per-field TTLs in `OrganizationContext` (3 / 7 / 14 / 30 days for urgent / operational / strategic / stable) are a reasonable seed for the operational profiles.

## Classification is collaborative

Agents propose, humans dispose, here too.

```
Item enters through any door
   |
Classifier (local SLM) proposes: org, function, class, decay profile, tags
   |
Item lands in the store with the proposed classification, marked UNCONFIRMED
   |
Human confirms, corrects, or promotes, only when they notice it is wrong
   |
Corrections become high-signal examples that improve future classification
```

Nobody sits down to tag a backlog. Classification happens at ingestion, automatically, and humans intervene only on error.

**The one action that requires a human: promotion to canonical.** `[DECIDED — invariant]` Declaring something a founding principle or a standing constraint permanently changes what the system considers always-relevant. It requires explicit approval and records who approved and when. Everything else the agent decides and a human can quietly fix later.

**Corrections are the training signal.** When an officer moves something from operational to precedent, or retags Events to Finance, that is a human teaching the system how this organization thinks. Log corrections separately from confirmations. They are the highest-value data in the store.

`[REC]` Surface a review queue for unconfirmed items, capped at ten. Never a backlog. An unbounded queue is unpaid work and gets abandoned.

## Retrieval usage as a relevance signal

`TaskProposal.sourcesUsed` records which entries informed each proposal. Entries that repeatedly inform proposals that get approved are demonstrably relevant. Entries that never surface are candidates for decay. This gives a relevance signal without anyone tagging anything. Design the store so this is recordable from the first proposal.

## Not in scope

- Vector search. Relational first; the three-class rule plus function scoping does more for retrieval quality than embeddings would at this data volume.
- A per-org model. The org brain is the store plus the relevance model plus tiered retrieval, not a bespoke model.
