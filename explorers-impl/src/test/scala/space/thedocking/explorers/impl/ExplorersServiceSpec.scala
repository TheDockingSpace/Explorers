package space.thedocking.explorers.impl

import java.io.File

import akka.cluster.Cluster
import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}
import space.thedocking.explorers.api._

class ExplorersServiceSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  private val server = ServiceTest.startServer(
    ServiceTest.defaultSetup
      .withCassandra(true)
  ) { ctx =>
    new ExplorersApplication(ctx) with LocalServiceLocator
  }

  val client = server.serviceClient.implement[ExplorersService]

  override protected def afterAll() = server.stop()

  "Explorers service" should {

    "list the content of a path" in {
      client.ls("/opt").invoke().map { answer =>
        answer should ===("FAILING FOR NOW")
      }
    }

    "allow responding with a custom message" in {
      for {
        _ <- client.useGreeting("Bob").invoke(GreetingMessage("Hi"))
        answer <- client.ls("/opt").invoke()
      } yield {
        answer should ===("FAILING FOR NOW")
      }
    }
  }
}
