package validate;

/**
 * Clase para ejecutar la herramienta
 * 
 * @author Borja Colmenarejo Garcia
 */
public class Main {

	public static void main(String[] args) {

		/* Error de aviso de minimo de errores */
		if (args.length < 2) {
			System.err.println("Introducir al menos los parametros \"-tb\" \"url fichero treebank\"");
			return;
		}

		/* variables para controlar los parametros de ejecucion */
		String treebank = "";
		boolean interna = true;
		boolean notInterna = false;
		boolean simple = false;
		boolean cruzada = false;
		int n = 10;
		int size = -1;
		String gold = "";
		String sentences = "";
		boolean save = false;
		String model = "";

		/* Lectura de los parametros de ejecucion */
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-tb")) {
				i++;
				treebank = args[i];
			} else if (args[i].equals("-i")) {
				if (!interna) {
					System.err.println("Incompatibilidad en las opciones: -i -e no son compatibles");
					return;
				}
				notInterna = true;
			} else if (args[i].equals("-e")) {
				if (notInterna) {
					System.err.println("Incompatibilidad en las opciones: -i -e no son compatibles");
					return;
				}
				interna = false;
			} else if (args[i].equals("-s")) {
				simple = true;
			} else if (args[i].equals("-c")) {
				cruzada = true;
			} else if (args[i].equals("-n")) {
				i++;
				n = Integer.parseInt(args[i]);
			} else if (args[i].equals("-size")) {
				i++;
				size = Integer.parseInt(args[i]);
			} else if (args[i].equals("-gold")) {
				i++;
				gold = args[i];
			} else if (args[i].equals("-sentences")) {
				i++;
				sentences = args[i];
			} else if (args[i].equals("-save")) {
				save = true;
			} else if (args[i].equals("-model")) {
				i++;
				model = args[i];
			}
		}

		/* Validacion interna */
		if (interna) {
			/* Error de treebank */
			if (treebank.equals("")) {
				System.err.println("Falta el treebank de entramiento");
				return;
			}
			/* Validacion interna simple */
			if (!simple && !cruzada || simple) {
				SimpleValidator validador = new SimpleValidator(treebank, save);
				ResultsModel rendimiento = validador.validate(size);
				rendimiento.print();
			}
			/* Validacion interna cruzada */
			if (cruzada) {
				CrossValidator validador = new CrossValidator(treebank, n, save);
				ResultsModel rendimiento = validador.validate(size);
				rendimiento.print();
			}
			/* Validacion externa */
		} else {
			/* Error de fichero sentences */
			if (sentences.equals("")) {
				System.err.println("Faltan las frases para validar");
				return;
			}
			/* Error de fichero input (gold, frases bien etiquetadas) */
			if (gold.equals("")) {
				System.err.println("Faltan las frases bien etiquetadas para calcular rendimiento");
				return;
			}
			/* Error de modelo y treebank */
			if (model.equals("") && treebank.equals("")) {
				System.err.println("Se necesita un modelo para validar o un treebank del cual crear el modelo");
				return;
			}

			SimpleValidator validador;
			ResultsModel rendimiento;

			/*
			 * En caso de modelo y treebank para crear modelo, el orden de
			 * prioridad es utilizar primero el modelo dado
			 */
			if (!model.equals("")) {
				validador = new SimpleValidator();
				rendimiento = validador.externalValidation(size, gold, sentences, model);
			} else {
				validador = new SimpleValidator(treebank, save);
				rendimiento = validador.externalValidation(size, gold, sentences);
			}

			rendimiento.print();
		}
	}

}
