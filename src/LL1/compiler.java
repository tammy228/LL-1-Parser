package LL1;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
public class compiler {
	public static void main(String[] args) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\亭臻\\Desktop\\CFG_1\\CFG_7.txt"));
		Scanner sc = new Scanner(System.in);
		ArrayList<String> CFG = new ArrayList<String>();
		ArrayList<String> leftHand = new ArrayList<String>();
		ArrayList<String> rightHand = new ArrayList<String>();
		ArrayList<String> terMinal = new ArrayList<String>();
		LinkedList<String> stack = new LinkedList<>();
		String line = null;
		while((line = br.readLine()) != null){	//把CFG吃進來
			CFG.add(line);
		}
//		String remainInput = sc.nextLine();
		for(int i=0; i<CFG.size(); i++){	//取LeftHandSide(Non-Terminal)  & rightHandSide(包含前面及中間的空格) & terminal
			String[] temp = CFG.get(i).split(" ");
			String[] righttemp = CFG.get(i).split(">|\\|");
			rightHand.add(righttemp[1]);	//RightHandSide
		//	if(!temp[1].equals("|"))	//leftHandSide 沒有 | 的版本
			leftHand.add(temp[1]);
			for(int j=0; j<findTerminal(temp).size(); j++){			//因為是以每一行傳進去function裡面找terminal 所以有可能會找到一樣的,所以要先經過判斷在 add 進 terMinal
//				System.out.println(terMinal.contains(findTerminal(temp).get(j)));
				if(!(terMinal.contains(findTerminal(temp).get(j))))
					terMinal.add(findTerminal(temp).get(j));
			}
		}
//		for(int i=0; i<terMinal.size(); i++){
//			System.out.print(terMinal.get(i) + " ");
//		}
		for(int i=0; i<terMinal.size(); i++){	//把$移到最後一個
			if(terMinal.get(i).equals("$"))	
				terMinal.remove(i);
		}
		terMinal.add("$");
		
		String[][] table = new String[leftHand.size()+1][terMinal.size()+1];
		String[][] first = new String[2][terMinal.size()+1];
		String[][] duplicateOrNot= new String[2][leftHand.size()];
		
		for(int j=0; j<leftHand.size(); j++){	//用在求follow set的地方,判斷有無重複出現follow set
			duplicateOrNot[0][j] = leftHand.get(j);
			duplicateOrNot[1][j] = "0";
		}
//		for(int i=0; i<2; i++){
//			for(int j=0; j<leftHand.size(); j++){
//				System.out.print(duplicateOrNot[i][j]);
//			}
//			System.out.println();
//		}
		for(int i=0; i<terMinal.size(); i++){	//把terminal放進 first 的第0行
			first[0][i] = terMinal.get(i);
		}
		for(int i=0; i<leftHand.size(); i++){	//建好table的第0行跟第0列
			table[i+1][0] = leftHand.get(i);	//包含"|"
		}
		for(int i=0; i<terMinal.size(); i++){
			table[0][i+1] = terMinal.get(i);
		}
		for(int i=0; i<CFG.size(); i++){	//取first set
//			System.out.println("test11");
//			System.out.println(i);
			for(int k=0; k<terMinal.size()+1; k++){	//歸0
				first[1][k] = "0";
			}
			for(int j=0; j<leftHand.size(); j++){	//用在求follow set的地方,判斷有無重複出現follow set
				duplicateOrNot[1][j] = "0";
			}
			String[] righttemp = CFG.get(i).split(">|\\|");
			String[] temp,temp2;
			temp = First(righttemp[1],terMinal,rightHand,leftHand,first);	//求first set 並且判斷會不會deriveEmpty
			
//			for(int k=0; k<temp.length; k++){
//				System.out.print(temp[k]+ " ");
//			}
//			System.out.println();
//			System.out.println();
			
			if(temp[temp.length-1].equals("1")){					//如果會DeriveEmpty就呼叫Follow
				int check = i;
				while(leftHand.get(check).equals("|")){				//檢查lefthand的Non-terminal是甚麼
					check--;
				}
//				System.out.println("test10");
//				System.out.println(leftHand.get(check));
				temp2 = Follow(leftHand.get(check),terMinal,rightHand,leftHand,first,duplicateOrNot);	//傳進去的參數已經是lefthand
//				for(int k=0; k<temp.length; k++){
//					System.out.print(temp2[k]+ " ");
//				}
//				System.out.println();
				for(int j=0; j<temp2.length-1; j++){					//填table
					if(!(temp2[j].equals("0"))){
						while(leftHand.get(check).equals("|")){		//檢查lefthand的Non-terminal是什麼
							check--;
						}
						table[check+1][j+1] = Integer.toString(i+1);	//行跟列+1的原因是因為table的形式,多了一行跟一列
					}	
				}
			}
			for(int j=0; j<temp.length-1; j++){					//填table
				if(!(temp[j].equals("0"))){
					int check = i;
					while(leftHand.get(check).equals("|")){		//檢查lefthand的Non-terminal是什麼
						check--;
					}
					table[check+1][j+1] = Integer.toString(i+1);
				}	
			}
		}
		
		for(int i=0; i<leftHand.size()+1; i++){					//印出table
			for(int j=0; j<terMinal.size()+1; j++){
				if(!"|".equals(table[i][0])){
					System.out.printf("%8s\t", table[i][j] == null ? " " : table[i][j]);
				}
			}
			System.out.println();
		}
		while(!sc.hasNext("0")){
			String remainInput = sc.nextLine();
			String answer = ApplyRule(remainInput,stack,rightHand,table);
			System.out.println(remainInput + " " + answer);
		}
//		String answer = ApplyRule(remainInput,stack,rightHand,table);
//		System.out.println(remainInput + " " + answer);
		
	}
	public static String[] First(String righthand, ArrayList<String> terminal, ArrayList<String> righthandrule, ArrayList lefthand,String[][] First){
		ArrayList<String> cal = new ArrayList<String>();
		int link,rule=1;
		String[] temp = new String[3];
		String[] rightWithOutBlank = righthand.split(" ",3);
		String Letter = rightWithOutBlank[1];		//取第一個字
		char alphabet = Letter.charAt(0);			//第一個字的第一個字母
		if(alphabet >= 65 && alphabet <= 90){		//第一次的判斷
			link = lefthand.indexOf(Letter);		//若是Non-terminal 則找lefthandside看它在第幾個
//			System.out.println(Letter);
			for(int i=link+1; lefthand.get(i).equals("|"); i++){		//找出Non-terminal 有幾個rules
				rule++;
				if((i+1) == lefthand.size())							//防止indexOubOfBound
					break;
			}
//			System.out.println(rule);
			for(int i=link; i<link+rule; i++){			//把righthand 換成rule 並存進cal 裡面
				if(rightWithOutBlank.length == 2)		//判斷rule 是否只有1項
					cal.add(righthandrule.get(i));
				else
					cal.add(righthandrule.get(i).concat(" ").concat(rightWithOutBlank[2]));
			}
//			for(int i=0; i<cal.size(); i++){
//				System.out.println("test15");
//				System.out.println(cal.get(i));
//			}
			for(int i=0; i<cal.size(); i++){		//把cal 裡面的東西做第二次判斷
				temp = cal.get(i).split(" ",3);
				if(cal.get(i).charAt(1) < 65 || cal.get(i).charAt(1) > 90 && !(temp[1].equals("lamda"))){		//非大寫
					for(int j=0; j<terminal.size(); j++){
						if(temp[1].equals(First[0][j]))
							First[1][j] = "1";
					}
				}else if(temp[1].equals("lamda") && temp.length != 2){	//確定為lamda 且後面有東西
					cal.set(i , cal.get(i).substring(6, cal.get(i).length()));
					First(cal.get(i),terminal,righthandrule,lefthand,First);
				}else{
					First(cal.get(i),terminal,righthandrule,lefthand,First);
				}
			} 
		}else if(rightWithOutBlank[1].equals("lamda")){
			First[1][terminal.size()] = "1";
		}else{										//第一次判斷就成功
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
		char alphabet = righthand.charAt(0);		//righthand的第一個字母
		if(alphabet >= 65 && alphabet <= 90){		//第一次的判斷 是不是大寫
			link = lefthand.indexOf(righthand);		//若是Non-terminal 則找lefthandside看它在第幾個
			for(int i=link+1; lefthand.get(i).equals("|"); i++){		//找出Non-terminal 有幾個rules
				rule++;
				if((i+1) == lefthand.size())
					break;
			}
			for(int i=link; i<link+rule; i++){
				if(righthandrule.get(i).equals(" lamda"))
					answer = true;
			}
		}
		return answer;
	}
	public static String[] Follow(String righthand, ArrayList<String> terminal, ArrayList<String> righthandrule, ArrayList<String> lefthand,String[][] follow, String[][] duplicate){
//		System.out.println("test8");
		for(int i=0; i<lefthand.size(); i++){	//把有做follow set 的lefthand做記號
			if(duplicate[0][i].equals(righthand))
				duplicate[1][i] = "1";
		} 
		String[][] allRightItem = new String[righthandrule.size()][50];
		for(int i=0; i<righthandrule.size(); i++){				//把righthand的單字都切好存到allRightItem裡面
			String[] temp = righthandrule.get(i).split(" ");
			for(int j=0; j<temp.length; j++)
				allRightItem[i][j] = temp[j];
			for(int j=temp.length; j<50; j++)					//其他沒有righthand的地方存 0
				allRightItem[i][j] = "0";
		}
		for(int i=0; i<righthandrule.size(); i++){
			for(int j=0; j<righthandrule.get(i).length(); j++){
				if(righthand.equals(allRightItem[i][j])){	//找到要follow的item
					String addBlank="";
//					System.out.println("test22");
					if(!(allRightItem[i][j+1].equals("0"))){	//righthand的後面還有東西
//						System.out.println("test5");
//						System.out.println(CheckDerive(allRightItem[i][j+1],righthandrule,lefthand));
						if(CheckDerive(allRightItem[i][j+1],righthandrule,lefthand)){
							int count = i;
							while(lefthand.get(count).equals("|")){									//找到lefthand要follow誰
								count--;
							}
							if(duplicate[1][count].equals("0")){									//並且確定沒有循環出現
								Follow(lefthand.get(count),terminal,righthandrule,lefthand,follow,duplicate);
							}
						}
						for(int k=j+1; !(allRightItem[i][k].equals("0")); k++)
							addBlank += "".concat(" ").concat(allRightItem[i][k]);
//						System.out.println(addBlank);
						follow[1] = First(addBlank,terminal,righthandrule,lefthand,follow);
//						System.out.println("test13");
					}
					if(allRightItem[i][j+1].equals("0") || CheckDerive(allRightItem[i][j+1],righthandrule,lefthand)){	//righthand最後一項 或是 後面一項會DeriveEmpty
//						System.out.println("test6");
						int count = i;
						while(lefthand.get(count).equals("|")){									//找到lefthand要follow誰
							count--;
						}
						if(duplicate[1][count].equals("0")){									//並且確定沒有循環出現
							Follow(lefthand.get(count),terminal,righthandrule,lefthand,follow,duplicate);
						}
					}
				}
			}
		}
		return follow[1];
	}
	public static ArrayList<String> findTerminal(String[] temp){	//找出Terminal
		ArrayList<String> tempterminal = new ArrayList<String>();
		for(int i=2; i<temp.length; i++){
			if((temp[i].charAt(0) > 90 || temp[i].charAt(0) < 65) && !(temp[i].equals("lamda")) && temp[i].charAt(0) != '>'){
					tempterminal.add(temp[i]);
			}
		}
		return tempterminal;
	}
	public static String ApplyRule(String input, LinkedList<String> stack,  ArrayList<String> righthandrule, String[][] table ){
		String rule="1 ";
		int row = 0, col = 0;
//		System.out.println("test20");
//		System.out.println(input);
		LinkedList<String> Input = new LinkedList<String>();
		String[] temp = righthandrule.get(0).split(" ");
		String[] temp2 = input.split(" ");
		for(int i=0; i<temp2.length; i++)							//把input以空格分開存進 LinkedList Input 裡面
			Input.add(temp2[i]);
		for(int i=1; i<temp.length; i++)							//把一開始的rule 以空格分開 Push 進來
			stack.add(temp[i]);
		while(!(stack.get(0).equals("$") && stack.size() == 1 && Input.get(0).equals("$"))){	//最後結束的條件
//			System.out.println("test2");
//			for(int i=0; i<stack.size(); i++)
//				System.out.print(stack.get(i) + " ");
//			System.out.println();
//			for(int i=0; i<Input.size(); i++)
//				System.out.print(Input.get(i) + " ");
//			System.out.println();
			if(stack.get(0).equals("$") || Input.get(0).equals("$")){
				System.out.print("Error ");
				break;
			}
			if(!(stack.get(0).equals(Input.get(0)))){				//判斷input 的開頭跟 stack 的開頭有沒有一樣
				for(int i=1; i<table[0].length; i++){
					if(table[0][i].equals(Input.get(0)))			//找出input的第一個字在table 的哪行
						col = i;
				}
				for(int i=1; i<table.length; i++){
					if(table[i][0].equals(stack.get(0)))			//找出stack的第一個字在table 的哪列
						row = i;
				}
//				System.out.println(row);
//				System.out.println(col);
//				System.out.println(table[row][col]);
				if(table[row][col] == null){
					System.out.print("Error ");
//					System.out.println("test1");
					break;
				}
				stack.pop();										//把stack 最上面的字 pop 掉
//				System.out.println(righthandrule.get(Integer.valueOf(table[row][col]) - 1));
				if(!(righthandrule.get(Integer.valueOf(table[row][col]) - 1).equals(" lamda"))){
					temp = righthandrule.get(Integer.valueOf(table[row][col]) - 1).split(" ");	//找出table對應righthandrule 並以空格切割
					for(int i=temp.length - 1; i>=1; i--){			//把以切割好的righthandrule push 進去stack 裡面
						stack.push(temp[i]);
					}
				}
				rule = rule.concat(table[row][col] + " ");			//加上apply rule
			}else{
				stack.pop();
				Input.pop();
			}
		}
		stack.clear();
		return rule;
	}
}
