job "presto-consul-demo" {
  type = "service"
  datacenters = ["dc1"]

  group "presto-consul" {

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
        name = "presto"
        port = "http"
      }
      resources {
        network {
          port "http" {
            static = 8080
            to     = 8080
          }
        }
        memory = 2048
      }
    }

  }
}
