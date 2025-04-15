pipeline {
    agent any

    environment {
        APP_NAME  = "oliveyoung"  // 애플리케이션 이름 설정
        IMAGE_TAG = "build-${env.BUILD_NUMBER}"  // 빌드 번호를 사용하여 이미지 태그 설정
        SONAR_PROJECT_KEY = credentials('sonarqube-projectkey')  // SonarQube 프로젝트 키
        SONAR_HOST_URL = credentials('sonarqube-hosturl')  // SonarQube 서버 주소
        SONAR_LOGIN = credentials('sonarqube-login')  // SonarQube 권한 부여 받기 위한 토큰
    }

    stages {
	    stage('Set Global DockerHub Account') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-account', usernameVariable: 'DOCKERHUB_ID', passwordVariable: 'DOCKERHUB_PW')]) {
                    script {
                        // DockerHub 아이디와 비밀번호를 환경 변수로 설정
                        env.IMAGE_NAME   = "${DOCKERHUB_ID}/${env.APP_NAME}"  // 이미지 이름 형식 설정 (아이디/앱 이름)
                    }
                }
            }
        }

        stage('Clone Repository') {
            steps {
                // GitHub에서 main 브랜치를 클론
                git branch: 'main', credentialsId: 'github-token', url: 'https://github.com/cloudwave-highfive/backend.git'
            }
        }

        stage('Prepare Config') {
            steps {
                withCredentials([file(credentialsId: 'spring-properties', variable: 'APP_PROPS')]) {
                    // Secret 파일을 ./src/main/resources에 저장
                    sh 'mkdir -p ./src/main/resources'
                    sh 'chmod 644 ./src/main/resources/application.properties'
                    sh "cp ${APP_PROPS} ./src/main/resources/application.properties"
                }
            }
        }

        stage('Build Spring Boot') {
            steps {
                // Gradle 실행 권한 부여
				        sh 'chmod +x gradlew'
				        // Spring Boot 프로젝트 빌드
                sh './gradlew clean build'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonarqube-token') {
                    // gradlew를 사용해 SonarQube 분석 실행
                    sh """
                    ./gradlew sonarqube \
                        -Dsonar.projectKey=${env.SONAR_PROJECT_KEY} \
                        -Dsonar.host.url=${env.SONAR_HOST_URL} \
                        -Dsonar.login=${env.SONAR_LOGIN}
                    """
                }
            }
        }\

        stage('Docker Login') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-account', usernameVariable: 'DOCKERHUB_ID', passwordVariable: 'DOCKERHUB_PW')]) {
                    // DockerHub 로그인
                    sh "echo ${DOCKERHUB_PW} | docker login -u ${DOCKERHUB_ID} --password-stdin"
                }
            }
        }

        stage('Build & Push Image') {
            steps {
                script {
                    // Docker 이미지를 빌드하고 DockerHub에 푸시
                    docker.build("${env.IMAGE_NAME}:${env.IMAGE_TAG}")
                        .push()
                }
            }
        }

        stage('Security Scan with Trivy') {
            steps {
                script {
                    // 1차: 실패 조건용 스캔
                    sh """
                    echo "[1/2] Checking CRITICAL/HIGH vulnerabilities..."
                    docker run --rm \
                        -v /var/run/docker.sock:/var/run/docker.sock \
                        -v ${env.WORKSPACE}/trivy-cache:/root/.cache/ \
                        aquasec/trivy:latest image \
                        --exit-code 1 \
                        --severity CRITICAL,HIGH \
                        --no-progress \
                        ${env.IMAGE_NAME}:${env.IMAGE_TAG}
                    """

                    // 2차: 리포트 저장용 스캔 (MEDIUM 이하)
                    sh """
                    echo "[2/2] Saving report of MEDIUM and below..."
                    mkdir -p trivy-reports
                    docker run --rm \
                        -v /var/run/docker.sock:/var/run/docker.sock \
                        -v ${env.WORKSPACE}/trivy-cache:/root/.cache/ \
                        -v ${env.WORKSPACE}/trivy-reports:/reports \
                        aquasec/trivy:latest image \
                        --exit-code 0 \
                        --severity UNKNOWN,LOW,MEDIUM \
                        --no-progress \
                        --format table \
                        --output /reports/medium_and_below.txt \
                        ${env.IMAGE_NAME}:${env.IMAGE_TAG}
                    """
                }
            }

            post {
                always {
                    // Jenkins 아티팩트로 리포트 업로드
                    archiveArtifacts artifacts: 'trivy-reports/medium_and_below.txt', onlyIfSuccessful: false
                }
            }
        }


        stage('Run Spring Container') {
            steps {
                script {
                    // 기존 컨테이너가 있으면 삭제
                    sh "docker rm -f ${env.APP_NAME} || true"

                    // 새로운 컨테이너 실행 (포트 매핑: 호스트 8081 -> 컨테이너 8080)
                    sh """
                    docker run -d --name ${env.APP_NAME} \
                        -p 8081:8080 \
                        --network spring-mongo-net \
                        ${env.IMAGE_NAME}:${env.IMAGE_TAG}
                    """
                }
            }
        }
    }
}