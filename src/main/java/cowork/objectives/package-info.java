/**
 * PULL ENGINE: objective -> path -> capabilities -> build. Phase 2 and later.
 *
 * <p><b>This package is deliberately empty. Do not scaffold it.</b> {@code [DECIDED]}
 *
 * <p>The engine decomposes an officer-declared objective into a path, identifies the
 * capabilities the path needs, proposes which agents and automations to stand up next, and
 * re-proposes as reality changes. It only works on top of accumulated context. Generic
 * proposals kill user trust on first contact, and premature structure invites premature
 * building.
 *
 * <p>Officers set objectives. Agents never do. Agents propose capabilities; humans approve.
 *
 * <p>The cheapest test of the concept needs no code here: declare one IDSSO objective and have
 * the check-in agent report weekly against it using whatever context exists. If the report says
 * something Grant did not already know, the engine is worth building. If not, the store is not
 * deep enough yet. See {@code docs/architecture.md} and {@code DECISIONS.md}.
 */
package cowork.objectives;
