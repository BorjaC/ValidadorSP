package utils;

import java.util.ArrayList;

/**
 * Clase nodo que almacena uno de los nodos del arbol.
 *
 * @author Borja Colmenarejo Garcia
 */
public class Node {

	private String key = ""; /*
								 * Funcion que representa el nodo dentro de la
								 * oracion
								 */
	private String value = ""; /*
								 * Palabra que representa el nodo, no siempre un
								 * nodo contiene una palabra
								 */
	private ArrayList<Node> childrens = new ArrayList<Node>(); /*
															 * Nodos que cuelgan
															 * de este
															 */
	private int nChildrens = 0; /* Numero de hijos que tiene el nodo */
	private Node father = null; /* Padre del nodo, si lo tiene null si no */
	private Boolean plural; /*
							 * Marcar el genero del nodo (Para verbos (VP) y
							 * sintagmas nominales (NP))
							 */

	/**
	 * Contructor de la clase
	 */
	public Node() {
		plural = false;
	}

	/**
	 * Clave del nodo
	 * 
	 * @return devuelve la clave del nodo
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Valor del nodo
	 * 
	 * @return devuelve el valor del nodo
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Hijos del nodo
	 * 
	 * @return devuelve los hijos del nodo, Array vacio si no tiene ninguno
	 */
	public ArrayList<Node> getChildrens() {
		return childrens;
	}

	/**
	 * Numero de hijos del nodo
	 * 
	 * @return devuelve el numero de hijos del nodo
	 */
	public int getNChildrens() {
		return nChildrens;
	}

	/**
	 * Guarda la clave del nodo
	 * 
	 * @param key
	 *            clave para guardar
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Guarda el valor de nodo
	 * 
	 * @param value
	 *            valor para guardar
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Incluye un hijo
	 * 
	 * @param child
	 *            hijo para guardar
	 */
	public void addChild(Node child) {
		childrens.add(child);
		nChildrens++;
	}

	/**
	 * Rrepresentacion del nodo
	 * 
	 * @return representacion del nodo
	 */
	public String toString() {
		if (value != "")
			return "(" + key + " \"" + value + "\")";
		else
			return "(" + key + ")";
	}

	/**
	 * Devuelve el nodo padre
	 * 
	 * @return nodo padre
	 */
	public Node getFather() {
		return father;
	}

	/**
	 * Guarda el padre del nodo
	 * 
	 * @param father
	 *            padre del nodo
	 */
	public void setFather(Node father) {
		this.father = father;
	}

	/**
	 * Devuelve si es singular o plural
	 * 
	 * @return true si es plural, false si es singular
	 */
	public Boolean getPlural() {
		return plural;
	}

	/**
	 * Guarda si es plural o singular
	 * 
	 * @param plural
	 *            true si es plural, false si es singular
	 */
	public void setPlural(Boolean plural) {
		this.plural = plural;
	}

}