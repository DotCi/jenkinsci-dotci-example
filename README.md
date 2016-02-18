## Fork your own custom jenkins + [DotCi](http://groupon.github.io/DotCi/) plugin

### Background

This repo is about creating your own jenkins with [DotCi](http://groupon.github.io/DotCi/). Only the github org admin of a repo can create __New DotCi job__ for its org/repo. If you are __NOT__ the admin, you can still fork then create a new job for your forked repo. Then you can pilot test your changes to see its green build result before generating a PR back into the original org/repo. Explore the following options with your existing DotCi build system before creating an entire new DotCi build system.

### Summary

This sample project is meant to be forked and customized to your needs. It details basic configurations and list of jenkins plugins depended by [DotCi](https://github.com/groupon/DotCi). There are two git submodules references in this repository that require ``git submodules init`` and ``git submodules update`` to be present. You can fork and customize those two additional repositories __OR__ in-line it directly. If you do choose to remove these git submodules,  I recommend ``docker publish`` and reference those _image:_ within _[docker-compose.yml](docker-compose.yml)_

 * [https://github.com/DotCi/jenkinsci](https://github.com/DotCi/jenkinsci) is modified [https://github.com/jenkinsci/docker](https://github.com/jenkinsci/docker) to control [jenkins version](https://jenkins-ci.org/changelog)
 * [https://github.com/DotCi/jenkinsci-slave](https://github.com/DotCi/jenkinsci-slave) definition

### [PreRequisites](http://groupon.github.io/DotCi/installation/PreRequisites.html)

#### Mongo DB

Install [mongodb](https://www.mongodb.org/) in a location accessible to your Jenkins instance.

#### Github Applications

Register an [OAuth
Application](https://github.com/settings/applications/new) with GitHub
to generate __Client ID__ and __Client Secret__. The __Authorization callback URL__ needs to be `http://<YOUR-JENKINS-URL>/dotci/finishLogin`

Register a separate [OAuth
Application](https://github.com/settings/applications/new) with GitHub
to generate __Client ID__ and __Client Secret__. The __Authorization callback URL__ needs to be `http://<YOUR-JENKINS-URL>/securityRealm/finishLogin`. This will later be used in conjuction to enable  [Matrix-based+security](https://wiki.jenkins-ci.org/display/JENKINS/Matrix-based+security).

[configure-dotci.groovy](configure-dotci.groovy) is a sample configuration using [Jenkins+Script+Console](https://wiki.jenkins-ci.org/display/JENKINS/Jenkins+Script+Console) to apply both values above and more.

#### [Ngrok](https://ngrok.com) for local development
The build setup for `https://github.com/<ORG>/<REPO>/settings/hooks` may not be able to reach your local  `http://<YOUR-JENKINS-URL>/githook`. Therefore, install [ngrok](https://ngrok.com), a tool to secure introspectable tunnels to localhost. Then commits to the repo will be able to trigger a new build for its corresponding `http://<YOUR-JENKINS-URL>/job/<ORG>/job/<REPO>`
```
curl -sH 'Accept-encoding: gzip' https://dl.ngrok.com/ngrok_2.0.19_darwin_amd64.zip | jar x && chmod 755 ./ngrok
sudo mv ./ngrok /usr/local/bin/
ngrok http <IP>:<PORT>
```

### Build / Run
```
docker build -t jenkins:1.648 jenkinsci
docker-compose build
docker-compose up -d
```

### Logs / Status
```
docker-compose logs
docker-compose ps
```

### Teardown / Cleanup

Flag -vf include removing volumes associated with containers to ensure recovery of diskspace and future fresh installation, esp if volume is not hard mounted.
Highly recommend cleanup before running new build to insure residue volume from previous build is not tainting current setup.
```
docker-compose stop
docker-compose rm -vf
```

### Files

#### Groovy scripts
 * [configure-dotci.groovy](configure-dotci.groovy) is a dotci configuration using [Jenkins+Script+Console](https://wiki.jenkins-ci.org/display/JENKINS/Jenkins+Script+Console) to __CUSTOMIZE__ your settings.
 * [configure-jenkins.groovy](configure-jenkins.groovy) sets the master executor to zero. This forces use of jenkins slave to build and unburdens master. Segregate frequent slave customization for build needs from master customization that may require jenkins restart.

#### [plugins.txt](plugins.txt)
list of jenkins plugins that dotci depends to install during creation of image.
 * To organize many plugins, break the list out to multiple files. Edit Dockerfile to add/run those. This improves Docker build cache to only re-download frequent changes if that file is last one in list.
 * see https://updates.jenkins-ci.org/download/plugins/

#### [docker-compose.yml](docker-compose.yml)
Compose is a tool for defining and running multi-container applications with Docker. With Compose, you define a multi-container application in a single file, then spin your application up in a single command.
  * doc: https://docs.docker.com/compose/
  * ref: https://docs.docker.com/compose/yml/
  * cli: https://docs.docker.com/compose/reference/

#### [Dockerfile](Dockerfile)
  * doc: http://docs.docker.com/mac/started/
  * ref: https://docs.docker.com/reference/builder/
  * cli: https://docs.docker.com/reference/run/ - PLEASE use ``docker-compose up`` instead ``docker run``.
