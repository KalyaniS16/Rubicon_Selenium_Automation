pipeline {
    agent any

    tools {
        jdk 'JDK21'
        maven 'Maven-3'
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    environment {
        DISPLAY = ':99'
        MAVEN_OPTS = '-Xmx1024m'
    }

    stages {

        stage('Checkout') {
            steps {
                echo '=== Checking out code from GitHub ==='
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/RUB-1-Deployment']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/KalyaniS16/Rubicon_Selenium_Automation.git'
                    ]]
                ])
            }
        }

        stage('Build') {
            steps {
                echo '=== Compiling project (skip tests) ==='
                sh 'mvn clean compile -DskipTests'
            }
        }

        stage('Run Dashboard TestNG Tests') {
            steps {
                echo '=== Starting Xvfb virtual display ==='
                sh '''
                    Xvfb :99 -screen 0 1920x1080x24 &
                    sleep 2
                '''

                echo '=== Running Dashboard TestNG suite only ==='
                sh '''
                    mvn test \
                      -Dsurefire.suiteXmlFiles=TestSuite/dashboard-only.xml \
                      -Dbrowser=chrome \
                      -Dheadless=true
                '''
            }
        }

        stage('Collect Logs & Reports') {
            steps {
                echo '=== Archiving logs and reports ==='
                archiveArtifacts artifacts: 'logs/test.log, reports/**', allowEmptyArchive: true
            }
        }
    }

    post {
        always {
            echo '=== Pipeline finished — check Console Output and archived artifacts ==='
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
        }
        success {
            echo 'Dashboard TestNG tests PASSED'
        }
        failure {
            echo 'Dashboard TestNG tests FAILED — check logs/test.log and reports/index.html'
        }
    }
}
