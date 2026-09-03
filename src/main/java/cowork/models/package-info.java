/**
 * MODEL LAYER: bought, not built.
 *
 * <p>Two tiers behind one {@code ModelRouter} interface (not yet written):
 * <ul>
 *   <li><b>Local SLM</b> (one 3-4B model): classification, extraction, routing, tagging,
 *       single-meeting summaries. On device. Phase 1.5 onward.</li>
 *   <li><b>Frontier via OpenRouter</b>: negotiation, synthesis, briefings, pull-engine reasoning,
 *       explaining reasoning to a human. API.</li>
 * </ul>
 *
 * <p>Rule: start small, escalate only when you can name the reason. Cascade on low confidence.
 * Instrument the escalation rate from day one. Never run an agent loop on a sub-1B model.
 *
 * <p><b>Phase 1 uses frontier for everything.</b> The only Phase 1 work here is (a) an OpenRouter
 * client implementing the existing {@code cowork.llm.LlmClient} interface, replacing
 * {@code AnthropicClient} via {@code cowork.config.ClientFactory}, and (b) logging every call with
 * its input type and token count (extend {@code cowork.llm.ApiRequestLog}). The log decides what
 * moves to the SLM in Phase 1.5.
 *
 * <p><b>Do not build here</b> ({@code [DECIDED]}): a provider router, per-provider clients,
 * fallback logic, or model selection heuristics. OpenRouter does all of that, including
 * per-request data policies that restrict which providers may receive FERPA-adjacent prompts.
 * Do not fine-tune a per-organization model; tune on the category, retrieve per org.
 *
 * <p>Whether the OpenRouter wrapper and SLM runner live here or in {@code cowork-shared} is
 * {@code [OPEN]} (see {@code docs/open-questions.md} #8). Until that is decided, design them
 * against this repo's first real caller and do not build speculatively in either place.
 */
package cowork.models;
