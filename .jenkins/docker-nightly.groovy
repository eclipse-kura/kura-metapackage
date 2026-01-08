@Library('releng-pipeline') _

timeout(time:90, unit:'MINUTES') {
    podTemplate(yaml: loadOverridableResource(libraryResource: 'org/eclipsefdn/container/agent.yml')) {
        node(POD_LABEL) {
            properties([
                buildDiscarder(logRotator(numToKeepStr: '5')),
                disableConcurrentBuilds(),
                pipelineTriggers([
                    cron('H 0 * * 6') // Run on Saturday night
                ])
            ])

            checkout scm

            withEnv(["HOME=${env.WORKSPACE}"]){
                stage('build') {
                    dir('docker') {
                        container('containertools') {
                            containerBuild(
                                credentialsId: 'docker-bot-token',
                                name: 'docker.io/eclipsekura/kura',
                                version: 'nightly',
                                dockerfile: 'Dockerfile.debian'
                            )
                        }
                    }
                }
            }
        }
    }
}
