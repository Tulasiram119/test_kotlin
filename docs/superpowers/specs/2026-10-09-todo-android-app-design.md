# Design Specification: Kotlin Android Todo Application

- **Date**: 2026-10-09
- **Platform**: Android (Kotlin, Jetpack Compose, Material 3, Room SQLite)
- **Status**: Approved by User

---

## 1. Executive Summary

This document specifies the technical design and architectural blueprint for a modern, production-ready Android Todo application built entirely with Kotlin. The application follows Google's recommended Modern Android Architecture (MVVM + Unidirectional Data Flow), leveraging Jetpack Compose for declarative UI, Material 3 design system, and Android Jetpack Room for persistent local SQLite storage.

---

## 2. Goals and Non-Goals

### Goals
- Fully functional Kotlin Android application managing tasks with CRUD operations (Create, Read, Update, Delete).
- Rich task metadata: Title, optional description, priority levels (`LOW`, `MEDIUM`, `HIGH`), completion status, and creation timestamps.
- Unidirectional Data Flow (UDF) using Kotlin Coroutines and `StateFlow`.
- Real-time reactivity via Room database `Flow` streams.
- Filter tasks by status (`All`, `Active`, `Completed`) and instantaneous search query matching.
- User experience enhancements: Checkbox toggle animations, delete with undo Snackbar, clean Material 3 theme.
- Standalone, self-contained Gradle project setup (`gradlew`, `libs.versions.toml`, `build.gradle.kts`) with full test coverage for ViewModel and Repository layers.

### Non-Goals
- Remote backend cloud synchronization or user accounts/auth (focus is local-first offline storage).
- Complex recurring reminders/alarms with `WorkManager` (can be layered on in future iterations).

---

## 3. Architecture & Project Structure

The project follows clean layer separation:

```
app/
├── src/
│   ├── main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/example/todo/
│   │   │   ├── data/
│   │   │   │   ├── local/
│   │   │   │   │   ├── PriorityConverters.kt
│   │   │   │   │   ├── TodoDao.kt
│   │   │   │   │   ├── TodoDatabase.kt
│   │   │   │   │   └── TodoEntity.kt
│   │   │   │   └── repository/
│   │   │   │       ├── TodoRepository.kt
│   │   │   │       └── TodoRepositoryImpl.kt
│   │   │   ├── domain/
│   │   │   │   └── model/
│   │   │   │       ├── Priority.kt
│   │   │   │       └── TodoItem.kt
│   │   │   ├── ui/
│   │   │   │   ├── components/
│   │   │   │   │   ├── AddTodoBottomSheet.kt
│   │   │   │   │   ├── FilterChipRow.kt
│   │   │   │   │   ├── PriorityBadge.kt
│   │   │   │   │   ├── TodoEmptyState.kt
│   │   │   │   │   └── TodoItemCard.kt
│   │   │   │   ├── screens/
│   │   │   │   │   ├── TodoListScreen.kt
│   │   │   │   │   └── TodoUiState.kt
│   │   │   │   ├── theme/
│   │   │   │   │   ├── Color.kt
│   │   │   │   │   ├── Theme.kt
│   │   │   │   │   └── Type.kt
│   │   │   │   ├── viewmodel/
│   │   │   │   │   └── TodoViewModel.kt
│   │   │   │   └── MainActivity.kt
│   │   └── res/
│   │       ├── values/
│   │       │   ├── strings.xml
│   │       │   └── themes.xml
│   └── test/
│       └── java/com/example/todo/
│           ├── data/
│           │   └── FakeTodoRepository.kt
│           └── ui/
│               └── TodoViewModelTest.kt
├── build.gradle.kts
└── proguard-rules.pro
```

---

## 4. Component Specifications

### 4.1 Domain & Data Models
- **`Priority`**:
  - Values: `LOW` (weight 1), `MEDIUM` (weight 2), `HIGH` (weight 3).
  - Utility helpers for display label and color representations.
- **`TodoEntity`**:
  - Table name: `todos`.
  - Columns:
    - `id: Long` (`@PrimaryKey(autoGenerate = true)`)
    - `title: String`
    - `description: String`
    - `priority: Priority` (persisted via `PriorityConverters` as enum string)
    - `isCompleted: Boolean`
    - `createdAt: Long`
- **`TodoItem`**:
  - Domain model matching `TodoEntity`, mapped cleanly through extension functions (`TodoEntity.toDomain()`, `TodoItem.toEntity()`).

### 4.2 Room DAO & Database
- **`TodoDao`**:
  - `@Query("SELECT * FROM todos ORDER BY createdAt DESC") fun getAllTodos(): Flow<List<TodoEntity>>`
  - `@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(todo: TodoEntity): Long`
  - `@Update suspend fun update(todo: TodoEntity)`
  - `@Delete suspend fun delete(todo: TodoEntity)`
  - `@Query("DELETE FROM todos WHERE isCompleted = 1") suspend fun deleteCompleted()`
- **`TodoDatabase`**:
  - `@Database(entities = [TodoEntity::class], version = 1, exportSchema = false)`
  - Abstract class extending `RoomDatabase`, singleton instance provider with `Room.databaseBuilder`.

### 4.3 Repository Layer
- **`TodoRepository`** interface:
  - `fun getTodos(): Flow<List<TodoItem>>`
  - `suspend fun addTodo(item: TodoItem): Long`
  - `suspend fun updateTodo(item: TodoItem)`
  - `suspend fun deleteTodo(item: TodoItem)`
  - `suspend fun toggleComplete(item: TodoItem)`
  - `suspend fun clearCompleted()`
- **`TodoRepositoryImpl`**:
  - Implements `TodoRepository` backed by `TodoDao` running IO operations on `Dispatchers.IO`.

### 4.4 Presentation Layer (MVVM & Compose)
- **`TodoViewModel`**:
  - Manages UI state with `StateFlow<TodoUiState>`.
  - Combines `repository.getTodos()`, `_selectedFilter`, and `_searchQuery` using reactive Flow operators (`combine`).
  - Computes counts: `allCount`, `activeCount`, `completedCount`.
  - Handles user interactions:
    - `onFilterSelected(filter: TodoFilter)`
    - `onSearchQueryChanged(query: String)`
    - `onToggleComplete(item: TodoItem)`
    - `onAddTodo(title: String, description: String, priority: Priority)`
    - `onDeleteTodo(item: TodoItem)`
    - `onUndoDelete(item: TodoItem)`
    - `onClearCompleted()`
    - `setAddSheetVisible(visible: Boolean)`
- **UI Screens & Widgets**:
  - `TodoListScreen`: Contains Material 3 `Scaffold`, TopAppBar with "Clear completed" menu action, search text field, filter chip row, LazyColumn of `TodoItemCard`, and FloatingActionButton.
  - `TodoItemCard`: Animated completion toggle (strikethrough text), priority chip, dismiss/delete action button.
  - `AddTodoBottomSheet`: Modal bottom sheet containing Title input (validated non-empty), Description input, Priority selector segments, and Save button.
  - `TodoEmptyState`: Illustrated placeholder when filter or search yields 0 items.

---

## 5. Error Handling & Edge Cases

1. **Empty / Whitespace Titles**: The "Save" button is disabled or triggers an error indicator if title is blank.
2. **Concurrent Modifications**: SQLite handles ACID transactions; Room `Flow` automatically dispatches the updated state to Compose.
3. **Accidental Deletions**: Instant Snackbar message with "Undo" action restores the deleted task before it is dismissed.

---

## 6. Testing Strategy

1. **Unit Testing**:
   - `FakeTodoRepository`: In-memory implementation of `TodoRepository` for rapid, deterministic testing without Android framework dependencies.
   - `TodoViewModelTest`: Validates initial state, filtering (`ALL`, `ACTIVE`, `COMPLETED`), search query filtering, adding tasks, toggling completion, and delete/undo logic.
2. **Deterministic Build Verification**:
   - Project configuration is verified via Gradle compilation (`./gradlew assembleDebug` or `./gradlew testDebugUnitTest`).

---

## 7. Build Configuration

- **Root Gradle**: `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`.
- **Version Catalog**: `gradle/libs.versions.toml` defining:
  - AGP (Android Gradle Plugin 8.5.2)
  - Kotlin 2.0.20 (or compatible standard)
  - AndroidX Core KTX, Lifecycle ViewModel Compose, Activity Compose
  - Jetpack Compose BOM 2024.09.00 / Compose Material3
  - Room 2.6.1 + KSP
  - JUnit 4, Kotlinx Coroutines Test, Turbine (optional flow testing)
- **Android Target**: compileSdk 34/35, minSdk 24, targetSdk 34/35.
