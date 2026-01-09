# Análisis y Limpieza de Datos de Películas en Scala

**Proyecto académico de procesamiento de datos con Scala funcional**

---

## Descripción del Proyecto

Este proyecto implementa un pipeline integral de procesamiento y análisis de datos cinematográficos utilizando **Scala 3**. El sistema está diseñado para trabajar con datasets reales que contienen información numérica, categórica y estructuras complejas en formato JSON.

El enfoque adoptado prioriza la **programación funcional**, el uso de estructuras inmutables y el manejo explícito de errores, permitiendo obtener un sistema robusto, escalable y fácil de mantener.

---

## Contexto y Justificación

Los datasets cinematográficos suelen presentar múltiples inconvenientes como:

- Datos faltantes
- Valores extremos
- Formatos inconsistentes
- Columnas con información anidada

Estos problemas dificultan el análisis directo y pueden generar resultados estadísticos poco confiables.

Por esta razón, el proyecto se centra en implementar un **proceso sistemático de limpieza y validación**, previo a cualquier análisis, garantizando la calidad de los datos utilizados.

---

## Objetivos del Proyecto

### Objetivo General

Desarrollar un sistema funcional en Scala capaz de procesar, limpiar y analizar un dataset de películas de forma automatizada.

### Objetivos Específicos

- Implementar lectura eficiente de archivos CSV mediante streaming
- Aplicar técnicas de limpieza y validación de datos
- Analizar columnas numéricas y categóricas
- Procesar columnas JSON embebidas usando Circe
- Generar reportes estadísticos detallados y legibles

---

## Arquitectura del Sistema

El sistema sigue una arquitectura modular basada en etapas claramente definidas:

```
Carga de Datos
   ↓
Transformación Inicial
   ↓
Limpieza y Validación
   ↓
Análisis Estadístico
   ↓
Generación de Reportes
```

Cada módulo opera de forma independiente, facilitando pruebas, mantenimiento y futuras extensiones.

---

## Estructura del Proyecto

```
src/main/scala/
├── AnalisisNumerico.scala
├── LimpiezaDatos.scala
├── AnalisisCategorico.scala
├── ProcesamientoCrew.scala
├── AnalisisColumnasJSON.scala
└── utils/
    ├── CsvReader.scala
    └── EstadisticasUtils.scala
```

---

## Módulos del Sistema

### Análisis Numérico de Columnas

#### `AnalisisNumerico.scala`

Este módulo realiza un análisis estadístico descriptivo de las columnas numéricas del dataset, proporcionando una visión general del comportamiento de los datos.

**Variables Analizadas:**

- `budget`
- `revenue`
- `popularity`
- `runtime`
- `vote_average`
- `vote_count`

**Estadísticas Calculadas:**

- Valor mínimo y máximo
- Promedio y mediana
- Desviación estándar
- Cuartiles (Q1, Q3)
- Suma total

Estas métricas permiten identificar dispersión, tendencias y posibles valores atípicos.

---

### Limpieza y Transformación de Datos

#### `LimpiezaDatos.scala`

Este módulo implementa un proceso de limpieza de datos dividido en múltiples etapas, asegurando coherencia y calidad.

**Transformaciones Aplicadas:**

- Extracción de componentes de la fecha de estreno
- Cálculo del retorno de inversión (ROI)
- Enriquecimiento del modelo de datos original

**Evaluación de Calidad de Datos:**

Durante esta etapa se identifican:

- Valores nulos o faltantes
- Valores cero o negativos en campos críticos
- Campos de texto vacíos
- Porcentaje de registros válidos

#### Proceso de Limpieza por Etapas

##### **Etapa 1: Eliminación de Registros Inválidos**

- Eliminación de registros con valores nulos
- Validación de campos obligatorios

##### **Etapa 2: Validación de Rangos Lógicos**

- Años de estreno dentro de límites históricos
- Duración máxima razonable
- Rangos válidos para votos y popularidad
- ROI mayor o igual a -1

##### **Etapa 3: Detección de Valores Atípicos**

- Método IQR
- Método Z-Score
- Modo estricto y modo flexible

---

### Análisis de Datos Categóricos

#### `AnalisisCategorico.scala`

Este módulo analiza la distribución de columnas categóricas y de texto para identificar patrones y tendencias.

**Columnas Analizadas:**

- `original_language`
- `status`
- `belongs_to_collection`

**Resultados Generados:**

- Top valores más frecuentes
- Distribución porcentual
- Conteo total por categoría

---

###  Procesamiento de Columnas JSON

#### `ProcesamientoCrew.scala`

Este módulo procesa columnas con información en formato JSON, enfocándose especialmente en la columna `crew`.

**Uso de Circe:**

- Parseo de JSON complejo
- Decodificación automática de estructuras
- Manejo funcional de errores con `Either`
- Uso de `Option` para campos opcionales

**Análisis del Crew:**

- Distribución por género
- Departamentos más frecuentes
- Roles más comunes
- Tamaño promedio del crew por película
- Rangos mínimo y máximo de integrantes

---

## Tecnologías Utilizadas

- **Scala 3**
- **Cats Effect**
- **FS2**
- **fs2-data-csv**
- **Circe** (core, generic, parser)

---

## Requisitos del Sistema

- JDK 17 o superior
- sbt 1.9 o superior

---

## Ejecución del Proyecto

```bash
sbt compile
sbt "runMain AnalisisNumerico"
sbt "runMain LimpiezaDatos"
sbt "runMain AnalisisCategorico"
sbt "runMain ProcesamientoCrew"
```

---

## Resultados Obtenidos

| Métrica                               | Valor  |
|---------------------------------------|--------|
| Registros iniciales                   | ~45,000|
| Registros válidos finales             | ~78%   |
| Registros descartados durante limpieza| ~22%   |

Estos resultados demuestran la necesidad de aplicar procesos de limpieza antes del análisis.

---

## Discusión de Resultados

La limpieza de datos permitió reducir ruido estadístico y mejorar la confiabilidad de las métricas obtenidas. El análisis del crew evidenció patrones interesantes en la distribución de roles y departamentos dentro de la industria cinematográfica.

---

## Limitaciones

- Dependencia de la calidad del dataset original
- JSON malformado en algunos registros
- Análisis limitado a estadística descriptiva

---

## Trabajo Futuro

- Integración de visualizaciones
- Análisis predictivo
- Optimización del procesamiento para datasets más grandes
- Extensión del análisis a otras columnas JSON

---

## Conclusiones

El proyecto confirma que Scala funcional es una herramienta eficaz para el procesamiento de datos complejos. La combinación de streaming, tipado fuerte y manejo funcional de errores permite construir pipelines confiables y mantenibles.

---

## Bibliografía

- Odersky, M. *Programming in Scala*. Artima Press
- Typelevel. *Cats Effect Documentation*
- Gnieh, G. *FS2 Streaming Guide*
- Circe Contributors. *Circe JSON Library*
- Tukey, J. *Exploratory Data Analysis*

---


