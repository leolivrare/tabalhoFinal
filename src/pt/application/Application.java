package pt.application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFileChooser;

import pt.clubedohardware.Path.Path;
import pt.clubedohardware.characters.doctor.Doctor;
import pt.clubedohardware.characters.doctor.IDoctor;
import pt.clubedohardware.characters.patient.IPatient;
import pt.clubedohardware.characters.patient.Patient;
import pt.clubedohardware.dataorganizer.DataOrganizer;
import pt.clubedohardware.dataset.DataSetComponent;
import pt.clubedohardware.dialoguecreator.DialogueCreator;
import pt.clubedohardware.dialoguecreator.IDialogue;
import pt.clubedohardware.fileusage.FileUsage;
import pt.clubedohardware.fileusage.IFileUsage;
import pt.clubedohardware.node.Tree;
import pt.clubedohardware.userinterface.AnimationC;
import pt.clubedohardware.userinterface.IAnimationC;

public class Application {

	public static void main(String[] args) {
		boolean aux = true;
		Scanner sc = new Scanner(System.in);
		IAnimationC animation = new AnimationC();
		DataOrganizer dataOr = new DataOrganizer();
		DataSetComponent dataset = new DataSetComponent();
		IFileUsage fileUsage = new FileUsage();
		IDialogue dialogue = new DialogueCreator();
		Tree tree;
		String[] attributes;
		int[][] symptomFrequency;

		
		IDoctor doctor = new Doctor("Doctor Variolla");
		
		animation.setDocName("Doctor Variolla");
		animation.setWindowName("Consultorio");
		
		
		while (aux) {
			//Deixa o usuario escolher o caminho da tabela ou da serializacao da arvore
	        String path = Path.getPath(".csv", ".txt");
			
			//Se ele escolher a tabela o programa ira construir a arvore, se ele escolher a arvore o jogo ira carregar ela
			if (path.contains(".csv")) {
			    dataset.setDataSource(path);
			    String[][] instances = dataset.requestInstances();
			    attributes = dataset.requestAttributes();
			    
			    List<String> diseases = dataOr.diseaseFilter(instances);
			    symptomFrequency = dataOr.symptomFilter(instances, diseases);
			    tree = dataOr.treeMaker(diseases, symptomFrequency, instances);
			    
			    doctor.connect(dataset);
			    doctor.setTree(tree);
			} else {
				String pathCSV = fileUsage.getPathCSV();
				dataset.setDataSource(pathCSV);
			    symptomFrequency = fileUsage.getFrequency();
				
				tree = fileUsage.getTree();
				doctor.connect(dataset);
				doctor.setTree(tree);
			}
			
			System.out.print("Digite o nome do Paciente: ");
			String patientName = sc.nextLine();
			System.out.println();
			
			//Instancia o paciente
			IPatient patient = new Patient(patientName);
			patient.connect(dataset);
			animation.setPacientName(patientName);
			
			//Conecta o doutor com o paciente
			doctor.connect(patient);
			doctor.startInterview(dialogue);
			

			String[] falas = (String[]) dialogue.getFalas().toArray();
			String[] personagem = (String[]) dialogue.getPersonagem().toArray();
			
			animation.story(falas, personagem);
			
			while(true) {
				System.out.println("Deseja salvar os dados (S/N)?");
				Character c = sc.next().charAt(0);
				if (c.equals('S')) {
					fileUsage.save(tree.getDiseases(), symptomFrequency, tree, path);
					System.out.println("Dados salvos com sucesso!");
					break;
				}
				else if (c.equals('N')) {
					break;
				}
				else {
					System.out.println("Opcao invalida!");
				}
			}
			
			while(true) {
				System.out.println("Deseja analisar outro paciente (S/N)?");
				Character c = sc.next().charAt(0);
				if (c.equals('S')) {
					break;
				}
				else if (c.equals('N')) {
					aux = false;
					break;
				}
				else {
					System.out.println("Opcao invalida!");
				}
			}
		}

	}

}
