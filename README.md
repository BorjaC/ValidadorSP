# ValidadorSP

Para la ejecución es necesario:

•	Treebank correctamente anotado. Se puede obtener el treebank empleado para el desarrollo de la herramienta a traves de  la siguiente web http://www.lllf.uam.es/ESP/Treebank.html

Además para la validación de datos externos es necesario:

•	Fichero gold con la salida de las oraciones que se quieren validar correctamente etiquetada.

•	Fichero sentences con las frases que se quieren validar.

•	Opcionalmente un fichero del modelo comprimido, con el modelo que se utilizara para efectuar la validacion externa

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

•	-model “fichero modelo”: marca que lo siguiente leído será el fichero que contendra el modelo, que contiene el modelo para validar las frases (senteces)

•	-save: guarda el mejor modelo de la ejecución, además, el fichero a partir del cual se ha creado el modelo, y la salida obtenida en la mejor ejecución.

Los parámetros por defecto son una validación interna simple con el tamaño del conjunto máximo. El orden de los parámetros se pueden ordenar de cualquier forma, es recomendable poner el parametro -tb en primer lugar, si se emplea. A tener en cuenta:

•	Los parámetros “-i” y “-e” son incompatibles.

•	El tamaño del conjunto pasado no debe superar al número de elementos contenidos en el fichero. Si se quiere usar el máximo de elementos no poner tamaño.

•	Validación interna simple se refiere a entrenar con todos los datos y validar todos.

•	Validación interna cruzada se refiere a entrenar con n-1 conjuntos y validar con el conjunto restante.

•	Validación externa se refiere a entrenar con el conjunto de datos del treebank y validar las frases del fichero sentences.

•	Los parámetros -s -c son compatibles, los resultados aparecerán siempre en el orden de la validación interna simple primero y la validación interna cruzada después.

•	En caso de introducir parámetros -tb -model se ejecutara el modelo leido, es decir, el parametro -tb sera ignorado.

 Ejemplos de ejecución:
	
  	java -jar validador.jar -size 400 tb “fichero treebank” 

 La salida obtenida será una validación interna con el tamaño de conjunto de 400 elementos.

  	java -jar validador.jar -tb “fichero treebank” -c

 La salida obtenida será una validación interna cruzada usando todos los elementos del conjunto.

	java -jar validador.jar -tb “fichero treebank” -input “fichero input” -sentences “fichero sentences” -e

 La salida obtenida será una validación externa usando todos los elementos para entrenar Stanford Parser.
