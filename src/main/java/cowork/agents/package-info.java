/**
 * AGENT LAYER: check-in, coordination, guide, briefing.
 *
 * <p>Nothing implemented yet. See {@code docs/architecture.md} for what each agent does and
 * which phase it belongs to. Phase 1 builds only the check-in agent, for one user (Grant),
 * on a daily cadence, writing into the context store.
 *
 * <p>Invariants every agent in this package obeys:
 * <ul>
 *   <li><b>Agents propose, humans dispose.</b> No agent commits its user to anything. A
 *       proposal shows its reasoning and its sources. Rejection carries a reason that is fed
 *       back into context.</li>
 *   <li><b>Officers set objectives. Agents never do.</b></li>
 *   <li><b>Nothing is auto-submitted.</b> Prepare forms and messages; a human sends them.</li>
 *   <li>Small models route and classify; frontier models (via OpenRouter) reason. Never run an
 *       agent loop on a sub-1B model.</li>
 * </ul>
 *
 * <p>Reuse before building: the check-in agent's call loop is the existing OpenClaw connector
 * (in {@code conductor/agents/OpenClawClient} and the old {@code aicollab}; possibly
 * {@code cowork-shared}); its cadence is the Daily Brief Apps Script trigger pattern; its
 * "structure and file" step is {@code cowork.tasks.DailyContextUpdateFunction}.
 */
package cowork.agents;
