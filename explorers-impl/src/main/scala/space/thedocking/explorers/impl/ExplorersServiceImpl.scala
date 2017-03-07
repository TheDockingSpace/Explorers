package space.thedocking.explorers.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import space.thedocking.explorers.api.ExplorersService
import scala.concurrent.Future
import ammonite.ops
import ammonite.ops._
import scala.concurrent.ExecutionContext
import space.thedocking.explorers.api.FilesAndFoldersResult
import java.io.File
import scala.collection.JavaConverters._
import java.security.MessageDigest
import java.security.DigestInputStream

/**
  * Implementation of the ExplorersService.
  */
class ExplorersServiceImpl(persistentEntityRegistry: PersistentEntityRegistry)(
    implicit ec: ExecutionContext)
    extends ExplorersService {

  /**
    * List the contents of a path. Lists all roots on windows if path is empty
    */
  override def ls(path: String) = ServiceCall { _ =>
    Future {
      val listing = {
        {
          val trimmedPath =
            if (path.trim.endsWith("/"))
              path.trim.substring(0, path.trim.lastIndexOf("/"))
            else path.trim
          // decide which path to list
          if (trimmedPath.isEmpty
              //TODO suggest that amm Util moves to ops
              && System.getProperty("os.name").startsWith("Windows")) {
            // list roots if path is empty on windows
            File.listRoots.map(r => Path(r.getName)).toList
          } else {
            val givenPath =
              if (trimmedPath.isEmpty) root else root / RelPath(trimmedPath)
            if (!givenPath.isDir) {
              // return the path itself if it is not a directory
              Seq(givenPath)
            } else {
              // list the content of the given path
              ops.ls ! givenPath
            }
          }
        } | { path =>
          // transform each path in a (isDir: Boolean, name: String) tuple
          (path.isDir, path.name)
        }
      }
      // use the isDir value to group the paths
        .groupBy(_._1)
        .mapValues(_.map(_._2))

      // get the requested group, returning empty list case it is not available for the given path
      def content(dir: Boolean) = listing.get(dir).map(_.toList).getOrElse(Nil)
      // fill the expected result type
      FilesAndFoldersResult(files = content(false), folders = content(true))
    }
  }

  /**
    * Calculates the md5 of a certain file
    */
  override def md5(file: String) = ServiceCall { _ =>
    Future {
      // instance md5 digest
      val md = MessageDigest.getInstance("MD5")
      // decorate InputStream to update the digest
      val dis =
        new DigestInputStream(read.getInputStream(root / RelPath(file)), md)
      val buffer = new Array[Byte](1024)
      while (dis.read(buffer) != -1) {
        // nothing needed here. dis is updating md every read
      }
      dis.close
      //make the hex string
      md.digest.map("%02X" format _).mkString
    }
  }

}
