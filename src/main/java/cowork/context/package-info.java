/**
 * CONTEXT STORE: per org, per function, accumulating. The moat.
 *
 * <p>What is here today is the ancestor, not the product: {@code OrganizationContext} holds ten
 * fixed narrative fields for a single org as {@code ContextEntry<String>} (value, last-updated,
 * source, confidence, status) with per-field TTLs, persisted to JSON. {@code ReconciliationService}
 * auto-applies safe proposed changes and queues the rest for approval. {@code ContextChangeLog}
 * is the audit trail. Keep the concepts; replace the storage with Postgres entities in Phase 1.
 * See {@code docs/port-list.md}.
 *
 * <p><b>The schema is {@code [OPEN]}.</b> Read {@code docs/context-schema.md} before designing
 * entities. Do not resolve the open questions in an agent session; Grant and his collaborator
 * own that decision. What IS settled and must be present from the first migration:
 * <ul>
 *   <li>Every entry has exactly one org and one {@code Function}.</li>
 *   <li>Every entry has a knowledge class: CANONICAL (never decays, versioned, never overwritten),
 *       PRECEDENT (cyclical; boosted by calendar proximity to the time it refers to), or
 *       OPERATIONAL (fast decay). <b>Do not ship a monotonic decay function.</b></li>
 *   <li>Decay profiles are per function and class, stored as config, not hand-tuned.</li>
 *   <li>Every entry records source, ingested-at, and referred-to time (two different times).</li>
 *   <li>Entries land UNCONFIRMED. Corrections are logged separately from confirmations.</li>
 *   <li>Promotion to CANONICAL requires explicit human approval and records who and when.</li>
 *   <li>Relational. No vector column until retrieval demands it.</li>
 * </ul>
 *
 * <p>Nothing enters this store without passing a classifier in {@code cowork.ingestion}. There is
 * no "raw" insert path. If you find yourself writing one, stop.
 */
package cowork.context;
