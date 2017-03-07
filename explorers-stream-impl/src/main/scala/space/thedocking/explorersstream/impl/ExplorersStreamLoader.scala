package space.thedocking.explorersstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import space.thedocking.explorersstream.api.ExplorersStreamService
import space.thedocking.explorers.api.ExplorersService
import com.softwaremill.macwire._

class ExplorersStreamLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new ExplorersStreamApplication(context) {
      override def serviceLocator = NoServiceLocator
    }

  override def loadDevMode(
      context: LagomApplicationContext): LagomApplication =
    new ExplorersStreamApplication(context) with LagomDevModeComponents

  override def describeServices = List(
    readDescriptor[ExplorersStreamService]
  )
}

abstract class ExplorersStreamApplication(context: LagomApplicationContext)
    extends LagomApplication(context)
    with AhcWSComponents {

  // Bind the services that this server provides
  override lazy val lagomServer = LagomServer.forServices(
    bindService[ExplorersStreamService].to(wire[ExplorersStreamServiceImpl])
  )

  // Bind the ExplorersService client
  lazy val explorersService = serviceClient.implement[ExplorersService]
}
