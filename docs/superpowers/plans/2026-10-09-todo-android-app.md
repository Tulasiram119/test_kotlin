# Kotlin Android Todo Application Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [x]`) syntax for tracking.

**Goal:** Build a complete, production-grade Android Todo application in Kotlin using Jetpack Compose (Material 3), Room SQLite persistence, and MVVM Unidirectional Data Flow.

**Architecture:** Modern Android MVVM Clean Architecture. The app uses Room for local SQLite caching, exposes reactive `Flow` streams through a Repository, which are transformed into a single `TodoUiState` `StateFlow` by `TodoViewModel` and consumed by Jetpack Compose UI components.

**Tech Stack:** Kotlin 2.0 / Android SDK 34-35 / Jetpack Compose BOM & Material 3 / Room 2.6 / Kotlinx Coroutines & Flow / JUnit 4 & Coroutines Test.

**Spec:** [docs/superpowers/specs/2026-10-09-todo-android-app-design.md](file:///home/hst000267/Downloads/personal_projects/test_kotlin/docs/superpowers/specs/2026-10-09-todo-android-app-design.md)

## Global Constraints

- Language: Kotlin 100%
- UI: Jetpack Compose + Material 3 (no XML layout inflation for UI)
- Local Database: Android Jetpack Room 2.6+ with KSP
- Target SDK: 35, Compile SDK: 35, Min SDK: 24
- State Management: Unidirectional Data Flow with `StateFlow` in `TodoViewModel`
- No third-party network or sync libraries (offline-first local persistence)
- Follow exact package naming: `com.example.todo`

---

### Task 1: Gradle Build System & Android Project Scaffolding

**Files:**
- Create: `settings.gradle.kts`
- Create: `build.gradle.kts`
- Create: `gradle.properties`
- Create: `gradle/libs.versions.toml`
- Create: `gradle/wrapper/gradle-wrapper.properties`
- Create: `app/build.gradle.kts`
- Create: `app/proguard-rules.pro`
- Create: `app/src/main/AndroidManifest.xml`
- Create: `app/src/main/res/values/strings.xml`
- Create: `app/src/main/res/values/themes.xml`

**Interfaces:**
- Produces: Complete Gradle and Android application module structure configured for Kotlin, Jetpack Compose, Room, and KSP.

- [x] **Step 1: Create Version Catalog (`gradle/libs.versions.toml`)**

```toml
[versions]
agp = "8.5.2"
kotlin = "2.0.20"
ksp = "2.0.20-1.0.25"
coreKtx = "1.13.1"
lifecycleRuntimeKtx = "2.8.6"
activityCompose = "1.9.2"
composeBom = "2024.09.02"
room = "2.6.1"
coroutines = "1.8.1"
junit = "4.13.2"
coroutinesTest = "1.8.1"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleRuntimeKtx" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }

androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-compose-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }
androidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }

androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }

kotlinx-coroutines-core = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version.ref = "coroutines" }
kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }

junit = { group = "junit", name = "junit", version.ref = "junit" }
kotlinx-coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "coroutinesTest" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

- [x] **Step 2: Create root Gradle files (`settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`)**

In `settings.gradle.kts`:
```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "TodoApp"
include(":app")
```

In `build.gradle.kts`:
```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
}
```

In `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
```

In `gradle/wrapper/gradle-wrapper.properties`:
```properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.9-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

- [x] **Step 3: Create `app/build.gradle.kts`, `app/proguard-rules.pro`, and Android Manifest & resources**

In `app/build.gradle.kts`:
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.todo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.todo"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
```

In `app/src/main/AndroidManifest.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <application
        android:name=".TodoApplication"
        android:allowBackup="true"
        android:icon="@android:drawable/ic_menu_agenda"
        android:label="@string/app_name"
        android:roundIcon="@android:drawable/ic_menu_agenda"
        android:supportsRtl="true"
        android:theme="@style/Theme.TodoApp">
        <activity
            android:name=".ui.MainActivity"
            android:exported="true"
            android:windowSoftInputMode="adjustResize"
            android:theme="@style/Theme.TodoApp">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

In `app/src/main/res/values/strings.xml`:
```xml
<resources>
    <string name="app_name">Tasks Todo</string>
</resources>
```

In `app/src/main/res/values/themes.xml`:
```xml
<resources>
    <style name="Theme.TodoApp" parent="android:Theme.Material.Light.NoActionBar" />
</resources>
```

In `app/proguard-rules.pro`:
```
# Proguard rules for TodoApp
```

- [x] **Step 4: Commit build scaffolding**

```bash
git add settings.gradle.kts build.gradle.kts gradle.properties gradle/ app/
git commit -m "build: scaffold Android project with Gradle, Jetpack Compose, Room, and KSP"
```

---

### Task 2: Domain Layer Models & Priority Enum

**Files:**
- Create: `app/src/main/java/com/example/todo/domain/model/Priority.kt`
- Create: `app/src/main/java/com/example/todo/domain/model/TodoItem.kt`
- Create: `app/src/test/java/com/example/todo/domain/PriorityTest.kt`

**Interfaces:**
- Produces: `enum class Priority { LOW, MEDIUM, HIGH }` and `data class TodoItem(id: Long, title: String, description: String, priority: Priority, isCompleted: Boolean, createdAt: Long)`

- [x] **Step 1: Write the failing unit test for Priority & TodoItem**

Create `app/src/test/java/com/example/todo/domain/PriorityTest.kt`:
```kotlin
package com.example.todo.domain

import com.example.todo.domain.model.Priority
import com.example.todo.domain.model.TodoItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PriorityTest {

    @Test
    fun testPriorityOrderAndWeights() {
        assertTrue(Priority.HIGH.weight > Priority.MEDIUM.weight)
        assertTrue(Priority.MEDIUM.weight > Priority.LOW.weight)
        assertEquals("High", Priority.HIGH.displayName)
        assertEquals("Medium", Priority.MEDIUM.displayName)
        assertEquals("Low", Priority.LOW.displayName)
    }

    @Test
    fun testTodoItemDefaultValues() {
        val item = TodoItem(
            id = 1L,
            title = "Buy groceries",
            description = "Milk and eggs",
            priority = Priority.HIGH,
            isCompleted = false,
            createdAt = 1000L
        )
        assertEquals(1L, item.id)
        assertEquals("Buy groceries", item.title)
        assertFalse(item.isCompleted)
        assertEquals(Priority.HIGH, item.priority)
    }
}
```

- [x] **Step 2: Implement `Priority.kt` and `TodoItem.kt`**

Create `app/src/main/java/com/example/todo/domain/model/Priority.kt`:
```kotlin
package com.example.todo.domain.model

enum class Priority(val displayName: String, val weight: Int) {
    LOW("Low", 1),
    MEDIUM("Medium", 2),
    HIGH("High", 3);

    companion object {
        fun fromString(value: String): Priority {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: MEDIUM
        }
    }
}
```

Create `app/src/main/java/com/example/todo/domain/model/TodoItem.kt`:
```kotlin
package com.example.todo.domain.model

data class TodoItem(
    val id: Long = 0L,
    val title: String,
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
```

- [x] **Step 3: Run the unit test to verify it passes**

Commit domain models:
```bash
git add app/src/main/java/com/example/todo/domain/ app/src/test/java/com/example/todo/domain/
git commit -m "feat: add Priority enum and TodoItem domain model with unit tests"
```

---

### Task 3: Room Database Entities, DAO, Converters & Database Singleton

**Files:**
- Create: `app/src/main/java/com/example/todo/data/local/PriorityConverters.kt`
- Create: `app/src/main/java/com/example/todo/data/local/TodoEntity.kt`
- Create: `app/src/main/java/com/example/todo/data/local/TodoDao.kt`
- Create: `app/src/main/java/com/example/todo/data/local/TodoDatabase.kt`
- Create: `app/src/main/java/com/example/todo/data/mapper/TodoMapper.kt`
- Create: `app/src/test/java/com/example/todo/data/TodoMapperTest.kt`

**Interfaces:**
- Consumes: `Priority`, `TodoItem`
- Produces: `TodoEntity`, `TodoDao`, `TodoDatabase`, `TodoEntity.toDomain(): TodoItem`, `TodoItem.toEntity(): TodoEntity`

- [x] **Step 1: Write the failing unit test for TodoMapper & PriorityConverters**

Create `app/src/test/java/com/example/todo/data/TodoMapperTest.kt`:
```kotlin
package com.example.todo.data

import com.example.todo.data.local.PriorityConverters
import com.example.todo.data.local.TodoEntity
import com.example.todo.data.mapper.toDomain
import com.example.todo.data.mapper.toEntity
import com.example.todo.domain.model.Priority
import com.example.todo.domain.model.TodoItem
import org.junit.Assert.assertEquals
import org.junit.Test

class TodoMapperTest {

    private val converter = PriorityConverters()

    @Test
    fun testPriorityConverters() {
        val stringValue = converter.fromPriority(Priority.HIGH)
        assertEquals("HIGH", stringValue)
        val enumValue = converter.toPriority("HIGH")
        assertEquals(Priority.HIGH, enumValue)
        val defaultFallback = converter.toPriority("INVALID")
        assertEquals(Priority.MEDIUM, defaultFallback)
    }

    @Test
    fun testMappingEntityToDomainAndBack() {
        val entity = TodoEntity(
            id = 42L,
            title = "Test Task",
            description = "Some description",
            priority = Priority.HIGH,
            isCompleted = true,
            createdAt = 123456789L
        )

        val domain = entity.toDomain()
        assertEquals(entity.id, domain.id)
        assertEquals(entity.title, domain.title)
        assertEquals(entity.description, domain.description)
        assertEquals(entity.priority, domain.priority)
        assertEquals(entity.isCompleted, domain.isCompleted)
        assertEquals(entity.createdAt, domain.createdAt)

        val mappedBack = domain.toEntity()
        assertEquals(entity, mappedBack)
    }
}
```

- [x] **Step 2: Implement Converters, Entity, DAO, Database & Mapper**

Create `app/src/main/java/com/example/todo/data/local/PriorityConverters.kt`:
```kotlin
package com.example.todo.data.local

import androidx.room.TypeConverter
import com.example.todo.domain.model.Priority

class PriorityConverters {
    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(value: String): Priority = Priority.fromString(value)
}
```

Create `app/src/main/java/com/example/todo/data/local/TodoEntity.kt`:
```kotlin
package com.example.todo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todo.domain.model.Priority

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
```

Create `app/src/main/java/com/example/todo/data/local/TodoDao.kt`:
```kotlin
package com.example.todo.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos ORDER BY createdAt DESC")
    fun getAllTodos(): Flow<List<TodoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: TodoEntity): Long

    @Update
    suspend fun update(todo: TodoEntity)

    @Delete
    suspend fun delete(todo: TodoEntity)

    @Query("DELETE FROM todos WHERE isCompleted = 1")
    suspend fun deleteCompleted()

    @Query("SELECT * FROM todos WHERE id = :id")
    suspend fun getTodoById(id: Long): TodoEntity?
}
```

Create `app/src/main/java/com/example/todo/data/local/TodoDatabase.kt`:
```kotlin
package com.example.todo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [TodoEntity::class], version = 1, exportSchema = false)
@TypeConverters(PriorityConverters::class)
abstract class TodoDatabase : RoomDatabase() {
    abstract val todoDao: TodoDao

    companion object {
        const val DATABASE_NAME = "todos_db"

        @Volatile
        private var INSTANCE: TodoDatabase? = null

        fun getInstance(context: Context): TodoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TodoDatabase::class.java,
                    DATABASE_NAME
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
```

Create `app/src/main/java/com/example/todo/data/mapper/TodoMapper.kt`:
```kotlin
package com.example.todo.data.mapper

import com.example.todo.data.local.TodoEntity
import com.example.todo.domain.model.TodoItem

fun TodoEntity.toDomain(): TodoItem = TodoItem(
    id = id,
    title = title,
    description = description,
    priority = priority,
    isCompleted = isCompleted,
    createdAt = createdAt
)

fun TodoItem.toEntity(): TodoEntity = TodoEntity(
    id = id,
    title = title,
    description = description,
    priority = priority,
    isCompleted = isCompleted,
    createdAt = createdAt
)
```

- [x] **Step 3: Run the unit test to verify mapping logic**

- [x] **Step 4: Commit Room persistence components**

```bash
git add app/src/main/java/com/example/todo/data/ app/src/test/java/com/example/todo/data/
git commit -m "feat: add Room DAO, Entity, Database, and Entity-to-Domain mapper"
```

---

### Task 4: Repository Layer (`TodoRepository` & `TodoRepositoryImpl`)

**Files:**
- Create: `app/src/main/java/com/example/todo/data/repository/TodoRepository.kt`
- Create: `app/src/main/java/com/example/todo/data/repository/TodoRepositoryImpl.kt`
- Create: `app/src/test/java/com/example/todo/data/FakeTodoDao.kt`
- Create: `app/src/test/java/com/example/todo/data/TodoRepositoryTest.kt`

**Interfaces:**
- Consumes: `TodoDao`, `TodoItem`, `TodoEntity`, `toDomain()`, `toEntity()`
- Produces: `TodoRepository` interface with `getTodos(): Flow<List<TodoItem>>`, `addTodo(item: TodoItem): Long`, `updateTodo(item: TodoItem)`, `deleteTodo(item: TodoItem)`, `toggleComplete(item: TodoItem)`, `clearCompleted()`

- [x] **Step 1: Write `FakeTodoDao.kt` and failing repository unit test**

Create `app/src/test/java/com/example/todo/data/FakeTodoDao.kt`:
```kotlin
package com.example.todo.data

import com.example.todo.data.local.TodoDao
import com.example.todo.data.local.TodoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeTodoDao : TodoDao {
    private val todosMap = mutableMapOf<Long, TodoEntity>()
    private val todosFlow = MutableStateFlow<List<TodoEntity>>(emptyList())
    private var nextId = 1L

    private fun updateFlow() {
        todosFlow.value = todosMap.values.sortedByDescending { it.createdAt }
    }

    override fun getAllTodos(): Flow<List<TodoEntity>> = todosFlow

    override suspend fun insert(todo: TodoEntity): Long {
        val id = if (todo.id == 0L) nextId++ else todo.id
        val entity = todo.copy(id = id)
        todosMap[id] = entity
        updateFlow()
        return id
    }

    override suspend fun update(todo: TodoEntity) {
        todosMap[todo.id] = todo
        updateFlow()
    }

    override suspend fun delete(todo: TodoEntity) {
        todosMap.remove(todo.id)
        updateFlow()
    }

    override suspend fun deleteCompleted() {
        val toRemove = todosMap.filter { it.value.isCompleted }.keys
        toRemove.forEach { todosMap.remove(it) }
        updateFlow()
    }

    override suspend fun getTodoById(id: Long): TodoEntity? {
        return todosMap[id]
    }
}
```

Create `app/src/test/java/com/example/todo/data/TodoRepositoryTest.kt`:
```kotlin
package com.example.todo.data

import com.example.todo.data.repository.TodoRepositoryImpl
import com.example.todo.domain.model.Priority
import com.example.todo.domain.model.TodoItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TodoRepositoryTest {

    private lateinit var fakeDao: FakeTodoDao
    private lateinit var repository: TodoRepositoryImpl

    @Before
    fun setUp() {
        fakeDao = FakeTodoDao()
        repository = TodoRepositoryImpl(fakeDao)
    }

    @Test
    fun testAddAndGetTodos() = runTest {
        val item = TodoItem(title = "Task 1", priority = Priority.HIGH)
        val id = repository.addTodo(item)
        val list = repository.getTodos().first()

        assertEquals(1, list.size)
        assertEquals(id, list[0].id)
        assertEquals("Task 1", list[0].title)
    }

    @Test
    fun testToggleComplete() = runTest {
        val item = TodoItem(title = "Task 1", isCompleted = false)
        val id = repository.addTodo(item)
        val saved = repository.getTodos().first().first { it.id == id }

        repository.toggleComplete(saved)
        val updated = repository.getTodos().first().first { it.id == id }
        assertTrue(updated.isCompleted)

        repository.toggleComplete(updated)
        val toggledBack = repository.getTodos().first().first { it.id == id }
        assertFalse(toggledBack.isCompleted)
    }

    @Test
    fun testDeleteAndClearCompleted() = runTest {
        val id1 = repository.addTodo(TodoItem(title = "Task 1", isCompleted = true))
        val id2 = repository.addTodo(TodoItem(title = "Task 2", isCompleted = false))

        repository.clearCompleted()
        val remaining = repository.getTodos().first()

        assertEquals(1, remaining.size)
        assertEquals(id2, remaining[0].id)
    }
}
```

- [x] **Step 2: Implement `TodoRepository.kt` and `TodoRepositoryImpl.kt`**

Create `app/src/main/java/com/example/todo/data/repository/TodoRepository.kt`:
```kotlin
package com.example.todo.data.repository

import com.example.todo.domain.model.TodoItem
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun getTodos(): Flow<List<TodoItem>>
    suspend fun addTodo(item: TodoItem): Long
    suspend fun updateTodo(item: TodoItem)
    suspend fun deleteTodo(item: TodoItem)
    suspend fun toggleComplete(item: TodoItem)
    suspend fun clearCompleted()
}
```

Create `app/src/main/java/com/example/todo/data/repository/TodoRepositoryImpl.kt`:
```kotlin
package com.example.todo.data.repository

import com.example.todo.data.local.TodoDao
import com.example.todo.data.mapper.toDomain
import com.example.todo.data.mapper.toEntity
import com.example.todo.domain.model.TodoItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class TodoRepositoryImpl(
    private val todoDao: TodoDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TodoRepository {

    override fun getTodos(): Flow<List<TodoItem>> {
        return todoDao.getAllTodos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addTodo(item: TodoItem): Long = withContext(ioDispatcher) {
        todoDao.insert(item.toEntity())
    }

    override suspend fun updateTodo(item: TodoItem) = withContext(ioDispatcher) {
        todoDao.update(item.toEntity())
    }

    override suspend fun deleteTodo(item: TodoItem) = withContext(ioDispatcher) {
        todoDao.delete(item.toEntity())
    }

    override suspend fun toggleComplete(item: TodoItem) = withContext(ioDispatcher) {
        val updated = item.copy(isCompleted = !item.isCompleted)
        todoDao.update(updated.toEntity())
    }

    override suspend fun clearCompleted() = withContext(ioDispatcher) {
        todoDao.deleteCompleted()
    }
}
```

- [x] **Step 3: Run repository test to verify passes**

- [x] **Step 4: Commit repository layer**

```bash
git add app/src/main/java/com/example/todo/data/repository/ app/src/test/java/com/example/todo/data/
git commit -m "feat: implement TodoRepository with Flow streaming and unit tests"
```

---

### Task 5: ViewModel, UI State & Filtering Layer

**Files:**
- Create: `app/src/main/java/com/example/todo/ui/screens/TodoUiState.kt`
- Create: `app/src/main/java/com/example/todo/ui/viewmodel/TodoViewModel.kt`
- Create: `app/src/test/java/com/example/todo/data/FakeTodoRepository.kt`
- Create: `app/src/test/java/com/example/todo/ui/TodoViewModelTest.kt`

**Interfaces:**
- Consumes: `TodoRepository`, `TodoItem`, `Priority`
- Produces: `TodoUiState`, `TodoFilter` (`ALL`, `ACTIVE`, `COMPLETED`), `TodoViewModel` with `uiState: StateFlow<TodoUiState>` and interaction methods.

- [x] **Step 1: Write `FakeTodoRepository.kt` and `TodoViewModelTest.kt`**

Create `app/src/test/java/com/example/todo/data/FakeTodoRepository.kt`:
```kotlin
package com.example.todo.data

import com.example.todo.data.repository.TodoRepository
import com.example.todo.domain.model.TodoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeTodoRepository : TodoRepository {
    private val todosMap = mutableMapOf<Long, TodoItem>()
    private val _todosFlow = MutableStateFlow<List<TodoItem>>(emptyList())
    private var nextId = 1L

    private fun refresh() {
        _todosFlow.value = todosMap.values.sortedByDescending { it.createdAt }
    }

    override fun getTodos(): Flow<List<TodoItem>> = _todosFlow.asStateFlow()

    override suspend fun addTodo(item: TodoItem): Long {
        val id = if (item.id == 0L) nextId++ else item.id
        val newItem = item.copy(id = id)
        todosMap[id] = newItem
        refresh()
        return id
    }

    override suspend fun updateTodo(item: TodoItem) {
        todosMap[item.id] = item
        refresh()
    }

    override suspend fun deleteTodo(item: TodoItem) {
        todosMap.remove(item.id)
        refresh()
    }

    override suspend fun toggleComplete(item: TodoItem) {
        val current = todosMap[item.id] ?: item
        todosMap[item.id] = current.copy(isCompleted = !current.isCompleted)
        refresh()
    }

    override suspend fun clearCompleted() {
        val toRemove = todosMap.filter { it.value.isCompleted }.keys
        toRemove.forEach { todosMap.remove(it) }
        refresh()
    }
}
```

Create `app/src/test/java/com/example/todo/ui/TodoViewModelTest.kt`:
```kotlin
package com.example.todo.ui

import com.example.todo.data.FakeTodoRepository
import com.example.todo.domain.model.Priority
import com.example.todo.domain.model.TodoItem
import com.example.todo.ui.screens.TodoFilter
import com.example.todo.ui.viewmodel.TodoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TodoViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeTodoRepository
    private lateinit var viewModel: TodoViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTodoRepository()
        viewModel = TodoViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState() = runTest {
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertEquals(0, state.items.size)
        assertEquals(TodoFilter.ALL, state.selectedFilter)
        assertEquals("", state.searchQuery)
        assertFalse(state.isAddSheetOpen)
    }

    @Test
    fun testAddTodoAndCounts() = runTest {
        advanceUntilIdle()
        viewModel.onAddTodo("Grocery shopping", "Buy fruits", Priority.HIGH)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.items.size)
        assertEquals(1, state.allCount)
        assertEquals(1, state.activeCount)
        assertEquals(0, state.completedCount)
        assertEquals("Grocery shopping", state.items[0].title)
    }

    @Test
    fun testToggleCompleteAndFilters() = runTest {
        advanceUntilIdle()
        viewModel.onAddTodo("Task 1", "", Priority.LOW)
        viewModel.onAddTodo("Task 2", "", Priority.MEDIUM)
        advanceUntilIdle()

        val task1 = viewModel.uiState.value.items.first { it.title == "Task 1" }
        viewModel.onToggleComplete(task1)
        advanceUntilIdle()

        var state = viewModel.uiState.value
        assertEquals(2, state.allCount)
        assertEquals(1, state.activeCount)
        assertEquals(1, state.completedCount)

        // Filter ACTIVE
        viewModel.onFilterSelected(TodoFilter.ACTIVE)
        advanceUntilIdle()
        state = viewModel.uiState.value
        assertEquals(1, state.items.size)
        assertEquals("Task 2", state.items[0].title)

        // Filter COMPLETED
        viewModel.onFilterSelected(TodoFilter.COMPLETED)
        advanceUntilIdle()
        state = viewModel.uiState.value
        assertEquals(1, state.items.size)
        assertEquals("Task 1", state.items[0].title)
    }

    @Test
    fun testSearchQueryFilter() = runTest {
        advanceUntilIdle()
        viewModel.onAddTodo("Learn Kotlin", "Basics of Kotlin", Priority.HIGH)
        viewModel.onAddTodo("Cook Dinner", "Pasta with sauce", Priority.LOW)
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("kotlin")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.items.size)
        assertEquals("Learn Kotlin", state.items[0].title)
    }

    @Test
    fun testDeleteAndUndo() = runTest {
        advanceUntilIdle()
        viewModel.onAddTodo("Task to delete", "", Priority.LOW)
        advanceUntilIdle()

        val item = viewModel.uiState.value.items[0]
        viewModel.onDeleteTodo(item)
        advanceUntilIdle()

        assertEquals(0, viewModel.uiState.value.items.size)

        viewModel.onUndoDelete(item)
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.items.size)
        assertEquals("Task to delete", viewModel.uiState.value.items[0].title)
    }
}
```

- [x] **Step 2: Implement `TodoUiState.kt` and `TodoViewModel.kt`**

Create `app/src/main/java/com/example/todo/ui/screens/TodoUiState.kt`:
```kotlin
package com.example.todo.ui.screens

import com.example.todo.domain.model.TodoItem

enum class TodoFilter(val label: String) {
    ALL("All"),
    ACTIVE("Active"),
    COMPLETED("Completed")
}

data class TodoUiState(
    val items: List<TodoItem> = emptyList(),
    val allCount: Int = 0,
    val activeCount: Int = 0,
    val completedCount: Int = 0,
    val selectedFilter: TodoFilter = TodoFilter.ALL,
    val searchQuery: String = "",
    val isAddSheetOpen: Boolean = false,
    val isLoading: Boolean = false,
    val recentlyDeletedItem: TodoItem? = null
)
```

Create `app/src/main/java/com/example/todo/ui/viewmodel/TodoViewModel.kt`:
```kotlin
package com.example.todo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todo.data.repository.TodoRepository
import com.example.todo.domain.model.Priority
import com.example.todo.domain.model.TodoItem
import com.example.todo.ui.screens.TodoFilter
import com.example.todo.ui.screens.TodoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoViewModel(
    private val repository: TodoRepository
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(TodoFilter.ALL)
    private val _searchQuery = MutableStateFlow("")
    private val _isAddSheetOpen = MutableStateFlow(false)
    private val _recentlyDeletedItem = MutableStateFlow<TodoItem?>(null)

    val uiState: StateFlow<TodoUiState> = combine(
        repository.getTodos(),
        _selectedFilter,
        _searchQuery,
        _isAddSheetOpen,
        _recentlyDeletedItem
    ) { allTodos, filter, query, isSheetOpen, deletedItem ->
        val trimmedQuery = query.trim().lowercase()

        val filteredBySearchAndStatus = allTodos.filter { item ->
            val matchesFilter = when (filter) {
                TodoFilter.ALL -> true
                TodoFilter.ACTIVE -> !item.isCompleted
                TodoFilter.COMPLETED -> item.isCompleted
            }
            val matchesQuery = if (trimmedQuery.isEmpty()) {
                true
            } else {
                item.title.lowercase().contains(trimmedQuery) ||
                        item.description.lowercase().contains(trimmedQuery)
            }
            matchesFilter && matchesQuery
        }

        TodoUiState(
            items = filteredBySearchAndStatus,
            allCount = allTodos.size,
            activeCount = allTodos.count { !it.isCompleted },
            completedCount = allTodos.count { it.isCompleted },
            selectedFilter = filter,
            searchQuery = query,
            isAddSheetOpen = isSheetOpen,
            isLoading = false,
            recentlyDeletedItem = deletedItem
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TodoUiState(isLoading = true)
    )

    fun onFilterSelected(filter: TodoFilter) {
        _selectedFilter.value = filter
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun setAddSheetVisible(visible: Boolean) {
        _isAddSheetOpen.value = visible
    }

    fun onAddTodo(title: String, description: String = "", priority: Priority = Priority.MEDIUM) {
        val trimmedTitle = title.trim()
        if (trimmedTitle.isEmpty()) return

        viewModelScope.launch {
            val newItem = TodoItem(
                title = trimmedTitle,
                description = description.trim(),
                priority = priority
            )
            repository.addTodo(newItem)
            _isAddSheetOpen.value = false
        }
    }

    fun onToggleComplete(item: TodoItem) {
        viewModelScope.launch {
            repository.toggleComplete(item)
        }
    }

    fun onDeleteTodo(item: TodoItem) {
        viewModelScope.launch {
            _recentlyDeletedItem.value = item
            repository.deleteTodo(item)
        }
    }

    fun onUndoDelete(item: TodoItem) {
        viewModelScope.launch {
            repository.addTodo(item)
            _recentlyDeletedItem.value = null
        }
    }

    fun onClearRecentlyDeleted() {
        _recentlyDeletedItem.value = null
    }

    fun onClearCompleted() {
        viewModelScope.launch {
            repository.clearCompleted()
        }
    }

    companion object {
        fun provideFactory(repository: TodoRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TodoViewModel(repository) as T
                }
            }
    }
}
```

- [x] **Step 3: Run the unit test to verify ViewModel logic**

- [x] **Step 4: Commit ViewModel and UI state layer**

```bash
git add app/src/main/java/com/example/todo/ui/ app/src/test/java/com/example/todo/ui/
git commit -m "feat: add TodoViewModel, TodoUiState, and comprehensive unit tests"
```

---

### Task 6: Material 3 Theme & UI Components

**Files:**
- Create: `app/src/main/java/com/example/todo/ui/theme/Color.kt`
- Create: `app/src/main/java/com/example/todo/ui/theme/Type.kt`
- Create: `app/src/main/java/com/example/todo/ui/theme/Theme.kt`
- Create: `app/src/main/java/com/example/todo/ui/components/PriorityBadge.kt`
- Create: `app/src/main/java/com/example/todo/ui/components/FilterChipRow.kt`
- Create: `app/src/main/java/com/example/todo/ui/components/TodoEmptyState.kt`
- Create: `app/src/main/java/com/example/todo/ui/components/TodoItemCard.kt`
- Create: `app/src/main/java/com/example/todo/ui/components/AddTodoBottomSheet.kt`

**Interfaces:**
- Consumes: `TodoItem`, `Priority`, `TodoFilter`, Material 3 Compose
- Produces: Visual components with animations, priority color coding, strike-through text on complete, and bottom sheet entry.

- [x] **Step 1: Implement Theme files (`Color.kt`, `Type.kt`, `Theme.kt`)**

Create `app/src/main/java/com/example/todo/ui/theme/Color.kt`:
```kotlin
package com.example.todo.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Priority Colors
val PriorityHighContainer = Color(0xFFFFDAD6)
val PriorityHighOnContainer = Color(0xFF93000A)
val PriorityMediumContainer = Color(0xFFFFE088)
val PriorityMediumOnContainer = Color(0xFF664400)
val PriorityLowContainer = Color(0xFFD7E3FF)
val PriorityLowOnContainer = Color(0xFF003062)
```

Create `app/src/main/java/com/example/todo/ui/theme/Type.kt`:
```kotlin
package com.example.todo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
```

Create `app/src/main/java/com/example/todo/ui/theme/Theme.kt`:
```kotlin
package com.example.todo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun TodoAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

- [x] **Step 2: Implement `PriorityBadge.kt`**

Create `app/src/main/java/com/example/todo/ui/components/PriorityBadge.kt`:
```kotlin
package com.example.todo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todo.domain.model.Priority
import com.example.todo.ui.theme.PriorityHighContainer
import com.example.todo.ui.theme.PriorityHighOnContainer
import com.example.todo.ui.theme.PriorityLowContainer
import com.example.todo.ui.theme.PriorityLowOnContainer
import com.example.todo.ui.theme.PriorityMediumContainer
import com.example.todo.ui.theme.PriorityMediumOnContainer

@Composable
fun PriorityBadge(priority: Priority, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (priority) {
        Priority.HIGH -> PriorityHighContainer to PriorityHighOnContainer
        Priority.MEDIUM -> PriorityMediumContainer to PriorityMediumOnContainer
        Priority.LOW -> PriorityLowContainer to PriorityLowOnContainer
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = priority.displayName,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
```

- [x] **Step 3: Implement `FilterChipRow.kt` and `TodoEmptyState.kt`**

Create `app/src/main/java/com/example/todo/ui/components/FilterChipRow.kt`:
```kotlin
package com.example.todo.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todo.ui.screens.TodoFilter

@Composable
fun FilterChipRow(
    selectedFilter: TodoFilter,
    allCount: Int,
    activeCount: Int,
    completedCount: Int,
    onFilterSelected: (TodoFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TodoFilter.entries.forEach { filter ->
            val count = when (filter) {
                TodoFilter.ALL -> allCount
                TodoFilter.ACTIVE -> activeCount
                TodoFilter.COMPLETED -> completedCount
            }
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text("${filter.label} ($count)") }
            )
        }
    }
}
```

Create `app/src/main/java/com/example/todo/ui/components/TodoEmptyState.kt`:
```kotlin
package com.example.todo.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todo.ui.screens.TodoFilter

@Composable
fun TodoEmptyState(
    filter: TodoFilter,
    isSearching: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val icon = if (isSearching) Icons.Outlined.SearchOff else Icons.Outlined.CheckCircle
        val message = when {
            isSearching -> "No tasks matched your search query"
            filter == TodoFilter.COMPLETED -> "No completed tasks yet. Finish a task to see it here!"
            filter == TodoFilter.ACTIVE -> "You're all caught up! No pending tasks."
            else -> "No tasks found. Tap '+' to create your first task!"
        }

        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
```

- [x] **Step 4: Implement `TodoItemCard.kt` and `AddTodoBottomSheet.kt`**

Create `app/src/main/java/com/example/todo/ui/components/TodoItemCard.kt`:
```kotlin
package com.example.todo.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.todo.domain.model.TodoItem

@Composable
fun TodoItemCard(
    item: TodoItem,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(
        targetValue = if (item.isCompleted) {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        label = "cardColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (item.isCompleted) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleComplete() }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isCompleted,
                onCheckedChange = { onToggleComplete() }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        color = if (item.isCompleted) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    PriorityBadge(priority = item.priority)
                }

                if (item.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = if (item.isCompleted) 0.5f else 0.8f
                        )
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete task",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
        }
    }
}
```

Create `app/src/main/java/com/example/todo/ui/components/AddTodoBottomSheet.kt`:
```kotlin
package com.example.todo.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todo.domain.model.Priority

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoBottomSheet(
    onDismiss: () -> Unit,
    onSave: (title: String, description: String, priority: Priority) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.MEDIUM) }
    var isTitleError by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Create New Task",
                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    if (it.isNotBlank()) isTitleError = false
                },
                label = { Text("Task Title *") },
                isError = isTitleError,
                supportingText = {
                    if (isTitleError) Text("Title cannot be empty")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Priority Level",
                style = androidx.compose.material3.MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Priority.entries.forEach { p ->
                    FilterChip(
                        selected = priority == p,
                        onClick = { priority = p },
                        label = { Text(p.displayName) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (title.isBlank()) {
                        isTitleError = true
                    } else {
                        onSave(title, description, priority)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Task")
            }
        }
    }
}
```

- [x] **Step 5: Commit UI components**

```bash
git add app/src/main/java/com/example/todo/ui/components/ app/src/main/java/com/example/todo/ui/theme/
git commit -m "feat: implement Material 3 theme and Compose components"
```

---

### Task 7: Screen Assembly, Application Singleton & MainActivity Integration

**Files:**
- Create: `app/src/main/java/com/example/todo/ui/screens/TodoListScreen.kt`
- Create: `app/src/main/java/com/example/todo/TodoApplication.kt`
- Create: `app/src/main/java/com/example/todo/ui/MainActivity.kt`

**Interfaces:**
- Consumes: `TodoViewModel`, `TodoUiState`, `TodoItemCard`, `FilterChipRow`, `TodoEmptyState`, `AddTodoBottomSheet`, `TodoDatabase`, `TodoRepositoryImpl`
- Produces: The full end-to-end runnable Android Todo app.

- [x] **Step 1: Implement `TodoListScreen.kt`**

Create `app/src/main/java/com/example/todo/ui/screens/TodoListScreen.kt`:
```kotlin
package com.example.todo.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todo.ui.components.AddTodoBottomSheet
import com.example.todo.ui.components.FilterChipRow
import com.example.todo.ui.components.TodoEmptyState
import com.example.todo.ui.components.TodoItemCard
import com.example.todo.ui.viewmodel.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    viewModel: TodoViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Undo delete snackbar handling
    val deletedItem = uiState.recentlyDeletedItem
    LaunchedEffect(deletedItem) {
        if (deletedItem != null) {
            val result = snackbarHostState.showSnackbar(
                message = "Deleted \"${deletedItem.title}\"",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.onUndoDelete(deletedItem)
            } else {
                viewModel.onClearRecentlyDeleted()
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("My Tasks", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    if (uiState.completedCount > 0) {
                        IconButton(onClick = { viewModel.onClearCompleted() }) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear completed tasks",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.setAddSheetVisible(true) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add new task",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = { Text("Search tasks...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Status Filter Chips
            FilterChipRow(
                selectedFilter = uiState.selectedFilter,
                allCount = uiState.allCount,
                activeCount = uiState.activeCount,
                completedCount = uiState.completedCount,
                onFilterSelected = { viewModel.onFilterSelected(it) }
            )

            // Task List or Empty State
            if (uiState.items.isEmpty()) {
                TodoEmptyState(
                    filter = uiState.selectedFilter,
                    isSearching = uiState.searchQuery.isNotBlank()
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(items = uiState.items, key = { it.id }) { item ->
                        TodoItemCard(
                            item = item,
                            onToggleComplete = { viewModel.onToggleComplete(item) },
                            onDelete = { viewModel.onDeleteTodo(item) }
                        )
                    }
                }
            }
        }

        // Add Task Modal Bottom Sheet
        if (uiState.isAddSheetOpen) {
            AddTodoBottomSheet(
                onDismiss = { viewModel.setAddSheetVisible(false) },
                onSave = { title, desc, priority ->
                    viewModel.onAddTodo(title, desc, priority)
                }
            )
        }
    }
}
```

- [x] **Step 2: Implement `TodoApplication.kt` and `MainActivity.kt`**

Create `app/src/main/java/com/example/todo/TodoApplication.kt`:
```kotlin
package com.example.todo

import android.app.Application
import com.example.todo.data.local.TodoDatabase
import com.example.todo.data.repository.TodoRepository
import com.example.todo.data.repository.TodoRepositoryImpl

class TodoApplication : Application() {
    val database: TodoDatabase by lazy {
        TodoDatabase.getInstance(this)
    }

    val repository: TodoRepository by lazy {
        TodoRepositoryImpl(database.todoDao)
    }
}
```

Create `app/src/main/java/com/example/todo/ui/MainActivity.kt`:
```kotlin
package com.example.todo.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.todo.TodoApplication
import com.example.todo.ui.screens.TodoListScreen
import com.example.todo.ui.theme.TodoAppTheme
import com.example.todo.ui.viewmodel.TodoViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TodoViewModel by viewModels {
        val app = application as TodoApplication
        TodoViewModel.provideFactory(app.repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoAppTheme {
                TodoListScreen(viewModel = viewModel)
            }
        }
    }
}
```

- [x] **Step 3: Commit completed integration**

```bash
git add app/src/main/java/com/example/todo/
git commit -m "feat: connect TodoListScreen, MainActivity, and TodoApplication"
```

- [x] **Step 4: Verify test suite and code compilation**

Run unit tests and verification steps to ensure code integrity across domain, data, and presentation layers.
