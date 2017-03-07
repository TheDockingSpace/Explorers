package space.thedocking.explorers.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import space.thedocking.explorers.api.ExplorersService
import com.softwaremill.macwire._
import com.lightbend.lagom.scaladsl.playjson.{
  JsonSerializerRegistry,
  JsonSerializer
}
import space.thedocking.explorers.api.FilesAndFoldersResult
import scala.collection.immutable

class ExplorersLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new ExplorersApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(
      context: LagomApplicationContext): LagomApplication =
    new ExplorersApplication(context) with LagomDevModeComponents

  override def describeServices = List(
    readDescriptor[ExplorersService]
  )
}

abstract class ExplorersApplication(context: LagomApplicationContext)
    extends LagomApplication(context)
    with CassandraPersistenceComponents
    with AhcWSComponents {

  // Bind the services that this server provides
  override lazy val lagomServer = LagomServer.forServices(
    bindService[ExplorersService].to(wire[ExplorersServiceImpl])
  )

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry = ExplorersSerializerRegistry

}

/**
  * Akka serialization, used by both persistence and remoting, needs to have
  * serializers registered for every type serialized or deserialized. While it's
  * possible to use any serializer you want for Akka messages, out of the box
  * Lagom provides support for JSON, via this registry abstraction.
  *
  * The serializers are registered here, and then provided to Lagom in the
  * application loader.
  */
object ExplorersSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: immutable.Seq[JsonSerializer[_]] = immutable.Seq(
    JsonSerializer[FilesAndFoldersResult]
  )
}
