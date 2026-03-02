  # Sprint 3 Review Report
**Project:** Employment Management System  
**Sprint:** 3  
**Dates:** 11.02.2026 – 04.03.2026

---

## Sprint Goal
The goal of Sprint 3 was to extend the functional prototype by implementing a graphical user interface with database integration, as well as user authentication and authorization. Unit tests were written for all functions, and JaCoCo was used for code coverage analysis. Jenkins was integrated to enable CI/CD, and the initial Docker image of the project was created and tested.

---

## Completed User Stories / Tasks

- Implement user interface for Employee, Department, Leave, and Salary scenes
- Implement user authentication and authorization for Register and Login forms
- Successfully connect the frontend with the backend, ensuring it works as intended
- Write unit tests for the newly implemented features
- Use JaCoCo for code coverage analysis, ensuring that the Jenkins pipeline generates and publishes the JaCoCo HTML report
- Integrate Jenkins for CI/CD, including code retrieval, compilation, unit test execution, and code coverage report generation
- Create and test the initial Docker image of the project
- Prepare and deliver the Sprint Review demonstration

All Sprint 3 backlog items were completed.

---

## Demo Summary

During the Sprint Review, the team demonstrated:

- **Fully Functional Prototype**
    - Employee, Department, Leave, and Salary scenes implemented in JavaFX
    - User authentication and authorization working for Register and Login forms
    - Frontend successfully connected to the backend with functional workflows

- **Jenkins CI/CD Pipeline**
    - Code retrieval from Git repository
    - Maven compilation and build
    - JUnit test execution with all tests passing
    - JaCoCo code coverage report generation and publication
  
- **Unit Testing**
    - JUnit tests covering all new features and functionalities
    - All tests passing successfully

- **Code Coverage**
    - JaCoCo integrated into Maven
    - HTML coverage report generated

- **Docker Image**
    - Initial Docker image created for the application
    - Successful testing of the Docker image locally

- **Development Tools**
    - Maven dependency management
    - Updated GitHub repository structure
    - Trello board reflecting sprint progress

- **Team Contributions**
    - Each team member's contributions were highlighted, showcasing their roles in the development process


---

## What Went Well

- UI development aligned well with the Figma design
- Backend integration was successful, providing the intended result between frontend and backend
- Authentication and authorization features were implemented effectively
- Jenkins integration for CI/CD was functional and worked as intended.
- Unit testing setup was smooth and tests were well‑structured
- Docker image creation and testing were successful
- JaCoCo integration worked without major issues
- Team collaboration and communication remained strong
- GitHub and Trello were consistently updated

---

## What Could Be Improved

- Some features took longer than expected to implement, impacting the sprint timeline
- Understanding of Sprint 3 requirements could have been improved to avoid doing extra work that was not needed for the demo
- Some of the functionalities were not needed for the demo, which could have been identified earlier to save time
- Unit test should have been written earlier in the development process to catch issues sooner
- More frequent team reviews could help catch issues earlier

---

## Next Sprint Focus

Sprint 4 will focus on:

- Finalize functionality of the prototype
- Containerize the project using Docker
- Create and push Docker image to Docker Hub
- Test the deployed image in a suitable environment
- Share the project with classmates for feedback
- Prepare for the final demonstration

---

## Time Spent by Each Team Member

| Team Member       | Hours Spent | Contributions                                                                                              |
|-------------------|-------------|------------------------------------------------------------------------------------------------------------|
| Amir Noori        | 19 hours    | Extended the project functionality based on user stories.                                                 |
| Jesper Holmström  | 18 hours    | Implemented user authentication and authorization, and ensured proper frontend–backend integration.     |
| Mehdi Zaidane     | 19 hours    | Designed the ER diagram and database schema, implemented Jenkins and Docker, and wrote the Sprint 3 reports. |
| Osama Aamer       | 20 hours    | Developed the JavaFX user interface for all project screens.                                            |            |

---

## Definition of Done Verification

- **Frontend:** All scenes implemented and functional, connected to backend
- **Authentication:** User authentication and authorization implemented and working
- **Backend:** Successfully connected to frontend, providing intended functionality
- **Jenkins:** CI/CD pipeline set up and functional, generating and publishing JaCoCo reports
- **Unit Testing:** Tests written and passing for all new features
- **Docker:** Initial image created and tested successfully
- **Code Coverage:** JaCoCo integrated and HTML report generated
- **Documentation:** Updated GitHub repository with latest code, documentation, and configuration files
- **Review Preparation:** All demo components ready, team contributions defined, tools updated

---

## Sprint Summary

Sprint 3 successfully transitioned the project from a basic functional prototype to a more complete application with a graphical user interface, backend integration, authentication features, and CI/CD capabilities. The team effectively collaborated to implement the planned features, write unit tests, and integrate Jenkins for continuous integration and delivery. The initial Docker image was created and tested successfully. The Sprint Review demonstration showcased the progress made, and the team identified areas for improvement to enhance future sprints. Overall, Sprint 3 was a significant step towards finalizing the Employment Management System project.
