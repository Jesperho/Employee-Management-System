# Technical Changes and Refactoring Summary
Employee Management System (EMS) – Sprint 7

This document summarizes all major technical changes, refactoring work, architecture updates, and code cleanup performed during Sprint 7. These changes were necessary to improve maintainability, testability, performance, and code quality to meet the Sprint 7 acceptance criteria.

---

## 1. Code Cleanup and Standardization
- Removed over 200 PMD violations related to unused imports, dead code, and inconsistent naming.
- Standardized JavaFX `@FXML` handler visibility from `private` to `protected` to avoid PMD false positives.
- Cleaned up redundant UI helper methods and replaced them with centralized utilities.
- Eliminated fully qualified class names and replaced them with clean imports.
- Ensured consistent coding standards across all controllers.

---

## 2. MVC Refactoring Across All Major Modules
A strict Model–View–Controller separation was implemented across the system:

### View Layer (New)
Created dedicated `view/*.java` classes for:
- DashboardView
- EmployeeView
- DepartmentView
- LeaveView
- SalaryView
- Shared FormBuilder utility

These classes now handle:
- Popup dialogs
- Form construction
- Table action columns
- UI styling and layout
- Reusable form components

### Controller Layer (Cleaned)
Controllers now focus only on:
- Business logic
- Data loading
- Filtering
- Statistics calculations
- Navigation
- DAO interactions

This dramatically reduced controller file sizes (up to 55% smaller) and increased testability.

---

## 3. Architecture Updates
- Introduced a dedicated **View layer** in the architecture specification.
- Updated the system to prevent controllers from constructing JavaFX UI nodes directly.
- Implemented dependency injection for Views to avoid tight coupling.
- Updated `MariaDbConnection` to automatically switch between production and test databases, preventing accidental data loss during `mvn test`.
- Added callback patterns (`Runnable`, `Consumer`) to keep Views decoupled from Controllers.

---

## 4. Performance Improvements
- All heavy database operations (`loadFromDatabase()`, filtering, translations) were moved to background threads using `javafx.concurrent.Task`.
- UI no longer freezes during:
    - Table loading
    - Filtering
    - Department/Employee/Leave/Salary updates
- Buttons and navigation remain responsive even during large data operations.

---

## 5. Bug Fixes and Stability Improvements
### Major fixes included:
- Corrected failing unit tests caused by outdated constructors and missing getters.
- Replaced insecure `e.printStackTrace()` calls with safe alert dialogs.
- Fixed SonarQube security hotspot in password validation by replacing regex with a safe character-counting algorithm.
- Fixed NPEs in `LanguageManager` when translation keys were missing.
- Corrected filtering logic in `EmployeeController` for contract types.
- Updated reflection-based tests to match new controller structures.

### Test Suite Result:
- **328/328 tests passing**
- **Build Status: SUCCESS**

---

## 6. UI/UX Improvements
- Centralized form styling using `FormBuilder`.
- Standardized labels, text fields, and layout spacing across all modules.
- Improved popup consistency and readability.
- Reduced duplicated UI code across controllers.
- Ensured consistent behavior for action buttons (View, Edit, Delete).

---

## 7. SonarQube & Code Coverage Improvements
- Refactoring enabled controllers to be fully testable.
- UI-heavy code moved to View layer, excluded from coverage.
- DAO and controller tests expanded to cover:
    - Localization logic
    - Salary calculations
    - Leave duration logic
    - Filtering and statistics
- Coverage increased toward the required **A/B grade threshold**.

---

## 8. Summary
Sprint 7 introduced major improvements to the EMS codebase:
- Cleaner architecture
- Higher testability
- Better performance
- More maintainable controllers
- Centralized UI logic
- Stable CI/CD pipeline
- Improved SonarQube quality

These changes ensure the system is production-ready and aligned with the Product Owner’s expectations for final delivery.
