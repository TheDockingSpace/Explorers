package space.thedocking.explorers.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import space.thedocking.explorers.api.ExplorersService
import com.softwaremill.macwire._

class ExplorersLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new ExplorersApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
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

  // Register the Explorers persistent entity
  persistentEntityRegistry.register(wire[ExplorersEntity])
}
