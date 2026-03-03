pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                // Specify the branch 'main' explicitly
                git branch: 'main', url: 'https://github.com/Jesperho/Employee-Management-System.git'
            }
        }
        stage('Build') {
            steps {
                bat 'mvn clean install' // use sh for Linux/macOS
            }
        }
        stage('Test') {
            steps {
                bat 'mvn test'
            }
        }
        stage('Code Coverage') {
            steps {
                bat 'mvn jacoco:report'
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
