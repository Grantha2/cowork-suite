# Open Questions

Every `[OPEN]` item, in one place, with who needs to answer it. Nothing here is for an agent to resolve.

| # | Question | Owner | Blocks | Where it is discussed |
|---|---|---|---|---|
| 1 | **The context schema.** How do context entries relate to organization, function, source, time, and authority? Users and tasks are straightforward; this is not. | Grant + his collaborator | Phase 1 item 1 (context store) | `docs/context-schema.md` |
| 2 | **Pricing model.** $0.99/org/month is close to zero revenue. Presence, CampusGroups, and similar sell to the institution or student government, not to individual orgs. | Grant | Phase 2 (second org) | `docs/phases.md` |
| 3 | **Data ownership.** Who owns the knowledge base when officers turn over, and what happens when an org dissolves? | Grant, likely with UIC student affairs | Any second org's data entering the system | `docs/privacy.md` |
| 4 | **UIC IT and student affairs approval path.** Touching university email or selling campus-wide has an approval process. Find it early. Dr. Lundquist is the suggested first conversation. | Grant | Email ingestion door; campus-wide sale | `docs/privacy.md`, `docs/capture-mechanisms.md` |
| 5 | **Where does path decomposition come from** for the pull engine: IDSSO's own history, cross-org patterns, or model reasoning over org context? Probably all three, weighted by how much data exists. | Grant, post-Phase 2 | Phase 3 | `docs/architecture.md` |
| 6 | **How does an objective decompose without becoming a project-management app?** The distinction to hold: propose capabilities to build, not tasks to complete. | Grant | Phase 2.5 | `docs/architecture.md` |
| 7 | **Approval-fatigue threshold.** Where exactly is the line between AUTOMATIC and OFFICER? Needs real usage data. | Grant, after a month of Phase 1 | Phase 2 | `docs/capture-mechanisms.md` |
| 8 | **`cowork-shared` ratification.** Does the shared library exist, or does each repo keep its own copy of the OpenRouter wrapper and OpenClaw connector? | Grant | First shared-library consumer | `DECISIONS.md` |
| 9 | **Overhaul in place vs. new repo.** Recommended in place; not decided. | Grant | Phase 1 migration session | `docs/port-list.md` |

Resolve an item by moving its answer to `DECISIONS.md` with a date and deleting the row here.
