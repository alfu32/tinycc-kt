# Agent Guidance

## Management rules

- Maintain the project task breakdown and current progress in the root `TASKS.md` file.
- Keep the report at the very top of `TASKS.md` and show counts for `DONE`, `DOING`, and `TODO`, plus its last-updated datetime.
- Give every task and subtask a unique ID, title, current status (`DONE`, `DOING`, or `TODO`), `begin_datetime`, and `resolution_datetime`.
- Store datetimes as ISO 8601 UTC timestamps (`YYYY-MM-DDTHH:MM:SSZ`). For `TODO`, both datetime fields are `null`. For `DOING`, record the start and leave resolution `null`. For `DONE`, record both. If historical work predates this register and a timestamp cannot be established reliably, use `null` and state that in the task note instead of guessing.
- Count every task and subtask record, including parent workstreams, in the report totals. Keep parent statuses consistent with their subtasks and recalculate the report whenever any task status changes.
- Update `TASKS.md` when work starts, changes status, completes, or acquires new subtasks. Keep remaining implementation, research, and validation steps visible as `TODO` items.

## Response and commit message specification

## response guidelines

- always respond in the sum up in the commitizen format

All commits must follow the Commitizen / Conventional Commits standard using the structural layout below:

### Commitizen / Conventional Commits standard

```text
<type>(<scope>): <subject>

<body>
```

#### Field Definitions

* **`<type>`**: Must be one of the following lowercase tokens:
    * `feat`: A new feature or capability.
    * `fix`: A bug fix.
    * `docs`: Documentation changes only.
    * `style`: Changes that do not affect the meaning of the code (white-space, formatting, missing semi-colons, etc).
    * `refactor`: A code change that neither fixes a bug nor adds a feature.
    * `perf`: A code change that improves performance.
    * `test`: Adding missing tests or correcting existing tests.
    * `chore`: Changes to the build process, auxiliary tools, or libraries/dependencies.
* **`<scope>`**: Optional. A noun naming the specific codebase component or module affected, wrapped in parentheses (e.g., `(parser)`, `(auth)`, `(runtime)`).
* **`<subject>`**: A brief, imperative-mood summary of the change. Do not capitalize the first letter. Do not end with a period.
* **`<body>`**: Optional. Separate from the subject with exactly one blank line. Provides the motivation for the change and contrasts it with previous behavior.

Additionally, structure the body as follows:

```text
(REQUEST:)
- summary of what was asked/requested

(IMPLEMENTATION:)
- summary of the solution or answer
implementation details:
- bulleted list of technical/functional modifications or planning steps (what you print out by default in the summary)

(NOT IMPLEMENTED:)
- summary of not implemented features/parts of the request
- features/requests remaining to be implemented/researched
- eventual steps/tests to be taken by the user before proceeding
```

### Examples

```text
fix(editor): persist and reveal mapped compiler diagnostics

REQUEST:
the user has to be able to see error points given by diagnostics by expandable markers in the gutter

IMPLEMENTATION:
  - Diagnostics are persisted on each node and restored with the project.
  - New validation/compilation clears previous diagnostics.
  - Gutter markers now reveal the mapped editor, section, and source line automatically.
  - Nodes with diagnostics show a red warning badge in the diagram.
  - Runtime/override errors without source-map entries are retained and shown as unmapped instead of being discarded.
  - The status bar now shows:
    generated-file:line:column -> node section source-line:column

NOT IMPLEMENTED:
  - colorisation and retrieval of code artifacts
  - research solution through local / embedded small LM.
    - we need CUDA working on this machine otherwise we'll not be able to test
```

```text
fix(compiler): resolve memory leaks on dynamic execution evaluation loops
```
