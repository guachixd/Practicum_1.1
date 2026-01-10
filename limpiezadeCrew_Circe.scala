import scala.io.Source
import io.circe._
import io.circe.parser._
import io.circe.generic.auto._
import io.circe.syntax._

case class MiembroEquipo(
  credit_id: Option[String],
  department: Option[String],
  gender: Option[Int],
  id: Option[Int],
  job: Option[String],
  name: Option[String],
  profile_path: Option[String]
)

object AnalizadorEquipoProduccion extends App {

  val rutaArchivo = "src/main/resources/data/pi-movies-complete-2026-01-08 (1).csv"

  val archivo = Source.fromFile(rutaArchivo, "UTF-8")
  val lineasArchivo = archivo.getLines().toList
  archivo.close()

  val encabezados = lineasArchivo.head.split(";").map(_.trim)
  val posicionCrew = encabezados.indexOf("crew")

  if (posicionCrew == -1) {
    println("Error: No se encontro la columna crew")
    sys.exit(1)
  }

  def prepararJSON(textoJSON: String): String =
    textoJSON
      .trim
      .replaceAll("'", "\"")
      .replaceAll("None", "null")
      .replaceAll("True", "true")
      .replaceAll("False", "false")
      .replaceAll("""\\""", "")

  def normalizarCampo(campo: String): Option[String] =
    val limpio = campo.trim.replaceAll("\\s+", " ")
    if limpio.isEmpty then None else Some(limpio)

  def procesarEquipo(lista: List[MiembroEquipo]): List[MiembroEquipo] =
    lista
      .map(m =>
        m.copy(
          credit_id = m.credit_id.flatMap(normalizarCampo),
          name = m.name.flatMap(normalizarCampo),
          department = m.department.flatMap(normalizarCampo),
          job = m.job.flatMap(normalizarCampo),
          profile_path = m.profile_path.flatMap(normalizarCampo)
        )
      )
      .distinct

  def dividirLineaCSV(linea: String): Array[String] =
    val (campos, buffer, _) =
      linea.foldLeft((Vector.empty[String], new StringBuilder, false)) {
        case ((acc, buf, dentro), c) =>
          c match
            case '"' => (acc, buf, !dentro)
            case ';' if !dentro =>
              (acc :+ buf.toString, new StringBuilder, false)
            case _ =>
              buf.append(c)
              (acc, buf, dentro)
      }
    (campos :+ buffer.toString).toArray

  val equipoExtraido: List[MiembroEquipo] =
    lineasArchivo.tail.flatMap { linea =>
      val columnas = dividirLineaCSV(linea)

      if columnas.length > posicionCrew then
        val crew = columnas(posicionCrew).trim

        if crew.nonEmpty && crew != "[]" then
          try
            decode[List[MiembroEquipo]](prepararJSON(crew)) match
              case Right(lista) => lista
              case Left(_) => Nil
          catch
            case _: Exception => Nil
        else Nil
      else Nil
    }

  val equipoProcesado = procesarEquipo(equipoExtraido)

  val credencialesNull = equipoProcesado.count(_.credit_id.isEmpty)
  val nombresVacios = equipoProcesado.count(_.name.isEmpty)
  val departamentosNull = equipoProcesado.count(_.department.isEmpty)
  val generosNull = equipoProcesado.count(_.gender.isEmpty)
  val identificadoresNull = equipoProcesado.count(_.id.isEmpty)
  val trabajosNull = equipoProcesado.count(_.job.isEmpty)
  val perfilesNull = equipoProcesado.count(_.profile_path.isEmpty)

  println("=" * 60)
  println("ANALISIS DE EQUIPO DE PRODUCCION")
  println("=" * 60)
  println(s"Total de miembros procesados: ${equipoProcesado.size}")
  println(s"Credit id nulos: $credencialesNull")
  println(s"Nombres nulos: $nombresVacios")
  println(s"Departamentos nulos: $departamentosNull")
  println(s"Genero nulo: $generosNull")
  println(s"Ids nulos: $identificadoresNull")
  println(s"Trabajos nulos: $trabajosNull")
  println(s"Perfiles nulos: $perfilesNull")

  println("\n" + "=" * 60)
  println("MUESTRA (5 REGISTROS)")
  println("=" * 60)

  equipoProcesado.take(5).foreach { m =>
    println(s"Nombre: ${m.name.getOrElse("null")}")
    println(s"Departamento: ${m.department.getOrElse("null")}")
    println(s"Rol: ${m.job.getOrElse("null")}")
    println("-" * 40)
  }

  println("\n" + "=" * 60)
  println("ANALISIS POR GENERO")
  println("=" * 60)

  equipoProcesado
    .filter(_.gender.isDefined)
    .groupBy(_.gender.get)
    .map((g, l) => (g, l.size))
    .toSeq
    .sortBy(_._1)
    .foreach { case (g, c) =>
      val etiqueta =
        g match
          case 0 => "No especificado"
          case 1 => "Femenino"
          case 2 => "Masculino"
          case _ => s"Otro ($g)"
      println(s"$etiqueta: $c")
    }

  println("\n" + "=" * 60)
  println("DEPARTAMENTOS TOP 10")
  println("=" * 60)

  equipoProcesado
    .filter(_.department.isDefined)
    .groupBy(_.department.get)
    .map((d, l) => (d, l.size))
    .toSeq
    .sortBy(-_._2)
    .take(10)
    .foreach { case (d, c) =>
      println(s"$d: $c")
    }

  println("\n" + "=" * 60)
  println("ROLES TOP 10")
  println("=" * 60)

  equipoProcesado
    .filter(_.job.isDefined)
    .groupBy(_.job.get)
    .map((r, l) => (r, l.size))
    .toSeq
    .sortBy(-_._2)
    .take(10)
    .foreach { case (r, c) =>
      println(s"$r: $c")
    }

  println("\n" + "=" * 60)
  println("MUESTRA JSON")
  println("=" * 60)

  println(equipoProcesado.take(3).asJson.spaces2)

  println("\n" + "=" * 60)
  println("PROCESO FINALIZADO")
  println("=" * 60)
}
