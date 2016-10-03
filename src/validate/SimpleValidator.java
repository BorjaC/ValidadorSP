package validate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Clase que implementa el validador simple
 * 
 * @author Borja Colmenarejo Garcia
 */
public class SimpleValidator extends Validator {

	/**
	 * Contructor de la clase
	 * 
	 * @param treebank treebank para leer
	 */
	@Deprecated
	public SimpleValidator(String treebank) {
		super(treebank);
	}

	/**
	 * Contructor de la clase
	 * 
	 * @param treebank treebank para leer
	 * @param save parametro que indica que se quiere guardar el modelo
	 */
	public SimpleValidator(String treebank, boolean save) {
		super(treebank);
		this.save = save;
	}

	/**
	 * Contructor de la clase
	 * 
	 */
	public SimpleValidator() {
		super();
	}

	/**
	 * Validar el modelo
	 * 
	 * @param setLenght
	 *            tamaño del conjunto que se quiere validar
	 */
	public ResultsModel validate(int setLenght) {

		/* Crear directorios temporales */
		createDirectories();

		/* Crear los ficheros */
		createFiles(setLenght);

		/* Entrenar */
		train(URL_TRAIN + TRAIN + LISP, URL_MODEL + MODEL + GZ, URL_MODELTXT + MODELTXT + TXT);

		/* Test */
		test(URL_SENTENCE + SENTENCE + TXT, URL_OUTPUT + OUTPUT + TXT, URL_MODEL + MODEL + GZ);

		ResultsModel rendimiento = TreeVerificator.validate(URL_OUTPUT + OUTPUT + TXT, URL_INPUT + INPUT + TXT);
		
		/* Guardar el modelo, si se requiere */
		if (save){
			moveFlie(URL_TRAIN + TRAIN + LISP);
			moveFlie(URL_MODEL + MODEL + GZ);
			moveFlie(URL_OUTPUT + OUTPUT + TXT);
		}
		
		/* Eliminar los directorios temporales */
		deleteDirectories();
		
		/* Calcular rendimiento */
		return rendimiento;

	}
	
	/**
	 * Crear los fichero de entrenamiento, entrada y de frases para la
	 * validacion cruzada
	 * 
	 * @param setLenght
	 *            tamaño del conjunto contenido en los ficheros
	 */
	protected void createFiles(int setLenght) {

		BufferedWriter train;
		BufferedWriter sentences;
		BufferedWriter input;
		
		if(setLenght == -1){
			setLenght = numSentences;
		}

		try {
			input = new BufferedWriter(new FileWriter(URL_INPUT + INPUT + TXT));
			train = new BufferedWriter(new FileWriter(URL_TRAIN + TRAIN + LISP));
			sentences = new BufferedWriter(new FileWriter(URL_SENTENCE + SENTENCE + TXT));

			for (int idx = 0; idx < setLenght; idx++) {
				/* Guardar datos de train */
				train.write(trees.get(indices.get(idx)).toString() + "\n");
				/* Guardar datos de test */
				sentences.write(trees.get(indices.get(idx)).getSentence() + "\n");
				/* Guardar los datos de input (para comprobar con output) */
				input.write(trees.get(indices.get(idx)).toString() + "\n");
			}

			/* Cerrar los ficheros si no puede fallar */
			train.close();
			sentences.close();
			input.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Validacion externa
	 * 
	 * @param setLenght tamaño del conjunto de entrenamiento
	 * @param gold fichero de entrada bien etiquetado
	 * @param sentences frases a evaluar
	 * @return Clase con el calculo del rendimiento obtenido.
	 */
	public ResultsModel externalValidation(int setLenght, String gold, String sentences) {

		createDirectories();

		String url = "data/";

		File file = new File(url);
		file.mkdirs();

		/* Crear los ficheros */
		createFiles(setLenght);

		/* Entrenar */
		train(URL_TRAIN + TRAIN + LISP, URL_MODEL + MODEL + GZ, URL_MODELTXT + MODELTXT + TXT);

		/* Test */
		test(sentences, url + "ext_" + OUTPUT + TXT, URL_MODEL + MODEL + GZ);

		ResultsModel rm = TreeVerificator.validate(url + "ext_" + OUTPUT + TXT, gold);

		/* Guardar el mejor modelo, si se requiere */
		if (save){
			moveFlie(URL_TRAIN + TRAIN + LISP);
			moveFlie(URL_MODEL + MODEL + GZ);
			moveFlie(url + OUTPUT + TXT);
		}
		
		/* Borrar los ficheros temporales creados */
		deleteDirectory(url);
		deleteDirectories();
		
		/* Devolver el objeto con los datos del rendimiento */
		return rm;
	}

	/**
	 * Validacion externa
	 * 
	 * @param setLenght tamaño del conjunto de entrenamiento
	 * @param gold fichero de entrada bien etiquetado
	 * @param sentences frases a evaluar
	 * @param model modelo con el cual validar
	 * @return Clase con el calculo del rendimiento obtenido.
	 */
	public ResultsModel externalValidation(int setLenght, String gold, String sentences, String model) {

		createDirectories();

		String url = "data/";

		File file = new File(url);
		file.mkdirs();

		/* Test */
		test(sentences, url + "ext_" + OUTPUT + TXT, model);

		ResultsModel rm = TreeVerificator.validate(url + OUTPUT + TXT, gold);

		/* Guardar el mejor modelo, si se requiere */
		if (save){
			moveFlie(URL_TRAIN + TRAIN + LISP);
			moveFlie(URL_MODEL + MODEL + GZ);
			moveFlie(url + OUTPUT + TXT);
		}
		
		/* Borrar los ficheros temporales creados */
		deleteDirectory(url);
		deleteDirectories();

		/* Devolver el objeto con los datos del rendimiento */
		return rm;
	}
	
}
