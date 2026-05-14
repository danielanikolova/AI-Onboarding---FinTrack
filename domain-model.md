# FinTrack — Domain Model

## Entity Relationship Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                         USER SYSTEM                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌──────────────┐         ┌──────────────┐                       │
│  │    USER      │◄────────┤   ACCOUNT    │                       │
│  ├──────────────┤ (1:N)   ├──────────────┤                       │
│  │ • id (PK)    │         │ • id (PK)    │                       │
│  │ • email      │         │ • user_id(FK)│                       │
│  │ • password   │         │ • name       │                       │
│  │ • created_at │         │ • type       │                       │
│  │ • updated_at │         │ • currency   │                       │
│  └──────────────┘         │ • initial_bal│                       │
│         ▲                 │ • created_at │                       │
│         │                 │ • updated_at │                       │
│         │                 └──────┬───────┘                       │
│         │                        │                               │
│         │                        │ (1:N)                         │
│         │                        ▼                               │
│         │              ┌──────────────────┐                      │
│         │              │   TRANSACTION    │                      │
│         │              ├──────────────────┤                      │
│         │              │ • id (PK)        │                      │
│         │              │ • account_id(FK) │                      │
│         │              │ • category_id(FK)│                      │
│         │              │ • amount         │                      │
│         │              │ • description    │                      │
│         │              │ • transaction_dt │                      │
│         │              │ • created_at     │                      │
│         │              │ • updated_at     │                      │
│         │              └──────────┬───────┘                      │
│         │                         │                              │
│         │                         │ (N:1)                        │
│         │                         ▼                              │
│         │              ┌──────────────────┐                      │
│         │              │   CATEGORY       │                      │
│         │              ├──────────────────┤                      │
│         │              │ • id (PK)        │                      │
│         │              │ • name (UNIQUE)  │                      │
│         │              │ • type           │                      │
│         │              │ • description    │                      │
│         │              │ • created_at     │                      │
│         │              │ • updated_at     │                      │
│         │              │ • deleted_at     │                      │
│         │              └──────────────────┘                      │
│         │                                                        │
│         │              ┌──────────────────┐                      │
│         └──────────────┤    BUDGET        │                      │
│         (1:N)          ├──────────────────┤                      │
│                        │ • id (PK)        │                      │
│                        │ • user_id (FK)   │                      │
│                        │ • category_id(FK)│                      │
│                        │ • amount         │                      │
│                        │ • start_date     │                      │
│                        │ • end_date       │                      │
│                        │ • created_at     │                      │
│                        │ • updated_at     │                      │
│                        └──────────────────┘                      │
│                                                                   │
└─────────────────────────────────────────────────────────────────┘
```

---

## Entity Relationships

| Relationship | Type | Description |
|---|---|---|
| USER → ACCOUNT | 1:N | A user owns multiple accounts |
| ACCOUNT → TRANSACTION | 1:N | An account has many transactions |
| TRANSACTION → CATEGORY | N:1 | Many transactions share one category |
| USER → BUDGET | 1:N | A user sets multiple budgets |
| BUDGET → CATEGORY | N:1 | A budget targets one category |

---

## Database Schema

### users
| Column | Type | Constraints |
|---|---|---|
| id | UUID | PK |
| email | VARCHAR | UNIQUE, NOT NULL |
| password_hash | VARCHAR | NOT NULL |
| created_at | TIMESTAMPTZ | NOT NULL |
| updated_at | TIMESTAMPTZ | NOT NULL |

### accounts
| Column | Type | Constraints |
|---|---|---|
| id | UUID | PK |
| user_id | UUID | FK → users.id |
| name | VARCHAR | NOT NULL |
| type | account_type | ENUM: SAVINGS, CHECKING, CREDIT_CARD |
| currency | VARCHAR(3) | e.g. USD, EUR |
| initial_balance | DECIMAL | NOT NULL |
| created_at | TIMESTAMPTZ | NOT NULL |
| updated_at | TIMESTAMPTZ | NOT NULL |

### transactions
| Column | Type | Constraints |
|---|---|---|
| id | UUID | PK |
| account_id | UUID | FK → accounts.id |
| category_id | UUID | FK → categories.id |
| amount | DECIMAL | NOT NULL |
| description | TEXT | nullable |
| transaction_date | TIMESTAMPTZ | NOT NULL |
| created_at | TIMESTAMPTZ | NOT NULL |
| updated_at | TIMESTAMPTZ | NOT NULL |

### categories
| Column | Type | Constraints |
|---|---|---|
| id | UUID | PK |
| name | VARCHAR | UNIQUE, NOT NULL |
| type | category_type | ENUM: INCOME, EXPENSE |
| description | TEXT | nullable |
| created_at | TIMESTAMPTZ | NOT NULL |
| updated_at | TIMESTAMPTZ | NOT NULL |
| deleted_at | TIMESTAMPTZ | nullable — soft delete |

### budgets
| Column | Type | Constraints |
|---|---|---|
| id | UUID | PK |
| user_id | UUID | FK → users.id |
| category_id | UUID | FK → categories.id |
| amount | DECIMAL | NOT NULL |
| start_date | DATE | NOT NULL |
| end_date | DATE | NOT NULL |
| created_at | TIMESTAMPTZ | NOT NULL |
| updated_at | TIMESTAMPTZ | NOT NULL |

---

## Design Notes

- All primary keys are **UUID** — avoids sequential ID enumeration
- Timestamps use **TIMESTAMPTZ** (timezone-aware) throughout
- **Soft delete** is applied only to `categories` via `deleted_at` (nullable timestamp); other entities use hard delete
- `account_type` enum: `SAVINGS`, `CHECKING`, `CREDIT_CARD`
- `category_type` enum: `INCOME`, `EXPENSE`
- `transactions` has no `type` field — type is derived from the linked `category.type`
- `budgets` uses a date range (`start_date` / `end_date`) rather than a period enum, giving flexible budget windows
