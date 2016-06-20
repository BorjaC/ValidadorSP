package validar;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import utiles.Arbol;

/**
 * Clase que implementa el validador cruzado
 * 
 * @author Borja Colmenarejo Garcia
 */
public class ValidadorCruzado extends Validador {

	/* Listas para almacenar los ficheros para la cross validate */
	private List<String> trainFile = new ArrayList<String>();
	private List<String> sentenceFile = new ArrayList<String>();
	private List<String> inputFile = new ArrayList<String>();
	private int n = 1;	/* Numero de particiones */
	private int mode = 2; /* Marca el modo con el que se realiza la validacion */
	/* modo = 1 usando el mejor modelo */
	/* modo = 2 usando la media de los modelons */
	
	/**
	 * Constructor de la clase que recibe el numero de particiones que usara la validacion cruzada
	 * 
	 * @param n numero de particiones de la validacion
	 */
	public ValidadorCruzado(int n) {
		super();
		this.n = n;
	}
	
	/**
	 * Constructor de la clase
	 * 
	 * @param treebank treebank del cual se leeran los datos 
	 * @param n numero de particiones de la validacion cruzada
	 */
	public ValidadorCruzado(String treebank, int n){
		super(treebank);
		this.n = n;
	}
	
	/**
	 * Guarda el numero de particiones de la validacion 
	 * 
	 * @param n numero de particiones de la validacion
	 */
	public void setN(int n){
		this.n = n;
	}
	
	/**
	 * Crear los fichero de entrenamiento, entrada y de frases para la
	 * validacion cruzada
	 * 
	 * @param n
	 *            numero de ficheros (particiones) de la validacion cruzada
	 */
	protected void createFiles(int setLenght) {

		BufferedWriter train;
		BufferedWriter rows;
		BufferedWriter input;

		int nTree;
		if (setLenght == -1) {
			nTree = trees.size();
		} else {
			nTree = setLenght;
		}
		
		/* Calculamos el numero de indices correspondientes a cada particion */
		int tamPart = (int) Math.floor(nTree / n);

		try {
			int idx = 0;
			for (int i = 0; i < n; i++) {
				// Crear los ficheros
				String in = URL_INPUT + INPUT + (i + 1) + TXT;
				String r = URL_SENTENCE + SENTENCE + (i + 1) + TXT;
				String t = URL_TRAIN + TRAIN + (i + 1) + LISP;
				trainFile.add(t);
				sentenceFile.add(r);
				inputFile.add(in);
				input = new BufferedWriter(new FileWriter(in));
				train = new BufferedWriter(new FileWriter(t));
				rows = new BufferedWriter(new FileWriter(r));

				/* Posicionar indice en el primer dato ha de guardar en train */
				idx = tamPart * i;
				/* Guardar datos de train */
				for (int k = 0; k < (nTree - tamPart); k++, idx++) {
					if (idx == nTree - 1) {
						idx = 0;
					}
					train.write(trees.get(indices.get(idx)).toString() + "\n");
				}
				/* Guardar datos de test */
				for (int j = 0; j < tamPart; j++, idx++) {
					rows.write(trees.get(indices.get(idx)).getFrase() + "\n");
					input.write(trees.get(indices.get(idx)).toString() + "\n");
				}

				// Cerrar los ficheros si no puede fallar
				train.close();
				rows.close();
				input.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Validar el modelo
	 * 
	 * @param setLenght
	 *            tamaño del conjunto a validar
	 */
	public RendimientoModelo validate(int setLenght) {

		createDirectories();

		/* Crear los ficheros para la validacion cruzada */
		createFiles(setLenght);

		RendimientoModelo d = new RendimientoModelo();
		RendimientoModelo rm = new RendimientoModelo();
		double f1score = 0.0;
		for (int i = 0; i < n; i++) {
			String out = URL_OUTPUT + OUTPUT + (i + 1) + TXT;
			String model = URL_MODEL + MODEL + (i + 1) + GZ;
			/* Entrenar */
			train(trainFile.get(i), model, URL_MODELTXT + MODELTXT + (i + 1) + TXT);
			/* Test */
			test(sentenceFile.get(i), out, model);

			/* Obtener rendimiento */
			d = VerificadorArbol.validate(out, inputFile.get(i));
			double f1scorePart = d.f1Score();
			/* Maximo */
			if (mode == 1) {
				if (f1scorePart > f1score) {
					f1score = f1scorePart;
					rm = d;
				}
				/* Media */
			} else if (mode == 2) {
				f1score += f1scorePart;
				rm.setTP(d.getTP() + rm.getTP());
				rm.setFP(d.getFP() + rm.getFP());
				rm.setFN(d.getFN() + rm.getFN());
				rm.setTTOut(d.getTTOut() + rm.getTTOut());
				rm.setTTIn(d.getTTIn() + rm.getTTIn());
			}
		}

		deleteDirectories();

		return rm;
	}
	
	
	/**
	 * Calcula que oraciones se puntuan mejor y el numero de las mismas.
	 * Crea ademas ficheros que almacenan las oraciones en forma de arbol
	 * 
	 * @param setLenght tamaño del conjunto a evaluar
	 * @return lista de arboles 
	 */
	public List<Arbol> getTreesError(int setLenght) {
		List<Arbol> treesError = new ArrayList<Arbol>();

		BufferedWriter golden0_5;
		BufferedWriter golden0_6;
		BufferedWriter golden0_7;
		BufferedWriter golden0_8;
		BufferedWriter golden0_9;
		BufferedWriter golden1;

		BufferedWriter out0_5;
		BufferedWriter out0_6;
		BufferedWriter out0_7;
		BufferedWriter out0_8;
		BufferedWriter out0_9;
		BufferedWriter out1;

		int trees0_5 = 0;
		int trees0_6 = 0;
		int trees0_7 = 0;
		int trees0_8 = 0;
		int trees0_9 = 0;
		int trees1 = 0;

		try {
			golden0_5 = new BufferedWriter(new FileWriter("data/golden0_5.txt"));
			golden0_6 = new BufferedWriter(new FileWriter("data/golden0_6.txt"));
			golden0_7 = new BufferedWriter(new FileWriter("data/golden0_7.txt"));
			golden0_8 = new BufferedWriter(new FileWriter("data/golden0_8.txt"));
			golden0_9 = new BufferedWriter(new FileWriter("data/golden0_9.txt"));
			golden1 = new BufferedWriter(new FileWriter("data/golden1.txt"));

			out0_5 = new BufferedWriter(new FileWriter("data/out0_5.txt"));
			out0_6 = new BufferedWriter(new FileWriter("data/out0_6.txt"));
			out0_7 = new BufferedWriter(new FileWriter("data/out0_7.txt"));
			out0_8 = new BufferedWriter(new FileWriter("data/out0_8.txt"));
			out0_9 = new BufferedWriter(new FileWriter("data/out0_9.txt"));
			out1 = new BufferedWriter(new FileWriter("data/out1.txt"));

			createDirectories();

			/* Crear los ficheros para la validacion cruzada */
			createFiles(setLenght);

			for (int i = 0; i < numSentences; i++) {
				String out = URL_OUTPUT + OUTPUT + (i + 1) + TXT;
				String model = URL_MODEL + MODEL + (i + 1) + GZ;
				/* Entrenar */
				train(trainFile.get(i), model, URL_MODELTXT + MODELTXT + (i + 1) + TXT);
				/* Test */
				test(sentenceFile.get(i), out, model);

				/* Obtener rendimiento */
				RendimientoModelo d = VerificadorArbol.validate(out, inputFile.get(i));
				double f1score = d.f1Score();
				if (f1score >= 0.5) {
					golden0_5.write(trees.get(indices.get(i)).toString() + "\n");
					copyText(out0_5, out);
					trees0_5++;
				}

				if (f1score >= 0.6) {
					golden0_6.write(trees.get(indices.get(i)).toString() + "\n");
					copyText(out0_6, out);
					trees0_6++;
				}

				if (f1score >= 0.7) {
					golden0_7.write(trees.get(indices.get(i)).toString() + "\n");
					copyText(out0_7, out);
					trees0_7++;
				}

				if (f1score >= 0.8) {
					golden0_8.write(trees.get(indices.get(i)).toString() + "\n");
					copyText(out0_8, out);
					trees0_8++;
				}

				if (f1score >= 0.9) {
					golden0_9.write(trees.get(indices.get(i)).toString() + "\n");
					copyText(out0_9, out);
					trees0_9++;
				}

				if (f1score == 1) {
					golden1.write(trees.get(indices.get(i)).toString() + "\n");
					copyText(out1, out);
					trees1++;
				}

			}

			golden0_5.close();
			golden0_6.close();
			golden0_7.close();
			golden0_8.close();
			golden0_9.close();
			golden1.close();

			out0_5.close();
			out0_6.close();
			out0_7.close();
			out0_8.close();
			out0_9.close();
			out1.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		deleteDirectories();

		System.out.println("Resultados");
		System.out.println(">= 0.5 " + trees0_5);
		System.out.println(">= 0.6 " + trees0_6);
		System.out.println(">= 0.7 " + trees0_7);
		System.out.println(">= 0.8 " + trees0_8);
		System.out.println(">= 0.9 " + trees0_9);
		System.out.println("== 1 " + trees1);

		return treesError;
	}	
	
}
