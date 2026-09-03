# Privacy

Student organization data is FERPA-adjacent: names, attendance, sometimes financial detail, and, through email, potentially student records. This system runs on a university campus. These rules are not preferences.

The two places a well-meaning contributor could create a real FERPA problem are **email ingestion** and **skipping the classifier**. Both rules are repeated as code comments in `src/main/java/cowork/ingestion/package-info.java` so they are seen by whoever is about to write the wrong code.

## Rules `[DECIDED]`

1. **Email ingestion reads a dedicated organization address only** (`idsso@uic.edu` style). Never a personal student inbox. A personal inbox holds grades, financial aid, medical appointments, and correspondence that has nothing to do with the org.
2. **If personal inboxes are ever supported** (they are not planned): label-scoped or sender-allowlisted, opt-in per source, never a whole-inbox grant.
3. **Officers can see exactly what was absorbed** and can delete any item from the store.
4. **Attachments that may contain student records are never ingested** without explicit per-item confirmation.
5. **Nothing enters the store unclassified.** If the system cannot say which org, function, source, and time an item belongs to, it does not ingest it. An unclassifiable item is a privacy risk, because scoping is what makes deletion and access control possible.
6. **Nothing is auto-submitted.** Forms, bookings, and messages are prepared for a human to send. Submitting commits the org's name and sometimes its money.
7. **Build the phone-call door before the email door.** The call loop has near-zero privacy surface; the officer chooses what to say.

## Why the model tiering is a privacy feature, not just a cost one

Raw ingested input (a call transcript, an email body) is the highest-volume and most sensitive path. Classifying it with a **local** small model means the raw layer never leaves the boundary. Only escalated cases reach a frontier provider, and by then the item is structured and can be scoped. Pair that with OpenRouter's per-request data policies for the escalated lane and the story is complete: local for volume, policy-constrained routing for judgment.

This is a compliance story that can be told to a university IT office in one sentence: **classify locally, escalate selectively, and the sensitive raw layer never crosses a provider boundary.**

Phase 1 uses frontier models for everything, so Phase 1 must not open the email door. The SLM classifier comes first.

## Why retrieve-per-org beats tune-per-org, for privacy

Per-org specificity lives in retrieval, which is instant, auditable, correctable, and deletable. Someone asks to remove data, you delete a row. You do not retrain. A per-org fine-tuned model cannot make that promise, and it would not survive a data-ownership conversation with a university.

## Existing safeguards in the code

- `cowork/llm/ApiRequestLog` persists request bodies to JSONL. `AnthropicClientRedactTest` covers key redaction. `FUTURE.md` item 4 (redact emails and phone numbers, cap retention) is still open and becomes mandatory before any door other than buttons opens.
- `.gitignore` excludes every runtime data file the app writes. Keep it that way as new stores are added.

## Open

- **Data ownership** at officer turnover and org dissolution. Answer before another org's data is in the system.
- **UIC IT and student affairs approval path.** Touching university email or selling campus-wide has one. Find it early; Dr. Lundquist is the suggested first conversation.

Both are tracked in `open-questions.md`.
