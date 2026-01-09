Sistema Funcional de Analisis y Limpieza de Datos Cinematograficos en Scala
Introduccion

En la actualidad, el analisis de datos se ha convertido en una herramienta fundamental para la toma de decisiones y la generacion de conocimiento. En el contexto de la industria cinematografica, los datasets suelen contener grandes volumenes de informacion heterogenea, combinando datos numericos, categoricos y estructuras complejas como JSON.

Este proyecto presenta el desarrollo de un sistema completo de analisis de datos de peliculas utilizando Scala 3 y un enfoque de programacion funcional. El sistema permite leer, limpiar, transformar y analizar un dataset cinematografico, garantizando calidad de datos y resultados estadisticos confiables.

Objetivos del Proyecto
Objetivo General

Desarrollar un pipeline funcional en Scala para el procesamiento, limpieza y analisis estadistico de un dataset de peliculas.

Objetivos Especificos

Implementar lectura eficiente de archivos CSV mediante procesamiento streaming

Aplicar tecnicas de limpieza y validacion de datos

Analizar variables numericas y categoricas

Procesar columnas con informacion en formato JSON usando Circe

Generar reportes estadisticos claros y estructurados

Diseño General del Sistema

El sistema sigue una arquitectura modular, donde cada componente cumple una funcion especifica dentro del flujo de procesamiento. Esta separacion facilita la mantenibilidad, el entendimiento del codigo y futuras extensiones del proyecto.

El flujo general del sistema es el siguiente:

Lectura del archivo CSV

Transformacion inicial de datos

Limpieza y validacion

Analisis estadistico

Generacion de reportes

Modulo 1: AnalisisNumerico.scala

Este modulo se enfoca en el estudio cuantitativo de las variables numericas presentes en el dataset.

Caracteristicas Principales

Lectura del archivo CSV utilizando FS2

Procesamiento de datos en streaming

Uso de estructuras inmutables

Calculo de estadisticas descriptivas

Variables Evaluadas

budget

revenue

popularity

runtime

vote_average

vote_count

Indicadores Calculados

valor minimo y maximo

promedio

mediana

desviacion estandar

cuartiles

suma total

Este analisis permite identificar tendencias generales, dispersion de datos y posibles valores atipicos desde una perspectiva exploratoria.

Modulo 2: LimpiezaDatos.scala

La limpieza de datos es una de las etapas mas criticas del proyecto. Este modulo implementa un proceso riguroso de validacion y depuracion.

Transformaciones Aplicadas

Separacion de la fecha de estreno en año, mes y dia

Calculo del retorno de inversion (ROI)

Enriquecimiento del modelo de datos con nuevos atributos derivados

Analisis de Calidad de Datos

Se evalua la calidad de los registros mediante:

Deteccion de valores nulos

Identificacion de ceros y valores negativos

Verificacion de textos vacios

Calculo del porcentaje de datos validos

Etapas del Proceso de Limpieza
Etapa 1: Filtrado Inicial

Eliminacion de registros con valores criticos invalidos

Validacion basica de campos obligatorios

Etapa 2: Reglas Logicas

Años de estreno dentro de un rango historico valido

Duracion maxima razonable de una pelicula

Rangos validos para votos y popularidad

ROI dentro de limites aceptables

Etapa 3: Deteccion de Valores Atipicos

Metodo IQR para detectar dispersion extrema

Metodo Z-Score para identificar desviaciones anormales

Modo flexible para preservar informacion relevante

Modulo 3: AnalisisCategorico.scala

Este componente analiza columnas de tipo texto y categorico para comprender la distribucion de valores no numericos.

Columnas Analizadas

original_language

status

belongs_to_collection

Resultados Generados

Top de valores mas frecuentes

Conteo total por categoria

Distribuciones relativas

Este analisis permite identificar patrones culturales, estados de produccion y tendencias en colecciones cinematograficas.

Modulo 4: ProcesamientoCrew.scala

Uno de los aspectos mas avanzados del proyecto es el procesamiento de columnas que contienen JSON embebido.

Uso de Circe

Se utiliza la libreria Circe para:

Parsear estructuras JSON complejas

Decodificar listas de objetos

Manejar errores mediante Either

Trabajar con campos opcionales usando Option

Analisis del Crew

Distribucion por genero

Departamentos mas frecuentes

Roles mas comunes

Tamaño promedio del equipo por pelicula

Rango minimo y maximo de integrantes

Este analisis aporta informacion valiosa sobre la estructura interna de las producciones cinematograficas.

Resultados Globales

Registros iniciales: aproximadamente 45,000

Registros validos finales: alrededor del 78%

Registros descartados: cerca del 22%

Estos resultados evidencian la importancia de un proceso de limpieza adecuado antes de realizar cualquier analisis avanzado.

Conclusiones y Discusion

El proyecto confirma que Scala es una herramienta poderosa para el analisis de datos cuando se combina con un enfoque funcional. El uso de FS2 permite manejar grandes volumenes de informacion de forma eficiente, mientras que Cats Effect garantiza control sobre los efectos y la ejecucion del programa.

La integracion de Circe facilita el tratamiento de datos semi-estructurados, resolviendo uno de los principales desafios del dataset. La correcta limpieza y validacion de los datos incrementa significativamente la confiabilidad de los resultados estadisticos obtenidos.

Finalmente, el sistema desarrollado es escalable, mantenible y adaptable a otros datasets similares, lo que lo convierte en una base solida para proyectos de analisis de datos y ciencia de datos.

Bibliografia (Referencial)

Odersky, M. (2022). Functional Programming in Scala. Artima Press.

Typelevel. (2024). Cats Effect User Guide.

Gnieh, G. (2023). Streaming Data Processing with FS2.

Circe Contributors. (2024). JSON Handling in Scala.

Tukey, J. (1977). Exploratory Data Analysis.

Han, J., & Kamber, M. (2021). Principles of Data Mining.
