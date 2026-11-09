# 1. Use Kotlin Multiplatform for shared logic with native UIs

- Status: Accepted
- Date: 2026-11-09
- Deciders: Firdous Shahani

## Context

Expensa is an offline-first travel and expense app that must ship on **both
Android and iOS**. The interesting, bug-prone parts of the app are not the
screens — they are the **business logic, local persistence, the offline-sync
engine, and the AI routing**. Those rules must behave *identically* on both
platforms; a conflict-resolution or money-rounding bug that differs between
Android and iOS would be a correctness disaster.

As a solo developer building this over ~12 weeks, I also cannot afford to write
and maintain that logic twice. At the same time, this project is a portfolio
piece aimed at senior mobile roles, so the choice has to stand up to scrutiny in
an interview — including *why not Flutter*.

The core tension: **maximise code reuse of the hard logic** without giving up a
**native, platform-correct UX** and without locking myself out of
platform-specific capabilities (CameraX, on-device AI, Keychain/Keystore).

## Decision

Use **Kotlin Multiplatform (KMP)** with this split:

- A shared Kotlin module (`:core` + `:app:shared`) holds the domain model,
  repositories, SQLDelight persistence, the sync engine, and AI routing. Written
  once, compiled to every target.
- **Native UI per platform**: Jetpack Compose on Android, SwiftUI on iOS. The
  iOS app consumes the shared code as a framework.
- **Compose Multiplatform** is kept available for *optionally* sharing one or two
  screens later (a stretch goal), not as the primary UI strategy.
- The same `:core` is reused by the Ktor **backend**, so wire types and
  validation rules can't drift between client and server.

## Alternatives considered

- **Flutter** — excellent single-codebase UI and fast iteration, but it means
  committing to Dart and a non-native widget layer, awkward access to the exact
  platform APIs this app needs (CameraX, Gemini Nano / Apple Foundation Models),
  and it doesn't let the backend share code. It also wouldn't demonstrate the
  native-interop skills these roles screen for.
- **React Native** — similar single-codebase appeal, JS/TS ecosystem, but the
  same native-API and shared-backend drawbacks, and a weaker story for the
  compute-heavy logic (sync, AI routing) I want to showcase.
- **Two fully native codebases (Kotlin + Swift)** — best possible UX and tooling,
  but doubles the logic I most need to keep identical and is the most expensive
  to maintain solo. Defeats the "write the hard part once" goal.

## Consequences

**Positive**

- The logic that must be identical across platforms is written and tested once.
- UIs stay fully native, so UX and platform-API access are unconstrained.
- The backend shares the domain model with the apps — one source of truth.
- Demonstrates senior-level skills interviewers probe: KMP, `expect`/`actual`,
  and Kotlin/Native → Swift interop.

**Negative / costs**

- **Slower iOS builds.** Kotlin/Native compiles via LLVM to a native binary;
  CI already shows iOS jobs taking much longer than Android, so the iOS workflow
  is path-filtered and cache-reliant. (See `.github/workflows/ios-build.yml`.)
- **Smaller ecosystem than Flutter/RN.** Some libraries need `expect`/`actual`
  shims or have no multiplatform equivalent.
- **Two UI layers to maintain** (Compose + SwiftUI) — the deliberate price of a
  native UX. Mitigated by keeping the apps thin and pushing all state/logic into
  the shared module.
- **iOS interop friction** (e.g. suspend functions, Flows) — mitigated with
  SKIE / Swift export where needed.

## Follow-ups

- SKIE vs. plain Swift export for a clean Swift API — to be decided in a later ADR.
- Local storage engine (SQLDelight) and sync strategy — separate ADRs.
