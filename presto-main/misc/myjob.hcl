job "java-connect-demo" {
  type = "service"
  datacenters = ["dc1"]

  group "components" {

    task "vault" {
      driver = "docker"

      config {
        image = "vault"
        hostname = "vault"
      }

      service {
        port = "http"
      }
      resources {
        network {
          mode = "bridge"
          port "http" {
            static = 8200
            to     = 8200
          }
        }
      }
    }

    task "consul" {
      driver = "docker"

      config {
        image = "consul"
        hostname = "consul"
      }

      service {
        port = "http"
      }
      resources {
        network {
          mode = "bridge"
          port "http" {
            static = 8500
            to     = 8500
          }
        }
      }
    }

    volume "certs" {
      type = "host"
      read_only = false
      source = "certs"
    }

    task "java-ssl" {

      driver = "docker"
      
      volume_mount {
        volume      = "certs"
        destination = "/certs"
      }

      config {
        image = "gugalnikov/demo-java-ssl:latest"
        hostname = "java-ssl"
      }

      service {
        port = "http"
      }
      resources {
        network {
          mode = "bridge"
          port "http" {
            static = 8080
            to     = 8080
          }
          port "https" {
            static = 8443
            to     = 8443
          }
        }
      }
    }

    task "presto" {

      driver = "docker"

      volume_mount {
        volume      = "certs"
        destination = "/certs"
      }

      config {
        image = "prestosql/presto"
        hostname = "presto"
      }

      service {
        port = "http"
      }
      resources {
        network {
          mode = "bridge"
          port "http" {
            static = 9080
            to     = 8080
          }
          port "https" {
            static = 9443
            to     = 8443
          }
        }
      }
    }

  }
}
