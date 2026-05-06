# Build & Run (JBang)
- Run: `jbang run src/Main.java`
- Build (compile check): `jbang build src/Main.java`
- Test: `jbang run src/test/TestRunner.java`
- Single test: `jbang run src/test/TestRunner.java --filter=TestClassName`

# Code Conventions
- Java 26, no framework — plain Java with libraries
- Base package: `be.lomagnette.duke.runner`
- Use records for data carriers, sealed interfaces for domain types
- Dependencies declared via `//DEPS` directives in source files
- Multi-file projects use `//SOURCES` directives

# Git Workflow
- Branch naming: feature/TICKET-xxx-desc, bugfix/TICKET-xxx-desc
- Commit format: conventional commits (feat:, fix:, refactor:, test:)
- Never push to main. Feature branches + PR only.
- Compile check before committing.

# Development Process
- For any feature or non-trivial change: use the skill workflow
  1. `/interview TICKET-xxx` — Interview and produce SPEC.md
  2. `/design TICKET-xxx` — Create PLAN.md + TASKS.md
  3. `/implement TICKET-xxx` — TDD implementation, one task at a time
  4. `/pr TICKET-xxx` — Create draft PR
- For bug fixes: `/bugfix TICKET-xxx`
- For refactoring: `/refactor <area>`

# Boundaries
- ALWAYS: run tests before commits, follow existing patterns, use existing abstractions
- ASK FIRST: new dependencies, public API changes
- NEVER: commit secrets, modify production configs, skip tests
