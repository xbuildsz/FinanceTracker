# 💰 FinanceTracker — Android App

A **Personal Finance & Budget Tracker** built with **Java** and **Firebase** (Auth, Firestore, Storage).

---

## ✨ Features

- 🔐 **Authentication** — Email/password login & registration
- 💸 **Transactions** — Add, edit, delete income & expense transactions
- 🧾 **Receipt Upload** — Attach photos to any transaction via Firebase Storage
- 📂 **Budget Categories** — Set monthly budgets per category with live progress
- 📊 **Dashboard** — Pie chart overview, balance summary, income vs expense
- 🔄 **Real-time sync** — All data updates live via Firestore listeners

---

## 🗂 Project Structure

```
app/src/main/
├── java/com/financetracker/
│   ├── activities/
│   │   ├── SplashActivity.java
│   │   ├── LoginActivity.java
│   │   ├── RegisterActivity.java
│   │   ├── MainActivity.java
│   │   ├── AddTransactionActivity.java
│   │   └── TransactionDetailActivity.java
│   ├── adapters/
│   │   ├── TransactionAdapter.java
│   │   └── BudgetAdapter.java
│   ├── fragments/
│   │   ├── DashboardFragment.java
│   │   ├── TransactionsFragment.java
│   │   └── BudgetFragment.java
│   ├── models/
│   │   ├── Transaction.java
│   │   └── Budget.java
│   └── utils/
│       ├── FirebaseHelper.java
│       └── Constants.java
└── res/
    ├── layout/         (all XML layouts)
    ├── drawable/       (vectors + shape drawables)
    ├── values/         (colors, strings, themes, dimens)
    ├── menu/           (bottom nav + toolbar menus)
    ├── xml/            (file_paths for FileProvider)
    └── font/           (Poppins via Google Fonts)
```

---

## 🚀 Setup Instructions

### Step 1 — Create Firebase Project

1. Go to [console.firebase.google.com](https://console.firebase.google.com)
2. Click **Add project** → name it `FinanceTracker`
3. Disable Google Analytics (optional) → **Create project**

---

### Step 2 — Add Android App to Firebase

1. Click the **Android** icon on the project overview page
2. Enter package name: `com.financetracker`
3. Enter app nickname: `FinanceTracker`
4. Click **Register app**
5. **Download** `google-services.json`
6. Place it at: `FinanceTracker/app/google-services.json`
7. Skip the remaining Firebase SDK steps (already in `build.gradle`)

---

### Step 3 — Enable Authentication

1. In Firebase Console → **Authentication** → **Get started**
2. Click **Sign-in method** tab
3. Enable **Email/Password** → toggle ON → **Save**

---

### Step 4 — Set Up Firestore Database

1. Firebase Console → **Firestore Database** → **Create database**
2. Choose **Start in test mode** (we'll add security rules later)
3. Pick a region close to your users → **Enable**

#### Create Composite Indexes

Go to **Firestore → Indexes → Composite** and add:

| Collection | Fields | Order |
|---|---|---|
| `transactions` | `userId` ASC, `date` DESC | — |
| `transactions` | `userId` ASC, `type` ASC, `date` DESC | — |
| `budgets` | `userId` ASC, `month` ASC, `year` ASC | — |

> **Tip:** The app will print clickable links in Logcat when index creation is needed — just tap them!

#### Apply Security Rules

Go to **Firestore → Rules** and paste the content from `firestore.rules`:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /transactions/{transactionId} {
      allow read, write: if request.auth != null
        && request.auth.uid == resource.data.userId;
      allow create: if request.auth != null
        && request.auth.uid == request.resource.data.userId;
    }
    match /budgets/{budgetId} {
      allow read, write: if request.auth != null
        && request.auth.uid == resource.data.userId;
      allow create: if request.auth != null
        && request.auth.uid == request.resource.data.userId;
    }
  }
}
```

---

### Step 5 — Set Up Firebase Storage

1. Firebase Console → **Storage** → **Get started**
2. Start in test mode → select region → **Done**

#### Apply Storage Security Rules

Go to **Storage → Rules** and paste from `storage.rules`:

```
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /receipts/{userId}/{allPaths=**} {
      allow read, write: if request.auth != null
        && request.auth.uid == userId;
    }
  }
}
```

---

### Step 6 — Build & Run

1. Open the project in **Android Studio Hedgehog** or later
2. Ensure `google-services.json` is in the `app/` folder
3. Click **Sync Project with Gradle Files**
4. Select a device/emulator (API 24+)
5. Click ▶ **Run**

---

## 🎨 UI Overview

| Screen | Description |
|---|---|
| **Splash** | Auto-routes to Login or Dashboard |
| **Login / Register** | Clean card-based auth screens |
| **Dashboard** | Balance hero card + pie chart |
| **Transactions** | Filterable list with emoji categories |
| **Add/Edit Transaction** | Full form with receipt image picker |
| **Transaction Detail** | Full view with edit/delete + receipt display |
| **Budget** | Monthly budget cards with progress bars |

---

## 📦 Key Dependencies

| Library | Purpose |
|---|---|
| Firebase Auth | User authentication |
| Firebase Firestore | Real-time database |
| Firebase Storage | Receipt image storage |
| MPAndroidChart | Pie chart on Dashboard |
| Glide | Image loading for receipts |
| Material Components | UI components |

---

## 🔧 Troubleshooting

**Build fails with `google-services.json` error:**
→ Make sure the file is placed at `app/google-services.json`, not the root

**Firestore queries fail with "requires an index":**
→ Tap the link in Logcat to auto-create the index in Firebase Console

**Image upload fails:**
→ Check Storage rules are published and that `READ_MEDIA_IMAGES` permission is granted on device

**App crashes on Android 12 (API 32):**
→ Ensure `android:exported` is set on all activities in `AndroidManifest.xml` (already done)

---

## 📝 License

MIT License — free to use and modify.
