package util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.traverse.DepthFirstIterator;

import model.Alignment;
import model.Ontology;

public class NetworkofOntologies {
		private Graph <Ontology,Alignment> ontoGraph = new DirectedPseudograph <Ontology,Alignment>(Alignment.class);
		private static NetworkofOntologies network;
		
		public static NetworkofOntologies getNetworkofOntologies() {
			if (network==null) {
				network = new NetworkofOntologies();
			}
			return network;
			
		}
		public Graph<Ontology, Alignment> getOntoGraph() {
			return ontoGraph;
		}

		public void setOntoGraph(Graph<Ontology, Alignment> ontoGraph) {
			this.ontoGraph = ontoGraph;
		}
		
		public boolean createOntology(Ontology newOntology) {
			if (!ontoGraph.containsVertex(newOntology)) {
				return ontoGraph.addVertex(newOntology);
			}
			return false;
		}
		
		public boolean createAlignment(Alignment newAlignment) {
			if (!ontoGraph.containsEdge(newAlignment)) {
				return ontoGraph.addEdge(newAlignment.getOntologia1(),newAlignment.getOntologia2(),newAlignment);
			}
			return false;
		}
		
		public boolean checkCycles(Ontology startPoint) {
			
			Ontology position = startPoint;
			List<Ontology> visitedOntos = new ArrayList<Ontology>();
			
			visitedOntos.add(startPoint);
			
			// API JGraphT
			DepthFirstIterator<Ontology, Alignment> efi = new DepthFirstIterator<Ontology, Alignment>(ontoGraph,startPoint);
			
			while (efi.hasNext()) {
				position = efi.next();
				System.out.println(" visitei - " + position.getName());
				if (visitedOntos.contains(position)) {
					System.out.println(" Achei ciclo!");
					return true;
				} else {
					visitedOntos.add(position);
				}
			}

			// by the hand...
			/*
			Set<Alignment> vertexes = ontoGraph.outgoingEdgesOf(startPoint);

			for (Iterator<Alignment> i = vertexes.iterator(); i.hasNext();) {
				Alignment aux = i.next();
				position = aux.getOntologia2();
			}
			*/
			return false;
		}
		
}
