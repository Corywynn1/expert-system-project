// Cory Wynn 13155679
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JLabel;


public class SmartDecision { 
	private JList QuestionList;
	private DefaultListModel QuestionlistModel;
	private JScrollPane scrollPane;

	private JList EvaluatedList;
	private DefaultListModel EvaluatedlistModel;
	private JScrollPane scrollPaneEval;

	private JComboBox myInput;
	private boolean fillingCombo = false;

	private JTextField tskValue;
	JLabel lblQuestion = new JLabel("Question",JLabel.CENTER);
	JLabel lblQuestHead = new JLabel("All Possible Conditions.",JLabel.CENTER);

	private String ConditionTree[][];
	private String QuestionsTree[][];
	private String distinctRules[][];    
	
	private int currentRuleIndex;
	private String currentRule;
	private String currentQuestion;
	
	public void start() { 
	
	//creating main window
	JFrame f = new JFrame("Expert System"); 
	JPanel p = new JPanel();
	p.setLayout(null);	

	
	//creating buttons
	JButton btnLoad = new JButton(); 
	JButton btnEvaluate = new JButton(); 
	JButton btnEnter = new JButton(); 

	JButton btnYes = new JButton(); 
	JButton btnNo = new JButton(); 
	JButton btnLess = new JButton(); 
	JButton btnZero = new JButton(); 
	JButton btnGtr = new JButton(); 
	
	//creating combo box
	myInput = new JComboBox();
	
	
	//Setting button Captions
	btnLoad.setText("LOAD"); 
	btnEvaluate.setText("CONSULT"); 
	btnEnter.setText("ENTER");
	
	btnYes.setText("YES");
	btnNo.setText("NO");
	btnLess.setText("< 0");
	btnZero.setText("0");
	btnGtr.setText("> 0");
	
	
	//Initializing fields
	tskValue = new JTextField();
	
	//creating lists
	QuestionList = new JList();
	scrollPane = new JScrollPane(QuestionList);
	
	EvaluatedList = new JList();
	scrollPaneEval = new JScrollPane(EvaluatedList);
	

	//setting lists positions
	scrollPane.setBounds(115, 25,300, 404);
	scrollPaneEval.setBounds(500,130,300,300);
	
	//setting button positions
	btnLoad.setBounds(5, 30,100, 25);
	btnEvaluate.setBounds(5, 80,100, 25);
	btnEnter.setBounds(720, 55,80, 25);

	btnYes.setBounds(500, 85,60, 25);
	btnNo.setBounds(564, 85,60, 25);
	btnLess.setBounds(632, 85,52, 25);
	btnZero.setBounds(690, 85,52, 25);
	btnGtr.setBounds(748, 85,52, 25);
	
	//setting combo box position
	myInput.setBounds(620, 55,90, 25);
	
	//setting fields positions
	tskValue.setBounds(500,55,110, 25);

	//setting label positions
	lblQuestion.setBounds(500,2,300, 50);
	lblQuestHead.setBounds(115, 5, 300, 20);
	
	p.add(btnLoad);
	p.add(btnEvaluate);
	p.add(btnEnter);
	p.add(btnYes);
	p.add(btnNo);
	p.add(btnLess);
	p.add(btnZero);
	p.add(btnGtr);
	
	p.add(myInput);
	p.add(tskValue);
	p.add(scrollPane);
	p.add(scrollPaneEval);
	p.add(lblQuestion);
	p.add(lblQuestHead);

	f.getContentPane().add(p);

	
		
	//setting window properties
	f.setSize(850,500); 
	f.setVisible(true); 
	//f.setLayout(new FlowLayout());
	
	//binding actions
	//Evaluate
	btnEvaluate.addActionListener( new ActionListener(){
	public void actionPerformed( ActionEvent e){
		EvaluatedlistModel = new DefaultListModel();
		
		setCurrentRule();
		setCurrentQuestion();
		fillInputCombo();
		lblQuestion.setText(currentQuestion);
		EvaluatedlistModel.addElement(currentQuestion);

		EvaluatedList.setModel(EvaluatedlistModel);
	} 
	});

	//Loading
	btnLoad.addActionListener( new ActionListener(){
	public void actionPerformed( ActionEvent e){
		//read files and loads condition and questions array
		readFile();
		//displaying all possible conditions lists.
		displayConditions();
		//loading unique rules along with there all possible inputs in an array (only for string rules)
		fillDistinctRules();
	} 
	});
	//onEnter action
	btnEnter.addActionListener( new ActionListener(){
	public void actionPerformed( ActionEvent e){
		evaluate(tskValue.getText());
	} 
	});
	
	//onYes action
	btnYes.addActionListener( new ActionListener(){
	public void actionPerformed( ActionEvent e){
		evaluate("Yes");
	} 
	});
	
	//onNo action
	btnNo.addActionListener( new ActionListener(){
	public void actionPerformed( ActionEvent e){
		evaluate("No");
	} 
	});
	
	//on >0 action
	btnGtr.addActionListener( new ActionListener(){
	public void actionPerformed( ActionEvent e){
		evaluate("1");
	} 
	});

	//onZero action
	btnZero.addActionListener( new ActionListener(){
	public void actionPerformed( ActionEvent e){
		evaluate("0");
	} 
	});
	//on <0 action
	btnLess.addActionListener( new ActionListener(){
	public void actionPerformed( ActionEvent e){
		evaluate("-1");
	} 
	});
	//onCombo change action
	myInput.addActionListener( new ActionListener(){
	public void actionPerformed( ActionEvent e){
	try{
		if(!fillingCombo){
			evaluate(myInput.getSelectedItem().toString());
		}
	}catch(Exception ex){
		ex.printStackTrace();
	}
	} 
	});
	}
	
	
	
	//method for displaying all possible conditions
	private void displayConditions(){

		String[] possibleConditions = new String[countPossibleRoutes()];
		QuestionlistModel = new DefaultListModel();
		
		String currentRule = "";
		int currentRuleIndex;
		int possCondInd = 0;
		
		for(int i=0;i<ConditionTree.length;i++){
			currentRule = ConditionTree[i][0];
			currentRuleIndex = i;
			 
			// if current Rule is final rule
			if(ConditionTree[i][5].equals("Y")){
				
				possibleConditions[possCondInd] = String.valueOf(currentRuleIndex) + ",";
				//loop until not founds the top rule
				//System.out.println("value of i: " + possCondInd + "  " + currentRule + " index is:" + currentRuleIndex);
				while(!ConditionTree[currentRuleIndex][1].equals("-")){
					//setting current rule to current parent rule
					currentRuleIndex = getRuleIndex(ConditionTree[currentRuleIndex][1]);
					currentRule = ConditionTree[currentRuleIndex][0];
					possibleConditions[possCondInd] += String.valueOf(currentRuleIndex) + ",";
					//System.out.println("value of i: " + possCondInd + "  " + currentRule + " index is:" + currentRuleIndex);
					
				}
				possibleConditions[possCondInd] = possibleConditions[possCondInd].substring(0, possibleConditions[possCondInd].length()-1);
				//System.out.println(possibleConditions[possCondInd]);
				possCondInd++;
			}
		}
		
		
		//Displaying in human language
		for(int k=0;k<possibleConditions.length;k++){
			String currRoute[] =  possibleConditions[k].split(",");

			int curRutInd;
			int nxtRutInd;
			String str = "if ";
			for(int i=currRoute.length-1;i>0;i--){
				curRutInd = Integer.parseInt(currRoute[i-1]);
				nxtRutInd = Integer.parseInt(currRoute[i]);
				
				for(int a=0;a<ConditionTree.length;a++){

					if(ConditionTree[a][0].equals(ConditionTree[curRutInd][1]) && 
							ConditionTree[a][1].equals(ConditionTree[nxtRutInd][1])){
						//System.out.println(ConditionTree[curRutInd][1] + " is " + ConditionTree[curRutInd][2] + " " + ConditionTree[curRutInd][3]);
						QuestionlistModel.addElement(ConditionTree[curRutInd][1] + " is " + ConditionTree[curRutInd][2] + " " + ConditionTree[curRutInd][3]);
						if(ConditionTree[curRutInd][5].equals("Y")){
							//System.out.println(ConditionTree[curRutInd][0]);
							QuestionlistModel.addElement(ConditionTree[curRutInd][0]);
						}
					}

				}
				
			}
			QuestionlistModel.addElement("\n");
		}
		QuestionList.setModel(QuestionlistModel);
	}
	
	//method for displaying valid inputs according to table
	private void fillInputCombo(){
//		myInput.actionPerformed(null);
//		myInput.setAction(null);
		fillingCombo = true;
		myInput.removeAllItems();
		for(int i=0;i<distinctRules.length;i++){
			if(distinctRules[i][0].equals(currentRule)){
				String allValue[] = distinctRules[i][1].split(",");
				for(int j=0;j<allValue.length;j++){
//					myInput.actionPerformed(null);
					myInput.addItem(allValue[j].toString());
				}
			}
		}
		fillingCombo = false;
	}
	
//method for evaluating value
	private void evaluate(String inVal){
		String treeCond="";
		String treeVal="";
		int treeIntVal =0;
		double treeDblVal = 0.000;
		boolean condMatched = false;
		//evaluating condition
		for(int i=0;i<ConditionTree.length;i++){
			if(ConditionTree[i][4].equals("Number")){
				try{
					treeCond = ConditionTree[i][2];
					if(ConditionTree[i][1].equals(currentRule)){
						treeDblVal = Double.parseDouble(ConditionTree[i][3]);
						double inDblVal = Double.parseDouble(inVal);
						if(treeCond.equals(">")){
							if(inDblVal > treeDblVal){
								currentRule = ConditionTree[i][0];
								condMatched = true;
								break;
							}
						}
						else if(treeCond.equals("<")){
							if(inDblVal < treeDblVal){
								currentRule = ConditionTree[i][0];
								condMatched = true;
								break;
							}
						}
						else if(treeCond.equals(">=")){
							if(inDblVal >= treeDblVal){
								currentRule = ConditionTree[i][0];
								condMatched = true;
								break;
							}
						}
						else if(treeCond.equals("<=")){
							if(inDblVal <= treeDblVal){
								currentRule = ConditionTree[i][0];
								condMatched = true;
								break;
							}
						}
						else if(treeCond.equals("<>")){
							if(inDblVal != treeDblVal){
								currentRule = ConditionTree[i][0];
								condMatched = true;
								break;
							}
						}
						else if(treeCond.equals("=")){
							if(inDblVal == treeDblVal){
								currentRule = ConditionTree[i][0];
								condMatched = true;
								break;
							}
						}
					
					}
				}catch(java.lang.NumberFormatException expNumFor){
					condMatched = false;
					EvaluatedlistModel.addElement("Please enter numbers only.");
					break;
				}
			}else if(ConditionTree[i][4].equals("String")){
				if(ConditionTree[i][1].equals(currentRule)){
					treeVal = ConditionTree[i][3];
					treeCond = ConditionTree[i][2];
					if(treeCond.equals("=")){
						if(inVal.equalsIgnoreCase(treeVal)){
							currentRule = ConditionTree[i][0];
							condMatched = true;
							break;
						}
					}
					else if(treeCond.equals("<>")){
						if(!inVal.equalsIgnoreCase(treeVal)){
							currentRule = ConditionTree[i][0];
							condMatched = true;
							break;
						}
					}
				}
			}
		}
		
		//checking if any condition matched else declare it a final rule.
		if(condMatched){
			if(chkCurRulIsLast()){
				lblQuestion.setText("Final Rule!");
				
				//printing to out list
				EvaluatedlistModel.addElement(inVal);
				EvaluatedlistModel.addElement(currentRule);
				EvaluatedlistModel.addElement("Final Rule!");

			}else{
				fillInputCombo();
				setCurrentQuestion();
				lblQuestion.setText(currentQuestion);

				//printing to outlist
				EvaluatedlistModel.addElement(inVal);
				EvaluatedlistModel.addElement(currentQuestion);
			}
		}else{
			myInput.removeAllItems();
			lblQuestion.setText("NO CONDITION");
		}
		tskValue.setText("");
	}
	//method for getting rule index
	private int getRuleIndex(String rule){
		for(int i=0; i<ConditionTree.length;i++){
			if(rule.equals(ConditionTree[i][0])){
				return i;
			}
		}
		return -1;
	}
	
	//method for printing output to Evaluated List
	private void printEvaList(){
		EvaluatedlistModel = new DefaultListModel();
		EvaluatedList.setModel(EvaluatedlistModel);
		
	}
	//method for getting current Rule isFinal status

private boolean chkCurRulIsLast(){
	for(int i=0; i<ConditionTree.length;i++){
		if(currentRule.equals(ConditionTree[i][0]) && ConditionTree[i][5].equals("Y")){
			return true;
		}
	}
	return false;	
}
	//method for setting current Rule for first time 
private void setCurrentRule(){
	for(int i=0; i<ConditionTree.length;i++){
		if(ConditionTree[i][1].equals("-")){
			currentRule = ConditionTree[i][0];
			currentRuleIndex = i;
		}
	}
}
	//method for setting current Question
private void setCurrentQuestion(){
	for(int i=0; i<QuestionsTree.length;i++){
		if(QuestionsTree[i][0].equals(currentRule)){
			currentQuestion = QuestionsTree[i][1];
		}
	}
}


	//method for reading file
private void readFile(){
		QuestionlistModel = new DefaultListModel();
		JFileChooser fc=new JFileChooser();
		fc.setDialogTitle("Choose file");
		
		int returnVal=fc.showOpenDialog(new JFrame());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
		    File file = fc.getSelectedFile();		
			try {
		
    	BufferedReader br = new BufferedReader(new FileReader(file));
        String strLine;
        //Read File Line By Line
        while ((strLine = br.readLine()) != null)   {
        	//Skipping blank lines
        	if(strLine.length()>0){
        		//Skipping buffered lines
	        	if(strLine.charAt(0) != '#'){
	                if(strLine.equals("CTS")){
	                	int i = 0;
	                	int j = (int) this.countLines("CTS", "CTE",file);
	                	ConditionTree  = new String[j][6];
	                	//filling conditions tree
	                	while(!(strLine = br.readLine()).equals("CTE")){
	                		
	                		try{
		                		String Cond[] = strLine.split(",");
		                		ConditionTree[i][0] = Cond[0].trim(); 
		                		ConditionTree[i][1] = Cond[1].trim(); 
		                		ConditionTree[i][2] = Cond[2].trim(); 
		                		ConditionTree[i][3] = Cond[3].trim(); 
		                		ConditionTree[i][4] = Cond[4].trim(); 
		                		ConditionTree[i][5] = Cond[5].trim(); 

		                		i++;
	                		}catch(NullPointerException ex){
	                	    	System.out.println("Error at line: "+ i);//donothing
	                	    } 
	                	}
	                }else if(strLine.equals("QTS")){
	                	int i = 0;
	                	int j = (int) this.countLines("QTS", "QTE",file);
	                	QuestionsTree = new String[j][2];
	                	//filling Questions tree 
	                	while(!(strLine = br.readLine()).equals("QTE")){
	                		
	                		try{
		                		String Qes[] = strLine.split(",");
		                		QuestionsTree[i][0] = Qes[0].trim(); 
		                		QuestionsTree[i][1] = Qes[1].trim(); 

		                		i++;
	                		}catch(NullPointerException ex){
	                	    	//donothing
	                	    } 
	                	}
	                	
	                	
	                }
	                
	        	}
        	}
        }
		QuestionList.setModel(QuestionlistModel);
    }catch(java.lang.StringIndexOutOfBoundsException ex){
    	System.out.println("String index out of bond.");
    }
	catch (Exception e) {
		e.printStackTrace();
    }
	}
	
}



private int countLines(String str, String end,File file){
	int i = 0;
	try {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String strLine;
        //Read File Line By Line
        while ((strLine = br.readLine()) != null)   {
        	if(strLine.equals(str)){
        		while(!(strLine = br.readLine()).equals(end)){
        			i++;
	            }
        	}
        }
    }
	catch (Exception e) {
		e.printStackTrace();
    }
	return i;

	
}

//counting all possible Condition routes
private int countPossibleRoutes(){
	int totPossRoutes =0;
	for(int i=0;i<ConditionTree.length;i++){
		if(ConditionTree[i][5].equals("Y")){
			totPossRoutes++;
		}
	}
	
	return totPossRoutes;
}
//counts and fills distinct rules array
private void fillDistinctRules(){
	int totRules =0;
	for(int i=1;i<ConditionTree.length;i++){
		if((!ConditionTree[i][1].equals(ConditionTree[i-1][1])) && ConditionTree[i][4].equals("String")){
			totRules++;
		}
	}

	distinctRules = new String[totRules][2];

	//filling distinct rules array first column i-e rules names only
	int disRulIndex = 0;
	for(int i=1;i<ConditionTree.length;i++){
		if((!ConditionTree[i][1].equals(ConditionTree[i-1][1])) && ConditionTree[i][4].equals("String")){
			distinctRules[disRulIndex][0] = ConditionTree[i][1];
			disRulIndex++;
		}
	}
	
	
	
	for(int a=0;a<distinctRules.length;a++){
		distinctRules[a][1] = "";
		for(int i=0;i<ConditionTree.length;i++){
			if(distinctRules[a][0].equals(ConditionTree[i][1])){
				distinctRules[a][1] += ConditionTree[i][3] + ",";
			}
		}
		//removing the last comma
		distinctRules[a][1] = distinctRules[a][1].substring(0, distinctRules[a][1].length()-1);
//		System.out.println(distinctRules[a][0] + " : " + distinctRules[a][1]);
	}
}


public static void main(String args[]) { 
	new SmartDecision().start(); 
} 

}