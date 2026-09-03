# Workflow: Room Reservation

The first end-to-end workflow. Phase 1 item 2. This is the demo, because it proves the store, the classifier, canonical constraints, and the propose-approve pattern in one artifact.

## The pipeline

```
1. LOOKUP      Room inventory + availability from public UIC sources (cached)
2. INTERPRET   Turn human-readable schedules and policy notes into structured availability
3. VALIDATE    Check candidates against org constraints (capacity, AV, budget, timing, policy)
4. PROPOSE     Present 2-3 ranked options with reasoning to the officer
5. APPROVE     Human picks one. Nothing is submitted without this.
6. COMPLETE    Fill the standard form with the approved values. Prepared, not sent.
```

Steps 1-4 are `propose()`. Step 6 is `commit()`. Step 5 is the human.

## As an `AgenticTask`

```
Function:      EVENTS
Availability:  requires cached room inventory < 48h old
               -> unavailable reason names the staleness explicitly:
                  "Unavailable: no room inventory cached since Aug 28"

propose(ctx):
  1. Read cached UIC room inventory and availability
  2. SLM extraction: normalize human-readable schedules and policy notes   (frontier in Phase 1)
  3. Pull constraints from CANONICAL context: capacity, AV, budget ceiling,
     time-of-day policy, prior-approval rules
  4. Validate candidates against them
  5. Return 2-3 options, ranked, with reasoning and constraintsApplied
  -> ApprovalLevel.OFFICER

commit(ctx, approved):
  Fill the RSO Facility Request PDF with the approved values.
  Draft the cover email.
  Does NOT submit either. Prepared for the officer to send.
  Writes the outcome to context: which options were offered, which was chosen.
```

That last line compounds. After two semesters the store knows which rooms this org actually picks, and the ranking in step 5 improves without anyone configuring it.

## Design calls

**Scrape and cache; do not browse live.** `[REC]` A scheduled scraper caches room inventory and availability once or twice daily. Browser automation (the existing `ComputerUseToolProxy`) is reserved for pages that genuinely require session state, which is the authenticated form step, not the read step. A per-query browser session is slow, fragile, expensive, and rate-limit visible; a cache serves every query in the org and is debuggable when it breaks.

**The interpretation step is the actual product.** The data is public; making it machine-usable is the work. Room descriptions, capacity notes, AV listings, and policy footnotes are written for humans. Normalizing them into structured constraints is a textbook SLM extraction task (Phase 1.5; frontier in Phase 1).

**Constraints are canonical knowledge.** "We need AV." "We cannot book past 9pm." "Anything over $200 needs treasurer approval." These live in the canonical class and are validated against automatically. This is the workflow that proves the knowledge layer earns its keep.

**Never auto-submit.** `[DECIDED — invariant]` The agent fills; the officer sends. A booking commits the org's name and sometimes its money.

**Log every proposal and outcome.** Which rooms were proposed, which was picked, what happened. Within two semesters that is precedent knowledge.

## What exists today

`cowork/workflows/RoomReservationWorkflow` runs one Claude turn with four tools registered: `RoomAvailabilityTool` (fixture or live mode), `PdfFillTool`, `EmailDraftTool`, and the computer-use proxy. It already never submits: the output is a filled-PDF path and an email draft. Input comes from a Swing dialog. Tests exist for the three tools.

The port: split the single turn into `propose()` and `commit()`, move the dialog's fields into a declared input schema, add the cache to `RoomAvailabilityTool`, read constraints from the store instead of the prompt, and write the outcome back. Nothing in the tools themselves needs to change.
