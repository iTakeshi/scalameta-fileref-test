package macros

import java.util.Properties
import java.io.FileInputStream
import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.language.experimental.macros
import scala.macros._
import scala.util.control.Exception

object Helpers {

  def getString(key: String): Option[String] = propsOpt.map(_.get(key)).map { value =>
    val str = value.toString
    if (str.startsWith("\"") && str.endsWith("\"") && str.length >= 2) {
      str.substring(1, str.length - 1)
    }
    else str
  }

  private[this] val propsOpt: Option[Properties] = Exception.allCatch.opt {
    def using[A <% { def close():Unit }](s: A)(f: A => Any) = try f(s) finally s.close()
    val fileName = "project/fileref.properties"
    val props = new Properties
    using(new FileInputStream(fileName))(props.load(_))
    props
  }
}

class fileref extends MacroAnnotation {

  def apply(defn: Any): Any = macro {
    val value = Helpers.getString("key").get
    defn match {
      case q"..$mods object $ename extends $template" =>
        val newTemplate = template match {
          case template"{ ..$estats } with ..$inits { $self => ..$stats }" =>
            val newStats = stats :+ q"def printString = println(${Lit.String(value)})"
            template"{ ..$estats } with ..$inits { $self => ..$newStats }"
          case otherwise => otherwise
        }
        q"..$mods object $ename extends $newTemplate"
      case otherwise => otherwise
    }
  }
}
