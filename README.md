# User Access System

A small Java console program that models how access to content is granted depending on the type of account a user has. Users are created as either Free or Premium accounts. Free accounts get a limited number of free accesses before being asked to upgrade, while Premium accounts get unlimited access. One shared piece of logic decides whether access is allowed, and each account type controls when that decision is asked for.

The project was built as a learning exercise in core Java: inheritance, abstract classes, polymorphism, the factory pattern, and using an enum to model a set of states.

![Java](https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)

---

## What It Does

Running the program creates a free account, then tries to access content several times in a row. Each attempt uses up one of the account's free trial credits. Once the credits run out, the program asks the user whether they want to upgrade.

If the user agrees, a Premium account is created to replace the free one, and access continues without a limit. If they decline, access stays blocked until they upgrade.

Accounts are tracked by ID in an in-memory store, so asking for the same ID again returns the existing account rather than creating a second one. During an upgrade, the account in the store is swapped for the new one, so the change is reflected everywhere that account is used.

A separate settings class holds the rules the whole system reads from — how many free trial credits a new account gets, and whether Premium access is currently granted. Changing those values changes how the program behaves.

---

## Features

- **Two account types** — Free and Premium, both built on a shared abstract base class
- **Shared access decision** — one method in the base class decides whether access is granted, based on whether the account is locked and what state its subscription is in
- **Trial credits** — free accounts count down their remaining free accesses
- **Interactive upgrade** — when the trial ends the user is asked whether to upgrade, and the account is replaced if they say yes
- **Account states** — an enum tracks trial, active, expired, and locked
- **Central construction** — a factory class is the only place accounts are created, keeping the type decisions in one spot
- **Central configuration** — trial counts and the Premium entitlement live in one settings class
- **Account registry** — accounts are kept in an ordered in-memory map keyed by ID

---

## How It's Built

The project is built around one abstract class, `User`, which holds everything both account types share: the person's details, the current subscription state, whether the account is locked, and the method that decides whether content can be accessed.

`FreeUser` and `PremiumUser` both extend it. Each one has its own `accessContent()` method, because each account type reaches the access decision differently — a free account has to check its trial credits first, while a Premium account checks its entitlement and goes straight to the decision.

That shared decision lives in `checkAccess()` on the base class. It looks at the account state and reports whether content can be reached. Because it is inherited rather than copied, adding a third account type later means writing one new class and nothing else.

Accounts are never constructed directly by the rest of the program. A factory class takes a type name and returns the right object, so the details of each account type stay contained.

---

## Tech Stack

| | |
|---|---|
| **Language** | Java 25 |
| **Build** | Maven |
| **Dependencies** | One — the JetBrains annotations library, used for a nullability marker |
| **Data store** | In-memory map |
| **Interface** | Console |

---

## Running the Project

Build with `mvn compile`, then run the `Main` class from `target/classes`. The program asks a question when the trial ends, so type `yes` to upgrade or anything else to decline.

The project needs **JDK 25 or newer**, because the entry point uses the relaxed `main` method form introduced in that release.

---

## License

Released under the [MIT License](LICENSE).

---

## Author

Built by **Anuoluwapo Majekobaje Emmanuel** — CS undergraduate at Nanchang University.
