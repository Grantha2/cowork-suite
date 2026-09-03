/**
 * BOARD: the function-organized button board.
 *
 * <p>This is a PORT of {@code cowork.tasks}, not new construction. {@code AgenticTaskRegistry}
 * groups tasks by category and the sidebar spawns a button per registered task with zero
 * per-task UI code. That mechanism is the board. See {@code docs/port-list.md}.
 *
 * <p>What changes in the port (all {@code [DECIDED]}):
 * <ul>
 *   <li>{@code String getCategory()} becomes {@code Function getFunction()}, an enum:
 *       EVENTS, FINANCE, MEMBERSHIP, COMMUNICATIONS, GOVERNANCE.</li>
 *   <li>{@code boolean isAvailable()} becomes {@code Availability checkAvailability(ctx)} with a
 *       human-readable reason ("Unavailable: no room inventory cached since Aug 28").</li>
 *   <li>{@code void execute(ctx)} is split into {@code TaskProposal propose(ctx)} (no side effects
 *       outside the log) and {@code TaskResult commit(ctx, ApprovedProposal)} (runs only after
 *       explicit human approval). <b>Never port {@code execute} forward as-is.</b></li>
 *   <li>{@code TaskProposal} carries summary, 1-3 options, reasoning, constraintsApplied,
 *       sourcesUsed (provenance; doubles as a relevance signal), and an {@code ApprovalLevel}
 *       of AUTOMATIC | OFFICER | GOVERNANCE.</li>
 *   <li>{@code propose()} is asynchronous. Never call a model or the network on a UI thread.</li>
 *   <li>{@code AgenticTaskContext} gains {@code ContextStore}, {@code ModelRouter}, and
 *       {@code ActorRef} (which officer, which agent). It must never reference a UI component.</li>
 * </ul>
 *
 * <p>Web surface, once it exists:
 * <pre>
 * GET  /api/tasks                    tasks grouped by function, with availability + reasons
 * POST /api/tasks/{id}/propose       returns TaskProposal
 * POST /api/proposals/{id}/approve   returns TaskResult
 * POST /api/proposals/{id}/reject    body carries a reason, logged to context
 * </pre>
 * The reject-with-reason endpoint matters as much as approve: rejections are the highest-value
 * correction signal in the system.
 *
 * <p>Phase 1 ports exactly one task end to end: room reservation.
 */
package cowork.board;
