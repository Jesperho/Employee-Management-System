# Final Testing and Bug Report
**Project:** Employee Management System  
**Phase:** Final Code Cleanup & CI/CD Verification

## Overview
This document tracks all critical bugs identified during the final testing phase of the application and their respective resolutions. The primary objective is to achieve a stable testing environment with no broken pipelines, passing all Unit Tests, and maintaining an A or B grade in SonarQube with >70% coverage.

## Bug Tracking Table

| ID | Issue Description | Root Cause | Resolution | Status |
|----|-------------------|------------|------------|--------|
| BUG-001 | Test Compilation Failures (`LeaveTranslationDaoTest`, etc.) | Constructors for entities like `Leave` and `Employee` were incorrectly instantiated in the tests with missing or mismatched parameters after the refactoring phase. | Updated test instantiation calls to perfectly match the current Entity constructors. Replaced missing getter methods (`getEmployee`) with standard `.stream().count()` assertions. | ✅ Resolved |
| BUG-002 | SonarQube Security Hotspot (`LoginController`) | The password validation logic used a regular expression `^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=_]).{8,20}$` which SonarQube flagged as vulnerable to ReDoS attacks. | Replaced the regex with a performant Java Streams `chars().filter().count()` approach to validate the password constraints programmatically. | ✅ Resolved |
| BUG-003 | SonarQube Reliability Warnings (`e.printStackTrace()`) | Exception blocks in controllers (`DashboardController`, `DepartmentController`) contained insecure `e.printStackTrace()` calls. | Removed all stack trace prints. Replaced them with the existing `showAlert()` dialog methodology to notify users securely and gracefully. | ✅ Resolved |
| BUG-004 | Headless CI/CD Unit Test NPEs (`LanguageManager`) | Unit tests for DAOs threw `NullPointerException` because the `ResourceBundle` translation dictionary couldn't load keys gracefully in the headless CI/CD environment when dummy strings were passed. | Implemented a null-safety check inside `LanguageManager.getTranslatedText()` to prevent NPEs when missing or null translation keys are queried during tests. | ✅ Resolved |
| BUG-005 | Reflection API Failures (`*ControllerTest.java`) | Changes to JavaFX `@FXML` field names and UI components (e.g., `TableView<Employee>`) broke the reflection-based property lookups in headless `EmployeeControllerTest` and `SalaryControllerTest`. | Refactored the test classes to reflect the new component structures. Replaced incorrect `ObservableList<String>` mocks with actual `Employee` and `Department` entities to fix `ClassCastException`s. | ✅ Resolved |
| BUG-006 | Filter Logic Discrepancy (`EmployeeController`) | Filtering employees by "All Contracts" failed to display employees due to a duplicate check comparing the translated default value with the employee's contract type. | Removed the duplicated `!contract.equals("ALL")` buggy condition in `applyFilters()` to ensure the default dropdown option resets the filter. | ✅ Resolved |

## Test Suite Execution Results
The Maven `verify` lifecycle now executes perfectly, confirming system stability:
- **Total Tests Run**: 328
- **Failures**: 0
- **Errors**: 0
- **Skipped**: 0
- **Build Status**: SUCCESS

## Next Steps
The project is now fully stabilized and secure. The user should proceed to execute the final SonarQube analysis pipeline (`mvn clean verify sonar:sonar`) to generate the final code coverage report and trigger the Docker deployment process in Jenkins.
Screenshot of Jenkins
![ER Diagram](https://github.com/Jesperho/Employee-Management-System/blob/main/docs/Diagrams/Jenkins-Test1.jpeg)