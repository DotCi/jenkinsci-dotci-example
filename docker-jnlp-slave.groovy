import hudson.model.*;
import hudson.slaves.*;
import jenkins.model.*;

Thread.start {
      sleep 10000
      def env = System.getenv()
      def instance = Jenkins.getInstance()

      // modify below to your needs
      println "--> adding JNLP docker slave(s)"
      List<String> clients = new ArrayList<String>()
      clients.add("docker-1")
      // clients.add("...")

      // http://javadoc.jenkins-ci.org/hudson/slaves/DumbSlave.html
      for (String client : clients) {
        println "--> creating /computer/" + client + " under /label/docker with 5 executors where docker and docker-compose is available"
        instance.addNode(
	  new DumbSlave(
		client,
                "JNLP slave with docker and docker-compose",
                "/root",
                "5",
                Node.Mode.NORMAL,
                "docker",
                new JNLPLauncher(),
                new RetentionStrategy.Always(),
                new LinkedList()
          )
        )
      }
      println "--> adding JNLP docker slave(s)... done"

      instance.save()
}
