# User Access System

A small, dependency-free Java console application demonstrating **role-based access control** and **subscription-based entitlements**. Users are created as either Free or Premium accounts, free accounts get a limited trial, and access to content is gated by a shared policy that checks account state before granting access.

Built as a learning exercise in the Java language fundamentals: **inheritance**, **abstract classes**, **polymorphism**, the **factory pattern**, and **enum-driven state machines**.

![Java](https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![Dependencies](https://img.shields.io/badge/Dependencies-1%20jar-6DB33F?style=for-the-badge)

---

## Table of Contents

- [Overview](#overview)
- [How It Works](#how-it-works)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Architecture](#architecture)
- [The Access Policy](#the-access-policy)
- [Getting Started](#getting-started)
- [Running the Application](#running-the-application)
- [Expected Output](#expected-output)
- [Configuration](#configuration)
- [Roadmap](#roadmap)
- [Known Issues](#known-issues)
- [License](#license)

---

## Overview

The system models a subscription-based content service with two account tiers:

| Tier | Behaviour |
|---|---|
| **Free** | Starts with a limited trial. Each content access consumes one trial credit. When the trial runs out, the user is prompted to upgrade. |
| **Premium** | Unlimited access, granted through a global entitlement flag. |

The core policy lives in one place — `User.checkAccess()` — and is inherited by both tiers. Each subclass decides *when* to invoke it and how to reach it, which is the point of the exercise: the shared decision lives in the base class, the tier-specific behaviour lives in the subclass.

### Design at a glance

```
UserFactory.createUser("FREE", ...)      ──►  new FreeUser(...)
UserFactory.createUser("PREMIUM", ...)   ──►  new PremiumUser(...)

FreeUser.accessContent()  ──►  updateStatus()  ──►  checkAccess()   [shared policy]
PremiumUser.accessContent()  ────────────────►  checkAccess()     [shared policy]
```

---

## Features

- **Two account tiers** via a single abstract base class
- **Abstract method** `accessContent()` — every tier must implement its own entry logic
- **Shared access policy** — `checkAccess()` in the base class handles lock and expiry
- **Enum-driven state machine** — `SubscriptionStatus` with five states
- **Trial accounting** — free accounts decrement a credit counter per access
- **Interactive upgrade flow** — prompts on the console and swaps the account in place
- **Factory pattern** — `UserFactory` is the single place that constructs users
- **Global configuration** — `GlobalSettings` holds tunable trial counts and entitlement flags
- **In-memory user store** — `LinkedHashMap` keyed by user ID, preserving insertion order

---

## Tech Stack

| | |
|---|---|
| **Language** | Java 25 |
| **Build** | Maven (`pom.xml`) |
| **Dependencies** | `org.jetbrains:annotations:24.0.0` — used only for the `@NotNull` annotation |
| **Data store** | In-memory `LinkedHashMap` |
| **UI** | Console (`System.out` / `Scanner`) |

There is no framework, no ORM, and no external service. The entire application is **7 source files**.

---

## Project Structure

```text
UserAccessSytem-main/
├── pom.xml
├── .gitignore
├── README.md                                # this file
└── src/main/java/org/example/
    ├── Main.java                            # Entry point and demo scenario
    ├── UserFactory.java                     # Factory — constructs Free / Premium users
    ├── globalsettings/
    │   └── GlobalSettings.java              # Tunable trial count + premium entitlement
    ├── model/
    │   ├── User.java                        # Abstract base — shared state + access policy
    │   ├── FreeUser.java                    # Trial logic + upgrade flow
    │   └── PremiumUser.java                 # Unlimited access logic
    └── service/
        └── FreeUserServices.java            # In-memory user registry
```

### Package roles

| Package | Responsibility |
|---|---|
| `org.example` | Entry point and the factory that wires objects together |
| `org.example.model` | The domain — `User` hierarchy and the `SubscriptionStatus` enum |
| `org.example.service` | Persistence boundary — user registry and lookup |
| `org.example.globalsettings` | Cross-cutting configuration flags |

---

## Architecture

### Class diagram

```
                    ┌──────────────────────────────┐
                    │          User  (abstract)     │
                    ├──────────────────────────────┤
                    │ - name : String               │
                    │ - age : int                   │
                    │ - userID : long               │
                    │ - status : SubscriptionStatus │
                    │ - isLocked : boolean          │
                    ├──────────────────────────────┤
                    │ + accessContent() : abstract  │
                    │ + checkAccess()               │
                    │ + getters / setters           │
                    └───────────┬──────────────────┘
                                │  extends
                ┌───────────────┴───────────────┐
                ▼                               ▼
    ┌────────────────────┐          ┌────────────────────┐
    │     FreeUser       │          │    PremiumUser     │
    ├────────────────────┤          ├────────────────────┤
    │ - remainingTrial   │          │ - premiumAccess    │
    │ - services         │          │                    │
    ├────────────────────┤          ├────────────────────┤
    │ + updateStatus()   │          │ + accessContent()  │
    │ + upgradePremium() │          │                    │
    │ + accessContent()  │          │                    │
    └────────────────────┘          └────────────────────┘
```

### The state machine

```
                   ┌──────────┐
                   │   NONE   │  (initial state, set in the User constructor)
                   └────┬─────┘
                        │ trial credits remaining > 0
                        ▼
                   ┌──────────┐
        ┌──────────│  TRIAL   │──────────┐
        │          └────┬─────┘          │
        │               │ credits == 0   │ upgrade confirmed
        │               ▼                │
        │          ┌──────────┐          │
        │          │ EXPIRED  │          │
        │          └──────────┘          │
        │                                ▼
        │                         ┌──────────┐
        └─────────────────────────│  ACTIVE  │
                                  └──────────┘
```

`LOCKED` is the fifth enum constant. It is defined but currently never assigned — see [Known Issues](#known-issues).

---

## The Access Policy

`User.checkAccess()` is the single gate. Every access path in the app routes through it:

```java
public void checkAccess() {
    if (isLocked()) {
        System.out.println("account locked unable to access content");
        return;
    }

    switch (status) {
        case EXPIRED, NONE -> System.out.println("Account expired: please upgrade");
        default -> System.out.println("Accessing content");
    }
}
```

The policy is deliberately small — lock takes precedence over everything, then subscription status decides. Because it lives in the abstract base class, adding a third tier later means implementing `accessContent()` and nothing else.

### Tier behaviour

**`FreeUser.accessContent()`** — refreshes the status from the trial counter, then acts on it:

| Status | Action |
|---|---|
| `TRIAL` | Grant access, then decrement `remainingTrial` |
| `ACTIVE` | Print the trial-finished notice, then offer the upgrade |
| anything else | Print "Unknown status, check account settings" |

**`PremiumUser.accessContent()`** — checks the entitlement flag, then delegates to the shared policy:

| Entitlement | Action |
|---|---|
| Granted | Print the account details, then call `checkAccess()` |
| Denied | Print "limited access: upgrade or renew sub to get premium access" |

---

## Getting Started

### Prerequisites

| Requirement | Version | Notes |
|---|---|---|
| **JDK** | **25 or newer** | Required — see the note below |
| **Maven** | 3.6+ | Or use the `mvn` bundled with IntelliJ IDEA |

> **Why JDK 25?** `Main` declares its entry point as `static void main()` — no `public` modifier and no `String[] args`. That form was finalized in **Java 25** under [JEP 512](https://openjdk.org/jeps/512) (*Compact Source Files and Instance Main Methods*), which relaxed the traditional `public static void main(String[] args)` requirement. It will not run on JDK 24 or earlier.

The `pom.xml` already reflects this:

```xml
<maven.compiler.source>25</maven.compiler.source>
<maven.compiler.target>25</maven.compiler.target>
```

### Clone and build

```bash
git clone <your-repo-url>
cd UserAccessSytem-main
mvn clean compile
```

### Run

```bash
mvn exec:java -Dexec.mainClass=org.example.Main
```

The application prompts for input at the upgrade step. Type `yes` to upgrade or anything else to decline.

---

## Running the Application

Three verified ways to run it:

### Option 1 — Maven exec (simplest)

```bash
mvn exec:java -Dexec.mainClass=org.example.Main
```

> `exec-maven-plugin` is not declared in `pom.xml`, so Maven downloads it on first use. Add it to the POM if you want this to work offline.

### Option 2 — Compile, then run with an explicit classpath

```bash
mvn clean compile
java -cp target/classes org.example.Main
```

The JetBrains annotations JAR is **not** needed at runtime — `@NotNull` has `CLASS` retention and is only read by the compiler.

### Option 3 — Package and run the JAR

```bash
mvn clean package
java -cp target/UserAccessSystrem-1.0-SNAPSHOT.jar org.example.Main
```

> The JAR is **not** executable via `java -jar` — the POM does not configure a `Main-Class` manifest entry, so that command fails with `no main manifest attribute`. Use the `-cp` form above, or add a manifest entry (see [Roadmap](#roadmap)).

### Input

| Prompt | Accepts |
|---|---|
| `Upgrade to premium (yes/no)` | `yes` (case-insensitive) to upgrade, anything else to decline |

---

## Expected Output

**Declining the upgrade** (`echo no | mvn exec:java -Dexec.mainClass=org.example.Main`):

```text
Accessing content
Accessing content
Accessing content
Accessing free trail content for user: majie
Free trial finished: please upgrade to premium to continue


=======upgrade to Premium user==========(yes/No)
Upgrade to premium (yes/no)
Access blocked until you upgrade
```

**Accepting the upgrade** (`echo yes | ...`):

```text
Accessing content
Accessing content
Accessing content
Accessing free trail content for user: majie
Free trial finished: please upgrade to premium to continue


=======upgrade to Premium user==========(yes/No)
Upgrade to premium (yes/no)
Account upgrade to premium
userId: 1 name: majie age: 20
Accessing content
```

The first three `Accessing content` lines are the three trial credits. The fourth call exhausts the trial and triggers the upgrade prompt. On acceptance, the account is replaced with a `PremiumUser` and access is granted.

---

## Configuration

### `GlobalSettings`

Central tunables for the whole application:

| Field | Default | Purpose |
|---|---|---|
| `TRIAL_COUNT_FREE_USER` | `3` | Baseline number of free trial credits |
| `FREE_USER_TRIAL_UPDATE` | `3` | Live value — override with `setFreeUserTrialUpdate(int)` |
| `PREMIUM_HAS_Access` | `true` | Baseline premium entitlement |
| `hasAccess_Flag` | `true` | Live value — override with `setHasAccess_Flag(boolean)` |

Change the trial length at startup, before any user is created:

```java
GlobalSettings.setFreeUserTrialUpdate(5);
GlobalSettings.setHasAccess_Flag(false);   // revoke premium entitlement
```

### `Main.java` — the demo scenario

```java
GlobalSettings.setFreeUserTrialUpdate(3);
FreeUserServices services = new FreeUserServices();

User user1 = services.getOrCreateNewUser("FREE", "majie", 20, 001);

if (user1 instanceof FreeUser freeUser) {
    freeUser.setServices(services);
}

user1.accessContent();   // × 4 — the fourth triggers the upgrade prompt
```

`FreeUserServices.getOrCreateNewUser()` returns the existing user if the ID is already registered, otherwise it creates one through the factory. `FreeUser` needs a reference to the service to swap itself out during an upgrade, which is why the `instanceof` block exists.

### `UserFactory`

```java
public static User createUser(String type, String name, int age, long userID) {
    if (type.equalsIgnoreCase("FREE")) {
        return new FreeUser(name, age, userID);
    } else if (type.equalsIgnoreCase("PREMIUM")) {
        return new PremiumUser(name, age, userID);
    } else {
        throw new IllegalArgumentException("Invalid user type");
    }
}
```

Type matching is case-insensitive. An unknown type throws `IllegalArgumentException`.

---

## Roadmap

- [ ] Fix the trial-exhaustion status transition (`ACTIVE` → `EXPIRED`)
- [ ] Fix the `20,001` argument bug in `Main` — should be `20_001`
- [ ] Add a `Main-Class` manifest entry so `java -jar` works
- [ ] Add JUnit 5 tests — the POM declares no test dependency and there is no `src/test` directory
- [ ] Add `exec-maven-plugin` to the POM so `mvn exec:java` works offline
- [ ] Add `mvn wrapper` so the project builds without a system Maven install
- [ ] Replace the static `GlobalSettings` with injected configuration
- [ ] Make `PremiumUser.premiumAccess` read the entitlement flag live instead of snapshotting it at construction
- [ ] Add a `toString()` to `User` for cleaner logging
- [ ] Support multiple users and a follower-style user listing
- [ ] Persist users to a file or database

---

## Known Issues

These are real defects in the current code. They are listed here rather than silently fixed, because the changes alter observable behaviour and belong to the project owner.

### 1. The trial-exhaustion status is inverted

`FreeUser.updateStatus()` assigns `ACTIVE` when the trial runs out:

```java
public void updateStatus() {
    if (remainingTrial > 0) {
        setStatus(SubscriptionStatus.TRIAL);
    } else {
        setStatus(SubscriptionStatus.ACTIVE);   // should be EXPIRED
    }
}
```

A free user whose trial has run out should become `EXPIRED`, not `ACTIVE`. As written, the `EXPIRED` branch of `checkAccess()` — the "Account expired: please upgrade" message — is **unreachable for free users**, since `FreeUser` never assigns it. The `ACTIVE` branch then prints the contradictory pair *"Accessing free trail content"* followed by *"Free trial finished"*.

**Fix:** change the `else` branch to `setStatus(SubscriptionStatus.EXPIRED);` and update the `accessContent()` branches to match.

### 2. `20,001` is two arguments, not one number

```java
User user1 = services.getOrCreateNewUser("FREE", "majie", 20,001);
```

Java does not support comma digit separators — `20,001` parses as the two literals `20` and `001`. The call therefore passes `age = 20` and `userID = 001`, and because `001` is octal, the user ID becomes **1**, not 20001. The upgrade output confirms it: `userId: 1 name: majie age: 20`.

**Fix:** use an underscore separator — `20_001` — or write the literal as `20001`.

### 3. `LOCKED` status and `isLocked` are never set

`SubscriptionStatus.LOCKED` is declared but never assigned. The `isLocked` boolean has a getter and setter but is only ever set to `false` in the constructor. The "account locked unable to access content" branch of `checkAccess()` is therefore dead code.

### 4. `PremiumUser.premiumAccess` snapshots the flag at construction

```java
boolean premiumAccess = GlobalSettings.isHasAccess_Flag();
```

This copies the value at construction time. Calling `GlobalSettings.setHasAccess_Flag(false)` later does not affect any already-created `PremiumUser`. Reading the flag inside `accessContent()` instead would make the setting live.

### 5. `FreeUserServices.replaceUser()` returns the old user

```java
public User replaceUser(long userID, User newUser) {
    if (userMap.containsKey(userID)) {
        return userMap.put(userID, newUser);   // Map.put returns the PREVIOUS value
    }
    ...
}
```

`Map.put` returns the value it replaced, so this returns the *old* user, not the new one. `FreeUser.upgradePremium()` ignores the return value and returns the premium user directly, so the bug is currently harmless — but the method's contract is wrong and will bite anyone who relies on it.

### 6. `Scanner` is never closed

`FreeUser.upgradePremium()` creates `new Scanner(System.in)` on every call and never closes it. It also re-reads the same `System.in` stream each time the method is invoked.

### 7. The `EXPIRED`/`NONE` branch is unreachable via `PremiumUser`

`PremiumUser.accessContent()` calls `checkAccess()` only when `premiumAccess` is true, and it sets the status to `ACTIVE` beforehand — so the expiry message never appears there either.

### 8. Cosmetic issues

- `artifactId` in `pom.xml` is `UserAccessSystrem` — a typo for "System". The JAR file name inherits it.
- The project directory is named `UserAccessSytem` (also missing the "s"), while the package and code use the correct spelling.
- `maven.compiler.source`/`target` are used instead of `maven.compiler.release`. The build emits a warning recommending `--release 25`, which is the safer option since it also pins the API surface.
- No Maven wrapper (`mvnw`) is included, despite `.gitignore` referencing `.mvn/wrapper/maven-wrapper.jar`.
- No `src/test` directory and no JUnit dependency.

---

## License

Released under the [MIT License](LICENSE).

---

## Author

Built by **Anuoluwapo Majekobaje Emmanuel** — CS undergraduate at Nanchang University.
