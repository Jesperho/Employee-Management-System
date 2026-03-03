pipeline {
    agent any
    tools {
        maven 'Maven 3.8.5' // match the name you configured
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Jesperho/Employee-Management-System.git'
            }
        }
        stage('Build') {
            steps {
                bat "${tool 'Maven 3.9'}\\bin\\mvn clean install"
            }
        }
        stage('Test') {
            steps {
                bat "${tool 'Maven 3.9'}\\bin\\mvn test"
            }
        }
        stage('Code Coverage') {
            steps {
                bat "${tool 'Maven 3.9'}\\bin\\mvn jacoco:report"
            }
        }
        stage('Publish Test Results') {
            steps {
                junit '**/target/surefire-reports/*.xml'
            }
        }
        stage('Publish Coverage Report') {
            steps {
                jacoco()
            }
        }
    }
}
