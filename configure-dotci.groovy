import hudson.model.*
import hudson.security.*
import jenkins.model.*
import jenkins.security.*
import org.jenkinsci.plugins.GithubSecurityRealm
import com.groupon.jenkins.SetupConfig

// https://github.com/groupon/DotCi/blob/master/src/main/java/com/groupon/jenkins/SetupConfig.java
SetupConfig config = SetupConfig.get()
def instance = Jenkins.getInstance()
def env = System.getenv()


/////////////
// generic //
/////////////
location = jenkins.model.JenkinsLocationConfiguration.get()
location.setUrl("http://CHANGE_ME/")
location.setAdminAddress("CHANGE_ME@xxx.com")


////////////////
// build type //
////////////////
config.setLabel("docker")
// config.setDefaultBuildType("com.groupon.jenkins.buildtype.dockercompose.DockerComposeBuild")
config.setDefaultBuildType("com.groupon.jenkins.buildtype.install_packages.InstallPackagesBuild")
config.setFromEmailAddress("CHANGE_ME@xxx.com")
config.save()
println "--> configured the default build type and jenkins label for new DotCi projects into " + env['JENKINS_HOME'] + "/com.groupon.jenkins.SetupConfig.xml"


//////////
// smtp //
//////////
def desc = instance.getDescriptor("hudson.tasks.Mailer")
desc.setSmtpAuth("user", "password")
desc.setReplyToAddress("CHANGE_ME@xxx.com")
desc.setSmtpHost("mailman")
desc.setUseSsl(false)
desc.setSmtpPort("25")
desc.setCharset("UTF-8")
instance.save()
println "--> configured smpt to host:mailman port:25 into " + env['JENKINS_HOME'] + "/config.xml"


/////////////
// mongodb //
/////////////
config.setDbHost("mongodb")
config.setDbPort(27017)
config.setDbName("dotci")
config.save()
println "--> configured mongodb:27017/dotci into " + env['JENKINS_HOME'] + "/com.groupon.jenkins.SetupConfig.xml"


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// github.com => Account settings => Org => Applications => Register new application => callback url:http://xx.xx.xx.xx:port/dotci/finishLogin //
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// This next configuration is responsible for enabling github commits to trigger builds on the DotCi docker container.
// It auto-defaults to your docker ip/port. You NEED to manually set this value if github can not reach your docker ip/port or /configure after jenkins startup.
// Your localhost is not registered in DNS so you'll notice that https://github.com/org/repo/settings/hooks will fail to deliver.
// Consider using https://ngrok.com, a tool to expose your localhost docker ip/port behind a NAT or firewall to the internet.
// config.setGithubCallbackUrl("http://CHANGE_ME.ngrok.io/githook/")

config.setGithubWebUrl("https://github.com")
config.setGithubApiUrl("https://github.com/api/v3")
config.setGithubClientID("CHANGE_ME")
config.setGithubClientSecret("CHANGE_ME")
config.setPrivateRepoSupport(false)
config.save()
println "--> configured https://github.com/settings/applications/CHANGE_ME into " + env['JENKINS_HOME'] + "/com.groupon.jenkins.SetupConfig.xml"


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// github.com => Account settings => Org => Applications => Register new application => callback url:https://xx.xx.xx.xx:port/securityRealm/finishLogin //
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// https://github.com/jenkinsci/github-oauth-plugin/blob/github-oauth-0.20/src/main/java/org/jenkinsci/plugins/GithubSecurityRealm.java#L115-L116
instance.setSecurityRealm( new GithubSecurityRealm( "https://github.com", "https://github.com/api/v3", "CHANGE_ME_CLIENT_ID", "CHANGE_ME_CLIENT_SECRET"))
instance.save()
println "--> configured securityRealm to https://github.com/settings/applications/CHANGE_ME into " + env['JENKINS_HOME'] + "/config.xml"


///////////////////////////////
// Configure Global Security //
///////////////////////////////
// the advanatge of performing new authorization is to flush out any modification outside this groovy script
def auth = new GlobalMatrixAuthorizationStrategy();

auth.add(jenkins.model.Jenkins.ADMINISTER,"vvitayau")
auth.add(jenkins.model.Jenkins.READ,"srlochen")
auth.add(jenkins.model.Jenkins.READ,"suryagaddipati")

///////////////////////////////////////////////////////////////////////
// https://wiki.jenkins-ci.org/display/JENKINS/Matrix-based+security //
///////////////////////////////////////////////////////////////////////
auth.add(jenkins.model.Jenkins.READ,"anonymous")
auth.add(hudson.model.Computer.CONNECT,"anonymous")
auth.add(hudson.model.Computer.DISCONNECT,"anonymous")
auth.add(hudson.model.Item.BUILD,"anonymous")
auth.add(hudson.model.Item.CANCEL,"anonymous")
// auth.add(hudson.model.Item.CONFIGURE,"anonymous")
// auth.add(hudson.model.Item.CREATE,"anonymous")
auth.add(hudson.model.Item.DISCOVER,"anonymous")
auth.add(hudson.model.Item.READ,"anonymous")
auth.add(hudson.model.Item.WORKSPACE,"anonymous")
// auth.add(hudson.model.Run.DELETE,"anonymous")
// auth.add(hudson.model.Run.UPDATE,"anonymous")
// auth.add(hudson.model.View.CREATE,"anonymous")
auth.add(hudson.model.View.READ,"anonymous")

instance.setAuthorizationStrategy(auth)
instance.save()
println "--> configured users of GlobalMatrixAuthorizationStrategy into " + env['JENKINS_HOME'] + "/config.xml"
