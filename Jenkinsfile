pipeline {
    agent any
    
    environment {
        JAVA_HOME = tool 'JDK-17'
        MAVEN_HOME = tool 'Maven-3.9'
        DOCKER_REGISTRY = 'your-registry.com'
        IMAGE_NAME = 'enterprise-api-test-framework'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        SLACK_CHANNEL = '#test-automation'
        
        DOCKER_CREDENTIALS = credentials('docker-registry-credentials')
        
        // Test Configuration
        TEST_ENVIRONMENT = 'jenkins'
        PARALLEL_THREADS = '4'
        TEST_SUITE = 'regression'
        
        // Reporting
        REPORT_PATH = 'target/reports'
        ALLURE_RESULTS = 'target/allure-results'
        ALLURE_REPORT = 'target/allure-report'
        
        // Notification
        EMAIL_RECIPIENTS = 'qa-team@company.com'
    }
    
    tools {
        maven 'Maven-3.9'
        jdk 'JDK-17'
    }
    
    options {
        timeout(time: 2, unit: 'HOURS')
        timestamps()
        ansiColor('xterm')
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
    }
    
    triggers {
        pollSCM('H/15 * * * *')
        cron('0 2 * * *') // Daily at 2 AM
    }
    
    stages {
        stage('Checkout & Setup') {
            steps {
                script {
                    // Checkout code
                    checkout scm
                    
                    // Set up workspace
                    sh '''
                        echo "Setting up workspace..."
                        mkdir -p ${WORKSPACE}/reports
                        mkdir -p ${WORKSPACE}/logs
                        mkdir -p ${WORKSPACE}/artifacts
                    '''
                    
                    // Validate environment
                    sh '''
                        echo "Validating environment..."
                        java -version
                        mvn -version
                        docker --version
                    '''
                }
            }
            post {
                always {
                    script {
                        // Archive workspace info
                        archiveArtifacts artifacts: '**/target/reports/**', allowEmptyArchive: true
                    }
                }
            }
        }
        
        stage('Code Quality') {
            parallel {

                
                stage('Code Coverage') {
                    steps {
                        script {
                            sh '''
                                mvn clean test jacoco:report \
                                    -Djacoco.destFile=target/jacoco.exec \
                                    -Djacoco.dataFile=target/jacoco.exec
                            '''
                        }
                    }
                    post {
                        always {
                            publishCoverage adapters: [jacocoAdapter('target/site/jacoco/jacoco.xml')], 
                                           sourceFileResolver: sourceFiles('STORE_LAST_BUILD')
                        }
                    }
                }
            }
            post {
                always {
                    script {
                        // Archive quality reports
                        archiveArtifacts artifacts: '**/target/site/**/*, **/dependency-check-report.*, **/target/jacoco/**/*', 
                                           allowEmptyArchive: true
                        
                        // Publish test results
                        publishTestResults testResultsPattern: '**/surefire-reports/*.xml'
                    }
                }
            }
        }
        
        stage('Build & Test') {
            parallel {
                stage('Unit Tests') {
                    steps {
                        script {
                            sh '''
                                mvn clean test \
                                    -Dtest=**/*Test \
                                    -DexcludedGroups=integration,smoke \
                                    -Dparallel=methods \
                                    -DthreadCount=4
                            '''
                        }
                    }
                }
                
                stage('Integration Tests') {
                    steps {
                        script {
                            sh '''
                                mvn test \
                                    -Dtest=**/*IntegrationTest \
                                    -Dgroups=integration \
                                    -Dparallel=classes \
                                    -DthreadCount=2
                            '''
                        }
                    }
                }
                
                stage('API Contract Tests') {
                    steps {
                        script {
                            sh '''
                                mvn test \
                                    -Dtest=**/*ContractTest \
                                    -Dgroups=contract \
                                    -Dparallel=methods \
                                    -DthreadCount=2
                            '''
                        }
                    }
                }
            }
            post {
                always {
                    script {
                        // Archive test reports
                        archiveArtifacts artifacts: '**/target/reports/**/*, **/target/surefire-reports/**/*, **/target/allure-results/**/*', 
                                           allowEmptyArchive: true
                        
                        // Generate and publish Allure report
                        allure([
                            includeProperties: false,
                            jdk: '',
                            properties: [],
                            reportBuildPolicy: 'ALWAYS',
                            results: [[path: 'target/allure-results']]
                        ])
                    }
                }
            }
        }
        

        

        
        stage('Build Docker Image') {
            steps {
                script {
                    sh '''
                        docker build -t ${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG} .
                        docker tag ${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG} ${DOCKER_REGISTRY}/${IMAGE_NAME}:latest
                    '''
                }
            }
            post {
                always {
                    script {
                        // Archive Docker artifacts
                        archiveArtifacts artifacts: 'Dockerfile, docker-compose.yml, .dockerignore', allowEmptyArchive: true
                    }
                }
            }
        }
        
        stage('Push to Registry') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                }
            }
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'docker-registry-credentials', 
                                                    usernameVariable: 'DOCKER_USER', 
                                                    passwordVariable: 'DOCKER_PASS')]) {
                        sh '''
                            echo ${DOCKER_PASS} | docker login ${DOCKER_REGISTRY} -u ${DOCKER_USER} --password-stdin
                            docker push ${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}
                            docker push ${DOCKER_REGISTRY}/${IMAGE_NAME}:latest
                        '''
                    }
                }
            }
        }
        
        stage('Quality Gates') {
            steps {
                script {
                    // Test Coverage Gate
                    def coverage = currentBuild.getAction(hudson.plugins.cobertura.CoberturaBuildAction.class)
                    if (coverage && coverage.getResult().getCoverage(hudson.plugins.cobertura.Ratio.class) < 80) {
                        error "Test coverage below 80% threshold"
                    }
                    

                }
            }
        }
    }
    
    post {
        always {
            script {
                // Cleanup
                sh '''
                    docker system prune -f
                '''
                
                // Archive final artifacts
                archiveArtifacts artifacts: '**/target/**/*, **/reports/**/*, **/logs/**/*', 
                                   allowEmptyArchive: true, 
                                   fingerprint: true
                
                // Publish test results
                publishTestResults testResultsPattern: '**/surefire-reports/*.xml'
                
                // Generate custom report
                sh '''
                    if [ -f run-custom-report.sh ]; then
                        chmod +x run-custom-report.sh
                        ./run-custom-report.sh
                    fi
                '''
            }
        }
        
        success {
            script {
                // Success notifications
                emailext (
                    subject: "✅ Enterprise API Test Framework - Build #${env.BUILD_NUMBER} SUCCESS",
                    body: """
                        <h2>Build Success</h2>
                        <p><strong>Build:</strong> #${env.BUILD_NUMBER}</p>
                        <p><strong>Branch:</strong> ${env.GIT_BRANCH}</p>
                        <p><strong>Commit:</strong> ${env.GIT_COMMIT}</p>
                        <p><strong>Duration:</strong> ${currentBuild.durationString}</p>
                        <p><strong>Console:</strong> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                        <p><strong>Reports:</strong> <a href="${env.BUILD_URL}allure/">Allure Report</a></p>
                    """,
                    recipientProviders: [[$class: 'DevelopersRecipientProvider']]
                )
                
                // Slack notification
                slackSend(
                    channel: env.SLACK_CHANNEL,
                    color: 'good',
                    message: "✅ Enterprise API Test Framework - Build #${env.BUILD_NUMBER} SUCCESS\n" +
                            "Branch: ${env.GIT_BRANCH}\n" +
                            "Duration: ${currentBuild.durationString}\n" +
                            "Console: ${env.BUILD_URL}"
                )
            }
        }
        
        failure {
            script {
                // Failure notifications
                emailext (
                    subject: "❌ Enterprise API Test Framework - Build #${env.BUILD_NUMBER} FAILED",
                    body: """
                        <h2>Build Failure</h2>
                        <p><strong>Build:</strong> #${env.BUILD_NUMBER}</p>
                        <p><strong>Branch:</strong> ${env.GIT_BRANCH}</p>
                        <p><strong>Commit:</strong> ${env.GIT_COMMIT}</p>
                        <p><strong>Duration:</strong> ${currentBuild.durationString}</p>
                        <p><strong>Console:</strong> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                        <p><strong>Error:</strong> ${currentBuild.description ?: 'Unknown error'}</p>
                    """,
                    recipientProviders: [[$class: 'DevelopersRecipientProvider']]
                )
                
                // Slack notification
                slackSend(
                    channel: env.SLACK_CHANNEL,
                    color: 'danger',
                    message: "❌ Enterprise API Test Framework - Build #${env.BUILD_NUMBER} FAILED\n" +
                            "Branch: ${env.GIT_BRANCH}\n" +
                            "Duration: ${currentBuild.durationString}\n" +
                            "Console: ${env.BUILD_URL}"
                )
            }
        }
        
        unstable {
            script {
                // Unstable notifications
                slackSend(
                    channel: env.SLACK_CHANNEL,
                    color: 'warning',
                    message: "⚠️ Enterprise API Test Framework - Build #${env.BUILD_NUMBER} UNSTABLE\n" +
                            "Branch: ${env.GIT_BRANCH}\n" +
                            "Duration: ${currentBuild.durationString}\n" +
                            "Console: ${env.BUILD_URL}"
                )
            }
        }
    }
} 