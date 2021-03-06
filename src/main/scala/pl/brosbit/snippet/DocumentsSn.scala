package pl.brosbit.snippet

import java.util.Date
import scala.xml.{ Text, Unparsed }
import _root_.net.liftweb._
import http.{ S, SHtml }
import common._
import util._
import mapper.{ OrderBy, Descending }
import pl.brosbit.model._
import mapper.By
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._

//for show list of all doc and create new doc
class DocumentsSn  extends BaseSnippet {
//edocuments => show edocuments list 
    
    val user =  User.currentUser.openOrThrowException("No user")
    val subjPar = S.param("s").openOr("")
    val levPar = S.param("l").openOr("3")
    val subjectId = if  (subjPar != "") subjPar else
         Subject.findAll.head._id.toString //fail for empty subject list
    
 def docList() = {
    val documents = Document.findAll(("ownerID" -> user.id.is)~("level"->levPar.toInt)~("subjectId"->subjectId))
    "tbody tr" #> documents.map(doc => "a" #> <a href={"/document/" + doc._id.toString} target="_blanck" >{ doc.title } </a> &
      ".descriptBook *" #> Text( doc.descript ) &
      ".department *" #> <em>{ doc.departmentName }</em> &
      ".subject *" #> Text(doc.subcjectName) &
      ".level *" #> Text(doc.level.toString) &
      ".department *" #> Text(doc.departmentName) &
      ".editBook *" #> <a href={"/resources/editdocument/" + doc._id.toString}> Edytuj</a>
      )
  }
 
 def showDocument() = {
     val id = S.param("id").openOr("0")
     Document.find(id) match {
         case Some(document) => {
             "#title *" #> document.title &
             "#subject *" #> document.subcjectName &
             "#level *" #> document.level.toString &
             "#department *" #> document.departmentName &
             "article *" #>  Unparsed("""<h1>%s</h1>""".format(document.title) + document.content)
             
         }
         case _ => "article" #> <h1>Nie znaleziono dokumentu!</h1>
     }     
 }
 
def selectors() = {
      val subj = Subject.findAll.map(s => (s._id.toString,  s.full))
    //val subj = Subject.findAll.map(s => s.full)
    //"#subjectSelect option" #> subj.map(s => <option value={s}>{s}</option>) &
  "#subjectSelect" #>SHtml.select(subj, Full(subjPar) , x => Unit) &
    "#levelSelect" #> SHtml.select(levList, Full(levPar) , x => Unit)
  }

}
