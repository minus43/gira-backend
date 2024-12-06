pipeline {
    agent any

    environment {
        REGION = "ap-northeast-2" // AWS 리전 (서울 리전)
        ECR_URL = "361769560582.dkr.ecr.ap-northeast-2.amazonaws.com/gira-repo" // AWS ECR URL
        DEPLOY_HOSTS = "172.31.24.122,172.31.26.199,172.31.19.98" // 배포할 EC2 인스턴스의 프라이빗 IP 주소
        SERVICES = "gira-eureka,gira-gateway,gira-user" // 현재 빌드 및 배포할 서비스
        PORTS = "8761,8181,8182" // 현재 서비스의 포트
    }

    stages {
        // **1단계: 소스 코드 가져오기**
        stage('Pull Codes from GitHub') {
            steps {
                checkout scm // GitHub에서 소스 코드를 체크아웃
            }
        }

        // **2단계: Docker 이미지 빌드 및 ECR 푸시**
        stage('Build and Push Docker Image') {
            steps {
                withAWS(region: "${REGION}", credentials: "aws-key") { // AWS 자격 증명으로 ECR에 로그인
                    script {
                        def services = SERVICES.split(',') // 서비스 목록을 배열로 변환
                        services.each { service ->
                            stage("Build and Push ${service}") {
                                sh """
                                    docker image prune -a -f
                                    aws ecr get-login-password --region ${REGION} | docker login --username AWS --password-stdin ${ECR_URL}
                                    docker build -t gira-repo ./${service}
                                    docker tag gira-repo:latest ${ECR_URL}:${service}
                                    docker push ${ECR_URL}:${service}
                                    docker system prune -f
                                """
                            }
                        }
                    }
                }
            }
        }

        // **3단계: EC2 인스턴스에 서비스 배포**
        stage('Deploy to AWS EC2') {
            steps {
                script {
                    def services = SERVICES.split(',')
                    def ports = PORTS.split(',')

                     services.eachWithIndex { service, index ->
                        sshPublisher(
                            publishers: [
                                sshPublisherDesc(
                                    configName: service, // 서비스 이름을 SSH 서버 이름으로 사용
                                    transfers: [
                                        sshTransfer(
                                            sourceFiles: "",
                                            execCommand: """
                                                aws ecr get-login-password --region ${REGION} | docker login --username AWS --password-stdin ${ECR_URL}
                                                docker pull ${ECR_URL}:${service}
                                                docker stop ${service} || true
                                                docker rm ${service} || true
                                                docker run -d \
                                                    -p ${ports[index]}:${ports[index]} \
                                                    --network host \
                                                    --name ${service} ${ECR_URL}:${service}
                                                docker system prune -f
                                                docker image prune -a -f
                                            """
                                        )
                                    ],
                                    usePromotionTimestamp: false,
                                    verbose: true
                                )
                            ]
                        )
                    }
                }
            }
        }
    }
}
