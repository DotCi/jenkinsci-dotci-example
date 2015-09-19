## Fork your own custom jenkins + [DotCi](http://groupon.github.io/DotCi/) plugin

### Background

This repo is about creating your own jenkins + [DotCi](http://groupon.github.io/DotCi/).

Only the github org admin of a repo can create New DotCi job for that org/repo. If you are __NOT__ the admin, you can still fork then create a new DotCi job for your forked repo. Then you can pilot test your changes to see its DotCi build before generating a PR back into the original org/repo. Explore these options before exploring creating another entire DotCi build system.

### Setup

 1. github.com => Account settings => Org => Applications => Register new application => callback url:http://xx.xx.xx.xx:port/dotci/finishLogin
 2. github.com => Account settings => Org => Applications => Register new application => callback url:https://xx.xx.xx.xx:port/securityRealm/finishLogin
 3. edit configure-dotci.groovy based on above modifications
 4. use https://ngrok.com to expose your localhost docker ip/port behind a NAT or firewall to the internet.
``
curl -sH 'Accept-encoding: gzip' https://dl.ngrok.com/ngrok_2.0.19_darwin_amd64.zip | jar x && chmod 755 ./ngrok
./ngrok http `docker-machine ip dev`:80
``

### Build / Run
```
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

### Configuration Files

#### docker-compose.yml
Compose is a tool for defining and running multi-container applications with Docker. With Compose, you define a multi-container application in a single file, then spin your application up in a single command.
  * doc: https://docs.docker.com/compose/
  * ref: https://docs.docker.com/compose/yml/
  * cli: https://docs.docker.com/compose/reference/

#### Dockerfile
  * doc: http://docs.docker.com/mac/started/
  * ref: https://docs.docker.com/reference/builder/
  * cli: https://docs.docker.com/reference/run/ - PLEASE use ``docker-compose up`` instead ``docker run``.

#### plugins.txt
list of jenkins plugins that dotci depends to install during creation of image.
 * To organize many plugins, break the list out to multiple files. Edit Dockerfile to add/run those. This improves Docker build cache to only re-download frequent changes if that file is last one in list.
  * see https://updates.jenkins-ci.org/download/plugins/

#### *.groovy
Set of files to include during creation of image. These groorvy scripts will then be invoked after jenkins startup. You can add or modify more to your needs.
  * doc: https://wiki.jenkins-ci.org/display/JENKINS/Jenkins+Script+Console
  * configure-jenkins.groovy - DO NOT set master executor > 0. Zero forces use of slave so master is not burden with builds. Best practice to segregate slave frequent customization for build needs from actual jenkins master coordinating the display of its result.
  * configure-dotci.groovy - an outline of DotCi required setting that  ===> YOU MUST CUSTOMIZE <=====

### MongoDB
 * Groupon jenkins master has mongo client so you can gain access through that to test mongo connectivity and data
```
docker images -a | grep _master
docker exec -it xxx_master_1 bash
mongo mongodb:27017/dotci
```
