pipeline {
    agent any

    environment {
        // 필요시 환경 변수 선언
        GRADLE_OPTS = "-Dorg.gradle.daemon=false"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build') {
            steps {
                sh './gradlew clean build'
            }
        }
        stage('Test') {
            steps {
                sh './gradlew test'
            }
        }
        stage('Package') {
            steps {
                sh './gradlew bootJar'
            }
        }
        stage('Deploy') {
            when {
                branch 'main'
            }
            steps {
                // 예시: 빌드 산출물 복사 또는 배포 스크립트 실행
                echo '배포 단계: 실제 배포 스크립트로 교체 필요'
                // sh 'scp build/libs/*.jar user@server:/deploy/path/'
            }
        }
    }

    post {
        always {
            junit 'build/test-results/test/*.xml'
        }
        failure {
            echo '빌드 실패!'
        }
    }
} 