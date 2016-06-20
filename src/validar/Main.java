package validar;

public class Main {

	public static void main(String[] args) {

		if (args.length < 2){
			System.err.println("Introducir al menos los parametros \"-tb\" \"url fichero treebank\"");
			return;
		}

		String treebank = "";
		boolean interna = true;
		boolean notInterna = false;		
		boolean simple = false;
		boolean cruzada = false;
		int n = 10;
		int size = -1;
		
		String input = "";
		String sentences = "";
		
		for(int i = 0; i < args.length; i++){
			if(args[i].equals("-tb")) {
				i++;
				treebank = args[i];
			} else if(args[i].equals("-i")) {
				if(!interna){
					System.err.println("Incompatibilidad en las opciones: -i -e no son compatibles");
					return;					
				}
				notInterna = true;
			} else if(args[i].equals("-e")){
				if(notInterna){
					System.err.println("Incompatibilidad en las opciones: -i -e no son compatibles");
					return;
				}
				interna = false;
			} else if(args[i].equals("-s")){
				simple = true;
			} else if(args[i].equals("-c")){
				cruzada = true;
			} else if(args[i].equals("-n")){
				i++;
				n = Integer.parseInt(args[i]);
			} else if(args[i].equals("-size")){
				i++;
				size = Integer.parseInt(args[i]);
			} else if(args[i].equals("-input")){
				i++;
				input = args[i];
			} else if(args[i].equals("-sentences")){
				i++;
				sentences = args[i];
			}
		}
		
		if (treebank.equals("")){
			System.err.println("Falta el treebank de entramiento");
			return;
		}
		
		if(interna){
			if(!simple && !cruzada || simple){
				ValidadorSimple validador = new ValidadorSimple(treebank);
				RendimientoModelo rendimiento = validador.validate(size);
				rendimiento.print();
			}
			
			if (cruzada){
				ValidadorCruzado validador = new ValidadorCruzado(treebank, n);
				RendimientoModelo rendimiento = validador.validate(size);
				rendimiento.print();
			}
			
		} else {
			if(sentences.equals("")){
				System.err.println("Faltan las frases para validar");
				return;				
			}
			
			if(input.equals("")){
				System.err.println("Faltan las frases bien etiquetadas para calcular rendimiento");
				return;
			}
			ValidadorSimple validador = new ValidadorSimple(treebank);
			RendimientoModelo rendimiento = validador.externalValidation(size, input, sentences);
			rendimiento.print();
		}
	}
	
}
