package org.lov.objects;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a Dataset: dataset using vocabularies
 * 
 * @author Pierre-Yves Vandenbussche
 *
 */
public class Dataset implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1561010940197708509L;
	private String uri;
	private String label;
	private int occurrences;
	private Date createdAt;	
	
	
	public Dataset(){super();}
	public Dataset(String uri, String label, int occurrences, Date createdAt){
		super();
		this.uri=uri;
		this.label=label;
		this.occurrences=occurrences;
		this.createdAt=createdAt;
	}
	
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public int getOccurrences() {
		return occurrences;
	}
	public void setOccurrences(int occurrences) {
		this.occurrences = occurrences;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
}
