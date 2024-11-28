pipeline {
    agent any

    environment {
        REGION = "ap-northeast-2" // AWS 리전 (서울 리전)
        ECR_URL = "361769560582.dkr.ecr.ap-northeast-2.amazonaws.com/gira-repo" // AWS ECR URL
        DEPLOY_HOSTS = "172.31.24.122" // 배포할 EC2 인스턴스의 프라이빗 IP 주소
        SERVICES = "gira-eureka" // 쉼표로 구분된 서비스 목록
        PORTS = "8761" // 쉼표로 구분된 포트 목록
    }

    stages {
        // **1단계: 소스 코드 가져오기**
        stage('Pull Codes from GitHub') {
            steps {
                checkout scm // GitHub에서 소스 코드를 체크아웃
            }
        }

        // **2단계: Docker 이미지 빌드 및 ECR 푸시**
        stage('Build and Push Docker Images') {
            steps {
                withAWS(region: "${REGION}", credentials: "aws-key") { // AWS 자격 증명으로 ECR에 로그인
                    script {
                        // SERVICES 배열에 정의된 각 마이크로서비스에 대해 반복하며 Docker 이미지를 빌드하고 푸시
                        def services = SERVICES.split(',') // 쉼표로 구분된 서비스 목록을 배열로 변환
                        def ports = PORTS.split(',') // 쉼표로 구분된 포트 목록을 배열로 변환

                        services.eachWithIndex { service, index ->
                            stage("Build and Push ${service}") {
                                sh """
                                    aws ecr get-login-password --region ${REGION} | docker login --username AWS --password-stdin ${ECR_URL}
                                    docker build -t gira-repo:${service}-${BUILD_NUMBER} ./${service}
                                    docker tag gira-repo:${service}-${BUILD_NUMBER} ${ECR_URL}:gira-${service}-${BUILD_NUMBER}
                                    docker push ${ECR_URL}:gira-${service}-${BUILD_NUMBER}
                                """
                            }
                        }
                    }
                }
            }
        }

        // **3단계: EC2 인스턴스에 서비스 배포**
        stage('Deploy to AWS EC2 VMs') {
            steps {
                publishOverSsh(
                    publishers: [
                        sshPublisher(
                            configName: "your-ssh-config-name", // Jenkins에 설정된 SSH 서버 이름
                            transfers: [
                                sshTransfer(
                                    sourceFiles: "target/*.jar", // 배포할 파일 경로
                                    remoteDirectory: "/home/ec2-user/deploy", // 원격 서버의 배포 디렉토리
                                    execCommand: """
                                        docker pull your-ecr-url/your-image:${BUILD_NUMBER}
                                        docker stop your-container || true
                                        docker rm your-container || true
                                        docker run -d -p 8080:8080 --name your-container your-ecr-url/your-image:${BUILD_NUMBER}
                                    """
                                )
                            ],
                            usePromotionTimestamp: false, // 프로모션 타임스탬프 사용 안 함
                            verbose: true // 상세 로그 출력
                        )
                    ]
                )
            }
        }
    }
}
