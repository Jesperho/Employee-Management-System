# Sprint 5 Review Report

- **Project:** Employment Management System
- **Sprint:** 5
- **Dates:** 19.03.2026 – 02.04.2026

---

## Sprint Goal

The goal of Sprint 5 was to prepare the application for full multilingual support by externalizing all UI text, implementing a complete localization system, integrating multiple languages (including two non‑Latin languages), enabling RTL/LTR layout switching, and updating documentation and backlog items accordingly.

---

## Completed User Stories / Tasks

- Externalized all UI text across every page of the application
- Created a complete localization spreadsheet for English → Persian, Arabic, and Spanish
- Implemented a functional language selector in the UI
- Integrated MessagesBundle files for EN, FA, AR, and ES
- Implemented dynamic locale switching for:
    - UI text
    - Table content
- Added full RTL support for Arabic and Persian (mirroring, alignment, layout adjustments)
- Verified that all UI elements update dynamically when switching languages
- Fixed layout issues caused by long translations and RTL formatting
- Updated README with localization setup instructions
- Updated Product and Sprint Backlogs with localization user stories and acceptance criteria
- Prepared Sprint 6 plan focusing on database localization
- Completed Sprint 5 documentation and committed all files to GitHub

All Sprint 5 backlog items were completed.

---

## Demo Summary

During the Sprint Review, the team demonstrated:

- **Multilingual UI**
    - Full language switching between English, Persian, Arabic, and Spanish
    - All UI text, labels, buttons, tables, and forms updating dynamically
    - Correct formatting of dates, numbers, and currency based on locale

- **RTL Support**
    - Proper mirroring of the interface for Arabic and Persian
    - Correct alignment of tables, forms, and navigation elements

- **Localization Framework**
    - MessagesBundle integration
    - Page‑specific localization keys
    - Clean and scalable localization architecture

- **Documentation**
    - Updated README with localization instructions
    - Completed localization spreadsheet
    - Updated Trello/Jira backlog with new user stories

- **Sprint 6 Preparation**
    - Database localization plan
    - Initial ERD considerations for multilingual content

---

## What Went Well

- Full multilingual support was successfully implemented
- RTL layout worked smoothly after adjustments
- Localization keys and MessagesBundle structure were clean and scalable
- Language selector functioned without requiring application restart
- Team collaboration was strong, especially during translation and UI testing
- Documentation and backlog updates were completed on time
- GitHub commits were well‑organized and easy to track

---

## What Could Be Improved

- RTL layout required more time than expected due to alignment issues
- Some UI components needed refactoring to support dynamic text resizing
- Translation verification took longer because of multiple languages
- Initial localization key naming required restructuring for consistency

---

## Next Sprint Focus

Sprint 6 will focus on:

- Database localization (schema updates, multilingual fields, UTF‑8 validation)
- Static code analysis (Checkstyle, PMD, SonarQube)
- Code cleanup and refactoring
- Acceptance test planning
- Updating ERD and UML diagrams
- Preparing the final acceptance documentation

---

## Time Spent by Each Team Member

| Team Member      | Hours Spent | Contributions | In-class tasks
|------------------|-------------|---------------|----------------------------
| Amir Noori       | 12 hours    | integrated MessagesBundle, documentation,Implemented Persian translation, Write planning and review report | Submitted |
| Jesper Holmström | 12 hours    | Updated backlog, fixed RTL layout issues, Implemented language selector | Submitted |
| Mehdi Zaidane    | 12 hours    | Externalized UI text, created localization keys, Implemented Spanish, added RTL support | Submitted |
| Osama Aamer      | 15 hours    | Updated UI components for dynamic localization, tested all languages,updated README, Implemented Arabic translations | Submitted |

---

## Definition of Done Verification

- **Localization:**
    - All UI text externalized and translated
    - MessagesBundle implemented for EN, FA, AR, ES
    - Language selector fully functional
    - RTL/LTR support implemented and tested

- **Documentation:**
    - README updated with localization instructions
    - Localization spreadsheet completed
    - Sprint 5 review summary prepared
    - Sprint 6 plan documented

- **Backlog:**
    - Product Backlog updated with localization stories
    - Acceptance criteria defined
    - Sprint Backlog updated

---

## Sprint Summary

Sprint 5 successfully delivered full multilingual UI localization for the Employee Management System. The team implemented dynamic language switching, RTL support, and complete translation coverage across all screens. Documentation and backlog updates were completed, and the project is now ready to move into Sprint 6, focusing on database localization, code review, refactoring, and acceptance test planning.