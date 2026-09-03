# Debt Inventory

**Not yet generated.** This file is the output of a read-only audit session, and it must exist before any Phase 1 migration session runs.

Run Template 1 from `docs/agentic-prompt-playbook.md` against this repo, on `main`, with a clean tree. The session writes only this file and fixes nothing.

Expected sections when filled:

1. Architecture map, one paragraph per package
2. Debt items with file and line ranges, why each matters, blast radius, blocked-or-independent
3. Dead code
4. Duplication
5. Recommended order of attack

`docs/port-list.md` already records what the scaffolding session found worth keeping and worth dropping. The audit should confirm or correct it, not repeat it.
