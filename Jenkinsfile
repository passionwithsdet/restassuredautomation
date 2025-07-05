pipeline {
    agent any
    
    environment {
        // Docker Configuration
        DOCKER_COMPOSE_FILE = 'docker-compose.yml'
        
        // Test Configuration
        TEST_ENVIRONMENT = 'jenkins'
        PARALLEL_THREADS = '4'
        
        // Reporting
        REPORT_PATH = 'reports'
        ALLURE_RESULTS = 'allure-results'
    }
    
    options {
        timeout(time: 2, unit: 'HOURS')
        timestamps()
        ansiColor('xterm')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }
    
    triggers {
        pollSCM('H/15 * * * *')  // Poll every 15 minutes
    }
    
    stages {
        stage('Checkout & Setup') {
            steps {
                script {
                    // Checkout code
                    checkout scm
                    
                    // Set up workspace directories
                    sh '''
                        echo "Setting up workspace directories..."
                        mkdir -p ${WORKSPACE}/reports
                        mkdir -p ${WORKSPACE}/logs
                        mkdir -p ${WORKSPACE}/allure-results
                        mkdir -p ${WORKSPACE}/artifacts
                    '''
                    
                    // Validate Docker
                    sh '''
                        echo "Validating Docker environment..."
                        docker --version
                        docker-compose --version
                    '''
                }
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    sh '''
                        echo "Building Docker image..."
                        docker-compose -f ${DOCKER_COMPOSE_FILE} build petstore-api-tests
                    '''
                }
            }
        }
        
        stage('Run Tests in Docker') {
            parallel {
                stage('Unit Tests') {
                    steps {
                        script {
                            sh '''
                                echo "Running Unit Tests in Docker..."
                                docker-compose -f ${DOCKER_COMPOSE_FILE} run --rm \
                                    -e TEST_SUITE=unit \
                                    petstore-api-tests \
                                    mvn clean test \
                                        -Dtest=**/*Test \
                                        -DexcludedGroups=integration,smoke \
                                        -Dparallel=methods \
                                        -DthreadCount=4 \
                                        -Dallure.results.directory=/app/target/allure-results
                            '''
                        }
                    }
                }
                
                stage('Integration Tests') {
                    steps {
                        script {
                            sh '''
                                echo "Running Integration Tests in Docker..."
                                docker-compose -f ${DOCKER_COMPOSE_FILE} run --rm \
                                    -e TEST_SUITE=integration \
                                    petstore-api-tests \
                                    mvn test \
                                        -Dtest=**/*IntegrationTest \
                                        -Dgroups=integration \
                                        -Dparallel=classes \
                                        -DthreadCount=2 \
                                        -Dallure.results.directory=/app/target/allure-results
                            '''
                        }
                    }
                }
            }
            post {
                always {
                    script {
                        // Archive test reports
                        archiveArtifacts artifacts: '**/reports/**/*, **/logs/**/*, **/allure-results/**/*', 
                                           allowEmptyArchive: true
                        
                        // Generate Allure report
                        allure([
                            includeProperties: false,
                            jdk: '',
                            properties: [],
                            reportBuildPolicy: 'ALWAYS',
                            results: [[path: 'allure-results']]
                        ])
                    }
                }
            }
        }
        
        stage('Generate Reports') {
            steps {
                script {
                    sh '''
                        echo "Generating custom reports..."
                        docker-compose -f ${DOCKER_COMPOSE_FILE} run --rm \
                            petstore-api-tests \
                            bash -c "
                                if [ -f run-custom-report.sh ]; then
                                    chmod +x run-custom-report.sh
                                    ./run-custom-report.sh
                                fi
                            "
                    '''
                }
            }
        }
    }
    
    post {
        always {
            script {
                // Cleanup Docker resources
                sh '''
                    echo "Cleaning up Docker resources..."
                    docker-compose -f ${DOCKER_COMPOSE_FILE} down -v
                    docker system prune -f
                '''
                
                // Archive final artifacts
                archiveArtifacts artifacts: '**/reports/**/*, **/logs/**/*, **/allure-results/**/*', 
                                   allowEmptyArchive: true, 
                                   fingerprint: true
                
                // Publish test results
                publishTestResults testResultsPattern: '**/surefire-reports/*.xml'
            }
        }
        
        success {
            script {
                echo "✅ Build #${env.BUILD_NUMBER} completed successfully!"
                echo "🐳 Tests executed in Docker containers"
                echo "📊 Test Results: ${env.BUILD_URL}testReport/"
                echo "📈 Allure Report: ${env.BUILD_URL}allure/"
            }
        }
        
        failure {
            script {
                echo "❌ Build #${env.BUILD_NUMBER} failed!"
                echo "🔍 Check console output: ${env.BUILD_URL}console"
            }
        }
    }
} 