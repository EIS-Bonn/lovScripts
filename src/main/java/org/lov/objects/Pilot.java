package org.lov.objects;

import java.io.Serializable;

/**
 * Represents statistics associated to a Pilot
 * 
 * @author atrillos
 *
 */
public class Pilot implements Serializable{

	private static final long serialVersionUID = 708893464922222242L;
	private String name;
	private String description;
	int nbOccurrences=0;
	
	public Pilot(){super();}
	
	public Pilot(String name){
		super();
		this.setName(name);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getNbOccurrences() {
		return nbOccurrences;
	}

	public void setNbOccurrences(int nbOccurrences) {
		this.nbOccurrences = nbOccurrences;
	}

	public void addOccurrence(){
		nbOccurrences++;
	}
	
	@Override
    public boolean equals(Object o){
        if(o instanceof Pilot){
        	Pilot toCompare = (Pilot) o;
            return name.equals(toCompare.getName());
        }
        return false;
    }

	
}
