pipeline {
    agent any

    tools {
        maven 'Maven3'
    }

    environment {
        PATH = "C:\\Program Files\\Docker\\Docker\\resources\\bin;${env.PATH}"
        SONARQUBE_SERVER = 'SonarQube Server'
        SONAR_PROJECT_KEY = 'EMP'
        SONAR_PROJECT_NAME = 'EMP'
        SONAR_TOKEN = credentials('SONAR_TOKEN')
        DOCKERHUB_CREDENTIALS_ID = 'docker-hub-cred'
        DOCKERHUB_REPO = 'osamaaa1/employee-management-system'
        DOCKER_IMAGE_TAG = 'latest'
    }

    stages {

        stage('Build Job') {
            steps {
                bat 'mvn clean install'
            }
        }

        stage('Test') {
            steps {
                bat 'mvn test'
            }
        }

        stage('Generate Report') {
            steps {
                bat 'mvn jacoco:report'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv("${SONARQUBE_SERVER}") {
                    bat 'mvn sonar:sonar -Dsonar.projectKey=%SONAR_PROJECT_KEY% -Dsonar.projectName=%SONAR_PROJECT_NAME% -Dsonar.coverage.jacoco.xmlReportPaths=target/jacoco-report/jacoco.xml -Dsonar.token=%SONAR_TOKEN%'
                }
            }
        }

        stage('Publish Test Result') {
            steps {
                junit '**/target/surefire-reports/*.xml'
            }
        }

        stage('Publish Coverage Report') {
            steps {
                jacoco()
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}")
                }
            }
        }

        stage('Push Docker Image to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', DOCKERHUB_CREDENTIALS_ID) {
                        docker.image("${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}").push()
                    }
                }
            }
        }
    }
}