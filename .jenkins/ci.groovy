@Library('add-ons-shared-libs@feat/repository_deploy') _

node {
    continuousIntegrationPipeline(
        buildType: "deploy",
        sonar: [ enable: false ]
    )
}
