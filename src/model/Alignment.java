package model;

import java.util.ArrayList;
import java.util.List;

public class Alignment {
	
	private Ontology ontologia1;
	private Ontology ontologia2;
	private List<Map> mappings = new ArrayList<Map>();
	
	public Ontology getOntologia1() {
		return ontologia1;
	}
	public void setOntologia1(Ontology ontologia1) {
		this.ontologia1 = ontologia1;
	}
	public Ontology getOntologia2() {
		return ontologia2;
	}
	public void setOntologia2(Ontology ontologia2) {
		this.ontologia2 = ontologia2;
	}
	public List<Map> getMappings() {
		return mappings;
	}
	public void setMappings(List<Map> mappings) {
		this.mappings = mappings;
	}
	public void addMap(Map m) {
		// TODO Auto-generated method stub
		mappings.add(m);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ontologia1 == null) ? 0 : ontologia1.hashCode());
		result = prime * result + ((ontologia2 == null) ? 0 : ontologia2.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Alignment other = (Alignment) obj;
		if (ontologia1 == null) {
			if (other.ontologia1 != null)
				return false;
		} else if (!ontologia1.equals(other.ontologia1))
			return false;
		if (ontologia2 == null) {
			if (other.ontologia2 != null)
				return false;
		} else if (!ontologia2.equals(other.ontologia2))
			return false;
		return true;
	}
	

}
