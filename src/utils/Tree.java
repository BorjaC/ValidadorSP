package utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase arbol que almacena las oraciones en forma de arbol.
 *
 * @author Borja Colmenarejo Garcia
 */
public class Tree {

	private static int ID = 0;

	private int id; /* Identificador numerico del arbol */
	private ArrayList<Node> nodes = new ArrayList<Node>(); /* Nodos que componen el arbol */
	private Node root = new Node(); /* Nodo raiz del arbol */
	private String sentence = ""; /* Frase que representa el arbol */
	private String representation = ""; /* Representacion del arbol */

	/**
	 * Contructor del arbol, crea un id unico
	 */
	public Tree() {
		id = ID;
		ID++;
	}

	/**
	 * Devuelve el id del arbol
	 * 
	 * @return id del arbol
	 */
	public int getId() {
		return id;
	}

	/**
	 * Devuelve el nodo raiz del arbol
	 * 
	 * @return nodo raiz del arbol
	 */
	public Node getRoot() {
		return root;
	}

	/**
	 * Devuelve la frase que representa el arbol
	 * 
	 * @return frase que representa el arbol
	 */
	public String getSentence() {
		return sentence;
	}

	/**
	 * Crea de forma recursiva la frase que representa el arbol
	 * 
	 * @param node
	 *            nodo raiz del cual empezar la representacion del arbol
	 * @return frase que representa el arbol
	 */
	private String getSentenceAux(Node node) {
		String ret = "";

		if (node.getValue() != "") {
			ret += node.getValue() + " ";
		} else {
			for (int i = 0; i < node.getNChildrens(); i++) {
				ret += getSentenceAux(node.getChildrens().get(i));
			}
		}
		return ret;
	}

	/**
	 * Guarda los nodos del arbol, y crea las representaciones en forma de arbo,
	 * y la frase del mismo
	 * 
	 * @param nodes
	 *            nodos del arbol
	 */
	public void setNodes(ArrayList<Node> nodes) {
		this.nodes.addAll(nodes);
		sentence = getSentenceAux(root);
		representation = toStringAux(root, "");
	}

	/**
	 * Devuelve los nodos del arbol
	 * 
	 * @return nodos del arbol
	 */
	public ArrayList<Node> getNodes() {
		return nodes;
	}

	/**
	 * Guarda la raiz del arbol
	 * 
	 * @param root
	 *            raiz del arbol
	 */
	public void setRoot(Node root) {
		this.root = root;
	}

	/**
	 * Devuelve la representacion en forma del arbol del mismo
	 */
	public String toString() {
		return representation;
	}

	/**
	 * Crea la representacion de forma recursiva en forma de arbol del mismo
	 * 
	 * @param node
	 *            nodo raiz del cual empezar la representacion
	 * @param sp
	 *            espacio para crear una representacion jerarquica mas clara
	 * @return representacion del arbol
	 */
	private String toStringAux(Node node, String sp) {
		String ret = "";

		if (node.getValue() != "") {
			ret += "\n" + sp + "(" + node.getKey() + " \"" + node.getValue() + "\")";
		} else {
			ret += "\n" + sp + "(" + node.getKey();

			for (int i = 0; i < node.getNChildrens(); i++) {
				ret += toStringAux(node.getChildrens().get(i), " " + sp);
			}

			ret += ")";
		}

		return ret;
	}

	/**
	 * Devuelve los nodos del arbol que contiene la clave pasada y respetan el numero del nodo
	 * 
	 * @param key clave donde buscar el nodo
	 * @param plural numero del nodo, true plural, false singular
	 * @return la lista de nodos que contienen el arbol con la clave y el numero
	 */
	public List<Node> getNodesKey(String key, boolean plural) {
		return getNodesKey(root, key, plural);
	}

	/**
	 * Funcion recursiva auxiliar que devuelve los nodos del arbol que
	 * contiene la clave pasada y respetan el numero del nodo
	 * 
	 * @param node nodo desde el cual empezar la busqueda
	 * @param key clave donde buscar el nodo
	 * @param plural numero del nodo, true plural, false singular
	 * @return la lista de nodos que contienen el arbol con la clave y el numero
	 */
	private List<Node> getNodesKey(Node node, String key, boolean plural) {
		List<Node> ret = new ArrayList<Node>();

		if (node.getKey().contentEquals(key) && node.getPlural() == plural) {
			ret.add(node);
		}

		for (int i = 0; i < node.getNChildrens(); i++) {
			ret.addAll(getNodesKey(node.getChildrens().get(i), key, plural));
		}

		return ret;
	}

	/**
	 * Intercambia el primer nodo que coincida con la clave y el numero por un nodo dado
	 * 
	 * @param node nodo para intercambiar
	 * @param key clave para buscar
	 * @param plural numero para buscar
	 * @return el nuevo arbol, null si no hay nuevo arbol
	 */
	public Tree swap(Node node, String key, boolean plural) {		
		if (swap(root, node, key, plural))
			return this;
		else 
			return null;
	}

	/**
	 * Funcion recursiva auxiliar que intercambia el primer nodo que coincida
	 * con la clave y el numero por un nodo dado
	 * 
	 * @param node1 nodo desde el cual empezar la busqueda para intercambiar
	 * @param node2 nodo para intercambiar
	 * @param key clave para buscar
	 * @param plural numero para buscar
	 * @return el nuevo arbol, null si no hay nuevo arbol
	 */	
	private boolean swap(Node node1, Node node2, String key, boolean plural) {

		boolean ret = false;
		
		if (node1 == null || node2 == null) {
			return false;
		}

		if (node1.getPlural() == plural && node1.getKey().contentEquals(key)) {

			Node father = node1.getFather();
			int idx;
			idx = father.getChildrens().indexOf(node1);
			father.getChildrens().remove(idx);
			father.getChildrens().add(idx, node2);

			node2.setFather(node1.getFather());
			node2.setKey(key);

			this.setNodes(nodes);
			ret = true;
		} 
		
		for (int i = 0; i < node1.getNChildrens(); i++) {
			if (!ret){
				ret = ret || swap(node1.getChildrens().get(i), node2, key, plural);
			}
		}
		
		return ret;
	}

}