job "consul-demo" {
  type = "service"
  datacenters = ["dc1"]

  group "consul" {

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
  }
}
