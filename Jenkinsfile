pipeline {
    agent {
        docker { image 'openjdk:8-jdk' }
    }
    stages {
        stage('Build') {
            environment {
                GRADLE_USER_HOME = '.gradle/user_home'
            }
            steps {
                cache(maxCacheSize: 4096, caches: [
                        [$class: 'ArbitraryFileCache', excludes: 'modules-2/modules-2.lock,*/plugin-resolution/**', includes: '**/*', path: '.gradle/user_home/caches'],
                        [$class: 'ArbitraryFileCache', excludes: '', includes: '**/*', path: '.gradle/user_home/wrapper'],
                ]) {
                    sh './gradlew build'
                }
                archiveArtifacts 'build/libs/*.jar', fingerprint: true
            }
        }
    }
}