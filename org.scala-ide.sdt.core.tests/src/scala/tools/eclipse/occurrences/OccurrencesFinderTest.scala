package scala.tools.eclipse.occurrences

import scala.tools.eclipse.javaelements._
import org.eclipse.jdt.core.IPackageFragmentRoot
import org.eclipse.jdt.core.JavaCore
import scala.tools.eclipse.testsetup.SDTTestUtils
import org.junit.Assert._
import org.eclipse.core.runtime.Path
import org.junit.Test
import org.eclipse.jface.text.Region
import scala.tools.eclipse.markoccurrences.ScalaOccurrencesFinder
import scala.tools.eclipse.ScalaWordFinder
import scala.tools.nsc.interactive.Response

object OccurrencesFinderTest {
  lazy val project = SDTTestUtils.setupProject("occurrences-hyperlinking")
  lazy val srcPackageRoot: IPackageFragmentRoot = {
    val javaProject = JavaCore.create(project.underlying)
    
    javaProject.open(null)
    javaProject.findPackageFragmentRoot(new Path("/occurrences-hyperlinking/src"))
  }
  
  assertNotNull(srcPackageRoot)
    
  srcPackageRoot.open(null)
  println("children: " + srcPackageRoot.getChildren.toList)
}

class OccurrencesFinderTest {
  import OccurrencesFinderTest._
  
  @Test def typeOccurrences() {
    val unit = srcPackageRoot.getPackageFragment("occ").getCompilationUnit("DummyOccurrences.scala").asInstanceOf[ScalaCompilationUnit];
    
    // first, 'open' the file by telling the compiler to load it
    project.withSourceFile(unit) { (src, compiler) =>
      val dummy = new Response[Unit]
      compiler.askReload(List(src), dummy)
      dummy.get
      
      val tree =  new Response[compiler.Tree]
      compiler.askType(src, false,tree)
      tree.get
    }()
    
    val contents = unit.getContents
    val positions = SDTTestUtils.markersOf(contents, "<")
    
    println("checking %d positions".format(positions.size))
    
    for ((pos, count) <- positions) {
      println("looking at position %d for %d occurrences".format(pos, count))
      val region = ScalaWordFinder.findWord(contents, pos - 1)
      val finder = new ScalaOccurrencesFinder(unit, region.getOffset, region.getLength)
      val occurrences = finder.findOccurrences
      assertTrue(finder.findOccurrences.isDefined)
      assertTrue(occurrences.get.locations.size == count)
    }
  }

}