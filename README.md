# Cowork Suite

A one-click AI desk for organizational leaders who aren't engineers. A board of buttons — each backed by a form, not a chat box — captures intent and runs an agentic routine: read the org's shared context, do the task, propose changes, draft messages.

Split out of [`aicollab`](https://github.com/grant-hauskins/aicollab) into its own project once the button/task system outgrew the multi-model debate app it started in.

## What's built

- **Button board** — seven built-in agentic routines (start-your-day briefing, weekly report, stakeholder briefing, meeting prep, outbound messages, context refresh, initiative review), each launched with a click and a short form.
- **Organizational context store** — shared context with freshness tracking, so routines always work from current information.
- **Reconciliation queue** — AI-proposed changes to org context are queued for human approval before they land.
- **Tools/MCP plumbing** — room-reservation workflow, email drafting, PDF filling, computer-use tool proxy.

Roadmap and design notes in [`FUTURE.md`](./FUTURE.md).

## Run it

```bash
mvn compile exec:java
```

First launch prompts for an Anthropic API key and writes a local, git-ignored `config.properties` — see `config.properties.example`.

## Test

```bash
mvn test
```

## Stack

Java · Maven · Swing (FlatLaf) · Anthropic API
