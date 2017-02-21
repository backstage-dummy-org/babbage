#!groovy

node {
    stage('Checkout') {
        checkout scm
        sh 'git clean -dfx'
        sh 'git rev-parse --short HEAD > git-commit'
        sh 'set +e && (git describe --exact-match HEAD || true) > git-tag'
    }

    def branch   = env.JOB_NAME.replaceFirst('.+/', '')
    def revision = revisionFrom(readFile('git-tag').trim(), readFile('git-commit').trim())
    def registry = registry(branch, revision)

    stage('Build') {
        sh 'npm install --no-bin-links --prefix ./src/main/web --sixteens-branch=develop'
        sh "${tool 'm3'}/bin/mvn clean package dependency:copy-dependencies"
    }

    stage('Test') {
        def elastic = docker.image('guidof/onswebsite-search:0.0.2')
        elastic.run('-p 9266:9200 -p 9366:9300')
        elastic.stop()
    }

    stage('Image') {
        docker.withRegistry(registry['uri'], { ->
            if (registry.containsKey('login')) sh registry['login']
            docker.build(registry['image']).push(registry['tag'])
        })
    }

    stage('Bundle') {
        sh sprintf('sed -i -e %s -e %s -e %s -e %s appspec.yml scripts/codedeploy/*', [
            "s/\\\${CODEDEPLOY_USER}/${env.CODEDEPLOY_USER}/g",
            "s/^ECR_REPOSITORY_URI=.*/ECR_REPOSITORY_URI=${env.ECR_REPOSITORY_URI}/",
            "s/^GIT_COMMIT=.*/GIT_COMMIT=${revision}/",
            "s/^AWS_REGION=.*/AWS_REGION=${env.AWS_DEFAULT_REGION}/",
        ])
        sh "tar -cvzf babbage-${revision}.tar.gz appspec.yml scripts/codedeploy"
        sh "aws s3 cp babbage-${revision}.tar.gz s3://${env.S3_REVISIONS_BUCKET}/babbage-${revision}.tar.gz"
    }

    if (branch != 'develop' && branch != 'dd-develop') return

    stage('Deploy') {
        for (group in deploymentGroupsFor(branch)) {
            sh sprintf('aws deploy create-deployment %s %s %s,bundleType=tgz,key=%s', [
                '--application-name babbage',
                "--deployment-group-name ${group}",
                "--s3-location bucket=${env.S3_REVISIONS_BUCKET}",
                "babbage-${revision}.tar.gz",
            ])
        }
    }
}

def deploymentGroupsFor(branch) {
    branch == 'develop'
        ? [env.CODEDEPLOY_FRONTEND_DEPLOYMENT_GROUP, env.CODEDEPLOY_PUBLISHING_DEPLOYMENT_GROUP]
        : [env.CODEDEPLOY_DISCOVERY_FRONTEND_DEPLOYMENT_GROUP, env.CODEDEPLOY_DISCOVERY_PUBLISHING_DEPLOYMENT_GROUP]
}

def registry(branch, tag) {
    [
        hub: [
            login: 'docker --config .dockerhub login --username=$DOCKERHUB_USER --password=$DOCKERHUB_PASS',
            image: "${env.DOCKERHUB_REPOSITORY}/babbage",
            tag: 'live',
            uri: "https://${env.DOCKERHUB_REPOSITORY_URI}",
        ],
        ecr: [
            image: 'babbage',
            tag: tag,
            uri: "https://${env.ECR_REPOSITORY_URI}",
        ],
    ][branch == 'live' ? 'hub' : 'ecr']
}

@NonCPS
def revisionFrom(tag, commit) {
    def matcher = (tag =~ /^release\/(\d+\.\d+\.\d+(?:-rc\d+)?)$/)
    matcher.matches() ? matcher[0][1] : commit
}
