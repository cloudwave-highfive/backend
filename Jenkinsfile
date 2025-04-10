pipeline {
    agent any

    environment {
		APP_NAME  = "oliveyoung"
        IMAGE_TAG = "build-${env.BUILD_NUMBER}"
    }

    stages {
	    stage('Set Global DockerHub Account') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-account', usernameVariable: 'DOCKERHUB_ID', passwordVariable: 'DOCKERHUB_PW')]) {
                    script {
                        # DockerHub 아이디, 비밀번호 및 이미지 이름을 환경 변수로 설정
		                env.DOCKERHUB_ID = "${DOCKERHUB_ID}"
				        env.DOCKERHUB_PW = "${DOCKERHUB_PW}"
                        env.IMAGE_NAME   = "${DOCKERHUB_ID}/${env.APP_NAME}"
                    }
                }
            }
        }

        stage('Clone Repository') {
            steps {
                git branch: 'main', credentialsId: 'github-token', url: 'https://github.com/cloudwave-highfive/backend.git'
            }
        }

        stage('Build Spring Boot') {
            steps {
		        sh 'chmod +x gradlew'
                sh './gradlew clean build'
            }
        }

        stage('Docker Login') {
            steps {
                sh "echo ${env.DOCKERHUB_PW} | docker login -u ${env.DOCKERHUB_ID} --password-stdin"
            }
        }

        stage('Build & Push Image') {
            steps {
                script {
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

                    // 새로운 컨테이너 실행
                    sh """
                    docker run -d --name ${env.APP_NAME} \
                      -p 8081:8080 \
                      ${env.IMAGE_NAME}:${env.IMAGE_TAG}
                    """
                }
            }
        }
    }
}