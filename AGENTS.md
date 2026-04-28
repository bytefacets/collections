# AGENTS.md

## Purpose
This document guides automated agents and contributors making changes to this repository.

## Non-Negotiable Rules
- Do not introduce external dependencies without discussion.
- Maintain Java 25+ compatibility.
- Preserve API backward compatibility unless explicitly allowed.
- All public APIs must include Javadoc.
- All changes must include unit tests.

## Architecture Constraints
- Core data structures must remain allocation-efficient.
- Avoid hidden object creation in hot paths.
- APIs are designed for composition via input/output ports — do not collapse abstractions for convenience.
- The library is by default intended to be single threaded. 
- No reflection in core execution paths.

## Coding Guidelines
- Prefer immutability for public-facing objects.
- Internal performance-sensitive paths may use mutable structures.
- Avoid Streams API in hot paths; prefer loops.
- Use primitive collections where possible to reduce boxing.

## Testing Requirements
- All new features must include:
    - happy path tests
    - edge cases (empty, null where applicable)
    - concurrency tests if relevant

## API Evolution
- Do not remove or change existing public methods without:
    - deprecation cycle
    - migration notes
- Prefer additive changes.
- Avoid widening types in a way that breaks type inference.

## Build & Validation
- Build: `./gradlew build`
- Run tests: `./gradlew test`
- Format: `./gradlew spotlessApply`
- CI must pass before merging.