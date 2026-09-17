pipeline {
    agent any

    stages {
        stage('Test') {
            steps {
                sh 'java -version && mvn -version && git --version'
            }
        }
    }
}
