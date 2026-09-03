# CLAUDE.md

Build: `mvn compile`    Run GUI: `mvn compile exec:java`    Test: `mvn test` (no network)

Rules:
- Agents propose, humans dispose. Never write code that submits a form, sends a message, or books anything. Prepare only.
- Nothing enters the context store unclassified (org, function, source, time). No ingestion path without a classifier.
- Email ingestion reads a dedicated org address only. Never a personal inbox. Read `docs/privacy.md` before touching `cowork/ingestion`.
- Do not build a provider router; OpenRouter is the model layer. Do not add vector search. Do not touch `cowork/objectives` (Phase 2+).
- Read `DEBT-INVENTORY.md` before any code change. If it is empty, run the audit session first; do not audit and change code in one session.
- One deliverable per session. Stop and report.
