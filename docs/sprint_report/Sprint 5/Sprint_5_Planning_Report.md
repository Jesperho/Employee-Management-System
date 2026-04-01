# Sprint 5 Planning Report

- **Project:** Employment Management System
- **Sprint:** 5
- **Dates:** 19.03.2026 – 02.04.2026

---

## Sprint Goal

The goal of this sprint is to prepare the entire application for multilingual support by externalizing all UI text, implementing a complete localization system, and enabling dynamic language switching. This includes adding support for non‑Latin languages (Persian and Arabic), ensuring RTL/LTR layout compatibility, and updating documentation and backlog items accordingly.

---

## Selected Product Backlog Items

- Externalize all static UI text across all pages
- Create localization spreadsheet (English → Persian, Arabic, Spanish)
- Implement language selector in the UI
- Implement dynamic locale switching (text, tables)
- Add RTL support for Arabic and Persian
- Integrate MessagesBundle files for all languages
- Update README with localization instructions
- Update Sprint & Product Backlog (Trello)
- Prepare Sprint 6 plan (database localization)
- Prepare Sprint 5 review summary

---

## Planned Tasks / Breakdown

### 1. UI Localization Preparation
- Identify and extract all UI text elements
- Create localization keys (page‑specific)
- Build MessagesBundle files for EN, FA, AR, ES
- Ensure all UI components load text from resource bundles

### 2. Implement Language Selector
- Add language dropdown to the UI
- Implement locale switching logic
- Reload UI dynamically on language change
- Apply locale‑based formatting (dates, numbers, currency)

### 3. RTL/LTR Layout Support
- Implement RTL layout for Arabic & Persian
- Ensure tables, forms, and navigation adjust correctly
- Test alignment, mirroring, and text direction

### 4. Full Localization Implementation
- Integrate Persian, Arabic, and Spanish translations
- Validate translations across all screens
- Fix layout issues caused by long text or RTL
- Test dynamic switching between all languages

### 5. Documentation & Backlog Updates
- Update README with localization setup instructions
- Add localization user stories to Product Backlog
- Define acceptance criteria for localization features
- Update Sprint Backlog with new tasks

### 6. Sprint Review & Sprint 6 Planning
- Prepare Sprint 5 review summary
- Document challenges, progress, and improvements
- Create Sprint 6 plan focusing on database localization
- Assign responsibilities and estimates

---

## Team Capacity & Assumptions

- **Team Size:** 4 members
- **Assumptions:**
    - All team members attend lectures and sprint review
    - All members complete assigned localization tasks
    - No major technical blockers with JavaFX, resource bundles, or RTL
    - GitHub, Trello and IDE tools remain fully accessible

---

## Definition of Done

- **UI Localization:**
    - All UI text externalized
    - MessagesBundle implemented for EN, FA, AR, ES
    - Language selector fully functional
    - Dynamic text, date, number, and table updates working
    - RTL support implemented and tested

- **Documentation:**
    - README updated with localization instructions
    - Localization spreadsheet completed
    - Sprint 5 review summary prepared
    - Sprint 6 plan documented

- **Backlog:**
    - Product Backlog updated with localization stories
    - Acceptance criteria defined
    - Sprint Backlog updated