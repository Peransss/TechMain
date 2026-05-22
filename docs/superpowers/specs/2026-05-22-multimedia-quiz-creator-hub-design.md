# Design Specification: Multimedia Quiz Creator Hub

**Date:** 2026-05-22
**Theme:** Neon Hacker (Dark/Navy, Cyan, Lime Green)
**Status:** Draft

## 1. Overview
The **Multimedia Quiz Creator Hub** transforms TechMain from a static quiz app into a dynamic, user-generated content platform. It allows all users to create, manage, and share educational quizzes that include image-based questions.

## 2. Core Features (CRUD)

### Create
*   **Studio Tab:** A new primary navigation tab for content creation.
*   **Creation Wizard:**
    *   **Step 1: Metadata:** Title, Category, and Description.
    *   **Step 2: Questions:** Add/Edit/Remove questions.
    *   **Step 3: Media:** Attach one image per question from the device gallery.
    *   **Step 4: Publish:** Save to Firebase Firestore and Storage.

### Read
*   **My Repository:** A list view in the Studio tab showing all quizzes created by the user.
*   **Stats Display:** View "Access Count" (plays) for each quiz.
*   **Global Discovery:** A "Daily Featured" carousel on the Battle Home screen for all users to discover top-tier community content.

### Update
*   **Repository Management:** Edit existing quizzes to fix typos, add more questions, or update images.
*   **Versioning:** Updates are pushed immediately to all users currently starting a new battle.

### Delete
*   **Purge:** Remove a quiz from the cloud. This deletes the Firestore document and all associated images in Firebase Storage to save space.

## 3. Multimedia Pipeline
*   **Selection:** Uses Android `ActivityResultContracts.GetContent`.
*   **Compression:** Local resizing to max 1024px before upload.
*   **Storage:** Firebase Storage path: `/media/quizzes/{userId}/{quizId}/{questionId}.jpg`.
*   **Delivery:** Coil (`AsyncImage`) for efficient, cached image loading in the game screen.

## 4. Technical Architecture

### Data Models
```kotlin
data class CustomQuiz(
    val id: String = "",
    val creatorId: String = "",
    val title: String = "",
    val categoryId: String = "",
    val questions: List<CustomQuestion> = emptyList(),
    val playCount: Int = 0,
    val isFeatured: Boolean = false,
    val createdAt: Long = 0L
)

data class CustomQuestion(
    val id: String = "",
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctAnswer: Int = 0,
    val imageUrl: String? = null
)
```

### UI Components (Neon Hacker Theme)
*   **Background:** `#0A0A0C` (Deep Navy/Black).
*   **Primary Accent:** `#00FFFF` (Cyan) for interactive headers and titles.
*   **Secondary Accent:** `#39FF14` (Lime Green) for stats and success states.
*   **Typography:** Monospaced fonts (e.g., `Courier New`) for a terminal-style look.
*   **Visuals:** Subtle glowing borders and scanline overlays on images.

### Backend Integration
*   **Firestore:** Collection `custom_quizzes` with indexes on `isFeatured` and `createdAt`.
*   **Storage:** Firebase Storage for all multimedia assets.
*   **Security:** Firebase Security Rules to ensure users can only `Update/Delete` their own content.

## 5. User Flow
1.  **Home:** User sees "⭐ PILIHAN HARI INI" (Daily Featured) at the top.
2.  **Studio:** User taps "+" to open the Creator Wizard.
3.  **Creation:** User picks a photo from their gallery, types a question, and hits "PUBLISH".
4.  **Battle:** User starts a battle; the question image glows in a Neon Hacker frame.

## 6. Success Criteria
*   Users can successfully upload and view images in a quiz.
*   The "Studio" tab accurately reflects the user's Firestore data.
*   The game correctly fetches and displays "Featured" community quizzes.
