package compiler;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
public class compiler {
	public static void main(String[] args) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\�F��\\Desktop\\test.txt"));
		ArrayList<String> CFG = new ArrayList<String>();
		ArrayList<String> leftHand = new ArrayList<String>();
		ArrayList<String> rightHand = new ArrayList<String>();
		ArrayList<String> terMinal = new ArrayList<String>();
		String line = null;
		while((line = br.readLine()) != null){	//��CFG�Y�i��
			CFG.add(line);
		}
		
		for(int i=0; i<CFG.size(); i++){	//��LeftHandSide(Non-Terminal)  & rightHandSide(�]�t�e���Τ������Ů�) & terminal
			String[] temp = CFG.get(i).split(" ");
			String[] righttemp = CFG.get(i).split(">|\\|");
			rightHand.add(righttemp[1]);	//RightHandSide
		//	if(!temp[1].equals("|"))	//leftHandSide �S�� | ������
			leftHand.add(temp[1]);
			terMinal.addAll(findTerminal(temp));
		}
		terMinal.add(terMinal.get(0));	//��$����̫�@��
		terMinal.remove(0);
		
		String[][] table = new String[leftHand.size()+1][terMinal.size()+1];
		String[][] first = new String[2][terMinal.size()+1];
		String[][] duplicateOrNot= new String[2][leftHand.size()];
		
		for(int j=0; j<leftHand.size(); j++){	//�Φb�Dfollow set���a��,�P�_���L���ƥX�{follow set
			duplicateOrNot[0][j] = leftHand.get(j);
			duplicateOrNot[1][j] = "0";
		}
//		for(int i=0; i<2; i++){
//			for(int j=0; j<leftHand.size(); j++){
//				System.out.print(duplicateOrNot[i][j]);
//			}
//			System.out.println();
//		}
		for(int i=0; i<terMinal.size(); i++){	//��terminal��i first ����0��
			first[0][i] = terMinal.get(i);
		}
		for(int i=0; i<leftHand.size(); i++){	//�ئntable����0����0�C
			table[i+1][0] = leftHand.get(i);	//�]�t"|"
		}
		for(int i=0; i<terMinal.size(); i++){
			table[0][i+1] = terMinal.get(i);
		}
		for(int i=0; i<CFG.size(); i++){	//��first set
			for(int k=0; k<terMinal.size()+1; k++){	//�k0
				first[1][k] = "0";
			}
			for(int j=0; j<leftHand.size(); j++){	//�Φb�Dfollow set���a��,�P�_���L���ƥX�{follow set
				duplicateOrNot[1][j] = "0";
			}
			String[] righttemp = CFG.get(i).split(">|\\|");
			String[] temp,temp2;
			temp = First(righttemp[1],terMinal,rightHand,leftHand,first);	//�Dfirst set �åB�P�_�|���|deriveEmpty
			
//			for(int k=0; k<temp.length; k++){
//				System.out.print(temp[k]+ " ");
//			}
//			System.out.println();
//			System.out.println();
			
			if(temp[temp.length-1].equals("1")){					//�p�G�|DeriveEmpty�N�I�sFollow
				int check = i;
				while(leftHand.get(check).equals("|")){				//�ˬdlefthand��Non-terminal�O�ƻ�
					check--;
				}
				System.out.println("test10");
				temp2 = Follow(leftHand.get(check),terMinal,rightHand,leftHand,first,duplicateOrNot);	//�Ƕi�h���ѼƤw�g�Olefthand
//				for(int k=0; k<temp.length; k++){
//					System.out.print(temp2[k]+ " ");
//				}
//				System.out.println();
				for(int j=0; j<temp2.length-1; j++){					//��table
					if(!(temp2[j].equals("0"))){
						while(leftHand.get(check).equals("|")){		//�ˬdlefthand��Non-terminal�O����
							check--;
						}
						table[check+1][j+1] = Integer.toString(i+1);	//���C+1����]�O�]��table���Φ�,�h�F�@���@�C
					}	
				}
			}
			for(int j=0; j<temp.length-1; j++){					//��table
				if(!(temp[j].equals("0"))){
					int check = i;
					while(leftHand.get(check).equals("|")){		//�ˬdlefthand��Non-terminal�O����
						check--;
					}
					table[check+1][j+1] = Integer.toString(i+1);
				}	
			}
		}
		for(int i=0; i<leftHand.size()+1; i++){					//�L�Xtable
			for(int j=0; j<terMinal.size()+1; j++){
				if(!"|".equals(table[i][0]))
					System.out.printf("%4s\t",table[i][j]);
			}
			System.out.println();
		}
	}
	public static String[] First(String righthand, ArrayList<String> terminal, ArrayList<String> righthandrule, ArrayList lefthand,String[][] First){
		ArrayList<String> cal = new ArrayList<String>();
		int link,rule=1;
		String[] temp = new String[3];
		String[] rightWithOutBlank = righthand.split(" ",3);
		String Letter = rightWithOutBlank[1];		//���Ĥ@�Ӧr
		char alphabet = Letter.charAt(0);			//�Ĥ@�Ӧr���Ĥ@�Ӧr��
		if(alphabet >= 65 && alphabet <= 90){		//�Ĥ@�����P�_
			link = lefthand.indexOf(Letter);		//�Y�ONon-terminal �h��lefthandside�ݥ��b�ĴX��
			for(int i=link+1; lefthand.get(i).equals("|"); i++)		//��XNon-terminal ���X��rules
				rule++;
			
			for(int i=link; i<link+rule; i++){			//��righthand ����rule �æs�ical �̭�
				cal.add(righthandrule.get(i).concat(" ").concat(rightWithOutBlank[2]));  
			}
		
			for(int i=0; i<cal.size(); i++){		//��cal �̭����F�谵�ĤG���P�_
				temp = cal.get(i).split(" ",3);
				
				if(cal.get(i).charAt(1) < 65 || cal.get(i).charAt(1) > 90 && !(temp[1].equals("lamda"))){		//�D�j�g
					for(int j=0; j<terminal.size(); j++){
						if(temp[1].equals(First[0][j]))
							First[1][j] = "1";
					}
//					for(int k=0; k<terminal.size(); k++){
//						System.out.println(First[1][k]);
//					}
					
				}else if(temp[1].equals("lamda")){
					cal.set(i , cal.get(i).substring(6, cal.get(i).length()));
					First(cal.get(i),terminal,righthandrule,lefthand,First);
				}else{
					First(cal.get(i),terminal,righthandrule,lefthand,First);
				}
			}
		}else if(rightWithOutBlank[1].equals("lamda")){
			First[1][terminal.size()] = "1";
		}else{										//�Ĥ@���P�_�N���\
			for(int i=0; i<terminal.size(); i++){
				if(Letter.equals(First[0][i])){
					First[1][i] = "1";
				}
			}
		}
		return First[1];
	}
	public static boolean CheckDerive(String righthand, ArrayList<String> righthandrule, ArrayList<String> lefthand){
		boolean answer = false;
		int link,rule=1;
		char alphabet = righthand.charAt(0);		//righthand���Ĥ@�Ӧr��
		if(alphabet >= 65 && alphabet <= 90){		//�Ĥ@�����P�_ �O���O�j�g
			link = lefthand.indexOf(righthand);		//�Y�ONon-terminal �h��lefthandside�ݥ��b�ĴX��
			for(int i=link+1; lefthand.get(i).equals("|"); i++)		//��XNon-terminal ���X��rules
				rule++;
			for(int i=link; i<link+rule; i++){
				if(righthandrule.get(i).equals("lamda"))
					answer = true;
			}
		}
		return answer;
	}
	public static String[] Follow(String righthand, ArrayList<String> terminal, ArrayList<String> righthandrule, ArrayList<String> lefthand,String[][] follow, String[][] duplicate){
//		System.out.println("test8");
//		System.out.println(righthand);
		for(int i=0; i<lefthand.size(); i++){	//�⦳��follow set ��lefthand���O��
			if(duplicate[0][i].equals(righthand))
				duplicate[1][i] = "1";
		} 
//		for(int i=0; i<2; i++){
//			for(int j=0; j<lefthand.size();j++){
//				System.out.print(duplicate[i][j]);
//			}
//			System.out.println();
//		}
		String[][] allRightItem = new String[righthandrule.size()][50];
		for(int i=0; i<righthandrule.size(); i++){				//��righthand����r�����n�s��allRightItem�̭�
			String[] temp = righthandrule.get(i).split(" ");
			for(int j=0; j<temp.length; j++)
				allRightItem[i][j] = temp[j];
			for(int j=temp.length; j<50; j++)					//��L�S��righthand���a��s 0
				allRightItem[i][j] = "0";
		}
		System.out.println("test3");
		for(int i=0; i<righthandrule.size(); i++){
			for(int j=0; j<righthandrule.get(i).length(); j++){
				if(righthand.equals(allRightItem[i][j])){	//���nfollow��item
					System.out.println("test4");
					if(!(allRightItem[i][j+1].equals("0"))){	//righthand���᭱�٦��F��
						System.out.println("test5");
						String addBlank = "".concat(" ").concat(allRightItem[i][j+1]);
						System.out.println(addBlank);
						follow[1] = First(addBlank,terminal,righthandrule,lefthand,follow);
						System.out.println("test2");
					}
//					for(int k=0; k<terminal.size()+1; k++){
//						System.out.print(follow[1][k] + " ");
//					}
//					System.out.println();
					if(allRightItem[i][j+1].equals("0") || CheckDerive(allRightItem[i][j+1],righthandrule,lefthand)){	//righthand�̫�@�� �άO �᭱�@���|DeriveEmpty
						System.out.println("test6");
						int count = i;
						while(lefthand.get(count).equals("|")){									//���lefthand�nfollow��
							count--;
						}
						if(duplicate[1][count].equals("0")){									//�åB�T�w�S���`���X�{
							System.out.println("test9");
							Follow(lefthand.get(count),terminal,righthandrule,lefthand,follow,duplicate);
						}
					}
				}
			}
		}
		return follow[1];
	}
	public static ArrayList<String> findTerminal(String[] temp){	//��XTerminal
		ArrayList<String> tempterminal = new ArrayList<String>();
		for(int i=2; i<temp.length; i++){
			if((temp[i].charAt(0) > 90 || temp[i].charAt(0) < 65) && temp[i].charAt(0) != 'l' && temp[i].charAt(0) != '>'){
				tempterminal.add(temp[i]);
			}
		}
		return tempterminal;
	}
}
