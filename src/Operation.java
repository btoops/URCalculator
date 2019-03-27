import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Stack;

public class Operation {
	private HashMap<String, Double> variables = new HashMap<>();
	private Stack<Double> operands = new Stack<>();
	private Stack<Integer> operators = new Stack<>();
	private ArrayList<Character> order = new ArrayList<>();
	private double total;

	Operation(){
		order.add('+'); //index 0  0/2 = 0
		order.add('-'); //index 1  1/2 = 0
		order.add('/'); //index 2  2/2 = 1
		order.add('*'); //index 3  3/2 = 1
		order.add('^'); //index 4  4/2 = 2 --> should be 1
		order.add('%'); //index 5  5/2 = 2
	}

	//assumes variables are already set
	//true if successfully converted
	//false if invalid expression
	public boolean infixToPostfixEvaluation(String input) {

		if(input.equals("")) {
			return false;
		}
		String temp = adjustString(input);
		if(temp.equals("Invalid Expression")) {
			System.out.println(temp);
			return false;
		}
		String[] splitInput = formatString(adjustString(input), false).split("\\s");
		for(int i = 0; i < splitInput.length; i ++) {
			double d;
			try {
				if(isASetVariable(splitInput[i])) {
					d = variables.get(splitInput[i]);
				}else {
					
					d = Double.parseDouble(splitInput[i]);					
				}

				operands.push(d);
			}catch(NumberFormatException e) {
				if(splitInput[i].equals("")) {
					System.out.println("Not a well formed expression (Parenthesis Mismatch)");
					return false;
				}else {
					if(Character.isLetter(splitInput[i].charAt(0))){
						System.out.println("\"" + splitInput[i] + "\"" + " is not defined.");
						operands.clear();
						operators.clear();
						return false;
					}

				}
			}
			if(splitInput[i].equals("")) {
				continue;
			}
			char tok = splitInput[i].charAt(0);
			precedence(tok);;
		}
		while(!operators.isEmpty()) {
			int operation = operators.pop();
			if(!calculate(operation)) {
				return false;
			}

		}
		if(!operands.isEmpty()) {
			total = operands.peek();
		}
		else {
			return false;
		}
		operators.clear();
		operands.clear();
		return true;
	}

	public HashMap<String, Double> getVariables() {
		return variables;
	}
	public boolean precedence(char tok) {
		if(isOperator(tok)) {
			if(operators.isEmpty()) {
				operators.add(order.indexOf(tok));
				return true;
			}
			else {
				int precedenceToAdd = (order.indexOf(tok)/2);
				int lastPrecedence = operators.peek()/2;
				if(tok == '%') {
					precedenceToAdd = (order.indexOf(tok) - 1)/2;
				}else if(operators.peek() == '%'){
					lastPrecedence = (operators.peek() - 1) / 2;
				}
				if(precedenceToAdd > lastPrecedence) {
					operators.add(order.indexOf(tok));
					return true;
				}else {
					while(precedenceToAdd <= lastPrecedence) {
						int operation = operators.pop();
						calculate(operation);
						if(!operators.isEmpty()) {
							lastPrecedence = operators.peek()/2;
						}
						else {
							break;
						}
					}
					operators.add(order.indexOf(tok));
					return true;
				}
			}
		}
		else {
			if(tok == '(') {
				operators.push(-91828);
			}else if(tok == ')') {
				while(operators.peek() != -91828) {
					if(operators.peek() < 0) {
						return false;
					}
					calculate(operators.pop());
				}
				operators.pop();
				return true;
			}
			if(tok == '[') {
				operators.push(-103847);
			}else if(tok == ']') {
				while(operators.peek() != -103847) {
					if(operators.peek() < 0) {
						return false;
					}
					calculate(operators.pop());
				}
				operators.pop();
				return true;
			}
			if(tok == '{') {
				operators.push(-7432214);
			}else if(tok == '}') {
				while(operators.peek() != -7432214) {
					if(operators.peek() < 0) {
						return false;
					}
					calculate(operators.pop());
				}
				operators.pop();
				return true;
			}
		}
		return false;
	}
	public boolean isOperator(char tok) {
		if(order.contains(tok)) {
			return true;
		}
		return false;
	}
	public boolean isOpeningParenthesis(char tok) {
		if(tok == '(' || tok == '{' || tok == '[' ) {
			return true;
		}
		return false;
	}
	public boolean isClosingParenthesis(char tok) {
		if(tok == ')' || tok == '}' || tok == ']' ) {
			return true;
		}
		return false;
	}
	public double getTotal() {
		return total;
	}
	public boolean calculate(int op) {
		int operation = op;
		double num2 = 0;
		double num1 = 0;
		try {
		num2 = operands.pop();
		num1 = operands.pop();
		}catch(EmptyStackException e) {
			System.out.println("Not a well formed expression (Parenthesis Mismatch)");
			return false;
		}
		if(operation == 0) {
			operands.push(num1 + num2);
		}
		if(operation == 1) {
			operands.push(num1 - num2);
		}
		if(operation == 2) {
			if(num2 == 0) {
				System.out.println("Can't divide by zero.");
				return false;
			}
			operands.push(num1 / num2);
		}
		if(operation == 3) {
			operands.push(num1 * num2);
		}
		if(operation == 4) {
			operands.push(Math.pow(num1, num2));
		}
		if(operation == 5) {
			operands.push(num1%num2);
		}
		return true;
	}

	public boolean isASetVariable(String s) {
		if(variables.containsKey(s)) {
			return true;
		}
		return false;
	}

	public boolean setVariables(String s) {
		String[] temp = formatString(adjustString(s), false).split(" =");
		try {
			double d = 0;
			if(infixToPostfixEvaluation(temp[temp.length - 1])) {
				d = total;
			}
			int x = temp.length - 2;
			while(x >= 0) {
				variables.put(temp[x], d);
				x--;
			}
		}catch(NumberFormatException e) {
			return false;
		}
		return true;
	}

	public void clearAll() {
		operands.clear();
		operators.clear();
		variables.clear();
	}

	public boolean clear(String key) {
		if(variables.containsKey(key)) {
			variables.remove(key);
			return true;
		}else {
			return false;
		}
	}

	public void showAll() {
		System.out.println(Arrays.asList(variables));	
	}


	public String formatString(String n, boolean weirdCase) {
		if(n.equals("")) {
			return "";
		}
		String[] firstSplit = n.split(" ");

		String[] temp;
		String s = "";
		for(int i = 0; i < firstSplit.length; i ++) {
			s += firstSplit[i];
		}
		temp = s.split("");

		String formated = "";
		int i;
		for( i = 0; i < temp.length; i++) {
			if(temp[i].equals(" ")) {
				continue;
			}
			if(i == 0) {
				if(temp[i].equals("+")) { //checks leading +a
					i++;
				}
				if(temp[i].equals("-")) {
					formated = "0 -";
					i++;
				}
			}
			else if(i > 0 && i < temp.length && (isOpeningParenthesis(temp[i-1].charAt(0)) || temp[i-1].equals("*") || temp[i-1].equals("/") || temp[i-1].equals("^") || temp[i-1].equals("%")) 
					&& temp[i].equals("+") 
					&& (Character.isDigit(temp[i+1].charAt(0)) || isASetVariable(temp[i+1]))) {
				i++;
			}
			else if(weirdCase && i > 0 && i < temp.length && (isOpeningParenthesis(temp[i-1].charAt(0)) || temp[i-1].equals("*") || temp[i-1].equals("/") || temp[i-1].equals("^") || temp[i-1].equals("%")) 
					&& temp[i].equals("-") 
					&& (Character.isDigit(temp[i+1].charAt(0)) || isASetVariable(temp[i+1]))) {
				formated += "(0 " + temp[i] + temp[i + 1] + ")";
				i+=2;
				if(i >= temp.length) {
					break;
				}
			}
			char t= temp[i].charAt(0);
			if(isOpeningParenthesis(t) && isOperator(temp[i + 1].charAt(0))) {
				formated += temp[i];
			}else if(isOpeningParenthesis(t)) {
				formated += temp[i] + " ";
			}
			else if(isClosingParenthesis(t)) {
				formated += " " + temp[i];
			}else if(temp[i].contains("=")) {
				formated += " " + temp[i];
			}
			else if(!isOperator(t)) {
				formated += temp[i];
			}else if(i >= temp.length - 1) {
				return "";

			}
			else if(isOperator(t) && isOperator(temp[i + 1].charAt(0))) { //fix for case 120 + 30- and 120 + 30 - ... throws ArrayIndexOutOfBoundsException
				formated += " " + temp[i];
			}
			else {
				formated += " " + temp[i] + " ";
			}
		}
		return formated;
	}


	//evaluates multiple + or - signs in a row to create a separate string
	public String adjustString(String input) {
		String[] characters = formatString(input, false).split("\\s");
		ArrayList<String> c = new ArrayList<>();
		for(int i = 0; i < characters.length; i ++) {
			c.add(characters[i]);
		}
		String adjusted = "";
		int count = 0;
		int size = c.size();
		for(int i = 0; i <= size; i++) {
			char tok1;
			char tok2;
			try {
				if(i == 0 && c.get(i).isEmpty()) {
					count++;
					tok1 = c.get(count).charAt(0);
				}else {
					tok1 = c.get(count).charAt(0);
				}
				tok2 = c.get(count + 1).charAt(0);
			}catch(IndexOutOfBoundsException e) {
				break;
			}
			if(tok1 == '+' && tok2 == '-') {
				c.remove(count + 1);
				c.set(count, "-");
			}else if(tok1 == '+' && tok2 == '+') {
				c.remove(count + 1);
			}else if(tok1 == '-' && tok2 == '-') {
				c.remove(count + 1);
				c.set(count, "+");
			}else if(tok1 == '-' && tok2 == '+') {
				c.remove(count + 1);
				c.set(count, "-");
			}
			else {
				count++;
			}
		}
		String fin = "";
		for(String s: c) {
			adjusted += s;
		}
		fin = formatString(adjusted, true);
		if(fin.equals("")) {
			fin = "Invalid Expression";
		}
		return fin;
	}


}

