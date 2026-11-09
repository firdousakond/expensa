# CLAUDE.md

Project rules for AI coding agents (and humans) working in Expensa. Read this
before making changes. When the AI gets something wrong, fix the code **and**
add a rule here so it doesn't recur.

## What Expensa is

An offline-first travel & expense app on Kotlin Multiplatform. All business
logic, sync, and AI routing live once in shared Kotlin; the apps are thin UI
layers on top. See `docs/project_plan.md` for the full plan.

## Module boundaries (do not cross these)

```
:core          Pure domain: models, repository interfaces, use cases. No UI,
               no Android/iOS imports, no Compose. Reused by apps AND backend.
:app:shared    Shared app layer: Compose UI, ViewModels, repository impls,
               AI routing, sync. Depends on :core.
:app:androidApp / :desktopApp / :webApp   Thin entry points. Call App(); hold
               no business logic.
:app:iosApp    Xcode project; consumes the Shared framework.
:server        Ktor BFF. Reuses :core models. Holds all API keys.
```

Rules:
- Domain logic and business rules go in `:core`, never in a platform app or a
  ViewModel.
- Platform-specific code uses Kotlin `expect`/`actual`, declared in
  `commonMain` with `actual`s per target source set.
- The apps never talk to a model provider directly. On-device models run
  locally; everything else goes through `:server`, which holds the keys.
- Package root is `com.firdous.expensa`.

## Money, data, and correctness

- **Never use `Double`/`Float` for money.** Store amounts as `Long` minor units
  (cents) plus a currency code. Add a currency only to amounts of the same
  currency.
- Use `kotlinx-datetime` (`LocalDate`) for dates — don't hand-roll date logic.
- Prefer typed IDs (`TripId`, `ExpenseId`) over raw `String`.
- The allowed expense categories are a fixed enum in `:core` — it is the single
  source of truth, also used by the AI schema check.

## Testing

Strong tests in `:core` where the logic lives; a thin layer of UI/e2e on top.
Test-drive new domain logic (write the failing test first).

Commands:
- Core tests (fast loop, JVM):  `./gradlew :core:jvmTest`
- Core tests (all targets):     `./gradlew :core:allTests`
- Shared desktop tests:         `./gradlew :app:shared:jvmTest`
- Shared Android host tests:    `./gradlew :app:shared:testAndroidHostTest`
- Shared iOS sim tests:         `./gradlew :app:shared:iosSimulatorArm64Test`
- Server tests:                 `./gradlew :server:test`

Run a single class while iterating with `--tests "com.firdous.expensa.XxxTest"`.
Run the full suite once before opening a PR.

## AI feature rules

Every AI request goes through one shared `AiRouter`: on-device first, cloud
fallback via `:server`. The router enforces:
- **Privacy:** cloud calls send only extracted text, never the photo, after
  redacting card numbers and emails. Cloud AI is user-toggleable.
- **Keys:** the app never holds a model API key. The backend does.
- **Safety:** receipt text is untrusted input. Prompts extract fields only; a
  JSON schema + allowed-category check rejects anything else (prompt-injection
  defence). Bad output falls back to manual entry.
- **Offline:** with no network and no on-device model, save the expense
  unprocessed and queue it.

## Git & workflow

- **Conventional Commits:** `feat(scope): ...`, `fix(scope): ...`,
  `chore: ...`, `ci: ...`, `docs: ...`. CI enforces the format.
- Commit type by change: `feat`/`fix` for app code, `chore` for repo/config,
  `ci` for workflows, `docs` for documentation. If the subject needs "and",
  it's probably two commits.
- Trunk-based: branch from `main` (`feat/12-...`), open a PR with the template,
  pass checks, squash-merge. Keep branches short.
- Never commit secrets, keystores, API keys, or `local.properties`.

## Conventions

- Coroutines + Flow for async; expose `Flow` from repositories for observation.
- Koin for DI (when introduced).
- kotlinx.serialization for JSON; share wire types via `:core`.
- Keep platform apps thin — new surface area lands behind `App()` in
  `:app:shared`.
