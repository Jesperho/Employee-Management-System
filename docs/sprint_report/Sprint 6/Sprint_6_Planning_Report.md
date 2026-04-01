# Sprint 6 Planning Report

- **Project:** Employment Management System
- **Sprint:** 6
- **Dates:** 02.04.2026 – 16.04.2026

---

## Sprint Goal

The goal of Sprint 6 is to extend localization support into the database layer, improve overall code quality through static code review and refactoring, and prepare the system for acceptance testing. This sprint focuses on database schema updates for multilingual content, code analysis using industry tools, code cleanup, and designing a formal acceptance test plan.

---

## Selected Product Backlog Items

- Design and implement database localization strategy
- Update ERD and schema for multilingual fields
- Ensure UTF‑8 encoding and locale‑safe data handling
- Validate multilingual data retrieval and display
- Perform static code analysis (Checkstyle, PMD, SonarQube/SonarScanner)
- Document code metrics and findings
- Refactor codebase based on analysis results
- Re-run unit tests and ensure no regressions
- Create acceptance criteria and acceptance test plan
- Update ERD and UML diagrams in `/docs`
- Update README with database localization documentation
- Prepare Sprint 6 review report

---

## Planned Tasks / Breakdown

### 1. Database Localization
- Analyze current database schema
- Identify fields requiring multilingual support
- Update ERD to include localized tables/columns
- Implement UTF‑8 encoding and locale configuration
- Modify DAO/service layers to handle localized content
- Test multilingual data retrieval and display
- Document chosen localization strategy in README

---

### 2. Static Code Review
- Run Checkstyle, PMD, and SonarScanner
- Collect metrics for:
    - Cyclomatic complexity
    - Lines of code per method
    - Duplicate or unreachable code
- Identify violations, inefficiencies, and potential bugs
- Document findings with screenshots, charts, and tables

---

### 3. Code Clean-Up and Refactoring
- Split large or complex methods into smaller units
- Remove redundant or duplicate code
- Improve naming conventions and formatting
- Add inline documentation and method-level comments
- Apply Java coding standards and linting rules
- Re-run unit tests to ensure stability

---

### 4. Acceptance Test Planning
- Review project and sprint requirements
- Define measurable acceptance criteria
- Design acceptance test cases for:
    - Functional testing
    - Usability testing
    - Performance and reliability testing
- Map each test case to user stories or requirements
- Prepare Acceptance Test Plan document

---

### 5. Architecture Design Documentation
- Update ERD to reflect multilingual database structure
- Update UML diagrams (Class, Sequence, Use Case)
- Add all diagrams to `/docs` folder in GitHub

---

## Team Capacity & Assumptions

- **Team Size:** 4 members
- **Assumptions:**
    - All team members attend lectures and sprint review
    - All members complete assigned tasks on time
    - Static analysis tools run without technical issues
    - Database changes do not break existing functionality
    - GitHub and Trello/Jira remain accessible

---

## Definition of Done

### ✔ Database Localization
- ERD updated for multilingual support
- Database schema modified and documented
- UTF‑8 encoding validated
- Multilingual data retrieval tested

### ✔ Code Review & Refactoring
- Static analysis completed (Checkstyle, PMD, SonarScanner)
- Code violations documented
- Refactoring completed and committed
- All unit tests passing

### ✔ Acceptance Test Planning
- Acceptance criteria defined
- Acceptance test plan created
- Test cases mapped to requirements

### ✔ Documentation
- README updated with database localization details
- ERD and UML diagrams added to `/docs`
- Sprint 6 planning and review reports completed

---