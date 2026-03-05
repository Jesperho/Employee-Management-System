# Sprint 4 Planning Report

**Project:** Employment Management System
**Sprint:** 4
**Dates:** 04.03.2026 - 11.03.2026

---

## Sprint Goal

The goal of this sprint is to finalize the prototype by ensuring all planned features are fully implemented and operational. The application will be containerized using Docker and the image will be pushed to Docker Hub for public access. The deployed image will be tested in Docker Play, and the finalized prototype will be shared with classmates for feedback. The sprint concludes with the final demonstration in Week 8.

---

## Selected Product Backlog Items

- Finalize product functionality
- Create and test Docker image
- Push Docker image to Docker Hub and test deployment
- Share prototype with classmates and gather feedback
- Prepare for final demonstration

---

## Planned Tasks / Breakdown

### 1. Finalize Product Functionality

- Ensure all planned features are fully implemented and operational: authentication, data visualization, and API integration
- Resolve data display errors, UI glitches, and error handling issues via debugging and log analysis
- Verify business logic accuracy through testing
- Ensure all UI components render and respond correctly
- Validate data input, storage, retrieval, and display across all screens

### 2. Create Docker Image (GUI Docker Image)

- Write the Dockerfile defining the base image, dependencies, application code, and startup command
- Build the Docker image using `docker build`
- Verify application functionality locally using `docker run`

### 3. Push Docker Image to Docker Hub and Test Deployment

- Tag the Docker image with the Docker Hub username and version
- Push the image to Docker Hub using `docker push`
- Confirm the image is publicly accessible on Docker Hub
- Set up Docker Play environment and deploy the image to the Docker Play cluster
- Verify the deployed image runs correctly in the Docker Play environment

### 4. Share Functional Prototype with the Class

- Deliver a project presentation following the lecturer's instructions
- Demonstrate the functional application, Docker image, Docker Hub link, test deployment results, user flow, and major features
- Gather peer feedback and address suggestions; highlight individual contributions and challenges solved

### 5. Finalize Project (GitHub + Trello/Jira)

- Update all project documentation
- Complete the README as instructed:
  - Project description
  - Technology stack
  - Architecture design (ER and Use Case diagrams with brief elaboration)
  - Link to the sprint report folder
- Update all sprint reports in GitHub
- Update the Trello/Jira board to reflect final project status

---

## Team Capacity & Assumptions

- **Team Size:** 4 members
- **Assumptions:**
  - All team members attend lectures and sprint meetings
  - All team members complete their responsibilities on time
  - Required tools (GitHub, Trello, Docker Hub, Canva) are accessible without technical issues

---

## Definition of Done

- **Finalized Product:** All planned features are implemented, integrated, and working without bugs
- **Docker Image:** Docker image builds successfully, runs the full application locally without errors, and is publicly available on Docker Hub
- **Deployment Test:** Docker image is successfully deployed and verified in Docker Play
- **Class Presentation:** Prototype is demonstrated to classmates, feedback is gathered, and individual contributions are clearly communicated
- **Project Finalization:** README is completed as instructed, all sprint reports are updated in GitHub, and the Trello/Jira board reflects the final state of the project