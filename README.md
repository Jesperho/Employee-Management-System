# Technology Selection

The technology stack for the **Employee Management System** has been selected to ensure full compliance with the course curriculum, which mandates a Java-based environment. The selection prioritizes technologies that support the required **DevOps practices** (CI/CD, Containerization) and **Software Engineering methodologies** (Agile/Scrum).

### Frontend
*   **Technology:** JavaFX
*   **Rationale:** As the project requires a standalone application and the course mandates **Java** for all compulsory assignments, JavaFX is the optimal choice. It allows for the development of a strictly typed, object-oriented interface. This ensures seamless integration with our backend logic and enables the creation of the five required views (Home, Employee, Department, Leave, Salary) without relying on external web frameworks unsupported by the course technical staff.

### Backend
*   **Language:** Java (JDK 17 or higher)
*   **Build Automation:** Maven
*   **Rationale:** Java is the core language for this course. **Maven** is selected for build automation and dependency management, as explicitly required for the Sprint 2 deliverables. This choice supports "Professional Development" standards by separating source code from build configurations and managing libraries like JUnit.

### Database
*   **Technology:** MySQL (Relational Database)
*   **Rationale:** The data managed by this system (Employees, Departments, Salaries) is highly structured and formalized. According to data modeling principles, a **Relational Database** is the correct choice for structured data where integrity and consistency are critical. A relational model allows us to implement the ER diagrams and cardinality constraints (e.g., 1:M relationships) designed in the logical design phase.

### DevOps & Tools
To meet the course's strict **DevOps** and **Quality Assurance** requirements, the following tools are integrated into the stack:

| Category | Tool | Rationale |
| :--- | :--- | :--- |
| **Version Control** | **Git & GitHub** | Essential for collaborative development and required for the weekly Sprint deliverables. |
| **CI/CD** | **Jenkins** | Mandatory for Sprint 3. Automates the build and testing process to ensure the software remains in a releasable state. |
| **Containerization** | **Docker** | Required for Sprint 4. Containerizes the application to ensure consistency across development and production environments. |
| **Testing** | **JUnit** | The standard framework for Java unit testing required for Week 3 and Code Coverage reports in Week 4. |
| **Modeling** | **StarUML (v5.0.1)** | The specific tool mandated by the course for creating Use Case and Class diagrams. |
