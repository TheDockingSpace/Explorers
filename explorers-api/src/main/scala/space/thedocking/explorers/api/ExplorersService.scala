package space.thedocking.explorers.api

import akka.{ Done, NotUsed }
import com.lightbend.lagom.scaladsl.api.{ Service, ServiceCall }
import play.api.libs.json.{ Format, Json }

/**
 * The Explorers service interface.
 * <p>
 * This describes everything that Lagom needs to know about how to serve and
 * consume the ExplorersService.
 */
trait ExplorersService extends Service {

  /**
   * Example: curl http://localhost:9000/api/ls/opt
   */
  def ls(path: String): ServiceCall[NotUsed, FilesAndFoldersResult]

  /**
   * Example: curl http://localhost:9000/api/ls
   */
  def lsRoot: ServiceCall[NotUsed, FilesAndFoldersResult] = ls("")

  /**
   * Example: curl -H "Content-Type: application/json" -X POST -d '{"message":
   * "Hi"}' http://localhost:9000/api/hello/Alice
   */
  def useGreeting(id: String): ServiceCall[GreetingMessage, Done]

  override final def descriptor = {
    import Service._
    // @formatter:off
    named("explorers").withCalls(
      pathCall("/api/ls", lsRoot),
      pathCall("/api/ls/", lsRoot),
      pathCall("/api/ls/*path", ls _),
      pathCall("/api/hello/:id", useGreeting _)).withAutoAcl(true)
    // @formatter:on
  }
}

/**
 * The greeting message class.
 */
case class GreetingMessage(message: String)

object GreetingMessage {
  /**
   * Format for converting greeting messages to and from JSON.
   *
   * This will be picked up by a Lagom implicit conversion from Play's JSON format to Lagom's message serializer.
   */
  implicit val format: Format[GreetingMessage] = Json.format[GreetingMessage]
}

sealed trait LsResult

trait Folders extends LsResult {
  val folders: List[String]
}

trait Files extends LsResult {
  val files: List[String]
}

case class FilesAndFoldersResult(override val files: List[String] = Nil, override val folders: List[String] = Nil) extends Files with Folders

object FilesAndFoldersResult {
  implicit val format: Format[FilesAndFoldersResult] = Json.format[FilesAndFoldersResult]
}