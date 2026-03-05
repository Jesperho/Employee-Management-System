<!DOCTYPE html>
<html>

<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="https://stackedit.io/style.css" />
</head>

<body class="stackedit">
  <div class="stackedit__html">

<h1 id="sprint-3-planning-report">Sprint 3 Planning Report</h1>

<p><strong>Project:</strong> Employment Management System<br>
<strong>Sprint:</strong> 3<br>
<strong>Dates:</strong> 11.02.2026 - 04.03.2026</p>

<hr>

<h2 id="sprint-goal">Sprint Goal</h2>
<p>
The goal of this sprint is to extend the fully functional project prototype by implementing a graphical user interface with database integration, as well as user authentication and authorization. Unit tests will be written for all functions, and JaCoCo will be used for code coverage analysis. Jenkins will be integrated to enable CI/CD. Additionally, the initial Docker image of the project will be created and tested.
</p>

<hr>

<h2 id="selected-product-backlog-items">Selected Product Backlog Items</h2>
<ul>
<li>Finalize functional prototype</li>
<li>Add unit and coverage testing</li>
<li>Integrate Jenkins for CI/CD</li>
<li>Develop and test the Docker image</li>
<li>Prepare Sprint Review demonstration</li>
</ul>

<hr>

<h2 id="planned-tasks-breakdown">Planned Tasks / Breakdown</h2>

<h3>1. Finalize Functional Prototype</h3>
<ul>
<li>Extend the graphical user interface by adding the missing scenes (Employee, Department, Leave, and Salary)</li>
<li>Add user authentication and authorization for the Register and Login forms</li>
<li>Successfully connect the frontend with the backend, ensuring it works as intended</li>
</ul>

<h3>2. Add Unit and Coverage Testing</h3>
<ul>
<li>Write unit tests for the newly implemented features</li>
<li>Use JaCoCo for code coverage analysis, ensuring that the Jenkins pipeline generates and publishes the JaCoCo HTML report</li>
</ul>

<h3>3. Integrate Jenkins for CI/CD</h3>
<ul>
<li>Retrieve the latest code from the Git repository</li>
<li>Compile the code using Maven</li>
<li>Execute unit tests using JUnit</li>
<li>Generate a code coverage report using JaCoCo</li>
</ul>

<h3>4. Develop and Test the Docker Image</h3>
<ul>
<li>Create and test the initial Docker image of the project</li>
</ul>

<h3>5. Sprint Review Preparation</h3>
<ul>
<li>Prepare a demonstration of the fully working prototype, highlighting the extended features and the Jenkins CI/CD pipeline</li>
<li>Demonstrate the Jenkins CI/CD pipeline, showcasing the build, test, and report generation processes</li>
<li>Present the updated code coverage report</li>
<li>Demonstrate that the Docker image is working locally</li>
<li>Define individual team member contributions</li>
<li>Update the Trello board to reflect current progress</li>
<li>Update the project repository with the latest code, documentation, and configuration files</li>
</ul>

<hr>

<h2 id="team-capacity-assumptions">Team Capacity &amp; Assumptions</h2>
<ul>
<li><strong>Team Size:</strong> 4 members</li>
<li><strong>Assumptions:</strong>
<ul>
<li>All team members attend lectures and sprint meetings</li>
<li>All team members complete their responsibilities on time</li>
<li>Required tools (GitHub, Trello, Canva) are accessible without technical issues</li>
</ul>
</li>
</ul>

<hr>

<h2 id="definition-of-done">Definition of Done</h2>
<ul>
<li><strong>Fully Functional Prototype:</strong> All planned features are implemented, integrated, and working without bugs</li>
<li><strong>Unit and Coverage Testing:</strong> Unit tests are implemented, passing successfully, and code coverage report is generated via Jenkins</li>
<li><strong>Jenkins Integration:</strong> CI/CD pipeline successfully builds the project, runs tests, and publishes coverage reports</li>
<li><strong>Docker Image:</strong> Docker image builds successfully and runs the application locally without errors</li>
<li><strong>Sprint Review Preparation:</strong> All demonstration components are ready and tested, each team member can clearly articulate their contributions, the GitHub repository is updated with the latest commits, and the Trello board reflects accurate progress</li>
</ul>

<hr>

  </div>
</body>

</html>