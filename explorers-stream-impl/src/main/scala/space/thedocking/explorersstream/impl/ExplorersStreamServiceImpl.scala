package space.thedocking.explorersstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import space.thedocking.explorersstream.api.ExplorersStreamService
import space.thedocking.explorers.api.ExplorersService

import scala.concurrent.Future

/**
  * Implementation of the ExplorersStreamService.
  */
class ExplorersStreamServiceImpl(explorersService: ExplorersService)
    extends ExplorersStreamService {
  def stream = ServiceCall { lss =>
    Future.successful(lss.mapAsync(8)(explorersService.ls(_).invoke()))
  }
}
