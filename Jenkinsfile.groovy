pipeline {
  environment {
    registry = "gitdemis/curso-jenkins"
    registryCredential = 'dockerhub'
  }
 agent any
  stages {

    stage('Build Docker Image') {
            steps {
                script {
                    app = docker.build("gitdemis/flaskapi:latest")
                }
            }
        }

   stage('Push Docker Image') {
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', 'dockerhub') {
                        app.push("${env.BUILD_NUMBER}")
                        app.push("latest")
                    }
                }
            }
        }
    
    stage ('Deploy k8s') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'ssh-key', usernameVariable: 'USUARIO', passwordVariable: 'CONTRASENIA')]) {
          sh '''
           sshpass -p ${CONTRASENIA} ssh -o StrictHostKeyChecking=no ${USUARIO}@54.153.2.192 'kubectl create deployment flaskapi --image=mguazzardo/flaskapi -n flaskapi'
          '''
        }
        
        
      }
    }
    
    
    }   


}
