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
                    
                    // Check for existing MongoDB container
                    sh '''
                        echo "Checking for existing MongoDB container..."
                        if docker ps -q -f name=petstore-test-mongodb | grep -q .; then
                            echo "✅ Found existing MongoDB container, will reuse it"
                            export MONGODB_EXISTS=true
                        else
                            echo "📦 No existing MongoDB found, will start new one"
                            export MONGODB_EXISTS=false
                        fi
                    '''
                }
            }
        }
        
        stage('Setup MongoDB') {
            steps {
                script {
                    sh '''
                        if [ "$MONGODB_EXISTS" = "false" ]; then
                            echo "🚀 Starting MongoDB container..."
                            docker-compose -f ${DOCKER_COMPOSE_FILE} up -d mongodb
                            echo "⏳ Waiting for MongoDB to be ready..."
                            sleep 10
                        else
                            echo "✅ Using existing MongoDB container"
                        fi
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
                                # Ensure target directory exists and has proper permissions
                                mkdir -p ${WORKSPACE}/target/reports
                                mkdir -p ${WORKSPACE}/target/allure-results
                                mkdir -p ${WORKSPACE}/target/allure-report
                                chmod -R 777 ${WORKSPACE}/target
                                
                                docker-compose -f ${DOCKER_COMPOSE_FILE} run --rm \
                                    -e TEST_SUITE=unit \
                                    -v ${WORKSPACE}/target:/app/target \
                                    petstore-api-tests \
                                    mvn clean test \
                                        -Dtest=PetApiTests \
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
                                # Ensure target directory exists and has proper permissions
                                mkdir -p ${WORKSPACE}/target/reports
                                mkdir -p ${WORKSPACE}/target/allure-results
                                mkdir -p ${WORKSPACE}/target/allure-report
                                chmod -R 777 ${WORKSPACE}/target
                                
                                docker-compose -f ${DOCKER_COMPOSE_FILE} run --rm \
                                    -e TEST_SUITE=integration \
                                    -v ${WORKSPACE}/target:/app/target \
                                    petstore-api-tests \
                                    mvn test \
                                        -Dtest=CustomReportExampleTest,GenerateCustomReportTest \
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
                        
                        // Generate static HTML reports
                        sh '''
                            echo "📊 Reports generated successfully!"
                            echo "📁 Allure Report: ${WORKSPACE}/target/allure-report/index.html"
                            echo "📁 ExtentReport: ${WORKSPACE}/target/reports/petstore_api_report_*.html"
                            echo "📁 Custom Report: ${WORKSPACE}/target/reports/custom_report_*.html"
                        '''
                    }
                }
            }
        }
        
        stage('Generate Reports') {
            steps {
                script {
                    sh '''
                        echo "Generating Allure static HTML reports..."
                        docker-compose -f ${DOCKER_COMPOSE_FILE} run --rm \
                            petstore-api-tests \
                            bash -c "
                                allure generate /app/target/allure-results --clean -o /app/target/allure-report
                                echo '✅ Allure report generated at: /app/target/allure-report/index.html'
                            "
                    '''
                    
                    sh '''
                        echo "Generating ExtentReports..."
                        docker-compose -f ${DOCKER_COMPOSE_FILE} run --rm \
                            petstore-api-tests \
                            mvn test -Dtest=GenerateCustomReportTest
                    '''
                    
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
                // Cleanup Docker resources (but preserve MongoDB if it was already running)
                sh '''
                    echo "Cleaning up Docker resources..."
                    if [ "$MONGODB_EXISTS" = "false" ]; then
                        echo "🗑️ Removing MongoDB container (was created by this build)"
                        docker-compose -f ${DOCKER_COMPOSE_FILE} down -v
                    else
                        echo "✅ Keeping existing MongoDB container"
                        docker-compose -f ${DOCKER_COMPOSE_FILE} down
                    fi
                    docker system prune -f
                '''
                
                // Archive final artifacts including static HTML reports
                archiveArtifacts artifacts: '**/reports/**/*, **/logs/**/*, **/allure-results/**/*, **/allure-report/**/*', 
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
                echo "📈 Static HTML Reports:"
                echo "   - Allure Report: ${env.BUILD_URL}artifact/target/allure-report/index.html"
                echo "   - ExtentReport: ${env.BUILD_URL}artifact/target/reports/"
                echo "   - Custom Report: ${env.BUILD_URL}artifact/target/reports/"
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