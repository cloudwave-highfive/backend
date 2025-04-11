pipeline {
    agent any

    environment {
		APP_NAME  = "oliveyoung"  // 애플리케이션 이름 설정
        IMAGE_TAG = "build-${env.BUILD_NUMBER}"  // 빌드 번호를 사용하여 이미지 태그 설정
    }

    stages {
	    stage('Set Global DockerHub Account') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-account', usernameVariable: 'DOCKERHUB_ID', passwordVariable: 'DOCKERHUB_PW')]) {
                    script {
                        // DockerHub 아이디와 비밀번호를 환경 변수로 설정
		                env.DOCKERHUB_ID = "${DOCKERHUB_ID}"  // DockerHub 아이디
				        env.DOCKERHUB_PW = "${DOCKERHUB_PW}"  // DockerHub 비밀번호
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

        stage('Docker Login') {
            steps {
                // DockerHub 로그인
                sh "echo ${env.DOCKERHUB_PW} | docker login -u ${env.DOCKERHUB_ID} --password-stdin"
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