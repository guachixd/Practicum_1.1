# Movie Data Processor

Sistema de procesamiento, validación y análisis estadístico de datos cinematográficos desarrollado en Scala utilizando el paradigma de programación funcional.

---

## Tabla de Contenidos

1. [Descripción General](#descripción-general)
2. [Tecnologías Utilizadas](#tecnologías-utilizadas)
3. [Arquitectura del Sistema](#arquitectura-del-sistema)
4. [Estructura del Proyecto](#estructura-del-proyecto)
5. [Modelo de Datos](#modelo-de-datos)
6. [Componentes del Sistema](#componentes-del-sistema)
7. [Proceso de Lectura de Datos](#proceso-de-lectura-de-datos)
8. [Limpieza y Validación de Datos](#limpieza-y-validación-de-datos)
9. [Parseo de JSON con Circe](#parseo-de-json-con-circe)
10. [Análisis Estadístico](#análisis-estadístico)
11. [Persistencia en Base de Datos](#persistencia-en-base-de-datos)
12. [Configuración y Ejecución](#configuración-y-ejecución)
13. [Ejemplo de Salida](#ejemplo-de-salida)

---

## Descripción General

Este proyecto implementa un pipeline completo de procesamiento de datos cinematográficos que abarca desde la lectura de archivos CSV hasta la generación de reportes estadísticos. El sistema está diseñado siguiendo los principios de programación funcional, garantizando inmutabilidad, composición de funciones y manejo seguro de efectos secundarios.

El flujo de procesamiento incluye las siguientes etapas:

1. Lectura del archivo CSV mediante streaming para optimizar el uso de memoria
2. Parseo de columnas con formato JSON embebido utilizando la librería Circe
3. Validación y limpieza de datos con corrección automática de errores comunes
4. Persistencia de los datos normalizados en una base de datos MySQL relacional
5. Generación de estadísticas descriptivas y análisis de correlaciones

El dataset procesado contiene información detallada de películas incluyendo metadatos básicos, información financiera, calificaciones de usuarios, elenco, equipo técnico, géneros, palabras clave y datos de producción.

---

## Tecnologías Utilizadas

El proyecto hace uso de las siguientes tecnologías y librerías del ecosistema Scala:

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Scala | 3.x | Lenguaje de programación principal con soporte para programación funcional |
| Cats Effect | 3.x | Librería para manejo de efectos secundarios de forma funcional mediante el tipo IO |
| fs2 | 3.x | Librería de streaming funcional para procesamiento de datos en flujo |
| fs2-data-csv | 1.x | Extensión de fs2 para lectura y decodificación de archivos CSV |
| Circe | 0.14.x | Librería funcional para parseo y manipulación de datos JSON |
| Doobie | 1.x | Librería funcional para acceso a bases de datos relacionales mediante JDBC |
| HikariCP | 5.x | Pool de conexiones de alto rendimiento para bases de datos |
| MySQL Connector | 8.x | Driver JDBC para conexión con bases de datos MySQL |
| Typesafe Config | 1.4.x | Librería para gestión de archivos de configuración |

---

## Arquitectura del Sistema

El sistema sigue una arquitectura en capas que separa las responsabilidades de cada componente:

```
+------------------+
|      Main        |  Orquestación del flujo principal
+------------------+
         |
         v
+------------------+
|     Services     |  Lógica de negocio (Estadisticas, Limpieza, JsonParser)
+------------------+
         |
         v
+------------------+
|       DAO        |  Acceso a datos (MovieDAO)
+------------------+
         |
         v
+------------------+
|     Database     |  Configuración de conexión (Database)
+------------------+
         |
         v
+------------------+
|      MySQL       |  Base de datos relacional
+------------------+
```

La capa de servicios contiene toda la lógica de procesamiento incluyendo validación, limpieza, parseo de JSON y cálculos estadísticos. La capa de acceso a datos encapsula las operaciones de inserción en la base de datos. La capa de configuración gestiona los parámetros de conexión.

---

## Estructura del Proyecto

El código fuente está organizado en los siguientes paquetes:

```
src/main/scala/
|
|-- Main.scala
|       Punto de entrada de la aplicación. Orquesta el flujo completo
|       de procesamiento desde la lectura del CSV hasta la generación
|       de estadísticas. Utiliza Cats Effect IO para manejar efectos.
|
|-- config/
|       |-- Database.scala
|               Configuración del pool de conexiones HikariCP.
|               Lee los parámetros de conexión desde application.conf
|               y crea el Transactor de Doobie.
|
|-- dao/
|       |-- MovieDAO.scala
|               Data Access Object que encapsula todas las operaciones
|               de inserción en la base de datos. Utiliza Doobie para
|               ejecutar queries SQL de forma funcional.
|
|-- models/
|       |-- Models.scala
|               Definición de todas las case classes del dominio.
|               Incluye RawMovieRow para datos crudos del CSV y
|               modelos limpios como Movie, Genre, Person, etc.
|
|-- services/
|       |-- Estadisticas.scala
|               Objeto con funciones puras para cálculos estadísticos.
|               Incluye medidas de tendencia central, dispersión,
|               frecuencias y correlaciones.
|       |
|       |-- JsonParser.scala
|               Objeto para parseo de columnas JSON utilizando Circe.
|               Incluye funciones para extraer géneros, países,
|               idiomas, elenco, equipo técnico y colecciones.
|       |
|       |-- Limpieza.scala
|               Objeto para validación y limpieza de datos.
|               Implementa correcciones automáticas para valores
|               nulos, negativos, fuera de rango y formatos inválidos.
|
|-- utils/
        |-- CsvReader.scala
                Utilidad para lectura de archivos CSV mediante fs2.
                Implementa streaming para manejo eficiente de memoria
                y eliminación de duplicados.
```

---

## Modelo de Datos

### Modelo Crudo (RawMovieRow)

La case class RawMovieRow representa una fila del archivo CSV con todos los campos como tipo String antes de ser procesados:

```scala
case class RawMovieRow(
  adult: String,
  belongs_to_collection: String,
  budget: String,
  genres: String,
  homepage: String,
  id: String,
  imdb_id: String,
  original_language: String,
  original_title: String,
  overview: String,
  popularity: String,
  poster_path: String,
  production_companies: String,
  production_countries: String,
  release_date: String,
  revenue: String,
  runtime: String,
  spoken_languages: String,
  status: String,
  tagline: String,
  title: String,
  video: String,
  vote_average: String,
  vote_count: String,
  keywords: String,
  cast: String,
  crew: String,
  ratings: String
)
```

### Modelo Limpio (Movie)

La case class Movie representa una película con los datos ya validados y convertidos a sus tipos correspondientes:

```scala
case class Movie(
  id: Int,
  title: String,
  originalTitle: Option[String],
  originalLanguage: Option[String],
  overview: Option[String],
  releaseDate: Option[String],
  status: Option[String],
  runtime: Int,
  adult: Boolean,
  video: Boolean,
  tagline: Option[String],
  popularity: Double,
  voteAverage: Double,
  voteCount: Int,
  budget: Long,
  revenue: Long,
  homepage: Option[String],
  imdbId: Option[String],
  posterPath: Option[String]
)
```

### Modelos de Catálogos

Los catálogos representan entidades que se relacionan con las películas:

```scala
case class Genre(id: Int, name: String)
case class Keyword(id: Int, name: String)
case class ProductionCompany(id: Int, name: String)
case class ProductionCountry(iso31661: String, name: String)
case class SpokenLanguage(iso6391: String, name: String)
case class Person(id: Int, name: String, gender: Int, profilePath: Option[String])
case class Collection(id: Int, name: String, posterPath: Option[String], backdropPath: Option[String])
```

### Modelos de Relación

Las tablas de relación implementan las asociaciones muchos a muchos entre películas y catálogos:

```scala
case class MovieGenre(movieId: Int, genreId: Int)
case class MovieKeyword(movieId: Int, keywordId: Int)
case class MovieCompany(movieId: Int, companyId: Int)
case class MovieCountry(movieId: Int, countryIso: String)
case class MovieLanguage(movieId: Int, languageIso: String)
case class MovieCast(movieId: Int, personId: Int, characterName: String, castOrder: Int)
case class MovieCrew(movieId: Int, personId: Int, job: String, department: String)
case class MovieCollection(movieId: Int, collectionId: Int)
```

### Esquema de Base de Datos

La base de datos MySQL implementa el siguiente esquema relacional:

| Tabla | Columnas Principales | Clave Primaria | Descripción |
|-------|---------------------|----------------|-------------|
| movies | id, title, budget, revenue, runtime, popularity, vote_average, vote_count | id | Tabla principal de películas |
| genres | id, name | id | Catálogo de géneros cinematográficos |
| keywords | id, name | id | Catálogo de palabras clave |
| production_companies | id, name | id | Catálogo de compañías productoras |
| production_countries | iso_3166_1, name | iso_3166_1 | Catálogo de países de producción |
| spoken_languages | iso_639_1, name | iso_639_1 | Catálogo de idiomas |
| persons | id, name, gender, profile_path | id | Catálogo de personas (actores y crew) |
| collections | id, name, poster_path, backdrop_path | id | Catálogo de sagas y franquicias |
| movie_genres | movie_id, genre_id | (movie_id, genre_id) | Relación películas-géneros |
| movie_keywords | movie_id, keyword_id | (movie_id, keyword_id) | Relación películas-keywords |
| movie_companies | movie_id, company_id | (movie_id, company_id) | Relación películas-productoras |
| movie_countries | movie_id, country_iso | (movie_id, country_iso) | Relación películas-países |
| movie_languages | movie_id, language_iso | (movie_id, language_iso) | Relación películas-idiomas |
| movie_cast | movie_id, person_id, character_name, cast_order | (movie_id, person_id) | Elenco de películas |
| movie_crew | movie_id, person_id, job, department | (movie_id, person_id, job) | Equipo técnico de películas |
| movie_collection | movie_id, collection_id | movie_id | Relación películas-colecciones |

---

## Componentes del Sistema

### Objeto CsvReader

El objeto CsvReader ubicado en el paquete utils es responsable de la lectura del archivo CSV. Utiliza fs2 para implementar streaming y fs2-data-csv para la decodificación automática de las filas.

```scala
object CsvReader:

  given CsvRowDecoder[RawMovieRow, String] = deriveCsvRowDecoder[RawMovieRow]

  private def distinctBy[A, K](f: A => K): Pipe[IO, A, A] = stream =>
    stream.evalMapAccumulate(Set.empty[K]) { (seen, a) =>
      val key = f(a)
      if seen.contains(key) then IO.pure((seen, None))
      else IO.pure((seen + key, Some(a)))
    }.collect { case (_, Some(a)) => a }

  def readCsv(filePath: String): Stream[IO, RawMovieRow] =
    Files[IO]
      .readAll(Path(filePath))
      .through(text.utf8.decode)
      .through(decodeUsingHeaders[RawMovieRow](';'))
      .filter(raw => raw.id.trim.nonEmpty && raw.id.trim.toLowerCase != "null")
      .through(distinctBy(_.id.trim))
```

Características principales:

- Utiliza derivación automática de decodificadores CSV mediante fs2-data-csv
- Implementa un pipe personalizado distinctBy para eliminar duplicados por ID
- Filtra registros con ID vacío o nulo antes del procesamiento
- Procesa el archivo en streaming sin cargar todo en memoria

### Objeto JsonParser

El objeto JsonParser ubicado en el paquete services implementa todas las funciones de parseo para las columnas que contienen datos en formato JSON. Utiliza la librería Circe para el procesamiento.

```scala
object JsonParser:

  private def limpiarJson(jsonStr: String): String =
    if jsonStr == null then return "[]"
    var limpio = jsonStr.trim
      .replace("'", "\"")
      .replace("None", "null")
      .replace("True", "true")
      .replace("False", "false")
    // Cierra corchetes faltantes si es necesario
    limpio

  def parseGenres(jsonStr: String): List[(Int, String)] =
    if jsonStr == null || jsonStr.trim.isEmpty || jsonStr == "[]" then return List.empty
    val limpio = limpiarJson(jsonStr)
    parse(limpio) match
      case Right(json) =>
        json.asArray.toList.flatten.flatMap { obj =>
          for
            id <- obj.hcursor.get[Int]("id").toOption
            name <- obj.hcursor.get[String]("name").toOption
          yield (id, name)
        }
      case Left(_) => List.empty
```

Funciones disponibles:

| Función | Tipo de Retorno | Campos Extraídos |
|---------|-----------------|------------------|
| parseGenres | List[(Int, String)] | id, name |
| parseKeywords | List[(Int, String)] | id, name |
| parseProductionCompanies | List[(Int, String)] | id, name |
| parseCountries | List[(String, String)] | iso_3166_1, name |
| parseLanguages | List[(String, String)] | iso_639_1, name |
| parseCast | List[(Int, String, Option[Int], Option[String], String, Int)] | id, name, gender, profile_path, character, order |
| parseCrew | List[(Int, String, Option[Int], Option[String], String, String)] | id, name, gender, profile_path, job, department |
| parseCollection | Option[(Int, String, Option[String], Option[String])] | id, name, poster_path, backdrop_path |

### Objeto Limpieza

El objeto Limpieza ubicado en el paquete services implementa la validación y corrección automática de datos. Define estructuras para registrar los errores encontrados y las correcciones aplicadas.

```scala
object Limpieza:

  case class ValidationError(field: String, value: String, message: String, correctedValue: String)
  case class ValidationResult(movie: Movie, errors: List[ValidationError])

  def cleanMovie(raw: RawMovieRow): Option[Movie] =
    val result = validateAndCleanMovie(raw)
    result match
      case Some(validationResult) =>
        if validationResult.errors.nonEmpty then
          // Registrar correcciones aplicadas
        Some(validationResult.movie)
      case None =>
        rescueMovie(raw)  // Intento de rescate con valores por defecto
```

Correcciones implementadas:

| Campo | Problema Detectado | Corrección Aplicada |
|-------|-------------------|---------------------|
| id | Valor vacío o inválido | Película descartada |
| id | Valor negativo | Multiplicación por -1 |
| title | Valor vacío o null | Asignación de "Sin Titulo (ID: X)" |
| title | Longitud excesiva | Truncamiento a 500 caracteres |
| budget | Valor negativo | Multiplicación por -1 |
| budget | Valor no numérico | Asignación de 0 |
| revenue | Valor negativo | Multiplicación por -1 |
| revenue | Valor no numérico | Asignación de 0 |
| runtime | Valor negativo | Multiplicación por -1 |
| runtime | Valor no numérico | Asignación de 0 |
| vote_average | Valor mayor a 10 | División sucesiva entre 10 |
| vote_average | Valor negativo | Multiplicación por -1 |
| release_date | Formato inválido | Asignación de "1900-01-01" |
| release_date | Año menor a 1800 | Ajuste a 1800 |
| release_date | Año mayor a 2030 | Ajuste a 2030 |
| adult | Valor no booleano | Asignación de false |
| video | Valor no booleano | Asignación de false |
| Campos opcionales | Valor vacío o null | Asignación de None |
| Campos opcionales | Longitud excesiva | Truncamiento al máximo permitido |

### Objeto Estadisticas

El objeto Estadisticas ubicado en el paquete services contiene funciones puras para realizar cálculos estadísticos sobre los datos numéricos del dataset.

```scala
object Estadisticas:

  def suma(datos: List[Int]): Int = datos.sum
  def sumaLong(datos: List[Long]): Long = datos.sum

  def promedio(datos: List[Int]): Double =
    if (datos.isEmpty) 0.0 else datos.sum.toDouble / datos.length

  def mediana(datos: List[Int]): Double = {
    if (datos.isEmpty) return 0.0
    val ordenados = datos.sorted
    val n = ordenados.length
    if (n % 2 == 0) (ordenados(n / 2 - 1) + ordenados(n / 2)).toDouble / 2.0
    else ordenados(n / 2).toDouble
  }

  def desviacionEstandar(datos: List[Int]): Double = {
    if (datos.isEmpty) return 0.0
    val prom = promedio(datos)
    val varianza = datos.map(x => pow(x - prom, 2)).sum / datos.length
    sqrt(varianza)
  }

  def frecuencias[A](datos: List[A]): Map[A, Int] =
    datos.groupBy(x => x).map((clave, lista) => clave -> lista.length)

  def topN[A](datos: List[A], n: Int): List[(A, Int)] =
    frecuencias(datos).toList.sortBy(-_._2).take(n)

  def correlacion(x: List[Double], y: List[Double]): Double = {
    // Implementación del coeficiente de correlación de Pearson
  }

  def interpretarCorrelacion(r: Double): String = {
    if (r > 0.7) "Correlacion positiva fuerte"
    else if (r > 0.3) "Correlacion positiva moderada"
    else if (r > -0.3) "Correlacion debil o nula"
    else if (r > -0.7) "Correlacion negativa moderada"
    else "Correlacion negativa fuerte"
  }
```

Categorías de funciones:

| Categoría | Funciones | Descripción |
|-----------|-----------|-------------|
| Agregación | suma, sumaLong | Suma total de valores enteros o largos |
| Tendencia Central | promedio, promedioDouble, mediana | Valores representativos del conjunto |
| Dispersión | maximo, minimo, rango, desviacionEstandar | Variabilidad de los datos |
| Frecuencia | frecuencias, topN | Conteo y ranking de valores |
| Correlación | correlacion, interpretarCorrelacion | Relación entre dos variables |

---

## Proceso de Lectura de Datos

El proceso de lectura del archivo CSV se realiza mediante streaming utilizando la librería fs2. Este enfoque permite procesar archivos de gran tamaño sin cargar todo el contenido en memoria.

El flujo de lectura sigue los siguientes pasos:

1. Apertura del archivo mediante Files[IO].readAll que retorna un Stream de bytes
2. Decodificación del contenido a texto UTF-8 mediante el pipe text.utf8.decode
3. Parseo de las líneas CSV utilizando decodeUsingHeaders con punto y coma como delimitador
4. Filtrado de registros con ID inválido (vacío o null)
5. Eliminación de duplicados mediante el pipe personalizado distinctBy

El decodificador de filas CSV se genera automáticamente mediante la función deriveCsvRowDecoder que utiliza las macros de Scala 3 para crear la instancia de CsvRowDecoder basándose en los nombres de los campos de la case class RawMovieRow.

---

## Limpieza y Validación de Datos

El proceso de limpieza implementa un sistema de validación con corrección automática. Cada campo del registro crudo pasa por una función de corrección específica que detecta problemas y aplica soluciones predefinidas.

El flujo de validación es el siguiente:

1. Se intenta validar y limpiar la película mediante validateAndCleanMovie
2. Si la validación es exitosa, se registran las correcciones aplicadas
3. Si la validación falla, se intenta rescatar la película con rescueMovie
4. Si el rescate falla, la película se descarta definitivamente

Las funciones de corrección siguen un patrón común:

```scala
private def correctNumericLong(value: String, field: String, errors: ListBuffer[ValidationError]): Long =
  val trimmed = value.trim
  if trimmed.isEmpty || trimmed.toLowerCase == "null" then return 0L
  try
    val num = trimmed.toDouble.toLong
    if num < 0 then
      val corrected = num * -1
      errors += ValidationError(field, trimmed, "Negativo, multiplicado por -1", corrected.toString)
      corrected
    else num
  catch
    case _: Exception =>
      errors += ValidationError(field, trimmed, "No es numero, usando 0", "0")
      0L
```

Este patrón garantiza que siempre se retorne un valor válido y que todas las correcciones queden registradas para su posterior revisión.

---

## Parseo de JSON con Circe

La librería Circe es una herramienta funcional para el procesamiento de JSON en Scala. El proyecto la utiliza para extraer datos de las columnas que contienen estructuras JSON embebidas.

### Conceptos Fundamentales de Circe

La función parse convierte una cadena de texto en un objeto Json:

```scala
parse(jsonString) match
  case Right(json) => // JSON válido
  case Left(error) => // Error de parseo
```

El cursor (HCursor) permite navegar por la estructura JSON de forma segura:

```scala
val cursor = json.hcursor
val id = cursor.get[Int]("id")        // Either[DecodingFailure, Int]
val name = cursor.get[String]("name") // Either[DecodingFailure, String]
```

El método asArray convierte un Json a una lista opcional de elementos:

```scala
json.asArray.toList.flatten  // List[Json]
```

### Limpieza de JSON Mal Formado

El dataset original contiene JSON con formato inconsistente proveniente de Python. La función limpiarJson corrige estos problemas:

```scala
private def limpiarJson(jsonStr: String): String =
  if jsonStr == null then return "[]"
  var limpio = jsonStr.trim
    .replace("'", "\"")      // Comillas simples a dobles
    .replace("None", "null") // None de Python a null de JSON
    .replace("True", "true") // Booleanos en minúsculas
    .replace("False", "false")
  // Verificar y cerrar corchetes faltantes
  limpio
```

### Extracción de Datos

Cada función de parseo sigue un patrón similar:

1. Verificar si el JSON está vacío o es nulo
2. Limpiar el JSON para corregir problemas de formato
3. Parsear el JSON con Circe
4. Navegar la estructura con cursores
5. Extraer los campos requeridos
6. Retornar una lista de tuplas o Option según corresponda

---

## Análisis Estadístico

El sistema genera un reporte estadístico completo que incluye las siguientes secciones:

### Estadísticas de Presupuesto (Budget)

- Total acumulado de todos los presupuestos
- Promedio de presupuesto por película
- Presupuesto máximo registrado
- Presupuesto mínimo (excluyendo ceros)
- Cantidad de películas con presupuesto informado

### Estadísticas de Ingresos (Revenue)

- Total acumulado de todos los ingresos
- Promedio de ingresos por película
- Ingreso máximo registrado
- Cantidad de películas con ingresos informados

### Estadísticas de Duración (Runtime)

- Promedio de duración en minutos
- Mediana de duración
- Duración máxima y mínima
- Rango de duración
- Desviación estándar

### Estadísticas de Popularidad

- Promedio del índice de popularidad
- Valores máximo y mínimo
- Desviación estándar

### Estadísticas de Calificación (Vote Average)

- Promedio de calificaciones
- Mediana de calificaciones
- Calificación máxima
- Desviación estándar

### Estadísticas de Votos (Vote Count)

- Total de votos acumulados
- Promedio de votos por película
- Máximo de votos en una película
- Mediana de votos

### Análisis de Frecuencia

- Top 10 años con mayor cantidad de películas

### Análisis de Correlaciones

- Correlación entre presupuesto e ingresos
- Correlación entre popularidad y cantidad de votos
- Correlación entre duración y calificación

### Rankings

- Top 5 películas por ingresos
- Top 5 películas mejor calificadas (mínimo 1000 votos)
- Top 5 películas más populares

---

## Persistencia en Base de Datos

La clase MovieDAO encapsula todas las operaciones de inserción utilizando Doobie. Cada método ejecuta una query SQL y maneja los errores de forma funcional.

```scala
class MovieDAO(xa: Transactor[IO]):

  def insertMovie(movie: Movie): IO[Int] =
    sql"""
      INSERT IGNORE INTO movies (id, title, original_title, ...)
      VALUES (${movie.id}, ${movie.title}, ${movie.originalTitle}, ...)
    """.update.run.transact(xa)
      .handleErrorWith(e => IO.println(s"Error: ${e.getMessage}") >> IO.pure(0))

  def insertGenre(genre: Genre): IO[Int] =
    sql"INSERT IGNORE INTO genres (id, name) VALUES (${genre.id}, ${genre.name})"
      .update.run.transact(xa)
      .handleErrorWith(_ => IO.pure(0))
```

Características de la implementación:

- Utiliza INSERT IGNORE para evitar errores por duplicados
- Maneja errores con handleErrorWith retornando 0 en caso de fallo
- Ejecuta las queries dentro de transacciones mediante transact(xa)
- Utiliza interpolación SQL de Doobie para prevenir inyección SQL

---

## Configuración y Ejecución

### Requisitos Previos

- JDK 11 o superior instalado
- SBT (Scala Build Tool) instalado
- MySQL 8.x instalado y en ejecución
- Base de datos creada con el esquema requerido

### Archivo de Configuración

Crear el archivo src/main/resources/application.conf con los parámetros de conexión:

```hocon
database {
  driver = "com.mysql.cj.jdbc.Driver"
  url = "jdbc:mysql://localhost:3306/movies_db?useSSL=false&serverTimezone=UTC"
  user = "root"
  password = "password"
}
```

### Dependencias (build.sbt)

```scala
libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % "3.5.0",
  "co.fs2" %% "fs2-core" % "3.7.0",
  "co.fs2" %% "fs2-io" % "3.7.0",
  "org.gnieh" %% "fs2-data-csv" % "1.7.1",
  "org.gnieh" %% "fs2-data-csv-generic" % "1.7.1",
  "io.circe" %% "circe-core" % "0.14.5",
  "io.circe" %% "circe-parser" % "0.14.5",
  "org.tpolecat" %% "doobie-core" % "1.0.0-RC2",
  "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC2",
  "mysql" % "mysql-connector-java" % "8.0.33",
  "com.typesafe" % "config" % "1.4.2"
)
```

### Ejecución

Para compilar y ejecutar el proyecto:

```bash
sbt compile
sbt run
```

Para generar un JAR ejecutable:

```bash
sbt assembly
java -jar target/scala-3.x/movie-processor.jar
```

---

## Ejemplo de Salida

```
============================================================
MOVIE DATA PROCESSOR - INICIANDO
============================================================
Primera pelicula: Lock, Stock and Two Smoking Barrels
Procesadas 100 peliculas...
Procesadas 200 peliculas...
Procesadas 300 peliculas...

============================================================
PROCESO COMPLETADO
============================================================
Total CSV leidas:        350
Peliculas insertadas:    342
Peliculas descartadas:   5
Errores de BD:           3
============================================================

============================================================
ESTADISTICAS DE PELICULAS
============================================================

--- PRESUPUESTO (Budget) ---
  Total:              $2,847,500,000
  Promedio:           $28,475,000.00
  Maximo:             $380,000,000
  Minimo (>0):        $1,200
  Peliculas con budget: 245

--- INGRESOS (Revenue) ---
  Total:              $15,234,000,000
  Promedio:           $152,340,000.00
  Maximo:             $2,797,800,564
  Peliculas con revenue: 198

--- DURACION (Runtime) ---
  Promedio:           107.50 minutos
  Mediana:            103.00 minutos
  Maximo:             238 minutos
  Minimo (>0):        45 minutos
  Rango:              193 minutos
  Desv. Estandar:     22.35

--- POPULARIDAD ---
  Promedio:           12.4523
  Maximo:             547.4882
  Minimo:             0.6012
  Desv. Estandar:     45.2341

--- CALIFICACION PROMEDIO (Vote Average) ---
  Promedio:           6.42 / 10
  Mediana:            6.50 / 10
  Maximo:             8.70 / 10
  Desv. Estandar:     1.12

--- NUMERO DE VOTOS (Vote Count) ---
  Total de votos:     4,523,456
  Promedio:           13,245.67 votos/pelicula
  Maximo:             22,186
  Mediana:            1,234

--- PELICULAS POR AÑO (Top 10) ---
  2016: 45 peliculas
  2015: 42 peliculas
  2017: 38 peliculas
  2014: 35 peliculas
  2018: 32 peliculas
  2013: 28 peliculas
  2012: 25 peliculas
  2011: 22 peliculas
  2019: 20 peliculas
  2010: 18 peliculas

--- CORRELACIONES ---
  Budget vs Revenue:      0.7234 (Correlacion positiva fuerte)
  Popularidad vs Votos:   0.5621 (Correlacion positiva moderada)
  Duracion vs Rating:     0.1245 (Correlacion debil o nula)

--- TOP 5 PELICULAS POR REVENUE ---
  1. Avatar                                   $2,797,800,564
  2. Avengers: Endgame                        $2,743,900,000
  3. Titanic                                  $2,187,463,944
  4. Star Wars: The Force Awakens             $2,068,223,624
  5. Avengers: Infinity War                   $2,048,359,754

--- TOP 5 PELICULAS POR RATING (min 1000 votos) ---
  1. The Shawshank Redemption                 8.70/10 (18,234 votos)
  2. The Godfather                            8.65/10 (15,456 votos)
  3. The Dark Knight                          8.52/10 (22,186 votos)
  4. Schindler's List                         8.50/10 (12,345 votos)
  5. Pulp Fiction                             8.48/10 (19,876 votos)

--- TOP 5 PELICULAS MAS POPULARES ---
  1. Spider-Man: No Way Home                  547.4882
  2. The Batman                               423.5521
  3. Avengers: Endgame                        398.2341
  4. Top Gun: Maverick                        356.7823
  5. Avatar: The Way of Water                 312.4521

============================================================
FIN DEL REPORTE
============================================================
```

---

## Consideraciones Finales

Este proyecto demuestra la aplicación práctica de los conceptos de programación funcional en Scala para el procesamiento de datos. Los principios aplicados incluyen:

- Inmutabilidad mediante el uso de case classes y colecciones inmutables
- Funciones puras sin efectos secundarios en el módulo de estadísticas
- Manejo seguro de efectos mediante el tipo IO de Cats Effect
- Composición de funciones mediante pipes y operadores de streaming
- Manejo explícito de errores mediante Option y Either
- Separación de responsabilidades en capas bien definidas

El código está diseñado para ser mantenible, extensible y testeable, siguiendo las mejores prácticas del desarrollo funcional en Scala.
