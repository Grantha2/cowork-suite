/**
 * INGESTION: the doors through which context enters the store.
 *
 * <p>Doors, in build order: buttons (exists in {@code cowork.tasks}), phone calls (Phase 1),
 * calendar and forms (Phase 1-2), email (Phase 2). See {@code docs/capture-mechanisms.md}.
 *
 * <p><b>STOP AND READ {@code docs/privacy.md} BEFORE WRITING CODE IN THIS PACKAGE.</b>
 * Two rules here are where a well-meaning contributor creates a real FERPA problem:
 *
 * <ol>
 *   <li><b>Email ingestion reads a dedicated organization mailbox only</b> ({@code idsso@uic.edu}
 *       style). NEVER a personal student inbox. A student's inbox holds grades, financial aid,
 *       and medical mail. If personal inboxes are ever supported (not planned): label-scoped or
 *       sender-allowlisted, opt-in per source, never a whole-inbox grant. Attachments that may
 *       contain student records are never ingested without per-item confirmation.</li>
 *   <li><b>No door opens without a classifier behind it.</b> Every item entering the store must be
 *       assigned org, function, knowledge class, source, and time, and must land marked
 *       UNCONFIRMED. If the classifier cannot say where an item belongs, do not ingest it.
 *       Scoping is what makes deletion and access control possible.</li>
 * </ol>
 *
 * <p>Also: officers must be able to see exactly what was absorbed and delete any item. Design
 * every door so that is a query and a delete, not an archaeology project.
 *
 * <p>Phase 1 uses frontier models for everything, so Phase 1 must not open the email door;
 * the local classifier (Phase 1.5) has to exist first so raw sensitive input never crosses a
 * provider boundary.
 *
 * <p>Intended contents (none implemented yet): a {@code Door} interface, a {@code Classifier}
 * interface with a frontier implementation and later a local-SLM implementation, and one
 * door implementation per source. Corrections to classifications are logged separately from
 * confirmations; see {@code docs/relevance-model.md}.
 */
package cowork.ingestion;
