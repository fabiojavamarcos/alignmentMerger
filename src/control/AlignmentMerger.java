package control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import model.Alignment;
import model.Map;
import model.Ontology;
import util.EOFRDFExcetion;
import util.NetworkofOntologies;

public class AlignmentMerger {

	private Alignment alignment = new Alignment();
	private List<Alignment> alinhamentos = new ArrayList<Alignment>();
	private List<Alignment> alinhamentosDuplicate = new ArrayList<Alignment>();
	
	NetworkofOntologies nOO = NetworkofOntologies.getNetworkofOntologies();

	public AlignmentMerger(String[] args) {
		// TODO Auto-generated constructor stub
		String dirTrab = System.getProperty("user.dir");
		String format = "owl";
		if (args.length==0) {
			dirTrab = System.getProperty("user.dir");
		} else {
		
			dirTrab=args[0];
			format = args[1];
			
		}
		
		//JFileChooser fileChooser = new JFileChooser();
		//fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		//File selectedFile = new File("C:\\\\temp\\\\ALIN-cmt-confOf.rdf");
		/*int result = fileChooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
		    selectedFile = fileChooser.getSelectedFile();
		    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
		}
		*/
		
		System.out.println("Processing ... "+dirTrab);
		File folder = new File(dirTrab);
		 
		//processFile(selectedFile);
		File[] listOfFiles = folder.listFiles();
		ArrayList<File> files = new ArrayList<>();
		
	    for (int i = 0; i < listOfFiles.length; i++) {
	    	if (listOfFiles[i].isFile()) {
		        System.out.println("File " + listOfFiles[i].getName());
		        files.add(listOfFiles[i]);
		        alignment = processFile(listOfFiles[i]);
		        boolean isNew;
		        if (alignment!=null) {
		        	/*isNew = alinhamentos.add(alignment);
		        	if (!isNew) {
		        		System.out.println("Alinhamento igual:"+ listOfFiles[i].getName());
		        		alinhamentosDuplicate.add(alignment);
			        	
		        		
		        	}
		        	*/
		        	boolean found = false;
		        	for (Iterator i1 = alinhamentos.iterator(); i1.hasNext(); ) {
		        		Alignment alinAux = (Alignment) i1.next();
		        		if (alinAux.getOntologia1().getName() .equals( alignment.getOntologia1().getName()) && alinAux.getOntologia2().getName().equals( alignment.getOntologia2().getName())) {
		        			found = true;
		        		}
		        	}
		        	if (!found) {
		        		alinhamentos.add(alignment);
		        	} else {
		        		alinhamentosDuplicate.add(alignment);
		        	}
		        }
		    } else if (listOfFiles[i].isDirectory()) {
		        System.out.println("Directory " + listOfFiles[i].getName());
		    }
	    }
	    
	    //processAlignments(alinhamentos);
	    
	    //createNetwork(alinhamentos);
	    
	    if (!alinhamentos.isEmpty()){
	    	//checkCycles(alinhamentos);
	    	Object [] ontologyObjects = new Object[2];
	    	if (format.equals("txt")){
	    		createOntologyFromAlignmentsTxt(alinhamentos, dirTrab);
	    	} else {
	    		ontologyObjects = createOntologyFromAlignments(alinhamentos);
	    		saveOntology(ontologyObjects,dirTrab);
	    	}
	    }else{
	    	System.out.println("No alignment found to create owl file");
	    }
	    if (!alinhamentos.isEmpty()){
	    	//checkCycles(alinhamentos);
	    	Object [] ontologyObjects = new Object[2];
	    	createRDFFromAlignments(alinhamentos, dirTrab, false);
	    	
	    }else{
	    	System.out.println("No alingnment found to create RDF file");
	    }
	    if (!alinhamentosDuplicate.isEmpty()){
	    	//checkCycles(alinhamentos);
	    	Object [] ontologyObjects = new Object[2];
	    	createRDFFromAlignments(alinhamentosDuplicate, dirTrab, true);
	    	
	    }else{
	    	System.out.println("No alignment duplicate found");
	    }
	    
	}

	private void createRDFFromAlignments(List<Alignment> alinhamentos2, String dirTrab, boolean isDuplicate) {
		// TODO Auto-generated method stub
		File saidaRDF; 
        for (Alignment alin : alinhamentos2) {
        	String name1Ont = alin.getOntologia1().getName();
            name1Ont = name1Ont.replaceAll("http://", "");
            name1Ont = name1Ont.replaceAll("\"", "");

        	String name2Ont = alin.getOntologia2().getName();
            name2Ont = name2Ont.replaceAll("http://", "");
            name2Ont = name2Ont.replaceAll("\"", "");
  		
			if (!isDuplicate)
				saidaRDF = new File(dirTrab+"/ALIN-"+name1Ont+"-"+name2Ont+"OUT.rdf.txt");
			else
				saidaRDF = new File(dirTrab+"/ALIN-"+name1Ont+"-"+name2Ont+"DUPLICATE.rdf.txt");
			BufferedWriter rdf;
			try {
				rdf = new BufferedWriter(new FileWriter(saidaRDF));		

				rdf.write("<?xml version='1.0' encoding='utf-8' standalone='no'?>\n");
				rdf.write("<rdf:RDF xmlns='http://knowledgeweb.semanticweb.org/heterogeneity/alignment#'\n");
				rdf.write("         xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'\n");
				rdf.write("         xmlns:xsd='http://www.w3.org/2001/XMLSchema#'>\n");
				rdf.write("<Alignment>\n");
				rdf.write("  <xml>yes</xml>\n");
				rdf.write("  <level>0</level>\n");
				rdf.write("  <type>?*</type>\n");
				rdf.write("  <onto1>\n");
				rdf.write("    <Ontology rdf:about=\""+alin.getOntologia1().getName()+"\">\n");
				rdf.write("      <location>"+alin.getOntologia1().getURI()+"</location>\n");
				rdf.write("    </Ontology>\n");
				rdf.write("  </onto1>\n");
				rdf.write("  <onto2>\n");
				rdf.write("    <Ontology rdf:about=\""+alin.getOntologia2().getName()+"\">\n");
				rdf.write("      <location>"+alin.getOntologia2().getURI()+"</location>\n");
				rdf.write("    </Ontology>\n");
				rdf.write("  </onto2>\n");
				
			
				List<Map> maps = alin.getMappings();
				for (Map map : maps) {
					rdf.write("  <map>\n");
					rdf.write("    <Cell>\n");
					rdf.write("      <entity1 rdf:resource='"+map.getEntity1()+"'/>\n");
					rdf.write("      <entity2 rdf:resource='"+map.getEntity2()+"'/>\n");
					rdf.write("      <relation>"+map.getRelation()+"</relation>\n");
					rdf.write("      <measure rdf:datatype='http://www.w3.org/2001/XMLSchema#float'>"+map.getMeasure()+"</measure>\n");
					rdf.write("    </Cell>\n");
					rdf.write("  </map>\n");
					//System.out.println ("map");
					//System.out.println (map.getEntity1());
					//System.out.println (map.getEntity2());
				}
				rdf.write("</Alignment>\n");
				rdf.write("</rdf:RDF>\n");
				rdf.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	private void createOntologyFromAlignmentsTxt(List<Alignment> alinhamentos2, String dirTrab) {
		// TODO Auto-generated method stub
		File file = new File(dirTrab+"/AlignmentMerge-"+System.currentTimeMillis()+".owl");
		IRI nameIRI =  IRI.create(file.toURI());
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    OutputStreamWriter osr = new OutputStreamWriter(os);
	    BufferedWriter bw = new BufferedWriter(osr);
	    String str = "<?xml version=\"1.0\"?>";
	    str = str + "<rdf:RDF\n";
    	str = str +	    "xmlns:xsp=\"http://www.owl-ontologies.com/2005/08/07/xsp.owl#\"\n";
		str = str +		    "xmlns:swrlb=\"http://www.w3.org/2003/11/swrlb#\"\n";
		str = str +		    "xmlns:swrl=\"http://www.w3.org/2003/11/swrl#\"\n";
		str = str +		    "xmlns:protege=\"http://protege.stanford.edu/plugins/owl/protege#\"\n";
		str = str +		    "xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n";
		str = str +		    "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n";
		str = str +		    "xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n";
		str = str +		    "xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n";
		str = str +		    "xmlns=\"http://cmt#\"\n";
		str = str +		  "xml:base=\"http://cmt\">\n";
		str = str +		  "<owl:Ontology rdf:about=\""+nameIRI+"\"/>\n";

	    try {
			bw.write(str);
		    
		    for (Alignment alin: alinhamentos2){
				for (Map mapeamento: alin.getMappings()){
					double measureDouble = Double.parseDouble(mapeamento.getMeasure());
				    //String classe1 = nameIRI.toString()+"#";
				   // String classe2 = nameIRI.toString()+"#";
				    String classe1 = "";
				    String classe2 = "";
				    
					if (mapeamento.getRelation().equals("=")&&measureDouble>0.8){
						
						classe1 += mapeamento.getEntity1();
						classe2 += mapeamento.getEntity2();
						if(!classe1.equals(classe2)) { 
							str = "<owl:Class rdf:about=\"" + classe1 + "\">";
							bw.write(str+"\n");
							for (Map maping: alin.getMappings()) {
								//String classe1Disjoint = nameIRI.toString()+"#";
							    //String classe2Disjoint = nameIRI.toString()+"#";
							    String classe1Disjoint = "";
							    String classe2Disjoint = "";
							    
							    classe1Disjoint += maping.getEntity1();
								classe2Disjoint += maping.getEntity2();
								
								if (!classe1.equals(classe1Disjoint)){
									str = "<owl:disjointWith rdf:resource=\"" + classe1Disjoint + "\"/>";
									bw.write(str+"\n");
								}
								if (!classe2.equals(classe2Disjoint)){
									str = "<owl:disjointWith rdf:resource=\"" + classe2Disjoint + "\"/>";
									bw.write(str+"\n");
								}
							}
							str = "</owl:Class>";
							bw.write(str+"\n");
							str = "<owl:Class rdf:about=\"" + classe2 + "\">";
							bw.write(str+"\n");
							for (Map maping: alin.getMappings()) {
								//String classe1Disjoint = nameIRI.toString()+"#";
							    //String classe2Disjoint = nameIRI.toString()+"#";
							    String classe1Disjoint = "";
							    String classe2Disjoint = "";
							    
							    classe1Disjoint += maping.getEntity1();
								classe2Disjoint += maping.getEntity2();
								
								if (!classe1.equals(classe1Disjoint)){
									str = "<owl:disjointWith rdf:resource=\"" + classe1Disjoint + "\"/>";
									bw.write(str+"\n");
								}
								if (!classe2.equals(classe2Disjoint)){
									str = "<owl:disjointWith rdf:resource=\"" + classe2Disjoint + "\"/>";
									bw.write(str+"\n");
								}
							}
							str = "</owl:Class>";
							bw.write(str+"\n");
						} else {
							str = "<owl:Class rdf:about=\"" + classe1 + "\">";
							bw.write(str+"\n");
							for (Map maping: alin.getMappings()) {
								//String classe1Disjoint = nameIRI.toString()+"#";
								String classe1Disjoint = "";
								
							    classe1Disjoint += maping.getEntity1();
								
								if (!classe1.equals(classe1Disjoint)){
									str = "<owl:disjointWith rdf:resource=\"" + classe1Disjoint + "\"/>";
									bw.write(str+"\n");
								}
							}
							str = "</owl:Class>";
							bw.write(str+"\n");
						}
						
						System.out.println(classe1+"-"+classe2);
					}
					
						
				}
			}
			/*for (Alignment alin: alinhamentos2){
				for (Map mapeamento: alin.getMappings()){
					double measureDouble = Double.parseDouble(mapeamento.getMeasure());
				    String classe1 = nameIRI.toString()+"#";
				    String classe2 = nameIRI.toString()+"#";
				    
					if (mapeamento.getRelation().equals("=")&&measureDouble>0.8){
						
						classe1 += mapeamento.getEntity1();
						classe2 += mapeamento.getEntity2();
						if (classe1.equals(classe2)){
							str = "<owl:Class rdf:about=\"" + classe1 + "\">";
							bw.write(str+"\n");
							if (!classe1Old.equals("Thing")){
								str = "<owl:disjointWith rdf:resource=\"" + classe1Old + "\">";
								bw.write(str+"\n");
							}
							str = "</owl:Class>";
							bw.write(str+"\n");
						} else { // may be if class 1 and class 2 are different each other they may be disjoint class 1 with 1 and 2 old and class 2 with 1 and 2 old so we may generate 4 disjointness. Lets prove. 
							str = "<owl:Class rdf:about=\"" + classe1 + "\">";
							bw.write(str+"\n");
							if (!classe1Old.equals("Thing")){
								str = "<owl:disjointWith rdf:resource=\"" + classe1Old + "\">";
								bw.write(str+"\n");
							}
							str = "</owl:Class>";
							bw.write(str+"\n");
							str = "<owl:Class rdf:about=\"" + classe2 + "\">";
							bw.write(str+"\n");
							if (!classe1Old.equals("Thing")){
								str = "<owl:disjointWith rdf:resource=\"" + classe2Old + "\">";
								bw.write(str+"\n");
							}
							str = "</owl:Class>";
							bw.write(str+"\n");
						}
						classe1Old = classe1;
						classe2Old = classe2;
						System.out.println(classe1+"-"+classe2);
					}
				}*/
			
			
			str = "</rdf:RDF>";
			bw.write(str);
			bw.flush();
			bw.close();
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void saveOntology(Object[] oo, String dirTrab) {
		// TODO Auto-generated method stub
		File file = new File(dirTrab+"/AlignmentMerge-"+System.currentTimeMillis()+".owl");
		IRI nameIRI =  IRI.create(file.toURI());
		OWLOntologyManager manager = (OWLOntologyManager) oo[0];
		OWLOntology o = (OWLOntology) oo[1];
		try {
			manager.saveOntology(o, nameIRI);
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Object[] createOntologyFromAlignments(List<Alignment> alinhamentos2) {
		// TODO Auto-generated method stub
		IRI IOR = IRI.create("http://www.semanticweb.org/ontologies/"+System.currentTimeMillis());
		OWLOntologyManager man = OWLManager.createOWLOntologyManager(); 
		OWLOntology o = null;
		try {
			o = man.createOntology();
			System.out.println(o);
		
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
		OWLDataFactory df = o.getOWLOntologyManager().getOWLDataFactory();
		OWLClass classe1 = null;
		OWLClass classe2 = null;
		OWLDeclarationAxiom da = null;
		for (Alignment alin: alinhamentos2){
			for (Map mapeamento: alin.getMappings()){
				double measureDouble = Double.parseDouble(mapeamento.getMeasure());
			    
				if (mapeamento.getRelation().equals("=")&&measureDouble>0.8){
					// using the IOR before the class. May be the name og class is already with the IOR
					//classe = df.getOWLClass(IOR+mapeamento.getEntity1()); // making only one axiom from each map. May be wrong
					/* 
					if (classe1.equals(classe2)){
						classe1 = df.getOWLClass(mapeamento.getEntity1());
						da = df.getOWLDeclarationAxiom(classe1);
						o.add(da);
					} else {
						classe1 = df.getOWLClass(mapeamento.getEntity1());
						da = df.getOWLDeclarationAxiom(classe1);
						o.add(da);
						classe2 = df.getOWLClass(mapeamento.getEntity2());
						da = df.getOWLDeclarationAxiom(classe2);
						o.add(da);
					}
					*/
					System.out.println(o);
				}
			}
		
		}
		// array to return more than 1 object
		Object [] oo = new Object[2];
		oo[0] = man;
		oo[1] = o;
		return oo;
		
	}

	private void checkCycles(List<Alignment> alinhamentos2) {
		// TODO Auto-generated method stub
		// starting from the first element in alignments 
		/*
	    if(nOO.checkCycles(alinhamentos.get(0).getOntologia1())) {
	    	System.out.println("Tem ciclo");
	    } else {
	    	System.out.println("N�o tem ciclo");
	    }
	    */
	}

	private void createNetwork(List<Alignment> alinhamentos2) {
		// TODO Auto-generated method stub
		//javax.ide.util.Graph<Ontology> alignmentsGraph = new javax.ide.util.Graph<Ontology>();
		
		//Graph <Ontology,Alignment>ontoGraph = new DirectedPseudograph <Ontology,Alignment>(Alignment.class);
		
		for (Alignment align : alinhamentos2) {
			Ontology o1=align.getOntologia1();
			Ontology o2=align.getOntologia2();
			
			if(nOO.createOntology(o1))
				System.out.println("Created vertex - "+ o1.getName());
			if(nOO.createOntology(o2))
				System.out.println("Created vertex - "+ o2.getName());
			if(nOO.createAlignment(align))
				System.out.println("Created edge from - "+ o1.getName()+" to -"+o2.getName());
		}
	}

	private void processAlignments(List<Alignment> alinhamentos) {
		// TODO Auto-generated method stub
		// Alignments for removal
		List<Alignment> aAux = new ArrayList<Alignment>();
		// check for equal alignments
		for (int i = 0; i < alinhamentos.size()-1; i++) {
			for (int j = 1; j < alinhamentos.size(); j++) {
				 
				System.out.println("Alinhamento[i]: "+ alinhamentos.get(i).getOntologia1().getName()+" - "+alinhamentos.get(i).getOntologia2().getName());
				System.out.println("Alinhamento[j]: "+ alinhamentos.get(j).getOntologia1().getName()+" - "+alinhamentos.get(j).getOntologia2().getName());
				if (alinhamentos.get(i).equals(alinhamentos.get(j))) {
					System.out.println("We can remove ->  "+ alinhamentos.get(i).getOntologia1().getName()+ " - "+ alinhamentos.get(i).getOntologia2().getName());
					aAux.add(alinhamentos.get(i));
				}
			}
		}
		// remove duplicate alignments 
		for (Alignment alinhamento : aAux) {
			if (alinhamentos.remove(alinhamento)) {
				System.out.println(" Removi - " + alinhamento.getOntologia1().getName() + " - " + alinhamento.getOntologia2().getName());
			}
				
		}
	}

	private Alignment processFile(File selectedFile) {
		// TODO Auto-generated method stub
		InputStream is = null;
		try {
			is = new FileInputStream(selectedFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    InputStreamReader isr = new InputStreamReader(is);
	    BufferedReader br = new BufferedReader(isr);

	    System.out.println("Arquivo da vez - "+selectedFile.getName());
	    
		if (selectedFile.getName().endsWith("rdf")){
		    String s, token = null;
		    StringTokenizer stk = null;
		    int pos = 0;
			try {
				//Stream st = br.lines(); 
				//st.
				System.out.println("leitura do rdf"+selectedFile.getName());
				s = br.readLine();// primeira linha do arquivo
				System.out.println(s);
				/*while (s != null) {
					System.out.println(s);
					s = br.readLine();
				}*/
				//while (s != null) {
				    
				while (s!=null&&s.indexOf("rdf:about=")==-1) {
					s = br.readLine(); // ler ateh achar <rdf:about=>
					
				}
				if (s==null) {
					throw new EOFRDFExcetion("RDF incompleto!");
				}
				// process alignment
				
				Alignment a = new Alignment();
				
				pos = s.indexOf("rdf:about=");
				token = s.substring(pos+10,s.indexOf(">"));
				Ontology o1 = new Ontology();
				String nomeOnto1 = token;
			    o1.setName(nomeOnto1);
			    
			    s = br.readLine();
			    System.out.println(s);
				while (s.indexOf("rdf:about=")==-1&&s!=null) {
					s = br.readLine(); // ler ateh achar <rdf:about=>
				
				}
				if (s==null) {
					throw new EOFRDFExcetion("RDF incompleto!");
				}
				pos = s.indexOf("rdf:about=");
				token = s.substring(pos+10,s.indexOf(">")); // 10 � o tam da String rdf:about=
				Ontology o2 = new Ontology();
				String nomeOnto2 = token;
			    o2.setName(nomeOnto2);
			    
			    // preenche as ontologias do alinhamento
			    a.setOntologia1(o1);
			    a.setOntologia2(o2);
			    
			    // processa os maps
			    s = br.readLine();
			    System.out.println(s);
			    while (s != null&&(s.indexOf("/Alignment")==-1) ) {
					while (s!=null&&s.indexOf("<map>")==-1) {
						
						s = br.readLine(); // ler ateh achar <map>
						System.out.println(s);
					}
					if (s==null) {
						throw new EOFRDFExcetion("RDF incompleto!");
					}
					String entity1 = null, entity2 = null, relation = null, measure =null;
					Map m = new Map();

					while (s!=null&&s.indexOf("</map>")==-1){
						// process Map
						s = br.readLine(); // li o <cell>
						System.out.println(s);
						//s = br.readLine(); // li linha do entity1
						//System.out.println(s);
						if (!(s.indexOf("rdf:resource='")==-1)&&!(s.indexOf("entity1")==-1)) {
							//pos = s.indexOf("rdf:resource='"); // with URI
							pos = s.indexOf("#");            // w\ URI
							//token = s.substring(pos+14,s.indexOf("'/")); // 14 eh o tam da String rdf:resource ' with URI
							token = s.substring(pos+1,s.indexOf("'/")); // 1 eh o tam da String #  w\ URI
							entity1 = token;
							m.setEntity1(entity1);
							
						}
	
						//s = br.readLine(); // li linha do entity2
						//System.out.println(s);
						if (!(s.indexOf("rdf:resource='")==-1)&&!(s.indexOf("entity2")==-1)) {
							//pos = s.indexOf("rdf:resource='");
							pos = s.indexOf("#");
							//token = s.substring(pos+14,s.indexOf("'/"));
							token = s.substring(pos+1,s.indexOf("'/")); // 1 eh o tam da String #
							
							entity2 = token;
							m.setEntity2(entity2);
							
						}
						//s = br.readLine(); // li linha do relation
						//System.out.println(s);
						if (!(s.indexOf("<relation>")==-1)) {
							pos = s.indexOf("<relation>");
							token = s.substring(pos+10,s.indexOf("</",pos)); // 10 � o tam da String <relation>
							relation = token;
							m.setRelation(relation);

						}
						//s = br.readLine(); // li linha do measure
						//System.out.println(s);
						if (!(s.indexOf("rdf:datatype=")==-1)) {	
							pos = s.indexOf("rdf:datatype=");
							pos = s.indexOf(">",pos);
							token = s.substring(pos+1,s.indexOf("</")); // 1 � o tam da String >
							//int tam = s.substring(pos+13,s.indexOf(">")).length();
							//token = s.substring(pos+tam,s.indexOf("</"));
							
							measure = token;
							m.setMeasure(measure);
							
						}
						if (entity1!=null&&entity2!=null&&relation!=null&&measure!=null) {
							// Preenchido os dados do map basta:						
							// adicionar map ao alinhamento
							a.addMap(m);
							entity1 = null; entity2 = null; relation = null; measure =null;
						}
					}
					if (s==null) {
						throw new EOFRDFExcetion("RDF incompleto!");
					}

					// leitura de final de loop
					s = br.readLine();
					System.out.println(s);
					
				}
			    
			    System.out.println("Fim do rdf"+selectedFile.getName());
				return a;

					
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchElementException en) {
				en.printStackTrace();
			} catch (EOFRDFExcetion e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println(e.getErrorMsg()+ "-> RDF bichada!");
				System.exit(1);
			}
		}
		return null;
	}

	

}
