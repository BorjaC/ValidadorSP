# ValidadorSP

Para la ejecución es necesario:

•	Treebank correctamente anotado.

Además para la validación de datos externos es necesario:

•	Fichero input con la salidad de las oraciones que se quieren validar correctamente etiquetada.

•	Fichero sentences con las frases que se quieren validar.

Los parámetros para la ejecución y sus acciones son los siguientes:

•	-tb “fichero treebank”: marca que lo siguiente leído será el fichero treebank utilizado en la validación. El “fichero treebank” hace referencia a la dirección completa de la ubicación y nombre del fichero.
•	-i: indica que se va a realizar una validación interna de datos.
•	-e: indica que se va a realizar una validación externa de datos.
•	-s: indica que se va a realizar una validación simple de datos.
•	-c: indica que se va a realizar una validación cruzada de datos, por defecto con 10 particiones.
•	-n “número entero”: indica el número de particiones usadas para la validación cruzada, “numero entero” será el número de particiones.
•	-size “número entero”: tamaño del conjunto para entrenar el Stanford Parser. Por defecto se usa el tamaño máximo del conjunto.
•	-input “fichero input”: marca que lo siguiente leído será el fichero input que contiene la salida de las frases que se van a validar correctamente etiquetadas.
•	-sentences “fichero sentences”: marca que lo siguiente leído será el fichero sentences que contiene las frases que se quieren validar.

Los parámetros por defecto son una validación interna simple con el tamaño del conjunto máximo. El orden de los parámetros se puede realizar de cualquier forma. A tener en cuenta:

•	Los parámetros “-i” y “-e” son incompatibles.
•	El tamaño del conjunto pasado no debe superar al número de elementos contenidos en el fichero. Si se quiere usar el máximo de elementos no poner tamaño.
•	Validación interna simple se refiere a entrenar con todos los datos y validar todos.
•	Validación interna cruzada se refiere a entrenar con n-1 conjuntos y validar con el conjunto restante.
•	Validación externa se refiere a entrenar con el conjunto de datos del treebank y validar las frases del fichero sentences.
•	Los parámetros -s -c son compatibles, los resultados aparecerán siempre en el orden de la validación interna simple primero y la validación interna cruzada después.

 Ejemplos de ejecución:
	
  java -jar validador.jar -size 400 tb “fichero treebank” 

 La salida obtenida será una validación interna con el tamaño de conjunto de 400 elementos.

  java -jar validador.jar -tb “fichero treebank” -c

 La salida obtenida será una validación interna cruzada usando todos los elementos del conjunto.

	java -jar validador.jar -tb “fichero treebank” -input “fichero input” -sentences “fichero sentences” -e

 La salida obtenida será una validación externa usando todos los elementos para entrenar Stanford Parser.
