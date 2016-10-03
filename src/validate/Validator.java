package validate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import utils.Node;
import utils.Tree;
import utils.Utils;

/**
 * Clase abstracta de la que extender y crear validadores
 * 
 * @author Borja Colmenarejo Garcia
 */
abstract public class Validator extends Utils {

	/* Arboles en memoria e indices de los mismos */
	protected List<Tree> trees;
	protected List<Integer> indices;
	protected int numSentences;
	protected boolean save = false;
	
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
	abstract public ResultsModel validate(int setLenght);

	/**
	 * Contruuctor de la clase
	 * 
	 * @param treebank treebank del cual crear las frases en memoria para hacer uso de ellas
	 * en la validacion.
	 */
	public Validator(String treebank) {
		trees = getTreesForFile(treebank);
		
//		Collections.sort(trees, new Comparator<Tree>() {
//			@Override
//			public int compare(Tree arg0, Tree arg1) {
//				// Aqui esta el truco, ahora comparamos p2 con p1 y no al reves como antes
//				return new Integer(arg0.getNodes().size()).compareTo(new Integer(arg1.getNodes().size()));
//			}
//		});
		
		indices = calculateIndex(trees.size(), false);
		numSentences = trees.size();
	}
	
	/**
	 * Contructor vacio
	 * 
	 */
	public Validator() {}

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
	 * @param st
	 *            cadena que contiene el arbol
	 * @return Arbol creado a partir de la cadena
	 */
	public Tree createTree(String st) {
		Tree tree = new Tree();
		ArrayList<Node> nodes = new ArrayList<Node>();
		Stack<Node> stack = new Stack<Node>();
		String value = new String();
		Boolean isValue = false, insertValue = false;

		String[] s = st.split("\\s");
		for (int i = 0; i < s.length; i++) {
			/* Para evitar iteraciones vacias */
			if (s[i].equals("")) {
				continue;
			}

			/* Comienzo de nodo */
			if (s[i].startsWith("(")) {
				/* Crear nodo y guardar clave */
				Node node = new Node();
				String key = s[i].replace("(", "");
				key = key.replace(")", "");
				node.setKey(key);

				/* Incluir a la lista de nodos */
				nodes.add(node);

				/* Guardar los hijos */
				if (!stack.isEmpty()) {
					/* Nodos hijos del arbol */
					Node father = stack.pop();
					father.addChild(node);
					node.setFather(father);
					stack.push(father);
				}

				/* Meter a la pila */
				stack.push(node);
			}

			if (s[i].startsWith("\"")) {
				/* Valor para asignar */
				value = s[i].replaceFirst("\"", "");
				isValue = false;
			}

			/* Si el valor no ha terminado de leerse */
			if (isValue) {
				value += " " + s[i];
			}

			/* Si termina el valor */
			if (value.contains("\"")) {
				value = value.replace(")", "");
				value = value.replace("\"", "");

				/* Si el valor es un ')' */
				if (value.contentEquals("/PUNCT"))
					value = ")" + value;

				insertValue = true;
				isValue = false;
			} else {
				isValue = true;
			}

			/* Meter el valor calculado anteriormente */
			if (insertValue) {
				Node nodoActual = stack.pop();
				nodoActual.setValue(value);
				stack.push(nodoActual);
				insertValue = false;
				value = new String();
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
		Node root = new Node();
		root.setKey("ROOT");
		root.addChild(nodes.get(0));
		nodes.add(0, root);
		tree.setRoot(root);

		/* Guardar los nodos del arbol, y contruye el arbol */
		tree.setNodes(nodes);

		/* Comprobar si esta bien balanceado (Si no error) */
		if (!stack.isEmpty()) {
			System.err.println("Arbol " + tree.getId() + ": no esta bien balanceado");
			System.err.println(tree.getSentence());
			System.err.println(stack);
		}

		return tree;
	}
	
}
