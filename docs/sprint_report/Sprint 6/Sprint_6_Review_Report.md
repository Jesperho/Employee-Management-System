# Sprint 6 Review Report

- **Project:** Employment Management System
- **Sprint:** 6
- **Dates:** 02.04.2026 – 16.04.2026

---

## Sprint Goal

The goal of Sprint 6 was to extend localization into the database layer, improve overall code quality through static code review and refactoring, and prepare the system for acceptance testing. This included updating the database schema for multilingual content, running static analysis tools, cleaning up the codebase, and designing a formal acceptance test plan.

---

## Completed User Stories / Tasks

- Designed and implemented a database localization strategy
- Updated ERD to support multilingual fields and UTF‑8 encoding
- Modified database schema and DAO/service layers for localized content
- Validated multilingual data retrieval and display
- Ran static code analysis using Checkstyle, PMD, and SonarScanner
- Collected metrics for cyclomatic complexity, LOC per method, and duplicate code
- Documented all findings with screenshots, tables, and charts
- Refactored codebase based on analysis results
- Removed redundant code and simplified complex methods
- Improved naming conventions and formatting across the project
- Added inline documentation and method‑level comments
- Unit tests to ensure no regressions
- Created a complete Acceptance Test Plan with criteria and test cases
- Updated ERD and UML diagrams and added them to `/docs`
- Updated README with database localization documentation
- Completed Sprint 6 documentation and committed all deliverables to GitHub

All Sprint 6 backlog items were completed.

---

## Demo Summary

During the Sprint Review, the team demonstrated:

### **Database Localization**
- Updated ERD with multilingual table structure
- UTF‑8 encoding validation
- Successful retrieval of localized content from the database
- UI correctly displaying localized database values

### **Static Code Review**
- Checkstyle, PMD, and SonarScanner reports
- Metrics for complexity, duplication, and code smells
- Screenshots and charts summarizing violations and improvements

### **Refactored Codebase**
- Cleaner, more maintainable code
- Reduced complexity in several controllers and services
- Consistent naming conventions and formatting
- Unit tests passing after some refactoring

### **Acceptance Test Plan**
- Defined acceptance criteria
- Functional, usability, and performance test cases
- Coverage matrix mapping tests to user stories

### **Updated Documentation**
- README updated with database localization strategy
- ERD and UML diagrams added to `/docs`
- Sprint 6 report and backlog updates completed

---

## What Went Well

- Database localization was successfully implemented without breaking existing functionality
- Static analysis tools provided clear insights and helped improve code quality
- Refactoring significantly improved readability and maintainability
- All unit tests passed after refactoring, confirming system stability
- Acceptance Test Plan was well‑structured and aligned with project requirements
- Documentation and diagrams were completed on time
- Team collaboration remained strong throughout the sprint

---

## What Could Be Improved

- Static analysis setup took longer than expected due to configuration issues
- Some refactoring tasks required deeper restructuring than initially planned
- Database localization required additional testing to ensure compatibility with all languages
- UML diagram updates took more time due to multiple revisions

---

## Next Sprint Focus (Sprint 7)

Sprint 7 will focus on **functional and non‑functional testing** to ensure the product meets all requirements defined by the Product Owner.  
Key areas include:

- Creating a full Test Plan (objectives, environment, resources)
- Conducting final unit testing after refactoring
- Building a bug tracking table and resolving issues
- Running Jenkins static code analysis and ensuring SonarQube grades A/B
- Performing heuristic evaluation
- Conducting User Acceptance Testing (UAT) based on Sprint 6 criteria
- Documenting technical changes resulting from testing
- Updating Trello and GitHub documentation

Sprint 7 is essential for validating the system’s quality before final documentation and submission.

---

## Time Spent by Each Team Member

| Team Member      | Hours Spent | Contributions | In-class Tasks |
|------------------|-------------|---------------|----------------|
| Amir Noori       | 22 hours    | Implemented database localization, updated ERD, validated multilingual data | Submitted |
| Jesper Holmström | 23 hours    | Ran static code analysis, documented findings, updated UML diagrams | Submitted |
| Mehdi Zaidane    | 25 hours    | Refactored codebase, removed duplicate logic, ensured unit tests passed | Submitted |
| Osama Aamer      | 24 hours    | Created Acceptance Test Plan, updated README, validated UI with localized DB content | Submitted |

---

## Definition of Done Verification

### **Database Localization**
- ERD updated and committed
- Schema modified for multilingual support
- UTF‑8 encoding validated
- Localized data retrieval tested

### **Static Code Review**
- Checkstyle, PMD, and SonarScanner executed
- Metrics collected and documented
- Findings summarized with screenshots

### **Refactoring**
- Code cleaned and standardized
- Redundant code removed
- Unit tests passing

### **Acceptance Test Planning**
- Acceptance criteria defined
- Test cases designed and mapped to requirements

### **Documentation**
- README updated
- ERD and UML diagrams added to `/docs`
- Sprint 6 review report completed

---

## Sprint Summary

Sprint 6 successfully extended localization into the database layer, improved code quality through static analysis and refactoring, and prepared the system for acceptance testing. All deliverables were completed, documentation was updated, and the project is now ready for Sprint 7, which will focus on functional and non‑functional testing to ensure the system is fully validated before final submission.
