package space.thedocking.explorers.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import space.thedocking.explorers.api.ExplorersService
import scala.concurrent.Future
import ammonite.ops
import ammonite.ops._
import scala.concurrent.ExecutionContext
import space.thedocking.explorers.api.FilesAndFoldersResult

/**
 * Implementation of the ExplorersService.
 */
class ExplorersServiceImpl(persistentEntityRegistry: PersistentEntityRegistry)(implicit ec: ExecutionContext) extends ExplorersService {

  override def ls(path: String) = ServiceCall { _ =>
    // Lists the contents of the path
    Future {
      val wd = if (path.isEmpty) root else root / path
      val listing = {
        ops.ls ! wd | { element =>
          (element.name, element.isDir)
        }
      }.groupBy(_._2).mapValues(_.map(_._1))
      def content(dir : Boolean) = listing.get(dir).map(_.toList).getOrElse(Nil)
      FilesAndFoldersResult(files = content(false), folders= content(true))
    }
  }

  override def useGreeting(id: String) = ServiceCall { request =>
    // Look up the Explorers entity for the given ID.
    val ref = persistentEntityRegistry.refFor[ExplorersEntity](id)

    // Tell the entity to use the greeting message specified.
    ref.ask(UseGreetingMessage(request.message))
  }
}
