package validate;

/**
 * Clase para almacenar los datos obtenidos de verificar un conjunto de arboles
 *
 * @author Borja Colmenarejo Garcia
 */
public class ResultsModel {

	private int tp; /* True positives */
	private int fp; /* False positives */
	private int fn; /* False negatives */
	private int ttIn; /* Total tags in input file */
	private int ttOut; /* Total tags in output file */

	/**
	 * Crea un nuevo rendimiento de modelo vacio
	 * 
	 */
	public ResultsModel() {
		this.tp = this.fp = this.fn = this.ttIn = this.ttOut = 0;
	}

	/**
	 * Crear un nuevo rendimiento de modelo con los parametros dados
	 * 
	 * @param tp
	 *            true positives
	 * @param fp
	 *            false positives
	 * @param fn
	 *            false negatives
	 * @param ttIn
	 *            etiquetas totales del fichero de entrada
	 * @param ttOut
	 *            etiqueta totales del ficherod de salida
	 */
	public ResultsModel(int tp, int fp, int fn, int ttIn, int ttOut) {
		this.tp = tp;
		this.fp = fp;
		this.fn = fn;
		this.ttIn = ttIn;
		this.ttOut = ttOut;
	}

	/**
	 * Metodo para obtener los true positives del rendimiento
	 * 
	 * @return true positives
	 */
	public int getTP() {
		return tp;
	}

	/**
	 * Remplaza los true positives del modelo por otro dados 
	 * 
	 * @param tp nuevos resultados para true positives
	 */
	public void setTP(int tp) {
		this.tp = tp;
	}

	/**
	 * Metodo para obtener los false positives del rendimiento
	 * 
	 * @return false positives
	 */
	public int getFP() {
		return fp;
	}

	/**
	 * Remplaza los false positives del modelo por otro dados 
	 * 
	 * @param fp nuevos resultados para false positives
	 */
	public void setFP(int fp) {
		this.fp = fp;
	}

	/**
	 * Metodo para obtener los false negatives del rendimiento
	 * 
	 * @return false negatives
	 */
	public int getFN() {
		return fn;
	}

	/**
	 * Remplaza los false negatives del modelo por otro dados 
	 * 
	 * @param fn nuevos resultados para false negatives
	 */
	public void setFN(int fn) {
		this.fn = fn;
	}

	/**
	 * Metodo para obtener las etiquetas totales del fichero de entrada
	 * 
	 * @return totatl de etiquetas del fichero de entrada (entrenamiento)
	 */
	public int getTTIn() {
		return ttIn;
	}

	/**
	 * Remplaza el numero de etiquetas totales del fichero de entrada
	 * 
	 * @param ttIn numero de etiquetas totales en el fichero de entrada
	 */
	public void setTTIn(int ttIn) {
		this.ttIn = ttIn;
	}

	/**
	 * Metodo para obtener las etiquetas totales del fichero de salida
	 * 
	 * @return totatl de etiquetas del fichero de salida
	 */
	public int getTTOut() {
		return ttOut;
	}

	/**
	 * Remplaza el numero de etiquetas totales del fichero de salida
	 * 
	 * @param ttOut numero de etiquetas totales en el fichero de salida
	 */
	public void setTTOut(int ttOut) {
		this.ttOut = ttOut;
	}

	/**
	 * Funcion F1Score, se puede interpretar como un promedio ponderado de la
	 * precisión y la recuperación, donde una puntuación de F1 alcanza su mejor
	 * valor en 1 y lo peor a 0.
	 * 
	 * @return Resultado de verificar el arbol usando f1Score
	 */
	public double f1Score() {
		double precision = precision();
		double recall = recall();

		return 2 * ((precision * recall) / (precision + recall));
	}

	/**
	 * Metodo para obtener la precision del modelo medido
	 * 
	 * @return precision
	 */
	public double precision() {
		return (double) tp / (double) (tp + fp);
	}

	/**
	 * Metodo para obtener el recall del modelo medido
	 * 
	 * @return recall
	 */
	public double recall() {
		return (double) tp / (double) (tp + fn);
	}

	/**
	 * Imprime los diferentes datos del resultado obtenido
	 * 
	 */
	public void print() {

		System.out.println("Summary\n");

		System.out.println("TP\t" + tp + "\tTrue positives");
		System.out.println("FN\t" + fn + "\tFalse negatives");
		System.out.println("FP\t" + fp + "\tFalse positives\n");

		System.out.println("precision: " + precision());
		System.out.println("recall: " + recall());

		System.out.println("F1Score: " + f1Score());
	}

	/**
	 * Metodo que encadena con los resultados obtenidos en la medidas del modelo
	 * 
	 * @return cadena con los resultados obtenidos en la medida del modelo
	 */
	public String toString() {
		return "tp: " + tp + "\nfp: " + fp + "\nfn: " + fn;
	}

}
