import jenkins.model.*
import com.cloudbees.plugins.credentials.*

def getNodes() {
// Returns all Nodes in the system, excluding Jenkins instance itself which represents the built-in node
// (in other words, this only returns agents).
  Jenkins.get().getNodes()
}

def getNodesAndMaster() {
  Jenkins.get().getComputers().collect { it.getNode() }
}

def getComputers() {
  Jenkins.get().getComputers()
}

def setOnline(computers) {
  computers.each {
    if (it.isOffline()) {
      it.doToggleOffline()
      println "${it.getDisplayName()} is now online."
    }
    else {
        println "${it.getDisplayName()} already online."
    }
  }
}

def setOffline(computers) {
  computers.each {
    if (it.isOffline()) {
      println "${it.getDisplayName()} already offline."
    }
    else {
      if (it.isIdle()) {
        it.doToggleOffline()
        println "${it.getDisplayName()} is now offline."
      }
      else {
        println "${it.getDisplayName()} is busy."
      }
    }
  }
}

def deleteWorkspaceContents(nodes) {
  nodes.each {
    it.createPath(it.getRootPath().getRemote() + '/workspace').deleteContents()
  }
}

def getLabels() {
  // or maybe use getAssignedLabels()
  Jenkins.get().getComputers().collect { it.getNode().getSelfLabel().toString() }
}

// examples
//def action = { sh 'uptime' }
//def action = { println "uname -a".execute().text }

def run(labels, action) {
  labels.each {
    node(it) {
      action()
    }
  }
}

def getCredentials() {
  CredentialsProvider.lookupCredentials(Credentials, Jenkins.instance, null, null)
}

@NonCPS
def printCredentials(credentials) {
  credentials.sort{it.id}.each {
    println it.properties
              .sort{it.key}
              .findAll{!['class', 'descriptor'].contains(it.key)}
              .collect{it}
              .join('\n')
    println()
  }
}
