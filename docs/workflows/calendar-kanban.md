# Workflow: Calendar and Connected Kanban

Second surface. Phase 2.5. Documented now so the context schema accommodates it; not built this semester.

## The insight

An event date is not a data point. It is **the endpoint of a backwards-planned schedule.**

```
Event: Fall Networking Night, November 12
   | backwards planning from the event-type template
Room booked            by Oct 15   -> EVENTS
Budget approved        by Oct 20   -> FINANCE
Speakers confirmed     by Oct 28   -> EVENTS
Promo campaign live    by Nov 3    -> COMMUNICATIONS
Headcount to catering  by Nov 8    -> FINANCE
Day-of assignments     by Nov 11   -> MEMBERSHIP
```

**An officer adds one event. The board populates itself.** Not a calendar with tasks next to it, but a calendar that knows what an event of this type requires and by when.

## Where the templates come from

Each event type has a lead-time template. Templates are **precedent knowledge** and improve every cycle:

- Year one: sensible defaults plus whatever the officers say
- Year two: adjusted by what actually happened last year
- Year three: the org has a genuine operational playbook nobody sat down to write

This is the memory thesis made visible. The calendar is where an outsider can *see* that the organization remembers.

## Connection rules

- Every card carries a `Function` and inherits the event's deadline chain.
- Moving an event date **proposes** shifts to every dependent card. It does not silently move them. Propose-dispose, as everywhere.
- Card completion writes back to context, which feeds next year's template.
- Cards can be created by agent negotiation. Nobody types a task.

## Scope discipline `[REC]`

The failure mode is rebuilding Asana. The distinction to hold: **this generates the plan from institutional knowledge; a project tool asks you to enter it.** If a feature only makes sense for a team that already knows its plan, it belongs in a project tool, not here.

## What the schema needs to support this (so Phase 1 does not block it)

- An `EVENT` entity or an event-typed context entry with a date and a type
- Precedent entries that can carry a structured lead-time template, not only prose
- Entries that reference other entries (card -> event), which is strawman B in `context-schema.md`

None of this is built in Phase 1. Only the schema's ability to hold it is.
