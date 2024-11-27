pipeline {
    agent any

    environment {
        REGION = "ap-northeast-2" // AWS 리전 (서울 리전)
        ECR_URL = "361769560582.dkr.ecr.ap-northeast-2.amazonaws.com/gira-repo" // AWS ECR URL
        DEPLOY_HOSTS = "172.31.24.122" // 배포할 EC2 인스턴스의 프라이빗 IP 주소
        SERVICES = "gira-eureka" // 배포할 마이크로서비스 목록
        PORTS = "8761" // 각 서비스별 포트
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
                        for (service in SERVICES) {
                            stage("Build and Push ${service}") {
                                sh """
                                    aws ecr get-login-password --region ${REGION} | docker login --username AWS --password-stdin ${ECR_URL} // ECR에 로그인
                                    docker build -t ${service}:${BUILD_NUMBER} ./${service} // 각 서비스별로 Docker 이미지 빌드
                                    docker tag ${service}:${BUILD_NUMBER} ${ECR_URL}/${service}:${BUILD_NUMBER} // ECR에 푸시할 이미지 태그 지정
                                    docker push ${ECR_URL}/${service}:${BUILD_NUMBER} // ECR에 Docker 이미지 푸시
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
                // 각 서비스별로 EC2에 배포를 수행
                publishOverSsh(
                    publishers: SERVICES.collect { service ->
                        // 현재 서비스의 인덱스에 맞는 EC2 인스턴스 IP와 포트를 가져옵니다.
                        def host = DEPLOY_HOSTS.split(',')[SERVICES.indexOf(service)].trim() // EC2 인스턴스 IP
                        def port = PORTS[SERVICES.indexOf(service)] // 서비스에 맞는 포트를 가져옵니다.

                        sshPublisher(
                            configName: "gira-eureka-${host}", // 미리 설정된 SSH 서버 구성 이름
                            transfers: [
                                sshTransfer(
                                    sourceFiles: "${service}/build/libs/*.jar", // 빌드된 JAR 파일 경로
                                    removePrefix: "${service}/build/libs", // 파일 경로에서 접두어를 제거하여 원격 디렉토리 구조 유지
                                    remoteDirectory: "/var/www/${service}", // 원격 서버의 배포 디렉토리
                                    execCommand: """
                                        aws ecr get-login-password --region ${REGION} | docker login --username AWS --password-stdin ${ECR_URL} // ECR 로그인
                                        docker pull ${ECR_URL}/${service}:${BUILD_NUMBER} // 최신 Docker 이미지 풀
                                        docker stop ${service} || true // 기존 컨테이너가 있으면 중지
                                        docker rm ${service} || true // 기존 컨테이너가 있으면 제거
                                        docker run -d -p ${port}:${port} --name ${service} ${ECR_URL}/${service}:${BUILD_NUMBER} // 새로운 컨테이너 실행 (각 서비스마다 포트가 다름)
                                    """
                                )
                            ],
                            usePromotionTimestamp: false, // 프로모션 타임스탬프 사용 안 함
                            verbose: true // 상세 로그 출력
                        )
                    }
                )
            }
        }
    }

    post {
        // **빌드 실패 시 이메일 알림**
        failure {
            mail to: 'minu425854@gmail.com', // 실패 시 알림을 받을 이메일 주소
                 subject: "Jenkins Build Failed: ${currentBuild.fullDisplayName}", // 이메일 제목
                 body: "빌드에 실패했습니다. Jenkins에서 로그를 확인해주세요." // 이메일 본문
        }

        // **빌드 성공 시 이메일 알림**
        success {
            mail to: 'minu425854@gmail.com', // 성공 시 알림을 받을 이메일 주소
                 subject: "Jenkins Build Succeeded: ${currentBuild.fullDisplayName}", // 이메일 제목
                 body: "빌드가 성공적으로 완료되었습니다. Jenkins에서 로그를 확인하세요." // 이메일 본문
        }
    }
}
