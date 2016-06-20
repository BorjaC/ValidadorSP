package validar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import utiles.Arbol;
import utiles.Nodo;
import utiles.Utiles;

/**
 * Clase abstracta de la que extender y crear validadores
 * 
 * @author Borja Colmenarejo Garcia
 */
abstract public class Validador extends Utiles {

	/* Arboles en memoria e indices de los mismos */
	protected List<Arbol> trees;
	protected List<Integer> indices;
	protected int numSentences;

	/**
	 * Funcion abstracta que implementa la creacion de ficheros necesaria para la validacion 
	 * 
	 * @param setLenght tamaño del conjunto que contendra cada fichero, -1 asigna el tamaño por
	 * defecto del conjunto
	 */
	abstract protected void createFiles(int setLenght);
	
	/**
	 * Valida
	 * 
	 * @param setLenght tamaño del conjunto a validar
	 * @return devuelve la clase que almacena los resultados obtenidos.
	 */
	abstract public RendimientoModelo validate(int setLenght);

	/**
	 * Contruuctor de la clase
	 * 
	 * @param treebank treebank del cual crear las frases en memoria para hacer uso de ellas
	 * en la validacion.
	 */
	public Validador(String treebank) {
		trees = getTreesForFile(treebank);
		indices = calculateIndex(trees.size(), false);
		numSentences = trees.size();
	}

	/**
	 * Contructor vacio
	 * 
	 */
	public Validador() {}

	/**
	 * Devuelve el numero de arboles del treebank
	 * 
	 * @return numero de arboles del treebank
	 */
	public int getNumSentences() {
		return numSentences;
	}

	/**
	 * Metodo para entrenar el Stanford Parser
	 * 
	 * @param train
	 *            fichero de entrenamiento (.lisp)
	 * @param model
	 *            modelo que creara el entrenamiento (.gz o .ser.gz)
	 * @param modelTxt
	 *            modelo en fichero (.txt)
	 */
	protected void train(String train, String model, String modelTxt) {

		String[] options = { "-PCFG", "-vMarkov", "5", "-uwm", "0", "-headFinder",
				"edu.stanford.nlp.trees.LeftHeadFinder", "-train", train, "-saveToSerializedFile", model,
				"-saveToTextFile", modelTxt };

		LexicalizedParser.main(options);

	}

	/**
	 * Realizar el test de las frases
	 * 
	 * @param rows
	 *            fichero que contiene las frases para test (.txt)
	 * @param output
	 *            fichero de salida (.txt)
	 * @param model
	 *            modelo usado para el test (.gz o .ser.gz)
	 */
	protected void test(String sentences, String output, String model) {

		try {
			PrintStream standard = System.out;

			PrintStream out = new PrintStream(new File(output));

			/* Cambiar la salida para guardarla en fichero */
			System.setOut(out);

			String[] options = { "-sentences", "newline", "-tagSeparator", "/", "-tokenizerFactory",
					"edu.stanford.nlp.process.WhitespaceTokenizer", "-tokenizerMethod", "newCoreLabelTokenizerFactory",
					model, sentences };

			LexicalizedParser.main(options);

			out.close();
			/* Cambiar la salida a consola otra vez */
			System.setOut(standard);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * calcula los indices de manera aleatoria para la creacion de los ficheros
	 * 
	 * @param nTree numero de arboles en el treebank sobre el calcular los indices
	 * @param random si hacerlos aleatorios o no
	 * @return lista de los indices
	 */
	private List<Integer> calculateIndex(int nTree, boolean random) {
		List<Integer> indices = new ArrayList<Integer>();

		/* Obtener lista de indices */
		for (int i = 0; i < nTree; i++) {
			indices.add(i);
		}

		/* Hacerlo aleatorio */
		if (random) {
			Collections.shuffle(indices);
		}

		return indices;
	}

	/**
	 * Crea un arbol a partir de una cadena
	 * 
	 * @param arbol
	 *            cadena que contiene el arbol
	 * @return Arbol creado a partir de la cadena
	 */
	public Arbol createTree(String arbol) {
		Arbol tree = new Arbol();
		ArrayList<Nodo> nodos = new ArrayList<Nodo>();
		Stack<Nodo> stack = new Stack<Nodo>();
		String valor = new String();
		Boolean esValor = false, meterValor = false;

		String[] s = arbol.split("\\s");
		for (int i = 0; i < s.length; i++) {
			/* Para evitar iteraciones vacias */
			if (s[i].equals("")) {
				continue;
			}

			/* Comienzo de nodo */
			if (s[i].startsWith("(")) {
				/* Crear nodo y guardar clave */
				Nodo nodo = new Nodo();
				String clave = s[i].replace("(", "");
				clave = clave.replace(")", "");
				nodo.setClave(clave);

				/* Incluir a la lista de nodos */
				nodos.add(nodo);

				/* Guardar los hijos */
				if (!stack.isEmpty()) {
					/* Nodos hijos del arbol */
					Nodo nodoPadre = stack.pop();
					nodoPadre.addHijo(nodo);
					nodo.setPadre(nodoPadre);
					stack.push(nodoPadre);
				}

				/* Meter a la pila */
				stack.push(nodo);
			}

			if (s[i].startsWith("\"")) {
				/* Valor para asignar */
				valor = s[i].replaceFirst("\"", "");
				esValor = false;
			}

			/* Si el valor no ha terminado de leerse */
			if (esValor) {
				valor += " " + s[i];
			}

			/* Si termina el valor */
			if (valor.contains("\"")) {
				valor = valor.replace(")", "");
				valor = valor.replace("\"", "");

				/* Si el valor es un ')' */
				if (valor.contentEquals("/PUNCT"))
					valor = ")" + valor;

				meterValor = true;
				esValor = false;
			} else {
				esValor = true;
			}

			/* Meter el valor calculado anteriormente */
			if (meterValor) {
				Nodo nodoActual = stack.pop();
				nodoActual.setValor(valor);
				stack.push(nodoActual);
				meterValor = false;
				valor = new String();
			}

			/* Terminar nodo (pueden ser varios a la vez) sacar de la pila */
			if (s[i].endsWith(")")) {
				int idx = s[i].lastIndexOf(")");
				int j = 0;
				while (s[i].charAt(idx - j) == ')') {
					j++;
					stack.pop();
				}
			}
		}

		/*
		 * Guardar el nodo raiz ROOT para comparaciones con la salida (pone ROOT
		 * por defecto)
		 */
		Nodo raiz = new Nodo();
		raiz.setClave("ROOT");
		raiz.addHijo(nodos.get(0));
		nodos.add(0, raiz);
		tree.setRaiz(raiz);

		/* Guardar los nodos del arbol, y contruye el arbol */
		tree.setNodos(nodos);

		/* Comprobar si esta bien balanceado (Si no error) */
		if (!stack.isEmpty()) {
			System.out.println("Arbol " + tree.getId() + ": no esta bien balanceado");
			System.out.println(tree.getFrase());
			System.out.println(stack);
		}

		return tree;
	}
	
}
