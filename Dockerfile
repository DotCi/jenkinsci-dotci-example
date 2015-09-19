FROM jenkins:1.609.3

# list of jenkins plugins to install
COPY plugins.txt /usr/share/jenkins/ref/

# download jenkins plugins from JENKINS_UC
RUN /usr/local/bin/plugins.sh /usr/share/jenkins/ref/plugins.txt

# https://github.com/groupon/DotCi/pull/142 - use http protocol instead of git+ssh protocol to clone repo
RUN curl -L https://github.com/vvitayau/DotCi/releases/download/DotCi-2.11.0%2Bpull142/DotCi.hpi -o /usr/share/jenkins/ref/plugins/DotCi.jpi

# uncomment to local plugin file(s) into docker image
#ADD *.?pi /usr/share/jenkins/ref/plugins/

# additional groovy scripts to be executed on startup
COPY *.groovy /usr/share/jenkins/ref/init.groovy.d/
