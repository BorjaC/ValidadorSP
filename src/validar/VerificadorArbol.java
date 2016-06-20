package validar;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

/** 
 * Clase que recibe dos ficheros, uno con arboles bien etiquetados y otro con los
 * arboles clasificados por el Stanford Parser y obtiene la tasa de error y de acierto del
 * algoritmo. Los errores y fallos los obtiene comparando las etiquetas.
 * 
 * @author Borja Colmenarejo Garcias
 */
public class VerificadorArbol {

	/**
	 * Analiza y evalua las etiquetas del modelo
	 * 
	 * @param outputFile fichero de salida etiquetado por el Stanford Parser
	 * @param inputFile fichero con las etiquetas correctas 
	 * @return Devuelve un objeto que contiene los aciertos (true positive), y fallos del modelo (false positives y 
	 * false negatives)
	 */
	public static RendimientoModelo validate(String outputFile, String inputFile) {
		List<List<String>> outputList = getTagListForFile(outputFile);
		List<List<String>> correctedList = getTagListForFile(inputFile);

		/* Contadores de datos */
		int tp = 0; /* True positives */
		int fp = 0; /* False positives */
		int fn = 0; /* False negatives */

		/* Añadido por mi */
		int ttTrain = 0; /* Total de etiquetas en el fichero de entrada */
		int ttOutput = 0; /* Total de etiquetas en el fichero de salida */

		for (int i = 0; i < outputList.size(); i++) {
			tp--; /* Quitar la etiqueta ROOT, siempre va a ser correcta */
			List<String> outputTreeList = outputList.get(i);
			List<String> correctedTreeList = correctedList.get(i);

			ttTrain += correctedTreeList.size() - 1;
			ttOutput += outputTreeList.size() - 1;

			int fnForTree = correctedTreeList.size();
			
			for (String node : outputTreeList) {
				if (correctedTreeList.contains(node)) {
					tp++;
					fnForTree--;
				} else {
					fp++;
					// fn ++;
				}
			}
			fn += fnForTree;
		}

		return new RendimientoModelo(tp, fp, fn, ttTrain, ttOutput);

	}

	/**
	 * Crea las etiquetas a partir de los ficheros
	 * 
	 * @param filename nombre del fichero 
	 * @return una lista de cadenas donde cada una representa las etiquetas de un arbol
	 */
	public static List<List<String>> getTagListForFile(String filename) {

		List<List<String>> tagsForFile = new ArrayList<List<String>>();

		Scanner sc;
		
		try {
			sc = new Scanner(new File(filename));
			int wordPos = 0;
			String s;
			List<String> tagsForTree = new ArrayList<String>();
			Stack<String> stack = new Stack<String>();

			while (sc.hasNext()) {
				s = sc.next();
				if (s.startsWith("(")) {
					stack.push(s.substring(1) + "(" + wordPos + ",");
				}
				if (s.endsWith(")")) {
					wordPos++;
					/* Extraer varios nodos a la vez (Puede a ver varios ')' seguidos)*/
					int idx = s.lastIndexOf(")");
					int j = 0;
					while ((idx - j >= 0) && (s.charAt(idx - j) == ')')) {
						j++;
						String tag = stack.pop();
						tagsForTree.add(tag + wordPos + ")");
					}
					if (stack.isEmpty()) {
						wordPos = 0;
						tagsForFile.add(tagsForTree);
						tagsForTree = new ArrayList<String>();
					}
				}
			}

			sc.close();
		} catch (FileNotFoundException e) {
			System.out.println(filename);
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		}

		return tagsForFile;
	}

}
